package de.pseifer.shar.dl

import de.pseifer.shar.core.Encodeable
import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.Iri

import de.pseifer.shar.error._

/** DLExpressions are:
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
