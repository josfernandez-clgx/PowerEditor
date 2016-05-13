// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:11 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Message.java

package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            NodeList, NodeToken, Node

public class Message
    implements Node
{

    public void setParent(Node n)
    {
        parent = n;
    }

    public Node getParent()
    {
        return parent;
    }

    public Message(NodeList n0, NodeToken n1)
    {
        nodeList = n0;
        if(nodeList != null)
            nodeList.setParent(this);
        nodeToken = n1;
        if(nodeToken != null)
            nodeToken.setParent(this);
    }

    public Message(NodeList n0)
    {
        nodeList = n0;
        if(nodeList != null)
            nodeList.setParent(this);
        nodeToken = new NodeToken("");
        if(nodeToken != null)
            nodeToken.setParent(this);
    }

    public void accept(Visitor v)
    {
        v.visit(this);
    }

    public Object accept(ObjectVisitor v, Object argu)
    {
        return v.visit(this, argu);
    }

    private Node parent;
    public NodeList nodeList;
    public NodeToken nodeToken;
}