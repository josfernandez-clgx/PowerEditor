// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Synonym.java

package com.mindbox.pe.model;

import java.io.Serializable;

public class Synonym implements Serializable {

	private static final long serialVersionUID = -9167365766210984101L;

	public void setValue(String s) {
		mValue = s;
	}

	public String getValue() {
		return mValue;
	}

	public String toString() {
		return "Synonym: " + getName() + "; Type=" + getSynonymType() + "; Value = " + getValue();
	}

	public Synonym(String s, String s1, String s2) {
		setName(s);
		setValue(s1);
		setSynonymType(s2);
	}

	public void setSynonymType(String s) {
		mSynonymType = s;
	}

	public String getSynonymType() {
		return mSynonymType;
	}

	public void setName(String s) {
		mName = s;
	}

	public String getName() {
		return mName;
	}

	private String mName;
	private String mValue;
	private String mSynonymType;
}
