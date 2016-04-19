package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            NodeToken, NodeOptional, Node

public class Arguments implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public Arguments(NodeToken nodetoken, NodeOptional nodeoptional, NodeToken nodetoken1) {
		nodeToken = nodetoken;
		if (nodeToken != null)
			nodeToken.setParent(this);
		nodeOptional = nodeoptional;
		if (nodeOptional != null)
			nodeOptional.setParent(this);
		nodeToken1 = nodetoken1;
		if (nodeToken1 != null)
			nodeToken1.setParent(this);
	}

	public Arguments(NodeOptional nodeoptional) {
		nodeToken = new NodeToken("(");
		if (nodeToken != null)
			nodeToken.setParent(this);
		nodeOptional = nodeoptional;
		if (nodeOptional != null)
			nodeOptional.setParent(this);
		nodeToken1 = new NodeToken(")");
		if (nodeToken1 != null)
			nodeToken1.setParent(this);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}

	public String toString() {
		return "Arguments[token="+nodeToken+",token1="+nodeToken1+",optional="+nodeOptional+"]";
	}
	
	private Node parent;
	public NodeToken nodeToken;
	public NodeOptional nodeOptional;
	public NodeToken nodeToken1;
}