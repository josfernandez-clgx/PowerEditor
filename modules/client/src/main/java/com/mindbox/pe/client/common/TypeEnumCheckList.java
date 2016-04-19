package com.mindbox.pe.client.common;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.TypeEnumValue;

/**
 * Widget that supports multiple selection of {@link com.mindbox.pe.model.TypeEnumValue} objects.
 * @author Geneho Kim
 * @see com.mindbox.pe.client.common.CheckList
 */
public class TypeEnumCheckList extends CheckList {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public TypeEnumCheckList(String typeKey, boolean sortValues) {
		this(EntityModelCacheFactory.getInstance().getTypeEnumComboModel(typeKey, false, sortValues));
	}

	public TypeEnumCheckList(ComboBoxModel model) {
		setModel(model);
	}

	protected String getListText(Object obj) {
		if (obj instanceof TypeEnumValue) {
			return ((TypeEnumValue) obj).getDisplayLabel();
		}
		else {
			return super.getListText(obj);
		}
	}

	public List<TypeEnumValue> getSelectedTypeEnumValues() {
		List<TypeEnumValue> selectedValues = new ArrayList<TypeEnumValue>();
		for (Object obj : getSelectedValues()) {
			selectedValues.add((TypeEnumValue) obj);
		}
		return selectedValues;
	}

	public void selectTypeEnumValues(List<String> values) {
		clearSelection();
		if (values == null) return;
		ListModel model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object item = model.getElementAt(i);
			if (item instanceof TypeEnumValue) {
				if (values.contains(((TypeEnumValue) item).getValue())) {
					setSelectedValue(item, true);
				}
			}
		}
	}
}
