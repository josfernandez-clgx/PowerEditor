package com.mindbox.pe.server.imexport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.model.assckey.GenericEntityAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.TimedAssociationKey;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeValue;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseAction;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.cbr.CBREnumeratedValue;
import com.mindbox.pe.model.comparator.IDObjectComparator;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnAttributeItemDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.model.template.RuleMessageContainer;
import com.mindbox.pe.model.template.TemplateMessageDigest;
import com.mindbox.pe.server.RuleDefinitionUtil;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.db.LDAPUserManagementProvider;
import com.mindbox.pe.server.imexport.provider.ExportDataProvider;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.data.ActionParameters;
import com.mindbox.pe.xsd.data.ActionParameters.Parameter;
import com.mindbox.pe.xsd.data.ActivationDates;
import com.mindbox.pe.xsd.data.CBRAttributeElement;
import com.mindbox.pe.xsd.data.CBRAttributeElement.AttributeType;
import com.mindbox.pe.xsd.data.CBRAttributeElement.CaseBase;
import com.mindbox.pe.xsd.data.CBRAttributeElement.EnumValues.EnumValue;
import com.mindbox.pe.xsd.data.CBRAttributeElement.ValueRange;
import com.mindbox.pe.xsd.data.CBRAttributeValueElement;
import com.mindbox.pe.xsd.data.CBRAttributeValueElement.Attribute;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement.CaseClass;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement.ScoringFunction;
import com.mindbox.pe.xsd.data.CBRCaseElement;
import com.mindbox.pe.xsd.data.CBRCaseElement.CaseActions.CaseAction;
import com.mindbox.pe.xsd.data.CBRDataElement;
import com.mindbox.pe.xsd.data.ColumnMessages;
import com.mindbox.pe.xsd.data.ColumnMessages.ColumnMessage;
import com.mindbox.pe.xsd.data.ContextElement;
import com.mindbox.pe.xsd.data.DataSpecElement;
import com.mindbox.pe.xsd.data.DataSpecElement.AttributeItem;
import com.mindbox.pe.xsd.data.DateDataElement;
import com.mindbox.pe.xsd.data.DateDataElement.DateElement;
import com.mindbox.pe.xsd.data.EntityDataElement;
import com.mindbox.pe.xsd.data.EntityDataElement.Category;
import com.mindbox.pe.xsd.data.EntityDataElement.Category.Parent;
import com.mindbox.pe.xsd.data.EntityDataElement.Entity;
import com.mindbox.pe.xsd.data.EntityDataElement.Entity.Association;
import com.mindbox.pe.xsd.data.EntityLink;
import com.mindbox.pe.xsd.data.GridActivationElement;
import com.mindbox.pe.xsd.data.GridActivationElement.GridValues.Row;
import com.mindbox.pe.xsd.data.GridDataElement;
import com.mindbox.pe.xsd.data.GridDataElement.Grid.ColumnNames;
import com.mindbox.pe.xsd.data.GridTypeAttribute;
import com.mindbox.pe.xsd.data.GuidelineActionDataElement;
import com.mindbox.pe.xsd.data.GuidelineActionDataElement.GuidelineAction;
import com.mindbox.pe.xsd.data.GuidelineMessageElement;
import com.mindbox.pe.xsd.data.MetaDataElement;
import com.mindbox.pe.xsd.data.MetaDataElement.PeData;
import com.mindbox.pe.xsd.data.MetaDataElement.SystemData;
import com.mindbox.pe.xsd.data.NextIDDataElement;
import com.mindbox.pe.xsd.data.NextIDDataElement.NextId;
import com.mindbox.pe.xsd.data.PowereditorData;
import com.mindbox.pe.xsd.data.PrivilegesElement;
import com.mindbox.pe.xsd.data.PropertyElement;
import com.mindbox.pe.xsd.data.RolesElement;
import com.mindbox.pe.xsd.data.SecurityDataElement;
import com.mindbox.pe.xsd.data.TemplateColumnElement;
import com.mindbox.pe.xsd.data.TemplateDataElement;
import com.mindbox.pe.xsd.data.TemplateDataElement.GuidelineTemplate;
import com.mindbox.pe.xsd.data.TemplateRuleElement;
import com.mindbox.pe.xsd.data.TemplateRuleElement.Precondition;
import com.mindbox.pe.xsd.data.TemplateRuleSet;
import com.mindbox.pe.xsd.data.TestConditionDataElement;
import com.mindbox.pe.xsd.data.TestConditionDataElement.TestCondition;
import com.mindbox.pe.xsd.data.TypeEnumDataElement;
import com.mindbox.pe.xsd.data.TypeEnumDataElement.TypeEnum;
import com.mindbox.pe.xsd.data.UserStatusAttribute;
import com.mindbox.pe.xsd.data.UsersElement;

/**
 * Generates an instance of {@link PowereditorData}.
 * NOTE: This is <b>not</b> thread-safe.
 *
 */
class ExportDataGenerator {

	private static final Logger LOG = Logger.getLogger(ExportDataGenerator.class);

	private static ActionParameters asActionParameters(final FunctionTypeDefinition typeDef) {
		final ActionParameters actionParameters = new ActionParameters();
		for (FunctionParameterDefinition paramDefinition : typeDef.getParameterDefinitions()) {
			final Parameter parameter = new Parameter();
			parameter.setId(paramDefinition.getId());
			parameter.setDataString(paramDefinition.getParamDataString());
			parameter.setDeployType(paramDefinition.getDeployType().toString());
			parameter.setName(paramDefinition.getName());
			actionParameters.getParameter().add(parameter);
		}
		return actionParameters;
	}

