grammar DescriptionLogics;

@header {
package de.pseifer.shar.parsing;
}

formula: formula1 EOF;

axiom: (subsumption)+;

subsumption: formula1 SQSUBSETEQ formula1;

formula1: union | formula2;

formula2: intersection | formula3;

formula3:
	paren_formula
	| negated_formula
	| universal
	| existential
	| greater
	| less
	| exactly
	| top
	| bottom
	| nominal
	| concept;

union: formula3 UNION formula3 | formula3 UNION union;

intersection:
	formula3 INTERSECTION formula3
	| formula3 INTERSECTION intersection;

negated_formula: NOT formula3;

paren_formula: GROUP_LEFT formula1 GROUP_RIGHT;

universal: UNIVERSAL role DOT formula3;

existential: EXISTENTIAL role DOT formula3;

greater: GREATER role DOT formula3;

less: LESS role DOT formula3;

exactly: EXACTLY role DOT formula3;

top: TOP;

bottom: BOTTOM;

concept: IRI;

nominal: NOMINAL_LEFT IRI NOMINAL_RIGHT;

role: IRI | '-' role;

/* IRI */

IRI: PREFIXED_IRI | FULL_IRI;

FULL_IRI: '<' ~('>')+ '>';

PREFIXED_IRI: PREFIX NAME;

PREFIX: CHARACTER* ':';

NAME: CHARACTER+;

/* TYPES: TODO */

/* TOKEN */

GREATER: '#>' NUMBER | '≥' NUMBER;

LESS: '#<' NUMBER | '≤' NUMBER;

EXACTLY: '#=' NUMBER  | '=' NUMBER;

NUMBER: ('0' .. '9')+;

CHARACTER: ('0' .. '9' | 'a' .. 'z' | 'A' .. 'Z' | '_');

WHITESPACE: (' ' | '\t' | '\r' | '\n')+ -> skip;

UNION: '|' | '⊔';

INTERSECTION: '&' | '⊓';

UNIVERSAL: '#A' | '∀';

EXISTENTIAL: '#E' | '∃';

NOT: '!' | '¬';

TOP: '#t' | '⊤';

BOTTOM: '#f' | '⊥';

NOMINAL_LEFT: '{';

NOMINAL_RIGHT: '}';

GROUP_LEFT: '(';

GROUP_RIGHT: ')';

DOT: '.';

SQSUBSETEQ: '⊑' | ':<=';

AXIOMSEP: ';';
