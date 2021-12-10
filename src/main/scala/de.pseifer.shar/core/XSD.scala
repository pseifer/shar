package de.pseifer.shar.core

import scala.language.implicitConversions
import scala.util.{Try, Success, Failure}

// Provides implicit conversion to XSD values for supported types.
implicit def support2XSD(value: Int | Float | Double | String | Boolean): XSD =
  XSD.from(value)

// All supported XSD types.
enum XSD(
    val typename: String,
    val value: Int | Float | Double | String | Boolean
):

  def embed: String =
    "\"" + clean(this.value.toString) + "\"" + "^^" + this.typename

  /*
   * Function to clean a String value,
   * evoiding injections.
   */
  final protected val clean = (value: String) => value.filter(_ != '"')

  def canEqual(a: Any) = a.isInstanceOf[XSD]

  override def equals(that: Any): Boolean =
    that match
      case that: XSD =>
        this.typename == that.typename && this.value == that.value
      case _ => false

  override def hashCode: Int = (typename ++ value.toString).hashCode

  case XSDInteger(i: Int) extends XSD("xsd:integer", i)
  case XSDFloat(f: Float) extends XSD("xsd:float", f)
  case XSDDouble(d: Double) extends XSD("xsd:double", d)
  case XSDString(s: String) extends XSD("xsd:string", s)
  case XSDBoolean(b: Boolean) extends XSD("xsd:boolean", b)

object XSD:

  /** Convert any type to supported XSD type, if possible.
    */
  def fromAny(any: Any): Option[XSD] = any match
    case i: Int     => Some(XSD.XSDInteger(i))
    case f: Float   => Some(XSD.XSDFloat(f))
    case d: Double  => Some(XSD.XSDDouble(d))
    case s: String  => Some(XSD.XSDString(s))
    case b: Boolean => Some(XSD.XSDBoolean(b))
    case _          => None

  /** Convert supported type to XSD.
    */
  def from(value: Int | Float | Double | String | Boolean): XSD =
    fromAny(value).get

  /** Convert a string value and type annotation to XSD type, if the annotation
    * is a supported type, and the conversion succeeeds.
    */
  def fromString(s: String, annotation: String): Option[XSD] =
    // Possible conversions.
    val ts1 = List(
      Try(s.toInt),
      Try(s.toFloat),
      Try(s.toDouble),
      Try(s),
      Try(s.toBoolean)
    )
    // Converted to XSD, if any successes.
    val ts2 = ts1.map(_.map(from))
    // Filter successes with matching annotations (max 1).
    val ts3 = ts2.flatMap {
      case Success(xsd) if xsd.typename == annotation => Some(xsd)
      case _                                          => None
    }
    // Return first (and only) element, if it exists.
    ts3.headOption
