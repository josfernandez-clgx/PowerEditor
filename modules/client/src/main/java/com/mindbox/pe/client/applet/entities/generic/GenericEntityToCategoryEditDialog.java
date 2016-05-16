/*
 * Created on Jan 8, 2007
 *
 */
package com.mindbox.pe.client.applet.entities.generic;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Generic entity to category relationship dialog.
 * 
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
public class GenericEntityToCategoryEditDialog extends JPanel {
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * Displays edit entity to catgory relationship dialog.
	 * @param entity entity
	 * @param key key
	 * @return key
	 */
	public static MutableTimedAssociationKey editParentCategory(GenericEntity entity, MutableTimedAssociationKey key) {
		GenericEntityToCategoryEditDialog dialog = new GenericEntityToCategoryEditDialog(entity, key);
		dialog.effDateEntryField.requestFocus();

		int option = JOptionPane.showConfirmDialog(
				ClientUtil.getApplet(),
				dialog,
				ClientUtil.getInstance().getLabel("d.title.edit.categorytoentity"),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		while ((option != JOptionPane.CANCEL_OPTION) && (!GenericEntityToCategoryEditDialog.isValid(dialog.effDateEntryField.getValue(), dialog.expDateEntryField.getValue())
				|| (!dialog.entityCanBelongToMultipleCategories && dialog.hasOverlappingParentCategory()))) {
			option = JOptionPane.showConfirmDialog(
					ClientUtil.getApplet(),
					dialog,
					ClientUtil.getInstance().getLabel("d.title.edit.categorytocategory"),
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
		}

		if (option == JOptionPane.CANCEL_OPTION) {
			return null;
		}
		else if (!UtilBase.isSame(key.getEffectiveDate(), dialog.effDateEntryField.getValue()) || !UtilBase.isSame(key.getExpirationDate(), dialog.expDateEntryField.getValue())) {
			MutableTimedAssociationKey newKey = new DefaultMutableTimedAssociationKey(
					dialog.associableID,
					dialog.effDateEntryField.getValue(),
					dialog.expDateEntryField.getValue());

			return newKey;
		}
		else {
			return null;
		}
	}

	private static boolean isValid(DateSynonym effDate, DateSynonym expDate) {
		String messageKey = Validator.validateDateRange(effDate, expDate);
		if (messageKey != null) {
			ClientUtil.getInstance().showErrorDialog(messageKey);
			return false;
		}
		else {
			return true;
		}
	}

	private final DateSelectorComboField effDateEntryField;
	private final DateSelectorComboField expDateEntryField;
	private final JTextField categoryNameField;
	private final GenericEntity entity;

	private final int associableID;

	private final boolean entityCanBelongToMultipleCategories;

	private GenericEntityToCategoryEditDialog(GenericEntity entity, MutableTimedAssociationKey key) {
		super();
		this.entity = entity;
		categoryNameField = new JTextField();
		categoryNameField.setEnabled(false);
		effDateEntryField = new DateSelectorComboField(true, true, true);
		expDateEntryField = new DateSelectorComboField(true, true, true);
		initDialog();

		EntityType entityTypeDef = ClientUtil.getEntityConfigHelper().findEntityTypeDefinition(entity.getType());
		entityCanBelongToMultipleCategories = ConfigUtil.isCanBelongToMultipleCategories(entityTypeDef);

		GenericCategory category = EntityModelCacheFactory.getInstance().getGenericCategory(entity.getType().getCategoryType(), key.getAssociableID());
		categoryNameField.setText(category.getName());
		effDateEntryField.setValue(key.getEffectiveDate());
		expDateEntryField.setValue(key.getExpirationDate());
		associableID = key.getAssociableID();
	}

	private boolean hasOverlappingParentCategory() {
		boolean overlaps = false;
		MutableTimedAssociationKey newkey = new DefaultMutableTimedAssociationKey(associableID, effDateEntryField.getValue(), expDateEntryField.getValue());

		for (Iterator<MutableTimedAssociationKey> i = entity.getCategoryIterator(); i.hasNext();) {
			MutableTimedAssociationKey key = i.next();

			if ((key.getAssociableID() != associableID) && newkey.overlapsWith(key)) {
				GenericCategory parentCat = EntityModelCacheFactory.getInstance().getGenericCategory(entity.getType(), key.getAssociableID());
				ClientUtil.getInstance().showErrorDialog("msg.error.category.overlaps", new Object[] { entity.getType().getName(), entity.getName(), parentCat.getName() });
				overlaps = true;

				break;
			}
		}

		return overlaps;
	}

	private void initDialog() {
		layoutComponents();
	}

	private void layoutComponents() {
		JPanel bPanel = null;

		bPanel = UIFactory.createJPanel(new GridLayout(7, 2, 4, 4));
		bPanel.add(UIFactory.createFormLabel("label.category.parent"));
		bPanel.add(categoryNameField);

		bPanel.add(UIFactory.createFormLabel("label.date.activation"));
		bPanel.add(effDateEntryField);

		bPanel.add(UIFactory.createFormLabel("label.date.expiration"));
		bPanel.add(expDateEntryField);

		setLayout(new BorderLayout(4, 4));
		add(bPanel, BorderLayout.CENTER);
	}
}
