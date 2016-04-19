package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AeActivationDateValue extends AbstractAeValue {

	public AeActivationDateValue(Node node) {
		super(node);
	}

	public String toString() {
		return "%activationDate%";
	}

}