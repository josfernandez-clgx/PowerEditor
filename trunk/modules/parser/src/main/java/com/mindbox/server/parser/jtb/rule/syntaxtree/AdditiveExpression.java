// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:03 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AdditiveExpression.java

package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            MultiplicativeExpression, NodeListOptional, Node

public class AdditiveExpression
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

    public AdditiveExpression(MultiplicativeExpression multiplicativeexpression, NodeListOptional nodelistoptional)
    {
        multiplicativeExpression = multiplicativeexpression;
        if(multiplicativeExpression != null)
            multiplicativeExpression.setParent(this);
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
    public MultiplicativeExpression multiplicativeExpression;
    public NodeListOptional nodeListOptional;
}