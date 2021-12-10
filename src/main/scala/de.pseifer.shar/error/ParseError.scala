package de.pseifer.shar.error

/** Error that arises during parsing. These result in compiler-thrown errors.
  */
sealed abstract class ParseError(msg: String)
    extends SharError("Parse Error", msg)

/** A parsing error on malformed prefix definitions.
  */
case class TypeParseError(reason: String = "")
    extends ParseError(TypeParseError.format(reason))

object TypeParseError:
  def format(reason: String) = reason

/*
/**
 * Error while encoding some value to String.
 */
case class EncodingError(reason: String = "")
  extends ParseError(EncodingError.format(reason))

object EncodingError:
  def format(reason: String) = reason


/**
 * Error while parsing SHACL definitions: Unsupported operator.
 */
case class UnsupportedSHACLConstruct(op: String = "")
  extends ParseError(EncodingError.format(op))

object UnsupportedSHACLConstruct:
  def format(op: String) =
    s"The SHACL property '$op' is invalid or currently not supported."


/**
 * Error while parsing SHACL definitions: Unsupported path expression.
 */
case class UnsupportedSHACLPath(path: String = "")
  extends ParseError(EncodingError.format(path))

object UnsupportedSHACLPath:
  def format(path: String) =
    s"The SHACL path expression '$path' is invalid or currently not supported."


/**
 * Error while parsing SHACL definitions: RDF faulty.
 */
case class FaultyRDFError(doc: String = "")
  extends ParseError(EncodingError.format(doc))

object FaultyRDFError:
  def format(doc: String) =
    s"The RDF in $doc is faulty."
 */
