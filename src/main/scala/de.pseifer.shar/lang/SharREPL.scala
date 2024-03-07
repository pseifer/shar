package de.pseifer.shar.lang

import de.pseifer.shar.core._
import de.pseifer.shar.dl._
import de.pseifer.shar.error._
import de.pseifer.shar.reasoning._
import de.pseifer.shar.parsing._

import de.pseifer.shar.Shar
import de.pseifer.shar.AxiomSetBuilder

import scala.io.StdIn._

/** A REPL for reasoning with DL knowledgebases. */
class SharREPL(config: REPLConfig = REPLConfig.default):

  private val shar = Shar(config.init, config.prefixes)
  import shar._

  // Implicit builder for axioms.
  implicit val axiomSetBuilder: AxiomSetBuilder = AxiomSetBuilder()

  // The reasoner.
  private val reasoner = config.reasoner(config.init)

  // Counter for unnamed reasoners.
  private var counter = 0

  // The parser for DL expressions.
  private val parser = ConceptParser(state)

  private def parseConcept(c: String): SharTry[Concept] =
    parser.parse(c) match
      case Left(p)           => Left(p)
      case Right(d: Concept) => Right(d)
      case Right(_) => Left(AxiomParseError("Axiom component not a concept expression: " ++ c))

  private def parseRole(c: String): SharTry[Role] =
    parser.parse(c) match
      case Left(p)           => Left(p)
      case Right(d: NamedConcept) => Right(NamedRole(d.c))
      case Right(d: Complement) => d.concept match 
        case (di: NamedConcept) => Right(Inverse(NamedRole(di.c)))
        case _ => Left(AxiomParseError("Axiom component not a role expression: " ++ c))
      case Right(_) => Left(AxiomParseError("Axiom component not a role expression: " ++ c))

  // Add the default prefix as 'shar' (if defaultIsSharPrefix is set).
  if config.defaultIsSharPrefix then
    state.prefixes.add(Prefix.fromString(":").toOption.get, Iri.shar)

  // The result that a CLI application returns.
  private var resultCode: Int = 0

  // The result that a CLI application returns.
  private var quit: Boolean = false

  private val repl: Boolean = config.interactive
  private var entMode: Boolean = config.entailmentMode

  // Info messages held back from printing.
  private var infoMessageLog: String = ""

  // Result messages held back from printing.
  private var resultMessageLog: String = ""

  private def isCommandPred(line: String): Boolean = 
    line.endsWith(".")

  private def isEntailmentPred(line: String): Boolean = 
    line.startsWith(":-") || line.startsWith("⊢")

  private def printHelp(): Unit = 
    val msg = """
    |  Define axioms (unicode or ASCII-only)
    |    :Child ⊑ ∃:knows.:Person
    |    :Child << #E:knows.:Person
    |    :NiceChild ≡ :Child ⊓ :Nice
    |    :NiceChild == :Child & :Nice
    |  
    |  Check entailment
    |    ⊢ :NiceChild ⊑ ∃:knows.⊤
    |    :- :NiceChild << #E:knows.#t
    |  
    |  Other useful commands
    |    'entailment.' enter entailment mode, check all axioms.
    |    'normal.' return to normal mode.
    |    'info.' print recent axioms in knowledge base.
    |    'quit.' quit the REPL.
    |
    |    Files supplied as arguments are processed.
    |    See also 'shar --help' for script mode.
    |
    | More information at https://github.com/pseifer/shar
    """.stripMargin
    println(msg)

  /** Handle one of a few special commands. */
  private def handleCommand(line: String): Unit =
    val cmd = line.stripSuffix(".").map(_.toLower)
    // Handle commands.
    cmd match
      case "i" => handleCommand("info.")
      case "r" => handleCommand("result.")
      case "q" => handleCommand("quit.")
      case "h" => handleCommand("help.")
      case "n" => handleCommand("normal.")
      case "e" => handleCommand("entailment.")
      case "info" => 
        if !config.silent then println(infoMessageLog) 
        infoMessageLog = ""
      case "result" => 
        if !config.silent then println(resultMessageLog) 
        resultMessageLog = ""
      case "noinfo" => infoMessageLog = ""
      case "noresult" => resultMessageLog = ""
      case "help" => printHelp()
      case "entailment" => 
        println("In entailment mode, all entered axioms are checked.")
        println("Use 'normal.' to return to normal mode.\n")
        entMode = true
      case "normal" => 
        println()
        entMode = false
      case "quit" => 
        quit = true
        println("Goodbye!") 
      case _ => println("no such command")

  private def parseAxiom(token: String, constructor: (Concept, Concept) => Axiom, line: String): SharTry[Axiom] =
    val i = line.indexOf(token)
    val (l, r) = line.splitAt(i)
    for 
      lp <- parseConcept(l.trim)
      rp <- parseConcept(r.stripPrefix(token).trim)
    yield constructor(lp, rp)

  private def parseRoleAxiom(token: String, constructor: (Role, Role) => Axiom, line: String): SharTry[Axiom] =
    val i = line.indexOf(token)
    val (l, r) = line.splitAt(i)
    for 
      lp <- parseRole(l.trim)
      rp <- parseRole(r.stripPrefix(token).trim)
    yield constructor(lp, rp)

  private def parseSatisfiability(line: String): SharTry[Axiom] =
    for 
      lp <- parseConcept(line.trim)
    yield Satisfiability(lp)

  private def parseAxiom(line: String, allowSat: Boolean): SharTry[Axiom] =
    if line.indexOf(" ⊑ ") != -1 then 
      parseAxiom("⊑", Subsumption(_, _), line)
    else if line.indexOf(" << ") != -1 then
      parseAxiom("<<", Subsumption(_, _), line)
    else if line.indexOf(" ⊒ ") != -1 then 
      parseAxiom("⊒", (l, r) => Subsumption(r, l), line)
    else if line.indexOf(" >> ") != -1 then
      parseAxiom(">>", (l, r) => Subsumption(r, l), line)
    else if line.indexOf(" ≡ ") != -1 then
      parseAxiom("≡", Equality(_, _), line)
    else if line.indexOf(" == ") != -1 then
      parseAxiom("==", Equality(_, _), line)
    else if line.indexOf(" <- ") != -1 then
      parseRoleAxiom("<-", RoleSubsumption(_, _), line)
    else if line.indexOf(" -> ") != -1 then
      parseRoleAxiom("->", (l, r) => RoleSubsumption(r, l), line)
    else if allowSat then
      parseSatisfiability(line)
    else
      Left(AxiomParseError("Not a valid axiom."))

  /** Handle an axiom, to be inserted into the KB. */
  private def handleAxiom(line: String, allowSat: Boolean): Unit =
    parseAxiom(line, allowSat) match
      case Left(err) => println(err.show)
      case Right(axiom) => 
        infoMessageLog += axiom.show ++ "\n"
        reasoner.addAxiom(axiom)

  /** Handle the entailment command. */
  private def handleEntailment(line: String): Unit =
    parseAxiom(line.stripPrefix("⊢").stripPrefix(":-"), true) match
      case Left(err) =>
        println(err.show)
        resultCode = 2
      case Right(axiom) => 
        val result = reasoner.prove(axiom)
        if result then
          resultCode = 0
          resultMessageLog += "⊢ " ++ axiom.show ++ "\n"
          if repl || config.noisy then handleCommand("result.")
        else
          resultCode = 1
          resultMessageLog += "⊬ " ++ axiom.show ++ "\n"
          if repl || config.noisy then handleCommand("result.")

  /** Process a single line. */
  private def processLine(line: String): Unit = 
    if isCommandPred(line) then
      handleCommand(line)
    else if isEntailmentPred(line) then
      handleEntailment(line)
    else
      handleAxiom(line, false)

  /** Process a sequence of lines. */
  private def process(lines: Seq[String]): Unit =
    lines
      // Strip whitespace.
      .map(_.trim)
      // Filter empty lines and comments.
      .filter(_.nonEmpty)
      .filter(!_.startsWith("//"))
      .filter(!_.startsWith("#"))
      .filter(!_.startsWith("--"))
      // Process all other lines.
      .foreach(processLine)

  /** Process multiple lines, in a String. */
  private def process(lines: String): Unit =
    process(lines.linesIterator.toSeq)

  /** User interaction: Prompt, read line, quit/repeat, or process with cmd.*/
  private def interact(prompt: String, cmd: String => Unit): Unit =
    print(prompt ++ " ")
    val line = readLine()
    if line == null then
      process("quit.")
    else if line.isEmpty() then
      interact(prompt, cmd)
    else
      cmd(line)

  /** Launch an interactive session. */
  private def startInteractive(): Unit = 
    println(config.infoline)
    println("Use 'help.' for help!")
    if entMode then
      println("In entailment mode, all entered axioms are checked.")
      println("Use 'normal.' to enter normal mode.")
    println()
    while !quit do
      if entMode then 
        interact("⊢", l => 
          if isCommandPred(l) then process(l)
          else process("⊢ " ++ l))
      else interact("shar>", process(_))

  /** Run a full, fresh REPL process. */
  def run(): Unit = 
    // Process source file.
    process(config.script)

    // Launch REPL or return result.
    if repl then startInteractive()
    else System.exit(resultCode)

