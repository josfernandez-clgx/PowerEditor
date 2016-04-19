package com.mindbox.pe.server.config;

import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.xsd.config.LHSPatternType;
import com.mindbox.pe.xsd.config.RuleGenerationLHS;

public class ControlPatternConfigHelper extends AbstractPatternConfigHelper {

	private final Map<String, String> attributes = new HashMap<String, String>();
	private String[] disallowedEntities;

	public ControlPatternConfigHelper(final RuleGenerationLHS.Pattern controlPattern) {
		super(controlPattern, LHSPatternType.CONTROL);
		for (final RuleGenerationLHS.Pattern.Attribute attribute : controlPattern.getAttribute()) {
			attributes.put(attribute.getType(), attribute.getName());
		}
		this.disallowedEntities = (controlPattern.getDisallowedEntities() == null ? new String[0] : controlPattern.getDisallowedEntities().split(","));
	}

	public String findAttributeNameForContextElement(String elementType) {
		if (attributes.containsKey(elementType)) {
			return attributes.get(elementType);
		}
		else {
			return null;
		}
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * 
	 * @return an array of disallowed entity names, if specified; <code>String[0]</code>, otherwise
	 */
	public String[] getDisallowedEntityNames() {
		return disallowedEntities;
	}

	public boolean isDisallowed(GenericEntityType entityType) {
		return UtilBase.isMember(entityType.getName(), this.disallowedEntities);
	}

	@Override
	public String toString() {
		return String.format("ControlPatternConfigHelper[attr.size=%d]", attributes.size());
	}
}