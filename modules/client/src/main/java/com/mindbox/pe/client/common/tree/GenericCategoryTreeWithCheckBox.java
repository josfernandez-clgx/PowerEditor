package com.mindbox.pe.client.common.tree;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * Generic category tree with check box.
 * This allows multiple selection of generic categories.
 * @author Geneho
 * @since PowerEditor 3.1.0
 */
public class GenericCategoryTreeWithCheckBox extends AbstractGenericCategorySelectionTree {

	public class CategoryOrEntityNodeEditor extends AbstractCellEditor implements TreeCellEditor {
		private static final long serialVersionUID = -3951228734910107454L;

		CategoryOrEntityNodeRenderer renderer;

		AbstractSelectableMutableTreeNode lastEditedNode;

		JCheckBox checkBox;

		public CategoryOrEntityNodeEditor() {
			renderer = new CategoryOrEntityNodeRenderer();
			checkBox = renderer.checkBox;
			checkBox.addActionListener(new java.awt.event.ActionListener() {

				@Override
				public void actionPerformed(java.awt.event.ActionEvent actionevent) {

					if (!checkBox.isSelected() && lastEditedNode instanceof GenericEntityNode) {
						selectEntityNodes((GenericEntityNode) lastEditedNode, false);
					}
					lastEditedNode.setSelected(checkBox.isSelected());
					isDirty = true;
					repaintAll();
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return lastEditedNode.getUserObject();
		}

		@Override
		public Component getTreeCellEditorComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i) {
			lastEditedNode = (AbstractSelectableMutableTreeNode) obj;
			return renderer.getTreeCellRendererComponent(jtree, obj, flag, flag1, flag2, i, true);
		}
	}

	public class CategoryOrEntityNodeRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = -3951228734910107454L;

		JCheckBox checkBox;
		private JPanel panel;

		public CategoryOrEntityNodeRenderer() {
			checkBox = UIFactory.createCheckBox("");
			setOpaque(true);
			checkBox.setOpaque(false);
			panel = UIFactory.createJPanel();
			panel.setOpaque(true);
			panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			panel.add(this);
			panel.add(checkBox);
			setRendererIcon(this, categoryIcon);
		}

		@Override
		public java.awt.Component getTreeCellRendererComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i, boolean flag3) {
			AbstractSelectableMutableTreeNode node = (AbstractSelectableMutableTreeNode) obj;
			if (obj instanceof GenericCategoryNode) {
				setRendererIcon(this, categoryIcon);
			}
			else if (obj instanceof GenericEntityNode) {
				setRendererIcon(this, entityIcon);
			}
			if (tree.isEditable() && hasParentSelected(node)) {
				node.setSelected(false);
				checkBox.setSelected(false);
				checkBox.setEnabled(false);
			}
			else {
				checkBox.setEnabled(tree.isEditable());
				checkBox.setSelected(node.isSelected());
			}

			super.getTreeCellRendererComponent(jtree, obj, flag, flag1, flag2, i, flag3);
			panel.invalidate();
			panel.repaint();
			return panel;
		}
	}

	/**
	 * If a category is expanded and the model allows entities and the entities
	 * are not yet loaded, load them.
	 */
	private class TreeWillExpandOrCollapseListener implements TreeWillExpandListener {

