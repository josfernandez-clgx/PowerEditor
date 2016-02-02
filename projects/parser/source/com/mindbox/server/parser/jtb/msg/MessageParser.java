// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:00:36 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   MessageParser.java

package com.mindbox.server.parser.jtb.msg;

import com.mindbox.server.parser.jtb.msg.syntaxtree.CellValue;
import com.mindbox.server.parser.jtb.msg.syntaxtree.ColumnLiteral;
import com.mindbox.server.parser.jtb.msg.syntaxtree.Message;
import com.mindbox.server.parser.jtb.msg.syntaxtree.Name;
//import com.mindbox.server.parser.jtb.msg.syntaxtree.Node;
import com.mindbox.server.parser.jtb.msg.syntaxtree.NodeChoice;
import com.mindbox.server.parser.jtb.msg.syntaxtree.NodeList;
import com.mindbox.server.parser.jtb.msg.syntaxtree.NodeSequence;
import com.mindbox.server.parser.jtb.msg.syntaxtree.Word;
import com.mindbox.server.parser.jtb.msg.visitor.TreeDumper;
import com.mindbox.server.parser.jtb.msg.visitor.TreeFormatter;
import java.io.*;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.mindbox.server.parser.jtb.msg:
//            Token, JTBToolkit, MessageParserTokenManager, ParseException,
//            ASCII_UCodeESC_CharStream, MessageParserConstants

