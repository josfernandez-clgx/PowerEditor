package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClassLink;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.config.ServerConfiguration.DeploymentConfig;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;

public interface PatternFactoryHelper {

	String asVariableName(String name);
	
	/**
	 * Returns formatted variable name from the specified name and override.
	 * If override is not <code>null</code>, this returns override.
	 * @param name
	 * @param override
	 * @return
	 */
	String asVariableName(String name, String override);

	DomainAttribute findDomainAttributeForContextElement(RuleGenerationConfiguration.ControlPatternConfig controlPatternConfig, String contextElementType)
			throws RuleGenerationException;

	String formatForExcludedObject(String value);

	String getClassAttributeVarName(String className, String attrName) throws RuleGenerationException;
	
	String getDeployLabelForAttribute(Reference reference) throws RuleGenerationException;

	String getDeployLabelForClass(Reference reference) throws RuleGenerationException;

	String getDeployLabelForClass(String className) throws RuleGenerationException;

	boolean isStringDeployTypeForAttribute(Reference reference) throws RuleGenerationException;
	
	DeploymentConfig getDeploymentConfig();

	DomainClassLink[] getLinkage(String childName, String ancestorName);

	RuleGenerationConfiguration getRuleGenerationConfiguration(TemplateUsageType usageType);

	Action getTestActionObject(int testTypeID);

	String makeAEName(String name);

	void reportError(String str) throws RuleGenerationException;
}
