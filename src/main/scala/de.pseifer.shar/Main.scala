package de.pseifer.shar

import de.pseifer.shar.lang.SharREPL
import de.pseifer.shar.lang.CommandLineInterface
import de.pseifer.shar.lang.REPLConfig

import scala.util.Failure
import scala.util.Try

@main def main(args: String*): Unit =

  // Initialize CLI parser.
  val conf = CommandLineInterface(REPLConfig.default, args)

  // Load source file, if any.
  val source = Try(
    if conf.sourceFile().isEmpty then Seq()
    else
      io.Source.fromFile(conf.sourceFile()).getLines.toSeq
  )

  // Handle any IO errors.
  source match
    case Failure(e) => println("IO FAILURE " + e.getLocalizedMessage)
    case _          => ()

  // Launch REPL predicate: No source file, or explicit flag.
  val launchRepl = conf.repl() || conf.sourceFile().isEmpty

  // Process source file.
  val repl = SharREPL(conf.toREPLConfig)
  for 
    s <- source
  do repl.process(s)

  // Launch REPL or return result.
  if launchRepl then
    repl.launch()
  else
    System.exit(repl.getResult)

