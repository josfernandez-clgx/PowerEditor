package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.xsd.config.GuidelineTab;


/**
 *
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public final class UsageGroupTreeNode extends AbstractDataTreeNode implements SelectableTreeNode {

	private boolean selected;

	public UsageGroupTreeNode(TreeNode parent, GuidelineTab tabConfig) {
		super(parent, tabConfig);
	}

	public UsageGroupTreeNode(TreeNode parent, UsageGroupTreeNode source) {
		this(parent, source.getGuidelineTab());
		// copy children
		for (int i = 0; i < source.getChildCount(); i++) {
			UsageTypeTreeNode childUsageNode = new UsageTypeTreeNode(this, (UsageTypeTreeNode) source.getChildAt(i));
			addChild(childUsageNode, false);
		}
	}

	public final GuidelineTab getGuidelineTab() {
		return GuidelineTab.class.cast(super.data);
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean flag) {
		this.selected = flag;
	}

	@Override
	public String toString() {
		return getGuidelineTab().getDisplayName();
	}
}
