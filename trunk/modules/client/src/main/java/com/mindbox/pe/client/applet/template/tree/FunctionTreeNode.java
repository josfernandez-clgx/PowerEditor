/*
 * Created on 2004. 1. 28.
 *
 */
package com.mindbox.pe.client.applet.template.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.model.rule.FunctionCall;
import com.mindbox.pe.model.rule.FunctionParameter;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class FunctionTreeNode extends AbstractRuleTreeNode {

	/**
	 * @param data
	 */
	public FunctionTreeNode(TreeNode parent, FunctionCall data) {
		super(parent, data);
		refreshChildren_internal();
	}
	
	public void refreshChildren() {

		refreshChildren_internal();
	}
	
	private void refreshChildren_internal() {
		removeAllChildren();
		for (int i = 0; i < getFunctionCall().size(); i++) {
			addChild(new ActionParamTreeNode(this, (FunctionParameter)getFunctionCall().get(i)));
		}
	}

	public FunctionCall getFunctionCall() {
		return (FunctionCall) super.data;
	}

	public ActionParamTreeNode getActionParamNodeAt(int index) {
		return (ActionParamTreeNode) super.getChildAt(index);
	}

	public String dispString() {
		if (super.data != null && getFunctionCall().getFunctionType() != null) {
			return getFunctionCall().getFunctionType().getName();
		}
		else {
			return "";
		}
	}

}
