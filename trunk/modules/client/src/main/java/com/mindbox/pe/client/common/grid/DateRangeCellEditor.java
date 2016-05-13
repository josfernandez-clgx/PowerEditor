package com.mindbox.pe.client.common.grid;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.EventObject;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

class DateRangeCellEditor extends AbstractCellEditor {

	private boolean viewOnly;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private DateRange rangeValue;
	private JButton editorComponent;
	private JPanel editPanel;
	private MDateDateField lowerField;
	private MDateDateField upperField;
	private JLabel lowerLimitLabel;
	private JLabel upperLimitLabel;
	private JCheckBox lowerInclCheckBox;
	private JCheckBox upperInclCheckBox;

	public DateRangeCellEditor(ColumnDataSpecDigest columnDataSpecDigest, boolean flag) {
		editorComponent = null;
		editPanel = new JPanel();
		lowerLimitLabel = new JLabel();
		upperLimitLabel = new JLabel();
		this.columnDataSpecDigest = columnDataSpecDigest;
		viewOnly = flag;
		lowerField = new MDateDateField(false, false, false);
		upperField = new MDateDateField(false, false, false);
		if (columnDataSpecDigest.getMinAsDate() != null) {
			lowerField.setMinimum(columnDataSpecDigest.getMinAsDate());
			upperField.setMinimum(columnDataSpecDigest.getMinAsDate());
		}
		if (columnDataSpecDigest.getMaxAsDate() != null) {
			lowerField.setMaximum(columnDataSpecDigest.getMaxAsDate());
			upperField.setMaximum(columnDataSpecDigest.getMaxAsDate());
		}
		initEditor();
		setClickCountToStart(2);
	}

	public boolean shouldSelectCell(EventObject eventobject) {
		boolean flag = super.shouldSelectCell(eventobject);
		if (eventobject instanceof MouseEvent) {
			MouseEvent mouseevent = (MouseEvent) eventobject;
			if (mouseevent.getClickCount() > 1) {
				if (JOptionPane.showConfirmDialog(ClientUtil.getApplet(), editPanel, ClientUtil.getInstance().getLabel("d.title.edit.range"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
					Date min = null;
					Date max = null;
					min = lowerField.getDate();
					max = upperField.getDate();

					if (min == null && max == null && !columnDataSpecDigest.isBlankAllowed()) {
						ClientUtil.getInstance().showErrorDialog("msg.warning.invalid.column.null.range.generic");
					}
					else if (CellValidator.validateValue(min, max, columnDataSpecDigest) && (min == null || max == null || min.compareTo(max) <= 0)) {
						rangeValue = new DateRange();
						rangeValue.setLowerValue((min == null ? null : min));
						rangeValue.setUpperValue((max == null ? null : max));
						rangeValue.setLowerValueInclusive(lowerInclCheckBox.isSelected());
						rangeValue.setUpperValueInclusive(upperInclCheckBox.isSelected());

						flag = true;
					}
					else {
						ClientUtil.getInstance().showErrorDialog("InvalidNumberRangeMsg");
					}
				}
				editorComponent.setText((String) getCellEditorValue());
			}
			else {
				return true;
			}
		}
		return flag;
	}

	public Component getTableCellEditorComponent(JTable jtable, Object obj, boolean flag, int i, int j) {
		if (obj == null) {
			editorComponent.setText("");
			rangeValue = (DateRange) null;
			return editorComponent;
		}
		else {
			editorComponent.setText(obj.toString());
			rangeValue = (obj instanceof DateRange ? (DateRange) obj : DateRange.parseValue(obj.toString()));
			if (rangeValue.getLowerValue() != null) {
				lowerField.setValue(rangeValue.getLowerValue());
			}
			else {
				lowerField.setValue(lowerField.getMinimum());
			}
			if (rangeValue.getUpperValue() != null) {
				upperField.setValue(rangeValue.getUpperValue());
			}
			else {
				upperField.setValue(upperField.getMaximum());
			}
			lowerInclCheckBox.setSelected(rangeValue.isLowerValueInclusive());
			upperInclCheckBox.setSelected(rangeValue.isUpperValueInclusive());
			return editorComponent;
		}
	}

	private void createEditor() {
		editorComponent = new JButton();
		editorComponent.addMouseListener(new EditingStoppingSingleMouseClickListener(this));

		lowerInclCheckBox = new JCheckBox(ClientUtil.getInstance().getLabel("label.inclusive"), true);
		upperInclCheckBox = new JCheckBox(ClientUtil.getInstance().getLabel("label.inclusive"), true);
		String s = ClientUtil.getInstance().getLabel("label.range.from") + ":";
		String s1 = ClientUtil.getInstance().getLabel("label.range.to") + ":";
		JPanel jpanel = new JPanel();
		jpanel.setLayout(new BorderLayout());
		JPanel jpanel1 = new JPanel();
		jpanel1.setLayout(new BorderLayout());
		editPanel.add(new JLabel(s));
		jpanel.add(lowerInclCheckBox, "North");
		jpanel.add(lowerField, "Center");
		jpanel.add(lowerLimitLabel, "South");
		editPanel.add(jpanel);
		editPanel.add(Box.createHorizontalStrut(32));
		editPanel.add(new JLabel(s1));
		jpanel1.add(upperInclCheckBox, "North");
		jpanel1.add(upperField, "Center");
		jpanel1.add(upperLimitLabel, "South");
		editPanel.add(jpanel1);
		editPanel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("d.title.edit.range")));
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	public Object getCellEditorValue() {
		return rangeValue != null ? rangeValue.toString() : "";
	}

	private void initEditor() {
		if (editorComponent == null) createEditor();
		Date min = columnDataSpecDigest.getMinAsDate();
		Date max = columnDataSpecDigest.getMaxAsDate();
		if (min != null) {
			lowerLimitLabel.setText("(> " + Constants.THREADLOCAL_FORMAT_DATE.get().format(min) + ")");
			lowerField.setValue(min);
		}
		else {
			lowerLimitLabel.setText("");
		}
		if (max != null) {
			upperLimitLabel.setText("(< " + Constants.THREADLOCAL_FORMAT_DATE.get().format(max) + ")");
			upperField.setValue(max);
		}
		else {
			upperLimitLabel.setText("");
		}
	}

}
