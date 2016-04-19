package com.mindbox.server.parser.jtb.rule.visitor;

import java.util.Iterator;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;
import com.mindbox.server.parser.jtb.rule.syntaxtree.AdditiveExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ArgumentList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Arguments;
import com.mindbox.server.parser.jtb.rule.syntaxtree.BooleanLiteral;
import com.mindbox.server.parser.jtb.rule.syntaxtree.CellValue;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ClassName;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ColumnLiteral;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ConditionalAndExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ConditionalExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRule;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRuleList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Function;
import com.mindbox.server.parser.jtb.rule.syntaxtree.InstanceName;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Literal;
import com.mindbox.server.parser.jtb.rule.syntaxtree.LiteralList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Message;
import com.mindbox.server.parser.jtb.rule.syntaxtree.MultiplicativeExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Name;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeListOptional;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeOptional;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeSequence;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ObjectCondition;
import com.mindbox.server.parser.jtb.rule.syntaxtree.PrimaryExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.RelationalExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.RuleName;
import com.mindbox.server.parser.jtb.rule.syntaxtree.UnaryExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Word;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.visitor:
//            ObjectVisitor

public class ObjectDepthFirst implements ObjectVisitor {

	public Object visit(InstanceName instancename, Object obj) {
		Object obj1 = null;
		instancename.name.accept(this, obj);
		return obj1;
	}

	public Object visit(ConditionalExpression conditionalexpression, Object obj) {
		Object obj1 = null;
		conditionalexpression.conditionalAndExpression.accept(this, obj);
		conditionalexpression.nodeListOptional.accept(this, obj);
		return obj1;
	}

	public Object visit(ConditionalAndExpression conditionalandexpression, Object obj) {
		Object obj1 = null;
		conditionalandexpression.relationalExpression.accept(this, obj);
		conditionalandexpression.nodeListOptional.accept(this, obj);
		return obj1;
	}

	public Object visit(RelationalExpression relationalexpression, Object obj) {
		Object obj1 = null;
		relationalexpression.nodeChoice.accept(this, obj);
		return obj1;
	}

	public Object visit(AdditiveExpression additiveexpression, Object obj) {
		Object obj1 = null;
		additiveexpression.multiplicativeExpression.accept(this, obj);
		additiveexpression.nodeListOptional.accept(this, obj);
		return obj1;
	}

	public Object visit(MultiplicativeExpression multiplicativeexpression, Object obj) {
		Object obj1 = null;
		multiplicativeexpression.unaryExpression.accept(this, obj);
		multiplicativeexpression.nodeListOptional.accept(this, obj);
		return obj1;
	}

	public Object visit(UnaryExpression unaryexpression, Object obj) {
		Object obj1 = null;
		unaryexpression.nodeChoice.accept(this, obj);
		return obj1;
	}

	public Object visit(PrimaryExpression primaryexpression, Object obj) {
		Object obj1 = null;
		primaryexpression.nodeChoice.accept(this, obj);
		return obj1;
	}

	public Object visit(LiteralList literallist, Object obj) {
		Object obj1 = null;
		literallist.nodeToken.accept(this, obj);
		literallist.literal.accept(this, obj);
		literallist.nodeListOptional.accept(this, obj);
		literallist.nodeToken1.accept(this, obj);
		return obj1;
	}

	public Object visit(Literal literal, Object obj) {
		Object obj1 = null;
		literal.nodeChoice.accept(this, obj);
		return obj1;
	}

	public Object visit(BooleanLiteral booleanliteral, Object obj) {
		Object obj1 = null;
		booleanliteral.nodeChoice.accept(this, obj);
		return obj1;
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
	
 	public Object visit(Arguments arguments, Object obj) {
		Object obj1 = null;
		arguments.nodeToken.accept(this, obj);
		arguments.nodeOptional.accept(this, obj);
		arguments.nodeToken1.accept(this, obj);
		return obj1;
	}

	public Object visit(ArgumentList argumentlist, Object obj) {
		Object obj1 = null;
		argumentlist.additiveExpression.accept(this, obj);
		argumentlist.nodeListOptional.accept(this, obj);
		return obj1;
	}

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

	public Object visit(DeploymentRuleList deploymentrulelist, Object obj) {
		Object obj1 = null;
		deploymentrulelist.nodeListOptional.accept(this, obj);
		return obj1;
	}

	public Object visit(DeploymentRule deploymentrule, Object obj) {
		Object obj1 = null;
		deploymentrule.nodeToken.accept(this, obj);
		deploymentrule.conditionalExpression.accept(this, obj);
		deploymentrule.nodeToken1.accept(this, obj);
		deploymentrule.action.accept(this, obj);
		deploymentrule.nodeOptional.accept(this, obj);
		return obj1;
	}

	public Object visit(ObjectCondition objectcondition, Object obj) {
		Object obj1 = null;
		objectcondition.nodeChoice.accept(this, obj);
		objectcondition.className.accept(this, obj);
		objectcondition.nodeOptional.accept(this, obj);
		objectcondition.nodeOptional1.accept(this, obj);
		objectcondition.nodeToken.accept(this, obj);
		objectcondition.relationalExpression.accept(this, obj);
		return obj1;
	}

	public Object visit(Action action, Object obj) {
		Object obj1 = null;
		action.name.accept(this, obj);
		action.arguments.accept(this, obj);
		return obj1;
	}

	public Object visit(Function function, Object obj) {
		Object obj1 = null;
		function.name.accept(this, obj);
		function.arguments.accept(this, obj);
		return obj1;
	}

	public Object visit(Name name, Object obj) {
		Object obj1 = null;
		name.nodeToken.accept(this, obj);
		name.nodeListOptional.accept(this, obj);
		return obj1;
	}

	public Object visit(ClassName classname, Object obj) {
		Object obj1 = null;
		classname.nodeToken.accept(this, obj);
		return obj1;
	}
	public Object visit(Message n, Object argu) {
		Object _ret = null;
		n.nodeList.accept(this, argu);
		n.nodeToken.accept(this, argu);
		return _ret;
	}

	public Object visit(Word n, Object argu) {
		Object _ret = null;
		n.nodeChoice.accept(this, argu);
		return _ret;
	}
}