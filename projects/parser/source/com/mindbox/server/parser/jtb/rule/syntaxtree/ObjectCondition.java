// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:17 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ObjectCondition.java

package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            NodeChoice, ClassName, NodeOptional, NodeToken, 
//            RelationalExpression, Node

public class ObjectCondition
    implements Node
{

    public void setParent(Node node)
    {
        parent = node;
    }

    public Node getParent()
    {
        return parent;
    }

    public ObjectCondition(NodeChoice nodechoice, ClassName classname, NodeOptional nodeoptional, NodeOptional nodeoptional1, NodeToken nodetoken, RelationalExpression relationalexpression)
    {
        nodeChoice = nodechoice;
        if(nodeChoice != null)
            nodeChoice.setParent(this);
        className = classname;
        if(className != null)
            className.setParent(this);
        nodeOptional = nodeoptional;
        if(nodeOptional != null)
            nodeOptional.setParent(this);
        nodeOptional1 = nodeoptional1;
        if(nodeOptional1 != null)
            nodeOptional1.setParent(this);
        nodeToken = nodetoken;
        if(nodeToken != null)
            nodeToken.setParent(this);
        relationalExpression = relationalexpression;
        if(relationalExpression != null)
            relationalExpression.setParent(this);
    }

    public ObjectCondition(NodeChoice nodechoice, ClassName classname, NodeOptional nodeoptional, NodeOptional nodeoptional1, RelationalExpression relationalexpression)
    {
        nodeChoice = nodechoice;
        if(nodeChoice != null)
            nodeChoice.setParent(this);
        className = classname;
        if(className != null)
            className.setParent(this);
        nodeOptional = nodeoptional;
        if(nodeOptional != null)
            nodeOptional.setParent(this);
        nodeOptional1 = nodeoptional1;
        if(nodeOptional1 != null)
            nodeOptional1.setParent(this);
        nodeToken = new NodeToken("with");
        if(nodeToken != null)
            nodeToken.setParent(this);
        relationalExpression = relationalexpression;
        if(relationalExpression != null)
            relationalExpression.setParent(this);
    }

    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    public Object accept(ObjectVisitor objectvisitor, Object obj)
    {
        return objectvisitor.visit(this, obj);
    }

    private Node parent;
    public NodeChoice nodeChoice;
    public ClassName className;
    public NodeOptional nodeOptional;
    public NodeOptional nodeOptional1;
    public NodeToken nodeToken;
    public RelationalExpression relationalExpression;
}