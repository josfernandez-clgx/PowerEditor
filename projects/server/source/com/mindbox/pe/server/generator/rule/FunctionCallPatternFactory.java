package com.mindbox.pe.server.generator.rule;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.IDNameObject;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.TestCondition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.config.LineagePatternConfigSet;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.AeRuleBuilder;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.aemodel.AeLiteralValue;
import com.mindbox.pe.server.generator.aemodel.AeNameValue;
import com.mindbox.pe.server.generator.aemodel.AeRule;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;

/**
 * Factory for producing {@link FunctionCallPattern} implementation.
 *
 */
public final class FunctionCallPatternFactory {

	//	 TODO Kim: 2007-01-09: replace old parser code (com.mindbox.server.parser.jtb.rule.syntaxtree.Action) when ready to do so

	/**
	 * Tests if the specified token is the specified literal. This works even if token is literal
	 * enclosed with double quotes; i.e., &quot;<code>literal</code>&quot;.
	 * 
	 * @param token
	 * @param literal
	 * @return
	 */
	private static final boolean isLiteral(String token, String literal) {
		return token != null && token.indexOf(literal) >= 0 && token.length() <= literal.length() + 2;
	}

	private static final boolean endsWith(String token, String str) {
		return token != null && (token.endsWith(str) || token.endsWith(str + '"'));
	}

	private final PatternFactoryHelper helper;
	private final boolean forTestCondition;
	private final Logger logger = Logger.getLogger(getClass());
	private final FunctionArgumentFactory functionArgumentFactory;

	public FunctionCallPatternFactory(PatternFactoryHelper helper, boolean forTestCondition) {
		this.helper = helper;
		this.forTestCondition = forTestCondition;
		functionArgumentFactory = new FunctionArgumentFactory(helper);
	}

	public FunctionCallPattern createFunctionCallPattern(TestCondition testCondition, TemplateUsageType usageType, LHSPatternList patternList)
			throws RuleGenerationException {
		TestTypeDefinition testTypeDef = testCondition.getTestType();
		if (testTypeDef == null) { throw new RuleGenerationException("No type defined for Test Condition: " + testCondition); }

		// obtain parsed objects from the old rule parser
		Action action = helper.getTestActionObject(testTypeDef.getID());
		if (action == null) {
			helper.reportError("The rule of test condition " + testTypeDef + " is invalid");
			throw new RuleGenerationException("The rule of test condition " + testTypeDef + " is invalid; action not found - possible rule syntax error");
		}
		logger.debug("    createTestPattern(TestCondition): parsing RHS...");

		AeRule aeRule = new AeRule();
		new AeRuleBuilder().visit(action, aeRule);

		// extract function name
		String functionName = action.name.nodeToken.tokenImage;
		if (functionName == null) {
			helper.reportError("The rule of test condition " + testTypeDef + " is invalid: action doesn't have function name");
			throw new RuleGenerationException("The rule of test condition " + testTypeDef + " is invalid: action doesn't have function name");
		}

		// extract arguments to the function
		FunctionCallPattern testPattern = new DefaultFunctionCallPattern(helper.makeAEName(functionName));
		processActionArgs(null, testPattern, new TestConditionParamContainer(testCondition), aeRule.getActionParms().iterator(), usageType, patternList);
		return testPattern;
	}

	public FunctionCallPattern createFunctionCallPattern(IDNameObject templateIDName, RuleDefinition ruleDefinition, LHSPatternList patternList)
			throws RuleGenerationException {
		logger.debug(">>> createFunctionCallPattern: " + ruleDefinition);
		// check RHS
		ActionTypeDefinition actionType = ruleDefinition.getRuleAction().getActionType();
		if (actionType == null) {
			helper.reportError("The rule has no action specified in the RHS");
			throw new RuleGenerationException("The rule has no action specified in the RHS");
		}

		Action action = GuidelineFunctionManager.getInstance().getActionObject(actionType.getID());
		if (action == null) {
			helper.reportError("The RHS of the rule is invalid: action not found - possible rule syntax error");
			throw new RuleGenerationException("The RHS of the rule is invalid: action not found - possible rule syntax error");
		}
		logger.debug("    createFunctionCallPattern: parsing RHS...");

		// obtain parsed objects from the old rule parser
		AeRule aeRule = (AeRule) ruleDefinition.getOldParserObjectForAction();
		if (aeRule == null) {
			logger.debug("    writeSingleRule: generating old parser object tree for " + action);
			aeRule = new AeRule();
			new AeRuleBuilder().visit(action, aeRule);
			ruleDefinition.setOldParserObjectForAction(aeRule);
		}

		String actionFunctionName = action.name.nodeToken.tokenImage;
		if (actionFunctionName == null) {
			helper.reportError("The RHS of the rule is invalid: action doesn't have function name");
			throw new RuleGenerationException("The RHS of the rule is invalid: action doesn't have function name");
		}

		FunctionCallPattern functionCallPattern = new DefaultFunctionCallPattern(helper.makeAEName(actionFunctionName));
		processActionArgs(
				templateIDName,
				functionCallPattern,
				new RuleDefinitionParamContainer(ruleDefinition),
				aeRule.getActionParms().iterator(),
				ruleDefinition.getUsageType(),
				patternList);
		return functionCallPattern;
	}

