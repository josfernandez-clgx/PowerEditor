// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:00:34 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   MessageParser.java

package com.mindbox.server.parser.jtb.msg;

import com.mindbox.server.parser.jtb.msg.syntaxtree.NodeToken;

// Referenced classes of package com.mindbox.server.parser.jtb.msg:
//            Token

class JTBToolkit
{

    JTBToolkit()
    {
    }

    static NodeToken makeNodeToken(Token token)
    {
        return new NodeToken(token.image.intern(), token.kind, token.beginLine, token.beginColumn, token.endLine, token.endColumn);
    }
}