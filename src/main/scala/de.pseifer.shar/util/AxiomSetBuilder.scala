package de.pseifer.shar.util

import de.pseifer.shar.dl.Axiom

import scala.collection.mutable.{ListBuffer => L}

/** Build a set of axioms.
  *
  * @param lb
  *   the mutable list of Axioms
  */
class AxiomSetBuilder(lb: L[Axiom] = L()):

  /** Convert to a set (resetting lb). */
  def toSet: Set[Axiom] =
    val s = lb.toSet
    lb.clear()
    s

  /** Add an axiom and return builder. */
  def add(a: Axiom): AxiomSetBuilder =
    lb += a
    this

end AxiomSetBuilder
