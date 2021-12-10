package de.pseifer.shar.core

//import java.io.File

/** Initialization for a Reasoner. */
sealed trait ReasonerInitialization:
  def show: String

/** Initialization consisting of an OWL ontology (IRI). */
case class OntologyInitialization(iri: Iri) extends ReasonerInitialization:
  def show = iri.encode

/** Empty initialization (no ontology). */
case object EmptyInitialization extends ReasonerInitialization:
  def show = "()"
