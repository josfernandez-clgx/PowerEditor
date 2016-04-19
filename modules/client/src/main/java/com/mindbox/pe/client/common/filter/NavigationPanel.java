package com.mindbox.pe.client.common.filter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.client.common.tree.CloneNavigationNode;
import com.mindbox.pe.client.common.tree.GenericCategorySelectionTree;
import com.mindbox.pe.client.common.tree.NavigationNode;
import com.mindbox.pe.client.common.tree.RootNavigationNode;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.IDNameObject;
import com.mindbox.pe.model.SimpleEntityData;
import com.mindbox.pe.model.filter.CloneGenericEntityFilter;
import com.mindbox.pe.model.filter.GenericEntityByCategoryFilter;

public final class NavigationPanel extends JPanel implements IFilterSubpanel<GenericEntity> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static final int NON_CLONED_ID = -1;

	private class NavigationTreeCellRenderer extends DefaultTreeCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private NavigationTreeCellRenderer(String openIconStr, String closeIconStr) {
			setOpenIcon(ClientUtil.getInstance().makeImageIcon(openIconStr));
			setClosedIcon(ClientUtil.getInstance().makeImageIcon(closeIconStr));
			setLeafIcon(ClientUtil.getInstance().makeImageIcon(closeIconStr));
		}

		public Component getTreeCellRendererComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i, boolean flag3) {
			Object obj1 = ((DefaultMutableTreeNode) obj).getUserObject();
			if (obj1 instanceof IDNameObject) obj1 = ((IDNameObject) obj1).getName();
			Component component = super.getTreeCellRendererComponent(jtree, obj1, flag, flag1, flag2, i, flag3);
			return component;
		}
	}

	private final boolean showCategories;
	private final boolean showClones;
	private final GenericEntityType entityType;
	private final int categoryType;
	private GenericCategorySelectionTree categoryTree = null;
	private JTree cloneTree = null;
	private JButton fetchButton = null;
	private final MDateDateField dateField;

	/**
	 * 
	 * @param displayCategories
	 * @param displayClones
	 * @param type
	 */
	public NavigationPanel(boolean displayCategories, boolean displayClones, GenericEntityType type, int categoryType) {
		super();
		this.showCategories = displayCategories;
		this.showClones = displayClones;
		this.entityType = type;
		this.categoryType = categoryType;
		this.dateField = new MDateDateField(true, false, true);
		UIFactory.setLookAndFeel(this);
	}

	public void build() {
		initComponents();
		addComponents();
	}

	private void addComponents() {
		setLayout(new BorderLayout(3, 3));
		if (showCategories && showClones) {
			JSplitPane splitPane = UIFactory.createSplitPane(JSplitPane.VERTICAL_SPLIT, categoryTree.getJComponent(), new JScrollPane(cloneTree));
			add(splitPane, BorderLayout.CENTER);
			splitPane.setDividerLocation(300);
		}
		else if (showCategories) {
			add(categoryTree.getJComponent(), BorderLayout.CENTER);
		}
		else {
			add(new JScrollPane(cloneTree), BorderLayout.CENTER);
		}
		add(fetchButton, BorderLayout.SOUTH);
	}

	private Date getSelectedDate() {
		return dateField.getDate();
	}

	private void initComponents() {
		if (showCategories) {
			categoryTree = new GenericCategorySelectionTree(categoryType, false, true, true);
			categoryTree.setTreeCellRenderer(new NavigationTreeCellRenderer("image.node.category", "image.node.category"));
			categoryTree.getTreeSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		}

		if (showClones) {
			cloneTree = new JTree(createCloneTreeModel());
			cloneTree.setCellRenderer(new NavigationTreeCellRenderer("image.node.product", "image.node.product"));
			cloneTree.putClientProperty("JTree.linestyle", "Angled");
			cloneTree.setShowsRootHandles(true);
			cloneTree.setRootVisible(false);
			cloneTree.setRowHeight(18);
			cloneTree.addTreeExpansionListener(new TreeExpansionListener() {

				public void treeCollapsed(TreeExpansionEvent treeexpansionevent) {
				}

				public void treeExpanded(TreeExpansionEvent treeexpansionevent) {
					TreePath treepath = treeexpansionevent.getPath();
					NavigationNode navigationnode = (NavigationNode) treepath.getLastPathComponent();
					if (!navigationnode.isExplored()) {
						DefaultTreeModel defaulttreemodel = (DefaultTreeModel) cloneTree.getModel();
						navigationnode.explore();
						defaulttreemodel.nodeStructureChanged(navigationnode);
					}
				}
			});
			cloneTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		}

		fetchButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.search." + entityType.toString(), "Search " + entityType.getDisplayName()), null, null, null);
	}

	private TreeModel createCloneTreeModel() {
		RootNavigationNode rootnavigationnode = null;
		CloneNavigationNode clonenavigationnode = null;
		String s = "<html><b><font size=3 color=#000000 face=arial,geneva>" + ClientUtil.getInstance().getLabel("label.node.clone.root") + "</font></b></html>";
		IDNameObject simpledatabean = new SimpleEntityData(NON_CLONED_ID, s);
		clonenavigationnode = new CloneNavigationNode(simpledatabean, entityType);
		rootnavigationnode = new RootNavigationNode("");
		if (clonenavigationnode != null) rootnavigationnode.add(clonenavigationnode);
		return new DefaultTreeModel(rootnavigationnode);
	}

	private List<GenericEntity> filterCategoryProducts(int[] ids) throws ServerException {
		return ClientUtil.getCommunicator().search(new GenericEntityByCategoryFilter(entityType, ids, getSelectedDate(), true));
	}

	private List<GenericEntity> filterProductClones(int id) throws ServerException {
		return ClientUtil.getCommunicator().search(new CloneGenericEntityFilter(entityType, id, true));
	}

	public void addActionListener(ActionListener actionlistener) {
		fetchButton.addActionListener(actionlistener);
	}

	public Insets getInsets() {
		return new Insets(2, 2, 2, 2);
	}

	public List<GenericEntity> doFilter() throws ServerException {
		boolean fetchForCategories = true;
		TreePath[] selectionPaths = null;
		GenericCategory[] categories = null;
		if (showCategories && showClones) {
			categories = categoryTree.getSelectedCategories();
			TreePath clonePath = cloneTree.getSelectionPath();
			if ((categories == null || categories.length == 0) && clonePath == null) {
				ClientUtil.getInstance().showWarning("msg.warning.select.catOrClone");
				return new ArrayList<GenericEntity>();
			}
			else if (categories != null && clonePath != null && categories.length > 0) {
				Object selection = JOptionPane.showInputDialog(
						ClientUtil.getApplet(),
						"You have selected both categories and clones. Please select entity for which to fetch " + entityType.getDisplayName() + ".",
						"Select Category of Clone",
						JOptionPane.QUESTION_MESSAGE,
						null,
						new String[] { "Category", "Clone" },
						null);
				if (selection == null) {
					return new ArrayList<GenericEntity>();
				}
				else if (selection.equals("Clone")) {
					fetchForCategories = false;
					selectionPaths = new TreePath[] { clonePath };
				}
			}
			else if (clonePath != null) {
				fetchForCategories = false;
				selectionPaths = new TreePath[] { clonePath };
			}
		}
		else if (showCategories) {
			categories = categoryTree.getSelectedCategories();
			if (categories == null || categories.length == 0) {
				ClientUtil.getInstance().showWarning("msg.warning.select.category");
				return new ArrayList<GenericEntity>();
			}
		}
		else {
			TreePath clonePath = cloneTree.getLeadSelectionPath();
			if (clonePath == null) {
				ClientUtil.getInstance().showWarning("msg.warning.select.clone");
				return new ArrayList<GenericEntity>();
			}
			fetchForCategories = false;
			selectionPaths = new TreePath[] { clonePath };
		}

		// check root node is not selected
		if (fetchForCategories) {
			int[] catIDs = new int[categories.length];
			for (int i = 0; i < catIDs.length; i++) {
				catIDs[i] = categories[i].getID();
			}
			return filterCategoryProducts(catIDs);
		}
		else {
			// filter for clone - only use the last selection
			if (!(selectionPaths[0].getLastPathComponent() instanceof CloneNavigationNode)) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.node.root");
				return new ArrayList<GenericEntity>();
			}
			return filterProductClones(((IDNameObject) ((NavigationNode) selectionPaths[0].getLastPathComponent()).getUserObject()).getID());
		}
	}

}
