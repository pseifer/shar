package de.pseifer.shar

import de.pseifer.shar.core.BackendState
import de.pseifer.shar.dl.Axiom
import de.pseifer.shar.reasoning.{AxiomSet, DLReasoner}
import de.pseifer.shar.core.ReasonerInitialization

/** A small wrapper around a reasoner, keeping track of a name and the set of
  * axioms (in AxiomSet representation).
  *
  * @param name
  *   The name of this reasoner
  * @param re
  *   The DLReasoner instance
  * @param noisy
  *   Always output proofs
  * @param state
  *   (implicit) BackendState
  */
case class KnowledgeBase(
    name: String,
    re: DLReasoner,
    noisy: Boolean,
    reasoner: ReasonerInitialization => DLReasoner
)(implicit
    state: BackendState
):

  /** Prove an axiom. */
  def prove(a: Axiom): Boolean = re.prove(a)

  /** Join with custom new name 'rename'. */
  def join(ok: KnowledgeBase, rename: String): KnowledgeBase =
    val newr =
      KnowledgeBase(rename, reasoner(state.reasonerInit), noisy, reasoner)
    newr.addAxioms(this.axioms)
    newr.addAxioms(ok.axioms)
    newr

  /** Join with custom new name 'rename'. */
  def ∪(ok: KnowledgeBase, rename: String): KnowledgeBase =
    join(ok, rename)

  /** Join with default name. */
  def join(ok: KnowledgeBase): KnowledgeBase =
    join(ok, this.name ++ " + " ++ ok.name)

  /** Join with default name. */
  def ∪(ok: KnowledgeBase): KnowledgeBase = join(ok)

  /** Add axioms to the knowledge base. */
  def +=(as: AxiomSetBuilder): KnowledgeBase =
    this.addAxioms(AxiomSet(as.toSet))
    this

  /** Add axioms to the knowledge base. */
  def ⩲(as: AxiomSetBuilder): KnowledgeBase = +=(as)

  /** Test entailment for the knowledge base. Print if noisy is set. */
  def |-(as: AxiomSetBuilder): Boolean =
    val b = as.toSet.map(doEntails(this, _, out = noisy)).forall(_ == true)
    if noisy then println("")
    b

  /** Test entailment for the knowledge base. Print if noisy is set. */
  def ⊢(as: AxiomSetBuilder): Boolean = |-(as)

  /** Test entailment for the knowledge base. Always prints. */
  def |-!(as: AxiomSetBuilder): Boolean =
    val b = as.toSet.map(doEntails(this, _, out = true)).forall(_ == true)
    println("")
    b

  /** Test entailment for the knowledge base. Always prints. */
  def ⊩(as: AxiomSetBuilder): Boolean = |-!(as)

  /** Print the knowledge base. */
  def show: Unit = println(this.toString ++ "\n")

  override def toString: String =
    s"-- $name --\n" ++
      axioms.show("\n") ++
      s"\n---${List.fill(name.size)("-").mkString("")}---"

  /** Check for entailment and output results (if out is set). */
  private def doEntails(r: KnowledgeBase, a: Axiom, out: Boolean): Boolean =
    if out then
      print(r.name)
      print(" ⊢ ")
      print(a.show)
      print(" : ")
    val res = r.prove(a)
    if out then println(res)
    res

  /** The set of axioms. */
  private var axioms: AxiomSet = AxiomSet(Set())

  /** Add axioms to the reasoner. */
  private def addAxioms(as: AxiomSet): Unit =
    axioms = axioms.join(as)
    re.addAxioms(as)

end KnowledgeBase
