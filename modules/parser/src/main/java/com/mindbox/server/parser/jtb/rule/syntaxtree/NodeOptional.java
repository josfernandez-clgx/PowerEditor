package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            Node

public class NodeOptional implements Node {

	public void setParent(Node node1) {
		parent = node1;
	}

	public Node getParent() {
		return parent;
	}

	public NodeOptional() {
		node = null;
	}

	public NodeOptional(Node node1) {
		addNode(node1);
	}

	public boolean present() {
		return node != null;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}

	public void addNode(Node node1) {
		if (node != null) {
			throw new Error("Attempt to set optional node twice");
		}
		else {
			node = node1;
			node1.setParent(this);
			return;
		}
	}

	public String toString() {
		return "NodeOptional[node="+node+"]";
	}
	
	private Node parent;
	public Node node;
}