/*
 * Created on Jun 11, 2003
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
 * @since PowerEditor 1.0
 */
public class AeNameValue extends AbstractAeValue {

	private String name = null;
	
	public AeNameValue(Node node) {
		super(node);
	}

	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}

}
