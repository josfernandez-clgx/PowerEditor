/*
 * Created on Dec 11, 2006
 *
 */
package com.mindbox.pe.client.applet.entities.generic.category;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.generic.GenericEntitySearchDialog;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.GenericEntityComboBox;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.xsd.config.EntityType;


/**
 * Category to entity relationship dialog.
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
public class CategoryToEntityEditDialog extends JPanel {

	private class FindL extends AbstractThreadedActionAdapter {
		@Override
		public void performAction(ActionEvent event) throws Exception {
			GenericEntity[] entities = GenericEntitySearchDialog.findGenericEntity(false, GenericEntityType.forCategoryType(category.getType()));
			if (entities != null && entities.length > 0) {
				entityCombo.selectGenericEntity(entities[0].getID());
			}
		}
	}

	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * Displays edit entity to catgory relationship dialog.
	 * @param category category
	 * @param data the data to edit
	 * @return CategoryToEntityAssociationData data, if dialog is not canceled; <code>null</code>, otherwise
	 */
	public static CategoryToEntityAssociationData editAssociationData(GenericCategory category, CategoryToEntityAssociationData data) {
		CategoryToEntityEditDialog dialog = new CategoryToEntityEditDialog(category, data);
		int option = JOptionPane.showConfirmDialog(
				ClientUtil.getApplet(),
				dialog,
				ClientUtil.getInstance().getLabel("d.title.edit.categorytoentity"),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		while (option != JOptionPane.CANCEL_OPTION && !isValid(dialog.effDateEntryField.getValue(), dialog.expDateEntryField.getValue())) {
			option = JOptionPane.showConfirmDialog(
					ClientUtil.getApplet(),
					dialog,
					ClientUtil.getInstance().getLabel("d.title.edit.categorytoentity"),
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
		}

		if (option == JOptionPane.CANCEL_OPTION) {
			return null;
		}
		else {
			MutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(category.getId(), dialog.effDateEntryField.getValue(), dialog.expDateEntryField.getValue());
			GenericEntity entity = EntityModelCacheFactory.getInstance().getGenericEntity(
					GenericEntityType.forCategoryType(category.getType()),
					dialog.entityCombo.getSelectedGenericEntityID());
			return new CategoryToEntityAssociationData(entity, key);
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

	/**
	 * Displays new entity to catgory relationship dialog.
	 * @param category category
	 * @return association data
	 */
	public static CategoryToEntityAssociationData newAssociationData(GenericCategory category) {
		CategoryToEntityEditDialog dialog = new CategoryToEntityEditDialog(category);
		dialog.entityCombo.requestFocus();

		int option = JOptionPane.showConfirmDialog(
				ClientUtil.getApplet(),
				dialog,
				ClientUtil.getInstance().getLabel("d.title.new.child.entity"),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		while (option != JOptionPane.CANCEL_OPTION && (!isValid(dialog.effDateEntryField.getValue(), dialog.expDateEntryField.getValue())
				|| (!dialog.entityCanBelongToMultipleCategories && dialog.hasOverlappingParentCategory()))) {
			option = JOptionPane.showConfirmDialog(
					ClientUtil.getApplet(),
					dialog,
					ClientUtil.getInstance().getLabel("d.title.new.child.entity"),
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
		}

		if (option == JOptionPane.CANCEL_OPTION) {
			return null;
		}
		else {
			GenericEntity entity = EntityModelCacheFactory.getInstance().getGenericEntity(
					GenericEntityType.forCategoryType(category.getType()),
					dialog.entityCombo.getSelectedGenericEntityID());
			MutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(category.getId(), dialog.effDateEntryField.getValue(), dialog.expDateEntryField.getValue());
			CategoryToEntityAssociationData data = new CategoryToEntityAssociationData(entity, key);

			return data;
		}
	}

	private DateSelectorComboField effDateEntryField;
	private DateSelectorComboField expDateEntryField;
	private final GenericCategory category;
	private GenericEntityComboBox entityCombo;
	private final JButton selectButton;
	private final boolean entityCanBelongToMultipleCategories;

	private CategoryToEntityEditDialog(GenericCategory category) {
		super();
		selectButton = UIFactory.createButton("", "image.btn.small.find", new FindL(), "button.tooltip.find", false);
		selectButton.setFocusable(false);
		this.category = category;
		initDialog();
		effDateEntryField.setValue(null);
		expDateEntryField.setValue(null);
		EntityType entityTypeDef = ClientUtil.getEntityConfigHelper().findEntityTypeDefinition(
				ClientUtil.getEntityConfigHelper().findEntityTypeForCategoryType(category.getType()));
		entityCanBelongToMultipleCategories = ConfigUtil.isCanBelongToMultipleCategories(entityTypeDef);
	}

	private CategoryToEntityEditDialog(GenericCategory category, CategoryToEntityAssociationData data) {
		super();
		this.category = category;
		selectButton = UIFactory.createButton("", "image.btn.small.find", new FindL(), "button.tooltip.find", false);
		selectButton.setFocusable(false);
		initDialog();
		entityCombo.selectGenericEntity(data.getEntity().getID());
		effDateEntryField.setValue(data.getAssociationKey().getEffectiveDate());
		expDateEntryField.setValue(data.getAssociationKey().getExpirationDate());
		EntityType entityTypeDef = ClientUtil.getEntityConfigHelper().findEntityTypeDefinition(data.getEntity().getType());
		entityCanBelongToMultipleCategories = ConfigUtil.isCanBelongToMultipleCategories(entityTypeDef);
	}

	private boolean hasOverlappingParentCategory() {
		boolean overlaps = false;
		GenericEntity entity = EntityModelCacheFactory.getInstance().getGenericEntity(
				GenericEntityType.forCategoryType(category.getType()),
				entityCombo.getSelectedGenericEntityID());
		MutableTimedAssociationKey newkey = new DefaultMutableTimedAssociationKey(category.getId(), effDateEntryField.getValue(), expDateEntryField.getValue());

		for (Iterator<MutableTimedAssociationKey> i = entity.getCategoryIterator(); i.hasNext();) {
			MutableTimedAssociationKey key = i.next();
			if (newkey.overlapsWith(key)) {
				GenericCategory parentCat = EntityModelCacheFactory.getInstance().getGenericCategory(entity.getType(), key.getAssociableID());
				ClientUtil.getInstance().showErrorDialog(
						"msg.error.entitycategory.overlaps",
						new Object[] { entity.getType().getName(), entityCombo.getSelectedGenericEntity().getName(), parentCat.getName() });
				overlaps = true;
				break;
			}
		}
		return overlaps;
	}

	private void initDialog() {
		entityCombo = new GenericEntityComboBox(GenericEntityType.forCategoryType(category.getType()), false, null, true);
		effDateEntryField = new DateSelectorComboField(true, true, true);
		expDateEntryField = new DateSelectorComboField(true, true, true);
		layoutComponents(GenericEntityType.forCategoryType(category.getType()));
	}

	private void layoutComponents(GenericEntityType type1) {
		JPanel bPanel = UIFactory.createJPanel(new GridLayout(6, 2, 4, 4));
		bPanel.add(UIFactory.createFormLabel(type1));

		JPanel p = UIFactory.createJPanel(new BorderLayout(0, 0));

		p.add(entityCombo, BorderLayout.CENTER);
		p.add(selectButton, BorderLayout.EAST);
		//bPanel.add(entityCombo);
		bPanel.add(p);
		bPanel.add(UIFactory.createFormLabel("label.date.activation"));
		bPanel.add(effDateEntryField);

		bPanel.add(UIFactory.createFormLabel("label.date.expiration"));
		bPanel.add(expDateEntryField);

		setLayout(new BorderLayout(4, 4));
		add(bPanel, BorderLayout.CENTER);
	}

}
