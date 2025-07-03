package de.pseifer.shar.core

/** The state of the Shar Backend. Consists of an initialization for the
  * reasoner and all current prefix definitions.
  */
case class BackendState(
    reasonerInit: ReasonerInitialization,
    prefixes: PrefixMapping = PrefixMapping()
)
