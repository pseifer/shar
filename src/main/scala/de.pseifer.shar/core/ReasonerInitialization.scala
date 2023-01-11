package de.pseifer.shar.core

import org.semanticweb.owlapi.reasoner.SimpleConfiguration
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration

/** Initialization for a Reasoner. */
sealed trait ReasonerInitialization:
  def show: String
  val config: OWLReasonerConfiguration

/** Initialization consisting of an OWL ontology (IRI). */
case class OntologyInitialization(
    iri: Iri,
    config: OWLReasonerConfiguration = SimpleConfiguration()
) extends ReasonerInitialization:
  def show = iri.encode

/** Empty initialization (no ontology). */
case class EmptyInitialization(
    config: OWLReasonerConfiguration = SimpleConfiguration()
) extends ReasonerInitialization:
  def show = "()"
