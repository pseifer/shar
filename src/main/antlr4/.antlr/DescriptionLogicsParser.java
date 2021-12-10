// Generated from d:\Description Logics\shar\src\main\antlr4\DescriptionLogics.g4 by ANTLR 4.8

package de.pseifer.shar.parsing;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DescriptionLogicsParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, IRI=3, FULL_IRI=4, PREFIXED_IRI=5, PREFIX=6, NAME=7, GREATER=8, 
		LESS=9, EXACTLY=10, NUMBER=11, CHARACTER=12, WHITESPACE=13, UNION=14, 
		INTERSECTION=15, UNIVERSAL=16, EXISTENTIAL=17, NOT=18, TOP=19, BOTTOM=20, 
		NOMINAL_LEFT=21, NOMINAL_RIGHT=22, GROUP_LEFT=23, GROUP_RIGHT=24, DOT=25, 
		SQSUBSETEQ=26, AXIOMSEP=27;
	public static final int
		RULE_formula = 0, RULE_formula0 = 1, RULE_concept_with_context = 2, RULE_axiom = 3, 
		RULE_subsumption = 4, RULE_formula1 = 5, RULE_formula2 = 6, RULE_formula3 = 7, 
		RULE_union = 8, RULE_intersection = 9, RULE_negated_formula = 10, RULE_paren_formula = 11, 
		RULE_universal = 12, RULE_existential = 13, RULE_greater = 14, RULE_less = 15, 
		RULE_exactly = 16, RULE_top = 17, RULE_bottom = 18, RULE_concept = 19, 
		RULE_nominal = 20, RULE_role = 21;
	private static String[] makeRuleNames() {
		return new String[] {
			"formula", "formula0", "concept_with_context", "axiom", "subsumption", 
			"formula1", "formula2", "formula3", "union", "intersection", "negated_formula", 
			"paren_formula", "universal", "existential", "greater", "less", "exactly", 
			"top", "bottom", "concept", "nominal", "role"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'@'", "'-'", null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "'{'", "'}'", "'('", 
			"')'", "'.'", null, "';'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "IRI", "FULL_IRI", "PREFIXED_IRI", "PREFIX", "NAME", 
			"GREATER", "LESS", "EXACTLY", "NUMBER", "CHARACTER", "WHITESPACE", "UNION", 
			"INTERSECTION", "UNIVERSAL", "EXISTENTIAL", "NOT", "TOP", "BOTTOM", "NOMINAL_LEFT", 
			"NOMINAL_RIGHT", "GROUP_LEFT", "GROUP_RIGHT", "DOT", "SQSUBSETEQ", "AXIOMSEP"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "DescriptionLogics.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DescriptionLogicsParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class FormulaContext extends ParserRuleContext {
		public Formula0Context formula0() {
			return getRuleContext(Formula0Context.class,0);
		}
		public TerminalNode EOF() { return getToken(DescriptionLogicsParser.EOF, 0); }
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
	}

	public final FormulaContext formula() throws RecognitionException {
		FormulaContext _localctx = new FormulaContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			formula0();
			setState(45);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Formula0Context extends ParserRuleContext {
		public Concept_with_contextContext concept_with_context() {
			return getRuleContext(Concept_with_contextContext.class,0);
		}
		public Formula1Context formula1() {
			return getRuleContext(Formula1Context.class,0);
		}
		public Formula0Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula0; }
	}

	public final Formula0Context formula0() throws RecognitionException {
		Formula0Context _localctx = new Formula0Context(_ctx, getState());
		enterRule(_localctx, 2, RULE_formula0);
		try {
			setState(49);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(47);
				concept_with_context();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(48);
				formula1();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Concept_with_contextContext extends ParserRuleContext {
		public Formula1Context formula1() {
			return getRuleContext(Formula1Context.class,0);
		}
		public TerminalNode GROUP_LEFT() { return getToken(DescriptionLogicsParser.GROUP_LEFT, 0); }
		public AxiomContext axiom() {
			return getRuleContext(AxiomContext.class,0);
		}
		public TerminalNode GROUP_RIGHT() { return getToken(DescriptionLogicsParser.GROUP_RIGHT, 0); }
		public Concept_with_contextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_concept_with_context; }
	}

	public final Concept_with_contextContext concept_with_context() throws RecognitionException {
		Concept_with_contextContext _localctx = new Concept_with_contextContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_concept_with_context);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			formula1();
			setState(52);
			match(T__0);
			setState(53);
			match(GROUP_LEFT);
			setState(54);
			axiom();
			setState(55);
			match(GROUP_RIGHT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AxiomContext extends ParserRuleContext {
		public List<SubsumptionContext> subsumption() {
			return getRuleContexts(SubsumptionContext.class);
		}
		public SubsumptionContext subsumption(int i) {
			return getRuleContext(SubsumptionContext.class,i);
		}
		public AxiomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_axiom; }
	}

	public final AxiomContext axiom() throws RecognitionException {
		AxiomContext _localctx = new AxiomContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_axiom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(57);
				subsumption();
				}
				}
				setState(60); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IRI) | (1L << GREATER) | (1L << LESS) | (1L << EXACTLY) | (1L << UNIVERSAL) | (1L << EXISTENTIAL) | (1L << NOT) | (1L << TOP) | (1L << BOTTOM) | (1L << NOMINAL_LEFT) | (1L << GROUP_LEFT))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubsumptionContext extends ParserRuleContext {
		public List<Formula0Context> formula0() {
			return getRuleContexts(Formula0Context.class);
		}
		public Formula0Context formula0(int i) {
			return getRuleContext(Formula0Context.class,i);
		}
		public TerminalNode SQSUBSETEQ() { return getToken(DescriptionLogicsParser.SQSUBSETEQ, 0); }
		public SubsumptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subsumption; }
	}

	public final SubsumptionContext subsumption() throws RecognitionException {
		SubsumptionContext _localctx = new SubsumptionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_subsumption);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			formula0();
			setState(63);
			match(SQSUBSETEQ);
			setState(64);
			formula0();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Formula1Context extends ParserRuleContext {
		public UnionContext union() {
			return getRuleContext(UnionContext.class,0);
		}
		public Formula2Context formula2() {
			return getRuleContext(Formula2Context.class,0);
		}
		public Formula1Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula1; }
	}

	public final Formula1Context formula1() throws RecognitionException {
		Formula1Context _localctx = new Formula1Context(_ctx, getState());
		enterRule(_localctx, 10, RULE_formula1);
		try {
			setState(68);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(66);
				union();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(67);
				formula2();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Formula2Context extends ParserRuleContext {
		public IntersectionContext intersection() {
			return getRuleContext(IntersectionContext.class,0);
		}
		public Formula3Context formula3() {
			return getRuleContext(Formula3Context.class,0);
		}
		public Formula2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula2; }
	}

	public final Formula2Context formula2() throws RecognitionException {
		Formula2Context _localctx = new Formula2Context(_ctx, getState());
		enterRule(_localctx, 12, RULE_formula2);
		try {
			setState(72);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				intersection();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(71);
				formula3();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Formula3Context extends ParserRuleContext {
		public Paren_formulaContext paren_formula() {
			return getRuleContext(Paren_formulaContext.class,0);
		}
		public Negated_formulaContext negated_formula() {
			return getRuleContext(Negated_formulaContext.class,0);
		}
		public UniversalContext universal() {
			return getRuleContext(UniversalContext.class,0);
		}
		public ExistentialContext existential() {
			return getRuleContext(ExistentialContext.class,0);
		}
		public GreaterContext greater() {
			return getRuleContext(GreaterContext.class,0);
		}
		public LessContext less() {
			return getRuleContext(LessContext.class,0);
		}
		public ExactlyContext exactly() {
			return getRuleContext(ExactlyContext.class,0);
		}
		public TopContext top() {
			return getRuleContext(TopContext.class,0);
		}
		public BottomContext bottom() {
			return getRuleContext(BottomContext.class,0);
		}
		public NominalContext nominal() {
			return getRuleContext(NominalContext.class,0);
		}
		public ConceptContext concept() {
			return getRuleContext(ConceptContext.class,0);
		}
		public Formula3Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula3; }
	}

	public final Formula3Context formula3() throws RecognitionException {
		Formula3Context _localctx = new Formula3Context(_ctx, getState());
		enterRule(_localctx, 14, RULE_formula3);
		try {
			setState(85);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case GROUP_LEFT:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				paren_formula();
				}
				break;
			case NOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(75);
				negated_formula();
				}
				break;
			case UNIVERSAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(76);
				universal();
				}
				break;
			case EXISTENTIAL:
				enterOuterAlt(_localctx, 4);
				{
				setState(77);
				existential();
				}
				break;
			case GREATER:
				enterOuterAlt(_localctx, 5);
				{
				setState(78);
				greater();
				}
				break;
			case LESS:
				enterOuterAlt(_localctx, 6);
				{
				setState(79);
				less();
				}
				break;
			case EXACTLY:
				enterOuterAlt(_localctx, 7);
				{
				setState(80);
				exactly();
				}
				break;
			case TOP:
				enterOuterAlt(_localctx, 8);
				{
				setState(81);
				top();
				}
				break;
			case BOTTOM:
				enterOuterAlt(_localctx, 9);
				{
				setState(82);
				bottom();
				}
				break;
			case NOMINAL_LEFT:
				enterOuterAlt(_localctx, 10);
				{
				setState(83);
				nominal();
				}
				break;
			case IRI:
				enterOuterAlt(_localctx, 11);
				{
				setState(84);
				concept();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnionContext extends ParserRuleContext {
		public List<Formula3Context> formula3() {
			return getRuleContexts(Formula3Context.class);
		}
		public Formula3Context formula3(int i) {
			return getRuleContext(Formula3Context.class,i);
		}
		public TerminalNode UNION() { return getToken(DescriptionLogicsParser.UNION, 0); }
		public UnionContext union() {
			return getRuleContext(UnionContext.class,0);
		}
		public UnionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_union; }
	}

	public final UnionContext union() throws RecognitionException {
		UnionContext _localctx = new UnionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_union);
		try {
			setState(95);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(87);
				formula3();
				setState(88);
				match(UNION);
				setState(89);
				formula3();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(91);
				formula3();
				setState(92);
				match(UNION);
				setState(93);
				union();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntersectionContext extends ParserRuleContext {
		public List<Formula3Context> formula3() {
			return getRuleContexts(Formula3Context.class);
		}
		public Formula3Context formula3(int i) {
			return getRuleContext(Formula3Context.class,i);
		}
		public TerminalNode INTERSECTION() { return getToken(DescriptionLogicsParser.INTERSECTION, 0); }
		public IntersectionContext intersection() {
			return getRuleContext(IntersectionContext.class,0);
		}
		public IntersectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intersection; }
	}

	public final IntersectionContext intersection() throws RecognitionException {
		IntersectionContext _localctx = new IntersectionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_intersection);
		try {
			setState(105);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(97);
				formula3();
				setState(98);
				match(INTERSECTION);
				setState(99);
				formula3();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(101);
				formula3();
				setState(102);
				match(INTERSECTION);
				setState(103);
				intersection();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Negated_formulaContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(DescriptionLogicsParser.NOT, 0); }
		public Formula3Context formula3() {
			return getRuleContext(Formula3Context.class,0);
		}
		public Negated_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negated_formula; }
	}

	public final Negated_formulaContext negated_formula() throws RecognitionException {
		Negated_formulaContext _localctx = new Negated_formulaContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_negated_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(NOT);
			setState(108);
			formula3();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Paren_formulaContext extends ParserRuleContext {
		public TerminalNode GROUP_LEFT() { return getToken(DescriptionLogicsParser.GROUP_LEFT, 0); }
		public Formula1Context formula1() {
			return getRuleContext(Formula1Context.class,0);
		}
		public TerminalNode GROUP_RIGHT() { return getToken(DescriptionLogicsParser.GROUP_RIGHT, 0); }
		public Paren_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paren_formula; }
	}

	public final Paren_formulaContext paren_formula() throws RecognitionException {
		Paren_formulaContext _localctx = new Paren_formulaContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_paren_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(GROUP_LEFT);
			setState(111);
			formula1();
			setState(112);
			match(GROUP_RIGHT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UniversalContext extends ParserRuleContext {
		public TerminalNode UNIVERSAL() { return getToken(DescriptionLogicsParser.UNIVERSAL, 0); }
		public RoleContext role() {
			return getRuleContext(RoleContext.class,0);
		}
		public TerminalNode DOT() { return getToken(DescriptionLogicsParser.DOT, 0); }
		public Formula3Context formula3() {
			return getRuleContext(Formula3Context.class,0);
		}
		public UniversalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_universal; }
	}

	public final UniversalContext universal() throws RecognitionException {
		UniversalContext _localctx = new UniversalContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_universal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(UNIVERSAL);
			setState(115);
			role();
			setState(116);
			match(DOT);
			setState(117);
			formula3();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExistentialContext extends ParserRuleContext {
		public TerminalNode EXISTENTIAL() { return getToken(DescriptionLogicsParser.EXISTENTIAL, 0); }
		public RoleContext role() {
			return getRuleContext(RoleContext.class,0);
		}
		public TerminalNode DOT() { return getToken(DescriptionLogicsParser.DOT, 0); }
		public Formula3Context formula3() {
			return getRuleContext(Formula3Context.class,0);
		}
		public ExistentialContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_existential; }
	}

	public final ExistentialContext existential() throws RecognitionException {
		ExistentialContext _localctx = new ExistentialContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_existential);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(EXISTENTIAL);
			setState(120);
			role();
			setState(121);
			match(DOT);
			setState(122);
			formula3();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GreaterContext extends ParserRuleContext {
		public TerminalNode GREATER() { return getToken(DescriptionLogicsParser.GREATER, 0); }
		public RoleContext role() {
			return getRuleContext(RoleContext.class,0);
		}
		public TerminalNode DOT() { return getToken(DescriptionLogicsParser.DOT, 0); }
		public Formula3Context formula3() {
			return getRuleContext(Formula3Context.class,0);
		}
		public GreaterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_greater; }
	}

	public final GreaterContext greater() throws RecognitionException {
		GreaterContext _localctx = new GreaterContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_greater);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			match(GREATER);
			setState(125);
			role();
			setState(126);
			match(DOT);
			setState(127);
			formula3();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LessContext extends ParserRuleContext {
		public TerminalNode LESS() { return getToken(DescriptionLogicsParser.LESS, 0); }
		public RoleContext role() {
			return getRuleContext(RoleContext.class,0);
		}
		public TerminalNode DOT() { return getToken(DescriptionLogicsParser.DOT, 0); }
		public Formula3Context formula3() {
			return getRuleContext(Formula3Context.class,0);
		}
		public LessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_less; }
	}

	public final LessContext less() throws RecognitionException {
		LessContext _localctx = new LessContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_less);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			match(LESS);
			setState(130);
			role();
			setState(131);
			match(DOT);
			setState(132);
			formula3();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExactlyContext extends ParserRuleContext {
		public TerminalNode EXACTLY() { return getToken(DescriptionLogicsParser.EXACTLY, 0); }
		public RoleContext role() {
			return getRuleContext(RoleContext.class,0);
		}
		public TerminalNode DOT() { return getToken(DescriptionLogicsParser.DOT, 0); }
		public Formula3Context formula3() {
			return getRuleContext(Formula3Context.class,0);
		}
		public ExactlyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exactly; }
	}

	public final ExactlyContext exactly() throws RecognitionException {
		ExactlyContext _localctx = new ExactlyContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_exactly);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(EXACTLY);
			setState(135);
			role();
			setState(136);
			match(DOT);
			setState(137);
			formula3();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TopContext extends ParserRuleContext {
		public TerminalNode TOP() { return getToken(DescriptionLogicsParser.TOP, 0); }
		public TopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_top; }
	}

	public final TopContext top() throws RecognitionException {
		TopContext _localctx = new TopContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_top);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(TOP);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BottomContext extends ParserRuleContext {
		public TerminalNode BOTTOM() { return getToken(DescriptionLogicsParser.BOTTOM, 0); }
		public BottomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bottom; }
	}

	public final BottomContext bottom() throws RecognitionException {
		BottomContext _localctx = new BottomContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_bottom);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(BOTTOM);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConceptContext extends ParserRuleContext {
		public TerminalNode IRI() { return getToken(DescriptionLogicsParser.IRI, 0); }
		public ConceptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_concept; }
	}

	public final ConceptContext concept() throws RecognitionException {
		ConceptContext _localctx = new ConceptContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_concept);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(IRI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NominalContext extends ParserRuleContext {
		public TerminalNode NOMINAL_LEFT() { return getToken(DescriptionLogicsParser.NOMINAL_LEFT, 0); }
		public TerminalNode IRI() { return getToken(DescriptionLogicsParser.IRI, 0); }
		public TerminalNode NOMINAL_RIGHT() { return getToken(DescriptionLogicsParser.NOMINAL_RIGHT, 0); }
		public NominalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nominal; }
	}

	public final NominalContext nominal() throws RecognitionException {
		NominalContext _localctx = new NominalContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_nominal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145);
			match(NOMINAL_LEFT);
			setState(146);
			match(IRI);
			setState(147);
			match(NOMINAL_RIGHT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RoleContext extends ParserRuleContext {
		public TerminalNode IRI() { return getToken(DescriptionLogicsParser.IRI, 0); }
		public RoleContext role() {
			return getRuleContext(RoleContext.class,0);
		}
		public RoleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_role; }
	}

	public final RoleContext role() throws RecognitionException {
		RoleContext _localctx = new RoleContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_role);
		try {
			setState(152);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IRI:
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				match(IRI);
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(150);
				match(T__1);
				setState(151);
				role();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\35\u009d\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\2\3\3\3\3"+
		"\5\3\64\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\6\5=\n\5\r\5\16\5>\3\6\3\6\3\6"+
		"\3\6\3\7\3\7\5\7G\n\7\3\b\3\b\5\bK\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\5\tX\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\nb\n\n\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13l\n\13\3\f\3\f\3\f\3\r\3\r\3\r\3"+
		"\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3"+
		"\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3"+
		"\24\3\24\3\25\3\25\3\26\3\26\3\26\3\26\3\27\3\27\3\27\5\27\u009b\n\27"+
		"\3\27\2\2\30\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,\2\2\2\u0097"+
		"\2.\3\2\2\2\4\63\3\2\2\2\6\65\3\2\2\2\b<\3\2\2\2\n@\3\2\2\2\fF\3\2\2\2"+
		"\16J\3\2\2\2\20W\3\2\2\2\22a\3\2\2\2\24k\3\2\2\2\26m\3\2\2\2\30p\3\2\2"+
		"\2\32t\3\2\2\2\34y\3\2\2\2\36~\3\2\2\2 \u0083\3\2\2\2\"\u0088\3\2\2\2"+
		"$\u008d\3\2\2\2&\u008f\3\2\2\2(\u0091\3\2\2\2*\u0093\3\2\2\2,\u009a\3"+
		"\2\2\2./\5\4\3\2/\60\7\2\2\3\60\3\3\2\2\2\61\64\5\6\4\2\62\64\5\f\7\2"+
		"\63\61\3\2\2\2\63\62\3\2\2\2\64\5\3\2\2\2\65\66\5\f\7\2\66\67\7\3\2\2"+
		"\678\7\31\2\289\5\b\5\29:\7\32\2\2:\7\3\2\2\2;=\5\n\6\2<;\3\2\2\2=>\3"+
		"\2\2\2><\3\2\2\2>?\3\2\2\2?\t\3\2\2\2@A\5\4\3\2AB\7\34\2\2BC\5\4\3\2C"+
		"\13\3\2\2\2DG\5\22\n\2EG\5\16\b\2FD\3\2\2\2FE\3\2\2\2G\r\3\2\2\2HK\5\24"+
		"\13\2IK\5\20\t\2JH\3\2\2\2JI\3\2\2\2K\17\3\2\2\2LX\5\30\r\2MX\5\26\f\2"+
		"NX\5\32\16\2OX\5\34\17\2PX\5\36\20\2QX\5 \21\2RX\5\"\22\2SX\5$\23\2TX"+
		"\5&\24\2UX\5*\26\2VX\5(\25\2WL\3\2\2\2WM\3\2\2\2WN\3\2\2\2WO\3\2\2\2W"+
		"P\3\2\2\2WQ\3\2\2\2WR\3\2\2\2WS\3\2\2\2WT\3\2\2\2WU\3\2\2\2WV\3\2\2\2"+
		"X\21\3\2\2\2YZ\5\20\t\2Z[\7\20\2\2[\\\5\20\t\2\\b\3\2\2\2]^\5\20\t\2^"+
		"_\7\20\2\2_`\5\22\n\2`b\3\2\2\2aY\3\2\2\2a]\3\2\2\2b\23\3\2\2\2cd\5\20"+
		"\t\2de\7\21\2\2ef\5\20\t\2fl\3\2\2\2gh\5\20\t\2hi\7\21\2\2ij\5\24\13\2"+
		"jl\3\2\2\2kc\3\2\2\2kg\3\2\2\2l\25\3\2\2\2mn\7\24\2\2no\5\20\t\2o\27\3"+
		"\2\2\2pq\7\31\2\2qr\5\f\7\2rs\7\32\2\2s\31\3\2\2\2tu\7\22\2\2uv\5,\27"+
		"\2vw\7\33\2\2wx\5\20\t\2x\33\3\2\2\2yz\7\23\2\2z{\5,\27\2{|\7\33\2\2|"+
		"}\5\20\t\2}\35\3\2\2\2~\177\7\n\2\2\177\u0080\5,\27\2\u0080\u0081\7\33"+
		"\2\2\u0081\u0082\5\20\t\2\u0082\37\3\2\2\2\u0083\u0084\7\13\2\2\u0084"+
		"\u0085\5,\27\2\u0085\u0086\7\33\2\2\u0086\u0087\5\20\t\2\u0087!\3\2\2"+
		"\2\u0088\u0089\7\f\2\2\u0089\u008a\5,\27\2\u008a\u008b\7\33\2\2\u008b"+
		"\u008c\5\20\t\2\u008c#\3\2\2\2\u008d\u008e\7\25\2\2\u008e%\3\2\2\2\u008f"+
		"\u0090\7\26\2\2\u0090\'\3\2\2\2\u0091\u0092\7\5\2\2\u0092)\3\2\2\2\u0093"+
		"\u0094\7\27\2\2\u0094\u0095\7\5\2\2\u0095\u0096\7\30\2\2\u0096+\3\2\2"+
		"\2\u0097\u009b\7\5\2\2\u0098\u0099\7\4\2\2\u0099\u009b\5,\27\2\u009a\u0097"+
		"\3\2\2\2\u009a\u0098\3\2\2\2\u009b-\3\2\2\2\n\63>FJWak\u009a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}