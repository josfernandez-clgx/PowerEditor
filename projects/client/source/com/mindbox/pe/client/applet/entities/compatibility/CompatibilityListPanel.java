package com.mindbox.pe.client.applet.entities.compatibility;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

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
import com.mindbox.pe.client.common.dialog.GenericEntityCompatibilityEditDialog;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class CompatibilityListPanel extends PanelBase {

	private class AddL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			if (prevType1 != null && prevType2 != null) {
				GenericEntityCompatibilityData data = GenericEntityCompatibilityEditDialog.newCompatibilityData(prevType1, prevType2);
				if (data != null) {
					String type1CellVal = EntityModelCacheFactory.getInstance().getGenericEntityName(
							data.getSourceType(),
							data.getSourceID(),
							null);
					String type2CellVal = EntityModelCacheFactory.getInstance().getGenericEntityName(
							data.getGenericEntityType(),
							data.getAssociableID(),
							null);

					int matchingRow = matchingRowInView(type1CellVal, type2CellVal);
					if (matchingRow > -1) {
						if (ClientUtil.getInstance().showConfirmation("msg.question.update.existing.compatibility")) {
							refresh();
							selectionTable.getSelectionModel().setSelectionInterval(matchingRow, matchingRow);
						}
					}
					else {
						selectionTableModel.addData(data);
					}
				}
			}
		}
	}

	private class RemoveL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			GenericEntityCompatibilityData data = getSelectedCompatibilityData();
			if (data != null && ClientUtil.getInstance().showConfirmation("msg.question.delete.compatibility")) {
				ClientUtil.getCommunicator().delete(data);
				selectionTableModel.removeData(data);
			}
		}
	}

	private class EditL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			GenericEntityCompatibilityData data = getSelectedCompatibilityData();
			int row = selectionTable.getSelectedRow();
			if (data != null) {
				GenericEntityCompatibilityData newData = GenericEntityCompatibilityEditDialog.editCompatibilityData(data);
				if (newData != null) {
					selectionTable.updateRow(row);
				}
			}
		}
	}

	private class TableSelectionL implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent arg0) {
			setEnabledSelectionAwares(selectionTable.getSelectedRow() > -1);
		}
	}

	private final CompatibilitySelectionTable selectionTable;
	private final CompatibilitySelectionTableModel selectionTableModel;
	private final JButton addButton, editButton, removeButton;
	private GenericEntityType prevType1, prevType2 = null;
	private final JCheckBox dateNameCheckbox;
	private final boolean readOnly;

	/**
	 *  
	 */
	public CompatibilityListPanel(boolean readOnly) {
		super();
		this.readOnly = readOnly;
		selectionTableModel = new CompatibilitySelectionTableModel(null, null);
		selectionTable = new CompatibilitySelectionTable(selectionTableModel);

		dateNameCheckbox = UIFactory.createCheckBox("checkbox.show.date.name");
		dateNameCheckbox.setSelected(true);
		dateNameCheckbox.addActionListener(new ShowDateNameL());

		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), "image.btn.small.edit", new EditL(), null);

		addButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new"), "image.btn.small.new", new AddL(), null);

		removeButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.remove"),
				"image.btn.small.delete",
				new RemoveL(),
				null);

		initPanel();

		setEnabled(false);
		selectionTable.getSelectionModel().addListSelectionListener(new TableSelectionL());
	}

	private GenericEntityCompatibilityData getSelectedCompatibilityData() {
		return selectionTable.getSelectedDataObject();
	}

	private int matchingRowInView(String type1CellVal, String type2CellVal) {
		for (int row = 0; row < selectionTable.getRowCount(); row++) {
			String col1Val = (String) selectionTableModel.getValueAt(row, 0);
			String col2Val = (String) selectionTableModel.getValueAt(row, 1);
			if ((UtilBase.nullSafeEquals(type1CellVal, col1Val) && UtilBase.nullSafeEquals(type2CellVal, col2Val))
					|| (UtilBase.nullSafeEquals(type1CellVal, col2Val) && UtilBase.nullSafeEquals(type2CellVal, col1Val))) {
				return selectionTable.convertRowIndexToView(row);
			}
		}
		return -1;
	}

	private void initPanel() {
		JPanel btnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
		if (!readOnly) {
			btnPanel.add(addButton);
			btnPanel.add(editButton);
			btnPanel.add(removeButton);
		}
		// @@@ add checkbox somewhere
		btnPanel.add(dateNameCheckbox);

		setLayout(new BorderLayout(1, 1));
		add(btnPanel, BorderLayout.NORTH);
		add(new JScrollPane(selectionTable), BorderLayout.CENTER);

		setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.entity.compatibility")));
	}

	private void refresh() throws Exception {
		List<GenericEntityCompatibilityData> list = ClientUtil.getCommunicator().fetchCompatibilityData(prevType1, prevType2);
		selectionTable.setData(prevType1, prevType2, list.toArray(new GenericEntityCompatibilityData[0]));
	}

	public synchronized void populateData(GenericEntityType type1, GenericEntityType type2, GenericEntityCompatibilityData[] data) {
		ClientUtil.getLogger().info(">>> populateData: " + type1 + "," + type2 + ",data.length=" + data.length);
		prevType1 = type1;
		prevType2 = type2;
		selectionTable.setData(type1, type2, data);

		setEnabled(true);
		setEnabledSelectionAwares(false);
		addButton.setEnabled(true);
	}

	public void setEnabled(boolean enabled) {
		addButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
		//selectionTable.setVisible(enabled);
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

}