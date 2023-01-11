package de.pseifer.shar.reasoning

import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.EmptyInitialization
import de.pseifer.shar.core.PrefixMapping
import de.pseifer.shar.core.ReasonerInitialization
import org.semanticweb.owlapi.reasoner.SimpleConfiguration
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory
import org.semanticweb.owlapi.model.OWLOntology

class OwlApiReasoner(
    factory: OWLReasonerFactory,
    initialization: ReasonerInitialization = EmptyInitialization(),
    debugging: Boolean = false
) extends OwlApiReasonerImpl(initialization, debugging):

  protected val reasoner =
    factory.createReasoner(ontology, initialization.config)
