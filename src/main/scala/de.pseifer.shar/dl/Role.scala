package de.pseifer.shar.dl

import de.pseifer.shar.core.Encodeable
import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.Iri

import de.pseifer.shar.error._

/** A description logics role expression.
  */
sealed trait Role extends DLExpression

object Role:
  def orElse(expr: DLExpression, error: SharError): SharTry[Role] =
    expr match
      case c: Role => Right(c)
      case _       => Left(error)

/** 'r'
  */
case class NamedRole(r: Iri) extends Role:
  def encode: String = r.encode
  override def show(implicit state: BackendState): String = r.show(state)

/**   - 'role'
  */
case class Inverse(role: Role) extends Role:
  def encode: String = "-" + role.encode
  override def show(implicit state: BackendState): String =
    "-" + role.show(state)

///**
// * data-role 'r'
// */
//case class Data(r: Iri) extends Role
//  def encode: String = r.encode
//  override def show(state: BackendState): String = r.show(state)
