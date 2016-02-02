package com.mindbox.server.parser.jtb.msg.syntaxtree;

import com.mindbox.server.parser.jtb.msg.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.msg.visitor.Visitor;

public class Message implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public Message(NodeList nodelist, NodeToken nodetoken) {
		nodeList = nodelist;
		if (nodeList != null)
			nodeList.setParent(this);
		nodeToken = nodetoken;
		if (nodeToken != null)
			nodeToken.setParent(this);
	}

	public Message(NodeList nodelist) {
		nodeList = nodelist;
		if (nodeList != null)
			nodeList.setParent(this);
		nodeToken = new NodeToken("");
		if (nodeToken != null)
			nodeToken.setParent(this);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}

	private Node parent;
	public NodeList nodeList;
	public NodeToken nodeToken;
}