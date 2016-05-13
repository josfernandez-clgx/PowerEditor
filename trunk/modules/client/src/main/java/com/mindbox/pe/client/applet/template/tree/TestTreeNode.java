/*
 * Created on 2004. 1. 28.
 *
 */
package com.mindbox.pe.client.applet.template.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.model.rule.FunctionCall;
import com.mindbox.pe.model.rule.TestCondition;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class TestTreeNode extends FunctionTreeNode {

	/**
	 * @param data
	 */
	public TestTreeNode(TreeNode parent, TestCondition data) {
		super(parent, (FunctionCall)data);
	}
	
	public TestCondition getTestCondition() {
		return (TestCondition) super.data;
	}

}
