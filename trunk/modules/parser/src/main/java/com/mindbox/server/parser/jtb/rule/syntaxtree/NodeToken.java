package com.mindbox.server.parser.jtb.rule.syntaxtree;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            Node

public class NodeToken implements Node {

	public String toString() {
		return tokenImage;
	}

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public NodeToken(String s) {
		this(s, -1, -1, -1, -1, -1);
	}

	public NodeToken(String s, int i, int j, int k, int l, int i1) {
		tokenImage = s;
		specialTokens = null;
		kind = i;
		beginLine = j;
		beginColumn = k;
		endLine = l;
		endColumn = i1;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}

	public NodeToken getSpecialAt(int i) {
		if (specialTokens == null)
			throw new NoSuchElementException("No specials in token");
		else
			return (NodeToken) specialTokens.get(i);
	}

	public String withSpecials() {
		if (specialTokens == null)
			return tokenImage;
		StringBuffer stringbuffer = new StringBuffer();
		for (Iterator iter = specialTokens.iterator(); iter.hasNext(); stringbuffer.append(iter.next().toString()));
		stringbuffer.append(tokenImage);
		return stringbuffer.toString();
	}

	public int numSpecials() {
		if (specialTokens == null)
			return 0;
		else
			return specialTokens.size();
	}

	public void trimSpecials() {
		if (specialTokens == null) {
			return;
		}
		else {
			//            specialTokens.trimToSize();
			return;
		}
	}

	public void addSpecial(NodeToken nodetoken) {
		if (specialTokens == null)
			specialTokens = new java.util.ArrayList();
		specialTokens.add(nodetoken);
		nodetoken.setParent(this);
	}

	private Node parent;
	public String tokenImage;
	public List specialTokens;
	public int beginLine;
	public int beginColumn;
	public int endLine;
	public int endColumn;
	public int kind;
}