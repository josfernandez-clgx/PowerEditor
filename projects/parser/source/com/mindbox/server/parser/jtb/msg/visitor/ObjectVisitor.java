package com.mindbox.server.parser.jtb.msg.visitor;

import com.mindbox.server.parser.jtb.msg.syntaxtree.*;

public interface ObjectVisitor
{

    public abstract Object visit(NodeList nodelist, Object obj);

    public abstract Object visit(NodeListOptional nodelistoptional, Object obj);

    public abstract Object visit(NodeOptional nodeoptional, Object obj);

    public abstract Object visit(NodeSequence nodesequence, Object obj);

    public abstract Object visit(NodeToken nodetoken, Object obj);

    public abstract Object visit(ColumnLiteral columnliteral, Object obj);

    public abstract Object visit(CellValue cellvalue, Object obj);
    
    public abstract Object visit(RuleName rulename, Object obj);

    public abstract Object visit(Name name, Object obj);

    public abstract Object visit(Message message, Object obj);

    public abstract Object visit(Word word, Object obj);
}