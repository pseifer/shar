package de.pseifer.shar

import de.pseifer.shar.lang.SharREPL
import de.pseifer.shar.lang.CommandLineInterface
import de.pseifer.shar.lang.REPLConfig

@main def main(args: String*): Unit =
  // Initialize CLI parser and process the CLI arguments.
  CommandLineInterface(REPLConfig.default, args).toREPLConfig match
    // Exit in case of errors.
    case Left(e) => 
      println(e.show)
      System.exit(2)
    // Launch REPL/interpreter otherwise.
    case Right(rconf) => SharREPL(rconf).run()

