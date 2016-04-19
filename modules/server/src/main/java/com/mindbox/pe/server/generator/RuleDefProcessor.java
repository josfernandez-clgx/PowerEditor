package com.mindbox.pe.server.generator;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.LHSElement;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.TestCondition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.generator.aemodel.AbstractAeCompoundCondition;
import com.mindbox.pe.server.generator.aemodel.AbstractAeCondition;
import com.mindbox.pe.server.generator.aemodel.AbstractAeValue;
import com.mindbox.pe.server.generator.aemodel.AeAttributePattern;
import com.mindbox.pe.server.generator.aemodel.AeCellValue;
import com.mindbox.pe.server.generator.aemodel.AeColumnValue;
import com.mindbox.pe.server.generator.aemodel.AeFormulaValue;
import com.mindbox.pe.server.generator.aemodel.AeInstanceAttrValue;
import com.mindbox.pe.server.generator.aemodel.AeLiteralValue;
import com.mindbox.pe.server.generator.aemodel.AeNameValue;
import com.mindbox.pe.server.generator.aemodel.AeObjectPattern;
import com.mindbox.pe.server.generator.aemodel.AePatternSet;
import com.mindbox.pe.server.generator.aemodel.AeRule;
import com.mindbox.pe.server.generator.aemodel.AeTestFunctionPattern;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken;

class RuleDefProcessor {

	private final Logger logger = Logger.getLogger(getClass());

	private final RuleDefinition ruleDefinition;

	private final int columnNo;

	private final User user;

	private final GridTemplate template;

	RuleDefProcessor(int columnNo, RuleDefinition ruleDefinition, GridTemplate template, User user) {
		this.ruleDefinition = ruleDefinition;
		this.columnNo = columnNo;
		this.user = user;
		this.template = template;
	}

	void process(AeRule aerule) throws SapphireException {
		LHSElement ruleElement = toRuleElement(aerule.getLhs());
		CompoundLHSElement compoundElement = null;
		if (ruleElement instanceof CompoundLHSElement) {
			compoundElement = (CompoundLHSElement) ruleElement;
		}
		else if (ruleElement != null) {
			compoundElement = RuleElementFactory.getInstance().createAndCompoundCondition();
			compoundElement.add(ruleElement);
		}
		if (compoundElement != null) {
			ruleDefinition.updateRootConditions(compoundElement);
		}
	}

	private String stipExtraCharsIfSequence(String valueStr) {
		if (valueStr != null && valueStr.length() > 0 && valueStr.charAt(0) == '[' && valueStr.charAt(valueStr.length() - 1) == ']') {
			String[] values = valueStr.substring(1, valueStr.length() - 1).split("\\,");
			if (values != null) {
				StringBuilder buff = new StringBuilder();
				String valueToProcess;
				for (int i = 0; i < values.length; i++) {
					valueToProcess = (values[i] == null ? null : values[i].trim());
					if (i > 0) buff.append(',');
					if (valueToProcess != null && valueToProcess.length() > 0 && valueToProcess.charAt(0) == '"' && valueToProcess.charAt(valueToProcess.length() - 1) == '"') {
						buff.append(valueToProcess.substring(1, valueToProcess.length() - 1));
					}
					else {
						buff.append(valueToProcess);
					}
				}
				return buff.toString();
			}
		}
		return valueStr;
	}

	private Reference toReference(AeInstanceAttrValue attrValue) {
		return RuleElementFactory.getInstance().createReference(attrValue.getClassOrObjectName(), attrValue.getAttributeName());
	}

	private LHSElement toRuleElement(AbstractAeCompoundCondition abstractaecompoundcondition) throws SapphireException {
		if (abstractaecompoundcondition instanceof AePatternSet) {
			return toRuleElement((AePatternSet) abstractaecompoundcondition);
		}
		else if (abstractaecompoundcondition instanceof AeObjectPattern) {
			return toRuleElement((AeObjectPattern) abstractaecompoundcondition);
		}
		else {
			return null;
		}
	}

	private LHSElement toRuleElement(AbstractAeCondition abstractaecondition) throws SapphireException {
		if (abstractaecondition instanceof AbstractAeCompoundCondition)
			return toRuleElement((AbstractAeCompoundCondition) abstractaecondition);
		else if (abstractaecondition instanceof AeAttributePattern)
			return toRuleElement((AeAttributePattern) abstractaecondition);
		else if (abstractaecondition instanceof AeTestFunctionPattern) {
			return toRuleElement((AeTestFunctionPattern) abstractaecondition);
		}
		else {
			return null;
		}
	}

