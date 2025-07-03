package de.pseifer.shar.dl

import de.pseifer.shar.core.Encodeable
import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.Iri

/** DLExpressions are one of:
  *
  * @see
  *   Concept
  * @see
  *   Role
  * @see
  *   Axiom
  */
trait DLExpression extends Encodeable:

  /** Default show is the same as encode. May be override where necessary.
    */
  def show(implicit state: BackendState): String = encode

  /** The set of iris that represent concepts in this expression.
    */
  def concepts: Set[Iri]

  /** The set of iris that represent properties in this expression.
    */
  def properties: Set[Iri]
