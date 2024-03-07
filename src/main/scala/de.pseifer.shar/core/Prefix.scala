package de.pseifer.shar.core

import de.pseifer.shar.error._

/** A prefix definition, always including the trailing colon. Constructor is
  * private, so prefix may only be created via the Prefix.fromString method,
  * which returns None on invalid prefixes.
  */
class Prefix private (val value: String):
  override def toString: String = value

  def canEqual(a: Any) = a.isInstanceOf[Prefix]

  override def equals(that: Any): Boolean =
    that match
      case that: Prefix => this.value == that.value
      case _            => false

  override def hashCode: Int = value.hashCode

  def showWith(name: String): String = toString ++ name

object Prefix:
  private def validChar(c: Char): Boolean =
    c.isLetterOrDigit || c == '_' || c == '-'

  def valid(s: String): Boolean =
    if s.isEmpty then false
    else
      val colon = s.last
      val prefix = s.dropRight(1)
      colon == ':' && prefix.forall(validChar)

  def fromString(s: String): SharTry[Prefix] =
    if valid(s) then Right(Prefix(s))
    else Left(InvalidPrefixError(s))

  def makeFromRawPrefix(s: String): SharTry[Prefix] =
    fromString(s ++ ":")

  private def define(str: String): Prefix =
    fromString(str).toOption.get
  
  val default: Prefix = define(":")

  val xsd: Prefix = define("xsd:")
  val owl: Prefix = define("owl:")
  val rdf: Prefix = define("rdf:")
  val rdfs: Prefix = define("rdfs:")
  val sh: Prefix = define("sh:")
  val ex: Prefix = define("ex:")

  val shar: Prefix = define("shar:")
