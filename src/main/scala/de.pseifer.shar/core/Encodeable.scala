package de.pseifer.shar.core

import de.pseifer.shar.core.BackendState



/**
 * Encodable things can be shown (user end)
 * or stringyfied in to a parseable form.
 */
trait Encodeable:

  /** 
   * Show a 'pretty' version of this thing,
   * with the purpose of user-end rendering.
   * May rely on config (e.g., to reduce IRI to prefixes).
   */
  def show(state: BackendState): String 

  /**
   * Encode the thing as String, so that in can
   * be parsed back (efficiently).
   */
  def encode: String

  /**
   * toString should rely on encode.
   */
  override def toString: String = encode
