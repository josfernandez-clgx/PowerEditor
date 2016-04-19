// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:01:36 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   ObjectDepthFirst.java

package com.mindbox.server.parser.jtb.msg.visitor;

import com.mindbox.server.parser.jtb.msg.syntaxtree.*;
import java.util.Iterator;

// Referenced classes of package com.mindbox.server.parser.jtb.msg.visitor:
//            ObjectVisitor

public class ObjectDepthFirst implements ObjectVisitor {

	public ObjectDepthFirst() {
	}

	public Object visit(NodeList nodelist, Object obj) {
		Object obj1 = null;
		int i = 0;
		for (Iterator iter = nodelist.iterator(); iter.hasNext();) {
			((Node) iter.next()).accept(this, obj);
			i++;
		}

		return obj1;
	}

	public Object visit(NodeListOptional nodelistoptional, Object obj) {
		if (nodelistoptional.present()) {
			Object obj1 = null;
			int i = 0;
			for (Iterator iter = nodelistoptional.iterator(); iter.hasNext();) {
				((Node) iter.next()).accept(this, obj);
				i++;
			}

			return obj1;
		}
		else {
			return null;
		}
	}

	public Object visit(NodeOptional nodeoptional, Object obj) {
		if (nodeoptional.present())
			return nodeoptional.node.accept(this, obj);
		else
			return null;
	}

	public Object visit(NodeSequence nodesequence, Object obj) {
		Object obj1 = null;
		int i = 0;
		for (Iterator iter = nodesequence.iterator(); iter.hasNext();) {
			((Node) iter.next()).accept(this, obj);
			i++;
		}

		return obj1;
	}

	public Object visit(NodeToken nodetoken, Object obj) {
		return null;
	}

	public Object visit(ColumnLiteral columnliteral, Object obj) {
		Object obj1 = null;
		columnliteral.nodeToken.accept(this, obj);
		columnliteral.nodeToken1.accept(this, obj);
		columnliteral.nodeToken2.accept(this, obj);
		columnliteral.nodeToken3.accept(this, obj);
		return obj1;
	}

	public Object visit(CellValue cellvalue, Object obj) {
		Object obj1 = null;
		cellvalue.nodeToken.accept(this, obj);
		cellvalue.nodeToken1.accept(this, obj);
		cellvalue.nodeToken2.accept(this, obj);
		return obj1;
	}

	public Object visit(RuleName rulename, Object obj) {
		Object obj1 = null;
		rulename.nodeToken.accept(this, obj);
		rulename.nodeToken1.accept(this, obj);
		rulename.nodeToken2.accept(this, obj);
		return obj1;
	}

	public Object visit(Name name, Object obj) {
		Object obj1 = null;
		name.nodeToken.accept(this, obj);
		name.nodeList.accept(this, obj);
		return obj1;
	}

	public Object visit(Message message, Object obj) {
		Object obj1 = null;
		message.nodeList.accept(this, obj);
		message.nodeToken.accept(this, obj);
		return obj1;
	}

	public Object visit(Word word, Object obj) {
		Object obj1 = null;
		word.nodeChoice.accept(this, obj);
		return obj1;
	}
}