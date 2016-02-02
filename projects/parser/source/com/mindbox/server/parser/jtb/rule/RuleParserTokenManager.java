package com.mindbox.server.parser.jtb.rule;

import java.io.IOException;

// Referenced classes of package com.mindbox.server.parser.jtb.rule:
//            ASCII_UCodeESC_CharStream, TokenMgrError, Token, RuleParserConstants

public class RuleParserTokenManager implements RuleParserConstants {

	private static final int jjMoveStringLiteralDfa8_0(long l, long l1) {
		if ((l1 &= l) == 0L)
			return jjStartNfa_0(6, l, 0L);
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			jjStopStringLiteralDfa_0(7, l1, 0L);
			return 8;
		}
		switch (curChar) {
			case 102 : // 'f'
			default :
				break;

			case 101 : // 'e'
				if ((l1 & 0x8000000L) != 0L)
					return jjStartNfaWithStates_0(8, 27, 32);
				break;

			case 103 : // 'g'
				if ((l1 & 0x2000000L) != 0L)
					return jjStartNfaWithStates_0(8, 25, 32);
				break;
		}
		return jjStartNfa_0(7, l1, 0L);
	}

	private static final boolean jjCanMove_0(int i, int j, int k, long l, long l1) {
		switch (i) {
			case 0 : // '\0'
				return (jjbitVec2[k] & l1) != 0L;
		}
		return (jjbitVec0[j] & l) != 0L;
	}

	private static final int jjStartNfaWithStates_0(int i, int j, int k) {
		jjmatchedKind = j;
		jjmatchedPos = i;
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			return i + 1;
		}
		return jjMoveNfa_0(k, i + 1);
	}

	private static final int jjMoveStringLiteralDfa0_0() {
		switch (curChar) {
			case 33 : // '!'
				jjmatchedKind = 39;
				return jjMoveStringLiteralDfa1_0(0x400000000000L);

			case 37 : // '%'
				return jjStopAtPos(0, 53);

			case 40 : // '('
				return jjStopAtPos(0, 59);

			case 41 : // ')'
				return jjStopAtPos(0, 60);

			case 42 : // '*'
				return jjStopAtPos(0, 51);

			case 43 : // '+'
				return jjStopAtPos(0, 49);

			case 44 : // ','
				return jjStopAtPos(0, 66);

			case 45 : // '-'
				return jjStopAtPos(0, 50);

			case 46 : // '.'
				return jjStartNfaWithStates_0(0, 67, 8);

			case 47 : // '/'
				jjmatchedKind = 52;
				return jjMoveStringLiteralDfa1_0(320L);

			case 58 : // ':'
				return jjStopAtPos(0, 42);

			case 59 : // ';'
				return jjStopAtPos(0, 65);

			case 60 : // '<'
				jjmatchedKind = 38;
				return jjMoveStringLiteralDfa1_0(0x100000000000L);

			case 61 : // '='
				jjmatchedKind = 36;
				return jjMoveStringLiteralDfa1_0(0x80000000000L);

			case 62 : // '>'
				jjmatchedKind = 37;
				return jjMoveStringLiteralDfa1_0(0x200000000000L);

			case 63 : // '?'
				return jjStopAtPos(0, 41);

			case 91 : // '['
				return jjStopAtPos(0, 63);

			case 93 : // ']'
				return jjStopAtPos(0, 64);

			case 97 : // 'a'
				return jjMoveStringLiteralDfa1_0(0x1000000e80000L);

			case 98 : // 'b'
				return jjMoveStringLiteralDfa1_0(0x80000000000000L);

			case 99 : // 'c'
				return jjMoveStringLiteralDfa1_0(0xc000000L);

			case 101 : // 'e'
				return jjMoveStringLiteralDfa1_0(0x2100000L);

			case 102 : // 'f'
				return jjMoveStringLiteralDfa1_0(16384L);

			case 105 : // 'i'
				return jjMoveStringLiteralDfa1_0(0x40000000008000L);

			case 110 : // 'n'
				return jjMoveStringLiteralDfa1_0(0x20000L);

			case 111 : // 'o'
				return jjMoveStringLiteralDfa1_0(0x800000000000L);

			case 116 : // 't'
				return jjMoveStringLiteralDfa1_0(0x50000L);

			case 119 : // 'w'
				return jjMoveStringLiteralDfa1_0(0x1000000L);

			case 123 : // '{'
				return jjStopAtPos(0, 61);

			case 125 : // '}'
				return jjStopAtPos(0, 62);

			case 126 : // '~'
				return jjStopAtPos(0, 40);
		}
		return jjMoveNfa_0(3, 0);
	}

	private static final void ReInitRounds() {
		jjround = 0x80000001;
		for (int i = 52; i-- > 0;)
			jjrounds[i] = 0x80000000;

	}

	public static void SwitchTo(int i) {
		if (i >= 4 || i < 0) {
			throw new TokenMgrError("Error: Ignoring invalid lexical state : " + i + ". State unchanged.", 2);
		}
		else {
			curLexState = i;
			return;
		}
	}

	private static final Token jjFillToken() {
		Token token = Token.newToken(jjmatchedKind);
		token.kind = jjmatchedKind;
		String s = jjstrLiteralImages[jjmatchedKind];
		token.image = s != null ? s : ASCII_UCodeESC_CharStream.GetImage();
		token.beginLine = ASCII_UCodeESC_CharStream.getBeginLine();
		token.beginColumn = ASCII_UCodeESC_CharStream.getBeginColumn();
		token.endLine = ASCII_UCodeESC_CharStream.getEndLine();
		token.endColumn = ASCII_UCodeESC_CharStream.getEndColumn();
		return token;
	}

	private static final int jjMoveStringLiteralDfa1_2(long l) {
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			return 1;
		}
		switch (curChar) {
			case 47 : // '/'
				if ((l & 2048L) != 0L)
					return jjStopAtPos(1, 11);
				else
					return 2;
		}
		return 2;
	}

	private static final int jjMoveStringLiteralDfa1_0(long l) {
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			jjStopStringLiteralDfa_0(0, l, 0L);
			return 1;
		}
		switch (curChar) {
			default :
				break;

			case 42 : // '*'
				if ((l & 256L) != 0L)
					return jjStartNfaWithStates_0(1, 8, 0);
				break;

			case 47 : // '/'
				if ((l & 64L) != 0L)
					return jjStopAtPos(1, 6);
				break;

			case 61 : // '='
				if ((l & 0x80000000000L) != 0L)
					return jjStopAtPos(1, 43);
				if ((l & 0x100000000000L) != 0L)
					return jjStopAtPos(1, 44);
				if ((l & 0x200000000000L) != 0L)
					return jjStopAtPos(1, 45);
				if ((l & 0x400000000000L) != 0L)
					return jjStopAtPos(1, 46);
				break;

			case 97 : // 'a'
				return jjMoveStringLiteralDfa2_0(l, 16384L);

			case 101 : // 'e'
				return jjMoveStringLiteralDfa2_0(l, 0x80000008000000L);

			case 102 : // 'f'
				if ((l & 32768L) != 0L)
					return jjStartNfaWithStates_0(1, 15, 32);
				break;

			case 104 : // 'h'
				return jjMoveStringLiteralDfa2_0(l, 0x10000L);

			case 105 : // 'i'
				return jjMoveStringLiteralDfa2_0(l, 0x1000000L);

			case 108 : // 'l'
				return jjMoveStringLiteralDfa2_0(l, 0x200000L);

			case 110 : // 'n'
				if ((l & 0x40000000000000L) != 0L)
					return jjStartNfaWithStates_0(1, 54, 32);
				else
					return jjMoveStringLiteralDfa2_0(l, 0x1000000080000L);

			case 111 : // 'o'
				return jjMoveStringLiteralDfa2_0(l, 0x4000000L);

			case 114 : // 'r'
				if ((l & 0x800000000000L) != 0L)
					return jjStartNfaWithStates_0(1, 47, 32);
				else
					return jjMoveStringLiteralDfa2_0(l, 0x40000L);

			case 116 : // 't'
				return jjMoveStringLiteralDfa2_0(l, 0xc00000L);

			case 117 : // 'u'
				return jjMoveStringLiteralDfa2_0(l, 0x20000L);

			case 120 : // 'x'
				return jjMoveStringLiteralDfa2_0(l, 0x2100000L);
		}
		return jjStartNfa_0(0, l, 0L);
	}

	private static final int jjMoveStringLiteralDfa0_3() {
		switch (curChar) {
			case 42 : // '*'
				return jjMoveStringLiteralDfa1_3(4096L);
		}
		return 1;
	}

	private static final int jjMoveNfa_1(int i, int j) {
		int k = 0;
		jjnewStateCnt = 3;
		int l = 1;
		jjstateSet[0] = i;
		int i1 = 0x7fffffff;
		do {
			if (++jjround == 0x7fffffff)
				ReInitRounds();
			if (curChar < '@') {
				long l1 = 1L << curChar;
				do switch (jjstateSet[--l]) {
						case 0 : // '\0'
							if ((9216L & l1) != 0L && i1 > 9)
								i1 = 9;
							if (curChar == '\r')
								jjstateSet[jjnewStateCnt++] = 1;
							break;

						case 1 : // '\001'
							if (curChar == '\n' && i1 > 9)
								i1 = 9;
							break;

						case 2 : // '\002'
							if (curChar == '\r')
								jjstateSet[jjnewStateCnt++] = 1;
							break;
					}
						while (l != k);
			}
			else if (curChar < '\200') {
				do switch (jjstateSet[--l]) {
					}
						while (l != k);
			}
			else {
				int j1 = curChar >> 8;
				do switch (jjstateSet[--l]) {
					}
						while (l != k);
			}
			if (i1 != 0x7fffffff) {
				jjmatchedKind = i1;
				jjmatchedPos = j;
				i1 = 0x7fffffff;
			}
			j++;
			if ((l = jjnewStateCnt) == (k = 3 - (jjnewStateCnt = k)))
				return j;
			try {
				curChar = ASCII_UCodeESC_CharStream.readChar();
			}
			catch (IOException _ex) {
				return j;
			}
		}
		while (true);
	}

	private static final void jjAddStates(int i, int j) {
		do jjstateSet[jjnewStateCnt++] = jjnextStates[i];
		while (i++ != j);
	}

	private static final void jjCheckNAddTwoStates(int i, int j) {
		jjCheckNAdd(i);
		jjCheckNAdd(j);
	}

	private static final int jjMoveStringLiteralDfa1_3(long l) {
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			return 1;
		}
		switch (curChar) {
			case 47 : // '/'
				if ((l & 4096L) != 0L)
					return jjStopAtPos(1, 12);
				else
					return 2;
		}
		return 2;
	}

	private static final int jjMoveStringLiteralDfa6_0(long l, long l1) {
		if ((l1 &= l) == 0L)
			return jjStartNfa_0(4, l, 0L);
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			jjStopStringLiteralDfa_0(5, l1, 0L);
			return 6;
		}
		switch (curChar) {
			default :
				break;

			case 105 : // 'i'
				return jjMoveStringLiteralDfa7_0(l1, 0x2000000L);

			case 108 : // 'l'
				return jjMoveStringLiteralDfa7_0(l1, 0x8000000L);

			case 110 : // 'n'
				if ((l1 & 0x80000000000000L) != 0L)
					return jjStartNfaWithStates_0(6, 55, 32);
				break;

			case 115 : // 's'
				return jjMoveStringLiteralDfa7_0(l1, 0x400000L);

			case 116 : // 't'
				if ((l1 & 0x800000L) != 0L)
					return jjStopAtPos(6, 23);
				break;
		}
		return jjStartNfa_0(5, l1, 0L);
	}

	private static final int jjMoveStringLiteralDfa2_0(long l, long l1) {
		if ((l1 &= l) == 0L)
			return jjStartNfa_0(0, l, 0L);
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			jjStopStringLiteralDfa_0(1, l1, 0L);
			return 2;
		}
		switch (curChar) {
			default :
				break;

			case 32 : // ' '
				return jjMoveStringLiteralDfa3_0(l1, 0xc00000L);

			case 99 : // 'c'
				return jjMoveStringLiteralDfa3_0(l1, 0x2000000L);

			case 100 : // 'd'
				if ((l1 & 0x1000000000000L) != 0L)
					return jjStartNfaWithStates_0(2, 48, 32);
				break;

			case 101 : // 'e'
				return jjMoveStringLiteralDfa3_0(l1, 0x10000L);

			case 105 : // 'i'
				return jjMoveStringLiteralDfa3_0(l1, 0x100000L);

			case 108 : // 'l'
				if ((l1 & 0x200000L) != 0L)
					return jjStartNfaWithStates_0(2, 21, 32);
				else
					return jjMoveStringLiteralDfa3_0(l1, 0xc024000L);

			case 116 : // 't'
				return jjMoveStringLiteralDfa3_0(l1, 0x80000001000000L);

			case 117 : // 'u'
				return jjMoveStringLiteralDfa3_0(l1, 0x40000L);

			case 121 : // 'y'
				if ((l1 & 0x80000L) != 0L)
					return jjStartNfaWithStates_0(2, 19, 32);
				break;
		}
		return jjStartNfa_0(1, l1, 0L);
	}

	private static final int jjMoveStringLiteralDfa0_1() {
		return jjMoveNfa_1(0, 0);
	}

	private static final boolean jjCanMove_1(int i, int j, int k, long l, long l1) {
		switch (i) {
			case 0 : // '\0'
				return (jjbitVec4[k] & l1) != 0L;

			case 48 : // '0'
				return (jjbitVec5[k] & l1) != 0L;

			case 49 : // '1'
				return (jjbitVec6[k] & l1) != 0L;

			case 51 : // '3'
				return (jjbitVec7[k] & l1) != 0L;

			case 61 : // '='
				return (jjbitVec8[k] & l1) != 0L;
		}
		return (jjbitVec3[j] & l) != 0L;
	}

	public RuleParserTokenManager(ASCII_UCodeESC_CharStream ascii_ucodeesc_charstream) {
		if (input_stream != null) {
			throw new TokenMgrError(
				"ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.",
				1);
		}
		else {
			input_stream = ascii_ucodeesc_charstream;
			return;
		}
	}

	public RuleParserTokenManager(ASCII_UCodeESC_CharStream ascii_ucodeesc_charstream, int i) {
		this(ascii_ucodeesc_charstream);
		SwitchTo(i);
	}

	public static void ReInit(ASCII_UCodeESC_CharStream ascii_ucodeesc_charstream) {
		jjmatchedPos = jjnewStateCnt = 0;
		curLexState = defaultLexState;
		input_stream = ascii_ucodeesc_charstream;
		ReInitRounds();
	}

	public static void ReInit(ASCII_UCodeESC_CharStream ascii_ucodeesc_charstream, int i) {
		ReInit(ascii_ucodeesc_charstream);
		SwitchTo(i);
	}

	private static final int jjStopStringLiteralDfa_0(int i, long l, long l1) {
		switch (i) {
			case 0 : // '\0'
				if ((l & 0x10000000000140L) != 0L)
					return 2;
				if ((l1 & 8L) != 0L)
					return 8;
				if ((l & 0xc180000fffc000L) != 0L) {
					jjmatchedKind = 56;
					return 32;
				}
				else {
					return -1;
				}

			case 1 : // '\001'
				if ((l & 256L) != 0L)
					return 0;
				if ((l & 0x8100000fff4000L) != 0L) {
					jjmatchedKind = 56;
					jjmatchedPos = 1;
					return 32;
				}
				return (l & 0x40800000008000L) == 0L ? -1 : 32;

			case 2 : // '\002'
				if ((l & 0x8000000f174000L) != 0L) {
					jjmatchedKind = 56;
					jjmatchedPos = 2;
					return 32;
				}
				if ((l & 0xc00000L) != 0L) {
					if (jjmatchedPos < 1) {
						jjmatchedKind = 56;
						jjmatchedPos = 1;
					}
					return -1;
				}
				return (l & 0x1000000280000L) == 0L ? -1 : 32;

			case 3 : // '\003'
				if ((l & 0xc00000L) != 0L) {
					if (jjmatchedPos < 1) {
						jjmatchedKind = 56;
						jjmatchedPos = 1;
					}
					return -1;
				}
				if ((l & 0x8000000e104000L) != 0L) {
					jjmatchedKind = 56;
					jjmatchedPos = 3;
					return 32;
				}
				return (l & 0x1070000L) == 0L ? -1 : 32;

			case 4 : // '\004'
				if ((l & 0xc00000L) != 0L) {
					if (jjmatchedPos < 1) {
						jjmatchedKind = 56;
						jjmatchedPos = 1;
					}
					return -1;
				}
				if ((l & 0x8000000e100000L) != 0L) {
					jjmatchedKind = 56;
					jjmatchedPos = 4;
					return 32;
				}
				return (l & 16384L) == 0L ? -1 : 32;

			case 5 : // '\005'
				if ((l & 0xc00000L) != 0L) {
					if (jjmatchedPos < 1) {
						jjmatchedKind = 56;
						jjmatchedPos = 1;
					}
					return -1;
				}
				if ((l & 0x8000000a000000L) != 0L) {
					jjmatchedKind = 56;
					jjmatchedPos = 5;
					return 32;
				}
				return (l & 0x4100000L) == 0L ? -1 : 32;

			case 6 : // '\006'
				if ((l & 0xc00000L) != 0L) {
					if (jjmatchedPos < 1) {
						jjmatchedKind = 56;
						jjmatchedPos = 1;
					}
					return -1;
				}
				if ((l & 0xa000000L) != 0L) {
					jjmatchedKind = 56;
					jjmatchedPos = 6;
					return 32;
				}
				return (l & 0x80000000000000L) == 0L ? -1 : 32;

			case 7 : // '\007'
				if ((l & 0x400000L) != 0L) {
					if (jjmatchedPos < 1) {
						jjmatchedKind = 56;
						jjmatchedPos = 1;
					}
					return -1;
				}
				if ((l & 0xa000000L) != 0L) {
					jjmatchedKind = 56;
					jjmatchedPos = 7;
					return 32;
				}
				else {
					return -1;
				}
		}
		return -1;
	}

	private static final int jjStartNfa_0(int i, long l, long l1) {
		return jjMoveNfa_0(jjStopStringLiteralDfa_0(i, l, l1), i + 1);
	}

	private static final int jjMoveStringLiteralDfa4_0(long l, long l1) {
		if ((l1 &= l) == 0L)
			return jjStartNfa_0(2, l, 0L);
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			jjStopStringLiteralDfa_0(3, l1, 0L);
			return 4;
		}
		switch (curChar) {
			case 86 : // 'V'
				return jjMoveStringLiteralDfa5_0(l1, 0x8000000L);

			case 101 : // 'e'
				if ((l1 & 16384L) != 0L)
					return jjStartNfaWithStates_0(4, 14, 32);
				else
					return jjMoveStringLiteralDfa5_0(l1, 0x80000000400000L);

			case 109 : // 'm'
				return jjMoveStringLiteralDfa5_0(l1, 0x4000000L);

			case 111 : // 'o'
				return jjMoveStringLiteralDfa5_0(l1, 0x800000L);

			case 116 : // 't'
				return jjMoveStringLiteralDfa5_0(l1, 0x100000L);

			case 117 : // 'u'
				return jjMoveStringLiteralDfa5_0(l1, 0x2000000L);
		}
		return jjStartNfa_0(3, l1, 0L);
	}

	private static final int jjMoveNfa_0(int i, int j) {
		int k = 0;
		jjnewStateCnt = 52;
		int l = 1;
		jjstateSet[0] = i;
		int i1 = 0x7fffffff;
		do {
			if (++jjround == 0x7fffffff)
				ReInitRounds();
			if (curChar < '@') {
				long l1 = 1L << curChar;
				do switch (jjstateSet[--l]) {
						case 3 : // '\003'
							if ((0x3ff000000000000L & l1) != 0L)
								jjCheckNAddStates(0, 6);
							else if (curChar == '$') {
								if (i1 > 56)
									i1 = 56;
								jjCheckNAdd(32);
							}
							else if (curChar == '"')
								jjCheckNAddStates(7, 9);
							else if (curChar == '\'')
								jjAddStates(10, 11);
							else if (curChar == '.')
								jjCheckNAdd(8);
							else if (curChar == '/')
								jjstateSet[jjnewStateCnt++] = 2;
							if ((0x3fe000000000000L & l1) != 0L) {
								if (i1 > 28)
									i1 = 28;
								jjCheckNAddTwoStates(5, 6);
							}
							else if (curChar == '0') {
								if (i1 > 28)
									i1 = 28;
								jjCheckNAddStates(12, 14);
							}
							break;

						case 0 : // '\0'
							if (curChar == '*')
								jjstateSet[jjnewStateCnt++] = 1;
							break;

						case 1 : // '\001'
							if ((0xffff7fffffffffffL & l1) != 0L && i1 > 7)
								i1 = 7;
							break;

						case 2 : // '\002'
							if (curChar == '*')
								jjstateSet[jjnewStateCnt++] = 0;
							break;

						case 4 : // '\004'
							if ((0x3fe000000000000L & l1) != 0L) {
								if (i1 > 28)
									i1 = 28;
								jjCheckNAddTwoStates(5, 6);
							}
							break;

						case 5 : // '\005'
							if ((0x3ff000000000000L & l1) != 0L) {
								if (i1 > 28)
									i1 = 28;
								jjCheckNAddTwoStates(5, 6);
							}
							break;

						case 7 : // '\007'
							if (curChar == '.')
								jjCheckNAdd(8);
							break;

						case 8 : // '\b'
							if ((0x3ff000000000000L & l1) != 0L) {
								if (i1 > 32)
									i1 = 32;
								jjCheckNAddStates(15, 17);
							}
							break;

						case 10 : // '\n'
							if ((0x280000000000L & l1) != 0L)
								jjCheckNAdd(11);
							break;

						case 11 : // '\013'
							if ((0x3ff000000000000L & l1) != 0L) {
								if (i1 > 32)
									i1 = 32;
								jjCheckNAddTwoStates(11, 12);
							}
							break;

						case 13 : // '\r'
							if (curChar == '\'')
								jjAddStates(10, 11);
							break;

						case 14 : // '\016'
							if ((0xffffff7fffffdbffL & l1) != 0L)
								jjCheckNAdd(15);
							break;

						case 15 : // '\017'
							if (curChar == '\'' && i1 > 34)
								i1 = 34;
							break;

						case 17 : // '\021'
							if ((0x8400000000L & l1) != 0L)
								jjCheckNAdd(15);
							break;

						case 18 : // '\022'
							if ((0xff000000000000L & l1) != 0L)
								jjCheckNAddTwoStates(19, 15);
							break;

						case 19 : // '\023'
							if ((0xff000000000000L & l1) != 0L)
								jjCheckNAdd(15);
							break;

						case 20 : // '\024'
							if ((0xf000000000000L & l1) != 0L)
								jjstateSet[jjnewStateCnt++] = 21;
							break;

						case 21 : // '\025'
							if ((0xff000000000000L & l1) != 0L)
								jjCheckNAdd(19);
							break;

						case 22 : // '\026'
							if (curChar == '"')
								jjCheckNAddStates(7, 9);
							break;

						case 23 : // '\027'
							if ((0xfffffffbffffdbffL & l1) != 0L)
								jjCheckNAddStates(7, 9);
							break;

						case 25 : // '\031'
							if ((0x8400000000L & l1) != 0L)
								jjCheckNAddStates(7, 9);
							break;

						case 26 : // '\032'
							if (curChar == '"' && i1 > 35)
								i1 = 35;
							break;

						case 27 : // '\033'
							if ((0xff000000000000L & l1) != 0L)
								jjCheckNAddStates(18, 21);
							break;

						case 28 : // '\034'
							if ((0xff000000000000L & l1) != 0L)
								jjCheckNAddStates(7, 9);
							break;

						case 29 : // '\035'
							if ((0xf000000000000L & l1) != 0L)
								jjstateSet[jjnewStateCnt++] = 30;
							break;

						case 30 : // '\036'
							if ((0xff000000000000L & l1) != 0L)
								jjCheckNAdd(28);
							break;

						case 31 : // '\037'

							if (curChar == '$') {
								if (i1 > 56)
									i1 = 56;
								jjCheckNAdd(32);
							}
							break;

						case 32 : // ' '
							if ((0x3ff001000000000L & l1) != 0L) {
								if (i1 > 56)
									i1 = 56;
								jjCheckNAdd(32);
							}
							break;

						case 33 : // '!'
							if ((0x3ff000000000000L & l1) != 0L)
								jjCheckNAddStates(0, 6);
							break;

						case 34 : // '"'
							if ((0x3ff000000000000L & l1) != 0L)
								jjCheckNAddTwoStates(34, 35);
							break;

						case 35 : // '#'
							if (curChar == '.') {
								if (i1 > 32)
									i1 = 32;
								jjCheckNAddStates(22, 24);
							}
							break;

						case 36 : // '$'
							if ((0x3ff000000000000L & l1) != 0L) {
								if (i1 > 32)
									i1 = 32;
								jjCheckNAddStates(22, 24);
							}
							break;

						case 38 : // '&'
							if ((0x280000000000L & l1) != 0L)
								jjCheckNAdd(39);
							break;

						case 39 : // '\''
							if ((0x3ff000000000000L & l1) != 0L) {
								if (i1 > 32)
									i1 = 32;
								jjCheckNAddTwoStates(39, 12);
							}
							break;

						case 40 : // '('
							if ((0x3ff000000000000L & l1) != 0L)
								jjCheckNAddTwoStates(40, 41);
							break;

						case 42 : // '*'
							if ((0x280000000000L & l1) != 0L)
								jjCheckNAdd(43);
							break;

						case 43 : // '+'
							if ((0x3ff000000000000L & l1) != 0L) {
								if (i1 > 32)
									i1 = 32;
								jjCheckNAddTwoStates(43, 12);
							}
							break;

						case 44 : // ','
							if ((0x3ff000000000000L & l1) != 0L)
								jjCheckNAddStates(25, 27);
							break;

						case 46 : // '.'
							if ((0x280000000000L & l1) != 0L)
								jjCheckNAdd(47);
							break;

						case 47 : // '/'
							if ((0x3ff000000000000L & l1) != 0L)
								jjCheckNAddTwoStates(47, 12);
							break;

						case 48 : // '0'
							if (curChar == '0') {
								if (i1 > 28)
									i1 = 28;
								jjCheckNAddStates(12, 14);
							}
							break;

						case 50 : // '2'
							if ((0x3ff000000000000L & l1) != 0L) {
								if (i1 > 28)
									i1 = 28;
								jjCheckNAddTwoStates(50, 6);
							}
							break;

						case 51 : // '3'
							if ((0xff000000000000L & l1) != 0L) {
								if (i1 > 28)
									i1 = 28;
								jjCheckNAddTwoStates(51, 6);
							}
							break;
					}
						while (l != k);
			}
			else if (curChar < '\200') {
				long l2 = 1L << (curChar & 0x3f);
				do switch (jjstateSet[--l]) {
						case 3 : // '\003'
						case 32 : // ' '
							if ((0x7fffffe87fffffeL & l2) != 0L) {
								if (i1 > 56)
									i1 = 56;
								jjCheckNAdd(32);
							}
							break;

						case 1 : // '\001'
							if (i1 > 7)
								i1 = 7;
							break;

						case 6 : // '\006'
							if ((0x100000001000L & l2) != 0L && i1 > 28)
								i1 = 28;
							break;

						case 9 : // '\t'
							if ((0x2000000020L & l2) != 0L)
								jjAddStates(28, 29);
							break;

						case 12 : // '\f'
							if ((0x5000000050L & l2) != 0L && i1 > 32)
								i1 = 32;
							break;

						case 14 : // '\016'
							if ((0xffffffffefffffffL & l2) != 0L)
								jjCheckNAdd(15);
							break;

						case 16 : // '\020'
							if (curChar == '\\')
								jjAddStates(30, 32);
							break;

						case 17 : // '\021'
							if ((0x14404410000000L & l2) != 0L)
								jjCheckNAdd(15);
							break;

						case 23 : // '\027'
							if ((0xffffffffefffffffL & l2) != 0L)
								jjCheckNAddStates(7, 9);
							break;

						case 24 : // '\030'
							if (curChar == '\\')
								jjAddStates(33, 35);
							break;

						case 25 : // '\031'
							if ((0x14404410000000L & l2) != 0L)
								jjCheckNAddStates(7, 9);
							break;

						case 37 : // '%'
							if ((0x2000000020L & l2) != 0L)
								jjAddStates(36, 37);
							break;

						case 41 : // ')'
							if ((0x2000000020L & l2) != 0L)
								jjAddStates(38, 39);
							break;

						case 45 : // '-'
							if ((0x2000000020L & l2) != 0L)
								jjAddStates(40, 41);
							break;

						case 49 : // '1'
							if ((0x100000001000000L & l2) != 0L)
								jjCheckNAdd(50);
							break;

						case 50 : // '2'
							if ((0x7e0000007eL & l2) != 0L) {
								if (i1 > 28)
									i1 = 28;
								jjCheckNAddTwoStates(50, 6);
							}
							break;
					}
						while (l != k);
			}
			else {
				int j1 = curChar >> 8;
				int k1 = j1 >> 6;
				long l3 = 1L << (j1 & 0x3f);
				int i2 = (curChar & 0xff) >> 6;
				long l4 = 1L << (curChar & 0x3f);
				do switch (jjstateSet[--l]) {
						case 3 : // '\003'
						case 32 : // ' '
							if (jjCanMove_1(j1, k1, i2, l3, l4)) {
								if (i1 > 56)
									i1 = 56;
								jjCheckNAdd(32);
							}
							break;

						case 1 : // '\001'
							if (jjCanMove_0(j1, k1, i2, l3, l4) && i1 > 7)
								i1 = 7;
							break;

						case 14 : // '\016'
							if (jjCanMove_0(j1, k1, i2, l3, l4))
								jjstateSet[jjnewStateCnt++] = 15;
							break;

						case 23 : // '\027'
							if (jjCanMove_0(j1, k1, i2, l3, l4))
								jjAddStates(7, 9);
							break;
					}
						while (l != k);
			}
			if (i1 != 0x7fffffff) {
				jjmatchedKind = i1;
				jjmatchedPos = j;
				i1 = 0x7fffffff;
			}
			j++;
			if ((l = jjnewStateCnt) == (k = 52 - (jjnewStateCnt = k)))
				return j;
			try {
				curChar = ASCII_UCodeESC_CharStream.readChar();
			}
			catch (IOException _ex) {
				return j;
			}
		}
		while (true);
	}

	private static final int jjStopAtPos(int i, int j) {
		jjmatchedKind = j;
		jjmatchedPos = i;
		return i + 1;
	}

	private static final void jjCheckNAddStates(int i, int j) {
		do jjCheckNAdd(jjnextStates[i]);
		while (i++ != j);
	}

	private static final void jjCheckNAddStates(int i) {
		jjCheckNAdd(jjnextStates[i]);
		jjCheckNAdd(jjnextStates[i + 1]);
	}

	static final void SkipLexicalActions(Token token) {
		switch (jjmatchedKind) {
			case 0 : // '\0'
			default :
				return;
		}
	}

	static final void MoreLexicalActions() {
		jjimageLen += lengthOfMatch = jjmatchedPos + 1;
		switch (jjmatchedKind) {
			case 7 : // '\007'
				if (image == null)
					image = new StringBuffer(new String(ASCII_UCodeESC_CharStream.GetSuffix(jjimageLen)));
				else
					image.append(ASCII_UCodeESC_CharStream.GetSuffix(jjimageLen));
				jjimageLen = 0;
				ASCII_UCodeESC_CharStream.backup(1);
				break;
		}
	}

	public static final Token getNextToken() {
		Token token = null;
		int i = 0;
		label0 : do {
			try {
				curChar = ASCII_UCodeESC_CharStream.BeginToken();
			}
			catch (IOException _ex) {
				jjmatchedKind = 0;
				Token token1 = jjFillToken();
				token1.specialToken = token;
				return token1;
			}
			image = null;
			jjimageLen = 0;
			do {
				switch (curLexState) {
					default :
						break;

					case 0 : // '\0'
						try {
							ASCII_UCodeESC_CharStream.backup(0);
							for (;
								curChar <= ' ' && (0x100003600L & 1L << curChar) != 0L;
								curChar = ASCII_UCodeESC_CharStream.BeginToken());
						}
						catch (IOException _ex) {
							continue label0;
						}
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						i = jjMoveStringLiteralDfa0_0();
						break;

					case 1 : // '\001'
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						i = jjMoveStringLiteralDfa0_1();
						if (jjmatchedPos == 0 && jjmatchedKind > 10)
							jjmatchedKind = 10;
						break;

					case 2 : // '\002'
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						i = jjMoveStringLiteralDfa0_2();
						if (jjmatchedPos == 0 && jjmatchedKind > 13)
							jjmatchedKind = 13;
						break;

					case 3 : // '\003'
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						i = jjMoveStringLiteralDfa0_3();
						if (jjmatchedPos == 0 && jjmatchedKind > 13)
							jjmatchedKind = 13;
						break;
				}
				if (jjmatchedKind == 0x7fffffff)
					break;
				if (jjmatchedPos + 1 < i)
					ASCII_UCodeESC_CharStream.backup(i - jjmatchedPos - 1);
				if ((jjtoToken[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 0x3f)) != 0L) {
					Token token2 = jjFillToken();
					token2.specialToken = token;
					if (jjnewLexState[jjmatchedKind] != -1)
						curLexState = jjnewLexState[jjmatchedKind];
					return token2;
				}
				if ((jjtoSkip[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 0x3f)) != 0L) {
					if ((jjtoSpecial[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 0x3f)) != 0L) {
						Token token3 = jjFillToken();
						if (token == null) {
							token = token3;
						}
						else {
							token3.specialToken = token;
							token = token.next = token3;
						}
						SkipLexicalActions(token3);
					}
					else {
						SkipLexicalActions(null);
					}
					if (jjnewLexState[jjmatchedKind] != -1)
						curLexState = jjnewLexState[jjmatchedKind];
				}
				else {
					MoreLexicalActions();
					if (jjnewLexState[jjmatchedKind] != -1)
						curLexState = jjnewLexState[jjmatchedKind];
					i = 0;
					jjmatchedKind = 0x7fffffff;
					try {
						curChar = ASCII_UCodeESC_CharStream.readChar();
						continue;
					}
					catch (IOException _ex) {
					}
					break;
				}
				continue label0;
			}
			while (true);
			int j = ASCII_UCodeESC_CharStream.getEndLine();
			int k = ASCII_UCodeESC_CharStream.getEndColumn();
			String s = null;
			boolean flag = false;
			try {
				ASCII_UCodeESC_CharStream.readChar();
				ASCII_UCodeESC_CharStream.backup(1);
			}
			catch (IOException _ex) {
				flag = true;
				s = i > 1 ? ASCII_UCodeESC_CharStream.GetImage() : "";
				if (curChar == '\n' || curChar == '\r') {
					j++;
					k = 0;
				}
				else {
					k++;
				}
			}
			if (!flag) {
				ASCII_UCodeESC_CharStream.backup(1);
				s = i > 1 ? ASCII_UCodeESC_CharStream.GetImage() : "";
			}
			throw new TokenMgrError(flag, curLexState, j, k, s, curChar, 0);
		}
		while (true);
	}

	private static final void jjCheckNAdd(int i) {
		if (jjrounds[i] != jjround) {
			jjstateSet[jjnewStateCnt++] = i;
			jjrounds[i] = jjround;
		}
	}

	private static final int jjMoveStringLiteralDfa0_2() {
		switch (curChar) {
			case 42 : // '*'
				return jjMoveStringLiteralDfa1_2(2048L);
		}
		return 1;
	}

	private static final int jjMoveStringLiteralDfa3_0(long l, long l1) {
		if ((l1 &= l) == 0L)
			return jjStartNfa_0(1, l, 0L);
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			jjStopStringLiteralDfa_0(2, l1, 0L);
			return 3;
		}
		switch (curChar) {
			default :
				break;

			case 101 : // 'e'
				if ((l1 & 0x40000L) != 0L)
					return jjStartNfaWithStates_0(3, 18, 32);
				break;

			case 104 : // 'h'
				if ((l1 & 0x1000000L) != 0L)
					return jjStartNfaWithStates_0(3, 24, 32);
				break;

			case 108 : // 'l'
				if ((l1 & 0x20000L) != 0L)
					return jjStartNfaWithStates_0(3, 17, 32);
				else
					return jjMoveStringLiteralDfa4_0(l1, 0xa400000L);

			case 109 : // 'm'
				return jjMoveStringLiteralDfa4_0(l1, 0x800000L);

			case 110 : // 'n'
				if ((l1 & 0x10000L) != 0L)
					return jjStartNfaWithStates_0(3, 16, 32);
				break;

			case 115 : // 's'
				return jjMoveStringLiteralDfa4_0(l1, 0x104000L);

			case 117 : // 'u'
				return jjMoveStringLiteralDfa4_0(l1, 0x4000000L);

			case 119 : // 'w'
				return jjMoveStringLiteralDfa4_0(l1, 0x80000000000000L);
		}
		return jjStartNfa_0(2, l1, 0L);
	}

	private static final int jjMoveStringLiteralDfa5_0(long l, long l1) {
		if ((l1 &= l) == 0L)
			return jjStartNfa_0(3, l, 0L);
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			jjStopStringLiteralDfa_0(4, l1, 0L);
			return 5;
		}
		switch (curChar) {
			default :
				break;

			case 97 : // 'a'
				return jjMoveStringLiteralDfa6_0(l1, 0x8400000L);

			case 100 : // 'd'
				return jjMoveStringLiteralDfa6_0(l1, 0x2000000L);

			case 101 : // 'e'
				return jjMoveStringLiteralDfa6_0(l1, 0x80000000000000L);

			case 110 : // 'n'
				if ((l1 & 0x4000000L) != 0L)
					return jjStartNfaWithStates_0(5, 26, 32);
				break;

			case 115 : // 's'
				if ((l1 & 0x100000L) != 0L)
					return jjStartNfaWithStates_0(5, 20, 32);
				else
					return jjMoveStringLiteralDfa6_0(l1, 0x800000L);
		}
		return jjStartNfa_0(4, l1, 0L);
	}

	private static final int jjMoveStringLiteralDfa7_0(long l, long l1) {
		if ((l1 &= l) == 0L)
			return jjStartNfa_0(5, l, 0L);
		try {
			curChar = ASCII_UCodeESC_CharStream.readChar();
		}
		catch (IOException _ex) {
			jjStopStringLiteralDfa_0(6, l1, 0L);
			return 7;
		}
		switch (curChar) {
			case 110 : // 'n'
				return jjMoveStringLiteralDfa8_0(l1, 0x2000000L);

			case 116 : // 't'
				if ((l1 & 0x400000L) != 0L)
					return jjStopAtPos(7, 22);
				break;

			case 117 : // 'u'
				return jjMoveStringLiteralDfa8_0(l1, 0x8000000L);
		}
		return jjStartNfa_0(6, l1, 0L);
	}

	static final long jjbitVec0[] = { -2L, -1L, -1L, -1L };
	static final long jjbitVec2[] = { 0, 0, -1L, -1L };
	static final long jjbitVec3[] = { 0x1ff00000fffffffeL, -16384L, 0xffffffffL, 0x600000000000000L };
	static final long jjbitVec4[] = { 0, 0, 0, 0xff7fffffff7fffffL };
	static final long jjbitVec5[] = { 0, -1L, -1L, -1L };
	static final long jjbitVec6[] = { -1L, -1L, 65535L, 0 };
	static final long jjbitVec7[] = { -1L, -1L, 0, 0 };
	static final long jjbitVec8[] = { 0x3fffffffffffL, 0, 0, 0 };
	static final int jjnextStates[] =
		{
			34,
			35,
			40,
			41,
			44,
			45,
			12,
			23,
			24,
			26,
			14,
			16,
			49,
			51,
			6,
			8,
			9,
			12,
			23,
			24,
			28,
			26,
			36,
			37,
			12,
			44,
			45,
			12,
			10,
			11,
			17,
			18,
			20,
			25,
			27,
			29,
			38,
			39,
			42,
			43,
			46,
			47 };
	public static final String jjstrLiteralImages[] =
		{
			"",
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			"false",
			"if",
			"then",
			"null",
			"true",
			"any",
			"exists",
			"all",
			"at least",
			"at most",
			"with",
			"excluding",
			"column",
			"cellValue",
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			"=",
			">",
			"<",
			"!",
			"~",
			"?",
			":",
			"==",
			"<=",
			">=",
			"!=",
			"or",
			"and",
			"+",
			"-",
			"*",
			"/",
			"%",
			"in",
			"between",
			null,
			null,
			null,
			"(",
			")",
			"{",
			"}",
			"[",
			"]",
			";",
			",",
			".",
			"ruleName",
			"for key" };
	public static final String lexStateNames[] =
		{ "DEFAULT", "IN_SINGLE_LINE_COMMENT", "IN_FORMAL_COMMENT", "IN_MULTI_LINE_COMMENT" };
	public static final int jjnewLexState[] =
		{
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			1,
			2,
			3,
			0,
			-1,
			0,
			0,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1 };
	static final long jjtoToken[] = { 0xf9fffffd1fffc001L, 15L };
	static final long jjtoSkip[] = { 6718L, 0 };
	static final long jjtoSpecial[] = { 6656L, 0 };
	static final long jjtoMore[] = { 9664L, 0 };
	private static ASCII_UCodeESC_CharStream input_stream;
	private static final int jjrounds[] = new int[52];
	private static final int jjstateSet[] = new int[104];
	static StringBuffer image;
	static int jjimageLen;
	static int lengthOfMatch;
	protected static char curChar;
	static int curLexState = 0;
	static int defaultLexState = 0;
	static int jjnewStateCnt;
	static int jjround;
	static int jjmatchedPos;
	static int jjmatchedKind;

}
