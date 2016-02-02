package com.mindbox.pe.server.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.AbstractGuidelineGrid;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.RuleMessageContainer;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.config.ConfigurationManager;


/**
 * @author MindBox
 * Used for the generation of XML for reporting. When a JSP page is invoked (generic-templates.jsp 
 * and specific-template.jsp) an instance of this class is instantiated. During the JSP processing
 * as the report is iterating over templates, guidelines, columns and columns the instance is
 * being updated to hold the current object in the iteration so JSTL calls can be made without 
 * paramters.
 * TODO: Most the these methods have been pulled from JSP pages and have not been
 * unit tested. 
 *
 */
public class ReportGeneratorHelper implements ErrorMessageStorage {

	private GridTemplate currentTemplate;
	private ProductGrid currentGrid;
	private int currentRowNumber;
	private int currentColNumber;
	private final List<String> errorMessageList;
	private final ReportFilterDataHolder reportFilterDataHolder;
	private final Logger logger = Logger.getLogger(ReportGeneratorHelper.class);

	public ReportGeneratorHelper(String templateName, String templateID, String usageType, String columns, String contextElements,
			String includeChildren, String includeParents, String filterColumnData, String includeEmptyContexts, String status,
			String dateStr) {
		this.errorMessageList = new ArrayList<String>();

		reportFilterDataHolder = new ReportFilterDataHolder(
				this,
				true,
				templateName,
				templateID,
				usageType,
				columns,
				contextElements,
				includeChildren,
				includeParents,
				filterColumnData,
				includeEmptyContexts,
				status,
				dateStr);
		initialize();
	}

	private void initialize() {
		if (!reportFilterDataHolder.isTemplateSpecified()) {
			addErrorMessage("No template report generated: no templates found.");
		}
		else {
			reportFilterDataHolder.removeTemplatesWithoutGrid();
		}
	}

	private String toXMLString(java.util.Date date) {
		return ConfigUtil.toDateXMLString(date);
	}

	public void addErrorMessage(String message) {
		errorMessageList.add(message);
	}

	public List<GridTemplate> getTemplateList() {
		return reportFilterDataHolder.getTemplates();
	}

	public Map<Integer, String> getColumnNumberElementMap() {
		Map<Integer, String> map = new TreeMap<Integer, String>();
		String[] columnNames = reportFilterDataHolder.getColumnNames();
		if ((columnNames != null) && (columnNames.length > 0)) {
			for (int i = 0; i < columnNames.length; i++) {
				AbstractTemplateColumn column = currentTemplate.findColumnWithTitle(columnNames[i], true);
				if (column != null) {
					map.put(column.getID(), ReportGenerator.toElementName(columnNames[i]) + "_column");
				}
			}
		}

		for (int col = 1; col <= currentTemplate.getNumColumns(); col++) {
			if (!map.containsKey(col)) {
				map.put(col, "column" + ReportGenerator.getColumnNamePostfix(col));
			}
		}

		return map;
	}

	public GridTemplate getCurrentTemplate() {
		return currentTemplate;
	}

	public void setCurrentTemplate(GridTemplate currentTemplate) {
		this.currentTemplate = currentTemplate;
	}

	public String getTemplateElementName() {
		return ReportGenerator.toElementName(currentTemplate.getName() + "_v" + currentTemplate.getVersion());
	}

	public String getTemplateNameVersion() {
		return ReportGenerator.htmlify(currentTemplate.getName()) + " (" + currentTemplate.getVersion() + ")";
	}

	public String getTemplateNameForReport() {
		return ReportGenerator.formatForReport(currentTemplate.getName());
	}

	public String getTemplateVersionForReport() {
		return ReportGenerator.formatForReport(currentTemplate.getVersion());
	}

	public String getUsageForReport() {
		return ReportGenerator.formatForReport(currentTemplate.getUsageType());
	}

	public List<AbstractGuidelineGrid> getActivations() {
		return filterActivations(GridManager.getInstance().getAllGridsForTemplate(currentTemplate.getId()));
	}

	public ProductGrid getCurrentGrid() {
		return currentGrid;
	}

	public void setCurrentGrid(ProductGrid currentGrid) {
		this.currentGrid = currentGrid;
	}

	public String getGridEffectiveDate() {
		return ((currentGrid.getEffectiveDate() == null) ? "" : toXMLString(currentGrid.getEffectiveDate().getDate()));
	}

