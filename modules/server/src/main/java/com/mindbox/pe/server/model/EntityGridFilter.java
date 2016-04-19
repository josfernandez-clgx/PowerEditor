// Decompiled by DJ v3.0.0.63 Copyright 2002 Atanas Neshkov  Date: 8/8/2002 2:56:55 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   EntityGridFilter.java

package com.mindbox.pe.server.model;


public class EntityGridFilter
{

    public String toString()
    {
        return "EntityGridFilter for type=" + getTemplateType();
    }

    public EntityGridFilter(String s, int ai[])
    {
        setTemplateType(s);
        setEntityIds(ai);
    }

    public void setTemplateType(String s)
    {
        mTemplateType = s;
    }

    public String getTemplateType()
    {
        return mTemplateType;
    }

    public void setEntityIds(int ai[])
    {
        mEntityIds = ai;
    }

    public int[] getEntityIds()
    {
        return mEntityIds;
    }

    private String mTemplateType;
    private int mEntityIds[];
}