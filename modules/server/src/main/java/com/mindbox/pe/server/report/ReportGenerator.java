package com.mindbox.pe.server.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.validate.TemplateValidator;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.comparator.IDNameObjectComparator;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.grid.GridCellCoordinates;
import com.mindbox.pe.model.grid.GridCellSet;
import com.mindbox.pe.model.grid.GridValueContainable;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.report.GuidelineReportSpec;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.MessageContainer;
import com.mindbox.pe.model.template.RuleContainer;
import com.mindbox.pe.model.template.RuleMessageContainer;
import com.mindbox.pe.model.template.TemplateMessageDigest;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.parser.jtb.message.MessageParser;
import com.mindbox.pe.server.parser.jtb.message.ParseException;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.servlet.ResourceUtil;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Report generator.
 * <p>
 * <b>Usage</b>
 * <ol>
 * <li>Create a new instance of this by invoking {@link #ReportGenerator(Writer)}. The generated
 * report will be written to the specified writer.</li>
 * <li>Call report generator method {@link #generateGuidelineReport(GuidelineReportSpec, List)}.</li>
 * </ol>
 * Make sure do not invoke report generator methods for more than once per each instance. It will
 * result in a runtime exception to be thrown.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class ReportGenerator {

	public static final String POLICY_SUMMARY_REPORT_FILE = "Policy-Summary-Report.rpt";

	private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	private static void appendEntityLabelName(StringBuilder buff, GenericEntityType entityType, int entityID) {
		if (entityID > 0 && entityType != null) {
			GenericEntity entity = EntityManager.getInstance().getEntity(entityType, entityID);
			buff.append("<b>");
			buff.append((entity == null ? "(<font color='red'>" + entityID + "</font>)" : entity.getName()));
			buff.append("</b>: ");
		}
	}

	private static void appendMessageString(StringBuilder buff, GridTemplate template, GenericEntityType entityType, int entityID, int column, GridValueContainable grid,
			boolean replaceColumnRef, int row) throws InvalidDataException {
		try {
			Message messageObj = findMessage(template, column, entityID);
			if (messageObj != null) {
				buff.append("<li>");
				appendEntityLabelName(buff, entityType, entityID);

				MessageReportGenerator messageProcessor = new MessageReportGenerator(replaceColumnRef);
				String messageStr = messageProcessor.process(messageObj, grid, template, row, column);
				Logger.getLogger(ReportGenerator.class).debug("* appendMessageString: messageStr = '" + messageStr + "'");
				buff.append(((messageStr == null || messageStr.trim().length() == 0) ? "None" : messageStr.trim()));
				buff.append("</li>");
			}
			else {
				buff.append("None");
			}
		}
		catch (ParseException ex) {
			Logger.getLogger(ReportGenerator.class).warn("Message for Template:" + template + ",Column" + column + ",entityID:" + entityID + " has syntax error", ex);
			buff.append("<span class='error'>***ERROR: Message contains syntax error: " + ex.getMessage() + "</span>");
		}
	}

	/**
	 * 
	 * @param buff buffReportGenerato
	 * @param template templateReportGenerato
	 * @param channelID channelIDReportGenerato
	 * @param column columnReportGenerato
	 * @param grid gridReportGenerato
	 * @param replaceColumnRef replaceColumnRefReportGenerato
	 * @param row rowReportGenerato
	 * @return <code>true</code> if <code>buff</code> was modified; <code>false</code>,
	 *         otherwise
	 * @throws ParseException
	 * @throws InvalidDataException
	 */
	private static boolean appendMessageStringForReport(StringBuilder buff, GridTemplate template, GenericEntityType entityType, int entityID, int column,
			GridValueContainable grid, boolean replaceColumnRef, int row) throws InvalidDataException {
		try {
			Message messageObj = findMessage(template, column, entityID);
			if (messageObj != null) {
				appendEntityLabelName(buff, entityType, entityID);

				MessageReportGenerator messageProcessor = new MessageReportGenerator(replaceColumnRef);
				messageProcessor.setUseFormattingInLine(true);
				String messageStr = messageProcessor.process(messageObj, grid, template, row, column);
				buff.append(((messageStr == null || messageStr.trim().length() == 0) ? "None" : messageStr.trim()));

				return true;
			}
			else {
				buff.append("None");
				return true;
			}
		}
		catch (ParseException ex) {
			Logger.getLogger(ReportGenerator.class).warn("Message for Template:" + template + ",Column" + column + ",entityID:" + entityID + " has syntax error", ex);
			buff.append("<font color='red'>***ERROR: Message contains syntax error: " + ex.getMessage() + "</font>");
			return true;
		}
	}

	private static String asGridCellValue(CategoryOrEntityValue categoryOrEntityValue) {
		if (categoryOrEntityValue.isForEntity()) {
			GenericEntity entity = EntityManager.getInstance().getEntity(categoryOrEntityValue.getEntityType(), categoryOrEntityValue.getId());
			return (entity == null ? categoryOrEntityValue.getEntityType() + "[id=" + categoryOrEntityValue.getId() + "]" : entity.getName());
		}
		else {
			GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryOrEntityValue.getEntityType().getCategoryType(), categoryOrEntityValue.getId());
			return (category == null ? categoryOrEntityValue.getEntityType() + "-Category[id=" + categoryOrEntityValue.getId() + "]" : category.getName());
		}
	}

	public static String asGridCellValue(Object value) {
		if (value instanceof CategoryOrEntityValues) {
			CategoryOrEntityValues values = (CategoryOrEntityValues) value;
			if (values.isEmpty()) return "";
			StringBuilder buff = new StringBuilder();
			if (values.isSelectionExclusion()) {
				buff.append("NOT ");
			}
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) buff.append(", ");
				CategoryOrEntityValue categoryOrEntityValue = (CategoryOrEntityValue) values.get(i);
				buff.append(asGridCellValue(categoryOrEntityValue));
			}
			return buff.toString();
		}

		if (value instanceof CategoryOrEntityValue) {
			return asGridCellValue((CategoryOrEntityValue) value);
		}

		if (EnumValues.class.isInstance(value)) {
			EnumValues<?> enumValueList = (EnumValues<?>) value;
			if (enumValueList.isEmpty()) return "";
			StringBuilder buff = new StringBuilder();
			if (enumValueList.isSelectionExclusion()) {
				buff.append("NOT ");
			}
			for (int i = 0; i < enumValueList.size(); i++) {
				if (i > 0) buff.append(", ");
				Object enumValue = enumValueList.get(i);
				buff.append(enumValue instanceof EnumValue ? ((EnumValue) enumValue).getDisplayLabel() : enumValue);
			}
			return buff.toString();
		}

		if (value instanceof EnumValue) {
			return ((EnumValue) value).getDisplayLabel();
		}

		return value == null ? "" : value.toString();
	}

	public static Map<Integer, RuleMessageContainer> buildColumnRuleMessageContainerMap(GridTemplate template) {
		Map<Integer, RuleMessageContainer> map = new TreeMap<Integer, RuleMessageContainer>();
		RuleMessageContainer container = template;
		for (int col = 1; col <= template.getNumColumns(); col++) {
			container = (RuleMessageContainer) template.getColumn(col);
			if (container.getRuleDefinition() != null && !container.getRuleDefinition().isEmpty()) {
				map.put(new Integer(col), container);
			}
		}
		return map;
	}

	private static String enumValuesToDisplayString(EnumValues<EnumValue> selectedEnumValues) {
		if (selectedEnumValues == null) return "";
		EnumValues<String> enumDisplayValues = enumValuesToDisplayValues(selectedEnumValues);

		String prefix = enumDisplayValues.isSelectionExclusion() && !enumDisplayValues.isEmpty() ? "<font color='red'>" + EnumValues.OLD_EXCLUSION_PREFIX + "</font>" : "";

		StringBuilder result = new StringBuilder(prefix);

		for (Iterator<String> enumStrValIter = enumDisplayValues.iterator(); enumStrValIter.hasNext();) {
			result.append(enumStrValIter.next());
			if (enumStrValIter.hasNext()) {
				result.append(',');
			}
		}
		return result.toString();
	}

	/** Returns an EnumValues object that contains only String instances representing the DisplayLabels of selectedEnumValues */
	private static EnumValues<String> enumValuesToDisplayValues(EnumValues<EnumValue> selectedEnumValues) {
		EnumValues<String> result = new EnumValues<String>();
		result.setSelectionExclusion(selectedEnumValues.isSelectionExclusion());

		for (int k = 0; k < selectedEnumValues.size(); k++) {
			EnumValue aSelectedEnumVal = selectedEnumValues.get(k);
			result.add(aSelectedEnumVal.getDisplayLabel());
		}
		return result;
	}

	/**
	 * Gets string representation of context element for the specified category type definition from
	 * the specified guideline context array.
	 * 
	 * @param contexts contextsReportGenerato
	 *            the contexts
	 * @param typeDef typeDefReportGenerato
	 *            the category type definition
	 * @return string representation of context element
	 * @since PowerEditor 4.4.0
	 */
	public static final String extractContext(GuidelineContext[] contexts, CategoryType typeDef) {
		StringBuilder buff = new StringBuilder();
		boolean hasText = false;
		for (int i = 0; i < contexts.length; i++) {
			int[] ids = contexts[i].getIDs();
			if (contexts[i] != null && contexts[i].getGenericCategoryType() == typeDef.getTypeID().intValue()) {
				for (int j = 0; j < ids.length; j++) {
					GenericCategory obj = EntityManager.getInstance().getGenericCategory(typeDef.getTypeID().intValue(), ids[j]);
					if (obj != null) {
						if (hasText) buff.append(",");
						buff.append(obj.getName());
						if (!hasText) hasText = true;
					}
				}
				return buff.toString();
			}
		}
		return buff.toString();
	}

	/**
	 * Gets string representation of context element for the specified entity type definition from
	 * the specified guideline context array.
	 * 
	 * @param contexts contextsReportGenerato
	 *            the contexts
	 * @param typeDef typeDefReportGenerato
	 *            the entity type definition
	 * @return string representation of context element
	 * @since PowerEditor 4.4.0
	 */
	public static final String extractContext(GuidelineContext[] contexts, EntityType typeDef) {
		return extractContextForEntity(contexts, typeDef.getTypeID().intValue());
	}

	public static final String extractContext(GuidelineContext[] contexts, GenericEntityType genericEntityType) {
		return extractContextForEntity(contexts, genericEntityType.getID());
	}

	/**
	 * Gets string representation of context element for the specified entity type definition from
	 * the specified guideline context array.
	 * 
	 * @param contexts contextsReportGenerato
	 *            the contexts
	 * @param typeDef typeDefReportGenerato
	 *            the entity type definition
	 * @return string representation of context element
	 * @since PowerEditor 4.4.0
	 */
	private static final String extractContextForEntity(GuidelineContext[] contexts, final int entityTypeId) {
		StringBuilder buff = new StringBuilder();
		boolean hasText = false;
		for (int i = 0; i < contexts.length; i++) {
			if (contexts[i].getGenericEntityType() != null && contexts[i].getGenericEntityType().getID() == entityTypeId) {
				int[] ids = contexts[i].getIDs();
				for (int j = 0; j < ids.length; j++) {
					GenericEntity obj = EntityManager.getInstance().getEntity(contexts[i].getGenericEntityType(), ids[j]);
					if (obj != null) {
						if (hasText) buff.append(",");
						buff.append(obj.getName());
						if (!hasText) hasText = true;
					}
				}
				return buff.toString();
			}
		}
		return buff.toString();
	}

	/**
	 * Extracts channel ids from the specified context elements.
	 * 
	 * @param context contextReportGenerato
	 *            the context array
	 * @return array of channel ids
	 */
	public static int[] extractMessageContextEntityIDs(GuidelineContext[] context) {
		EntityType etDef = ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeForMessageContext();
		if (etDef != null) {
			for (int i = 0; i < context.length; i++) {
				if (etDef.getTypeID().intValue() == context[i].getGenericCategoryType()) {
					return context[i].getIDs();
				}
			}
		}
		return new int[] { -1 };
	}

	/**
	 * 
	 * @param guidelineList guidelineListReportGenerato
	 * @return map of usage type as key and a list of templates as values
	 */
	private static Map<TemplateUsageType, List<GridTemplate>> extractUsageTemplateMap(List<GuidelineReportData> guidelineList) {
		Map<TemplateUsageType, List<GridTemplate>> map = new HashMap<TemplateUsageType, List<GridTemplate>>();
		for (Iterator<GuidelineReportData> iter = guidelineList.iterator(); iter.hasNext();) {
			GuidelineReportData element = iter.next();
			GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(element.getID());
			if (template != null) {
				if (map.containsKey(element.getUsageType())) {
					List<GridTemplate> list = map.get(element.getUsageType());
					if (!list.contains(template)) {
						list.add(template);
					}
				}
				else {
					List<GridTemplate> list = new ArrayList<GridTemplate>();
					list.add(template);
					map.put(element.getUsageType(), list);
				}
			}
		}

		// sort template by template name
		for (List<GridTemplate> templateList : map.values()) {
			Collections.sort(templateList, new IDNameObjectComparator<GridTemplate>());
		}

		return map;
	}

	private static Message findMessage(GridTemplate template, int column, int entityID) throws ParseException {
		MessageContainer messageContainer = null;
		if (column < 0) {
			messageContainer = template;
		}
		else {
			messageContainer = (MessageContainer) template.getColumn(column);
		}
		// check if an override message exists
		if (messageContainer != null) {
			TemplateMessageDigest digest = messageContainer.findMessageForEntity(entityID);
			if (digest != null) {
				MessageParser messageParser = new MessageParser(new StringReader((digest.getText() == null ? "" : digest.getText())));
				return messageParser.Message();
			}
		}
		return null;
	}

	/**
	 * Gets the specified text as order XML text elements. For example,
	 * 
	 * <pre>
	 *           &lt;text&gt;&lt;order&gt;1&lt;/order&gt;&lt;value&gt;text from largeText&lt;/vaue&gt;&lt;/order&gt;&lt;/text&gt;&lt;text&gt;&lt;order&gt;2&lt;/order&gt;&lt;value&gt;...&lt;/value&gt;&lt;/text&gt;
	 * </pre>.
	 * 
	 * @param largeText largeTextReportGenerato
	 * @return string with ordered text elements
	 */
	public static String formatAsOrderedTextElements(String largeText) {
		if (largeText == null) return "";
		return new LargeTextWriter().asElements(largeText, "text", "order", "value", true);
	}

	/**
	 * Gets the specified string as XML text elements. For example,
	 * 
	 * <pre>
	 *           &lt;text&gt;text from largeText&lt;/text&gt;&lt;text&gt;...&lt;/text&gt;
	 * </pre>
	 * 
	 * @param largeText largeTextReportGenerato
	 * @return string with text elements
	 */
	public static String formatAsTextElements(String largeText) {
		if (largeText == null) return "";
		return new LargeTextWriter().asElements(largeText, "text", true);
	}

	public static String formatForReport(Date date) {
		synchronized (DATE_FORMAT) {
			return (date == null ? "" : DATE_FORMAT.format(date));
		}
	}

	public static String formatForReport(DateSynonym dateSynonym) {
		return (dateSynonym == null ? "" : formatForReport(dateSynonym.getDate()));
	}

	public static String formatForReport(Object cellValue) {
		if (cellValue == null) return "";
		return RuleDefinitionReportWriter.htmlify(asGridCellValue(cellValue));
	}

	public static String formatForReport(String str) {
		if (str == null) return "";
		return RuleDefinitionReportWriter.htmlify(str);
	}

	private static String generateError(String errorStr, String detail) {
		StringBuilder buff = new StringBuilder("<br/><span class='error'>");
		buff.append(errorStr);
		buff.append("<br/>");
		buff.append(detail);
		buff.append("</span><br/>");
		return buff.toString();
	}

	private static String generateGridDataHTML(GridTemplate template, ProductGrid grid) {
		StringBuilder buff = new StringBuilder();
		buff.append("<table id='grid' cellpadding='2' cellspacing='0' width='100%'>");
		buff.append("<tr class='colheading'><td><nobr>");
		buff.append(ResourceUtil.getInstance().getResource("label.row"));
		buff.append("</nobr></td>");
		// generate column headings
		String[] columnNames = new String[template.getNumColumns()];
		int[] columnWidth = new int[template.getNumColumns()];
		int totalWidth = 0;
		for (int i = 0; i < columnNames.length; i++) {
			columnNames[i] = template.getColumn(i + 1).getName();
			columnWidth[i] = template.getColumn(i + 1).getColumnWidth();
			totalWidth += columnWidth[i];
		}
		for (int i = 0; i < columnWidth.length; i++) {
			buff.append("<td width='");
			buff.append((columnWidth[i] / totalWidth) * 100);
			buff.append("%'>");
			buff.append(template.getColumn(i + 1).getTitle());
			buff.append("</td>");
		}
		buff.append("</tr>");

		// generate grid
		for (int row = 1; row <= grid.getNumRows(); row++) {
			buff.append("<tr class='row1'><td>");
			buff.append(row);
			buff.append("</td>");
			for (int i = 0; i < columnNames.length; i++) {
				buff.append("<td>");
				try {
					buff.append(asGridCellValue(grid.getCellValueObject(row, i + 1, null)));
				}
				catch (InvalidDataException e) {
					buff.append("<!-- Error: " + RuleDefinitionReportWriter.htmlify(e.toString()) + " -->");
				}
				buff.append("</td>");
			}
			buff.append("</tr>");
		}

		buff.append("</table>");
		return buff.toString();
	}

	private static String generateGridDataHTML(GridTemplate template, ProductGrid grid, GridCellSet cells) {
		StringBuilder buff = new StringBuilder();
		buff.append("<table id='grid' cellpadding='2' cellspacing='0' width='100%'>");
		buff.append("<tr class='colheading'><td><nobr>");
		buff.append(ResourceUtil.getInstance().getResource("label.row"));
		buff.append("</nobr></td>");

		// generate column headings
		SortedSet<Integer> columnsToInclude = cells.getColumnIndexes();
		int[] columnWidth = new int[columnsToInclude.size()];
		int totalWidth = 0, i = 0;
		for (Iterator<Integer> columnIter = columnsToInclude.iterator(); columnIter.hasNext(); i++) {
			int colIndex = columnIter.next();
			columnWidth[i] = template.getColumn(colIndex + 1).getColumnWidth();
			totalWidth += columnWidth[i];
		}
		i = 0;
		for (Iterator<Integer> columnIter = columnsToInclude.iterator(); columnIter.hasNext(); i++) {
			int colIndex = columnIter.next();
			buff.append("<td width='");
			buff.append((columnWidth[i] / totalWidth) * 100);
			buff.append("%'>");
			buff.append(template.getColumn(colIndex + 1).getTitle());
			buff.append("</td>");
		}
		buff.append("</tr>");

		// generate grid
		SortedSet<Integer> rowsToInclude = cells.getRowIndexes();
		for (Iterator<Integer> rowIter = rowsToInclude.iterator(); rowIter.hasNext();) {
			int rowIndex = rowIter.next();

			buff.append("<tr class='row1'><td>");
			buff.append(rowIndex + 1);
			buff.append("</td>");

			for (Iterator<Integer> columnIter = columnsToInclude.iterator(); columnIter.hasNext();) {
				buff.append("<td>");
				try {
					int colIndex = columnIter.next();

					GridCellCoordinates coords = cells.get(rowIndex, colIndex);

					if (coords != null) { // not all cells for rowsToInclude X columnsToInclude will be in the subset
						buff.append(asGridCellValue(grid.getCellValueObject(rowIndex + 1, colIndex + 1, null)));

						if (coords.getPayload() != null) {
							buff.append("<br>" + coords.getPayload());
						}
					}
				}
				catch (InvalidDataException e) {
					buff.append("<!-- Error: " + RuleDefinitionReportWriter.htmlify(e.toString()) + " -->");
				}
				buff.append("</td>");
			}
			buff.append("</tr>");
		}

		buff.append("</table>");
		return buff.toString();
	}

	public static String generateGridDataHTMLNoStyle(GridTemplate template, ProductGrid grid) {
		StringBuilder buff = new StringBuilder();
		buff.append("<table border='1' cellpadding='2' cellspacing='0' width='100%'>");
		buff.append("<tr bgcolor='#c8c8c8;'><td><nobr>");
		buff.append(ResourceUtil.getInstance().getResource("label.row"));
		buff.append("</nobr></td>");
		// generate column headings
		String[] columnNames = new String[template.getNumColumns()];
		int[] columnWidth = new int[template.getNumColumns()];
		int totalWidth = 0;
		for (int i = 0; i < columnNames.length; i++) {
			columnNames[i] = template.getColumn(i + 1).getName();
			columnWidth[i] = template.getColumn(i + 1).getColumnWidth();
			totalWidth += columnWidth[i];
		}
		for (int i = 0; i < columnWidth.length; i++) {
			buff.append("<td width='");
			buff.append((columnWidth[i] / totalWidth) * 100);
			buff.append("%'>");
			buff.append(template.getColumn(i + 1).getTitle());
			buff.append("</td>");
		}
		buff.append("</tr>");

		// generate grid
		for (int row = 1; row <= grid.getNumRows(); row++) {
			buff.append("<tr><td>");
			buff.append(row);
			buff.append("</td>");
			for (int i = 0; i < columnNames.length; i++) {
				buff.append("<td>");
				try {
					buff.append(asGridCellValue(grid.getCellValueObject(row, i + 1, null)));
				}
				catch (InvalidDataException e) {
					buff.append("<!-- Error: " + RuleDefinitionReportWriter.htmlify(e.toString()) + " -->");
				}
				buff.append("</td>");
			}
			buff.append("</tr>");
		}

		buff.append("</table>");
		return buff.toString();
	}

	private static String generateMessageString(GridTemplate template, GenericEntityType entityType, int[] entityIDs, GridValueContainable grid)
			throws InvalidDataException, ParseException {
		StringBuilder buff = new StringBuilder();
		if (hasRule(template)) {
			buff.append("<ul>");
			if (entityIDs == null || entityIDs.length == 0) {
				appendMessageString(buff, template, null, -1, -1, grid, false, 1);
			}
			else {
				for (int i = 0; i < entityIDs.length; i++) {
					appendMessageString(buff, template, entityType, entityIDs[i], -1, grid, false, 1);
				}
			}
			buff.append("</ul><br/>");
		}
		if (hasColumnRule(template)) {
			buff.append("<table id='message' cellpadding='2' cellspacing='0' width='100%'>");
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (hasRule(element)) {
					buff.append("<tr><td class='row2'>Column <nobr>");
					buff.append(element.getTitle());
					buff.append("</nobr></td><td class='row1' width='100%'>");
					buff.append("<ul>");
					if (entityIDs == null || entityIDs.length == 0) {
						appendMessageString(buff, template, null, -1, element.getColumnNumber(), grid, false, 1);
					}
					else {
						for (int i = 0; i < entityIDs.length; i++) {
							appendMessageString(buff, template, entityType, entityIDs[i], element.getColumnNumber(), grid, false, 1);
						}
					}
					buff.append("</ul>");
					buff.append("</td></tr>");
				}
			}
			buff.append("</table>");
		}
		return buff.toString();
	}

	public static String generateMessageStringForReport(GridTemplate template, int[] entityIDs, GridValueContainable grid) throws InvalidDataException, ParseException {
		GenericEntityType entityType = (ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeForMessageContext() == null
				? null
				: GenericEntityType.forID(ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeForMessageContext().getTypeID().intValue()));
		StringBuilder buff = new StringBuilder();
		if (hasRule(template)) {
			for (int i = 0; i < entityIDs.length; i++) {
				appendMessageStringForReport(buff, template, entityType, entityIDs[i], -1, grid, false, 1);
			}
			buff.append("<p>");
		}
		if (hasColumnRule(template)) {
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (hasRule(element)) {
					buff.append("Column ");
					buff.append(element.getTitle());
					buff.append(":  ");
					for (int i = 0; i < entityIDs.length; i++) {
						appendMessageStringForReport(buff, template, entityType, entityIDs[i], element.getColumnNumber(), grid, false, 1);
					}
				}
			}
		}
		return formatAsTextElements(buff.toString());
	}

	public static String generateMessageStringForReport(int column, GridTemplate template) throws InvalidDataException, ParseException {
		StringBuilder buff = new StringBuilder();
		MessageContainer messageContainer = null;
		if (column > 0)
			messageContainer = (MessageContainer) template.getColumn(column);
		else
			messageContainer = template;

		GenericEntityType entityType = (ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeForMessageContext() == null
				? null
				: GenericEntityType.forID(ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeForMessageContext().getTypeID().intValue()));
		List<TemplateMessageDigest> messageList = messageContainer.getAllMessageDigest();
		for (java.util.Iterator<TemplateMessageDigest> mi = messageList.iterator(); mi.hasNext();) {
			TemplateMessageDigest messageDigest = mi.next();
			appendEntityLabelName(buff, entityType, messageDigest.getEntityID());
			buff.append(messageDigest.getText());
			buff.append("<br>");
		}
		return formatAsTextElements(buff.toString());
	}

	private static String generateRowMessageString(int row, GridTemplate template, GenericEntityType entityType, int[] entityIDs, GridValueContainable grid)
			throws InvalidDataException, ParseException {
		StringBuilder buff = new StringBuilder();

		if (hasRule(template)) {
			buff.append("<ul>");
			if (entityIDs == null || entityIDs.length == 0) {
				appendMessageString(buff, template, null, -1, -1, grid, true, row);
			}
			else {
				for (int i = 0; i < entityIDs.length; i++) {
					appendMessageString(buff, template, entityType, entityIDs[i], -1, grid, true, row);
				}
			}
			buff.append("</ul><br/>");
		}
		if (hasColumnRule(template)) {
			buff.append("<table id='message' cellpadding='2' cellspacing='0' width='100%'>");
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (hasRule(element)) {
					buff.append("<tr><td class='row2'>Column <nobr>");
					buff.append(element.getTitle());
					buff.append("</nobr></td><td class='row1' width='100%'>");
					buff.append("<ul>");
					if (entityIDs == null || entityIDs.length == 0) {
						appendMessageString(buff, template, null, -1, element.getColumnNumber(), grid, true, row);
					}
					else {
						for (int i = 0; i < entityIDs.length; i++) {
							appendMessageString(buff, template, entityType, entityIDs[i], element.getColumnNumber(), grid, true, row);
						}
					}
					buff.append("</ul>");
					buff.append("</td></tr>");
				}
			}
			buff.append("</table>");
		}
		return buff.toString();
	}

	public static String generateRowMessageStringForReport(int row, GridTemplate template, GenericEntityType entityType, int[] entityIDs, GridValueContainable grid)
			throws InvalidDataException, ParseException {
		StringBuilder buff = new StringBuilder();

		if (hasRule(template)) {
			for (int i = 0; i < entityIDs.length; i++) {
				appendMessageStringForReport(buff, template, entityType, entityIDs[i], -1, grid, true, row);
			}
			buff.append("<br><br>");
		}
		if (hasColumnRule(template)) {
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (hasRule(element)) {
					buff.append("Column ");
					buff.append(element.getTitle());
					buff.append(":  ");
					for (int i = 0; i < entityIDs.length; i++) {
						appendMessageStringForReport(buff, template, entityType, entityIDs[i], element.getColumnNumber(), grid, true, row);
					}
				}
			}
		}
		return formatAsTextElements(buff.toString());
	}

	public static String generateRowMessageStringForReport(int row, int columnNo, GridTemplate template, GenericEntityType entityType, int[] entityIDs, GridValueContainable grid)
			throws InvalidDataException, ParseException {
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < entityIDs.length; i++) {
			appendMessageStringForReport(buff, template, entityType, entityIDs[i], columnNo, grid, true, row);
			buff.append("<br><br>");
		}
		return formatAsTextElements(buff.toString());
	}

	private static String generateRowRuleString(int row, GridTemplate template, GridValueContainable grid) throws ReportException {
		StringBuilder buff = new StringBuilder();
		if (hasRule(template)) {
			// validate rule before generating report
			String validationError = TemplateValidator.isValid(template.getRuleDefinition(), template, DomainManager.getInstance());
			if (validationError != null) {
				buff.append(generateError("Error validating rule for " + template.getRuleDefinition(), validationError));
			}
			else {
				buff.append(RuleDefinitionReportWriter.generateReport(template.getRuleDefinition(), template, grid, row, true));
			}
			buff.append("<br/>");
		}
		if (hasColumnRule(template)) {
			buff.append("<table id='rule' cellpadding='2' cellspacing='0' width='100%'>");
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (hasRule(element)) {
					buff.append("<tr><td class='row2'>Column: <nobr>");
					buff.append(element.getTitle());
					buff.append("</nobr></td><td class='row1' width='100%'>");
					// validate rule before generating report
					String validationError = TemplateValidator.isValid(element.getRuleDefinition(), template, DomainManager.getInstance());
					if (validationError != null) {
						buff.append(generateError("Error validating rule for " + element.getRuleDefinition(), validationError));
					}
					else {
						buff.append(RuleDefinitionReportWriter.generateReport(element.getRuleDefinition(), template, grid, row, true));
					}
					buff.append("</td></tr>");
				}
			}
			buff.append("</table>");
		}
		return buff.toString();
	}

	public static String generateRowRuleStringForReport(int row, GridTemplate template, GridValueContainable grid) throws ReportException {
		StringBuilder buff = new StringBuilder();
		if (hasRule(template)) {
			// validate rule before generating report
			String validationError = TemplateValidator.isValid(template.getRuleDefinition(), template, DomainManager.getInstance());
			if (validationError != null) {
				buff.append(generateError("Error validating rule for " + template.getRuleDefinition(), validationError));
			}
			else {
				buff.append(RuleDefinitionReportWriter.generateReport(template.getRuleDefinition(), template, grid, row, true));
			}
		}
		if (hasColumnRule(template)) {
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (hasRule(element)) {
					buff.append("<br>");
					buff.append("Column  ");
					buff.append(element.getTitle());
					buff.append(":  ");
					// validate rule before generating report
					String validationError = TemplateValidator.isValid(element.getRuleDefinition(), template, DomainManager.getInstance());
					if (validationError != null) {
						buff.append(generateError("Error validating rule for " + element.getRuleDefinition(), validationError));
					}
					else {
						buff.append(RuleDefinitionReportWriter.generateReport(element.getRuleDefinition(), template, grid, row, true));
					}
				}
			}
		}
		return formatAsTextElements(buff.toString());
	}

	public static String generateRowRuleStringForReport(int row, RuleDefinition ruleDefinition, GridTemplate template, GridValueContainable grid) throws ReportException {
		String validationError = TemplateValidator.isValid(ruleDefinition, template, DomainManager.getInstance());
		if (validationError != null) {
			return formatAsTextElements(generateError("Error validating rule for " + ruleDefinition, validationError));
		}
		else {
			return formatAsTextElements(RuleDefinitionReportWriter.generateReport(ruleDefinition, template, grid, row, true));
		}
	}

	private static String generateRuleString(GridTemplate template) throws ReportException {
		StringBuilder buff = new StringBuilder();
		if (hasRule(template)) {
			// validate rule before generating report
			String validationError = TemplateValidator.isValid(template.getRuleDefinition(), template, DomainManager.getInstance());
			if (validationError != null) {
				buff.append(generateError("Error validating rule for " + template.getRuleDefinition(), validationError));
			}
			else {
				buff.append(RuleDefinitionReportWriter.generateReport(template.getRuleDefinition(), template, true));
			}
			buff.append("<br/>");
		}
		if (hasColumnRule(template)) {
			buff.append("<table id='rule' cellpadding='2' cellspacing='0' width='100%'>");
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (hasRule(element)) {
					buff.append("<tr><td class='row2'>Column <nobr>");
					buff.append(element.getTitle());
					buff.append("</nobr></td><td class='row1' width='100%'>");
					// validate rule before generating report
					String validationError = TemplateValidator.isValid(element.getRuleDefinition(), template, DomainManager.getInstance());
					if (validationError != null) {
						buff.append(generateError("Error validating rule for " + element.getRuleDefinition(), validationError));
					}
					else {
						buff.append(RuleDefinitionReportWriter.generateReport(element.getRuleDefinition(), template, true));
					}
					buff.append("</td></tr>");
				}
			}
			buff.append("</table>");
		}
		return buff.toString();
	}

	public static String generateRuleStringForReport(GridTemplate template) throws ReportException {
		StringBuilder buff = new StringBuilder();
		if (hasRule(template)) {
			// validate rule before generating report
			String validationError = TemplateValidator.isValid(template.getRuleDefinition(), template, DomainManager.getInstance());
			if (validationError != null) {
				buff.append(generateError("Error validating rule for " + template.getRuleDefinition(), validationError));
			}
			else {
				buff.append(RuleDefinitionReportWriter.generateReport(template.getRuleDefinition(), template, true));
			}
		}
		if (hasColumnRule(template)) {
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (hasRule(element)) {
					buff.append("<br>");
					buff.append("Column ");
					buff.append(element.getTitle());
					buff.append(":  ");
					// validate rule before generating report
					String validationError = TemplateValidator.isValid(element.getRuleDefinition(), template, DomainManager.getInstance());
					if (validationError != null) {
						buff.append(generateError("Error validating rule for " + element.getRuleDefinition(), validationError));
					}
					else {
						buff.append(RuleDefinitionReportWriter.generateReport(element.getRuleDefinition(), template, true));
					}
				}
			}
		}
		return formatAsTextElements(buff.toString());
	}

	public static String generateRuleStringForReport(RuleDefinition ruleDefinition, GridTemplate template) throws ReportException {
		String validationError = TemplateValidator.isValid(ruleDefinition, template, DomainManager.getInstance());
		if (validationError != null) {
			return formatAsTextElements(generateError("Error validating rule for " + ruleDefinition, validationError));
		}
		else {
			return formatAsTextElements(RuleDefinitionReportWriter.generateReport(ruleDefinition, template, true));
		}
	}

	public static String getColumnNamePostfix(int n) {
		return getColumnNamePostfix(-1, n - 1);
	}

	private static String getColumnNamePostfix(int prefix, int n) {
		if (0 <= n && n < LETTERS.length) {
			return (prefix >= 0 ? new String(new char[] { LETTERS[prefix], LETTERS[n] }) : String.valueOf(LETTERS[n]));
		}
		else {
			return getColumnNamePostfix(++prefix, n - LETTERS.length);
		}
	}

	public static String getCustomReportURL(String reportFilename) {
		return "/set-report.jsp?reportname=" + reportFilename;
	}

	public static GenericEntityType getMessageContextEntityType() {
		EntityType etDef = ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeForMessageContext();
		return (etDef == null ? null : GenericEntityType.forID(etDef.getTypeID().intValue()));
	}

	/**
	 * Gets the URL of the specified report.
	 * 
	 * @param reportID unique report id
	 * @return the report URL
	 * @since PowerEditor 4.4.0
	 */
	public static String getPolicySummaryReportURL(long reportID) {
		return "/set-report.jsp?reportid=" + reportID + "&reportname=" + POLICY_SUMMARY_REPORT_FILE;
	}

	public static String getReportFileBaseDir() {
		return "/WEB-INF/classes";
	}

	private static final boolean hasColumnRule(GridTemplate template) {
		for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
			GridTemplateColumn element = iter.next();
			if (hasRule(element)) return true;
		}
		return false;
	}

	private static final boolean hasRule(RuleContainer ruleContainer) {
		return (ruleContainer.getRuleDefinition() != null && !ruleContainer.getRuleDefinition().isEmpty());
	}

	public static final String htmlify(String str) {
		return RuleDefinitionReportWriter.htmlify(str);
	}

	private static String toActivationReportString(ProductGrid grid) {
		StringBuilder buff = new StringBuilder();
		if (grid.getSunrise() != null) {
			buff.append(Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(grid.getSunrise()));
		}
		buff.append(" - ");
		if (grid.getSunset() != null) {
			buff.append(Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(grid.getSunset()));
		}
		return buff.toString();
	}

	public static String toElementName(CategoryType categoryTypeDefinition) {
		return toElementName(categoryTypeDefinition.getName());
	}

	public static String toElementName(GenericEntityType entityType) {
		return toElementName(entityType.getName());
	}

	public static String toElementName(String value) {
		String str = value.replaceAll("[^a-zA-Z_0-9\\-]", "_");
		if (str.matches("^[0-9].*")) {
			return "_" + str;
		}
		else {
			return str;
		}
	}

	/**
	 * 
	 * @param contexts contextsReportGenerato
	 * @return the HTML representation of the context elements
	 * @since PowerEditor 4.1.1
	 */
	public static final String toHTMLString(GuidelineContext[] contexts) {
		StringBuilder buff = new StringBuilder();
		if (contexts != null && contexts.length > 0) {
			for (int i = 0; i < contexts.length; i++) {
				if (i > 0) buff.append(RuleDefinitionReportWriter.NEW_LINE);

				if (contexts[i].getGenericEntityType() != null) {
					// handle generic entity types
					buff.append(contexts[i].getGenericEntityType().getDisplayName().toString().toUpperCase());
					buff.append(": ");
					int[] ids = contexts[i].getIDs();
					for (int j = 0; j < ids.length; j++) {
						if (j > 0) buff.append(",  ");
						buff.append(EntityManager.getInstance().getEntity(contexts[i].getGenericEntityType(), ids[j]).getName());
					}
				}
				else if (contexts[i].getGenericCategoryType() > 0) {
					// handle generic entity types
					CategoryType catTypeDef = ConfigurationManager.getInstance().getEntityConfigHelper().findCategoryTypeDefinition(contexts[i].getGenericCategoryType());
					if (catTypeDef == null) {
						buff.append("<font color='red'>");
						buff.append(contexts[i].getGenericCategoryType());
						buff.append("</font>");
					}
					else {
						buff.append(htmlify(catTypeDef.getName().toUpperCase()));
					}
					buff.append(": ");
					int[] ids = contexts[i].getIDs();
					for (int j = 0; j < ids.length; j++) {
						if (j > 0) buff.append(",  ");
						GenericCategory category = EntityManager.getInstance().getGenericCategory(contexts[i].getGenericCategoryType(), ids[j]);
						buff.append((category == null ? String.valueOf(ids[j]) : category.getName()));
					}
				}
			}
		}
		return buff.toString();
	}

	/**
	 * Translates enum ids into enum strings in the specified grid. The specified grid object will
	 * be modified.
	 * 
	 * @param grid the grid of which cell values to translate for enums
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void translateEnumCellValues(ProductGrid grid) {
		Logger logger = Logger.getLogger(ReportGenerator.class);
		String[] columnNames = grid.getColumnNames();
		// generate grid
		for (int row = 1; row <= grid.getNumRows(); row++) {
			for (int col = 1; col <= columnNames.length; col++) {
				try {
					Object cellValueObject = grid.getCellValueObject(row, col, null);
					if ((cellValueObject instanceof EnumValues || cellValueObject instanceof String)
							&& grid.getColumnDataSpecDigest(col).getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {

						EnumValues selectedEnumValues = cellValueObject instanceof EnumValues ? (EnumValues) cellValueObject : EnumValues.parseValue((String) cellValueObject);

						grid.setValue(row, col, enumValuesToDisplayString(selectedEnumValues));
					}
				}
				catch (InvalidDataException ex) {
					logger.error("Error while translating enum cell values", ex);
				}
			}
		}
	}

	private final PrintWriter writer;
	private final Logger logger;

	public ReportGenerator(Writer writer) {
		this.logger = Logger.getLogger(getClass());
		this.writer = new PrintWriter(new BufferedWriter(writer), false);
		logger.info("initialized with " + writer);
	}

	private void deinit() {
		this.writer.flush();
		this.writer.close();
		logger.info("deinit completed");
	}

	private String generateGridView(GridTemplate template, ProductGrid grid, GenericEntityType entityType, int[] entityIDs, GridCellSet cells)
			throws ReportException, InvalidDataException, ParseException {
		StringBuilder buff = new StringBuilder();
		if (cells.isEmpty()) {
			// gen full report including all grid cells and rules and messages
			buff.append(generateGridDataHTML(template, grid));
			buff.append("<br><b>");
			buff.append(ResourceUtil.getInstance().getResource("label.rule"));
			buff.append("</b><br/>");
			buff.append(generateRuleString(template));
			buff.append("<br/><b>");
			buff.append(ResourceUtil.getInstance().getResource("label.message"));
			buff.append("</b><br/>");
			buff.append(generateMessageString(template, entityType, entityIDs, grid));
			buff.append("<br/>");

		}
		else {
			// gen report for just specific cells
			buff.append(generateGridDataHTML(template, grid, cells));
		}

		return buff.toString();
	}

	public synchronized void generateGuidelineReport(GuidelineReportSpec reportSpec, List<GuidelineReportData> guidelineList) throws ReportException {
		if (guidelineList == null || guidelineList.isEmpty()) return;

		logger.debug(">>> generateGuidelineReport: spec=" + reportSpec + ",list.size()=" + guidelineList.size());
		Date generationDate = new Date();
		try {
			writer.println("<html>");
			writer.println("<head>");
			writer.println("<title>PowerEditor Guideline Report: " + generationDate + "</title>");
			writer.println("<LINK rel=\"stylesheet\" type=\"text/css\" href=\"pe_report_style.css\">");
			writer.println("<META http-equiv=\"Pragma\" content=\"no-cache\">");
			writer.println("</head>");
			writer.println("<body>");
			writer.println("<a name='top'>&nbsp</a><br/>");
			writer.flush();

			Map<TemplateUsageType, List<GridTemplate>> usageTemplateMap = extractUsageTemplateMap(guidelineList);
			logger.debug("... generateGuidelineReport: usageTemplateMap.size = " + usageTemplateMap.size());
			for (Map.Entry<TemplateUsageType, List<GridTemplate>> entry : usageTemplateMap.entrySet()) {
				TemplateUsageType usage = entry.getKey();
				List<GridTemplate> templateList = entry.getValue();

				// write usage info
				writer.println("<a name='usage_" + usage.toString() + "'><span class='usage'>Guideline Type:  " + usage.getDisplayName() + "</span></a><br/>");
				writer.flush();

				writeGuidelineReport(reportSpec, templateList, guidelineList);
			}

			writer.println("<hr noshade/>");
			writer.print("<span class='small'>PowerEditor guideline report generated on ");
			writer.print(generationDate);
			writer.println("</span><br/>");
			writer.flush();
			logger.debug("<<< generateGuidelineReport");
		}
		catch (ReportException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Error while generating guideline reports for " + guidelineList.size() + " guideline(s)", ex);
			throw new ReportException(ex.getMessage());
		}
		finally {
			deinit();
		}
	}

	private String generateRuleView(GridTemplate template, ProductGrid grid, GenericEntityType type, int[] entityIDs) throws ReportException, InvalidDataException, ParseException {
		StringBuilder buff = new StringBuilder();
		buff.append("<table id='rule' cellpadding='2' cellspacing='0' width='100%'>");
		buff.append("<tr class='colheading'><td>");
		buff.append(ResourceUtil.getInstance().getResource("label.row"));
		buff.append("</td><td>");
		buff.append(ResourceUtil.getInstance().getResource("label.rule"));
		buff.append(" / ");
		buff.append(ResourceUtil.getInstance().getResource("label.message"));
		buff.append("</td></tr>");

		for (int row = 1; row <= grid.getNumRows(); row++) {
			buff.append("<tr class='row1'><td>");
			buff.append(row);
			buff.append("</td><td width='100%'><b>");
			buff.append(ResourceUtil.getInstance().getResource("label.rule"));
			buff.append("</b><br/>");
			buff.append(generateRowRuleString(row, template, grid));
			buff.append("<br/><b>");
			buff.append(ResourceUtil.getInstance().getResource("label.message"));
			buff.append("</b><br/>");
			buff.append(generateRowMessageString(row, template, type, entityIDs, grid));
			buff.append("</td></tr>");
		}
		buff.append("</table>");
		return buff.toString();
	}

	private int writeAs2ColumnTableRow(int rowID, String value1, String value2) {
		writer.print("<tr class='row");
		writer.print(rowID);
		writer.print("'><td><nobr>");
		writer.print(value1);
		writer.print("</nobr></td><td width='100%'>");
		writer.print(value2);
		writer.println("</td></tr>");
		return (rowID % 2) + 1;
	}

	private synchronized void writeGuidelineReport(GuidelineReportSpec reportSpec, GridTemplate template, GuidelineReportData guidelineData)
			throws IOException, ServletActionException, ReportException, InvalidDataException, ParseException {
		logger.debug(">>> writeGuidelineReport(template): " + reportSpec + "," + template + "," + guidelineData);

		ProductGrid cachedGrid = GridActionCoordinator.getInstance().getProductGridFor(guidelineData);

		writer.println("<table id='guideline' cellpadding='3' cellspacing='0' width='100%'>");

		if (cachedGrid == null) {
			writer.println("<tr><td>ERROR: failed to retrieve grid data for the specified guideline: grid not found</td></tr>");
		}
		else {
			// the report writing process below may mutate the grid, so make a copy rather than change the cached grid!
			ProductGrid grid = ProductGrid.copyOf(cachedGrid, (GridTemplate) cachedGrid.getTemplate(), cachedGrid.getEffectiveDate(), cachedGrid.getExpirationDate());

			GenericEntityType entityType = (ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeForMessageContext() == null
					? null
					: GenericEntityType.forID(ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeForMessageContext().getTypeID().intValue()));

			int rowID = 1;
			// template and context are common among all grid in gridList
			rowID = writeAs2ColumnTableRow(
					rowID,
					ResourceUtil.getInstance().getResource("label.template"),
					template.getName() + " (" + template.getID() + ')' + "  Version " + template.getVersion());
			rowID = writeAs2ColumnTableRow(rowID, ResourceUtil.getInstance().getResource("label.context"), toHTMLString(guidelineData.getContext()));

			rowID = writeAs2ColumnTableRow(rowID, ResourceUtil.getInstance().getResource("label.activation"), toActivationReportString(grid));

			if (reportSpec.isCreatedDateOn()) {
				rowID = writeAs2ColumnTableRow(
						rowID,
						ResourceUtil.getInstance().getResource("label.date.creation"),
						Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(guidelineData.getCreationDate()));
			}
			if (reportSpec.isStatusOn()) {
				rowID = writeAs2ColumnTableRow(rowID, ResourceUtil.getInstance().getResource("label.status"), grid.getStatus());
			}
			if (reportSpec.isStatusChangeDateOn()) {
				rowID = writeAs2ColumnTableRow(
						rowID,
						ResourceUtil.getInstance().getResource("label.date.lastStatus"),
						Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(grid.getStatusChangeDate()));
			}
			if (reportSpec.isCommentsOn()) {
				rowID = writeAs2ColumnTableRow(rowID, ResourceUtil.getInstance().getResource("label.comments"), "<pre>" + asGridCellValue(grid.getComments()) + "</pre>");
			}

			if (reportSpec.isGridOn() || reportSpec.isRowOn()) {// optimization
				translateEnumCellValues(grid);
			}
			if (reportSpec.isGridOn()) {
				rowID = writeAs2ColumnTableRow(
						rowID,
						ResourceUtil.getInstance().getResource("label.view.grid"),
						generateGridView(template, grid, entityType, (entityType == null ? null : grid.getGenericEntityIDs(entityType)), guidelineData.getCellSubset()));
			}
			if (reportSpec.isRowOn()) {
				rowID = writeAs2ColumnTableRow(
						rowID,
						ResourceUtil.getInstance().getResource("label.view.rule"),
						generateRuleView(template, grid, entityType, (entityType == null ? null : grid.getGenericEntityIDs(entityType))));
			}
		}
		writer.println("</table><br/>");
		writer.flush();
		// }
		logger.debug("<<< writeGuidelineReport(template)");
	}

	private synchronized void writeGuidelineReport(GuidelineReportSpec reportSpec, List<GridTemplate> templateList, List<GuidelineReportData> guidelineList)
			throws ServletActionException, ReportException, InvalidDataException, IOException, ParseException {
		logger.debug(">>> writeGuidelineReport(templateList): spec=" + reportSpec + ",templateList.size()" + templateList.size() + ",guidelineList.size()=" + guidelineList.size());
		for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate template = iter.next();

			// write template info
			writer.println(
					"<a name='template_" + template.getID() + "'><span class='template'>Template:  " + template.getName() + "   Version " + template.getVersion()
							+ "</span></a><br/>");
			writer.flush();

			for (Iterator<GuidelineReportData> iterator = guidelineList.iterator(); iterator.hasNext();) {
				GuidelineReportData element = iterator.next();
				logger.debug(">>> writeGuidelineReport: processing " + element);
				if (element.getID() == template.getID()) {
					writeGuidelineReport(reportSpec, template, element);
				}
			}
		}
		logger.debug("<<< writeGuidelineReport(templateList)");
	}

}