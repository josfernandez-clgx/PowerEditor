package com.mindbox.pe.model.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.Associable;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.xsd.config.EntityPropertyType;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Filter with name that can be persisted.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 3.0.0
 */
public class GenericEntityFilterSpec extends GenericEntityPropertySearchFilter implements Associable, PersistentFilterSpec {

	private static final long serialVersionUID = 200404237000L;

	public static final String PARAMETER_SEPARATOR = "|";

	public static final String FIELD_ASSIGNMENT = "=";

	public static final String KEY_NAME = "name";

	public static final String KEY_PARENT = "parent.id";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String name = null;

	private int id = -1;

	public GenericEntityFilterSpec(GenericEntityType entityType, String name) {
		this(entityType, -1, name);
	}

	public GenericEntityFilterSpec(GenericEntityType entityType, int filterID, String name) {
		super(entityType);
		this.name = name;
		this.id = filterID;
	}

	public boolean equals(Object obj) {
		return (obj instanceof GenericEntityFilterSpec) && super.equals(obj);
	}

	public String toString() {
		return getName();
	}

	public SearchFilter<GenericEntity> asSearchFilter() {
		return this;
	}

	public void setInvariants(Map<String, String> paramMap) {
		throw new UnsupportedOperationException();
	}

	public void setInvariants(Map<String, String> paramMap, Object helperObject) {
		if (helperObject == null || !(helperObject instanceof EntityType)) throw new UnsupportedOperationException();
		EntityType typeDef = (EntityType) helperObject;
		for (Map.Entry<String, String> element : paramMap.entrySet()) {
			String key = element.getKey();
			if (key.equals(KEY_NAME)) {
				nameCriterion = (String) element.getValue();
			}
			else if (key.equals(KEY_PARENT)) {
				try {
					parentIDCriterion = Integer.parseInt((String) element.getValue());
				}
				catch (Exception ex) {
					parentIDCriterion = -1;
				}
			}
			else {
				String valueStr = (String) element.getValue();
				EntityPropertyType propType = ConfigUtil.findPropertyType(typeDef, key);

				if (propType == EntityPropertyType.BOOLEAN) {
					setPropertyCriterion(key, Boolean.valueOf(valueStr).booleanValue());
				}
				else if (propType == EntityPropertyType.CURRENCY || propType == EntityPropertyType.DOUBLE) {
					setPropertyCriterion(key, Double.valueOf(valueStr).doubleValue());
				}
				else if (propType == EntityPropertyType.DATE) {
					try {
						synchronized (DATE_FORMAT) {
							setPropertyCriterion(key, DATE_FORMAT.parse(valueStr));
						}
					}
					catch (ParseException e) {
						Logger.getLogger(getClass()).warn("Ignoring search key " + key + ": invalid date str " + valueStr, e);
					}
				}
				else if (propType == EntityPropertyType.FLOAT || propType == EntityPropertyType.PERCENT) {
					setPropertyCriterion(key, Float.valueOf(valueStr).floatValue());
				}
				else if (propType == EntityPropertyType.INTEGER) {
					setPropertyCriterion(key, Integer.valueOf(valueStr).intValue());
				}
				else if (propType == EntityPropertyType.LONG) {
					setPropertyCriterion(key, Long.valueOf(valueStr).longValue());
				}
				else if (propType == EntityPropertyType.STRING || propType == EntityPropertyType.SYMBOL || propType == EntityPropertyType.ENUM) {
					setPropertyCriterion(key, valueStr);
				}
				else {
					Logger.getLogger(getClass()).warn("Ignoring search key " + key + ": invalid property type " + propType);
				}
			}
		}
	}

	public String toParamString() {
		StringBuilder buff = new StringBuilder();
		if (nameCriterion != null && nameCriterion.length() > 0) {
			buff.append(KEY_NAME);
			buff.append(FIELD_ASSIGNMENT);
			buff.append(nameCriterion);
			buff.append(PARAMETER_SEPARATOR);
		}
		if (parentIDCriterion != -1) {
			buff.append(KEY_PARENT);
			buff.append(FIELD_ASSIGNMENT);
			buff.append(parentIDCriterion);
			buff.append(PARAMETER_SEPARATOR);
		}
		for (Map.Entry<String, Object> element : propCriteriaMap.entrySet()) {
			buff.append(element.getKey());
			buff.append(FIELD_ASSIGNMENT);
			Object value = element.getValue();
			if (value instanceof Date) {
				synchronized (DATE_FORMAT) {
					buff.append(DATE_FORMAT.format((Date) value));
				}
			}
			else {
				buff.append(value);
			}
			buff.append(PARAMETER_SEPARATOR);
		}
		return buff.toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getEntityTypeID() {
		return super.entityType.getID();
	}

	public boolean isForGenericEntity() {
		return true;
	}
}