package de.pseifer.shar.reasoning

import de.pseifer.shar.dl._

import de.pseifer.shar.core.Encodeable
import de.pseifer.shar.core.Iri
import de.pseifer.shar.core.BackendState

import de.pseifer.shar.error._

/** Wrapper type for a set of Axioms. */
class AxiomSet(private val axioms: Set[Axiom])
    extends DLExpression
    with Encodeable:

  /** Join with another AxiomSet. */
  def join(others: AxiomSet): AxiomSet =
    AxiomSet.join(this, others)

  /** Returns true if the AxiomSet contains no axioms. */
  def isEmpty: Boolean = axioms.isEmpty

  /** Convert to a Seq of Axiom. */
  def getAxiomSeq: Seq[Axiom] = axioms.toSeq

  /** Get all concepts Iris in the AxiomSet. */
  def concepts: Set[Iri] = axioms.flatMap(_.concepts).toSet

  /** Get all property Iris in the AxiomSet. */
  def properties: Set[Iri] = axioms.flatMap(_.properties).toSet

  /** Encode as a parsable String. */
  def encode: String = axioms.map(_.encode).mkString(" ")
  override def show(implicit state: BackendState): String =
    axioms.map(_.show(state)).mkString(", ")

  /** Print for humans. */
  def show(token: String)(implicit state: BackendState): String =
    axioms.map(_.show(state)).mkString(token)

  /** Apply a function to all top-level (!) Concepts (within Axioms) in the
    * AxiomSet. This is not a deep map, unlike Concept.map!
    */
  def map(f: Concept => Concept): AxiomSet =
    AxiomSet(axioms.map { m =>
      m match
        case Subsumption(c, d)            => Subsumption(f(c), f(d))
        case Equality(c, d)               => Equality(f(c), f(d))
        case Satisfiability(c)            => Satisfiability(f(c))
        case rsub @ RoleSubsumption(_, _) => rsub
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

  /** Join two AxiomSet. */
  def join(lhs: AxiomSet, rhs: AxiomSet): AxiomSet =
    AxiomSet(lhs.axioms ++ rhs.axioms)

  /** Initialize an empty AxiomSet. */
  def empty: AxiomSet = AxiomSet(Set())

  def orElse(expr: DLExpression, error: SharError): SharTry[AxiomSet] =
    expr match
      case a: AxiomSet => Right(a)
      case _           => Left(error)
