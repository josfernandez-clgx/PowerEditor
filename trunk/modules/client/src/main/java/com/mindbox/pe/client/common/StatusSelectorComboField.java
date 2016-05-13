/*
 * Created on 2007. 8. 31.
 *
 */
package com.mindbox.pe.client.common;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.mindbox.pe.client.applet.UIFactory;


/**
 * Status selector field.
 * @author Quy Nguyen
 * @since PowerEditor 5.4.0
 */
public class StatusSelectorComboField extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;
	private final JButton deleteButton;
	private final TypeEnumValueComboBox ssComboBox;

	public StatusSelectorComboField() {
		this(false);
	}

	public StatusSelectorComboField(boolean hasEmpty) {
		UIFactory.setLookAndFeel(this);
		ssComboBox = UIFactory.createStatusComboBox(hasEmpty);
		deleteButton = UIFactory.createButton(null, "image.btn.small.remove", this, "button.tooltip.clear.value", false);

		this.initPanel();
	}

	private void initPanel() {
		JPanel rightPanel = UIFactory.createFlowLayoutPanel(FlowLayout.RIGHT, 1, 1);
		rightPanel.add(deleteButton);

		setLayout(new BorderLayout(0, 0));
		add(ssComboBox, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.EAST);
	}

	public final void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == deleteButton) {
			ssComboBox.setSelectedItem(null);
		}
	}

	public void addActionListener(ActionListener l) {
		ssComboBox.addActionListener(l);
	}

	public void removeActionListener(ActionListener l) {
		ssComboBox.removeActionListener(l);
	}

	public synchronized String getSelectedEnumValueValue() {
		return ssComboBox.getSelectedEnumValueValue();
	}

	public synchronized void setSelectedItem(Object anObject) {
		ssComboBox.setSelectedItem(anObject);
	}

	public synchronized void setSelectedStatus(String status) {
		ssComboBox.selectTypeEnumValue(status);
	}

	public synchronized void clearSelection() {
		ssComboBox.setSelectedItem(null);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
		ssComboBox.setEnabled(enabled);
	}

	public void setVisible(boolean enabled) {
		super.setVisible(enabled);
		deleteButton.setVisible(enabled);
		ssComboBox.setVisible(enabled);
	}

}