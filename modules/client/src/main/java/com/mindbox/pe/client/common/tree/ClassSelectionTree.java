/*
 * Created on Jan 6, 2004
 */
package com.mindbox.pe.client.common.tree;

import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.model.domain.DomainClass;

/**
 * Domain class selection tree.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 4.0
 */
public final class ClassSelectionTree extends JTree {
	/**
	 * 
	 */

	private static final long serialVersionUID = -3951228734910107454L;

	public ClassSelectionTree() {
		super();
		setExpandsSelectedPaths(true);
		setScrollsOnExpand(true);
		setModel(EntityModelCacheFactory.getInstance().getAttributeTreeModel());
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		setCellRenderer(new DomainClassAttributeTreeRenderer());
		putClientProperty("JTree.linestyle", "Angled");
		setShowsRootHandles(true);
		setRootVisible(false);
		setRowHeight(18);
		setEditable(false);
	}

	public boolean isClassNodeSelected() {
		TreePath treePath = getSelectionPath();
		if (treePath != null) {
			return (treePath.getLastPathComponent() instanceof DomainTreeNode);
		}
		else {
			return false;
		}
	}

	public DomainClass getSelectedDomainClass() {
		TreePath treePath = getSelectionPath();
		if (treePath != null) {
			if (treePath.getLastPathComponent() instanceof DomainTreeNode) {
				return ((DomainTreeNode) treePath.getLastPathComponent()).getDomainClass();
			}
		}
		return null;
	}


	public void selectDomainClass(String className) {
		if (className != null) {
			if (DomainModel.getInstance().getDomainClass(className) != null) {
				selectDomainClass(DomainModel.getInstance().getDomainClass(className));
			}
		}
	}

	public void selectDomainClass(DomainClass dc) {
		if (dc == null) {
			clearSelection();
		}
		else {
			DomainTreeNode node = findNode(dc);
			if (node != null) {
				TreePath path = getTreePath(node);
				setSelectionPath(path);
				expandPath(path);
			}
		}
	}

	private DomainTreeNode findNode(DomainClass dc) {
		DomainTreeNode node = null;
		for (Enumeration<?> enumeration = ((RootTreeNode) getModel().getRoot()).children(); enumeration.hasMoreElements() && node == null;) {
			node = findNode((DomainTreeNode) enumeration.nextElement(), dc);
		}
		return node;
	}

	private DomainTreeNode findNode(DomainTreeNode parent, DomainClass dc) {
		if (parent.getDomainClass() == dc) return parent;
		DomainTreeNode node = null;
		for (Enumeration<?> enumeration = parent.children(); enumeration.hasMoreElements() && node == null;) {
			node = findNode((DomainTreeNode) enumeration.nextElement(), dc);
		}
		return node;
	}

	private TreePath getTreePath(DomainTreeNode node) {
		LinkedList<TreeNode> parentList = new LinkedList<TreeNode>();
		parentList.add(node);
		for (TreeNode parent = node.getParent(); parent != null;) {
			parentList.add(0, parent);
			parent = parent.getParent();
		}
		return new TreePath(parentList.toArray(new TreeNode[0]));
	}
}
