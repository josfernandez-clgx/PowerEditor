//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.message.visitor;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.CellValueLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnMessagesLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnNumberList;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.FreeText;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeList;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeListOptional;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeOptional;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeSequence;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeToken;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Reference;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.RuleNameLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.SingleFreeChar;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Word;

/**
 * All void visitors must implement this interface.
 */
public interface Visitor {
   //
   // void Auto class visitors
   //
   public void visit(NodeList n);
   public void visit(NodeListOptional n);
   public void visit(NodeOptional n);
   public void visit(NodeSequence n);
   public void visit(NodeToken n);

   //
   // User-generated visitor methods below
   //

   /**
    * <PRE>
    * f0 -> ( Word() )*
    * f1 -> &lt;EOF&gt;
    * </PRE>
    */
   public void visit(Message n);

   /**
    * <PRE>
    * f0 -> &lt;INTEGER_LITERAL&gt;
    *       | &lt;IDENTIFIER&gt;
    *       | ColumnLiteral()
    *       | CellValueLiteral()
    *       | RuleNameLiteral()
    *       | ColumnMessagesLiteral()
    *       | Reference()
    *       | FreeText()
    *       | SingleFreeChar()
    * </PRE>
    */
   public void visit(Word n);

   /**
    * <PRE>
    * f0 -> ( "%column" | "%column " )
    * f1 -> &lt;INTEGER_LITERAL&gt;
    * f2 -> "%"
    * </PRE>
    */
   public void visit(ColumnLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "cellValue"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(CellValueLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "ruleName"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(RuleNameLiteral n);

   /**
    * <PRE>
    * f0 -> "|"
    * f1 -> &lt;IDENTIFIER&gt;
    * f2 -> "|"
    * </PRE>
    */
   public void visit(Reference n);

   /**
    * <PRE>
    * f0 -> &lt;VALID_FREE_CHAR&gt;
    * </PRE>
    */
   public void visit(FreeText n);

   /**
    * <PRE>
    * f0 -> "."
    *       | "%"
    *       | ","
    *       | "("
    *       | ")"
    *       | "|"
    * </PRE>
    */
   public void visit(SingleFreeChar n);

   /**
    * <PRE>
    * f0 -> "%columnMessages"
    * f1 -> "("
    * f2 -> ColumnNumberList()
    * f3 -> ")"
    * f4 -> "%"
    * </PRE>
    */
   public void visit(ColumnMessagesLiteral n);

   /**
    * <PRE>
    * f0 -> &lt;INTEGER_LITERAL&gt;
    * f1 -> ( "," &lt;INTEGER_LITERAL&gt; )*
    * </PRE>
    */
   public void visit(ColumnNumberList n);

}

