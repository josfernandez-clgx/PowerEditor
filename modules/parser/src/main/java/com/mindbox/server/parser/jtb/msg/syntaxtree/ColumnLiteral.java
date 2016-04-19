// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:01:07 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ColumnLiteral.java

package com.mindbox.server.parser.jtb.msg.syntaxtree;

import com.mindbox.server.parser.jtb.msg.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.msg.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.msg.syntaxtree:
//            NodeToken, Node

public class ColumnLiteral
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

    public ColumnLiteral(NodeToken nodetoken, NodeToken nodetoken1, NodeToken nodetoken2, NodeToken nodetoken3)
    {
        nodeToken = nodetoken;
        if(nodeToken != null)
            nodeToken.setParent(this);
        nodeToken1 = nodetoken1;
        if(nodeToken1 != null)
            nodeToken1.setParent(this);
        nodeToken2 = nodetoken2;
        if(nodeToken2 != null)
            nodeToken2.setParent(this);
        nodeToken3 = nodetoken3;
        if(nodeToken3 != null)
            nodeToken3.setParent(this);
    }

    public ColumnLiteral(NodeToken nodetoken)
    {
        nodeToken = new NodeToken("%");
        if(nodeToken != null)
            nodeToken.setParent(this);
        nodeToken1 = new NodeToken("column");
        if(nodeToken1 != null)
            nodeToken1.setParent(this);
        nodeToken2 = nodetoken;
        if(nodeToken2 != null)
            nodeToken2.setParent(this);
        nodeToken3 = new NodeToken("%");
        if(nodeToken3 != null)
            nodeToken3.setParent(this);
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
    public NodeToken nodeToken1;
    public NodeToken nodeToken2;
    public NodeToken nodeToken3;
}