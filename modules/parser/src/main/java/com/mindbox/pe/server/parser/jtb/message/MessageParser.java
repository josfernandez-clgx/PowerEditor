/* Generated By:JavaCC: Do not edit this line. MessageParser.java */
package com.mindbox.pe.server.parser.jtb.message;

import com.mindbox.pe.server.parser.jtb.message.syntaxtree.CellValueLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnMessagesLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnNumberList;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.FreeText;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeChoice;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeListOptional;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeSequence;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeToken;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Reference;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.RuleNameLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.SingleFreeChar;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Word;


public final class MessageParser implements MessageParserConstants {

  final public Message Message() throws ParseException {
    trace_call("Message");
    try {
   NodeListOptional n0 = new NodeListOptional();
   Word n1;
   NodeToken n2;
   Token n3;

   {
   }
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case INTEGER_LITERAL:
        case PERCENT:
        case VBAR:
        case PERIOD:
        case LPARAN:
        case RPARAN:
        case COMMA:
        case IDENTIFIER:
        case VALID_FREE_CHAR:
        case 24:
        case 25:
        case 26:
          ;
          break;
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        n1 = Word();
        n0.addNode(n1);
      }
     n0.nodes.trimToSize();
      n3 = jj_consume_token(0);
      n3.beginColumn++; n3.endColumn++;
      n2 = JTBToolkit.makeNodeToken(n3);
     {if (true) return new Message(n0,n2);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Message");
    }
  }

