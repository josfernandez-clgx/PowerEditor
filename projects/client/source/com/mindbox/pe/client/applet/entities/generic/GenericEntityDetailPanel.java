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

import mseries.ui.MChangeEvent;
import mseries.ui.MChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.AbstractListField;
import com.mindbox.pe.client.common.TypeEnumMultiSelectPanel;
import com.mindbox.pe.client.common.detail.AbstractDetailPanel;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.client.common.event.ValueChangeListener;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.common.config.EntityPropertyTabDefinition;
import com.mindbox.pe.common.config.EntityTabConfig;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

/**
 * Generic entity detail panel.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class GenericEntityDetailPanel extends AbstractDetailPanel<GenericEntity,EntityManagementButtonPanel<GenericEntity>> implements ValueChangeListener {

	private class FieldChangeL implements ActionListener, MChangeListener {

		public void actionPerformed(ActionEvent e) {
			fireDetailChanged();
		}

		public void valueChanged(MChangeEvent arg0) {
			fireDetailChanged();
		}

		@SuppressWarnings("unused")
		public void valueChanged() {
			fireDetailChanged();
		}
	}

	private JTextField nameField;
	private EntityPropertyDefinition[] propDefs;
	private Map<String, JComponent> propFieldMap;
	private List<JComponent> autoModifiedFieldList;
	private FieldChangeL comboChangeListener = null;
	private GenericEntityToCategoryPanel categoriesPanel;
	private JTabbedPane tab;
	private CategoryTypeDefinition categoryDef;

	/**
	 * @param entityType
	 */
	public GenericEntityDetailPanel(GenericEntityType entityType) {
		super(entityType);
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
				((JComboBox) element).setSelectedIndex(-1);
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

	protected void populateDetails(GenericEntity object) {
		this.nameField.setText(((GenericEntity) object).getName());
		populatePropertyFields((GenericEntity) object);
	}

	private void populatePropertyFields(GenericEntity entity) {
		for (int i = 0; i < propDefs.length; i++) {
			GenericEntityUtil.setEditComponentValue(
					entity,
					propDefs[i].getName(),
					propFieldMap.get(propDefs[i].getName()),
					propDefs[i].getType());
		}
		if (categoryDef != null) {
			categoriesPanel.setEntity(entity);
		}
	}

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

	protected void setCurrentObjectFromFields() {
		if (currentObject == null) {
			currentObject = new GenericEntity(-1, super.genericEntityType, getNameFieldText());
		}
		else {
			currentObject.setName(getNameFieldText());
		}

		GenericEntity entity = currentObject;
		for (int i = 0; i < propDefs.length; i++) {
			GenericEntityUtil.setPropertyFromEditComponent(
					entity,
					propDefs[i].getName(),
					propFieldMap.get(propDefs[i].getName()),
					propDefs[i].getType());
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

	private JComponent createEditComponent(EntityPropertyDefinition def) {
		JComponent comp = GenericEntityUtil.createEditComponent(def, !def.isRequired());
		if (comp != null) {
			propFieldMap.put(def.getName(), comp);
		}
		return comp;
	}

	private void initComponents() {
		autoModifiedFieldList = new ArrayList<JComponent>();
		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.setFocusable(false);
		this.categoryDef = ClientUtil.getEntityConfiguration().getCategoryDefinition(this.genericEntityType);
		if (categoryDef != null) {
			categoriesPanel = new GenericEntityToCategoryPanel(this.genericEntityType);
		}
		else {
			categoriesPanel = null;
		}
	}

	private void setComponentLinks() {
		for (Map.Entry<String,JComponent> entry : propFieldMap.entrySet()) {
			String key = entry.getKey();
			EntityPropertyDefinition propDef = findPropertyDefinition(key);
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

	private EntityPropertyDefinition findPropertyDefinition(String propName) {
		for (int i = 0; i < propDefs.length; i++) {
			if (propDefs[i].getName().equalsIgnoreCase(propName)) return propDefs[i];
		}
		return null;
	}

	private void addFieldForProperty(EntityPropertyDefinition entityPropertyDef, JPanel panel, GridBagLayout bag, GridBagConstraints c) {
		JComponent comp = createEditComponent(entityPropertyDef);
		if (comp != null) {
			c.gridwidth = 1;
			c.weightx = 0.0;
			addComponent(panel, bag, c, UIFactory.createFormLabel(
					"label." + entityPropertyDef.getName(),
					entityPropertyDef.getDisplayName(),
					entityPropertyDef.isRequired()));

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			addComponent(panel, bag, c, comp);
		}
	}

	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		initComponents();

		this.nameField = new JTextField(10);

		propFieldMap = new HashMap<String, JComponent>();
		propDefs = ClientUtil.getEntityConfiguration().findEntityTypeDefinition(genericEntityType).getEntityPropertyDefinitions();

		EntityTabConfig tabConfig = (EntityTabConfig) ClientUtil.getUserSession().getEntityTabConfigMap().get(genericEntityType);
		EntityPropertyTabDefinition[] tabDefs = (tabConfig == null ? null : tabConfig.getEntityPropertyTagDefinitions());

		if (!EntityPropertyTabDefinition.isContainedInTab("name", tabDefs)) {
			c.gridwidth = 1;
			c.weightx = 0.0;
			addComponent(this, bag, c, UIFactory.createFormLabel("label.name", true));

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			addComponent(this, bag, c, nameField);
		}

		if (tabDefs != null && tabDefs.length > 0) {
			// add fields for properties that are not specified in the tab first
			for (int i = 0; i < propDefs.length; i++) {
				if (!EntityPropertyTabDefinition.isContainedInTab(propDefs[i].getName(), tabDefs)) {
					addFieldForProperty(propDefs[i], this, bag, c);
				}
			}

			for (int i = 0; i < tabDefs.length; i++) {
				GridBagLayout propBag = new GridBagLayout();
				c.gridheight = 1;
				c.weighty = 0.0;

				JPanel detailPanel = UIFactory.createJPanel(propBag);
				detailPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				String[] propNames = tabDefs[i].getPropertyNames();
				for (int j = 0; j < propNames.length; j++) {
					if (propNames[j].equalsIgnoreCase("name")) {
						c.gridwidth = 1;
						c.weightx = 0.0;
						addComponent(detailPanel, propBag, c, UIFactory.createFormLabel("label.name", true));

						c.gridwidth = GridBagConstraints.REMAINDER;
						c.weightx = 1.0;
						addComponent(detailPanel, propBag, c, nameField);
					}
					else {
						EntityPropertyDefinition entityPropertyDef = findPropertyDefinition(propNames[j]);
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
				tab.addTab(tabDefs[i].getTitle(), new JScrollPane(detailPanel));

			}
		}
		else {
			GridBagLayout propBag = new GridBagLayout();
			JPanel detailPanel = UIFactory.createJPanel(propBag);
			detailPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			tab.addTab(ClientUtil.getInstance().getLabel(
					"tab." + genericEntityType.toString() + ".details",
					genericEntityType.getDisplayName() + " Details"), new JScrollPane(detailPanel));

			for (int i = 0; i < propDefs.length; i++) {
				addFieldForProperty(propDefs[i], detailPanel, propBag, c);
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

	protected void addDocumentListener(DocumentListener dl) {
		nameField.getDocument().addDocumentListener(dl);
		for (Iterator<JComponent> iter = propFieldMap.values().iterator(); iter.hasNext();) {
			JComponent element = iter.next();
			if (element instanceof MDateDateField) {
				((MDateDateField) element).addMChangeListener(getFieldChangeListener());
			}
			else if (element instanceof JTextComponent) {
				((JTextComponent) element).getDocument().addDocumentListener(dl);
			}
			else if (element instanceof JTextComponent) {
				((JTextComponent) element).getDocument().addDocumentListener(dl);
			}
			else if (element instanceof JComboBox) {
				((JComboBox) element).addActionListener(getFieldChangeListener());
			}
			else if (element instanceof AbstractButton) {
				((AbstractButton) element).addActionListener(getFieldChangeListener());
			}
			else if (element instanceof AbstractListField) {
				((AbstractListField) element).addDocumentListener(dl);
			}
			else if (element instanceof TypeEnumMultiSelectPanel) {
				((TypeEnumMultiSelectPanel) element).addDocumentListener(dl);
			}
		}
		if (categoryDef != null) {
			categoriesPanel.addValueChangeListener(this);
		}
	}

	protected void removeDocumentListener(DocumentListener dl) {
		nameField.getDocument().removeDocumentListener(dl);
		for (Iterator<JComponent> iter = propFieldMap.values().iterator(); iter.hasNext();) {
			JComponent element = iter.next();
			if (element instanceof MDateDateField) {
				((MDateDateField) element).removeMChangeListener(getFieldChangeListener());
			}
			else if (element instanceof JTextComponent) {
				((JTextComponent) element).getDocument().removeDocumentListener(dl);
			}
			else if (element instanceof JComboBox) {
				((JComboBox) element).removeActionListener(getFieldChangeListener());
			}
			else if (element instanceof AbstractButton) {
				((AbstractButton) element).removeActionListener(getFieldChangeListener());
			}
			else if (element instanceof AbstractListField) {
				((AbstractListField) element).removeDocumentListener(dl);
			}
			else if (element instanceof TypeEnumMultiSelectPanel) {
				((TypeEnumMultiSelectPanel) element).removeDocumentListener(dl);
			}
		}
		if (categoryDef != null) {
			categoriesPanel.removeValueChangeListener(this);
		}
	}

	public void valueChanged(ValueChangeEvent e) {
		fireDetailChanged();
	}

}