/*
 * Created on 2005. 5. 5.
 *
 */
package com.mindbox.pe.server.config;

import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.xsd.config.ObjectGenerationDefault;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class ParameterContextConfigHelper {

	private final Map<String, ObjectGenerationDefault.ParameterContext.Attribute> attributeMap = new HashMap<String, ObjectGenerationDefault.ParameterContext.Attribute>();

	public ParameterContextConfigHelper(final ObjectGenerationDefault.ParameterContext parameterContext) {
		if (parameterContext != null && parameterContext.getAttribute() != null) {
			for (final ObjectGenerationDefault.ParameterContext.Attribute attribute : parameterContext.getAttribute()) {
				attributeMap.put(attribute.getType(), attribute);
			}
		}
	}

	public final ObjectGenerationDefault.ParameterContext.Attribute findAttributeConfiguration(String type) {
		synchronized (attributeMap) {
			return attributeMap.get(type);
		}
	}

	public final String findAttributeName(String type, String defaultName) {
		ObjectGenerationDefault.ParameterContext.Attribute attribute = findAttributeConfiguration(type);
		return (attribute == null ? defaultName : attribute.getName());
	}

	public final String findAttributeValue(String type, String defaultValue) {
		ObjectGenerationDefault.ParameterContext.Attribute attribute = findAttributeConfiguration(type);
		return (attribute == null ? defaultValue : attribute.getValue());
	}

	public final boolean findAttributeIsValueAsString(String type) {
		ObjectGenerationDefault.ParameterContext.Attribute attribute = findAttributeConfiguration(type);
		return (attribute == null ? false : UtilBase.asBoolean(attribute.isValueAsString(), false));
	}

	@Override
	public String toString() {
		return String.format("ParameterContextConfigHelper[size=%d]", attributeMap.size());
	}
}
