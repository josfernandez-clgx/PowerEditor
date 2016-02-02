package com.mindbox.pe.server.validate.oval;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.common.validate.oval.HasRequiredProperties;
import com.mindbox.pe.common.validate.oval.ServerConstraintCheck;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;

/**
 * OVal contraint check for {@link HasRequiredProperties} annotation.
 * This is thread safe.
 * @author kim
 *
 */
public class HasValidValuesForPropertiesConstraintCheck implements ServerConstraintCheck {

	static boolean isValidEnumPropertyValue(EntityPropertyDefinition propDef, String value) {
		List<String> enumValueList = new ArrayList<String>();
		// (1) build enum value list
		if (propDef.isAttributeMapSet()) {
			DomainAttribute da = DomainManager.getInstance().getDomainAttributeForAttributeMap(propDef.getAttributeMap());
			if (da != null) {
				EnumValue[] enumValues = da.getEnumValues();
				for (int i = 0; i < enumValues.length; i++) {
					if (enumValues[i].isActive()) {
						TypeEnumValue typeEnumValue = new TypeEnumValue(
								enumValues[i].getDeployID().intValue(),
								enumValues[i].getDeployValue(),
								enumValues[i].getDisplayLabel());
						enumValueList.add(typeEnumValue.getValue());
					}
				}
			}
		}
		else {
			List<TypeEnumValue> enumList = TypeEnumValueManager.getInstance().getAllEnumValues(propDef.getEnumType());
			for (TypeEnumValue typeEnumValue : enumList) {
				enumValueList.add(typeEnumValue.getValue());
			}
		}
		// (2) check values
		List<String> valuesToCheck;
		if (propDef.allowMultiple()) {
			valuesToCheck = GenericEntity.toMultiEnumValues(value);
		}
		else {
			valuesToCheck = new ArrayList<String>();
			valuesToCheck.add(value);
		}
		for (String valueToCheck : valuesToCheck) {
			if (!enumValueList.contains(valueToCheck)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isValidValue(EntityPropertyDefinition propDef, Object value) {
		if (value == null) return true;
		String propType = propDef.getType();
		if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
			return value instanceof Boolean;
		}

		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_CURRENCY) || propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DOUBLE)) {
			return value instanceof Double;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
			return value instanceof Date;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
			if (!(value instanceof String)) return false;
			return isValidEnumPropertyValue(propDef, (String) value);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_FLOAT) || propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_PERCENT)) {
			return value instanceof Float;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INT)) {
			return value instanceof Integer;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
			if (!(value instanceof String)) return false;
			try {
				UtilBase.toIntArray((String) value);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_LONG)) {
			return value instanceof Long;
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
			return value instanceof String && UtilBase.isValidSymbol((String) value);
		}
		return true;
	}

	/**
	 * @param validatedObject must be {@link GenericEntity}; if not, this returns <code>false</code>
	 * @param valueToValidate must be <code>Map<String,Object></code>; if not this returns <code>false</code>
	 */
	@Override
	public boolean isValid(Object validatedObject, Object valueToValidate, OValContext arg2, Validator validator,
			Map<String, String> messageVarMap) throws OValException {
		if (valueToValidate == null) return true;
		if (validatedObject instanceof GenericEntity && Map.class.isInstance(valueToValidate)) {
			List<String> invalidPropertyNames = new LinkedList<String>();
			Map<?, ?> map = (Map<?, ?>) valueToValidate;
			GenericEntityType type = ((GenericEntity) validatedObject).getType();
			if (type != null) {
				EntityPropertyDefinition[] propertyDefinitions = GenericEntityType.getEntityTypeDefinition(type).getEntityPropertyDefinitions();
				for (EntityPropertyDefinition propertyDefinition : propertyDefinitions) {
					Object value = map.get(propertyDefinition.getName());
					if (!isValidValue(propertyDefinition, value)) {
						invalidPropertyNames.add(propertyDefinition.getDisplayName());
					}
				}
				if (!invalidPropertyNames.isEmpty()) {
					messageVarMap.put("invalidProperties", UtilBase.toString(invalidPropertyNames, ", "));
				}
				return invalidPropertyNames.isEmpty();
			}
			return true;
		}
		else {
			return false;
		}
	}


}
