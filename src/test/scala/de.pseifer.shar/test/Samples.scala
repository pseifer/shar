package de.pseifer.shar.test

import de.pseifer.shar.core.Iri
import de.pseifer.shar.dl._

/** Collection of sample instances for SharGen and test cases. */
object Samples:

  /** Nominal concepts 'a' through 'e'. */
  val nominal: List[NominalConcept] = List(
    NominalConcept(mkIri("a")),
    NominalConcept(mkIri("b")),
    NominalConcept(mkIri("c")),
    NominalConcept(mkIri("d")),
    NominalConcept(mkIri("e"))
  )

  /** Named concepts 'A' through 'E'. */
  val namedConcept: List[NamedConcept] = List(
    NamedConcept(mkIri("A")),
    NamedConcept(mkIri("B")),
    NamedConcept(mkIri("C")),
    NamedConcept(mkIri("D")),
    NamedConcept(mkIri("E"))
  )

  /** Role names 'r' through 'v'. */
  val role: List[NamedRole] = List(
    NamedRole(mkIri("r")),
    NamedRole(mkIri("s")),
    NamedRole(mkIri("t")),
    NamedRole(mkIri("u")),
    NamedRole(mkIri("v"))
  )

  /** Prefix used in the sample domain. */
  val prefix: String = "https://github.com/pseifer/shar/ontology/"

  /** Create an Iri (unsafe) in the sample domain. */
  def mkIri(s: String): Iri =
    Iri.fromString("<" ++ prefix ++ s ++ ">").toOption match
      case Some(i) => i
      case None    =>
        throw new RuntimeException("Invalid IRI in test case (mkIri).")
