/*
 * Created on 2004. 1. 19.
 *
 */
package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 1.2.0
 */
public class AeMessageLiteral extends AbstractAeValue {

	/**
	 * @param node
	 */
	public AeMessageLiteral(Node node) {
		super(node);
	}
	
	public String toString() {
		return "%message%";
	}

}
