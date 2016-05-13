//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.message.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> "|"
 * f1 -> &lt;IDENTIFIER&gt;
 * f2 -> "|"
 * </PRE>
 */
public class Reference implements Node {
   private Node parent;
   public NodeToken f0;
   public NodeToken f1;
   public NodeToken f2;

   public Reference(NodeToken n0, NodeToken n1, NodeToken n2) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
      f1 = n1;
      if ( f1 != null ) f1.setParent(this);
      f2 = n2;
      if ( f2 != null ) f2.setParent(this);
   }

   public Reference(NodeToken n0) {
      f0 = new NodeToken("|");
      if ( f0 != null ) f0.setParent(this);
      f1 = n0;
      if ( f1 != null ) f1.setParent(this);
      f2 = new NodeToken("|");
      if ( f2 != null ) f2.setParent(this);
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

