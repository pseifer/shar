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

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._
import org.semanticweb.owlapi.reasoner.Node
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl

import scala.collection.JavaConverters._
import scala.language.implicitConversions

import java.io.File

/** A description logics reasoner using OWL API. */
abstract class OwlApiReasoner(
    initialization: ReasonerInitialization,
    debugging: Boolean = false
) extends DLReasoner(initialization):

  /** Print message, if debugging is enabled. */
  private def debug(message: String): Unit =
    if debugging then println(message)

  // Setup.

  private val manager: OWLOntologyManager =
    OWLManager.createOWLOntologyManager()
  val df: OWLDataFactory = manager.getOWLDataFactory

  protected val ontology: OWLOntology = initialization match
    // Load OWL ontology.
    case OntologyInitialization(owl) =>
      manager.loadOntology(convert(owl))
    // Create fresh, empty one.
    case EmptyInitialization =>
      manager.createOntology()

  // To be implemented by reasoner.

  protected def rFlush(): Unit

  protected def rEntailment(subp: OWLSubObjectPropertyOfAxiom): Boolean

  protected def rEntailment(sub: OWLSubClassOfAxiom): Boolean

  protected def rSatisfiability(c: OWLClassExpression): Boolean

  // Flushing state to be optimized.

  private var isFlushed = false

  private def flush(): Unit =
    rFlush()
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

  // Internal Functionality.

  private def entailed(subp: OWLSubObjectPropertyOfAxiom): Boolean =
    if !isFlushed then flush()
    rEntailment(subp)

  private def entailed(sub: OWLSubClassOfAxiom): Boolean =
    if !isFlushed then flush()
    rEntailment(sub)

  private def satisfiable(c: OWLClassExpression): Boolean =
    if !isFlushed then flush()
    rSatisfiability(c)

  private def model[T](fn: OWLDataFactory => T): T = fn(df)

  // Public API -- Functionality.

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

  def prove(axiom: Axiom): Boolean =

    debug("Axiom: " ++ axiom.toString)

    val r = axiom match
      case Subsumption(c, d)     => subsumed(c, d)
      case Equality(c, d)        => equal(c, d)
      case Satisfiability(c)     => satisfiable(c)
      case RoleSubsumption(r, p) => subsumed(r, p)
    r

  // Internal Conversions.

  private def equal(c: Concept, d: Concept): Boolean =
    subsumed(c, d) && subsumed(d, c)

  private def subsumed(r: Role, p: Role): Boolean =
    debug("Starting ROLE SUBSUMPTION task.")
    val result = entailed(
      model(_.getOWLSubObjectPropertyOfAxiom(convert(r), convert(p)))
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

    val result = entailed(task)

    debug(s"Result: $result")
    result

  private def satisfiable(c: Concept): Boolean =
    debug("Starting SATISFIABILITY task.")
    val result = satisfiable(convert(c))
    debug(s"Result: $result")
    result

  private def convert(iri: Iri): IRI = IRI.create(iri.getRaw)

  private def convert(role: Role): OWLObjectPropertyExpression =
    role match
      case NamedRole(r) => model(_.getOWLObjectProperty(convert(r)))
      case Inverse(r)   => convert(r).getInverseProperty()

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
