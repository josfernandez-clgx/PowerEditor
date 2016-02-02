package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.model.GridTemplate;

/**
 * Template tree node.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class TemplateTreeNode extends AbstractDataTreeNode implements SelectableTreeNode {

	public TemplateTreeNode(TreeNode parent, TemplateTreeNode source) {
		this(parent, source.getTemplate());
	}
	
	public TemplateTreeNode(TreeNode parent, GridTemplate td) {
		super(parent, td);
	}

	public final GridTemplate getTemplate() {
		return (GridTemplate) super.data;
	}

	public final void setTemplate(GridTemplate template) {
		super.data = template;
	}

	public String toString() {
		if (super.data == null) return "TemplateTreeNode(null)";
		return ((GridTemplate) super.data).getName() + " v." + ((GridTemplate) super.data).getVersion();
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
