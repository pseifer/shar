package de.pseifer

import de.pseifer.shar.core.BackendState

/** TODO Document */
package object shar:

  // Re-export the Shar manager and related things.

  val SHAR = de.pseifer.shar.Shar

  val OntologyInitialization = de.pseifer.shar.core.OntologyInitialization
  val EmptyInitialization = de.pseifer.shar.core.EmptyInitialization
  val PrefixMapping = de.pseifer.shar.core.PrefixMapping

  type SharError = de.pseifer.shar.error.SharError
  type SharTry[T] = de.pseifer.shar.error.SharTry[T]

  // Re-export the core DL API.

  type Iri = de.pseifer.shar.core.Iri
  val Iri = de.pseifer.shar.core.Iri

  type Prefix = de.pseifer.shar.core.Prefix
  val Prefix = de.pseifer.shar.core.Prefix

  type Concept = de.pseifer.shar.dl.Concept

  val Top = de.pseifer.shar.dl.Top
  val Bottom = de.pseifer.shar.dl.Bottom

  val NamedConcept = de.pseifer.shar.dl.NamedConcept
  val NominalConcept = de.pseifer.shar.dl.NominalConcept

  val Existential = de.pseifer.shar.dl.Existential
  val Universal = de.pseifer.shar.dl.Universal
  val LessThan = de.pseifer.shar.dl.LessThan
  val Exactly = de.pseifer.shar.dl.Exactly
  val GreaterThan = de.pseifer.shar.dl.GreaterThan

  val Complement = de.pseifer.shar.dl.Complement

  val Union = de.pseifer.shar.dl.Union
  val Intersection = de.pseifer.shar.dl.Intersection

  type Axiom = de.pseifer.shar.dl.Axiom

  val Subsumption = de.pseifer.shar.dl.Subsumption
  val Equality = de.pseifer.shar.dl.Equality
  val RoleSubsumption = de.pseifer.shar.dl.RoleSubsumption
  val Satisfiability = de.pseifer.shar.dl.Satisfiability

  // Concept extension methods for Axiom construction.

  extension (c: Concept)
    /** Subsumption */
    def <:<(d: Concept): Axiom = Subsumption(c, d)

    /** Equality. */
    def =:=(d: Concept): Axiom = Subsumption(c, d)

    /** Satisfiability. */
    def unary_~ = Satisfiability(c)

  // String extension methods for IRI and Prefix.

  extension (s: String)

    /** Safe parsing of an Iri. */
    def toIri: SharTry[Iri] = Iri.fromString(s)

    /** Unsafe. Convert a String to an Iri. */
    def iri: Iri =
      s.toIri.toOption match
        case None        => throw new RuntimeException("Invalid IRI: " ++ s)
        case Some(value) => value

    /** Safe parsing of a Prefix. */
    def toPrefix: SharTry[Prefix] = Prefix.fromString(s)

    /** Unsafe. Convert a String to a Prefix. */
    def prefix: Prefix =
      Prefix.fromString(s).toOption match
        case None        => throw new RuntimeException("Invalid Prefix: " ++ s)
        case Some(value) => value

  // String context for splicing DL expressions.

  /** Helper class for d"" StringContext. */
  case class ConceptComposer(parts: Seq[String], spliced: Seq[Concept]):
    def show(state: BackendState): String =
      // Interleave both lists, adding a final empty string to
      // the splice values, since |c.parts| = |c.splices| + 1.
      // The c.spliced values are String encoded using _.show(state).
      List(parts, spliced.map(_.show(state)) ++ List("")).transpose.flatten
        .mkString("")

  implicit class ConceptHelper(private val sc: StringContext) extends AnyVal:
    /** Unsafe (!) conversion from String via dl"" StringContext. */
    def dl(args: Concept*): ConceptComposer =
      ConceptComposer(sc.parts, args)
