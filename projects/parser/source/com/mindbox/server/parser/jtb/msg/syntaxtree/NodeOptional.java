// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:01:11 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   NodeOptional.java

package com.mindbox.server.parser.jtb.msg.syntaxtree;

import com.mindbox.server.parser.jtb.msg.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.msg.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.msg.syntaxtree:
//            Node

public class NodeOptional
    implements Node
{

    public void setParent(Node node1)
    {
        parent = node1;
    }

    public Node getParent()
    {
        return parent;
    }

    public NodeOptional()
    {
        node = null;
    }

    public NodeOptional(Node node1)
    {
        addNode(node1);
    }

    public boolean present()
    {
        return node != null;
    }


    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    public Object accept(ObjectVisitor objectvisitor, Object obj)
    {
        return objectvisitor.visit(this, obj);
    }

    public void addNode(Node node1)
    {
        if(node != null)
        {
            throw new Error("Attempt to set optional node twice");
        } else
        {
            node = node1;
            node1.setParent(this);
            return;
        }
    }

    private Node parent;
    public Node node;
}