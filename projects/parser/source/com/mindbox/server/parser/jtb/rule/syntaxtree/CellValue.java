package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            NodeToken, Node

public class CellValue implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public CellValue(NodeToken nodetoken, NodeToken nodetoken1, NodeToken nodetoken2) {
		nodeToken = nodetoken;
		if (nodeToken != null)
			nodeToken.setParent(this);
		nodeToken1 = nodetoken1;
		if (nodeToken1 != null)
			nodeToken1.setParent(this);
		nodeToken2 = nodetoken2;
		if (nodeToken2 != null)
			nodeToken2.setParent(this);
	}

	public CellValue() {
		nodeToken = new NodeToken("%");
		if (nodeToken != null)
			nodeToken.setParent(this);
		nodeToken1 = new NodeToken("cellValue");
		if (nodeToken1 != null)
			nodeToken1.setParent(this);
		nodeToken2 = new NodeToken("%");
		if (nodeToken2 != null)
			nodeToken2.setParent(this);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}

	private Node parent;
	public NodeToken nodeToken;
	public NodeToken nodeToken1;
	public NodeToken nodeToken2;
}