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
		public boolean hasMoreElements() {
			return iter.hasNext();
		}

		public TreeNode nextElement() {
			return iter.next();
		}

	}

	private List<TreeNode> children;
	protected Object data;

	/**
	 *
	 */
	public RootTreeNode(Object data) {
		super();
		this.data = data;
		this.children = new ArrayList<TreeNode>();
	}
	
	public void clear() {
		this.data = null;
		this.children = null;
	}

	public final void removeAllChildren() {
		synchronized (children) {
			children.clear();
		}
	}

	public final TreeNode getChildAt(int index) {
		synchronized (children) {
			return children.get(index);
		}
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
	
	public final int getChildCount() {
		synchronized (children) {
			return children.size();
		}
	}

	public int getIndex(TreeNode arg0) {
		return 0;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public final boolean isLeaf() {
		synchronized (children) {
			return children.isEmpty();
		}
	}

	public final boolean containsChild(TreeNode node) {
		return children.contains(node);
	}

	public final Enumeration<TreeNode> children() {
		synchronized (children) {
			return new IteratorEnum(children);
		}
	}

	public TreeNode getParent() {
		return null;
	}

	public final void addChild(TreeNode node, boolean sort) {
		synchronized (children) {
			this.children.add(node);
			if(sort){
			   Collections.sort(children, new TreeNodeComparator());	
			}
		}
	}
	
	public final void removeChild(TreeNode node, boolean sort) {
		synchronized (children) {
			this.children.remove(node);
			if(sort){
			Collections.sort(children, new TreeNodeComparator());
			}
		}
	}

	public String toString() {
		return (data == null ? "NULL" : data.toString());
	}
}
