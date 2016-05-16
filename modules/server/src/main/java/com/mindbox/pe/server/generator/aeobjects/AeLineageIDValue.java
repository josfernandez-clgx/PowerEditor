/*
 * Created on Jul 22, 2003
 */
package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class AeLineageIDValue extends AbstractAeValue {

	/**
	 * @param node node
	 */
	public AeLineageIDValue(Node node) {
		super(node);
	}

	@Override
	public String toString() {
		return "%lineageID%";
	}

}
