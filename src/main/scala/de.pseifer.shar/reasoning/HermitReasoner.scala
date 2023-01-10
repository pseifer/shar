package de.pseifer.shar.reasoning

import de.pseifer.shar.core.{
  ReasonerInitialization,
  EmptyInitialization,
  OntologyInitialization,
  PrefixMapping,
  BackendState,
  Iri
}

import de.pseifer.shar.dl._

import org.semanticweb.HermiT.{Configuration, Reasoner => Hermit}
import org.semanticweb.HermiT.Configuration.TableauMonitorType
import org.semanticweb.owlapi.model._

/** A DL reasoner that uses HermiT internally.
  */
class HermitReasoner(
    initialization: ReasonerInitialization,
    configuration: HermitConfiguration = HermitConfiguration(),
    debugging: Boolean = false
) extends OwlApiReasoner(initialization, debugging):

  // Custom reasoner configuration, so we can tell HermiT to ignore
  // 'unsupported' (i.e., non-OWL-2-mapping) data types.
  private val reasonerConfig = Configuration()
  reasonerConfig.ignoreUnsupportedDatatypes = true

  // Monitoring (when debugging is enabled).
  if debugging then
    reasonerConfig.tableauMonitorType = TableauMonitorType.TIMING

  // Set all remainign configurations.
  configuration.set(reasonerConfig)

  private val hermit: Hermit = Hermit(reasonerConfig, ontology)

  protected def rFlush(): Unit = hermit.flush()

  protected def rEntailment(subp: OWLSubObjectPropertyOfAxiom): Boolean =
    hermit.isEntailed(subp)

  protected def rEntailment(sub: OWLSubClassOfAxiom): Boolean =
    hermit.isEntailed(sub)

  protected def rSatisfiability(c: OWLClassExpression): Boolean =
    hermit.isSatisfiable(c)

object HermitReasoner:
  def default: HermitReasoner =
    default(HermitConfiguration(), debugging = false)

  def default(config: HermitConfiguration, debugging: Boolean): HermitReasoner =
    val state: BackendState =
      BackendState(EmptyInitialization, PrefixMapping.default)
    HermitReasoner(state.reasonerInit, config, debugging = debugging)
