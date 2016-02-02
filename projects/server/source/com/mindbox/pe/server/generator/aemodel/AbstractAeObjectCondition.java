// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 2:52:11 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AbstractAeObjectCondition.java

package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

// Referenced classes of package com.mindbox.server.generator:
//            AbstractAeObject, AeObjectPatternSet, AeRule

public class AbstractAeObjectCondition extends AbstractAeObject
{

    public String toString()
    {
        return "lhs: nested in=" + (getParentPatternSet() != null ? getParentPatternSet().toString() : "null");
    }

    public AbstractAeObjectCondition(Node pNode)
    {
        super(pNode);
    }

    public AeRule getParentRule()
    {
        if(mParent != null)
            return mParent;
        if(getParentPatternSet() != null)
            return getParentPatternSet().getParentRule();
        else
            return null;
    }

    public void setParentRule(AeRule pParent)
    {
        mParent = pParent;
    }

    public AeObjectPatternSet getParentPatternSet()
    {
        return mParentPatternSet;
    }

    public void setParentPatternSet(AeObjectPatternSet pParentPatternSet)
    {
        mParentPatternSet = pParentPatternSet;
    }

    private AeObjectPatternSet mParentPatternSet;
    private AeRule mParent;
}