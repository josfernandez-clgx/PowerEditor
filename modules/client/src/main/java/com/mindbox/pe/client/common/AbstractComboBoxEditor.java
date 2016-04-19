// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AbstractComboBoxEditor.java

package com.mindbox.pe.client.common;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxEditor;
import javax.swing.event.EventListenerList;

public abstract class AbstractComboBoxEditor
    implements ComboBoxEditor
{

    public abstract Component getEditorComponent();

    protected void fireActionPerformed(ActionEvent actionevent)
    {
        Object aobj[] = listenerList.getListenerList();
        for(int i = aobj.length - 2; i >= 0; i -= 2)
            if(aobj[i] == (java.awt.event.ActionListener.class))
                ((ActionListener)aobj[i + 1]).actionPerformed(actionevent);

    }

    public abstract void setItem(Object obj);

    public abstract Object getItem();

    public AbstractComboBoxEditor()
    {
        listenerList = new EventListenerList();
    }

    public void removeActionListener(ActionListener actionlistener)
    {
        listenerList.remove(java.awt.event.ActionListener.class, actionlistener);
    }

    public void addActionListener(ActionListener actionlistener)
    {
        listenerList.add(java.awt.event.ActionListener.class, actionlistener);
    }

    public abstract void selectAll();

    EventListenerList listenerList;
}
