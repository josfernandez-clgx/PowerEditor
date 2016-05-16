package com.mindbox.pe.client.common.filter;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.Persistent;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class AbstractPersistedFilterPanel<T extends Persistent, B extends ButtonPanel> extends AbstractFilterPanel<T, B> {

	private class ClearL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			clearSearchFields();
		}
	}

	private static final long serialVersionUID = -3951228734910107454L;

	protected static final void addComponent(JPanel panel, GridBagLayout bag, GridBagConstraints c, Component component) {
		bag.setConstraints(component, c);
		panel.add(component);
	}

	protected final PeDataType filterEntityType;
	protected final GenericEntityType filterGenericEntityType;
	private JButton filterButton;
	private JButton clearSearchFieldsButton;

	protected AbstractPersistedFilterPanel(AbstractSelectionPanel<T, B> selectionPanel, GenericEntityType filterGenericEntityType, boolean hideManagementButtons) {
		this(selectionPanel, null, filterGenericEntityType, hideManagementButtons);
	}

	protected AbstractPersistedFilterPanel(AbstractSelectionPanel<T, B> selectionPanel, PeDataType filterEntityType, boolean hideManagementButtons) {
		this(selectionPanel, filterEntityType, null, hideManagementButtons);
	}

	private AbstractPersistedFilterPanel(AbstractSelectionPanel<T, B> selectionPanel, PeDataType filterEntityType, GenericEntityType filterGenericEntityType,
			boolean hideManagementButtons) {
		super(selectionPanel);
		this.filterEntityType = filterEntityType;
		this.filterGenericEntityType = filterGenericEntityType;

		clearSearchFieldsButton = UIFactory.createButton(null, "image.btn.small.delete", new ClearL(), "button.tooltip.clear.filter");
		clearSearchFieldsButton.setEnabled(true);

		String filterButtonStr = null;
		if (filterGenericEntityType != null) {
			filterButtonStr = ClientUtil.getInstance().getLabel("button.search." + filterGenericEntityType.toString(), "Search " + filterGenericEntityType.getDisplayName());
		}
		else {
			filterButtonStr = "Search " + filterEntityType.toString();
		}
		filterButton = UIFactory.createButton(filterButtonStr, null, getFilterListener(), null);

		// note hideManagementButtons is no longer being used
		initPanel(hideManagementButtons);
	}

	/**
	 * Adds additional UI components, if necessary. This should be overriden by sub-classes. This
	 * method implementation does nothing.
	 * @param bag bag
	 * @param c constraint
	 */
	protected abstract void addComponents(GridBagLayout bag, GridBagConstraints c);

	protected abstract void clearSearchFields();

	private void initPanel(boolean hideManagementButtons) {
		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		ButtonPanel buttonPanel = new ButtonPanel(new JButton[] { clearSearchFieldsButton }, FlowLayout.LEFT);
		addComponent(this, bag, c, buttonPanel);
		c.insets = new Insets(2, 1, 3, 1);
		addComponent(this, bag, c, new JSeparator());

		// add impl-specific fields
		addComponents(bag, c);

		c.insets = new Insets(4, 2, 2, 2);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, filterButton);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, Box.createVerticalGlue());
	}
}
