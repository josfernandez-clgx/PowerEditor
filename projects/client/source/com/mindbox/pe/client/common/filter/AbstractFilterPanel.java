package com.mindbox.pe.client.common.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;

/**
 * T represents the item that will be displayed, not the filter.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class AbstractFilterPanel<T extends Persistent,B extends ButtonPanel> extends JPanel implements PowerEditorTabPanel {

	public static final String NAVIGATE_FILTER_LBL = "tab.navigate";
	public static final String NAVIGATE_FILTER_TOOLTIP = "tab.tooltip.navigate";

	private class FilterL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) {
			if (!panelActionSaveCheck()) { return; } //make sure there are no changes to save
			
			search();
		}
	}

	// TT 2021
	public void search() {	
		SearchFilter<T> searchFilter = getSearchFilterFromFields();
		if (searchFilter != null) {
			try {
				List<T> resultList = ClientUtil.getCommunicator().search(searchFilter);
				selectionPanel.populate(resultList);
			}
			catch (ServerException ex) {
				ClientUtil.getInstance().showErrorDialog("msg.error.generic.service", new Object[] { ClientUtil.getInstance().getErrorMessage(ex)});
			}
		}
	}

	protected final AbstractSelectionPanel<T,B> selectionPanel;
	protected final Logger logger;

	protected AbstractFilterPanel(AbstractSelectionPanel<T,B> selectionPanel) {
		this.logger = Logger.getLogger(getClass());
		this.selectionPanel = selectionPanel;
		UIFactory.setLookAndFeel(this);
	}

	protected final ActionListener getFilterListener() {
		return new FilterL();
	}

	protected abstract SearchFilter<T> getSearchFilterFromFields();
	
	// Next three implement default behavior for PowerEditorTabPanel interface
	public boolean hasUnsavedChanges() { return false; }
	public void saveChanges() throws CanceledException, ServerException {}
	public void discardChanges() {}
	
	public boolean panelActionSaveCheck() { return true; }

}