  final public Word Word() throws ParseException {
    trace_call("Word");
    try {
   NodeChoice n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   ColumnLiteral n5;
   CellValueLiteral n6;
   RuleNameLiteral n7;
   ColumnMessagesLiteral n8;
   Reference n9;
   FreeText n10;
   SingleFreeChar n11;

   {
   }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER_LITERAL:
        n2 = jj_consume_token(INTEGER_LITERAL);
                             n1 = JTBToolkit.makeNodeToken(n2);
        n0 = new NodeChoice(n1, 0);
        break;
      case IDENTIFIER:
        n4 = jj_consume_token(IDENTIFIER);
                        n3 = JTBToolkit.makeNodeToken(n4);
        n0 = new NodeChoice(n3, 1);
        break;
      default:
        jj_la1[1] = jj_gen;
        if (jj_2_1(2)) {
          n5 = ColumnLiteral();
        n0 = new NodeChoice(n5, 2);
        } else if (jj_2_2(2)) {
          n6 = CellValueLiteral();
        n0 = new NodeChoice(n6, 3);
        } else if (jj_2_3(2)) {
          n7 = RuleNameLiteral();
        n0 = new NodeChoice(n7, 4);
        } else if (jj_2_4(2)) {
          n8 = ColumnMessagesLiteral();
        n0 = new NodeChoice(n8, 5);
        } else if (jj_2_5(2)) {
          n9 = Reference();
        n0 = new NodeChoice(n9, 6);
        } else {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case VALID_FREE_CHAR:
            n10 = FreeText();
        n0 = new NodeChoice(n10, 7);
            break;
          case PERCENT:
          case VBAR:
          case PERIOD:
          case LPARAN:
          case RPARAN:
          case COMMA:
            n11 = SingleFreeChar();
        n0 = new NodeChoice(n11, 8);
            break;
          default:
            jj_la1[2] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
      }
     {if (true) return new Word(n0);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Word");
    }
  }

  final public ColumnLiteral ColumnLiteral() throws ParseException {
    trace_call("ColumnLiteral");
    try {
   NodeChoice n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;

   NodeToken n7;
   Token n8;

   {
   }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 24:
        n2 = jj_consume_token(24);
                        n1 = JTBToolkit.makeNodeToken(n2);
           n0 = new NodeChoice(n1, 0);
        break;
      case 25:
        n4 = jj_consume_token(25);
                         n3 = JTBToolkit.makeNodeToken(n4);
           n0 = new NodeChoice(n3, 1);
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      n6 = jj_consume_token(INTEGER_LITERAL);
                          n5 = JTBToolkit.makeNodeToken(n6);
      n8 = jj_consume_token(PERCENT);
            n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new ColumnLiteral(n0,n5,n7);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ColumnLiteral");
    }
  }

  final public CellValueLiteral CellValueLiteral() throws ParseException {
    trace_call("CellValueLiteral");
    try {
   NodeToken n0;
   Token n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;

   {
   }
      n1 = jj_consume_token(PERCENT);
            n0 = JTBToolkit.makeNodeToken(n1);
      n3 = jj_consume_token(CELLVALUE);
                    n2 = JTBToolkit.makeNodeToken(n3);
      n5 = jj_consume_token(PERCENT);
            n4 = JTBToolkit.makeNodeToken(n5);
     {if (true) return new CellValueLiteral(n0,n2,n4);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("CellValueLiteral");
    }
  }

  final public RuleNameLiteral RuleNameLiteral() throws ParseException {
    trace_call("RuleNameLiteral");
    try {
   NodeToken n0;
   Token n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;

   {
   }
      n1 = jj_consume_token(PERCENT);
            n0 = JTBToolkit.makeNodeToken(n1);
      n3 = jj_consume_token(RULENAME);
                   n2 = JTBToolkit.makeNodeToken(n3);
      n5 = jj_consume_token(PERCENT);
            n4 = JTBToolkit.makeNodeToken(n5);
     {if (true) return new RuleNameLiteral(n0,n2,n4);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("RuleNameLiteral");
    }
  }

  final public Reference Reference() throws ParseException {
    trace_call("Reference");
    try {
   NodeToken n0;
   Token n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;

   {
   }
      n1 = jj_consume_token(VBAR);
            n0 = JTBToolkit.makeNodeToken(n1);
      n3 = jj_consume_token(IDENTIFIER);
                     n2 = JTBToolkit.makeNodeToken(n3);
      n5 = jj_consume_token(VBAR);
            n4 = JTBToolkit.makeNodeToken(n5);
     {if (true) return new Reference(n0,n2,n4);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Reference");
    }
  }

  final public FreeText FreeText() throws ParseException {
    trace_call("FreeText");
    try {
   NodeToken n0;
   Token n1;

   {
   }
      n1 = jj_consume_token(VALID_FREE_CHAR);
                          n0 = JTBToolkit.makeNodeToken(n1);
     {if (true) return new FreeText(n0);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("FreeText");
    }
  }

  final public SingleFreeChar SingleFreeChar() throws ParseException {
    trace_call("SingleFreeChar");
    try {
   NodeChoice n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeToken n9;
   Token n10;
   NodeToken n11;
   Token n12;

   {
   }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PERIOD:
        n2 = jj_consume_token(PERIOD);
               n1 = JTBToolkit.makeNodeToken(n2);
        n0 = new NodeChoice(n1, 0);
        break;
      case PERCENT:
        n4 = jj_consume_token(PERCENT);
               n3 = JTBToolkit.makeNodeToken(n4);
        n0 = new NodeChoice(n3, 1);
        break;
      case COMMA:
        n6 = jj_consume_token(COMMA);
               n5 = JTBToolkit.makeNodeToken(n6);
        n0 = new NodeChoice(n5, 2);
        break;
      case LPARAN:
        n8 = jj_consume_token(LPARAN);
               n7 = JTBToolkit.makeNodeToken(n8);
        n0 = new NodeChoice(n7, 3);
        break;
      case RPARAN:
        n10 = jj_consume_token(RPARAN);
                n9 = JTBToolkit.makeNodeToken(n10);
        n0 = new NodeChoice(n9, 4);
        break;
      case VBAR:
        n12 = jj_consume_token(VBAR);
                n11 = JTBToolkit.makeNodeToken(n12);
        n0 = new NodeChoice(n11, 5);
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
     {if (true) return new SingleFreeChar(n0);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("SingleFreeChar");
    }
  }

  final public ColumnMessagesLiteral ColumnMessagesLiteral() throws ParseException {
    trace_call("ColumnMessagesLiteral");
    try {
   NodeToken n0;
   Token n1;
   NodeToken n2;
   Token n3;
   ColumnNumberList n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;

   {
   }
      n1 = jj_consume_token(26);
                          n0 = JTBToolkit.makeNodeToken(n1);
      n3 = jj_consume_token(LPARAN);
            n2 = JTBToolkit.makeNodeToken(n3);
      n4 = ColumnNumberList();
      n6 = jj_consume_token(RPARAN);
            n5 = JTBToolkit.makeNodeToken(n6);
      n8 = jj_consume_token(PERCENT);
            n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new ColumnMessagesLiteral(n0,n2,n4,n5,n7);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ColumnMessagesLiteral");
    }
  }

  final public ColumnNumberList ColumnNumberList() throws ParseException {
    trace_call("ColumnNumberList");
    try {
   NodeToken n0;
   Token n1;
   NodeListOptional n2 = new NodeListOptional();
   NodeSequence n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;

   {
   }
      n1 = jj_consume_token(INTEGER_LITERAL);
                          n0 = JTBToolkit.makeNodeToken(n1);
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[5] = jj_gen;
          break label_2;
        }
        n3 = new NodeSequence(2);
        n5 = jj_consume_token(COMMA);
               n4 = JTBToolkit.makeNodeToken(n5);
        n3.addNode(n4);
        n7 = jj_consume_token(INTEGER_LITERAL);
                             n6 = JTBToolkit.makeNodeToken(n7);
        n3.addNode(n6);
        n2.addNode(n3);
      }
     n2.nodes.trimToSize();
     {if (true) return new ColumnNumberList(n0,n2);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ColumnNumberList");
    }
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_1();
    jj_save(0, xla);
    return retval;
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_2();
    jj_save(1, xla);
    return retval;
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_3();
    jj_save(2, xla);
    return retval;
  }

  final private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_4();
    jj_save(3, xla);
    return retval;
  }

  final private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_5();
    jj_save(4, xla);
    return retval;
  }

  final private boolean jj_3_2() {
    if (jj_3R_4()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_4() {
    if (jj_scan_token(PERCENT)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(CELLVALUE)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_3R_3()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_5() {
    if (jj_scan_token(PERCENT)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(RULENAME)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_7() {
    if (jj_scan_token(VBAR)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(IDENTIFIER)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_3() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_8()) {
    jj_scanpos = xsp;
    if (jj_3R_9()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    } else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(INTEGER_LITERAL)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_5() {
    if (jj_3R_7()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_4() {
    if (jj_3R_6()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_9() {
    if (jj_scan_token(25)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_6() {
    if (jj_scan_token(26)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(LPARAN)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_3R_5()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_8() {
    if (jj_scan_token(24)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  public MessageParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[6];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x787f200,0x40200,0x83f000,0x3000000,0x3f000,0x20000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[5];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public MessageParser(java.io.InputStream stream) {
    trace_enabled = false;
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new MessageParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public MessageParser(java.io.Reader stream) {
    trace_enabled = false;
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new MessageParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public MessageParser(MessageParserTokenManager tm) {
    trace_enabled = false;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(MessageParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      trace_token(token, "");
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    return (jj_scanpos.kind != kind);
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
      trace_token(token, " (in getNextToken)");
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration enumeration = jj_expentries.elements(); enumeration.hasMoreElements();) {
        int[] oldentry = (int[])(enumeration.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[27];
    for (int i = 0; i < 27; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 6; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 27; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  private int trace_indent = 0;
  private boolean trace_enabled = false;

  final public void enable_tracing() {
    trace_enabled = true;
  }

  final public void disable_tracing() {
    trace_enabled = false;
  }

  final private void trace_call(String s) {
    if (trace_enabled) {
      //for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
    }
    trace_indent = trace_indent + 2;
  }

  final private void trace_return(String s) {
    trace_indent = trace_indent - 2;
    if (trace_enabled) {
      //for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
    }
  }

  final private void trace_token(Token t, String where) {
    if (trace_enabled) {
     // for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      if (t.kind != 0 && !tokenImage[t.kind].equals("\"" + t.image + "\"")) {
      }
    }
  }

  final private void trace_scan(Token t1, int t2) {
    if (trace_enabled) {
      //for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      if (t1.kind != 0 && !tokenImage[t1.kind].equals("\"" + t1.image + "\"")) {
      }
    }
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 5; i++) {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
          }
        }
        p = p.next;
      } while (p != null);
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}

class JTBToolkit {
   static NodeToken makeNodeToken(Token t) {
      return new NodeToken(t.image.intern(), t.kind, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
   }
}
