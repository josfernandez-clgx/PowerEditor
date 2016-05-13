package com.mindbox.pe.model.filter;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityPropertySearchFilter extends GenericEntityBasicSearchFilter {

	private static final long serialVersionUID = -904011948054646853L;

	protected final Map<String, Object> propCriteriaMap = Collections.synchronizedMap(new HashMap<String, Object>());

	/**
	 * @param entityType entityType
	 */
	public GenericEntityPropertySearchFilter(GenericEntityType entityType) {
		super(entityType);
	}

	public final Object getPropertyCriterion(String prop) {
		return propCriteriaMap.get(prop);
	}

	@Override
	public boolean isAcceptable(GenericEntity object) {
		if (!super.isAcceptable(object)) {
			return false;
		}

		for (Map.Entry<String, Object> entry : propCriteriaMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				if (!object.hasProperty(key)) return false;
				if (value instanceof Boolean) {
					if (object.getBooleanProperty(key) != ((Boolean) value).booleanValue()) {
						return false;
					}
				}
				else if (value instanceof Integer) {
					if (((Number) value).intValue() != 0) {
						if (object.getIntProperty(key) != ((Integer) value).intValue()) {
							return false;
						}
					}
				}
				else if (value instanceof Long) {
					if (((Long) value).longValue() != 0L) {
						if (object.getLongProperty(key) != ((Long) value).longValue()) {
							return false;
						}
					}
				}
				else if (value instanceof Float) {
					if (Float.floatToIntBits(((Float) value).floatValue()) != 0) {
						if (Float.floatToIntBits(object.getFloatProperty(key)) != Float.floatToIntBits(((Float) value).floatValue())) {
							return false;
						}
					}
				}
				else if (value instanceof Double) {
					if (Double.doubleToLongBits(((Double) value).doubleValue()) != 0L) {
						if (Double.doubleToLongBits(object.getDoubleProperty(key)) != Double.doubleToLongBits(((Double) value).doubleValue())) {
							return false;
						}
					}
				}
				else if (value instanceof Date) {
					if (!object.getDateProperty(key).equals(value)) {
						return false;
					}
				}
				else {
					if (!contains(object.getStringProperty(key), value.toString())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public final void setPropertyCriterion(String prop, boolean value) {
		propCriteriaMap.put(prop, (value ? Boolean.TRUE : Boolean.FALSE));
	}

	public final void setPropertyCriterion(String prop, Date value) {
		propCriteriaMap.put(prop, value);
	}

	public final void setPropertyCriterion(String prop, double value) {
		propCriteriaMap.put(prop, new Double(value));
	}

	public final void setPropertyCriterion(String prop, float value) {
		propCriteriaMap.put(prop, new Float(value));
	}

	public final void setPropertyCriterion(String prop, int value) {
		propCriteriaMap.put(prop, new Integer(value));
	}

	public final void setPropertyCriterion(String prop, long value) {
		propCriteriaMap.put(prop, new Long(value));
	}

	public final void setPropertyCriterion(String prop, String value) {
		propCriteriaMap.put(prop, value);
	}
}