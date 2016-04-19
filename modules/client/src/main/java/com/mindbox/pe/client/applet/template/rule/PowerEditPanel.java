package com.mindbox.pe.client.applet.template.rule;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.template.tree.AbstractRuleTreeNode;
import com.mindbox.pe.client.applet.template.tree.ActionParamTreeNode;
import com.mindbox.pe.client.applet.template.tree.ActionTreeNode;
import com.mindbox.pe.client.applet.template.tree.ConditionTreeNode;
import com.mindbox.pe.client.applet.template.tree.ExistTreeNode;
import com.mindbox.pe.client.applet.template.tree.FunctionTreeNode;
import com.mindbox.pe.client.applet.template.tree.IfTreeNode;
import com.mindbox.pe.client.applet.template.tree.LogicalOpAttachable;
import com.mindbox.pe.client.applet.template.tree.LogicalOpTreeNode;
import com.mindbox.pe.client.applet.template.tree.RuleTreeRenderer2;
import com.mindbox.pe.client.applet.template.tree.RuleTreeRendererColRefValue;
import com.mindbox.pe.client.applet.template.tree.TestTreeNode;
import com.mindbox.pe.client.applet.template.tree.ThenTreeNode;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.tree.RootTreeNode;
import com.mindbox.pe.common.validate.DataTypeCompatibilityValidator;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.CompoundRuleElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.LHSElement;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.TestCondition;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * Rule editor.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class PowerEditPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static boolean isNonEmptyNotNode(AbstractRuleTreeNode node) {
		return (node instanceof LogicalOpTreeNode && ((LogicalOpTreeNode) node).getCompoundLHSElementType() == CompoundLHSElement.TYPE_NOT && ((LogicalOpTreeNode) node).getChildCount() > 0);
	}

	private static boolean isAndNodeWithNotParent(AbstractRuleTreeNode node) {
		return (node instanceof LogicalOpTreeNode && ((LogicalOpTreeNode) node).getCompoundLHSElementType() == CompoundLHSElement.TYPE_AND)
				&& (node.getParent() instanceof LogicalOpTreeNode && ((LogicalOpTreeNode) node.getParent()).getCompoundLHSElementType() == CompoundLHSElement.TYPE_NOT);
	}

	private static ImageIcon createImage(String name) {
		return ClientUtil.getInstance().makeImageIcon(name);
	}

	private class DeleteAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private DeleteAction() {
			super(null, createImage("image.btn.small.delete"));
		}

		public void actionPerformed(ActionEvent arg0) {
			if (currentNode instanceof ConditionTreeNode) {
				if (ClientUtil.getInstance().showConfirmation("msg.question.delete.rule.condition")) {
					// delete condition
					deleteNode((ConditionTreeNode) currentNode);
				}
			}
			else if (currentNode instanceof LogicalOpTreeNode && ((LogicalOpTreeNode) currentNode).getCompoundLHSElementType() == CompoundLHSElement.TYPE_NOT) {
				AbstractRuleTreeNode selectedNode = currentNode;
				if (selectedNode.getChildCount() < 1) {
					deleteNode(selectedNode);
				}
				else {
					AbstractRuleTreeNode parentNode = (AbstractRuleTreeNode) selectedNode.getParent();
					int index = parentNode.getIndex(selectedNode);

					AbstractRuleTreeNode childNode = (AbstractRuleTreeNode) selectedNode.getChildAt(0);

					selectedNode.removeChild(childNode);
					deleteNode(selectedNode);

					addToNode(parentNode, index, childNode, true, true);
				}
			}
			else if (currentNode instanceof LogicalOpTreeNode || currentNode instanceof ExistTreeNode) {
				if (currentNode.getChildCount() > 0) {
					ClientUtil.getInstance().showWarning("msg.warning.not.empty.rule.element", new Object[] { currentNode.getRuleElement().toDisplayName() });
				}
				else if (ClientUtil.getInstance().showConfirmation("msg.question.delete.rule.element", new Object[] { currentNode.getRuleElement().toDisplayName() })) {
					deleteNode((AbstractRuleTreeNode) currentNode);
				}
			}
			else if (currentNode instanceof ActionTreeNode) {
				// delete action
				if (ClientUtil.getInstance().showConfirmation("msg.question.delete.rule.action")) {
					// delete condition
					deleteNode((ActionTreeNode) currentNode);
				}
			}
			else if (currentNode instanceof TestTreeNode) {
				// delete action
				if (ClientUtil.getInstance().showConfirmation("msg.question.delete.test.condition")) {
					// delete condition
					deleteNode((TestTreeNode) currentNode);
				}
			}
			else if (currentNode instanceof ThenTreeNode && currentNode.getChildCount() > 0) {
				// delete action
				if (ClientUtil.getInstance().showConfirmation("msg.question.delete.rule.action")) {
					// delete condition
					deleteNode((AbstractRuleTreeNode) currentNode.getChildAt(0), false);
				}
			}
			else if (currentNode instanceof IfTreeNode && currentNode.getChildCount() > 0) {
				if (ClientUtil.getInstance().showConfirmation("msg.question.delete.rule.conditions")) {
					IfTreeNode ifNode = (IfTreeNode) currentNode;
					int count = ifNode.getChildCount();
					for (int i = 0; i < count; i++) {
						deleteNode((AbstractRuleTreeNode) ifNode.getChildAt(0), false);
					}
				}
			}
		}
	}

	private class ActionAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private ActionAction() {
			super(null, createImage("image.btn.adhoc.action"));
		}

		public void actionPerformed(ActionEvent arg0) {
			if (currentNode instanceof ThenTreeNode) {
				RuleAction action = FunctionEditDialog.newRuleAction(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), usageType);
				if (action != null) {
					addToNode(currentNode, -1, new ActionTreeNode(currentNode, action), true, true);
				}
			}
		}
	}

	private class TestAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private TestAction() {
			super(null, createImage("image.btn.adhoc.test"));
		}

		public void actionPerformed(ActionEvent arg0) {
			if (currentNode instanceof IfTreeNode) {
				TestCondition test = FunctionEditDialog.newTestCondition(JOptionPane.getFrameForComponent(ClientUtil.getApplet()));
				if (test != null) {
					addToNode(currentNode, -1, new TestTreeNode(currentNode, test), true, true);
				}
			}
		}
	}


	private class AssignAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private AssignAction() {
			super(null, createImage("image.btn.adhoc.assign"));
		}

		public void actionPerformed(ActionEvent arg0) {
		}
	}

	private class ConditionAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private ConditionAction() {
			super(null, createImage("image.btn.adhoc.cond"));
		}

		public void actionPerformed(ActionEvent arg0) {
			TreeNode parent = currentNode;
			if (currentNode instanceof ConditionTreeNode) {
				parent = currentNode.getParent();
			}

			Condition condition = ConditionEditDialog.createCondition(template);
			if (condition != null) {
				ConditionTreeNode child = new ConditionTreeNode(parent, condition);
				addToNode((AbstractRuleTreeNode) parent, -1, child, false, true);
				updateRuleFromFields();
			}
		}
	}

	private class ExistAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private ExistAction() {
			super(null, createImage("image.btn.small.exist"));
		}

		public void actionPerformed(ActionEvent arg0) {
			synchronized (PowerEditPanel.this) {
				if (currentNode instanceof LogicalOpAttachable) {
					ExistExpression existExpression = ExistExpressionEditDialog.createExistExpression();
					if (existExpression != null) {
						addToCurrentNode(-1, new ExistTreeNode(currentNode, existExpression), true, false);
					}
				}
			}
		}
	}

	private class AndAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private AndAction() {
			super(null, createImage("image.node.adhoc.and"));
		}

		public void actionPerformed(ActionEvent arg0) {
			synchronized (PowerEditPanel.this) {
				if (currentNode instanceof LogicalOpAttachable) {
					AbstractRuleTreeNode child = new LogicalOpTreeNode(currentNode, RuleElementFactory.getInstance().createAndCompoundCondition());
					addToCurrentNode(-1, child, true, false);
				}
			}
		}
	}

	private class OrAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private OrAction() {
			super(null, createImage("image.node.adhoc.or"));
		}

		public void actionPerformed(ActionEvent arg0) {
			synchronized (PowerEditPanel.this) {
				if (currentNode instanceof LogicalOpAttachable) {
					addToCurrentNode(-1, new LogicalOpTreeNode(currentNode, RuleElementFactory.getInstance().createOrCompoundCondition()), true, false);
				}
				else {
				}
			}
		}
	}

	private class NotAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private NotAction() {
			super(null, createImage("image.node.adhoc.not"));
		}

		public void actionPerformed(ActionEvent arg0) {
			synchronized (PowerEditPanel.this) {
				// remove selected node from parent
				AbstractRuleTreeNode selectedNode = currentNode;

				AbstractRuleTreeNode parentNode = (AbstractRuleTreeNode) selectedNode.getParent();
				int index = parentNode.getIndex(selectedNode);
				deleteNode_internal(selectedNode);

				CompoundLHSElement notElement = RuleElementFactory.getInstance().createNotCompoundCondition();
				notElement.add((LHSElement) selectedNode.getData());
				LogicalOpTreeNode notNode = new LogicalOpTreeNode(parentNode, notElement);

				addToNode(parentNode, index, notNode, true, true);
			}
		}
	}

	private class EditAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private EditAction() {
			super(null, createImage("image.btn.small.edit"));
		}

		public void actionPerformed(ActionEvent arg0) {
			editCurrentNode();
		}
	}

	private class CopyAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private CopyAction() {
			super(null, createImage("image.btn.small.copy"));
		}

		public void actionPerformed(ActionEvent arg0) {
			copyCurrentNode();
		}
	}

	private class CutAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private CutAction() {
			super(null, createImage("image.btn.small.cut"));
		}

		public void actionPerformed(ActionEvent arg0) {
			cutCurrentNode();
		}
	}

	private class PasteAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private PasteAction() {
			super(null, createImage("image.btn.small.paste"));
		}

		public void actionPerformed(ActionEvent arg0) {
			String str = ClientUtil.getClipBoardContent();
			if (str == null) {
				ClientUtil.getInstance().showWarning("msg.warning.failure.paste.clipboard");
			}
			else {
				pasteIntoCurrentNode(str);
			}
		}
	}

	private class UpAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private UpAction() {
			super(null, createImage("image.btn.adhoc.up"));
		}

		public void actionPerformed(ActionEvent arg0) {
			moveCurrentNodeUp();
		}
	}

	private class DownAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private DownAction() {
			super(null, createImage("image.btn.adhoc.down"));
		}

		public void actionPerformed(ActionEvent arg0) {
			moveCurrentNodeDown();
		}
	}

	private class LeftAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private LeftAction() {
			super(null, createImage("image.btn.adhoc.outdent"));
		}

		public void actionPerformed(ActionEvent arg0) {
			outdentCurrentNode();
		}
	}

	private class RightAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private RightAction() {
			super(null, createImage("image.btn.adhoc.indent"));
		}

		public void actionPerformed(ActionEvent arg0) {
			indentCurrentNode();
		}
	}

	private final class TreeSelectionL implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent e) {
			setCurrentNode((AbstractRuleTreeNode) tree.getLastSelectedPathComponent());
		}
	}

	private class TreeModelL implements TreeModelListener {

		public void treeNodesChanged(TreeModelEvent arg0) {
			fireRuleChanged();
		}

		public void treeNodesInserted(TreeModelEvent arg0) {
			fireRuleChanged();
		}

		public void treeNodesRemoved(TreeModelEvent arg0) {
			fireRuleChanged();
		}

		public void treeStructureChanged(TreeModelEvent arg0) {
			fireRuleChanged();
		}
	}

	private final JTree tree;
	private RootTreeNode rootNode = null;
	private DefaultTreeModel treeModel = null;
	private TreeSelectionL treeSelectionListener = null;
	private boolean treeInitialized = false;
	private final Action andAction, orAction, notAction, testAction;
	private final Action editAction, deleteAction, copyAction, cutAction, pasteAction;
	private final Action upAction, downAction, rightAction, leftAction;
	private final Action condAction, actionAction, assignAction;
	private final Action existAction;

	private AbstractRuleTreeNode currentNode = null;
	private RuleDefinition rule = null;

	private boolean enabled = false;
	private TemplateUsageType usageType = null;
	private GridTemplate template = null;
	private final List<RuleChangeListener> ruleChangeListenerList;
	private final TreeModelL treeModelListener;
	private final boolean columnRefValueEditOnly;
	private final RuleTreeRendererColRefValue colRefTreeRenderer;
	private final List<CellValueChangeListener> cellValueChangeListenerList;

	public PowerEditPanel(RuleDefinition rule) {
		this(false, rule);
	}

	/**
	 * 
	 */
	public PowerEditPanel(boolean columnRefValueEditOnly, RuleDefinition rule) {
		super(new BorderLayout());
		this.columnRefValueEditOnly = columnRefValueEditOnly;
		this.rootNode = new RootTreeNode(null);
		this.rule = rule;
		this.ruleChangeListenerList = new ArrayList<RuleChangeListener>();
		this.cellValueChangeListenerList = new ArrayList<CellValueChangeListener>();
		this.treeModelListener = new TreeModelL();

		treeModel = new DefaultTreeModel(rootNode);
		colRefTreeRenderer = (columnRefValueEditOnly ? new RuleTreeRendererColRefValue() : null);
		tree = (columnRefValueEditOnly ? UIFactory.createTree(treeModel, colRefTreeRenderer) : UIFactory.createTree(treeModel, new RuleTreeRenderer2()));

		andAction = new AndAction();
		orAction = new OrAction();
		notAction = new NotAction();
		testAction = new TestAction();
		editAction = new EditAction();
		deleteAction = new DeleteAction();
		copyAction = new CopyAction();
		cutAction = new CutAction();
		pasteAction = new PasteAction();
		upAction = new UpAction();
		downAction = new DownAction();
		rightAction = new RightAction();
		leftAction = new LeftAction();
		condAction = new ConditionAction();
		actionAction = new ActionAction();
		assignAction = new AssignAction();
		existAction = new ExistAction();

		initPanel();
		initTree();
		tree.setRowHeight(20);

		MouseListener ml = new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1 && enabled) {
					if (e.getClickCount() == 1) {
						//mySingleClick(selRow, selPath);
					}
					else if (e.getClickCount() == 2) {
						setCurrentNode((AbstractRuleTreeNode) selPath.getLastPathComponent());
						if (editAction.isEnabled()) {
							editCurrentNode();
						}
					}
				}
			}
		};
		tree.addMouseListener(ml);

		setEnabledAll(false);

		treeModel.addTreeModelListener(treeModelListener);
	}

	public void addRuleChangeListener(RuleChangeListener listener) {
		synchronized (ruleChangeListenerList) {
			if (!ruleChangeListenerList.contains(listener)) {
				ruleChangeListenerList.add(listener);
			}
		}
	}

	public void removeRuleChangeListener(RuleChangeListener listener) {
		synchronized (ruleChangeListenerList) {
			if (ruleChangeListenerList.contains(listener)) {
				ruleChangeListenerList.remove(listener);
			}
		}
	}

	void fireRuleChanged() {
		synchronized (ruleChangeListenerList) {
			for (int i = 0; i < ruleChangeListenerList.size(); i++) {
				ruleChangeListenerList.get(i).ruleChanged();
			}
		}
	}

	public void addCellValueChangeListener(CellValueChangeListener listener) {
		synchronized (cellValueChangeListenerList) {
			if (!cellValueChangeListenerList.contains(listener)) {
				cellValueChangeListenerList.add(listener);
			}
		}
	}

	public void removeCellValueChangeListener(CellValueChangeListener listener) {
		synchronized (cellValueChangeListenerList) {
			if (cellValueChangeListenerList.contains(listener)) {
				cellValueChangeListenerList.remove(listener);
			}
		}
	}

	private void fireCellValueChanged(int column, Object value) {
		synchronized (cellValueChangeListenerList) {
			for (int i = 0; i < cellValueChangeListenerList.size(); i++) {
				cellValueChangeListenerList.get(i).cellValueChanged(column, value);
			}
		}
	}

	private synchronized void editCurrentNode() {
		if (currentNode instanceof ConditionTreeNode) {
			if (columnRefValueEditOnly) {
				Condition origCondition = ((ConditionTreeNode) currentNode).getCondition();
				if (origCondition.getValue() instanceof ColumnReference) {
					int columnNo = ((ColumnReference) origCondition.getValue()).getColumnNo();

					Condition convertedCond = RuleElementFactory.deepCopyCondition(origCondition);
					convertedCond.setValue(RuleElementFactory.getInstance().createValue(colRefTreeRenderer.getCellValue(columnNo)));

					Condition c = ConditionEditDialog.editCondition(template, convertedCond, columnNo);
					if (c != null) {
						String newValueStr = c.getValue().toString();
						Object newValue = newValueStr;
						if (columnNo > 0 && template.getColumn(columnNo) != null && template.getColumn(columnNo).getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)
								&& template.getColumn(columnNo).getColumnDataSpecDigest().isMultiSelectAllowed()) {
							newValue = EnumValues.parseValue(newValueStr, true, null);
						}
						fireCellValueChanged(columnNo, newValue);
						refreshNode(currentNode);
					}
				}
				else if (origCondition.getValue() instanceof MathExpressionValue) {
					MathExpressionValue origMathExp = (MathExpressionValue) origCondition.getValue();
					int columnNo = origMathExp.getColumnReference().getColumnNo();

					Condition convertedCond = RuleElementFactory.deepCopyCondition(origCondition);
					convertedCond.setValue(RuleElementFactory.getInstance().createValue(colRefTreeRenderer.getCellValue(columnNo), origMathExp.getOperator(), origMathExp.getAttributeReference()));

					Condition c = ConditionEditDialog.editCondition(template, convertedCond, true);
					if (c != null) {
						String newValue = ((MathExpressionValue) c.getValue()).getValue();
						fireCellValueChanged(columnNo, newValue);
						refreshNode(currentNode);
					}
				}
			}
			else {
				Condition c = ConditionEditDialog.editCondition(template, ((ConditionTreeNode) currentNode).getCondition());
				if (c != null) {
					refreshNode(currentNode);
				}
			}
		}
		else if (currentNode instanceof ActionTreeNode) {
			RuleAction action = FunctionEditDialog.editRuleAction(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), usageType, ((ActionTreeNode) currentNode).getRuleAction());
			if (action != null) {
				((ActionTreeNode) currentNode).refreshChildren();
				replaceNode(currentNode);
			}
		}
		else if (currentNode instanceof TestTreeNode) {
			TestCondition test = FunctionEditDialog.editTestCondition(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), ((TestTreeNode) currentNode).getTestCondition());
			if (test != null) {
				((TestTreeNode) currentNode).refreshChildren();
				replaceNode(currentNode);
			}
		}
		else if (currentNode instanceof ActionParamTreeNode) {
			if (columnRefValueEditOnly) {
				FunctionParameter origParam = ((ActionParamTreeNode) currentNode).getFunctionParameter();
				if (origParam != null && origParam instanceof ColumnReference && currentNode.getParent() instanceof FunctionTreeNode) {
					FunctionTypeDefinition typeDef = ((FunctionTreeNode) currentNode.getParent()).getFunctionCall().getFunctionType();
					if (typeDef != null) {
						int columnNo = ((ColumnReference) origParam).getColumnNo();
						FunctionParameter convertedParam = RuleElementFactory.getInstance().createFunctionParameter(
								origParam.index(),
								typeDef.getParameterDefinitionAt(origParam.index()).getName(),
								colRefTreeRenderer.getCellValue(columnNo));

						FunctionParameter newParam = ParameterEditDialog.editFunctionParameter(template, typeDef.getParameterDefinitionAt(origParam.index()), convertedParam, true);
						if (newParam != null) {
							String newValue = newParam.valueString();
							fireCellValueChanged(columnNo, newValue);
							refreshNode(currentNode);
						}
					}
				}
			}
			else {
				FunctionParameter prevParam = ((ActionParamTreeNode) currentNode).getFunctionParameter();
				if (prevParam != null && currentNode.getParent() instanceof FunctionTreeNode) {
					FunctionTypeDefinition typeDef = ((FunctionTreeNode) currentNode.getParent()).getFunctionCall().getFunctionType();
					if (typeDef != null) {
						FunctionParameter newParam = ParameterEditDialog.editFunctionParameter(template, typeDef.getParameterDefinitionAt(prevParam.index()), prevParam);
						if (newParam != null && newParam != prevParam) {
							((ActionParamTreeNode) currentNode).setFunctionParameter(newParam);
							((FunctionTreeNode) currentNode.getParent()).getFunctionCall().replace(prevParam, newParam);
						}
						refreshNode(currentNode);
					}
				}
			}
		}
		else if (currentNode instanceof ExistTreeNode) {
			ExistExpression updatedExistExpression = ExistExpressionEditDialog.editExistExpression(((ExistTreeNode) currentNode).getExistExpression());
			if (updatedExistExpression != null) {
				((ExistTreeNode) currentNode).getExistExpression().setClassName(updatedExistExpression.getClassName());
				((ExistTreeNode) currentNode).getExistExpression().setObjectName(updatedExistExpression.getObjectName());
				((ExistTreeNode) currentNode).getExistExpression().setExcludedObjectName(updatedExistExpression.getExcludedObjectName());
				refreshNode(currentNode);
			}
		}
	}

	private synchronized void moveCurrentNodeDown() {
		AbstractRuleTreeNode nodeToMove = currentNode;
		AbstractRuleTreeNode parent = (AbstractRuleTreeNode) nodeToMove.getParent();

		int childIndex = parent.getIndex(nodeToMove);
		if (childIndex >= 0 && childIndex < (parent.getChildCount() - 1)) {
			tree.removeTreeSelectionListener(treeSelectionListener);
			try {
				parent.swapChildren(childIndex, childIndex + 1);
				treeModel.nodeStructureChanged(parent);

				selectNode(nodeToMove);
			}
			finally {
				tree.addTreeSelectionListener(treeSelectionListener);
			}
		}
	}

	private synchronized void moveCurrentNodeUp() {
		AbstractRuleTreeNode nodeToMove = currentNode;
		AbstractRuleTreeNode parent = (AbstractRuleTreeNode) nodeToMove.getParent();

		int childIndex = parent.getIndex(nodeToMove);
		if (childIndex > 0) {
			tree.removeTreeSelectionListener(treeSelectionListener);
			try {
				parent.swapChildren(childIndex, childIndex - 1);
				treeModel.nodeStructureChanged(parent);

				selectNode(nodeToMove);
			}
			finally {
				tree.addTreeSelectionListener(treeSelectionListener);
			}
		}
	}

	private synchronized void indentCurrentNode() {
		AbstractRuleTreeNode nodeToMove = currentNode;
		AbstractRuleTreeNode parent = (AbstractRuleTreeNode) nodeToMove.getParent();

		int childIndex = parent.getIndex(nodeToMove);
		if (childIndex > 0 && (parent.getChildAt(childIndex - 1) instanceof LogicalOpAttachable)) {
			tree.removeTreeSelectionListener(treeSelectionListener);
			try {

				deleteNode_internal(nodeToMove);
				addToNode_internal((AbstractRuleTreeNode) parent.getChildAt(childIndex - 1), -1, nodeToMove);

				selectNode(nodeToMove);
			}
			finally {
				tree.addTreeSelectionListener(treeSelectionListener);
			}
		}
	}

	private synchronized void outdentCurrentNode() {
		AbstractRuleTreeNode nodeToMove = currentNode;

		AbstractRuleTreeNode parent = (AbstractRuleTreeNode) nodeToMove.getParent();
		if (!(parent instanceof IfTreeNode)) {
			tree.removeTreeSelectionListener(treeSelectionListener);
			try {
				int parentIndex = parent.getParent().getIndex(parent);

				deleteNode_internal(nodeToMove);

				// TT 1013 -- if parent is AND and parent's parent is NOT, 
				//            add it to its parent's parent, not the parent
				if (isAndNodeWithNotParent(parent)) {
					TreeNode notNode = parent.getParent();

					parentIndex = notNode.getParent().getIndex(notNode);
					addToNode_internal((AbstractRuleTreeNode) notNode.getParent(), parentIndex + 1, nodeToMove);
				}
				else {
					addToNode_internal((AbstractRuleTreeNode) parent.getParent(), parentIndex + 1, nodeToMove);
				}

				selectNode(nodeToMove);
			}
			finally {
				tree.addTreeSelectionListener(treeSelectionListener);
			}
		}
	}

	private synchronized void addToCurrentNode(int index, AbstractRuleTreeNode node, boolean selectNode, boolean expand) {
		addToNode(currentNode, index, node, selectNode, expand);
	}

	private synchronized void addToNode(AbstractRuleTreeNode parent, int index, AbstractRuleTreeNode node, boolean selectNode, boolean expandNode) {
		tree.removeTreeSelectionListener(treeSelectionListener);

		try {
			addToNode_internal(parent, index, node);

			if (selectNode) {
				selectNode(node);
				if (expandNode) {
					expandNodeAll(node);
				}

			}
			else {
				selectNode(parent);
				if (expandNode) {
					expandNodeAll(parent);
				}

			}
		}
		finally {
			tree.addTreeSelectionListener(treeSelectionListener);
		}
	}

	private void addToNode_internal(AbstractRuleTreeNode parent, int index, AbstractRuleTreeNode node) {
		// TT 1013 -- add AND node to NOT when adding this results in more than one child
		if (isNonEmptyNotNode(parent)) {
			LogicalOpTreeNode notNode = (LogicalOpTreeNode) parent;

			AbstractRuleTreeNode firstChild = (AbstractRuleTreeNode) notNode.getChildAt(0);

			// if the parent already contains a single AND node, just add to it
			if (notNode.getChildCount() == 1 && firstChild instanceof LogicalOpTreeNode && ((LogicalOpTreeNode) firstChild).getCompoundLHSElementType() == CompoundLHSElement.TYPE_AND) {
				addToNode_internal(firstChild, firstChild.getChildCount(), node);
			}
			// if not, add to a new AND node
			else {
				// create a new AND element
				CompoundLHSElement andElement = RuleElementFactory.getInstance().createAndCompoundCondition();
				for (int i = 0; i < notNode.getChildCount(); i++) {
					andElement.add((LHSElement) ((AbstractRuleTreeNode) notNode.getChildAt(i)).getData());
				}

				// remove all children
				for (int i = 0; i < notNode.getChildCount(); i++) {
					deleteNode_internal((AbstractRuleTreeNode) notNode.getChildAt(i));
				}

				// create a new AND node and add it to the parent
				LogicalOpTreeNode andNode = new LogicalOpTreeNode(notNode, andElement);
				addToNode_internal(notNode, 0, andNode);
				addToNode_internal(andNode, notNode.getChildCount(), node);
			}
		}
		else {
			if (index < 0 || index > parent.getChildCount()) {
				parent.addChild(node);
				treeModel.nodesWereInserted(parent, new int[] { parent.getIndex(node) });
			}
			else {
				parent.addChild(index, node);
				treeModel.nodesWereInserted(parent, new int[] { index });
			}
			node.setParent(parent);
			updateParentRuleElementForAdd(node);
		}
	}

	/**
	 * 
	 * @param node
	 * @return the parent of node
	 */
	private AbstractRuleTreeNode deleteNode_internal(AbstractRuleTreeNode node) {
		updateParentRuleElementForDelete(node);
		// remove tree node
		AbstractRuleTreeNode parent = (AbstractRuleTreeNode) node.getParent();
		int index = parent.getIndex(node);
		parent.removeChild(node);

		treeModel.nodesWereRemoved(parent, new int[] { index }, new Object[] { node });

		// TT 1031 -- remove AND node if it's the only child and it contains only a single element
		// make sure parent.getParent() is not IF node
		if ((parent.getParent() instanceof AbstractRuleTreeNode) && isNonEmptyNotNode((AbstractRuleTreeNode) parent.getParent())) {
			LogicalOpTreeNode notNode = (LogicalOpTreeNode) parent.getParent();

			// if the parent already contains a single-child AND node, remove the AND node
			if (notNode.getChildCount() == 1 && parent instanceof LogicalOpTreeNode && ((LogicalOpTreeNode) parent).getCompoundLHSElementType() == CompoundLHSElement.TYPE_AND
					&& ((LogicalOpTreeNode) parent).getChildCount() == 1) {
				AbstractRuleTreeNode firstChild = (AbstractRuleTreeNode) parent.getChildAt(0);
				deleteNode_internal(firstChild);
				deleteNode_internal(parent);
				addToNode_internal(notNode, 0, firstChild);
			}
		}
		return parent;
	}

	private synchronized void deleteNode(AbstractRuleTreeNode node) {
		deleteNode(node, true);
	}

	private synchronized void deleteNode(AbstractRuleTreeNode node, boolean select) {
		tree.removeTreeSelectionListener(treeSelectionListener);
		try {
			if (select)
				selectNode(deleteNode_internal(node));
			else
				deleteNode_internal(node);
		}
		finally {
			tree.addTreeSelectionListener(treeSelectionListener);
		}
	}

	private void replaceNode(AbstractRuleTreeNode node) {
		replaceNode(node, node);
	}

	private void replaceNode(AbstractRuleTreeNode oldNode, AbstractRuleTreeNode newNode) {
		tree.removeTreeSelectionListener(treeSelectionListener);
		try {
			AbstractRuleTreeNode parent = (AbstractRuleTreeNode) oldNode.getParent();

			deleteNode_internal(oldNode);
			addToNode_internal(parent, -1, newNode);

			if (newNode.getChildCount() > 0) {
				expandNodeAll(newNode);
			}
			selectNode(newNode);
		}
		finally {
			tree.addTreeSelectionListener(treeSelectionListener);
		}
	}

	private void refreshNode(AbstractRuleTreeNode node) { // called from Edit action
		tree.removeTreeSelectionListener(treeSelectionListener);
		try {
			treeModel.nodeChanged(node);

			if (node.getChildCount() > 0) {
				expandNodeAll(node);
			}
			selectNode(node);
		}
		finally {
			tree.addTreeSelectionListener(treeSelectionListener);
		}
	}

	private void selectNode(AbstractRuleTreeNode node) {
		TreePath path = getTreePath(node);
		tree.scrollPathToVisible(path);
		tree.setSelectionPath(path);
		setCurrentNode(node);
	}

	private synchronized void setCurrentNode(AbstractRuleTreeNode node) {
		currentNode = node;
		refreshButtons();
	}

	private void expandNodeAll(AbstractRuleTreeNode node) {
		TreePath path = getTreePath(node);
		if (path != null) {
			for (int i = 0; i < node.getChildCount(); i++) {
				AbstractRuleTreeNode child = (AbstractRuleTreeNode) node.getChildAt(i);
				expandNodeAll(child);
			}
			tree.expandPath(path);
		}
	}

	private void refreshButtons() {
		if (columnRefValueEditOnly) {
			if (currentNode instanceof ConditionTreeNode) {
				Value value = ((ConditionTreeNode) currentNode).getCondition().getValue();
				boolean hasColRef = (value instanceof ColumnReference || value instanceof MathExpressionValue);
				editAction.setEnabled(enabled && hasColRef);
			}
			else if (currentNode instanceof ActionParamTreeNode) {
				editAction.setEnabled(enabled && (((ActionParamTreeNode) currentNode).getFunctionParameter() instanceof ColumnReference));
			}
			else {
				editAction.setEnabled(false);
			}
		}
		else {
			if (currentNode instanceof IfTreeNode) {
				andAction.setEnabled(enabled && false);
				orAction.setEnabled(enabled && true);
				notAction.setEnabled(enabled && false);
				testAction.setEnabled(enabled && true);
				editAction.setEnabled(enabled && false);
				copyAction.setEnabled(enabled && true);
				cutAction.setEnabled(enabled && true);
				pasteAction.setEnabled(enabled && true);
				deleteAction.setEnabled(enabled && true);
				actionAction.setEnabled(enabled && false);
				condAction.setEnabled(enabled && true);
				existAction.setEnabled(enabled & true);
				assignAction.setEnabled(enabled && false);
				setEnabledDirectionButtons(enabled && false);
			}
			else if (currentNode instanceof ExistTreeNode) {
				andAction.setEnabled(enabled && false);
				orAction.setEnabled(enabled && true);
				notAction.setEnabled(enabled && true);
				testAction.setEnabled(enabled && false);
				editAction.setEnabled(enabled && true);
				copyAction.setEnabled(true);
				cutAction.setEnabled(enabled && true);
				pasteAction.setEnabled(enabled && true);
				deleteAction.setEnabled(enabled && true);
				actionAction.setEnabled(enabled && false);
				condAction.setEnabled(enabled && true);
				existAction.setEnabled(enabled & true);
				assignAction.setEnabled(enabled && false);
				setEnabledDirectionButtons(enabled && true);
			}
			else if (currentNode instanceof LogicalOpAttachable) {
				andAction.setEnabled(enabled && ((LogicalOpTreeNode) currentNode).getCompoundLHSElementType() == CompoundLHSElement.TYPE_OR);
				orAction.setEnabled(enabled && true);
				notAction.setEnabled(enabled && true);
				testAction.setEnabled(enabled && false);
				editAction.setEnabled(enabled && false);
				copyAction.setEnabled(true);
				cutAction.setEnabled(enabled && true && ((LogicalOpTreeNode) currentNode).getCompoundLHSElementType() != CompoundLHSElement.TYPE_NOT);
				pasteAction.setEnabled(enabled && true);
				deleteAction.setEnabled(enabled && true);
				actionAction.setEnabled(enabled && false);
				condAction.setEnabled(enabled && true);
				existAction.setEnabled(enabled & true);
				assignAction.setEnabled(enabled && false);
				setEnabledDirectionButtons(enabled && true);
			}
			else if (currentNode instanceof ConditionTreeNode) {
				AbstractRuleTreeNode parentNode = (AbstractRuleTreeNode) currentNode.getParent();
				boolean parentNot = parentNode instanceof LogicalOpTreeNode && ((LogicalOpTreeNode) parentNode).getCompoundLHSElementType() == CompoundLHSElement.TYPE_NOT;
				existAction.setEnabled(enabled & false);
				andAction.setEnabled(enabled && false);
				orAction.setEnabled(enabled && false);
				notAction.setEnabled(enabled && true);
				testAction.setEnabled(enabled && false);
				editAction.setEnabled(enabled && true);
				copyAction.setEnabled(true);
				cutAction.setEnabled(enabled && true && !parentNot);
				pasteAction.setEnabled(enabled && true);
				deleteAction.setEnabled(enabled && true && !parentNot);
				actionAction.setEnabled(enabled && false);
				condAction.setEnabled(enabled && false);
				assignAction.setEnabled(enabled && false);
				setEnabledDirectionButtons(enabled && true);
			}
			else if (currentNode instanceof ActionTreeNode) {
				setEnabledAll(enabled && false);
				copyAction.setEnabled(enabled && true);
				pasteAction.setEnabled(enabled && true);
				cutAction.setEnabled(enabled && true);
				editAction.setEnabled(enabled && true);
				deleteAction.setEnabled(enabled && true);
			}
			else if (currentNode instanceof TestTreeNode) {
				setEnabledAll(enabled && false);
				copyAction.setEnabled(enabled && true);
				pasteAction.setEnabled(enabled && true);
				cutAction.setEnabled(enabled && true);
				editAction.setEnabled(enabled && true);
				deleteAction.setEnabled(enabled && true);
			}
			else if (currentNode instanceof ActionParamTreeNode) {
				andAction.setEnabled(enabled && false);
				orAction.setEnabled(enabled && false);
				notAction.setEnabled(enabled && false);
				testAction.setEnabled(enabled && false);
				editAction.setEnabled(enabled && true);
				copyAction.setEnabled(enabled && true);
				cutAction.setEnabled(enabled && false);
				pasteAction.setEnabled(enabled && true);
				deleteAction.setEnabled(enabled && false);
				actionAction.setEnabled(enabled && false);
				condAction.setEnabled(enabled && false);
				existAction.setEnabled(enabled & false);
				assignAction.setEnabled(enabled && true);
				setEnabledDirectionButtons(enabled && false);
			}
			else if (currentNode instanceof ThenTreeNode) {
				setEnabledAll(enabled && false);
				copyAction.setEnabled(enabled && true);
				pasteAction.setEnabled(enabled && true);
				cutAction.setEnabled(enabled && true);
				deleteAction.setEnabled(enabled && true);
				actionAction.setEnabled(enabled && !((ThenTreeNode) currentNode).hasActionNode());
			}
		}
	}

	private final boolean canMoveUp() {
		if (columnRefValueEditOnly) return false;
		return currentNode.getParent().getIndex(currentNode) > 0;
	}

	private final boolean canMoveDown() {
		if (columnRefValueEditOnly) return false;
		return currentNode.getParent().getIndex(currentNode) < (currentNode.getParent().getChildCount() - 1);
	}

	private final boolean canIndent() {
		if (columnRefValueEditOnly) return false;
		int index = currentNode.getParent().getIndex(currentNode);
		return index > 0 && (currentNode.getParent().getChildAt(index - 1) instanceof LogicalOpAttachable);
	}

	private final boolean canOutdent() {
		if (columnRefValueEditOnly) return false;
		return !(currentNode.getParent() instanceof IfTreeNode);
	}

	private void setEnabledDirectionButtons(boolean enabled) {
		upAction.setEnabled(enabled && canMoveUp());
		downAction.setEnabled(enabled && canMoveDown());
		rightAction.setEnabled(enabled && canIndent());
		leftAction.setEnabled(enabled && canOutdent());
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.enabled = enabled;
		if (!enabled) {
			setEnabledAll(false);
		}
		else {
			refreshButtons();
		}
		//propPanel.setEnabled(enabled);
	}

	private void setEnabledAll(boolean enabled) {
		andAction.setEnabled(enabled);
		orAction.setEnabled(enabled);
		notAction.setEnabled(enabled);
		testAction.setEnabled(enabled);
		editAction.setEnabled(enabled);
		copyAction.setEnabled(enabled);
		cutAction.setEnabled(enabled);
		pasteAction.setEnabled(enabled);
		deleteAction.setEnabled(enabled);
		actionAction.setEnabled(enabled);
		condAction.setEnabled(enabled);
		assignAction.setEnabled(enabled);
		existAction.setEnabled(enabled);
		setEnabledDirectionButtons(enabled);
	}

	private void initPanel() {
		JToolBar toolbar = new JToolBar("Rule Actions");
		toolbar.setFloatable(true);

		if (!columnRefValueEditOnly) {
			UIFactory.addToToolbar(toolbar, condAction, "Add New Condition");
			UIFactory.addToToolbar(toolbar, actionAction, "Add New Action");
			toolbar.addSeparator();
			UIFactory.addToToolbar(toolbar, existAction, "Add New Exist Expression");
			UIFactory.addToToolbar(toolbar, andAction, "Add New AND");
			UIFactory.addToToolbar(toolbar, orAction, "Add New OR");
			UIFactory.addToToolbar(toolbar, notAction, "Add New NOT");
			UIFactory.addToToolbar(toolbar, testAction, "Add New Test Condition");
			toolbar.addSeparator();
		}
		UIFactory.addToToolbar(toolbar, editAction, "Edit Selected Element");
		if (!columnRefValueEditOnly) {
			UIFactory.addToToolbar(toolbar, deleteAction, "Delete Selected Element");
			toolbar.addSeparator();
			UIFactory.addToToolbar(toolbar, copyAction, "Copy Selected Element");
			UIFactory.addToToolbar(toolbar, cutAction, "Cut Selected Element");
			UIFactory.addToToolbar(toolbar, pasteAction, "Paste Selected Element");
			toolbar.addSeparator();
			UIFactory.addToToolbar(toolbar, upAction, "Move Selected Element Up");
			UIFactory.addToToolbar(toolbar, downAction, "Move Selected Element Down");
			UIFactory.addToToolbar(toolbar, leftAction, "Move Selected Element Left");
			UIFactory.addToToolbar(toolbar, rightAction, "Move Selected Element Right");
		}

		add(toolbar, BorderLayout.NORTH);
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	private void initTree() {
		if (!treeInitialized) {
			refreshTree();
			treeInitialized = true;

			treeSelectionListener = new TreeSelectionL();
			tree.addTreeSelectionListener(treeSelectionListener);

		}
	}

	private synchronized void copyCurrentNode() {
		String str = null;
		if (currentNode instanceof IfTreeNode) {
			CompoundLHSElement rootConditions = RuleElementFactory.getInstance().createAndCompoundCondition();
			updateRuleFromTree_aux(rootConditions, currentNode);
			str = RuleElementFactory.asCopyString(rootConditions);
		}
		else if (currentNode instanceof ConditionTreeNode) {
			str = RuleElementFactory.asCopyString(((ConditionTreeNode) currentNode).getCondition());
		}
		else if (currentNode instanceof LogicalOpTreeNode) {
			// somehow get a compound LHS element for the node
			str = RuleElementFactory.asCopyString((CompoundLHSElement) ((LogicalOpTreeNode) currentNode).getRuleElement());
		}
		else if (currentNode instanceof ThenTreeNode) {
			str = RuleElementFactory.asCopyString(((ActionTreeNode) currentNode.getChildAt(0)).getRuleAction());
		}
		else if (currentNode instanceof ActionTreeNode) {
			str = RuleElementFactory.asCopyString(((ActionTreeNode) currentNode).getRuleAction());
		}
		else if (currentNode instanceof TestTreeNode) {
			str = RuleElementFactory.asCopyString(((TestTreeNode) currentNode).getTestCondition());
		}
		else if (currentNode instanceof ActionParamTreeNode) {
			str = RuleElementFactory.asCopyString(((ActionParamTreeNode) currentNode).getFunctionParameter());
		}

		if (str != null) {
			ClientUtil.placeOnClipboard(str);
		}
	}

	private synchronized void cutCurrentNode() {
		String str = null;
		if (currentNode instanceof IfTreeNode) {
			CompoundLHSElement rootConditions = RuleElementFactory.getInstance().createAndCompoundCondition();
			updateRuleFromTree_aux(rootConditions, currentNode);
			str = RuleElementFactory.asCopyString(rootConditions);
		}
		else if (currentNode instanceof ConditionTreeNode) {
			str = RuleElementFactory.asCopyString(((ConditionTreeNode) currentNode).getCondition());
		}
		else if (currentNode instanceof LogicalOpTreeNode) {
			// somehow get a compound LHS element for the node
			str = RuleElementFactory.asCopyString((CompoundLHSElement) ((LogicalOpTreeNode) currentNode).getRuleElement());
		}
		else if (currentNode instanceof ThenTreeNode && currentNode.getChildCount() > 0) {
			str = RuleElementFactory.asCopyString(((ActionTreeNode) currentNode.getChildAt(0)).getRuleAction());
		}
		else if (currentNode instanceof ActionTreeNode) {
			str = RuleElementFactory.asCopyString(((ActionTreeNode) currentNode).getRuleAction());
		}
		else if (currentNode instanceof TestTreeNode) {
			str = RuleElementFactory.asCopyString(((TestTreeNode) currentNode).getTestCondition());
		}
		if (str != null) {
			ClientUtil.placeOnClipboard(str);
			if (currentNode instanceof IfTreeNode) {
				IfTreeNode ifNode = (IfTreeNode) currentNode;
				int count = ifNode.getChildCount();
				for (int i = 0; i < count; i++) {
					deleteNode((AbstractRuleTreeNode) ifNode.getChildAt(0), false);
				}
			}
			else if (currentNode instanceof ThenTreeNode && currentNode.getChildCount() > 0)
				deleteNode((AbstractRuleTreeNode) currentNode.getChildAt(0), false);
			else
				deleteNode(currentNode);
		}
	}

	private synchronized void pasteIntoCurrentNode(String str) {
		RuleElement currentElement = currentNode.getRuleElement();
		if (currentNode instanceof LogicalOpAttachable) {
			LHSElement element = RuleElementFactory.toLHSElement(str, DomainModel.getInstance(), ClientUtil.getInstance());
			if (element == null) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.clipboard.content", new Object[] { "LHS element" });
			}
			else {
				// TT 1211 -- allow pasting into IF and THEN node
				if (currentNode instanceof IfTreeNode) {
					// skip validation because IfNode doesn't represent a rule element
				}
				else {
					CompoundRuleElement<?> compoundElement = (currentElement instanceof CompoundRuleElement ? (CompoundRuleElement<?>) currentElement : (currentElement instanceof ExistExpression
							? ((ExistExpression) currentElement).getCompoundLHSElement()
							: null));
					if (compoundElement == null) {
						ClientUtil.getInstance().showWarning("msg.warning.failure.paste.generic", new Object[] { "the selected node cannot contain another rule element." });
						return;
					}
					String validationError = DataTypeCompatibilityValidator.isValid((RuleElement) element, compoundElement, template, DomainModel.getInstance(), false);
					if (validationError != null) {
						ClientUtil.getInstance().showWarning("msg.warning.paste.validation", new Object[] { validationError });
						return;
					}
				}
				AbstractRuleTreeNode nodeToPaste = null;
				if (element instanceof Condition) {
					nodeToPaste = new ConditionTreeNode(currentNode, (Condition) element);
				}
				else if (element instanceof CompoundLHSElement) {
					nodeToPaste = new LogicalOpTreeNode(currentNode, (CompoundLHSElement) element);
				}
				else if (element instanceof TestCondition) {
					nodeToPaste = new TestTreeNode(currentNode, (TestCondition) element);
				}

				if (nodeToPaste != null) {
					if (element instanceof CompoundLHSElement && currentNode instanceof IfTreeNode && ((CompoundLHSElement) element).getType() == CompoundLHSElement.TYPE_AND) {
						CompoundLHSElement root = (CompoundLHSElement) element;
						IfTreeNode ifNode = (IfTreeNode) currentNode;
						int count = ifNode.getChildCount();
						for (int i = 0; i < count; i++) {
							deleteNode((AbstractRuleTreeNode) ifNode.getChildAt(0), false);
						}
						for (int i = 0; i < root.size(); i++) {
							LHSElement sub = (LHSElement) root.get(i);
							if (sub instanceof Condition) {
								addToCurrentNode(-1, new ConditionTreeNode(ifNode, (Condition) sub), false, true);
							}
							else if (sub instanceof ExistExpression) {
								addToCurrentNode(-1, new ExistTreeNode(ifNode, (ExistExpression) sub), false, true);
							}
							else if (sub instanceof CompoundLHSElement) {
								addToCurrentNode(-1, new LogicalOpTreeNode(ifNode, (CompoundLHSElement) sub), false, true);
							}
							else if (sub instanceof TestCondition) {
								addToCurrentNode(-1, new TestTreeNode(ifNode, (TestCondition) sub), false, true);
							}
						}
					}
					else if (element instanceof TestCondition && currentNode instanceof IfTreeNode) {
						addToCurrentNode(-1, nodeToPaste, true, true);
					}
					else
						addToCurrentNode(-1, nodeToPaste, true, true);
				}
			}
		}
		else if (currentNode instanceof ConditionTreeNode) {
			LHSElement element = RuleElementFactory.toLHSElement(str, DomainModel.getInstance(), ClientUtil.getInstance());
			if (element == null) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.clipboard.content", new Object[] { "LHS element" });
			}
			else {
				String validationError = DataTypeCompatibilityValidator.isValid(
						(RuleElement) element,
						(CompoundRuleElement<?>) ((AbstractRuleTreeNode) currentNode.getParent()).getRuleElement(),
						template,
						DomainModel.getInstance(),
						false);
				if (validationError != null) {
					ClientUtil.getInstance().showWarning("msg.warning.paste.validation", new Object[] { validationError });
					return;
				}

				AbstractRuleTreeNode nodeToPaste = null;
				if (element instanceof Condition) {
					nodeToPaste = new ConditionTreeNode(currentNode, (Condition) element);
				}
				else if (element instanceof CompoundLHSElement) {
					nodeToPaste = new LogicalOpTreeNode(currentNode, (CompoundLHSElement) element);
				}

				if (nodeToPaste != null) {
					int index = currentNode.getParent().getIndex(currentNode) + 1;
					addToNode((AbstractRuleTreeNode) currentNode.getParent(), index, nodeToPaste, true, true);
				}
			}
		}
		else if (currentNode instanceof ThenTreeNode) {
			RuleAction action = RuleElementFactory.toRuleAction(str, ClientUtil.getInstance());
			String validationError = DataTypeCompatibilityValidator.isValid((RuleElement) action, (CompoundRuleElement<?>) currentElement, template, DomainModel.getInstance(), false);
			if (validationError != null) {
				ClientUtil.getInstance().showWarning("msg.warning.paste.validation", new Object[] { validationError });
				return;
			}

			if (action == null) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.clipboard.content", new Object[] { "rule action" });
			}
			else {
				ActionTreeNode nodeToPaste = new ActionTreeNode(currentNode, action);
				if (currentNode.getChildCount() > 0) {
					replaceNode((AbstractRuleTreeNode) currentNode.getChildAt(0), nodeToPaste);
				}
				else {
					addToCurrentNode(-1, nodeToPaste, true, true);
				}
			}
		}
		else if (currentNode instanceof ActionTreeNode) {
			FunctionParameter param = RuleElementFactory.toFunctionParameter(str);
			if (param == null) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.clipboard.content", new Object[] { "action parameter" });
			}
			else {
				String validationError = DataTypeCompatibilityValidator.isValid((RuleElement) param, (CompoundRuleElement<?>) currentElement, template, DomainModel.getInstance(), false);
				if (validationError != null) {
					ClientUtil.getInstance().showWarning("msg.warning.paste.validation", new Object[] { validationError });
					return;
				}

				RuleAction action = ((ActionTreeNode) currentNode).getRuleAction();
				if (action.getActionType() == null) {
					ClientUtil.getInstance().showWarning("msg.warning.failure.paste.generic", new Object[] { "action type is not yet specified." });
				}
				else if (action.getActionType().parameterSize() < param.index()) {
					ClientUtil.getInstance().showWarning("msg.warning.failure.paste.generic", new Object[] { "selected action does not have such parameter." });
				}
				else {
					ActionParamTreeNode paramNode = (ActionParamTreeNode) ((ActionTreeNode) currentNode).getChildAt(param.index() - 1);
					paramNode.setFunctionParameter(param);
					refreshNode(paramNode);
				}
			}
		}
		else if (currentNode instanceof TestTreeNode) {
			FunctionParameter param = RuleElementFactory.toFunctionParameter(str);
			if (param == null) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.clipboard.content", new Object[] { "test condition parameter" });
			}
			else {
				String validationError = DataTypeCompatibilityValidator.isValid((RuleElement) param, (CompoundRuleElement<?>) currentElement, template, DomainModel.getInstance(), false);
				if (validationError != null) {
					ClientUtil.getInstance().showWarning("msg.warning.paste.validation", new Object[] { validationError });
					return;
				}
				TestCondition test = ((TestTreeNode) currentNode).getTestCondition();
				if (test.getTestType().parameterSize() < param.index()) {
					ClientUtil.getInstance().showWarning("msg.warning.failure.paste.generic", new Object[] { "selected test condition does not have such parameter." });
				}
				else {
					ActionParamTreeNode paramNode = (ActionParamTreeNode) ((TestTreeNode) currentNode).getChildAt(param.index() - 1);
					paramNode.setFunctionParameter(param);
					refreshNode(paramNode);
				}
			}
		}
	}

	public synchronized void setRule(GridTemplate template, RuleDefinition rule) {
		treeModel.removeTreeModelListener(treeModelListener);
		try {
			this.usageType = template.getUsageType();
			this.template = template;
			this.rule = rule;
			refreshTree();
			if (colRefTreeRenderer != null) {
				colRefTreeRenderer.setTemplate(template);
			}
		}
		finally {
			treeModel.addTreeModelListener(treeModelListener);
		}
	}

	public synchronized void setCellValues(List<Object> cellValues) {
		treeModel.removeTreeModelListener(treeModelListener);
		try {
			if (colRefTreeRenderer != null) {
				colRefTreeRenderer.setCellValues(cellValues);
			}
			refreshTree();
		}
		finally {
			treeModel.addTreeModelListener(treeModelListener);
		}
	}

	public synchronized RuleDefinition getRule() {
		return this.rule;
	}

	public synchronized void clearFields() {
		this.usageType = null;
		this.rule = null;
		refreshTree();
	}

	public synchronized void clearExceptRule() {
		this.usageType = null;
		refreshTree();
	}

	public synchronized void updateRuleFromFields() {
		if (rule == null) {
			rule = new RuleDefinition(0, "", "");
		}
		updateRuleFromTree();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateParentRuleElementForAdd(AbstractRuleTreeNode child) {
		AbstractRuleTreeNode parent = (AbstractRuleTreeNode) child.getParent();
		int index = parent.getIndex(child);
		CompoundRuleElement parentElement = null;
		RuleElement e = parent.getRuleElement();
		if (e != null && e instanceof ExistExpression)
			parentElement = ((ExistExpression) e).getCompoundLHSElement();
		else
			parentElement = (CompoundRuleElement) e;
		RuleElement childElement = child.getRuleElement();
		if (parentElement == null) {
			return;
		}
		if (parentElement.size() == parent.getChildCount()) {
			parentElement.replace(parentElement.get(index), childElement);
		}
		else if (parentElement.size() + 1 == parent.getChildCount()) {
			parentElement.insert(index, childElement);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateParentRuleElementForDelete(AbstractRuleTreeNode child) {
		AbstractRuleTreeNode parent = (AbstractRuleTreeNode) child.getParent();
		CompoundRuleElement parentElement = null;
		RuleElement e = parent.getRuleElement();
		if (e != null && e instanceof ExistExpression)
			parentElement = ((ExistExpression) e).getCompoundLHSElement();
		else
			parentElement = (CompoundRuleElement) e;
		RuleElement childElement = child.getRuleElement();
		if (parentElement == null) {
			return;
		}
		parentElement.remove(childElement);
	}


	private void updateRuleFromTree() {
		// update LHS
		IfTreeNode ifNode = (IfTreeNode) rootNode.getChildAt(0);

		CompoundLHSElement rootConditions = RuleElementFactory.getInstance().createAndCompoundCondition();
		updateRuleFromTree_aux(rootConditions, ifNode);

		rule.clearLHS();
		rule.updateRootConditions(rootConditions);

		// update actions
		rule.clearAction();
		ThenTreeNode thenNode = (ThenTreeNode) rootNode.getChildAt(1);
		if (thenNode.hasActionNode()) {
			RuleAction action = thenNode.getActionNode().getRuleAction();
			rule.updateAction(action);
		}
	}

	private void updateRuleFromTree_aux(CompoundLHSElement compoundElement, AbstractRuleTreeNode node) {
		if (node instanceof ConditionTreeNode) {
			updateRuleFromTree_aux(compoundElement, (ConditionTreeNode) node);
		}
		else {
			if (node instanceof ExistTreeNode) {
				ExistExpression existExpression = RuleElementFactory.getInstance().createExistExpression(((ExistTreeNode) node).getExistClassName());
				existExpression.setObjectName(((ExistTreeNode) node).getExistExpression().getObjectName());
				existExpression.setExcludedObjectName(((ExistTreeNode) node).getExistExpression().getExcludedObjectName());
				for (int i = 0; i < node.getChildCount(); i++) {
					updateRuleFromTree_aux(existExpression.getCompoundLHSElement(), (AbstractRuleTreeNode) node.getChildAt(i));
				}
				compoundElement.add(existExpression);
			}
			else if (node instanceof LogicalOpTreeNode) {
				CompoundLHSElement conditions = null;
				switch (((LogicalOpTreeNode) node).getCompoundLHSElementType()) {
				case CompoundLHSElement.TYPE_AND:
					conditions = RuleElementFactory.getInstance().createAndCompoundCondition();
					break;
				case CompoundLHSElement.TYPE_OR:
					conditions = RuleElementFactory.getInstance().createOrCompoundCondition();
					break;
				case CompoundLHSElement.TYPE_NOT:
					conditions = RuleElementFactory.getInstance().createNotCompoundCondition();
					break;
				}

				for (int i = 0; i < node.getChildCount(); i++) {
					updateRuleFromTree_aux(conditions, (AbstractRuleTreeNode) node.getChildAt(i));
				}
				compoundElement.add(conditions);
			}
			else if (node instanceof IfTreeNode) {
				for (int i = 0; i < node.getChildCount(); i++) {
					updateRuleFromTree_aux(compoundElement, (AbstractRuleTreeNode) node.getChildAt(i));
				}
			}
			else if (node instanceof TestTreeNode) {
				updateRuleFromTree_aux(compoundElement, (TestTreeNode) node);
			}
		}

	}


	private void updateRuleFromTree_aux(CompoundLHSElement compoundElement, TestTreeNode node) {
		compoundElement.add(RuleElementFactory.deepCopyTestCondition(node.getTestCondition()));
	}

	private void updateRuleFromTree_aux(CompoundLHSElement compoundElement, ConditionTreeNode node) {
		compoundElement.add(RuleElementFactory.deepCopyCondition(((ConditionTreeNode) node).getCondition()));
	}

	private void refreshTree() {
		rootNode.removeAllChildren();

		tree.removeTreeSelectionListener(treeSelectionListener);

		IfTreeNode ifNode = new IfTreeNode(rootNode);
		ThenTreeNode thenNode = new ThenTreeNode(rootNode);
		rootNode.addChild(ifNode, true);
		rootNode.addChild(thenNode, true);

		if (rule != null) {
			for (int i = 0; i < rule.sizeOfRootElements(); i++) {
				LHSElement element = rule.getRootElementAt(i);
				if (element instanceof Condition) {
					ifNode.addChild(new ConditionTreeNode(ifNode, RuleElementFactory.deepCopyCondition((Condition) element)));
				}
				else if (element instanceof ExistExpression) {
					ifNode.addChild(new ExistTreeNode(ifNode, RuleElementFactory.deepCopyExistExpression((ExistExpression) element)));
				}
				else if (element instanceof TestCondition) {
					ifNode.addChild(new TestTreeNode(ifNode, RuleElementFactory.deepCopyTestCondition((TestCondition) element)));
				}
				else if (element instanceof CompoundLHSElement) {
					ifNode.addChild(new LogicalOpTreeNode(ifNode, RuleElementFactory.deepCopyCompoundLHSElement((CompoundLHSElement) element)));
				}
			}
			if (rule.hasAction()) {
				thenNode.addChild(new ActionTreeNode(thenNode, RuleElementFactory.deepCopyRuleAction(rule.getRuleAction())));
			}
		}

		treeModel.reload();

		expandNodeAll(ifNode);
		expandNodeAll(thenNode);
		tree.addTreeSelectionListener(treeSelectionListener);
	}

	private TreePath getTreePath(AbstractRuleTreeNode node) {
		LinkedList<TreeNode> parentList = new LinkedList<TreeNode>();
		parentList.add(node);
		for (TreeNode parent = node.getParent(); parent != null;) {
			parentList.add(0, parent);
			parent = parent.getParent();
		}
		return new TreePath(parentList.toArray(new TreeNode[0]));
	}

}