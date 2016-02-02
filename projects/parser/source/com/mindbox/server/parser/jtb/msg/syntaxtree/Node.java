// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:01:09 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Node.java

package com.mindbox.server.parser.jtb.msg.syntaxtree;

import com.mindbox.server.parser.jtb.msg.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.msg.visitor.Visitor;
import java.io.Serializable;

public interface Node
    extends Serializable
{

    public abstract void setParent(Node node);

    public abstract Node getParent();

    public abstract void accept(Visitor visitor);

    public abstract Object accept(ObjectVisitor objectvisitor, Object obj);
}