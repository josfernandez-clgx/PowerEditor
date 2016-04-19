package com.mindbox.pe.server.generator;

import java.io.StringReader;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.common.MutableBoolean;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.GridValueContainable;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.CompoundRuleElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.TemplateMessageDigest;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ControlPatternConfigHelper;
import com.mindbox.pe.server.generator.aemodel.AbstractAeValue;
import com.mindbox.pe.server.generator.aemodel.AeCellValue;
import com.mindbox.pe.server.generator.aemodel.AeColumnValue;
import com.mindbox.pe.server.generator.aemodel.AeNameValue;
import com.mindbox.pe.server.generator.aemodel.AeRule;
import com.mindbox.pe.server.generator.processor.MessageProcessor;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.parser.jtb.message.MessageParser;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.server.parser.jtb.rule.ParseException;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRule;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken;

/**
 * Provides helper methods for ARTScript rule generation.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 * @see com.mindbox.pe.server.generator.RuleGenerator
 */
public class RuleGeneratorHelper {

	public static final String NUMERIC_FUNCTION_GREATER = "numeric-greater-than";
	public static final String NUMERIC_FUNCTION_GREATER_EQUAL = "numeric-greater-than-or-equal";
	public static final String NUMERIC_FUNCTION_LESS = "numeric-less-than";

	public static final String NUMERIC_FUNCTION_LESS_EQUAL = "numeric-less-than-or-equal";
	public static final String CREATE_SEQ_FUNCTION = "create$";
	public static final String ENTITY_MATCH_FUNCTION = "pe:entity-match";
	public static final String TIME_SLICE_VARIABLE = "?time-slice";
	/** A*E Equalify function */
	public static final String EQUALIFY_FUNCTION = "eq";
	/** A*E Equalify function for numeric values */
	public static final String EQUALIFY_FUNCTION_NUMERIC = "=";
	/** A*E Inequalify function */
	public static final String INEQUALIFY_FUNCTION = "/=";
	/** A*E True Value */
	public static final String AE_TRUE = "T";

	/** A*E Nil Value */
	public static final String AE_NIL = "nil";

	public static final String ART_FILE_EXTENSION = ".art";

	public static final String QUOTE = "\"";
	public static final String OPEN_PAREN = "(";

	public static final String CLOSE_PAREN = ")";

	private static final String STARTS_WITH_SPRINTF_SUBSTITUTION_REGEXP = "^([as]|\\.\\d+f).*";
	private static final Pattern SPRINTF_SUBSTITUTION_PATTERN = Pattern.compile(STARTS_WITH_SPRINTF_SUBSTITUTION_REGEXP);

	private static final String INVALID_FILENAME_CHARS = "([\\(\\)\\[\\]\\;\\\\/\\:\\*\\?<>]|\\s)";

	private static ThreadLocal<FormatterFactory> FORMATTER_FACTORY_THREAD_LOCAL = new ThreadLocal<FormatterFactory>() {
		@Override
		protected FormatterFactory initialValue() {
			// ignore precision by default
			return new DefaultFormatterFactory(ConfigurationManager.getInstance().getPowerEditorConfiguration().getRuleGeneration().isIgnorePrecision() == null
					|| ConfigurationManager.getInstance().getPowerEditorConfiguration().getRuleGeneration().isIgnorePrecision().booleanValue());
		}
	};

	private static RuleGeneratorHelper instance = null;

