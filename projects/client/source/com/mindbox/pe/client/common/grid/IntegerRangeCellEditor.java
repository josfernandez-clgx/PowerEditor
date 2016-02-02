package com.mindbox.pe.client.common.grid;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
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
import com.mindbox.pe.client.common.NumberTextField;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.table.IntegerRange;

class IntegerRangeCellEditor extends AbstractCellEditor {


	private boolean viewOnly;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private IntegerRange integerRange;
	private JButton editorComponent;
	private JPanel editPanel;
	private NumberTextField lowerField;
	private NumberTextField upperField;
	private JLabel lowerLimitLabel;
	private JLabel upperLimitLabel;
	private JCheckBox lowerInclCheckBox;
	private JCheckBox upperInclCheckBox;


	public IntegerRangeCellEditor(ColumnDataSpecDigest columnDataSpecDigest, boolean flag) {
		editorComponent = null;
		editPanel = new JPanel();
		lowerField = new NumberTextField(10);
		upperField = new NumberTextField(10);
		lowerLimitLabel = new JLabel();
		upperLimitLabel = new JLabel();
		this.columnDataSpecDigest = columnDataSpecDigest;
		viewOnly = flag;
		initEditor();
		setClickCountToStart(2);
	}


	public boolean shouldSelectCell(EventObject eventobject) {
		boolean flag = super.shouldSelectCell(eventobject);
		if (eventobject instanceof MouseEvent) {
			MouseEvent mouseevent = (MouseEvent) eventobject;
			if (mouseevent.getClickCount() > 1) {
				if (JOptionPane.showConfirmDialog(
						null,
						editPanel,
						ClientUtil.getInstance().getLabel("d.title.edit.range"),
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE) == 0) {
					Integer min = lowerField.getValue();
					Integer max = upperField.getValue();
					if (min == null && max == null && !columnDataSpecDigest.isBlankAllowed()) {
						JOptionPane.showMessageDialog(ClientUtil.getApplet(), ClientUtil.getInstance().getMessage(
								"msg.warning.invalid.column.null.range.generic"), ClientUtil.getInstance().getMessage("ErrorMsgTitle"), 0);
					}
					else if (CellValidator.validateValue(min, columnDataSpecDigest)
							&& CellValidator.validateValue(max, columnDataSpecDigest)
							&& (min == null || max == null || min.intValue() <= max.intValue())) {
						integerRange = new IntegerRange();
						integerRange.setLowerValue((min == null ? null : min));
						integerRange.setUpperValue((max == null ? null : max));
						integerRange.setLowerValueInclusive(lowerInclCheckBox.isSelected());
						integerRange.setUpperValueInclusive(upperInclCheckBox.isSelected());

						flag = true;
					}
					else {
						JOptionPane.showMessageDialog(
								null,
								ClientUtil.getInstance().getMessage("InvalidNumberRangeMsg"),
								ClientUtil.getInstance().getMessage("ErrorMsgTitle"),
								0);
					}
					String strValue = (String) getCellEditorValue();
					editorComponent.setText(strValue);
				}
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
			integerRange = (IntegerRange) obj;
			return editorComponent;
		}
		else {
			editorComponent.setText(obj.toString());
			if (obj instanceof IntegerRange) {
				integerRange = (IntegerRange) obj;
			}
			else {
				integerRange = IntegerRange.parseValue(obj.toString());
			}
			if (integerRange.getLowerValue() != null) {
				lowerField.setValue(integerRange.getLowerValue().intValue());
			}
			else {
				lowerField.setText("");
			}
			if (integerRange.getUpperValue() != null) {
				upperField.setValue(integerRange.getUpperValue().intValue());
			}
			else {
				upperField.setText("");
			}
			lowerInclCheckBox.setSelected(integerRange.isLowerValueInclusive());
			upperInclCheckBox.setSelected(integerRange.isUpperValueInclusive());
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
		return integerRange != null ? integerRange.toString() : "";
	}

	private void initEditor() {
		if (editorComponent == null) createEditor();
		if (columnDataSpecDigest.hasMinValue()) {
			int min = (int) columnDataSpecDigest.getMinAsLong();
			lowerLimitLabel.setText("(> " + min + ")");
			lowerField.setValue(min);
		}
		else {
			lowerLimitLabel.setText("");
		}
		if (columnDataSpecDigest.hasMaxValue()) {
			int max = (int) columnDataSpecDigest.getMaxAsLong();
			upperLimitLabel.setText("(< " + max + ")");
			upperField.setValue(max);
		}
		else {
			upperLimitLabel.setText("");
		}
	}
}
