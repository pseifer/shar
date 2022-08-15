package de.pseifer.shar

import de.pseifer.shar.Shar

val shar = Shar()
import shar._

@main def example: Unit =

  // Create a new knowledge base called "C".
  val c = K("C")

  // Add some axioms.
  c ⩲ {
    ":NiceChild" ⊑ (":Child" ⊓ ":Nice")
    ":Child" ⊑ ":Person"
  }

  // Another knowledge base, called "A".
  val a = K("A") ⩲ {
    ":Person" ⊑ ":Agent"
    ":Agent" ⊑ (∃(":knows") ∘ ":Agent")
  }

  // Entailment of an axiom in the union of "C" and "A".
  (c ∪ a) ⊢ {
    ":NiceChild" ⊑ (∃(":knows") ∘ ":Agent")
  }

  // Print axioms in the knowledge base "C".
  c.show
