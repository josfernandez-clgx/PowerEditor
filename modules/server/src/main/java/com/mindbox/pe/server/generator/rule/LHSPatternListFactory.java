package com.mindbox.pe.server.generator.rule;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainClassLink;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.rule.TestCondition;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.model.DomainClassLinkPattern;

/**
 * Factory for {@link LHSPatternList}.
 * Usage:
 * <ol><li>Create a new instance; e.g., <code>new LHSPatternListFactory(helper);</code></li>
 * <li>Call {@link #produce(RuleDefinition)}</li>
 * </ol>
 * This is not thread-safe. At most one invocation of {@link #produce(RuleDefinition)} on the same object is allowed. 
 *
 */
public final class LHSPatternListFactory {


	/**
	 * 
	 * @param condition
	 * @return
	 * @throws IllegalArgumentException if condition doesn't contain a refernece in value
	 */
	private static Reference extractReferenceValue(Condition condition) {
		Value value = condition.getValue();
		if (value == null) return null;
		if (value instanceof Reference) {
			return (Reference) value;
		}
		else if (value instanceof MathExpressionValue) {
			return ((MathExpressionValue) value).getAttributeReference();
		}
		else {
			throw new IllegalArgumentException("Invalid condition type: doesn't have reference in value");
		}
	}

	private final PatternFactoryHelper helper;
	private final ObjectPatternFactory objectPatternFactory;
	private final AttributePatternFactory attributePatternFactory;
	private final FunctionCallPatternFactory testPatternFactory;
	private final Logger logger = Logger.getLogger(getClass());
	private TemplateUsageType usageType;

	public LHSPatternListFactory(PatternFactoryHelper helper) {
		if (helper == null) throw new NullPointerException("helper cannot be null");
		this.helper = helper;
		this.objectPatternFactory = new ObjectPatternFactory(helper);
		this.attributePatternFactory = new AttributePatternFactory(helper);
		this.testPatternFactory = new FunctionCallPatternFactory(helper, true);
	}

	public LHSPatternList produce(RuleDefinition ruleDefinition, TemplateUsageType usageType) throws RuleGenerationException {
		logger.debug(">>> produce: " + usageType + "; " + ruleDefinition);
		this.usageType = usageType;

		LHSPatternList patternList = new OptimizingLHSPatternList(LHSPatternList.TYPE_AND);
		if (!ruleDefinition.isEmpty()) {
			process(patternList, ruleDefinition.getRootElement(), null);
		}
		logger.debug("    produce: processing control pattern...");
		// Insert control pattern
		ObjectPattern objectPattern = objectPatternFactory.createControlPattern(usageType);
		if (objectPattern != null) {
			patternList.insert(objectPattern, true);
		}

		logger.debug("    produce: processing request pattern...");
		// Insert request pattern
		objectPattern = objectPatternFactory.createRequestPattern(usageType);
		if (objectPattern != null) {
			patternList.insert(objectPattern, true);
		}
		logger.debug("<<< produce: size = " + patternList.size());
		return patternList;
	}

	private void process(LHSPatternList parentPatternList, CompoundLHSElement elements, String objectVarOverride) throws RuleGenerationException {
		logger.debug(">>> process: " + elements);
		LHSPatternList childPatternList;
		switch (elements.getType()) {
		case CompoundLHSElement.TYPE_AND: {
			if (parentPatternList.getType() == LHSPatternList.TYPE_OR) {
				childPatternList = new OptimizingLHSPatternList(LHSPatternList.TYPE_AND);
				parentPatternList.append(childPatternList);
			}
			else {
				childPatternList = parentPatternList;
			}
			process_aux(childPatternList, elements, objectVarOverride);
			break;
		}
		case CompoundLHSElement.TYPE_OR: {
			// don't use an optimizing pattern list for OR
			childPatternList = new DefaultLHSPatternList(LHSPatternList.TYPE_OR);
			parentPatternList.append(childPatternList);
			process_aux(childPatternList, elements, objectVarOverride);
			break;
		}
		case CompoundLHSElement.TYPE_NOT: {
			childPatternList = new OptimizingLHSPatternList(LHSPatternList.TYPE_NOT);
			parentPatternList.append(childPatternList);
			process_aux(childPatternList, elements, objectVarOverride);
			// NOTE: must add the "NOT" pattern after processing child elements (TT 1953)
			break;
		}
		default:
			throw new RuleGenerationException("Invalid compound element type found " + elements);
		}
	}

	private void process_aux(LHSPatternList patternListToAddElements, CompoundLHSElement elements, String objectVarOverride) throws RuleGenerationException {
		for (int i = 0; i < elements.size(); ++i) {
			RuleElement element = elements.get(i);
			if (element instanceof CompoundLHSElement) {
				process(patternListToAddElements, (CompoundLHSElement) element, null);
			}
			else if (element instanceof Condition) {
				process(patternListToAddElements, (Condition) element, objectVarOverride);
			}
			else if (element instanceof ExistExpression) {
				// if parent is OR, wrap all with an AND
				if (elements.getType() == CompoundLHSElement.TYPE_OR) {
					LHSPatternList childPatternList = new OptimizingLHSPatternList(LHSPatternList.TYPE_AND);
					patternListToAddElements.append(childPatternList);
					process(childPatternList, (ExistExpression) element, childPatternList);
				}
				else {
					process(patternListToAddElements, (ExistExpression) element, patternListToAddElements);
				}
			}
			else if (element instanceof TestCondition) {
				process(patternListToAddElements, (TestCondition) element);
			}
		}
	}