	// TODO Kim, 2007-03-13: refactor as a seperate class for easier unit testing
	
	private void processActionArgs(IDNameObject templateIDName, FunctionCallPattern functionCallPattern, FunctionParameterContainer functionContainer,
			Iterator<com.mindbox.pe.server.generator.aemodel.AbstractAeValue> argIterator, TemplateUsageType usageType, LHSPatternList patternList) throws RuleGenerationException {
		logger.debug(">>> processActionArgs: " + functionCallPattern);
		while (argIterator.hasNext()) {
			String currToken = toPrintableValue(argIterator.next(), patternList);
			logger.debug("    processActionArgs: currToken = " + currToken);

			if (currToken.equalsIgnoreCase("$create")) {
				FunctionCallPattern subPattern = new DefaultFunctionCallPattern("create$");
				processActionArgs(templateIDName, subPattern, functionContainer, argIterator, usageType, patternList);
				functionCallPattern.add(subPattern);
			}
			else if (currToken.equalsIgnoreCase("create$")) {
				// exit so that recursion ends (sub test pattern has ended)
				return;
			}
			else if (currToken.startsWith("\"%column ")) {
				int col = Integer.parseInt(currToken.substring(9, currToken.length() - 2));
				ValueSlot valueSlot = new ColumnReferencePatternValueSlot(col);
				functionCallPattern.add(valueSlot);
			}
			else if (currToken.startsWith("\"%parameter ")) {
				int paramNo = Integer.parseInt(currToken.substring(12, currToken.lastIndexOf("%")));
				functionCallPattern.add(functionArgumentFactory.createFunctionArgument(paramNo, functionContainer, patternList));
			}

			// To fix TT 1042, don't use indexOf(<literal>) >= 0. Use more accurate tests.
			else if (isLiteral(currToken, "%cellValue%")) {
				if (forTestCondition)
					helper.reportError("%cellValue% is not supported for Test Conditions");
				else {
					functionCallPattern.add(new CellValueValueSlot());
				}
			}
			else if (isLiteral(currToken, "%ruleID%")) {
				if (forTestCondition)
					helper.reportError("%ruleID% not supported for Test Conditions");
				else {
					functionCallPattern.add(new RuleIDValueSlot());
				}
			}
			else if (isLiteral(currToken, "%ruleName%")) {
				if (forTestCondition)
					helper.reportError("%ruleName% not supported for Test Conditions");
				else {
					functionCallPattern.add(new RuleNameValueSlot());
				}
			}
			else if (isLiteral(currToken, "%templateID%")) {
				if (forTestCondition) {
					helper.reportError("%templateID% not supported for Test Conditions");
				}
				else {
					functionCallPattern.add(new StaticFunctionArgument(String.valueOf(templateIDName.getID())));
				}
			}
			else if (isLiteral(currToken, "%templateName%")) {
				if (forTestCondition) {
					helper.reportError("%templateName% not supported for Test Conditions");
				}
				else {
					functionCallPattern.add(new StaticFunctionArgument("\"" + templateIDName.getName() + "\""));
				}
			}
			else if (isLiteral(currToken, "%rowNumber%")) {
				functionCallPattern.add(new RowNumberValueSlot());
			}
			else if (isLiteral(currToken, "%activationSpan%")) {
				functionCallPattern.add(new ActivationSpanValueSlot());
			}
			else if (isLiteral(currToken, "%lineageID%")) {
				// TODO Unknown as to what PE should do when more than one lineage is specified --
				//      just write the first one for now
				LineagePatternConfigSet lineagePatternSet = helper.getRuleGenerationConfiguration(usageType).getLineagePatternConfigSet();
				if (lineagePatternSet.size() > 0) {
					RuleGenerationConfiguration.LineagePatternConfig[] configs = lineagePatternSet.getLineagePatternConfigs(lineagePatternSet.getPrefix()[0]);
					if (configs.length > 0) {
						functionCallPattern.add(new StaticFunctionArgument("?" + configs[0].getVariable()));
					}
				}
			}
			else if (isLiteral(currToken, "%activationDate%")) {
				functionCallPattern.add(new DatePropertyValueSlot(DatePropertyValueSlot.DATE_TYPE_ACTIVATION_DATE));
			}
			else if (isLiteral(currToken, "%expirationDate%")) {
				functionCallPattern.add(new DatePropertyValueSlot(DatePropertyValueSlot.DATE_TYPE_EXPIRATION_DATE));
			}
			// Note: categoryID check must come before *ID% check (fix for TT 1836)
			else if (isLiteral(currToken, "%categoryID%")) {
				if (forTestCondition)
					helper.reportError("%categoryID% is not supported for Test Conditions");
				else {
					GenericEntityType productType = GenericEntityType.forName("product");
					if (productType != null && productType.hasCategory()) {
						functionCallPattern.add(new CategoryIDValueSlot(productType));
					}
					else {
						functionCallPattern.add(new StaticFunctionArgument(RuleGeneratorHelper.AE_NIL));
					}
				}
			}
			else if (isLiteral(currToken, "%categoryName%")) {
				if (forTestCondition)
					helper.reportError("%categoryName% not supported for Test Conditions");
				else {
					GenericEntityType productType = GenericEntityType.forName("product");
					if (productType != null && productType.hasCategory()) {
						functionCallPattern.add(new CategoryNameValueSlot(productType));
					}
					else {
						functionCallPattern.add(new StaticFunctionArgument(RuleGeneratorHelper.AE_NIL));
					}
				}
			}
			else if (endsWith(currToken, "ID%")) {
				if (forTestCondition) {
					helper.reportError(currToken + " is not supported for Test Conditions");
				}
				else {
					String str = AeMapper.stripQuotes(currToken);
					str = str.substring(1, str.indexOf("ID%")); // first character must be a '%', strip it off
					logger.debug(" str = " + str);
					GenericEntityType type = GenericEntityType.forName(str);
					if (type == null) {
						logger.warn(currToken + " is not valid");
						helper.reportError("WARNING: " + currToken + " is not valid");
					}
					else {
						RuleGenerationConfiguration.ControlPatternConfig controlPatternConfig = helper.getRuleGenerationConfiguration(usageType).getControlPatternConfig();
						DomainAttribute da = helper.findDomainAttributeForContextElement(controlPatternConfig, type.toString());
						if (da == null) {
							throw new RuleGenerationException("No domain attribute found for control pattern for type " + type);
						}
						else {
							// Add entity id pattern value slot (fix to TT 1836)
							functionCallPattern.add(new EntityIDValueSlot(type, helper.asVariableName(da.getName())));
						}
					}
				}
			}
			else if (isLiteral(currToken, "%context%")) {
				if (forTestCondition)
					helper.reportError("%context% is not supported for Test Conditions");
				else {
					functionCallPattern.add(new ContextValueSlot());
				}
			}
			else {
				functionCallPattern.add(new StaticFunctionArgument(currToken));
			}
		}
	}
	
