package de.pseifer.shar.test

import de.pseifer.shar.dl._

import org.scalacheck._
import Gen._

/** Collection of generators for Shar tests. Generation of individual-,
  * concept-, and role-names is limited to a set of predefined names. This is
  * irrelevant for some tests, but useful if, e.g., reasoning is tested, since
  * with random strings, all names would be distinct.
  */
object SharGen:

  def genTop: Gen[Concept] = const(Top)

  def genBottom: Gen[Concept] = const(Bottom)

  def genKnownNominal: Gen[Concept] = oneOf(Samples.nominal)

  def genKnownNamed: Gen[Concept] = oneOf(Samples.namedConcept)

  def genKnownRole: Gen[Role] = oneOf(Samples.role)

  def genInverseRole: Gen[Role] =
    for r <- genKnownRole
    yield Inverse(r)

  def genRole: Gen[Role] =
    oneOf(genKnownRole, genInverseRole)

  def genQualifiedNumberRestriction(gC: Gen[Concept]): Gen[Concept] =
    for
      n <- oneOf(0, 1, 2, 3)
      op <- oneOf(GreaterThan.apply, LessThan.apply, Exactly.apply)
      r <- genRole
      c <- gC
    yield op(n, r, c)

  def genRestriction(gC: Gen[Concept]): Gen[Concept] =
    for
      op <- oneOf(Existential.apply, Universal.apply)
      r <- genRole
      c <- gC
    yield op(r, c)

  def genComplement(gC: Gen[Concept]): Gen[Concept] =
    for c <- gC
    yield Complement(c)

  def genUnion(gC: Gen[Concept]): Gen[Concept] =
    for
      c <- gC
      d <- gC
    yield Union(c, d)

  def genIntersection(gC: Gen[Concept]): Gen[Concept] =
    for
      c <- gC
      d <- gC
    yield Intersection(c, d)

  /** Generate a Concept, that is most likely very small. Best suited for
    * reasoning tests or to initialize a knowledge base with axioms.
    */
  def genSmallConcept: Gen[Concept] =
    frequency(
      (1, lzy(genUnion(genSmallConcept))),
      (1, lzy(genIntersection(genSmallConcept))),
      (1, lzy(genComplement(genSmallConcept))),
      (1, lzy(genQualifiedNumberRestriction(genSmallConcept))),
      (2, genKnownNominal),
      (3, genTop),
      (3, genBottom),
      (20, lzy(genRestriction(genSmallConcept))),
      (20, genKnownNamed)
    )

  /** Generate a Concept, likely to include union or intersection on the top
    * level, but less likely to include deeply nested structures.
    */
  def genConcept: Gen[Concept] =
    frequency(
      (1, genKnownNominal),
      (1, lzy(genQualifiedNumberRestriction(genSmallConcept))),
      (1, genTop),
      (1, genBottom),
      (1, lzy(genComplement(genSmallConcept))),
      (2, lzy(genRestriction(genSmallConcept))),
      (2, genKnownNamed),
      (3, lzy(genUnion(genSmallConcept))),
      (3, lzy(genIntersection(genSmallConcept)))
    )

  /** Generate a Concept that is rather large, with a focus on union and
    * intersection, and many complements. Most suitable for testing performance
    * criteria, rather than functionality.
    */
  def genHugeConcept: Gen[Concept] =
    frequency(
      (1, genKnownNominal),
      (1, lzy(genQualifiedNumberRestriction(genHugeConcept))),
      (1, genTop),
      (1, genBottom),
      (1, lzy(genRestriction(genHugeConcept))),
      (1, genKnownNamed),
      (5, lzy(genComplement(genHugeConcept))),
      (10, lzy(genUnion(genHugeConcept))),
      (10, lzy(genIntersection(genHugeConcept)))
    )
