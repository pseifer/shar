package de.pseifer.shar.test

import de.pseifer.shar.Shar
import de.pseifer.shar.core.Iri

import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalactic.anyvals.PosInt

/** Includes pre-configured ScalaCheckPropertyChecks and a Shar instance. */
trait TestConfig(val samples: PosInt = 100) extends ScalaCheckPropertyChecks:

  /* Available instance of Shar. **/
  val shar = Shar()

  /** Utility function to create unsafe Iri instances in the Samples domain. */
  def force[C](s: String, c: Iri => C): C =
    c(Samples.mkIri(s))

  // Configure ScalaCheck to run with N samples.
  implicit override val generatorDrivenConfig =
    PropertyCheckConfiguration(minSuccessful = samples)