	public String getGridExpirationDate() {
		return ((currentGrid.getExpirationDate() == null) ? "" : toXMLString(currentGrid.getExpirationDate().getDate()));
	}

	public String getGridCreationDate() {
		return toXMLString(currentGrid.getCreationDate());
	}

	public String getGridStatusChangeDate() {
		return toXMLString(currentGrid.getStatusChangeDate());
	}

	public String getGridComments() {
		return ReportGenerator.formatAsTextElements(currentGrid.getComments());
	}

	/**
	 * @return a string representing the template level rule with
	 * html formatting.
	 */
	public String getTemplateRuleStringForReport() {
		String ruleString = null;

		if ((currentTemplate.getRuleDefinition() != null) && !currentTemplate.getRuleDefinition().isEmpty()) {
			try {
				ruleString = ReportGenerator.generateRuleStringForReport(currentTemplate.getRuleDefinition(), currentTemplate);
			}
			catch (ReportException e) {
				logger.error("Error: caught exception in getTemplateRuleStringForReport()->" + e.toString());
				ruleString = "<!-- Error: " + e.toString() + " -->";
			}
		}
		return ruleString;
	}

	public String getTemplateMessageForReport() {
		String messageString = null;

		if ((currentTemplate.getRuleDefinition() != null) && !currentTemplate.getRuleDefinition().isEmpty()) {
			try {
				messageString = ReportGenerator.generateMessageStringForReport(-1, currentTemplate);
			}
			catch (Exception e) {
				logger.error("Error: caught exception in getTemplateMessageForReport()->" + e.toString());
				messageString = "<!-- Error: " + e.toString() + " -->";
			}
		}
		return messageString;
	}

	public int getCurrentRowNumber() {
		return currentRowNumber;
	}

	public void setCurrentRowNumber(int currentRowNumber) {
		this.currentRowNumber = currentRowNumber;
	}

	public Map<String, String> getEntityContextValuesMap() {
		Map<String, String> map = new HashMap<String, String>();
		EntityConfiguration entityConfig = ConfigurationManager.getInstance().getEntityConfiguration();
		EntityTypeDefinition[] typeDefs = entityConfig.getEntityTypeDefinitions();

		for (int i = 0; i < typeDefs.length; i++) {
			if (typeDefs[i].useInContext()) {
				String key = ReportGenerator.toElementName(typeDefs[i].getName());
				String value = ReportGenerator.extractContext(currentGrid.extractGuidelineContext(), typeDefs[i]);
				map.put(key, value);

				if (typeDefs[i].hasCategory()) {
					CategoryTypeDefinition categoryDef = entityConfig.findCategoryTypeDefinition(typeDefs[i].getCategoryType());

					if (categoryDef != null) {
						key = ReportGenerator.toElementName(categoryDef.getName());
						value = ReportGenerator.extractContext(currentGrid.extractGuidelineContext(), categoryDef);
						map.put(key, value);
					}
				}
			}
		}
		return map;
	}

	public int getCurrentColNumber() {
		return currentColNumber;
	}

	public void setCurrentColNumber(int currentColNumber) {
		this.currentColNumber = currentColNumber;
	}

	public String getColumnElementNameForReport() {
		return ReportGenerator.toElementName(currentTemplate.getColumn(currentColNumber).getTitle());
	}

	public String getCellValueForReport() {
		String cellValue = null;

		try {
			cellValue = ReportGenerator.formatForReport(currentGrid.getCellValueObject(currentRowNumber, currentColNumber, " "));
		}
		catch (InvalidDataException e) {
			logger.error("Error: caught exception in getCellValueForReport()->" + e.toString());
			cellValue = "<!-- Error: " + e.toString() + " -->";
		}
		return cellValue;
	}

	public String getRowRuleStringForReport() {
		String rowRuleString = "";

		if ((currentTemplate.getRuleDefinition() != null) && !currentTemplate.getRuleDefinition().isEmpty()) {
			try {
				rowRuleString = ReportGenerator.generateRowRuleStringForReport(
						currentRowNumber,
						currentTemplate.getRuleDefinition(),
						currentTemplate,
						currentGrid);
			}
			catch (ReportException e) {
				logger.error("Error: caught exception in generateRowRuleStringForReport()->" + e.toString());
				rowRuleString = "<!-- Error: " + e.toString() + " -->";
			}
		}
		return rowRuleString;
	}

