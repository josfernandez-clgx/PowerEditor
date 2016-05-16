/*
 * Created on Dec 12, 2006
 */
package com.mindbox.pe.client.applet.entities.generic.category;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.common.MutableBoolean;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;


/**
 * Panel holding category to category relationships and related buttons.
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
public class CategoryToCategoryListPanel extends PanelBase {

	private class EditL extends AbstractThreadedActionAdapter {
		@Override
		public void performAction(ActionEvent event) throws Exception {
			int selectedRow = selectionTable.getSelectedRow();
			MutableTimedAssociationKey oldKey = getSelectedData();

			if (oldKey != null) {
				MutableTimedAssociationKey newKey = CategoryToCategoryEditDialog.editParentCategory(category, oldKey);

				if ((newKey != null) && isValid(oldKey, newKey, false)) {
					oldKey.setAssociableID(newKey.getAssociableID());
					oldKey.setEffectiveDate(newKey.getEffectiveDate());
					oldKey.setExpirationDate(newKey.getExpirationDate());
					selectionTable.updateRow(selectedRow);
				}
			}
		}
	}

	private class NewL extends AbstractThreadedActionAdapter {
		private MutableTimedAssociationKey getMostRecentKey() {
			MutableTimedAssociationKey results = null;
			for (MutableTimedAssociationKey key : selectionTable.getSelectionTableModel().getDataList()) {
				if (key.getExpirationDate() == null) {
					results = key;

					break;
				}
				else if ((results == null) || key.getExpirationDate().after(results.getExpirationDate())) {
					results = key;
				}
			}

			return results;
		}

		@Override
		public void performAction(ActionEvent event) throws Exception {
			if (category != null) {
				// create mutable boolean object so the we can get back the autoExpire option. This
				// is not the preferable approach to getting back information from a method...
				MutableBoolean autoExpire = new MutableBoolean(false);
				MutableTimedAssociationKey data = CategoryToCategoryEditDialog.newParentCategory(category, autoExpire, null);

				while ((data != null) && !isValid(null, data, autoExpire.booleanValue())) {
					data = CategoryToCategoryEditDialog.newParentCategory(category, autoExpire, data);
				}

				if (data != null) {
					if (autoExpire.booleanValue()) {
						MutableTimedAssociationKey lastKey = getMostRecentKey();
						lastKey.setExpirationDate(data.getEffectiveDate());
					}
					selectionTable.getSelectionTableModel().addData(data);
				}
			}
		}
	}

	private class RemoveL extends AbstractThreadedActionAdapter {
		@Override
		public void performAction(ActionEvent event) throws Exception {
			if (ClientUtil.getInstance().showConfirmation("msg.question.remove.entity", new Object[] { "category to category association" })) {
				MutableTimedAssociationKey key = getSelectedData();
				selectionTable.getSelectionTableModel().removeData(key);
			}
		}
	}

	private final class ShowDateNameL extends AbstractThreadedActionAdapter {
		@Override
		public void performAction(ActionEvent e) {
			selectionTable.refresh(dateNameCheckbox.isSelected());
		}
	}

	private class TableSelectionL implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			setEnabledSelectionAwares(selectionTable.getSelectedRow() > -1);
		}
	}

	private static final long serialVersionUID = -3951228734910107454L;

	private final CategoryToCategorySelectionTable selectionTable;
	private final GenericCategory category;
	private final JButton newButton;
	private final JButton editButton;
	private final JButton removeButton;
	private final JCheckBox dateNameCheckbox;

	public CategoryToCategoryListPanel(GenericCategory category) throws ServerException {
		super();
		this.category = category;
		selectionTable = new CategoryToCategorySelectionTable(new CategoryToCategorySelectionTableModel(category.getType()));

		dateNameCheckbox = UIFactory.createCheckBox("checkbox.show.date.name");
		dateNameCheckbox.setSelected(true);
		dateNameCheckbox.addActionListener(new ShowDateNameL());

		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), "image.btn.small.edit", new EditL(), null);

		newButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new"), "image.btn.small.new", new NewL(), null);

		removeButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.remove"), "image.btn.small.delete", new RemoveL(), null);

		initPanel();

		setEnabled(false);

		// only enable new button if there are categories to select and it is not the root 
		newButton.setEnabled(category.getAllParentAssociations() != null && category.getAllParentAssociations().size() > 0);
		selectionTable.getSelectionModel().addListSelectionListener(new TableSelectionL());
	}

	List<MutableTimedAssociationKey> getParentCategoryAssociations() {
		return selectionTable.getSelectionTableModel().getDataList();
	}

	private MutableTimedAssociationKey getSelectedData() {
		return selectionTable.getSelectedDataObject();
	}

	CategoryToCategorySelectionTable getSelectionTable() {
		return selectionTable;
	}

	private void initPanel() throws ServerException {
		setLayout(new BorderLayout(1, 1));
		add(UIFactory.createLabel("label.parent.categories"), BorderLayout.NORTH);
		add(new JScrollPane(selectionTable), BorderLayout.WEST);

		JPanel btnPanel = UIFactory.createJPanel(new BorderLayout(1, 1));
		btnPanel.add(newButton, BorderLayout.NORTH);
		btnPanel.add(editButton, BorderLayout.CENTER);
		btnPanel.add(removeButton, BorderLayout.SOUTH);

		add(btnPanel, BorderLayout.EAST);
		populateData();
	}

	private boolean isValid(MutableTimedAssociationKey oldData, MutableTimedAssociationKey newData, boolean autoExpire) {
		if (newData.getAssociableID() < 1) {
			ClientUtil.getInstance().showErrorDialog("msg.errors.required", "A parent category");

			return false;
		}

		if ((newData.getEffectiveDate() != null) && (newData.getExpirationDate() != null) && newData.getEffectiveDate().after(newData.getExpirationDate())) {
			ClientUtil.getInstance().showErrorDialog("InvalidActivationDateRangeMsg");

			return false;
		}
		else {
			for (Iterator<MutableTimedAssociationKey> i = selectionTable.getSelectionTableModel().getDataList().iterator(); i.hasNext();) {
				MutableTimedAssociationKey listData = i.next();

				if ((oldData == null) || (oldData != listData)) {
					if (autoExpire) {
						if ((newData.getEffectiveDate() == null) || ((listData.getEffectiveDate() != null) && newData.getEffectiveDate().before(listData.getEffectiveDate()))) {
							ClientUtil.getInstance().showErrorDialog("msg.error.cannot.autoexpire.parent");

							return false;
						}
					}
					else if (newData.overlapsWith(listData)) {
						ClientUtil.getInstance().showErrorDialog("CategoryToCategoryRelationshipOverlaps");

						return false;
					}
				}
			}
		}

		return true;
	}

	private synchronized void populateData() throws ServerException {
		List<MutableTimedAssociationKey> data = new ArrayList<MutableTimedAssociationKey>();
		for (Iterator<MutableTimedAssociationKey> i = category.getParentKeyIterator(); i.hasNext();) {
			MutableTimedAssociationKey key = i.next();
			MutableTimedAssociationKey newkey = new DefaultMutableTimedAssociationKey(key.getAssociableID(), key.getEffectiveDate(), key.getExpirationDate());
			data.add(newkey);
		}
		selectionTable.setDataList(data);
		setEnabled(true);
		setEnabledSelectionAwares(false);
		newButton.setEnabled(true);
	}

	private void setEnabledSelectionAwares(boolean enabled) {
		removeButton.setEnabled(enabled && (selectionTable.getModel().getRowCount() != 1));
		editButton.setEnabled(enabled);
	}
}
