/*
 * Created on 2004. 2. 13.
 *
 */
package com.mindbox.pe.client.applet.template.rule;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.model.table.DateRange;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class DateRangeEditDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static String editRange(JDialog parent, String valueStr) {
		DateRangeEditDialog dialog = new DateRangeEditDialog();

		if (valueStr != null) {
			dialog.populateSelectedValues(valueStr);
		}

		int option = JOptionPane.showConfirmDialog(parent, dialog, "Date Range Editor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if (option == JOptionPane.OK_OPTION) {
			return dialog.extractSelectedValues();
		}
		else {
			return null;
		}
	}

	public static String newRange(JDialog parent) {
		return editRange(parent, null);
	}

	private final MDateDateField lowerField;
	private final MDateDateField upperField;
	private final JCheckBox lowerInclCheckBox;
	private final JCheckBox upperInclCheckBox;
	private DateRange rangeValue = null;

	/**
	 * 
	 */
	private DateRangeEditDialog() {
		lowerInclCheckBox = new JCheckBox(ClientUtil.getInstance().getLabel("label.inclusive"), true);
		upperInclCheckBox = new JCheckBox(ClientUtil.getInstance().getLabel("label.inclusive"), true);
		lowerField = new MDateDateField(true, true);
		upperField = new MDateDateField(true, true);

		String s = ClientUtil.getInstance().getLabel("label.range.from") + ":";
		String s1 = ClientUtil.getInstance().getLabel("label.range.to") + ":";

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets.top = 4;
		c.insets.bottom = 4;
		c.insets.left = 4;
		c.insets.right = 4;

		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(this, bag, c, new JLabel(s));

		c.weightx = 0.5;
		UIFactory.addComponent(this, bag, c, lowerField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(this, bag, c, new JLabel(s1));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.5;
		UIFactory.addComponent(this, bag, c, upperField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(this, bag, c, new JLabel(""));

		c.weightx = 0.5;
		UIFactory.addComponent(this, bag, c, lowerInclCheckBox);

		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(this, bag, c, new JLabel(""));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.5;
		UIFactory.addComponent(this, bag, c, upperInclCheckBox);

	}

	private void populateSelectedValues(String valueStr) {
		rangeValue = DateRange.parseValue(valueStr);
		if (rangeValue.getLowerValue() != null) {
			lowerField.setValue(rangeValue.getLowerValue());
		}
		else {
			lowerField.setValue(null);
		}
		if (rangeValue.getUpperValue() != null) {
			upperField.setValue(rangeValue.getUpperValue());
		}
		else {
			upperField.setValue(null);
		}
		lowerInclCheckBox.setSelected(rangeValue.isLowerValueInclusive());
		upperInclCheckBox.setSelected(rangeValue.isUpperValueInclusive());
	}

	private String extractSelectedValues() {
		if (rangeValue == null) {
			rangeValue = new DateRange();
		}

		Date min = lowerField.getDate();
		Date max = upperField.getDate();
		if (min == null && max == null) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field.range");
			return null;
		}
		else if (min != null && max != null && !min.before(max)) {
			ClientUtil.getInstance().showWarning("msg.warning.invalid.range.minMax");
			return null;
		}

		rangeValue.setLowerValue((min == null ? null : min));
		rangeValue.setUpperValue((max == null ? null : max));
		rangeValue.setLowerValueInclusive(lowerInclCheckBox.isSelected());
		rangeValue.setUpperValueInclusive(upperInclCheckBox.isSelected());
		return rangeValue.toString();
	}

}