// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:00:38 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Token.java

package com.mindbox.server.parser.jtb.msg;


public class Token
{

    public final String toString()
    {
        return image;
    }

    public Token()
    {
    }

    public static final Token newToken(int i)
    {
        switch(i)
        {
        case 0: // '\0'
        default:
            return new Token();
        }
    }

    public int kind;
    public int beginLine;
    public int beginColumn;
    public int endLine;
    public int endColumn;
    public String image;
    public Token next;
    public Token specialToken;
}