package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.EntityType;

/**
 * Filter for {@link com.mindbox.pe.model.AbstractIDNameDescriptionObject}.
 * This is <b>not</b> thread-safe.
 * @since PowerEditor 1.0
 */
public class NameDescriptionSearchFilter<T extends AbstractIDNameDescriptionObject> extends NameSearchFilter<T> {

	private static final long serialVersionUID = 2003062619332100L;

	private String descCriteria = null;

	/**
	 * @param entityType the entity type
	 */
	public NameDescriptionSearchFilter(EntityType entityType) {
		super(entityType);
	}


	/**
	 * @return the description criteria
	 */
	public final String getDescriptionCriterion() {
		return descCriteria;
	}

	public String toString() {
		return "NameDescSearchFilter[" + entityType + ",name=" + getNameCriterion() + ",desc=" + descCriteria + "]";
	}

	/**
	 * @param string the new description criteria
	 */
	public final void setDescriptionCriterion(String string) {
		descCriteria = string;
	}

	public boolean isAcceptable(T object) {
		if (object == null) throw new NullPointerException("NameDescription object is null");

		if (super.isAcceptable(object)) {
			if (descCriteria == null) {
				return true;
			}
			else {
				return contains(object.getDescription(), this.descCriteria);
			}
		}
		else {
			return false;
		}

	}
}
