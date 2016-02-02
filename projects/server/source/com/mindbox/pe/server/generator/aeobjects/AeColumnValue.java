package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AeColumnValue extends AbstractAeValue {

	private int columnNumber;

	public AeColumnValue(Node node) {
		super(node);
	}

	public String toString() {
		return "Column " + getColumnNumber();
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int i) {
		this.columnNumber = i;
	}

}