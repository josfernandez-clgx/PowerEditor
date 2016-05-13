package com.mindbox.pe.client.common.table;

import java.util.LinkedList;
import java.util.List;

import javax.swing.ListSelectionModel;

import com.mindbox.pe.common.ui.AbstractSortableTable;
import com.mindbox.pe.model.IDNameObject;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class IDNameObjectSelectionTable<M extends IDNameObjectSelectionTableModel<D>, D extends IDNameObject> extends AbstractSortableTable<M, D> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public IDNameObjectSelectionTable(M tableModel, boolean canSelectMultiple) {
		super(tableModel);
		setRowSelectionAllowed(true);
		setAutoResizeMode(2);
		setSelectionMode((canSelectMultiple ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION));
	}

	@Override
	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		int colCount = columnNames.length;
		if (colCount > 0) {
			getColumnModel().getColumn(0).setPreferredWidth(200);
		}
	}

	@SuppressWarnings("unchecked")
	public final int getIndexOfIDNameObjectInView(int entityID) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			D value = (D) getModel().getValueAt(i, -1);
			if (entityID == value.getID()) {
				return convertRowIndexToView(i);
			}
		}
		return -1;
	}

	public final List<Integer> getSelectedObjectIDs() {
		List<Integer> list = new LinkedList<Integer>();
		List<D> selectedData = getSelectedDataObjects();
		if (selectedData != null) {
			for (D data : selectedData) {
				list.add(data.getID());
			}
		}
		return list;
	}

	public final void selectIDNameObject(int entityID) {
		int rowID = getIndexOfIDNameObjectInView(entityID);
		if (rowID != -1) {
			selectOneRow(rowID);
		}
	}

}
