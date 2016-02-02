// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:13 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   NodeList.java

package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            Node, NodeListInterface

public class NodeList implements NodeListInterface {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public NodeList() {
		nodes = new java.util.ArrayList();
	}

	public NodeList(Node node) {
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