	public String getRowMessageStringForReport() {
		String rowMsgString = "";

		if ((currentTemplate.getRuleDefinition() != null) && !currentTemplate.getRuleDefinition().isEmpty()) {
			try {
				int[] entityIDs = ReportGenerator.extractMessageContextEntityIDs(currentGrid.extractGuidelineContext());
				rowMsgString = ReportGenerator.generateRowMessageStringForReport(
						currentRowNumber,
						-1,
						currentTemplate,
						ReportGenerator.getMessageContextEntityType(),
						entityIDs,
						currentGrid);
			}
			catch (Exception e) {
				logger.error("Error: caught exception in generateRowMessageStringForReport()->" + e.toString());
				rowMsgString = "<!-- Error: " + e.toString() + " -->";
			}
		}
		return rowMsgString;
	}

	public Map<String, String[]> getRowColumnRuleAndMessageMap() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		Map<Integer, RuleMessageContainer> containerMap = ReportGenerator.buildColumnRuleMessageContainerMap(currentTemplate);

		for (Iterator<Integer> keyIter = containerMap.keySet().iterator(); keyIter.hasNext();) {
			int[] entityIDs = ReportGenerator.extractMessageContextEntityIDs(currentGrid.extractGuidelineContext());
			Integer columnValue = keyIter.next();
			RuleMessageContainer rmContainer = containerMap.get(columnValue);
			String colNameToUse = ReportGenerator.toElementName(((GridTemplateColumn) rmContainer).getTitle());
			String[] colRuleAndMsg = new String[2];

			try {
				colRuleAndMsg[0] = ReportGenerator.generateRowRuleStringForReport(
						currentRowNumber,
						rmContainer.getRuleDefinition(),
						currentTemplate,
						currentGrid);
			}
			catch (ReportException e) {
				logger.error("Error: caught exception in getRowColumnRuleAndMessageMap()->" + e.toString());
				colRuleAndMsg[0] = "<!-- Error: " + e.toString() + " -->";
			}

			try {
				int colNoToUse = ((columnValue.intValue() > 0) ? columnValue.intValue() : (-1));
				colRuleAndMsg[1] = ReportGenerator.generateRowMessageStringForReport(
						currentRowNumber,
						colNoToUse,
						currentTemplate,
						ReportGenerator.getMessageContextEntityType(),
						entityIDs,
						currentGrid);
			}
			catch (Exception e) {
				logger.error("Error: caught exception in getRowColumnRuleAndMessageMap()->" + e.toString());
				colRuleAndMsg[1] = "<!-- Error: " + e.toString() + " -->";
			}
			map.put(colNameToUse, colRuleAndMsg);
		}
		return map;
	}

	public Map<String, String[]> getColumnRuleAndMessageMap() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		Map<Integer, RuleMessageContainer> containerMap = ReportGenerator.buildColumnRuleMessageContainerMap(currentTemplate);

		for (Iterator<Integer> keyIter = containerMap.keySet().iterator(); keyIter.hasNext();) {
			Integer columnValue = keyIter.next();
			RuleMessageContainer rmContainer = containerMap.get(columnValue);
			String colNameToUse = getColumnNumberElementMap().get(columnValue);
			String[] colRuleAndMsg = new String[2];

			try {
				colRuleAndMsg[0] = ReportGenerator.generateRuleStringForReport(rmContainer.getRuleDefinition(), currentTemplate);
			}
			catch (ReportException e) {
				logger.error("Error: caught exception in getColumnRuleAndMessageMap()->" + e.toString());
				colRuleAndMsg[0] = "<!-- Error: " + e.toString() + " -->";
			}

			try {
				int colNoToUse = ((columnValue.intValue() > 0) ? columnValue.intValue() : (-1));
				colRuleAndMsg[1] = ReportGenerator.generateMessageStringForReport(colNoToUse, currentTemplate);
			}
			catch (Exception e) {
				logger.error("Error: caught exception in getColumnRuleAndMessageMap()->" + e.toString());
				colRuleAndMsg[1] = "<!-- Error: " + e.toString() + " -->";
			}
			map.put(colNameToUse, colRuleAndMsg);
		}
		return map;
	}

	public List<String> getErrorMessages() {
		return errorMessageList;
	}

	private List<AbstractGuidelineGrid> filterActivations(List<ProductGrid> grids) {
		List<AbstractGuidelineGrid> activations = new ArrayList<AbstractGuidelineGrid>();
		for (Iterator<ProductGrid> i = grids.iterator(); i.hasNext();) {
			ProductGrid grid = i.next();
			// check status
			if (!reportFilterDataHolder.matchesStatus(grid)) {
				continue;
			}
			// check date
			Date date = reportFilterDataHolder.getDate();
			if (date != null) {
				if (grid.getEffectiveDate() != null && grid.getEffectiveDate().getDate() != null
						&& date.before(grid.getEffectiveDate().getDate())) {
					continue;
				}
				else if (grid.getExpirationDate() != null && grid.getExpirationDate().getDate() != null
						&& date.after(grid.getExpirationDate().getDate())) {
					continue;
				}
			}
			// Check context
			if (!reportFilterDataHolder.isContextSpecified()) {
				activations.add(grid);
			}
			else {
				if (reportFilterDataHolder.containsContext(grid) && (!reportFilterDataHolder.isFilterColumnData() || !grid.getTemplate().hasEntityTypeColumns())) {
					activations.add(grid);
				}
				// check for context match in entity columns
				else if (reportFilterDataHolder.isFilterColumnData()) {
					ProductGrid gridCopy = ProductGrid.copyOf(
							(ProductGrid) grid,
							currentTemplate,
							grid.getEffectiveDate(),
							grid.getExpirationDate());
					List<List<Object>> matchingRows = findMatchingGridRows(grid);
					if ((matchingRows != null) && (matchingRows.size() > 0)) {
						gridCopy.setDataList(matchingRows);
						activations.add(gridCopy);
					}
				}
			}
		}
		return activations;
	}

	private List<List<Object>> findMatchingGridRows(AbstractGuidelineGrid grid) {
		List<List<Object>> rows = new ArrayList<List<Object>>();
		List<Integer> list = ServerContextUtil.findMatchingGridRows(
				reportFilterDataHolder.getContexts(),
				grid,
				reportFilterDataHolder.isIncludeParents(),
				reportFilterDataHolder.isIncludeChildren(),
				reportFilterDataHolder.isIncludeEmptyContexts());

		if ((list != null) && (list.size() > 0)) {
			for (Iterator<Integer> i = list.iterator(); i.hasNext();) {
				rows.add(buildRow(i.next().intValue(), grid));
			}
		}

		return rows;
	}

	private List<Object> buildRow(int rowNumber, AbstractGuidelineGrid grid) {
		List<Object> objs = new ArrayList<Object>();

		for (int c = 1; c <= currentTemplate.getNumColumns(); c++) {
			objs.add(grid.getCellValue(rowNumber, currentTemplate.getColumn(c).getName()));
		}

		return objs;
	}

	public boolean isColumnHaveRuleDef() {
		return (((GridTemplateColumn) currentTemplate.getColumn(currentColNumber)).getRuleDefinition() != null && !((GridTemplateColumn) currentTemplate.getColumn(currentColNumber)).getRuleDefinition().isEmpty());
	}

	public String[] getGridColumnNames() {
		int colCount = getMaxColumnCount();
		List<String> list = new LinkedList<String>();
		String[] columnNames = reportFilterDataHolder.getColumnNames();
		if (columnNames != null) {
			for (int i = 0; i < columnNames.length; i++) {
				list.add(ReportGenerator.toElementName(columnNames[i]) + "_column");
			}
		}
		for (int i = 1; i <= colCount; i++) {
			list.add("column" + ReportGenerator.getColumnNamePostfix(i));
		}
		return list.toArray(new String[0]);
	}

	private int getMaxColumnCount() {
		int max = 0;
		for (Iterator<GridTemplate> iter = reportFilterDataHolder.getTemplates().iterator(); iter.hasNext();) {
			GridTemplate gridtemplate = iter.next();
			max = Math.max(max, gridtemplate.getNumColumns());
		}

		String[] columnNames = reportFilterDataHolder.getColumnNames();
		if (columnNames == null || columnNames.length == 0) return max;

		for (Iterator<GridTemplate> iter = reportFilterDataHolder.getTemplates().iterator(); iter.hasNext();) {
			GridTemplate gridtemplate = iter.next();
			max = Math.max(max, countNonMatchingColumns(gridtemplate));
		}
		return max;
	}

	private int countNonMatchingColumns(GridTemplate template) {
		int count = 0;
		for (int col = 1; col <= template.getNumColumns(); col++) {
			if (!UtilBase.isMember(template.getColumn(col).getTitle(), reportFilterDataHolder.getColumnNames(), true)) {
				++count;
			}
		}
		return count;
	}

}
