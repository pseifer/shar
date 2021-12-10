package de.pseifer.shar.parsing

import de.pseifer.shar.parsing.DescriptionLogicsBaseVisitor
import de.pseifer.shar.parsing.{DescriptionLogicsLexer, DescriptionLogicsParser}
import de.pseifer.shar.parsing.DescriptionLogicsParser._

import de.pseifer.shar.core.{Prefix, Iri}
import de.pseifer.shar.parsing.AntlrBasedParser
import de.pseifer.shar.reasoning.{DLReasoner, AxiomSet}

import de.pseifer.shar.dl._
import de.pseifer.shar.error._

import de.pseifer.shar.core.BackendState

import scala.collection.JavaConverters._
import scala.language.implicitConversions

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree.{ParseTreeVisitor, ParseTree};

/** Parse a description logics expression to a Concept.
  */
class ConceptParser(state: BackendState)
    extends AntlrBasedParser[
      DLExpression,
      DescriptionLogicsLexer,
      DescriptionLogicsParser
    ] {

  // Make internal error message.

  def ierr(location: String): SharError =
    TypeParseError(s"internal parser error ($location)")

  // Set the parsing backend.

  def mkVisitor = DLVisitor()
  def mkLexer(c: CharStream) = DescriptionLogicsLexer(c)
  def mkParser(t: TokenStream) = DescriptionLogicsParser(t)
  def doParse(p: DescriptionLogicsParser) = p.formula()

  def mkSyntaxError(symbol: String, message: String, line: Int): SharError =
    TypeParseError(message)

  // Define the visitor.

  private class DLVisitor
      extends DescriptionLogicsBaseVisitor[SharTry[DLExpression]]:

    /** Visit one of the binary operators.
      */
    private def visitBinary(
        ctx: IntersectionContext | UnionContext,
        t: (Concept, Concept) => DLExpression
    ): SharTry[DLExpression] =
      for
        left <- visit(ctx.getChild(0))
        right <- visit(ctx.getChild(2))
        c1 <- Concept.orElse(left, ierr("visitBinary"))
        c2 <- Concept.orElse(right, ierr("visitBinary"))
      yield t(c1, c2)

    /** Visit any number restriction.
      */
    private def visitNQuantification(
        ctx: LessContext | GreaterContext | ExactlyContext,
        t: (Int, Role, Concept) => DLExpression
    ): SharTry[DLExpression] =
      for
        role <- visit(ctx.getChild(1))
        rhs <- visit(ctx.getChild(3))
        r <- Role.orElse(role, ierr("visitQuantification"))
        c <- Concept.orElse(rhs, ierr("visitQuantification"))
      yield t(ctx.getChild(0).toString.drop(2).toInt, r, c)

    /** Visit any quantification.
      */
    private def visitQuantification(
        ctx: UniversalContext | ExistentialContext,
        t: (Role, Concept) => DLExpression
    ): SharTry[DLExpression] =
      for
        role <- visit(ctx.getChild(1))
        rhs <- visit(ctx.getChild(3))
        r <- Role.orElse(role, ierr("visitQuantification"))
        c <- Concept.orElse(rhs, ierr("visitQuantification"))
      yield t(r, c)

    /** Select a single child 'i' and construct type with 't' based on child.
      */
    private def selectChild(
        ctx: Negated_formulaContext | Paren_formulaContext | FormulaContext,
        i: Int,
        t: Concept => DLExpression
    ): SharTry[DLExpression] =
      for
        child <- visit(ctx.getChild(i))
        concept <- Concept.orElse(child, ierr("selectChild"))
      yield t(concept)

    override def visitFormula(ctx: FormulaContext): SharTry[DLExpression] =
      selectChild(ctx, 0, identity)

    override def visitBottom(ctx: BottomContext): SharTry[DLExpression] =
      Right(Bottom)

    override def visitTop(ctx: TopContext): SharTry[DLExpression] =
      Right(Top)

    override def visitConcept(ctx: ConceptContext): SharTry[DLExpression] =
      val rawIri = ctx.getChild(0).getText
      val iri = state.prefixes.expandString(rawIri)
      iri.map(NamedConcept.apply)

    // A concept can either be a normal concept (prefixed or raw),
    // or it may be a magic scaspa 'defined' concept.

    // Magic Shar prefix:
    //if rawIri.startsWith(Prefix.shar.toString) then
    //  for name <- DefinedName.fromString(rawIri)
    //  yield DefinedConcept(name)

    //// Magic Shar IRI:
    //else if Iri.shar.startOf(rawIri) then
    //  for
    //    i <- iri
    //    name <- DefinedName.fromIri(state.prefixes, i)
    //  yield DefinedConcept(name)

    // Other

    override def visitNominal(ctx: NominalContext): SharTry[DLExpression] =
      val rawIri = ctx.getChild(1).getText
      val iri = state.prefixes.expandString(rawIri)
      iri.map(NominalConcept.apply)

    override def visitRole(ctx: RoleContext): SharTry[DLExpression] =
      // -- role --
      if ctx.children.size == 1 then
        val rawIri = ctx.getChild(0).getText
        state.prefixes.expandString(rawIri).map(NamedRole.apply)

      // -- inverse role --
      else
        for
          child <- visit(ctx.getChild(1))
          role <- Role.orElse(child, ierr("visitRole"))
        yield Inverse(role)

    override def visitUnion(ctx: UnionContext): SharTry[DLExpression] =
      visitBinary(ctx, Union.apply)

    override def visitIntersection(
        ctx: IntersectionContext
    ): SharTry[DLExpression] =
      visitBinary(ctx, Intersection.apply)

    override def visitExistential(
        ctx: ExistentialContext
    ): SharTry[DLExpression] =
      visitQuantification(ctx, Existential.apply)

    override def visitUniversal(
        ctx: UniversalContext
    ): SharTry[DLExpression] =
      visitQuantification(ctx, Universal.apply)

    override def visitLess(ctx: LessContext): SharTry[DLExpression] =
      visitNQuantification(ctx, LessThan.apply)

    override def visitGreater(ctx: GreaterContext): SharTry[DLExpression] =
      visitNQuantification(ctx, GreaterThan.apply)

    override def visitExactly(ctx: ExactlyContext): SharTry[DLExpression] =
      visitNQuantification(ctx, Exactly.apply)

    override def visitNegated_formula(
        ctx: Negated_formulaContext
    ): SharTry[DLExpression] =
      selectChild(ctx, 1, Complement.apply)

    override def visitParen_formula(
        ctx: Paren_formulaContext
    ): SharTry[DLExpression] =
      selectChild(ctx, 1, identity)

    override def visitConcept_with_context(
        ctx: Concept_with_contextContext
    ): SharTry[DLExpression] =
      for
        concept <- visit(ctx.getChild(0))
        c <- Concept.orElse(concept, ierr("visitConcept_with_context"))
        axiomsI <- visit(ctx.getChild(3))
        axioms <- AxiomSet.orElse(
          axiomsI,
          ierr("visitConcept_with_context")
        )
      yield ConceptWithContext(c, axioms)

    override def visitAxiom(ctx: AxiomContext): SharTry[AxiomSet] =
      val axioms = ctx.subsumption.asScala.toList.map { child =>
        for
          c <- visit(child)
          axiom <- Subsumption.orElse(c, ierr("visitAxiom"))
        yield axiom
      }
      SharError
        .getFirst(axioms)
        .map(_.toSet)
        .map(a => AxiomSet(a.map(_.asInstanceOf[Axiom])))

    override def visitSubsumption(ctx: SubsumptionContext): SharTry[Axiom] =
      for
        lhsI <- visit(ctx.getChild(0))
        rhsI <- visit(ctx.getChild(2))
        lhs <- Concept.orElse(lhsI, ierr("visitSubsumption"))
        rhs <- Concept.orElse(rhsI, ierr("visitSubsumption"))
      yield Subsumption(lhs, rhs)
}
