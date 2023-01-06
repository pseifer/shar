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
import org.semanticweb.HermiT.Configuration.TableauMonitorType
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
class HermitReasoner(
    val initialization: ReasonerInitialization,
    configuration: HermitConfiguration = HermitConfiguration(),
    debugging: Boolean = false
) extends DLReasoner(initialization):

  // TODO: Wrap internal in a flush-state dependent thing.

  /** Print message, if debugging is enabled. */
  private def debug(message: String): Unit =
    if debugging then println(message)

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

  // Monitoring (when debugging is enabled).
  if debugging then
    reasonerConfig.tableauMonitorType = TableauMonitorType.TIMING

  // Set all remainign configurations.
  configuration.set(reasonerConfig)

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
          Set(df.getOWLSubClassOfAxiom(convert(c), convert(d)))
        case Equality(c, d) =>
          val cc = convert(c)
          val dd = convert(d)
          Set(
            df.getOWLSubClassOfAxiom(cc, dd),
            df.getOWLSubClassOfAxiom(dd, cc)
          )
        case Satisfiability(c) =>
          Set(df.getOWLDisjointClassesAxiom(convert(c), convert(Bottom)))
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

    debug("Axiom: " ++ axiom.toString)

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
    debug("Starting ROLE SUBSUMPTION task.")
    val result = reason(
      _.isEntailed(
        model(_.getOWLSubObjectPropertyOfAxiom(convert(r), convert(p)))
      )
    )
    debug(s"Result: $result")
    result

  private def subsumed(c: Concept, d: Concept): Boolean =

    debug(s"Converting: $c")
    val cc = convert(c)

    debug(s"Converted: $cc")
    debug(s"Converting: $d")

    val dd = convert(d)

    debug(s"Converted: $dd")
    debug(s"Obtaining task.")

    val task = model(_.getOWLSubClassOfAxiom(cc, dd))

    debug(s"Obtained task: $task")
    debug("Starting SUBSUMPTION task.")

    val result = reason(_.isEntailed(task))

    debug(s"Result: $result")
    result

  private def satisfiable(c: Concept): Boolean =
    debug("Starting SATISFIABILITY task.")
    val result = reason(_.isSatisfiable(convert(c)))
    debug(s"Result: $result")
    result

  private def convert(iri: Iri): IRI = IRI.create(iri.getRaw)

  private def convert(role: Role): OWLObjectPropertyExpression =
    role match
      case NamedRole(r) => model(_.getOWLObjectProperty(convert(r)))
      case Inverse(r)   => model(_.getOWLObjectInverseOf(convert(r)))

  private def convertD(role: NamedRole): OWLDataPropertyExpression =
    model(_.getOWLDataProperty(convert(role.r)))

  private def convertD(concept: NamedConcept): OWLDataRange =
    model(_.getOWLDatatype(convert(concept.c)))

  private def convert(concept: Concept): OWLClassExpression =
    concept match

      case Top =>
        model(_.getOWLThing)

      case Bottom =>
        model(_.getOWLNothing)

      case NominalConcept(iri) =>
        model(_.getOWLObjectOneOf(OWLNamedIndividualImpl(convert(iri))))

      case NamedConcept(iri) =>
        model(_.getOWLClass(convert(iri)))

      case Complement(c) =>
        model(_.getOWLObjectComplementOf(convert(c)))

      case Intersection(c, d) =>
        model(_.getOWLObjectIntersectionOf(convert(c), convert(d)))

      case Union(c, d) =>
        model(_.getOWLObjectUnionOf(convert(c), convert(d)))

      // Roles - DataProperties

      case GreaterThan(n, r: NamedRole, c: NamedConcept) if c.c.isDatatype =>
        model(_.getOWLDataMinCardinality(n, convertD(r), convertD(c)))

      case LessThan(n, r: NamedRole, c: NamedConcept) if c.c.isDatatype =>
        model(_.getOWLDataMaxCardinality(n, convertD(r), convertD(c)))

      case Existential(r: NamedRole, c: NamedConcept) if c.c.isDatatype =>
        model(_.getOWLDataSomeValuesFrom(convertD(r), convertD(c)))

      case Universal(r: NamedRole, c: NamedConcept) if c.c.isDatatype =>
        model(_.getOWLDataAllValuesFrom(convertD(r), convertD(c)))

      // Roles - ObjectProperties

      case GreaterThan(n, r, c) =>
        model(_.getOWLObjectMinCardinality(n, convert(r), convert(c)))

      case LessThan(n, r, c) =>
        model(_.getOWLObjectMaxCardinality(n, convert(r), convert(c)))

      case Existential(r, c) =>
        model(_.getOWLObjectSomeValuesFrom(convert(r), convert(c)))

      case Universal(r, c) =>
        model(_.getOWLObjectAllValuesFrom(convert(r), convert(c)))

      case d: DerivedNumberRestriction => convert(d.toGreaterThan)

object HermitReasoner:
  def default: HermitReasoner =
    default(HermitConfiguration(), debugging = false)

  def default(config: HermitConfiguration, debugging: Boolean): HermitReasoner =
    val state: BackendState =
      BackendState(EmptyInitialization, PrefixMapping.default)
    HermitReasoner(state.reasonerInit, config, debugging = debugging)
