package de.pseifer.shar.dl

import de.pseifer.shar.core.Encodeable
import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.Iri
import de.pseifer.shar.reasoning.AxiomSet

import de.pseifer.shar.error._

/** A description logics concept.
  */
sealed trait Concept extends DLExpression

/** ⊤
  */
case object Top extends Concept:
  def encode: String = "⊤"

/** ⊥
  */
case object Bottom extends Concept:
  def encode: String = "⊥"

/** <'c'>
  */
final case class NamedConcept(c: Iri) extends Concept:
  def encode: String = c.encode
  override def show(implicit state: BackendState): String = c.show(state)

/** ¬ 'concept'
  */
final case class Complement(concept: Concept) extends Concept:
  def encode: String = format(concept.encode)
  override def show(implicit state: BackendState): String = format(
    concept.show(state)
  )
  private def format(inner: String): String =
    "¬(" + inner + ")"

/** {<'name'>}
  */
final case class NominalConcept(name: Iri) extends Concept:
  def encode: String = "{" ++ name.encode ++ "}"
  override def show(implicit state: BackendState): String =
    "{" ++ name.show(state) ++ "}"

/** <'..scaspa../name/'> Note: A defined concept is only meaningful as the head
  * of a ConceptWithContext (CwC), or within the context of a CwC.
  */
//final case class DefinedConcept(defined: DefinedName) extends Concept:
//  def encode: String = defined.encode
//  override def show(state: BackendState): String = defined.show

/** concept ; axiom* A concept, but it brings some 'context' that is required
  * when reasoning with this concept. Typically used for DefinedConcepts.
  */
final case class ConceptWithContext(defined: Concept, context: AxiomSet)
    extends Concept:
  def encode: String = defined.encode ++ " @ ( " + context.encode + " )"
  override def show(implicit state: BackendState): String = encode

/** 'lhs' ⊔ 'rhs'
  */
