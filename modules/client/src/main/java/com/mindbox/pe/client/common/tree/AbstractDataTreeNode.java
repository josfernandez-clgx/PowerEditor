package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeNode;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public abstract class AbstractDataTreeNode extends RootTreeNode {

	private TreeNode parent;

	protected AbstractDataTreeNode(TreeNode parent, Object data) {
		super(data);
		this.parent = parent;
	}

	@Override
	public void clear() {
		super.clear();
		this.parent = null;
	}

	public final Object getData() {
		return super.data;
	}

	@Override
	public final TreeNode getParent() {
		return parent;
	}
}