// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:00:36 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   MessageParserConstants.java

package com.mindbox.server.parser.jtb.msg;


public interface MessageParserConstants
{

    public static final int EOF = 0;
    public static final int SINGLE_LINE_COMMENT = 9;
    public static final int FORMAL_COMMENT = 11;
    public static final int MULTI_LINE_COMMENT = 12;
    public static final int COLUMN = 14;
    public static final int CELLVALUE = 15;
    public static final int INTEGER_LITERAL = 16;
    public static final int DECIMAL_LITERAL = 17;
    public static final int HEX_LITERAL = 18;
    public static final int OCTAL_LITERAL = 19;
    public static final int FLOATING_POINT_LITERAL = 20;
    public static final int EXPONENT = 21;
    public static final int CHARACTER_LITERAL = 22;
    public static final int STRING_LITERAL = 23;
    public static final int IDENTIFIER = 24;
    public static final int LETTER = 25;
    public static final int DIGIT = 26;
    public static final int DOT = 27;
    public static final int PERCENT = 28;
    public static final int DEFAULT = 0;
    public static final int IN_SINGLE_LINE_COMMENT = 1;
    public static final int IN_FORMAL_COMMENT = 2;
    public static final int IN_MULTI_LINE_COMMENT = 3;
    public static final String tokenImage[] = {
        "<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "\"\\f\"", "\"//\"", "<token of kind 7>", "\"/*\"", "<SINGLE_LINE_COMMENT>", 
        "<token of kind 10>", "\"*/\"", "\"*/\"", "<token of kind 13>", "\"column\"", "\"cellValue\"", "<INTEGER_LITERAL>", "<DECIMAL_LITERAL>", "<HEX_LITERAL>", "<OCTAL_LITERAL>", 
        "<FLOATING_POINT_LITERAL>", "<EXPONENT>", "<CHARACTER_LITERAL>", "<STRING_LITERAL>", "<IDENTIFIER>", "<LETTER>", "<DIGIT>", "\".\"", "\"%\""
    };

}