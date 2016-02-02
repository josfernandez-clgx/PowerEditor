package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            AdditiveExpression, NodeListOptional, Node

public class ArgumentList implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public ArgumentList(AdditiveExpression additiveexpression, NodeListOptional nodelistoptional) {
		additiveExpression = additiveexpression;
		if (additiveExpression != null)
			additiveExpression.setParent(this);
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
		return "ArgumentList[expression="+additiveExpression+",optional="+nodeListOptional+"]";
	}
	
	private Node parent;
	public AdditiveExpression additiveExpression;
	public NodeListOptional nodeListOptional;
}