// Generated from d:\Description Logics\shar\src\main\antlr4\DescriptionLogics.g4 by ANTLR 4.8

package de.pseifer.shar.parsing;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DescriptionLogicsLexer extends Lexer {
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "IRI", "FULL_IRI", "PREFIXED_IRI", "PREFIX", "NAME", 
			"GREATER", "LESS", "EXACTLY", "NUMBER", "CHARACTER", "WHITESPACE", "UNION", 
			"INTERSECTION", "UNIVERSAL", "EXISTENTIAL", "NOT", "TOP", "BOTTOM", "NOMINAL_LEFT", 
			"NOMINAL_RIGHT", "GROUP_LEFT", "GROUP_RIGHT", "DOT", "SQSUBSETEQ", "AXIOMSEP"
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


	public DescriptionLogicsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DescriptionLogics.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\35\u00a2\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\3\3\3\3\4\3\4\5\4@\n\4\3"+
		"\5\3\5\6\5D\n\5\r\5\16\5E\3\5\3\5\3\6\3\6\3\6\3\7\7\7N\n\7\f\7\16\7Q\13"+
		"\7\3\7\3\7\3\b\6\bV\n\b\r\b\16\bW\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n"+
		"\3\n\3\13\3\13\3\13\3\13\3\13\3\f\6\fj\n\f\r\f\16\fk\3\r\3\r\3\16\6\16"+
		"q\n\16\r\16\16\16r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\21\5\21~"+
		"\n\21\3\22\3\22\3\22\5\22\u0083\n\22\3\23\3\23\3\24\3\24\3\24\5\24\u008a"+
		"\n\24\3\25\3\25\3\25\5\25\u008f\n\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31"+
		"\3\31\3\32\3\32\3\33\3\33\3\33\3\33\5\33\u009f\n\33\3\34\3\34\2\2\35\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\35\3\2\b\3\2"+
		"@@\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\4\2~~\u2296\u2296\4\2((\u2295\u2295"+
		"\4\2##\u00ae\u00ae\2\u00ac\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\39\3\2\2\2\5;\3\2\2\2\7?\3\2\2\2\tA\3\2\2\2\13I\3\2\2\2\rO\3"+
		"\2\2\2\17U\3\2\2\2\21Y\3\2\2\2\23^\3\2\2\2\25c\3\2\2\2\27i\3\2\2\2\31"+
		"m\3\2\2\2\33p\3\2\2\2\35v\3\2\2\2\37x\3\2\2\2!}\3\2\2\2#\u0082\3\2\2\2"+
		"%\u0084\3\2\2\2\'\u0089\3\2\2\2)\u008e\3\2\2\2+\u0090\3\2\2\2-\u0092\3"+
		"\2\2\2/\u0094\3\2\2\2\61\u0096\3\2\2\2\63\u0098\3\2\2\2\65\u009e\3\2\2"+
		"\2\67\u00a0\3\2\2\29:\7B\2\2:\4\3\2\2\2;<\7/\2\2<\6\3\2\2\2=@\5\13\6\2"+
		">@\5\t\5\2?=\3\2\2\2?>\3\2\2\2@\b\3\2\2\2AC\7>\2\2BD\n\2\2\2CB\3\2\2\2"+
		"DE\3\2\2\2EC\3\2\2\2EF\3\2\2\2FG\3\2\2\2GH\7@\2\2H\n\3\2\2\2IJ\5\r\7\2"+
		"JK\5\17\b\2K\f\3\2\2\2LN\5\31\r\2ML\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2"+
		"\2\2PR\3\2\2\2QO\3\2\2\2RS\7<\2\2S\16\3\2\2\2TV\5\31\r\2UT\3\2\2\2VW\3"+
		"\2\2\2WU\3\2\2\2WX\3\2\2\2X\20\3\2\2\2YZ\7@\2\2Z[\7?\2\2[\\\3\2\2\2\\"+
		"]\5\27\f\2]\22\3\2\2\2^_\7>\2\2_`\7?\2\2`a\3\2\2\2ab\5\27\f\2b\24\3\2"+
		"\2\2cd\7?\2\2de\7?\2\2ef\3\2\2\2fg\5\27\f\2g\26\3\2\2\2hj\4\62;\2ih\3"+
		"\2\2\2jk\3\2\2\2ki\3\2\2\2kl\3\2\2\2l\30\3\2\2\2mn\t\3\2\2n\32\3\2\2\2"+
		"oq\t\4\2\2po\3\2\2\2qr\3\2\2\2rp\3\2\2\2rs\3\2\2\2st\3\2\2\2tu\b\16\2"+
		"\2u\34\3\2\2\2vw\t\5\2\2w\36\3\2\2\2xy\t\6\2\2y \3\2\2\2z{\7%\2\2{~\7"+
		"C\2\2|~\7\u2202\2\2}z\3\2\2\2}|\3\2\2\2~\"\3\2\2\2\177\u0080\7%\2\2\u0080"+
		"\u0083\7G\2\2\u0081\u0083\7\u2205\2\2\u0082\177\3\2\2\2\u0082\u0081\3"+
		"\2\2\2\u0083$\3\2\2\2\u0084\u0085\t\7\2\2\u0085&\3\2\2\2\u0086\u0087\7"+
		"%\2\2\u0087\u008a\7v\2\2\u0088\u008a\7\u22a6\2\2\u0089\u0086\3\2\2\2\u0089"+
		"\u0088\3\2\2\2\u008a(\3\2\2\2\u008b\u008c\7%\2\2\u008c\u008f\7h\2\2\u008d"+
		"\u008f\7\u22a7\2\2\u008e\u008b\3\2\2\2\u008e\u008d\3\2\2\2\u008f*\3\2"+
		"\2\2\u0090\u0091\7}\2\2\u0091,\3\2\2\2\u0092\u0093\7\177\2\2\u0093.\3"+
		"\2\2\2\u0094\u0095\7*\2\2\u0095\60\3\2\2\2\u0096\u0097\7+\2\2\u0097\62"+
		"\3\2\2\2\u0098\u0099\7\60\2\2\u0099\64\3\2\2\2\u009a\u009f\7\u2293\2\2"+
		"\u009b\u009c\7<\2\2\u009c\u009d\7>\2\2\u009d\u009f\7?\2\2\u009e\u009a"+
		"\3\2\2\2\u009e\u009b\3\2\2\2\u009f\66\3\2\2\2\u00a0\u00a1\7=\2\2\u00a1"+
		"8\3\2\2\2\16\2?EOWkr}\u0082\u0089\u008e\u009e\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}