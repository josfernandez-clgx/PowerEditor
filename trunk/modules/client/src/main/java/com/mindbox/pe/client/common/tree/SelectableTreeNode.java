package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeNode;

public interface SelectableTreeNode extends TreeNode {
	
	void setSelected(boolean flag);

	boolean isSelected();

}
