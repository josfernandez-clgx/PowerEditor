package com.mindbox.pe.server.imexport;

import static com.mindbox.pe.common.LogUtil.logDebug;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mindbox.pe.common.validate.DataTypeCompatibilityValidator;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.AbstractCondition;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.LHSElement;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnAttributeItemDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.RuleMessageContainer;
import com.mindbox.pe.model.template.TemplateMessageDigest;
import com.mindbox.pe.server.RuleDefinitionUtil;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.parser.jtb.message.ParseException;
import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfigType;
import com.mindbox.pe.xsd.config.RangeStyleType;
import com.mindbox.pe.xsd.data.ColumnMessages.ColumnMessage;
import com.mindbox.pe.xsd.data.DataSpecElement;
import com.mindbox.pe.xsd.data.DataSpecElement.AttributeItem;
import com.mindbox.pe.xsd.data.GuidelineMessageElement;
import com.mindbox.pe.xsd.data.TemplateColumnElement;
import com.mindbox.pe.xsd.data.TemplateDataElement;
import com.mindbox.pe.xsd.data.TemplateDataElement.GuidelineTemplate;
import com.mindbox.pe.xsd.data.TemplateRuleElement;

final class GuidelineTemplateImporter extends AbstractImporter<TemplateDataElement, TemplateImportOptionalData> {

	private static final Logger LOG = Logger.getLogger(GuidelineTemplateImporter.class);

	private static String asErrorContext(final GuidelineTemplate guidelineTemplate) {
		return String.format("GuidelineTemplate[id=%s,name=%s]", guidelineTemplate.getId(), guidelineTemplate.getName());
	}

