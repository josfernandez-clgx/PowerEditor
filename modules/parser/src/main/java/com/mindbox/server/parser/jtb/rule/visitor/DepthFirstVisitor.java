// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:48 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DepthFirstVisitor.java

package com.mindbox.server.parser.jtb.rule.visitor;

import com.mindbox.server.parser.jtb.rule.syntaxtree.*;
import java.util.Iterator;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.visitor:
//            Visitor

public class DepthFirstVisitor implements Visitor {

	public void visit(InstanceName instancename) {
		instancename.name.accept(this);
	}

	public void visit(ConditionalExpression conditionalexpression) {
		conditionalexpression.conditionalAndExpression.accept(this);
		conditionalexpression.nodeListOptional.accept(this);
	}

	public void visit(ConditionalAndExpression conditionalandexpression) {
		conditionalandexpression.relationalExpression.accept(this);
		conditionalandexpression.nodeListOptional.accept(this);
	}

	public void visit(RelationalExpression relationalexpression) {
		relationalexpression.nodeChoice.accept(this);
	}

	public void visit(AdditiveExpression additiveexpression) {
		additiveexpression.multiplicativeExpression.accept(this);
		additiveexpression.nodeListOptional.accept(this);
	}

	public void visit(MultiplicativeExpression multiplicativeexpression) {
		multiplicativeexpression.unaryExpression.accept(this);
		multiplicativeexpression.nodeListOptional.accept(this);
	}

	public void visit(UnaryExpression unaryexpression) {
		unaryexpression.nodeChoice.accept(this);
	}

	public void visit(PrimaryExpression primaryexpression) {
		primaryexpression.nodeChoice.accept(this);
	}

	public void visit(LiteralList literallist) {
		literallist.nodeToken.accept(this);
		literallist.literal.accept(this);
		literallist.nodeListOptional.accept(this);
		literallist.nodeToken1.accept(this);
	}

	public void visit(Literal literal) {
		literal.nodeChoice.accept(this);
	}

	public void visit(BooleanLiteral booleanliteral) {
		booleanliteral.nodeChoice.accept(this);
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

	public void visit(Arguments arguments) {
		arguments.nodeToken.accept(this);
		arguments.nodeOptional.accept(this);
		arguments.nodeToken1.accept(this);
	}

	public void visit(ArgumentList argumentlist) {
		argumentlist.additiveExpression.accept(this);
		argumentlist.nodeListOptional.accept(this);
	}

	public DepthFirstVisitor() {
	}

	public void visit(NodeList nodelist) {
		for (Iterator iter = nodelist.iterator(); iter.hasNext();((Node) iter.next()).accept(this));
	}

	public void visit(NodeListOptional nodelistoptional) {
		if (nodelistoptional.present()) {
			for (Iterator iter = nodelistoptional.iterator();
				iter.hasNext();
				((Node) iter.next()).accept(this));
		}
	}

	public void visit(NodeOptional nodeoptional) {
		if (nodeoptional.present())
			nodeoptional.node.accept(this);
	}

	public void visit(NodeSequence nodesequence) {
		for (Iterator iter = nodesequence.iterator();
			iter.hasNext();
			((Node) iter.next()).accept(this));
	}

	public void visit(NodeToken nodetoken) {
	}

	public void visit(DeploymentRuleList deploymentrulelist) {
		deploymentrulelist.nodeListOptional.accept(this);
	}

	public void visit(DeploymentRule deploymentrule) {
		deploymentrule.nodeToken.accept(this);
		deploymentrule.conditionalExpression.accept(this);
		deploymentrule.nodeToken1.accept(this);
		deploymentrule.action.accept(this);
		deploymentrule.nodeOptional.accept(this);
	}

	public void visit(ObjectCondition objectcondition) {
		objectcondition.nodeChoice.accept(this);
		objectcondition.className.accept(this);
		objectcondition.nodeOptional.accept(this);
		objectcondition.nodeOptional1.accept(this);
		objectcondition.nodeToken.accept(this);
		objectcondition.relationalExpression.accept(this);
	}

	public void visit(Action action) {
		action.name.accept(this);
		action.arguments.accept(this);
	}

	public void visit(Function function) {
		function.name.accept(this);
		function.arguments.accept(this);
	}

	public void visit(Name name) {
		name.nodeToken.accept(this);
		name.nodeListOptional.accept(this);
	}

	public void visit(ClassName classname) {
		classname.nodeToken.accept(this);
	}
	public void visit(Message n) {
		n.nodeList.accept(this);
		n.nodeToken.accept(this);
	}

	public void visit(Word n) {
		n.nodeChoice.accept(this);
	}
}