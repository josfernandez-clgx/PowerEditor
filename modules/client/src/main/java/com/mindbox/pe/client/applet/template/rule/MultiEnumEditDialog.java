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
	/**
	 * 
	 */
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

	private final DefaultListModel listModel;
	private final JList jlist;
	private final JCheckBox excludeCheckBox;

	private MultiEnumEditDialog(List<EnumValue> enumValueList) {
		this.listModel = new DefaultListModel();
		this.excludeCheckBox = UIFactory.createCheckBox("checkbox.exclude.enum");

		if (enumValueList != null && enumValueList.size() > 0) {
			for (EnumValue enumValue : enumValueList) {
				if (enumValue.isActive()) {
					listModel.addElement(enumValue);
				}
			}
		}
		setLayout(new BorderLayout(4, 4));

		this.jlist = new JList();
		jlist.setCellRenderer(new EnumValueCellRenderer());
		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlist.setModel(listModel);

		this.add(new JLabel("Select one or more values"), BorderLayout.NORTH);
		this.add(new JScrollPane(jlist), BorderLayout.CENTER);
		this.add(excludeCheckBox, BorderLayout.SOUTH);
	}

	private void populateSelectedValues(String valueStr) {
		EnumValues<?> enumValues = EnumValues.parseValue(valueStr, true, null);
		excludeCheckBox.setSelected(enumValues.isSelectionExclusion());

		List<Integer> indexList = new LinkedList<Integer>();
		for (int i = 0; i < enumValues.size(); i++) {
			int index = -1;
			for (int j = 0; j < listModel.size(); j++) {
				EnumValue ev = (EnumValue) listModel.get(j);
				if (ev.getDisplayLabel().equals(enumValues.get(i)) || ev.getDeployID().toString().equals(enumValues.get(i))) {
					index = j;
					break;
				}
			}
			if (index >= 0) indexList.add(new Integer(index));
		}

		int[] indices = new int[indexList.size()];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = indexList.get(i).intValue();
		}
		jlist.setSelectedIndices(indices);
	}

	private String extractSelectedValues() {
		StringBuilder buff = new StringBuilder();
		Object[] objects = jlist.getSelectedValues();
		if (objects == null || objects.length == 0) {
			return "";
		}
		else {
			if (excludeCheckBox.isSelected()) {
				buff.append("Not ");
			}
			buff.append((objects[0] instanceof EnumValue ? ((EnumValue) objects[0]).getDisplayLabel() : objects[0].toString()));
			for (int i = 1; i < objects.length; i++) {
				buff.append(",");
				if (objects[i] instanceof EnumValue) {
					buff.append(((EnumValue) objects[i]).getDisplayLabel());
				}
				else {
					buff.append(objects[i].toString());
				}
			}
		}
		return buff.toString();
	}

}
