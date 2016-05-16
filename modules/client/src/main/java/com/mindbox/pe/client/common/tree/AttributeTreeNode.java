/*
 * Created on 2003. 12. 17.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.model.domain.DomainAttribute;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class AttributeTreeNode extends RootTreeNode {

	private TreeNode parent = null;

	public AttributeTreeNode(DomainAttribute dc, TreeNode parent) {
		super(dc);
		this.parent = parent;
	}

	public final DomainAttribute getDomainAttribute() {
		return (DomainAttribute) super.data;
	}

	@Override
	public final TreeNode getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return ((DomainAttribute) data).getDisplayLabel();
	}
}
