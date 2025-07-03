package de.pseifer.shar.dl

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

  def concepts: Set[Iri] = c.concepts.union(d.concepts)
  def properties: Set[Iri] = c.properties.union(d.properties)

object Subsumption:
  def orElse(expr: DLExpression, error: SharError): SharTry[Subsumption] =
    expr match
      case a: Subsumption => Right(a)
      case _              => Left(error)

case class RoleSubsumption(r: Role, p: Role) extends Axiom:
  override def show(implicit state: BackendState): String =
    r.show(state) ++ " ⊑ " ++ p.show(state)

  def encode: String = r.encode ++ " :<= " ++ p.encode

  def concepts: Set[Iri] = Set()

  def properties: Set[Iri] = r.properties.union(p.properties)

case class Equality(c: Concept, d: Concept) extends Axiom:
  override def show(implicit state: BackendState): String =
    c.show(state) ++ " ≡ " ++ d.show(state)
  def encode: String = c.encode ++ " === " ++ d.encode

  def concepts: Set[Iri] = c.concepts.union(d.concepts)
  def properties: Set[Iri] = c.properties.union(d.properties)

object Equality:
  def orElse(expr: DLExpression, error: SharError): SharTry[Equality] =
    expr match
      case a: Equality => Right(a)
      case _           => Left(error)

case class Satisfiability(c: Concept) extends Axiom:
  override def show(implicit state: BackendState): String =
    c.show(state) ++ " ≢ " ++ Bottom.show(state)
  def encode: String = c.encode

  def concepts: Set[Iri] = c.concepts
  def properties: Set[Iri] = c.properties

object Satisfiability:
  def orElse(expr: DLExpression, error: SharError): SharTry[Satisfiability] =
    expr match
      case a: Satisfiability => Right(a)
      case _                 => Left(error)
