package com.mindbox.pe.client.applet.template.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.client.common.tree.NonSortingRootTreeNode;
import com.mindbox.pe.model.rule.RuleElement;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public abstract class AbstractRuleTreeNode extends NonSortingRootTreeNode {

	private TreeNode parent = null;

	public AbstractRuleTreeNode(TreeNode parent, RuleElement data) {
		super(data);
		this.parent = parent;
	}

	public String dispString(boolean selected) {
		return (data == null ? "" : (selected ? data.toString() : data.toString()));
	}

	public Object getData() {
		return super.data;
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	public RuleElement getRuleElement() {
		return (RuleElement) super.data;
	}

	public void setParent(AbstractRuleTreeNode parent) {
		this.parent = parent;
	}
}
