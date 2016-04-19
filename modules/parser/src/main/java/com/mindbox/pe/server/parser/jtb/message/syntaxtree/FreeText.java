//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.message.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> &lt;VALID_FREE_CHAR&gt;
 * </PRE>
 */
public class FreeText implements Node {
   private Node parent;
   public NodeToken f0;

   public FreeText(NodeToken n0) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
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

