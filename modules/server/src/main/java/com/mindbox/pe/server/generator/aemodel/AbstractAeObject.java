package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

public class AbstractAeObject {

	public AbstractAeObject(Node node) {
		setNode(node);
	}

	public AbstractAeObject() {
	}
	
	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public String toString() {
		return (node == null ? "" : node.toString());
	}
	private Node node;
}