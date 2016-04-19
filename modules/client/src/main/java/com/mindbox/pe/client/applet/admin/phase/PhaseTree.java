/*
 * Created on Jan 6, 2004
 */
package com.mindbox.pe.client.applet.admin.phase;

import java.awt.Component;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseReference;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor - Generic 2.2.0
 */
final class PhaseTree extends JTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private class PhaseTreeCellRenderer extends DefaultTreeCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		public Component getTreeCellRendererComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i, boolean flag3) {
			String value = null;
			if (obj instanceof PhaseNode) {
				Phase phase = ((PhaseNode) obj).getPhase();
				if (phase instanceof PhaseReference) {
					phase = ((PhaseReference) phase).getReferecePhase();
					setLeafIcon(refIcon);
					setOpenIcon(refIcon);
				}
				else {
					setLeafIcon(icon);
					setOpenIcon(icon);
				}
				value = phase.getDisplayName() + " (" + (phase.getPhaseTask() == null ? "has no task" : "task: " + phase.getPhaseTask().getName()) + ")";
			}
			else {
				value = obj.toString();
			}
			Component component = super.getTreeCellRendererComponent(jtree, value, flag, flag1, flag2, i, flag3);
			return component;
		}

		private final ImageIcon icon = ClientUtil.getInstance().makeImageIcon("image.node.phase");
		private final ImageIcon refIcon = ClientUtil.getInstance().makeImageIcon("image.node.phase.reference");

		PhaseTreeCellRenderer() {
			setOpenIcon(icon);
			setClosedIcon(icon);
			setDisabledIcon(icon);
			setLeafIcon(icon);
		}
	}

	public PhaseTree(TreeModel model, boolean showRoot) {
		super();
		setExpandsSelectedPaths(true);
		setScrollsOnExpand(true);
		setModel(model);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		setCellRenderer(new PhaseTreeCellRenderer());
		putClientProperty("JTree.linestyle", "Angled");
		setShowsRootHandles(true);
		setRootVisible(showRoot);
		setRowHeight(18);
		expandRow(0);
		setEditable(false);
	}

	public Phase getSelectedPhase() {
		TreePath categoryPath = getSelectionPath();
		if (categoryPath != null) {
			return ((PhaseNode) categoryPath.getLastPathComponent()).getPhase();
		}
		else {
			return null;
		}
	}

	public void selectPhase(int phaseID) {
		if (phaseID < 0) {
			clearSelection();
		}
		else {
			PhaseNode node = findNode(phaseID);
			if (node != null) {
				TreePath path = getTreePath(node);
				setSelectionPath(path);
				expandPath(path);
			}
		}
	}

	private TreePath getTreePath(PhaseNode node) {
		LinkedList<TreeNode> parentList = new LinkedList<TreeNode>();
		parentList.add(node);
		for (TreeNode parent = node.getParent(); parent != null;) {
			parentList.add(0, parent);
			parent = parent.getParent();
		}
		return new TreePath(parentList.toArray(new TreeNode[0]));
	}

	private PhaseNode findNode(int catID) {
		PhaseNode node = null;
		for (Enumeration<?> enumeration = ((PhaseNode) getModel().getRoot()).children(); enumeration.hasMoreElements() && node == null;) {
			node = findNode((PhaseNode) enumeration.nextElement(), catID);
		}
		return node;
	}

	private PhaseNode findNode(PhaseNode parent, int phaseID) {
		if (parent.getPhase().getID() == phaseID) {
			return parent;
		}
		else if (parent.isLeaf()) {
			return null;
		}
		else {
			PhaseNode node = null;
			for (Enumeration<?> enumeration = parent.children(); enumeration.hasMoreElements() && node == null;) {
				node = findNode((PhaseNode) enumeration.nextElement(), phaseID);
			}
			return node;
		}
	}
}