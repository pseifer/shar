package de.pseifer.shar.core

import de.pseifer.shar.core.BackendState

/** Encodable things can be shown (user end) or stringyfied in to a parseable
  * form.
  */
trait Encodeable extends Showable:

  /** Encode the thing as String, so that in can be parsed back (efficiently).
    */
  def encode: String

  /** toString should rely on encode.
    */
  override def toString: String = encode
