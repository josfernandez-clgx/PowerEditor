package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.model.TemplateUsageType;


/**
 *
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public final class UsageTypeTreeNode extends AbstractDataTreeNode implements SelectableTreeNode {

	private boolean selected;

	public UsageTypeTreeNode(TreeNode parent, TemplateUsageType usageType) {
		super(parent, usageType);
	}

	public UsageTypeTreeNode(TreeNode parent, UsageTypeTreeNode source) {
		this(parent, source.getUsageType());
		// copy children
		for (int i = 0; i < source.getChildCount(); i++) {
			TemplateTreeNode childNode = new TemplateTreeNode(this, (TemplateTreeNode) source.getChildAt(i));
			addChild(childNode, true);
		}
	}

	private TemplateTreeNode getChildFor(int templateID) {
		for (int i = 0; i < getChildCount(); i++) {
			TemplateTreeNode childNode = (TemplateTreeNode) getChildAt(i);
			if (childNode.getTemplate() != null && childNode.getTemplate().getID() == templateID) {
				return childNode;
			}
		}
		return null;
	}

	public final TemplateUsageType getUsageType() {
		return (TemplateUsageType) super.data;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	public TemplateTreeNode removeChild(int templateID) {
		TemplateTreeNode childNode = getChildFor(templateID);
		if (childNode != null) {
			super.removeChild(childNode, true);
		}
		return childNode;
	}

	@Override
	public void setSelected(boolean flag) {
		this.selected = flag;
	}

	@Override
	public String toString() {
		return ((TemplateUsageType) super.data).getDisplayName();
	}
}
