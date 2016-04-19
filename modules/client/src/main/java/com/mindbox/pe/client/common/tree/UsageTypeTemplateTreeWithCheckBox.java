package com.mindbox.pe.client.common.tree;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.xsd.config.GuidelineTab;

/**
 * Generic category tree with check box.
 * This allows multiple selection of generic categories.
 * @author Geneho
 * @since PowerEditor 3.1.0
 */
public class UsageTypeTemplateTreeWithCheckBox extends AbstractSelectionTree {

	private class UsageTemplateNodeRenderer extends JCheckBox implements TreeCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private final Icon templateCheckedIcon, templateUncheckedIcon;
		private final Icon openCheckedIcon, openUncheckedIcon, closedCheckedIcon, closedUnCheckedIcon;

		public UsageTemplateNodeRenderer() {
			templateCheckedIcon = ClientUtil.getInstance().makeImageIcon("image.node.template.checked");
			templateUncheckedIcon = ClientUtil.getInstance().makeImageIcon("image.node.template.unchecked");
			openCheckedIcon = ClientUtil.getInstance().makeImageIcon("image.node.folder.open.checked");
			openUncheckedIcon = ClientUtil.getInstance().makeImageIcon("image.node.folder.open.unchecked");
			closedCheckedIcon = ClientUtil.getInstance().makeImageIcon("image.node.folder.closed.checked");
			closedUnCheckedIcon = ClientUtil.getInstance().makeImageIcon("image.node.folder.closed.unchecked");
			setMargin(new Insets(0, 0, 0, 0));
			setOpaque(false);
		}

