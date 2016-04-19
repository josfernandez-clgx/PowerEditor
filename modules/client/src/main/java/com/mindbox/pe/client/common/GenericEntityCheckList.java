package com.mindbox.pe.client.common;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * Widget that supports multiple selection of {@link com.mindbox.pe.model.GenericEntity} objects.
 * @author Geneho Kim
 * @see com.mindbox.pe.client.common.CheckList
 */
public class GenericEntityCheckList extends CheckList {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public GenericEntityCheckList(GenericEntityType entityType) {
		setModel(EntityModelCacheFactory.getInstance().getGenericEntityListModel(entityType, false));
	}

	protected String getListText(Object obj) {
		if (obj instanceof GenericEntity) {
			return ((GenericEntity) obj).getName();
		}
		else {
			return super.getListText(obj);
		}
	}

	public GenericEntity[] getSelectedGenericEntities() {
		Object[] objs = getSelectedValues();
		GenericEntity[] entities = new GenericEntity[objs.length];
		for (int i = 0; i < entities.length; i++) {
			entities[i] = (GenericEntity) objs[i];
		}
		return entities;
	}

	/**
	 * Makes sure the specified entities are selected.
	 * This does not clear selection.
	 * 
	 * @param entities
	 */
	public void select(GenericEntity[] entities) {
		if (entities != null && entities.length > 0) {
			for (int i = 0; i < entities.length; i++) {
				setSelectedValue(entities[i], true);
			}
		}
	}

	public void selectAll(boolean selected) {
		if (selected) {
			selectAll();
		}
		else {
			removeSelectionInterval(0, getModel().getSize() - 1);
		}
	}
}
