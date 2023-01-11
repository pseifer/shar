<p align="center">
  <img width="768" height="320" src="resources/shar-logo.svg?raw=true">
</p>

# Scala + HermiT API and REPL

```Shar``` features a collection of abstract data types for description logic expressions and axioms in a robust API for working with the HermiT reasoner (as well as other OWL-API based reasoners), additional tools such as parsers from two DL notations (formal and an ASCII-only variant) and a simple DSL for constructing and working with DL knowledge bases.

# API

```Shar``` features a collection of abstract data types for representing DL concept expressions and axioms, including management of prefixes, pretty printing and parsing from multiple concrete syntaxes.

```scala
// TODO: Example for importing some ontology and checking entailment.
```

# DSL

The Shar DSL can be used by instantiating ```Shar()``` and importing the instance.

https://github.com/pseifer/shar/blob/d7f36d3a3487078dbe6e336c62666315959bcd17/examples/DSL.scala#L3-L6

We can now construct a knowledge base and add several axioms:

https://github.com/pseifer/shar/blob/d7f36d3a3487078dbe6e336c62666315959bcd17/examples/DSL.scala#L10-L18

There are various ways of constructing axioms and concept expressions, including the Unicode operators used in this example. Note, that in some instances and due to the operator precedence rules in Scala, additional parentheses are required. Additionally, there are ASCII-only alternative operators (including ```&, |, E, A, >>=, ===, |-``` and ```+=```). Concept expressions can also be parsed in their entirety from Strings, where again full Unicode and ASCII variants exists (see also this [grammar](https://github.com/pseifer/shar/blob/main/src/main/antlr4/DescriptionLogics.g4)). For the sake of simplicity, parsing of IRIs fails with runtime exceptions in the DSL. When using the full ```Shar``` API (see below), proper error handling via ```Try``` is available.

While there is also a default knowledge base (accessible by using operators without left-hand-side arguments), using explicitly named knowledge bases is preferable. Thus, we can construct another knowledge base and then use the union of both knowledge bases for checking entailment of an axiom: 

https://github.com/pseifer/shar/blob/d7f36d3a3487078dbe6e336c62666315959bcd17/examples/DSL.scala#L20-L31

By default, using the entailment operator prints the axiom and result (in addition to returning the result as a Boolean value). Supplying the argument ```noisy=false``` to the ```Shar``` constructor disables this behaviour. A knowledge base can be printed via ```.show```. The DSL also offers functions ```show(s: String*)``` and ```showfocus(s: String*)``` for formatting text output in-between knowledge bases and entailments.

https://github.com/pseifer/shar/blob/d7f36d3a3487078dbe6e336c62666315959bcd17/examples/DSL.scala#L33-L34

Further configuration allows importing ontologies (```init: ReasonerInitilization```) and predefining additional prefixes (```prefixes: PrefixMapping```) via the ```Shar``` constructor. 

## Replacing HermiT

When using any OWL-API based reasoner (i.e., a reasoner created by a ```OWLReasonerFactory```), such as JFact or Openllet, the generic ```OwlApiReasoner``` can be used. See the following two examples. (Note, that the respective dependencies must be added as well.)

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

Other custom reasoner implementations can be supplied by instantiating the ```DLReasoner``` trait.

```scala
class FalseReasoner(init: ReasonerInitialization) extends DLReasoner(init):
  def addAxioms(axioms: AxiomSet): Unit = ()
  def prove(axiom: Axiom): Boolean = false
end FalseReasoner
```

A replacement reasoner can be activated by supplying the ```Shar``` constructor with the optional argument ```reasoner: ReasonerInitialization => DLReasoner```.

```scala
val shar = Shar(FalseReasoner(_))
import shar._
```

# REPL

For now, the ```Shar``` REPL consists of the standard Scala console. A session can be started via

```sh
sbt console
scala> :load repl.scala
```

On Windows Systems, you may want to use ```chcp 65001``` for UTF-8 support.

# References

- [HermiT Reasoner](http://www.hermit-reasoner.com/)
- [Openllet Reasoner](https://github.com/Galigator/openllet)
- [JFact Reasoner](https://jfact.sourceforge.net/)
- [OWL API](https://github.com/owlcs/owlapi)

Shar was developed by Philipp Seifer, Software Languages Team at University Koblenz-Landau, Koblenz, Germany.
