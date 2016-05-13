package com.mindbox.pe.client.common.tree;

import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.applet.UIFactory;

/**
 * Contains a selection tree with collapse/expand all buttons.
 * <B>Note: <B>Be sure to set the model of the tree.
 *
 */
public abstract class AbstractSelectionTree {

	/**
	 * Tree widget.
	 */
	protected final JTree tree;
	protected final JButton expandAllButton;
	protected final JButton collapseAllButton;

	protected AbstractSelectionTree(int selectionMode, final boolean showRoot, boolean showRootHandles, boolean showCollapseExpandButtons,
			boolean sort) {
		tree = new JTree();

		tree.getSelectionModel().setSelectionMode(selectionMode);
		tree.putClientProperty("JTree.linestyle", "Angled");
		tree.setShowsRootHandles(showRootHandles);
		tree.setRootVisible(showRoot);
		tree.setEditable(false);

		tree.setExpandsSelectedPaths(true);
		tree.setScrollsOnExpand(true);
		tree.setDoubleBuffered(true);

		if (showCollapseExpandButtons) {
			expandAllButton = UIFactory.createButton("", "image.btn.expandall", null, "button.tooltip.expandall", false);
			expandAllButton.setFocusable(false);
			expandAllButton.setBackground(null);
			expandAllButton.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent actionevent) {
					try {
						getJComponent().setCursor(UIFactory.getWaitCursor());
						expandAll(true);
					}
					finally {
						getJComponent().setCursor(UIFactory.getDefaultCursor());
					}
				}
			});
			collapseAllButton = UIFactory.createButton("", "image.btn.collapseall", null, "button.tooltip.collapeseall", false);
			collapseAllButton.setFocusable(false);
			collapseAllButton.setBackground(null);
			collapseAllButton.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent actionevent) {
					try {
						getJComponent().setCursor(UIFactory.getWaitCursor());
						expandAll(false);
						if (showRoot) {
							expandAll(false);
						}
						else {
							TreeNode rootNode = (TreeNode) tree.getModel().getRoot();
							for (int i = 0; i < rootNode.getChildCount(); i++) {
								tree.collapsePath(getTreePath(rootNode.getChildAt(i)));
							}
						}
					}
					finally {
						getJComponent().setCursor(UIFactory.getDefaultCursor());
					}
				}
			});
		}
		else {
			expandAllButton = null;
			collapseAllButton = null;
		}
	}

	protected abstract JPanel getJComponent();

	protected final TreePath getTreePath(TreeNode node) {
		LinkedList<TreeNode> parentList = new LinkedList<TreeNode>();
		parentList.add(node);
		for (TreeNode parent = node.getParent(); parent != null;) {
			parentList.add(0, parent);
			parent = parent.getParent();
		}
		TreePath path = new TreePath(parentList.toArray(new TreeNode[0]));
		return path;
	}

	public final void expandAll(boolean expand) {
		TreeUtil.expandAll(tree, expand);
	}

	public final void setEnabled(boolean enabled) {
		tree.setEditable(enabled);
	}

	public final void addMouseListener(MouseListener mouseListener) {
		tree.addMouseListener(mouseListener);
	}

	public final void removeMouseListener(MouseListener mouseListener) {
		tree.removeMouseListener(mouseListener);
	}

	public final void addTreeWillExpandListener(TreeWillExpandListener treeWillExpandListener) {
		tree.addTreeWillExpandListener(treeWillExpandListener);
	}

	public final void removeTreeWillExpandListener(TreeWillExpandListener treeWillExpandListener) {
		tree.addTreeWillExpandListener(treeWillExpandListener);
	}

	public final void addTreeSelectionListener(TreeSelectionListener treeSelectionListener) {
		tree.addTreeSelectionListener(treeSelectionListener);
	}

	public final void removeTreeSelectionListener(TreeSelectionListener treeSelectionListener) {
		tree.removeTreeSelectionListener(treeSelectionListener);
	}

	public void clearSelection() {
		tree.clearSelection();
	}

	public final void expandPath(TreePath path) {
		tree.expandPath(path);
	}

	public final TreeSelectionModel getTreeSelectionModel() {
		return tree.getSelectionModel();
	}

	public final void setTreeCellRenderer(TreeCellRenderer renderer) {
		tree.setCellRenderer(renderer);
	}
	
	public final void scrollPathToVisible(TreePath treePath) {
		tree.scrollPathToVisible(treePath);
	}
}
