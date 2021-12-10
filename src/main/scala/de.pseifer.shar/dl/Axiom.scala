package de.pseifer.shar.dl

import de.pseifer.shar.core.Encodeable
import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.Iri

import de.pseifer.shar.error._

/** A DL axiom. */
sealed trait Axiom extends DLExpression

object Axiom:
  def orElse(expr: DLExpression, error: SharError): SharTry[Axiom] =
    expr match
      case a: Axiom => Right(a)
      case _        => Left(error)

case class Subsumption(c: Concept, d: Concept) extends Axiom:
  override def show(implicit state: BackendState): String =
    c.show(state) ++ " ⊑ " ++ d.show(state)
  def encode: String = c.encode ++ " :<= " ++ d.encode

object Subsumption:
  def orElse(expr: DLExpression, error: SharError): SharTry[Subsumption] =
    expr match
      case a: Subsumption => Right(a)
      case _              => Left(error)

case class Satisfiability(c: Concept) extends Axiom:
  override def show(implicit state: BackendState): String =
    c.show(state) ++ " ≢ " ++ Bottom.show(state)
  def encode: String = c.encode

object Satisfiability:
  def orElse(expr: DLExpression, error: SharError): SharTry[Satisfiability] =
    expr match
      case a: Satisfiability => Right(a)
      case _                 => Left(error)
