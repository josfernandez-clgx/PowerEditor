package com.mindbox.pe.client.applet.cbr;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTable;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRCaseBaseAttributesManagementPanel extends JPanel implements PowerEditorTabPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private CBRCaseBase caseBase;
	private CBRCaseBaseManagementPanel cbPanel;
	private CBRAttributeManagementPanel attPanel;

	/**
	 * 
	 */
	public CBRCaseBaseAttributesManagementPanel(CBRCaseBase cb, boolean readOnly) {
		super();
		caseBase = cb;
		try {
			cbPanel = new CBRCaseBaseManagementPanel(caseBase);
			cbPanel.setMinimumSize(new Dimension(100, 100));
			cbPanel.setPreferredSize(new Dimension(270, 1000));
			CBRAttributeTableModel tableModel = new CBRAttributeTableModel();
			CBRAttributeDetailPanel detailPanel = new CBRAttributeDetailPanel(cb);
			IDNameDescriptionObjectSelectionTable<CBRAttributeTableModel, CBRAttribute> table = new IDNameDescriptionObjectSelectionTable<CBRAttributeTableModel, CBRAttribute>(tableModel, false);
			CBRAttributeSelectionPanel selectionPanel = new CBRAttributeSelectionPanel(ClientUtil.getInstance().getLabel("label.title.cbr.attributes"), table, detailPanel, readOnly);
			CBRAttributeFilterPanel filterPanel = new CBRAttributeFilterPanel(selectionPanel, false, caseBase);
			attPanel = new CBRAttributeManagementPanel(filterPanel, selectionPanel, detailPanel);
			attPanel.setMinimumSize(new Dimension(200, 100));
			attPanel.setPreferredSize(new Dimension(630, 1000));
			detailPanel.setSelectionPanel(selectionPanel);
			JSplitPane splitPane = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setLeftComponent(cbPanel);
			splitPane.setRightComponent(attPanel);
			splitPane.setDividerLocation(270);
			splitPane.setResizeWeight(.3);
			setLayout(new BorderLayout(0, 0));
			add(splitPane, BorderLayout.CENTER);
		}
		catch (ServerException ex) {
			ClientUtil.handleRuntimeException(ex);
		}
	}

	public boolean hasUnsavedChanges() {
		return cbPanel.hasUnsavedChanges() || attPanel.hasUnsavedChanges();
	}

	public void saveChanges() throws CanceledException, ServerException {
		if (cbPanel.hasUnsavedChanges()) cbPanel.saveChanges();
		if (attPanel.hasUnsavedChanges()) attPanel.saveChanges();
	}

	public void discardChanges() {
		if (cbPanel.hasUnsavedChanges()) cbPanel.discardChanges();
		if (attPanel.hasUnsavedChanges()) attPanel.discardChanges();
	}

}
