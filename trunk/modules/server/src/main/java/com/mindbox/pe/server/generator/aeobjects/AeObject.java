// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 2:52:17 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AeObject.java

package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

public class AeObject
{

    public AeObject(Node pNode)
    {
        setNode(pNode);
    }

    public Node getNode()
    {
        return mNode;
    }

    public void setNode(Node pNode)
    {
        mNode = pNode;
    }

    private Node mNode;
}