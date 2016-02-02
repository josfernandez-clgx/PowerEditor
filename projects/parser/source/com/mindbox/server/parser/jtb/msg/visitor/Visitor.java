// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:01:35 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Visitor.java

package com.mindbox.server.parser.jtb.msg.visitor;

import com.mindbox.server.parser.jtb.msg.syntaxtree.*;

public interface Visitor
{

    public abstract void visit(NodeList nodelist);

    public abstract void visit(NodeListOptional nodelistoptional);

    public abstract void visit(NodeOptional nodeoptional);

    public abstract void visit(NodeSequence nodesequence);

    public abstract void visit(NodeToken nodetoken);

    public abstract void visit(ColumnLiteral columnliteral);

    public abstract void visit(CellValue cellvalue);
	
	public abstract void visit(RuleName rulename);
	
    public abstract void visit(Name name);

    public abstract void visit(Message message);

    public abstract void visit(Word word);
}