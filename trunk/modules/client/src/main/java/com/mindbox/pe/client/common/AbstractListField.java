package com.mindbox.pe.client.common;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import com.mindbox.pe.client.applet.UIFactory;

public abstract class AbstractListField extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private class EditL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			handleEditAction();
		}
	}

	private class DeleteL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			synchronized (AbstractListField.this) {
				textField.setText("");
			}
		}
	}

	private final JTextField textField;

	private final JButton editButton, deleteButton;

	protected final String dialogTitle;

	protected AbstractListField(String dialogTitle) {
		super();
		this.dialogTitle = dialogTitle;
		this.textField = new JTextField();
		textField.setEditable(false);

		editButton = UIFactory.createButton(null, "image.btn.small.edit", new EditL(), "button.tooltip.edit.value");
		deleteButton = UIFactory.createButton(null, "image.btn.small.remove", new DeleteL(), "button.tooltip.clear.value", false);
		initPanel();
	}

	private void initPanel() {
		JPanel rightPanel = UIFactory.createFlowLayoutPanel(FlowLayout.RIGHT, 1, 1);
		rightPanel.add(editButton);
		rightPanel.add(deleteButton);

		setLayout(new BorderLayout(0, 0));
		add(textField, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.EAST);
	}

	public void addDocumentListener(DocumentListener dl) {
		textField.getDocument().addDocumentListener(dl);
	}

	public void removeDocumentListener(DocumentListener dl) {
		textField.getDocument().removeDocumentListener(dl);
	}

	protected abstract void handleEditAction();

	public final synchronized String getStringValue() {
		return textField.getText();
	}

	public final synchronized void setValue(String stringValue) {
		textField.setText(stringValue);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
	}

}
