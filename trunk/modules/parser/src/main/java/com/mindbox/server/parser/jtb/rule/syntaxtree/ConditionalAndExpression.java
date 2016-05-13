package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            RelationalExpression, NodeListOptional, Node

public class ConditionalAndExpression implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public ConditionalAndExpression(RelationalExpression relationalexpression, NodeListOptional nodelistoptional) {
		relationalExpression = relationalexpression;
		if (relationalExpression != null)
			relationalExpression.setParent(this);
		nodeListOptional = nodelistoptional;
		if (nodeListOptional != null)
			nodeListOptional.setParent(this);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}

	public String toString() {
		return "ConditionalAndExpression[" + relationalExpression + "]";
	}

	private Node parent;
	public RelationalExpression relationalExpression;
	public NodeListOptional nodeListOptional;
}