package com.mindbox.server.parser.jtb.rule;

// Referenced classes of package com.mindbox.server.parser.jtb.rule:
//            Token

public class ParseException extends Exception {

	public ParseException(Token token, int ai[][], String as[]) {
		super("");
		eol = System.getProperty("line.separator", "\n");
		specialConstructor = true;
		currentToken = token;
		expectedTokenSequences = ai;
		tokenImage = as;
	}

	public ParseException() {
		eol = System.getProperty("line.separator", "\n");
		specialConstructor = false;
	}

	public ParseException(String s) {
		super(s);
		eol = System.getProperty("line.separator", "\n");
		specialConstructor = false;
	}

	public String getMessage() {
		if (!specialConstructor)
			return super.getMessage();
		String s = "";
		int i = 0;
		for (int j = 0; j < expectedTokenSequences.length; j++) {
			if (i < expectedTokenSequences[j].length)
				i = expectedTokenSequences[j].length;
			for (int k = 0; k < expectedTokenSequences[j].length; k++)
				s += tokenImage[expectedTokenSequences[j][k]] + " ";

			if (expectedTokenSequences[j][expectedTokenSequences[j].length - 1] != 0)
				s += "...";
			s += eol + "    ";
		}

		String s1 = "Encountered \"";
		Token token = currentToken.next;
		for (int l = 0; l < i; l++) {
			if (l != 0)
				s1 += " ";
			if (token.kind == 0) {
				s1 += tokenImage[0];
				break;
			}
			s1 += add_escapes(token.image);
			token = token.next;
		}

		s1 += "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn + "." + eol;
		if (expectedTokenSequences.length == 1)
			s1 += "Was expecting:" + eol + "    ";
		else
			s1 += "Was expecting one of:" + eol + "    ";
		s1 += s;
		return s1;
	}

	protected String add_escapes(String s) {
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c;
			switch (s.charAt(i)) {
				case 0 : // '\0'
					break;

				case 8 : // '\b'
					stringbuffer.append("\\b");
					break;

				case 9 : // '\t'
					stringbuffer.append("\\t");
					break;

				case 10 : // '\n'
					stringbuffer.append("\\n");
					break;

				case 12 : // '\f'
					stringbuffer.append("\\f");
					break;

				case 13 : // '\r'
					stringbuffer.append("\\r");
					break;

				case 34 : // '"'
					stringbuffer.append("\\\"");
					break;

				case 39 : // '\''
					stringbuffer.append("\\'");
					break;

				case 92 : // '\\'
					stringbuffer.append("\\\\");
					break;

				default :
					if ((c = s.charAt(i)) < ' ' || c > '~') {
						String s1 = "0000" + Integer.toString(c, 16);
						stringbuffer.append("\\u" + s1.substring(s1.length() - 4, s1.length()));
					}
					else {
						stringbuffer.append(c);
					}
					break;
			}
		}

		return stringbuffer.toString();
	}

	protected boolean specialConstructor;
	public Token currentToken;
	public int expectedTokenSequences[][];
	public String tokenImage[];
	protected String eol;
}