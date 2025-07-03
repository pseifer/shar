package de.pseifer.shar.test

import de.pseifer.shar.dl._

import org.scalatest.flatspec.AnyFlatSpec

class AxiomTests extends AnyFlatSpec with TestConfig:

  "Axioms" should "contain the correct concepts and properties" in:
    val c = Samples.namedConcept(0)
    val d = Samples.namedConcept(1)
    val r = Samples.role(0)
    val s = Samples.role(0)

    assert(Subsumption(c, d).concepts == Set(c.c, d.c))
    assert(Equality(c, d).concepts == Set(c.c, d.c))
    assert(Satisfiability(c).concepts == Set(c.c))

    assert(
      Subsumption(Existential(r, c), Universal(s, d)).properties == Set(
        r.r,
        s.r
      )
    )

    assert(
      Equality(Existential(r, c), Universal(s, d)).properties == Set(
        r.r,
        s.r
      )
    )

    assert(Satisfiability(Existential(r, c)).properties == Set(r.r))
    assert(RoleSubsumption(r, s).properties == Set(r.r, s.r))
    assert(RoleSubsumption(r, s).concepts == Set())
