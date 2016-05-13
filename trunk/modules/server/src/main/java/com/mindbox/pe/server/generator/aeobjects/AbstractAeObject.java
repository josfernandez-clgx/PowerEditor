package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AbstractAeObject {

	private Node node;

	public AbstractAeObject(Node node) {
		setNode(node);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public String toString() {
		return node.toString();
	}
}