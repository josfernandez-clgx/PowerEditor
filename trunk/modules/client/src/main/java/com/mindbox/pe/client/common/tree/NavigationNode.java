/*
 * Created on 2004. 3. 3.
 *
 */
package com.mindbox.pe.client.common.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public abstract class NavigationNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	protected boolean explored;

	public NavigationNode() {
		explored = false;
	}

	public NavigationNode(Object obj) {
		super(obj);
		explored = false;
	}

	public boolean isExplored() {
		return explored;
	}

	public abstract void explore();

}
