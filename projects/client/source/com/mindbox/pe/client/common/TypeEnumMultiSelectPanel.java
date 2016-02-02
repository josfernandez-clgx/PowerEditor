package com.mindbox.pe.client.common;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TypeEnumValue;

public class TypeEnumMultiSelectPanel extends JPanel {

	private final TypeEnumCheckList checkList;
	private final JTextField valueField;
	private final JButton button;
	private final List<String> selectedValues;
	private final boolean required;
	private final String propertyName;
	private JDialog dialog;

	public TypeEnumMultiSelectPanel(String propertyName, ComboBoxModel model, boolean required) {
		this.required = required;
		this.propertyName = propertyName;
		selectedValues = new ArrayList<String>();
		checkList = new TypeEnumCheckList(model);
		valueField = new JTextField();
		valueField.setEditable(false);
		button = UIFactory.createButton("...", null, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editValue();
			}
		}, null);
		initPanel();
	}

	public void addDocumentListener(DocumentListener listener) {
		valueField.getDocument().addDocumentListener(listener);
	}

	public void removeDocumentListener(DocumentListener listener) {
		valueField.getDocument().removeDocumentListener(listener);
	}

	public synchronized List<String> getSelectedValues() {
		return Collections.unmodifiableList(selectedValues);
	}

	public synchronized void setSelectedValues(List<String> values) {
		resetSelectedValues(values);
	}

	public synchronized boolean hasSelectedValue() {
		return !selectedValues.isEmpty();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		button.setEnabled(enabled);
		valueField.setEnabled(enabled);
	}

	private void initPanel() {
		setLayout(new BorderLayout(1, 1));
		add(valueField, BorderLayout.CENTER);
		add(button, BorderLayout.EAST);
		refreshValueField();
	}

	private void resetSelectedValues(List<String> values) {
		selectedValues.clear();
		if (values != null) selectedValues.addAll(values);
		refreshValueField();
	}

	private void refreshValueField() {
		valueField.setText(UtilBase.toString(selectedValues));
	}

	private synchronized void editValue() {
		checkList.selectTypeEnumValues(selectedValues);
		getEditDialog().setVisible(true);
	}

	private JDialog getEditDialog() {
		if (dialog == null) {
			dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
			dialog.setTitle("Enumeration Value Selector");

			JPanel buttonPanel = UIFactory.createFlowLayoutPanelCenterAlignment(2, 2);
			buttonPanel.add(UIFactory.createButton("Accept", null, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					List<TypeEnumValue> enumValues = checkList.getSelectedTypeEnumValues();
					if (required && enumValues.isEmpty()) {
						ClientUtil.getInstance().showWarning("msg.warning.empty.field.enum");
					}
					else {
						List<String> strValues = new ArrayList<String>();
						for (TypeEnumValue enumValue : enumValues) {
							strValues.add(enumValue.getValue());
						}
						resetSelectedValues(strValues);
						dialog.setVisible(false);
					}
				}
			}, null));
			buttonPanel.add(UIFactory.createButton("Cancel", null, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					dialog.setVisible(false);
				}
			}, null));

			JPanel panel = UIFactory.createBorderLayoutPanel(4, 4);
			panel.setBorder(BorderFactory.createEtchedBorder());
			panel.add(new JLabel("Select values for " + propertyName + ":"), BorderLayout.NORTH);
			panel.add(new JScrollPane(checkList), BorderLayout.CENTER);
			panel.add(buttonPanel, BorderLayout.SOUTH);

			dialog.getContentPane().setLayout(new BorderLayout(1, 1));
			dialog.getContentPane().add(panel, BorderLayout.CENTER);
			dialog.setSize(360, 400);
			UIFactory.centerize(dialog);
		}
		return dialog;
	}
}
