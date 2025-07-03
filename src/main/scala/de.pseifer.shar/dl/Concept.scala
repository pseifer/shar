package de.pseifer.shar.dl

import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.Iri

import de.pseifer.shar.error._

/** A description logics concept.
  */
sealed trait Concept extends DLExpression:
  def concepts: Set[Iri] = Set()
  def properties: Set[Iri] = Set()

  def map(f: Concept => Concept): Concept =
    Concept.map(f, this)

  def foreach(proc: Concept => Unit): Unit =
    Concept.foreach(proc, this)

/** The Concept Top, printing as ⊤.
  */
case object Top extends Concept:
  def encode: String = "⊤"

/** The Concept Bottom, printing as ⊥.
  */
case object Bottom extends Concept:
  def encode: String = "⊥"

/** A named concept <'c'>.
  */
final case class NamedConcept(c: Iri) extends Concept:
  def encode: String = c.encode
  override def show(implicit state: BackendState): String = c.show(state)

  override def concepts: Set[Iri] = Set(c)

/** A nominal concept, printing as {<'name'>}.
  */
final case class NominalConcept(name: Iri) extends Concept:
  def encode: String = "{" ++ name.encode ++ "}"
  override def show(implicit state: BackendState): String =
    "{" ++ name.show(state) ++ "}"

/** The complement of any concept C, printing as ¬C.
  */
final case class Complement(concept: Concept) extends Concept:
  def encode: String = format(concept.encode)
  override def show(implicit state: BackendState): String = format(
    concept.show(state)
  )
  private def format(inner: String): String =
    "¬(" + inner + ")"

  override def concepts: Set[Iri] = concept.concepts

/** The union of two concepts C and D, printing as C ⊔ D.
  */
