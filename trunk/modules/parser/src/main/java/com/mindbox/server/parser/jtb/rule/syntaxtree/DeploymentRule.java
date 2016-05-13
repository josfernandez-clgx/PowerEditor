package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            NodeToken, ConditionalExpression, Action, NodeOptional, 
//            Node

public class DeploymentRule implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public DeploymentRule(
		NodeToken nodetoken,
		ConditionalExpression conditionalexpression,
		NodeToken nodetoken1,
		Action action1,
		NodeOptional nodeoptional) {

		nodeToken = nodetoken;
		if (nodeToken != null)
			nodeToken.setParent(this);
		conditionalExpression = conditionalexpression;
		if (conditionalExpression != null)
			conditionalExpression.setParent(this);
		nodeToken1 = nodetoken1;
		if (nodeToken1 != null)
			nodeToken1.setParent(this);
		action = action1;
		if (action != null)
			action.setParent(this);
		nodeOptional = nodeoptional;
		if (nodeOptional != null)
			nodeOptional.setParent(this);
	}

	public DeploymentRule(ConditionalExpression conditionalexpression, Action action1, NodeOptional nodeoptional) {
		nodeToken = new NodeToken("if");
		if (nodeToken != null)
			nodeToken.setParent(this);
		conditionalExpression = conditionalexpression;
		if (conditionalExpression != null)
			conditionalExpression.setParent(this);
		nodeToken1 = new NodeToken("then");
		if (nodeToken1 != null)
			nodeToken1.setParent(this);
		action = action1;
		if (action != null)
			action.setParent(this);
		this.nodeOptional = nodeoptional;
		if (this.nodeOptional != null)
			this.nodeOptional.setParent(this);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}
	
	public String toString() {
		return "DeploymentRule[token="+nodeToken+",exp="+conditionalExpression+"]";
	}
	
	private Node parent;
	public NodeToken nodeToken;
	public ConditionalExpression conditionalExpression;
	public NodeToken nodeToken1;
	public Action action;
	public NodeOptional nodeOptional;
}