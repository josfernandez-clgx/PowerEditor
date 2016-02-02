//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.message.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> ( Word() )*
 * f1 -> &lt;EOF&gt;
 * </PRE>
 */
public class Message implements Node {
   private Node parent;
   public NodeListOptional f0;
   public NodeToken f1;

   public Message(NodeListOptional n0, NodeToken n1) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
      f1 = n1;
      if ( f1 != null ) f1.setParent(this);
   }

   public Message(NodeListOptional n0) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
      f1 = new NodeToken("");
      if ( f1 != null ) f1.setParent(this);
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