final case class Union(lhs: Concept, rhs: Concept) extends Concept:
  def encode: String = format(lhs.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(lhs.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "(" ++ left ++ ")⊔(" ++ right ++ ")"

  override def concepts: Set[Iri] = lhs.concepts.union(rhs.concepts)
  override def properties: Set[Iri] = lhs.properties.union(rhs.properties)

/** The intersection of two concepts C and D, printing as C ⊓ D.
  */
final case class Intersection(lhs: Concept, rhs: Concept) extends Concept:
  def encode: String = format(lhs.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(lhs.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "(" ++ left ++ ")⊓(" ++ right ++ ")"

  override def concepts: Set[Iri] = lhs.concepts.union(rhs.concepts)
  override def properties: Set[Iri] = lhs.properties.union(rhs.properties)

/** Base form of number restrictions for derived forms. Derived forms are kept
  * at some stages to keep user representation identical, but all
  * NumberRestrictions can be transformed to a basic GreaterThan form.
  */
sealed trait DerivedNumberRestriction extends Concept:
  def toGreaterThan: Concept
  override def concepts: Set[Iri] = this.toGreaterThan.concepts
  override def properties: Set[Iri] = this.toGreaterThan.properties

/** A qualified number restriction, requiring at least n role names r to the
  * concept C, printing as >=n r.C.
  */
final case class GreaterThan(n: Int, role: Role, rhs: Concept) extends Concept:
  def toGreaterThan = this
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "≥" ++ n.toString ++ " " ++ left ++ ".(" + right + ")"

  override def concepts: Set[Iri] = rhs.concepts
  override def properties: Set[Iri] = role.properties

/** A qualified number restriction, requiring at most n role names r to the
  * concept C, printing as <=n r.C.
  */
final case class LessThan(n: Int, role: Role, rhs: Concept)
    extends DerivedNumberRestriction:
  def toGreaterThan = Complement(GreaterThan(n + 1, role, rhs))
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "≤" ++ n.toString ++ " " ++ left ++ ".(" + right + ")"

/** A qualified number restriction, requiring exactly n role names r to the
  * concept C, printing as ==n r.C.
  */
final case class Exactly(n: Int, role: Role, rhs: Concept)
    extends DerivedNumberRestriction:
  def toGreaterThan =
    Intersection(
      GreaterThan(n, role, rhs),
      LessThan(n, role, rhs).toGreaterThan
    )
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "=" ++ n.toString ++ " " ++ left ++ ".(" + right + ")"

/** The existential restriction, requiring at least 1 role name r to the concept
  * C, printing as ∃ r.C.
  */
final case class Existential(role: Role, rhs: Concept)
    extends DerivedNumberRestriction:
  def toGreaterThan = GreaterThan(1, role, rhs)
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "∃" ++ left ++ ".(" + right + ")"

/** The universal restriction, requiring for all role names r the concept C,
  * printing as ∀ r.C.
  */
final case class Universal(role: Role, rhs: Concept)
    extends DerivedNumberRestriction:
  def toGreaterThan = Complement(LessThan(0, role, Complement(rhs)))
  def encode: String = format(role.encode, rhs.encode)
  override def show(implicit state: BackendState): String =
    format(role.show(state), rhs.show(state))
  private def format(left: String, right: String): String =
    "∀" ++ left ++ ".(" + right + ")"

object Concept:

  /** Helper function for constructing internal parse errors, lifting
    * DLExpressions to Concept, or a given SharError (if they are not Concept).
    */
  def orElse(expr: DLExpression, error: SharError): SharTry[Concept] =
    expr match
      case c: Concept => Right(c)
      case _          => Left(error)

  /** Defines the Intersection of the given list of concepts. Returns Top for
    * empty lists.
    */
  def intersectionOf(cs: List[Concept]): Concept =
    simplify(cs.foldLeft(Top: Concept)(Intersection.apply))

  /** Defines the Union of the given list of concepts. Returns Bottom for empty
    * lists.
    */
  def unionOf(cs: List[Concept]): Concept =
    simplify(cs.foldLeft(Bottom: Concept)(Union.apply))

  /** Map a function f: Concept => Concept over the given concept, applying it
    * to all subexpressions. For example, given a union Union(lhs, rhs), the
    * function would be applied as f(Union(map(f, lhs), map(f, rhs))).
    */
  def map(f: Concept => Concept, concept: Concept): Concept = concept match
    case Top                        => f(Top)
    case Bottom                     => f(Bottom)
    case n @ NamedConcept(_)        => f(n)
    case n @ NominalConcept(_)      => f(n)
    case Complement(c)              => f(Complement(map(f, c)))
    case Existential(r, expr)       => f(Existential(r, map(f, expr)))
    case Universal(r, expr)         => f(Universal(r, map(f, expr)))
    case GreaterThan(n, r, expr)    => f(GreaterThan(n, r, map(f, expr)))
    case LessThan(n, r, expr)       => f(LessThan(n, r, map(f, expr)))
    case Exactly(n, r, expr)        => f(Exactly(n, r, map(f, expr)))
    case Union(lexpr, rexpr)        => f(Union(map(f, lexpr), map(f, rexpr)))
    case Intersection(lexpr, rexpr) =>
      f(Intersection(map(f, lexpr), map(f, rexpr)))

  /** Apply a procedure proc: Concept => Unit to the given concept, and to all
    * its components. For example, given a Union(lhs, rhs), it would first apply
    * proc(Union(lhs, rhs)) and then foreach(proc, lhs); foreach(prof, rhs).
    */
  def foreach(proc: Concept => Unit, concept: Concept): Unit =
    proc(concept)
    concept match
      // Nothing to do here.
      case Top               => ()
      case Bottom            => ()
      case NamedConcept(_)   => ()
      case NominalConcept(_) => ()
      // Recursively iterate on sub-components.
      case Complement(i) =>
        foreach(proc, i)
      case Existential(_, expr) =>
        foreach(proc, expr)
      case Universal(_, expr) =>
        foreach(proc, expr)
      case GreaterThan(_, _, expr) =>
        foreach(proc, expr)
      case LessThan(_, _, expr) =>
        foreach(proc, expr)
      case Exactly(_, _, expr) =>
        foreach(proc, expr)
      case Union(lexpr, rexpr) =>
        foreach(proc, lexpr)
        foreach(proc, rexpr)
      case Intersection(lexpr, rexpr) =>
        foreach(proc, lexpr)
        foreach(proc, rexpr)

  /** Simplify a Concept fully, according to the equivalency rules implemented
    * in `equivalentConcept`.
    */
  def simplify(concept: Concept): Concept =
    val s = equivalentConcept(concept)
    if s == concept then s
    else simplify(s)

  /** Transform this concept into an equivalent concept by applying a single
    * equivalency rule.
    */
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
        // Union and Intersection
        case Union(expr1, expr2) if expr1 == expr2                    => expr1
        case Intersection(expr1, expr2) if expr1 == expr2             => expr1
        case Intersection(expr1, Complement(expr2)) if expr1 == expr2 => Bottom
        case Intersection(Complement(expr1), expr2) if expr1 == expr2 => Bottom
        case Union(expr1, Complement(expr2)) if expr1 == expr2        => Top
        case Union(Complement(expr1), expr2) if expr1 == expr2        => Top
        // Remove double negation.
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
