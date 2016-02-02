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
public final class IfTreeNode extends AbstractRuleTreeNode implements LogicalOpAttachable {

	/**
	 * @param parent the parent
	 */
	public IfTreeNode(TreeNode parent) {
		super(parent, null);
	}

}
