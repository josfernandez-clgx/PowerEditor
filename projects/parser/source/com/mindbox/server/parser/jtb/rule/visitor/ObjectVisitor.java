package com.mindbox.server.parser.jtb.rule.visitor;

import com.mindbox.server.parser.jtb.rule.syntaxtree.*;

public interface ObjectVisitor {

	public abstract Object visit(InstanceName instancename, Object obj);

	public abstract Object visit(ConditionalExpression conditionalexpression, Object obj);

	public abstract Object visit(ConditionalAndExpression conditionalandexpression, Object obj);

	public abstract Object visit(RelationalExpression relationalexpression, Object obj);

	public abstract Object visit(AdditiveExpression additiveexpression, Object obj);

	public abstract Object visit(MultiplicativeExpression multiplicativeexpression, Object obj);

	public abstract Object visit(UnaryExpression unaryexpression, Object obj);

	public abstract Object visit(PrimaryExpression primaryexpression, Object obj);

	public abstract Object visit(LiteralList literallist, Object obj);

	public abstract Object visit(Literal literal, Object obj);

	public abstract Object visit(BooleanLiteral booleanliteral, Object obj);

	public abstract Object visit(ColumnLiteral columnliteral, Object obj);
	
	public abstract Object visit(RuleName rulename, Object obj);

	public abstract Object visit(CellValue cellvalue, Object obj);

	public abstract Object visit(Arguments arguments, Object obj);

	public abstract Object visit(ArgumentList argumentlist, Object obj);

	public abstract Object visit(NodeList nodelist, Object obj);

	public abstract Object visit(NodeListOptional nodelistoptional, Object obj);

	public abstract Object visit(NodeOptional nodeoptional, Object obj);

	public abstract Object visit(NodeSequence nodesequence, Object obj);

	public abstract Object visit(NodeToken nodetoken, Object obj);

	public abstract Object visit(DeploymentRuleList deploymentrulelist, Object obj);

	public abstract Object visit(DeploymentRule deploymentrule, Object obj);

	public abstract Object visit(ObjectCondition objectcondition, Object obj);

	public abstract Object visit(Action action, Object obj);

	public abstract Object visit(Function function, Object obj);

	public abstract Object visit(Name name, Object obj);

	public abstract Object visit(ClassName classname, Object obj);

	public abstract Object visit(Message message, Object obj);

	public abstract Object visit(Word word, Object obj);
}