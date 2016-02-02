// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:01:09 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   NodeChoice.java

package com.mindbox.server.parser.jtb.msg.syntaxtree;

import com.mindbox.server.parser.jtb.msg.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.msg.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.msg.syntaxtree:
//            Node

public class NodeChoice
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

    public NodeChoice(Node node)
    {
        this(node, -1);
    }

    public NodeChoice(Node node, int i)
    {
        choice = node;
        choice.setParent(this);
        which = i;
    }

    public void accept(Visitor visitor)
    {
        choice.accept(visitor);
    }

    public Object accept(ObjectVisitor objectvisitor, Object obj)
    {
        return choice.accept(objectvisitor, obj);
    }

    private Node parent;
    public Node choice;
    public int which;
}