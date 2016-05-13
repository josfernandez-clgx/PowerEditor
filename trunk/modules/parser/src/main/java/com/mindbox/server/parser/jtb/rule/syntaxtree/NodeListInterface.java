// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:14 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   NodeListInterface.java

package com.mindbox.server.parser.jtb.rule.syntaxtree;

import java.util.Iterator;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            Node

public interface NodeListInterface
    extends Node
{

    Iterator iterator();

    int size();

    Node elementAt(int i);

    void addNode(Node node);
}