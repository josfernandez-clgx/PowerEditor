package com.mindbox.pe.common.validate.oval;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * OVal contraint check for {@link HasRequiredProperties} annotation.
 * This is thread safe.
 * @author kim
 *
 */
public class HasRequiredPropertiesCheck extends AbstractAnnotationCheck<HasRequiredProperties> {

	private static final long serialVersionUID = -548549499608336316L;

	private final List<String> missingPropertyNames = new LinkedList<String>();
	
	@Override
	public synchronized boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext arg2, Validator validator) throws OValException {
		missingPropertyNames.clear();
		if (valueToValidate == null) return true;
		if (validatedObject instanceof GenericEntity && valueToValidate instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) valueToValidate;
			GenericEntityType type = ((GenericEntity) validatedObject).getType();
			if (type != null) {
				EntityPropertyDefinition[] propertyDefinitions = GenericEntityType.getEntityTypeDefinition(type).getEntityPropertyDefinitions();
				for (int i = 0; i < propertyDefinitions.length; i++) {
					if (propertyDefinitions[i].isRequired() && !map.containsKey(propertyDefinitions[i].getName())) {
						missingPropertyNames.add(propertyDefinitions[i].getDisplayName());
					}
				}
				return missingPropertyNames.isEmpty();
			}
		}
		return false;
	}

	@Override
	public synchronized Map<String, String> getMessageVariables() {
		Map<String, String> messageVariables = new HashMap<String, String>(1);
		  messageVariables.put("missingProperties", UtilBase.toString(missingPropertyNames));
		  return messageVariables;
	}
}
