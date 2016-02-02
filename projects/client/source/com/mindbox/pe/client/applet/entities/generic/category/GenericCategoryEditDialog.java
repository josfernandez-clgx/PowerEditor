package com.mindbox.pe.client.applet.entities.generic.category;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.dialog.ValidationErrorReportDialog;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.communication.ValidationException;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

/**
 * 
 * @author Geneho
 * @since PowerEditor 3.1.0 but re-worked extensively in 5.1.0
 */
class GenericCategoryEditDialog extends JPanel {
    
	public static GenericCategory editGenericCategory(Frame owner, GenericCategory category) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("Edit Category Properties");

		GenericCategoryEditDialog panel;
		try {
			panel = new GenericCategoryEditDialog(dialog, category);
			UIFactory.addToDialog(dialog, panel);
			dialog.setVisible(true);
			return panel.category;
		}
		catch (ServerException e) {
			ClientUtil.getInstance().showErrorDialog(
					"msg.error.failure.lock",
					new Object[] { category.getName(), ClientUtil.getInstance().getErrorMessage(e) });
			return null;
		}
	}

    private final JTextField nameField;
	private GenericCategory category = null;
	private JDialog dialog;
	private CategoryToEntityListPanel entityAssociationsPanel;
	private CategoryToCategoryListPanel categoryAssociationsPanel;
	private JCheckBox dateNameCheckbox;

	private GenericCategoryEditDialog(JDialog dialog, GenericCategory category) throws ServerException {
		this.dialog = dialog;
		this.category = category;
		nameField = new JTextField(10);
		initDialog();
		populateFields();
		setSize(550, 360);
	}

	private class SaveL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				category.setName(nameField.getText());
				saveEntityAssociations();
				category.removeAllParentAssociations();
				for (Iterator<MutableTimedAssociationKey> i = categoryAssociationsPanel.getParentCategoryAssociations().iterator(); i.hasNext();) {
					category.addParentKey(i.next());
				}
				
				ClientUtil.getCommunicator().save(category, false);
				
				dialog.dispose();
			}
			catch (ValidationException ex) {
				ValidationErrorReportDialog.showErrors(ex, false);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				ClientUtil.handleRuntimeException(ex);
			}

		}
	}

	private class CancelL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			category = null;
			dialog.dispose();
		}
	}

	void setGenericCategory(GenericCategory category) {
		this.category = category;
		if (category == null) {
			clearFields();
		}
		else {
			populateFields();
		}
	}

	private void clearFields() {
		nameField.setText("");
	}

	public void setEnabled(boolean enabled) {
		nameField.setEditable(enabled);
	}

	private void populateFields() {
		if (category != null) {
			nameField.setText(category.getName() == null ? "" : category.getName());
		}
		else {
			nameField.setText("");
		}
	}

	private void initDialog() throws ServerException {
        
        dateNameCheckbox = UIFactory.createCheckBox("checkbox.show.date.name");
        dateNameCheckbox.setSelected(true);
        dateNameCheckbox.addActionListener(new ShowDateNameL());
        
		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.name"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, nameField);

		if (dialog != null) {
            categoryAssociationsPanel = new CategoryToCategoryListPanel(category); 
            PanelBase.addComponent(this, bag, c, new JSeparator());
            UIFactory.addComponent(this, bag, c, dateNameCheckbox);            
            PanelBase.addComponent(this, bag, c, categoryAssociationsPanel);
            
            entityAssociationsPanel = new CategoryToEntityListPanel(category);
            PanelBase.addComponent(this, bag, c, new JSeparator());
            PanelBase.addComponent(this, bag, c, entityAssociationsPanel);
            
			JButton acceptButton = new JButton("Accept");
			acceptButton.addActionListener(new SaveL());
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new CancelL());

			JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
			buttonPanel.add(acceptButton);
			buttonPanel.add(cancelButton);

			PanelBase.addComponent(this, bag, c, new JSeparator());
			c.insets.top = 12;
			c.insets.bottom = 4;
			PanelBase.addComponent(this, bag, c, buttonPanel);
		}
		else {
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			c.gridheight = GridBagConstraints.REMAINDER;
			c.weighty = 1.0;
			UIFactory.addComponent(this, bag, c, Box.createVerticalGlue());
		}
	}

	GenericCategory getGenericCategory() {
		return category;
	}

	private void saveEntityAssociations() throws ServerException {
		List<GenericEntity> entities = entityAssociationsPanel.getLockedEntities();
		if (entities != null) {
			for (Iterator<GenericEntity> i = entities.iterator(); i.hasNext();) {
				GenericEntity entity = i.next();
				entity.removeAllCategoryAssociations(category.getId());
				List<MutableTimedAssociationKey> newKeys = entityAssociationsPanel.getEntityAssociations(entity.getID());
				if (newKeys != null) {
					for (Iterator<MutableTimedAssociationKey> iter = newKeys.iterator(); iter.hasNext();) {
						entity.addCategoryAssociation(iter.next());
					}
				}
				ClientUtil.getCommunicator().save(entity, false, false);
				ClientUtil.getCommunicator().unlock(entity.getID(), entity.getType());
			}
			entities.clear();
		}
	}


	private final class ShowDateNameL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			entityAssociationsPanel.getSelectionTable().refresh(dateNameCheckbox.isSelected());
			categoryAssociationsPanel.getSelectionTable().refresh(dateNameCheckbox.isSelected());
		}
	}

}