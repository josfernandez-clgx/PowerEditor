package com.mindbox.pe.server.generator.rule;

import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.DomainClassLink;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ControlPatternConfigHelper;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.GuidelineRuleGenerator;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.xsd.config.ServerConfig.Deployment;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;

public class DefaultPatternFactoryHelper implements PatternFactoryHelper {

	// TODO Kim, 2007-01-09: cache results of various methods for performance improvement

	private static final String asKey(String className, String attrName) {
		return className + "." + (attrName == null ? "" : attrName);
	}

	private final GuidelineRuleGenerator guidelineRuleGenerator;
	private final Map<String, String> variableNameMap = new HashMap<String, String>();
	private final Map<String, String> classAttributeVarMap = new HashMap<String, String>();
	private final Map<String, String> deployLabelMap = new HashMap<String, String>();

	public DefaultPatternFactoryHelper(GuidelineRuleGenerator guidelineRuleGenerator) {
		super();
		this.guidelineRuleGenerator = guidelineRuleGenerator;
	}

	public String asVariableName(String name) {
		String variableName = variableNameMap.get(name);
		if (variableName == null) {
			variableName = AeMapper.makeAEVariable(name);
			variableNameMap.put(name, variableName);
		}
		return variableName;
	}

	public String asVariableName(String name, String override) {
		return (override == null ? asVariableName(name) : override);
	}

	public DomainAttribute findDomainAttributeForContextElement(ControlPatternConfigHelper controlPatternConfig, String contextElementType) throws RuleGenerationException {
		DomainClass dc = DomainManager.getInstance().getDomainClass(controlPatternConfig.getPattern().getClazz());
		DomainAttribute da = RuleGeneratorHelper.findDomainAttributeForContextElement(controlPatternConfig, dc, contextElementType);
		return da;
	}

	public String formatForExcludedObject(String value) {
		return RuleGeneratorHelper.formatForExcludedObject(value);
	}

	public String getClassAttributeVarName(String className, String attrName) throws RuleGenerationException {
		String key = asKey(className, attrName);
		String varName = classAttributeVarMap.get(key);
		if (varName == null) {
			varName = AeMapper.getClassAttributeVarName(className, attrName);
			classAttributeVarMap.put(key, varName);
		}
		return varName;
	}

	public String getDeployLabelForAttribute(Reference reference) throws RuleGenerationException {
		return AeMapper.mapAttributeName(reference.getClassName(), reference.getAttributeName());
	}

	public String getDeployLabelForClass(Reference reference) throws RuleGenerationException {
		return getDeployLabelForClass(reference.getClassName());
	}

	public String getDeployLabelForClass(String className) throws RuleGenerationException {
		String deployLabel = deployLabelMap.get(className);
		if (deployLabel == null) {
			deployLabel = AeMapper.mapClassName(className);
			deployLabelMap.put(className, deployLabel);
		}
		return deployLabel;
	}

	public Deployment getDeploymentConfig() {
		return ConfigurationManager.getInstance().getServerConfigHelper().getDeploymentConfig();
	}

	public DomainClassLink[] getLinkage(String childName, String ancestorName) {
		return DomainManager.getInstance().getLinkage(childName, ancestorName);
	}

	public RuleGenerationConfigHelper getRuleGenerationConfiguration(TemplateUsageType usageType) {
		return ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType);
	}

	public Action getTestActionObject(int testTypeID) {
		return GuidelineFunctionManager.getInstance().getTestActionObject(testTypeID);
	}

	public boolean isStringDeployTypeForAttribute(Reference reference) throws RuleGenerationException {
		return DomainManager.getInstance().isStringDeployType(reference);
	}

	public String makeAEName(String name) {
		return AeMapper.makeAEName(name);
	}

	public void reportError(final String str) throws RuleGenerationException {
		guidelineRuleGenerator.reportError(str);
	}

}
