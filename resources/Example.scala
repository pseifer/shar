import de.pseifer.shar.core._
import de.pseifer.shar.dl._

import de.pseifer.shar.util.Shar

@main def example: Unit =

  // Instantiate the SHAR framework...
  val shar = Shar()
  import shar._

  // ...and a HermiT intance.
  val kb = mkHermit()

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
      // Prove entailment of axiom.
      kb.prove(Subsumption(NamedConcept(c1), NamedConcept(c2)))

    println(r) // prints Right(true)

