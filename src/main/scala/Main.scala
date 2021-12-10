package de.pseifer.shar

import de.pseifer.shar.dl._
import de.pseifer.shar.reasoning._

import de.pseifer.shar.Shar

@main def miniSharExample: Unit =

  val shar = Shar()
  import shar._

  reasoner.addAxioms(
    AxiomSet(
      Set(
        Subsumption(":NiceChild", ":Child"),
        Subsumption(":Child", ":Person"),
        Subsumption(":Person", ":Agent")
      )
    )
  )

  val sub = Subsumption(":NiceChild & :Person", ":Agent")
  println(sub.show)
  println(reasoner.prove(sub))

  val notsub = Subsumption(":Agent", ":NiceChild")
  println(notsub.show)
  println(reasoner.prove(notsub))
