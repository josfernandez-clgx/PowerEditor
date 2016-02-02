// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:47 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   TreeFormatter.java

package com.mindbox.server.parser.jtb.rule.visitor;

import com.mindbox.server.parser.jtb.rule.syntaxtree.*;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.visitor:
//            DepthFirstVisitor, FormatCommand, TreeFormatterException

public class TreeFormatter extends DepthFirstVisitor
{

    protected FormatCommand outdent()
    {
        return outdent(1);
    }

    protected FormatCommand outdent(int i)
    {
        return new FormatCommand(2, i);
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

    private void acceptLogicalSequence(NodeSequence nodesequence)
    {
        add(outdent());
        add(force());
        nodesequence.elementAt(0).accept(this);
        add(indent());
        nodesequence.elementAt(1).accept(this);
    }

    protected FormatCommand indent()
    {
        return indent(1);
    }

    protected FormatCommand indent(int i)
    {
        return new FormatCommand(1, i);
    }

    public void visit(AdditiveExpression additiveexpression)
    {
        additiveexpression.multiplicativeExpression.accept(this);
        if(additiveexpression.nodeListOptional.present())
            processList(additiveexpression.nodeListOptional, space());
    }

    public void visit(MultiplicativeExpression multiplicativeexpression)
    {
        multiplicativeexpression.unaryExpression.accept(this);
        if(multiplicativeexpression.nodeListOptional.present())
            processList(multiplicativeexpression.nodeListOptional, space());
    }

    public void visit(UnaryExpression unaryexpression)
    {
        unaryexpression.nodeChoice.accept(this);
    }

    public void visit(PrimaryExpression primaryexpression)
    {
        if(primaryexpression.nodeChoice.which != 3);
        primaryexpression.nodeChoice.accept(this);
    }

    public void visit(LiteralList literallist)
    {
        literallist.nodeToken.accept(this);
        literallist.literal.accept(this);
        if(literallist.nodeListOptional.present())
            processList(literallist.nodeListOptional);
        literallist.nodeToken1.accept(this);
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

    public void visit(DeploymentRuleList deploymentrulelist)
    {
        if(deploymentrulelist.nodeListOptional.present())
            processList(deploymentrulelist.nodeListOptional);
    }

    public void visit(DeploymentRule deploymentrule)
    {
        deploymentrule.nodeToken.accept(this);
        add(indent());
        deploymentrule.conditionalExpression.accept(this);
        add(outdent());
        add(force());
        deploymentrule.nodeToken1.accept(this);
        add(indent());
        add(force());
        deploymentrule.action.accept(this);
        add(outdent());
        add(force(2));
        if(deploymentrule.nodeOptional.present())
            deploymentrule.nodeOptional.accept(this);
    }

    public void visit(ObjectCondition objectcondition)
    {
        if(objectcondition.nodeChoice.which < 3)
            objectcondition.nodeChoice.accept(this);
        else
            processList((NodeListInterface)objectcondition.nodeChoice.choice, space());
        add(space());
        objectcondition.className.accept(this);
        add(space());
        if(objectcondition.nodeOptional.present())
        {
            objectcondition.nodeOptional.accept(this);
            add(space());
        }
        if(objectcondition.nodeOptional1.present())
        {
            processList((NodeListInterface)objectcondition.nodeOptional1.node, space());
            add(space());
        }
        objectcondition.nodeToken.accept(this);
        add(indent());
        objectcondition.relationalExpression.accept(this);
        add(outdent());
    }

    public void visit(Action action)
    {
        action.name.accept(this);
        action.arguments.accept(this);
        add(force());
    }

    public void visit(Function function)
    {
        function.name.accept(this);
        function.arguments.accept(this);
    }

    public void visit(Name name)
    {
        name.nodeToken.accept(this);
        if(name.nodeListOptional.present())
            processList(name.nodeListOptional);
    }

    public void visit(ClassName classname)
    {
        classname.nodeToken.accept(this);
    }

    public void visit(InstanceName instancename)
    {
        instancename.name.accept(this);
    }

    public void visit(ConditionalExpression conditionalexpression)
    {
        if(conditionalexpression.nodeListOptional.present())
            add(indent());
        conditionalexpression.conditionalAndExpression.accept(this);
        if(conditionalexpression.nodeListOptional.present())
        {
            for(Iterator iter = conditionalexpression.nodeListOptional.iterator(); iter.hasNext(); acceptLogicalSequence((NodeSequence)iter.next()));
            add(outdent());
        }
    }

    public void visit(ConditionalAndExpression conditionalandexpression)
    {
        if(conditionalandexpression.nodeListOptional.present())
            add(indent());
        conditionalandexpression.relationalExpression.accept(this);
        if(conditionalandexpression.nodeListOptional.present())
        {
            for(Iterator iter = conditionalandexpression.nodeListOptional.iterator(); iter.hasNext(); acceptLogicalSequence((NodeSequence)iter.next()));
            add(outdent());
        }
    }

    protected FormatCommand space()
    {
        return space(1);
    }

    protected FormatCommand space(int i)
    {
        return new FormatCommand(3, i);
    }

    public void visit(RelationalExpression relationalexpression)
    {
        add(force());
        if(relationalexpression.nodeChoice.which == 0)
        {
            relationalexpression.nodeChoice.accept(this);
        } else
        {
            NodeSequence nodesequence = (NodeSequence)relationalexpression.nodeChoice.choice;
            nodesequence.elementAt(0).accept(this);
            if(nodesequence.size() > 1)
            {
                NodeListOptional nodelistoptional = (NodeListOptional)nodesequence.elementAt(1);
                if(nodelistoptional.present())
                {
                    NodeSequence nodesequence1;
                    for(Iterator iter = nodelistoptional.iterator(); iter.hasNext(); nodesequence1.elementAt(1).accept(this))
                    {
                        nodesequence1 = (NodeSequence)iter.next();
                        add(space());
                        nodesequence1.elementAt(0).accept(this);
                        add(space());
                    }

                }
            }
        }
    }

    public void visit(Literal literal)
    {
        literal.nodeChoice.accept(this);
    }

    public void visit(BooleanLiteral booleanliteral)
    {
        booleanliteral.nodeChoice.accept(this);
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

    public void visit(ColumnLiteral columnliteral)
    {
        columnliteral.nodeToken.accept(this);
        columnliteral.nodeToken1.accept(this);
        columnliteral.nodeToken2.accept(this);
        columnliteral.nodeToken3.accept(this);
    }

    public void visit(Arguments arguments)
    {
        arguments.nodeToken.accept(this);
        if(arguments.nodeOptional.present())
            arguments.nodeOptional.accept(this);
        arguments.nodeToken1.accept(this);
    }

    public void visit(ArgumentList argumentlist)
    {
        argumentlist.additiveExpression.accept(this);
        if(argumentlist.nodeListOptional.present())
            processList(argumentlist.nodeListOptional);
    }

    public void visit(Message message)
    {
        processList(message.nodeList);
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

    public void visit(Word word)
    {
        word.nodeChoice.accept(this);
    }

    protected void processList(NodeListInterface nodelistinterface)
    {
        processList(nodelistinterface, null);
    }

    protected void processList(NodeListInterface nodelistinterface, FormatCommand formatcommand)
    {
        for(Iterator iter = nodelistinterface.iterator(); iter.hasNext(); add(space()))
        {
            ((Node)iter.next()).accept(this);
            if(formatcommand != null && iter.hasNext())
                cmdQueue.add(formatcommand);
        }

    }

    private List cmdQueue;
    private boolean lineWrap;
    private int wrapWidth;
    private int indentAmt;
    private int curLine;
    private int curColumn;
    private int curIndent;
}