	private LHSElement toRuleElement(AeAttributePattern aeattributepattern) throws SapphireException {
		logger.debug(">>> toRuleElement(AeAttributePattern: " + aeattributepattern);
		Condition condition = RuleElementFactory.getInstance().createCondition();

		if (aeattributepattern.getClassName() == null) {
			aeattributepattern.setClassName((aeattributepattern.getParentObjectPattern() == null ? "ERROR_UNKNOWN" : aeattributepattern.getParentObjectPattern().getClassName()));
		}
		condition.setReference(RuleElementFactory.getInstance().createReference(aeattributepattern.getClassName(), aeattributepattern.getAttributeName()));

		// set op
		String function = aeattributepattern.getComparatorFunction();
		if (function.equals(AeAttributePattern.COMPARE_BETWEEN)) {
			condition.setOp((aeattributepattern.isNegated() ? Condition.OP_NOT_BETWEEN : Condition.OP_BETWEEN));
		}
		else if (function.equals(AeAttributePattern.COMPARE_EQ)) {
			condition.setOp((aeattributepattern.isNegated() ? Condition.OP_NOT_EQUAL : Condition.OP_EQUAL));
		}
		else if (function.equals(AeAttributePattern.COMPARE_GT)) {
			condition.setOp((aeattributepattern.isNegated() ? Condition.OP_LESS_EQUAL : Condition.OP_GREATER));
		}
		else if (function.equals(AeAttributePattern.COMPARE_GT_EQ)) {
			condition.setOp((aeattributepattern.isNegated() ? Condition.OP_LESS : Condition.OP_GREATER_EQUAL));
		}
		else if (function.equals(AeAttributePattern.COMPARE_LT)) {
			condition.setOp((aeattributepattern.isNegated() ? Condition.OP_GREATER_EQUAL : Condition.OP_LESS));
		}
		else if (function.equals(AeAttributePattern.COMPARE_LT_EQ)) {
			condition.setOp((aeattributepattern.isNegated() ? Condition.OP_GREATER : Condition.OP_LESS_EQUAL));
		}
		else if (function.equals(AeAttributePattern.COMPARE_MEMBER)) {
			condition.setOp((aeattributepattern.isNegated() ? Condition.OP_NOT_IN : Condition.OP_IN));
		}
		else if (function.equals(AeAttributePattern.COMPARE_NOT_EQ)) {
			condition.setOp((aeattributepattern.isNegated() ? Condition.OP_EQUAL : Condition.OP_NOT_EQUAL));
		}

		// set value
		AbstractAeValue value = aeattributepattern.getValue();
		logger.debug("... value = " + value);
		if (value instanceof AeCellValue) {
			if (columnNo < 1) {
				throw new SapphireException("CellValue is not allowed in a template rule");
			}
			condition.setValue(RuleElementFactory.getInstance().createValue(RuleElementFactory.getInstance().createColumnReference(columnNo)));
		}
		else if (value instanceof AeColumnValue) {
			condition.setValue(RuleElementFactory.getInstance().createValue(RuleElementFactory.getInstance().createColumnReference(((AeColumnValue) value).getColumnNumber())));
		}
		else if (value instanceof AeFormulaValue) {
			AeFormulaValue formulaValue = (AeFormulaValue) value;
			Reference attrRef = null;
			ColumnReference colRef = null;
			for (Iterator<Object> iter = formulaValue.getArguments().iterator(); iter.hasNext();) {
				Object formulaElement = (Object) iter.next();
				if (formulaElement instanceof AeInstanceAttrValue) {
					attrRef = toReference((AeInstanceAttrValue) formulaElement);
				}
				else if (formulaElement instanceof AeColumnValue) {
					colRef = RuleElementFactory.getInstance().createColumnReference(((AeColumnValue) formulaElement).getColumnNumber());
				}
			}
			condition.setValue(RuleElementFactory.getInstance().createValue(colRef, formulaValue.getOperator(), attrRef));
		}
		else if (value instanceof AeInstanceAttrValue) {
			condition.setValue(RuleElementFactory.getInstance().createValue(toReference((AeInstanceAttrValue) value)));
		}
		else if (value instanceof AeLiteralValue) {
			if (((AeLiteralValue) value).getValue() != null) {
				logger.debug("... literalValue Class: " + ((AeLiteralValue) value).getValue().getClass().getName());
			}
			String literalValueStr = (((AeLiteralValue) value).getValue() == null ? RuleGeneratorHelper.AE_NIL : value.toString());
			logger.debug("... literalValueStr (before)= " + literalValueStr);
			literalValueStr = stipExtraCharsIfSequence(literalValueStr);
			logger.debug("... literalValueStr (after) = " + literalValueStr);
			condition.setValue(RuleElementFactory.getInstance().createValue(literalValueStr));
		}
		else if (value instanceof AeNameValue) {
			condition.setValue(RuleElementFactory.getInstance().createValue(((AeNameValue) value).getName()));
		}
		return condition;
	}

