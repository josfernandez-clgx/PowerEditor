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
public class AeCategoryNameValue extends AbstractAeValue {

	/**
	 * @param node node
	 */
	public AeCategoryNameValue(Node node) {
		super(node);
	}

	@Override
	public String toString() {
		return "%categoryName%";
	}

}
