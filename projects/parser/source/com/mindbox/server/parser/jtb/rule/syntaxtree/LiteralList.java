// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:10 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LiteralList.java

package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            NodeToken, Literal, NodeListOptional, Node

public class LiteralList
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

    public LiteralList(NodeToken nodetoken, Literal literal1, NodeListOptional nodelistoptional, NodeToken nodetoken1)
    {
        nodeToken = nodetoken;
        if(nodeToken != null)
            nodeToken.setParent(this);
        literal = literal1;
        if(literal != null)
            literal.setParent(this);
        nodeListOptional = nodelistoptional;
        if(nodeListOptional != null)
            nodeListOptional.setParent(this);
        nodeToken1 = nodetoken1;
        if(nodeToken1 != null)
            nodeToken1.setParent(this);
    }

    public LiteralList(Literal literal1, NodeListOptional nodelistoptional)
    {
        nodeToken = new NodeToken("[");
        if(nodeToken != null)
            nodeToken.setParent(this);
        literal = literal1;
        if(literal != null)
            literal.setParent(this);
        nodeListOptional = nodelistoptional;
        if(nodeListOptional != null)
            nodeListOptional.setParent(this);
        nodeToken1 = new NodeToken("]");
        if(nodeToken1 != null)
            nodeToken1.setParent(this);
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
    public NodeToken nodeToken;
    public Literal literal;
    public NodeListOptional nodeListOptional;
    public NodeToken nodeToken1;
}