	private LHSElement toRuleElement(AeObjectPattern aeobjectpattern) throws SapphireException {
		ExistExpression existExpression = RuleElementFactory.getInstance().createExistExpression(aeobjectpattern.getClassName());
		existExpression.setObjectName(aeobjectpattern.getObjectName());
		existExpression.setExcludedObjectName(aeobjectpattern.getExcludedObjectName());

		// initializeNames(aeobjectpattern);
		AbstractAeCondition abstractaecondition = aeobjectpattern.getNestedCondition();
		if (abstractaecondition != null) {
			LHSElement ruleElement = toRuleElement(abstractaecondition);
			if (ruleElement instanceof CompoundLHSElement && ((CompoundLHSElement) ruleElement).getType() == CompoundLHSElement.TYPE_AND) {
				for (int i = 0; i < ((CompoundLHSElement) ruleElement).size(); i++) {
					existExpression.getCompoundLHSElement().add(((CompoundLHSElement) ruleElement).get(i));
				}
			}
			else {
				existExpression.getCompoundLHSElement().add(ruleElement);
			}
		}
		return existExpression;
	}

	private LHSElement toRuleElement(AePatternSet aepatternset) throws SapphireException {
		CompoundLHSElement compoundElement = null;
		if (aepatternset.getConditionType() == AePatternSet.CONDITION_AND) {
			compoundElement = RuleElementFactory.getInstance().createAndCompoundCondition();
		}
		else {
			compoundElement = RuleElementFactory.getInstance().createOrCompoundCondition();
		}

		List<AbstractAeCondition> list = new java.util.ArrayList<AbstractAeCondition>(aepatternset.getConditions());
		for (int i = 0; i < list.size(); i++) {
			AbstractAeCondition abstractaecondition = list.get(i);
			LHSElement ruleElement = toRuleElement(abstractaecondition);
			if (ruleElement != null) {
				compoundElement.add(ruleElement);
			}
		}
		return compoundElement;
	}

	private LHSElement toRuleElement(AeTestFunctionPattern testPattern) throws ServletActionException {
		TestCondition testCondition = RuleElementFactory.getInstance().createTestCondition();

		// 1 Create a new test function definition and persist
		TestTypeDefinition testTypeDefinition = toTestTypeDefinition(testPattern, testCondition);
		int typeID = BizActionCoordinator.getInstance().save(testTypeDefinition, user);
		testTypeDefinition.setID(typeID);

		// 2 add a test function condition
		testCondition.setName(testPattern.getFunctionName());
		testCondition.setTestType(testTypeDefinition);

		return testCondition;
	}

	private TestTypeDefinition toTestTypeDefinition(AeTestFunctionPattern testPattern, TestCondition testCondition) {
		TestTypeDefinition def = new TestTypeDefinition(-1, testPattern.getFunctionName(), testPattern.getFunctionName() + " - Created by PowerEditor");

		StringBuilder buff = new StringBuilder();
		buff.append(testPattern.getFunctionName());
		buff.append("(");
		// process arguments
		int paramID = 1;
		for (int i = 0; i < testPattern.size(); i++) {
			if (i > 0) buff.append(",");
			AbstractAeValue element = testPattern.getParamAt(i);
			if (element instanceof AeColumnValue) {
				RuleGeneratorHelper.addActionParamDefinitionAndRuleParam(def, testCondition, paramID, template, ((AeColumnValue) element).getColumnNumber(), true);
				buff.append("\"%parameter " + paramID + "%\"");
				++paramID;
			}
			else if (element instanceof AeCellValue) {
				RuleGeneratorHelper.addActionParamDefinitionAndRuleParam(def, testCondition, paramID, template, columnNo, true);
				buff.append("\"%parameter " + paramID + "%\"");
				++paramID;
			}
			else if (element == null) {
				buff.append(RuleGeneratorHelper.AE_NIL);
			}
			else {
				String token = null;
				if (element.getNode() instanceof NodeToken) {
					token = ((NodeToken) element.getNode()).tokenImage;
				}
				else if (element instanceof AeNameValue) {
					token = ((AeNameValue) element).getName();
				}
				else {
					token = element.toString();
				}
				if (token.startsWith("\"%column ")) {
					int col = Integer.parseInt(token.substring(9, token.length() - 2));
					RuleGeneratorHelper.addActionParamDefinitionAndRuleParam(def, testCondition, paramID, template, col, true);
					buff.append("\"%parameter " + paramID + "%\"");
					++paramID;
				}
				else if (token.equals("\"%cellValue%\"")) {
					RuleGeneratorHelper.addActionParamDefinitionAndRuleParam(def, testCondition, paramID, template, columnNo, true);
					buff.append("\"%parameter " + paramID + "%\"");
					++paramID;
				}
				else {
					buff.append(token);
				}
			}
		}
		buff.append(")");
		def.setDeploymentRule(buff.toString());

		return def;
	}
}