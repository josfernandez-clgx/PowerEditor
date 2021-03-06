//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.rule.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> AdditiveExpression()
 * f1 -> ( ( ConditionalOperator() | MembershipOperator() ) AdditiveExpression() )*
 * </PRE>
 */
public class ConditionalExpression implements Node {
   private Node parent;
   public AdditiveExpression f0;
   public NodeListOptional f1;

   public ConditionalExpression(AdditiveExpression n0, NodeListOptional n1) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
      f1 = n1;
      if ( f1 != null ) f1.setParent(this);
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

