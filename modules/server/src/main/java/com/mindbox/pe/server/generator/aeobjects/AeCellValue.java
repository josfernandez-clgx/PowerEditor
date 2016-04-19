package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AeCellValue extends AbstractAeValue {

	public AeCellValue(Node node) {
		super(node);
	}

	public String toString() {
		return "%cellValue%";
	}

}