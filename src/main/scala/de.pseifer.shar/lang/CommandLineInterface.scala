package de.pseifer.shar.lang

import org.rogach.scallop._

/*
-p | --prefixes       Load prefixes from this file.
-o | --owl            Load OWL ontology from this IRI or FILE.
-e AX | --entails AX  Check entailment of axiom AX and terminate with result.
- multiple source files.
*/

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
      descrYes = "Launch REPL session, even if file is provided."
    )

  val sourceFile = trailArg[String](
    required = false,
    descr = "File to load and execute.",
    default = Some("")
  )

  verify()

  def toREPLConfig: REPLConfig = baseConfiguration.copy(
    noisy = noisy(),
    silent = silent(),
    infoline = infos
  )

