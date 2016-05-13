//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.message.syntaxtree;

import java.util.*;

/**
 * Represents a sequence of nodes nested within a choice, list,
 * optional list, or optional, e.g. ( A B )+ or [ C D E ]
 */
public class NodeSequence implements NodeListInterface {
   public NodeSequence(int n) {
      nodes = new Vector(n);
   }

   public NodeSequence(Node firstNode) {
      nodes = new Vector();
      addNode(firstNode);
   }

   public void addNode(Node n) {
      nodes.addElement(n);
      n.setParent(this);
   }

   public Node elementAt(int i)  { return (Node)nodes.elementAt(i); }
   public Enumeration elements() { return nodes.elements(); }
   public int size()             { return nodes.size(); }
   public void accept(com.mindbox.pe.server.parser.jtb.message.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.mindbox.pe.server.parser.jtb.message.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }

   public void setParent(Node n) { parent = n; }
   public Node getParent()       { return parent; }

   private Node parent;
   public Vector nodes;
}

