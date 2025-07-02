<p align="center">
  <img width="768" height="247" src="resources/shar.png?raw=true">
</p>

# Scala HermiT Algebraic Representation

- [The SHAR API](#the-shar-api)
- [Replacing HermiT](#replacing-hermit)
- [References and Examples](#references-and-examples)

*SHAR* features a collection of algebraic data types for description logic expressions and axioms in a robust API for working with the HermiT reasoner (as well as other OWL-API based reasoners), that includes additional tools such as parsers from two DL notations (formal Unicode-based and an ASCII-only variant).

## The SHAR API

*SHAR* features a collection of abstract data types for representing DL concept expressions and axioms, and interacting with reasoners. Consider the following minimal example, where first the *SHAR* framework is initialized (which can keep track of prefixes and similar state-related things) as well as an empty instance of Hermit:

```scala
@main def example: Unit =

  // Instantiate the SHAR framework...
  val shar = Shar()
  import shar._

  // ...and a HermiT intance.
  val kb = mkHermit()
```

Next, we create a few IRI from Strings, and then add them to the knowledge base; finally, we prove an entailed axiom and print the result:

```scala
  val r = for
    // Parse (and validate) IRIs.
    c1 <- Iri.fromString("<http://example.org#ComputerScientist>")
    c2 <- Iri.fromString("<http://example.org#Scientist>")
    c3 <- Iri.fromString("<http://example.org#Person>")
  yield
    // Add axioms to the KB.
    kb.addAxiom(Subsumption(NamedConcept(c1), NamedConcept(c2)))
    kb.addAxiom(Subsumption(NamedConcept(c2), NamedConcept(c3)))

    // Prove entailment of axiom.
    kb.prove(Subsumption(NamedConcept(c1), NamedConcept(c3)))

  println(r) // prints Right(true)
```

Alternatively, we can also initialize HermiT with an OWL ontology, by giving its IRI to the *SHAR* framework:

```scala
@main def exampleOWL: Unit =

  // Instantiate the SHAR framework...
  for
    ontology <- Iri.fromString("<https://www.w3.org/TR/owl-guide/wine.rdf>")
  do
    val shar = Shar(OntologyInitialization(ontology))
    import shar._
    val kb = mkHermit()
    val wine = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#"

    val r = for
      c1 <- Iri.fromParts(wine, "RedWine")
      c2 <- Iri.fromParts(wine, "Wine")
    yield
      kb.prove(Subsumption(NamedConcept(c1), NamedConcept(c2)))

    println(r) // prints Right(true)
```

For concrete projects utilizing the *SHAR* API, see also [From Shapes to Shapes](https://github.com/softlang/s2s) as well as [SHARDIK](https://github.com/pseifer/shar) -- a description logics REPL (and interpreter) built on *SHAR*.

## Replacing HermiT

When using any OWL-API based reasoner (i.e., a reasoner created by a ```OWLReasonerFactory```), such as JFact or Openllet, the generic ```OwlApiReasoner``` can be used. See the following two examples. Note, that the respective dependencies must be added as well.

```scala
import uk.ac.manchester.cs.jfact.JFactFactory

/** A reasoner that uses JFact. */
val jfact = OwlApiReasoner(JFactFactory())
```

```scala
import openllet.owlapi.OpenlletReasonerFactory

/** A reasoner that uses Openllet. */
val openllet = OwlApiReasoner(OpenlletReasonerFactory())
```

Other custom reasoner implementations can be supplied by instantiating the ```DLReasoner``` trait:

```scala
class FalseReasoner(init: ReasonerInitialization) extends DLReasoner(init):
  def addAxioms(axioms: AxiomSet): Unit = ()
  def prove(axiom: Axiom): Boolean = false
end FalseReasoner

val kb = mkReasoner(FalseReasoner(_))
```

## References and Examples

- [HermiT Reasoner](http://www.hermit-reasoner.com/)
- [Openllet Reasoner](https://github.com/Galigator/openllet)
- [JFact Reasoner](https://jfact.sourceforge.net/)
- [OWL API](https://github.com/owlcs/owlapi)
- [From Shapes to Shapes](https://github.com/softlang/s2s) uses *SHAR*
- The description logic REPL [SHARDIK](https://github.com/pseifer/shardik) uses *SHAR*
