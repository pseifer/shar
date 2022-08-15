<p align="center">
  <img width="768" height="320" src="resources/shar-logo.svg?raw=true">
</p>

# Scala + HermiT API and REPL

Shar features a collection of abstract data types for description logic expressions, a wrapper around the HermiT reasoner, additional tools such as parsers from two DL notations (formal and an ASCII-only variant) and a simple DSL for constructing and working with DL knowledge bases.

# DSL

The Shar DSL can be used by instantiating ```Shar()``` and importing the instance. 

https://github.com/pseifer/shar/blob/dc4c6d02fd3d966cb803336a873204ff4a8e1056/src/main/scala/Main.scala#L3-L6

We can now construct a knowledge base and add several axioms:

https://github.com/pseifer/shar/blob/dc4c6d02fd3d966cb803336a873204ff4a8e1056/src/main/scala/Main.scala#L10-L17

There are various ways of constructing axioms and concept expressions, including the Unicode operators used in this example. Note, that in some instances and due to the operator precedence rules in Scala, additional parentheses are required. Additionally, there are ASCII-only alternative operators (including ```&, |, E, A, >>=, ===, |-``` and ```+=```). Concept expressions can also be parsed in their entirety from Strings, where again full Unicode and ASCII variants exists (see also this [grammar](https://github.com/pseifer/shar/blob/main/src/main/antlr4/DescriptionLogics.g4)). For the sake of simplicity, parsing of IRIs fails with runtime exceptions in the DSL. When using the full ```Shar``` API (see below), proper error handling via ```Try``` is available.

While there is also a default knowledge base (accessible by using operators without left-hand-side arguments), using explicitly named knowledge bases is preferrable. Thus, we can construct another knowledge base and then use the union of both knowledge bases for checking entailment of an axiom: 

https://github.com/pseifer/shar/blob/dc4c6d02fd3d966cb803336a873204ff4a8e1056/src/main/scala/Main.scala#L19-L28

By default, using the entailment operator prints the axiom and result (in addition to returning the result as a boolean value). Supplying the argument ```noisy=false``` to the ```Shar``` constructor disables this behaviour. A knowledge base can be printed via:

https://github.com/pseifer/shar/blob/dc4c6d02fd3d966cb803336a873204ff4a8e1056/src/main/scala/Main.scala#L30-L31

Further configuration allows importing ontologies (```init: ReasonerInitilization```) and predefining additional prefixes (```prefixes: PrefixMapping```) via the ```Shar``` constructor. 

## Replacing HermiT

Custom reasoner implementations can be supplied by instantiating the ```DLReasoner``` trait and supplying the ```Shar``` constructor with the optional argument ```reasoner: ReasonerInitialization => DLReasoner```.

```scala
class FalseReasoner(init: ReasonerInitialization) extends DLReasoner(init):
  def addAxioms(axioms: AxiomSet): Unit = ()
  def prove(axiom: Axiom): Boolean = false
end FalseReasoner

val shar = Shar(reasoner = FalseReasoner(_))
import shar._
```

# API

TBD

# REPL

TBD

# References

[HermiT Reasoner](http://www.hermit-reasoner.com/)

Shar was developed by Philipp Seifer, Software Languages Team at Universtity Koblenz-Landau, Koblenz, Germany.
