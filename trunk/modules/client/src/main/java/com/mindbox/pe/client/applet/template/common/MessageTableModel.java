package com.mindbox.pe.client.applet.template.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.template.TemplateMessageDigest;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor
 */
public final class MessageTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final List<TemplateMessageDigest> valueList;
	private final GenericEntityType entityType;

	public MessageTableModel(GenericEntityType entityType) {
		super();
		this.entityType = entityType;
		this.valueList = new ArrayList<TemplateMessageDigest>();
	}

	public void addRow(TemplateMessageDigest messageDigest) {
		addRow_internal(messageDigest);
		fireTableDataChanged();
	}

	private void addRow_internal(TemplateMessageDigest messageDigest) {
		this.valueList.add(messageDigest);
	}

	public void updateRow(int row) {
		fireTableRowsUpdated(row, row);
	}

	public void setData(List<TemplateMessageDigest> digestList) {
		this.valueList.clear();
		for (Iterator<TemplateMessageDigest> iter = digestList.iterator(); iter.hasNext();) {
			addRow_internal(iter.next());
		}
		fireTableDataChanged();
	}

	public void removeRow(TemplateMessageDigest column) {
		if (valueList.contains(column)) {
			valueList.remove(column);
		}
		fireTableDataChanged();
	}

	public void removeAllRows() {
		this.valueList.clear();
		fireTableDataChanged();
	}

	public int getRowCount() {
		return valueList.size();
	}

	public int getColumnCount() {
		return (entityType == null ? 3 : 4);
	}

	public TemplateMessageDigest getColumnAt(int row) {
		return valueList.get(row);
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return (entityType == null ? ClientUtil.getInstance().getLabel("label.delim.cond") : entityType.getDisplayName());
		case 1:
			return ClientUtil.getInstance().getLabel((entityType == null ? "label.delim.cond.final" : "label.delim.cond"));
		case 2:
			return ClientUtil.getInstance().getLabel((entityType == null ? "label.message.text" : "label.delim.cond.final"));
		case 3:
			return ClientUtil.getInstance().getLabel("label.message.text");
		default:
			return "ERROR";
		}
	}

	public Class<?> getColumnClass(int col) {
		return String.class;
	}

	public Object getValueAt(int row, int col) {
		TemplateMessageDigest digest = valueList.get(row);
		switch (col) {
		case 0: {
			if (entityType == null) {
				return digest.getConditionalDelimiter();
			}
			else {
				GenericEntity entity = EntityModelCacheFactory.getInstance().getGenericEntity(entityType, digest.getEntityID());
				return (entity == null ? "All" : entity.getName());
			}
		}
		case 1: {
			if (entityType == null) {
				return digest.getConditionalFinalDelimiter();
			}
			else {
				return digest.getConditionalDelimiter();
			}
		}
		case 2: {
			if (entityType == null) {
				return digest.getText();
			}
			else {
				return digest.getConditionalFinalDelimiter();
			}
		}
		case 3:
			return digest.getText();
		default:
			return digest;
		}
	}

	public int indexOf(TemplateMessageDigest column) {
		return valueList.indexOf(column);
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
