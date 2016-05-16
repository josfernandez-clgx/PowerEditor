package com.mindbox.pe.client.applet.entities.generic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.filter.AbstractPersistedFilterPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;
import com.mindbox.pe.model.filter.PersistentFilterSpec;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Search panel for generic entities. This does not support save named filters feature, yet.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public final class GenericEntityFilterPanel extends AbstractPersistedFilterPanel<GenericEntity, EntityManagementButtonPanel<GenericEntity>> {

	private static final long serialVersionUID = -3951228734910107454L;

	private JTextField nameField;
	private Map<String, JComponent> propFieldMap;
	private List<EntityProperty> propDefs;

	public GenericEntityFilterPanel(GenericEntityType type, AbstractSelectionPanel<GenericEntity, EntityManagementButtonPanel<GenericEntity>> selectionPanel) {
		super(selectionPanel, type, false);
	}

	@Override
	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		this.propFieldMap = new HashMap<String, JComponent>();
		this.nameField = new JTextField(10);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.name.contains"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, nameField);

		EntityType typeDef = ClientUtil.getEntityConfigHelper().findEntityTypeDefinition(filterGenericEntityType);

		this.propDefs = typeDef.getEntityProperty();
		for (final EntityProperty entityProperty : propDefs) {
			if (UtilBase.asBoolean(entityProperty.isIsSearchable(), false)) {
				JComponent comp = createEditComponent(entityProperty);
				if (comp != null) {
					c.gridwidth = 1;
					c.weightx = 0.0;
					addComponent(this, bag, c, UIFactory.createFormLabel("label." + entityProperty.getName(), entityProperty.getDisplayName(), false));

					c.gridwidth = GridBagConstraints.REMAINDER;
					c.weightx = 1.0;
					addComponent(this, bag, c, comp);
				}
			}
		}
	}

	@Override
	protected void clearSearchFields() {
		nameField.setText("");
		for (Iterator<JComponent> iter = propFieldMap.values().iterator(); iter.hasNext();) {
			JComponent element = iter.next();
			if (element instanceof JTextComponent) {
				if (element instanceof JFormattedTextField) {
					((JFormattedTextField) element).setValue("");
				}
				else {
					((JTextComponent) element).setText("");
				}
			}
			else if (element instanceof JComboBox) {
				if (((JComboBox<?>) element).getItemCount() <= 0) {
					((JComboBox<?>) element).setSelectedIndex(-1);
				}
				else {
					((JComboBox<?>) element).setSelectedIndex(0);
				}
			}
			else if (element instanceof AbstractButton) {
				((AbstractButton) element).setSelected(false);
			}
			else {
				ClientUtil.getLogger().warn("... Unknown type: " + element.getClass());
			}
		}
	}

	private JComponent createEditComponent(EntityProperty def) {
		JComponent comp = GenericEntityUtil.createEditComponent(def, true);
		if (comp != null) {
			propFieldMap.put(def.getName(), comp);
		}
		return comp;
	}

	protected PersistentFilterSpec createFilterSpecFromFields(String filterName) {
		GenericEntityFilterSpec spec = new GenericEntityFilterSpec(this.filterGenericEntityType, filterName);
		updateFilterSpecFromFields(spec);
		return spec;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected SearchFilter<GenericEntity> getSearchFilterFromFields() {
		return (SearchFilter<GenericEntity>) createFilterSpecFromFields(null);
	}

	private void updateFilterSpecFromFields(GenericEntityFilterSpec filterSpec) {
		if (!UtilBase.isEmptyAfterTrim(nameField.getText())) {
			filterSpec.setNameCriterion(nameField.getText());
		}
		else {
			filterSpec.setNameCriterion(null);
		}

		for (final EntityProperty entityProperty : propDefs) {
			if (UtilBase.asBoolean(entityProperty.isIsSearchable(), false)) {
				GenericEntityUtil.setPropertyFromEditComponent(filterSpec, entityProperty.getName(), propFieldMap.get(entityProperty.getName()), entityProperty.getType());
			}
		}
	}
}