// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:01:38 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   TreeFormatter.java

package com.mindbox.server.parser.jtb.msg.visitor;

import com.mindbox.server.parser.jtb.msg.syntaxtree.*;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.mindbox.server.parser.jtb.msg.visitor:
//            DepthFirstVisitor, FormatCommand, TreeFormatterException

public class TreeFormatter extends DepthFirstVisitor
{

    protected FormatCommand space()
    {
        return space(1);
    }

    protected FormatCommand space(int i)
    {
        return new FormatCommand(3, i);
    }

    protected FormatCommand outdent()
    {
        return outdent(1);
    }

    protected FormatCommand outdent(int i)
    {
        return new FormatCommand(2, i);
    }

    private void placeToken(NodeToken nodetoken, int i, int j)
    {
        int k = nodetoken.tokenImage.length();
        if(!lineWrap || nodetoken.tokenImage.indexOf('\n') != -1 || j + k <= wrapWidth)
        {
            nodetoken.beginColumn = j;
        } else
        {
            i++;
            j = curIndent + indentAmt + 1;
            nodetoken.beginColumn = j;
        }
        nodetoken.beginLine = i;
        for(int l = 0; l < k; l++)
            if(nodetoken.tokenImage.charAt(l) == '\n' && l < k - 1)
            {
                i++;
                j = 1;
            } else
            {
                j++;
            }

        nodetoken.endLine = i;
        nodetoken.endColumn = j;
    }

    public TreeFormatter()
    {
        this(3, 0);
    }

    public TreeFormatter(int i, int j)
    {
        cmdQueue = new java.util.ArrayList();
        curLine = 1;
        curColumn = 1;
        curIndent = 0;
        indentAmt = i;
        wrapWidth = j;
        if(j > 0)
            lineWrap = true;
        else
            lineWrap = false;
    }

    protected void add(FormatCommand formatcommand)
    {
        cmdQueue.add(formatcommand);
    }

    protected FormatCommand force()
    {
        return force(1);
    }

    protected FormatCommand force(int i)
    {
        return new FormatCommand(0, i);
    }

    protected void processList(NodeListInterface nodelistinterface)
    {
        processList(nodelistinterface, null);
    }

    protected void processList(NodeListInterface nodelistinterface, FormatCommand formatcommand)
    {
        for(Iterator iter = nodelistinterface.iterator(); iter.hasNext();)
        {
            ((Node)iter.next()).accept(this);
            if(formatcommand != null && iter.hasNext())
                cmdQueue.add(formatcommand);
        }

    }

    protected FormatCommand indent()
    {
        return indent(1);
    }

    protected FormatCommand indent(int i)
    {
        return new FormatCommand(1, i);
    }

    public void visit(NodeToken nodetoken)
    {
        for(Iterator iter = cmdQueue.iterator(); iter.hasNext();)
        {
            FormatCommand formatcommand = (FormatCommand)iter.next();
            switch(formatcommand.getCommand())
            {
            case 0: // '\0'
                curLine += formatcommand.getNumCommands();
                curColumn = curIndent + 1;
                break;

            case 1: // '\001'
                curIndent += indentAmt * formatcommand.getNumCommands();
                break;

            case 2: // '\002'
                if(curIndent >= indentAmt)
                    curIndent -= indentAmt * formatcommand.getNumCommands();
                break;

            case 3: // '\003'
                curColumn += formatcommand.getNumCommands();
                break;

            default:
                throw new TreeFormatterException("Invalid value in command queue.");
            }
        }
        cmdQueue.clear();
        
        if(nodetoken.numSpecials() > 0)
        {
            for(Iterator iter1 = nodetoken.specialTokens.iterator(); iter1.hasNext();)
            {
                NodeToken nodetoken1 = (NodeToken)iter1.next();
                placeToken(nodetoken1, curLine, curColumn);
                curLine = nodetoken1.endLine + 1;
            }

        }
        placeToken(nodetoken, curLine, curColumn);
        curLine = nodetoken.endLine;
        curColumn = nodetoken.endColumn;
    }

    public void visit(ColumnLiteral columnliteral)
    {
        columnliteral.nodeToken.accept(this);
        columnliteral.nodeToken1.accept(this);
        columnliteral.nodeToken2.accept(this);
        columnliteral.nodeToken3.accept(this);
    }

    public void visit(CellValue cellvalue)
    {
        cellvalue.nodeToken.accept(this);
        cellvalue.nodeToken1.accept(this);
        cellvalue.nodeToken2.accept(this);
    }

    public void visit(Name name)
    {
        name.nodeToken.accept(this);
        processList(name.nodeList);
    }

    public void visit(Message message)
    {
        processList(message.nodeList);
        message.nodeToken.accept(this);
    }

    public void visit(Word word)
    {
        word.nodeChoice.accept(this);
    }

    private List cmdQueue;
    private boolean lineWrap;
    private int wrapWidth;
    private int indentAmt;
    private int curLine;
    private int curColumn;
    private int curIndent;
}