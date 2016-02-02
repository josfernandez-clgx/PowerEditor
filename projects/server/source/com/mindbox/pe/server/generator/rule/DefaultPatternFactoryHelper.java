package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.DomainClassLink;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.config.RuleGenerationConfiguration.ControlPatternConfig;
import com.mindbox.pe.server.config.ServerConfiguration.DeploymentConfig;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.GuidelineRuleGenerator;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;

public class DefaultPatternFactoryHelper implements PatternFactoryHelper {

	// TODO Kim, 2007-01-09: cache results of various methods for performance improvement

	public String asVariableName(String name) {
		return AeMapper.makeAEVariable(name);
	}

	public String asVariableName(String name, String override) {
		return (override == null ? asVariableName(name) : override);
	}

	public DomainAttribute findDomainAttributeForContextElement(ControlPatternConfig controlPatternConfig, String contextElementType) throws RuleGenerationException {
		DomainClass dc = DomainManager.getInstance().getDomainClass(controlPatternConfig.getPatternClassName());
		DomainAttribute da = RuleGeneratorHelper.findDomainAttributeForContextElement(controlPatternConfig, dc, contextElementType);
		return da;
	}

	public String formatForExcludedObject(String value) {
		return RuleGeneratorHelper.formatForExcludedObject(value);
	}

	public String getClassAttributeVarName(String className, String attrName) throws RuleGenerationException {
		return AeMapper.getClassAttributeVarName(className, attrName);
	}
	
	public String getDeployLabelForAttribute(Reference reference) throws RuleGenerationException {
		return AeMapper.mapAttributeName(reference.getClassName(), reference.getAttributeName());
	}

	public boolean isStringDeployTypeForAttribute(Reference reference) throws RuleGenerationException {
		return DomainManager.getInstance().isStringDeployType(reference);
	}
	
	public String getDeployLabelForClass(Reference reference) throws RuleGenerationException {
		return getDeployLabelForClass(reference.getClassName());
	}

	public String getDeployLabelForClass(String className) throws RuleGenerationException {
		return AeMapper.mapClassName(className);
	}

	public DeploymentConfig getDeploymentConfig() {
		return ConfigurationManager.getInstance().getServerConfiguration().getDeploymentConfig();
	}

	public DomainClassLink[] getLinkage(String childName, String ancestorName) {
		return DomainManager.getInstance().getLinkage(childName, ancestorName);
	}

	public RuleGenerationConfiguration getRuleGenerationConfiguration(TemplateUsageType usageType) {
		return ConfigurationManager.getInstance().getRuleGenerationConfiguration(usageType);
	}

	public Action getTestActionObject(int testTypeID) {
		return GuidelineFunctionManager.getInstance().getTestActionObject(testTypeID);
	}

	public String makeAEName(String name) {
		return AeMapper.makeAEName(name);
	}

	public void reportError(String str) throws RuleGenerationException {
		GuidelineRuleGenerator.getInstance().reportError(str);
	}

}
