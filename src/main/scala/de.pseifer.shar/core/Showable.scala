package de.pseifer.shar.core

trait Showable:

  /** Show a 'pretty' version of this thing, with the purpose of user-end
    * rendering. May rely on config (e.g., to reduce IRI to prefixes).
    */
  def show(implicit state: BackendState): String

end Showable
