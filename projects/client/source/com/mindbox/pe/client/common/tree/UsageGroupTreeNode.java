package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.common.config.GuidelineTabConfig;


/**
 *
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public final class UsageGroupTreeNode extends AbstractDataTreeNode implements SelectableTreeNode {

	public UsageGroupTreeNode(TreeNode parent, UsageGroupTreeNode source) {
		this(parent, source.getGuidelineTabConfig());
		// copy children
		for (int i = 0; i < source.getChildCount(); i++) {
			UsageTypeTreeNode childUsageNode = new UsageTypeTreeNode(this, (UsageTypeTreeNode) source.getChildAt(i));
			addChild(childUsageNode, false);
		}
	}
	
	/**
	 *
	 */
	public UsageGroupTreeNode(TreeNode parent, GuidelineTabConfig tabConfig) {
		super(parent, tabConfig);
	}

	public final GuidelineTabConfig getGuidelineTabConfig() {
		return (GuidelineTabConfig) super.data;
	}

	public String toString() {
		return getGuidelineTabConfig().getTitle();
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean flag) {
		this.selected = flag;
	}

	private boolean selected;
}
