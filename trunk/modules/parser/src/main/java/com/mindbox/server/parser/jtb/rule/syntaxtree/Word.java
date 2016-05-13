// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:02 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Word.java

package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            NodeChoice, Node

public class Word
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

    public Word(NodeChoice n0)
    {
        nodeChoice = n0;
        if(nodeChoice != null)
            nodeChoice.setParent(this);
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
    public NodeChoice nodeChoice;
}