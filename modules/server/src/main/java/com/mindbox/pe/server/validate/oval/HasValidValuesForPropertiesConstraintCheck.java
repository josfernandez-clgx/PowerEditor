package com.mindbox.pe.server.validate.oval;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.validate.oval.HasRequiredProperties;
import com.mindbox.pe.common.validate.oval.ServerConstraintCheck;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityPropertyType;

import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * OVal contraint check for {@link HasRequiredProperties} annotation.
 * This is thread safe.
 * @author kim
 *
 */
public class HasValidValuesForPropertiesConstraintCheck implements ServerConstraintCheck {

	static boolean isValidEnumPropertyValue(final EntityProperty propDef, String value) {
		List<String> enumValueList = new ArrayList<String>();
		// (1) build enum value list
		if (!UtilBase.isEmpty(propDef.getAttributeMap())) {
			DomainAttribute da = DomainManager.getInstance().getDomainAttributeForAttributeMap(propDef.getAttributeMap());
			if (da != null) {
				EnumValue[] enumValues = da.getEnumValues();
				for (int i = 0; i < enumValues.length; i++) {
					if (enumValues[i].isActive()) {
						TypeEnumValue typeEnumValue = new TypeEnumValue(enumValues[i].getDeployID().intValue(), enumValues[i].getDeployValue(), enumValues[i].getDisplayLabel());
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
		if (UtilBase.asBoolean(propDef.isAllowMultiple(), false)) {
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

	private static boolean isValidValue(final EntityProperty propDef, Object value) {
		if (value == null) return true;
		final EntityPropertyType propType = propDef.getType();
		switch (propType) {
		case BOOLEAN:
			return value instanceof Boolean;
		case CURRENCY:
		case DOUBLE:
			return value instanceof Double;
		case DATE:
			return value instanceof Date;
		case ENUM:
			if (!(value instanceof String)) return false;
			return isValidEnumPropertyValue(propDef, (String) value);
		case FLOAT:
		case PERCENT:
			return value instanceof Float;
		case INTEGER:
			return value instanceof Integer;
		case INTEGER_LIST:
			if (!(value instanceof String)) return false;
			try {
				UtilBase.toIntArray((String) value);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		case LONG:
			return value instanceof Long;
		case SYMBOL:
			return value instanceof String && UtilBase.isValidSymbol((String) value);
		default:
			return true;
		}
	}

	/**
	 * @param validatedObject must be {@link GenericEntity}; if not, this returns <code>false</code>
	 * @param valueToValidate must be <code>Map&lt;String,Object&gt;</code>; if not this returns <code>false</code>
	 * @param arg2 context
	 * @param validator validator
	 * @param messageVarMap message var map
	 * @return true if valid; false, otherwise
	 */
	@Override
	public boolean isValid(Object validatedObject, Object valueToValidate, OValContext arg2, Validator validator, Map<String, String> messageVarMap) throws OValException {
		if (valueToValidate == null) return true;
		if (validatedObject instanceof GenericEntity && Map.class.isInstance(valueToValidate)) {
			List<String> invalidPropertyNames = new LinkedList<String>();
			Map<?, ?> map = (Map<?, ?>) valueToValidate;
			final GenericEntityType type = ((GenericEntity) validatedObject).getType();
			if (type != null) {
				for (final EntityProperty entityProperty : ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeDefinition(type).getEntityProperty()) {
					Object value = map.get(entityProperty.getName());
					if (!isValidValue(entityProperty, value)) {
						invalidPropertyNames.add(entityProperty.getDisplayName());
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
