package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.config.ControlPatternConfigHelper;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.DomainClassLinkPattern;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Pattern;

/**
 * Factory for {@link ObjectPattern}.
 *
 */
public final class ObjectPatternFactory {

	private final PatternFactoryHelper helper;
	private final AttributePatternFactory attributePatternFactory;

	public ObjectPatternFactory(PatternFactoryHelper helper) {
		if (helper == null) throw new NullPointerException("helper cannot be null");
		this.helper = helper;
		this.attributePatternFactory = new AttributePatternFactory(helper);
	}

	public ObjectPattern createControlPattern(TemplateUsageType usageType) throws RuleGenerationException {
		if (helper.getRuleGenerationConfiguration(usageType).getControlPatternConfig().isPatternOn()) {
			ControlPatternConfigHelper controlPatternConfig = helper.getRuleGenerationConfiguration(usageType).getControlPatternConfig();

			String className = helper.getDeployLabelForClass(controlPatternConfig.getPattern().getClazz());
			String variableName = helper.asVariableName(controlPatternConfig.getPattern().getClazz());
			ObjectPattern objectPattern = OptimizingObjectPattern.createShouldBeFirstInstance(className, variableName);
			objectPattern.setCanBeSkipped(false);

			GenericEntityType[] entityTypes = GenericEntityType.getAllGenericEntityTypes();
			for (int i = 0; i < entityTypes.length; i++) {
				if (entityTypes[i].isUsedInContext() && !controlPatternConfig.isDisallowed(entityTypes[i])) {
					DomainAttribute da = helper.findDomainAttributeForContextElement(controlPatternConfig, entityTypes[i].getName());
					String attributeName = da.getDeployLabel();
					String attrVarName = helper.asVariableName(da.getName());
					objectPattern.add(new ContextElementAttributePattern(attributeName, attrVarName, entityTypes[i], da.getDeployType().equals(DeployType.STRING)));
				}
			}
			return objectPattern;
		}
		else {
			return null;
		}
	}

	public ObjectPattern createEmptyObjectPattern(ExistExpression existExpression) throws RuleGenerationException {
		String existObjectNameVar = (UtilBase.isEmpty(existExpression.getObjectName())
				? helper.asVariableName(existExpression.getClassName())
				: "?" + existExpression.getObjectName());
		if (existExpression.getExcludedObjectName() != null) {
			existObjectNameVar += " " + helper.formatForExcludedObject(existExpression.getExcludedObjectName());
		}
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(helper.getDeployLabelForClass(existExpression.getClassName()), existObjectNameVar);
		objectPattern.setCanBeSkipped(false);
		return objectPattern;
	}

	public ObjectPattern createEmptyObjectPattern(ObjectPattern objectPattern) throws RuleGenerationException {
		ObjectPattern objectPatternToReturn = OptimizingObjectPattern.createNormalInstance(objectPattern.getClassName(), objectPattern.getVariableName());
		objectPatternToReturn.setCanBeSkipped(false);
		return objectPatternToReturn;
	}

	public ObjectPattern createEmptyObjectPattern(Reference reference) throws RuleGenerationException {
		ObjectPattern objectPattern = createEmptyObjectPattern(reference.getClassName());
		objectPattern.setCanBeSkipped(false);
		return objectPattern;
	}

	public ObjectPattern createEmptyObjectPattern(String className) throws RuleGenerationException {
		if (className == null) throw new NullPointerException("className cannot be null");
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(helper.getDeployLabelForClass(className), helper.asVariableName(className));
		objectPattern.setCanBeSkipped(false);
		return objectPattern;
	}

	public ObjectPattern createLinkObjectPattern(DomainClassLinkPattern linkPattern, TemplateUsageType usageType) throws RuleGenerationException {
		if (usageType == null) throw new NullPointerException("usageType cannot be null");
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(
				helper.getDeployLabelForClass(linkPattern.getDomainClassLink().getParentName()),
				helper.asVariableName(linkPattern.getDomainClassLink().getParentName()));
		objectPattern.setCanBeSkipped(false);
		objectPattern.add(attributePatternFactory.createLinkAttributePattern(linkPattern, usageType));
		return objectPattern;
	}

	public ObjectPattern createObjectPattern(Condition condition, TemplateUsageType usageType) throws RuleGenerationException {
		return createObjectPattern(condition, usageType, null);
	}

	public ObjectPattern createObjectPattern(Condition condition, TemplateUsageType usageType, String objectVarOverride) throws RuleGenerationException {
		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);

		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(
				helper.getDeployLabelForClass(condition.getReference()),
				helper.asVariableName(
						condition.getReference().getClassName(),
						(UtilBase.isEmpty(condition.getObjectName())
								? (UtilBase.isEmpty(objectVarOverride) ? null : objectVarOverride)
								: helper.asVariableName(condition.getObjectName()))));
		objectPattern.add(attributePattern);
		objectPattern.setCanBeSkipped(true);
		return objectPattern;
	}

	/**
	 * 
	 * @param usageType usageType
	 * @return object pattern for the request pattern; may be <code>null</code>
	 * @throws RuleGenerationException on error
	 */
	public ObjectPattern createRequestPattern(TemplateUsageType usageType) throws RuleGenerationException {
		Pattern requestPatternConfig = helper.getRuleGenerationConfiguration(usageType).getRequestPatternConfig();
		if (requestPatternConfig != null && ConfigUtil.isPatternOn(requestPatternConfig)) {
			String className = requestPatternConfig.getPrefix() + requestPatternConfig.getClazz();
			String variableName = "?" + requestPatternConfig.getClazz();
			ObjectPattern objectPattern = OptimizingObjectPattern.createShouldBeFirstInstance(className, variableName);
			objectPattern.setCanBeSkipped(false);
			objectPattern.add(new FocusOfAttentionAttributePattern(requestPatternConfig.getPrefix() + "focus-of-attention"));
			objectPattern.add(new TimeSliceAttributePattern(requestPatternConfig.getPrefix() + "current-time-slice", RuleGeneratorHelper.TIME_SLICE_VARIABLE));
			return objectPattern;
		}
		else {
			return null;
		}
	}

	public ObjectPattern createSingleAttrbiuteObjectPattern(Reference reference) throws RuleGenerationException {
		ObjectPattern objectPattern = createEmptyObjectPattern(reference);
		if (!UtilBase.isEmpty(reference.getAttributeName())) {
			objectPattern.add(new StaticTextAttributePattern(helper.getDeployLabelForAttribute(reference), helper.asVariableName(reference.getAttributeName())));
		}
		return objectPattern;
	}
}
