/*
 * Created on 2004. 8. 9.
 */
package com.mindbox.pe.model.rule;

import java.io.Serializable;

import com.mindbox.pe.common.UtilBase;

/**
 * Immutable implementation of Reference.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
class ReferenceImpl implements Reference, Serializable {

	private static final long serialVersionUID = 20040809200001L;

	private String cn;
	private String an;

	ReferenceImpl(String className, String attribName) {
		if (className == null) throw new NullPointerException("className cannot be null");
		//if (attribName == null) throw new NullPointerException("attribName cannot be null");
		this.cn = className;
		this.an = attribName;
	}

	ReferenceImpl(Reference ref) {
		this.cn = ref.getClassName();
		this.an = ref.getAttributeName();
	}

	public String getClassName() {
		return cn;
	}

	public String getAttributeName() {
		return an;
	}

	public void setClassName(String className) {
		this.cn = className;
	}

	public void setAttributeName(String attributeName) {
		this.an = attributeName;
	}

	public String toString() {
		return cn + "." + an;
	}
	
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Reference) {
			return UtilBase.isSame(cn, ((Reference)obj).getClassName()) && UtilBase.isSame(an, ((Reference)obj).getAttributeName());
		}
		else {
			return false;
		}
	}

}