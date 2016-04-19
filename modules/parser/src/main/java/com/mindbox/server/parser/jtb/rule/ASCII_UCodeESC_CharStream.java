// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:02:11 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ASCII_UCodeESC_CharStream.java

package com.mindbox.server.parser.jtb.rule;

import java.io.*;

public final class ASCII_UCodeESC_CharStream
{

    /**
     * @deprecated Method getColumn is deprecated
     */

    public static final int getColumn()
    {
        return bufcolumn[bufpos];
    }

    /**
     * @deprecated Method getLine is deprecated
     */

    public static final int getLine()
    {
        return bufline[bufpos];
    }

    public static final int getEndLine()
    {
        return bufline[bufpos];
    }

    public ASCII_UCodeESC_CharStream(Reader reader, int i, int j, int k)
    {
        if(inputStream != null)
        {
            throw new Error("\n   ERROR: Second call to the constructor of a static ASCII_UCodeESC_CharStream.  You must\n       either use ReInit() or set the JavaCC option STATIC to false\n       during the generation of this class.");
        } else
        {
            inputStream = reader;
            line = i;
            column = j - 1;
            available = bufsize = k;
            buffer = new char[k];
            bufline = new int[k];
            bufcolumn = new int[k];
            nextCharBuf = new char[4096];
            return;
        }
    }

    public ASCII_UCodeESC_CharStream(Reader reader, int i, int j)
    {
        this(reader, i, j, 4096);
    }

    public void ReInit(Reader reader, int i, int j, int k)
    {
        inputStream = reader;
        line = i;
        column = j - 1;
        if(buffer == null || k != buffer.length)
        {
            available = bufsize = k;
            buffer = new char[k];
            bufline = new int[k];
            bufcolumn = new int[k];
            nextCharBuf = new char[4096];
        }
        prevCharIsLF = prevCharIsCR = false;
        tokenBegin = inBuf = maxNextCharInd = 0;
        nextCharInd = bufpos = -1;
    }

    public void ReInit(Reader reader, int i, int j)
    {
        ReInit(reader, i, j, 4096);
    }

    public ASCII_UCodeESC_CharStream(InputStream inputstream, int i, int j, int k)
    {
        this(((Reader) (new InputStreamReader(inputstream))), i, j, 4096);
    }

    public static final int getEndColumn()
    {
        return bufcolumn[bufpos];
    }

    public ASCII_UCodeESC_CharStream(InputStream inputstream, int i, int j)
    {
        this(inputstream, i, j, 4096);
    }

    public void ReInit(InputStream inputstream, int i, int j, int k)
    {
        ReInit(((Reader) (new InputStreamReader(inputstream))), i, j, 4096);
    }

    private static final void ExpandBuff(boolean flag)
    {
        char ac[] = new char[bufsize + 2048];
        int ai[] = new int[bufsize + 2048];
        int ai1[] = new int[bufsize + 2048];
        try
        {
            if(flag)
            {
                System.arraycopy(buffer, tokenBegin, ac, 0, bufsize - tokenBegin);
                System.arraycopy(buffer, 0, ac, bufsize - tokenBegin, bufpos);
                buffer = ac;
                System.arraycopy(bufline, tokenBegin, ai, 0, bufsize - tokenBegin);
                System.arraycopy(bufline, 0, ai, bufsize - tokenBegin, bufpos);
                bufline = ai;
                System.arraycopy(bufcolumn, tokenBegin, ai1, 0, bufsize - tokenBegin);
                System.arraycopy(bufcolumn, 0, ai1, bufsize - tokenBegin, bufpos);
                bufcolumn = ai1;
                bufpos += bufsize - tokenBegin;
            } else
            {
                System.arraycopy(buffer, tokenBegin, ac, 0, bufsize - tokenBegin);
                buffer = ac;
                System.arraycopy(bufline, tokenBegin, ai, 0, bufsize - tokenBegin);
                bufline = ai;
                System.arraycopy(bufcolumn, tokenBegin, ai1, 0, bufsize - tokenBegin);
                bufcolumn = ai1;
                bufpos -= tokenBegin;
            }
        }
        catch(Throwable throwable)
        {
            throw new Error(throwable.getMessage());
        }
        available = bufsize += 2048;
        tokenBegin = 0;
    }

