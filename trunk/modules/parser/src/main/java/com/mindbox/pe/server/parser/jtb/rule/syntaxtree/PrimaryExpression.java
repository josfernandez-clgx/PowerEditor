//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.rule.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> LiteralExpression()
 *       | LiteralList()
 *       | Reference()
 *       | "(" OrExpression() ")"
 * </PRE>
 */
public class PrimaryExpression implements Node {
   private Node parent;
   public NodeChoice f0;

   public PrimaryExpression(NodeChoice n0) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
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

