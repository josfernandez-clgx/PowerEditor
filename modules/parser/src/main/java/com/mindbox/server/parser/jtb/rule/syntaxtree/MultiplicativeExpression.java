// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:11 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   MultiplicativeExpression.java

package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            UnaryExpression, NodeListOptional, Node

public class MultiplicativeExpression
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

    public MultiplicativeExpression(UnaryExpression unaryexpression, NodeListOptional nodelistoptional)
    {
        unaryExpression = unaryexpression;
        if(unaryExpression != null)
            unaryExpression.setParent(this);
        nodeListOptional = nodelistoptional;
        if(nodeListOptional != null)
            nodeListOptional.setParent(this);
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
    public UnaryExpression unaryExpression;
    public NodeListOptional nodeListOptional;
}