	private static ActivationDates asActivationDates(DateSynonym effDate, DateSynonym expDate) {
		if (effDate == null && expDate == null) {
			return null;
		}

		final ActivationDates activationDates = new ActivationDates();
		if (effDate != null) {
			activationDates.setEffectiveDateID(effDate.getId());
		}
		if (expDate != null) {
			activationDates.setExpirationDateID(expDate.getId());
		}
		return activationDates;
	}

	private static Association asAssociation(final GenericEntityAssociationKey asscKey) {
		return asAssociation(asscKey.getGenericEntityType().toString(), asscKey);
	}

	private static Association asAssociation(String type, TimedAssociationKey asscKey) {
		final Association association = new Association();
		association.setActivationDates(asActivationDates(asscKey.getEffectiveDate(), asscKey.getExpirationDate()));
		association.setEntityLink(asEntityLink(type, asscKey.getAssociableID()));
		return association;
	}

	private static Category asCategoryElement(GenericCategory category, String entityType) {
		final Category categoryElement = new Category();
		categoryElement.setId(category.getID());
		categoryElement.setType(entityType);
		categoryElement.getProperty().add(asPropertyElement("name", category.getName()));
		if (category.isRoot()) {
			final Parent parent = new Parent();
			parent.setParentID(-1);
			parent.setActivationDates(asActivationDates(DateSynonymManager.getInstance().getEarliestDateSynonym(), null));
			categoryElement.getParent().add(parent);
		}
		else {
			for (Iterator<MutableTimedAssociationKey> parentIter = category.getParentKeyIterator(); parentIter.hasNext();) {
				categoryElement.getParent().add(asParentKeyElement(parentIter.next()));
			}
		}

		return categoryElement;
	}

	private static CBRAttributeElement asCBRAttributeElement(final CBRAttribute attribute) throws ExportException {
		final CBRAttributeElement cbrAttributeElement = new CBRAttributeElement();
		cbrAttributeElement.setId(attribute.getId());
		cbrAttributeElement.setName(attribute.getName());
		cbrAttributeElement.setAbsencePenalty(attribute.getAbsencePenalty());
		final AttributeType attributeType = new AttributeType();
		attributeType.setId(attribute.getAttributeType().getId());
		cbrAttributeElement.setAttributeType(attributeType);
		final CaseBase caseBase = new CBRAttributeElement.CaseBase();
		caseBase.setId(attribute.getCaseBase().getId());
		cbrAttributeElement.setCaseBase(caseBase);
		cbrAttributeElement.setDescription(attribute.getDescription());
		cbrAttributeElement.setHighestValue(attribute.getHighestValue());
		cbrAttributeElement.setLowestValue(attribute.getLowestValue());
		cbrAttributeElement.setMatchContribution(attribute.getMatchContribution());
		cbrAttributeElement.setMatchInterval(attribute.getMatchInterval());
		cbrAttributeElement.setMismatchPenalty(attribute.getMismatchPenalty());
		final ValueRange valueRange = new CBRAttributeElement.ValueRange();
		valueRange.setId(attribute.getValueRange().getId());
		cbrAttributeElement.setValueRange(valueRange);

		cbrAttributeElement.setEnumValues(new CBRAttributeElement.EnumValues());
		for (CBREnumeratedValue enumeratedValue : attribute.getEnumeratedValues()) {
			final EnumValue enumValue = new CBRAttributeElement.EnumValues.EnumValue();
			enumValue.setId(enumeratedValue.getId());
			enumValue.setName(enumeratedValue.getName());
			cbrAttributeElement.getEnumValues().getEnumValue().add(enumValue);
		}

		return cbrAttributeElement;
	}

	private static CBRCaseBaseElement asCBRCaseBaseElement(final CBRCaseBase caseBase) throws ExportException {
		final CBRCaseBaseElement cbrCaseBaseElement = new CBRCaseBaseElement();
		cbrCaseBaseElement.setId(caseBase.getId());
		cbrCaseBaseElement.setName(caseBase.getName());
		cbrCaseBaseElement.setActivationDates(asActivationDates(caseBase.getEffectiveDate(), caseBase.getExpirationDate()));
		final CaseClass caseClass = new CaseClass();
		caseClass.setId(caseBase.getCaseClass().getId());
		cbrCaseBaseElement.setCaseClass(caseClass);
		cbrCaseBaseElement.setDescription(caseBase.getDescription());
		cbrCaseBaseElement.setIndexFile(caseBase.getIndexFile());
		cbrCaseBaseElement.setMatchThreshold(caseBase.getMatchThreshold());
		cbrCaseBaseElement.setMaximumMatches(caseBase.getMaximumMatches());
		cbrCaseBaseElement.setNamingAttribute(caseBase.getNamingAttribute());
		final ScoringFunction scoringFunction = new ScoringFunction();
		scoringFunction.setId(caseBase.getScoringFunction().getId());
		cbrCaseBaseElement.setScoringFunction(scoringFunction);
		return cbrCaseBaseElement;
	}

	private static CBRCaseElement asCBRCaseElement(final CBRCase cbrCase) throws ExportException {
		final CBRCaseElement cbrCaseElement = new CBRCaseElement();
		cbrCaseElement.setId(cbrCase.getId());
		cbrCaseElement.setName(cbrCase.getName());
		cbrCaseElement.setActivationDates(asActivationDates(cbrCase.getEffectiveDate(), cbrCase.getExpirationDate()));
		cbrCaseElement.setAttributeValues(new CBRCaseElement.AttributeValues());
		// CBR attribute values
		for (final CBRAttributeValue attributeValue : cbrCase.getAttributeValues()) {
			final CBRAttributeValueElement valueElement = new CBRAttributeValueElement();
			valueElement.setId(attributeValue.getId());
			valueElement.setValue(attributeValue.getName());
			final Attribute attribute = new CBRAttributeValueElement.Attribute();
			attribute.setId(attributeValue.getAttribute().getId());
			valueElement.setAttribute(attribute);
			valueElement.setDescription(attributeValue.getDescription());
			valueElement.setMatchContribution(attributeValue.getMatchContribution());
			valueElement.setMismatchPenalty(attributeValue.getMismatchPenalty());

			cbrCaseElement.getAttributeValues().getAttributeValue().add(valueElement);
		}

		// CBR case actions
		cbrCaseElement.setCaseActions(new CBRCaseElement.CaseActions());
		for (final CBRCaseAction action : cbrCase.getCaseActions()) {
			final CaseAction caseActionElement = new CBRCaseElement.CaseActions.CaseAction();
			caseActionElement.setId(action.getId());
			cbrCaseElement.getCaseActions().getCaseAction().add(caseActionElement);
		}

		final com.mindbox.pe.xsd.data.CBRCaseElement.CaseBase caseBase = new CBRCaseElement.CaseBase();
		caseBase.setId(cbrCase.getCaseBase().getId());
		cbrCaseElement.setCaseBase(caseBase);
		cbrCaseElement.setDescription(cbrCase.getDescription());
		return cbrCaseElement;
	}

