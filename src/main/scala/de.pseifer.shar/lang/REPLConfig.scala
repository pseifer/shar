package de.pseifer.shar.lang

import de.pseifer.shar.core.ReasonerInitialization
import de.pseifer.shar.reasoning.DLReasoner
import de.pseifer.shar.core.PrefixMapping
import de.pseifer.shar.reasoning.HermitReasoner
import de.pseifer.shar.core.EmptyInitialization

/** A REPL for description logic knowledgebases.
  *
  * @param reasoner
  *   constructor for a `DLReasoner`; only required if custom instance of
  *   `DLReasoner` is defined
  * @param init
  *   a custom reasoner initialization (e.g., ontology IRI).
  * @param prefixes
  *   a custom, predefined prefix mapping
  * @param defaultIsSharPrefix
  *   use the prefix 'shar:' as default (':')
  * @param noisy
  *   Always output to stdout (e.g., for entailment ⊢),
  *   instead of only for certain commands.
  * @param silent
  *   Supress command outputs.
  */
case class REPLConfig(
    reasoner: ReasonerInitialization => DLReasoner,
    init: ReasonerInitialization,
    prefixes: PrefixMapping,
    defaultIsSharPrefix: Boolean,
    noisy: Boolean,
    silent: Boolean,
    infoline: String
)

/** Some preset configurations. */
object REPLConfig:

  /** Default configuration. This is mainly for internal use.
    */
  def default: REPLConfig = REPLConfig(
    reasoner = HermitReasoner(_),
    init = EmptyInitialization(),
    prefixes = PrefixMapping.default,
    defaultIsSharPrefix = true,
    noisy = false,
    silent = false,
    infoline = "shar"
 )

