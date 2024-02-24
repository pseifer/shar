package de.pseifer.shar.util

import de.pseifer.shar.core.BackendState
import de.pseifer.shar.dl.Axiom
import de.pseifer.shar.reasoning.{AxiomSet, DLReasoner, HermitReasoner}
import de.pseifer.shar.core.ReasonerInitialization

/** A small wrapper around a reasoner, keeping track of a name and the set of
  * axioms (in AxiomSet representation).
  *
  * @param name
  *   The name of this reasoner
  * @param re
  *   The DLReasoner instance
  * @param state
  *   (implicit) BackendState
  */
case class KnowledgeBase(
    mkReasoner: ReasonerInitialization => DLReasoner = HermitReasoner(_)
)(implicit
    state: BackendState
):

  // The reasoner.
  val reasoner = mkReasoner(state.reasonerInit)

  /** Prove an axiom. */
  def prove(a: Axiom): Boolean = reasoner.prove(a)

  /** Join with custom new name 'rename'. */
  def join(ok: KnowledgeBase): KnowledgeBase =
    KnowledgeBase(mkReasoner)
      .addAxioms(this.axioms)
      .addAxioms(ok.axioms)

  /** Join with custom new name 'rename'. */
  def ∪(ok: KnowledgeBase): KnowledgeBase = join(ok)

  /** Add axioms to the knowledge base. */
  def +=(as: AxiomSet): KnowledgeBase = addAxioms(as)

  /** Add axioms to the knowledge base. */
  def ⩲(as: AxiomSet): KnowledgeBase = +=(as)

  /** Add axiom to the knowledge base. */
  def +=(a: Axiom): KnowledgeBase = addAxioms(AxiomSet(Set(a)))

  /** Add axiom to the knowledge base. */
  def ⩲(as: Axiom): KnowledgeBase = +=(as)

  /** Test entailment for the knowledge base. */
  def |-(as: AxiomSet): Boolean =
    as.getAxiomSeq.map(prove(_)).forall(_ == true)

  /** Test entailment for the knowledge base. */
  def ⊢(as: AxiomSet): Boolean = |-(as)

  /** Test entailment for the knowledge base. */
  def |-(a: Axiom): Boolean = |-(AxiomSet(Set(a)))

  /** Test entailment for the knowledge base. */
  def ⊢(as: Axiom): Boolean = |-(as)

  /** Print the knowledge base. */
  def show: String = this.toString

  override def toString: String = axioms.show("\n")

  /** The set of axioms. */
  private var axioms: AxiomSet = AxiomSet(Set())

  /** Add axioms to the reasoner. */
  private def addAxioms(as: AxiomSet): KnowledgeBase =
    axioms = axioms.join(as)
    reasoner.addAxioms(as)
    this

