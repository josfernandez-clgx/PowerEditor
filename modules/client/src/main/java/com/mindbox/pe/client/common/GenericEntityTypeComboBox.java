package com.mindbox.pe.client.common;

import java.util.Arrays;

import javax.swing.JComboBox;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.comparator.GenericEntityTypeComparator;

/**
 * JComboBox that displays generic entity types.
 * @author Geneho Kim
 *
 */
public class GenericEntityTypeComboBox extends JComboBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public GenericEntityTypeComboBox(boolean hasEmpty, boolean sortItems, boolean compatibilityOnly) {
		super();
		UIFactory.setLookAndFeel(this);
		setRenderer(new GenericEntityTypeCellRenderer(null));
		setFocusable(true);
		if (hasEmpty) {
			addItem(" ");
		}
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		if (compatibilityOnly) {
		}
		if (sortItems) {
			Arrays.sort(types, GenericEntityTypeComparator.getInstance());
		}
		for (int i = 0; i < types.length; i++) {
			if (!compatibilityOnly || ConfigUtil.isUseInCompatibility(ClientUtil.getEntityConfigHelper().findEntityTypeDefinition(types[i]))) {
				addItem(types[i]);
			}
		}
	}

	public GenericEntityType getSelectedEntityType() {
		Object obj = super.getSelectedItem();
		if (obj instanceof GenericEntityType) {
			return (GenericEntityType) obj;
		}
		else {
			return null;
		}
	}

	public boolean hasSelection() {
		return super.getSelectedIndex() >= 0;
	}

	public void selectGenericEntityType(String entityName) {
		if (entityName == null || GenericEntityType.forName(entityName) == null) {
			setSelectedIndex(-1);
		}
		else {
			setSelectedItem(GenericEntityType.forName(entityName));
		}
	}

	public void selectGenericEntityType(GenericEntityType type) {
		if (type == null) {
			setSelectedIndex(-1);
		}
		else {
			setSelectedItem(type);
		}
	}
}
