package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AeLiteralValue extends AbstractAeValue {

	private Object value;

	public AeLiteralValue(Node node) {
		this(node, null);
	}

	public AeLiteralValue(Node node, Object value) {
		super(node);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object obj) {
		this.value = obj;
	}

	public String toString() {
		if (value == null)
			return "<null>";
		else
			return "Literal["+value.toString()+"]";
	}

}