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
public class TypeEnumCheckList extends CheckList<TypeEnumValue> {
	private static final long serialVersionUID = -3951228734910107454L;

	public TypeEnumCheckList(ComboBoxModel<TypeEnumValue> model) {
		setModel(model);
	}

	public TypeEnumCheckList(String typeKey, boolean sortValues) {
		this(EntityModelCacheFactory.getInstance().getTypeEnumComboModel(typeKey, false, sortValues));
	}

	@Override
	protected String getListText(TypeEnumValue obj) {
		return obj.getDisplayLabel();
	}

	public List<TypeEnumValue> getSelectedTypeEnumValues() {
		List<TypeEnumValue> selectedValues = new ArrayList<TypeEnumValue>();
		for (TypeEnumValue obj : getSelectedValuesList()) {
			selectedValues.add(obj);
		}
		return selectedValues;
	}

	public void selectTypeEnumValues(List<String> values) {
		clearSelection();
		if (values == null) {
			return;
		}
		ListModel<TypeEnumValue> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			TypeEnumValue item = model.getElementAt(i);
			if (values.contains(item.getValue())) {
				setSelectedValue(item, true);
			}
		}
	}
}
