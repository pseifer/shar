package de.pseifer.shar.test

// Note: v Only allowed (shar) import for this test module.
import de.pseifer.shar._
// Note: ^ Only allowed (shar) import for this test module.

import org.scalatest.flatspec.AnyFlatSpec

class ApiTests extends AnyFlatSpec with TestConfig:

  // Test the basic functionality of the package-level API.

  "The safe SHAR API" should "be in scope" in:
    // Fully save variant of using SHAR, returning meaningfull errors
    // when anything does not parse.
    val out = for
      // Parse a prefix.
      p <- ":".toPrefix
      // Parse an IRI.
      i <- "<https://github.com/pseifer/shar/>".toIri
      // Initialize the backend with a prefix mapping.
      s = SHAR(PrefixMapping(p -> i))
      // Parse concept expressions.
      c1 <- s.parse(":Person") // ":Person".dl with 'import shar.safe._'
      c2 <- s.parse(":Agent")
      c3 <- s.parse(":Person ⊔ :Agent") // Alternative: Union(c1, c2)
      // Add axioms to the standard HermiT instance.
      _ = s.addAxioms(c1 <:< c2, c2 <:< c1)
    yield
      // Prove axioms using the standard HermiT instance.
      s.prove(c3 =:= c1)

    assert(out.isRight)
    assert(out.getOrElse(false))

  "The convenient SHAR API" should "be in scope" in:
    val prefixes =
      PrefixMapping(":".prefix -> "<https://github.com/pseifer/shar/>".iri)
    val s = SHAR(prefixes)
    import s.unsafe._ // for .dl
    import s.unsafe.given // for conversion from String

    // Prove satisfiability.
    s.prove(~":Person")

    // Implicit conversions.
    s.addAxioms(":Person" <:< ":Agent", ":Agent" <:< ":Person")
    assert(s.prove(":Person" =:= ":Person ⊔ :Agent"))

    // .dl extension method
    s.addAxioms(":Person".dl <:< ":Agent".dl, ":Agent".dl <:< ":Person".dl)
    assert(s.prove(":Person".dl =:= ":Person ⊔ :Agent".dl))

  "The safe implicit SHAR API" should "be in scope" in:
    val prefixes =
      PrefixMapping(
        ":".prefix -> "<https://github.com/pseifer/shar/>".iri
      ) // unsafe
    val s = SHAR(prefixes)
    import s.safe.given

    val out = for
      c1 <- ":Person"
      c2 <- ":Agent"
      _ = s.addAxioms(c1 <:< c2, c2 <:< c1)
    yield s.prove(c1 =:= Union(c1, c2))

    assert(out.isRight)
    assert(out.getOrElse(false))

  // Note: This test case is inactive, because it spams w3.org.

  /*
  "The API" should "work with external ontology" in:
    val s = SHAR(
      prefixes = PrefixMapping(
        ":".prefix -> "<http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>".iri
      ),
      init = OntologyInitialization(
        "<https://www.w3.org/TR/2003/PR-owl-guide-20031215/wine>".iri
      )
    )
    import s.unsafe.given

    assert(s.prove(":RedWine" <:< ":Wine"))
    assert(!s.prove(":RedWine" <:< ":WhiteWine"))
   */

  "Third-party reasoners" should "be supported" in:
    import uk.ac.manchester.cs.jfact.JFactFactory

    val s = SHAR()
    val r = s.mkFromFactory(JFactFactory())

    assert(r.prove(~Top))
    assert(!r.prove(~Bottom))

  "Custom reasoners" should "be supported" in:
    import de.pseifer.shar.core._
    import de.pseifer.shar.reasoning._

    class FalseReasoner(init: ReasonerInitialization) extends DLReasoner(init):
      def addAxioms(axioms: AxiomSet): Unit = ()
      def prove(axiom: Axiom): Boolean = false

    assert(!SHAR().mkReasoner(FalseReasoner(_)).prove(~Top))

    class TrueReasoner(init: ReasonerInitialization) extends DLReasoner(init):
      def addAxioms(axioms: AxiomSet): Unit = ()
      def prove(axiom: Axiom): Boolean = true

    assert(SHAR().mkReasoner(TrueReasoner(_)).prove(~Top))

  "The dl StringContext" should "interpolate values correctly" in:
    val prefixes =
      PrefixMapping(":".prefix -> "<https://github.com/pseifer/shar/>".iri)
    val s = SHAR(prefixes)
    import s.unsafe._ // for .dl
    import s.unsafe.given // for conversion from String

    // Explicit (unsafe).
    assert(dl"⊤".dl == Top)

    // Implicit (must force the Concept context for conversion here).
    assert(~dl"⊤" == Satisfiability(Top))
    assert(~dl"⊤ ⊔ ${Top}" == Satisfiability(Union(Top, Top)))

    val c1 = ":Person".dl
    val c2: Concept = dl":Agent"
    val a = dl"${c1} ⊔ ${c2}" <:< c1
    assert(a == Subsumption(Union(c1, c2), c1))
