package com.mindbox.pe.server.imexport.digest;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.digester.Digester;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.model.CBRCaseAction;
import com.mindbox.pe.model.CBREnumeratedValue;
import com.mindbox.pe.model.ColumnAttributeItemDigest;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;

/**
 * Digester for parsing import XML.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ImportXMLDigester {

	private static ImportXMLDigester instance = null;

	public static ImportXMLDigester getInstance() {
		if (instance == null) {
			instance = new ImportXMLDigester();
		}
		return instance;
	}

	private ImportXMLDigester() {
		// TT 2031
		ConvertUtils.register(new Converter() {
			@SuppressWarnings("unchecked")
			@Override
			public Object convert(Class arg0, Object value) {
				Integer intObj = value == null ? new Integer(0) : Integer.valueOf(value.toString());
				return intObj;
			}

		}, int.class);
	}

	private Digester getTemplateRuleDigester() {
		Digester digester = new Digester();
		digester.setValidating(false);

		digester.addObjectCreate("powereditor-data/template-data/guideline-template", TemplateRuleContainer.class);
		digester.addSetProperties("powereditor-data/template-data/guideline-template");
		digester.addSetNext("powereditor-data/template-data/guideline-template", "addObject");

		digester.addObjectCreate("powereditor-data/template-data/guideline-template/rules/rule", TemplateRule.class);
		digester.addSetProperties("powereditor-data/template-data/guideline-template/rules/rule");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/rules/rule/description", "description");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/rules/rule/definition", "definition");
		digester.addSetNext("powereditor-data/template-data/guideline-template/rules/rule", "addObject");

		digester.addObjectCreate(
				"powereditor-data/template-data/guideline-template/rules/rule/messages/message",
				TemplateMessageDigest.class);
		digester.addSetProperties("powereditor-data/template-data/guideline-template/rules/rule/messages/message");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/rules/rule/messages/message/message-text", "text");
		digester.addSetNext("powereditor-data/template-data/guideline-template/rules/rule/messages/message", "addMessage");

		digester.addObjectCreate("powereditor-data/template-data/guideline-template/rules/rule/precondition", RulePrecondition.class);
		digester.addSetProperties("powereditor-data/template-data/guideline-template/rules/rule/precondition");
		digester.addSetNext("powereditor-data/template-data/guideline-template/rules/rule/precondition", "addPrecondition");

		return digester;
	}

	private Digester getImportDigester() {
		Digester digester = new Digester();
		// common elements -------------------
		digester.addObjectCreate("*/activation-dates", ActivationDates.class);
		digester.addSetProperties("*/activation-dates");
		digester.addBeanPropertySetter("*/activation-dates/activation-date", "activationDate");
		digester.addBeanPropertySetter("*/activation-dates/expiration-date", "expirationDate");
		digester.addBeanPropertySetter("*/activation-dates/activation-date-ID", "effectiveDateID");
		digester.addBeanPropertySetter("*/activation-dates/expiration-date-ID", "expirationDateID");
		digester.addSetNext("*/activation-dates", "setActivationDates");

		digester.addObjectCreate("*/context/entity-link", EntityIdentity.class);
		digester.addSetProperties("*/context/entity-link");
		digester.addSetNext("*/context/entity-link", "addObject");

		// entity data -----------------------

		// category
		digester.addObjectCreate("powereditor-data/entity-data/category", CategoryDigest.class);
		digester.addSetProperties("powereditor-data/entity-data/category");
		digester.addSetNext("powereditor-data/entity-data/category", "addObject");

		digester.addObjectCreate("powereditor-data/entity-data/category/parent", Parent.class);
		digester.addBeanPropertySetter("powereditor-data/entity-data/category/parent/parentID", "id");
		digester.addSetNext("powereditor-data/entity-data/category/parent", "addObject");

		digester.addObjectCreate("powereditor-data/entity-data/category/property", Property.class);
		digester.addSetProperties("powereditor-data/entity-data/category/property");
		digester.addSetNext("powereditor-data/entity-data/category/property", "addObject");

		// entity
		digester.addObjectCreate("powereditor-data/entity-data/entity", Entity.class);
		digester.addSetProperties("powereditor-data/entity-data/entity");
		digester.addSetNext("powereditor-data/entity-data/entity", "addObject");

		digester.addObjectCreate("powereditor-data/entity-data/entity/property", Property.class);
		digester.addSetProperties("powereditor-data/entity-data/entity/property");
		digester.addSetNext("powereditor-data/entity-data/entity/property", "addObject");

		digester.addObjectCreate("powereditor-data/entity-data/entity/association", Association.class);
		digester.addSetNext("powereditor-data/entity-data/entity/association", "addObject");

		digester.addObjectCreate("powereditor-data/entity-data/entity/association/entity-link", EntityIdentity.class);
		digester.addSetProperties("powereditor-data/entity-data/entity/association/entity-link");
		digester.addSetNext("powereditor-data/entity-data/entity/association/entity-link", "setEntityLink");

		// next-id data ----------------------
		digester.addObjectCreate("powereditor-data/next-id-data/next-id", NextIDSeed.class);
		digester.addSetProperties("powereditor-data/next-id-data/next-id");
		digester.addSetNext("powereditor-data/next-id-data/next-id", "addObject");

		// filter data -----------------------

		digester.addObjectCreate("powereditor-data/filter-data/filter", Filter.class);
		digester.addSetProperties("powereditor-data/filter-data/filter");
		digester.addBeanPropertySetter("powereditor-data/filter-data/filter/criteria", "criteria");
		digester.addSetNext("powereditor-data/filter-data/filter", "addObject");

		// security data ---------------------

		digester.addObjectCreate("powereditor-data/security-data/privileges/privilege", Privilege.class);
		digester.addSetProperties("powereditor-data/security-data/privileges/privilege");
		digester.addSetNext("powereditor-data/security-data/privileges/privilege", "addObject");

		digester.addObjectCreate("powereditor-data/security-data/roles/role", Role.class);
		digester.addSetProperties("powereditor-data/security-data/roles/role");
		digester.addBeanPropertySetter("powereditor-data/security-data/roles/role/privilege-link", "privilegeLink");
		digester.addSetNext("powereditor-data/security-data/roles/role", "addObject");

		digester.addObjectCreate("powereditor-data/security-data/users/user", User.class);
		digester.addSetProperties("powereditor-data/security-data/users/user");
		digester.addBeanPropertySetter("powereditor-data/security-data/users/user/role-link", "roleLink");

		digester.addObjectCreate("powereditor-data/security-data/users/user/user-password", UserPassword.class);
		digester.addSetProperties("powereditor-data/security-data/users/user/user-password", new String[] {
				"encryptedPassword",
				"passwordChangeDate" }, new String[] { "encryptedPassword", "passwordChangeDateString" });
		digester.addSetNext("powereditor-data/security-data/users/user/user-password", "addUserPassword");

		digester.addObjectCreate("powereditor-data/security-data/users/user/entity-link", EntityIdentity.class);
		digester.addSetProperties("powereditor-data/security-data/users/user/entity-link");
		digester.addSetNext("powereditor-data/security-data/users/user/entity-link", "addEntityLink");
		digester.addSetNext("powereditor-data/security-data/users/user", "addObject");

		// grid data ---------------------------
		digester.addObjectCreate("powereditor-data/grid-data/grid", Grid.class);
		digester.addSetProperties("powereditor-data/grid-data/grid");
		digester.addSetNext("powereditor-data/grid-data/grid", "addObject");

		digester.addBeanPropertySetter("powereditor-data/grid-data/grid/column-names/column", "columnName");

		// activation
		digester.addObjectCreate("powereditor-data/grid-data/grid/activation", GridActivation.class);
		digester.addSetProperties("powereditor-data/grid-data/grid/activation");
		digester.addSetNext("powereditor-data/grid-data/grid/activation", "addObject");
		digester.addBeanPropertySetter("powereditor-data/grid-data/grid/activation/comment", "comment");

		digester.addObjectCreate("powereditor-data/grid-data/grid/activation/grid-values/row", GridRow.class);
		digester.addBeanPropertySetter("powereditor-data/grid-data/grid/activation/grid-values/row/cell-value", "cellValue");
		digester.addSetNext("powereditor-data/grid-data/grid/activation/grid-values/row", "addRow");

		// ad-hoc rule data
		digester.addObjectCreate("powereditor-data/rule-data/ruleset", RuleSet.class);
		digester.addSetProperties("powereditor-data/rule-data/ruleset");
		digester.addBeanPropertySetter("powereditor-data/rule-data/ruleset/description", "description");
		digester.addSetNext("powereditor-data/rule-data/ruleset", "addObject");

		digester.addObjectCreate("powereditor-data/rule-data/ruleset/rules/rule", Rule.class);
		digester.addSetProperties("powereditor-data/rule-data/ruleset/rules/rule");
		digester.addSetNext("powereditor-data/rule-data/ruleset/rules/rule", "addObject");

		// [4] Rules for guildeine templates --------------------------------------------------

		digester.addObjectCreate("powereditor-data/template-data/guideline-template", GridTemplate.class);
		digester.addSetProperties(
				"powereditor-data/template-data/guideline-template",
				new String[] { "id", "parentID", "usage" },
				new String[] { "idString", "parentTemplateID", "usageTypeString" });
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/comment", "comment");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/complete-cols", "completeColumnsString");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/consistent-cols", "consistentColumnsString");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/description", "description");
		digester.addSetNext("powereditor-data/template-data/guideline-template", "addObject");

		// [5] rules for guideline template columns --------------------------------------------

		digester.addObjectCreate("powereditor-data/template-data/guideline-template/columns/column", GridTemplateColumn.class);
		digester.addSetProperties(
				"powereditor-data/template-data/guideline-template/columns/column",
				new String[] { "id", "attributeMap" },
				new String[] { "idString", "attributeMapOldStr" });
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/columns/column/color", "color");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/columns/column/description", "description");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/columns/column/font", "font");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/columns/column/width", "columnWidth");
		digester.addSetNext("powereditor-data/template-data/guideline-template/columns/column", "addGridTemplateColumn");

		digester.addObjectCreate("powereditor-data/template-data/guideline-template/columns/column/dataspec", ColumnDataSpecDigest.class);
		digester.addSetProperties("powereditor-data/template-data/guideline-template/columns/column/dataspec");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/columns/column/dataspec/max-value", "maxValue");
		digester.addBeanPropertySetter("powereditor-data/template-data/guideline-template/columns/column/dataspec/min-value", "minValue");
		digester.addBeanPropertySetter(
				"powereditor-data/template-data/guideline-template/columns/column/dataspec/precision",
				"precisionImport");
		digester.addBeanPropertySetter(
				"powereditor-data/template-data/guideline-template/columns/column/dataspec/allow-category",
				"allowCategory");
		digester.addBeanPropertySetter(
				"powereditor-data/template-data/guideline-template/columns/column/dataspec/allow-entity",
				"allowEntity");
		digester.addBeanPropertySetter(
				"powereditor-data/template-data/guideline-template/columns/column/dataspec/entity-type",
				"entityType");
		digester.addBeanPropertySetter(
				"powereditor-data/template-data/guideline-template/columns/column/dataspec/enum-type",
				"enumSourceTypeStr");
		digester.addBeanPropertySetter(
				"powereditor-data/template-data/guideline-template/columns/column/dataspec/enum-attribute",
				"attributeMap");
		digester.addBeanPropertySetter(
				"powereditor-data/template-data/guideline-template/columns/column/dataspec/enum-source-name",
				"enumSourceName");
		digester.addBeanPropertySetter(
				"powereditor-data/template-data/guideline-template/columns/column/dataspec/enum-selector-column",
				"enumSelectorColumnName");
		// process enumerated values 
		digester.addCallMethod("powereditor-data/template-data/guideline-template/columns/column/dataspec/enum-value", "addColumnEnumValue", 1);
		digester.addCallParam("powereditor-data/template-data/guideline-template/columns/column/dataspec/enum-value", 0);
		digester.addSetNext("powereditor-data/template-data/guideline-template/columns/column/dataspec", "setDataSpecDigest");
		// process attribute items
		digester.addObjectCreate(
				"powereditor-data/template-data/guideline-template/columns/column/dataspec/attribute-item",
				ColumnAttributeItemDigest.class);
		digester.addSetProperties("powereditor-data/template-data/guideline-template/columns/column/dataspec/attribute-item");
		digester.addSetNext("powereditor-data/template-data/guideline-template/columns/column/dataspec/attribute-item", "addAttributeItem");

		// process ColumnMessage fragments
		digester.addObjectCreate(
				"powereditor-data/template-data/guideline-template/columns/column/column-messages/column-message",
				ColumnMessageFragmentDigest.class);
		digester.addSetProperties("powereditor-data/template-data/guideline-template/columns/column/column-messages/column-message");
		digester.addBeanPropertySetter(
				"powereditor-data/template-data/guideline-template/columns/column/column-messages/column-message/message-text",
				"text");
		digester.addSetNext(
				"powereditor-data/template-data/guideline-template/columns/column/column-messages/column-message",
				"addColumnMessageFragment");

		// [6] Guideline actions ------------------------------------------------------

		digester.addObjectCreate("*/parameters/parameter", FunctionParameterDefinition.class);
		digester.addSetProperties("*/parameters/parameter", new String[] { "id", "deployType" }, new String[] {
				"idString",
				"deployTypeString" });
		digester.addBeanPropertySetter("*/parameters/parameter/data-string", "paramDataString");
		digester.addSetNext("*/parameters/parameter", "addParameterDefinition");

		digester.addObjectCreate("powereditor-data/guideline-action-data/guideline-action", ActionTypeDefinition.class);
		digester.addSetProperties(
				"powereditor-data/guideline-action-data/guideline-action",
				new String[] { "id" },
				new String[] { "idString" });
		digester.addBeanPropertySetter("powereditor-data/guideline-action-data/guideline-action/description", "description");
		digester.addBeanPropertySetter("powereditor-data/guideline-action-data/guideline-action/deployment-rule", "deploymentRule");

		// NOTE:
		// Last argument of 0 is misleading but this is how digester works.
		// This number is additional arguments in addition to the body from the specified rule;
		// this will pass body of <usage> tag as the only argument to addUsageTypeString method
		digester.addCallMethod("powereditor-data/guideline-action-data/guideline-action/usage", "addUsageTypeString", 0);
		digester.addSetNext("powereditor-data/guideline-action-data/guideline-action", "addObject");

		digester.addObjectCreate("powereditor-data/test-condition-data/test-condition", TestTypeDefinition.class);
		digester.addSetProperties("powereditor-data/test-condition-data/test-condition", new String[] { "id" }, new String[] { "idString" });
		digester.addBeanPropertySetter("powereditor-data/test-condition-data/test-condition/description", "description");
		digester.addBeanPropertySetter("powereditor-data/test-condition-data/test-condition/deployment-rule", "deploymentRule");
		digester.addSetNext("powereditor-data/test-condition-data/test-condition", "addObject");

		digester.addObjectCreate("powereditor-data/date-synonyms/date-synonym", DateSynonym.class);
		digester.addSetProperties("powereditor-data/date-synonyms/date-synonym", new String[] { "id" }, new String[] { "idString" });
		digester.addBeanPropertySetter("powereditor-data/date-synonyms/date-synonym/description", "description");
		digester.addBeanPropertySetter("powereditor-data/date-synonyms/date-synonym/date", "dateString");
		digester.addSetNext("powereditor-data/date-synonyms/date-synonym", "addObject");

		// [7] Date Data -----------------------------------
		//new from 5.0 xsd
		digester.addObjectCreate("powereditor-data/date-data/DateElement", DateSynonym.class);
		digester.addSetProperties(
				"powereditor-data/date-data/DateElement",
				new String[] { "id", "date", "name", "description" },
				new String[] { "idString", "dateString", "name", "description" });
		//digester.addSetProperties("powereditor-data/date-data/DateElement");
		digester.addSetNext("powereditor-data/date-data/DateElement", "addObject");

		// [8] CBR Data ------------------------------------

		// CBR case base
		digester.addObjectCreate("powereditor-data/cbr-data/cbr-case-base", CBRCaseBaseDigest.class);
		digester.addSetProperties("powereditor-data/cbr-data/cbr-case-base", new String[] { "id", "name" }, new String[] {
				"idString",
				"name" });
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-case-base/case-class", CBRCaseClassCreationFactory.class);
		digester.addSetNext("powereditor-data/cbr-data/cbr-case-base/case-class", "setCaseClass");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-case-base/description", "description");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-case-base/index-file", "indexFile");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-case-base/match-threshold", "matchThreshold");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-case-base/maximum-matches", "maximumMatches");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-case-base/naming-attribute", "namingAttribute");
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-case-base/scoring-function", CBRScoringFunctionCreationFactory.class);
		digester.addSetNext("powereditor-data/cbr-data/cbr-case-base/scoring-function", "setScoringFunction");
		digester.addSetNext("powereditor-data/cbr-data/cbr-case-base", "addObject");

		// CBR case
		digester.addObjectCreate("powereditor-data/cbr-data/cbr-case", CBRCaseDigest.class);
		digester.addSetProperties("powereditor-data/cbr-data/cbr-case", new String[] { "id", "name" }, new String[] { "idString", "name" });
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-case/case-base", CBRCaseBaseCreationFactory.class);
		digester.addSetNext("powereditor-data/cbr-data/cbr-case/case-base", "setCaseBase");
		// handle attribute values
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-case/attribute-values", new ListCreationFactory<CBRAttributeValue>());
		digester.addObjectCreate("powereditor-data/cbr-data/cbr-case/attribute-values/attribute-value", CBRAttributeValue.class);
		digester.addSetProperties(
				"powereditor-data/cbr-data/cbr-case/attribute-values/attribute-value",
				new String[] { "id", "value" },
				new String[] { "idString", "name" });
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-case/attribute-values/attribute-value/description", "description");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-case/attribute-values/attribute-value/match-contribution", "matchContribution");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-case/attribute-values/attribute-value/mismatch-penalty", "mismatchPenalty");
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-case/attribute-values/attribute-value/attribute", CBRAttributeCreationFactory.class);
		digester.addSetNext("powereditor-data/cbr-data/cbr-case/attribute-values/attribute-value/attribute", "setAttribute");
		digester.addSetNext("powereditor-data/cbr-data/cbr-case/attribute-values/attribute-value", "add");
		digester.addSetNext("powereditor-data/cbr-data/cbr-case/attribute-values", "setAttributeValues");
		// handle case actions
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-case/case-actions", new ListCreationFactory<CBRCaseAction>());
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-case/case-actions/case-action", CBRCaseActionCreationFactory.class);
		digester.addSetNext("powereditor-data/cbr-data/cbr-case/case-actions/case-action", "add");
		digester.addSetNext("powereditor-data/cbr-data/cbr-case/case-actions", "setCaseActions");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-case/description", "description");
		digester.addSetNext("powereditor-data/cbr-data/cbr-case", "addObject");

		// CBR attributes		
		digester.addObjectCreate("powereditor-data/cbr-data/cbr-attribute", CBRAttribute.class);
		digester.addSetProperties("powereditor-data/cbr-data/cbr-attribute", new String[] { "id", "name" }, new String[] {
				"idString",
				"name" });
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-attribute/attribute-type", CBRAttributeTypeCreationFactory.class);
		digester.addSetNext("powereditor-data/cbr-data/cbr-attribute/attribute-type", "setAttributeType");
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-attribute/case-base", CBRCaseBaseCreationFactory.class);
		digester.addSetNext("powereditor-data/cbr-data/cbr-attribute/case-base", "setCaseBase");
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-attribute/enum-values", new ListCreationFactory<CBREnumeratedValue>());
		digester.addObjectCreate("powereditor-data/cbr-data/cbr-attribute/enum-values/enum-value", CBREnumeratedValue.class);
		digester.addSetProperties(
				"powereditor-data/cbr-data/cbr-attribute/enum-values/enum-value",
				new String[] { "id", "name" },
				new String[] { "idString", "name" });
		digester.addSetNext("powereditor-data/cbr-data/cbr-attribute/enum-values/enum-value", "add");
		digester.addSetNext("powereditor-data/cbr-data/cbr-attribute/enum-values", "setEnumeratedValues");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-attribute/description", "description");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-attribute/absence-penalty", "absencePenalty");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-attribute/highest-value", "highestValue");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-attribute/lowest-value", "lowestValue");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-attribute/match-contribution", "matchContribution");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-attribute/match-interval", "matchInterval");
		digester.addBeanPropertySetter("powereditor-data/cbr-data/cbr-attribute/mismatch-penalty", "mismatchPenalty");
		digester.addFactoryCreate("powereditor-data/cbr-data/cbr-attribute/value-range", CBRValueRangeCreationFactory.class);
		digester.addSetNext("powereditor-data/cbr-data/cbr-attribute/value-range", "setValueRange");
		digester.addSetNext("powereditor-data/cbr-data/cbr-attribute", "addObject");

		return digester;
	}

	public DigestedObjectHolder digestImportXML(String content) throws IOException, SAXException {
		return digestImportXML(content, Level.WARN);
	}

	public DigestedObjectHolder digestImportXML(String content, Level loggerLevel) throws IOException, SAXException {
		Logger.getLogger("org.apache.commons.digester").setLevel(loggerLevel);

		DigestedObjectHolder objectHolder = new DigestedObjectHolder();

		StringReader reader = new StringReader(content);

		// digest the input XML
		Digester digester = getImportDigester();
		digester.push(objectHolder);
		digester.setValidating(false);

		digester.parse(reader);

		reader.close();
		reader = null;

		// digest rules iff rules exist
		if (content.indexOf("<rules>") > 0) {
			reader = new StringReader(content);

			// digest template rules
			digester = null;
			digester = getTemplateRuleDigester();
			digester.push(objectHolder);
			digester.setValidating(false);
			digester.parse(reader);
			reader.close();
		}

		return objectHolder;
	}

}