package com.mindbox.pe.common;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.FunctionCall;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.TestCondition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class TemplateUtil {

	public static GridTemplateColumn generateColumnsFromAttribute(DomainClassProvider domainClassProvider, TemplateUsageType usage, String reference,
			int columnNo) {
		return generateColumnsFromAttribute(domainClassProvider, usage, reference, Condition.OP_EQUAL, columnNo, false);
	}

	public static GridTemplateColumn generateColumnsFromAttribute(DomainClassProvider domainClassProvider, TemplateUsageType usage, String reference,
			int op, int columnNo) {
		return generateColumnsFromAttribute(domainClassProvider, usage, reference, op, columnNo, true);
	}

	private static GridTemplateColumn generateColumnsFromAttribute(DomainClassProvider domainClassProvider, TemplateUsageType usage,
			String reference, int op, int columnNo, boolean checkForRange) {
		String[] strs = reference.split("\\.");
		if (strs.length < 2) { throw new IllegalArgumentException("Invalid reference: " + reference); }
		DomainClass dc = domainClassProvider.getDomainClass(strs[0]);
		if (dc == null) { throw new IllegalArgumentException("Invalid reference - class not found: " + reference); }
		DomainAttribute attribute = dc.getDomainAttribute(strs[1]);
		if (attribute == null) { throw new IllegalArgumentException("Invalid reference - attribute not found: " + reference); }

		GridTemplateColumn column = new GridTemplateColumn();
		column.setID(columnNo);
		column.setName(attribute.getName() + columnNo);
		column.setTitle(attribute.getDisplayLabel());
		column.setDescription(attribute.getName() + " column (auto-generated)");
		column.setColumnWidth(100);
		column.setUsageType(usage);
		column.setColor("default");
		column.setFont("arial");

		ColumnDataSpecDigest digest = new ColumnDataSpecDigest();
		digest.setAttributeMap(dc.getName() + "." + attribute.getName());
		if (attribute.hasEnumValue()) {
			digest.setType("EnumList");
			digest.setIsBlankAllowed(true);
			digest.setIsEnumValueNeedSorted(true);
			if (checkForRange)
				digest.setIsMultiSelectAllowed(op == Condition.OP_IN || op == Condition.OP_NOT_IN);
			else
				digest.setIsMultiSelectAllowed(true);
			digest.setIsLHSAttributeVisible(false);
			digest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		}
		else if (attribute.getDeployType() == DeployType.BOOLEAN) {
			digest.setType("Boolean");
			digest.setIsBlankAllowed(false);
		}
		else if (attribute.getDeployType() == DeployType.CODE 
				|| attribute.getDeployType() == DeployType.STRING) {
			digest.setType("String");
			digest.setIsBlankAllowed(true);
		}
		else if (attribute.getDeployType() == DeployType.SYMBOL) {
			digest.setType("Symbol");
			digest.setIsBlankAllowed(true);
		}
		else if (attribute.getDeployType() == DeployType.CURRENCY) {
			if (!checkForRange || op == Condition.OP_BETWEEN || op == Condition.OP_NOT_BETWEEN) {
				digest.setType("CurrencyRange");
			}
			else {
				digest.setType("Currency");
			}
			digest.setIsBlankAllowed(true);
			digest.setMinValue("0");
		}
		else if (attribute.getDeployType() == DeployType.FLOAT || attribute.getDeployType() == DeployType.PERCENT) {
			if (!checkForRange || op == Condition.OP_BETWEEN || op == Condition.OP_NOT_BETWEEN) {
				digest.setType("FloatRange");
			}
			else {
				digest.setType("Float");
			}
			digest.setIsBlankAllowed(true);
		}
		else if (attribute.getDeployType() == DeployType.DATE) {
			digest.setType("Date");
			digest.setIsBlankAllowed(true);
		}
		else if (attribute.getDeployType() == DeployType.INTEGER) {
			if (!checkForRange || op == Condition.OP_BETWEEN || op == Condition.OP_NOT_BETWEEN) {
				digest.setType("IntegerRange");
			}
			else {
				digest.setType("Integer");
			}
			digest.setIsBlankAllowed(true);
			digest.setMinValue("0");
		}
		else {
			digest.setType("String");
			digest.setIsBlankAllowed(true);
		}
		column.setDataSpecDigest(digest);

		return column;
	}

	public static void generateAndAddColumns(GridTemplate template, ActionTypeDefinition actionType) {
		FunctionParameterDefinition[] paramDefs = actionType.getParameterDefinitions();
		for (int i = 0; i < paramDefs.length; i++) {
			if (paramDefs[i] != null) {
				GridTemplateColumn column = new GridTemplateColumn();
				column.setID(template.getNumColumns() + 1);
				column.setName(paramDefs[i].getName() + column.getID());
				column.setTitle(paramDefs[i].getName());
				column.setDescription(paramDefs[i].getName() + " column (auto-generated)");
				column.setColumnWidth(100);
				column.setUsageType(template.getUsageType());
				column.setColor("default");
				column.setFont("arial");

				ColumnDataSpecDigest digest = new ColumnDataSpecDigest();
				if (paramDefs[i].getDeployType() == DeployType.BOOLEAN) {
					digest.setType("Boolean");
					digest.setIsBlankAllowed(false);
				}
				else if (paramDefs[i].getDeployType() == DeployType.CODE || paramDefs[i].getDeployType() == DeployType.SYMBOL) {
					digest.setType("Symbol");
					digest.setIsBlankAllowed(true);
				}
				else if (paramDefs[i].getDeployType() == DeployType.STRING) {
					digest.setType("String");
					digest.setIsBlankAllowed(true);
				}
				else if (paramDefs[i].getDeployType() == DeployType.CURRENCY) {
					digest.setType("Currency");
					digest.setIsBlankAllowed(true);
					digest.setMinValue("0");
				}
				else if (paramDefs[i].getDeployType() == DeployType.FLOAT || paramDefs[i].getDeployType() == DeployType.PERCENT) {
					digest.setType("Float");
					digest.setIsBlankAllowed(true);
				}
				else if (paramDefs[i].getDeployType() == DeployType.DATE) {
					digest.setType("Date");
					digest.setIsBlankAllowed(true);
				}
				else if (paramDefs[i].getDeployType() == DeployType.INTEGER) {
					digest.setType("Integer");
					digest.setIsBlankAllowed(true);
					digest.setMinValue("0");
				}
                else if (paramDefs[i].getDeployType() == DeployType.ENTITY_LIST) {
                    GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
                    if (types != null && types.length > 0) {
                        digest.setEntityType(types[0].toString());
                    }
                    digest.setType(ColumnDataSpecDigest.TYPE_ENTITY);
                    digest.setIsBlankAllowed(true);            
                    digest.setAllowCategory(Constants.VALUE_YES);
                    digest.setAllowEntity(Constants.VALUE_YES);                    
                    digest.setMultipleSelect(Constants.VALUE_YES);
                }
				else {
					digest.setType("String");
					digest.setIsBlankAllowed(true);
				}
				column.setDataSpecDigest(digest);

				template.addGridTemplateColumn(column);
			}
		}
	}

	public static void generateAndSetRuleDefinition(GridTemplate template, ActionTypeDefinition actionType) {
		RuleDefinition ruleDef = new RuleDefinition(-1, template.getName(), template.getName() + " rule: auto generated");

		FunctionParameterDefinition[] paramDefs = actionType.getParameterDefinitions();

		// add condition for each non-action param columns
		CompoundLHSElement rootConditions = RuleElementFactory.getInstance().createAndCompoundCondition();

		int columnNo = 1;
		for (; columnNo <= template.getNumColumns() - paramDefs.length; columnNo++) {
			AbstractTemplateColumn column = template.getColumn(columnNo);
			if (column != null) {
				Condition condition = RuleElementFactory.getInstance().createCondition();
				condition.setReference(RuleElementFactory.getInstance().createReference(column.getMappedAttribute()));
				if (column.getColumnDataSpecDigest().getType().endsWith("Range")) {
					condition.setOp(Condition.OP_BETWEEN);
				}
				else if (column.getColumnDataSpecDigest().getType().equals("EnumList")) {
					condition.setOp(Condition.OP_IN);
				}
				else {
					condition.setOp(Condition.OP_EQUAL);
				}
				condition.setValue(RuleElementFactory.getInstance().createValue(
						RuleElementFactory.getInstance().createColumnReference(column.getColumnNumber())));

				rootConditions.add(condition);
			}
		}
		ruleDef.updateRootConditions(rootConditions);

		generateRuleAction(template, ruleDef, actionType);

		template.setRuleDefinition(ruleDef);
	}
	

	/**
	 * generates and sets LHS side of a rule if in template creation wizard user did not select action for a template. 
	 * @author vineet khosla
	 * @since PowerEditor 5.0
	 * @param template
	 */
	public static void generateAndSetLHSRuleDefinition(GridTemplate template) {
		RuleDefinition ruleDef = new RuleDefinition(-1, template.getName(), template.getName() + " rule: auto generated");

		// add condition for each non-action param columns
		CompoundLHSElement rootConditions = RuleElementFactory.getInstance().createAndCompoundCondition();

		int columnNo = 1;
		for (; columnNo <= template.getNumColumns() ; columnNo++) {
			AbstractTemplateColumn column = template.getColumn(columnNo);
			if (column != null) {
				Condition condition = RuleElementFactory.getInstance().createCondition();
				condition.setReference(RuleElementFactory.getInstance().createReference(column.getMappedAttribute()));
				if (column.getColumnDataSpecDigest().getType().endsWith("Range")) {
					condition.setOp(Condition.OP_BETWEEN);
				}
				else if (column.getColumnDataSpecDigest().getType().equals("EnumList")) {
					condition.setOp(Condition.OP_IN);
				}
				else {
					condition.setOp(Condition.OP_EQUAL);
				}
				condition.setValue(RuleElementFactory.getInstance().createValue(
						RuleElementFactory.getInstance().createColumnReference(column.getColumnNumber())));

				rootConditions.add(condition);
			}
		}
		ruleDef.updateRootConditions(rootConditions);

		template.setRuleDefinition(ruleDef);
	}

	private static void generateRuleAction(GridTemplate template, RuleDefinition ruleDef, ActionTypeDefinition actionType) {
		FunctionParameterDefinition[] paramDefs = actionType.getParameterDefinitions();
		int columnNo = template.getNumColumns() - paramDefs.length + 1;
		RuleAction ruleAction = RuleElementFactory.getInstance().createRuleAction();
		ruleAction.setActionType(actionType);

		for (int i = 0; i < paramDefs.length; i++) {
			AbstractTemplateColumn column = template.getColumn(columnNo + i);
			FunctionParameterDefinition paramDefinition = paramDefs[i];
			ruleAction.add(RuleElementFactory.getInstance().createFunctionParameter(
					i + 1,
					paramDefinition.getName(),
					RuleElementFactory.getInstance().createColumnReference(column.getColumnNumber())));
		}
		ruleDef.updateAction(ruleAction);
	}
	
	private static void regenerateRuleAction(RuleDefinition rd, ActionTypeDefinition newActionType, 
			ActionTypeDefinition oldActionType) {
		FunctionCall function = (FunctionCall)rd.getRuleAction();
		RuleElementFactory.getInstance().createParametersForFunctionCall(function, newActionType, function.getElements(), oldActionType);
		rd.updateAction((RuleAction)function);
	}

	public static void updateForModifiedAction(GridTemplate template, ActionTypeDefinition oldActionType, ActionTypeDefinition newActionType) {
		RuleDefinition rd = template.getRuleDefinition();
		if (rd != null && rd.getActionTypeID() == oldActionType.getID()) regenerateRuleAction(rd, newActionType, oldActionType);
		for (Iterator<GridTemplateColumn> colIt = template.getColumns().iterator(); colIt.hasNext();) {
			rd = colIt.next().getRuleDefinition();
			if (rd != null && rd.getActionTypeID() == oldActionType.getID()) regenerateRuleAction(rd, newActionType, oldActionType);
		}
	}

	private static void regenerateTestCondition(TestCondition tc, TestTypeDefinition testType) {
		FunctionParameterDefinition[] paramDefs = testType.getParameterDefinitions();

		tc.setTestType(testType);
		int oldCount = tc.size();
		if (paramDefs.length > oldCount) {
			for (int i = oldCount; i < paramDefs.length; i++) {
				tc.add(RuleElementFactory.getInstance().createFunctionParameter(i + 1, paramDefs[i].getName(), ""));
			}
		}
		else if (paramDefs.length < oldCount) {
			for (int i = oldCount - 1; i >= paramDefs.length; i--) {
				tc.remove(i);
			}
		}
		for (int i = 0; i < tc.size(); i++) {
			RuleElement fp = (RuleElement) tc.get(i);
			fp.setName(paramDefs[i].getName());
		}
	}

	public static void updateForModifiedTest(GridTemplate template, TestTypeDefinition oldTestType, TestTypeDefinition newTestType) {
		List<RuleDefinition> ruleDefs = new ArrayList<RuleDefinition>();
		RuleDefinition rd = template.getRuleDefinition();
		if (rd != null) ruleDefs.add(rd);
		for (Iterator<GridTemplateColumn> colIt = template.getColumns().iterator(); colIt.hasNext();) {
			rd = colIt.next().getRuleDefinition();
			if (rd != null) ruleDefs.add(rd);
		}
		for (Iterator<RuleDefinition> ruleIt = ruleDefs.iterator(); ruleIt.hasNext();) {
			rd = ruleIt.next();
			for (TestCondition tc : rd.getTestConditions()) {
				if (tc.getTestType().getID() == oldTestType.getID()) regenerateTestCondition(tc, newTestType);
			}
		}
	}

	private TemplateUtil() {
	}
}