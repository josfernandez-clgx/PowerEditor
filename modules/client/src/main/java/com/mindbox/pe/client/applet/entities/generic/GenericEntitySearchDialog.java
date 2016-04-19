/*
 * Created on Dec 18, 2006
 *
 */
package com.mindbox.pe.client.applet.entities.generic;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.IDNameObjectCellRenderer;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.comparator.IDNameObjectComparator;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;


/**
 * Search for generic entity dialog.
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
public class GenericEntitySearchDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;
	private final MDateDateField noParentAsOf;
	private final JTextField nameField;
	private final boolean forMultiSelect;
	private final GenericEntityType genericEntityType;
	private final JButton searchButton;
	private final JList resultsList;
	private final JDialog dialog;

	private GenericEntitySearchDialog(JDialog dialog, boolean forMultiSelect, GenericEntityType genericEntityType) {
		super();
		this.dialog = dialog;
		this.forMultiSelect = forMultiSelect;
		this.genericEntityType = genericEntityType;
		nameField = new JTextField(10);
		searchButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.search"), "image.btn.small.new", new SearchL(), null);

		resultsList = new JList();
		noParentAsOf = new MDateDateField(true, true, true);
		noParentAsOf.setValue(null);
		initDialog();
	}

	/**
	 * Displays dialog for searching for generic entities 
	 * @param forMultiSelect True is users should be allowed to multiselect entities  
	 * @param genericEntityType Entity Type of selection
	 * @return An arrayList of selected entities
	 */
	public static GenericEntity[] findGenericEntity(boolean forMultiSelect, GenericEntityType genericEntityType) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);

		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.search.entity"));
		GenericEntitySearchDialog panel = new GenericEntitySearchDialog(dialog, forMultiSelect, genericEntityType);
		UIFactory.addToDialog(dialog, panel);
		dialog.setVisible(true);

		return panel.getSelectedGenericEntities();
	}

	private void initDialog() {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;

		GridBagLayout bag = new GridBagLayout();

		JPanel criteriaContentsPanel = UIFactory.createJPanel(bag);

		c.weightx = 0.25;
		UIFactory.addComponent(criteriaContentsPanel, bag, c, UIFactory.createFormLabel("label.name.contains"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.75;
		UIFactory.addComponent(criteriaContentsPanel, bag, c, nameField);

		c.weightx = 0.25;
		c.gridwidth = 1;
		UIFactory.addComponent(criteriaContentsPanel, bag, c, UIFactory.createFormLabel("label.no.parent.asof"));
		c.weightx = 0.75;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(criteriaContentsPanel, bag, c, noParentAsOf);

		UIFactory.addComponent(criteriaContentsPanel, bag, c, searchButton);

		JPanel resultsPanel = UIFactory.createBorderLayoutPanel(2, 2);
		resultsPanel.add(new JScrollPane(resultsList), BorderLayout.CENTER);
		resultsPanel.setBorder(BorderFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.search.results")));

		JPanel mainPanel = UIFactory.createJPanel(new GridLayout(1, 2, 1, 1));

		JPanel criteriaPanel = UIFactory.createJPanel(new BorderLayout());
		criteriaPanel.setBorder(BorderFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.search.criteria")));

		criteriaPanel.add(criteriaContentsPanel, BorderLayout.NORTH);
		mainPanel.add(criteriaPanel);
		mainPanel.add(resultsPanel);

		final JPanel buttonsPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));

		final JButton okButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.ok"), null, new SelectL(), null);
		okButton.setEnabled(false);

		final JButton cancelButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.cancel"), null, new CancelL(), null);
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.NORTH);
		add(buttonsPanel, BorderLayout.SOUTH);

		resultsList.setAutoscrolls(true);
		resultsList.setSelectionMode(forMultiSelect ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);

		resultsList.setCellRenderer(new IDNameObjectCellRenderer("image.node.entity"));

		resultsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (resultsList.getSelectedValue() != null) {
					okButton.setEnabled(true);
				}
				else {
					okButton.setEnabled(false);
				}
			}
		});

		setSize(620, 220);
	}

	private GenericEntity[] getSelectedGenericEntities() {
		if (forMultiSelect) {
			GenericEntity[] entities = null;
			Object[] selectedItems = resultsList.getSelectedValues();
			if (selectedItems != null && selectedItems.length > 0) {
				entities = new GenericEntity[selectedItems.length];
				for (int i = 0; i < selectedItems.length; i++) {
					entities[i] = (GenericEntity) selectedItems[i];
				}
			}
			return entities;
		}
		else {
			return (resultsList.getSelectedValue() == null) ? null : new GenericEntity[] { (GenericEntity) resultsList.getSelectedValue() };
		}
	}

	private class SearchL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) {
			Date date = null;

			try {
				date = noParentAsOf.getValue();
			}
			catch (ParseException e) {
				ClientUtil.getInstance().showErrorDialog("msg.warning.invalid.date", new Object[] { Constants.FORMAT_STR_DATE_TIME_SEC.toString() });

				return;
			}

			GenericEntityFilterSpec searchSpec = new GenericEntityFilterSpec(genericEntityType, null);
			searchSpec.setNameCriterion(nameField.getText());

			try {
				setCursor(UIFactory.getWaitCursor());
				List<GenericEntity> list = ClientUtil.getCommunicator().search(searchSpec);
				if ((list != null) && (date != null)) {
					for (Iterator<GenericEntity> i = list.iterator(); i.hasNext();) {
						GenericEntity entity = i.next();
						List<Integer> parents = entity.getCategoryIDList(date);
						if ((parents != null) && (parents.size() > 0)) {
							i.remove();
						}
					}
				}
				Collections.sort(list, new IDNameObjectComparator<GenericEntity>());
				resultsList.setListData(list.toArray(new GenericEntity[0]));
			}
			catch (ServerException ex) {
				ClientUtil.getInstance().showErrorDialog("msg.error.generic.service", new Object[] { ClientUtil.getInstance().getErrorMessage(ex) });
			}
			finally {
				setCursor(UIFactory.getDefaultCursor());
			}
		}
	}

	private class CancelL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			resultsList.clearSelection();
			resultsList.setSelectedIndex(-1);
			dialog.dispose();
		}
	}

	private class SelectL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dialog.dispose();
		}
	}
}
