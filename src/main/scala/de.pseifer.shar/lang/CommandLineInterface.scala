package de.pseifer.shar.lang

import org.rogach.scallop._
import de.pseifer.shar.core.OntologyInitialization
import de.pseifer.shar.core.EmptyInitialization
import de.pseifer.shar.core.ReasonerInitialization
import de.pseifer.shar.core.Iri
import de.pseifer.shar.error.SharTry
import de.pseifer.shar.error.IOError

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import de.pseifer.shar.core.PrefixMapping

/** Command line interface definition. */
class CommandLineInterface(baseConfiguration: REPLConfig, arguments: Seq[String])
    extends ScallopConf(arguments):
  
  val pkg = getClass.getPackage
  val version = pkg.getImplementationVersion
  val name = pkg.getImplementationTitle
  val infos = s"$name $version - Copyright (C) Philipp Seifer"

  version(infos)

  banner("\n" + """Usage: shar [OPTIONS] <source-file>
           |
           | where OPTIONS include:
           |""".stripMargin)
  footer(
    "\n" +
      """This program is free sofware and comes without warranty of any kind.
      |For more information, including associated publications, please see the repository at
      |    https://github.com/pseifer/shar
      |or the LICENSE document included with this distribution.
      |""".stripMargin
  )

  val noisy =
    toggle(
      default = Some(false),
      descrYes = "Output lots of immediate information (def: false)."
    )

  val silent =
    toggle(
      default = Some(false),
      descrYes = "Supress all output of commands (def: false)."
    )

  val repl =
    toggle(
      default = Some(false),
      descrYes = "Launch REPL session (even if SHAR files are provided)."
    )

  val sourceFiles = trailArg[List[String]](
    required = false,
    descr = "SHAR file(s) to load and execute.",
    default = Some(Nil)
  )

  val owl =
    opt[String](
      required = false,
      descr = "OWL ontology to load (must be valid IRI)."
    )

  val prefixes =
    opt[String](
      required = false,
      descr = "Load prefix definitions from a file (SPARQL syntax)."
    )

  val entails =
    opt[String](
      required = false,
      descr = "Single axiom to check (after processing scripts)."
    )

  val command =
    opt[String](
      required = false,
      descr = "Single comman to run (last)."
    )

  verify()

  // Load prefix definitions.
  private def loadPrefixes: SharTry[PrefixMapping] =
    if prefixes.isDefined then
      val pref = PrefixMapping.default
      Try(pref.addFromSource(io.Source.fromFile(prefixes()))) match
        case Failure(e) => Left(IOError("Invalid prefix file."))
        case Success(Left(e)) => Left(e)
        case Success(Right(_)) => Right(pref)
    else
      Right(PrefixMapping.default)

  // Load specifically given entails/command.
  private def givens: Seq[String] =  
    entails.toOption.map("⊢ " ++ _).toSeq ++ command.toOption.toSeq

  // Load source files, if any, as well s
  private def loadSources: SharTry[Seq[String]] = 
    Try(sourceFiles().flatMap(f => io.Source.fromFile(f).getLines.toSeq)) match 
      case Failure(e) => Left(IOError("Invalid script file(s)."))
      case Success(s) => Right(s ++ givens)
  
  // Load an ontology document.
  private def loadOntology: SharTry[ReasonerInitialization] = 
    if owl.isDefined then
      Iri.makeFromRawIri(owl.toOption.get).map(OntologyInitialization(_))
    else
      Right(EmptyInitialization())

  /** Convert the parsed command line to REPL configuration. */
  def toREPLConfig: SharTry[REPLConfig] = 
    for 
      i <- loadOntology
      s <- loadSources
      p <- loadPrefixes
    yield baseConfiguration.copy(
      noisy = noisy(),
      silent = silent(),
      infoline = infos,
      init = i,
      script = s,
      prefixes = p,
      interactive = repl() || (sourceFiles().isEmpty && entails.isEmpty && command.isEmpty),
      entailmentMode = sourceFiles().nonEmpty || owl.isDefined
    )