	static void addActionParamDefinitionAndRuleParam(FunctionTypeDefinition actionDef, CompoundRuleElement<FunctionParameter> ruleAction, int paramID, GridTemplate template, int columnNo,
			boolean addActionParamDefinition) {
		AbstractTemplateColumn column = template.getColumn(columnNo);
		String paramName = (column == null ? "Param-" + paramID : column.getTitle());
		if (addActionParamDefinition) {
			FunctionParameterDefinition actionParamDef = new FunctionParameterDefinition(paramID, paramName);
			if (actionDef.getID() < 0) {
				if (column == null) {
					actionParamDef.setDeployType(DeployType.STRING);
				}
				else {
					if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
						actionParamDef.setDeployType(DeployType.BOOLEAN);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_CODE)) {
						actionParamDef.setDeployType(DeployType.SYMBOL);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_CURRENCY)) {
						actionParamDef.setDeployType(DeployType.CURRENCY);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE)) {
						actionParamDef.setDeployType(DeployType.CURRENCY);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_DATE)) {
						actionParamDef.setDeployType(DeployType.DATE);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
						actionParamDef.setDeployType(DeployType.SYMBOL);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_FLOAT)) {
						actionParamDef.setDeployType(DeployType.PERCENT);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_FLOAT_RANGE)) {
						actionParamDef.setDeployType(DeployType.PERCENT);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_INTEGER)) {
						actionParamDef.setDeployType(DeployType.INTEGER);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_INTEGER_RANGE)) {
						actionParamDef.setDeployType(DeployType.INTEGER);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_PERCENT)) {
						actionParamDef.setDeployType(DeployType.PERCENT);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_STRING)) {
						actionParamDef.setDeployType(DeployType.STRING);
					}
					else if (column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_SYMBOL)) {
						actionParamDef.setDeployType(DeployType.SYMBOL);
					}
					else {
						actionParamDef.setDeployType(DeployType.STRING);
					}
				}
				actionDef.addParameterDefinition(actionParamDef);
			}
		}
		// set parameter
		ruleAction.add(RuleElementFactory.getInstance().createFunctionParameter(paramID, paramName, RuleElementFactory.getInstance().createColumnReference(columnNo)));
	}

	public static void appendCategoryIntArrayForCommentString(StringBuilder buff, GenericEntityType entityType, int[] ids) {
		buff.append("Category: ");
		GenericCategory category;
		for (int i = 0; i < ids.length; i++) {
			if (i > 0) buff.append(", ");
			category = EntityManager.getInstance().getGenericCategory(entityType.getCategoryType(), ids[i]);
			buff.append((category == null ? "[" + ids[i] + "]" : category.getName()));
		}
	}

	public static void appendEntityIntArrayForCommentString(StringBuilder buff, GenericEntityType entityType, int[] ids) {
		buff.append("Entity: ");
		GenericEntity entity;
		for (int i = 0; i < ids.length; i++) {
			if (i > 0) buff.append(", ");
			entity = EntityManager.getInstance().getEntity(entityType, ids[i]);
			buff.append((entity == null ? "[" + ids[i] + "]" : entity.getName()));
		}
	}

	/**
	 * Appends a quoted string if the specified flag is true.
	 * If <code>asString</code> is <code>false</code> and <code>value</code> is already quoted,
	 * this stripped off the quotes.
	 * @param value
	 * @param asString
	 * @return quoted value if <code>asString</code> is <code>true</code>; unquoted value, otherwise
	 */
	public static void appendFormattedForStringType(StringBuilder buff, String value, boolean asString) {
		if (value == null) {
			buff.append(AE_NIL);
		}
		else if (asString) {
			String valueToAppend;
			if (value.startsWith(RuleGeneratorHelper.QUOTE) && value.endsWith(RuleGeneratorHelper.QUOTE)) {
				valueToAppend = value.substring(1, value.length() - 1).replaceAll("\"", "\\\\\"");
			}
			else {
				valueToAppend = value.replaceAll("\"", "\\\\\"");
			}
			buff.append(RuleGeneratorHelper.QUOTE);
			buff.append(valueToAppend);
			buff.append(RuleGeneratorHelper.QUOTE);
		}
		else {
			buff.append(AeMapper.stripQuotes(value));
		}
	}

	public static void appendIntArrayAsEntityMatchFunctionArg(StringBuilder buff, int[] ids) {
		buff.append("(build$ ");
		for (int i = 0; i < ids.length; i++) {
			if (i > 0) buff.append(" ");
			buff.append(ids[i]);
		}
		buff.append(")");
	}

	public static IRange asIRangeValue(String valueStr, DeployType deployType) throws InvalidDataException {
		// parse the value as range
		IRange rangeObj;
		if (deployType == null) {
			rangeObj = null;
		}
		else if (deployType == DeployType.INTEGER) {
			rangeObj = IntegerRange.parseValue(valueStr);
		}
		else if (deployType == DeployType.CURRENCY || deployType == DeployType.FLOAT || deployType == DeployType.PERCENT || deployType == DeployType.SYMBOL) {
			rangeObj = FloatRange.parseValue(valueStr);
		}
		// TT 1991
		else if (deployType == DeployType.DATE) {
			rangeObj = DateRange.parseValue(valueStr);
		}
		else {
			throw new InvalidDataException("irange", valueStr, "Invalid deploy type of reference for BETWEEN '" + valueStr + "'");
		}
		return rangeObj;
	}

	private static boolean beginsWithSprintfSubstitutionPattern(String s) {
		return SPRINTF_SUBSTITUTION_PATTERN.matcher(s).matches();
	}

	static String emptyString(int length) {
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < length; i++) {
			buff.append(" ");
		}
		return buff.toString();
	}

	public static DomainAttribute findDomainAttributeForContextElement(ControlPatternConfigHelper controlPatternConfig, DomainClass dc, String contextElementType) throws RuleGenerationException {
		DomainAttribute da = dc.getDomainAttribute(controlPatternConfig.findAttributeNameForContextElement(contextElementType));
		if (da == null) {
			throw new RuleGenerationException("Domain attribute of name " + contextElementType + " not found for " + dc);
		}
		return da;
	}

	private static ActionTypeDefinition findMatchingActionType(GridTemplate template, int columnNo, AeRule aeRule) throws ServletActionException {
		String deploymentRule = generateActionTypeDeploymentRule(template, columnNo, aeRule);
		Logger.getLogger(RuleGeneratorHelper.class).debug("    findMatchingActionType: template = " + template);
		Logger.getLogger(RuleGeneratorHelper.class).debug("    findMatchingActionType: deploymentRule = " + deploymentRule);
		return BizActionCoordinator.getInstance().findActionTypeDefinitionWithDeploymentRule(template.getUsageType(), deploymentRule);
	}

	public static String formatDateValueForLHS(Date date) {
		String strValue = toRuleDateString(date);
		if (ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getLhsDateFormat().equalsIgnoreCase("julian")) {
			return strValue;
		}
		else {
			StringBuilder buff = new StringBuilder();
			buff.append(OPEN_PAREN);
			buff.append("read-formatted-date ");
			buff.append(strValue);
			buff.append(" ");
			buff.append(QUOTE);
			buff.append("%m/%d/%Y");
			buff.append(QUOTE);
			buff.append(CLOSE_PAREN);
			return buff.toString();
		}
	}

	/**
	 * Gets the string formatted for category argument of context match function.
	 * @param type
	 * @param groupList list of collections of Integers
	 * @return
	 */
	public static String formatForContextMatchFunctionCategoryArg(List<Collection<Integer>> groupList) {
		StringBuilder buff = new StringBuilder();
		if (!groupList.isEmpty()) {
			buff.append("(build$ ");
			for (Iterator<Collection<Integer>> iter = groupList.iterator(); iter.hasNext();) {
				Collection<Integer> element = iter.next();
				if (!element.isEmpty()) {
					buff.append("(");
					for (Iterator<Integer> iterator2 = element.iterator(); iterator2.hasNext();) {
						Integer id = (Integer) iterator2.next();
						buff.append(id);
						if (iterator2.hasNext()) buff.append(" ");
					}
					buff.append(")");
					if (iter.hasNext()) buff.append(" ");
				}
			}
			buff.append(")");
		}
		return buff.toString();
	}

	/**
	 * Formats the specified value for excluded object.
	 * 
	 * @return formatted for excluded object, first character of which is &amp;
	 */
	public static String formatForExcludedObject(String value) {
		if (value == null) return "";
		String[] strs = value.split("\\&");
		StringBuilder buff = new StringBuilder();
		boolean isFirst = true;
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].trim().length() > 0) {
				if (!isFirst) buff.append(' ');
				buff.append("& ~?");
				buff.append(strs[i].trim());
				if (isFirst) isFirst = false;
			}
		}
		return buff.toString();
	}

	/**
	 * @param unformattedString
	 * @return modified String which is compliant with ARTScript
	 */
	public static String formatForSprintf(String unformattedString) {
		StringBuilder buff = new StringBuilder(unformattedString.length() * 2);
		char[] cs = unformattedString.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			if (i == 0) {
				buff.append(cs[i]);
			}
			else {
				if (cs[i] == '"' && cs[i - 1] != '\\') {
					buff.append('\\');
				}
				else if (cs[i] == '\\' && i < cs.length - 1 && cs[i + 1] != '"') {
					buff.append('\\');
				}
				else if (cs[i] == '%') {
					if (i == cs.length - 1 || !beginsWithSprintfSubstitutionPattern(unformattedString.substring(i + 1))) {
						buff.append('%');
					}
				}
				buff.append(cs[i]);
			}
		}
		return buff.toString();
	}

	/**
	 * Makes sure the returned string is quoted if the specified flag is true, and vice versa.
	 * If <code>asString</code> is <code>false</code> and <code>value</code> is already quoted,
	 * this stripped off the quotes.
	 * @param value
	 * @param asString
	 * @return quoted value if <code>asString</code> is <code>true</code>; unquoted value, otherwise
	 */
	public static String formatForStringType(String value, boolean asString) {
		StringBuilder buff = new StringBuilder();
		appendFormattedForStringType(buff, value, asString);
		return buff.toString();
	}

	/**
	 * This does not generate action parameter definitions. It simply emulate deploy rule from the
	 * specified arguments. Equivalent to
	 * <code>generateActionTypeDeploymentRule(template, columnNo, aeRule, false, null, null)</code>.
	 * 
	 * @param template
	 * @param columnNo
	 * @param aeRule
	 * @return
	 */
	private static String generateActionTypeDeploymentRule(GridTemplate template, int columnNo, AeRule aeRule) {
		return generateActionTypeDeploymentRule(template, columnNo, aeRule, false, false, null, null);
	}

	/**
	 * 
	 * @param template
	 * @param columnNo
	 * @param aeRule
	 * @param generateParamDefinitions
	 * @param actionDef
	 * @param ruleAction
	 * @return
	 */
	private static String generateActionTypeDeploymentRule(GridTemplate template, int columnNo, AeRule aeRule, boolean generateParamDefinitions, boolean generateRuleParams,
			ActionTypeDefinition actionDef, RuleAction ruleAction) {
		StringBuilder buff = new StringBuilder();
		buff.append(aeRule.getActionMethod());
		buff.append("(");
		int paramID = 1;
		for (Iterator<AbstractAeValue> iter = aeRule.getActionParms().iterator(); iter.hasNext();) {
			AbstractAeValue element = iter.next();
			if (element instanceof AeColumnValue) {
				if (generateRuleParams) addActionParamDefinitionAndRuleParam(actionDef, ruleAction, paramID, template, ((AeColumnValue) element).getColumnNumber(), generateParamDefinitions);
				buff.append("\"%parameter " + paramID + "%\"");
				++paramID;
			}
			else if (element instanceof AeCellValue) {
				if (generateRuleParams) addActionParamDefinitionAndRuleParam(actionDef, ruleAction, paramID, template, columnNo, generateParamDefinitions);
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
					if (generateRuleParams) addActionParamDefinitionAndRuleParam(actionDef, ruleAction, paramID, template, col, generateParamDefinitions);
					buff.append("\"%parameter " + paramID + "%\"");
					++paramID;
				}
				else if (token.equals("\"%cellValue%\"")) {
					if (generateRuleParams) addActionParamDefinitionAndRuleParam(actionDef, ruleAction, paramID, template, columnNo, generateParamDefinitions);
					buff.append("\"%parameter " + paramID + "%\"");
					++paramID;
				}
				else {
					buff.append(token);
				}
			}
			if (iter.hasNext()) {
				buff.append(",");
			}
		}
		buff.append(")");
		return buff.toString();
	}

	public static StringBuilder generateContextSequence(String type, String categoryOrEntity, int[] ids) {
		StringBuilder sequence = new StringBuilder();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				sequence.append("(build$ ");
				sequence.append(type + " " + categoryOrEntity + " " + ids[i]);
				sequence.append(")");
			}
		}
		return sequence;
	}

	public static String generateMessageText(GuidelineGenerateParams ruleParams, int entityID) throws com.mindbox.pe.server.parser.jtb.message.ParseException {
		TemplateMessageDigest digest = ruleParams.findTemplateMessageDigest(entityID);
		MessageProcessor messageProcessor = new MessageProcessor();
		Message messageObj = parseMessage(digest);

		if (messageObj == null) {
			return "\"\"";
		}
		String msg = messageProcessor.process(messageObj, ruleParams, digest);
		return msg;
	}

	static GenericEntityType getFirstDisallowedEntityInContext(ProductGrid productGrid) {
		TemplateUsageType usageType = productGrid.getTemplate().getUsageType();
		ControlPatternConfigHelper controlPatternConfig = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).getControlPatternConfig();
		GenericEntityType[] entityTypes = productGrid.getGenericCategoryEntityTypesInUse();
		for (int i = 0; i < entityTypes.length; i++) {
			if (controlPatternConfig.isDisallowed(entityTypes[i])) {
				return entityTypes[i];
			}
		}
		entityTypes = productGrid.getGenericEntityTypesInUse();
		for (int i = 0; i < entityTypes.length; i++) {
			if (controlPatternConfig.isDisallowed(entityTypes[i])) {
				return entityTypes[i];
			}
		}
		return null;
	}

	public static FormatterFactory getFormatterFactoryForCurrentThread() {
		return FORMATTER_FACTORY_THREAD_LOCAL.get();
	}

	/**
	 * @param entity cannot be <code>null</code>
	 * @param usageType
	 * @return string value for the specified generic entity for rule generation
	 * @throws NullPointerException if <code>entity</code> is <code>null</code>
	 */
	// From 5.0, PE always write id of generic entity, no matter what; i.e., no longer configurable
	public static final String getGenericEntityIDValue(GenericEntity entity) {
		return String.valueOf(entity.getID());
	}

	public static RuleGeneratorHelper getInstance() {
		if (instance == null) {
			instance = new RuleGeneratorHelper();
		}
		return instance;
	}

	public static String getRuleFilename(final GridTemplate template) throws RuleGenerationException {
		return getRuleFilename(template.getUsageType().toString(), template.getName());
	}

	private static String getRuleFilename(final String prefix) throws RuleGenerationException {
		return String.format("%s-rules", prefix.toUpperCase());
	}

	public static String getRuleFilename(final String prefix, final String subName) throws RuleGenerationException {
		if (subName == null) {
			return getRuleFilename(prefix);
		}
		else {
			return String.format("%s_%s-rules", prefix.toUpperCase(), subName.replaceAll(INVALID_FILENAME_CHARS, "_"));
		}
	}

	public static String getRuleFilename(final TemplateUsageType templateUsageType) throws RuleGenerationException {
		return getRuleFilename(templateUsageType.toString().toUpperCase());
	}

	static boolean hasSameEffectiveAndExpirationDates(AbstractGrid<?> grid) {
		return grid.getEffectiveDate() != null && grid.getEffectiveDate().isSameDate(grid.getExpirationDate());
	}

	public static boolean hasValue(Object value, AbstractGenerateParms generateParams) {
		if (value instanceof AbstractAeValue) {
			return hasValue((AbstractAeValue) value, generateParams);
		}
		else if (value instanceof String) {
			return (value != null && ((String) value).length() > 0);
		}
		else {
			return value != null;
		}
	}

	static boolean inStatus(AbstractGrid<?> abstractgrid, String targetStatus) {
		return inStatus(abstractgrid.getStatus(), targetStatus);
	}

	public static boolean inStatus(String status, String targetStatus) {
		int i = statusIndex(status);
		int j = statusIndex(targetStatus);
		return i >= j;
	}

	static boolean isNotEmpty(Object value) {
		return !(value == null || value.toString() == null || value.toString().trim().length() == 0);
	}

	public static Message parseMessage(TemplateMessageDigest digest) throws com.mindbox.pe.server.parser.jtb.message.ParseException {
		if (digest != null && digest.getText() != null) {
			MessageParser messageParser = new MessageParser(new StringReader(digest.getText()));
			return messageParser.Message();
		}
		return null;
	}

	public static Date parseToDate(String str) throws java.text.ParseException {
		return getFormatterFactoryForCurrentThread().getRuleDateFormat().parse(str);
	}

	public static final String replaceColumnReferenceInDynamicString(DynamicStringValue dsValue, GridTemplate template, GridValueContainable gridData, int row) throws InvalidDataException {
		String text = dsValue.toString();
		Pattern pattern = Pattern.compile("%column ([0-9]+)%");
		Matcher matcher = pattern.matcher(text);

		StringBuilder buff = new StringBuilder();

		int prevIndex = 0;
		while (matcher.find()) {
			// add text upto the match
			buff.append(text.substring(prevIndex, matcher.start()));
			prevIndex = matcher.end();

			int col = Integer.parseInt(matcher.group(1));
			Object columnValue = gridData.getCellValueObject(row, col, null); // ruleParams.getColumnValue(col);
			if (columnValue == null) {
				buff.append(RuleGeneratorHelper.AE_NIL);
			}
			else if (EnumValues.class.isInstance(columnValue)) {
				MessageProcessor.writeEnum(
						template.getColumn(col),
						(EnumValues<?>) columnValue,
						buff,
						ConfigurationManager.getInstance().getRuleGenerationConfigHelper(template.getUsageType()).getMessageConfig());
			}
			else if (columnValue instanceof IRange) {
				MessageProcessor.writeRange(
						(IRange) columnValue,
						buff,
						template.getColumn(col).getColumnDataSpecDigest(),
						ConfigurationManager.getInstance().getRuleGenerationConfigHelper(template.getUsageType()).getMessageConfig());
			}
			else {
				buff.append(columnValue);
			}
		}
		if (prevIndex == 0) {
			buff.append(text);
		}
		else if (prevIndex > 0 && prevIndex < text.length() - 1) {
			buff.append(text.substring(prevIndex));
		}

		return buff.toString();
	}

	private static int statusIndex(String s) {
		return TypeEnumValueManager.getInstance().getEnumValueIDForValue("system.status", s);
	}

	/**
	 * Gets ART*Enteprise function for the specified arithematic operator.
	 * @param op the operator; one of constants defined in {@link Condition}
	 * @return A*E function for <code>op</code>
	 */
	public final static String toARTScriptOpString(int op) {
		switch (op) {
		case Condition.OP_EQUAL:
			return EQUALIFY_FUNCTION;
		case Condition.OP_NOT_EQUAL:
			return INEQUALIFY_FUNCTION;
		case Condition.OP_GREATER:
			return Condition.OPSTR_GREATER;
		case Condition.OP_GREATER_EQUAL:
			return Condition.OPSTR_GREATER_EQUAL;
		case Condition.OP_LESS:
			return Condition.OPSTR_LESS;
		case Condition.OP_LESS_EQUAL:
			return Condition.OPSTR_LESS_EQUAL;
		case Condition.OP_IN:
			return Condition.OPSTR_IN;
		case Condition.OP_NOT_IN:
			return Condition.OPSTR_NOT_IN;
		case Condition.OP_BETWEEN:
			return Condition.OPSTR_BETWEEN;
		case Condition.OP_NOT_BETWEEN:
			return Condition.OPSTR_NOT_BETWEEN;
		case Condition.OP_IS_EMPTY:
			return Condition.OPSTR_IS_EMPTY;
		case Condition.OP_ANY_VALUE:
			return Condition.OPSTR_ANY_VALUE;
		case Condition.OP_IS_NOT_EMPTY:
			return Condition.OPSTR_IS_NOT_EMPTY;
		default:
			throw new IllegalArgumentException("Invalid op: " + op);
		}
	}

	private static RuleAction toRuleAction(GridTemplate template, int columnNo, String ruleName, AeRule aeRule, MutableBoolean wasActionCreated, User user) throws ServletActionException,
			ParseException {
		Logger logger = Logger.getLogger(RuleGeneratorHelper.class);
		logger.debug(">>> toRuleAction: " + template.getID() + ", " + columnNo + ", " + ruleName);

		// create new action type definition only if necessary
		ActionTypeDefinition actionDef = GuidelineFunctionManager.getInstance().getActionTypeDefinition(ruleName);
		if (actionDef == null) {
			actionDef = findMatchingActionType(template, columnNo, aeRule);
		}

		if (actionDef == null) {
			logger.debug("... toRuleAction: creating new action definition...");
			actionDef = new ActionTypeDefinition(-1, ruleName, "Action created for " + ruleName);
			actionDef.addUsageType(template.getUsageType());
		}
		else {
			logger.debug("... toRuleAction: existing action type def found: " + actionDef);
		}

		RuleAction ruleAction = RuleElementFactory.getInstance().createRuleAction();
		ruleAction.setActionType(actionDef);

		// don't generate params if actionDef is an existing one
		String deploymentRule = generateActionTypeDeploymentRule(template, columnNo, aeRule, (actionDef.getID() < 0), true, actionDef, ruleAction);

		// persist action definition if necessary
		if (actionDef.getID() < 0) {
			logger.debug("... toRuleAction: setting rule text to " + deploymentRule);

			actionDef.setDeploymentRule(deploymentRule);

			int actionID = BizActionCoordinator.getInstance().save(actionDef, user);
			actionDef.setID(actionID);
			wasActionCreated.setState(true);
		}
		else {
			wasActionCreated.setState(false);
		}

		logger.debug("<<< toRuleAction: " + wasActionCreated);
		return ruleAction;
	}

	public static String toRuleDateString(Date date) {
		if (date != null) {
			if (ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getLhsDateFormat().equalsIgnoreCase("julian")) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				return String.valueOf(DateUtil.dateToJulian(calendar));
			}
			else {
				return '"' + getFormatterFactoryForCurrentThread().getRuleDateFormat().format(date) + '"';
			}
		}
		else {
			return RuleGeneratorHelper.AE_NIL;
		}
	}

	static String toRuleDateTimeString(Date date) {
		if (date != null) {
			if (ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getLhsDateFormat().equalsIgnoreCase("julian")) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				return String.valueOf(DateUtil.dateToJulian(calendar));
			}
			else {
				return '"' + getFormatterFactoryForCurrentThread().getRuleDateTimeFormat().format(date) + '"';
			}
		}
		else {
			return RuleGeneratorHelper.AE_NIL;
		}
	}

	static String toRuleDateTimeString(DateSynonym dateSynonym) {
		return (dateSynonym == null ? toRuleDateTimeString((Date) null) : toRuleDateTimeString(dateSynonym.getDate()));
	}

	public static RuleDefinition toRuleDefinition(GridTemplate template, int columnNo, String ruleName, DeploymentRule deploymentRule, MutableBoolean wasActionCreated, User user)
			throws SapphireException, ServletActionException, ParseException {
		Logger logger = Logger.getLogger(RuleGeneratorHelper.class);
		logger.debug(">>> toRuleDefinition: " + template.getID() + ", " + columnNo + ", " + ruleName);

		RuleDefinition ruleDefinition = new RuleDefinition(-1, ruleName, ruleName);

		AeRule aeRule = new AeRuleBuilder().generateRuleSkeleton(deploymentRule, template.getUsageType());
		new RuleDefProcessor(columnNo, ruleDefinition, template, user).process(aeRule);

		logger.debug("... toRuleDefinition: LHS processed");

		// update action
		ruleDefinition.updateAction(toRuleAction(template, columnNo, ruleName, aeRule, wasActionCreated, user));

		logger.debug("<<< toRuleDefinition");
		return ruleDefinition;
	}

	static void writeDateValue(DefaultBufferedGenerator generator, Date date) {
		generator.print(formatDateValueForLHS(date));
	}

	private RuleGeneratorHelper() {
		super();
	}

}