package de.pseifer.shar.parsing

import de.pseifer.shar.core.Iri
import de.pseifer.shar.error._

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree.{ParseTreeVisitor, ParseTree};

/** Common wrapper for Antlr based parsers. Handles setup, parsing pipeline and
  * SharError management.
  */
trait AntlrBasedParser[T, L <: Lexer, P <: Parser]:

  def mkVisitor: ParseTreeVisitor[SharTry[T]]
  def mkLexer(charStream: CharStream): L
  def mkParser(tokens: TokenStream): P
  def doParse(parser: P): ParseTree

  def mkSyntaxError(symbol: String, message: String, line: Int): SharError

  private var errors: List[SharError] = Nil

  private class SharErrorListener extends BaseErrorListener:

    override def syntaxError(
        recognizer: Recognizer[_, _],
        offendingSymbol: Object,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException
    ): Unit =
      errors = mkSyntaxError(
        if offendingSymbol != null then offendingSymbol.toString else "",
        msg,
        line
      ) :: errors

  def parse(input: String): SharTry[T] =

    // Reset internal error state.
    errors = Nil

    val charStream = CharStreams.fromString(input)

    val lexer = mkLexer(charStream)
    lexer.removeErrorListeners()
    lexer.addErrorListener(SharErrorListener())

    if errors.nonEmpty then Left(errors.head)
    else
      val tokens = CommonTokenStream(lexer)
      val parser = mkParser(tokens)
      parser.removeErrorListeners()
      parser.addErrorListener(SharErrorListener())
      val tree = doParse(parser)

      if errors.nonEmpty then Left(errors.head)
      else
        val res = mkVisitor.visit(tree)
        if res == null then
          Left(TypeParseError("Unspecified parse error(s) occurred."))
        else res