	private static GridTemplate asGridTemplate(final GuidelineTemplate guidelineTemplate, final boolean merge, final Map<String, Integer> actionIDMap) throws ParseException, SAXException,
			IOException, ParserConfigurationException {
		logDebug(LOG, "asGridTemplate: %s,merge=%b", guidelineTemplate, merge);
		final GridTemplate gridTemplate = new GridTemplate();
		gridTemplate.setComment(guidelineTemplate.getComment());
		gridTemplate.setCompleteColumnsString(guidelineTemplate.getCompleteCols());
		gridTemplate.setConsistentColumnsString(guidelineTemplate.getConsistentCols());
		gridTemplate.setDescription(guidelineTemplate.getDescription());
		gridTemplate.setFitToScreen(guidelineTemplate.isFitToScreen());
		gridTemplate.setID(guidelineTemplate.getId());
		gridTemplate.setMaxNumOfRows(guidelineTemplate.getMaxRows());
		gridTemplate.setName(guidelineTemplate.getName());
		gridTemplate.setParentTemplateID(guidelineTemplate.getParentID());
		gridTemplate.setStatus(guidelineTemplate.getStatus());
		gridTemplate.setUsageTypeString(guidelineTemplate.getUsage());
		gridTemplate.setVersion(guidelineTemplate.getVersion());

		for (final TemplateColumnElement columnElement : guidelineTemplate.getColumns().getColumn()) {
			final GridTemplateColumn gridTemplateColumn = new GridTemplateColumn(
					columnElement.getId(),
					columnElement.getName(),
					columnElement.getDescription(),
					columnElement.getWidth(),
					columnElement.getUsage() == null ? null : TemplateUsageType.valueOf(columnElement.getUsage()));

			logDebug(LOG, "asGridTemplate: columnElement=%s(%s); templateColumn(b4proc)=%s", columnElement.getTitle(), columnElement, gridTemplateColumn);

			gridTemplateColumn.setColor(columnElement.getColor());
			gridTemplateColumn.setFont(columnElement.getFont());
			gridTemplateColumn.setTitle(columnElement.getTitle());

			final DataSpecElement dataSpecElement = columnElement.getDataspec();
			logDebug(
					LOG,
					"asGridTemplate: dataSpecElement[type=%s,multi=%s,sortEnum=%s,allowBlank=%s,allowCat=%s,allowEntity=%s,showLHS=%s,min=%s,max=%s,prec=%s](%s)",
					dataSpecElement.getType(),
					dataSpecElement.isMultipleSelect(),
					dataSpecElement.isSortEnumValue(),
					dataSpecElement.isAllowBlank(),
					dataSpecElement.isAllowCategory(),
					dataSpecElement.isAllowEntity(),
					dataSpecElement.isShowLhsAttribute(),
					dataSpecElement.getMinValue(),
					dataSpecElement.getMaxValue(),
					dataSpecElement.getPrecision(),
					dataSpecElement);

			gridTemplateColumn.setDataSpecDigest(new ColumnDataSpecDigest());
			gridTemplateColumn.getColumnDataSpecDigest().setIsBlankAllowed(dataSpecElement.isAllowBlank());
			gridTemplateColumn.getColumnDataSpecDigest().setIsCategoryAllowed(dataSpecElement.isAllowCategory() == null ? false : dataSpecElement.isAllowCategory());
			gridTemplateColumn.getColumnDataSpecDigest().setIsEntityAllowed(dataSpecElement.isAllowEntity() == null ? false : dataSpecElement.isAllowEntity());
			gridTemplateColumn.getColumnDataSpecDigest().setIsEnumValueNeedSorted(dataSpecElement.isSortEnumValue());
			gridTemplateColumn.getColumnDataSpecDigest().setIsLHSAttributeVisible(dataSpecElement.isShowLhsAttribute());
			gridTemplateColumn.getColumnDataSpecDigest().setIsMultiSelectAllowed(dataSpecElement.isMultipleSelect());
			gridTemplateColumn.getColumnDataSpecDigest().setAttributeMap(dataSpecElement.getEnumAttribute());
			gridTemplateColumn.getColumnDataSpecDigest().setEntityType(dataSpecElement.getEntityType());
			gridTemplateColumn.getColumnDataSpecDigest().setEnumSelectorColumnName(dataSpecElement.getEnumSelectorColumn());
			gridTemplateColumn.getColumnDataSpecDigest().setEnumSourceName(dataSpecElement.getEnumSourceName());
			if (dataSpecElement.getEnumType() != null) {
				gridTemplateColumn.getColumnDataSpecDigest().setEnumSourceTypeStr(dataSpecElement.getEnumType());
			}
			// TT-80 add enum values
			if (dataSpecElement.getEnumValue() != null && !dataSpecElement.getEnumValue().isEmpty()) {
				for (final String enumValue : dataSpecElement.getEnumValue()) {
					gridTemplateColumn.getColumnDataSpecDigest().addColumnEnumValue(enumValue);
				}
			}
			gridTemplateColumn.getColumnDataSpecDigest().setMaxValue(dataSpecElement.getMaxValue());
			gridTemplateColumn.getColumnDataSpecDigest().setMinValue(dataSpecElement.getMinValue());
			if (dataSpecElement.getPrecision() != null) {
				gridTemplateColumn.getColumnDataSpecDigest().setPrecision(dataSpecElement.getPrecision());
			}
			gridTemplateColumn.getColumnDataSpecDigest().setType(dataSpecElement.getType());
			if (dataSpecElement.getAttributeItem() != null) {
				for (final AttributeItem attributeItem : dataSpecElement.getAttributeItem()) {
					final ColumnAttributeItemDigest columnAttributeItemDigest = new ColumnAttributeItemDigest();
					columnAttributeItemDigest.setDisplayValue(attributeItem.getDisplayValue());
					columnAttributeItemDigest.setName(attributeItem.getName());
					gridTemplateColumn.getColumnDataSpecDigest().addAttributeItem(columnAttributeItemDigest);
				}
			}
			if (columnElement.getColumnMessages() != null) {
				for (final ColumnMessage columnMessage : columnElement.getColumnMessages().getColumnMessage()) {
					final ColumnMessageFragmentDigest columnMessageFragmentDigest = new ColumnMessageFragmentDigest();
					if (columnMessage.getCellSelection() != null) {
						columnMessageFragmentDigest.setCellSelection(CellSelectionType.fromValue(columnMessage.getCellSelection()));
					}
					columnMessageFragmentDigest.setEnumDelimiter(columnMessage.getEnumDelimiter());
					columnMessageFragmentDigest.setEnumFinalDelimiter(columnMessage.getEnumFinalDelimiter());
					columnMessageFragmentDigest.setEnumPrefix(columnMessage.getEnumPrefix());
					if (columnMessage.getRangeStyle() != null) {
						columnMessageFragmentDigest.setRangeStyle(RangeStyleType.fromValue(columnMessage.getRangeStyle()));
					}
					columnMessageFragmentDigest.setText(columnMessage.getMessageText());
					if (columnMessage.getType() != null) {
						columnMessageFragmentDigest.setType(MessageConfigType.fromValue(columnMessage.getType().toLowerCase()));
					}
					gridTemplateColumn.addColumnMessageFragment(columnMessageFragmentDigest);
				}
			}

			gridTemplate.addColumn(gridTemplateColumn);
		}

		for (final TemplateRuleElement ruleElement : guidelineTemplate.getRules().getRule()) {
			if (ruleElement.getPrecondition() == null || ruleElement.getPrecondition().isEmpty()) {
				logDebug(LOG, "setting template rule,messages: %s,merge=%s,rule=%s", gridTemplate, merge, ruleElement);
				setRulesMessages(gridTemplate, merge, ruleElement, actionIDMap);
			}
			else {
				int columnNo = ruleElement.getPrecondition().get(0).getColumnID();
				logDebug(LOG, "setting column rule,messages: %s,merge=%s,rule=%s", gridTemplate.getColumn(columnNo), merge, ruleElement);
				setRulesMessages(gridTemplate.getColumn(columnNo), merge, ruleElement, actionIDMap);
			}
		}
		return gridTemplate;
	}

