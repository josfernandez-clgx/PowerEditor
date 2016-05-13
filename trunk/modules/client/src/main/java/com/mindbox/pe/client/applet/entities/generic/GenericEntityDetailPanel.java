package com.mindbox.pe.client.applet.entities.generic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.AbstractListField;
import com.mindbox.pe.client.common.TypeEnumMultiSelectPanel;
import com.mindbox.pe.client.common.detail.AbstractDetailPanel;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.client.common.event.ValueChangeListener;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityTab;
import com.mindbox.pe.xsd.config.EntityTab.EntityPropertyTab;

import mseries.ui.MChangeEvent;
import mseries.ui.MChangeListener;

/**
 * Generic entity detail panel.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class GenericEntityDetailPanel extends AbstractDetailPanel<GenericEntity, EntityManagementButtonPanel<GenericEntity>> implements ValueChangeListener {
	private class FieldChangeL implements ActionListener, MChangeListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			fireDetailChanged();
		}

		@SuppressWarnings("unused")
		public void valueChanged() {
			fireDetailChanged();
		}

		@Override
		public void valueChanged(MChangeEvent arg0) {
			fireDetailChanged();
		}
	}

	private static final long serialVersionUID = -3951228734910107454L;

	private JTextField nameField;
	private List<EntityProperty> propDefs;
	private Map<String, JComponent> propFieldMap;
	private List<JComponent> autoModifiedFieldList;
	private FieldChangeL comboChangeListener = null;
	private GenericEntityToCategoryPanel categoriesPanel;
	private JTabbedPane tab;
	private CategoryType categoryDef;

	/**
	 * @param entityType
	 */
	public GenericEntityDetailPanel(GenericEntityType entityType) {
		super(entityType);
	}

	@Override
	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		initComponents();

		this.nameField = new JTextField(10);

		propFieldMap = new HashMap<String, JComponent>();
		propDefs = ClientUtil.getEntityConfigHelper().findEntityTypeDefinition(genericEntityType).getEntityProperty();

		final EntityTab tabConfig = ClientUtil.getEntityTabMap().get(genericEntityType);
		List<EntityPropertyTab> tabDefs = (tabConfig == null ? null : tabConfig.getEntityPropertyTab());

		if (!ConfigUtil.isContainedInTab("name", tabDefs)) {
			c.gridwidth = 1;
			c.weightx = 0.0;
			addComponent(this, bag, c, UIFactory.createFormLabel("label.name", true));

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			addComponent(this, bag, c, nameField);
		}

		if (tabDefs != null && !tabDefs.isEmpty()) {
			// add fields for properties that are not specified in the tab first
			for (final EntityProperty entityProperty : propDefs) {
				if (!ConfigUtil.isContainedInTab(entityProperty.getName(), tabDefs)) {
					addFieldForProperty(entityProperty, this, bag, c);
				}
			}

			for (final EntityPropertyTab entityPropertyTab : tabDefs) {
				GridBagLayout propBag = new GridBagLayout();
				c.gridheight = 1;
				c.weighty = 0.0;

				JPanel detailPanel = UIFactory.createJPanel(propBag);
				detailPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				for (final String propName : entityPropertyTab.getEntityPropertyName()) {
					if (propName.equalsIgnoreCase("name")) {
						c.gridwidth = 1;
						c.weightx = 0.0;
						addComponent(detailPanel, propBag, c, UIFactory.createFormLabel("label.name", true));

						c.gridwidth = GridBagConstraints.REMAINDER;
						c.weightx = 1.0;
						addComponent(detailPanel, propBag, c, nameField);
					}
					else {
						EntityProperty entityPropertyDef = findPropertyDefinition(propName);
						if (entityPropertyDef != null) {
							addFieldForProperty(entityPropertyDef, detailPanel, propBag, c);
						}
					}
				}
				c.gridwidth = GridBagConstraints.REMAINDER;
				c.weightx = 1.0;
				c.gridheight = GridBagConstraints.REMAINDER;
				c.weighty = 1.0;
				addComponent(detailPanel, propBag, c, Box.createVerticalGlue());
				tab.addTab(entityPropertyTab.getTitle(), new JScrollPane(detailPanel));

			}
		}
		else {
			GridBagLayout propBag = new GridBagLayout();
			JPanel detailPanel = UIFactory.createJPanel(propBag);
			detailPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			tab.addTab(
					ClientUtil.getInstance().getLabel("tab." + genericEntityType.toString() + ".details", genericEntityType.getDisplayName() + " Details"),
					new JScrollPane(detailPanel));

			for (final EntityProperty entityProperty : propDefs) {
				addFieldForProperty(entityProperty, detailPanel, propBag, c);
			}
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			c.gridheight = GridBagConstraints.REMAINDER;
			c.weighty = 1.0;
			addComponent(detailPanel, propBag, c, Box.createVerticalGlue());
		}

		if (categoryDef != null) {
			tab.addTab(categoryDef.getName(), categoriesPanel);
		}

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, tab);

		setComponentLinks();
	}

	@Override
	protected void addDocumentListener(final DocumentListener documentListener, final MChangeListener mchangeListener) {
		nameField.getDocument().addDocumentListener(documentListener);
		for (JComponent element : propFieldMap.values()) {
			if (MDateDateField.class.isInstance(element)) {
				MDateDateField.class.cast(element).addChangeListener(documentListener, mchangeListener);
			}
			else if (element instanceof JTextComponent) {
				((JTextComponent) element).getDocument().addDocumentListener(documentListener);
			}
			else if (element instanceof JTextComponent) {
				((JTextComponent) element).getDocument().addDocumentListener(documentListener);
			}
			else if (element instanceof JComboBox) {
				((JComboBox<?>) element).addActionListener(getFieldChangeListener());
			}
			else if (element instanceof AbstractButton) {
				((AbstractButton) element).addActionListener(getFieldChangeListener());
			}
			else if (element instanceof AbstractListField) {
				((AbstractListField) element).addDocumentListener(documentListener);
			}
			else if (element instanceof TypeEnumMultiSelectPanel) {
				((TypeEnumMultiSelectPanel) element).addDocumentListener(documentListener);
			}
		}
		if (categoryDef != null) {
			categoriesPanel.addValueChangeListener(this);
		}
	}

	private void addFieldForProperty(EntityProperty entityPropertyDef, JPanel panel, GridBagLayout bag, GridBagConstraints c) {
		JComponent comp = createEditComponent(entityPropertyDef);
		if (comp != null) {
			c.gridwidth = 1;
			c.weightx = 0.0;
			addComponent(
					panel,
					bag,
					c,
					UIFactory.createFormLabel(
							"label." + entityPropertyDef.getName(),
							entityPropertyDef.getDisplayName(),
							UtilBase.asBoolean(entityPropertyDef.isIsRequired(), false)));

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			addComponent(panel, bag, c, comp);
		}
	}

	@Override
	public void clearFields() {
		setForViewOnly(true);
		nameField.setText("");

		for (Iterator<JComponent> iter = propFieldMap.values().iterator(); iter.hasNext();) {
			JComponent element = iter.next();
			if (element instanceof MDateDateField) {
				((MDateDateField) element).setValue(null);
			}
			else if (element instanceof JFormattedTextField) {
				((JFormattedTextField) element).setValue(null);
			}
			else if (element instanceof JTextComponent) {
				((JTextComponent) element).setText("");
			}
			else if (element instanceof JComboBox) {
				((JComboBox<?>) element).setSelectedIndex(-1);
			}
			else if (element instanceof AbstractButton) {
				((AbstractButton) element).setSelected(false);
			}
			else if (element instanceof AbstractListField) {
				((AbstractListField) element).setValue("");
			}
		}
		this.currentObject = null;
		if (categoryDef != null) {
			categoriesPanel.clearFields();
		}
	}

	private JComponent createEditComponent(EntityProperty def) {
		JComponent comp = GenericEntityUtil.createEditComponent(def, !UtilBase.asBoolean(def.isIsRequired(), false));
		if (comp != null) {
			propFieldMap.put(def.getName(), comp);
		}
		return comp;
	}

	private EntityProperty findPropertyDefinition(String propName) {
		for (final EntityProperty entityProperty : propDefs) {
			if (entityProperty.getName().equalsIgnoreCase(propName)) {
				return entityProperty;
			}
		}
		return null;
	}

	private FieldChangeL getFieldChangeListener() {
		if (comboChangeListener == null) {
			comboChangeListener = new FieldChangeL();
		}
		return comboChangeListener;
	}

	protected final String getNameFieldText() {
		return nameField.getText();
	}

	private void initComponents() {
		autoModifiedFieldList = new ArrayList<JComponent>();
		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.setFocusable(false);
		this.categoryDef = ClientUtil.getEntityConfigHelper().getCategoryDefinition(this.genericEntityType);
		if (categoryDef != null) {
			categoriesPanel = new GenericEntityToCategoryPanel(this.genericEntityType);
		}
		else {
			categoriesPanel = null;
		}
	}

	@Override
	protected void populateDetails(GenericEntity object) {
		this.nameField.setText(object.getName());
		populatePropertyFields(object);
	}

	@Override
	public void populateForClone(GenericEntity object) {
		this.nameField.setText(object.getName() + " - Copy");
		populatePropertyFields(object);
		currentObject = new GenericEntity(-1, super.genericEntityType, getNameFieldText());
		if (object.isForClone()) {
			currentObject.setForClone(true);
			currentObject.setCopyPolicies(object.shouldCopyPolicies());
			currentObject.setParentID(object.getID());
		}
	}

	private void populatePropertyFields(GenericEntity entity) {
		for (final EntityProperty entityProperty : propDefs) {
			GenericEntityUtil.setEditComponentValue(entity, entityProperty.getName(), propFieldMap.get(entityProperty.getName()), entityProperty.getType());
		}
		if (categoryDef != null) {
			categoriesPanel.setEntity(entity);
		}
	}

	@Override
	protected void removeDocumentListener(final DocumentListener documentListener, final MChangeListener mchangeListener) {
		nameField.getDocument().removeDocumentListener(documentListener);
		for (JComponent element : propFieldMap.values()) {
			if (element instanceof JTextComponent) {
				((JTextComponent) element).getDocument().removeDocumentListener(documentListener);
			}
			else if (element instanceof JComboBox) {
				((JComboBox<?>) element).removeActionListener(getFieldChangeListener());
			}
			else if (element instanceof AbstractButton) {
				((AbstractButton) element).removeActionListener(getFieldChangeListener());
			}
			else if (element instanceof AbstractListField) {
				((AbstractListField) element).removeDocumentListener(documentListener);
			}
			else if (element instanceof TypeEnumMultiSelectPanel) {
				((TypeEnumMultiSelectPanel) element).removeDocumentListener(documentListener);
			}
			else if (MDateDateField.class.isInstance(element)) {
				MDateDateField.class.cast(element).removeChangeListener(documentListener, mchangeListener);
			}
		}
		if (categoryDef != null) {
			categoriesPanel.removeValueChangeListener(this);
		}
	}

	private void setComponentLinks() {
		for (Map.Entry<String, JComponent> entry : propFieldMap.entrySet()) {
			String key = entry.getKey();
			EntityProperty propDef = findPropertyDefinition(key);
			if (propDef != null && propDef.getAutoUpdatedDateProperty() != null) {
				JComponent sourceComponent = entry.getValue();
				JComponent targetComponent = propFieldMap.get(propDef.getAutoUpdatedDateProperty());
				if (sourceComponent != null && targetComponent != null && targetComponent instanceof JTextField) {
					GenericEntityUtil.addAutoUpdateListener(sourceComponent, (JTextField) targetComponent);
					targetComponent.setEnabled(false);
					autoModifiedFieldList.add(targetComponent);
				}
			}
		}
	}

	@Override
	protected void setCurrentObjectFromFields() {
		if (currentObject == null) {
			currentObject = new GenericEntity(-1, super.genericEntityType, getNameFieldText());
		}
		else {
			currentObject.setName(getNameFieldText());
		}

		GenericEntity entity = currentObject;
		for (final EntityProperty entityProperty : propDefs) {
			GenericEntityUtil.setPropertyFromEditComponent(entity, entityProperty.getName(), propFieldMap.get(entityProperty.getName()), entityProperty.getType());
		}
		entity.removeAllCategoryAssociations();
		if (categoryDef != null) {
			if (categoriesPanel.getCategoryAssociations() != null) {
				for (Iterator<MutableTimedAssociationKey> i = categoriesPanel.getCategoryAssociations().iterator(); i.hasNext();) {
					entity.addCategoryAssociation(i.next());
				}
			}
			categoriesPanel.setEntity(entity);
		}
	}

	@Override
	protected void setEnabledFields(boolean enabled) {
		nameField.setEnabled(enabled);
		for (Iterator<JComponent> iter = propFieldMap.values().iterator(); iter.hasNext();) {
			JComponent element = iter.next();
			if (!autoModifiedFieldList.contains(element)) {
				element.setEnabled(enabled);
			}
		}
		if (categoryDef != null) {
			categoriesPanel.setEnabledFields(enabled);
		}

	}

	@Override
	public void valueChanged(ValueChangeEvent e) {
		fireDetailChanged();
	}
}