		public Component getTreeCellRendererComponent(JTree jtree, Object obj, boolean selected, boolean expanded, boolean leaf, int i, boolean flag3) {
			if (obj instanceof SelectableTreeNode) {
				SelectableTreeNode node = (SelectableTreeNode) obj;
				boolean forTemplate = obj instanceof TemplateTreeNode;
				if (forTemplate) {
					if (((TemplateTreeNode) obj).getTemplate() == null) {
						setText("Error: Template not found; please refresh");
					}
					else {
						setText(((TemplateTreeNode) obj).getTemplate().getName() + " (" + ((TemplateTreeNode) obj).getTemplate().getVersion() + ")");
					}
				}
				else {
					setText((obj == null ? "" : obj.toString()));
				}
				boolean isParentSelected = isAncestorSelected(node);
				if (tree.isEditable() && isParentSelected) {
					node.setSelected(false);
					setSelected(false);
					setEnabled(false);
				}
				else {
					setEnabled(tree.isEditable());
					setSelected(node.isSelected());
				}
				if (forTemplate) {
					setIcon(templateUncheckedIcon);
					setSelectedIcon(templateCheckedIcon);
				}
				else if (expanded) {
					setIcon(openUncheckedIcon);
					setSelectedIcon(openCheckedIcon);
				}
				else {
					setIcon(closedUnCheckedIcon);
					setSelectedIcon(closedCheckedIcon);
				}
			}
			else {
				setText("ERROR: Unsupported Note Type");
			}
			return this;
		}
	}

	public class UsageTemplateNodeEditor extends AbstractCellEditor implements TreeCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		UsageTemplateNodeRenderer renderer;
		SelectableTreeNode lastEditedNode;
		JCheckBox checkBox;

		public UsageTemplateNodeEditor() {
			renderer = new UsageTemplateNodeRenderer();
			checkBox = renderer;
			checkBox.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent actionevent) {
					markNode(lastEditedNode, checkBox.isSelected());
					isDirty = true;
				}
			});
		}

		public Component getTreeCellEditorComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i) {
			lastEditedNode = (SelectableTreeNode) obj;
			return renderer.getTreeCellRendererComponent(jtree, obj, flag, flag1, flag2, i, true);
		}

		public Object getCellEditorValue() {
			return ((AbstractDataTreeNode) lastEditedNode).getData();
		}

	}

	private boolean isDirty;
	private JPanel panel = null;

	///////////////////////// Constructors and Instance Methods ///////////////

	/**
	 * Equivalent to <code>new GenericCategoryTreeWithCheckBox(entityType, allowEntity, true, sort)</code>.
	 */
	public UsageTypeTemplateTreeWithCheckBox(boolean sort) {
		this(true, sort);
	}

	public UsageTypeTemplateTreeWithCheckBox(boolean showCollapseExpandButtons, boolean sort) {
		super(TreeSelectionModel.SINGLE_TREE_SELECTION, false, true, showCollapseExpandButtons, sort);
		// set tree model
		tree.setModel(EntityModelCacheFactory.getInstance().createGuidelineTemplateTreeModel());

		UsageTemplateNodeRenderer categorynoderenderer = new UsageTemplateNodeRenderer();
		UsageTemplateNodeEditor categorynodeeditor = new UsageTemplateNodeEditor();
		tree.setCellRenderer(categorynoderenderer);
		tree.setCellEditor(categorynodeeditor);
		tree.setEditable(true);
	}

	private DefaultTreeModel getDefaultTreeModel() {
		return (DefaultTreeModel) tree.getModel();
	}

	public void setDirty(boolean flag) {
		isDirty = flag;
	}

	public boolean isDirty() {
		return isDirty;
	}

	private synchronized void markNode(SelectableTreeNode node, boolean selected) {
		if (selected) {
			if (node instanceof UsageGroupTreeNode || node instanceof UsageTypeTreeNode) {
				selectAll(node, false, true);
			}
		}
		node.setSelected(selected);
		getDefaultTreeModel().nodeStructureChanged(node);//nodeChanged(node);
	}

	private boolean isAncestorSelected(TreeNode node) {
		if (node.getParent() != null) {
			TreeNode parent = (TreeNode) node.getParent();
			if (parent instanceof SelectableTreeNode && ((SelectableTreeNode) parent).isSelected()) {
				return true;
			}
			else {
				return isAncestorSelected(parent);
			}
		}
		else {
			return false;
		}
	}

	public synchronized void setSelected(List<String> usageGroupNames, List<TemplateUsageType> usageTypes, List<Integer> templateIDs) {
		TreeUtil.expandAll(tree, false);
		setSelectedTemplates(templateIDs);
		setSelectedUsageTypes(usageTypes);
		setSelectedUsageGroups(usageGroupNames);
		isDirty = false;
	}

	private void setSelectedTemplates(List<Integer> list) {
		RootTreeNode rootNode = (RootTreeNode) tree.getModel().getRoot();
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			TreeNode child = rootNode.getChildAt(i);
			if (child instanceof UsageGroupTreeNode) {
				for (int j = 0; j < child.getChildCount(); j++) {
					UsageTypeTreeNode usageTypeNode = (UsageTypeTreeNode) child.getChildAt(j);
					setSelectedTemplates(usageTypeNode, list);
				}
			}
		}
	}

	private void setSelectedTemplates(UsageTypeTreeNode node, List<Integer> list) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TemplateTreeNode templateTreeNode = (TemplateTreeNode) node.getChildAt(i);
			if (templateTreeNode != null && list != null && list.contains(templateTreeNode.getTemplate().getID())) {
				markNode(templateTreeNode, true);
			}
			else {
				markNode(templateTreeNode, false);
			}
		}
	}

	private void setSelectedUsageTypes(List<TemplateUsageType> list) {
		RootTreeNode rootNode = (RootTreeNode) tree.getModel().getRoot();
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			TreeNode child = rootNode.getChildAt(i);
			if (child instanceof UsageGroupTreeNode) {
				setSelectedUsageTypes((UsageGroupTreeNode) child, list);
			}
		}
	}

	private void setSelectedUsageTypes(UsageGroupTreeNode node, List<TemplateUsageType> list) {
		for (int i = 0; i < node.getChildCount(); i++) {
			UsageTypeTreeNode usageTypeNode = (UsageTypeTreeNode) node.getChildAt(i);
			if (usageTypeNode != null && list != null && list.contains(usageTypeNode.getUsageType())) {
				markNode(usageTypeNode, true);
			}
			else {
				markNode(usageTypeNode, false);
			}
		}
	}

	private void setSelectedUsageGroups(List<String> list) {
		RootTreeNode rootNode = (RootTreeNode) tree.getModel().getRoot();
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			TreeNode child = rootNode.getChildAt(i);
			if (child instanceof UsageGroupTreeNode) {
				setSelectedUsageGroups((UsageGroupTreeNode) child, list);
			}
		}
	}

	private void setSelectedUsageGroups(UsageGroupTreeNode node, List<String> list) {
		if (node != null && list != null && list.contains(node.getGuidelineTab().getDisplayName())) {
			markNode(node, true);
		}
		else {
			markNode(node, false);
		}
	}

	public synchronized void selectAllTemplates() {
		clearSelection_internal();
		TreeNode rootNode = (TreeNode) tree.getModel().getRoot();
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			TreeNode child = rootNode.getChildAt(i);
			if (child instanceof UsageGroupTreeNode) {
				markNode((UsageGroupTreeNode) child, false);
				for (int j = 0; j < child.getChildCount(); j++) {
					UsageTypeTreeNode usageTypeNode = (UsageTypeTreeNode) child.getChildAt(j);
					markNode(usageTypeNode, false);
					selectAll(usageTypeNode, true, true);
				}
			}
		}
		isDirty = true;
		expandAll(true);
		tree.invalidate();
		tree.repaint();
	}

	public synchronized void selectAllUsageTypes() {
		clearSelection_internal();
		TreeNode rootNode = (TreeNode) tree.getModel().getRoot();
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			TreeNode child = rootNode.getChildAt(i);
			if (child instanceof UsageGroupTreeNode) {
				markNode((UsageGroupTreeNode) child, false);
				for (int j = 0; j < child.getChildCount(); j++) {
					UsageTypeTreeNode usageTypeNode = (UsageTypeTreeNode) child.getChildAt(j);
					markNode(usageTypeNode, true);
					scrollPathToVisible(getTreePath(usageTypeNode));
				}

			}
		}
		isDirty = true;
		tree.invalidate();
		tree.repaint();
	}

	@Override
	public synchronized void clearSelection() {
		clearSelection_internal();
	}

	private void clearSelection_internal() {
		tree.cancelEditing();
		selectAll((TreeNode) tree.getModel().getRoot(), false);
		isDirty = true;
	}

	private void selectAll(TreeNode parent, boolean select, boolean childrenOnly) {
		if (parent instanceof SelectableTreeNode) {
			SelectableTreeNode anode = (SelectableTreeNode) parent;
			if (!childrenOnly) {
				markNode(anode, select);
			}
		}
		if (parent.getChildCount() >= 0) {
			for (Enumeration<?> e = parent.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				selectAll(n, select);
			}
		}
	}

	private void selectAll(TreeNode treeNode, boolean select) {
		if (treeNode instanceof SelectableTreeNode) {
			SelectableTreeNode anode = (SelectableTreeNode) treeNode;
			markNode(anode, select);
		}
		if (treeNode.getChildCount() >= 0) {
			for (Enumeration<?> e = treeNode.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				selectAll(n, select);
			}
		}
	}

	/**
	 * This method is overrides getSelectedGenericCategories() in 
	 * AbstractGenericCategorySelectionTree.
	 * @return array of Generic categories.
	 */
	public synchronized List<GuidelineTab> getSelectedUsageTypeGroups() {
		List<GuidelineTab> list = new ArrayList<GuidelineTab>();
		RootTreeNode rootNode = (RootTreeNode) tree.getModel().getRoot();
		getSelectedUsageTypeGroups(list, rootNode);
		return Collections.unmodifiableList(list);
	}

	private void getSelectedUsageTypeGroups(List<GuidelineTab> list, RootTreeNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof UsageGroupTreeNode && ((UsageGroupTreeNode) child).isSelected()) {
				list.add(((UsageGroupTreeNode) child).getGuidelineTab());
			}
		}
	}

	/**
	 * This method is overrides getSelectedGenericCategories() in 
	 * AbstractGenericCategorySelectionTree.
	 * @return array of Generic categories.
	 */
	public synchronized List<TemplateUsageType> getSelectedUsageTypes() {
		List<TemplateUsageType> list = new java.util.ArrayList<TemplateUsageType>();
		RootTreeNode rootNode = (RootTreeNode) tree.getModel().getRoot();
		getSelectedUsageTypes(list, rootNode);
		return Collections.unmodifiableList(list);
	}

	private void getSelectedUsageTypes(List<TemplateUsageType> list, RootTreeNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof UsageGroupTreeNode && !((UsageGroupTreeNode) child).isSelected()) {
				getSelectedUsageTypes(list, (UsageGroupTreeNode) child);
			}
		}
	}

	private void getSelectedUsageTypes(List<TemplateUsageType> list, UsageGroupTreeNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof UsageTypeTreeNode && ((UsageTypeTreeNode) child).isSelected()) {
				list.add(((UsageTypeTreeNode) child).getUsageType());
			}
		}
	}

	public List<Integer> getSelectedTemplateIDs() {
		List<GridTemplate> list = getSelectedTemplates();
		List<Integer> idList = new ArrayList<Integer>();
		for (GridTemplate template : list) {
			idList.add(template.getID());
		}
		return idList;
	}

	/**
	 * This method is overrides getSelectedGenericCategories() in 
	 * AbstractGenericCategorySelectionTree.
	 * @return array of Generic categories.
	 */
	public synchronized List<GridTemplate> getSelectedTemplates() {
		List<GridTemplate> list = new java.util.ArrayList<GridTemplate>();
		RootTreeNode rootNode = (RootTreeNode) tree.getModel().getRoot();
		getSelectedTemplates(list, rootNode);
		return Collections.unmodifiableList(list);
	}

	private void getSelectedTemplates(List<GridTemplate> list, RootTreeNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof UsageGroupTreeNode && !((UsageGroupTreeNode) child).isSelected()) {
				getSelectedTemplates(list, (UsageGroupTreeNode) child);
			}
		}
	}

	private void getSelectedTemplates(List<GridTemplate> list, UsageGroupTreeNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof UsageTypeTreeNode && !((UsageTypeTreeNode) child).isSelected()) {
				getSelectedTemplates(list, (UsageTypeTreeNode) child);
			}
		}
	}

	private void getSelectedTemplates(List<GridTemplate> list, UsageTypeTreeNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof TemplateTreeNode && ((TemplateTreeNode) child).isSelected()) {
				list.add(((TemplateTreeNode) child).getTemplate());
			}
		}
	}

	@Override
	public synchronized JPanel getJComponent() {
		if (panel == null) {
			GridBagLayout bag = new GridBagLayout();
			panel = UIFactory.createJPanel(bag);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(3, 3, 3, 3);
			c.gridwidth = 1;
			c.gridheight = 1;
			c.weightx = 0.0;
			c.weighty = 0.0;

			JButton button = UIFactory.createJButton("button.select.all", null, new AbstractThreadedActionAdapter() {
				@Override
				public void performAction(ActionEvent event) throws Exception {
					selectAllTemplates();
				}
			}, null);
			button.setIcon(ClientUtil.getInstance().makeImageIcon("image.node.template"));
			button.setHorizontalTextPosition(SwingConstants.LEADING);
			UIFactory.addComponent(panel, bag, c, button);
			button = UIFactory.createJButton("button.select.all", null, new AbstractThreadedActionAdapter() {
				@Override
				public void performAction(ActionEvent event) throws Exception {
					selectAllUsageTypes();
				}
			}, null);
			button.setIcon(ClientUtil.getInstance().makeImageIcon("image.node.folder.closed"));
			button.setHorizontalTextPosition(SwingConstants.LEADING);
			UIFactory.addComponent(panel, bag, c, button);
			button = UIFactory.createJButton("button.clear.selection", null, new AbstractThreadedActionAdapter() {
				@Override
				public void performAction(ActionEvent event) throws Exception {
					clearSelection();
				}
			}, null);
			UIFactory.addComponent(panel, bag, c, button);
			c.weightx = 1.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			UIFactory.addComponent(panel, bag, c, Box.createHorizontalGlue());

			if (expandAllButton != null || collapseAllButton != null) {
				JPanel bp = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
				if (expandAllButton != null) {
					bp.add(expandAllButton);
				}
				if (collapseAllButton != null) {
					bp.add(collapseAllButton);
				}
				c.weightx = 1.0;
				c.gridwidth = GridBagConstraints.REMAINDER;
				UIFactory.addComponent(panel, bag, c, bp);
			}

			c.weighty = 1.0;
			c.gridheight = GridBagConstraints.REMAINDER;
			UIFactory.addComponent(panel, bag, c, new JScrollPane(tree));

			panel.setBorder(BorderFactory.createLoweredBevelBorder());
		}
		return panel;
	}

}
