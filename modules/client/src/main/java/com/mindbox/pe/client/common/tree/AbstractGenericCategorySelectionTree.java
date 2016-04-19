package com.mindbox.pe.client.common.tree;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import mseries.Calendar.MFieldListener;
import mseries.ui.MChangeEvent;
import mseries.ui.MChangeListener;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * Generic category selection tree.
 * This only allows a single selection.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 3.1.0
 */
public abstract class AbstractGenericCategorySelectionTree extends AbstractSelectionTree {

	/**
	 * Date widget for entering the "tree as of" date.
	 */
	protected final MDateDateField dateField;

	/**
	 * Whether or not this allows entities.
	 */
	protected final boolean allowEntity;

	protected final GenericEntityType entityType;

	private final JButton refreshButton;
	private JPanel panel = null;

	protected AbstractGenericCategorySelectionTree(int categoryType, boolean allowEntity, int selectionMode, boolean showRoot,
			boolean showRootHandles, boolean showCollapseExpandButtons, boolean sort) {
		this(
				GenericEntityType.forCategoryType(categoryType),
				allowEntity,
				selectionMode,
				showRoot,
				showRootHandles,
				showCollapseExpandButtons,
				sort);
	}

	/**
	 * 
	 * @param selectionMode tree selection mode
	 * @param showRoot show root flag
	 */
	protected AbstractGenericCategorySelectionTree(GenericEntityType entityType, boolean allowEntity, int selectionMode, boolean showRoot,
			boolean showRootHandles, boolean showCollapseExpandButtons, boolean sort) {
		super(selectionMode, showRoot, showRootHandles, showCollapseExpandButtons, sort);
		if (entityType == null) throw new NullPointerException("entityType cannot be null");
		tree.setCellRenderer(new NavigationTreeCellRenderer());
		this.allowEntity = allowEntity;
		this.entityType = entityType;
		dateField = new MDateDateField(true, false, true);
		dateField.setValue(new Date());
		refreshButton = UIFactory.createJButton("label.refresh", null, new AbstractThreadedActionAdapter() {
			public void performAction(ActionEvent event) throws Exception {
				if (dateField.getDate() != null) {
					refreshTree();
				}
			}
		}, null);
		refreshButton.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				refreshButton.setEnabled(dateField.getDate() != null);
			}

