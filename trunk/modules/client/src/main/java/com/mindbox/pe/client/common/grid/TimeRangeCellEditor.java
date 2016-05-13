package com.mindbox.pe.client.common.grid;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.client.common.formatter.FormatterFactory;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.table.TimeRange;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

/**
 *
 * @since PowerEditor 3.3.0p5
 */
class TimeRangeCellEditor extends AbstractCellEditor {

	private boolean viewOnly;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private TimeRange mRange;
	private JButton editorButton;
	private JPanel editPanel;
	private JFormattedTextField lowerField;
	private JFormattedTextField upperField;
	private JCheckBox lowerInclCheckBox;
	private JCheckBox upperInclCheckBox;

	public TimeRangeCellEditor(ColumnDataSpecDigest columnDataSpecDigest, boolean flag) {
		editorButton = null;
		editPanel = new JPanel();

		lowerField = new JFormattedTextField(FormatterFactory.getTimeFormatter());
		upperField = new JFormattedTextField(FormatterFactory.getTimeFormatter());

		this.columnDataSpecDigest = columnDataSpecDigest;
		this.viewOnly = flag;
		initEditor();
		setClickCountToStart(2);
	}

	private void createEditor() {
		editorButton = new JButton();
		editorButton.addMouseListener(new EditingStoppingSingleMouseClickListener(this));

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
		editPanel.add(jpanel);
		editPanel.add(Box.createHorizontalStrut(32));
		editPanel.add(new JLabel(s1));
		jpanel1.add(upperInclCheckBox, "North");
		jpanel1.add(upperField, "Center");
		editPanel.add(jpanel1);
		editPanel.setBorder(UIFactory.createTitledBorder("Edit Time Range"));
	}

	public Object getCellEditorValue() {
		String value = (mRange != null && mRange.toString().length() > 0) ? TimeRange.getRangeValueString(mRange) : "";
		return value;
	}

	public Component getTableCellEditorComponent(JTable jtable, Object obj, boolean flag, int i, int j) {
		if (obj == null) {
			editorButton.setText("");
			lowerField.setText("");
			upperField.setText("");
			mRange = (TimeRange) null;
			return editorButton;
		}
		else {
			editorButton.setText(obj.toString());
			mRange = (obj instanceof TimeRange ? (TimeRange) obj : TimeRange.parseTimeRangeValue(obj.toString()));
			if (mRange.getLowerValue() != null) {
				lowerField.setText(TimeRange.toTimeString(mRange.getLowerValue()));
			}
			else {
				lowerField.setText(null);
			}
			if (mRange.getUpperValue() != null) {
				upperField.setText(TimeRange.toTimeString(mRange.getUpperValue()));
			}
			else {
				upperField.setText(null);
			}
			lowerInclCheckBox.setSelected(mRange.isLowerValueInclusive());
			upperInclCheckBox.setSelected(mRange.isUpperValueInclusive());
			return editorButton;
		}
	}

	private void initEditor() {
		if (editorButton == null) createEditor();
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	public boolean shouldSelectCell(EventObject eventobject) {
		boolean flag = super.shouldSelectCell(eventobject);
		if (eventobject instanceof MouseEvent) {
			MouseEvent mouseevent = (MouseEvent) eventobject;
			if (mouseevent.getClickCount() > 1) {
				if (JOptionPane.showConfirmDialog(ClientUtil.getApplet(), editPanel, "Edit Time Range", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
					try {
						Integer min = TimeRange.toTimeInteger(lowerField.getText());
						Integer max = TimeRange.toTimeInteger(upperField.getText());
						if (min == null && max == null && !columnDataSpecDigest.isBlankAllowed()) {
							JOptionPane.showMessageDialog(
									ClientUtil.getApplet(),
									ClientUtil.getInstance().getMessage("msg.warning.invalid.column.null.range.generic"),
									ClientUtil.getInstance().getMessage("ErrorMsgTitle"),
									0);
						}
						else if (min == null || max == null || min.intValue() <= max.intValue()) {
							mRange = new TimeRange();
							mRange.setLowerValue((min == null ? null : min));
							mRange.setUpperValue((max == null ? null : max));
							mRange.setLowerValueInclusive(lowerInclCheckBox.isSelected());
							mRange.setUpperValueInclusive(upperInclCheckBox.isSelected());

							flag = true;
						}
						else {
							JOptionPane.showMessageDialog(ClientUtil.getApplet(), ClientUtil.getInstance().getMessage("InvalidNumberRangeMsg"), ClientUtil.getInstance().getMessage("ErrorMsgTitle"), 0);
						}
					}
					catch (InvalidDataException ex) {
						JOptionPane.showMessageDialog(ClientUtil.getApplet(), "Invalid time data. Specify time in hh:mm:ss format", ClientUtil.getInstance().getMessage("ErrorMsgTitle"), 0);
					}
				}
				editorButton.setText((String) getCellEditorValue());
			}
			else {
				return true;
			}
		}
		return flag;
	}
}