    private static final void FillBuff()
        throws IOException
    {
        if(maxNextCharInd == 4096)
            maxNextCharInd = nextCharInd = 0;
        int i;
        try
        {
            if((i = inputStream.read(nextCharBuf, maxNextCharInd, 4096 - maxNextCharInd)) == -1)
            {
                inputStream.close();
                throw new IOException();
            } else
            {
                maxNextCharInd += i;
                return;
            }
        }
        catch(IOException ioexception)
        {
            if(bufpos != 0)
            {
                bufpos--;
                backup(0);
            } else
            {
                bufline[bufpos] = line;
                bufcolumn[bufpos] = column;
            }
            throw ioexception;
        }
    }

    public void ReInit(InputStream inputstream, int i, int j)
    {
        ReInit(inputstream, i, j, 4096);
    }

    public static final String GetImage()
    {
        if(bufpos >= tokenBegin)
            return new String(buffer, tokenBegin, (bufpos - tokenBegin) + 1);
        else
            return new String(buffer, tokenBegin, bufsize - tokenBegin) + new String(buffer, 0, bufpos + 1);
    }

    public static void adjustBeginLineColumn(int i, int j)
    {
        int k = tokenBegin;
        int l;
        if(bufpos >= tokenBegin)
            l = (bufpos - tokenBegin) + inBuf + 1;
        else
            l = (bufsize - tokenBegin) + bufpos + 1 + inBuf;
        int i1 = 0;
        int j1 = 0;
        boolean flag = false;
        boolean flag1 = false;
        int i2 = 0;
        int k1;
        for(; i1 < l && bufline[j1 = k % bufsize] == bufline[k1 = ++k % bufsize]; i1++)
        {
            bufline[j1] = i;
            int l1 = (i2 + bufcolumn[k1]) - bufcolumn[j1];
            bufcolumn[j1] = j + i2;
            i2 = l1;
        }

        if(i1 < l)
        {
            bufline[j1] = i++;
            bufcolumn[j1] = j + i2;
            while(i1++ < l) 
                if(bufline[j1 = k % bufsize] != bufline[++k % bufsize])
                    bufline[j1] = i++;
                else
                    bufline[j1] = i;
        }
        line = bufline[j1];
        column = bufcolumn[j1];
    }

    private static final char ReadByte()
        throws IOException
    {
        if(++nextCharInd >= maxNextCharInd)
            FillBuff();
        return nextCharBuf[nextCharInd];
    }

    public static final char BeginToken()
        throws IOException
    {
        if(inBuf > 0)
        {
            inBuf--;
            return buffer[tokenBegin = bufpos != bufsize - 1 ? ++bufpos : (bufpos = 0)];
        } else
        {
            tokenBegin = 0;
            bufpos = -1;
            return readChar();
        }
    }

    public static void Done()
    {
        nextCharBuf = null;
        buffer = null;
        bufline = null;
        bufcolumn = null;
    }

    public static final char readChar()
        throws IOException
    {
        if(inBuf > 0)
        {
            inBuf--;
            return buffer[bufpos != bufsize - 1 ? ++bufpos : (bufpos = 0)];
        }
        if(++bufpos == available)
            AdjustBuffSize();
        char c;
        if((buffer[bufpos] = c = (char)(0xff & ReadByte())) == '\\')
        {
            UpdateLineColumn(c);
            int i = 1;
            do
            {
                if(++bufpos == available)
                    AdjustBuffSize();
                try
                {
                    if((buffer[bufpos] = c = (char)(0xff & ReadByte())) != '\\')
                    {
                        UpdateLineColumn(c);
                        if(c == 'u' && (i & 1) == 1)
                        {
                            if(--bufpos < 0)
                                bufpos = bufsize - 1;
                        } else
                        {
                            backup(i);
                            return '\\';
                        }
                        break;
                    }
                }
                catch(IOException _ex)
                {
                    if(i > 1)
                        backup(i);
                    return '\\';
                }
                UpdateLineColumn(c);
                i++;
            } while(true);
            try
            {
                while((c = (char)(0xff & ReadByte())) == 'u') 
                    column++;
                buffer[bufpos] = c = (char)(hexval(c) << 12 | hexval((char)(0xff & ReadByte())) << 8 | hexval((char)(0xff & ReadByte())) << 4 | hexval((char)(0xff & ReadByte())));
                column += 4;
            }
            catch(IOException _ex)
            {
                throw new Error("Invalid escape character at line " + line + " column " + column + ".");
            }
            if(i == 1)
            {
                return c;
            } else
            {
                backup(i - 1);
                return '\\';
            }
        } else
        {
            UpdateLineColumn(c);
            return c;
        }
    }

