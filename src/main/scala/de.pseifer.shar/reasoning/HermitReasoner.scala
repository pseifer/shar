package de.pseifer.shar.reasoning

import de.pseifer.shar.core.{
  ReasonerInitialization,
  EmptyInitialization,
  OntologyInitialization,
  PrefixMapping,
  BackendState,
  Iri
}

import de.pseifer.shar.dl._

import org.semanticweb.HermiT.{Configuration, Reasoner => Hermit}
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._
import org.semanticweb.owlapi.reasoner.Node
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl

import scala.collection.JavaConverters._
import scala.language.implicitConversions

import java.io.File

/** A DL reasoner that uses HermiT internally.
  */
class HermitReasoner(val initialization: ReasonerInitialization)
    extends DLReasoner(initialization):

  // TODO: Wrap internal in a flush-state dependent thing.

  // Setup.

  private val manager: OWLOntologyManager =
    OWLManager.createOWLOntologyManager()
  val df: OWLDataFactory = manager.getOWLDataFactory

  private val ontology: OWLOntology = initialization match
    // Load OWL ontology.
    case OntologyInitialization(owl) =>
      manager.loadOntology(convert(owl))
    // Create fresh, empty one.
    case EmptyInitialization =>
      manager.createOntology()

  // Custom reasoner configuration, so we can tell HermiT to ignore
  // 'unsupported' (i.e., non-OWL-2-mapping) data types.
  private val reasonerConfig = Configuration()
  reasonerConfig.ignoreUnsupportedDatatypes = true

  //reasonerConfig.blockingStrategyType =
  //  Configuration.BlockingStrategyType.ANYWHERE
  //reasonerConfig.existentialStrategyType =
  //  Configuration.ExistentialStrategyType.CREATION_ORDER

  //reasonerConfig.directBlockingType = Configuration.DirectBlockingType.PAIR_WISE

  private val hermit: Hermit = Hermit(reasonerConfig, ontology)

  // Flushing state to be optimized.

  private var isFlushed = false

  private def flush(): Unit =
    hermit.flush
    isFlushed = true

  private def mkAxiomset(axioms: AxiomSet): Set[OWLAxiom] =
    axioms.getAxiomSeq.flatMap { a =>
      a match
        case Subsumption(c, d) =>
          val (cc, ac) = convert(c)(AxiomSet.empty)
          val (dd, ad) = convert(d)(AxiomSet.empty)
          val l1 = if !ac.isEmpty then mkAxiomset(ac) else Set()
          val l2 = if !ad.isEmpty then mkAxiomset(ac) else Set()
          (Set(df.getOWLSubClassOfAxiom(cc, dd)) ++ l1 ++ l2).toList
        case Equality(c, d) =>
          val (cc, ac) = convert(c)(AxiomSet.empty)
          val (dd, ad) = convert(d)(AxiomSet.empty)
          val l1 = if !ac.isEmpty then mkAxiomset(ac) else Set()
          val l2 = if !ad.isEmpty then mkAxiomset(ac) else Set()
          (Set(df.getOWLSubClassOfAxiom(cc, dd)) ++ Set(
            df.getOWLSubClassOfAxiom(dd, cc)
          ) ++ l1 ++ l2).toList
        case Satisfiability(c) =>
          val (cc, ac) = convert(c)(AxiomSet.empty)
          val (dd, ad) = convert(Bottom)(AxiomSet.empty)
          val l1 = if !ac.isEmpty then mkAxiomset(ac) else Set()
          val l2 = if !ad.isEmpty then mkAxiomset(ac) else Set()
          (Set(df.getOWLDisjointClassesAxiom(cc, dd)) ++ l1 ++ l2).toList
        case RoleSubsumption(r, p) =>
          Set(df.getOWLSubObjectPropertyOfAxiom(convert(r), convert(p)))
    }.toSet

  // Functionality.

  def saveToFile(file: File): Boolean =
    try {
      file.createNewFile()
      val iri = IRI.create(file)
      manager.saveOntology(
        ontology,
        OWLXMLOntologyFormat(),
        IRI.create(file.toURI)
      )
      true
    } catch {
      case e: Exception => false
    }

  def addAxioms(axioms: AxiomSet): Unit =
    if !axioms.isEmpty then
      isFlushed = false
      manager.addAxioms(ontology, mkAxiomset(axioms).asJava)

  def removeAxioms(axioms: AxiomSet): Unit =
    if !axioms.isEmpty then
      isFlushed = false
      manager.removeAxioms(ontology, mkAxiomset(axioms).asJava)

  def model[T](fn: OWLDataFactory => T): T = fn(df)

  def reason[T](fn: Hermit => T): T =
    if !isFlushed then flush()
    fn(hermit)

  // Reasoner API implementation.

  def prove(axiom: Axiom): Boolean =

    val r = axiom match
      case Subsumption(c, d)     => subsumed(c, d)
      case Equality(c, d)        => equal(c, d)
      case Satisfiability(c)     => satisfiable(c)
      case RoleSubsumption(r, p) => subsumed(r, p)
    r

  // Internal.

  private def equal(c: Concept, d: Concept): Boolean =
    subsumed(c, d) && subsumed(d, c)

  private def subsumed(r: Role, p: Role): Boolean =
    reason(
      _.isEntailed(
        model(_.getOWLSubObjectPropertyOfAxiom(convert(r), convert(p)))
      )
    )

  private def subsumed(c: Concept, d: Concept): Boolean =
    val (cc, ac) = convert(c)(AxiomSet.empty)
    val (dd, ad) = convert(d)(AxiomSet.empty)
    val axioms = ac.join(ad)

    addAxioms(axioms)

    val result = reason(_.isEntailed(model(_.getOWLSubClassOfAxiom(cc, dd)))) &&
      !reason(
        _.isEntailed(
          model(
            _.getOWLSubClassOfAxiom(cc, model(_.getOWLObjectComplementOf(dd)))
          )
        )
      )

    removeAxioms(axioms)

    result

  private def satisfiable(c: Concept): Boolean =
    val (cc, a) = convert(c)(AxiomSet.empty) // TODO: Add axioms.
    reason(_.isSatisfiable(cc))

  private def convert(iri: Iri): IRI = IRI.create(iri.getRaw)

  private def convert(role: Role): OWLObjectPropertyExpression =
    role match
      case NamedRole(r) => model(_.getOWLObjectProperty(convert(r)))
      case Inverse(r)   => model(_.getOWLObjectInverseOf(convert(r)))

  private def convertD(role: NamedRole): OWLDataPropertyExpression =
    model(_.getOWLDataProperty(convert(role.r)))

  private def convertD(concept: NamedConcept): OWLDataRange =
    model(_.getOWLDatatype(convert(concept.c)))

  private def convert(concept: Concept)(implicit
      axioms: AxiomSet
  ): (OWLClassExpression, AxiomSet) =
    concept match

      case Top =>
        (model(_.getOWLThing), axioms)

      case Bottom =>
        (model(_.getOWLNothing), axioms)

      case NominalConcept(iri) =>
        (
          model(_.getOWLObjectOneOf(OWLNamedIndividualImpl(convert(iri)))),
          axioms
        )

      case NamedConcept(iri) =>
        (model(_.getOWLClass(convert(iri))), axioms)

      //case DefinedConcept(name) =>
      //  (model(_.getOWLClass(convert(name.toIri))), axioms)

      case ConceptWithContext(concept, set) =>
        val (c, a) = convert(concept)
        (c, a.join(set))

      case Complement(c) =>
        val (cc, a) = convert(c)
        (model(_.getOWLObjectComplementOf(cc)), a)

      case Intersection(c, d) =>
        val (cc, ac) = convert(c)
        val (dd, ad) = convert(d)
        (model(_.getOWLObjectIntersectionOf(cc, dd)), ac.join(ad))

      case Union(c, d) =>
        val (cc, ac) = convert(c)
        val (dd, ad) = convert(d)
        (model(_.getOWLObjectUnionOf(cc, dd)), ac.join(ad))

      // Roles - DataProperties

      // Specifically for Datatypes (GT)
      case GreaterThan(n, r: NamedRole, c: NamedConcept) if c.c.isDatatype =>
        (model(_.getOWLDataMinCardinality(n, convertD(r), convertD(c))), axioms)

      // Specifically for Datatypes (LT)
      case LessThan(n, r: NamedRole, c: NamedConcept) if c.c.isDatatype =>
        (model(_.getOWLDataMaxCardinality(n, convertD(r), convertD(c))), axioms)

      // Specifically for Datatypes (EXISTS)
      case Existential(r: NamedRole, c: NamedConcept) if c.c.isDatatype =>
        (model(_.getOWLDataSomeValuesFrom(convertD(r), convertD(c))), axioms)

      // Specifically for Datatypes (FORALL)
      case Universal(r: NamedRole, c: NamedConcept) if c.c.isDatatype =>
        (model(_.getOWLDataAllValuesFrom(convertD(r), convertD(c))), axioms)

      // Roles - ObjectProperties

      case GreaterThan(n, r, c) =>
        val (cc, ac) = convert(c)
        (model(_.getOWLObjectMinCardinality(n, convert(r), cc)), ac)

      // Optional, presumably better performance.
      case LessThan(n, r, c) =>
        val (cc, ac) = convert(c)
        (model(_.getOWLObjectMaxCardinality(n, convert(r), cc)), ac)

      // Optional, presumably better performance.
      case Existential(r, c) =>
        val (cc, ac) = convert(c)
        (model(_.getOWLObjectSomeValuesFrom(convert(r), cc)), ac)

      // Optional, presumably better performance.
      case Universal(r, c) =>
        val (cc, ac) = convert(c)
        (model(_.getOWLObjectAllValuesFrom(convert(r), cc)), ac)

      case d: DerivedNumberRestriction => convert(d.toGreaterThan)

object HermitReasoner:
  def default: HermitReasoner =
    val state: BackendState =
      BackendState(EmptyInitialization, PrefixMapping.default)
    HermitReasoner(state.reasonerInit)