			public void focusLost(FocusEvent e) {
			}
		});
		dateField.addMChangeListener(new MChangeListener() {
			public void valueChanged(MChangeEvent arg0) {
				refreshButton.setEnabled(dateField.getDate() != null);
			}
		});
		dateField.addMFieldListener(new MFieldListener() {
			public void fieldEntered(FocusEvent arg0) {
				refreshButton.setEnabled(dateField.getDate() != null);
			}

			public void fieldExited(FocusEvent arg0) {
				refreshButton.setEnabled(dateField.getDate() != null);
			}
		});

		tree.setModel(EntityModelCacheFactory.getInstance().createGenericCategoryTreeModel(
				entityType.getCategoryType(),
				dateField.getDate(),
				allowEntity,
				sort));
	}

	/**
	 * Recalibrates the tree using the currently selected date, if date has changed.
	 *
	 */
	protected final void refreshTree() {
		GenericCategory selectedCategory = getSelectedGenericCategory();
		GenericEntity selectedEntity = getSelectedGenericEntity();
		Enumeration<?> e = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
		List<Integer> expandedCategories = new ArrayList<Integer>();
		while (e != null && e.hasMoreElements()) {
			TreePath path = (TreePath) e.nextElement();
			expandedCategories.add(new Integer(((GenericCategoryNode) path.getLastPathComponent()).getGenericCategoryID()));
		}
		tree.clearSelection(); // TT 2010 - NullPointerException when Refresh on the "New Parent Category Association" dialog
		getDatedTreeModel().recalibrate(getSelectedDate());
		expandCategories(new TreePath(tree.getModel().getRoot()), expandedCategories);
		if (selectedCategory != null) {
			selectGenericCategory(selectedCategory.getID());
		}
		if (selectedEntity != null) {
			selectGenericEntity(selectedEntity.getID());
		}
	}

	public final Date getSelectedDate() {
		return dateField.getDate();
	}

	public DatedCategoryTreeModel getDatedTreeModel() {
		return (DatedCategoryTreeModel) tree.getModel();
	}

	public final void updateTreeForEntities(boolean showEntities) {
		List<GenericCategoryNode> categories = new ArrayList<GenericCategoryNode>();
		Enumeration<?> e = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
		getDatedTreeModel().setCategoriesNeedLoading((GenericCategoryNode) tree.getModel().getRoot(), true);
		while (e != null && e.hasMoreElements()) {
			TreePath path = (TreePath) e.nextElement();
			if (path.getLastPathComponent() instanceof GenericCategoryNode) {
				GenericCategoryNode parent = (GenericCategoryNode) path.getLastPathComponent();
				categories.add(parent);
				parent.setEntitiesNeedLoading(false);
			}
		}

		getDatedTreeModel().resetShowEntities(showEntities, categories);
		tree.repaint();
	}

	public final JPanel getJComponent() {
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

			JLabel label = UIFactory.createFormLabel("label.date");
			label.setVerticalAlignment(SwingConstants.CENTER);
			UIFactory.addComponent(panel, bag, c, label);
			c.weightx = 0.1;
			UIFactory.addComponent(panel, bag, c, dateField.getJComponent());
			c.weightx = 0.0;
			UIFactory.addComponent(panel, bag, c, refreshButton);
			if (expandAllButton != null) UIFactory.addComponent(panel, bag, c, expandAllButton);
			if (collapseAllButton != null) UIFactory.addComponent(panel, bag, c, collapseAllButton);

			c.weightx = 1.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			UIFactory.addComponent(panel, bag, c, Box.createHorizontalGlue());

			c.weighty = 1.0;
			c.gridheight = GridBagConstraints.REMAINDER;
			UIFactory.addComponent(panel, bag, c, new JScrollPane(tree));

			panel.setBorder(BorderFactory.createLoweredBevelBorder());
		}
		return panel;
	}

	public GenericCategory[] getSelectedCategories() {
		TreePath[] categoryPaths = tree.getSelectionPaths();
		if (categoryPaths != null) {
			List<GenericCategory> catList = new ArrayList<GenericCategory>();
			for (int i = 0; i < categoryPaths.length; i++) {
				Object lastPathComponent = categoryPaths[i].getLastPathComponent();
				if (lastPathComponent instanceof GenericCategoryNode) {
					catList.add(((GenericCategoryNode) lastPathComponent).getGenericCategory());
				}
			}
			return catList.toArray(new GenericCategory[0]);
		}
		else {
			return null;
		}
	}

	public final GenericCategory getSelectedGenericCategory() {
		TreePath selectedPath = tree.getSelectionPath();
		if (selectedPath != null) {
			Object lastPathComponent = selectedPath.getLastPathComponent();
			if (lastPathComponent instanceof GenericCategoryNode) {
				return ((GenericCategoryNode) lastPathComponent).getGenericCategory();
			}
		}
		return null;
	}

	public final void selectGenericCategory(int catID) {
		if (catID < 0) {
			tree.clearSelection();
		}
		else {
			GenericCategoryNode node = findNode(catID);
			if (node != null) {
				TreePath path = getTreePath(node);
				tree.setSelectionPath(path);
				tree.expandPath(path);
			}
		}
	}

	public final void expandGenericCategory(int catID) {
		if (catID < 0) {
			tree.clearSelection();
		}
		else {
			GenericCategoryNode node = findNode(catID);
			if (node != null) {
				TreePath path = getTreePath(node);
				tree.expandPath(path);
			}
		}
	}

	public final GenericEntity getSelectedGenericEntity() {
		TreePath selectionPath = tree.getSelectionPath();
		if (selectionPath != null) {
			if (selectionPath.getLastPathComponent() instanceof GenericEntityNode) {
				return ((GenericEntityNode) selectionPath.getLastPathComponent()).getGenericEntity();
			}
		}
		return null;
	}

	public final void selectGenericEntity(int entityID) {
		if (entityID < 0) {
			tree.clearSelection();
		}
		else {
			GenericEntityNode node = findEntityNode(entityID);
			if (node != null) {
				TreePath path = getTreePath(node);
				tree.setSelectionPath(path);
				expandPath(path);
			}
		}
	}

	private GenericEntityNode findEntityNode(int catID) {
		GenericEntityNode node = null;
		for (Enumeration<?> enumeration = ((TreeNode) tree.getModel().getRoot()).children(); enumeration.hasMoreElements() && node == null;) {
			Object obj = enumeration.nextElement();
			if (obj instanceof GenericCategoryNode) {
				node = findEntityNode((GenericCategoryNode) obj, catID);
			}
		}
		return node;
	}

	private GenericEntityNode findEntityNode(GenericCategoryNode parent, int catID) {
		if (parent.isLeaf()) {
			return null;
		}
		else {
			GenericEntityNode node = null;
			for (Enumeration<?> enumeration = parent.children(); enumeration.hasMoreElements() && node == null;) {
				Object obj = enumeration.nextElement();
				if (obj instanceof GenericCategoryNode) {
					node = findEntityNode((GenericCategoryNode) obj, catID);
				}
				else if (obj instanceof GenericEntityNode) {
					node = findEntityNode((GenericEntityNode) obj, catID);
				}
			}
			return node;
		}
	}

	private GenericEntityNode findEntityNode(GenericEntityNode node, int catID) {
		if (node.getGenericEntity().getID() == catID) {
			return node;
		}
		else {
			return null;
		}
	}

	public final TreePath getTreePath(int catID) {
		GenericCategoryNode node = findNode(catID);
		return (node == null ? null : getTreePath(node));
	}

	private GenericCategoryNode findNode(int catID) {
		GenericCategoryNode node = null;
		for (Enumeration<?> enumeration = ((GenericCategoryNode) tree.getModel().getRoot()).children(); enumeration.hasMoreElements()
				&& node == null;) {
			Object element = enumeration.nextElement();
			if (element instanceof GenericCategoryNode) {
				node = findNode((GenericCategoryNode) element, catID);
			}
		}
		return node;
	}

	private GenericCategoryNode findNode(GenericCategoryNode parent, int catID) {
		if (parent.getGenericCategory().getID() == catID) {
			return parent;
		}
		else if (parent.isLeaf()) {
			return null;
		}
		else {
			GenericCategoryNode node = null;
			for (Enumeration<?> enumeration = parent.children(); enumeration.hasMoreElements() && node == null;) {
				Object element = enumeration.nextElement();
				if (element instanceof GenericCategoryNode) {
					node = findNode((GenericCategoryNode) element, catID);
				}
			}
			return node;
		}
	}

	public void expandCategories(TreePath parent, List<Integer> categoryIDs) {
		for (Iterator<Integer> i = categoryIDs.iterator(); i.hasNext();) {
			Integer catID = i.next();
			expandGenericCategory(catID.intValue());
		}
	}

	public final void addRefreshButtonListener(ActionListener actionListener) {
		refreshButton.addActionListener(actionListener);
	}

	public final void removeRefreshButtonListener(ActionListener actionListener) {
		refreshButton.removeActionListener(actionListener);
	}

}