// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:01:12 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   NodeSequence.java

package com.mindbox.server.parser.jtb.msg.syntaxtree;

import com.mindbox.server.parser.jtb.msg.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.msg.visitor.Visitor;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.mindbox.server.parser.jtb.msg.syntaxtree:
//            Node, NodeListInterface

public class NodeSequence implements NodeListInterface {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public NodeSequence(int i) {
		nodes = new java.util.ArrayList(i);
	}

	public NodeSequence(Node node) {
		nodes = new java.util.ArrayList();
		addNode(node);
	}

	public Iterator iterator() {
		return nodes.iterator();
	}

	public int size() {
		return nodes.size();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}

	public Node elementAt(int i) {
		return (Node) nodes.get(i);
	}

	public void addNode(Node node) {
		nodes.add(node);
		node.setParent(this);
	}

	private Node parent;
	public List nodes;
}