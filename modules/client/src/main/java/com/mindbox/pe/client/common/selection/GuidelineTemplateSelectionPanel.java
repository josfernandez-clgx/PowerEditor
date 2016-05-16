/*
 * Created on 2003. 12. 15.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.common.selection;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.table.TemplateIDNameTable;
import com.mindbox.pe.client.common.tree.AbstractDataTreeNode;
import com.mindbox.pe.client.common.tree.RootTreeNode;
import com.mindbox.pe.client.common.tree.TemplateTreeNode;
import com.mindbox.pe.client.common.tree.TemplateTreeRenderer;
import com.mindbox.pe.client.common.tree.UsageGroupTreeNode;
import com.mindbox.pe.client.common.tree.UsageTypeTreeNode;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.TemplateFilter;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.xsd.config.GuidelineTab;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class GuidelineTemplateSelectionPanel extends PanelBase {

	private final class TableViewCheckBoxL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (((JRadioButton) e.getSource()).isSelected()) {
				displayTemplateAsTable = true;
				refreshDisplay();
				fireClearSelection();
			}
		}
	}

	private class TemplateTableSelectionL implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			if (displayTemplateAsTable) {

				if (templateTable.getSelectedRow() != -1) {
					try {
						fireTemplateSelected((GridTemplate) templateTable.getModel().getValueAt(templateTable.getSelectedRow(), -1));
					}
					catch (CanceledException e) {
						if (lastSelectedTableRow < 0) {
							templateTable.clearSelection();
						}
						else {
							templateTable.getSelectionModel().setSelectionInterval(lastSelectedTableRow, lastSelectedTableRow);
						}
					}
				}
				else {
					fireClearSelection();
				}
			}
		}
	}

	private final class TreeExpandL implements TreeWillExpandListener, TreeExpansionListener {

		@Override
		public void treeCollapsed(TreeExpansionEvent event) {
			Object node = event.getPath().getLastPathComponent();
			setLastCollapsedNode(node);
		}

		@Override
		public void treeExpanded(TreeExpansionEvent event) {
		}

		@Override
		public void treeWillCollapse(TreeExpansionEvent event) {
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent event) {
		}
	}

	private final class TreeSelectionL implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			Object node = tree.getLastSelectedPathComponent();
			if (node == null) {
				fireClearSelection();
				lastSelectedPath = tree.getSelectionPath();
			}
			else {
				if (node == getLastCollapsedNode()) {
					return;
				}

				if (node instanceof UsageTypeTreeNode) {
					lastSelectedUsage = ((UsageTypeTreeNode) node).getUsageType();
					try {
						fireUsageSelected(lastSelectedUsage);
					}
					catch (CanceledException e2) {
						if (lastSelectedPath == null) {
							tree.clearSelection();
						}
						else {
							tree.setSelectionPath(lastSelectedPath);
						}
					}

					if (!displayTemplateAsTable) {
						try {
							refreshTree((UsageTypeTreeNode) node);
						}
						catch (ServerException e1) {
							e1.printStackTrace();
							String messageFromResource = null;
							try {
								messageFromResource = ClientUtil.getInstance().getMessage(e1.getMessage());
							}
							catch (Exception ex2) {
							}
							ClientUtil.getInstance().showWarning(
									"msg.warning.failure.fetch.template",
									new Object[] {
											((UsageTypeTreeNode) node).getUsageType().getDisplayName(),
											(messageFromResource == null ? e1.getMessage() : messageFromResource) });
						}
					}
				}
				else if (node instanceof TemplateTreeNode && !displayTemplateAsTable) {
					try {
						// This is a high-overhead fix to TT938: after creating a new template and
						// saving it, then creating columns and saving them, and going to another
						// template in the same usage type and back to the new template, the columns 
						// cannot be seen.  This fixes it by getting the template again from the server 
						// every time you switch templates.  This next line can be removed if the
						// the cause is discovered -pdk
						//node = refreshTemplateTreeNode((TemplateTreeNode) node);

						// gkim - trying...

						// populate detail panel
						fireTemplateSelected(((TemplateTreeNode) node).getTemplate());
					}
					catch (CanceledException e1) {
						if (lastSelectedPath == null) {
							tree.clearSelection();
						}
						else {
							tree.setSelectionPath(lastSelectedPath);
						}
						return;
					}
				}
				else if (node instanceof UsageGroupTreeNode) {
					try {
						// populate detail panel
						fireUsageGroupdSelected(((UsageGroupTreeNode) node).getGuidelineTab());
					}
					catch (CanceledException e1) {
						if (lastSelectedPath == null) {
							tree.clearSelection();
						}
						else {
							tree.setSelectionPath(lastSelectedPath);
						}
						return;
					}
				}
				lastSelectedPath = tree.getSelectionPath();
			}
		}
	}

	private final class TreeViewCheckBoxL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (((JRadioButton) e.getSource()).isSelected()) {
				displayTemplateAsTable = false;
				refreshDisplay();
				fireClearSelection();
			}
		}
	}

	private static final long serialVersionUID = -3951228734910107454L;

	private final JPanel viewPanel;
	private final CardLayout viewCard;
	private boolean displayTemplateAsTable = false;
	private JTree tree = null;
	private DefaultTreeModel templateTreeModel;
	private TreeSelectionL treeSelectionListener = null;
	private RootTreeNode rootNode = null;
	private TemplateIDNameTable templateTable = null;
	private TemplateTableSelectionL tableSelectionListener = null;
	private GuidelineTemplateSelectionListener guidelineSelectionListener = null;
	private TemplateUsageType lastSelectedUsage = null;
	private TreePath lastSelectedPath = null;
	private int lastSelectedTableRow = -1;
	private Object lastCollapsedNode = null;
	private final boolean readOnly;

	public GuidelineTemplateSelectionPanel(boolean allowTableView, boolean searchView, boolean readOnly) {
		this(null, allowTableView, searchView, readOnly);
	}

	public GuidelineTemplateSelectionPanel(JButton[] topButtons, boolean allowTableView, boolean searchView, boolean readOnly) {
		this.readOnly = readOnly;
		templateTable = new TemplateIDNameTable(EntityModelCacheFactory.getInstance().getTemplateIDNameTableModel());
		templateTable.setRowSelectionAllowed(true);
		templateTable.setColumnSelectionAllowed(false);
		templateTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.rootNode = new RootTreeNode(null);
		if (searchView) {// make search template tree based on guideline privileges
			templateTreeModel = EntityModelCacheFactory.getInstance().getGuidelineTemplateSearchTreeModel();
		}
		else {// make manage template tree based on template privileges
			templateTreeModel = EntityModelCacheFactory.getInstance().getGuidelineTemplateTreeModel();
		}

		tree = UIFactory.createTree(templateTreeModel, new TemplateTreeRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		viewCard = new CardLayout();
		viewPanel = new JPanel(viewCard);
		if (allowTableView) {
			viewPanel.add(new JScrollPane(templateTable), "TABLE");
		}
		viewPanel.add(new JScrollPane(tree), "TREE");

		initPanel(topButtons, allowTableView);

		tableSelectionListener = new TemplateTableSelectionL();
		templateTable.getSelectionModel().addListSelectionListener(tableSelectionListener);
		treeSelectionListener = new TreeSelectionL();
		tree.addTreeSelectionListener(treeSelectionListener);

		TreeExpandL treeExpandListener = new TreeExpandL();
		tree.addTreeExpansionListener(treeExpandListener);

		refreshDisplay();
		expandTreePaths();
	}

	public void addMouseListenerToTable(MouseListener ml) {
		templateTable.addMouseListener(ml);
	}

	public void addMouseListenerToTree(MouseListener ml) {
		tree.addMouseListener(ml);
	}

	public void addTemplate(GridTemplate template) {
		EntityModelCacheFactory.getInstance().add(template);
		addToCurrentNode_internal(template);
	}

	private void addToCurrentNode_internal(GridTemplate template) {
		tree.removeTreeSelectionListener(treeSelectionListener);
		try {
			UsageTypeTreeNode usageNode = findUsageTypeTreeNode(template.getUsageType());
			if (usageNode != null) {

				TemplateTreeNode childNode = findChild(usageNode, template);
				if (childNode == null) {
				}
				else {
					TreePath selectionPath = new TreePath(templateTreeModel.getPathToRoot(childNode));
					tree.expandPath(selectionPath);
					tree.setSelectionPath(selectionPath);
				}
			}
			else {
				ClientUtil.getLogger().warn("NO usage type node found for " + template.getUsageType());
			}
		}
		finally {
			tree.addTreeSelectionListener(treeSelectionListener);
		}
	}

	public void editTemplate(GridTemplate td) throws CanceledException {
		// select the template
		UsageTypeTreeNode usageNode = findUsageTypeTreeNode(td.getUsageType());
		if (usageNode != null) {
			TemplateTreeNode node = findChild(usageNode, td);
			if (node != null) {
				tree.setSelectionPath(getTreePath(node));
			}
		}
	}

	private void expandTreePaths() {

	}

	private void expandUsageGroupNodes() {
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			TreeNode node = this.rootNode.getChildAt(i);
			TreePath selectionPath = new TreePath(templateTreeModel.getPathToRoot(node));
			tree.expandPath(selectionPath);
		}
	}

	private TemplateTreeNode findChild(UsageTypeTreeNode parent, GridTemplate template) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			if (template.getID() == ((GridTemplate) ((TemplateTreeNode) parent.getChildAt(i)).getData()).getID()) {
				return (TemplateTreeNode) parent.getChildAt(i);
			}
		}
		return null;
	}

	private UsageTypeTreeNode findUsageTypeTreeNode(TemplateUsageType usage) {
		RootTreeNode rootNode = (RootTreeNode) templateTreeModel.getRoot();
		UsageTypeTreeNode usageNode = null;
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			usageNode = findUsageTypeTreeNode_aux((AbstractDataTreeNode) rootNode.getChildAt(i), usage);
			if (usageNode != null) {
				return usageNode;
			}
		}
		return null;
	}

	private UsageTypeTreeNode findUsageTypeTreeNode_aux(AbstractDataTreeNode parent, TemplateUsageType usage) {
		if (parent instanceof UsageTypeTreeNode) {
			if (usage == ((UsageTypeTreeNode) parent).getData()) {
				return (UsageTypeTreeNode) parent;
			}
		}
		else if (parent instanceof UsageGroupTreeNode) {
			for (int i = 0; i < parent.getChildCount(); i++) {
				if (usage == ((UsageTypeTreeNode) parent.getChildAt(i)).getData()) {
					return (UsageTypeTreeNode) parent.getChildAt(i);
				}
			}
		}
		return null;
	}

	private void fireClearSelection() {
		if (guidelineSelectionListener != null) {
			guidelineSelectionListener.selectionCleared();
		}
	}

	private void fireTemplateSelected(GridTemplate td) throws CanceledException {
		if (guidelineSelectionListener != null) {
			guidelineSelectionListener.templateSelected(td);
		}
	}

	private void fireUsageGroupdSelected(GuidelineTab config) throws CanceledException {
		if (guidelineSelectionListener != null) {
			guidelineSelectionListener.usageGroupSelected(config);
		}
	}

	private void fireUsageSelected(TemplateUsageType usage) throws CanceledException {
		if (guidelineSelectionListener != null) {
			guidelineSelectionListener.usageSelected(usage);
		}
	}

	private Object getLastCollapsedNode() {
		synchronized (tree) {
			return lastCollapsedNode;
		}
	}

	public TemplateUsageType getLastSelectedUsageType() {
		return lastSelectedUsage;
	}

	public List<GridTemplate> getSelectedTemplates() {
		List<GridTemplate> list = new ArrayList<GridTemplate>();
		if (displayTemplateAsTable) {
			int row = templateTable.getSelectedRow();
			if (row > -1) {
				list.add((GridTemplate) templateTable.getModel().getValueAt(row, -1));
			}
		}
		else {
			TreeNode node = (TreeNode) tree.getLastSelectedPathComponent();
			if (node != null) {
				getSelectedTemplates_aux(list, node);
			}
		}
		return list;
	}

	private void getSelectedTemplates_aux(List<GridTemplate> list, TreeNode node) {
		if (node instanceof UsageGroupTreeNode) {
			for (int i = 0; i < node.getChildCount(); i++) {
				getSelectedTemplates_aux(list, node.getChildAt(i));
			}
		}
		else if (node instanceof UsageTypeTreeNode) {
			for (int i = 0; i < node.getChildCount(); i++) {
				getSelectedTemplates_aux(list, node.getChildAt(i));
			}
		}
		else if (node instanceof TemplateTreeNode) {
			list.add(((TemplateTreeNode) node).getTemplate());
		}
	}


	private TreePath getTreePath(AbstractDataTreeNode node) {
		if (node == null) return null;
		LinkedList<TreeNode> parentList = new LinkedList<TreeNode>();
		parentList.add(node);
		for (TreeNode parent = node.getParent(); parent != null;) {
			parentList.add(0, parent);
			parent = parent.getParent();
		}
		return new TreePath(parentList.toArray(new TreeNode[0]));
	}

	private void initPanel(JButton[] topButtons, boolean allowTableView) {
		JPanel tnPanel = null;
		if (allowTableView) {
			JRadioButton asTreeRadio = new JRadioButton(ClientUtil.getInstance().getLabel("radio.as.tree"), false);
			asTreeRadio.addActionListener(new TreeViewCheckBoxL());
			asTreeRadio.setFocusable(false);
			JRadioButton asTableRadio = new JRadioButton(ClientUtil.getInstance().getLabel("radio.as.table"), false);
			asTableRadio.addActionListener(new TableViewCheckBoxL());
			asTableRadio.setFocusable(false);

			ButtonGroup group = new ButtonGroup();
			group.add(asTreeRadio);
			group.add(asTableRadio);

			tnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
			tnPanel.add(asTreeRadio);
			tnPanel.add(asTableRadio);

			asTreeRadio.setSelected(true);
			asTableRadio.setSelected(false);
		}

		JPanel topPanel = UIFactory.createBorderLayoutPanel(0, 0);

		if (topButtons != null && topButtons.length > 0) {
			JPanel btnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
			for (int i = 0; i < topButtons.length; i++) {
				btnPanel.add(topButtons[i]);
			}
			topPanel.add(btnPanel, BorderLayout.EAST);
			if (tnPanel != null) topPanel.add(tnPanel, BorderLayout.WEST);
		}
		else {
			if (tnPanel != null) topPanel.add(tnPanel, BorderLayout.CENTER);
		}

		setLayout(new BorderLayout(1, 1));
		add(topPanel, BorderLayout.NORTH);
		add(viewPanel, BorderLayout.CENTER);
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void moveTemplateNode(TemplateUsageType oldUsage, GridTemplate template) {
		EntityModelCacheFactory.getInstance().move(template, oldUsage);
		tree.removeTreeSelectionListener(treeSelectionListener);
		try {
			fireUsageSelected(template.getUsageType());
			fireTemplateSelected(template);
			UsageTypeTreeNode usageNode = findUsageTypeTreeNode(template.getUsageType());
			if (usageNode != null) {
				TemplateTreeNode childNode = findChild(usageNode, template);
				if (childNode == null) {
				}
				else {
					TreePath selectionPath = new TreePath(templateTreeModel.getPathToRoot(childNode));
					tree.expandPath(selectionPath);
					tree.setSelectionPath(selectionPath);
					fireTemplateSelected(template);
				}
			}
			else {
				ClientUtil.getLogger().warn("NO usage type node found for " + template.getUsageType());
			}
		}
		catch (CanceledException e) {
			e.printStackTrace();
		}
		finally {
			tree.addTreeSelectionListener(treeSelectionListener);
		}
	}

	private void refreshDisplay() {
		if (displayTemplateAsTable) {
			templateTable.clearSelection();
			fireClearSelection();
			viewCard.show(viewPanel, "TABLE");
		}
		else {
			tree.clearSelection();
			fireClearSelection();
			viewCard.show(viewPanel, "TREE");
		}
	}

	/**
	 * Reload templates for the specified usage type and refreshes tree.
	 * @param usageNode
	 * @throws ServerException
	 */
	private void refreshTree(UsageTypeTreeNode usageNode) throws ServerException {
		setCursor(UIFactory.getWaitCursor());
		try {
			TemplateFilter filter = new TemplateFilter(usageNode.getUsageType());
			filter.setSkipSecurityCheck(true);
			List<GridTemplate> list = ClientUtil.getCommunicator().search(filter);
			if (list != null) {
				usageNode.removeAllChildren();
				for (Iterator<GridTemplate> iter = list.iterator(); iter.hasNext();) {
					GridTemplate template = iter.next();
					usageNode.addChild(new TemplateTreeNode(usageNode, template), true);
				}
				templateTreeModel.nodeStructureChanged(usageNode);
				tree.expandPath(tree.getSelectionPath());
			}
		}
		finally {
			setCursor(UIFactory.getDefaultCursor());
		}
	}

	public GridTemplate reloadTemplate(GridTemplate template) {
		TemplateTreeNode node = findChild(findUsageTypeTreeNode(template.getUsageType()), template);
		if (node != null) {
			try {
				TemplateFilter filter = new TemplateFilter(template.getUsageType());
				filter.setSkipSecurityCheck(true);
				filter.setTemplateID(template.getID());
				List<GridTemplate> list = ClientUtil.getCommunicator().search(filter);
				if (list != null && list.size() > 0) {
					node.setTemplate(list.get(0));
					return node.getTemplate();
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				setCursor(UIFactory.getDefaultCursor());
			}
		}
		return template;
	}

	public void reloadTemplates() throws ServerException {
		EntityModelCacheFactory.getInstance().cacheTemplates();
		EntityModelCacheFactory.getInstance().reloadGuidelineTemplateTreeModel();
		templateTreeModel = EntityModelCacheFactory.getInstance().getGuidelineTemplateTreeModel();
		tree.setModel(templateTreeModel);
		refreshDisplay();
		fireClearSelection();
		expandUsageGroupNodes();
	}

	public void removeTemplate(GridTemplate template) {
		EntityModelCacheFactory.getInstance().remove(template);
	}

	public void setGuidelineSelectionListener(GuidelineTemplateSelectionListener listener) {
		this.guidelineSelectionListener = listener;
	}

	private void setLastCollapsedNode(Object node) {
		synchronized (tree) {
			lastCollapsedNode = node;
		}
	}

	public void updateTemplateName(GridTemplate template) {
		EntityModelCacheFactory.getInstance().updateName(template);
		tree.removeTreeSelectionListener(treeSelectionListener);
		try {
			UsageTypeTreeNode usageNode = findUsageTypeTreeNode(template.getUsageType());
			if (usageNode != null) {
				TemplateTreeNode childNode = findChild(usageNode, template);
				if (childNode == null) {
				}
				else {
					TreePath selectionPath = new TreePath(templateTreeModel.getPathToRoot(childNode));
					tree.expandPath(selectionPath);
					tree.setSelectionPath(selectionPath);
					fireTemplateSelected(template);
				}
			}
			else {
				ClientUtil.getLogger().warn("NO usage type node found for " + template.getUsageType());
			}
		}
		catch (CanceledException e) {
			e.printStackTrace();
		}
		finally {
			tree.addTreeSelectionListener(treeSelectionListener);
		}
	}
}
