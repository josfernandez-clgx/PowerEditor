package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClassLink;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.config.ControlPatternConfigHelper;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.xsd.config.ServerConfig.Deployment;
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

	DomainAttribute findDomainAttributeForContextElement(ControlPatternConfigHelper controlPatternConfig, String contextElementType) throws RuleGenerationException;

	String formatForExcludedObject(String value);

	String getClassAttributeVarName(String className, String attrName) throws RuleGenerationException;

	String getDeployLabelForAttribute(Reference reference) throws RuleGenerationException;

	String getDeployLabelForClass(Reference reference) throws RuleGenerationException;

	String getDeployLabelForClass(String className) throws RuleGenerationException;

	boolean isStringDeployTypeForAttribute(Reference reference) throws RuleGenerationException;

	Deployment getDeploymentConfig();

	DomainClassLink[] getLinkage(String childName, String ancestorName);

	RuleGenerationConfigHelper getRuleGenerationConfiguration(TemplateUsageType usageType);

	Action getTestActionObject(int testTypeID);

	String makeAEName(String name);

	void reportError(String str) throws RuleGenerationException;
}
