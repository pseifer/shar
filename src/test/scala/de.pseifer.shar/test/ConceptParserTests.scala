package de.pseifer.shar.test

import de.pseifer.shar.Shar

import de.pseifer.shar.core._
import de.pseifer.shar.dl._
import de.pseifer.shar.error.SharTry

import de.pseifer.shar.parsing.ConceptParser

import org.scalacheck._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class ConceptParserTests extends AnyFlatSpec with ScalaCheckPropertyChecks:

  // Configure ScalaCheck to run with N samples.
  implicit override val generatorDrivenConfig =
    PropertyCheckConfiguration(minSuccessful = 10000)

  // Instantiate shar and the parser.

  private val shar = Shar()
  import shar._

  val parser = ConceptParser(state)

  // Construct a valid parse result from a String and Constructor.
  def force[C](s: String, c: Iri => C): SharTry[C] =
    Right(
      c(
        Iri
          .fromString("<https://github.com/pseifer/shar/ontology/" ++ s ++ ">")
          .toOption
          .get
      )
    )

  // Parse multiple Strings against the same target.
  def alternatives[C](target: C, variants: String*) =
    variants.foreach(v => assert(parser.parse(v) == target))

  // Assert that the String 's' may not parse.
  def error(s: String): Unit =
    assert(parser.parse(s).isLeft)

  // Assert that a String 's' parses correctly.
  def success(s: String): Unit =
    assert(parser.parse(s).isRight)

  // ==========================================================================

  "Parsing invalid concept names" should "fail" in:
    // Invalid tokens.
    error("shar:+")
    error("shar:s@meth^ng")
    // No space allowed around ':'
    error("shar: Person")
    error("shar :Person")
    error("shar : Person")
    // Invalid spaces.
    error(">= 3 shar:knows.shar:Person")
    // Invalid braces.
    error("{(shar:tim)}")
    error("∃(shar:knows.shar:Person)")
    error("∃(shar:knows).shar:Person")

  "Parsing with an undefined prefix" should "fail" in:
    error("never:Person")
    error(":Person")
    error("{never:tim}")

  "Top" should "parse as Top" in:
    alternatives(Right(Top), "⊤", "#t")

  "Bottom" should "parse as Bottom" in:
    alternatives(Right(Bottom), "⊥", "#f")

  "A named concept" should "parse as NamedConcept" in:
    alternatives(
      force("Person", NamedConcept.apply),
      "shar:Person",
      "(shar:Person)",
      "( shar:Person )",
      " (shar:Person) ",
      " ( shar:Person ) "
    )

  "A nominal concept" should "parse as NominalConcept" in:
    alternatives(
      force("tim", NominalConcept.apply),
      "{shar:tim}",
      "{ shar:tim }",
      " {shar:tim} ",
      " { shar:tim } ",
      "({shar:tim})"
    )

  "Existential role restrictions" should "parse as Existstential" in:
    alternatives(
      for
        c <- force("Person", NamedConcept.apply)
        r <- force("knows", NamedRole.apply)
      yield Existential(r, c),
      "∃shar:knows.shar:Person",
      "∃ shar:knows . shar:Person",
      "#Eshar:knows.shar:Person",
      "#E shar:knows . shar:Person",
      "∃shar:knows.(shar:Person)",
      "(∃shar:knows.(shar:Person))"
    )

  "Universal role restrictions" should "parse as Universal" in:
    alternatives(
      for
        c <- force("Person", NamedConcept.apply)
        r <- force("knows", NamedRole.apply)
      yield Universal(r, c),
      "∀shar:knows.shar:Person",
      "∀ shar:knows . shar:Person",
      "#Ashar:knows.shar:Person",
      "#A shar:knows . shar:Person",
      "∀shar:knows.(shar:Person)",
      "(∀shar:knows.(shar:Person))"
    )

  ">= restrictions" should "parse as GreaterThan" in:
    alternatives(
      for
        c <- force("Person", NamedConcept.apply)
        r <- force("knows", NamedRole.apply)
      yield GreaterThan(42, r, c),
      "≥42shar:knows.shar:Person",
      "≥42 shar:knows . shar:Person",
      "≥42shar:knows.(shar:Person)",
      "(≥42shar:knows.(shar:Person))",
      "#>42shar:knows.shar:Person",
      "#>42 shar:knows . shar:Person",
      "#>42shar:knows.(shar:Person)",
      "(#>42shar:knows.(shar:Person))"
    )

  "<= restrictions" should "parse as LessThan" in:
    alternatives(
      for
        c <- force("Person", NamedConcept.apply)
        r <- force("knows", NamedRole.apply)
      yield LessThan(42, r, c),
      "≤42shar:knows.shar:Person",
      "≤42 shar:knows . shar:Person",
      "≤42shar:knows.(shar:Person)",
      "(≤42shar:knows.(shar:Person))",
      "#<42shar:knows.shar:Person",
      "#<42 shar:knows . shar:Person",
      "#<42shar:knows.(shar:Person)",
      "(#<42shar:knows.(shar:Person))"
    )

  "== restrictions" should "parse as Exactly" in:
    alternatives(
      for
        c <- force("Person", NamedConcept.apply)
        r <- force("knows", NamedRole.apply)
      yield Exactly(42, r, c),
      "=42shar:knows.shar:Person",
      "=42 shar:knows . shar:Person",
      "=42shar:knows.(shar:Person)",
      "(=42shar:knows.(shar:Person))",
      "#=42shar:knows.shar:Person",
      "#=42 shar:knows . shar:Person",
      "#=42shar:knows.(shar:Person)",
      "(#=42shar:knows.(shar:Person))"
    )

  "Complementary concepts" should "parse as Complement" in:
    alternatives(
      for c <- force("Person", NamedConcept.apply)
      yield Complement(c),
      "¬shar:Person",
      "¬(shar:Person)",
      "¬ shar:Person",
      "¬ (shar:Person)",
      "¬( shar:Person)",
      "¬ ( shar:Person)",
      "!shar:Person",
      "!(shar:Person)",
      "! shar:Person",
      "! (shar:Person)",
      "!( shar:Person)",
      "! ( shar:Person)"
    )

  "The union of two concepts" should "parse as Union" in:
    alternatives(
      for
        c1 <- force("Person", NamedConcept.apply)
        c2 <- force("Agent", NamedConcept.apply)
      yield Union(c1, c2),
      "shar:Person⊔shar:Agent",
      "shar:Person ⊔ shar:Agent",
      "(shar:Person) ⊔ (shar:Agent)",
      "((shar:Person) ⊔ (shar:Agent))",
      "shar:Person|shar:Agent",
      "shar:Person | shar:Agent",
      "(shar:Person) | (shar:Agent)",
      "((shar:Person) | (shar:Agent))"
    )

  "The intersection of two concepts" should "parse as Intersection" in:
    alternatives(
      for
        c1 <- force("Person", NamedConcept.apply)
        c2 <- force("Agent", NamedConcept.apply)
      yield Intersection(c1, c2),
      "shar:Person⊓shar:Agent",
      "shar:Person ⊓ shar:Agent",
      "(shar:Person) ⊓ (shar:Agent)",
      "((shar:Person) ⊓ (shar:Agent))",
      "shar:Person&shar:Agent",
      "shar:Person & shar:Agent",
      "(shar:Person) & (shar:Agent)",
      "((shar:Person) & (shar:Agent))"
    )

  "Pretty printing and parsing" should "produce the same Concept" in:
    forAll(SharGen.genConcept): c =>
      assert(parser.parse(c.encode) == Right(c))
