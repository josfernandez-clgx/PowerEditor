package com.mindbox.pe.server.validate.oval;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.validate.oval.HasRequiredProperties;
import com.mindbox.pe.common.validate.oval.ServerConstraintCheck;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.EntityProperty;

/**
 * OVal contraint check for {@link HasRequiredProperties} annotation.
 * This is thread safe.
 * @author kim
 *
 */
public class HasRequiredPropertiesConstraintsCheck implements ServerConstraintCheck {

	/**
	 * @param validatedObject must be {@link GenericEntity}; if not, this returns <code>false</code>
	 * @param valueToValidate must be <code>Map<String,Object></code>; if not this returns <code>false</code>
	 */
	@Override
	public boolean isValid(Object validatedObject, Object valueToValidate, OValContext arg2, Validator validator, Map<String, String> messageVarMap) throws OValException {
		if (valueToValidate == null) return true;
		if (validatedObject instanceof GenericEntity && Map.class.isInstance(valueToValidate)) {
			final List<String> missingPropertyNames = new LinkedList<String>();
			Map<?, ?> map = (Map<?, ?>) valueToValidate;
			final GenericEntityType type = ((GenericEntity) validatedObject).getType();
			if (type != null) {
				for (final EntityProperty entityProperty : ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeDefinition(type).getEntityProperty()) {
					if (UtilBase.asBoolean(entityProperty.isIsRequired(), false) && !map.containsKey(entityProperty.getName())) {
						missingPropertyNames.add(entityProperty.getDisplayName());
					}
				}
				if (!missingPropertyNames.isEmpty()) {
					messageVarMap.put("missingProperties", UtilBase.toString(missingPropertyNames, ", "));
				}
				return missingPropertyNames.isEmpty();
			}
			return true;
		}
		else {
			return false;
		}
	}
}
