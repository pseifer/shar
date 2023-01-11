package de.pseifer.shar.reasoning

import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.EmptyInitialization
import de.pseifer.shar.core.PrefixMapping
import de.pseifer.shar.core.ReasonerInitialization
import org.semanticweb.owlapi.reasoner.SimpleConfiguration
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration
import org.semanticweb.owlapi.reasoner.OWLReasoner
import org.semanticweb.owlapi.model.OWLOntology

class OwlApiReasoner(
    factory: (OWLOntology, OWLReasonerConfiguration) => OWLReasoner,
    initialization: ReasonerInitialization,
    debugging: Boolean = false
) extends DLReasonerImpl(initialization, debugging):

  protected val reasoner = factory(ontology, initialization.config)

object OwlApiReasoner:
  def make(
      factory: (OWLOntology, OWLReasonerConfiguration) => OWLReasoner,
      debugging: Boolean = false
  ): OwlApiReasoner =
    val state: BackendState =
      BackendState(EmptyInitialization(), PrefixMapping.default)
    make(factory, state.reasonerInit, debugging)

  def make(
      factory: (OWLOntology, OWLReasonerConfiguration) => OWLReasoner,
      initialization: ReasonerInitialization,
      debugging: Boolean
  ): OwlApiReasoner =
    OwlApiReasoner(factory, initialization, debugging)
    OwlApiReasoner(factory, initialization, debugging)
