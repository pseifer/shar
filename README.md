![S.H.A.R Logo](resources/shar-logo.svg?raw=true "S.H.A.R Logo")

# Usage Example
```scala
package de.pseifer.shar.example

import de.pseifer.shar.dl._
import de.pseifer.shar.reasoning._

import de.pseifer.shar.Shar

@main def miniSharExample: Unit =

  val shar = Shar()
  import shar._

  reasoner.addAxioms(
    AxiomSet(
      Set(
        Subsumption(":Child", ":Person"),
        Subsumption(":Person", ":Agent")
      )
    )
  )

  val sub = Subsumption(":Child & :Person", ":Agent")
  println(sub.show)
  println(reasoner.prove(sub))

  val notsub = Subsumption(":Agent", ":Child")
  println(notsub.show)
  println(reasoner.prove(notsub))
```
