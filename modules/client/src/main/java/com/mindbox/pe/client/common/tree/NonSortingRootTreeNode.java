package com.mindbox.pe.client.common.tree;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.TreeNode;

/**
 *
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class NonSortingRootTreeNode implements TreeNode {

	private static class IteratorEnum implements Enumeration<Object> {

		private final Iterator<Object> iter;

		public IteratorEnum(List<Object> list) {
			this.iter = list.iterator();
		}

		@Override
		public boolean hasMoreElements() {
			return iter.hasNext();
		}

		@Override
		public Object nextElement() {
			return iter.next();
		}

	}

	private final LinkedList<Object> children;
	protected Object data;

	/**
	 *
	 */
	public NonSortingRootTreeNode(Object data) {
		super();
		this.data = data;
		this.children = new LinkedList<Object>();
	}

	public final void addChild(int index, TreeNode node) {
		synchronized (children) {
			this.children.add(index, node);
		}
	}

	public final void addChild(TreeNode node) {
		synchronized (children) {
			this.children.add(node);
		}
	}

	@Override
	public final Enumeration<?> children() {
		synchronized (children) {
			return new IteratorEnum(children);
		}
	}

	public final boolean containsChild(TreeNode node) {
		return children.contains(node);
	}

	public final List<Object> getAllChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public final TreeNode getChildAt(int index) {
		synchronized (children) {
			return (TreeNode) children.get(index);
		}
	}

	@Override
	public final int getChildCount() {
		synchronized (children) {
			return children.size();
		}
	}

	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	@Override
	public TreeNode getParent() {
		return null;
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

	public final void removeChild(TreeNode node) {
		synchronized (children) {
			children.remove(node);
		}
	}

	/**
	 * 
	 * @param index1 index1
	 * @param index2 index2
	 */
	public void swapChildren(int index1, int index2) {
		if (index1 == index2) return;
		synchronized (children) {
			Object obj1 = children.get(index1);
			children.set(index1, children.get(index2));
			children.set(index2, obj1);
		}
	}

	@Override
	public String toString() {
		return (data == null ? "NULL" : data.toString());
	}
}