	private void process(LHSPatternList patternList, ExistExpression existExpression, LHSPatternList patternListToAddNextedElementsInExist) throws RuleGenerationException {
		logger.debug(">>> process(ExistExpression): " + existExpression);
		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(existExpression);
		patternList.append(objectPattern);

		// add link attribute pattern/object patterms for each child of the exist expression
		processChildLinkPatterns(objectPattern, existExpression.getClassName(), existExpression.getCompoundLHSElement(), patternList);

		// process elements in the exist expression
		process(patternListToAddNextedElementsInExist, existExpression.getCompoundLHSElement(), (existExpression.getObjectName() == null ? null : "?" + existExpression.getObjectName()));
	}

	private void processChildLinkPatterns(ObjectPattern objectPattern, String parentClassName, CompoundLHSElement childElements, LHSPatternList patternList) throws RuleGenerationException {
		for (int i = 0; i < childElements.size(); i++) {
			RuleElement element = childElements.get(i);
			logger.debug("... processChildLinkPatterns: childElement = " + element);
			if (element instanceof Condition) {
				// link to class of the condition's reference
				processChildLinkPatterns(objectPattern, parentClassName, ((Condition) element).getReference().getClassName(), null, patternList);
				if (((Condition) element).getValue() instanceof Reference) {
					// link to the class of the reference value of the condition
					processChildLinkPatterns(objectPattern, parentClassName, ((Reference) ((Condition) element).getValue()).getClassName(), null, patternList);
				}
			}
			else if (element instanceof ExistExpression) {
				// link to the class name of the exist expression
				processChildLinkPatterns(objectPattern, parentClassName, ((ExistExpression) element).getClassName(), ((ExistExpression) element).getObjectName(), patternList);
			}
			else if (element instanceof CompoundLHSElement) {
				processChildLinkPatterns(objectPattern, parentClassName, (CompoundLHSElement) element, patternList);
			}
		}

	}

	private void processChildLinkPatterns(ObjectPattern objectPattern, String parentClassName, String childClassName, String objectName, LHSPatternList patternList) throws RuleGenerationException {
		if (parentClassName.equalsIgnoreCase(childClassName)) return;
		logger.debug(">>> processChildLinkPatterns: " + parentClassName + "," + childClassName + ",object=" + objectName);
		DomainClassLink[] dcLinks = helper.getLinkage(childClassName, parentClassName);
		if (dcLinks == null || dcLinks.length == 0) {
			logger.warn("No linkage generated for child " + childClassName + " from " + parentClassName + " - NO LINKAGE EXISTS");
			// make it configurable to turn this report off
			if (UtilBase.asBoolean(helper.getDeploymentConfig().isReportMissingLink(), true)) {
				helper.reportError("WARNING: No linkage generated for child " + childClassName + " from " + parentClassName + " - NO LINKAGE EXISTS");
			}
		}
		else {
			logger.debug("... processChildLinkPatterns_aux: dcLinks.length = " + dcLinks.length);
			AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(dcLinks[0], (dcLinks.length == 1 ? objectName : null), usageType);
			//ObjectPattern objectPatternToCheckForConflict = patternList.
			if (patternList.hasConflictingAttributePattern(objectPattern.getVariableName(), attributePattern)) {
				//			if (OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern)) {
				ObjectPattern objectPatternToUse = objectPatternFactory.createEmptyObjectPattern(objectPattern);
				objectPatternToUse.add(attributePattern);
				patternList.append(objectPatternToUse);
			}
			else {
				objectPattern.add(attributePattern);
			}

			for (int i = 1; i < dcLinks.length; i++) {
				DomainClassLinkPattern linkPattern = new DomainClassLinkPattern(dcLinks[i]);
				if (i == dcLinks.length - 1) linkPattern.setObjectName(objectName);
				patternList.append(objectPatternFactory.createLinkObjectPattern(linkPattern, usageType));
			}
		}
	}

	private void process(LHSPatternList patternList, TestCondition testCondition) throws RuleGenerationException {
		FunctionCallPattern testPattern = testPatternFactory.createFunctionCallPattern(testCondition, usageType, patternList);
		patternList.append(testPattern);
	}

	private void process(LHSPatternList patternList, Condition condition, String objectVarOverride) throws RuleGenerationException {
		logger.debug(">>> process(Condition): " + condition);
		ObjectPattern objectPattern = objectPatternFactory.createObjectPattern(condition, usageType, objectVarOverride);

		if (condition.hasReferenceValue()) {
			// create an object pattern for the referenced reference
			Reference referenceValue = extractReferenceValue(condition);
			ObjectPattern refObjectPattern = objectPatternFactory.createSingleAttrbiuteObjectPattern(referenceValue);
			AttributePattern refAttributePattern = refObjectPattern.get(0);
			if (!condition.getReference().getClassName().equalsIgnoreCase(referenceValue.getClassName()) || OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, refAttributePattern)) {
				// if parent is NOT or OR and condition's value is attribute ref, wrap patterns with AND
				if ((patternList.getType() == LHSPatternList.TYPE_OR || patternList.getType() == LHSPatternList.TYPE_NOT)) {
					// write AND wrapper
					LHSPatternList andPatternList = new OptimizingLHSPatternList(LHSPatternList.TYPE_AND);
					andPatternList.append(refObjectPattern);
					andPatternList.append(objectPattern);
					patternList.append(andPatternList);
					refObjectPattern.addMustBeBeforeVariable(objectPattern.getVariableName());
				}
				else {
					patternList.append(objectPattern);
					patternList.insertBefore(refObjectPattern, objectPattern.getVariableName());
					refObjectPattern.addMustBeBeforeVariable(objectPattern.getVariableName());
				}
			}
			else {
				objectPattern.insert(refAttributePattern);
				patternList.append(objectPattern);
			}
		}
		else {
			patternList.append(objectPattern);
		}
	}
}