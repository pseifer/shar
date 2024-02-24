package de.pseifer.shar.core

import scala.io.Source

import de.pseifer.shar.error._

/** Mapping from Prefix to BaseIri.
  */
class PrefixMapping:

  // Defaults + external configuration.
  private var defaults: Map[Prefix, Iri] =
    Map(
      Prefix.xsd -> Iri.xsd,
      Prefix.owl -> Iri.owl,
      Prefix.rdf -> Iri.rdf,
      Prefix.rdfs -> Iri.rdfs,
      Prefix.sh -> Iri.sh,
      Prefix.ex -> Iri.ex,
      Prefix.shar -> Iri.shar
    )

  // Initialize the prefix mapping with defaults.
  private var mapping: Map[Prefix, Iri] = defaults

  /** Reset the prefix mapping to the defaults.
    */

  def reset(): Unit =
    mapping = defaults

  /** Return copy of current prefix mappings.
    */
  def toMap: Map[Prefix, Iri] = mapping

  /** Generate SPARQL-syntax prefix definitions for a query.
    */
  def toSPARQL(indent: String = ""): String =
    mapping
      .map { case (pre, iri) =>
        indent + "PREFIX " + pre.toString + " " + iri.encode
      }
      .mkString("\n")

  /** Generate Turtle-syntax perfix definitions.
    */
  def toTurtle(indent: String = ""): String =
    mapping
      .map { case (pre, iri) =>
        indent + "@prefix " + pre.toString + " " + iri.encode + " ."
      }
      .mkString("\n")

  /** Merge a prefix mapping into this one.
    */
  def addAll(
      other: PrefixMapping,
      addDefaults: Boolean = false
  ): SharTry[true] =
    //// Join both mappings.
    //val s = mapping.toSeq ++ other.mapping.toSeq
    //// Group again by prefixes, mapping to List of distinct iris.
    //val t = s.groupBy { case (prefix, iri) => prefix }.view.mapValues { _.map(_._2).distinct }
    //// If there is any list with size > 1 there where conflicting mappings.
    //if t.values.toList.exists ( _.size > 1 ) then
    //  // In this case, there is a dublication error with at least one offender.
    //  val offender = t.filter { (k,v) => v.size > 1 }.keys.head
    //  Left(DublicatePrefixDefinitionError(offender.toString))
    //else
    //  // Otherwise, we can savely merge the mappings.
    //  mapping = mapping ++ other.mapping
    //  Right(true)
    val r =
      for (prefix, iri) <- other.mapping
      yield
        if addDefaults then addToDefaults(prefix, iri)
        else add(prefix, iri)
    //r.collectFirst { case x @ Left(_) => x } getOrElse (Right(true))
    r.filter(_.isLeft).headOption.getOrElse(Right(true))

  /** Add a new prefix, if not already defined (differently).
    */
  def add(prefix: Prefix, iri: Iri): SharTry[true] =
    if mapping contains prefix then
      if mapping(prefix) == iri then Right(true)
      else Left(DublicatePrefixDefinitionError(prefix.toString))
    else
      mapping = mapping.updated(prefix, iri)
      Right(true)

  /** Add a new prefix (add) and also add it to defaults.
    */
  def addToDefaults(prefix: Prefix, iri: Iri): SharTry[true] =
    add(prefix, iri) match
      case Left(err) => Left(err)
      case Right(_) =>
        defaults = defaults.updated(prefix, iri)
        Right(true)

  /** Get a base iri for a prefix.
    */
  def get(prefix: Prefix): Option[Iri] =
    mapping.get(prefix)

  /** Get the prefixes known for a base iri.
    */
  def get(base: Iri): List[Prefix] =
    mapping.filter { case (k, v) => v == base }.keys.toList

  /** Extend a prefix + prefixedName to a full IRI String.
    */
  def expand(prefix: Prefix, prefixedName: String): Option[String] =
    get(prefix).map(_.expanded(prefixedName))

  /** Try to expand a prefix in a string.
    */
  def expandString(string: String): SharTry[Iri] =
    Iri.fromString(string) match
      // If 'string' is a valid IRI, return that.
      case Right(iri) => Right(iri)
      // Otherwise
      case Left(_) =>
        // Split at first ':'
        val (p, n) = string.splitAt(string.indexOf(':'))
        // No colon found -> this is not a prefixed name
        if !n.startsWith(":") then Left(InvalidIRIError(string))
        // Otherwise, try to make a prefix.
        else
          val prefix = Prefix.fromString(p + ":") match
            case Left(_)   => Left(InvalidIRIError(string))
            case Right(pr) => Right(pr)

          prefix
            // Try to expand against the store.
            .flatMap { pr =>
              expand(pr, n.drop(1)) match
                case None    => Left(UndefinedPrefixError(pr.toString))
                case Some(r) => Right(r)
            }
            // Try to now make Iri from expanded.
            .flatMap { i =>
              Iri.fromString(i) match
                case Left(_)    => Left(InvalidIRIError(i))
                case Right(iri) => Right(iri)
            }

  /** Retract a full IRI, trying to replace some part with a prefix. The
    * selected prefix, if multiple apply, is arbitrary.
    */
  def retract(target: Iri): Option[(Prefix, String)] =
    toMap.flatMap { case (p, i) =>
      target.retracted(i).map((p, _))
    }.headOption

  /** Convert Iri to string, trying to replace an arbitray prefix. If not
    * possible, the full iri is returned.
    * @see
    *   retract
    */
  def retractToStringIfPossible(target: Iri): String =
    retract(target) match
      case None         => target.encode
      case Some((p, n)) => p.showWith(n)

  private val prefixRegex = """(PREFIX|prefix)\s+([^\s]+)\s+(<[^>]+>)""".r

  private def findPrefixes(query: String): Map[String, String] =
    prefixRegex
      .findAllIn(query)
      .matchData
      .map { m =>
        m.group(2) -> m.group(3)
      }
      .toMap

  /** Add prefix definitions from a File.
    */
  def addFromSource(
      source: Source,
      addDefaults: Boolean = true
  ): SharTry[true] =

    val definitions = findPrefixes(source.getLines.mkString(" ")).map {
      case (p, i) =>
        for
          prefix <- Prefix.fromString(p)
          iri <- Iri.fromString(i)
        yield (prefix, iri)
    }

    if definitions.exists(_.isLeft) then
      Left(definitions.find(_.isLeft).get.swap.toOption.get)
    else
      val pr = definitions.map(_.toOption.get)
      val updated =
        if addDefaults then pr.map(addToDefaults)
        else pr.map(add)
      // Report (the first) error.
      //updated.collectFirst { case x @ Left(_) => x } getOrElse (Right(true))
      updated.filter(_.isLeft).headOption.getOrElse(Right(true))

object PrefixMapping:
  def default: PrefixMapping = PrefixMapping()