final case class Union(lhs: Concept, rhs: Concept) extends Concept:
  def encode: String = format(lhs.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(lhs.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "(" ++ left ++ ")⊔(" ++ right ++ ")"

/** 'lhs' ⊓ 'rhs'
  */
final case class Intersection(lhs: Concept, rhs: Concept) extends Concept:
  def encode: String = format(lhs.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(lhs.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "(" ++ left ++ ")⊓(" ++ right ++ ")"

/** Base form of number restrictions for derived forms. Derived forms are kept
  * at some stages to keep user representation identical, but all
  * NumberRestrictions can be transformed to a basic GreaterThan form.
  */
trait DerivedNumberRestriction extends Concept:
  def toGreaterThan: Concept

/** >=n 'role' . 'rhs' Number restriction
  */
final case class GreaterThan(n: Int, role: Role, rhs: Concept) extends Concept:
  def toGreaterThan = this
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    ">=" ++ n.toString ++ " " ++ left ++ "." + right

/** <=n 'role' . 'rhs' Number restriction
  */
final case class LessThan(n: Int, role: Role, rhs: Concept)
    extends DerivedNumberRestriction:
  def toGreaterThan = Complement(GreaterThan(n + 1, role, rhs))
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "<=" ++ n.toString ++ " " ++ left ++ "." + right

/** ==n 'role' . 'rhs'
  */
final case class Exactly(n: Int, role: Role, rhs: Concept)
    extends DerivedNumberRestriction:
  def toGreaterThan =
    Intersection(GreaterThan(n, role, rhs), LessThan(n, role, rhs))
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "==" ++ n.toString ++ " " ++ left ++ "." + right

/** ∃ 'role' . 'rhs'
  */
final case class Existential(role: Role, rhs: Concept)
    extends DerivedNumberRestriction:
  def toGreaterThan = GreaterThan(1, role, rhs)
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "∃" ++ left ++ "." + right

/** ∀ 'role' . 'rhs'
  */
final case class Universal(role: Role, rhs: Concept)
    extends DerivedNumberRestriction:
  def toGreaterThan = Complement(LessThan(0, role, Complement(rhs)))
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "∀" ++ left ++ "." + right

object Concept:

  def orElse(expr: DLExpression, error: SharError): SharTry[Concept] =
    expr match
      case c: Concept => Right(c)
      case _          => Left(error)

  def intersectionOf(cs: List[Concept]): Concept =
    simplify(cs.foldLeft(Top: Concept)(Intersection.apply))

  def unionOf(cs: List[Concept]): Concept =
    simplify(cs.foldLeft(Bottom: Concept)(Union.apply))

  //def hasDefined(c: Concept, defcon: DefinedConcept): Boolean =
  //  var flag: Boolean = false
  //  def isTheDefcon(c: Concept): Unit = flag = true
  //  Concept.foreach(isTheDefcon, c)
  //  flag

  def map(f: Concept => Concept, concept: Concept): Concept = concept match
    case Complement(i)           => f(Complement(map(f, i)))
    case Existential(r, expr)    => f(Existential(r, map(f, expr)))
    case Universal(r, expr)      => f(Universal(r, map(f, expr)))
    case GreaterThan(n, r, expr) => f(GreaterThan(n, r, map(f, expr)))
    case LessThan(n, r, expr)    => f(LessThan(n, r, map(f, expr)))
    case Union(lexpr, rexpr)     => f(Union(map(f, lexpr), map(f, rexpr)))
    case Intersection(lexpr, rexpr) =>
      f(Intersection(map(f, lexpr), map(f, rexpr)))
    case ConceptWithContext(c, cntx) => f(ConceptWithContext(f(c), cntx.map(f)))
    case _                           => f(concept)

  def foreach(f: Concept => Unit, concept: Concept): Unit = concept match
    case ConceptWithContext(c, cntx) =>
      f(concept)
      foreach(f, c)
    case Complement(i) =>
      f(concept)
      foreach(f, i)
    case Existential(_, expr) =>
      f(concept)
      foreach(f, expr)
    case Universal(_, expr) =>
      f(concept)
      foreach(f, expr)
    case GreaterThan(_, _, expr) =>
      f(concept)
      foreach(f, expr)
    case LessThan(_, _, expr) =>
      f(concept)
      foreach(f, expr)
    case Union(lexpr, rexpr) =>
      foreach(f, lexpr)
      foreach(f, rexpr)
    case Intersection(lexpr, rexpr) =>
      foreach(f, lexpr)
      foreach(f, rexpr)
    case _ => f(concept)

  def simplify(concept: Concept): Concept =
    val s = equivalentConcept(concept)
    if s == concept then s
    else simplify(s)

  // Equivalence rules for concepts.
  def equivalentConcept(concept: Concept): Concept =
    map(
      {
        // Exists
        case Existential(
              NamedRole(r1),
              Existential(Inverse(NamedRole(r2)), Top)
            ) if r1 == r2 =>
          Existential(NamedRole(r1), Top)
        case Existential(
              Inverse(NamedRole(r1)),
              Existential(NamedRole(r2), Top)
            ) if r1 == r2 =>
          Existential(Inverse(NamedRole(r1)), Top)
        // Union
        case Union(expr1, expr2) if expr1 == expr2                    => expr1
        case Intersection(expr1, expr2) if expr1 == expr2             => expr1
        case Intersection(expr1, Complement(expr2)) if expr1 == expr2 => Bottom
        case Intersection(Complement(expr1), expr2) if expr1 == expr2 => Bottom
        case Union(expr1, Complement(expr2)) if expr1 == expr2        => Top
        case Union(Complement(expr1), expr2) if expr1 == expr2        => Top
        // Negation
        case Complement(Complement(expr)) => expr
        // a ^ (a v b) = a
        case Intersection(expr1, Union(expr2, _)) if expr1 == expr2 => expr1
        case Intersection(expr1, Union(_, expr2)) if expr1 == expr2 => expr1
        case Intersection(Union(expr2, _), expr1) if expr1 == expr2 => expr1
        case Intersection(Union(_, expr2), expr1) if expr1 == expr2 => expr1
        // a v (a ^ b) = a
        case Union(expr1, Intersection(expr2, _)) if expr1 == expr2 => expr1
        case Union(expr1, Intersection(_, expr2)) if expr1 == expr2 => expr1
        case Union(Intersection(expr2, _), expr1) if expr1 == expr2 => expr1
        case Union(Intersection(_, expr2), expr1) if expr1 == expr2 => expr1
        // Bottom
        case Complement(Bottom)      => Top
        case Union(Bottom, expr)     => expr
        case Union(expr, Bottom)     => expr
        case Intersection(Bottom, _) => Bottom
        case Intersection(_, Bottom) => Bottom
        // Top
        case Complement(Top)         => Bottom
        case Union(_, Top)           => Top
        case Union(Top, _)           => Top
        case Intersection(Top, expr) => expr
        case Intersection(expr, Top) => expr
        // Else
        case c => c
      },
      concept
    )

//object DefinedConcept:
//  def orElse(expr: DLExpression, error: SharError): SharTry[DefinedConcept] =
//    expr match
//      case dc: DefinedConcept => Right(dc)
//      case _                  => Left(error)
