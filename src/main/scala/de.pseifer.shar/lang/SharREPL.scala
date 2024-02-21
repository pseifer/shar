package de.pseifer.shar.lang

import de.pseifer.shar.core._
import de.pseifer.shar.dl._
import de.pseifer.shar.error._
import de.pseifer.shar.reasoning._
import de.pseifer.shar.parsing._
import de.pseifer.shar.util._

import scala.io.StdIn._

/** A REPL for description logic knowledgebases. */
class SharREPL(
    config: REPLConfig = REPLConfig.default
):

  private val shar = Shar(config.init, config.prefixes)
  import shar._

  // Implicit builder for axioms.
  implicit val axiomSetBuilder: AxiomSetBuilder = AxiomSetBuilder()

  // Make a KnowledgeBase with a name and standard setup.
  private def mkKB(name: String) =
    KnowledgeBase(name, config.reasoner(state.reasonerInit), false, config.reasoner)

  // The default reasoner.
  private val defaultReasoner = mkKB("K")

  // Counter for unnamed reasoners.
  private var counter = 0

  // The parser for DL expressions.
  private val parser = ConceptParser(state)

  private def parseConcept(c: String): SharTry[Concept] =
    parser.parse(c) match
      case Left(p)           => Left(p)
      case Right(d: Concept) => Right(d)
      case Right(_) => Left(AxiomParseError("Axiom component not a concept expression."))

  // Add the default prefix as 'shar' (if defaultIsSharPrefix is set).
  if config.defaultIsSharPrefix then
    state.prefixes.add(Prefix.fromString(":").toOption.get, Iri.shar)

  // The result that a CLI application returns.
  private var resultCode: Int = 1

  // The result that a CLI application returns.
  private var quit: Boolean = false
  private var repl: Boolean = false

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
    |    :NiceChild ⊑ :Child ⊓ :Nice
    |    :NiceChild << :Child & :Nice
    |  
    |  Check entailment
    |    ⊢ :NiceChild ⊑ ∃:knows.⊤
    |    :- :NiceChild << #E:knows.#t
    |  
    |  Other useful commands
    |    'info.' print recent axioms in knowledge base.
    |    'quit.' quit the REPL.
    |    Files supplied as arguments are processed.
    |
    | More information at https://github.com/pseifer/shar
    |
    """.stripMargin
    println(msg)

  private def handleCommand(line: String): Unit =
    val cmd = line.stripSuffix(".").map(_.toLower)
    // Handle commands.
    cmd match
      case "i" => handleCommand("info.")
      case "r" => handleCommand("result.")
      case "q" => handleCommand("quit.")
      case "h" => handleCommand("help.")
      case "info" => 
        if !config.silent then println(infoMessageLog) 
        infoMessageLog = ""
      case "result" => 
        if !config.silent then println(resultMessageLog) 
        resultMessageLog = ""
      case "noinfo" => infoMessageLog = ""
      case "noresult" => resultMessageLog = ""
      case "help" => printHelp()
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

  private def parseAxiom(line: String): SharTry[Axiom] =
    if line.indexOf(" ⊑ ") != -1 then 
      parseAxiom("⊑", Subsumption(_, _), line)
    else if line.indexOf(" << ") != -1 then
      parseAxiom("<<", Subsumption(_, _), line)
    else if line.indexOf(" ≡ ") != -1 then
      parseAxiom("<<", Subsumption(_, _), line)
    else if line.indexOf(" == ") != -1 then
      parseAxiom("<<", Subsumption(_, _), line)
    else
      Left(AxiomParseError("Not a valid axiom."))

  private def handleAxiom(line: String): Unit =
    parseAxiom(line) match
      case Left(err) => println(err.show)
      case Right(axiom) => 
        infoMessageLog += axiom.show ++ "\n"
        defaultReasoner += axiom

  private def handleEntailment(line: String): Unit =
    parseAxiom(line.stripPrefix("⊢").stripPrefix(":-")) match
      case Left(err) =>
        println(err.show)
        resultCode = 1
      case Right(axiom) => 
        val result = defaultReasoner |- axiom
        if result then
          resultCode = 0
          resultMessageLog += "⊢ " ++ axiom.show ++ "\n"
          if repl || config.noisy then handleCommand("result.")
        else
          resultCode = 1
          resultMessageLog += "⊬ " ++ axiom.show ++ "\n"
          if repl || config.noisy then handleCommand("result.")

  private def processLine(line: String): Unit = 
    if isCommandPred(line) then
      handleCommand(line)
    else if isEntailmentPred(line) then
      handleEntailment(line)
    else
      handleAxiom(line)

  def process(lines: Seq[String]): Unit =
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

  def process(lines: String): Unit =
    process(lines.linesIterator.toSeq)

  def launch(): Unit = 
    println(config.infoline)
    println("Use 'help.' for help!")
    println()
    repl = true
    while !quit do
      print("> ")
      val line = readLine()
      process(line)

  def getResult: Int = resultCode
