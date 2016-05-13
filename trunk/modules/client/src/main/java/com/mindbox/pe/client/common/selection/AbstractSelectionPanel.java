package com.mindbox.pe.client.common.selection;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.model.Persistent;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class AbstractSelectionPanel<D extends Persistent, B extends ButtonPanel> extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static final void addComponent(JPanel panel, GridBagLayout bag, GridBagConstraints c, Component component) {
		bag.setConstraints(component, c);
		panel.add(component);
	}

	protected B buttonPanel;
	private final boolean readOnly;

	protected AbstractSelectionPanel(String title, boolean readOnly) {
		super();
		this.readOnly = readOnly;
		UIFactory.setLookAndFeel(this);
		setBorder(UIFactory.createTitledBorder(title));
	}

	protected final boolean isReadOnly() {
		return readOnly;
	}

	public abstract void clearSelection();

	public abstract void populate(List<D> dataList);

	public abstract List<D> getSelectedObjects();

	public abstract void remove(D object);

	public abstract void add(D object);

	// TT 2072
	public abstract void update(D object);

	public abstract void updateDisplay(int entityID);

	public abstract void selectEntity(int entityID);

	public void discardChanges() {
		if (buttonPanel != null) buttonPanel.discardChanges();
	}

	public abstract void setEnabledSelectionAwares(boolean enabled);

	protected abstract void createButtonPanel();

	public B getButtonPanel() {
		return buttonPanel;
	}

	public void refresh() {
		//noop
	}

	public void setCategoryOnDate(Date date) {
		//noop
	}

	// for 1934
	public boolean hasChangesInDetails() {
		return false;
	}

	// for 1934
	public boolean saveChangesInDetails() {
		return true;
	}

	// for 1934
	public void discardChangesInDetails() {
		// noop
	}

}
