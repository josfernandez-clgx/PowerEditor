/*
 * Created on Dec 11, 2006
 *
 */
package com.mindbox.pe.client.applet.entities.generic.category;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.GenericCategorySelectField;
import com.mindbox.pe.common.MutableBoolean;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;


/**
 * Category to category relationship dialog.
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
public class CategoryToCategoryEditDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;
	private final DateSelectorComboField effDateEntryField;
	private final DateSelectorComboField expDateEntryField;
	private final GenericCategorySelectField parentCatField;
	private final GenericCategory childCategory;
	private final boolean addMode;
	private JCheckBox autoAdjustCheckBox;

	private CategoryToCategoryEditDialog(GenericCategory childCategory, MutableTimedAssociationKey key, boolean addMode) {
		super();
		this.addMode = addMode;
		this.childCategory = childCategory;
		effDateEntryField = new DateSelectorComboField(true, true, true);
		expDateEntryField = new DateSelectorComboField(true, true, true);
		parentCatField = new GenericCategorySelectField(childCategory.getType(), false);
		parentCatField.setAllowDelete(false);

		initDialog();

		if (key != null) {
			GenericCategory parentCategory = EntityModelCacheFactory.getInstance().getGenericCategory(childCategory.getType(), key.getAssociableID());
			parentCatField.setValue(parentCategory);
			effDateEntryField.setValue(key.getEffectiveDate());
			expDateEntryField.setValue(key.getExpirationDate());
		}
	}

	/**
	 * Displays new category to catgory relationship dialog. Used to change the parent
	 * category.
	 */
	/**
	 * @param childCategory
	 * @param autoExpire Mutable boolean object that is set by this method so the caller
	 * knows if the auto expire option has been selected.
	 * @param data
	 * @return new relationship key.
	 */
	public static MutableTimedAssociationKey newParentCategory(GenericCategory childCategory, MutableBoolean autoExpire, MutableTimedAssociationKey data) {
		CategoryToCategoryEditDialog dialog = new CategoryToCategoryEditDialog(childCategory, data, true);
		dialog.parentCatField.requestFocus();

		int option = JOptionPane.showConfirmDialog(
				ClientUtil.getApplet(),
				dialog,
				ClientUtil.getInstance().getLabel("d.title.new.categorytocategory"),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		while ((option != JOptionPane.CANCEL_OPTION) && !dialog.isValid(dialog.effDateEntryField.getValue(), dialog.expDateEntryField.getValue(), null)) {
			option = JOptionPane.showConfirmDialog(
					ClientUtil.getApplet(),
					dialog,
					ClientUtil.getInstance().getLabel("d.title.new.categorytocategory"),
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
		}

		if (option == JOptionPane.CANCEL_OPTION) {
			return null;
		}
		else {
			autoExpire.setState(dialog.autoAdjustCheckBox.isSelected());

			MutableTimedAssociationKey newKey = new DefaultMutableTimedAssociationKey(
					dialog.parentCatField.getGenericCategoryID(),
					dialog.effDateEntryField.getValue(),
					dialog.expDateEntryField.getValue());

			return newKey;
		}
	}

	/**
	 * Displays edit category to catgory relationship dialog.
	 * @param childCategory
	 * @param key
	 * @return
	 */
	public static MutableTimedAssociationKey editParentCategory(GenericCategory childCategory, MutableTimedAssociationKey key) {
		CategoryToCategoryEditDialog dialog = new CategoryToCategoryEditDialog(childCategory, key, false);
		dialog.effDateEntryField.requestFocus();

		int option = JOptionPane.showConfirmDialog(
				ClientUtil.getApplet(),
				dialog,
				ClientUtil.getInstance().getLabel("d.title.edit.categorytocategory"),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		while ((option != JOptionPane.CANCEL_OPTION) && !dialog.isValid(dialog.effDateEntryField.getValue(), dialog.expDateEntryField.getValue(), key)) {
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
		else if ((key.getAssociableID() != dialog.parentCatField.getGenericCategoryID()) || !UtilBase.isSame(key.getEffectiveDate(), dialog.effDateEntryField.getValue())
				|| !UtilBase.isSame(key.getExpirationDate(), dialog.expDateEntryField.getValue())) {
			MutableTimedAssociationKey newKey = new DefaultMutableTimedAssociationKey(
					dialog.parentCatField.getGenericCategoryID(),
					dialog.effDateEntryField.getValue(),
					dialog.expDateEntryField.getValue());

			return newKey;
		}
		else {
			return null;
		}
	}

	private boolean isValid(DateSynonym effDate, DateSynonym expDate, MutableTimedAssociationKey oldKey) {
		if (parentCatField.getGenericCategory() == null) {
			ClientUtil.getInstance().showErrorDialog("msg.errors.required", "A parent category");

			return false;
		}
		else if (childCategory.getId() == parentCatField.getGenericCategoryID()) {
			ClientUtil.getInstance().showErrorDialog("msg.error.childcategory.equals.parent");
			return false;
		}
		else {
			String messageKey = Validator.validateDateRange(effDate, expDate);
			if (messageKey != null) {
				ClientUtil.getInstance().showErrorDialog(messageKey);
				return false;
			}
			else if (addMode || oldKey != null && oldKey.getAssociableID() != parentCatField.getGenericCategoryID()) {
				// check to make sure new parent has never been a decendent of the current category
				int parentID = parentCatField.getGenericCategory().getId();
				int[] childIDs = new int[] { childCategory.getId() };
				if (EntityModelCacheFactory.getInstance().isDescendentAtAnyTime(parentID, childIDs, childCategory.getType())) {
					ClientUtil.getInstance().showErrorDialog("msg.error.parentcategory.isdescendant");
					return false;
				}
				else {
					return true;
				}
			}
			else {
				return true;
			}
		}
	}

	private void initDialog() {
		layoutComponents();
	}

	private void layoutComponents() {
		JPanel bPanel = null;

		if (addMode) {
			bPanel = UIFactory.createJPanel(new GridLayout(7, 2, 4, 4));
		}
		else {
			bPanel = UIFactory.createJPanel(new GridLayout(7, 2, 4, 4));
		}

		bPanel.add(UIFactory.createFormLabel("label.category.parent"));
		bPanel.add(parentCatField);

		bPanel.add(UIFactory.createFormLabel("label.date.activation"));
		bPanel.add(effDateEntryField);

		bPanel.add(UIFactory.createFormLabel("label.date.expiration"));
		bPanel.add(expDateEntryField);

		if (addMode) {
			autoAdjustCheckBox = UIFactory.createCheckBox("checkbox.adjust.auto.parent");
			autoAdjustCheckBox.setForeground(PowerEditorSwingTheme.primary1);
			bPanel.add(autoAdjustCheckBox);
		}

		setLayout(new BorderLayout(4, 4));
		add(bPanel, BorderLayout.CENTER);
	}
}
