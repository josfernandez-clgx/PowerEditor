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
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.imexport.ObjectConverter;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityPropertyType;
import com.mindbox.pe.xsd.data.EntityDataElement.Entity;


/**
 * OVal contraint check for {@link HasValidValuesForEntityProperties} annotation.
 * This is thread safe.
 */
public class HasValidValuesForEntityPropertiesCheck extends AbstractAnnotationCheck<HasValidValuesForEntityProperties> {

	private static final long serialVersionUID = -548549499608336319L;

	private static boolean isValidValue(EntityProperty propDef, String value) {
		if (UtilBase.isEmpty(value)) return true;
		final EntityPropertyType propType = propDef.getType();
		switch (propType) {
		case BOOLEAN:
			return value.equalsIgnoreCase(Constants.CONFIG_VALUE_YES) || value.equalsIgnoreCase(Constants.CONFIG_VALUE_NO) || value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("FALSE");
		case CURRENCY:
		case DOUBLE:
		case FLOAT:
		case PERCENT:
			try {
				Double.valueOf(value);
				return true;
			}
			catch (NumberFormatException ex) {
				return false;
			}
		case INTEGER:
			try {
				Integer.valueOf(value);
				return true;
			}
			catch (NumberFormatException ex) {
				return false;
			}
		case LONG:
			try {
				Long.valueOf(value);
				return true;
			}
			catch (NumberFormatException ex) {
				return false;
			}
		case DATE:
			try {
				Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().parse(value);
				return true;
			}
			catch (ParseException ex) {
				return ConfigUtil.toDate(value) != null;
			}
		case ENUM:
			if (!(value instanceof String)) return false;
			return HasValidValuesForPropertiesConstraintCheck.isValidEnumPropertyValue(propDef, value);
		case INTEGER_LIST:
			try {
				UtilBase.toIntArray(value);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		case SYMBOL:
			return UtilBase.isValidSymbol(value);
		default:
			return true;
		}
	}

	private final List<String> invalidPropertyNames = new LinkedList<String>();

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

	/**
	 * @param valueToValidate must be {@link Entity}; if not, this returns <code>true</code>
	 * @returns <code>true</code>, if valudateToValidate is <code>null</code>; otherwise, true only if valueToValidate is valid
	 */
	@Override
	public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext context, Validator validator) throws OValException {
		Logger.getLogger(getClass()).debug("---> isSatisfied:" + validatedObject + ",value=" + valueToValidate + ",context=" + context);
		if (valueToValidate == null) return true;
		if (validatedObject instanceof Entity) {
			Entity entity = (Entity) validatedObject;
			final GenericEntityType entityType = GenericEntityType.forName(entity.getType());
			if (entityType != null) {
				synchronized (invalidPropertyNames) {
					invalidPropertyNames.clear();
					for (final EntityProperty entityProperty : ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeDefinition(entityType).getEntityProperty()) {
						final String value = ObjectConverter.getProperty(entity, entityProperty.getName());
						if (!isValidValue(entityProperty, value)) {
							invalidPropertyNames.add(entityProperty.getDisplayName());
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
}
