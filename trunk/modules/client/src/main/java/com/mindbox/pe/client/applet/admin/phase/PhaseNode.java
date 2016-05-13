/*
 * Created on 2004. 2. 26.
 *
 */
package com.mindbox.pe.client.applet.admin.phase;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.client.common.tree.NonSortingRootTreeNode;
import com.mindbox.pe.model.process.Phase;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public final class PhaseNode extends NonSortingRootTreeNode {
	
	private NonSortingRootTreeNode parent = null;

	public PhaseNode(NonSortingRootTreeNode parent,Phase phase) {
		super(phase);
		this.parent = parent;
		Phase[] children = phase.getSubPhases();
		for (int i = 0; i < children.length; i++) {
			addChild(new PhaseNode(this, children[i]));
		}
	}

	public String toString() {
		return getPhase().getDisplayName();
	}

	public Phase getPhase() {
		return (Phase) super.data;
	}
	
	public TreeNode getParent() {
		return parent;
	}

	public void setParent(NonSortingRootTreeNode parent) {
		this.parent = parent;
	}
}
