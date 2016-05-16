package com.mindbox.pe.client.common.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.GenericEntityComboBox;
import com.mindbox.pe.communication.ValidationException;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityCompatibilityEditDialog extends JPanel {
	private class AcceptL extends AbstractThreadedActionAdapter {
		@Override
		public void performAction(ActionEvent event) throws Exception {
			updateFromGUI();
			if (GenericEntityCompatibilityEditDialog.this.data != null) {
				try {
					if (forEdit) {
						ClientUtil.getCommunicator().save(data, false);
						dialog.dispose();
					}
					else {
						boolean isExistingCompData = ClientUtil.getCommunicator().isExistingCompatibility(
								GenericEntityCompatibilityEditDialog.this.data.getSourceType(),
								GenericEntityCompatibilityEditDialog.this.data.getSourceID(),
								GenericEntityCompatibilityEditDialog.this.data.getGenericEntityType(),
								GenericEntityCompatibilityEditDialog.this.data.getAssociableID());
						if (!isExistingCompData || ClientUtil.getInstance().showConfirmation("msg.question.update.existing.compatibility")) {
							ClientUtil.getCommunicator().save(data, false);
							dialog.dispose();
						}
					}
				}
				catch (ValidationException ex) {
					ValidationErrorReportDialog.showErrors(ex, false);
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private class CancelL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			GenericEntityCompatibilityEditDialog.this.data = null;
			dialog.dispose();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * Displays edit compatibility dialog.
	 * @param data the compatibility data to edit
	 * @return compatibility data, if dialog is not canceled; <code>null</code>,
	 *         otherwise
	 */
	public static GenericEntityCompatibilityData editCompatibilityData(GenericEntityCompatibilityData data) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.edit.compatibility"));
		GenericEntityCompatibilityEditDialog compatibilityEditDialog = new GenericEntityCompatibilityEditDialog(dialog, data);
		compatibilityEditDialog.type1Combo.requestFocus();
		UIFactory.addToDialog(dialog, compatibilityEditDialog);
		compatibilityEditDialog.type1Combo.requestFocus();
		dialog.setVisible(true);

		return compatibilityEditDialog.data;
	}

	/**
	 * Displays new compatibility dialog.
	 * 
	 * @param type1 type1
	 * @param type2 type2
	 * @return compatibility data, if dialog is not canceled; <code>null</code>,
	 *         otherwise
	 */
	public static GenericEntityCompatibilityData newCompatibilityData(GenericEntityType type1, GenericEntityType type2) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.new.compatibility"));
		GenericEntityCompatibilityEditDialog compatibilityEditDialog = new GenericEntityCompatibilityEditDialog(dialog, type1, type2);

		UIFactory.addToDialog(dialog, compatibilityEditDialog);
		compatibilityEditDialog.type1Combo.requestFocus();
		dialog.setVisible(true);

		return compatibilityEditDialog.data;
	}

	private final DateSelectorComboField effDateEntryField;
	private final DateSelectorComboField expDateEntryField;
	private final GenericEntityComboBox type1Combo, type2Combo;
	private final JDialog dialog;
	private boolean forEdit = false;
	private GenericEntityCompatibilityData data;

	private GenericEntityCompatibilityEditDialog(JDialog dialog, GenericEntityCompatibilityData data) {
		this(dialog, data.getSourceType(), data.getGenericEntityType());
		forEdit = true;
		type1Combo.selectGenericEntity(data.getSourceID());
		type1Combo.setEnabled(false);
		type2Combo.selectGenericEntity(data.getAssociableID());
		type2Combo.setEnabled(false);
		effDateEntryField.setValue(data.getEffectiveDate());
		expDateEntryField.setValue(data.getExpirationDate());
	}

	private GenericEntityCompatibilityEditDialog(JDialog dialog, GenericEntityType type1, GenericEntityType type2) {
		super();
		this.dialog = dialog;
		effDateEntryField = new DateSelectorComboField(true, true, true);
		expDateEntryField = new DateSelectorComboField(true, true, true);

		effDateEntryField.setValue(null);
		expDateEntryField.setValue(null);

		type1Combo = new GenericEntityComboBox(type1, false, null, true);
		type2Combo = new GenericEntityComboBox(type2, false, null, true);

		layoutComponents(type1, type2);
		setSize(400, 250);
	}

	private void layoutComponents(GenericEntityType type1, GenericEntityType type2) {
		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new AcceptL());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		JPanel bPanel = UIFactory.createJPanel(new GridLayout(8, 2, 4, 4));
		bPanel.add(UIFactory.createFormLabel(type1));
		bPanel.add(type1Combo);
		bPanel.add(UIFactory.createFormLabel(type2));
		bPanel.add(type2Combo);

		bPanel.add(UIFactory.createFormLabel("label.date.activation"));
		bPanel.add(effDateEntryField);

		bPanel.add(UIFactory.createFormLabel("label.date.expiration"));
		bPanel.add(expDateEntryField);

		setLayout(new BorderLayout(4, 4));
		add(bPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void updateFromGUI() {
		if (!forEdit || data == null) {
			data = new GenericEntityCompatibilityData(
					type1Combo.getGenericEntityType(),
					type1Combo.getSelectedGenericEntityID(),
					type2Combo.getGenericEntityType(),
					type2Combo.getSelectedGenericEntityID(),
					effDateEntryField.getValue(),
					expDateEntryField.getValue());
		}
		else {
			data.setEffectiveDate(effDateEntryField.getValue());
			data.setExpirationDate(expDateEntryField.getValue());
		}
	}
}