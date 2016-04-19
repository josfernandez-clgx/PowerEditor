package com.mindbox.server.parser.jtb.rule;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import com.mindbox.server.parser.jtb.rule.syntaxtree.CellValue;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ColumnLiteral;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Message;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Name;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeChoice;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeListOptional;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeSequence;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Word;

// Referenced classes of package com.mindbox.server.parser.jtb.rule:
//            ParseException, ASCII_UCodeESC_CharStream, MessageParserTokenManager, Token,
//            MessageParserConstants, JTBToolkit

public class MessageParser implements MessageParserConstants {
	static final class JJCalls {

		int gen;
		Token first;
		int arg;
		JJCalls next;

		JJCalls() {
		}
	}

	public static synchronized MessageParser getInstance(Reader reader) {
		if (mSingleton == null)
			mSingleton = new MessageParser(reader);
		else
			ReInit(reader);
		return mSingleton;
	}

	/*
	public static void main(String args[]) {
		if (args.length == 0) {
			System.out.println("MessageParser:  Reading from standard input . . .");
			MessageParser messageparser = new MessageParser(System.in);
			enable_tracing();
		}
		else if (args.length == 1) {
			System.out.println("MessageParser:  Reading from file " + args[0] + " . . .");
			MessageParser messageparser1;
			try {
				messageparser1 = new MessageParser(new FileInputStream(args[0]));
			}
			catch (FileNotFoundException filenotfoundexception) {
				System.out.println("MessageParser:  File " + args[0] + " not found.");
				return;
			}
		}
		else {
			System.out.println("MessageParser:  Usage is one of:");
			System.out.println("         java MessageParser < inputfile");
			System.out.println("OR");
			System.out.println("         java MessageParser inputfile");
			return;
		}
		try {
			Message message = Message();
			System.err.println("Sapphire Messages parsed successfully.");
			message.accept(new TreeFormatter());
			message.accept(new TreeDumper());
			System.out.println("Message Parser: Deployment rule(s) parsed successfully");
		}
		catch (ParseException parseexception) {
			System.out.println("MessageParser:  Encountered errors during parse.");
			parseexception.printStackTrace();
		}
	}*/

	public static final ColumnLiteral ColumnLiteral() throws ParseException {
		Token token1 = jj_consume_token(33);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		Token token2 = jj_consume_token(14);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
		Token token3 = jj_consume_token(16);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
		Token token4 = jj_consume_token(33);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken3 = JTBToolkit.makeNodeToken(token4);
		return new ColumnLiteral(nodetoken, nodetoken1, nodetoken2, nodetoken3);
	}

	public static final CellValue CellValue() throws ParseException {
		Token token1 = jj_consume_token(33);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		Token token2 = jj_consume_token(15);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
		Token token3 = jj_consume_token(33);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
		return new CellValue(nodetoken, nodetoken1, nodetoken2);
	}

