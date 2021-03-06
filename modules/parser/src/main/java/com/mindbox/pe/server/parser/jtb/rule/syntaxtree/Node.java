//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.rule.syntaxtree;

/**
 * The interface which all syntax tree classes must implement.
 */
public interface Node extends java.io.Serializable {
   public void accept(com.mindbox.pe.server.parser.jtb.rule.visitor.Visitor v);
   public Object accept(com.mindbox.pe.server.parser.jtb.rule.visitor.ObjectVisitor v, Object argu);

   // It is the responsibility of each implementing class to call
   // setParent() on each of its child Nodes.
   public void setParent(Node n);
   public Node getParent();
}

