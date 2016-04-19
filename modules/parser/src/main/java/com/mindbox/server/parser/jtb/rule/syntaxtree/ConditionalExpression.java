package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            ConditionalAndExpression, NodeListOptional, Node

public class ConditionalExpression implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public ConditionalExpression(ConditionalAndExpression conditionalandexpression, NodeListOptional nodelistoptional) {
		conditionalAndExpression = conditionalandexpression;
		if (conditionalAndExpression != null)
			conditionalAndExpression.setParent(this);
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
		return "CondExpression[andexp="+conditionalAndExpression+",optional="+nodeListOptional+"]";
	}
	private Node parent;
	public ConditionalAndExpression conditionalAndExpression;
	public NodeListOptional nodeListOptional;
}