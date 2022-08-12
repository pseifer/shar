package de.pseifer.shar

import de.pseifer.shar.core._
import de.pseifer.shar.dl._
import de.pseifer.shar.error._
import de.pseifer.shar.reasoning._
import de.pseifer.shar.parsing._

import scala.collection.mutable.{ListBuffer => L}

// DSL for description logic knowledgebases.
class Shar(
    // Initialization (e.g., ontology) for the reasoner.
    init: ReasonerInitialization = EmptyInitialization,
    // Prefix mapping.
    prefixes: PrefixMapping = PrefixMapping.default,
    // Constructor for a reasoner of type DLReasoner.
    reasoner: ReasonerInitialization => DLReasoner = HermitReasoner(_),
    // Use ':' for a build-in default 'shar:' prefix.
    defaultIsSharPrefix: Boolean = true
):

  // State of the reasoning backend.
  implicit val state: BackendState = BackendState(init, prefixes)

  // The default reasoner.
  val defaultReasoner = ReasonerReference("K", reasoner(state.reasonerInit))

  // Counter for unnamed reasoners.
  private var counter = 0

  // The parser for DL expressions.
  private val parser = ConceptParser(state)

  // Add the default prefix as 'shar' (if defaultIsSharPrefix is set).
  if defaultIsSharPrefix then
    state.prefixes.add(Prefix.fromString(":").toOption.get, Iri.shar)

  // A small wrapper around a reasoner, keeping track of a name and
  // the set of axioms (in AxiomSet representation).
  class ReasonerReference(val name: String, reasoner: DLReasoner):
    // The set of axioms.
    var axioms: AxiomSet = AxiomSet(Set())

    // Restricted reasoner API.
    def addAxioms(as: AxiomSet): Unit = reasoner.addAxioms(as)
    def prove(a: Axiom): Boolean = reasoner.prove(a)

    // Add axioms to the reasoner.
    def +=(as: AxiomSetBuilder): ReasonerReference = 
      val ax = AxiomSet(as.toSet)
      axioms = axioms.join(ax)
      reasoner.addAxioms(ax)
      this

    // Entailment.
    def |-(as: AxiomSetBuilder): Unit = 
      as.toSet.foreach(doEntails(this, _))
    
    // Pretty print.
    def show: Unit = println(this.toString)

    // Pretty print.
    override def toString: String = 
      s"-- $name --\n" ++ 
      axioms.show("\n") ++ 
      s"\n---${List.fill(name.size)("-").mkString("")}---"



  // == Axiom DSL ==

  // Intermediate data structure to collect blocks of axioms (DSL)
  class AxiomSetBuilder(lb: L[Axiom]):
    def toSet: Set[Axiom] = 
      val s = lb.toSet
      lb.clear()
      s
    def add(a: Axiom): AxiomSetBuilder = 
      lb += a
      this

  implicit val axiomSetBuilder: AxiomSetBuilder = AxiomSetBuilder(L())

  // Extensions for building subsumption and equivalence axioms.
  extension (c: Concept) {
    def ⊑(d: Concept)(implicit asb: AxiomSetBuilder): AxiomSetBuilder = <<=(d)

    def <<=(d: Concept)(implicit asb: AxiomSetBuilder): AxiomSetBuilder = 
      asb.add(Subsumption(c, d))

    def ≡(d: Concept)(implicit asb: AxiomSetBuilder): AxiomSetBuilder = ===(d)

    def ===(d: Concept)(implicit asb: AxiomSetBuilder): AxiomSetBuilder = 
      asb.add(Equality(c, d))
  }



  // == Concept DLS ==

  // Top and Bottom.
  val ⊤ = Top
  val ⊥ = Bottom

  // Construct concepts from Concept with infix notation.
  extension (c: Concept) {
    // Intersection
    def ⊓(d: Concept): Concept = Intersection(c, d)
    def &(d: Concept): Concept = Intersection(c, d)
    // Union
    def ⊔(d: Concept): Concept = Union(c, d)
    def |(d: Concept): Concept = Union(c, d)
  }

  // Quantification

  enum TaggedRole:
    case ER(r: Role)
    case FR(r: Role)

  extension (t: TaggedRole) {
    def o(c: Concept): Concept = 
      t match
        case TaggedRole.ER(r) => Existential(r, c)
        case TaggedRole.FR(r) => Universal(r, c)
  }

  def ∃-(r: Iri): TaggedRole = InverseExists(r)
  def ∃(r: Iri): TaggedRole = Exists(r)
  def Ei(r: Iri): TaggedRole = InverseExists(r)
  def E(r: Iri): TaggedRole = Exists(r)
  def Exists(r: Iri): TaggedRole = 
    TaggedRole.ER(NamedRole(r))
  def InverseExists(r: Iri): TaggedRole = 
    TaggedRole.ER(Inverse(NamedRole(r)))
  
  def ∀(r: Iri): TaggedRole = Forall(r)
  def ∀-(r: Iri): TaggedRole = InverseForall(r)
  def A(r: Iri): TaggedRole = Forall(r)
  def Ai(r: Iri): TaggedRole = InverseForall(r)
  def Forall(r: Iri): TaggedRole = 
    TaggedRole.FR(NamedRole(r))
  def InverseForall(r: Iri): TaggedRole = 
    TaggedRole.FR(Inverse(NamedRole(r)))



  // == Knowledge Base DSL ==

  // Define a new reasoner.
  def K(name: String): ReasonerReference = 
    ReasonerReference(name, reasoner(state.reasonerInit))

  // Define a new reasoner (default name).
  def K: ReasonerReference = 
    counter += 1
    ReasonerReference("K"++counter.toString, reasoner(state.reasonerInit))

  // Define (or add Axioms to) the knowledge base.
  def +=(as: AxiomSetBuilder): ReasonerReference = 
    defaultReasoner.addAxioms(AxiomSet(as.toSet))
    defaultReasoner

  // Test, whether an axiom is entailed by the knowledge base.
  def |-(as: AxiomSetBuilder): Unit =
    as.toSet.foreach(doEntails(defaultReasoner, _))

  private def doEntails(r: ReasonerReference, a: Axiom): Unit =
    print(r.name)
    print(" ⊢ ")
    print(a.show)
    print(" : ")
    println(r.prove(a))

  // Visualize all axioms.
  def show(): Unit =
    println(s"-- ${defaultReasoner.name} --")
    println(defaultReasoner.axioms.show("\n"))
    println(s"-- ${List.fill(defaultReasoner.name.size)("-").mkString("")} --")

  // Convert Strings to Iris, implicitly.
  implicit def stringToIri(s: String): Iri =
    Iri.fromString(Iri.shar.expanded(s)) match
      case Left(e) =>
        throw new RuntimeException("Not an IRI: " ++ s ++ " @ " ++ e.show)
      case Right(i) => i

  // Convert Strings to NamedConcepts, implicitly.
  implicit def stringToConcept(s: String): Concept =
    parser.parse(s) match
      case Left(p)           => throw new RuntimeException(p.show)
      case Right(c: Concept) => c
      case Right(_) => throw new RuntimeException("Shar: Expected a Concept")
