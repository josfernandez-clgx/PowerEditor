/*
 * Created on Jun 10, 2003
 */
package com.mindbox.pe.client.common.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.NumberTextField;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class EditIntListDialog extends JPanel {

	/**
	 * Displays Edit int array list with the specified title and initial list.
	 * @param title
	 * @param input
	 * @return int array after dialog has disposed
	 */
	public static int[] editIntArray(String title, int[] input) {
		EditIntListDialog dialog = null;
		dialog = new EditIntListDialog(input);
		int option =
			JOptionPane.showConfirmDialog(
				ClientUtil.getApplet(),
				dialog,
				title,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (option == JOptionPane.CANCEL_OPTION) {
			return input;
		}
		else {
			int[] intArray = new int[dialog.listModel.getSize()];
			for (int i = 0; i < intArray.length; i++) {
				intArray[i] = ((Integer) dialog.listModel.get(i)).intValue();
			}
			return intArray;
		}
	}

	private class AddL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Integer value = valueField.getValue();
			if (value != null && listModel.contains(value)) {
				ClientUtil.getInstance().showInformation("msg.warning.duplicate.value");
			}
			else if (value != null) {
				listModel.addElement(value);
				sortValues();
				valueList.clearSelection();
				setEnabledSelectionAwares(false);
			}
			else {
				ClientUtil.getInstance().showInformation("msg.warning.invalid.number");
			}
		}
	}
	private class UpdateL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int index = valueList.getSelectedIndex();
			if (index != -1) {
				Integer value = valueField.getValue();
				if (value != null && listModel.contains(value)) {
					ClientUtil.getInstance().showInformation("msg.warning.duplicate.value");
				}
				else if (value != null) {
					listModel.setElementAt(value, index);
					sortValues();
					valueList.setSelectedValue(value, true);
				}
				else {
					ClientUtil.getInstance().showInformation("msg.warning.invalid.number");
				}
			}
		}
	}
	private class DeleteL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int index = valueList.getSelectedIndex();
			if (index != -1) {
				listModel.removeElementAt(index);
				valueList.clearSelection();
				setEnabledSelectionAwares(false);
			}
		}
	}

	private class SelectionL implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			int index = valueList.getSelectedIndex();
			setEnabledSelectionAwares(index > -1);
			if (index > -1) {
				valueField.setValue(((Integer) listModel.getElementAt(index)).intValue());
			}
		}
	}

	private EditIntListDialog(int[] input) {
		super();
		int[] intArray = (input == null ? new int[0] : input);
		this.valueField = new NumberTextField(0);
		this.addButton = UIFactory.createButton("Add", "image.btn.small.add", new AddL(), null);
		this.editButton = UIFactory.createButton("Update", "image.btn.small.update", new UpdateL(), null);
		this.deleteButton = UIFactory.createButton("Delete", "image.btn.small.delete", new DeleteL(), null);
		this.listModel = new DefaultListModel();
		this.valueList = UIFactory.createList();
		this.valueList.setModel(listModel);
		this.valueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// sort the inputs
		Arrays.sort(intArray);
		for (int i = 0; i < intArray.length; i++) {
			listModel.addElement(new Integer(intArray[i]));
		}
		this.valueList.addListSelectionListener(new SelectionL());

		layoutComponents();
		setEnabledSelectionAwares(false);
	}

	private void sortValues() {
		Object[] values = listModel.toArray();
		Arrays.sort(values);
		listModel.clear();
		for (int i = 0; i < values.length; i++) {
			listModel.addElement(values[i]);
		}
	}

	private void layoutComponents() {
		JPanel bPanel = new JPanel(new GridLayout(3, 1));
		bPanel.add(addButton);
		bPanel.add(editButton);
		bPanel.add(deleteButton);

		JPanel ePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		ePanel.add(bPanel);

		valueList.setPreferredSize(new Dimension(150, 20));
		JPanel cPanel = new JPanel(new BorderLayout(2, 2));
		cPanel.add(valueField, BorderLayout.NORTH);
		cPanel.add(new JScrollPane(valueList), BorderLayout.CENTER);

		setLayout(new BorderLayout(4, 4));
		add(cPanel, BorderLayout.CENTER);
		add(ePanel, BorderLayout.EAST);
	}

	private void setEnabledSelectionAwares(boolean flag) {
		editButton.setEnabled(flag);
		deleteButton.setEnabled(flag);
	}

	//private final String title;
	private final JButton addButton, editButton, deleteButton;
	private final NumberTextField valueField;
	private final JList valueList;
	private final DefaultListModel listModel;
}