	private static void setRulesMessages(final RuleMessageContainer rmContainer, final boolean merge, final TemplateRuleElement ruleElement, Map<String, Integer> actionIDMap) throws SAXException,
			IOException, ParserConfigurationException {
		final RuleDefinition ruleDefinition = RuleDefinitionUtil.parseToRuleDefinition(ruleElement.getDefinition(), (merge ? actionIDMap : null));
		rmContainer.setRuleDefinition(ruleDefinition);
		if (ruleElement.getMessages() != null) {
			for (final GuidelineMessageElement guidelineMessageElement : ruleElement.getMessages().getMessage()) {
				final TemplateMessageDigest templateMessageDigest = new TemplateMessageDigest();
				templateMessageDigest.setConditionalDelimiter(guidelineMessageElement.getConditionalDelimiter());
				templateMessageDigest.setConditionalFinalDelimiter(guidelineMessageElement.getConditionalFinalDelimiter());
				templateMessageDigest.setEntityID(guidelineMessageElement.getEntityID() == null ? -1 : guidelineMessageElement.getEntityID().intValue());
				templateMessageDigest.setText(guidelineMessageElement.getMessageText());
				rmContainer.addMessageDigest(templateMessageDigest);
			}
		}
	}

	protected GuidelineTemplateImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	private void processConditionForInvalidConditionOperators(GridTemplate template, Condition condition) {
		if (condition.getValue() instanceof ColumnReference) {
			ColumnReference colRef = (ColumnReference) condition.getValue();
			AbstractTemplateColumn c = template.getColumn(colRef.getColumnNo());
			if (c != null && c.getColumnDataSpecDigest() != null && c.getColumnDataSpecDigest().getType() != null && c.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)
					&& (condition.getOp() != Condition.OP_ENTITY_MATCH_FUNC && condition.getOp() != Condition.OP_NOT_ENTITY_MATCH_FUNC) && condition.getReference() != null) {
				int newConditionOp = -1;
				if (condition.getOp() == Condition.OP_NOT_EQUAL || condition.getOp() == Condition.OP_NOT_IN) {
					newConditionOp = Condition.OP_NOT_ENTITY_MATCH_FUNC;
				}
				else {
					newConditionOp = Condition.OP_ENTITY_MATCH_FUNC;
				}
				String message = DataTypeCompatibilityValidator.isValid(condition.getReference(), newConditionOp, condition.getValue(), template, DomainManager.getInstance(), true);
				if (message != null) {
					addError(template, new ImportException("Invalid condition on an entityList column " + message));
				}
				else {
					condition.setOp(newConditionOp);
				}
			}
		}
	}

	@Override
	protected void processData(TemplateDataElement dataToImport, TemplateImportOptionalData optionalData) throws ImportException {
		if (dataToImport != null && dataToImport.getGuidelineTemplate() != null) {
			int count = processTemplates(dataToImport.getGuidelineTemplate(), optionalData);
			if (count > 0) {
				importResult.addMessage("  Imported " + count + " guideline templates", "");
				importResult.setTemplateImported(true);
			}
		}
		else {
			logger.info("No guideline template to import.");
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

	private int processTemplates(List<TemplateDataElement.GuidelineTemplate> templateList, TemplateImportOptionalData optionalData) throws ImportException {
		logDebug(logger, ">>> processTemplates: merge=%b", merge);
		int templateCount = 0;
		try {
			// import guideline-templates, if any
			if (!templateList.isEmpty()) {
				for (final GuidelineTemplate template : templateList) {
					logDebug(logger, "processing %s", asErrorContext(template));
					final GridTemplate gridTemplate = asGridTemplate(template, merge, optionalData.getActionIDMap());

					// set enum source type if not set
					for (int col = 1; col <= gridTemplate.getColumnCount(); col++) {
						ColumnDataSpecDigest digest = gridTemplate.getColumn(col).getColumnDataSpecDigest();
						digest.resetColumnEnumSourceTypeIfNecessary();
					}

					try {
						// Iterate over the LHS conditions checking for conditions that use an invalid operator on 
						// entityList columns. If so, change the operator to the correct one.
						processTemplateForInvalidConditionOperators(gridTemplate);
						// validate template for TT 729                        
						importBusinessLogic.validateTemplateForImport(gridTemplate, true, merge);
						importBusinessLogic.importTemplate(gridTemplate, merge, optionalData.getTemplateIDMap(), user);
						++templateCount;
					}
					catch (ImportException ex) {
						logger.error("Failed to import guideline template: " + template, ex);
						addError(asErrorContext(template), ex);
						optionalData.getUnimportedTemplateIDs().add(gridTemplate.getID());
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

}
