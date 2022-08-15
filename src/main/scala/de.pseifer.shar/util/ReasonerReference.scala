package de.pseifer.shar

import de.pseifer.shar.core.BackendState
import de.pseifer.shar.dl.Axiom
import de.pseifer.shar.reasoning.{AxiomSet, DLReasoner}

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
case class ReasonerReference(name: String, re: DLReasoner, noisy: Boolean)(
    implicit state: BackendState
):

  /** Prove an axiom. */
  def prove(a: Axiom): Boolean = re.prove(a)

  /** Join with custom new name 'rename'. */
  def join(ok: ReasonerReference, rename: String): ReasonerReference =
    //val newr = ReasonerReference(rename, reasoner(state.reasonerInit))
    val newr = this.copy(name = rename)
    newr.addAxioms(this.axioms)
    newr.addAxioms(ok.axioms)
    newr

  /** Join with custom new name 'rename'. */
  def ∪(ok: ReasonerReference, rename: String): ReasonerReference =
    join(ok, rename)

  /** Join with default name. */
  def join(ok: ReasonerReference): ReasonerReference =
    join(ok, this.name ++ " + " ++ ok.name)

  /** Join with default name. */
  def ∪(ok: ReasonerReference): ReasonerReference = join(ok)

  /** Add axioms to the reasoner. */
  def +=(as: AxiomSetBuilder): ReasonerReference =
    this.addAxioms(AxiomSet(as.toSet))
    this

  /** Add axioms to the reasoner. */
  def ⩲(as: AxiomSetBuilder): ReasonerReference = +=(as)

  /** Entailment. */
  def |-(as: AxiomSetBuilder): Boolean =
    val b = as.toSet.map(doEntails(this, _, out = noisy)).forall(_ == true)
    if noisy then println("")
    b

  /** Entailment. */
  def ⊢(as: AxiomSetBuilder): Boolean = |-(as)

  /** Entailment (and always print). */
  def |-!(as: AxiomSetBuilder): Boolean =
    val b = as.toSet.map(doEntails(this, _, out = true)).forall(_ == true)
    println("")
    b

  /** Entailment (and always print). */
  def ⊩(as: AxiomSetBuilder): Boolean = |-!(as)

  /** Pretty print. */
  def show: Unit = println(this.toString)

  /** Check for entailment and output results (if out is set). */
  private def doEntails(r: ReasonerReference, a: Axiom, out: Boolean): Boolean =
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

  override def toString: String =
    s"-- $name --\n" ++
      axioms.show("\n") ++
      s"\n---${List.fill(name.size)("-").mkString("")}---\n"

end ReasonerReference
