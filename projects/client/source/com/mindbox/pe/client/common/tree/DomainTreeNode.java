/*
 * Created on 2003. 12. 17.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.common.tree;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.model.DomainClass;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class DomainTreeNode extends RootTreeNode {

	private TreeNode parent = null;

	/**
	 * 
	 */
	public DomainTreeNode(DomainClass dc, TreeNode parent) {
		super(dc);
		this.parent = parent;
		addChildren();
	}

	protected void addChildren() {
		for (Iterator<DomainClass> iter = DomainModel.getInstance().getChildClasses(((DomainClass) data).getName()).iterator();
			iter.hasNext();
			) {
			DomainClass childClass = (DomainClass) iter.next();
			addChild(new DomainTreeNode(childClass, this),true);
		}
	}

	public final DomainClass getDomainClass() {
		return (DomainClass) super.data;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getParent()
	 */
	public final TreeNode getParent() {
		return parent;
	}

	public String toString() {
		return ((DomainClass) data).getDisplayLabel();
	}
}
