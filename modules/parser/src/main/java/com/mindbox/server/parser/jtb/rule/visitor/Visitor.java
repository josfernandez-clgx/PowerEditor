// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:45 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Visitor.java

package com.mindbox.server.parser.jtb.rule.visitor;

import com.mindbox.server.parser.jtb.rule.syntaxtree.*;

public interface Visitor
{

    public abstract void visit(InstanceName instancename);

    public abstract void visit(ConditionalExpression conditionalexpression);

    public abstract void visit(ConditionalAndExpression conditionalandexpression);

    public abstract void visit(RelationalExpression relationalexpression);

    public abstract void visit(AdditiveExpression additiveexpression);

    public abstract void visit(MultiplicativeExpression multiplicativeexpression);

    public abstract void visit(UnaryExpression unaryexpression);

    public abstract void visit(PrimaryExpression primaryexpression);

    public abstract void visit(LiteralList literallist);

    public abstract void visit(Literal literal);

    public abstract void visit(BooleanLiteral booleanliteral);

    public abstract void visit(ColumnLiteral columnliteral);

    public abstract void visit(CellValue cellvalue);
    
    public abstract void visit(RuleName rulename);

    public abstract void visit(Arguments arguments);

    public abstract void visit(ArgumentList argumentlist);

    public abstract void visit(NodeList nodelist);

    public abstract void visit(NodeListOptional nodelistoptional);

    public abstract void visit(NodeOptional nodeoptional);

    public abstract void visit(NodeSequence nodesequence);

    public abstract void visit(NodeToken nodetoken);

    public abstract void visit(DeploymentRuleList deploymentrulelist);

    public abstract void visit(DeploymentRule deploymentrule);

    public abstract void visit(ObjectCondition objectcondition);

    public abstract void visit(Action action);

    public abstract void visit(Function function);

    public abstract void visit(Name name);

    public abstract void visit(ClassName classname);
    
    public abstract void visit(Message message);

    public abstract void visit(Word word);
}