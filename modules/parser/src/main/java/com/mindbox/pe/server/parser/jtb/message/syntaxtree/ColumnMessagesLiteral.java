//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.message.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> "%columnMessages"
 * f1 -> "("
 * f2 -> ColumnNumberList()
 * f3 -> ")"
 * f4 -> "%"
 * </PRE>
 */
public class ColumnMessagesLiteral implements Node {
   private Node parent;
   public NodeToken f0;
   public NodeToken f1;
   public ColumnNumberList f2;
   public NodeToken f3;
   public NodeToken f4;

   public ColumnMessagesLiteral(NodeToken n0, NodeToken n1, ColumnNumberList n2, NodeToken n3, NodeToken n4) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
      f1 = n1;
      if ( f1 != null ) f1.setParent(this);
      f2 = n2;
      if ( f2 != null ) f2.setParent(this);
      f3 = n3;
      if ( f3 != null ) f3.setParent(this);
      f4 = n4;
      if ( f4 != null ) f4.setParent(this);
   }

   public ColumnMessagesLiteral(ColumnNumberList n0) {
      f0 = new NodeToken("%columnMessages");
      if ( f0 != null ) f0.setParent(this);
      f1 = new NodeToken("(");
      if ( f1 != null ) f1.setParent(this);
      f2 = n0;
      if ( f2 != null ) f2.setParent(this);
      f3 = new NodeToken(")");
      if ( f3 != null ) f3.setParent(this);
      f4 = new NodeToken("%");
      if ( f4 != null ) f4.setParent(this);
   }

   public void accept(com.mindbox.pe.server.parser.jtb.message.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.mindbox.pe.server.parser.jtb.message.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
   public void setParent(Node n) { parent = n; }
   public Node getParent()       { return parent; }
}
