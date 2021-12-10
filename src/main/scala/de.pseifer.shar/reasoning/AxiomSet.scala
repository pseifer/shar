package de.pseifer.shar.reasoning

import de.pseifer.shar.dl._

import de.pseifer.shar.core.Encodeable
import de.pseifer.shar.core.Iri
import de.pseifer.shar.core.BackendState

import de.pseifer.shar.error._

// A set of axioms.
class AxiomSet(private val axioms: Set[Axiom])
    extends DLExpression
    with Encodeable:
  def join(others: AxiomSet): AxiomSet =
    AxiomSet.join(this, others)
  def isEmpty: Boolean = axioms.isEmpty
  def getAxiomSeq: Seq[Axiom] = axioms.toSeq

  def encode: String = axioms.map(_.encode).mkString(" ")
  override def show(implicit state: BackendState): String =
    axioms.map(_.show(state)).mkString("\n")

  def map(f: Concept => Concept): AxiomSet =
    AxiomSet(axioms.map { m =>
      m match
        case Subsumption(c, d) => Subsumption(f(c), f(d))
        case a                 => a
    })

  def canEqual(a: Any) = a.isInstanceOf[AxiomSet]

  override def equals(that: Any): Boolean =
    that match
      case that: AxiomSet =>
        this.axioms == that.axioms
      case _ => false

  override def hashCode: Int =
    this.axioms.hashCode

object AxiomSet:
  def join(lhs: AxiomSet, rhs: AxiomSet): AxiomSet =
    AxiomSet(lhs.axioms ++ rhs.axioms)
  def empty: AxiomSet = AxiomSet(Set())
  def orElse(expr: DLExpression, error: SharError): SharTry[AxiomSet] =
    expr match
      case a: AxiomSet => Right(a)
      case _           => Left(error)
