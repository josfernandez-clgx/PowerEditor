// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:01:08 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Name.java

package com.mindbox.server.parser.jtb.msg.syntaxtree;

import com.mindbox.server.parser.jtb.msg.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.msg.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.msg.syntaxtree:
//            NodeToken, NodeList, Node

public class Name
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

    public Name(NodeToken nodetoken, NodeList nodelist)
    {
        nodeToken = nodetoken;
        if(nodeToken != null)
            nodeToken.setParent(this);
        nodeList = nodelist;
        if(nodeList != null)
            nodeList.setParent(this);
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
    public NodeList nodeList;
}