public class MessageParser
    implements MessageParserConstants
{
    static final class JJCalls
    {

        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls()
        {
        }
    }


    public static final Message Message()
        throws ParseException
    {
        NodeList nodelist = new NodeList();
label0:
        do
        {
            Word word = Word();
            nodelist.addNode(word);
            switch(jj_ntk != -1 ? jj_ntk : jj_ntk())
            {
            case 16: // '\020'
            case 20: // '\024'
            case 22: // '\026'
            case 23: // '\027'
            case 24: // '\030'
            case 27: // '\033'
            case 28: // '\034'
                break;

            default:
                jj_la1[1] = jj_gen;
                break label0;
            }
        } while(true);
//        nodelist.nodes.trimToSize();
        Token token1 = jj_consume_token(0);
        token1.beginColumn++;
        token1.endColumn++;
        com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
        return new Message(nodelist, nodetoken);
    }

    private static final boolean jj_scan_token(int i)
    {
        if(jj_scanpos == jj_lastpos)
        {
            jj_la--;
            if(jj_scanpos.next == null)
                jj_lastpos = jj_scanpos = jj_scanpos.next = MessageParserTokenManager.getNextToken();
            else
                jj_lastpos = jj_scanpos = jj_scanpos.next;
        } else
        {
            jj_scanpos = jj_scanpos.next;
        }
        if(jj_rescan)
        {
            int j = 0;
            Token token1;
            for(token1 = token; token1 != null && token1 != jj_scanpos; token1 = token1.next)
                j++;

            if(token1 != null)
                jj_add_error_token(i, j);
        }
        return jj_scanpos.kind != i;
    }

    private static void jj_add_error_token(int i, int j)
    {
        if(j >= 100)
            return;
        if(j == jj_endpos + 1)
            jj_lasttokens[jj_endpos++] = i;
        else
        if(jj_endpos != 0)
        {
            jj_expentry = new int[jj_endpos];
            for(int k = 0; k < jj_endpos; k++)
                jj_expentry[k] = jj_lasttokens[k];

            boolean flag = false;
            Iterator iter = jj_expentries.iterator();
            while(iter.hasNext())
            {
                int ai[] = (int[])iter.next();
                if(ai.length != jj_expentry.length)
                    continue;
                flag = true;
                for(int l = 0; l < jj_expentry.length; l++)
                {
                    if(ai[l] == jj_expentry[l])
                        continue;
                    flag = false;
                    break;
                }

                if(flag)
                    break;
            }
            if(!flag)
                jj_expentries.add(jj_expentry);
            if(j != 0)
                jj_lasttokens[(jj_endpos = j) - 1] = i;
        }
    }

    private static final void jj_rescan_token()
    {
        jj_rescan = true;
        int i = 0;
        do
        {
            JJCalls jjcalls = jj_2_rtns[i];
            do
            {
                if(jjcalls.gen > jj_gen)
                {
                    jj_la = jjcalls.arg;
                    jj_lastpos = jj_scanpos = jjcalls.first;
                    switch(i)
                    {
                    case 0: // '\0'
                        jj_3_1();
                        break;

                    case 1: // '\001'
                        jj_3_2();
                        break;

                    case 2: // '\002'
                        jj_3_3();
                        break;
                    }
                }
                jjcalls = jjcalls.next;
            } while(jjcalls != null);
        } while(++i < 3);
        jj_rescan = false;
    }

    public static final void enable_tracing()
    {
    }

    public static final void disable_tracing()
    {
    }

    private static final boolean jj_2_3(int i)
    {
        jj_la = i;
        jj_lastpos = jj_scanpos = token;
        boolean flag = !jj_3_3();
        jj_save(2, i);
        return flag;
    }

    public static void main(String args[])
    {
        if(args.length == 0)
        {
            System.out.println("MessageParser:  Reading from standard input . . .");
            MessageParser messageparser = new MessageParser(System.in);
            enable_tracing();
        } else
        if(args.length == 1)
        {
            System.out.println("MessageParser:  Reading from file " + args[0] + " . . .");
            MessageParser messageparser1;
            try
            {
                messageparser1 = new MessageParser(new FileInputStream(args[0]));
            }
            catch(FileNotFoundException _ex)
            {
                System.out.println("MessageParser:  File " + args[0] + " not found.");
                return;
            }
        } else
        {
            System.out.println("MessageParser:  Usage is one of:");
            System.out.println("         java MessageParser < inputfile");
            System.out.println("OR");
            System.out.println("         java MessageParser inputfile");
            return;
        }
        try
        {
            Message message = Message();
            System.err.println("Sapphire Messages parsed successfully.");
            message.accept(new TreeFormatter());
            message.accept(new TreeDumper());
            System.out.println("Message Parser: Deployment rule(s) parsed successfully");
        }
        catch(ParseException parseexception)
        {
            System.out.println("MessageParser:  Encountered errors during parse.");
            parseexception.printStackTrace();
        }
    }

    public static final CellValue CellValue()
        throws ParseException
    {
        Token token1 = jj_consume_token(28);
        com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
        Token token2 = jj_consume_token(15);
        com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
        Token token3 = jj_consume_token(28);
        com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
        return new CellValue(nodetoken, nodetoken1, nodetoken2);
    }

    private static final boolean jj_3R_5()
    {
        if(jj_scan_token(24))
            return true;
        return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
    }

    private static final boolean jj_3_2()
    {
        if(jj_3R_4())
            return true;
        return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
    }

    private static final void jj_save(int i, int j)
    {
        JJCalls jjcalls;
        for(jjcalls = jj_2_rtns[i]; jjcalls.gen > jj_gen; jjcalls = jjcalls.next)
        {
            if(jjcalls.next != null)
                continue;
            jjcalls = jjcalls.next = new JJCalls();
            break;
        }

        jjcalls.gen = (jj_gen + j) - jj_la;
        jjcalls.first = token;
        jjcalls.arg = j;
    }

    private static final boolean jj_3R_3()
    {
        if(jj_scan_token(28))
            return true;
        if(jj_la == 0 && jj_scanpos == jj_lastpos)
            return false;
        if(jj_scan_token(14))
            return true;
        return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
    }

    public static final Word Word()
        throws ParseException
    {
        NodeChoice nodechoice;
        switch(jj_ntk != -1 ? jj_ntk : jj_ntk())
        {
        case 16: // '\020'
            Token token1 = jj_consume_token(16);
            com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
            nodechoice = new NodeChoice(nodetoken, 0);
            break;

        case 20: // '\024'
            Token token2 = jj_consume_token(20);
            com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
            nodechoice = new NodeChoice(nodetoken1, 1);
            break;

        case 22: // '\026'
            Token token3 = jj_consume_token(22);
            com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
            nodechoice = new NodeChoice(nodetoken2, 2);
            break;

        case 23: // '\027'
           Token token4 = jj_consume_token(23);
            com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken3 = JTBToolkit.makeNodeToken(token4);
            nodechoice = new NodeChoice(nodetoken3, 3);
            break;

        case 17: // '\021'
        case 18: // '\022'
        case 19: // '\023'
        case 21: // '\025'
        default:
            jj_la1[2] = jj_gen;
            if(jj_2_1(2))
            {
                ColumnLiteral columnliteral = ColumnLiteral();
                nodechoice = new NodeChoice(columnliteral, 4);
                break;
            }
            if(jj_2_2(2))
            {
                CellValue cellvalue = CellValue();
                nodechoice = new NodeChoice(cellvalue, 5);
                break;
            }
            if(jj_2_3(2))
            {
                NodeSequence nodesequence = new NodeSequence(4);
                Token token5 = jj_consume_token(28);
                com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken4 = JTBToolkit.makeNodeToken(token5);
                nodesequence.addNode(nodetoken4);
                Name name = Name();
                nodesequence.addNode(name);
                Token token6 = jj_consume_token(28);
                com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken5 = JTBToolkit.makeNodeToken(token6);
                nodesequence.addNode(nodetoken5);
                nodechoice = new NodeChoice(nodesequence, 6);
                break;
            }
            switch(jj_ntk != -1 ? jj_ntk : jj_ntk())
            {
            case 24: // '\030'
                Token token7 = jj_consume_token(24);
                com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken6 = JTBToolkit.makeNodeToken(token7);
                nodechoice = new NodeChoice(nodetoken6, 7);
                break;

            case 27: // '\033'
                Token token8 = jj_consume_token(27);
                com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken7 = JTBToolkit.makeNodeToken(token8);
                nodechoice = new NodeChoice(nodetoken7, 8);
                break;

            case 28: // '\034'
                Token token9 = jj_consume_token(28);
                com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken8 = JTBToolkit.makeNodeToken(token9);
                nodechoice = new NodeChoice(nodetoken8, 9);
                break;

            case 25: // '\031'
            case 26: // '\032'
            default:
                jj_la1[3] = jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            }
            break;
        }
        return new Word(nodechoice);
    }

    private static final boolean jj_3R_4()
    {
        if(jj_scan_token(28))
            return true;
        if(jj_la == 0 && jj_scanpos == jj_lastpos)
            return false;
        if(jj_scan_token(15))
            return true;
        return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
    }

    private static final boolean jj_2_1(int i)
    {
        jj_la = i;
        jj_lastpos = jj_scanpos = token;
        boolean flag = !jj_3_1();
        jj_save(0, i);
        return flag;
    }

    private static final boolean jj_3_3()
    {
        if(jj_scan_token(28))
            return true;
        if(jj_la == 0 && jj_scanpos == jj_lastpos)
            return false;
        if(jj_3R_5())
            return true;
        return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
    }

    private static final Token jj_consume_token(int i)
        throws ParseException
    {
        Token token1;
        if((token1 = token).next != null)
            token = token.next;
        else
            token = token.next = MessageParserTokenManager.getNextToken();
        jj_ntk = -1;
        if(token.kind == i)
        {
            jj_gen++;
            if(++jj_gc > 100)
            {
                jj_gc = 0;
                for(int j = 0; j < jj_2_rtns.length; j++)
                {
                    for(JJCalls jjcalls = jj_2_rtns[j]; jjcalls != null; jjcalls = jjcalls.next)
                        if(jjcalls.gen < jj_gen)
                            jjcalls.first = null;

                }

            }
            return token;
        } else
        {
            token = token1;
            jj_kind = i;
            throw generateParseException();
        }
    }

    public static final Token getToken(int i)
    {
        Token token1 = lookingAhead ? jj_scanpos : token;
        for(int j = 0; j < i; j++)
            if(token1.next != null)
                token1 = token1.next;
            else
                token1 = token1.next = MessageParserTokenManager.getNextToken();

        return token1;
    }

    public MessageParser(InputStream inputstream)
    {
        if(jj_initialized_once)
        {
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
        int i = 0;
        do
            jj_la1[i] = -1;
        while(++i < 4);
        for(int j = 0; j < jj_2_rtns.length; j++)
            jj_2_rtns[j] = new JJCalls();

    }

    public static void ReInit(InputStream inputstream)
    {
        jj_input_stream.ReInit(inputstream, 1, 1);
        MessageParserTokenManager.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        int i = 0;
        do
            jj_la1[i] = -1;
        while(++i < 4);
        for(int j = 0; j < jj_2_rtns.length; j++)
            jj_2_rtns[j] = new JJCalls();

    }

    public MessageParser(Reader reader)
    {
        if(jj_initialized_once)
        {
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
        int i = 0;
        do
            jj_la1[i] = -1;
        while(++i < 4);
        for(int j = 0; j < jj_2_rtns.length; j++)
            jj_2_rtns[j] = new JJCalls();

    }

    public static void ReInit(Reader reader)
    {
        jj_input_stream.ReInit(reader, 1, 1);
        MessageParserTokenManager.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        int i = 0;
        do
            jj_la1[i] = -1;
        while(++i < 4);
        for(int j = 0; j < jj_2_rtns.length; j++)
            jj_2_rtns[j] = new JJCalls();

    }

    public MessageParser(MessageParserTokenManager messageparsertokenmanager)
    {
        if(jj_initialized_once)
        {
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
        int i = 0;
        do
            jj_la1[i] = -1;
        while(++i < 4);
        for(int j = 0; j < jj_2_rtns.length; j++)
            jj_2_rtns[j] = new JJCalls();

    }

    public void ReInit(MessageParserTokenManager messageparsertokenmanager)
    {
        token_source = messageparsertokenmanager;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        int i = 0;
        do
            jj_la1[i] = -1;
        while(++i < 4);
        for(int j = 0; j < jj_2_rtns.length; j++)
            jj_2_rtns[j] = new JJCalls();

    }

    public static final Name Name()
        throws ParseException
    {
        NodeList nodelist = new NodeList();
        Token token1 = jj_consume_token(24);
        com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
label0:
        do
        {
            NodeSequence nodesequence = new NodeSequence(2);
            Token token2 = jj_consume_token(27);
            com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
            nodesequence.addNode(nodetoken1);
            Token token3 = jj_consume_token(24);
            com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
            nodesequence.addNode(nodetoken2);
            nodelist.addNode(nodesequence);
            switch(jj_ntk != -1 ? jj_ntk : jj_ntk())
            {
            case 27: // '\033'
                break;

            default:
                jj_la1[0] = jj_gen;
                break label0;
            }
        } while(true);

//        nodelist.nodes.trimToSize();
        return new Name(nodetoken, nodelist);
    }

    public static final ParseException generateParseException()
    {
        jj_expentries.clear();
        boolean aflag[] = new boolean[29];
        int i = 0;
        do
            aflag[i] = false;
        while(++i < 29);
        if(jj_kind >= 0)
        {
            aflag[jj_kind] = true;
            jj_kind = -1;
        }
        i = 0;
        do
            if(jj_la1[i] == jj_gen)
            {
                int j = 0;
                do
                    if((jj_la1_0[i] & 1 << j) != 0)
                        aflag[j] = true;
                while(++j < 32);
            }
        while(++i < 4);
        i = 0;
        do
            if(aflag[i])
            {
                jj_expentry = new int[1];
                jj_expentry[0] = i;
                jj_expentries.add(jj_expentry);
            }
        while(++i < 29);
        jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);
        int ai[][] = new int[jj_expentries.size()][];
        for(int k = 0; k < jj_expentries.size(); k++)
            ai[k] = (int[])jj_expentries.get(k);

        return new ParseException(token, ai, MessageParserConstants.tokenImage);
    }

    private static final boolean jj_3_1()
    {
        if(jj_3R_3())
            return true;
        return jj_la != 0 || jj_scanpos != jj_lastpos ? false : false;
    }

    public static final Token getNextToken()
    {
        if(token.next != null)
            token = token.next;
        else
            token = token.next = MessageParserTokenManager.getNextToken();
        jj_ntk = -1;
        jj_gen++;
        return token;
    }

    public static final ColumnLiteral ColumnLiteral()
        throws ParseException
    {
        Token token1 = jj_consume_token(28);
        com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken = JTBToolkit.makeNodeToken(token1);
        Token token2 = jj_consume_token(14);
        com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken1 = JTBToolkit.makeNodeToken(token2);
        Token token3 = jj_consume_token(16);
        com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken2 = JTBToolkit.makeNodeToken(token3);
        Token token4 = jj_consume_token(28);
        com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken nodetoken3 = JTBToolkit.makeNodeToken(token4);
        return new ColumnLiteral(nodetoken, nodetoken1, nodetoken2, nodetoken3);
    }

    private static final int jj_ntk()
    {
        if((jj_nt = token.next) == null)
            return jj_ntk = (token.next = MessageParserTokenManager.getNextToken()).kind;
        else
            return jj_ntk = jj_nt.kind;
    }

    private static final boolean jj_2_2(int i)
    {
        jj_la = i;
        jj_lastpos = jj_scanpos = token;
        boolean flag = !jj_3_2();
        jj_save(1, i);
        return flag;
    }

    public static synchronized MessageParser getInstance(Reader reader)
    {
        if(mSingleton == null)
            mSingleton = new MessageParser(reader);
        else
            ReInit(reader);
        return mSingleton;
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
    private static final int jj_la1[] = new int[4];
    private static final int jj_la1_0[] = {
        0x8000000, 0x19d10000, 0xd10000, 0x19000000
    };
    private static final JJCalls jj_2_rtns[] = new JJCalls[3];
    private static boolean jj_rescan = false;
    private static int jj_gc = 0;
    private static List jj_expentries = new java.util.ArrayList();
    private static int jj_expentry[];
    private static int jj_kind = -1;
    private static int jj_lasttokens[] = new int[100];
    private static int jj_endpos;

}