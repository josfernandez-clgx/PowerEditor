// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:00:34 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TokenMgrError.java

package com.mindbox.server.parser.jtb.msg;


public class TokenMgrError extends Error
{

    public TokenMgrError()
    {
    }

    public TokenMgrError(String s, int i)
    {
        super(s);
        errorCode = i;
    }

    public TokenMgrError(boolean flag, int i, int j, int k, String s, char c, int l)
    {
        this(LexicalError(flag, i, j, k, s, c), l);
    }

    protected static final String addEscapes(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            char c;
            switch(s.charAt(i))
            {
            case 0: // '\0'
                break;

            case 8: // '\b'
                stringbuffer.append("\\b");
                break;

            case 9: // '\t'
                stringbuffer.append("\\t");
                break;

            case 10: // '\n'
                stringbuffer.append("\\n");
                break;

            case 12: // '\f'
                stringbuffer.append("\\f");
                break;

            case 13: // '\r'
                stringbuffer.append("\\r");
                break;

            case 34: // '"'
                stringbuffer.append("\\\"");
                break;

            case 39: // '\''
                stringbuffer.append("\\'");
                break;

            case 92: // '\\'
                stringbuffer.append("\\\\");
                break;

            default:
                if((c = s.charAt(i)) < ' ' || c > '~')
                {
                    String s1 = "0000" + Integer.toString(c, 16);
                    stringbuffer.append("\\u" + s1.substring(s1.length() - 4, s1.length()));
                } else
                {
                    stringbuffer.append(c);
                }
                break;
            }
        }

        return stringbuffer.toString();
    }

    public String getMessage()
    {
        return super.getMessage();
    }

    private static final String LexicalError(boolean flag, int i, int j, int k, String s, char c)
    {
        return "Lexical error at line " + j + ", column " + k + ".  Encountered: " + (flag ? "<EOF> " : "\"" + addEscapes(String.valueOf(c)) + "\"" + " (" + (int)c + "), ") + "after : \"" + addEscapes(s) + "\"";
    }

    static final int LEXICAL_ERROR = 0;
    static final int STATIC_LEXER_ERROR = 1;
    static final int INVALID_LEXICAL_STATE = 2;
    static final int LOOP_DETECTED = 3;
    int errorCode;
}