package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            NodeChoice, Node

public class Literal implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public Literal(NodeChoice nodechoice) {
		nodeChoice = nodechoice;
		if (nodeChoice != null)
			nodeChoice.setParent(this);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}
	
	public String toString() {
		return "Literal["+nodeChoice+"]";
	}
	
	private Node parent;
	public NodeChoice nodeChoice;
}