		@Override
		public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
			if (allowEntity) {
				try {
					ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());
					TreePath path = event.getPath();
					if (path != null) {
						if (path.getLastPathComponent() instanceof GenericCategoryNode && ((GenericCategoryNode) path.getLastPathComponent()).entitiesNeedLoading()) {
							GenericCategoryNode node = (GenericCategoryNode) path.getLastPathComponent();
							getDatedTreeModel().addGenericEntityNodesForNodeOnly(node, entityType);
							node.setEntitiesNeedLoading(false);
							getDatedTreeModel().nodeStructureChanged(node);
						}
					}
				}
				catch (Exception e) {
					ClientUtil.handleRuntimeException(e);
				}
				finally {
					ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
				}
			}
		}
	}

	private static final ImageIcon categoryIcon = ClientUtil.getInstance().makeImageIcon("image.node.category");

	private static final ImageIcon entityIcon = ClientUtil.getInstance().makeImageIcon("image.node.entity");

	private static void setRendererIcon(DefaultTreeCellRenderer renderer, ImageIcon icon) {
		renderer.setOpenIcon(icon);
		renderer.setClosedIcon(icon);
		renderer.setDisabledIcon(icon);
		renderer.setLeafIcon(icon);
	}

	boolean isDirty;
	private TreeWillExpandOrCollapseListener expansionListener = null;

	/**
	 * Equivalent to <code>new GenericCategoryTreeWithCheckBox(entityType, allowEntity, true, sort)</code>.
	 * @param entityType entityType
	 * @param allowEntity allowEntity
	 * @param sort sort
	 */
	public GenericCategoryTreeWithCheckBox(GenericEntityType entityType, boolean allowEntity, boolean sort) {
		this(entityType, allowEntity, true, sort);
	}

	public GenericCategoryTreeWithCheckBox(GenericEntityType entityType, boolean allowEntity, boolean showCollapseExpandButtons, boolean sort) {
		super(entityType, allowEntity, TreeSelectionModel.SINGLE_TREE_SELECTION, true, true, showCollapseExpandButtons, sort);
		CategoryOrEntityNodeRenderer categorynoderenderer = new CategoryOrEntityNodeRenderer();
		CategoryOrEntityNodeEditor categorynodeeditor = new CategoryOrEntityNodeEditor();
		tree.setCellRenderer(categorynoderenderer);
		tree.setCellEditor(categorynodeeditor);
		tree.setEditable(true);
		if (allowEntity) {
			expansionListener = new TreeWillExpandOrCollapseListener();
			tree.addTreeWillExpandListener(expansionListener);
		}
	}

	public final void addTreeModelListener(TreeModelListener l) {
		tree.getModel().addTreeModelListener(l);
	}

	public void clearAll() {
		// begin bug fix - bizarre rendering problem
		CategoryOrEntityNodeEditor editor = (CategoryOrEntityNodeEditor) tree.getCellEditor();
		if (editor.lastEditedNode != null) {
			editor.lastEditedNode.setSelected(false);
			editor.checkBox.setSelected(false);
			editor.checkBox.repaint();
		}
		// end bug fix        
		selectAll((TreeNode) tree.getModel().getRoot(), false, false);
		repaintAll();
		isDirty = true;
	}

	/**
	 * This method is overrides getSelectedGenericCategories() in 
	 * AbstractGenericCategorySelectionTree.
	 * @return array of Generic categories.
	 */
	@Override
	public GenericCategory[] getSelectedCategories() {
		List<GenericCategory> list = new java.util.ArrayList<GenericCategory>();
		GenericCategoryNode rootNode = (GenericCategoryNode) tree.getModel().getRoot();
		getSelectedCategories(list, rootNode);
		return list.toArray(new GenericCategory[0]);
	}

	private void getSelectedCategories(List<GenericCategory> list, GenericCategoryNode node) {
		if (node.isSelected()) {
			list.add(node.getGenericCategory());
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof GenericCategoryNode) {
				getSelectedCategories(list, (GenericCategoryNode) child);
			}
		}
	}

	public List<Integer> getSelectedGenericCategoryIDs() {
		List<Integer> list = new java.util.ArrayList<Integer>();
		GenericCategoryNode rootNode = (GenericCategoryNode) tree.getModel().getRoot();
		getSelectedGenericCategoryIDs(list, rootNode);
		return list;
	}

	private void getSelectedGenericCategoryIDs(List<Integer> list, GenericCategoryNode node) {
		if (node.isSelected()) {
			list.add(new Integer(node.getGenericCategoryID()));
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof GenericCategoryNode) {
				getSelectedGenericCategoryIDs(list, (GenericCategoryNode) child);
			}
		}
	}

	public List<Integer> getSelectedGenericEntityIDs() {
		List<Integer> list = new java.util.ArrayList<Integer>();
		GenericCategoryNode rootNode = (GenericCategoryNode) tree.getModel().getRoot();
		getSelectedGenericEntityIDs(list, rootNode);
		return list;
	}

	private void getSelectedGenericEntityIDs(List<Integer> list, GenericCategoryNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof GenericCategoryNode) {
				getSelectedGenericEntityIDs(list, (GenericCategoryNode) child);
			}
			else if (child instanceof GenericEntityNode) {
				getSelectedGenericEntityIDs(list, (GenericEntityNode) child);
			}
		}
	}

	private void getSelectedGenericEntityIDs(List<Integer> list, GenericEntityNode node) {
		if (node.isSelected()) {
			Integer id = new Integer(node.getGenericEntityID());
			if (!list.contains(id)) {
				list.add(id);
			}
		}
	}

	private boolean hasParentSelected(AbstractSelectableMutableTreeNode node) {
		if (node.getParent() != null) {
			AbstractSelectableMutableTreeNode parent = (AbstractSelectableMutableTreeNode) node.getParent();
			if (parent.isSelected()) {
				return true;
			}
			else {
				return hasParentSelected(parent);
			}
		}
		else {
			return false;
		}
	}

	public boolean isDirty() {
		return isDirty;
	}

	private void loadCategoryEntitieForSelectedEntities(GenericCategoryNode node, Set<Integer> categoryIDset) {
		GenericCategory category = node.getGenericCategory();
		if (node.entitiesNeedLoading() && category != null && categoryIDset != null && categoryIDset.contains(new Integer(category.getID()))) {
			getDatedTreeModel().addGenericEntityNodesForNodeOnly(node, entityType);
			node.setEntitiesNeedLoading(false);
			getDatedTreeModel().nodeStructureChanged(node);
		}

		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof GenericCategoryNode) {
				loadCategoryEntitieForSelectedEntities((GenericCategoryNode) child, categoryIDset);
			}
		}
	}

	private void loadCategoryEntitieForSelectedEntities(Set<Integer> categoryIDset) {
		GenericCategoryNode rootNode = (GenericCategoryNode) tree.getModel().getRoot();
		loadCategoryEntitieForSelectedEntities(rootNode, categoryIDset);
	}

	/**
	 * Gets a list of the parent categories of the saved entities  
	 * and loads all the entities assoicated with those categories.
	 * @param entities Saved entities to select
	 */
	private void loadSavedEntities(List<Integer> entities) {
		if (entities != null && entities.size() > 0) {
			Set<Integer> categoryIDs = new HashSet<Integer>();
			for (Iterator<Integer> i = entities.iterator(); i.hasNext();) {
				Integer entityID = i.next();
				GenericEntity entity = EntityModelCacheFactory.getInstance().getGenericEntity(entityType, entityID.intValue());
				List<Integer> parentCatIDs = entity.getCategoryIDList(getDatedTreeModel().getDate());
				if (parentCatIDs != null && parentCatIDs.size() > 0) {
					categoryIDs.addAll(parentCatIDs);
				}
			}
			loadCategoryEntitieForSelectedEntities(categoryIDs);
		}
	}

	private void repaintAll() {
		tree.invalidate();
		tree.repaint();
	}

	private void selectAll(TreeNode parent, boolean select, boolean entitiesOnly) {
		AbstractSelectableMutableTreeNode anode = (AbstractSelectableMutableTreeNode) parent;
		if (!select) {
			anode.setSelected(false);
		}
		else if (!entitiesOnly || anode instanceof GenericEntityNode) {
			anode.setSelected(true);
		}
		if (parent.getChildCount() >= 0) {
			for (Enumeration<?> e = parent.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				selectAll(n, select, entitiesOnly);
			}
		}
	}

	public void selectAllEntities() {
		// begin bug fix - bizarre rendering problem
		tree.removeTreeWillExpandListener(expansionListener);
		CategoryOrEntityNodeEditor editor = (CategoryOrEntityNodeEditor) tree.getCellEditor();
		if (editor.lastEditedNode != null && editor.lastEditedNode instanceof GenericEntityNode) {
			editor.lastEditedNode.setSelected(true);
			editor.checkBox.setSelected(true);
			editor.checkBox.repaint();
		}
		// end bug fix
		selectAll((TreeNode) tree.getModel().getRoot(), true, true);
		tree.addTreeWillExpandListener(expansionListener);
		repaintAll();
		isDirty = true;
	}

	private void selectEntityNode(TreeNode parent, GenericEntityNode nodeToSelect, boolean select) {
		AbstractSelectableMutableTreeNode anode = (AbstractSelectableMutableTreeNode) parent;
		if (anode instanceof GenericEntityNode && ((GenericEntityNode) anode).getGenericEntityID() == nodeToSelect.getGenericEntityID()) {
			anode.setSelected(select);
		}
		if (parent.getChildCount() >= 0) {
			for (Enumeration<?> e = parent.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				selectEntityNode(n, nodeToSelect, select);
			}
		}
	}

	protected void selectEntityNodes(GenericEntityNode node, boolean select) {
		selectEntityNode((TreeNode) tree.getModel().getRoot(), node, select);
	}

	public void setDirty(boolean flag) {
		isDirty = flag;
	}

	private void setSelectedCategories(GenericCategoryNode node, List<Integer> list) {
		GenericCategory category = node.getGenericCategory();
		if (category != null && list != null && list.contains(new Integer(category.getID()))) {
			node.setSelected(true);
			if (node.getParent() != null) {
				tree.expandPath(new TreePath(((GenericCategoryNode) node.getParent()).getPath()));
			}
		}
		else {
			node.setSelected(false);
		}

		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof GenericCategoryNode) {
				setSelectedCategories((GenericCategoryNode) child, list);
			}
			else if (child instanceof GenericEntityNode) {
				((GenericEntityNode) child).setSelected(false);
			}
		}
	}

	private void setSelectedCategories(List<Integer> list) {
		GenericCategoryNode rootNode = (GenericCategoryNode) tree.getModel().getRoot();
		setSelectedCategories(rootNode, list);
	}

	public void setSelectedCategoriesAndEntities(List<Integer> categories, List<Integer> entities) {
		if (allowEntity) {
			tree.removeTreeWillExpandListener(expansionListener);
			loadSavedEntities(entities);
		}
		TreeUtil.expandAll(tree, false);
		setSelectedCategories(categories);
		setSelectedEntities(entities);
		isDirty = false;
		repaintAll();
		if (allowEntity) {
			tree.addTreeWillExpandListener(expansionListener);
		}
	}

	private void setSelectedEntities(GenericCategoryNode node, List<Integer> list) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child instanceof GenericCategoryNode) {
				setSelectedEntities((GenericCategoryNode) child, list);
			}
			else if (child instanceof GenericEntityNode) {
				setSelectedEntities((GenericEntityNode) child, list);
			}
		}
	}

	private void setSelectedEntities(GenericEntityNode node, List<Integer> list) {
		GenericEntity entity = node.getGenericEntity();
		if (entity != null && list != null && list.contains(new Integer(entity.getID()))) {
			node.setSelected(true);
			if (node.getParent() != null) {
				tree.expandPath(new TreePath(((GenericCategoryNode) node.getParent()).getPath()));
			}
		}
		else {
			node.setSelected(false);
		}
	}

	private void setSelectedEntities(List<Integer> list) {
		GenericCategoryNode rootNode = (GenericCategoryNode) tree.getModel().getRoot();
		setSelectedEntities(rootNode, list);
	}

}