	public static final Name Name() throws ParseException {
		NodeListOptional nodelist = new NodeListOptional();
		Token token1 = jj_consume_token(24);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		label0 : do {
			NodeSequence nodesequence = new NodeSequence(2);
			Token token2 = jj_consume_token(34);
			com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
			nodesequence.addNode(nodetoken1);
			Token token3 = jj_consume_token(24);
			com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
			nodesequence.addNode(nodetoken2);
			nodelist.addNode(nodesequence);
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				case 34 : // '"'
					break;

				default :
					jj_la1[0] = jj_gen;
					break label0;
			}
		}
		while (true);
		//        nodelist.nodes.trimToSize();
		return new Name(nodetoken, nodelist);
	}

	public static final Message Message() throws ParseException {
		NodeList nodelist = new NodeList();
		label0 : do {
			Word word = Word();
			nodelist.addNode(word);
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				case 24 : // '\030'
				case 33 : // '!'
					break;

				default :
					jj_la1[1] = jj_gen;
					break label0;
			}
		}
		while (true);
		//        nodelist.nodes.trimToSize();
		Token token1 = jj_consume_token(0);
		token1.beginColumn++;
		token1.endColumn++;
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		return new Message(nodelist, nodetoken);
	}

	public static final Word Word() throws ParseException {
		NodeChoice nodechoice;
		if (jj_2_1(2)) {
			ColumnLiteral columnliteral = ColumnLiteral();
			nodechoice = new NodeChoice(columnliteral, 0);
		}
		else {
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				case 33 : // '!'
					NodeSequence nodesequence = new NodeSequence(3);
					Token token1 = jj_consume_token(33);
					com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
					nodesequence.addNode(nodetoken);
					Name name = Name();
					nodesequence.addNode(name);
					Token token2 = jj_consume_token(33);
					com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
					nodesequence.addNode(nodetoken1);
					nodechoice = new NodeChoice(nodesequence, 1);
					break;

				case 24 : // '\030'
					Token token3 = jj_consume_token(24);
					com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
					nodechoice = new NodeChoice(nodetoken2, 2);
					break;

				default :
					jj_la1[2] = jj_gen;
					jj_consume_token(-1);
					throw new ParseException();
			}
		}
		return new Word(nodechoice);
	}

	private static final boolean jj_2_1(int i) {
		jj_la = i;
		jj_lastpos = jj_scanpos = token;
		boolean flag = !jj_3_1();
		jj_save(0, i);
		return flag;
	}

	private static final boolean jj_3_1() {
		if (jj_3R_3())
			return true;
		return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
	}

	private static final boolean jj_3R_3() {
		if (jj_scan_token(33))
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			return false;
		if (jj_scan_token(14))
			return true;
		return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
	}

	public MessageParser(InputStream inputstream) {
		if (jj_initialized_once) {
			System.out.println("ERROR: Second call to constructor of static parser.  You must");
			System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
			System.out.println("       during parser generation.");
			throw new Error();
		}
		jj_initialized_once = true;
		jj_input_stream = new ASCII_UCodeESC_CharStream(inputstream, 1, 1);
		token_source = new MessageParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 3; i++)
			jj_la1[i] = -1;

		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public static void ReInit(InputStream inputstream) {
		jj_input_stream.ReInit(inputstream, 1, 1);
		MessageParserTokenManager.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 3; i++)
			jj_la1[i] = -1;

		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public MessageParser(Reader reader) {
		if (jj_initialized_once) {
			System.out.println("ERROR: Second call to constructor of static parser.  You must");
			System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
			System.out.println("       during parser generation.");
			throw new Error();
		}
		jj_initialized_once = true;
		jj_input_stream = new ASCII_UCodeESC_CharStream(reader, 1, 1);
		token_source = new MessageParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 3; i++)
			jj_la1[i] = -1;

		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public static void ReInit(Reader reader) {
		jj_input_stream.ReInit(reader, 1, 1);
		MessageParserTokenManager.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 3; i++)
			jj_la1[i] = -1;

		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public MessageParser(MessageParserTokenManager messageparsertokenmanager) {
		if (jj_initialized_once) {
			System.out.println("ERROR: Second call to constructor of static parser.  You must");
			System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
			System.out.println("       during parser generation.");
			throw new Error();
		}
		jj_initialized_once = true;
		token_source = messageparsertokenmanager;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 3; i++)
			jj_la1[i] = -1;

		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public void ReInit(MessageParserTokenManager messageparsertokenmanager) {
		token_source = messageparsertokenmanager;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 3; i++)
			jj_la1[i] = -1;

		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	private static final Token jj_consume_token(int i) throws ParseException {
		Token token1;
		if ((token1 = token).next != null)
			token = token.next;
		else
			token = token.next = MessageParserTokenManager.getNextToken();
		jj_ntk = -1;
		if (token.kind == i) {
			jj_gen++;
			if (++jj_gc > 100) {
				jj_gc = 0;
				for (int j = 0; j < jj_2_rtns.length; j++) {
					for (JJCalls jjcalls = jj_2_rtns[j]; jjcalls != null; jjcalls = jjcalls.next)
						if (jjcalls.gen < jj_gen)
							jjcalls.first = null;

				}

			}
			return token;
		}
		else {
			token = token1;
			jj_kind = i;
			throw generateParseException();
		}
	}

	private static final boolean jj_scan_token(int i) {
		if (jj_scanpos == jj_lastpos) {
			jj_la--;
			if (jj_scanpos.next == null)
				jj_lastpos = jj_scanpos = jj_scanpos.next = MessageParserTokenManager.getNextToken();
			else
				jj_lastpos = jj_scanpos = jj_scanpos.next;
		}
		else {
			jj_scanpos = jj_scanpos.next;
		}
		if (jj_rescan) {
			int j = 0;
			Token token1;
			for (token1 = token; token1 != null && token1 != jj_scanpos; token1 = token1.next)
				j++;

			if (token1 != null)
				jj_add_error_token(i, j);
		}
		return jj_scanpos.kind != i;
	}

	public static final Token getNextToken() {
		if (token.next != null)
			token = token.next;
		else
			token = token.next = MessageParserTokenManager.getNextToken();
		jj_ntk = -1;
		jj_gen++;
		return token;
	}

	public static final Token getToken(int i) {
		Token token1 = lookingAhead ? jj_scanpos : token;
		for (int j = 0; j < i; j++)
			if (token1.next != null)
				token1 = token1.next;
			else
				token1 = token1.next = MessageParserTokenManager.getNextToken();

		return token1;
	}

	private static final int jj_ntk() {
		if ((jj_nt = token.next) == null)
			return jj_ntk = (token.next = MessageParserTokenManager.getNextToken()).kind;
		else
			return jj_ntk = jj_nt.kind;
	}

	private static void jj_add_error_token(int i, int j) {
		if (j >= 100)
			return;
		if (j == jj_endpos + 1)
			jj_lasttokens[jj_endpos++] = i;
		else if (jj_endpos != 0) {
			jj_expentry = new int[jj_endpos];
			for (int k = 0; k < jj_endpos; k++)
				jj_expentry[k] = jj_lasttokens[k];

			boolean flag = false;
			Iterator iter = jj_expentries.iterator();
			while (iter.hasNext()) {
				int ai[] = (int[]) iter.next();
				if (ai.length != jj_expentry.length)
					continue;
				flag = true;
				for (int l = 0; l < jj_expentry.length; l++) {
					if (ai[l] == jj_expentry[l])
						continue;
					flag = false;
					break;
				}

				if (flag)
					break;
			}
			if (!flag)
				jj_expentries.add(jj_expentry);
			if (j != 0)
				jj_lasttokens[(jj_endpos = j) - 1] = i;
		}
	}

	public static final ParseException generateParseException() {
		jj_expentries.clear();
		boolean aflag[] = new boolean[35];
		for (int i = 0; i < 35; i++)
			aflag[i] = false;

		if (jj_kind >= 0) {
			aflag[jj_kind] = true;
			jj_kind = -1;
		}
		for (int j = 0; j < 3; j++)
			if (jj_la1[j] == jj_gen) {
				for (int k = 0; k < 32; k++) {
					if ((jj_la1_0[j] & 1 << k) != 0)
						aflag[k] = true;
					if ((jj_la1_1[j] & 1 << k) != 0)
						aflag[32 + k] = true;
				}

			}

		for (int l = 0; l < 35; l++)
			if (aflag[l]) {
				jj_expentry = new int[1];
				jj_expentry[0] = l;
				jj_expentries.add(jj_expentry);
			}

		jj_endpos = 0;
		jj_rescan_token();
		jj_add_error_token(0, 0);
		int ai[][] = new int[jj_expentries.size()][];
		for (int i1 = 0; i1 < jj_expentries.size(); i1++)
			ai[i1] = (int[]) jj_expentries.get(i1);

		return new ParseException(token, ai, MessageParserConstants.tokenImage);
	}

	public static final void enable_tracing() {
	}

	public static final void disable_tracing() {
	}

	private static final void jj_rescan_token() {
		jj_rescan = true;
		for (int i = 0; i < 1; i++) {
			JJCalls jjcalls = jj_2_rtns[i];
			do {
				if (jjcalls.gen > jj_gen) {
					jj_la = jjcalls.arg;
					jj_lastpos = jj_scanpos = jjcalls.first;
					switch (i) {
						case 0 : // '\0'
							jj_3_1();
							break;
					}
				}
				jjcalls = jjcalls.next;
			}
			while (jjcalls != null);
		}

		jj_rescan = false;
	}

	private static final void jj_save(int i, int j) {
		JJCalls jjcalls;
		for (jjcalls = jj_2_rtns[i]; jjcalls.gen > jj_gen; jjcalls = jjcalls.next) {
			if (jjcalls.next != null)
				continue;
			jjcalls = jjcalls.next = new JJCalls();
			break;
		}

		jjcalls.gen = (jj_gen + j) - jj_la;
		jjcalls.first = token;
		jjcalls.arg = j;
	}

	private static MessageParser mSingleton = null;
	private static boolean jj_initialized_once = false;
	public static MessageParserTokenManager token_source;
	static ASCII_UCodeESC_CharStream jj_input_stream;
	public static Token token;
	public static Token jj_nt;
	private static int jj_ntk;
	private static Token jj_scanpos;
	private static Token jj_lastpos;
	private static int jj_la;
	public static boolean lookingAhead = false;
	private static boolean jj_semLA;
	private static int jj_gen;
	private static final int jj_la1[] = new int[3];
	private static final int jj_la1_0[] = { 0, 0x1000000, 0x1000000 };
	private static final int jj_la1_1[] = { 4, 2, 2 };
	private static final JJCalls jj_2_rtns[] = new JJCalls[1];
	private static boolean jj_rescan = false;
	private static int jj_gc = 0;
	private static List jj_expentries = new java.util.ArrayList();
	private static int jj_expentry[];
	private static int jj_kind = -1;
	private static int jj_lasttokens[] = new int[100];
	private static int jj_endpos;

}
