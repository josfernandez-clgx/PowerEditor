package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AeInstanceAttrValue extends AbstractAeValue {

	private String classOrObjectName;
	private String attributeName;

	public AeInstanceAttrValue(Node node) {
		super(node);
	}

	public String getClassOrObjectName() {
		return classOrObjectName;
	}

	public void setClassOrObjectName(String s) {
		this.classOrObjectName = s;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String s) {
		this.attributeName = s;
	}

	public String toString() {
		return "AeInstanceAttrValue["+classOrObjectName+"." + attributeName+"]";
	}
	
	public int hashCode() {
		return (classOrObjectName+"."+attributeName).hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof AeInstanceAttrValue) {
			return this.hashCode() == obj.hashCode();
		}
		else {
			return false;
		}
	}
	
}