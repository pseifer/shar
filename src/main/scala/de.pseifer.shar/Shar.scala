package de.pseifer.shar

import de.pseifer.shar.core._
import de.pseifer.shar.dl._
import de.pseifer.shar.error._
import de.pseifer.shar.reasoning._
import de.pseifer.shar.parsing._

class Shar(
    init: ReasonerInitialization = EmptyInitialization,
    prefixes: PrefixMapping = PrefixMapping.default,
    defaultIsSharPrefix: Boolean = true
):

  implicit val state: BackendState = BackendState(init, prefixes)
  val reasoner = HermitReasoner(state.reasonerInit)

  private val parser = ConceptParser(state)

  if defaultIsSharPrefix then
    state.prefixes.add(Prefix.fromString(":").toOption.get, Iri.shar)

  // Convert Strings to Iris, implicitly.
  implicit def stringToIri(s: String): Iri =
    Iri.fromString(Iri.shar.expanded(s)) match
      case Left(e) =>
        throw new RuntimeException("Not an IRI: " ++ s ++ " @ " ++ e.show)
      case Right(i) => i

  // Convert Strings to NamedConcepts, implicitly.
  implicit def stringToConcept(s: String): Concept =
    parser.parse(s) match
      case Left(p)           => throw new RuntimeException(p.show)
      case Right(c: Concept) => c
      case Right(_) => throw new RuntimeException("Shar: Expected a Concept")
