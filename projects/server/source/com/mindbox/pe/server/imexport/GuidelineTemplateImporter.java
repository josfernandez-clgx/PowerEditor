package com.mindbox.pe.server.imexport;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.common.validate.DataTypeCompatibilityValidator;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.RuleMessageContainer;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.comparator.IDObjectComparator;
import com.mindbox.pe.model.rule.AbstractCondition;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.LHSElement;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.server.RuleDefinitionUtil;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.imexport.digest.RulePrecondition;
import com.mindbox.pe.server.imexport.digest.TemplateRule;
import com.mindbox.pe.server.imexport.digest.TemplateRuleContainer;

final class GuidelineTemplateImporter extends AbstractImporter<TemplateImportOptionalData> {

	private static GridTemplate findGuidelineTemplate(List<GridTemplate> templateList, int id) {
		for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate element = iter.next();
			if (element.getID() == id) return element;
		}
		return null;
	}

	protected GuidelineTemplateImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, TemplateImportOptionalData optionalData)
			throws ImportException {
		int count = processTemplates(objectHolder, optionalData);
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " guideline templates", "File: " + filename);
			importResult.setTemplateImported(true);
		}
	}

	private int processTemplates(DigestedObjectHolder objectHolder, TemplateImportOptionalData optionalData) throws ImportException {
		logger.debug(">>> processTemplates: merge=" + merge);
		int templateCount = 0;
		try {
			// import guideline-templates, if any
			List<GridTemplate> templateList = objectHolder.getObjects(GridTemplate.class, new IDObjectComparator<GridTemplate>());
			if (!templateList.isEmpty()) {
				List<TemplateRuleContainer> ruleContainerList = objectHolder.getObjects(TemplateRuleContainer.class);
				for (TemplateRuleContainer element : ruleContainerList) {
					GridTemplate template = findGuidelineTemplate(templateList, element.getId());
					if (template == null) {
						throw new ImportException("No template of " + element.getId() + " found; element = " + element);
					}
					List<TemplateRule> ruleList = element.getObjects(TemplateRule.class);
					if (ruleList != null) {
						for (TemplateRule rule : ruleList) {
							if (rule.hasPrecondition()) {
								// Just check for the first one for now
								RulePrecondition preCond = rule.getPreconditions()[0];
								if (preCond.getColumnID() > 0) {
									setRulesMessages(
											(RuleMessageContainer) template.getColumn(preCond.getColumnID()),
											rule,
											optionalData.getActionIDMap());
								}
								else {
									setRulesMessages(template, rule, optionalData.getActionIDMap());
								}
							}
							else {
								setRulesMessages(template, rule, optionalData.getActionIDMap());
							}
						}
					}
				}
				for (GridTemplate template : templateList) {
					// set enum source type if not set
					for (int col = 1; col <= template.getColumnCount(); col++) {
						ColumnDataSpecDigest digest = template.getColumn(col).getColumnDataSpecDigest();
						digest.resetColumnEnumSourceTypeIfNecessary();
					}

					try {
						// Iterate over the LHS conditions checking for conditions that use an invalid operator on 
						// entityList columns. If so, change the operator to the correct one.
						processTemplateForInvalidConditionOperators(template);
						// validate template for TT 729                        
						importBusinessLogic.validateTemplateForImport(template, true, merge);
						importBusinessLogic.importTemplate(template, merge, optionalData.getTemplateIDMap(), user);
						++templateCount;
					}
					catch (ImportException ex) {
						logger.error("Failed to import guideline template: " + template, ex);
						addError(template, ex);
						optionalData.getUnimportedTemplateIDs().add(template.getID());
					}
				}
			}
			return templateCount;
		}
		catch (Exception ex) {
			logger.error("Failed to import templates", ex);
			throw new ImportException(ex.getMessage());
		}
	}

	private void processTemplateForInvalidConditionOperators(GridTemplate template) {
		if (template != null) {
			RuleDefinition ruleDef = template.getRuleDefinition();
			if (ruleDef != null) {
				processTemplateForInvalidConditionOperators(template, ruleDef);
			}
			for (int c = 1; c <= template.getColumnCount(); c++) {
				ruleDef = ((GridTemplateColumn) template.getColumn(c)).getRuleDefinition();
				if (ruleDef != null) {
					processTemplateForInvalidConditionOperators(template, ruleDef);
				}
			}
		}
	}

	/**
	 * 
	 * @param template
	 * @param ruleDef
	 * @throws NullPointerException if <code>ruleDef</code> is <code>null</code>
	 * 
	 */
	private void processTemplateForInvalidConditionOperators(GridTemplate template, RuleDefinition ruleDef) {
		for (int i = 0; i < ruleDef.getRootElement().size(); i++) {
			LHSElement element = ruleDef.getRootElementAt(i);
			if (element instanceof Condition || element instanceof AbstractCondition) {
				processConditionForInvalidConditionOperators(template, (Condition) element);
			}
			else if (element instanceof ExistExpression) {
				processLHSElementForInvalidConditionOperators(template, ((ExistExpression) element).getCompoundLHSElement());
			}
			else if (element instanceof CompoundLHSElement) {
				processLHSElementForInvalidConditionOperators(template, (CompoundLHSElement) element);
			}
		}
	}

	private void processLHSElementForInvalidConditionOperators(GridTemplate template, CompoundLHSElement element) {
		if (element != null && !element.isEmpty()) {
			for (int i = 0; i < element.size(); i++) {
				RuleElement child = (RuleElement) element.get(i);
				if (child instanceof CompoundLHSElement) {
					processLHSElementForInvalidConditionOperators(template, (CompoundLHSElement) child);
				}
				else if (child instanceof ExistExpression) {
					ExistExpression expr = (ExistExpression) child;
					processLHSElementForInvalidConditionOperators(template, expr.getCompoundLHSElement());
				}
				else if (child instanceof Condition || child instanceof AbstractCondition) {
					processConditionForInvalidConditionOperators(template, (Condition) child);
				}
			}
		}
	}

	private void processConditionForInvalidConditionOperators(GridTemplate template, Condition condition) {
		if (condition.getValue() instanceof ColumnReference) {
			ColumnReference colRef = (ColumnReference) condition.getValue();
			AbstractTemplateColumn c = template.getColumn(colRef.getColumnNo());
			if (c != null && c.getColumnDataSpecDigest() != null && c.getColumnDataSpecDigest().getType() != null
					&& c.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)
					&& (condition.getOp() != Condition.OP_ENTITY_MATCH_FUNC && condition.getOp() != Condition.OP_NOT_ENTITY_MATCH_FUNC)
					&& condition.getReference() != null) {
				int newConditionOp = -1;
				if (condition.getOp() == Condition.OP_NOT_EQUAL || condition.getOp() == Condition.OP_NOT_IN) {
					newConditionOp = Condition.OP_NOT_ENTITY_MATCH_FUNC;
				}
				else {
					newConditionOp = Condition.OP_ENTITY_MATCH_FUNC;
				}
				String message = DataTypeCompatibilityValidator.isValid(
						condition.getReference(),
						newConditionOp,
						condition.getValue(),
						template,
						DomainManager.getInstance(),
						true);
				if (message != null) {
					addError(template, new ImportException("Invalid condition on an entityList column " + message));
				}
				else {
					condition.setOp(newConditionOp);
				}
			}
		}
	}

	private void setRulesMessages(RuleMessageContainer rmContainer, TemplateRule rule, Map<String, Integer> actionIDMap)
			throws ImportException {
		try {
			RuleDefinition ruleDef = RuleDefinitionUtil.parseToRuleDefinition(rule.getDefinition(), (merge ? actionIDMap : null));
			rmContainer.setRuleDefinition(ruleDef);

			TemplateMessageDigest[] messages = rule.getMessages();
			for (int i = 0; i < messages.length; i++) {
				if (messages[i].getChannel() != null) {
					messages[i].setEntityIDStr(messages[i].getChannel());
				}
				rmContainer.addMessageDigest(messages[i]);
			}
		}
		catch (Exception ex) {
			logger.error("Failed to import " + rmContainer, ex);
			throw new ImportException("Failed to import " + rmContainer + ": " + ex.getMessage());
		}
	}

}