	public String toPrintableValue(Object element, LHSPatternList patternList) throws RuleGenerationException {
		if (element == null) { return RuleGeneratorHelper.AE_NIL; }
		if (element instanceof AeNameValue) {
			return toPrintableValue((AeNameValue) element);
		}
		else if (element instanceof AeLiteralValue) {
			return toPrintableValue((AeLiteralValue) element, patternList);
		}
		else {
			return element.toString();
		}
	}

	private String toPrintableValue(AeNameValue nameValue) {
		return helper.makeAEName(nameValue.getName());
	}

	private String toPrintableValue(AeLiteralValue literalValue, LHSPatternList patternList) throws RuleGenerationException {
		String value = literalValue.toString();
		if (value == null || value.length() < 1) { return RuleGeneratorHelper.AE_NIL; }
		boolean isStringLiteral = false;
		if (value.length() > 1 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
			isStringLiteral = true;
			value = value.substring(1, value.length() - 1);
		}
		if (value.charAt(0) == '|' && value.charAt(value.length() - 1) == '|') {
			// handle class or class.attr reference
			int index = value.indexOf(".");
			if (index == -1) {
				// get class reference
				String className = value.substring(1, value.length() - 1).toUpperCase();
				String varName = helper.getClassAttributeVarName(className, null);
				patternList.append(new ObjectPatternFactory(helper).createSingleAttrbiuteObjectPattern(RuleElementFactory.getInstance().createReference(className, null)));
				return varName;
			}
			else {
				// get attribute reference
				String className = value.substring(1, index);
				String attrName = value.substring(index + 1, value.length() - 1).toUpperCase();
				patternList.append(new ObjectPatternFactory(helper).createSingleAttrbiuteObjectPattern(RuleElementFactory.getInstance().createReference(className, attrName)));
				return helper.getClassAttributeVarName(className, attrName);
			}
		}
		else if (isStringLiteral) {
			return '"' + value + '"';
		}
		else {
			return value;
		}
	}

}
