package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            NodeToken, NodeListOptional, Node

public class Name implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public Name(NodeToken nodetoken, NodeListOptional nodelistoptional) {
		nodeToken = nodetoken;
		if (nodeToken != null)
			nodeToken.setParent(this);
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
		return "Name[token="+nodeToken+",optional="+nodeListOptional+"]";
	}
	
	private Node parent;
	public NodeToken nodeToken;
	public NodeListOptional nodeListOptional;
}