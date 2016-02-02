package com.mindbox.pe.client.applet.entities.generic;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.tree.AbstractGenericCategorySelectionTree;
import com.mindbox.pe.client.common.tree.GenericCategorySelectionTree;
import com.mindbox.pe.client.common.tree.GenericCategoryTreeWithCheckBox;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * Dialog used for allowing users to select multiple effective-dated
 * parent category associations from a tree view for an entity.
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
class GenericEntityToCategoryBulkAddDialog extends JPanel {
	
	private JDialog dialog;
	private final AbstractGenericCategorySelectionTree categoriesTreeField;
	private final DateSelectorComboField effDateEntryField;
	private final DateSelectorComboField expDateEntryField;
	private final JTextField entityNameField;
	private final GenericEntity entity;
	private final boolean entityCanBelongToMultipleCategories;
	private final JButton okButton;
	private final JButton cancelButton;

	private List<MutableTimedAssociationKey> keys;

	private GenericEntityToCategoryBulkAddDialog(GenericEntity entity, JDialog dialog) {
		this.dialog = dialog;
		this.entity = entity;
		entityNameField = new JTextField(10);
		entityNameField.setEnabled(false);
		entityNameField.setText(entity.getName());
		effDateEntryField = new DateSelectorComboField(true, true, true);
		expDateEntryField = new DateSelectorComboField(true, true, true);
		okButton = new JButton(ClientUtil.getInstance().getLabel("button.ok"));
		okButton.setEnabled(false);
		cancelButton = new JButton(ClientUtil.getInstance().getLabel("button.cancel"));

		EntityTypeDefinition entityTypeDef = ClientUtil.getEntityConfiguration().findEntityTypeDefinition(entity.getType());
		entityCanBelongToMultipleCategories = entityTypeDef.canBelongToMultipleCategories();

		if (entityCanBelongToMultipleCategories) {
			categoriesTreeField = new GenericCategoryTreeWithCheckBox(entity.getType(), false, true);
		}
		else {
			categoriesTreeField = new GenericCategorySelectionTree(entity.getType().getCategoryType(), false, true, true);
		}

		categoriesTreeField.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				okButton.setEnabled(true);
			}
		});

		initDialog();
		setSize(350, 500);
	}

	public static List<MutableTimedAssociationKey> getNewCategoryAssociationsForEntity(Frame owner, GenericEntity entity) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.new.parent.category"));

		GenericEntityToCategoryBulkAddDialog panel = new GenericEntityToCategoryBulkAddDialog(entity, dialog);
		UIFactory.addToDialog(dialog, panel);
		dialog.setVisible(true);

		return panel.keys;
	}

	private void initDialog() {
		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;

		c.weighty = 0.0;
		c.weightx = 0.0;

		c.gridheight = 1;
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy = 0;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.entity"));

		c.gridx = 0;
		c.gridy = 1;
		UIFactory.addComponent(this, bag, c, entityNameField);

		c.gridx = 0;
		c.gridy = 2;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.date.activation"));

		c.gridx = 0;
		c.gridy = 3;
		UIFactory.addComponent(this, bag, c, effDateEntryField);

		c.gridx = 0;
		c.gridy = 4;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.date.expiration"));

		c.gridx = 0;
		c.gridy = 5;
		UIFactory.addComponent(this, bag, c, expDateEntryField);

		c.gridx = 0;
		c.gridy = 6;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, categoriesTreeField.getJComponent());

		okButton.addActionListener(new AddL());
		cancelButton.addActionListener(new CancelL());

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		c.gridx = 0;
		c.gridy = 7;
		c.weighty = 0.0;
		c.weightx = 0.0;
		UIFactory.addComponent(this, bag, c, buttonPanel);
	}

	private boolean validate(GenericCategory[] categories) {
		if (!entityCanBelongToMultipleCategories && categories.length > 1) {
			ClientUtil.getInstance().showErrorDialog(
					"msg.error.addmultiplecategories.overlaps",
					new Object[] { entity.getType().getName(), entity.getName() });
			return false;
		}
		String messageKey = Validator.validateDateRange(effDateEntryField.getValue(), expDateEntryField.getValue());
		if (messageKey != null) {
			ClientUtil.getInstance().showErrorDialog(messageKey);
			return false;
		}
		for (int i = 0; i < categories.length; i++) {
			MutableTimedAssociationKey newkey = new DefaultMutableTimedAssociationKey(
					categories[i].getId(),
					effDateEntryField.getValue(),
					expDateEntryField.getValue());

			for (Iterator<MutableTimedAssociationKey> iter = entity.getCategoryIterator(); iter.hasNext();) {
				MutableTimedAssociationKey existingKey = iter.next();
				if (newkey.overlapsWith(existingKey)) {
					if (newkey.getAssociableID() == existingKey.getAssociableID()) {
						ClientUtil.getInstance().showErrorDialog(
								"msg.error.entitycannot.overlap.parent",
								new Object[] { entity.getName(), categories[i].getName() });

						return false;
					}
					else if (!entityCanBelongToMultipleCategories) {
						GenericCategory overlappingCategory = EntityModelCacheFactory.getInstance().getGenericCategory(
								entity.getType().getCategoryType(),
								existingKey.getAssociableID());
						ClientUtil.getInstance().showErrorDialog(
								"msg.error.addentitycategory.overlaps",
								new Object[] { entity.getType().getName(), entity.getName(), overlappingCategory.getName() });
						return false;
					}
				}
			}
		}

		return true;
	}

	private class AddL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GenericCategory[] categories = null;
			if (categoriesTreeField instanceof GenericCategoryTreeWithCheckBox) {
				categories = ((GenericCategoryTreeWithCheckBox) categoriesTreeField).getSelectedCategories();
			}
			else {
				categories = ((GenericCategorySelectionTree) categoriesTreeField).getSelectedCategories();
			}

			if (validate(categories)) {
				if ((categories != null) && (categories.length > 0)) {
					keys = new ArrayList<MutableTimedAssociationKey>();

					for (int i = 0; i < categories.length; i++) {
						MutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(
								categories[i].getId(),
								effDateEntryField.getValue(),
								expDateEntryField.getValue());
						keys.add(key);
					}
				}
				else {
					keys = null;
				}

				dialog.dispose();
			}
		}
	}

	private class CancelL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			keys = null;
			dialog.dispose();
		}
	}
}
