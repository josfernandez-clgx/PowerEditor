package com.mindbox.pe.client.applet.datesynonym;

import java.util.Date;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTableModel;
import com.mindbox.pe.model.DateSynonym;

public class DateSynonymTableModel extends IDNameDescriptionObjectSelectionTableModel<DateSynonym> {
	private static final long serialVersionUID = -3951228734910107454L;

	public DateSynonymTableModel() {
		super(ClientUtil.getInstance().getLabel("label.name"), ClientUtil.getInstance().getLabel("label.date"), ClientUtil.getInstance().getLabel("label.desc"));
	}

	@Override
	public Class<?> getColumnClass(int col) {
		if (col == 1) return Date.class;
		return String.class;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row < 0 || dataList.size() <= row) throw new IllegalArgumentException("Invalid row: " + row);

		DateSynonym dateSynonym = dataList.get(row);
		switch (col) {
		case 0:
			return dateSynonym.getName();
		case 1:
			return dateSynonym.getDate();
		case 2:
			return dateSynonym.getDescription();
		}
		return dateSynonym;
	}

}