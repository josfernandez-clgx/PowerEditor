/*
 * Created on 2003. 12. 17.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.model.domain.DomainClass;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class DomainTreeFlatNode extends DomainTreeNode {

	public DomainTreeFlatNode(DomainClass dc, TreeNode parent) {
		super(dc, parent);
	}

	@Override
	protected void addChildren() {
		// noop
	}
}