	private static List<Association> asEntityAssociations(final int entityID, final GenericEntityType type) {
		List<Association> list = new ArrayList<EntityDataElement.Entity.Association>();
		List<GenericEntityAssociationKey> asscKeyList = EntityManager.getInstance().getCompatibilities(entityID, type);
		for (Iterator<GenericEntityAssociationKey> iterator = asscKeyList.iterator(); iterator.hasNext();) {
			list.add(asAssociation(iterator.next()));
		}
		return list;
	}

	private static EntityLink asEntityLink(final String type, final int id) {
		final EntityLink entityLink = new EntityLink();
		entityLink.setId(id);
		entityLink.setType(type);
		return entityLink;
	}

	/**
	 * @since 3.0.0
	 */
	private static List<Entity> asGenericEntityElements() {
		final List<Entity> entityElements = new ArrayList<EntityDataElement.Entity>();
		final GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < types.length; i++) {
			final String typeName = types[i].toString();
			for (final GenericEntity entity : EntityManager.getInstance().getAllEntities(types[i])) {
				final Entity entityElement = new EntityDataElement.Entity();
				entityElement.setId(entity.getId());
				entityElement.setType(typeName);
				entityElement.setParentID(entity.getParentID());
				entityElement.getProperty().add(asPropertyElement("name", entity.getName()));

				// write properties
				String[] props = entity.getProperties();
				for (int j = 0; j < props.length; j++) {
					Object value = entity.getProperty(props[j]);
					String valueStr = null;
					if (value == null) {
						valueStr = "";
					}
					else if (value instanceof Date) {
						valueStr = ConfigUtil.toDateXMLString((Date) value);
					}
					else {
						valueStr = value.toString();
					}
					entityElement.getProperty().add(asPropertyElement(props[j], valueStr));
				}

				// write linked generic categories
				for (Iterator<MutableTimedAssociationKey> parentIter = entity.getCategoryIterator(); parentIter.hasNext();) {
					entityElement.getAssociation().add(asAssociation("category", parentIter.next()));
				}

				// write compatibility data
				entityElement.getAssociation().addAll(asEntityAssociations(entity.getID(), types[i]));

				entityElements.add(entityElement);
			}
		}
		return entityElements;
	}

	private static GuidelineAction asGuidelineActionElement(final ActionTypeDefinition actionType) throws ExportException {
		final GuidelineAction guidelineActionElement = new GuidelineAction();
		guidelineActionElement.setId(actionType.getId());
		guidelineActionElement.setName(actionType.getName());
		guidelineActionElement.setDescription(actionType.getDescription());
		guidelineActionElement.setDeploymentRule(actionType.getDeploymentRule());
		TemplateUsageType[] usages = actionType.getUsageTypes();
		if (usages != null) {
			for (final TemplateUsageType usageType : usages) {
				guidelineActionElement.getUsage().add(usageType.toString());
			}
		}
		guidelineActionElement.setParameters(asActionParameters(actionType));
		return guidelineActionElement;
	}

	private static Parent asParentKeyElement(final TimedAssociationKey asscKey) {
		final Parent parent = new Parent();
		parent.setParentID(asscKey.getAssociableID());
		parent.setActivationDates(asActivationDates(asscKey.getEffectiveDate(), asscKey.getExpirationDate()));
		return parent;
	}

	private static PropertyElement asPropertyElement(final String property, String value) {
		final PropertyElement propertyElement = new PropertyElement();
		propertyElement.setName(property);
		propertyElement.setValue(value);
		return propertyElement;
	}

	private static TemplateColumnElement asTemplateColumnElement(final GridTemplateColumn column) throws ExportException {
		final TemplateColumnElement templateColumnElement = new TemplateColumnElement();
		templateColumnElement.setId(column.getId());
		templateColumnElement.setName(column.getName());
		templateColumnElement.setTitle(column.getTitle());
		templateColumnElement.setDescription(column.getDescription());
		templateColumnElement.setColor(column.getColor());
		templateColumnElement.setFont(column.getFont());
		templateColumnElement.setWidth(column.getColumnWidth());
		if (column.getUsageType() != null) {
			templateColumnElement.setUsage(column.getUsageType().toString());
		}
		final DataSpecElement dataSpecElement = new DataSpecElement();
		dataSpecElement.setType(column.getColumnDataSpecDigest().getType());
		dataSpecElement.setAllowBlank(column.getColumnDataSpecDigest().isBlankAllowed());
		dataSpecElement.setMultipleSelect(column.getColumnDataSpecDigest().isMultiSelectAllowed());
		dataSpecElement.setShowLhsAttribute(column.getColumnDataSpecDigest().isLHSAttributeVisible());
		dataSpecElement.setSortEnumValue(column.getColumnDataSpecDigest().isEnumValueNeedSorted());
		dataSpecElement.setMaxValue(column.getColumnDataSpecDigest().getMaxValue());
		dataSpecElement.setMinValue(column.getColumnDataSpecDigest().getMinValue());
		dataSpecElement.setPrecision((column.getColumnDataSpecDigest().isPrecisionSet() ? column.getColumnDataSpecDigest().getPrecision() : null));

		// next three rules added for entity column support
		if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
			dataSpecElement.setAllowCategory(column.getColumnDataSpecDigest().getAllowCategory() != null && Constants.VALUE_YES.equals(column.getColumnDataSpecDigest().getAllowCategory()));
			dataSpecElement.setAllowEntity(column.getColumnDataSpecDigest().getAllowEntity() != null && Constants.VALUE_YES.equals(column.getColumnDataSpecDigest().getAllowEntity()));
			dataSpecElement.setEntityType(column.getColumnDataSpecDigest().getEntityType());
		}
		else if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
			dataSpecElement.setEnumType(column.getColumnDataSpecDigest().getEnumSourceType().toString());
			if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.COLUMN) {
				if (column.getColumnDataSpecDigest().hasEnumValue()) {
					for (final String columnEnumValue : column.getColumnDataSpecDigest().getAllColumnEnumValues()) {
						dataSpecElement.getEnumValue().add(columnEnumValue);
					}
				}
			}
			else if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.DOMAIN_ATTRIBUTE) {
				dataSpecElement.setEnumAttribute(column.getColumnDataSpecDigest().getMappedAttribute());
			}
			else if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.EXTERNAL) {
				dataSpecElement.setEnumSourceName(column.getColumnDataSpecDigest().getEnumSourceName());
				if (!UtilBase.isEmpty(column.getColumnDataSpecDigest().getEnumSelectorColumnName())) {
					dataSpecElement.setEnumSelectorColumn(column.getColumnDataSpecDigest().getEnumSelectorColumnName());
				}
			}
		}

		List<ColumnAttributeItemDigest> attributeItemList = column.getColumnDataSpecDigest().getAllAttributeItems();
		if (attributeItemList != null && !attributeItemList.isEmpty()) {
			for (ColumnAttributeItemDigest element : attributeItemList) {
				final AttributeItem attributeItem = new AttributeItem();
				attributeItem.setName(element.getName());
				attributeItem.setDisplayValue(element.getDisplayValue());
				dataSpecElement.getAttributeItem().add(attributeItem);
			}
		}
		templateColumnElement.setDataspec(dataSpecElement);

		if (column.hasMessageFragmentDigest()) {
			templateColumnElement.setColumnMessages(new ColumnMessages());
			for (final ColumnMessageFragmentDigest element : column.getAllMessageFragmentDigests()) {
				final ColumnMessage columnMessage = new ColumnMessage();
				columnMessage.setType(element.getType().value());
				if (element.getCellSelection() != null) {
					columnMessage.setCellSelection(element.getCellSelection().value());
				}
				columnMessage.setEnumDelimiter(element.getEnumDelimiter());
				columnMessage.setEnumFinalDelimiter(element.getEnumFinalDelimiter());
				columnMessage.setEnumPrefix(element.getEnumPrefix());
				if (element.getRangeStyle() != null) {
					columnMessage.setRangeStyle(element.getRangeStyle().value());
				}
				// TT-87
				columnMessage.setMessageText(element.getText());
				templateColumnElement.getColumnMessages().getColumnMessage().add(columnMessage);
			}
		}
		return templateColumnElement;
	}

	private static GuidelineTemplate asGuidelineTemplate(final GridTemplate template) throws ExportException {
		final GuidelineTemplate guidelineTemplate = new GuidelineTemplate();
		guidelineTemplate.setId(template.getId());
		guidelineTemplate.setName(template.getName());
		guidelineTemplate.setVersion(template.getVersion());
		guidelineTemplate.setParentID(template.getParentTemplateID());
		guidelineTemplate.setFitToScreen(template.fitToScreen());
		guidelineTemplate.setMaxRows(template.getMaxNumOfRows());
		guidelineTemplate.setStatus(template.getStatus());
		guidelineTemplate.setUsage(template.getUsageType().toString());
		guidelineTemplate.setComment(template.getComment());
		guidelineTemplate.setDescription(template.getDescription());
		guidelineTemplate.setCompleteCols(UtilBase.toString(template.getCompletenessColumns()));
		guidelineTemplate.setConsistentCols(UtilBase.toString(template.getConsistencyColumns()));
		// write columns
		guidelineTemplate.setColumns(new GuidelineTemplate.Columns());
		for (int i = 1; i <= template.getNumColumns(); ++i) {
			final GridTemplateColumn column = template.getColumn(i);
			if (column == null) {
				throw new ExportException("Template " + template.getName() + " v." + template.getVersion() + " (" + template.getID() + ") does not have column number " + i);
			}
			guidelineTemplate.getColumns().getColumn().add(asTemplateColumnElement(column));
		}

		// write rules
		guidelineTemplate.setRules(new TemplateRuleSet());
		guidelineTemplate.getRules().getRule().add(asTemplateRuleElement(-1, template.getUsageType(), template));
		for (int i = 1; i <= template.getNumColumns(); ++i) {
			guidelineTemplate.getRules().getRule().add(
					asTemplateRuleElement(i, template.getColumn(i).getUsageType() == null ? template.getUsageType() : template.getColumn(i).getUsageType(), template.getColumn(i)));
		}

		return guidelineTemplate;
	}

	private static TemplateRuleElement asTemplateRuleElement(final int columnNo, final TemplateUsageType usage, final RuleMessageContainer rmContainer) throws ExportException {
		if (rmContainer.getRuleDefinition() != null && !rmContainer.getRuleDefinition().isEmpty()) {
			final TemplateRuleElement templateRuleElement = new TemplateRuleElement();
			final RuleDefinition ruleDefinition = rmContainer.getRuleDefinition();
			templateRuleElement.setId(ruleDefinition.getID());
			templateRuleElement.setName(ruleDefinition.getName());
			templateRuleElement.setUsage(usage.toString());
			templateRuleElement.setDescription(ruleDefinition.getDescription());
			templateRuleElement.setRuleSetID(ruleDefinition.getRuleSetID());
			if (columnNo > 0) {
				final Precondition precondition = new Precondition();
				precondition.setColumnID(columnNo);
				templateRuleElement.getPrecondition().add(precondition);
			}

			templateRuleElement.setDefinition(RuleDefinitionUtil.toString(ruleDefinition));

			final List<TemplateMessageDigest> messageList = rmContainer.getAllMessageDigest();
			if (messageList != null && !messageList.isEmpty()) {
				templateRuleElement.setMessages(new TemplateRuleElement.Messages());
				for (final TemplateMessageDigest element : messageList) {
					final GuidelineMessageElement guidelineMessageElement = new GuidelineMessageElement();
					guidelineMessageElement.setEntityID(element.getEntityID());
					guidelineMessageElement.setConditionalDelimiter(element.getConditionalDelimiter());
					guidelineMessageElement.setConditionalFinalDelimiter(element.getConditionalFinalDelimiter());
					guidelineMessageElement.setMessageText(element.getText());
					templateRuleElement.getMessages().getMessage().add(guidelineMessageElement);
				}
			}
			return templateRuleElement;
		}
		else {
			return null;
		}
	}

	private static TestCondition asTestCondition(TestTypeDefinition testType) throws ExportException {
		final TestCondition testCondition = new TestCondition();
		testCondition.setId(testType.getId());
		testCondition.setName(testType.getName());
		testCondition.setDescription(testType.getDescription());
		testCondition.setDeploymentRule(testType.getDeploymentRule());
		testCondition.setParameters(asActionParameters(testType));
		return testCondition;
	}

	private static ContextElement extractContextElement(AbstractGrid<?> grid) throws ExportException {
		final ContextElement contextElement = new ContextElement();
		if (grid.hasAnyGenericEntityContext()) {
			final GenericEntityType[] types = grid.getGenericEntityTypesInUse();
			for (int i = 0; i < types.length; i++) {
				int[] ids = grid.getGenericEntityIDs(types[i]);
				for (int j = 0; j < ids.length; j++) {
					contextElement.getEntityLink().add(asEntityLink(types[i].toString(), ids[j]));
				}
			}
		}
		if (grid.hasAnyGenericCategoryContext()) {
			final GenericEntityType[] types = grid.getGenericCategoryEntityTypesInUse();
			for (int i = 0; i < types.length; i++) {
				int[] ids = grid.getGenericCategoryIDs(types[i]);
				for (int j = 0; j < ids.length; j++) {
					contextElement.getEntityLink().add(asEntityLink("generic-category:" + types[i].toString(), ids[j]));
				}
			}
		}
		return contextElement;
	}

	private final ExportDataProvider dataProvider;

	private int gridTag = 0;

	private final PowereditorData powereditorData;

	public ExportDataGenerator(final ExportDataProvider dataProvider) {
		this.dataProvider = dataProvider;
		this.powereditorData = new PowereditorData();
	}

	private void addNextID(String type, int nextID, int cache) throws ExportException {
		if (powereditorData.getNextIdData() == null) {
			powereditorData.setNextIdData(new NextIDDataElement());
		}
		final NextId nextId = new NextId();
		nextId.setCache(cache);
		nextId.setSeed(nextID);
		nextId.setType(type);
		powereditorData.getNextIdData().getNextId().add(nextId);
	}

	/**
	 * @since PowerEditor 4.5.0
	 */
	private List<Category> asGenericCategoryElements() {
		List<Category> categoryElements = new ArrayList<EntityDataElement.Category>();
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < types.length; i++) {
			final CategoryType categoryTypeDef = ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(types[i]);
			if (categoryTypeDef != null) {
				for (final GenericCategory category : EntityManager.getInstance().getAllCategories(categoryTypeDef.getTypeID().intValue())) {
					categoryElements.add(asCategoryElement(category, types[i].toString()));
				}
			}
		}
		return categoryElements;
	}

	private GridActivationElement asGridActivationElement(final ParameterGrid grid, final int columnCount) throws ExportException {
		final GridActivationElement gridActivationElement = new GridActivationElement();
		gridActivationElement.setId(grid.getId());
		gridActivationElement.setStatus(grid.getStatus());
		gridActivationElement.setStatusChangedOn(grid.getStatusChangeDate());
		gridActivationElement.setParentID(-1);
		gridActivationElement.setCreatedOn(grid.getCreationDate());
		gridActivationElement.setComment(grid.getComments());

		gridActivationElement.setActivationDates(asActivationDates(grid.getEffectiveDate(), grid.getExpirationDate()));

		// write grid cell values
		if (grid.getCellValues() != null && grid.getCellValues().length() > 0) {
			gridActivationElement.setGridValues(new GridActivationElement.GridValues());
			for (int i = 1; i <= grid.getNumRows(); i++) {
				final Row rowElement = new GridActivationElement.GridValues.Row();
				if (!isRowEmpty(grid, i, columnCount)) {
					for (int j = 1; j <= columnCount; j++) {
						rowElement.getCellValue().add(dataProvider.getCellValue(grid, i, j, ""));
					}
				}
				gridActivationElement.getGridValues().getRow().add(rowElement);
			}
		}
		return gridActivationElement;
	}

	private GridActivationElement asGridActivationElement(final ProductGrid grid, final int columnCount) throws ExportException {
		final GridActivationElement gridActivationElement = new GridActivationElement();
		gridActivationElement.setId(grid.getId());
		gridActivationElement.setStatus(grid.getStatus());
		gridActivationElement.setStatusChangedOn(grid.getStatusChangeDate());
		gridActivationElement.setParentID(grid.getCloneOf());
		gridActivationElement.setCreatedOn(grid.getCreationDate());
		gridActivationElement.setComment(grid.getComments());

		gridActivationElement.setActivationDates(asActivationDates(grid.getEffectiveDate(), grid.getExpirationDate()));

		// write grid cell values
		if (!grid.isEmpty()) {
			gridActivationElement.setGridValues(new GridActivationElement.GridValues());
			for (int i = 1; i <= grid.getNumRows(); i++) {
				final Row rowElement = new GridActivationElement.GridValues.Row();
				if (!isRowEmpty(grid, i, columnCount)) {
					for (int j = 1; j <= columnCount; j++) {
						rowElement.getCellValue().add(dataProvider.getCellValue(grid, i, j, ""));
					}
				}
				gridActivationElement.getGridValues().getRow().add(rowElement);
			}
		}
		return gridActivationElement;
	}

	private List<com.mindbox.pe.xsd.data.GridDataElement.Grid> asGridElements(GridTemplate template, GuidelineReportFilter filter) throws ExportException {
		List<com.mindbox.pe.xsd.data.GridDataElement.Grid> grids = new ArrayList<com.mindbox.pe.xsd.data.GridDataElement.Grid>();
		int templateID = template.getID();
		List<ProductGrid> gridList = dataProvider.getGuidelineGrids(templateID);
		if (gridList != null) {
			for (Iterator<ProductGrid> iter = gridList.iterator(); iter.hasNext();) {
				ProductGrid element = iter.next();
				if (filter.isAcceptable(element)) {
					if (element != null) {
						final com.mindbox.pe.xsd.data.GridDataElement.Grid gridElement = new com.mindbox.pe.xsd.data.GridDataElement.Grid();
						gridElement.setType(GridTypeAttribute.GUIDELINE);
						gridElement.setTemplateID(templateID);
						gridElement.setGridTag(++(this.gridTag));
						gridElement.setContext(extractContextElement(element));

						gridElement.setColumnNames(new ColumnNames());
						for (int col = 1; col <= template.getNumColumns(); col++) {
							AbstractTemplateColumn column = template.getColumn(col);
							gridElement.getColumnNames().getColumn().add(column.getTitle());
						}

						gridElement.getActivation().add(asGridActivationElement(element, template.getNumColumns()));

						grids.add(gridElement);
					}
				}
			}
		}
		return grids;
	}

	void exportCBRData() throws ExportException {
		powereditorData.setCbrData(new CBRDataElement());
		CBRManager cbrManager = CBRManager.getInstance();
		// Write CBRCaseBase
		for (CBRCaseBase caseBase : cbrManager.getCBRCaseBases()) {
			powereditorData.getCbrData().getCbrCaseBase().add(asCBRCaseBaseElement(caseBase));
		}
		// Write CBRCase
		for (CBRCase cbrCase : cbrManager.getCBRCases()) {
			powereditorData.getCbrData().getCbrCase().add(asCBRCaseElement(cbrCase));
		}
		// Write CBRAttribute
		for (CBRAttribute attribute : cbrManager.getCBRAttributes()) {
			powereditorData.getCbrData().getCbrAttribute().add(asCBRAttributeElement(attribute));
		}
	}

	/**
	 * This is replacing exportDateSynonyms() of 4.5.x
	 * @throws ExportException
	 * @since 5.0.0
	 */
	void exportDateData() throws ExportException {
		powereditorData.setDateData(new DateDataElement());
		Collection<DateSynonym> dateSynonyms = DateSynonymManager.getInstance().getAllDateSynonyms();
		List<DateSynonym> list = new ArrayList<DateSynonym>(dateSynonyms);
		Collections.sort(list, new IDObjectComparator<DateSynonym>());
		for (final DateSynonym ds : list) {
			final DateElement dateElement = new DateElement();
			dateElement.setId(ds.getId());
			dateElement.setName(ds.getName());
			dateElement.setDate(ds.getDate());
			dateElement.setDescription(ds.getDescription());
			powereditorData.getDateData().getDateElement().add(dateElement);
		}
	}

	void exportTypeEnumData() throws ExportException {
		powereditorData.setTypeEnumData(new TypeEnumDataElement());
		for (final Map.Entry<String, List<TypeEnumValue>> entry : TypeEnumValueManager.getInstance().getTypeEnumValueMap().entrySet()) {
			final String type = entry.getKey();
			for (final TypeEnumValue typeEnumValue : entry.getValue()) {
				final TypeEnum typeEnum = new TypeEnum();
				typeEnum.setEnumId(typeEnumValue.getId());
				typeEnum.setDisplayLabel(typeEnumValue.getDisplayLabel());
				typeEnum.setEnumValue(typeEnumValue.getValue());
				typeEnum.setType(type);
				powereditorData.getTypeEnumData().getTypeEnum().add(typeEnum);
			}
		}
	}

	/**
	 * This export of entities excludes entities that expired within the days specified
	 * @since PowerEditor 5.0.0
	 * @param useDaysAgo
	 * @param daysAgo
	 * @throws ExportException
	 */
	void exportEntities(boolean useDaysAgo, int daysAgo) throws ExportException {
		LOG.debug(">>> exportEntities: useDaysAgo=" + useDaysAgo + ",daysAgo=" + daysAgo);
		powereditorData.setEntityData(new EntityDataElement());
		powereditorData.getEntityData().getCategory().addAll(asGenericCategoryElements());
		powereditorData.getEntityData().getEntity().addAll(asGenericEntityElements());
	}

	void exportGuidelineActions() throws ExportException {
		powereditorData.setGuidelineActionData(new GuidelineActionDataElement());
		List<ActionTypeDefinition> list = GuidelineFunctionManager.getInstance().getAllActionTypes();
		Collections.sort(list, new IDObjectComparator<ActionTypeDefinition>());
		for (final ActionTypeDefinition element : list) {
			powereditorData.getGuidelineActionData().getGuidelineAction().add(asGuidelineActionElement(element));
		}
	}

	void exportGuidelines(GuidelineReportFilter filter) throws ExportException {
		powereditorData.setGridData(new GridDataElement());

		LOG.debug(">>> exportGuidelines: " + filter);
		// export guidelines for the specified template ID, if it's > -1
		List<GridTemplate> gridTemplateList = null;
		if (filter.isIncludeTemplates()) {
			gridTemplateList = new ArrayList<GridTemplate>();
			if (!filter.getGuidelineTemplateIDs().isEmpty()) {
				for (int templateID : filter.getGuidelineTemplateIDs()) {
					GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
					if (template != null) {
						gridTemplateList.add(template);
					}
					else {
						throw new ExportException("There is no guideline template of id " + templateID);
					}
				}
			}
			if (!filter.getUsageTypes().isEmpty()) {
				for (TemplateUsageType usageType : filter.getUsageTypes()) {
					gridTemplateList.addAll(GuidelineTemplateManager.getInstance().getTemplates(usageType));
				}
			}
			else if (filter.getGuidelineTemplateIDs().isEmpty()) {
				gridTemplateList.addAll(GuidelineTemplateManager.getInstance().getAllTemplates());
			}
		}
		else {
			gridTemplateList = dataProvider.getAllGuidelineTemplates();
		}

		if (gridTemplateList != null) {
			LOG.debug("... exportGuidelines: " + gridTemplateList.size());
			for (Iterator<GridTemplate> iter = gridTemplateList.iterator(); iter.hasNext();) {
				final GridTemplate element = iter.next();
				powereditorData.getGridData().getGrid().addAll(asGridElements(element, filter));
			}
		}
	}

	void exportGuidelineTemplates(List<TemplateUsageType> usageTypes, List<Integer> templateIDs) throws ExportException {
		powereditorData.setTemplateData(new TemplateDataElement());
		if (usageTypes.isEmpty() && templateIDs.isEmpty()) {
			List<GridTemplate> list = dataProvider.getAllGuidelineTemplates();
			Collections.sort(list, new IDObjectComparator<GridTemplate>());
			if (list != null) {
				for (Iterator<GridTemplate> iter = list.iterator(); iter.hasNext();) {
					final GridTemplate template = iter.next();
					powereditorData.getTemplateData().getGuidelineTemplate().add(asGuidelineTemplate(template));
				}
			}
		}
		else {
			if (!templateIDs.isEmpty()) {
				for (int templateID : templateIDs) {
					final GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
					if (template == null) {
						throw new ExportException("No template of id " + templateID + " found");
					}
					powereditorData.getTemplateData().getGuidelineTemplate().add(asGuidelineTemplate(template));
				}
			}
			if (!usageTypes.isEmpty()) {
				for (TemplateUsageType usageType : usageTypes) {
					List<GridTemplate> templateList = GuidelineTemplateManager.getInstance().getTemplates(usageType);
					for (final GridTemplate template : templateList) {
						powereditorData.getTemplateData().getGuidelineTemplate().add(asGuidelineTemplate(template));
					}
				}
			}
		}
	}

	/**
	 * @throws ExportException
	 * @since 5.0.0
	 */
	void exportMetaData(String userID) throws ExportException {
		powereditorData.setMetaData(new MetaDataElement());
		final PeData peData = new PeData();
		peData.setPowerEditorBuild(ConfigurationManager.getInstance().getAppBuild());
		peData.setPowerEditorVersion(ConfigurationManager.getInstance().getAppVersion());
		peData.setDateExported(new Date());
		powereditorData.getMetaData().setPeData(peData);
		powereditorData.getMetaData().setUserData(new MetaDataElement.UserData());
		powereditorData.getMetaData().getUserData().setUserName(userID);
		final SystemData systemData = new SystemData();
		systemData.setJavaVersion(System.getProperty("java.version"));
		systemData.setDatabase(ConfigurationManager.getInstance().getServerConfigHelper().getDatabaseConfig().getConnectionStr());
		powereditorData.getMetaData().setSystemData(systemData);
	}

	void exportNextIDSeeds() throws ExportException {
		try {
			addNextID(DBIdGenerator.FILTER_ID, DBIdGenerator.getInstance().nextFilterID(), 2);
			addNextID(DBIdGenerator.GRID_ID, DBIdGenerator.getInstance().nextGridID(), 20);
			addNextID(DBIdGenerator.SEQUENTIAL_ID, DBIdGenerator.getInstance().nextSequentialID(), 10);
			addNextID(DBIdGenerator.AUDIT_ID, DBIdGenerator.getInstance().nextAuditID(), 10);
			addNextID(DBIdGenerator.RULE_ID, DBIdGenerator.getInstance().nextRuleID(), 20);
		}
		catch (SapphireException e) {
			LOG.error("Failed to write next-id", e);
			throw new ExportException("Failed to write next id: " + e);
		}
	}

	void exportParameters(GuidelineReportFilter filter) throws ExportException {
		LOG.debug(">>> exportParameters: " + filter);
		// write parameter grids
		List<ParameterTemplate> paramTemplateList = null;
		if (filter.getParameterTemplateIDs().isEmpty()) {
			paramTemplateList = dataProvider.getAllParameterTemplates();
		}
		else {
			paramTemplateList = new ArrayList<ParameterTemplate>();
			for (int templateID : filter.getParameterTemplateIDs()) {
				ParameterTemplate template = ParameterTemplateManager.getInstance().getTemplate(templateID);
				if (template != null) {
					paramTemplateList.add(template);
				}
				else {
					throw new ExportException("There is no parameter template of id " + templateID);
				}
			}
		}

		if (powereditorData.getGridData() == null) {
			powereditorData.setGridData(new GridDataElement());
		}

		if (paramTemplateList != null) {

			for (final ParameterTemplate template : paramTemplateList) {
				int templateID = template.getID();
				for (Iterator<ParameterGrid> iter = dataProvider.getParameterGrids(templateID).iterator(); iter.hasNext();) {
					final ParameterGrid parameterGrid = iter.next();
					if (filter.isAcceptable(parameterGrid)) {
						try {
							final com.mindbox.pe.xsd.data.GridDataElement.Grid gridElement = new GridDataElement.Grid();
							gridElement.setType(GridTypeAttribute.PARAMETER);
							gridElement.setTemplateID(templateID);
							gridElement.setGridTag(++(this.gridTag));
							gridElement.setContext(extractContextElement(parameterGrid));

							gridElement.setColumnNames(new GridDataElement.Grid.ColumnNames());
							for (int col = 1; col <= template.getNumColumns(); col++) {
								AbstractTemplateColumn column = template.getColumn(col);
								gridElement.getColumnNames().getColumn().add(column.getTitle());
							}

							gridElement.getActivation().add(asGridActivationElement(parameterGrid, template.getColumnCount()));

							powereditorData.getGridData().getGrid().add(gridElement);
						}
						catch (Exception e) {
							LOG.error("Failed to write activations for grid " + parameterGrid, e);
							throw new ExportException(String.format("Failed to write activations for grid %s: %s", parameterGrid, e.getMessage()));
						}
					}
				}
			}
		}
	}

	void exportSecurity() throws ExportException {
		powereditorData.setSecurityData(new SecurityDataElement());

		// write privileges
		powereditorData.getSecurityData().setPrivileges(new PrivilegesElement());
		final Privilege[] privileges = dataProvider.getAllPrivileges();
		Arrays.sort(privileges, new IDObjectComparator<Privilege>());
		for (final Privilege privilege : privileges) {
			final com.mindbox.pe.xsd.data.PrivilegesElement.Privilege element = new com.mindbox.pe.xsd.data.PrivilegesElement.Privilege();
			element.setId(privilege.getId());
			element.setName(privilege.getName());
			element.setDisplayName(privilege.getDisplayString());
			element.setPrivilegeType(privilege.getPrivilegeType());
			powereditorData.getSecurityData().getPrivileges().getPrivilege().add(element);
		}

		// write rules
		powereditorData.getSecurityData().setRoles(new RolesElement());
		final Role[] roles = dataProvider.getAllRoles();
		Arrays.sort(roles, new IDObjectComparator<Role>());
		for (final Role role : roles) {
			final com.mindbox.pe.xsd.data.RolesElement.Role roleElement = new RolesElement.Role();
			roleElement.setId(role.getId());
			roleElement.setName(role.getName());

			final Privilege[] privilegesForRole = (Privilege[]) role.getPrivileges().toArray(new Privilege[0]);
			Arrays.sort(privilegesForRole, new IDObjectComparator<Privilege>());
			for (int j = 0; j < privilegesForRole.length; j++) {
				if (privilegesForRole[j] != null) {
					roleElement.getPrivilegeLink().add(privilegesForRole[j].getID());
				}
			}
			powereditorData.getSecurityData().getRoles().getRole().add(roleElement);
		}

		if (!LDAPUserManagementProvider.class.isInstance(ServiceProviderFactory.getUserManagementProvider())) {
			powereditorData.getSecurityData().setUsers(new UsersElement());
			for (final User user : dataProvider.getAllUsers()) {
				final com.mindbox.pe.xsd.data.UsersElement.User userElement = new UsersElement.User();
				userElement.setId(user.getUserID());
				userElement.setName(user.getName());
				userElement.setStatus(user.getStatus() == null ? UserStatusAttribute.ACTIVE : UserStatusAttribute.valueOf(user.getStatus().toUpperCase()));
				userElement.setPasswordChangeRequired(user.getPasswordChangeRequired());
				userElement.setFailedLoginCounter(user.getFailedLoginCounter());

				//<user-password> tag
				for (Iterator<UserPassword> iterator = user.getPasswordHistory().iterator(); iterator.hasNext();) {
					final UserPassword userPassword = iterator.next();
					final com.mindbox.pe.xsd.data.UsersElement.User.UserPassword passwordElement = new UsersElement.User.UserPassword();
					passwordElement.setEncryptedPassword(userPassword.getPassword());
					passwordElement.setPasswordChangeDate(userPassword.getPasswordChangeDate());
					userElement.getUserPassword().add(passwordElement);
				}

				// <role-link> tag
				for (Iterator<Role> iterator = user.getRoles().iterator(); iterator.hasNext();) {
					final Role role = iterator.next();
					if (role != null) {
						userElement.getRoleLink().add(role.getID());
					}
				}
				powereditorData.getSecurityData().getUsers().getUser().add(userElement);
			}
		}
	}

	void exportTestConditions() throws ExportException {
		powereditorData.setTestConditionData(new TestConditionDataElement());
		List<TestTypeDefinition> list = GuidelineFunctionManager.getInstance().getAllTestTypes();
		Collections.sort(list, new IDObjectComparator<TestTypeDefinition>());
		for (final TestTypeDefinition element : list) {
			powereditorData.getTestConditionData().getTestCondition().add(asTestCondition(element));
		}
	}

	public PowereditorData getPowereditorData() {
		return powereditorData;
	}

	private final boolean isRowEmpty(final AbstractGrid<?> grid, final int row, final int columnCount) throws ExportException {
		String value = null;
		for (int col = 1; col <= columnCount; col++) {
			value = dataProvider.getCellValue(grid, row, col, null);
			if (value != null) {
				return false;
			}
		}
		return true;
	}
}