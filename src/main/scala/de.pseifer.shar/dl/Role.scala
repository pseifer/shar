package de.pseifer.shar.dl

import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.Iri

import de.pseifer.shar.error._

/** A description logics role expression.
  */
sealed trait Role extends DLExpression:
  def concepts: Set[Iri] = Set()

object Role:

  /** Helper function for constructing internal parse errors, lifting
    * DLExpressions to Role, or a given SharError (if they are not Role).
    */
  def orElse(expr: DLExpression, error: SharError): SharTry[Role] =
    expr match
      case c: Role => Right(c)
      case _       => Left(error)

/** A basic role with name 'r'.
  */
case class NamedRole(r: Iri) extends Role:
  def encode: String = r.encode
  override def show(implicit state: BackendState): String = r.show(state)

  def properties: Set[Iri] = Set(r)

/** The inverse of another role.
  */
case class Inverse(role: Role) extends Role:
  def encode: String = "-" + role.encode
  override def show(implicit state: BackendState): String =
    "-" + role.show(state)

  def properties: Set[Iri] = role.properties
