package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.IDNameObject;

/**
 * Filter for {@link com.mindbox.pe.model.AbstractIDNameDescriptionObject}.
 * This is <b>not</b> thread-safe.
 * @since PowerEditor 1.0
 */
public class NameSearchFilter<T extends IDNameObject> extends AbstractSearchFilter<T> {

	private static final long serialVersionUID = 2003062619332000L;


	private String nameCriteria = null;

	/**
	 * @param entityType the entity type
	 */
	public NameSearchFilter(EntityType entityType) {
		super(entityType);
	}


	/**
	 * @return the name criteria
	 */
	public final String getNameCriterion() {
		return nameCriteria;
	}

	/**
	 * @param string the new name criteria
	 */
	public final void setNameCriterion(String string) {
		nameCriteria = string;
	}

	public boolean isAcceptable(T object) {
		if (object == null) throw new NullPointerException("Name object is null");

		if (nameCriteria == null) {
			return true;
		}
		else {
			return contains(object.getName(), this.nameCriteria);
		}
	}

	public String toString() {
		return "NameSearchFilter[" + entityType + ",name=" + nameCriteria + "]";
	}
}
