package com.mindbox.pe.client.common.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Abstract mutable tree node that maintains selection status.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.5.0
 */
public abstract class AbstractSelectableMutableTreeNode extends DefaultMutableTreeNode implements SelectableTreeNode {

	protected AbstractSelectableMutableTreeNode() {
	}


}
