/*
 * Created on Jan 6, 2004
 */
package com.mindbox.pe.client.common.tree;

import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor - Generic 2.2.0
 */
public final class AttributeSelectionTree extends JTree {

	public AttributeSelectionTree() {
		this(null);
	}

	public AttributeSelectionTree(int[] genericDataTypes) {
		super();
		setExpandsSelectedPaths(true);
		setScrollsOnExpand(true);
		setModel(EntityModelCacheFactory.getInstance().getAttributeTreeModel(genericDataTypes));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		setCellRenderer(new DomainClassAttributeTreeRenderer());
		putClientProperty("JTree.linestyle", "Angled");
		setShowsRootHandles(true);
		setRootVisible(false);
		setRowHeight(18);
		setEditable(false);
	}

	public boolean isAttributeNodeSelected() {
		TreePath treePath = getSelectionPath();
		if (treePath != null) {
			return (treePath.getLastPathComponent() instanceof AttributeTreeNode);
		}
		else {
			return false;
		}
	}

	public DomainAttribute getSelectedAttribute() {
		TreePath treePath = getSelectionPath();
		if (treePath != null) {
			if (treePath.getLastPathComponent() instanceof AttributeTreeNode) { return ((AttributeTreeNode) treePath.getLastPathComponent()).getDomainAttribute(); }
		}
		return null;
	}

	public DomainClass getClassOfSelectedAttribute() {
		TreePath treePath = getSelectionPath();
		if (treePath != null) {
			if (treePath.getLastPathComponent() instanceof AttributeTreeNode) { return ((DomainWithAttributeTreeNode) ((AttributeTreeNode) treePath.getLastPathComponent()).getParent()).getDomainClass(); }
		}
		return null;
	}

	public void selectAttribute(String refString) {
		if (refString != null) {
			String[] strs = refString.split("\\.");
			if (strs.length > 1) {
				selectAttribute(strs[0], strs[1]);
			}
		}
	}

	public void selectAttribute(String className, String attribName) {
		if (className != null && attribName != null) {
			if (DomainModel.getInstance().getDomainClass(className) != null) {
				if (DomainModel.getInstance().getDomainClass(className).getDomainAttribute(attribName) != null) {
					selectAttribute(DomainModel.getInstance().getDomainClass(className).getDomainAttribute(attribName));
				}
			}
		}
	}

	public void selectAttribute(DomainAttribute dc) {
		if (dc == null) {
			clearSelection();
		}
		else {
			AttributeTreeNode node = findNode(dc);
			if (node != null) {
				TreePath path = getTreePath(node);
				setSelectionPath(path);
				expandPath(path);
			}
		}
	}

	private AttributeTreeNode findNode(DomainAttribute dc) {
		AttributeTreeNode node = null;
		for (Enumeration<?> enumeration = ((RootTreeNode) getModel().getRoot()).children(); enumeration.hasMoreElements() && node == null;) {
			node = findNode((DomainWithAttributeTreeNode) enumeration.nextElement(), dc);
		}
		return node;
	}

	private AttributeTreeNode findNode(DomainWithAttributeTreeNode parent, DomainAttribute dc) {
		AttributeTreeNode node = null;
		for (Enumeration<?> enumeration = parent.children(); enumeration.hasMoreElements() && node == null;) {
			node = findNode((AttributeTreeNode) enumeration.nextElement(), dc);
		}
		return node;
	}

	private AttributeTreeNode findNode(AttributeTreeNode parent, DomainAttribute dc) {
		if (parent.getDomainAttribute() == dc) {
			return parent;
		}
		else {
			return null;
		}
	}

	private TreePath getTreePath(AttributeTreeNode node) {
		LinkedList<TreeNode> parentList = new LinkedList<TreeNode>();
		parentList.add(node);
		for (TreeNode parent = node.getParent(); parent != null;) {
			parentList.add(0, parent);
			parent = parent.getParent();
		}
		return new TreePath(parentList.toArray(new TreeNode[0]));
	}

	public void filterAttributes(int[] genericDataTypes) {
		DefaultTreeModel tm = (DefaultTreeModel) EntityModelCacheFactory.getInstance().getAttributeTreeModel(genericDataTypes);
		setModel(tm);
		tm.nodeChanged((TreeNode) tm.getRoot());
	}
}
