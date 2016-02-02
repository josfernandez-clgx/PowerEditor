package com.mindbox.server.parser.jtb.rule;

public interface RuleParserConstants {

	public static final int EOF = 0;
	public static final int SINGLE_LINE_COMMENT = 9;
	public static final int FORMAL_COMMENT = 11;
	public static final int MULTI_LINE_COMMENT = 12;
	public static final int FALSE = 14;
	public static final int IF = 15;
	public static final int THEN = 16;
	public static final int NULL = 17;
	public static final int TRUE = 18;
	public static final int ANY = 19;
	public static final int EXISTS = 20;
	public static final int ALL = 21;
	public static final int ATLEAST = 22;
	public static final int ATMOST = 23;
	public static final int WITH = 24;
	public static final int EXCLUDING = 25;
	public static final int COLUMN = 26;
	public static final int CELLVALUE = 27;
	public static final int INTEGER_LITERAL = 28;
	public static final int DECIMAL_LITERAL = 29;
	public static final int HEX_LITERAL = 30;
	public static final int OCTAL_LITERAL = 31;
	public static final int FLOATING_POINT_LITERAL = 32;
	public static final int EXPONENT = 33;
	public static final int CHARACTER_LITERAL = 34;
	public static final int STRING_LITERAL = 35;
	public static final int ASSIGN = 36;
	public static final int GT = 37;
	public static final int LT = 38;
	public static final int BANG = 39;
	public static final int TILDE = 40;
	public static final int HOOK = 41;
	public static final int COLON = 42;
	public static final int EQ = 43;
	public static final int LE = 44;
	public static final int GE = 45;
	public static final int NE = 46;
	public static final int SC_OR = 47;
	public static final int SC_AND = 48;
	public static final int PLUS = 49;
	public static final int MINUS = 50;
	public static final int STAR = 51;
	public static final int SLASH = 52;
	public static final int REM = 53;
	public static final int IN = 54;
	public static final int BETWEEN = 55;
	public static final int IDENTIFIER = 56;
	public static final int LETTER = 57;
	public static final int DIGIT = 58;
	public static final int LPAREN = 59;
	public static final int RPAREN = 60;
	public static final int LBRACE = 61;
	public static final int RBRACE = 62;
	public static final int LBRACKET = 63;
	public static final int RBRACKET = 64;
	public static final int SEMICOLON = 65;
	public static final int COMMA = 66;
	public static final int DOT = 67;
	public static final int DEFAULT = 0;
	public static final int IN_SINGLE_LINE_COMMENT = 1;
	public static final int IN_FORMAL_COMMENT = 2;
	public static final int IN_MULTI_LINE_COMMENT = 3;
	
	public static final int RULENAME = 68; 
	public static final int FORKEY = 69;
	
	public static final String tokenImage[] =
		{
			"<EOF>",
			"\" \"",
			"\"\\t\"",
			"\"\\n\"",
			"\"\\r\"",
			"\"\\f\"",
			"\"//\"",
			"<token of kind 7>",
			"\"/*\"",
			"<SINGLE_LINE_COMMENT>",
			"<token of kind 10>",
			"\"*/\"",
			"\"*/\"",
			"<token of kind 13>",
			"\"false\"",
			"\"if\"",
			"\"then\"",
			"\"null\"",
			"\"true\"",
			"\"any\"",
			"\"exists\"",
			"\"all\"",
			"\"at least\"",
			"\"at most\"",
			"\"with\"",
			"\"excluding\"",
			"\"column\"",
			"\"cellValue\"",
			"<INTEGER_LITERAL>",
			"<DECIMAL_LITERAL>",
			"<HEX_LITERAL>",
			"<OCTAL_LITERAL>",
			"<FLOATING_POINT_LITERAL>",
			"<EXPONENT>",
			"<CHARACTER_LITERAL>",
			"<STRING_LITERAL>",
			"\"=\"",
			"\">\"",
			"\"<\"",
			"\"!\"",
			"\"~\"",
			"\"?\"",
			"\":\"",
			"\"==\"",
			"\"<=\"",
			"\">=\"",
			"\"!=\"",
			"\"or\"",
			"\"and\"",
			"\"+\"",
			"\"-\"",
			"\"*\"",
			"\"/\"",
			"\"%\"",
			"\"in\"",
			"\"between\"",
			"<IDENTIFIER>",
			"<LETTER>",
			"<DIGIT>",
			"\"(\"",
			"\")\"",
			"\"{\"",
			"\"}\"",
			"\"[\"",
			"\"]\"",
			"\";\"",
			"\",\"",
			"\".\"",
			"\"ruleName\"",
			"\"for key\"" };

}