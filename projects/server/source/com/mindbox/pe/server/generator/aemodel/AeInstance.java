package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;
import java.util.List;

@SuppressWarnings("unchecked")
public class AeInstance extends AbstractAeObject
{

    public void setClassName(String s)
    {
        mClassName = s;
    }

    public AeInstance(Node node)
    {
        super(node);
    }

    public String getObjectName()
    {
        return mObjectName;
    }

    public void setObjectName(String s)
    {
        mObjectName = s;
    }

    public List getInstanceAttributes()
    {
        return mInstanceAttributes;
    }

    public void setInstanceAttributes(List list)
    {
        mInstanceAttributes = list;
    }

    public String getClassName()
    {
        return mClassName;
    }

    private String mObjectName;
    private String mClassName;
    private List mInstanceAttributes;
}