package de.pseifer.shar.error

/** Error that arises during SPARQL parsing/processing. These result in
  * compiler-thrown errors.
  */
sealed abstract class FormatError(msg: String)
    extends SharError("Format Error", msg)

// /** A Format error on malformed prefix definitions.
//   */
// case class MalformedPrefixDefinitionError(pref: String = "")
//     extends FormatError(MalformedPrefixDefinitionError.format(pref))
//
// object MalformedPrefixDefinitionError:
//   def format(pref: String) =
//     s"Malformed prefix definition: '$pref'"
//
// /** A Format error on malformed BaseIRI definitions.
//   */
// case class MalformedBaseIRIDefinitionError(base: String = "")
//     extends FormatError(MalformedBaseIRIDefinitionError.format(base))
//
// object MalformedBaseIRIDefinitionError:
//   def format(base: String) =
//     s"Malformed base IRI definition: '$base'"
//
/** A Format error on malformed BaseIRI definitions.
  */
case class DublicatePrefixDefinitionError(pref: String = "")
    extends FormatError(DublicatePrefixDefinitionError.format(pref))

object DublicatePrefixDefinitionError:
  def format(pref: String) =
    s"Dublicate definition of pref '$pref'."

/** A Format error on undefined prefixes.
  */
case class UndefinedPrefixError(pref: String = "?")
    extends FormatError(UndefinedPrefixError.format(pref))

object UndefinedPrefixError:
  def format(pref: String) = s"Prefix '$pref' is never defined."

/** A Format error on invalid IRIs.
  */
case class InvalidIRIError(iri: String = "<?>")
    extends FormatError(InvalidIRIError.format(iri))

object InvalidIRIError:
  def format(iri: String) = s"The IRI '$iri' is not valid."
//
// /** A Format error on invalid Prefixes.
//   */
case class InvalidPrefixError(pre: String = "?:")
    extends FormatError(InvalidPrefixError.format(pre))

object InvalidPrefixError:
  def format(pre: String) = s"The prefix '$pre' is not valid."
//
// /** A Format error on invalid Variables.
//   */
// case class InvalidVariableError(variable: String = "?x")
//     extends FormatError(InvalidVariableError.format(variable))
//
// object InvalidVariableError:
//   def format(variable: String) = s"The variable '$variable' is not valid."
//
// /** Format query has syntax error.
//   */
// case class UnableToParseFormatError(reason: String = "")
//     extends FormatError(UnableToParseFormatError.format(reason))
//
// object UnableToParseFormatError:
//   def format(reason: String) = s"$reason"
//
// /** Unsupported feature encountered in query tree construction.
//   */
// case object SyntaxTreeConstructionError
//     extends FormatError("Features of this query are not supported")
//
// /** Unsupported kind of query encountered in query tree construction.
//   */
// case object WrongQueryKindError
//     extends FormatError("Only SELECT and ASK queries are supported")
//
// /** Invalid name in query.1
//   */
// case class InvalidNameInQueryError(name: String = "")
//     extends FormatError(InvalidNameInQueryError.format(name))
//
// object InvalidNameInQueryError:
//   def format(name: String) =
//     s"Invalid name '$name' found in query. Neither an IRI, valid value or variable."
