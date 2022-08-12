<p align="center">
  <img width="768" height="320" src="resources/shar-logo.svg?raw=true">
</p>

# Scala + HermiT API and REPL

Shar features a collection of abstract data types for description logics, a wrapper around the HermiT reasoner, additional tools such as parsers from formal two DL notations (formal and an ASCII variant) and a simple DSL for constructing and working with DL knowledge bases.

# Example: Shar DSL
```scala
package de.pseifer.shar

import de.pseifer.shar.Shar

val shar = Shar()
import shar._

@main def example: Unit =

  val example = K += {
    ":NiceChild" ⊑ (":Child" ⊓ ":Nice")
    ":Child" ⊑ ":Person"
    ":Person" ⊑ ":Agent"
  }

  example += {
    ":Dog" ⊑ ":Agent"
    ":Person" ⊑ (∃(":knows") o ":Person")
  }

  example.show

  example |- { ((∃(":knows") o ":NiceChild") ⊓ ":Dog") ⊑ ":Agent" }
```

# Example: Shar API
TBD

# Example: Shar REPL

TBD

# References

[HermiT Reasoner](http://www.hermit-reasoner.com/)

Shar was developed by Philipp Seifer, Software Languages Team at Universtity Koblenz-Landau, Koblenz, Germany.
