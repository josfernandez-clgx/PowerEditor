// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 3:02:11 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   RuleParser.java

package com.mindbox.server.parser.jtb.rule;

import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken;

// Referenced classes of package com.mindbox.server.parser.jtb.rule:
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