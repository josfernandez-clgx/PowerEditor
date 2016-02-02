package com.mindbox.server.parser.jtb.rule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;
import com.mindbox.server.parser.jtb.rule.syntaxtree.AdditiveExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ArgumentList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Arguments;
import com.mindbox.server.parser.jtb.rule.syntaxtree.BooleanLiteral;
import com.mindbox.server.parser.jtb.rule.syntaxtree.CellValue;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ClassName;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ColumnLiteral;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ConditionalAndExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ConditionalExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRule;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRuleList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Function;
import com.mindbox.server.parser.jtb.rule.syntaxtree.InstanceName;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Literal;
import com.mindbox.server.parser.jtb.rule.syntaxtree.LiteralList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.MultiplicativeExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Name;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeChoice;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeListOptional;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeOptional;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeSequence;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ObjectCondition;
import com.mindbox.server.parser.jtb.rule.syntaxtree.PrimaryExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.RelationalExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.RuleName;
import com.mindbox.server.parser.jtb.rule.syntaxtree.UnaryExpression;
import com.mindbox.server.parser.jtb.rule.visitor.TreeDumper;
import com.mindbox.server.parser.jtb.rule.visitor.TreeFormatter;

// Referenced classes of package com.mindbox.server.parser.jtb.rule:
//            JTBToolkit, ParseException, Token, RuleParserTokenManager,
//            ASCII_UCodeESC_CharStream, RuleParserConstants

public class RuleParser implements RuleParserConstants {
	static final class JJCalls {

		int gen;
		Token first;
		int arg;
		JJCalls next;

		JJCalls() {
		}
	}

	public static final InstanceName InstanceName() throws ParseException {
		Name name = Name();
		return new InstanceName(name);
	}

