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
