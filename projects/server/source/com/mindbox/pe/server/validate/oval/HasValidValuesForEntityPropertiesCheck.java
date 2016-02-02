package com.mindbox.pe.server.validate.oval;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.imexport.digest.Entity;


/**
 * OVal contraint check for {@link HasValidValuesForEntityProperties} annotation.
 * This is thread safe.
 */
public class HasValidValuesForEntityPropertiesCheck extends AbstractAnnotationCheck<HasValidValuesForEntityProperties> {

	private static final long serialVersionUID = -548549499608336319L;

	private static boolean isValidValue(EntityPropertyDefinition propDef, String value) {
		if (UtilBase.isEmpty(value)) return true;
		String propType = propDef.getType();
		if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_BOOLEAN)) {
			return value.equalsIgnoreCase(ConfigUtil.CONFIG_VALUE_YES) || value.equalsIgnoreCase(ConfigUtil.CONFIG_VALUE_NO)
					|| value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("FALSE");
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_CURRENCY) || propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DOUBLE)
				|| propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_FLOAT) || propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_PERCENT)) {
			try {
				Double.valueOf(value);
				return true;
			}
			catch (NumberFormatException ex) {
				return false;
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INT)) {
			try {
				Integer.valueOf(value);
				return true;
			}
			catch (NumberFormatException ex) {
				return false;
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_LONG)) {
			try {
				Long.valueOf(value);
				return true;
			}
			catch (NumberFormatException ex) {
				return false;
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_DATE)) {
			try {
				UIConfiguration.FORMAT_DATE_TIME_SEC.parse(value);
				return true;
			}
			catch (ParseException ex) {
				return ConfigUtil.toDate(value) != null;
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_ENUM)) {
			if (!(value instanceof String)) return false;
			return HasValidValuesForPropertiesConstraintCheck.isValidEnumPropertyValue(propDef, value);
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_INTEGERLIST)) {
			try {
				UtilBase.toIntArray(value);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (propType.equalsIgnoreCase(ConfigUtil.PROPERTY_TYPE_SYMBOL)) {
			return UtilBase.isValidSymbol(value);
		}
		return true;
	}

	private final List<String> invalidPropertyNames = new LinkedList<String>();

	/**
	 * @param valueToValidate must be {@link Entity}; if not, this returns <code>true</code>
	 * @returns <code>true</code>, if valudateToValidate is <code>null</code>; otherwise, true only if valueToValidate is valid
	 */
	@Override
	public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext context, Validator validator)
			throws OValException {

		Logger.getLogger(getClass()).debug("---> isSatisfied:" + validatedObject+",value="+valueToValidate+",context="+context);
		if (valueToValidate == null) return true;
		if (validatedObject instanceof Entity) {
			Entity entity = (Entity) validatedObject;
			GenericEntityType entityType = GenericEntityType.forName(entity.getType());
			if (entityType != null) {
				EntityPropertyDefinition[] propertyDefinitions = GenericEntityType.getEntityTypeDefinition(entityType).getEntityPropertyDefinitions();
				synchronized (invalidPropertyNames) {
					invalidPropertyNames.clear();
					for (EntityPropertyDefinition propertyDefinition : propertyDefinitions) {
						String value = entity.getProperty(propertyDefinition.getName(), null);
						if (!isValidValue(propertyDefinition, value)) {
							invalidPropertyNames.add(propertyDefinition.getDisplayName());
						}
					}
					return invalidPropertyNames.isEmpty();
				}
			}
			return true;
		}
		else {
			return true;
		}
	}

	@Override
	public Map<String, String> getMessageVariables() {
		synchronized (invalidPropertyNames) {
			Map<String, String> messageVarMap = new HashMap<String, String>();
			if (!invalidPropertyNames.isEmpty()) {
				Collections.sort(invalidPropertyNames);
				messageVarMap.put("invalidProperties", UtilBase.toString(invalidPropertyNames, ", "));
			}
			return messageVarMap;
		}
	}
}
