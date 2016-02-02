package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            Node

public class NodeChoice implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public NodeChoice(Node node) {
		this(node, -1);
	}

	public NodeChoice(Node node, int i) {
		choice = node;
		choice.setParent(this);
		which = i;
	}

	public void accept(Visitor visitor) {
		choice.accept(visitor);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return choice.accept(objectvisitor, obj);
	}

	public String toString() {
		return "NodeChoice["+choice+"]";
	}
	
	private Node parent;
	public Node choice;
	public int which;
}