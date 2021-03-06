//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.message.syntaxtree;

/**
 * The interface which all syntax tree classes must implement.
 */
public interface Node extends java.io.Serializable {
   public void accept(com.mindbox.pe.server.parser.jtb.message.visitor.Visitor v);
   public Object accept(com.mindbox.pe.server.parser.jtb.message.visitor.ObjectVisitor v, Object argu);

   // It is the responsibility of each implementing class to call
   // setParent() on each of its child Nodes.
   public void setParent(Node n);
   public Node getParent();
}

