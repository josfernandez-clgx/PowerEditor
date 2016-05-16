package com.mindbox.pe.model.filter;

import java.util.Map;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.PeDataType;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class NameDescriptionFilterSpec<T extends AbstractIDNameDescriptionObject> extends NameFilterSpec<T> {

	private static final long serialVersionUID = 2003061622304000L;

	private String descCriterion = null;

	/**
	 * 
	 * @param entityType entityType
	 * @param genericEntityType genericEntityType
	 * @param filterID filterID
	 * @param name name
	 */
	public NameDescriptionFilterSpec(PeDataType entityType, GenericEntityType genericEntityType, int filterID, String name) {
		super(entityType, genericEntityType, filterID, name);
	}

	/**
	 * 
	 * @param entityType entityType
	 * @param genericEntityType genericEntityType
	 * @param name name
	 */
	public NameDescriptionFilterSpec(PeDataType entityType, GenericEntityType genericEntityType, String name) {
		super(entityType, genericEntityType, name);
	}

	@Override
	public SearchFilter<T> asSearchFilter() {
		NameDescriptionSearchFilter<T> filter = new NameDescriptionSearchFilter<T>(super.getEntityType());
		if (getNameCriterion() != null) {
			filter.setNameCriterion(getNameCriterion());
		}
		if (descCriterion != null) {
			filter.setDescriptionCriterion(descCriterion);
		}
		return filter;
	}

	/**
	 * @return description filter criterion
	 */
	public final String getDescCriterion() {
		return descCriterion;
	}

	/**
	 * @param string new description filter criterion
	 */
	public final void setDescCriterion(String string) {
		descCriterion = string;
	}

	@Override
	public void setInvariants(Map<String, String> paramMap) {
		super.setInvariants(paramMap);
		descCriterion = paramMap.get("desc");
	}

	@Override
	public String toParamString() {
		if (descCriterion != null && descCriterion.length() > 0) {
			return super.toParamString() + "desc" + FIELD_ASSIGNMENT + descCriterion + PARAMETER_SEPARATOR;
		}
		else {
			return super.toParamString();
		}
	}

}
