/*
 * Created on 2004. 1. 19.
 *
 */
package com.mindbox.pe.server.generator.aeobjects;

import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 1.2.0
 */
public class AeReferenceValue extends AbstractAeValue {

	private final List<String> nameList;

	/**
	 * @param node
	 */
	public AeReferenceValue(Node node) {
		super(node);
		this.nameList = new LinkedList<String>();
	}

	public void addName(String name) {
		this.nameList.add(name);
	}

	public String[] getNames() {
		return nameList.toArray(new String[0]);
	}

	public String toString() {
		StringBuffer buff = new StringBuffer("Reference[");
		if (!nameList.isEmpty()) {
			buff.append(nameList.get(0));
			for (int i = 1; i < nameList.size(); i++) {
				buff.append(".");
				buff.append(nameList.get(i));
			}
		}
		buff.append("]");
		return buff.toString();
	}
}
