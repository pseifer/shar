package de.pseifer.shar

import de.pseifer.shar.core._

import de.pseifer.shar.reasoning.HermitReasoner
import de.pseifer.shar.reasoning.HermitConfiguration
import de.pseifer.shar.reasoning.DLReasoner
import de.pseifer.shar.parsing.ConceptParser
import de.pseifer.shar.error.GenericParseError
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory
import de.pseifer.shar.reasoning.OwlApiReasoner

/** A wrapper and constructor for state.
  *
  * @param init
  *   a custom reasoner initialization (e.g., ontology IRI).
  * @param prefixes
  *   a custom, predefined prefix mapping
  */
class Shar(
    prefixes: PrefixMapping = PrefixMapping.default,
    init: ReasonerInitialization = EmptyInitialization()
):

  /** Pre-configured, standard HermiT instance. */
  lazy val hermit = mkHermit()

  /** Prove an Axiom with the default HermiT instance. */
  def prove(axiom: Axiom): Boolean =
    hermit.prove(axiom)

  /** Add axioms to the default HermiT instance. */
  def addAxioms(axiom: Axiom*): Unit =
    hermit.addAxioms(axiom: _*)

  /** Get a fresh HermiT instance. */
  def mkHermit(
      config: HermitConfiguration = HermitConfiguration()
  ): HermitReasoner =
    HermitReasoner(init, config)

  /** Get a fresh DLReasoner instance from mk. */
  def mkReasoner(mk: ReasonerInitialization => DLReasoner): DLReasoner =
    mk(init)

  /** Get a reasoner from a factory, directly */
  def mkFromFactory(factory: OWLReasonerFactory): DLReasoner =
    mkReasoner(OwlApiReasoner(factory, _))

  /** Internal configuration. */
  implicit val state: BackendState = BackendState(init, prefixes)

  /** A configured parser instance. */
  private lazy val parser = ConceptParser(state)

  /** Parse a Concept. */
  def parse(s: String): SharTry[Concept] =
    parser.parse(s) match
      case Left(err)         => Left(err)
      case Right(v: Concept) => Right(v)
      case Right(_)          => Left(GenericParseError("Not a Concept."))

  /** Defines given Conversion from String to SharTry[Concept]. */
  object safe:

    extension (s: String)
      /** Safe parsing of a String. */
      def dl: SharTry[Concept] = parse(s)

    extension (c: ConceptComposer)
      /** Safe parsing of a String. */
      def dl: SharTry[Concept] = parse(c.show(state))

    /** Implicit, safe conversion from String to Concept. */
    given scala.Conversion[String, SharTry[Concept]] with
      def apply(s: String): SharTry[Concept] = s.dl

    /** Implicit, safe conversion from ConceptComposer to Concept. */
    given scala.Conversion[ConceptComposer, SharTry[Concept]] with
      def apply(c: ConceptComposer): SharTry[Concept] = c.dl

  /** Defines given Conversion from String to Concept. */
  object unsafe:

    extension (s: String)
      /** Unsafe. Convert a String to a Concept. */
      def dl: Concept =
        parse(s) match
          case Left(err) => throw new RuntimeException(err.toString)
          case Right(v)  => v

    extension (c: ConceptComposer)
      /** Unsafe. Convert a String to a Concept. */
      def dl: Concept =
        parse(c.show(state)) match
          case Left(err) => throw new RuntimeException(err.toString)
          case Right(v)  => v

    /** Implicit, unsafe conversion from String to Concept. */
    given scala.Conversion[String, Concept] with
      def apply(s: String): Concept = s.dl

    /** Implicit, unsafe conversion from ConceptComposer to Concept. */
    given scala.Conversion[ConceptComposer, Concept] with
      def apply(c: ConceptComposer): Concept = c.dl