	public static final ConditionalAndExpression ConditionalAndExpression() throws ParseException {
		NodeListOptional nodelistoptional = new NodeListOptional();
		RelationalExpression relationalexpression = RelationalExpression();
		label0 : do switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				default :
					jj_la1[7] = jj_gen;
					break label0;

				case 48 : // '0'
					NodeSequence nodesequence = new NodeSequence(2);
					Token token1 = jj_consume_token(48);
					com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
					nodesequence.addNode(nodetoken);
					RelationalExpression relationalexpression1 = RelationalExpression();
					nodesequence.addNode(relationalexpression1);
					nodelistoptional.addNode(nodesequence);
					break;
			}
				while (true);
		//        nodelistoptional.nodes.trimToSize();
		return new ConditionalAndExpression(relationalexpression, nodelistoptional);
	}

	public static final RelationalExpression RelationalExpression() throws ParseException {
		NodeListOptional nodelistoptional = new NodeListOptional();
		NodeChoice nodechoice;
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 19 : // '\023'
			case 20 : // '\024'
			case 21 : // '\025'
			case 22 : // '\026'
			case 23 : // '\027'
				ObjectCondition objectcondition = ObjectCondition();
				nodechoice = new NodeChoice(objectcondition, 0);
				break;

			case 14 : // '\016'
			case 17 : // '\021'
			case 18 : // '\022'
			case 28 : // '\034'
			case 32 : // ' '
			case 34 : // '"'
			case 35 : // '#'
			case 39 : // '\''
			case 40 : // '('
			case 49 : // '1'
			case 50 : // '2'
			case 53 : // '5'
			case 56 : // '8'
			case 59 : // ';'
			case 63 : // '?'
				NodeSequence nodesequence = new NodeSequence(2);
				AdditiveExpression additiveexpression = AdditiveExpression();
				nodesequence.addNode(additiveexpression);
				label0 : do switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
						default :
							jj_la1[8] = jj_gen;
							break label0;

						case 37 : // '%'
						case 38 : // '&'
						case 43 : // '+'
						case 44 : // ','
						case 45 : // '-'
						case 46 : // '.'
						case 54 : // '6'
						case 55 : // '7'
							NodeSequence nodesequence1 = new NodeSequence(2);
							NodeChoice nodechoice1;
							switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
								case 43 : // '+'
									Token token1 = jj_consume_token(43);
									com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken =
										JTBToolkit.makeNodeToken(token1);
									nodechoice1 = new NodeChoice(nodetoken, 0);
									break;

								case 46 : // '.'
									Token token2 = jj_consume_token(46);
									com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 =
										JTBToolkit.makeNodeToken(token2);
									nodechoice1 = new NodeChoice(nodetoken1, 1);
									break;

								case 38 : // '&'
									Token token3 = jj_consume_token(38);
									com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 =
										JTBToolkit.makeNodeToken(token3);
									nodechoice1 = new NodeChoice(nodetoken2, 2);
									break;

								case 37 : // '%'
									Token token4 = jj_consume_token(37);
									com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken3 =
										JTBToolkit.makeNodeToken(token4);
									nodechoice1 = new NodeChoice(nodetoken3, 3);
									break;

								case 44 : // ','
									Token token5 = jj_consume_token(44);
									com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken4 =
										JTBToolkit.makeNodeToken(token5);
									nodechoice1 = new NodeChoice(nodetoken4, 4);
									break;

								case 45 : // '-'
									Token token6 = jj_consume_token(45);
									com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken5 =
										JTBToolkit.makeNodeToken(token6);
									nodechoice1 = new NodeChoice(nodetoken5, 5);
									break;

								case 54 : // '6'
									Token token7 = jj_consume_token(54);
									com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken6 =
										JTBToolkit.makeNodeToken(token7);
									nodechoice1 = new NodeChoice(nodetoken6, 6);
									break;

								case 55 : // '7'
									Token token8 = jj_consume_token(55);
									com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken7 =
										JTBToolkit.makeNodeToken(token8);
									nodechoice1 = new NodeChoice(nodetoken7, 7);
									break;

								default :
									jj_la1[9] = jj_gen;
									jj_consume_token(-1);
									throw new ParseException();
							}
							nodesequence1.addNode(nodechoice1);
							AdditiveExpression additiveexpression1 = AdditiveExpression();
							nodesequence1.addNode(additiveexpression1);
							nodelistoptional.addNode(nodesequence1);
							break;
					}
						while (true);
				//            nodelistoptional.nodes.trimToSize();
				nodesequence.addNode(nodelistoptional);
				nodechoice = new NodeChoice(nodesequence, 1);
				break;

			default :
				jj_la1[10] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
		}
		return new RelationalExpression(nodechoice);
	}

	public static final MultiplicativeExpression MultiplicativeExpression() throws ParseException {
		NodeListOptional nodelistoptional = new NodeListOptional();
		UnaryExpression unaryexpression = UnaryExpression();
		label0 : do switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				default :
					jj_la1[13] = jj_gen;
					break label0;

				case 51 : // '3'
				case 52 : // '4'
				case 53 : // '5'
					NodeSequence nodesequence = new NodeSequence(2);
					NodeChoice nodechoice;
					switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
						case 51 : // '3'
							Token token1 = jj_consume_token(51);
							com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
							nodechoice = new NodeChoice(nodetoken, 0);
							break;

						case 52 : // '4'
							Token token2 = jj_consume_token(52);
							com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
							nodechoice = new NodeChoice(nodetoken1, 1);
							break;

						case 53 : // '5'
							Token token3 = jj_consume_token(53);
							com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
							nodechoice = new NodeChoice(nodetoken2, 2);
							break;

						default :
							jj_la1[14] = jj_gen;
							jj_consume_token(-1);
							throw new ParseException();
					}
					nodesequence.addNode(nodechoice);
					UnaryExpression unaryexpression1 = UnaryExpression();
					nodesequence.addNode(unaryexpression1);
					nodelistoptional.addNode(nodesequence);
					break;
			}
				while (true);
		//        nodelistoptional.nodes.trimToSize();
		return new MultiplicativeExpression(unaryexpression, nodelistoptional);
	}

	public static final DeploymentRuleList DeploymentRuleList() throws ParseException {
		NodeListOptional nodelistoptional = new NodeListOptional();
		label0 : do switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				default :
					jj_la1[0] = jj_gen;
					break label0;

				case 15 : // '\017'
					DeploymentRule deploymentrule = parseDeploymentRule();
					nodelistoptional.addNode(deploymentrule);
					break;
			}
				while (true);
		//        nodelistoptional.nodes.trimToSize();
		return new DeploymentRuleList(nodelistoptional);
	}

	public static final UnaryExpression UnaryExpression() throws ParseException {
		NodeChoice nodechoice;
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 39 : // '\''
			case 40 : // '('
			case 49 : // '1'
			case 50 : // '2'
				NodeSequence nodesequence = new NodeSequence(2);
				NodeChoice nodechoice1;
				switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
					case 49 : // '1'
						Token token1 = jj_consume_token(49);
						com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
						nodechoice1 = new NodeChoice(nodetoken, 0);
						break;

					case 50 : // '2'
						Token token2 = jj_consume_token(50);
						com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
						nodechoice1 = new NodeChoice(nodetoken1, 1);
						break;

					case 40 : // '('
						Token token3 = jj_consume_token(40);
						com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
						nodechoice1 = new NodeChoice(nodetoken2, 2);
						break;

					case 39 : // '\''
						Token token4 = jj_consume_token(39);
						com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken3 = JTBToolkit.makeNodeToken(token4);
						nodechoice1 = new NodeChoice(nodetoken3, 3);
						break;

					default :
						jj_la1[15] = jj_gen;
						jj_consume_token(-1);
						throw new ParseException();
				}
				nodesequence.addNode(nodechoice1);
				UnaryExpression unaryexpression = UnaryExpression();
				nodesequence.addNode(unaryexpression);
				nodechoice = new NodeChoice(nodesequence, 0);
				break;

			case 14 : // '\016'
			case 17 : // '\021'
			case 18 : // '\022'
			case 28 : // '\034'
			case 32 : // ' '
			case 34 : // '"'
			case 35 : // '#'
			case 53 : // '5'
			case 56 : // '8'
			case 59 : // ';'
			case 63 : // '?'
				PrimaryExpression primaryexpression = PrimaryExpression();
				nodechoice = new NodeChoice(primaryexpression, 1);
				break;

			default :
				jj_la1[16] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
		}
		return new UnaryExpression(nodechoice);
	}

	public static final LiteralList LiteralList() throws ParseException {
		NodeListOptional nodelistoptional = new NodeListOptional();
		Token token1 = jj_consume_token(63);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		Literal literal = Literal();
		label0 : do switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				default :
					jj_la1[18] = jj_gen;
					break label0;

				case 66 : // 'B'
					NodeSequence nodesequence = new NodeSequence(2);
					Token token2 = jj_consume_token(66);
					com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
					nodesequence.addNode(nodetoken1);
					Literal literal1 = Literal();
					nodesequence.addNode(literal1);
					nodelistoptional.addNode(nodesequence);
					break;
			}
				while (true);
		//        nodelistoptional.nodes.trimToSize();
		Token token3 = jj_consume_token(64);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
		return new LiteralList(nodetoken, literal, nodelistoptional, nodetoken2);
	}

	public static synchronized RuleParser getInstance(Reader reader) {
		if (mSingleton == null)
			mSingleton = new RuleParser(reader);
		else
			reInitialize(reader);
		return mSingleton;
	}

	private static final boolean jj_scan_token(int i) {
		if (jj_scanpos == jj_lastpos) {
			jj_la--;
			if (jj_scanpos.next == null)
				jj_lastpos = jj_scanpos = jj_scanpos.next = RuleParserTokenManager.getNextToken();
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

	private static final void jj_rescan_token() {
		jj_rescan = true;
		int i = 0;
		do {
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
		while (++i < 1);
		jj_rescan = false;
	}

	public static final void enable_tracing() {
	}

	public static final void disable_tracing() {
	}

	public static final Function Function() throws ParseException {
		Name name = Name();
		Arguments arguments = Arguments();
		return new Function(name, arguments);
	}

	public static final ConditionalExpression ConditionalExpression() throws ParseException {
		NodeListOptional nodelistoptional = new NodeListOptional();
		ConditionalAndExpression conditionalandexpression = ConditionalAndExpression();
		label0 : do switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				default :
					jj_la1[6] = jj_gen;
					break label0;

				case 47 : // '/'
					NodeSequence nodesequence = new NodeSequence(2);
					Token token1 = jj_consume_token(47);
					com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
					nodesequence.addNode(nodetoken);
					ConditionalAndExpression conditionalandexpression1 = ConditionalAndExpression();
					nodesequence.addNode(conditionalandexpression1);
					nodelistoptional.addNode(nodesequence);
					break;
			}
				while (true);
		//        nodelistoptional.nodes.trimToSize();
		return new ConditionalExpression(conditionalandexpression, nodelistoptional);
	}

	public static void main(String args[]) {
		if (args.length == 0) {
			System.out.println("RuleParser:  Reading from standard input . . .");
			RuleParser ruleparser = new RuleParser(System.in);
			enable_tracing();
		}
		else if (args.length == 1) {
			System.out.println("RuleParser:  Reading from file " + args[0] + " . . .");
			RuleParser ruleparser1;
			try {
				ruleparser1 = new RuleParser(new FileInputStream(args[0]));
			}
			catch (FileNotFoundException _ex) {
				System.out.println("RuleParser:  File " + args[0] + " not found.");
				return;
			}
		}
		else {
			System.out.println("RuleParser:  Usage is one of:");
			System.out.println("         java RuleParser < inputfile");
			System.out.println("OR");
			System.out.println("         java RuleParser inputfile");
			return;
		}
		try {
			DeploymentRuleList deploymentrulelist = DeploymentRuleList();
			System.err.println("Sapphire Rules parsed successfully.");
			deploymentrulelist.accept(new TreeFormatter());
			deploymentrulelist.accept(new TreeDumper());
			System.out.println("Rule Parser: Deployment rule(s) parsed successfully");
		}
		catch (ParseException parseexception) {
			System.out.println("RuleParser:  Encountered errors during parse.");
			parseexception.printStackTrace();
		}
	}

	public static final CellValue CellValue() throws ParseException {
		Token token1 = jj_consume_token(REM);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		Token token2 = jj_consume_token(CELLVALUE);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
		Token token3 = jj_consume_token(REM);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
		return new CellValue(nodetoken, nodetoken1, nodetoken2);
	}

	public static final RuleName RuleName() throws ParseException {
		Token token1 = jj_consume_token(REM);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		Token token2 = jj_consume_token(RULENAME);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
		Token token3 = jj_consume_token(REM);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
		return new RuleName(nodetoken, nodetoken1, nodetoken2);
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

	public static final ClassName ClassName() throws ParseException {
		Token token1 = jj_consume_token(56);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		return new ClassName(nodetoken);
	}

	private static final boolean jj_3R_10() {
		if (jj_scan_token(53))
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			return false;
		if (jj_scan_token(26))
			return true;
		return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
	}

	public static final AdditiveExpression AdditiveExpression() throws ParseException {
		NodeListOptional nodelistoptional = new NodeListOptional();
		MultiplicativeExpression multiplicativeexpression = MultiplicativeExpression();
		label0 : do switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				default :
					jj_la1[11] = jj_gen;
					break label0;

				case 49 : // '1'
				case 50 : // '2'
					NodeSequence nodesequence = new NodeSequence(2);
					NodeChoice nodechoice;
					switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
						case 49 : // '1'
							Token token1 = jj_consume_token(49);
							com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
							nodechoice = new NodeChoice(nodetoken, 0);
							break;

						case 50 : // '2'
							Token token2 = jj_consume_token(50);
							com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
							nodechoice = new NodeChoice(nodetoken1, 1);
							break;

						default :
							jj_la1[12] = jj_gen;
							jj_consume_token(-1);
							throw new ParseException();
					}
					nodesequence.addNode(nodechoice);
					MultiplicativeExpression multiplicativeexpression1 = MultiplicativeExpression();
					nodesequence.addNode(multiplicativeexpression1);
					nodelistoptional.addNode(nodesequence);
					break;
			}
				while (true);
		//        nodelistoptional.nodes.trimToSize();
		return new AdditiveExpression(multiplicativeexpression, nodelistoptional);
	}

	public static final PrimaryExpression PrimaryExpression() throws ParseException {
		NodeChoice nodechoice;
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 63 : // '?'
				LiteralList literallist = LiteralList();
				nodechoice = new NodeChoice(literallist, 0);
				break;

			case 14 : // '\016'
			case 17 : // '\021'
			case 18 : // '\022'
			case 28 : // '\034'
			case 32 : // ' '
			case 34 : // '"'
			case 35 : // '#'
			case 53 : // '5'
				Literal literal = Literal();
				nodechoice = new NodeChoice(literal, 1);
				break;

			case 56 : // '8'
				Name name = Name();
				nodechoice = new NodeChoice(name, 2);
				break;

			case 59 : // ';'
				NodeSequence nodesequence = new NodeSequence(3);
				Token token1 = jj_consume_token(59);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
				nodesequence.addNode(nodetoken);
				ConditionalExpression conditionalexpression = ConditionalExpression();
				nodesequence.addNode(conditionalexpression);
				Token token2 = jj_consume_token(60);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
				nodesequence.addNode(nodetoken1);
				nodechoice = new NodeChoice(nodesequence, 3);
				break;

			default :
				jj_la1[17] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
		}
		return new PrimaryExpression(nodechoice);
	}

	private static final boolean jj_2_1(int i) {
		jj_la = i;
		jj_lastpos = jj_scanpos = token;
		boolean flag = !jj_3_1();
		jj_save(0, i);
		return flag;
	}

	private static final Token jj_consume_token(int i) throws ParseException {
		Token token1;
		if ((token1 = token).next != null)
			token = token.next;
		else
			token = token.next = RuleParserTokenManager.getNextToken();
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

	public static final Token getToken(int i) {
		Token token1 = lookingAhead ? jj_scanpos : token;
		for (int j = 0; j < i; j++)
			if (token1.next != null)
				token1 = token1.next;
			else
				token1 = token1.next = RuleParserTokenManager.getNextToken();

		return token1;
	}

	public static final Literal Literal() throws ParseException {
		NodeChoice nodechoice;
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case INTEGER_LITERAL : // '\034'
				Token token1 = jj_consume_token(28);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
				nodechoice = new NodeChoice(nodetoken, 0);
				break;

			case FLOATING_POINT_LITERAL : // ' '
				Token token2 = jj_consume_token(32);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
				nodechoice = new NodeChoice(nodetoken1, 1);
				break;

			case CHARACTER_LITERAL : // '"'
				Token token3 = jj_consume_token(34);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
				nodechoice = new NodeChoice(nodetoken2, 2);
				break;

			case STRING_LITERAL : // '#'
				Token token4 = jj_consume_token(35);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken3 = JTBToolkit.makeNodeToken(token4);
				nodechoice = new NodeChoice(nodetoken3, 3);
				break;

			case FALSE : // '\016'
			case TRUE : // '\022'
				BooleanLiteral booleanliteral = BooleanLiteral();
				nodechoice = new NodeChoice(booleanliteral, 4);
				break;

			case NULL : // '\021'
				Token token5 = jj_consume_token(17);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken4 = JTBToolkit.makeNodeToken(token5);
				nodechoice = new NodeChoice(nodetoken4, 5);
				break;

			default :
				jj_la1[19] = jj_gen;
				boolean flag = jj_2_1(2);

				//System.out.println("RuleParser.Literal: before flag - jj_la1[19] = " + jj_la1[19]);
				//System.out.println("RuleParser.Literal: before flag - is4columnLiteral? = " + flag);
				if (flag) {
					ColumnLiteral columnliteral = ColumnLiteral();
					nodechoice = new NodeChoice(columnliteral, 6);
					break;
				}
				//System.out.println("RuleParser.Literal: before cellvalue; jj_ntk = " + jj_ntk);
				switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
					case REM : // '%'
						CellValue cellvalue = CellValue();
						nodechoice = new NodeChoice(cellvalue, 7);
						break;

					default :
						jj_la1[20] = jj_gen;
						jj_consume_token(-1);
						throw new ParseException();
				}
				break;
		}
		return new Literal(nodechoice);
	}

	public RuleParser(InputStream inputstream) {
		if (jj_initialized_once) {
			System.out.println("ERROR: Second call to constructor of static parser.  You must");
			System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
			System.out.println("       during parser generation.");
			throw new Error();
		}
		jj_initialized_once = true;
		jj_input_stream = new ASCII_UCodeESC_CharStream(inputstream, 1, 1);
		token_source = new RuleParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		int i = 0;
		do jj_la1[i] = -1;
		while (++i < 24);
		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public static void ReInit(InputStream inputstream) {
		jj_input_stream.ReInit(inputstream, 1, 1);
		RuleParserTokenManager.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		int i = 0;
		do jj_la1[i] = -1;
		while (++i < 24);
		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}


	public RuleParser(Reader reader) {
		if (jj_initialized_once) {
			System.out.println("ERROR: Second call to constructor of static parser.  You must");
			System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
			System.out.println("       during parser generation.");
			throw new Error();
		}
		jj_initialized_once = true;
		jj_input_stream = new ASCII_UCodeESC_CharStream(reader, 1, 1);
		token_source = new RuleParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		int i = 0;
		do jj_la1[i] = -1;
		while (++i < 24);
		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public static void reInitialize(Reader reader) {
		jj_input_stream.ReInit(reader, 1, 1);
		RuleParserTokenManager.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		int i = 0;
		do jj_la1[i] = -1;
		while (++i < 24);
		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public RuleParser(RuleParserTokenManager ruleparsertokenmanager) {
		if (jj_initialized_once) {
			System.out.println("ERROR: Second call to constructor of static parser.  You must");
			System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
			System.out.println("       during parser generation.");
			throw new Error();
		}
		jj_initialized_once = true;
		token_source = ruleparsertokenmanager;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		int i = 0;
		do jj_la1[i] = -1;
		while (++i < 24);
		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public void ReInit(RuleParserTokenManager ruleparsertokenmanager) {
		token_source = ruleparsertokenmanager;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		int i = 0;
		do jj_la1[i] = -1;
		while (++i < 24);
		for (int j = 0; j < jj_2_rtns.length; j++)
			jj_2_rtns[j] = new JJCalls();

	}

	public static final DeploymentRule parseDeploymentRule() throws ParseException {
		NodeOptional nodeoptional = new NodeOptional();
		Token token1 = jj_consume_token(15);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		ConditionalExpression conditionalexpression = ConditionalExpression();
		Token token2 = jj_consume_token(16);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
		Action action = Action();
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 0 : // '\0'
				Token token3 = jj_consume_token(0);
				token3.beginColumn++;
				token3.endColumn++;
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
				nodeoptional.addNode(nodetoken2);
				break;

			default :
				jj_la1[1] = jj_gen;
				break;
		}
		return new DeploymentRule(nodetoken, conditionalexpression, nodetoken1, action, nodeoptional);
	}

	public static final Name Name() throws ParseException {
		NodeListOptional nodelistoptional = new NodeListOptional();
		Token token1 = jj_consume_token(56);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		label0 : do switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				default :
					jj_la1[5] = jj_gen;
					break label0;

				case 67 : // 'C'
					NodeSequence nodesequence = new NodeSequence(2);
					Token token2 = jj_consume_token(67);
					com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
					nodesequence.addNode(nodetoken1);
					Token token3 = jj_consume_token(56);
					com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
					nodesequence.addNode(nodetoken2);
					nodelistoptional.addNode(nodesequence);
					break;
			}
				while (true);
		//        nodelistoptional.nodes.trimToSize();
		return new Name(nodetoken, nodelistoptional);
	}

	public static final ArgumentList ArgumentList() throws ParseException {
		NodeListOptional nodelistoptional = new NodeListOptional();
		AdditiveExpression additiveexpression = AdditiveExpression();
		label0 : do switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				default :
					jj_la1[23] = jj_gen;
					break label0;

				case 66 : // 'B'
					NodeSequence nodesequence = new NodeSequence(2);
					Token token1 = jj_consume_token(66);
					com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
					nodesequence.addNode(nodetoken);
					AdditiveExpression additiveexpression1 = AdditiveExpression();
					nodesequence.addNode(additiveexpression1);
					nodelistoptional.addNode(nodesequence);
					break;
			}
				while (true);
		//        nodelistoptional.nodes.trimToSize();
		return new ArgumentList(additiveexpression, nodelistoptional);
	}

	public static final Arguments Arguments() throws ParseException {
		NodeOptional nodeoptional = new NodeOptional();
		Token token1 = jj_consume_token(59);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 14 : // '\016'
			case 17 : // '\021'
			case 18 : // '\022'
			case 28 : // '\034'
			case 32 : // ' '
			case 34 : // '"'
			case 35 : // '#'
			case 39 : // '\''
			case 40 : // '('
			case 49 : // '1'
			case 50 : // '2'
			case 53 : // '5'
			case 56 : // '8'
			case 59 : // ';'
			case 63 : // '?'
				ArgumentList argumentlist = ArgumentList();
				nodeoptional.addNode(argumentlist);
				break;

			default :
				jj_la1[22] = jj_gen;
				break;
		}
		Token token2 = jj_consume_token(60);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
		return new Arguments(nodetoken, nodeoptional, nodetoken1);
	}

	public static final Action Action() throws ParseException {
		Name name = Name();
		Arguments arguments = Arguments();
		return new Action(name, arguments);
	}

	public static final ParseException generateParseException() {
		jj_expentries.clear();
		boolean aflag[] = new boolean[68];
		int i = 0;
		do aflag[i] = false;
		while (++i < 68);
		if (jj_kind >= 0) {
			aflag[jj_kind] = true;
			jj_kind = -1;
		}
		i = 0;
		do if (jj_la1[i] == jj_gen) {
				int j = 0;
				do {
					if ((jj_la1_0[i] & 1 << j) != 0)
						aflag[j] = true;
					if ((jj_la1_1[i] & 1 << j) != 0)
						aflag[32 + j] = true;
					if ((jj_la1_2[i] & 1 << j) != 0)
						aflag[64 + j] = true;
				}
				while (++j < 32);
			}
		while (++i < 24);
		i = 0;
		do if (aflag[i]) {
				jj_expentry = new int[1];
				jj_expentry[0] = i;
				jj_expentries.add(jj_expentry);
			}
				while (++i < 68);
		jj_endpos = 0;
		jj_rescan_token();
		jj_add_error_token(0, 0);
		int ai[][] = new int[jj_expentries.size()][];
		for (int k = 0; k < jj_expentries.size(); k++)
			ai[k] = (int[]) jj_expentries.get(k);

		return new ParseException(token, ai, RuleParserConstants.tokenImage);
	}

	private static final boolean jj_3_1() {
		if (jj_3R_10())
			return true;
		return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
	}

	public static final Token getNextToken() {
		if (token.next != null)
			token = token.next;
		else
			token = token.next = RuleParserTokenManager.getNextToken();
		jj_ntk = -1;
		jj_gen++;
		return token;
	}

	public static final BooleanLiteral BooleanLiteral() throws ParseException {
		NodeChoice nodechoice;
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 18 : // '\022'
				Token token1 = jj_consume_token(18);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
				nodechoice = new NodeChoice(nodetoken, 0);
				break;

			case 14 : // '\016'
				Token token2 = jj_consume_token(14);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
				nodechoice = new NodeChoice(nodetoken1, 1);
				break;

			case 15 : // '\017'
			case 16 : // '\020'
			case 17 : // '\021'
			default :
				jj_la1[21] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
		}
		return new BooleanLiteral(nodechoice);
	}

	public static final ColumnLiteral ColumnLiteral() throws ParseException {
		Token token1 = jj_consume_token(REM);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
		Token token2 = jj_consume_token(COLUMN);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
		Token token3 = jj_consume_token(INTEGER_LITERAL);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
		Token token4 = jj_consume_token(REM);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken3 = JTBToolkit.makeNodeToken(token4);
		return new ColumnLiteral(nodetoken, nodetoken1, nodetoken2, nodetoken3);
	}

	private static final int jj_ntk() {
		if ((jj_nt = token.next) == null)
			return jj_ntk = (token.next = RuleParserTokenManager.getNextToken()).kind;
		else
			return jj_ntk = jj_nt.kind;
	}

	public static final ObjectCondition ObjectCondition() throws ParseException {
		NodeOptional nodeoptional = new NodeOptional();
		NodeOptional nodeoptional1 = new NodeOptional();
		NodeChoice nodechoice;
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 20 : // '\024'
				Token token1 = jj_consume_token(20);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
				nodechoice = new NodeChoice(nodetoken, 0);
				break;

			case 19 : // '\023'
				Token token2 = jj_consume_token(19);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
				nodechoice = new NodeChoice(nodetoken1, 1);
				break;

			case 21 : // '\025'
				Token token3 = jj_consume_token(21);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
				nodechoice = new NodeChoice(nodetoken2, 2);
				break;

			case 22 : // '\026'
				NodeSequence nodesequence = new NodeSequence(2);
				Token token4 = jj_consume_token(22);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken3 = JTBToolkit.makeNodeToken(token4);
				nodesequence.addNode(nodetoken3);
				Token token5 = jj_consume_token(28);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken4 = JTBToolkit.makeNodeToken(token5);
				nodesequence.addNode(nodetoken4);
				nodechoice = new NodeChoice(nodesequence, 3);
				break;

			case 23 : // '\027'
				NodeSequence nodesequence1 = new NodeSequence(2);
				Token token6 = jj_consume_token(23);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken5 = JTBToolkit.makeNodeToken(token6);
				nodesequence1.addNode(nodetoken5);
				Token token7 = jj_consume_token(28);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken6 = JTBToolkit.makeNodeToken(token7);
				nodesequence1.addNode(nodetoken6);
				nodechoice = new NodeChoice(nodesequence1, 4);
				break;

			default :
				jj_la1[2] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
		}
		ClassName classname = ClassName();
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 56 : // '8'
				InstanceName instancename = InstanceName();
				nodeoptional.addNode(instancename);
				break;

			default :
				jj_la1[3] = jj_gen;
				break;
		}
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 25 : // '\031'
				NodeSequence nodesequence2 = new NodeSequence(2);
				Token token8 = jj_consume_token(25);
				com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken7 = JTBToolkit.makeNodeToken(token8);
				nodesequence2.addNode(nodetoken7);
				InstanceName instancename1 = InstanceName();
				nodesequence2.addNode(instancename1);
				nodeoptional1.addNode(nodesequence2);
				break;

			default :
				jj_la1[4] = jj_gen;
				break;
		}
		Token token9 = jj_consume_token(24);
		com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken nodetoken8 = JTBToolkit.makeNodeToken(token9);
		RelationalExpression relationalexpression = RelationalExpression();
		return new ObjectCondition(nodechoice, classname, nodeoptional, nodeoptional1, nodetoken8, relationalexpression);
	}

	private static RuleParser mSingleton = null;
	private static boolean jj_initialized_once = false;
	public static RuleParserTokenManager token_source;
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
	private static final int jj_la1[] = new int[24];
	private static final int jj_la1_0[] =
		{
			32768,
			1,
			0xf80000,
			0,
			0x2000000,
			0,
			0,
			0,
			0,
			0,
			0x10fe4000,
			0,
			0,
			0,
			0,
			0,
			0x10064000,
			0x10064000,
			0,
			0x10064000,
			0,
			0x44000,
			0x10064000,
			0 };
	private static final int jj_la1_1[] =
		{
			0,
			0,
			0,
			0x1000000,
			0,
			0,
			32768,
			0x10000,
			0xc07860,
			0xc07860,
			0x8926018d,
			0x60000,
			0x60000,
			0x380000,
			0x380000,
			0x60180,
			0x8926018d,
			0x8920000d,
			0,
			13,
			0x200000,
			0,
			0x8926018d,
			0 };
	private static final int jj_la1_2[] = { 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 4 };
	private static final JJCalls jj_2_rtns[] = new JJCalls[1];
	private static boolean jj_rescan = false;
	private static int jj_gc = 0;
	private static List jj_expentries = new java.util.ArrayList();
	private static int jj_expentry[];
	private static int jj_kind = -1;
	private static int jj_lasttokens[] = new int[100];
	private static int jj_endpos;

}