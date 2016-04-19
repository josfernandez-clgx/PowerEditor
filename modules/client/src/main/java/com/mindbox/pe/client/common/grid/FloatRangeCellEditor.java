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
import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.common.format.FloatRangeFormatter;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

/**
 * JTable cell editor for float range columns.
 * 
 * @since PowerEditor 1.0
 */
class FloatRangeCellEditor extends AbstractCellEditor {

	private boolean viewOnly;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private FloatRange floatRange;
	private JButton editorComponent;
	private JPanel editPanel;
	private FloatTextField lowerField;
	private FloatTextField upperField;
	private JLabel lowerLimitLabel;
	private JLabel upperLimitLabel;
	private JCheckBox lowerInclCheckBox;
	private JCheckBox upperInclCheckBox;

	public FloatRangeCellEditor(ColumnDataSpecDigest columnDataSpecDigest, boolean forCurrency, boolean flag) {
		editorComponent = null;
		editPanel = UIFactory.createJPanel();
		lowerField = new FloatTextField(10, forCurrency);
		upperField = new FloatTextField(10, forCurrency);
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
						ClientUtil.getApplet(),
						editPanel,
						ClientUtil.getInstance().getLabel("d.title.edit.range"),
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE) == 0) {

					Double min = lowerField.getDoubleValue();
					Double max = upperField.getDoubleValue();

					if (min == null && max == null && !columnDataSpecDigest.isBlankAllowed()) {
						ClientUtil.getInstance().showErrorDialog("msg.warning.invalid.column.null.range.generic");
					}
					else if (CellValidator.validateValue(min, columnDataSpecDigest)
							&& CellValidator.validateValue(max, columnDataSpecDigest)
							&& (min == null || max == null || min.compareTo(max) <= 0)) {
						floatRange = new FloatRange();
						floatRange.setLowerValue((min == null ? null : min));
						floatRange.setUpperValue((max == null ? null : max));
						floatRange.setLowerValueInclusive(lowerInclCheckBox.isSelected());
						floatRange.setUpperValueInclusive(upperInclCheckBox.isSelected());

						flag = true;
					}
					else {
						ClientUtil.getInstance().showErrorDialog("InvalidNumberRangeMsg");
					}
				}
				FloatRange result = (FloatRange) getCellEditorValue();
				FloatRangeFormatter formatter = new FloatRangeFormatter(columnDataSpecDigest.getPrecision());
				editorComponent.setText(formatter.format(result));
			}
		}
		return flag;
	}

	public Component getTableCellEditorComponent(JTable jtable, Object obj, boolean flag, int i, int j) {
		if (obj == null) {
			editorComponent.setText("");
			floatRange = (FloatRange) obj;
			return editorComponent;
		}
		else {
			editorComponent.setText(obj.toString());

			if (obj instanceof FloatRange) {
				floatRange = (FloatRange) obj;
			}
			else {
				floatRange = FloatRange.parseValue(obj.toString());
			}

			if (floatRange.getLowerValue() != null) {
				lowerField.setText(FloatCellEditor.asEditorStringValue(floatRange.getLowerValue()));
			}
			else {
				lowerField.setText("");
			}
			if (floatRange.getUpperValue() != null) {
				upperField.setText(FloatCellEditor.asEditorStringValue(floatRange.getUpperValue()));
			}
			else {
				upperField.setText("");
			}
			lowerInclCheckBox.setSelected(floatRange.isLowerValueInclusive());
			upperInclCheckBox.setSelected(floatRange.isUpperValueInclusive());
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
		JPanel jpanel = UIFactory.createJPanel();
		jpanel.setLayout(new BorderLayout());
		JPanel jpanel1 = UIFactory.createJPanel();
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
		return floatRange;
	}

	private void initEditor() {
		if (editorComponent == null) createEditor();
		if (columnDataSpecDigest.hasMinValue()) {
			double f = columnDataSpecDigest.getMinAsDouble();
			lowerLimitLabel.setText("(> " + f + ")");
		}
		else {
			lowerLimitLabel.setText("");
		}
		if (columnDataSpecDigest.hasMaxValue()) {
			double f = columnDataSpecDigest.getMaxAsDouble();
			upperLimitLabel.setText("(< " + f + ")");
		}
		else {
			upperLimitLabel.setText("");
		}
	}
}
