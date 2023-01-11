package de.pseifer.shar.reasoning

import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.EmptyInitialization
import de.pseifer.shar.core.PrefixMapping
import de.pseifer.shar.core.ReasonerInitialization
import org.semanticweb.HermiT.Configuration
import org.semanticweb.HermiT.Configuration.TableauMonitorType
import org.semanticweb.HermiT.{Reasoner => Hermit}
import org.semanticweb.owlapi.model._

/** A DL reasoner that uses HermiT internally.
  */
class HermitReasoner(
    initialization: ReasonerInitialization = EmptyInitialization(),
    configuration: HermitConfiguration = HermitConfiguration(),
    debugging: Boolean = false
) extends OwlApiReasonerImpl(initialization, debugging):

  // Custom reasoner configuration, so we can tell HermiT to ignore
  // 'unsupported' (i.e., non-OWL-2-mapping) data types.
  private val reasonerConfig = Configuration()
  reasonerConfig.ignoreUnsupportedDatatypes = true

  // Monitoring (when debugging is enabled).
  if debugging then
    reasonerConfig.tableauMonitorType = TableauMonitorType.TIMING

  // Set all remainign configurations.
  configuration.set(reasonerConfig)

  protected val reasoner: Hermit = Hermit(reasonerConfig, ontology)
