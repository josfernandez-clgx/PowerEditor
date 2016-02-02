/*
 * Created on 2004. 6. 29.
 */
package com.mindbox.pe.client.applet.admin.phase;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.tree.NonSortingRootTreeNode;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.filter.AllSearchFilter;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseFactory;
import com.mindbox.pe.model.process.PhaseReference;
import com.mindbox.pe.model.process.ProcessRequest;

/**
 * 
 *
 * @author kim
 * @since PowerEditor  
 */
class PhaseTreePanel extends JPanel {

	private class LeftL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent arg0) {
			if (currentNode.getParent() != null && currentNode.getParent().getParent() == rootNode
					&& currentNode.getPhase().hasPrerequisites()) {
				ClientUtil.getInstance().showWarning(
						"msg.warning.process.invalid.prereq",
						new Object[] { currentNode.getPhase().getDisplayName()});
			}
			else {
				outdentCurrentNode();
			}
		}
	}

	private class RightL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent arg0) {
			indentCurrentNode();
		}
	}

	private class UpL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent arg0) {
			moveCurrentNodeUp();
		}
	}

	private class DownL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent arg0) {
			moveCurrentNodeDown();
		}
	}

	private class SubRefL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent arg0) {
			try {
				Phase phaseToReference = (Phase) JOptionPane.showInputDialog(
						ClientUtil.getApplet(),
						"Select Phase to reference as a sub-phase",
						"Select Sub-Phase",
						JOptionPane.PLAIN_MESSAGE,
						null,
						getReferencablePhases(),
						null);

				if (phaseToReference != null && currentNode != null) {
					Phase phaseToCreate = PhaseFactory.createPhase(PhaseFactory.TYPE_REFERENCE, -1, "reference", "reference");
					phaseToCreate.setParent(currentNode.getPhase());
					((PhaseReference) phaseToCreate).setReferecePhase(phaseToReference);

					int newID = ClientUtil.getCommunicator().save(phaseToCreate, false);
					phaseToCreate.setID(newID);

					phaseToCreate.getParent().addSubPhase(phaseToCreate);
					ClientUtil.getCommunicator().save(phaseToCreate.getParent(), false);

					// add to selection table
					addToCurrentNode(-1, new PhaseNode(currentNode, phaseToCreate), true, true);
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}

		}
	}

	private class NewL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent arg0) {
			Phase phase = PhaseEditDialog.newPhase(getSelectedPhase());
			if (phase != null) {
				try {
					int newID = ClientUtil.getCommunicator().save(phase, false);
					phase.setID(newID);

					if (phase.getParent() != null) {
						phase.getParent().addSubPhase(phase);
						ClientUtil.getCommunicator().save(phase.getParent(), false);
					}

					// add to selection table
					if (currentNode == null) {
						addToNode(rootNode, -1, new PhaseNode(rootNode, phase), true, true);
					}
					else {
						currentNode.getPhase().addSubPhase(phase);
						addToCurrentNode(-1, new PhaseNode(currentNode, phase), true, true);
					}
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private class DeleteL extends AbstractThreadedActionAdapter {

		private boolean canDelete(Phase phase) {
			try {
				List<ProcessRequest> requestList = ClientUtil.getCommunicator().search(new AllSearchFilter<ProcessRequest>(EntityType.PROCESS_REQUEST));
				for (Iterator<ProcessRequest> iter = requestList.iterator(); iter.hasNext();) {
					ProcessRequest element = iter.next();
					if (element.getPhase().getID() == phase.getID()) { return ClientUtil.getInstance().showConfirmation(
							"msg.question.delete.phase.used.request",
							new Object[] { phase.getDisplayName(), element.getDisplayName()}); }
				}
				return ClientUtil.getInstance().showConfirmation("msg.question.delete.phase", new Object[] { phase.getDisplayName()});
			}
			catch (ServerException e) {
				ClientUtil.handleRuntimeException(e);
				return false;
			}

		}

		public void performAction(ActionEvent arg0) {
			Phase phase = getSelectedPhase();
			if (phase != null && canDelete(phase)) {
				// delete phase
				try {
					ClientUtil.getCommunicator().delete(phase.getID(), EntityType.PROCESS_PHASE);

					if (phase.getParent() != null) {
						phase.getParent().removeSubPhase(phase);
						ClientUtil.getCommunicator().save(phase.getParent(), false);
					}
					deleteNode(currentNode);
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private class EditL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent arg0) {
			Phase phase = getSelectedPhase();
			if (phase != null) {
				if (phase instanceof PhaseReference) {
					Phase phaseToReference = (Phase) JOptionPane.showInputDialog(
							ClientUtil.getApplet(),
							"Select Phase to reference as a sub-phase",
							"Select Sub-Phase",
							JOptionPane.PLAIN_MESSAGE,
							null,
							getReferencablePhases(),
							((PhaseReference) phase).getReferecePhase());
					if (phaseToReference != null) {
						((PhaseReference) phase).setReferecePhase(phaseToReference);
						try {
							ClientUtil.getCommunicator().save(phase, false);

							// update node
							refreshNode(currentNode);
						}
						catch (Exception ex) {
							ClientUtil.handleRuntimeException(ex);
						}
					}
				}
				else {
					phase = PhaseEditDialog.editPhase(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), phase);
					if (phase != null) {
						try {
							ClientUtil.getCommunicator().save(phase, false);

							// update node
							refreshNode(currentNode);
						}
						catch (Exception ex) {
							ClientUtil.handleRuntimeException(ex);
						}
					}
				}
			}
		}
	}

	private class LoadL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent arg0) {
			try {
				refreshTree(ClientUtil.getCommunicator().search(new AllSearchFilter<Phase>(EntityType.PROCESS_PHASE)));
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private final class TreeSelectionL implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent e) {
			setCurrentNode((PhaseNode) tree.getLastSelectedPathComponent());
		}
	}

	//private final PhaseDetailPanel detailPanel;
	private final JButton newButton, editButton, loadButton, deleteButton, subRefButton;
	private final JButton leftButton, rightButton, upButton, downButton;
	private final DefaultTreeModel treeModel;
	private final PhaseTree tree;
	private final NonSortingRootTreeNode rootNode;
	private TreeSelectionL treeSelectionListener = null;
	private PhaseNode currentNode = null;

	/**
	 * 
	 */
	public PhaseTreePanel(boolean readOnly) {
		super();
		//this.detailPanel = detailPanel;
		this.editButton = UIFactory.createJButton("button.edit", "image.btn.small.edit", new EditL(), null);
		this.deleteButton = UIFactory.createJButton("button.delete", "image.btn.small.delete", new DeleteL(), null);
		this.loadButton = UIFactory.createJButton("button.load.phase", "image.btn.small.update", new LoadL(), null);
		this.newButton = UIFactory.createJButton("button.new", "image.btn.small.new", new NewL(), null);
		this.leftButton = UIFactory.createJButton(null, "image.btn.adhoc.outdent", new LeftL(), null);
		this.rightButton = UIFactory.createJButton(null, "image.btn.adhoc.indent", new RightL(), null);
		this.upButton = UIFactory.createJButton(null, "image.btn.adhoc.up", new UpL(), null);
		this.downButton = UIFactory.createJButton(null, "image.btn.adhoc.down", new DownL(), null);
		this.subRefButton = UIFactory.createJButton("button.phase.reference", "image.btn.small.bookmart", new SubRefL(), null);

		this.rootNode = new NonSortingRootTreeNode(null);
		this.treeModel = new DefaultTreeModel(rootNode);
		this.tree = new PhaseTree(treeModel, false);

		initPanel();

		treeSelectionListener = new TreeSelectionL();
		tree.addTreeSelectionListener(treeSelectionListener);
		
		if (readOnly) {
			
		}
	}

	private void initPanel() {
		setLayout(new BorderLayout(2, 2));
		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(loadButton);
		buttonPanel.add(newButton);
		buttonPanel.add(subRefButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(new JSeparator());
		buttonPanel.add(upButton);
		buttonPanel.add(downButton);
		buttonPanel.add(leftButton);
		buttonPanel.add(rightButton);
		add(buttonPanel, BorderLayout.NORTH);
		add(new JScrollPane(tree), BorderLayout.CENTER);

		editButton.setEnabled(false);
		subRefButton.setEnabled(false);
		deleteButton.setEnabled(false);
		leftButton.setEnabled(false);
		rightButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
	}

	private Phase getSelectedPhase() {
		return (currentNode == null ? null : currentNode.getPhase());
	}

	private void selectNode(NonSortingRootTreeNode node) {
		if (node != rootNode) {
			TreePath path = getTreePath(node);
			tree.scrollPathToVisible(path);
			tree.setSelectionPath(path);
			setCurrentNode(node);
		}
	}

	private TreePath getTreePath(NonSortingRootTreeNode node) {
		LinkedList<TreeNode> parentList = new LinkedList<TreeNode>();
		parentList.add(node);
		for (TreeNode parent = node.getParent(); parent != null;) {
			parentList.add(0, parent);
			parent = parent.getParent();
		}
		return new TreePath(parentList.toArray(new TreeNode[0]));
	}

	private void refreshNode(PhaseNode node) { // called from Edit action
		tree.removeTreeSelectionListener(treeSelectionListener);
		treeModel.nodeChanged(node);

		if (node.getChildCount() > 0) {
			expandNodeAll(node);
		}
		selectNode(node);
		tree.addTreeSelectionListener(treeSelectionListener);
	}

	private final boolean canIndent() {
		// has a previous sibling that can contain others
		//int index = treeModel.getIndexOfChild(currentNode.getParent(), currentNode);
		int index = currentNode.getParent().getIndex(currentNode);
		//return index > 0 && treeModel.getChild(currentNode.getParent(), index - 1) instanceof LogicalOpAttachable;
		return index > 0 && (currentNode.getParent().getChildAt(index - 1) instanceof PhaseNode);
	}

	private final boolean canOutdent() {
		return currentNode.getParent() instanceof PhaseNode;
	}

	private synchronized void indentCurrentNode() {
		PhaseNode nodeToMove = currentNode;
		NonSortingRootTreeNode parent = (NonSortingRootTreeNode) nodeToMove.getParent();

		int childIndex = parent.getIndex(nodeToMove);
		if (childIndex > 0 && (parent.getChildAt(childIndex - 1) instanceof PhaseNode)) {
			tree.removeTreeSelectionListener(treeSelectionListener);
			try {
				// save old parent's changes
				if (parent instanceof PhaseNode) {
					((PhaseNode) parent).getPhase().removeSubPhase(nodeToMove.getPhase());
					ClientUtil.getCommunicator().save(((PhaseNode) parent).getPhase(), false);
				}

				// save new parent node to move
				PhaseNode newParentNode = (PhaseNode) parent.getChildAt(childIndex - 1);
				newParentNode.getPhase().addSubPhase(nodeToMove.getPhase());
				ClientUtil.getCommunicator().save(newParentNode.getPhase(), false);

				// save node to move
				nodeToMove.getPhase().setParent(newParentNode.getPhase());
				ClientUtil.getCommunicator().save(nodeToMove.getPhase(), false);

				deleteNode_internal(nodeToMove);
				addToNode_internal(newParentNode/*(PhaseNode) parent.getChildAt(childIndex - 1)*/, -1, nodeToMove);

				selectNode(nodeToMove);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				tree.addTreeSelectionListener(treeSelectionListener);
			}
		}
	}

	private synchronized void outdentCurrentNode() {
		PhaseNode nodeToMove = currentNode;
		NonSortingRootTreeNode parent = (NonSortingRootTreeNode) nodeToMove.getParent();
		if (parent != rootNode) {
			tree.removeTreeSelectionListener(treeSelectionListener);
			try {
				int parentIndex = parent.getParent().getIndex(parent);

				// save old parent's changes
				if (parent instanceof PhaseNode) {
					((PhaseNode) parent).getPhase().removeSubPhase(nodeToMove.getPhase());
					ClientUtil.getCommunicator().save(((PhaseNode) parent).getPhase(), false);
				}

				// save new parent's changes
				NonSortingRootTreeNode newParentNode = (NonSortingRootTreeNode) parent.getParent();
				if (newParentNode instanceof PhaseNode) {
					((PhaseNode) newParentNode).getPhase().addSubPhase(nodeToMove.getPhase());
					ClientUtil.getCommunicator().save(((PhaseNode) newParentNode).getPhase(), false);
				}

				// save node to move
				nodeToMove.getPhase().setParent((newParentNode instanceof PhaseNode ? ((PhaseNode) newParentNode).getPhase() : null));
				ClientUtil.getCommunicator().save(nodeToMove.getPhase(), false);

				deleteNode_internal(nodeToMove);
				addToNode_internal(newParentNode/*(NonSortingRootTreeNode) parent.getParent()*/, parentIndex + 1, nodeToMove);

				selectNode(nodeToMove);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				tree.addTreeSelectionListener(treeSelectionListener);
			}
		}
	}

	private synchronized void moveCurrentNodeDown() {
		PhaseNode nodeToMove = currentNode;
		NonSortingRootTreeNode parent = (NonSortingRootTreeNode) nodeToMove.getParent();

		int childIndex = parent.getIndex(nodeToMove);
		if (childIndex >= 0 && childIndex < (parent.getChildCount() - 1)) {
			tree.removeTreeSelectionListener(treeSelectionListener);
			try {
				deleteNode_internal(nodeToMove);
				addToNode_internal(parent, childIndex + 1, nodeToMove);

				selectNode(nodeToMove);
			}
			finally {
				tree.addTreeSelectionListener(treeSelectionListener);
			}
		}
	}

	private synchronized void moveCurrentNodeUp() {
		PhaseNode nodeToMove = currentNode;
		NonSortingRootTreeNode parent = (NonSortingRootTreeNode) nodeToMove.getParent();

		int childIndex = parent.getIndex(nodeToMove);
		if (childIndex > 0) {
			tree.removeTreeSelectionListener(treeSelectionListener);
			try {
				deleteNode_internal(nodeToMove);
				addToNode_internal(parent, childIndex - 1, nodeToMove);

				selectNode(nodeToMove);
			}
			finally {
				tree.addTreeSelectionListener(treeSelectionListener);
			}
		}
	}

	private synchronized void addToCurrentNode(int index, PhaseNode node, boolean selectNode, boolean expand) {
		addToNode(currentNode, index, node, selectNode, expand);
	}

	private synchronized void addToNode(NonSortingRootTreeNode parent, int index, PhaseNode node, boolean selectNode, boolean expandNode) {
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

	private void addToNode_internal(NonSortingRootTreeNode parent, int index, PhaseNode node) {
		if (index < 0 || index > parent.getChildCount()) {
			parent.addChild(node);
			treeModel.nodesWereInserted(parent, new int[] { parent.getChildCount() - 1});
		}
		else {
			parent.addChild(index, node);
			treeModel.nodesWereInserted(parent, new int[] { index});
		}
		node.setParent(parent);
	}

	private NonSortingRootTreeNode deleteNode_internal(PhaseNode node) {
		// remove tree node
		NonSortingRootTreeNode parent = (NonSortingRootTreeNode) node.getParent();
		int index = parent.getIndex(node);
		parent.removeChild(node);

		treeModel.nodesWereRemoved(parent, new int[] { index}, new Object[] { node});
		return parent;
	}

	private synchronized void deleteNode(PhaseNode node) {
		tree.removeTreeSelectionListener(treeSelectionListener);

		selectNode(deleteNode_internal(node));

		tree.addTreeSelectionListener(treeSelectionListener);
	}

	private synchronized void setCurrentNode(NonSortingRootTreeNode node) {
		currentNode = (node instanceof PhaseNode ? (PhaseNode) node : null);
		refreshButtons();
	}

	private void expandNodeAll(NonSortingRootTreeNode node) {
		TreePath path = getTreePath(node);
		if (path != null) {
			for (int i = 0; i < node.getChildCount(); i++) {
				NonSortingRootTreeNode child = (NonSortingRootTreeNode) node.getChildAt(i);
				expandNodeAll(child);
			}
			tree.expandPath(path);
		}
	}

	private final boolean canMoveUp() {
		return currentNode.getParent().getIndex(currentNode) > 0;
	}

	private final boolean canMoveDown() {
		return currentNode.getParent().getIndex(currentNode) < (currentNode.getParent().getChildCount() - 1);
	}

	private void refreshButtons() {
		editButton.setEnabled(currentNode != null);
		deleteButton.setEnabled(currentNode != null && currentNode.isLeaf());
		subRefButton.setEnabled(currentNode != null && !(currentNode.getPhase() instanceof PhaseReference));
		leftButton.setEnabled(currentNode != null && canOutdent());
		rightButton.setEnabled(currentNode != null && canIndent());
		upButton.setEnabled(currentNode != null && canMoveUp());
		downButton.setEnabled(currentNode != null && canMoveDown());
	}

	private Phase[] getReferencablePhases() {
		List<Phase> rootPhaseList = new ArrayList<Phase>();
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			PhaseNode node = (PhaseNode) rootNode.getChildAt(i);
			if (!isAncestorOf(node, currentNode)) {
				rootPhaseList.add(node.getPhase());
			}
		}
		return rootPhaseList.toArray(new Phase[0]);
	}

	private boolean isAncestorOf(NonSortingRootTreeNode nodeToCheck, NonSortingRootTreeNode childNode) {
		if (childNode.getParent() == nodeToCheck) {
			return true;
		}
		else if (childNode.getParent() == rootNode) {
			return false;
		}
		else {
			return isAncestorOf(nodeToCheck, (NonSortingRootTreeNode) childNode.getParent());
		}
	}

	private void refreshTree(List<Phase> phaseList) {
		tree.removeTreeSelectionListener(treeSelectionListener);

		rootNode.removeAllChildren();
		for (Iterator<Phase> iter = phaseList.iterator(); iter.hasNext();) {
			Phase element = iter.next();
			if (element.isRoot()) {
				rootNode.addChild(new PhaseNode(rootNode, element));
			}
		}
		treeModel.reload();
		expandNodeAll(rootNode);

		setCurrentNode(null);
		tree.addTreeSelectionListener(treeSelectionListener);
	}

}