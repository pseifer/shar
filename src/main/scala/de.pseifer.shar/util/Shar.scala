package de.pseifer.shar

import de.pseifer.shar.core._

/** A wrapper and constructor for state.
  *
  * @param init
  *   a custom reasoner initialization (e.g., ontology IRI).
  * @param prefixes
  *   a custom, predefined prefix mapping
  */
class Shar(
    init: ReasonerInitialization = EmptyInitialization(),
    prefixes: PrefixMapping = PrefixMapping.default
):

  implicit val state: BackendState = BackendState(init, prefixes)
