package com.mindbox.pe.client.applet.entities.compatibility;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JSeparator;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.GenericEntityTypeComboBox;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class CompatibilitySearchPanel extends PanelBase {

	private class Combo1L implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			updateSearchButtonStatus();
		}
	}

	private class Combo2L implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			updateSearchButtonStatus();
		}
	}

	private class SearchL extends AbstractThreadedActionAdapter {

		@Override
		public synchronized void performAction(ActionEvent event) throws Exception {
			GenericEntityType type1 = type1Combo.getSelectedEntityType();
			GenericEntityType type2 = type2Combo.getSelectedEntityType();
			if (type1 != null && type2 != null) {
				List<GenericEntityCompatibilityData> list = ClientUtil.getCommunicator().fetchCompatibilityData(type1, type2);
				selectionPanel.populateData(type1, type2, list.toArray(new GenericEntityCompatibilityData[0]));
			}
		}
	}

	private static final long serialVersionUID = -3951228734910107454L;

	private final JButton searchButton;
	private final GenericEntityTypeComboBox type1Combo, type2Combo;
	private final CompatibilityListPanel selectionPanel;

	public CompatibilitySearchPanel(CompatibilityListPanel selectionPanel) {
		super();
		this.selectionPanel = selectionPanel;
		searchButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.search"), "image.btn.small.find", new SearchL(), null);

		type1Combo = new GenericEntityTypeComboBox(false, true, true);
		type1Combo.addActionListener(new Combo1L());
		type2Combo = new GenericEntityTypeComboBox(false, true, true);
		type2Combo.addActionListener(new Combo2L());

		initPanel();

		updateSearchButtonStatus();
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.entity.type"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, type1Combo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.entity.type"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, type2Combo);

		addComponent(this, bag, c, new JSeparator());

		addComponent(this, bag, c, searchButton);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, Box.createVerticalGlue());
	}

	private synchronized void updateSearchButtonStatus() {
		searchButton.setEnabled((type1Combo.getSelectedItem() != null && type2Combo.getSelectedItem() != null));
	}
}