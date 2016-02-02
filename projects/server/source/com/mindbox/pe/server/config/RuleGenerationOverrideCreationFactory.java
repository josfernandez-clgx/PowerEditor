package com.mindbox.pe.server.config;

import org.apache.commons.digester.ObjectCreationFactory;
import org.xml.sax.Attributes;

import com.mindbox.pe.model.TemplateUsageType;


/**
 * ObjectCreationFactory for rule generation override tag. 
 * Used in conjunction with {@link com.mindbox.pe.server.config.ConfigXMLDigester}.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class RuleGenerationOverrideCreationFactory extends AbstractDigestedObjectCreationFactory implements ObjectCreationFactory {

	private final ConfigurationManager configManager;

	RuleGenerationOverrideCreationFactory(ConfigurationManager configManager) {
		this.configManager = configManager;
	}

	public Object createObject(Attributes attributes) throws Exception {
		String usageType = attributes.getValue("usageType");
		if (usageType == null) throw new IllegalArgumentException("usageType attribute is required");
		Object object = configManager.getRuleGenerationConfiguration(TemplateUsageType.valueOf(usageType));
		return object;
	}

}