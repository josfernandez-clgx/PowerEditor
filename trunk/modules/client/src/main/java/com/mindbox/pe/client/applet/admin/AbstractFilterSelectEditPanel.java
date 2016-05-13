package com.mindbox.pe.client.applet.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.IClientConstants;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.exceptions.CanceledException;


public abstract class AbstractFilterSelectEditPanel extends PanelBase implements IClientConstants, PropertyChangeListener, PowerEditorTabPanel {

	private class ListSelectionListenerImpl implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent listselectionevent) {
			if (!listselectionevent.getValueIsAdjusting()) {
				updateButtonStates();
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	protected static String DELETE_CONFIRM_MSG = "RemoveSimpleEntityMsg";

	private PanelBase detailsPanel;
	private PanelBase filterPanel;
	private JTable selectionTable;
	private JButton editButton;
	private JButton viewButton;
	private JButton removeButton;
	private JButton newButton;
	private JButton cloneButton;
	private JButton saveButton;
	private final boolean isVerticalSplit;

	private final ListSelectionListenerImpl listenerImpl = new ListSelectionListenerImpl();

	protected AbstractFilterSelectEditPanel(boolean isVerticalSplit) {
		this.isVerticalSplit = isVerticalSplit;
		detailsPanel = null;
		filterPanel = null;
		selectionTable = null;
		editButton = null;
		viewButton = null;
		removeButton = null;
		newButton = null;
		cloneButton = null;
		saveButton = null;
	}

	/**
	 * Addes the specified button to the end of the button panel.
	 * @param button the button to add
	 * @since PowerEditor 4.5.0
	 */
	protected final void addButton(JButton button) {
		getViewBtn().getParent().add(button);
	}

	protected synchronized void addSelectionListener() {
		selectionTable.getSelectionModel().addListSelectionListener(listenerImpl);
	}

	/**
	 * Override and return <code>false</code> to hide the Copy button.
	 * @return <code>true</code>
	 */
	protected boolean allowCopy() {
		return true;
	}

	protected abstract boolean allowNew();

	protected abstract boolean allowRemove();

	protected void create(ActionListener actionlistener) {
		setLayout(new BorderLayout());

		detailsPanel = createDetailsPanel();
		selectionTable = createSelectionTable();

		JPanel jpanel = UIFactory.createJPanel();

		JPanel selectionPanel = UIFactory.createJPanel();

		initSelectionButtonPanel(jpanel);
		initTablePanel(selectionPanel, actionlistener, jpanel);
		JScrollPane jscrollpane = new JScrollPane(selectionTable);
		initTable();
		selectionPanel.add(jscrollpane, "Center");
		selectionPanel.add(jpanel, "North");

		JSplitPane jsplitpane = UIFactory.createSplitPane((isVerticalSplit ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT), selectionPanel, new JScrollPane(detailsPanel));
		jsplitpane.setDividerLocation((isVerticalSplit ? 280 : 500));

		filterPanel = createFilterPanel(selectionTable);
		if (filterPanel != null) {
			JSplitPane jsplitpane1 = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, jsplitpane);
			jsplitpane1.setDividerLocation(280);
			add(jsplitpane1, BorderLayout.CENTER);
		}
		else {
			add(jsplitpane, BorderLayout.CENTER);
		}
	}

	protected abstract PanelBase createDetailsPanel();

	protected abstract PanelBase createFilterPanel(JTable jtable);

	protected abstract JTable createSelectionTable();

	@Override
	public void discardChanges() {
		if (detailsPanel instanceof PowerEditorTabPanel) {
			((PowerEditorTabPanel) detailsPanel).discardChanges();
		}
	}

	protected JButton getCloneBtn() {
		return cloneButton;
	}

	protected PanelBase getDetailsPanel() {
		return detailsPanel;
	}

	protected JButton getEditBtn() {
		return editButton;
	}

	protected abstract String getEditPermission();

	protected PanelBase getFilterPanel() {
		return filterPanel;
	}

	protected JButton getNewBtn() {
		return newButton;
	}

	protected String getRefreshProperty() {
		return "SelUpdated";
	}

	protected JButton getRemoveBtn() {
		return removeButton;
	}

	protected JButton getSaveBtn() {
		return saveButton;
	}

	protected JTable getSelectionTable() {
		return selectionTable;
	}

	protected abstract String getTableTitle();

	protected JButton getViewBtn() {
		return viewButton;
	}

	protected abstract void handleDetailUpdate(PropertyChangeEvent propertychangeevent);

	@Override
	public boolean hasUnsavedChanges() {
		if (detailsPanel instanceof PowerEditorTabPanel) {
			return ((PowerEditorTabPanel) detailsPanel).hasUnsavedChanges();
		}
		else {
			return false;
		}
	}

	private void initSelectionButtonPanel(JPanel jpanel) {
		jpanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
	}

	private void initTable() {
		selectionTable.setSelectionMode(0);
		selectionTable.setShowHorizontalLines(true);
		selectionTable.addPropertyChangeListener(getRefreshProperty(), this);
		detailsPanel.addPropertyChangeListener("DetailUpdated", this);
		addSelectionListener();

		selectionTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent mouseevent) {
				int i = mouseevent.getClickCount();
				if (i == 1) {
					viewButton.doClick();
				}
				else if (i == 2 && isEditAllowed()) {
					editButton.doClick();
				}
			}
		});
	}

	private void initTablePanel(JPanel jpanel, ActionListener actionlistener, JPanel jpanel1) {
		jpanel.setLayout(new BorderLayout());
		TitledBorder titledborder = UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel(getTableTitle()));
		jpanel.setBorder(titledborder);

		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), "image.btn.small.edit", actionlistener, null);
		newButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new"), "image.btn.small.new", actionlistener, null);
		// This is a copy button as of PowerEditor 4.2.0.
		cloneButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.copy"), "image.btn.small.copy", actionlistener, null);
		removeButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.remove"), "image.btn.small.delete", actionlistener, null);
		viewButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.view"), "image.btn.small.view", actionlistener, null);

		if (isEditAllowed()) {
			if (!allowNew()) {
				newButton.setVisible(false);
			}
			if (!allowRemove()) {
				removeButton.setVisible(false);
			}
			if (!allowCopy()) {
				cloneButton.setVisible(false);
			}
			JButton ajbutton[] = { editButton, viewButton, newButton, cloneButton, removeButton };
			for (int i = 0; i < ajbutton.length; i++) {
				if (ajbutton[i] != null) {
					jpanel1.add(ajbutton[i]);
					ajbutton[i].setEnabled(false);
				}
			}
			newButton.setEnabled(true);
		}
		else {
			editButton.setVisible(false);
			cloneButton.setVisible(false);
			newButton.setVisible(false);
			removeButton.setVisible(false);
			viewButton.setEnabled(false);
			jpanel1.add(viewButton);
		}
	}

	protected boolean isEditAllowed() {
		return !isReadOnly() && ClientUtil.checkPermissionByPrivilegeName(getEditPermission());
	}

	protected abstract boolean isReadOnly();

	@Override
	public void propertyChange(PropertyChangeEvent propertychangeevent) {
		propertychangeevent.getNewValue();
		if (propertychangeevent.getPropertyName().equals("DetailUpdated"))
			handleDetailUpdate(propertychangeevent);
		else if (propertychangeevent.getPropertyName().equals("EntityRefreshed")) updateButtonStates();
	}

	protected synchronized void removeSelectionListener() {
		selectionTable.getSelectionModel().removeListSelectionListener(listenerImpl);
	}

	@Override
	public void saveChanges() throws CanceledException, ServerException {
		if (detailsPanel instanceof PowerEditorTabPanel) {
			((PowerEditorTabPanel) detailsPanel).saveChanges();
		}
	}

	void updateButtonStates() {
		boolean hasSelection = selectionTable.getSelectedRow() >= 0;
		newButton.setVisible(allowNew());
		if (isEditAllowed()) {
			if (!allowCopy()) {
				cloneButton.setVisible(false);
			}
			if (!allowRemove()) {
				removeButton.setVisible(false);
			}
			JButton ajbutton[] = { editButton, viewButton, cloneButton };
			for (int j = 0; j < ajbutton.length; j++) {
				if (ajbutton[j] != null) {
					ajbutton[j].setEnabled(hasSelection);
				}
			}
			removeButton.setEnabled(allowRemove() && hasSelection);
		}
		else {
			JButton ajbutton[] = { editButton, cloneButton, removeButton };
			for (int j = 0; j < ajbutton.length; j++) {
				if (ajbutton[j] != null) {
					ajbutton[j].setEnabled(false);
				}
			}
			viewButton.setEnabled(hasSelection);
		}
		updateButtonStatesInternal(hasSelection);
	}

	protected void updateButtonStatesInternal(boolean hasSelection) {
	}
}
