package com.mindbox.pe.server.generator.aeobjects;

import java.util.List;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;

public class AeInstance extends AbstractAeObject {

	private String objectName;
	private String className;
	private List<Object> attributeList;

	public AeInstance(Node node) {
		super(node);
	}


	public void setClassName(String s) {
		this.className = s;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String s) {
		this.objectName = s;
	}

	public List<Object> getInstanceAttributes() {
		return attributeList;
	}

	public void setInstanceAttributes(List<Object> list) {
		this.attributeList = list;
	}

	public String getClassName() {
		return className;
	}

}