package de.pseifer.shar.core

import java.net.{URI, URISyntaxException, URL}

import scala.util.Try

import de.pseifer.shar.core.BackendState
import de.pseifer.shar.core.Encodeable
import de.pseifer.shar.core.XSD

import de.pseifer.shar.error._

/** Defines a Name. A name is either an Iri, Variable, TypedValue or the
  * (renamebale) name of a DefinedConcept.
  *
  * Note, that prefixed names must be extended to Iris before further use, since
  * this may fail, depending on available prefix mappings in the respective
  * context.
  *
  * @see
  *   Iri
  */
sealed trait Name extends Encodeable:

  /** Per default, names are the same when showing to users and encoding
    * internally.
    */
  def show(implicit state: BackendState): String = encode

/** IRI. Constructor is private, so IRI may only be created via the
  * Iri.fromString method, which returns None on invalid IRIs.
  */
class Iri private (val value: String) extends Name:

  /** Encode this iri in parseable form.
    */
  def encode: String = value

  /** Show this Iri for user-end purposes.
    */
  override def show(implicit state: BackendState): String =
    state.prefixes.retractToStringIfPossible(this)

  private def stripped: String = value.drop(1).dropRight(1)

  /** Match on this IRI as a prefix to the argument (raw) IRI.
    */
  def startOf(raw: String): Boolean =
    raw.startsWith(value.dropRight(1))

  /** Expand (a base iri) with 'name'.
    */
  def expanded(name: String): String =
    "<" + stripped ++ name + ">"

  /** Reverse of expand; Get part of the Iri without the base Iri.
    */
  def retracted(base: Iri): Option[String] =
    // Base without '<' & '>'
    val b = base.value.drop(1).dropRight(1)
    // This without '<' & '>'
    val i = value.drop(1).dropRight(1)

    if i startsWith b then Some(i.drop(b.size))
    else None

  /** Get the raw IRI as a String (without '<', '>').
    */
  def getRaw: String = value.drop(1).dropRight(1)

  /** Get this Iri as a java.net.URL
    */
  def getURL: URL = URL(getRaw)

  /** Returns 'true' if this Iri is an XSD datatype.
    */
  def isDatatype: Boolean = Iri.xsd.startOf(this.encode)

  def canEqual(a: Any) = a.isInstanceOf[Iri]

  override def equals(that: Any): Boolean =
    that match
      case that: Iri => this.value == that.value
      case _         => false

  override def hashCode: Int = value.hashCode

object Iri:
  /** Test if 's' is a valid Iri.
    */
  def valid(s: String): Boolean =
    val valid =
      try {
        if s.isEmpty then false
        else
          URI(s.drop(1).dropRight(1))
          true
      } catch {
        case e: URISyntaxException => false
      }
    valid && s.head == '<' && s.last == '>'

  /** Try to parse an Iri from String 's'.
    */
  def fromString(s: String): SharTry[Iri] =
    if valid(s) then Right(Iri(s))
    else Left(InvalidIRIError(s))

  /** Try to parse an Iri from a prefix and local name as <$prefix$local>.
    */
  def fromParts(prefix: String, local: String): SharTry[Iri] =
    fromString(s"<$prefix$local>")

  /** Try to parse an Iri from a raw IRI string.
    */
  def makeFromRawIri(raw: String): SharTry[Iri] =
    Iri.fromString("<" + raw + ">")

  private def define(str: String): Iri =
    fromString(str).toOption.get

  val xsd: Iri = define("<http://www.w3.org/2001/XMLSchema#>")
  val owl: Iri = define("<http://www.w3.org/2002/07/owl#>")
  val rdf: Iri = define("<http://www.w3.org/1999/02/22-rdf-syntax-ns#>")
  val rdfs: Iri = define("<http://www.w3.org/2000/01/rdf-schema#>")
  val sh: Iri = define("<http://www.w3.org/ns/shacl#>")
  val ex: Iri = define("<http://example.org#>")

  val rdf_type: Iri = define(
    "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"
  )
  val shar: Iri = define("<https://github.com/pseifer/shar/ontology/>")

/** A value with type annotation. Can be parsed from Strings using
  * TypedValue.fromString.
  */
case class TypedValue private (val value: XSD) extends Name:

  // TODO: ?
  def encode = value.embed

  def canEqual(a: Any) = a.isInstanceOf[TypedValue]

  override def equals(that: Any): Boolean =
    that match
      case that: TypedValue => this.value == that.value
      case _                => false

  override def hashCode: Int = value.hashCode

object TypedValue:
  private val isTypedLiteral = "\"(.*?)\"\\^\\^<(.*?)>".r

  // TODO: This may be broken TEST ME FIX ME
  def fromString(s: String): Option[TypedValue] =
    s match
      case isTypedLiteral(v, t) =>
        XSD.fromString(v, t).map(TypedValue(_))
      case _ => None
