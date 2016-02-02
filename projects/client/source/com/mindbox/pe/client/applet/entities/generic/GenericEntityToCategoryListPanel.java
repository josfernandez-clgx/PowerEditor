package com.mindbox.pe.client.applet.entities.generic;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.client.common.event.ValueChangeListener;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;
import com.mindbox.pe.client.common.table.AbstractSortableTable;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

/**
 * Panel holding table of entity to category relationships and corresponding operations.  
 * 
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
public class GenericEntityToCategoryListPanel extends PanelBase {

	final SelectionTable selectionTable;
	private GenericEntity entity;
	final SelectionTableModel selectionTableModel;
	private final JButton newButton;
	private final JButton editButton;
	private final JButton removeButton;
	private final JCheckBox dateNameCheckbox;
	private final List<ValueChangeListener> changeListenerList;

	/**
	 * @throws ServerException
	 * 
	 */
	public GenericEntityToCategoryListPanel(GenericEntityType entityType) {
		super();
		changeListenerList = new ArrayList<ValueChangeListener>();
		selectionTableModel = new SelectionTableModel(entityType.getCategoryType());
		selectionTable = new SelectionTable(selectionTableModel);
		selectionTable.getSelectionModel().addListSelectionListener(new TableSelectionL());

		newButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new"), "image.btn.small.new", new NewL(), null);
		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), "image.btn.small.edit", new EditL(), null);
		removeButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.remove"),
				"image.btn.small.delete",
				new RemoveL(),
				null);

		dateNameCheckbox = UIFactory.createCheckBox("checkbox.show.date.name");
		dateNameCheckbox.setSelected(true);
		dateNameCheckbox.addActionListener(new ShowDateNameL());

		initPanel();
		setEnabled(false);
	}

	private void initPanel() {
		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
		buttonPanel.add(newButton);
		buttonPanel.add(editButton);
		buttonPanel.add(removeButton);

		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;

		c.gridy = 0;
		c.weighty = 0.0;
		UIFactory.addComponent(this, bag, c, buttonPanel);

		c.gridy = 1;
		c.weighty = 0.0;
		UIFactory.addComponent(this, bag, c, dateNameCheckbox);

		c.gridy = 2;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		UIFactory.addComponent(this, bag, c, new JScrollPane(selectionTable));

		populateData();
		setEnabledSelectionAwares(false);
	}

	private synchronized void populateData() {
		List<MutableTimedAssociationKey> data = new ArrayList<MutableTimedAssociationKey>();

		if (entity != null) {
			for (Iterator<MutableTimedAssociationKey> i = entity.getCategoryIterator(); i.hasNext();) {
				MutableTimedAssociationKey key = i.next();
				MutableTimedAssociationKey newkey = new DefaultMutableTimedAssociationKey(
						key.getAssociableID(),
						key.getEffectiveDate(),
						key.getExpirationDate());
				data.add(newkey);
			}
		}

		selectionTable.setData(data);
		setEnabled(true);
	}

	public void setEntity(GenericEntity entity) {
		this.entity = entity;
		populateData();
		setEnabledSelectionAwares(false);
	}

	private void setEnabledSelectionAwares(boolean enabled) {
		newButton.setEnabled(entity != null);
		removeButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
		dateNameCheckbox.setEnabled(entity != null);
	}

	public final void addValueChangeListener(ValueChangeListener cl) {
		synchronized (changeListenerList) {
			if (!changeListenerList.contains(cl)) {
				changeListenerList.add(cl);
			}
		}
	}

	public final void removeValueChangeListener(ValueChangeListener cl) {
		synchronized (changeListenerList) {
			if (changeListenerList.contains(cl)) {
				changeListenerList.remove(cl);
			}
		}
	}

	void notifyValueChanged() {
		if (changeListenerList != null) {
			for (Iterator<ValueChangeListener> i = changeListenerList.iterator(); i.hasNext();) {
				ValueChangeListener listener = i.next();
				listener.valueChanged(new ValueChangeEvent());
			}
		}
	}

	List<MutableTimedAssociationKey> getCategoryAssociations() {
		return selectionTable.getSelectionTableModel().getDataList();
	}

	/**
	 * Table for entity to category relationships.
	 * 
	 * @author MindBox, Inc
	 * @since PowerEditor 5.1.0
	 */
	class SelectionTable extends AbstractSortableTable<SelectionTableModel, MutableTimedAssociationKey> {
		/**
		 * @param tableModel
		 */
		public SelectionTable(SelectionTableModel tableModel) {
			super(tableModel);
			setColumnSelectionAllowed(false);
			setRowSelectionAllowed(true);
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			setShowHorizontalLines(true);
			setAutoCreateColumnsFromModel(false);
		}

		void setData(List<MutableTimedAssociationKey> data) {
			getSelectionTableModel().setDataList(data);
		}
	}

	/**
	 * Model for entity to category association.
	 * 
	 * @since PowerEditor 5.1.0
	 */
	class SelectionTableModel extends AbstractSelectionTableModel<MutableTimedAssociationKey> {

		private final int categoryType;

		public SelectionTableModel(int categoryType) {
			super(
					ClientUtil.getInstance().getLabel("label.category.parent"),
					ClientUtil.getInstance().getLabel("label.date.activation"),
					ClientUtil.getInstance().getLabel("label.date.expiration"));
			this.categoryType = categoryType;
		}

		@Override
		public Object getValueAt(int row, int col) {
			if ((dataList == null) || (dataList.size() < row)) {
				return null;
			}
			MutableTimedAssociationKey value = dataList.get(row);
			GenericCategory category = EntityModelCacheFactory.getInstance().getGenericCategory(categoryType, value.getAssociableID());
			switch (col) {
			case 0:
				return (category == null) ? "" : category.getName();

			case 1:
				return toDisplayString(value.getEffectiveDate());

			case 2:
				return toDisplayString(value.getExpirationDate());

			default:
				return value;
			}
		}
	}

	private final class ShowDateNameL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent e) {
			selectionTable.refresh(dateNameCheckbox.isSelected());
		}
	}

	private class NewL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			GenericEntity entityCopy = new GenericEntity(entity);
			entityCopy.removeAllCategoryAssociations();
			for (Iterator<MutableTimedAssociationKey> i = selectionTableModel.getDataList().iterator(); i.hasNext();) {
				entityCopy.addCategoryAssociation(i.next());
			}
			List<MutableTimedAssociationKey> newkeys = GenericEntityToCategoryBulkAddDialog.getNewCategoryAssociationsForEntity(
					JOptionPane.getFrameForComponent(ClientUtil.getApplet()),
					entityCopy);
			if (newkeys != null) {
				// TT 2081
				selectionTableModel.addDataList(newkeys);
				//selectionTableModel.fireTableDataChanged();
				notifyValueChanged();
			}
		}
	}

	private class EditL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			if (selectionTable.getSelectedRowCount() == 1) {
				MutableTimedAssociationKey key = (MutableTimedAssociationKey) selectionTable.getDateObjectAt(selectionTable.getSelectedRow());
				MutableTimedAssociationKey newkey = GenericEntityToCategoryEditDialog.editParentCategory(entity, key);

				if (newkey != null) {
					// TT 2081
					selectionTableModel.removeData(key);
					selectionTableModel.addData(newkey);
					//selectionTableModel.fireTableDataChanged();
					notifyValueChanged();
				}
			}
		}
	}

	private class RemoveL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			if (selectionTable.getSelectedRowCount() > 0) {
				if (ClientUtil.getInstance().showConfirmation("msg.question.remove.category.association")) {
					int[] rowIndex = selectionTable.getSelectedRows();
					List<MutableTimedAssociationKey> removedKeys = new ArrayList<MutableTimedAssociationKey>();

					for (int i = 0; i < rowIndex.length; i++) {
						MutableTimedAssociationKey key = (MutableTimedAssociationKey) selectionTable.getValueAt(rowIndex[i], -1);
						removedKeys.add(key);
					}

					for (Iterator<MutableTimedAssociationKey> i = removedKeys.iterator(); i.hasNext();) {
						// TT 2081
						selectionTableModel.removeData(i.next());
					}

					//selectionTableModel.fireTableDataChanged();
					notifyValueChanged();
				}
			}
		}
	}

	private class TableSelectionL implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			removeButton.setEnabled(selectionTable.getSelectedRowCount() > 0);
			editButton.setEnabled(selectionTable.getSelectedRowCount() == 1);
		}
	}

	public void setEnabledFields(boolean enabled) {
		selectionTable.setEnabled(enabled);
		dateNameCheckbox.setEnabled(enabled);
		if (enabled) {
			newButton.setEnabled(entity != null);
			removeButton.setEnabled(selectionTable.getSelectedRowCount() > 0);
			editButton.setEnabled(selectionTable.getSelectedRowCount() == 1);
		}
		else {
			newButton.setEnabled(enabled);
			removeButton.setEnabled(enabled);
			editButton.setEnabled(enabled);
		}
	}
}
