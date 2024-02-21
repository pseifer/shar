package de.pseifer.shar.util

import de.pseifer.shar.core._
import de.pseifer.shar.dl._
import de.pseifer.shar.error._
import de.pseifer.shar.reasoning._
import de.pseifer.shar.parsing._

import scala.language.implicitConversions

/** A DSL for description logic knowledgebases.
  *
  * @param init
  *   a custom reasoner initialization (e.g., ontology IRI).
  * @param prefixes
  *   a custom, predefined prefix mapping
  * @param reasoner
  *   constructor for a `DLReasoner`; only required if custom instance of
  *   `DLReasoner` is defined
  * @param defaultIsSharPrefix
  *   use the prefix 'shar:' as default (':')
  * @param noisy
  *   Always output to stdout (e.g., for entailment ⊢)
  */
class SharDSL(
    reasoner: ReasonerInitialization => DLReasoner,
    init: ReasonerInitialization = EmptyInitialization(),
    prefixes: PrefixMapping = PrefixMapping.default,
    defaultIsSharPrefix: Boolean = true,
    noisy: Boolean = true
):

  val shar = Shar(init, prefixes)
  import shar.{state => _, _}

  // Implicit builder for axioms.
  implicit val axiomSetBuilder: AxiomSetBuilder = AxiomSetBuilder()

  implicit val state: BackendState = shar.state

  // Make a KnowledgeBase with a name and standard setup.
  private def mkKB(name: String) =
    KnowledgeBase(name, reasoner(state.reasonerInit), noisy, reasoner)

  // The default reasoner.
  val defaultReasoner = mkKB("K")

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

    /** Subsumption axiom constructed from two concept expressions. */
    def ⊑(d: Concept)(implicit asb: AxiomSetBuilder): AxiomSetBuilder = <<=(d)

    /** Subsumption axiom constructed from two concept expressions. */
    def <<=(d: Concept)(implicit asb: AxiomSetBuilder): AxiomSetBuilder =
      asb.add(Subsumption(c, d))

    /** Equality axiom constructed from two concept expressions. */
    def ≡(d: Concept)(implicit asb: AxiomSetBuilder): AxiomSetBuilder = ===(d)

    /** Equality axiom constructed from two concept expressions. */
    def ===(d: Concept)(implicit asb: AxiomSetBuilder): AxiomSetBuilder =
      asb.add(Equality(c, d))
  }

  // Terminal for axiom set builder.
  def <(a: AxiomSetBuilder): AxiomSet =
    AxiomSet(a.toSet)

  // == Concept DLS ==

  /** Top */
  val ⊤ = Top

  /** Bottom */
  val ⊥ = Bottom

  // Construct concepts from Concept with infix notation.
  extension (c: Concept) {

    /** Concept intersection. */
    def ⊓(d: Concept): Concept = Intersection(c, d)

    /** Concept intersection. */
    def &(d: Concept): Concept = Intersection(c, d)

    /** Concept union. */
    def ⊔(d: Concept): Concept = Union(c, d)

    /** Concept union. */
    def |(d: Concept): Concept = Union(c, d)
  }

  // Quantification

  // Used to tagg a role (for prefix operators).
  enum TaggedRole:
    case ER(r: Role)
    case FR(r: Role)

  // Tagged role and Concept construct Existential/Universal.
  extension (t: TaggedRole) {

    /** ...to concept... */
    def ∘(c: Concept): Concept = o(c)

    /** ...to concept... */
    def o(c: Concept): Concept =
      t match
        case TaggedRole.ER(r) => Existential(r, c)
        case TaggedRole.FR(r) => Universal(r, c)
  }

  // Variants of prefix operators for existential quantification.

  /** Existential quantification over role... */
  def ∃(r: Iri): TaggedRole = Exists(r)

  /** Inverse existential quantification over role... */
  def ∃-(r: Iri): TaggedRole = InverseExists(r)

  /** Existential quantification over role... */
  def E(r: Iri): TaggedRole = Exists(r)

  /** Inverse existential quantification over role... */
  def Ei(r: Iri): TaggedRole = InverseExists(r)

  /** Existential quantification over role... */
  def Exists(r: Iri): TaggedRole =
    TaggedRole.ER(NamedRole(r))

  /** Inverse existential quantification over role... */
  def InverseExists(r: Iri): TaggedRole =
    TaggedRole.ER(Inverse(NamedRole(r)))

  /** Universal quantification over role... */
  def ∀(r: Iri): TaggedRole = Forall(r)

  /** Inverse universal quantification over role... */
  def ∀-(r: Iri): TaggedRole = InverseForall(r)

  /** Universal quantification over role... */
  def A(r: Iri): TaggedRole = Forall(r)

  /** Inverse universal quantification over role... */
  def Ai(r: Iri): TaggedRole = InverseForall(r)

  /** Universal quantification over role... */
  def Forall(r: Iri): TaggedRole =
    TaggedRole.FR(NamedRole(r))

  /** Inverse universal quantification over role... */
  def InverseForall(r: Iri): TaggedRole =
    TaggedRole.FR(Inverse(NamedRole(r)))

  // == Knowledge Base DSL ==

  /** Define a new knowledge base with ```name```. */
  def K(name: String): KnowledgeBase = mkKB(name)

  /** Define a new knowledge base with (fresh) name Kn. */
  def K: KnowledgeBase =
    counter += 1
    mkKB("K" ++ counter.toString)

  // == KnowledgeBase API on defaultReasoner ==

  /** Add axioms to the default knowledge base. */
  def +=(as: AxiomSet): KnowledgeBase = defaultReasoner += as

  /** Add axioms to the default knowledge base. */
  def ⩲(as: AxiomSet): KnowledgeBase = +=(as)

  /** Test entailment for the default knowledge base. Print if noisy is set. */
  def |-(as: AxiomSet): Boolean = defaultReasoner |- as

  /** Test entailment for the default knowledge base. Print if noisy is set. */
  def ⊢(as: AxiomSet): Boolean = |-(as)

  /** Test entailment for the default knowledge base. Always prints. */
  def !|-(as: AxiomSet): Boolean = defaultReasoner |-! as

  /** Test entailment for the default knowledge base. Always prints. */
  def ⊩(as: AxiomSet): Boolean = !|-(as)

  /** Print the default knowledge base. */
  def show(): Unit = defaultReasoner.show

  // == IO Utility ==

  private def doshow(s: String, level: Int): Unit =
    print(List.fill(level)(" ").mkString(""))
    println(s)

  /** Print strings. */
  def show(s: String*): Unit =
    s.foreach(doshow(_, 0))
    if s.nonEmpty then println("")

  /** Print strings indented. */
  def showfocus(s: String*): Unit =
    s.foreach(doshow(_, 2))
    if s.nonEmpty then println("")

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
