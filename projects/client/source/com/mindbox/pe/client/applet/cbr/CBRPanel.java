package com.mindbox.pe.client.applet.cbr;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.AllSearchFilter;


/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRPanel extends JPanel implements PowerEditorTabPanel, ChangeListener {

	private static CBRPanel instance = null;

	public static CBRPanel getInstance() {
		if (instance == null) throw new IllegalStateException("Call getNewInstance(boolean) first");
		return instance;
	}

	public static CBRPanel getNewInstance(boolean readOnly) {
		instance = new CBRPanel(readOnly);
		return instance;
	}

	// allow GC
	public static void reset() {
		instance = null;
	}

	private class NewL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_newCaseBase();
		}
	}

	private PowerEditorTab tabbedPane;
	private JButton newButton;
	private TitledBorder titledborder;
	private HashMap<String, CBRCaseBase> caseBaseTabs = new HashMap<String, CBRCaseBase>();
	private boolean noCaseBases = true;
	private final boolean readOnly;

	public CBRPanel(boolean readOnly) {
		super();
		this.readOnly = readOnly;
		tabbedPane = new PowerEditorTab();
		tabbedPane.setTabPlacement(SwingConstants.TOP);
		tabbedPane.setFont(PowerEditorSwingTheme.tabFont);
		tabbedPane.setFocusable(false);
		newButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.casebase.new"),
				"image.btn.small.new",
				new NewL(),
				null);
		titledborder = UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.no.casebases"));
		addNoCaseBasesWidgets();

		if (readOnly) {
			newButton.setEnabled(false);
			newButton.setVisible(false);
		}
	}

	private void updateWidgets() {
		if (caseBaseTabs.size() == 0) {
			if (!noCaseBases) {
				removeCaseBasesWidgets();
				addNoCaseBasesWidgets();
				noCaseBases = true;
			}
		}
		else {
			if (noCaseBases) {
				removeNoCaseBasesWidgets();
				addCaseBasesWidgets();
				noCaseBases = false;
			}
		}
	}

	private void addCaseBasesWidgets() {
		setLayout(new GridLayout(1, 1, 0, 0));
		add(tabbedPane);
	}

	private void removeCaseBasesWidgets() {
		remove(tabbedPane);
	}

	private void addNoCaseBasesWidgets() {
		setLayout(new FlowLayout());
		setBorder(titledborder);
		add(newButton);
	}

	private void removeNoCaseBasesWidgets() {
		setBorder(null);
		remove(newButton);
	}

	private void _newCaseBase() {
		try {
			this.newCaseBase(new CBRCaseBase());
		}
		catch (Exception x) {
		}
	}

	private List<CBRCaseBase> getCaseBasesFromServer() {
		List<CBRCaseBase> caseBaseList = new ArrayList<CBRCaseBase>();
		try {
			caseBaseList = ClientUtil.getCommunicator().search(new AllSearchFilter<CBRCaseBase>(EntityType.CBR_CASE_BASE));
		}
		catch (ServerException x) {
			ClientUtil.handleRuntimeException(x);
		}
		return caseBaseList;
	}

	private void updateCaseBaseTabs(List<CBRCaseBase> caseBaseList) {
		// find the new set of Case Base tabs
		HashMap<String, CBRCaseBase> newCBTabs = new HashMap<String, CBRCaseBase>();
		Iterator<CBRCaseBase> it = caseBaseList.iterator();
		while (it.hasNext()) {
			CBRCaseBase cb = it.next();
			newCBTabs.put(cb.getName(), cb);
			CBRCaseBase oldCb = caseBaseTabs.get(cb.getName());
			if (oldCb != null) {
				caseBaseTabs.remove(cb.getName());
			}
			else {
				addCaseBaseTab(cb);
			}
		}
		// remove tabs for Case Bases which no longer exist
		Iterator<String> keyIter = caseBaseTabs.keySet().iterator();
		while (keyIter.hasNext()) {
			String tabName = keyIter.next();
			int index = tabbedPane.indexOfTab(tabName);
			if (index != -1) tabbedPane.remove(index);
		}
		caseBaseTabs = newCBTabs;
	}

	private void addCaseBaseTab(CBRCaseBase cb) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (cb.getName().toUpperCase().compareTo(tabbedPane.getTitleAt(i).toUpperCase()) < 0) {
				tabbedPane.insertTab(cb.getName(), null, new CBRCaseBaseTab(cb, readOnly), null, i);
				return;
			}
		}
		tabbedPane.addTab(cb.getName(), new CBRCaseBaseTab(cb, readOnly));
	}

	public boolean hasUnsavedChanges() {
		Component c = tabbedPane.getTabCount() > 0 ? tabbedPane.getSelectedComponent() : null;
		if (c == null)
			return false;
		else if (c instanceof PowerEditorTabPanel)
			return ((PowerEditorTabPanel) c).hasUnsavedChanges();
		else
			return false;
	}

	public void saveChanges() throws CanceledException, ServerException {
		Component c = tabbedPane.getTabCount() > 0 ? tabbedPane.getSelectedComponent() : null;
		if (c == null)
			return;
		else if (c instanceof PowerEditorTabPanel) ((PowerEditorTabPanel) c).saveChanges();
	}

	public void discardChanges() {
		Component c = tabbedPane.getTabCount() > 0 ? tabbedPane.getSelectedComponent() : null;
		if (c == null)
			return;
		else if (c instanceof PowerEditorTabPanel) ((PowerEditorTabPanel) c).discardChanges();
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JTabbedPane) {
			if (((JTabbedPane) e.getSource()).getSelectedComponent().equals(this)) updateFromServer();
		}
	}

	public void newCaseBase(CBRCaseBase newCaseBase) throws CanceledException, ServerException {
		List<CBRCaseBase> existingCaseBases = getCaseBasesFromServer();
		String newName = ClientUtil.getInstance().getLabel("cbr.new.casebase.name");
		newName = assureUnique(newName);
		newCaseBase.setName(newName);
		existingCaseBases.add(newCaseBase);
		updateCaseBaseTabs(existingCaseBases);
		updateWidgets();
		saveCaseBase(newCaseBase);
		selectCaseBase(newCaseBase);
	}

	public void cloneCaseBase(CBRCaseBase caseBase) throws ServerException {
		ClientUtil.getCommunicator().cloneCaseBases(caseBase.getId(), assureUnique(caseBase.getName()));
	}

	public void updateFromServer() {
		updateCaseBaseTabs(getCaseBasesFromServer());
		updateWidgets();
	}

	public void selectCaseBase(CBRCaseBase cb) {
		int index = tabbedPane.indexOfTab(cb.getName());
		if (index > -1) tabbedPane.setSelectedIndex(index);
	}

	public void saveCaseBase(CBRCaseBase cb) {
		int index = tabbedPane.indexOfTab(cb.getName());
		if (index > -1) {
			Component c = tabbedPane.getComponentAt(index);
			if (c instanceof PowerEditorTabPanel) try {
				((PowerEditorTabPanel) c).saveChanges();
			}
			catch (Exception x) {
			}
		}
	}

	private String assureUnique(String name) {
		if (caseBaseTabs.get(name) != null)
			return assureUnique(name + "*");
		else
			return name;
	}
}
