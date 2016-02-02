/*
 * Created on Oct 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mindbox.pe.client.applet.cbr;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRCaseBaseManagementPanel extends JPanel implements
		PowerEditorTabPanel {

	private class NewL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_newEntity();
		}
	}

	private class RemoveL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_removeEntity();
		}
	}

	private class CloneL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_cloneEntity();
		}
	}

	private CBRCaseBase caseBase = null;
	private ButtonPanel buttonPanel = null;
	private CBRCaseBaseDetailPanel cbDetails = null;
	/**
	 * 
	 */
	public CBRCaseBaseManagementPanel(CBRCaseBase cb) throws ServerException {
		super();
		caseBase = cb;
		setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.cbr.case.base")));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addButtonPanel();
		cbDetails = new CBRCaseBaseDetailPanel(cb);
		add(cbDetails);
		// Auto-generated constructor stub
	}

	private void addButtonPanel(){
		JButton newButton =
			UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.new"),
				"image.btn.small.new",
				new NewL(),
				null);

		// This is a copy button as of PE 4.2.0.
		JButton cloneButton =
			UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.copy"),
				"image.btn.small.copy",
				new CloneL(),
				null);
		JButton removeButton =
			UIFactory.createButton(
					ClientUtil.getInstance().getLabel("button.remove"),
				"image.btn.small.delete",
				new RemoveL(),
				null);
		JButton[] buttons = new JButton[] { newButton, cloneButton, removeButton };


		buttonPanel = new ButtonPanel(buttons, FlowLayout.LEFT);
		add(buttonPanel);
	}
	
	public boolean hasUnsavedChanges() {
		return this.cbDetails.hasUnsavedChanges();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.mindbox.pe.client.common.tab.PowerEditorTabPanel#discardChanges()
	 */
	public void discardChanges() {
		this.cbDetails.discardChanges();
	}

	/* (non-Javadoc)
	 * @see com.mindbox.pe.client.common.tab.PowerEditorTabPanel#saveChanges()
	 */
	public void saveChanges() throws CanceledException, ServerException {
		this.cbDetails.saveChanges();
	}
	
	private void _newEntity() {
		try {
			CBRPanel.getInstance().newCaseBase(new CBRCaseBase());
		} catch (Exception x) {
			// what to do here???
		}
	}
	private void _cloneEntity() {
		try {
			CBRPanel.getInstance().cloneCaseBase(caseBase);
			/*
			CBRCaseBase cb = new CBRCaseBase();
			cb.copyFrom(caseBase);
			CBRPanel.getInstance().newCaseBase(cb);
			*/
		} catch (Exception x) {
			// what to do here???
		}
		CBRPanel.getInstance().updateFromServer();
	}
	
	private void _removeEntity() {
		if (ClientUtil
				.getInstance()
				.showConfirmation("msg.question.remove.entity", new Object[] { "Case-Base" })) {
			try {
				if (caseBase.getID() != CBRCaseBase.UNASSIGNED_ID) {
					ClientUtil.getCommunicator().delete(caseBase.getID(),EntityType.CBR_CASE_BASE);
				}
			} catch (Exception x) {
				// what to do here???
			}
			CBRPanel.getInstance().updateFromServer();
		}
	}
}
