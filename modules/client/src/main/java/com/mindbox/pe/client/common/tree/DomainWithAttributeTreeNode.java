/*
 * Created on 2003. 12. 17.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.common.tree;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.common.validate.DataTypeCompatibilityValidator;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.DomainView;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class DomainWithAttributeTreeNode extends RootTreeNode {

	private TreeNode parent = null;

	public DomainWithAttributeTreeNode(DomainClass dc, TreeNode parent) {
		super(dc);
		this.parent = parent;
		addChildren();
	}

	public DomainWithAttributeTreeNode(DomainClass dc, TreeNode parent, int[] genericDataTypes) {
		super(dc);
		this.parent = parent;
		addChildren(genericDataTypes);
	}

	protected void addChildren() {
		for (Iterator<DomainAttribute> iter = ((DomainClass) data).getDomainAttributes().iterator(); iter.hasNext();) {
			DomainAttribute childClass = (DomainAttribute) iter.next();
			if (childClass.hasDomainView(DomainView.POLICY_EDITOR) || childClass.hasDomainView(DomainView.TEMPLATE_EDITOR)) {
				addChild(new AttributeTreeNode(childClass, this), true);
			}
		}
	}

	protected void addChildren(int[] genericDataTypes) {
		for (Iterator<DomainAttribute> iter = ((DomainClass) data).getDomainAttributes().iterator(); iter.hasNext();) {
			DomainAttribute childClass = (DomainAttribute) iter.next();
			boolean viewable = false;
			if (genericDataTypes == null)
				viewable = true;
			else {
				DeployType dt = childClass.getDeployType();
				int genericDataType = DataTypeCompatibilityValidator.getGenericDataType(dt);
				for (int i = 0; i < genericDataTypes.length; i++)
					if (genericDataTypes[i] == genericDataType) {
						viewable = true;
						break;
					}
			}
			if (viewable && (childClass.hasDomainView(DomainView.POLICY_EDITOR) || childClass.hasDomainView(DomainView.TEMPLATE_EDITOR))) {
				addChild(new AttributeTreeNode(childClass, this), true);
			}
		}
	}

	public final DomainClass getDomainClass() {
		return (DomainClass) super.data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getParent()
	 */
	@Override
	public final TreeNode getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return ((DomainClass) data).getDisplayLabel();
	}
}

