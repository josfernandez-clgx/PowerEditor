/*
 * Created on Dec 12, 2006
 */
package com.mindbox.pe.client.applet.entities.generic.category;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;


/**
 * Panel holding category to category relationships and related buttons.
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
public class CategoryToEntityListPanel extends PanelBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final CategoryToEntitySelectionTable selectionTable;
	private final GenericCategory category;
	private final JButton addButton, editButton, removeButton;
	private final JCheckBox dateNameCheckbox;
	private List<GenericEntity> lockedEntities;

	/**
	 * @throws ServerException 
	 *  
	 */
	public CategoryToEntityListPanel(GenericCategory category) throws ServerException {
		super();
		this.category = category;
		selectionTable = new CategoryToEntitySelectionTable(new CategoryToEntitySelectionTableModel());

		dateNameCheckbox = UIFactory.createCheckBox("checkbox.show.date.name");
		dateNameCheckbox.setSelected(true);
		dateNameCheckbox.addActionListener(new ShowDateNameL());

		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), "image.btn.small.edit", new EditL(), null);

		addButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new"), "image.btn.small.new", new AddL(), null);

		removeButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.remove"), "image.btn.small.delete", new RemoveL(), null);

		initPanel();

		setEnabled(false);
		ComboBoxModel model = EntityModelCacheFactory.getInstance().getGenericEntityComboModel(GenericEntityType.forCategoryType(category.getType()), false);
		addButton.setEnabled(model.getSize() > 0);
		selectionTable.getSelectionModel().addListSelectionListener(new TableSelectionL());
	}

	private void initPanel() throws ServerException {
		setLayout(new BorderLayout(1, 1));
		add(UIFactory.createLabel("label.child.entities"), BorderLayout.NORTH);
		add(new JScrollPane(selectionTable), BorderLayout.WEST);

		JPanel btnPanel = UIFactory.createJPanel(new BorderLayout(1, 1));
		btnPanel.add(addButton, BorderLayout.NORTH);
		btnPanel.add(editButton, BorderLayout.CENTER);
		btnPanel.add(removeButton, BorderLayout.SOUTH);

		add(btnPanel, BorderLayout.EAST);
		populateData();
	}

	private synchronized void populateData() throws ServerException {
		List<CategoryToEntityAssociationData> dataList = EntityModelCacheFactory.getInstance().getCategoryToEntityAssociationsByCategory(category);
		List<GenericEntity> entities = new ArrayList<GenericEntity>();
		for (Iterator<CategoryToEntityAssociationData> i = dataList.iterator(); i.hasNext();) {
			CategoryToEntityAssociationData data = i.next();
			entities.add(data.getEntity());
		}
		lockEntities(entities);

		selectionTable.setDataList(dataList);
		setEnabled(true);
		setEnabledSelectionAwares(false);
		addButton.setEnabled(true);
	}

	/**
	 * Locks all entities in list.
	 * TODO: Gaughan create lock request that contains array of entities to lock
	 * instead of having to lock one at a time.
	 * @param entities
	 * @throws ServerException 
	 */
	private void lockEntities(List<GenericEntity> entities) throws ServerException {
		for (Iterator<GenericEntity> i = entities.iterator(); i.hasNext();) {
			lockEntity(i.next());
		}
	}

	private void lockEntity(GenericEntity entity) throws ServerException {
		if (lockedEntities == null) {
			lockedEntities = new ArrayList<GenericEntity>();
		}
		GenericEntity cachedEntity = EntityModelCacheFactory.getInstance().getGenericEntity(entity.getType(), entity.getID());
		if (!lockedEntities.contains(cachedEntity)) {
			ClientUtil.getCommunicator().lock(cachedEntity.getID(), cachedEntity.getType());
			lockedEntities.add(cachedEntity);
		}
	}

	public void setEnabled(boolean enabled) {
		removeButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
	}

	private void setEnabledSelectionAwares(boolean enabled) {
		removeButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
	}

	private final class ShowDateNameL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			selectionTable.refresh(dateNameCheckbox.isSelected());
		}
	}

	private class AddL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			if (category != null) {
				CategoryToEntityAssociationData data = CategoryToEntityEditDialog.newAssociationData(category);
				while (data != null && !isValid(null, data)) {
					data = CategoryToEntityEditDialog.editAssociationData(category, data);
				}

				if (data != null) {
					try {
						lockEntity(data.getEntity());
						selectionTable.getSelectionTableModel().addData(data);
					}
					catch (ServerException e) {
						ClientUtil.getInstance().showErrorDialog("msg.error.failure.lock", new Object[] { data.getEntity().getName(), ClientUtil.getInstance().getErrorMessage(e) });
					}
				}
			}

		}
	}

	private class RemoveL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			if (ClientUtil.getInstance().showConfirmation("msg.question.remove.entity", new Object[] { "entity to category association" })) {
				CategoryToEntityAssociationData data = getSelectedData();
				selectionTable.getSelectionTableModel().removeData(data);
			}
		}
	}

	private class EditL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			CategoryToEntityAssociationData oldData = getSelectedData();
			int row = selectionTable.getSelectedRow();
			if (oldData != null) {
				CategoryToEntityAssociationData newData = CategoryToEntityEditDialog.editAssociationData(category, oldData);
				while (newData != null && !isValid(oldData, newData)) {
					newData = CategoryToEntityEditDialog.editAssociationData(category, newData);
				}
				if (newData != null) {
					try {
						lockEntity(newData.getEntity());
						oldData.setEntity(newData.getEntity());
						oldData.setMutableTimedAssociationKey(newData.getAssociationKey());
						selectionTable.updateRow(row);
					}
					catch (ServerException e) {
						ClientUtil.getInstance().showErrorDialog("msg.error.failure.lock", new Object[] { newData.getEntity().getName(), ClientUtil.getInstance().getErrorMessage(e) });
					}
				}

			}
		}
	}

	private class TableSelectionL implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent arg0) {
			setEnabledSelectionAwares(selectionTable.getSelectedRow() > -1);
		}
	}

	public List<MutableTimedAssociationKey> getEntityAssociations(int entityID) {
		List<MutableTimedAssociationKey> results = new ArrayList<MutableTimedAssociationKey>();
		if (selectionTable.getSelectionTableModel().getDataList() != null) {
			for (CategoryToEntityAssociationData data : selectionTable.getSelectionTableModel().getDataList()) {
				if (data.getEntity().getID() == entityID) {
					results.add(data.getAssociationKey());
				}
			}
		}
		return results;
	}

	private boolean isValid(CategoryToEntityAssociationData oldData, CategoryToEntityAssociationData newData) {
		if (newData.getAssociationKey().getEffectiveDate() != null && newData.getAssociationKey().getExpirationDate() != null
				&& newData.getAssociationKey().getEffectiveDate().after(newData.getAssociationKey().getExpirationDate())) {
			ClientUtil.getInstance().showErrorDialog("InvalidActivationDateRangeMsg");
			return false;
		}
		else {
			for (CategoryToEntityAssociationData listData : selectionTable.getSelectionTableModel().getDataList()) {
				if ((oldData == null || oldData != listData) && newData.getEntity().equals(listData.getEntity()) && newData.getAssociationKey().overlapsWith(listData.getAssociationKey())) {
					ClientUtil.getInstance().showErrorDialog("EntityToCategoryRelationshipOverlaps");
					return false;
				}
			}
		}
		return true;
	}

	private CategoryToEntityAssociationData getSelectedData() {
		return selectionTable.getSelectedDataObject();
	}

	List<GenericEntity> getLockedEntities() {
		return lockedEntities;
	}

	CategoryToEntitySelectionTable getSelectionTable() {
		return selectionTable;
	}

}