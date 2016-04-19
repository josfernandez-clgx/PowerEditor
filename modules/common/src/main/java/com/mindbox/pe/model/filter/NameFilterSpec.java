package com.mindbox.pe.model.filter;

import java.util.Map;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.IDNameObject;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class NameFilterSpec<T extends IDNameObject> extends AbstractPersistentFilterSpec<T> {

	private static final long serialVersionUID = 2003061622304001L;

	private String nameCriterion = null;

	public NameFilterSpec(PeDataType entityType, GenericEntityType genericEntityType, String name) {
		super(entityType, genericEntityType, name);
	}

	public NameFilterSpec(PeDataType entityType, GenericEntityType genericEntityType, int filterID, String name) {
		super(entityType, genericEntityType, filterID, name);
	}

	public SearchFilter<T> asSearchFilter() {
		NameSearchFilter<T> filter = new NameSearchFilter<T>(super.getEntityType());
		if (nameCriterion != null) {
			filter.setNameCriterion(nameCriterion);
		}
		return filter;
	}

	/**
	 * @return name filter criterion
	 */
	public String getNameCriterion() {
		return nameCriterion;
	}

	/**
	 * @param string
	 *            new name filter criterion
	 */
	public void setNameCriterion(String string) {
		nameCriterion = string;
	}

	public void setInvariants(Map<String,String> paramMap, Object helper) {
		nameCriterion = paramMap.get("name");
	}

	public String toParamString() {
		if (nameCriterion != null && nameCriterion.length() > 0) {
			return "name" + FIELD_ASSIGNMENT + nameCriterion + PARAMETER_SEPARATOR;
		}
		else {
			return "";
		}
	}

}
