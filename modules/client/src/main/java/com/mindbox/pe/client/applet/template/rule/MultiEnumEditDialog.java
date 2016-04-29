package com.mindbox.pe.client.applet.template.rule;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.EnumValueCellRenderer;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
class MultiEnumEditDialog extends JPanel {

	private static final long serialVersionUID = -3951228734910107454L;

	public static <E> String editEnums(JDialog parent, List<EnumValue> enumValueList, String enumStr) {
		MultiEnumEditDialog dialog = new MultiEnumEditDialog(enumValueList);

		if (enumStr != null) {
			dialog.populateSelectedValues(enumStr);
		}
		int option = JOptionPane.showConfirmDialog(parent, dialog, "Value Selector", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if (option == JOptionPane.OK_OPTION) {
			return dialog.extractSelectedValues();
		}
		else {
			return null;
		}
	}

	public static String newEnums(JDialog parent, List<EnumValue> enumValueList) {
		return editEnums(parent, enumValueList, null);
	}

	private final DefaultListModel<EnumValue> listModel;
	private final JList<EnumValue> jlist;
	private final JCheckBox excludeCheckBox;

	private MultiEnumEditDialog(List<EnumValue> enumValueList) {
		this.listModel = new DefaultListModel<EnumValue>();
		this.excludeCheckBox = UIFactory.createCheckBox("checkbox.exclude.enum");

		if (enumValueList != null && enumValueList.size() > 0) {
			for (EnumValue enumValue : enumValueList) {
				if (enumValue.isActive()) {
					listModel.addElement(enumValue);
				}
			}
		}
		setLayout(new BorderLayout(4, 4));

		this.jlist = new JList<EnumValue>();
		jlist.setCellRenderer(new EnumValueCellRenderer());
		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlist.setModel(listModel);

		this.add(new JLabel("Select one or more values"), BorderLayout.NORTH);
		this.add(new JScrollPane(jlist), BorderLayout.CENTER);
		this.add(excludeCheckBox, BorderLayout.SOUTH);
	}

	private String extractSelectedValues() {
		StringBuilder buff = new StringBuilder();
		List<EnumValue> selections = jlist.getSelectedValuesList();
		if (selections.isEmpty()) {
			return "";
		}
		else {
			if (excludeCheckBox.isSelected()) {
				buff.append("Not ");
			}
			boolean first = true;
			for (EnumValue selection : selections) {
				if (!first) {
					buff.append(",");
				}
				buff.append(selection.getDisplayLabel());
				if (first) {
					first = false;
				}
			}
		}
		return buff.toString();
	}

	private void populateSelectedValues(String valueStr) {
		EnumValues<?> enumValues = EnumValues.parseValue(valueStr, true, null);
		excludeCheckBox.setSelected(enumValues.isSelectionExclusion());

		List<Integer> indexList = new LinkedList<Integer>();
		for (int i = 0; i < enumValues.size(); i++) {
			int index = -1;
			for (int j = 0; j < listModel.size(); j++) {
				EnumValue ev = listModel.get(j);
				if (ev.getDisplayLabel().equals(enumValues.get(i)) || ev.getDeployID().toString().equals(enumValues.get(i))) {
					index = j;
					break;
				}
			}
			if (index >= 0) {
				indexList.add(new Integer(index));
			}
		}

		int[] indices = new int[indexList.size()];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = indexList.get(i).intValue();
		}
		jlist.setSelectedIndices(indices);
	}
}
