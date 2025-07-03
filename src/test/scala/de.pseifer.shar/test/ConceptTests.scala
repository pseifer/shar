package de.pseifer.shar.test

import de.pseifer.shar.Shar

import de.pseifer.shar.core._
import de.pseifer.shar.dl._
//import de.pseifer.shar.error.SharTry

//import org.scalacheck._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class ConceptTests extends AnyFlatSpec with ScalaCheckPropertyChecks:

  // Configure ScalaCheck to run with N samples.
  implicit override val generatorDrivenConfig =
    PropertyCheckConfiguration(minSuccessful = 1000)

  private val shar = Shar()
  import shar._
  val hermit = shar.mkHermit()

  // Construct an axiom from a String.
  def force[C](s: String, c: Iri => C): C =
    c(
      Iri
        .fromString("<https://github.com/pseifer/shar/ontology/" ++ s ++ ">")
        .toOption
        .get
    )

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
    forAll(SharGen.genConcept): c =>
      assert(hermit.prove(Equality(c, c)))

  "A concept" should "be reasoning-equal to its simplification" in:
    forAll(SharGen.genConcept): c =>
      assert(hermit.prove(Equality(c, Concept.simplify(c))))

  "unionOf" should "produce the correct union" in:
    assert(Concept.unionOf(Nil) == Bottom)
    val c = SharGen.namedSamples(0)
    assert(Concept.unionOf(List(c)) == c)
    val d = SharGen.namedSamples(1)
    assert(Concept.unionOf(List(c, d)) == Union(c, d))
    val e = SharGen.namedSamples(2)
    assert(Concept.unionOf(List(c, d, e)) == Union(Union(c, d), e))

  "intersectionOf" should "produce the correct intersection" in:
    assert(Concept.intersectionOf(Nil) == Top)
    val c = SharGen.namedSamples(0)
    assert(Concept.intersectionOf(List(c)) == c)
    val d = SharGen.namedSamples(1)
    assert(Concept.intersectionOf(List(c, d)) == Intersection(c, d))
    val e = SharGen.namedSamples(2)
    assert(
      Concept.intersectionOf(List(c, d, e)) == Intersection(
        Intersection(c, d),
        e
      )
    )
