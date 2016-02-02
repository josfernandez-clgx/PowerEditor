package com.mindbox.pe.server.imexport;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.CBRCaseAction;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.CBREnumeratedValue;
import com.mindbox.pe.model.ColumnAttributeItemDigest;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.RuleMessageContainer;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.model.assckey.GenericEntityAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.TimedAssociationKey;
import com.mindbox.pe.model.comparator.IDObjectComparator;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.server.RuleDefinitionUtil;
import com.mindbox.pe.server.bizlogic.SearchCooridinator;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.db.LDAPUserManagementProvider;
import com.mindbox.pe.server.imexport.digest.Grid;
import com.mindbox.pe.server.imexport.provider.DefaultDataProvider;
import com.mindbox.pe.server.imexport.provider.ExportDataProvider;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.spi.ServiceProviderFactory;

/**
 * Export service that provides export functionality.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ExportService {

	private static ExportService instance = null;

	public static ExportService getInstance() {
		if (instance == null) {
			instance = new ExportService();
		}
		return instance;
	}

	private class ExportWriter extends XMLWriter {

		private final ExportDataProvider dataProvider;

		private int gridTag = 0;

		public ExportWriter(Writer writer, ExportDataProvider dataProvider) {
			super(writer, 2);
			this.dataProvider = dataProvider;
			this.writeln("<?xml version=\"1.0\"?>");
		}

		private void writeNextID(String type, int nextID, int cache) throws ExportException {
			writeOpenCloseTag("next-id", new String[] { "type", "seed", "cache" }, new Object[] { type, nextID, cache });
		}

		void exportNextIDSeeds() throws ExportException {
			writeOpenTag("next-id-data");
			try {
				writeNextID(DBIdGenerator.FILTER_ID, DBIdGenerator.getInstance().nextFilterID(), 2);
				writeNextID(DBIdGenerator.GRID_ID, DBIdGenerator.getInstance().nextGridID(), 20);
				writeNextID(DBIdGenerator.SEQUENTIAL_ID, DBIdGenerator.getInstance().nextSequentialID(), 10);
				writeNextID(DBIdGenerator.AUDIT_ID, DBIdGenerator.getInstance().nextAuditID(), 10);
				writeNextID(DBIdGenerator.RULE_ID, DBIdGenerator.getInstance().nextRuleID(), 20);
			}
			catch (SapphireException e) {
				logger.error("Failed to write next-id", e);
				throw new ExportException("Failed to write next id: " + e);
			}
			writeCloseTag();
		}

		void exportCBRData() throws ExportException {
			CBRManager cbrManager = CBRManager.getInstance();
			// Write CBRCaseBase
			for (CBRCaseBase caseBase : cbrManager.getCBRCaseBases()) {
				writeCBRCaseBase(caseBase);
			}
			// Write CBRCase
			for (CBRCase cbrCase : cbrManager.getCBRCases()) {
				writeCBRCase(cbrCase);
			}
			// Write CBRAttribute
			for (CBRAttribute attribute : cbrManager.getCBRAttributes()) {
				writeCBRAttribute(attribute);
			}
		}

		private void writeCBRCaseBase(CBRCaseBase caseBase) throws ExportException {
			writeOpenTag("cbr-case-base", new String[] { "id", "name" }, new Object[] { caseBase.getId(), caseBase.getName() });
			writeActivationDates(caseBase.getEffectiveDate(), caseBase.getExpirationDate());
			writeOpenCloseTag("case-class", "id", String.valueOf(caseBase.getCaseClass().getId()));
			writeBodyTag("description", caseBase.getDescription());
			writeBodyTag("index-file", caseBase.getIndexFile());
			writeBodyTag("match-threshold", caseBase.getMatchThreshold());
			writeBodyTag("maximum-matches", caseBase.getMaximumMatches());
			writeBodyTag("naming-attribute", caseBase.getNamingAttribute());
			writeOpenCloseTag("scoring-function", "id", String.valueOf(caseBase.getScoringFunction().getId()));
			writeCloseTag();
		}

		private void writeCBRCase(CBRCase cbrCase) throws ExportException {
			writeOpenTag("cbr-case", new String[] { "id", "name" }, new Object[] { cbrCase.getId(), cbrCase.getName() });
			writeActivationDates(cbrCase.getEffectiveDate(), cbrCase.getExpirationDate());
			// CBR attribute values
			writeOpenTag("attribute-values");
			for (CBRAttributeValue attributeValue : cbrCase.getAttributeValues()) {
				writeOpenTag("attribute-value", new String[] { "id", "value" }, new Object[] {
						attributeValue.getId(),
						attributeValue.getName() });
				writeOpenCloseTag("attribute", "id", attributeValue.getAttribute().getId());
				writeBodyTag("description", attributeValue.getDescription());
				writeBodyTag("match-contribution", attributeValue.getMatchContribution());
				writeBodyTag("mismatch-penalty", attributeValue.getMismatchPenalty());
				writeCloseTag();
			}
			writeCloseTag();
			// CBR case actions
			writeOpenTag("case-actions");
			for (CBRCaseAction action : cbrCase.getCaseActions()) {
				writeOpenCloseTag("case-action", "id", action.getId());
			}
			writeCloseTag();
			writeOpenCloseTag("case-base", "id", cbrCase.getCaseBase().getId());
			writeBodyTag("description", cbrCase.getDescription());
			writeCloseTag();
		}

		private void writeCBRAttribute(CBRAttribute attribute) throws ExportException {
			writeOpenTag("cbr-attribute", new String[] { "id", "name" }, new Object[] { attribute.getId(), attribute.getName() });
			writeBodyTag("absence-penalty", attribute.getAbsencePenalty());
			writeOpenCloseTag("attribute-type", "id", attribute.getAttributeType().getId());
			writeOpenCloseTag("case-base", "id", attribute.getCaseBase().getId());
			writeBodyTag("description", attribute.getDescription());
			writeOpenTag("enum-values");
			for (CBREnumeratedValue enumeratedValue : attribute.getEnumeratedValues()) {
				writeOpenCloseTag("enum-value", new String[] { "id", "name" }, new Object[] {
						enumeratedValue.getId(),
						enumeratedValue.getName() });
			}
			writeCloseTag();
			writeBodyTag("highest-value", attribute.getHighestValue());
			writeBodyTag("lowest-value", attribute.getLowestValue());
			writeBodyTag("match-contribution", attribute.getMatchContribution());
			writeBodyTag("match-interval", attribute.getMatchInterval());
			writeBodyTag("mismatch-penalty", attribute.getMismatchPenalty());
			writeOpenCloseTag("value-range", "id", attribute.getValueRange().getId());
			writeCloseTag();
		}

		void exportSecurity() throws ExportException {
			writeOpenTag("security-data");

			// write privileges
			writeOpenTag("privileges");
			Privilege[] privileges = dataProvider.getAllPrivileges();
			Arrays.sort(privileges, new IDObjectComparator<Privilege>());
			for (int i = 0; i < privileges.length; i++) {
				writeOpenCloseTag("privilege", new String[] { "id", "name", "displayName", "privilegeType" }, new Object[] {
						String.valueOf(privileges[i].getID()),
						privileges[i].getName(),
						privileges[i].getDisplayString(),
						String.valueOf(privileges[i].getPrivilegeType()) });
			}
			writeCloseTag();// closing <privileges>

			// write rules
			writeOpenTag("roles");
			Role[] roles = dataProvider.getAllRoles();
			Arrays.sort(roles, new IDObjectComparator<Role>());
			for (int i = 0; i < roles.length; i++) {
				Role role = roles[i];
				writeOpenTag("role", new String[] { "id", "name" }, new Object[] { String.valueOf(role.getID()), role.getName() });
				Privilege[] privilegesForRole = (Privilege[]) role.getPrivileges().toArray(new Privilege[0]);
				Arrays.sort(privilegesForRole, new IDObjectComparator<Privilege>());
				for (int j = 0; j < privilegesForRole.length; j++) {
					if (privilegesForRole[j] != null) {
						writeBodyTag("privilege-link", String.valueOf(privilegesForRole[j].getID()));
					}
				}
				writeCloseTag();// closing <privilege-link>
			}
			writeCloseTag();// closing <roles>


			if (ServiceProviderFactory.getUserManagementProvider() instanceof LDAPUserManagementProvider) {
				// Do nothing...
			}
			else {// write users
				writeOpenTag("users");
				User[] users = dataProvider.getAllUsers();
				for (int ui = 0; ui < users.length; ui++) {
					User user = users[ui];
					writeOpenTag(
							"user",
							new String[] { "id", "name", "status", "passwordChangeRequired", "failedLoginCounter" },
							new Object[] {
									user.getUserID(),
									user.getName(),
									(user.getStatus() == null ? "active" : user.getStatus()),
									new Boolean(user.getPasswordChangeRequired()),
									new Integer(user.getFailedLoginCounter()) });

					//<user-password> tag
					for (Iterator<UserPassword> iterator = user.getPasswordHistory().iterator(); iterator.hasNext();) {
						UserPassword up = iterator.next();
						writeOpenCloseTag("user-password", new String[] { "encryptedPassword", "passwordChangeDate" }, new Object[] {
								up.getPassword(),
								ConfigUtil.toDateXMLString(up.getPasswordChangeDate()) });
					}

					// <role-link> tag
					for (Iterator<Role> iterator = user.getRoles().iterator(); iterator.hasNext();) {
						Role role = iterator.next();
						if (role != null) {
							writeBodyTag("role-link", String.valueOf(role.getID()));
						}
					}
					writeCloseTag();// closing <user>
				}
				writeCloseTag();// closing <users>
			}

			writeCloseTag();// closing <security-data>
		}

		private void writeGuidelines(GridTemplate template, GuidelineReportFilter filter) throws ExportException {
			int templateID = template.getID();
			List<ProductGrid> gridList = dataProvider.getGuidelineGrids(templateID);
			if (gridList != null) {
				for (Iterator<ProductGrid> iter = gridList.iterator(); iter.hasNext();) {
					ProductGrid element = iter.next();
					if (filter.isAcceptable(element)) {
						if (element != null) {
							writeOpenTag("grid", new String[] { "type", "templateID", "gridTag" }, new Object[] {
									Grid.GRID_TYPE_GUIDELINE,
									String.valueOf(templateID),
									String.valueOf(++(this.gridTag)) });

							// write generic entity context
							writeContextAsEntityLinks(element);

							writeOpenTag("column-names");
							for (int col = 1; col <= template.getNumColumns(); col++) {
								AbstractTemplateColumn column = template.getColumn(col);
								writeBodyTag("column", column.getTitle());
							}
							writeCloseTag();
							writeActivation(element, template.getNumColumns());
							writeCloseTag();
						}
					}
				}
			}
		}

		private void writeContextAsEntityLinks(AbstractGrid<?> grid) throws ExportException {
			writeOpenTag("context");
			if (grid.hasAnyGenericEntityContext()) {
				GenericEntityType[] types = grid.getGenericEntityTypesInUse();
				for (int i = 0; i < types.length; i++) {
					int[] ids = grid.getGenericEntityIDs(types[i]);
					for (int j = 0; j < ids.length; j++) {
						writeEntityLink(types[i].toString(), ids[j]);
					}
				}
			}
			if (grid.hasAnyGenericCategoryContext()) {
				GenericEntityType[] types = grid.getGenericCategoryEntityTypesInUse();
				for (int i = 0; i < types.length; i++) {
					int[] ids = grid.getGenericCategoryIDs(types[i]);
					for (int j = 0; j < ids.length; j++) {
						writeEntityLink("generic-category:" + types[i].toString(), ids[j]);
					}
				}
			}
			writeCloseTag();
		}

		void exportGuidelineTemplates(List<TemplateUsageType> usageTypes, List<Integer> templateIDs) throws ExportException {
			if (usageTypes.isEmpty() && templateIDs.isEmpty()) {
				List<GridTemplate> list = dataProvider.getAllGuidelineTemplates();
				Collections.sort(list, new IDObjectComparator<GridTemplate>());
				if (list != null) {
					for (Iterator<GridTemplate> iter = list.iterator(); iter.hasNext();) {
						GridTemplate template = iter.next();
						writeTemplate(template);
					}
				}
			}
			else {
				if (!templateIDs.isEmpty()) {
					for (int templateID : templateIDs) {
						GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
						if (template == null) {
							throw new ExportException("No template of id " + templateID + " found");
						}
						writeTemplate(template);
					}
				}
				if (!usageTypes.isEmpty()) {
					for (TemplateUsageType usageType : usageTypes) {
						List<GridTemplate> templateList = GuidelineTemplateManager.getInstance().getTemplates(usageType);
						for (GridTemplate template : templateList) {
							writeTemplate(template);
						}
					}
				}
			}
		}

		private void writeTemplate(GridTemplate template) throws ExportException {
			writeOpenTag("guideline-template", new String[] {
					"id",
					"name",
					"version",
					"parentID",
					"fitToScreen",
					"maxRows",
					"status",
					"usage" }, new Object[] {
					String.valueOf(template.getID()),
					template.getName(),
					template.getVersion(),
					String.valueOf(template.getParentTemplateID()),
					String.valueOf(template.fitToScreen()),
					String.valueOf(template.getMaxNumOfRows()),
					template.getStatus(),
					template.getUsageType().toString() });
			writeOpenTag("comment");
			writelnXMLified(template.getComment());
			writeCloseTag();
			writeBodyTag("description", template.getDescription());
			writeBodyTag("complete-cols", UtilBase.toString(template.getCompletenessColumns()), true);
			writeBodyTag("consistent-cols", UtilBase.toString(template.getConsistencyColumns()), true);
			// write columns
			writeOpenTag("columns");
			for (int i = 1; i <= template.getNumColumns(); ++i) {
				AbstractTemplateColumn column = template.getColumn(i);
				if (column == null) {
					throw new ExportException("Template " + template.getName() + " v." + template.getVersion() + " (" + template.getID()
							+ ") does not have column number " + i);
				}
				writeTemplateColumn((GridTemplateColumn) column);
			}
			writeCloseTag();

			// write rules
			writeOpenTag("rules");
			writeTemplateRule(-1, template.getUsageType(), template);
			for (int i = 1; i <= template.getNumColumns(); ++i) {
				writeTemplateRule(i, (template.getColumn(i).getUsageType() == null
						? template.getUsageType()
						: template.getColumn(i).getUsageType()), (GridTemplateColumn) template.getColumn(i));
			}
			writeCloseTag();

			writeCloseTag(); // guideline-template
		}

		private void writeTemplateRule(int columnNo, TemplateUsageType usage, RuleMessageContainer rmContainer) throws ExportException {
			if (rmContainer.getRuleDefinition() != null && !rmContainer.getRuleDefinition().isEmpty()) {
				RuleDefinition ruleDefinition = rmContainer.getRuleDefinition();
				writeOpenTag("rule", new String[] { "id", "name", "usage" }, new Object[] {
						String.valueOf(ruleDefinition.getID()),
						ruleDefinition.getName(),
						usage.toString() }, true);
				writeBodyTag("description", ruleDefinition.getDescription(), true);
				if (columnNo > 0) {
					writeOpenCloseTag("precondition", new String[] { "columnID" }, new Object[] { String.valueOf(columnNo) });
				}

				// write rule definition
				writeOpenTag("definition");
				writeCDATA(RuleDefinitionUtil.toString(ruleDefinition));
				writeCloseTag(); // definition

				List<TemplateMessageDigest> messageList = rmContainer.getAllMessageDigest();
				if (messageList != null && !messageList.isEmpty()) {
					writeOpenTag("messages");
					for (Iterator<TemplateMessageDigest> iter = messageList.iterator(); iter.hasNext();) {
						TemplateMessageDigest element = iter.next();
						writeOpenTag(
								"message",
								new String[] { "entityID", "conditionalDelimiter", "conditionalFinalDelimiter" },
								new Object[] {
										String.valueOf(element.getEntityID()),
										element.getConditionalDelimiter(),
										element.getConditionalFinalDelimiter() },
								true);
						writeBodyTag("message-text", element.getText());
						writeCloseTag();
					}
					writeCloseTag(); // messages
				}
				writeCloseTag(); // rule
			}
		}

		private void writeTemplateColumn(GridTemplateColumn column) throws ExportException {
			writeOpenTag("column", new String[] { "id", "name", "title" }, new Object[] {
					String.valueOf(column.getID()),
					column.getName(),
					column.getTitle() });
			writeBodyTag("description", column.getDescription());
			writeBodyTag("color", column.getColor(), true);
			writeBodyTag("font", column.getFont(), true);
			writeBodyTag("width", String.valueOf(column.getColumnWidth()), true);
			writeOpenTag(
					"dataspec",
					new String[] { "type", "allowBlank", "multipleSelect", "showLhsAttribute", "sortEnumValue" },
					new Object[] {
							column.getColumnDataSpecDigest().getType(),
							(column.getColumnDataSpecDigest().isBlankAllowed() ? Constants.VALUE_YES : Constants.VALUE_NO),
							(column.getColumnDataSpecDigest().isMultiSelectAllowed() ? Constants.VALUE_YES : Constants.VALUE_NO),
							(column.getColumnDataSpecDigest().isLHSAttributeVisible() ? Constants.VALUE_YES : Constants.VALUE_NO),
							(column.getColumnDataSpecDigest().isEnumValueNeedSorted() ? Constants.VALUE_YES : Constants.VALUE_NO) },
					true);
			writeBodyTag("max-value", column.getColumnDataSpecDigest().getMaxValue(), true);
			writeBodyTag("min-value", column.getColumnDataSpecDigest().getMinValue(), true);
			// TT 1879 write empty precision, if no precision is set
			writeBodyTag("precision", (column.getColumnDataSpecDigest().isPrecisionSet()
					? String.valueOf(column.getColumnDataSpecDigest().getPrecision())
					: ""), true);
			// next three rules added for entity column support
			if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
				writeBodyTag("allow-category", String.valueOf(column.getColumnDataSpecDigest().getAllowCategory()), true);
				writeBodyTag("allow-entity", String.valueOf(column.getColumnDataSpecDigest().getAllowEntity()), true);
				writeBodyTag("entity-type", String.valueOf(column.getColumnDataSpecDigest().getEntityType()), true);
			}
			else if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
				writeBodyTag("enum-type", column.getColumnDataSpecDigest().getEnumSourceType().toString());
				if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.COLUMN) {
					if (column.getColumnDataSpecDigest().hasEnumValue()) {
						for (Iterator<String> i = column.getColumnDataSpecDigest().getAllColumnEnumValues().iterator(); i.hasNext();) {
							writeBodyTag("enum-value", i.next(), true);
						}
					}
				}
				else if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.DOMAIN_ATTRIBUTE) {
					writeBodyTag("enum-attribute", column.getColumnDataSpecDigest().getMappedAttribute());
				}
				else if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.EXTERNAL) {
					writeBodyTag("enum-source-name", column.getColumnDataSpecDigest().getEnumSourceName());
					if (!UtilBase.isEmpty(column.getColumnDataSpecDigest().getEnumSelectorColumnName())) {
						writeBodyTag("enum-selector-column", column.getColumnDataSpecDigest().getEnumSelectorColumnName());
					}
				}
			}

			List<ColumnAttributeItemDigest> attributeItemList = column.getColumnDataSpecDigest().getAllAttributeItems();
			if (attributeItemList != null && !attributeItemList.isEmpty()) {
				for (Iterator<ColumnAttributeItemDigest> iter = attributeItemList.iterator(); iter.hasNext();) {
					ColumnAttributeItemDigest element = iter.next();
					writeOpenCloseTag("attribute-item", new String[] { "name", "displayValue" }, new Object[] {
							element.getName(),
							element.getDisplayValue() });
				}
			}
			writeCloseTag(); // close dataspec element
			if (column.hasMessageFragmentDigest()) {
				writeOpenTag("column-messages");
				List<ColumnMessageFragmentDigest> columnMessageList = column.getAllMessageFragmentDigests();
				for (Iterator<ColumnMessageFragmentDigest> iter = columnMessageList.iterator(); iter.hasNext();) {
					ColumnMessageFragmentDigest element = iter.next();
					writeOpenTag("column-message", new String[] {
							"type",
							"cellSelection",
							"enumDelimiter",
							"enumFinalDelimiter",
							"enumPrefix",
							"rangeStyle" }, new Object[] {
							element.getType(),
							element.getCellSelection(),
							element.getEnumDelimiter(),
							element.getEnumFinalDelimiter(),
							element.getEnumPrefix(),
							element.getRangeStyle() }, true);
					writeBodyTag("message-text", element.getText());
					writeCloseTag();
				}
				writeCloseTag();
			}
			writeCloseTag(); // column
		}

		void exportGuidelineActions() throws ExportException {
			List<ActionTypeDefinition> list = GuidelineFunctionManager.getInstance().getAllActionTypes();
			Collections.sort(list, new IDObjectComparator<ActionTypeDefinition>());
			for (Iterator<ActionTypeDefinition> iter = list.iterator(); iter.hasNext();) {
				ActionTypeDefinition element = iter.next();
				writeGuidelineAction(element);
			}
		}

		/**
		 * This is replacing exportDateSynonyms() of 4.5.x
		 * @throws ExportException
		 * @since 5.0.0
		 */
		void exportDateData() throws ExportException {
			Collection<DateSynonym> dateSynonyms = DateSynonymManager.getInstance().getAllDateSynonyms();
			List<DateSynonym> list = new ArrayList<DateSynonym>(dateSynonyms);
			Collections.sort(list, new IDObjectComparator<DateSynonym>());
			for (Iterator<DateSynonym> i = list.iterator(); i.hasNext();) {
				DateSynonym ds = i.next();
				writeOpenCloseTag("DateElement", new String[] { "id", "name", "date", "description" }, new Object[] {
						String.valueOf(ds.getID()),
						ds.getName(),
						ConfigUtil.toDateXMLString(ds.getDate()),
						ds.getDescription() });
				//writeCloseTag();
			}
		}

		private void writeGuidelineAction(ActionTypeDefinition actionType) throws ExportException {
			writeOpenTag("guideline-action", new String[] { "id", "name" }, new Object[] {
					String.valueOf(actionType.getID()),
					actionType.getName() });
			writeBodyTag("description", actionType.getDescription());
			writeOpenTag("deployment-rule");
			writelnXMLified(actionType.getDeploymentRule());
			writeCloseTag();
			TemplateUsageType[] usages = actionType.getUsageTypes();
			if (usages != null) {
				for (int i = 0; i < usages.length; i++) {
					writeBodyTag("usage", usages[i].toString());
				}
			}
			writeParametersIfAny(actionType);
			writeCloseTag();
		}

		private void writeParametersIfAny(FunctionTypeDefinition typeDef) throws ExportException {
			if (typeDef.hasParameter()) {
				writeOpenTag("parameters");
				FunctionParameterDefinition[] paramDefs = typeDef.getParameterDefinitions();
				for (int i = 0; i < paramDefs.length; i++) {
					writeOpenTag("parameter", new String[] { "id", "name", "deployType" }, new Object[] {
							String.valueOf(paramDefs[i].getID()),
							paramDefs[i].getName(),
							paramDefs[i].getDeployType() });
					writeBodyTag("data-string", paramDefs[i].getParamDataString(), true);
					writeCloseTag();
				}
				writeCloseTag();
			}
		}

		void exportTestConditions() throws ExportException {
			List<TestTypeDefinition> list = GuidelineFunctionManager.getInstance().getAllTestTypes();
			Collections.sort(list, new IDObjectComparator<TestTypeDefinition>());
			for (Iterator<TestTypeDefinition> iter = list.iterator(); iter.hasNext();) {
				TestTypeDefinition element = iter.next();
				writeTestCondition(element);
			}
		}

		private void writeTestCondition(TestTypeDefinition testType) throws ExportException {
			writeOpenTag("test-condition", new String[] { "id", "name" }, new Object[] {
					String.valueOf(testType.getID()),
					testType.getName() });
			writeBodyTag("description", testType.getDescription());
			writeOpenTag("deployment-rule");
			writelnXMLified(testType.getDeploymentRule());
			writeCloseTag();
			writeParametersIfAny(testType);
			writeCloseTag();
		}

		void exportGuidelines(GuidelineReportFilter filter) throws ExportException {
			logger.debug(">>> exportGuidelines: " + filter);
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
				logger.debug("... exportGuidelines: " + gridTemplateList.size());
				for (Iterator<GridTemplate> iter = gridTemplateList.iterator(); iter.hasNext();) {
					GridTemplate element = iter.next();
					writeGuidelines(element, filter);
				}
			}
		}

		void exportParameters(GuidelineReportFilter filter) throws ExportException {
			logger.debug(">>> exportParameters: " + filter);
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
			if (paramTemplateList != null) {
				for (Iterator<ParameterTemplate> tempIter = paramTemplateList.iterator(); tempIter.hasNext();) {
					ParameterTemplate template = tempIter.next();
					int templateID = template.getID();
					for (Iterator<ParameterGrid> iter = dataProvider.getParameterGrids(templateID).iterator(); iter.hasNext();) {
						ParameterGrid element = iter.next();
						if (filter.isAcceptable(element)) {
							try {
								writeOpenTag("grid", new String[] { "type", "templateID", "gridTag" }, new Object[] {
										Grid.GRID_TYPE_PARAMETER,
										String.valueOf(templateID),
										String.valueOf(++(this.gridTag)) });

								writeContextAsEntityLinks(element);

								writeOpenTag("column-names");
								for (int col = 1; col <= template.getNumColumns(); col++) {
									AbstractTemplateColumn column = template.getColumn(col);
									writeBodyTag("column", column.getTitle());
								}
								writeCloseTag();
								// write activations
								writeActivation(element, template.getColumnCount());
								writeCloseTag();
							}
							catch (Exception e) {
								logger.error("Failed to write activations for grid " + element, e);
								writeln("<!-- ERROR - failed to write activations for grid " + element + ": " + e.getMessage() + " -->");
							}
						}
					}
				}
			}
		}

		private final boolean isRowEmpty(AbstractGrid<?> grid, int row, int columnCount) throws ExportException {
			String value = null;
			for (int col = 1; col <= columnCount; col++) {
				value = dataProvider.getCellValue(grid, row, col, null);
				if (value != null) {
					return false;
				}
			}
			return true;
		}

		private void writeActivation(ParameterGrid element, int columnCount) throws ExportException {
			writeOpenTag("activation", new String[] { "id", "status", "parentID", "statusChangedOn" }, new Object[] {
					String.valueOf(element.getID()),
					element.getStatus(),
					"-1",
					ConfigUtil.toDateXMLString(element.getStatusChangeDate()), });

			writeActivationDates(element.getEffectiveDate(), element.getExpirationDate());
			// write grid cell values
			if (element.getCellValues() != null && element.getCellValues().length() > 0) {
				writeOpenTag("grid-values");
				for (int i = 1; i <= element.getNumRows(); i++) {
					writeOpenTag("row");
					if (!isRowEmpty(element, i, columnCount)) {
						for (int j = 1; j <= columnCount; j++) {
							writeBodyTag("cell-value", dataProvider.getCellValue(element, i, j, ""));
						}
					}
					writeCloseTag();
				}
				writeCloseTag();
			}
			writeCloseTag();
		}

		private void writeActivation(ProductGrid grid, int columnCount) throws ExportException {
			writeOpenTag("activation", new String[] { "id", "status", "parentID", "statusChangedOn", "createdOn" }, new Object[] {
					String.valueOf(grid.getID()),
					grid.getStatus(),
					String.valueOf(grid.getCloneOf()),
					ConfigUtil.toDateXMLString(grid.getStatusChangeDate()),
					ConfigUtil.toDateXMLString(grid.getCreationDate()) });

			writeActivationDates(grid.getEffectiveDate(), grid.getExpirationDate());
			// write grid cell values
			if (!grid.isEmpty()) {
				writeOpenTag("grid-values");
				for (int i = 1; i <= grid.getNumRows(); i++) {
					writeOpenTag("row");
					if (!isRowEmpty(grid, i, columnCount)) {
						for (int j = 1; j <= columnCount; j++) {
							String cellValue = dataProvider.getCellValue(grid, i, j, "");
							writeBodyTag("cell-value", cellValue);
						}
					}
					writeCloseTag();
				}
				writeCloseTag();
			}
			writeBodyTag("comment", grid.getComments());
			writeCloseTag();
		}

		/**
		 * This export of entities excludes entities that expired within the days specified
		 * @since PowerEditor 5.0.0
		 * @param useDaysAgo
		 * @param daysAgo
		 * @throws ExportException
		 */
		void exportEntities(boolean useDaysAgo, int daysAgo) throws ExportException {
			logger.debug(">>> exportEntities: useDaysAgo=" + useDaysAgo + ",daysAgo=" + daysAgo);
			writeOpenTag("entity-data");
			writeGenericCategories();
			writeGenericEntities();
			writeCloseTag();
		}

		/**
		 * @since PowerEditor 4.5.0
		 */
		private void writeGenericCategories() {
			GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
			for (int i = 0; i < types.length; i++) {
				writeComment("Generic Categories for " + types[i]);
				CategoryTypeDefinition categoryTypeDef = ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(
						types[i]);
				if (categoryTypeDef != null) {
					for (Iterator<GenericCategory> iter = EntityManager.getInstance().getAllCategories(categoryTypeDef.getTypeID()).iterator(); iter.hasNext();) {
						GenericCategory cat = iter.next();
						writeGenericCategoryTag(cat, types[i].toString());
					}
				}
			}
		}

		/** @since 5.0.0 */
		private void writeGenericCategoryTag(GenericCategory category, String entityType) {
			writeOpenTag("category", new String[] { "id", "type" }, new Object[] { new Integer(category.getID()), entityType });

			writePropertyTag("name", category.getName());

			// [Rich] Writing a parent tag for root categories is temporary during 5.1 alpha development and will be removed for 5.1 final.
			// To do: remove the next 6 lines (down to and including the "else") and the closing brace of the if-else.
			// Leaving only the "for (...parentKeyIterator...) {...}", and the final writeCloseTag().
			if (category.isRoot()) {
				writeOpenTag("parent");
				writeBodyTag("parentID", "-1");
				writeActivationDates(DateSynonymManager.getInstance().getEarliestDateSynonym(), null);
				writeCloseTag();
			}
			else {
				for (Iterator<MutableTimedAssociationKey> parentIter = category.getParentKeyIterator(); parentIter.hasNext();) {
					writeParentKey(parentIter.next());
				}
			}

			writeCloseTag();
		}

		/**
		 * @since 3.0.0
		 */
		private void writeGenericEntities() {
			GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
			for (int i = 0; i < types.length; i++) {
				String typeName = types[i].toString();
				for (Iterator<GenericEntity> iter = EntityManager.getInstance().getAllEntities(types[i]).iterator(); iter.hasNext();) {
					GenericEntity entity = iter.next();
					writeOpenTag("entity", new String[] { "id", "type", "parentID" }, new Object[] {
							String.valueOf(entity.getID()),
							typeName,
							String.valueOf(entity.getParentID()) });
					writePropertyTag("name", entity.getName());

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
						writePropertyTag(props[j], valueStr);
					}

					// write linked generic categories
					for (Iterator<MutableTimedAssociationKey> parentIter = entity.getCategoryIterator(); parentIter.hasNext();) {
						writeAssociationKey("category", parentIter.next());
					}

					// write compatibility data
					writeCompatibility(entity.getID(), types[i]);
					writeCloseTag();
				}
			}
		}

		private void writeCompatibility(int entityID, GenericEntityType type) {
			List<GenericEntityAssociationKey> asscKeyList = EntityManager.getInstance().getCompatibilities(entityID, type);
			for (Iterator<GenericEntityAssociationKey> iterator = asscKeyList.iterator(); iterator.hasNext();) {
				writeAssociationKey(iterator.next());
			}
		}

		private void writePropertyTag(String property, String value) {
			writeOpenCloseTag("property", new String[] { "name", "value" }, new Object[] { property, value });
		}

		private void writeEntityLink(String type, int id) {
			writeOpenCloseTag("entity-link", new String[] { "id", "type" }, new Object[] { String.valueOf(id), type });
		}

		private void writeParentKey(TimedAssociationKey asscKey) {
			writeOpenTag("parent");
			writeBodyTag("parentID", String.valueOf(asscKey.getAssociableID()));
			writeActivationDates(asscKey.getEffectiveDate(), asscKey.getExpirationDate());
			writeCloseTag();
		}

		private void writeAssociationKey(String type, TimedAssociationKey asscKey) {
			writeOpenTag("association");
			writeEntityLink(type, asscKey.getAssociableID());
			writeActivationDates(asscKey.getEffectiveDate(), asscKey.getExpirationDate());
			writeCloseTag();
		}

		private void writeAssociationKey(GenericEntityAssociationKey asscKey) {
			writeAssociationKey(asscKey.getGenericEntityType().toString(), asscKey);
		}

		private void writeActivationDates(DateSynonym effDate, DateSynonym expDate) {
			if (effDate == null && expDate == null) {
				return;
			}

			List<String> attrNames = new ArrayList<String>(2);
			List<Integer> attrValues = new ArrayList<Integer>(2);
			int count = 0;
			if (effDate != null) {
				attrNames.add("effectiveDateID");
				attrValues.add(new Integer(effDate.getId()));
				count++;
			}
			if (expDate != null) {
				attrNames.add("expirationDateID");
				attrValues.add(new Integer(expDate.getId()));
				count++;
			}

			writeOpenCloseTag("activation-dates", attrNames.toArray(new String[count]), attrValues.toArray());
		}

		/**
		 * @throws ExportException
		 * @since 5.0.0
		 */
		void exportMetaData(String userID) throws ExportException {
			Package serverPackage = Package.getPackage("com.mindbox.pe.server");
			writeOpenTag("meta-data");
			writeOpenCloseTag("pe-data", new String[] { "PowerEditor-version", "PowerEditor-build", "date-exported" }, new Object[] {
					serverPackage.getSpecificationVersion(),
					serverPackage.getImplementationVersion(),
					ConfigUtil.toDateXMLString(new Date()) });
			writeOpenCloseTag("user-data", new String[] { "user-name" }, new Object[] { userID });
			writeOpenCloseTag("system-data", new String[] { "java-version", "database" }, new Object[] {
					System.getProperty("java.version"),
					ConfigurationManager.getInstance().getServerConfiguration().getDatabaseConfig().getConnectionStr() });
			writeCloseTag();

		}

	}


	private ExportWriter writer = null;
	private final Logger logger = Logger.getLogger(getClass());
	private final ExportDataProvider exportDataProvider = new DefaultDataProvider();

	private ExportService() {
	}

	public void exportAll(Writer writer, String userID) throws ExportException {
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeEntities(true);
		filter.setIncludeSecurityData(true);
		filter.setIncludeGuidelines(true);
		filter.setIncludeParameters(true);
		filter.setIncludeTemplates(true);
		filter.setIncludeGuidelineActions(true);
		filter.setIncludeTestConditions(true);
		filter.setIncludeDateSynonyms(true);
		export_internal(writer, filter, userID);
	}

	public void export(Writer writer, GuidelineReportFilter filter, String userID) throws ExportException {
		if (filter == null) throw new NullPointerException("filter is null");
		export_internal(writer, filter, userID);
	}

	private synchronized void export_internal(Writer writer, GuidelineReportFilter filter, String userID) throws ExportException {
		logger.debug(">>> export_internal: filter=" + filter);
		try {
			this.writer = new ExportWriter(writer, exportDataProvider);

			this.writer.writeln("<!-- Version: 1.0 - Generated on " + new Date() + " -->");
			this.writer.writeOpenTag("powereditor-data");

			this.writer.exportMetaData(userID);

			// new in 5.0 XSD
			if (filter.isIncludeDateSynonyms()) {
				this.writer.writeOpenTag("date-data");
				this.writer.exportDateData();
				this.writer.writeCloseTag();
			}

			if (filter.isIncludeEntities()) {
				this.writer.exportNextIDSeeds();
				this.writer.exportEntities(filter.useDaysAgo(), filter.getDaysAgo());
			}

			if (filter.isIncludeSecurityData()) {
				this.writer.exportSecurity();
			}

			if (filter.isIncludeGuidelineActions()) {
				this.writer.writeOpenTag("guideline-action-data");
				this.writer.exportGuidelineActions();
				this.writer.writeCloseTag();
			}

			if (filter.isIncludeTestConditions()) {
				this.writer.writeOpenTag("test-condition-data");
				this.writer.exportTestConditions();
				this.writer.writeCloseTag();
			}

			if (filter.isIncludeTemplates()) {
				this.writer.writeOpenTag("template-data");
				this.writer.exportGuidelineTemplates(filter.getUsageTypes(), filter.getGuidelineTemplateIDs());
				this.writer.writeCloseTag();
			}

			if (filter.isIncludeGuidelines() || filter.isIncludeParameters()) {
				filter.setServerFilterHelper(SearchCooridinator.getServerFilterHelper());

				this.writer.writeOpenTag("grid-data");

				if (filter.isIncludeGuidelines()) {
					this.writer.exportGuidelines(filter);
				}
				if (filter.isIncludeParameters()) {
					this.writer.exportParameters(filter);
				}

				this.writer.writeCloseTag();
			}

			if (filter.isIncludeCBR()) {
				this.writer.writeOpenTag("cbr-data");
				this.writer.exportCBRData();
				this.writer.writeCloseTag();
			}

			this.writer.writeCloseTag();
			this.writer.close();
		}
		catch (Exception ex) {
			logger.error("Export failed", ex);
			ex.printStackTrace(System.err);
			throw new ExportException(ex.getMessage());
		}
	}
}