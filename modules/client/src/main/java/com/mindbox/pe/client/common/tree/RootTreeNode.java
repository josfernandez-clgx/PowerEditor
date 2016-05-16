package com.mindbox.pe.client.common.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.TreeNode;

/**
 *
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class RootTreeNode implements TreeNode {

	private static class IteratorEnum implements Enumeration<TreeNode> {
		private final Iterator<TreeNode> iter;

		public IteratorEnum(List<TreeNode> list) {
			this.iter = list.iterator();
		}

		@Override
		public boolean hasMoreElements() {
			return iter.hasNext();
		}

		@Override
		public TreeNode nextElement() {
			return iter.next();
		}

	}

	private List<TreeNode> children;
	protected Object data;

	public RootTreeNode(Object data) {
		super();
		this.data = data;
		this.children = new ArrayList<TreeNode>();
	}

	public final void addChild(TreeNode node, boolean sort) {
		synchronized (children) {
			this.children.add(node);
			if (sort) {
				Collections.sort(children, new TreeNodeComparator());
			}
		}
	}

	@Override
	public final Enumeration<TreeNode> children() {
		synchronized (children) {
			return new IteratorEnum(children);
		}
	}

	public void clear() {
		this.data = null;
		this.children = null;
	}

	public final boolean containsChild(TreeNode node) {
		return children.contains(node);
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public final TreeNode getChildAt(int index) {
		synchronized (children) {
			return children.get(index);
		}
	}

	@Override
	public final int getChildCount() {
		synchronized (children) {
			return children.size();
		}
	}

	@Override
	public int getIndex(TreeNode arg0) {
		return 0;
	}

	@Override
	public TreeNode getParent() {
		return null;
	}

	public final int indexOfChild(TreeNode node) {
		TreeNode child;
		synchronized (children) {
			for (int i = 0; i < children.size(); i++) {
				child = children.get(i);
				if (node == child || node.equals(child)) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public final boolean isLeaf() {
		synchronized (children) {
			return children.isEmpty();
		}
	}

	public final void removeAllChildren() {
		synchronized (children) {
			children.clear();
		}
	}

	public final void removeChild(TreeNode node, boolean sort) {
		synchronized (children) {
			this.children.remove(node);
			if (sort) {
				Collections.sort(children, new TreeNodeComparator());
			}
		}
	}

	@Override
	public String toString() {
		return (data == null ? "NULL" : data.toString());
	}
}
