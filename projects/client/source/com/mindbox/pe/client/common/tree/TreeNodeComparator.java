package com.mindbox.pe.client.common.tree;

import java.util.Comparator;

import javax.swing.tree.TreeNode;

/**
 * Domain class comparator.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class TreeNodeComparator implements Comparator<TreeNode> {

	public TreeNodeComparator() {
		super();
	}

	public int compare(TreeNode dc1, TreeNode dc2) {
		if (dc1 == dc2) return 0;
		return dc1.toString().compareTo(dc2.toString());
	}
}
