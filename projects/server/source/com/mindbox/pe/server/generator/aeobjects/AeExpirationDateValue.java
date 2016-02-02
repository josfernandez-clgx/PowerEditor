package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AeExpirationDateValue extends AbstractAeValue {

	public AeExpirationDateValue(Node node) {
		super(node);
	}

	public String toString() {
		return "%expirationDate%";
	}

}