    private static final void AdjustBuffSize()
    {
        if(available == bufsize)
        {
            if(tokenBegin > 2048)
            {
                bufpos = 0;
                available = tokenBegin;
            } else
            {
                ExpandBuff(false);
            }
        } else
        if(available > tokenBegin)
            available = bufsize;
        else
        if(tokenBegin - available < 2048)
            ExpandBuff(true);
        else
            available = tokenBegin;
    }

    public static final int getBeginColumn()
    {
        return bufcolumn[tokenBegin];
    }

    public static final void backup(int i)
    {
        inBuf += i;
        if((bufpos -= i) < 0)
            bufpos += bufsize;
    }

    public static final int getBeginLine()
    {
        return bufline[tokenBegin];
    }

    static final int hexval(char c)
        throws IOException
    {
        switch(c)
        {
        case 48: // '0'
            return 0;

        case 49: // '1'
            return 1;

        case 50: // '2'
            return 2;

        case 51: // '3'
            return 3;

        case 52: // '4'
            return 4;

        case 53: // '5'
            return 5;

        case 54: // '6'
            return 6;

        case 55: // '7'
            return 7;

        case 56: // '8'
            return 8;

        case 57: // '9'
            return 9;

        case 65: // 'A'
        case 97: // 'a'
            return 10;

        case 66: // 'B'
        case 98: // 'b'
            return 11;

        case 67: // 'C'
        case 99: // 'c'
            return 12;

        case 68: // 'D'
        case 100: // 'd'
            return 13;

        case 69: // 'E'
        case 101: // 'e'
            return 14;

        case 70: // 'F'
        case 102: // 'f'
            return 15;
        }
        throw new IOException();
    }

    public static final char[] GetSuffix(int i)
    {
        char ac[] = new char[i];
        if(bufpos + 1 >= i)
        {
            System.arraycopy(buffer, (bufpos - i) + 1, ac, 0, i);
        } else
        {
            System.arraycopy(buffer, bufsize - (i - bufpos - 1), ac, 0, i - bufpos - 1);
            System.arraycopy(buffer, 0, ac, i - bufpos - 1, bufpos + 1);
        }
        return ac;
    }

    private static final void UpdateLineColumn(char c)
    {
        column++;
        if(prevCharIsLF)
        {
            prevCharIsLF = false;
            line += column = 1;
        } else
        if(prevCharIsCR)
        {
            prevCharIsCR = false;
            if(c == '\n')
                prevCharIsLF = true;
            else
                line += column = 1;
        }
        switch(c)
        {
        case 13: // '\r'
            prevCharIsCR = true;
            break;

        case 10: // '\n'
            prevCharIsLF = true;
            break;

        case 9: // '\t'
            column--;
            column += 8 - (column & 7);
            break;
        }
        bufline[bufpos] = line;
        bufcolumn[bufpos] = column;
    }

    public static final boolean staticFlag = true;
    public static int bufpos = -1;
    static int bufsize;
    static int available;
    static int tokenBegin;
    private static int bufline[];
    private static int bufcolumn[];
    private static int column = 0;
    private static int line = 1;
    private static Reader inputStream;
    private static boolean prevCharIsCR = false;
    private static boolean prevCharIsLF = false;
    private static char nextCharBuf[];
    private static char buffer[];
    private static int maxNextCharInd = 0;
    private static int nextCharInd = -1;
    private static int inBuf = 0;

}