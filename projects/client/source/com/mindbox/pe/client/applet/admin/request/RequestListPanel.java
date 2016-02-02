package com.mindbox.pe.client.applet.admin.request;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.selection.IDNameDescriptionObjectSelectionPanel;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTable;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.filter.AllSearchFilter;
import com.mindbox.pe.model.process.ProcessRequest;

/**
 * 
 *
 * @author kim
 * @since PowerEditor 3.3.0
 */
class RequestListPanel extends IDNameDescriptionObjectSelectionPanel<ProcessRequest, ButtonPanel> {

	public static RequestListPanel createInstance(boolean readOnly) {
		RequestListPanel panel = new RequestListPanel(
				ClientUtil.getInstance().getLabel("label.request"),
				new IDNameDescriptionObjectSelectionTable<RequestSelectionTableModel, ProcessRequest>(
						new RequestSelectionTableModel(),
						true),
				readOnly);
		return panel;
	}

	private class NewL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent arg0) {
			ProcessRequest request = RequestEditDialog.newRequest();
			if (request != null) {
				try {
					int newID = ClientUtil.getCommunicator().save(request, false);
					request.setID(newID);

					// add to selection table
					selectionTable.add(request);
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private class DeleteL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent arg0) {
			ProcessRequest request = getSelectedRequest();
			if (request != null
					&& ClientUtil.getInstance().showConfirmation("msg.question.delete.request", new Object[] { request.getDisplayName() })) {
				// delete request
				try {
					ClientUtil.getCommunicator().delete(request.getID(), EntityType.PROCESS_REQUEST);
					selectionTable.remove(request);
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private class EditL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent arg0) {
			ProcessRequest request = getSelectedRequest();
			if (request != null) {
				request = RequestEditDialog.editRequest(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), request);
				if (request != null) {
					try {
						ClientUtil.getCommunicator().save(request, false);

						// update selection table
						selectionTable.updateRow(selectionTable.getSelectedRow());
					}
					catch (Exception ex) {
						ClientUtil.handleRuntimeException(ex);
					}
				}
			}
		}
	}

	private class LoadL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent arg0) {
			try {
				selectionTable.setDataList(ClientUtil.getCommunicator().search(
						new AllSearchFilter<ProcessRequest>(EntityType.PROCESS_REQUEST)));
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private JButton[] buttons;

	private RequestListPanel(String title,
			IDNameDescriptionObjectSelectionTable<RequestSelectionTableModel, ProcessRequest> selectionTable, boolean readOnly) {
		super(title, selectionTable, readOnly);
		setEnabledSelectionAwares(false);
	}

	private ProcessRequest getSelectedRequest() {
		if (selectionTable.getSelectedRow() > -1) {
			return (ProcessRequest) selectionTable.getModel().getValueAt(selectionTable.getSelectedRow(), -1);
		}
		else {
			return null;
		}
	}

	public void discardChanges() {
	}

	public void setEnabledSelectionAwares(boolean enabled) {
		if (buttons != null) {
			buttons[2].setEnabled(enabled);
			buttons[3].setEnabled(enabled);
		}
	}

	protected void createButtonPanel() {
		JButton button1 = UIFactory.createJButton("button.load.request", "image.btn.small.update", new LoadL(), null);
		JButton button2 = UIFactory.createJButton("button.new", "image.btn.small.new", new NewL(), null);
		JButton button3 = UIFactory.createJButton("button.edit", "image.btn.small.edit", new EditL(), null);
		JButton button4 = UIFactory.createJButton("button.delete", "image.btn.small.delete", new DeleteL(), null);
		buttons = new JButton[] { button1, button2, button3, button4 };

		super.buttonPanel = new ButtonPanel(buttons, FlowLayout.LEFT);

		if (isReadOnly()) {
			ClientUtil.setEnabled(false, button2, button3, button4);
		}
	}
}