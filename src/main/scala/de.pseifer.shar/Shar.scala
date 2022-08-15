package de.pseifer.shar

import de.pseifer.shar.core._
import de.pseifer.shar.dl._
import de.pseifer.shar.error._
import de.pseifer.shar.reasoning._
import de.pseifer.shar.parsing._

// DSL for description logic knowledgebases.
class Shar(
    // Initialization (e.g., ontology) for the reasoner.
    init: ReasonerInitialization = EmptyInitialization,
    // Prefix mapping.
    prefixes: PrefixMapping = PrefixMapping.default,
    // Constructor for a reasoner of type DLReasoner.
    reasoner: ReasonerInitialization => DLReasoner = HermitReasoner(_),
    // Use ':' for a build-in default 'shar:' prefix.
    defaultIsSharPrefix: Boolean = true,
    // Controlls, whether certain DSL expressions output to stdout.
    noisy: Boolean = true
):

  // State of the reasoning backend.
  implicit val state: BackendState = BackendState(init, prefixes)

  // Implicit builder for axioms.
  implicit val axiomSetBuilder: AxiomSetBuilder = AxiomSetBuilder()

  // The default reasoner.
  val defaultReasoner =
    ReasonerReference("K", reasoner(state.reasonerInit), noisy)

  // Counter for unnamed reasoners.
  private var counter = 0

  // The parser for DL expressions.
  private val parser = ConceptParser(state)

  // Add the default prefix as 'shar' (if defaultIsSharPrefix is set).
  if defaultIsSharPrefix then
    state.prefixes.add(Prefix.fromString(":").toOption.get, Iri.shar)

  // == Axiom DSL ==

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

  // Used to tagg a role (for prefix operators).
  enum TaggedRole:
    case ER(r: Role)
    case FR(r: Role)

  // Tagged role and Concept construct Existential/Universal.
  extension (t: TaggedRole) {
    def ∘(c: Concept): Concept = o(c)
    def o(c: Concept): Concept =
      t match
        case TaggedRole.ER(r) => Existential(r, c)
        case TaggedRole.FR(r) => Universal(r, c)
  }

  // Variants of prefix operators for existential quantification.
  def ∃(r: Iri): TaggedRole = Exists(r)
  def ∃-(r: Iri): TaggedRole = InverseExists(r)
  def E(r: Iri): TaggedRole = Exists(r)
  def Ei(r: Iri): TaggedRole = InverseExists(r)

  def Exists(r: Iri): TaggedRole =
    TaggedRole.ER(NamedRole(r))

  def InverseExists(r: Iri): TaggedRole =
    TaggedRole.ER(Inverse(NamedRole(r)))

  // Variants of prefix operators for universal quantification.
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
    ReasonerReference(name, reasoner(state.reasonerInit), noisy)

  // Define a new reasoner (default name).
  def K: ReasonerReference =
    counter += 1
    ReasonerReference(
      "K" ++ counter.toString,
      reasoner(state.reasonerInit),
      noisy
    )

  // == ReasonerReference API on defaultReasoner ==

  def +=(as: AxiomSetBuilder): ReasonerReference = defaultReasoner += as
  def ⩲(as: AxiomSetBuilder): ReasonerReference = +=(as)
  def |-(as: AxiomSetBuilder): Boolean = defaultReasoner |- as
  def ⊢(as: AxiomSetBuilder): Boolean = |-(as)
  def !|-(as: AxiomSetBuilder): Boolean = defaultReasoner |-! as
  def ⊩(as: AxiomSetBuilder): Boolean = !|-(as)
  def show(): Unit = defaultReasoner.show

  // == IO Utility ==

  private def doshow(s: String, level: Int, break: Boolean): Unit =
    print(List.fill(level * 2)(" ").mkString(""))
    println(s)
    if break then println("")

  def show(s: String): Unit = doshow(s, 0, true)
  def show(s: String*): Unit =
    s.foreach(doshow(_, 0, false))
    println("")
  def showfocus(s: String): Unit = doshow(s, 1, true)
  def showfocus(s: String*): Unit =
    s.foreach(doshow(_, 1, false))
    println("")

  // == Implicit conversions ==

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
