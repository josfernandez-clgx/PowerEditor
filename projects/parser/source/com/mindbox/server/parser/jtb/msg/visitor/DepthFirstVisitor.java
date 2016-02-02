package com.mindbox.server.parser.jtb.msg.visitor;

import java.util.Iterator;

import com.mindbox.server.parser.jtb.msg.syntaxtree.CellValue;
import com.mindbox.server.parser.jtb.msg.syntaxtree.ColumnLiteral;
import com.mindbox.server.parser.jtb.msg.syntaxtree.Message;
import com.mindbox.server.parser.jtb.msg.syntaxtree.Name;
import com.mindbox.server.parser.jtb.msg.syntaxtree.Node;
import com.mindbox.server.parser.jtb.msg.syntaxtree.NodeList;
import com.mindbox.server.parser.jtb.msg.syntaxtree.NodeListOptional;
import com.mindbox.server.parser.jtb.msg.syntaxtree.NodeOptional;
import com.mindbox.server.parser.jtb.msg.syntaxtree.NodeSequence;
import com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken;
import com.mindbox.server.parser.jtb.msg.syntaxtree.RuleName;
import com.mindbox.server.parser.jtb.msg.syntaxtree.Word;

// Referenced classes of package com.mindbox.server.parser.jtb.msg.visitor:
//            Visitor

public class DepthFirstVisitor implements Visitor {

	public DepthFirstVisitor() {
	}

	public void visit(NodeList nodelist) {
		for (Iterator iter = nodelist.iterator(); iter.hasNext();((Node) iter.next()).accept(this));
	}

	public void visit(NodeListOptional nodelistoptional) {
		if (nodelistoptional.present()) {
			for (Iterator iter = nodelistoptional.iterator(); iter.hasNext();((Node) iter.next()).accept(this));
		}
	}

	public void visit(NodeOptional nodeoptional) {
		if (nodeoptional.present())
			nodeoptional.node.accept(this);
	}

	public void visit(NodeSequence nodesequence) {
		for (Iterator iter = nodesequence.iterator(); iter.hasNext();((Node) iter.next()).accept(this));
	}

	public void visit(NodeToken nodetoken) {
	}

	public void visit(ColumnLiteral columnliteral) {
		columnliteral.nodeToken.accept(this);
		columnliteral.nodeToken1.accept(this);
		columnliteral.nodeToken2.accept(this);
		columnliteral.nodeToken3.accept(this);
	}

	public void visit(CellValue cellvalue) {
		cellvalue.nodeToken.accept(this);
		cellvalue.nodeToken1.accept(this);
		cellvalue.nodeToken2.accept(this);
	}

	public void visit(RuleName rulename) {
		rulename.nodeToken.accept(this);
		rulename.nodeToken1.accept(this);
		rulename.nodeToken2.accept(this);
	}
	
	public void visit(Name name) {
		name.nodeToken.accept(this);
		name.nodeList.accept(this);
	}

	public void visit(Message message) {
		message.nodeList.accept(this);
		message.nodeToken.accept(this);
	}

	public void visit(Word word) {
		word.nodeChoice.accept(this);
	}
}