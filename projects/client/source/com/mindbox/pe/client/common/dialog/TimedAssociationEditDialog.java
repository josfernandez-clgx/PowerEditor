/*
 * Created on Jun 10, 2003
 */
package com.mindbox.pe.client.common.dialog;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.IDNameObjectComboBox;
import com.mindbox.pe.model.assckey.TimedAssociationKey;
import com.mindbox.pe.model.assckey.TimedAssociationKeyFactory;

/**
 * Timed association edit dialog.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class TimedAssociationEditDialog extends JPanel {

	/**
	 * Displays new association dialog for the specified parameters.
	 * @param entityName the name of the entity
	 * @param combo combo box with a list of entities to choose from
	 * @param takFactory factory for creating a new entity
	 * @return new timed association key
	 */
	public static TimedAssociationKey newAssociation(
		String entityName,
		IDNameObjectComboBox combo,
		TimedAssociationKeyFactory takFactory) {

		TimedAssociationEditDialog dialog = null;
		dialog = new TimedAssociationEditDialog(entityName, combo);
		dialog.effDateEntryField.requestFocus();
		int option =
			JOptionPane.showConfirmDialog(
				ClientUtil.getApplet(),
				dialog,
				"New " + entityName + " Association",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (option == JOptionPane.CANCEL_OPTION) {
			return null;
		}
		else if (dialog.effDateEntryField.getValue() == null) {
			ClientUtil.getInstance().showErrorMessage("Effective date must be provided.");
			return newAssociation(entityName, combo, takFactory);
		}
		else {
			return takFactory.createInstance(
				combo.getSelectedObjectID(),
				dialog.effDateEntryField.getDate(),
				(dialog.expDateEntryField.getDate() == null ? null : dialog.expDateEntryField.getDate()));
		}
	}

	private final DateSelectorComboField  effDateEntryField;
	private final DateSelectorComboField expDateEntryField;

	private TimedAssociationEditDialog(String entityName, IDNameObjectComboBox combo) {
		super();

		effDateEntryField = new DateSelectorComboField();
		expDateEntryField = new DateSelectorComboField();
		expDateEntryField.setValue(null);
		layoutComponents(entityName, combo);
	}


	private void layoutComponents(String entityName, IDNameObjectComboBox combo) {
		JPanel bPanel = UIFactory.createJPanel(new GridLayout(6, 1, 4, 4));
		bPanel.add(new JLabel("Select " + entityName + ":"));
		bPanel.add(combo);

		bPanel.add(new JLabel(ClientUtil.getInstance().getLabel("label.date.activation") + " (mm/dd/yyyy):"));
		bPanel.add(effDateEntryField);

		bPanel.add(new JLabel(ClientUtil.getInstance().getLabel("label.date.expiration") + " (mm/dd/yyyy):"));
		bPanel.add(expDateEntryField);

		setLayout(new BorderLayout(4, 4));
		add(bPanel, BorderLayout.CENTER);
	}

}
