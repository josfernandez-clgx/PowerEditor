package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;
import java.util.List;

@SuppressWarnings("unchecked")
public class AeLhs extends AeObject
{

    public AeLhs(Node pNode)
    {
        super(pNode);
    }

    public List getObjectPatterns()
    {
        return mObjectPatterns;
    }

    public void setObjectPatterns(List pObjectPatterns)
    {
        mObjectPatterns = pObjectPatterns;
    }

    private List mObjectPatterns;
}