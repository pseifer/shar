<p align="center">
  <img width="768" height="320" src="resources/shar-logo.svg?raw=true">
</p>

# Scala + HermiT API and REPL

- [The SHAR API](#the-shar-api)
- [The SHAR REPL](#the-shar-repl)
  - [Command Line Interface](#command-line-interface)
  - [Language](#language)
- [Replacing HermiT](#replacing-hermit)
- [References and Examples](#references-and-examples)

*SHAR* features a collection of abstract data types for description logic expressions and axioms in a robust API for working with the HermiT reasoner (as well as other OWL-API based reasoners), that includes additional tools such as parsers from two DL notations (formal Unicode-based and an ASCII-only variant). Secondly, *SHAR* is also a REPL and interpreter for working with HermiT (or other DL (OWL-API) reasoners) directly via an external DSL for defining axioms (and/or loading OWL ontologies) and checking axioms for entailment and satisfiability.

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

For a concrete project utilizing the *SHAR* API, see also [From Shapes to Shapes](https://github.com/softlang/s2s).

## The SHAR REPL

*SHAR* features a REPL and interpreter that can be most easily invoked via the included script ```shar```. Let us first look at using ```shar``` to interpret script files. Consider a file [kb.shar](resources/kb.shar) containing the following axiom definitions:

```
:NiceChild ⊑ :Child ⊓ :Nice
:Child ⊑ :Person
:Person ⊑ :Agent
:Agent ⊑ ∃:knows.:Agent
```

Invoking ```./shar resources/kb.shar``` parses and then loads these four axioms in HermiT, before terminating and printing nothing. Exciting. So let us extend the *SHAR* source file with a few more lines, introducing some addtional commands and entailment checks ([example.shar](resources/example.shar)):

```
-- This is the knowledge base (and a comment).
:NiceChild ⊑ :Child ⊓ :Nice
:Child ⊑ :Person
:Person ⊑ :Agent
:Agent ⊑ ∃:knows.:Agent
info.

-- These are the tests to be performed.
⊢ :NiceChild ⊑ ∃:knows.:Agent
result.
```

Both "info." and "result." are commands. These commands (recognizable from the "." terminating them) perform certain actions, in this case, "info." prints all preceeding, not-yet-printed axioms added to the KB, and "result." prints the results of all preceeding entailment tests that were not yet printed. Another useful command -- when in a REPL session -- is "help." for help and "quit." to quit. Most commands can be shortened to a single letter (e.g., "i." instead of "info."), though this is again mostly useful in the interactive REPL.

The preceeding document, when passed to ```shar``` (```./shar resources/example.shar```), will print all axioms and the entailment result. It will also terminate with return code '0', because the final entailment is true. If we were only interested in the result of this single entailment, we could also call ```./shar resources/kb.shar --entails ":Child ⊑ :Agent"``` (with the initial four-lined file, otherwise, we would print stuff) and the exit code would indicate the result. We could also make this again more human-readable by using ```./shar resources/kb.shar --entails ":Child ⊑ :Agent" --command "result."```, which will (finally) call the "result." command.

Let's next call ```./shar --repl resources/kb.shar```. This will throw us into an interactive REPL session, after executing the ```kb.shar``` script, that is, the respective axioms are already loaded. Since we supplied a script file, *SHAR* assumes that we want to be in *entailment mode*: Any axiom we enter at the REPL will be checked for entailment. If we want to add additional axioms instead, we can use the command "normal." to return to *normal mode*, where axioms are added, unless prefixed with "⊢", just as when using a script file. Usually, it is easiest to write axioms to a file, load it into the REPL, and then experiment in *entailment mode*. Of course, all commands are available at the REPL as well.

### Command Line Interface

TBD

### Language

TBD

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
- The project [From Shapes to Shapes](https://github.com/softlang/s2s) uses *SHAR*

