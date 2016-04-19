//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.rule.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> "%"
 * f1 -> "investorID"
 * f2 -> "%"
 * </PRE>
 */
public class InvestorIDLiteral implements Node {
   private Node parent;
   public NodeToken f0;
   public NodeToken f1;
   public NodeToken f2;

   public InvestorIDLiteral(NodeToken n0, NodeToken n1, NodeToken n2) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
      f1 = n1;
      if ( f1 != null ) f1.setParent(this);
      f2 = n2;
      if ( f2 != null ) f2.setParent(this);
   }

   public InvestorIDLiteral() {
      f0 = new NodeToken("%");
      if ( f0 != null ) f0.setParent(this);
      f1 = new NodeToken("investorID");
      if ( f1 != null ) f1.setParent(this);
      f2 = new NodeToken("%");
      if ( f2 != null ) f2.setParent(this);
   }

   public void accept(com.mindbox.pe.server.parser.jtb.rule.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.mindbox.pe.server.parser.jtb.rule.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
   public void setParent(Node n) { parent = n; }
   public Node getParent()       { return parent; }
}

