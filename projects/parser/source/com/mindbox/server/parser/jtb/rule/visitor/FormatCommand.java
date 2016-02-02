// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:03:45 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TreeFormatter.java

package com.mindbox.server.parser.jtb.rule.visitor;


class FormatCommand
{

    FormatCommand(int i, int j)
    {
        command = i;
        numCommands = j;
    }

    public int getCommand()
    {
        return command;
    }

    public void setCommand(int i)
    {
        command = i;
    }

    public int getNumCommands()
    {
        return numCommands;
    }

    public void setNumCommands(int i)
    {
        numCommands = i;
    }

    public static final int FORCE = 0;
    public static final int INDENT = 1;
    public static final int OUTDENT = 2;
    public static final int SPACE = 3;
    private int command;
    private int numCommands;
}