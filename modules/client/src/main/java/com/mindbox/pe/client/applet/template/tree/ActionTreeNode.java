package com.mindbox.pe.client.applet.template.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.model.rule.FunctionCall;
import com.mindbox.pe.model.rule.RuleAction;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class ActionTreeNode extends FunctionTreeNode {

	/**
	 * @param data
	 */
	public ActionTreeNode(TreeNode parent, RuleAction data) {
		super(parent, (FunctionCall)data);
	}
	
	public RuleAction getRuleAction() {
		return (RuleAction) super.data;
	}

	public String dispString(boolean selected) {
		if (super.data == null || getRuleAction().getActionType() == null) {
			return "";
		}
		else {
			return getRuleAction().getActionType().getName();
		}
	}
}
