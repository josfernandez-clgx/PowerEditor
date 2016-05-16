/*
 * Created on Jul 22, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class AeRuleName extends AbstractAeValue {

	/**
	 * @param node node
	 */
	public AeRuleName(Node node) {
		super(node);
	}

	@Override
	public String toString() {
		return "%ruleName%";
	}

}
