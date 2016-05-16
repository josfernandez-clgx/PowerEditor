/*
 * Created on 2004. 1. 28.
 *
 */
package com.mindbox.pe.client.applet.template.tree;

import javax.swing.tree.TreeNode;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public final class ThenTreeNode extends AbstractRuleTreeNode {

	public ThenTreeNode(TreeNode parent) {
		super(parent, null);
	}

	public ActionTreeNode getActionNode() {
		return (ActionTreeNode) getChildAt(0);
	}

	@Override
	public boolean getAllowsChildren() {
		return super.getChildCount() == 0;
	}

	public boolean hasActionNode() {
		return super.getChildCount() > 0;
	}

}
