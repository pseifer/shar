package de.pseifer.shar.core

import de.pseifer.shar.core.ReasonerInitialization
import de.pseifer.shar.core.{Iri, PrefixMapping}

/** The state of the ScaSpa Backend. Consists of an initialization for the
  * reasoner and all current prefix definitions.
  */
case class BackendState(
    reasonerInit: ReasonerInitialization,
    prefixes: PrefixMapping = PrefixMapping()
)
