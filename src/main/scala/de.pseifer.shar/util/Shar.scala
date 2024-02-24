package de.pseifer.shar.util

import de.pseifer.shar.core._

import de.pseifer.shar.reasoning.HermitReasoner
import de.pseifer.shar.reasoning.HermitConfiguration
import de.pseifer.shar.reasoning.DLReasoner

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

  /** Get a fresh hermit instance. */
  def mkHermit(config: HermitConfiguration = HermitConfiguration()): HermitReasoner = 
    HermitReasoner(init, config)

  /** Get a fresh reasoner instance from mk. */
  def mkReasoner(mk: ReasonerInitialization => DLReasoner): DLReasoner = mk(init)
