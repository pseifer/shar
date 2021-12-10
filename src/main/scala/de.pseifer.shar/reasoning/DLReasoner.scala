package de.pseifer.shar.reasoning

import de.pseifer.shar.dl.Axiom

/** A description logics reasoner, that can prove axioms.
  *
  * @see
  *   Axiom
  * @see
  *   Concept
  * @see
  *   Role
  */
trait DLReasoner:

  /** Prove the axiom 'Axiom'. Optionally accepts a List of additional axioms
    * used as a context. The context is added to the ontology before prooving
    * the axiom, and removed afterwards.
    */
  def prove(axiom: Axiom): Boolean

  /** Add axiom set. */
  def addAxioms(axioms: AxiomSet): Unit

  /** Add axiom. */
  def addAxiom(axiom: Axiom): Unit
