package de.pseifer.shar.test

import de.pseifer.shar.dl._

import org.scalatest.flatspec.AnyFlatSpec

class ConceptTests extends AnyFlatSpec with TestConfig:

  // For systematic tests of encode(), refer to ConceptParserTests.scala

  "Mapping identity over a concept" should "produce the same concept" in:
    forAll(SharGen.genConcept): c =>
      assert(c.map(identity) == c)

  "Map and foreach" should "visit the same number of sub-clauses" in:
    forAll(SharGen.genConcept): c =>
      var count1 = 0
      var count2 = 0

      def f(c: Concept) =
        count1 += 1
        c

      def proc(c: Concept) =
        count2 += 1

      val _ = c.map(f)
      c.foreach(proc)

      assert(count1 == count2)

  "A concept" should "be value-equal to itself" in:
    forAll(SharGen.genConcept): c =>
      assert(c == c)

  "A concept" should "be reasoning-equal to itself" in:
    val hermit = shar.mkHermit()
    forAll(SharGen.genConcept): c =>
      assert(hermit.prove(Equality(c, c)))

  "A concept" should "be reasoning-equal to its simplification" in:
    val hermit = shar.mkHermit()
    forAll(SharGen.genConcept): c =>
      assert(hermit.prove(Equality(c, Concept.simplify(c))))

  "unionOf" should "produce the correct union" in:
    assert(Concept.unionOf(Nil) == Bottom)
    val c = Samples.namedConcept(0)
    assert(Concept.unionOf(List(c)) == c)
    val d = Samples.namedConcept(1)
    assert(Concept.unionOf(List(c, d)) == Union(c, d))
    val e = Samples.namedConcept(2)
    assert(Concept.unionOf(List(c, d, e)) == Union(Union(c, d), e))

  "intersectionOf" should "produce the correct intersection" in:
    assert(Concept.intersectionOf(Nil) == Top)
    val c = Samples.namedConcept(0)
    assert(Concept.intersectionOf(List(c)) == c)
    val d = Samples.namedConcept(1)
    assert(Concept.intersectionOf(List(c, d)) == Intersection(c, d))
    val e = Samples.namedConcept(2)
    assert(
      Concept.intersectionOf(List(c, d, e)) == Intersection(
        Intersection(c, d),
        e
      )
    )

  "Concepts" should "contain the correct concepts and properties" in:
    val c = Samples.namedConcept(0)
    val d = Samples.namedConcept(1)
    val e = Samples.namedConcept(2)
    val f = Samples.namedConcept(3)
    val r = Samples.role(0)
    val s = Samples.role(0)
    assert(c.concepts == Set(c.c))
    assert(Union(c, d).concepts == Set(c.c, d.c))
    assert(Intersection(c, d).concepts == Set(c.c, d.c))
    assert(Complement(c).concepts == Set(c.c))
    assert(GreaterThan(1, r, c).concepts == Set(c.c))
    assert(LessThan(4, r, c).concepts == Set(c.c))
    assert(Exactly(42, r, c).concepts == Set(c.c))

    val big =
      Union(
        Intersection(Complement(c), d),
        Union(Existential(Inverse(r), e), Complement(Universal(s, f)))
      )
    assert(
      big.concepts == Set(
        c.c,
        d.c,
        e.c,
        f.c
      )
    )

    assert(GreaterThan(1, r, c).properties == Set(r.r))
    assert(LessThan(4, r, c).properties == Set(r.r))
    assert(Exactly(42, r, c).properties == Set(r.r))
    assert(big.properties == Set(r.r, s.r))

    assert(Exactly(42, Inverse(r), c).properties == Set(r.r))
    assert(Existential(Inverse(r), c).properties == Set(r.r))
