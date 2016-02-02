package com.mindbox.pe.server.generator;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.ParameterTemplateColumn;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.table.BooleanDataHelper;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ParameterContextConfiguration;
import com.mindbox.pe.server.model.GenericEntityIdentity;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterGenerator extends AbstractBufferedGenerator {

	private static ParameterGenerator instance = null;

	private static int counter = 0;

	/**
	 * 
	 * @param prodKeyType
	 *            the product key type
	 * @param product
	 *            the product
	 * @param usageType
	 *            the usage type
	 * @return the product ID pattern
	 */
	static final String getIDValueForParameter(ParameterContextConfiguration paramContextConfig, String type, GenericEntity genericEntity, boolean writeAsString) {
		if (genericEntity == null) return "Nil";
		StringBuffer buff = new StringBuffer();
		String keyType = paramContextConfig.findAttributeValue(type, "id");
		// QN 2007-08-15 ignore valueAsString, use domain attribute DeployType  
		if (writeAsString) {
			buff.append('"');
		}
		if (keyType.equalsIgnoreCase("id")) {
			buff.append(genericEntity.getID());
		}
		else if (keyType.equalsIgnoreCase("name")) {
			buff.append(genericEntity.getName().trim());
		}
		else {
			Object value =genericEntity.getProperty(keyType); 
			buff.append((value == null ? RuleGeneratorHelper.AE_NIL : value));
		}
		if (paramContextConfig.findAttributeIsValueAsString(type)) {
			buff.append('"');
		}
		return buff.toString();
	}
	
	public static ParameterGenerator getInstance() {
		if (instance == null) {
			instance = new ParameterGenerator();
		}
		return instance;
	}

	private ParameterTemplate currTemplate = null;

	private ParameterGrid currGrid = null;

	private int currRow = 0;

	private ParameterGenerator() {
		super();
	}

	public synchronized void init(OutputController outputController) {
		super.init(outputController);
		currTemplate = null;
		currRow = 0;
	}

	public synchronized void generate(ParameterTemplate template, GuidelineReportFilter filter) throws RuleGenerationException {
		logger.info(">>> generate(template): " + template);

		if (template.getDeployMethod() == ParameterTemplate.DEPLOY_AS_OBJECTS) {
			for (Iterator<ParameterGrid> iter = ParameterManager.getInstance().getGrids(template.getID()).iterator(); iter.hasNext();) {
				ParameterGrid paramGrid = iter.next();
				if (filter.isAcceptable(paramGrid)) {
					generate(template, paramGrid);
				}
				else {
					logger.info("generate: grid skipped because the filter rejected it: " + paramGrid);
				}
			}
		}
		else {
			String scriptDetails = template.getDeployScriptDetails();
			if (scriptDetails == null || scriptDetails.length() == 0) {
				logger.warn("generate: parameter template skipped as its script details are not found: " + template.getID());
				reportError("Parameter template " + template.getID() + " not deployed as its script details are not found");
			}
			else {
				try {
					String[] strs = scriptDetails.split(" ");
					for (int i = 0; i < strs.length; i++) {
						if (strs[i].equals("%templateID%")) {
							strs[i] = String.valueOf(template.getID());
						}
					}
					logger.info("Executing '" + UtilBase.toString(strs) + "' for Parameter Template " + template.getID());
					Runtime.getRuntime().exec(strs);
				}
				catch (Exception ex) {
					logger.error("Failed to execute " + scriptDetails, ex);
					reportError("Failed to execute " + scriptDetails + " for Parameter Template " + template.getID() + ": " + ex.getMessage());
				}
			}
		}
		logger.info("<<< generate(template): ");
	}

	private void generate(ParameterTemplate template, ParameterGrid grid) throws RuleGenerationException {
		logger.debug(">>> generate(template,grid): " + template + "," + grid);

		this.currTemplate = template;
		this.currGrid = grid;

		ParameterContextConfiguration paramContextConfig = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getParameterContextConfiguration();

		DateSynonym[] dateSynonymRanges = EntityManager.getInstance().getDateSynonymsForChangesInCategoryToEntityRelationships(
				grid,
				grid.getEffectiveDate(),
				grid.getExpirationDate()); 
		// Note: dateSynonymRanges may contain null
		for (int i = 0; i < dateSynonymRanges.length - 1; i++) {
			writeInstance(paramContextConfig, template, grid, dateSynonymRanges[i], dateSynonymRanges[i + 1]);
		}
		super.writeAll();
	}

	private String getMappedClassName(ParameterTemplate template) throws RuleGenerationException {
		return getMappedClassDeployLabel(template.getColumn(1).getMAClassName());
	}

	private synchronized String getUniqueNum() {
		if (counter > 9999999) counter = 0; // Protect against overflow.
		return "" + (new java.util.Date().getTime()) + counter++;
	}

	private void writeCategoryOrEntityValue(CategoryOrEntityValue value, boolean asString) throws RuleGenerationException {
		logger.debug("writeValue(): writing CategoryOrEntityValue: " + value);
		if (value == null) {
			print("");
		}
		else {
			String valueToPrint = null;
			if (value.isForEntity()) {
				GenericEntity entity = EntityManager.getInstance().getEntity(value.getEntityType(), value.getId());
				valueToPrint = (entity == null ? String.valueOf(value.getId()) : RuleGeneratorHelper.getGenericEntityIDValue(entity));
			}
			else {
				// Kim 2006-08-14
				// Note: just print the category id for now (until designs for version 5.0 is flushed out)
				valueToPrint = String.valueOf(value.getId());
			}
			if (asString) {
				if (valueToPrint.charAt(0) != '"') {
					valueToPrint = '"' + valueToPrint + '"';
				}
			}
			else {
				valueToPrint = AeMapper.stripQuotes(valueToPrint);
			}
			print(valueToPrint);
		}
	}

	private void writeInstance(ParameterContextConfiguration paramContextConfig, ParameterTemplate template, ParameterGrid grid, DateSynonym startDate,
			DateSynonym endDate) throws RuleGenerationException {

		logger.debug(">>> writeInstance: " + template + "," + grid);

		String className = template.getColumn(1).getMAClassName();
		String mappedClassName = getMappedClassName(template);
		String actDateAttrName = getMappedAttributeDeployLabel(className, paramContextConfig.findAttributeName("activationDate", "ActivationDate"));
		String expDateAttrName = getMappedAttributeDeployLabel(className, paramContextConfig.findAttributeName("expirationDate", "ExpirationDate"));

		int colCount = template.getColumnCount();

		String[] colAttrNames = new String[colCount];
		for (int i = 0; i < colAttrNames.length; i++) {
			colAttrNames[i] = getMappedAttributeDeployLabel(className, template.getColumn(i + 1).getMAAttributeName());
		}

		// If we are using make-instance to create instances, then don't use parens for attributes.
		// If we are using define-instance to create instances, use parens for attributes.
		String instanceCreateText = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getObjectGenInstanceCreateText();
		boolean useParen;
		String instanceName;
		if (instanceCreateText.equals("make-instance")) {
			useParen = false;
			instanceName = "";
		}
		else {
			useParen = true;
			instanceName = "gen-" + getUniqueNum();
		}

		int rowCount = grid.getNumRows();
		for (int row = 0; row < rowCount; row++) {
			this.currRow = row + 1;
			openParan();
			print(instanceCreateText); // either make-instance or define-instance
			if (instanceName.length() > 0) {
				instanceName = "gen-" + getUniqueNum();
			}
			print(" " + instanceName); // if instance-name is "", then nothing will be printed
			// here.
			print(" " + mappedClassName);
			nextLineIndent();

			// write dates
			if (startDate != null) {
				if (useParen) openParan();
				print(actDateAttrName);
				print(" ");
				print(RuleGeneratorHelper.toRuleDateTimeString(startDate));
				if (useParen) closeParan();
				nextLine();
			}
			if (endDate != null) {
				if (useParen) openParan();
				print(expDateAttrName);
				print(" ");
				print(RuleGeneratorHelper.toRuleDateTimeString(endDate));
				if (useParen) closeParan();
				nextLine();
			}

			// generic entity context
			if (grid.hasAnyGenericEntityContext()) {
				writeGenericEntityAttributes(paramContextConfig, grid, className, useParen);
			}
			if (grid.hasAnyGenericCategoryContext()) {
				writeGenericCategoryAttributes(paramContextConfig, grid, className, useParen, startDate);
			}

			// write attributes for columns
			boolean doNextLine = false;
			for (int col = 0; col < colCount; col++) {
				ParameterTemplateColumn column = (ParameterTemplateColumn) template.getColumn(col + 1);
				try {
					String valueStr = grid.getCellValue(row + 1, col + 1, null);
					doNextLine = writeSingleCell(row, col, column, valueStr, grid, doNextLine, useParen, className, colAttrNames[col]);
				}
				catch (InvalidDataException ex) {
					reportError("Failed to get cell value (i,j)=(" + (row + 1) + "," + (col + 1) + "): " + ex.getMessage());
					throw new RuleGenerationException("Failed to get cell value (i,j)=(" + (row + 1) + "," + (col + 1) + "): " + ex.getMessage());
				}
			} // for each column

			nextLineOutdent();
			closeParan();
			nextLine();
			nextLine();

			incrementObjectCount();
		} // for each row in the grid
	}
	
	
	// TODO Kim, 2007-04-23: refactor to use value classes for each column data type, similar to rule generation
	private boolean writeSingleCell(int row, int col, ParameterTemplateColumn column, String valueStr, ParameterGrid grid,
			boolean doNextLine, boolean useParen, String className, String colAttrName) throws RuleGenerationException {
		try {
			boolean doWriteAttr = !UtilBase.isEmpty(valueStr);
			if (doWriteAttr && column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
				doWriteAttr = BooleanDataHelper.mapToBooleanValue(valueStr, column.getColumnDataSpecDigest().isBlankAllowed()) != null;
			}
			if (doWriteAttr) {
				valueStr = valueStr.trim();
				if (doNextLine) {
					nextLine();
				}

				if (useParen) openParan();
				print(colAttrName);
				print(" ");

				DeployType deployType = DomainManager.getInstance().getDomainAttribute(className, column.getMAAttributeName()).getDeployType();
				boolean writeAsString = (deployType == DeployType.STRING);
				// write value
				if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
					Object obj = grid.getCellValueObject(row + 1, col + 1, null);
					CategoryOrEntityValue categoryOrEntityValue = (obj instanceof CategoryOrEntityValue
							? (CategoryOrEntityValue) obj
							: CategoryOrEntityValue.valueOf(
									valueStr,
									column.getColumnDataSpecDigest().getEntityType(),
									column.getColumnDataSpecDigest().isEntityAllowed(),
									column.getColumnDataSpecDigest().isCategoryAllowed()));
					writeCategoryOrEntityValue(categoryOrEntityValue, writeAsString);
				}
				else if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
					
					// TODO Kim, 2008-05-28: E.S. -- handle enums from column & data source, when supported
					
					String mappedValue = AeMapper.getEnumAttributeIfApplicable(className, column.getMAAttributeName(), valueStr, false);
					if (mappedValue == null) {
						reportError("DeployValue of " + valueStr + " not found for Class.Attribute " + column.getMappedAttribute());
						mappedValue = "ERROR-DeployValue-Not-Found-" + valueStr;
					}

					print(mappedValue);
				}
				else if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
					String strToUse = (valueStr == null ? null : (valueStr.equalsIgnoreCase("Y") ? BooleanDataHelper.TRUE_VALUE : valueStr));
					if (!BooleanDataHelper.isValidString(strToUse)) {
						throw new RuleGenerationException("Invalid boolean value at row " + (row+1) + ", column " + (col+1) + ": " + valueStr);
					}
					Boolean mappedValue = BooleanDataHelper.mapToBooleanValue(strToUse, column.getColumnDataSpecDigest().isBlankAllowed());
					if (mappedValue != null) {
						String valueToPrint = (deployType == DeployType.BOOLEAN ? (mappedValue.booleanValue() ? RuleGeneratorHelper.AE_TRUE : RuleGeneratorHelper.AE_NIL) :
							((mappedValue.booleanValue() ? "Y" : "N")));
						if (writeAsString) {
							print("\"" + valueToPrint + "\"");
						}
						else {
							print(valueToPrint);
						}
					}
				}
				else {
					if (writeAsString) {
						print("\"" + valueStr + "\"");
					}
					else {
						print(valueStr);
					}
				}
				if (useParen) closeParan();
				return true;
			}
			else {
				return doNextLine;
			}
		}
		catch (RuleGenerationException ex) {
			throw ex;
		}
		catch (Exception ex) {
			reportError("Failed to write cell value (i,j)=(" + (row + 1) + "," + (col + 1) + "): " + ex.getMessage());
			throw new RuleGenerationException("Failed to get cell value (i,j)=(" + (row + 1) + "," + (col + 1) + "): " + ex.getMessage());
		}
	}

	private void writeGenericCategoryAttributes(ParameterContextConfiguration paramContextConfig, ParameterGrid grid, String className, boolean useParen,
			DateSynonym effDate) throws RuleGenerationException {
		GenericEntityType[] types = grid.getGenericCategoryEntityTypesInUse();
		for (int typei = 0; typei < types.length; typei++) {
			int[] catIDs = grid.getGenericCategoryIDs(types[typei]);
			GenericEntityIdentity[] entities = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(
					types[typei].getCategoryType(),
					catIDs,
					(effDate == null ? new Date() : effDate.getDate()),
					false); // false indicates we care about dates in entity-category associations
			if (entities != null && entities.length > 0) {
				if (useParen) openParan();
				String attrName = paramContextConfig.findAttributeName(types[typei].toString(), types[typei].toString() + "Context");
				print(getMappedAttributeDeployLabel(className, attrName));
				print(" ");
				print("(create$ ");				
				// QN 2007-08-15 always print entity ID to be in synch with the LHS context.
				for (int k = 0; k < entities.length; k++) {
					writeGenericEntityPattern(types[typei], entities[k].getEntityID());
					print(" ");
				}
				print(")");
				if (useParen) closeParan();
				nextLine();
			}
		}
	}

	private void writeGenericEntityAttributes(ParameterContextConfiguration paramContextConfig, ParameterGrid grid, String className, boolean useParen)
			throws RuleGenerationException {
		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		for (int typei = 0; typei < types.length; typei++) {
			int[] entityIDs = grid.getGenericEntityIDs(types[typei]);
			if (entityIDs != null && entityIDs.length > 0) {
				if (useParen) openParan();
				String attrName = paramContextConfig.findAttributeName(types[typei].toString(), types[typei].toString() + "Context");
				print(getMappedAttributeDeployLabel(className, attrName));
				print(" ");
				print("(create$ ");
				// QN 2007-08-15 always print entity ID to be in synch with the LHS context.
				//boolean writeAsString = DomainManager.getInstance().isStringDeployType(className, attrName);						
				for (int k = 0; k < entityIDs.length; k++) {
					//writeGenericEntityID(paramContextConfig, types[typei], entityIDs[k], writeAsString);
					writeGenericEntityPattern(types[typei], entityIDs[k]);
					print(" ");
				}
				print(")");
				if (useParen) closeParan();
				nextLine();
			}
		}
	}

	final void writeGenericEntityID(ParameterContextConfiguration paramConfig, GenericEntityType type, int entityID, boolean writeAsString) throws RuleGenerationException {
		logger.debug("writeGenericEntityID(" + type + "," + entityID + ")");
		GenericEntity entity = EntityManager.getInstance().getEntity(type, entityID);
		if (entity != null) {			
			print(getIDValueForParameter(paramConfig, type.toString(), EntityManager.getInstance().getEntity(type, entityID), writeAsString));
		}
		else {
			print("ERROR-" + type + "-" + entityID + "-not-found");
			reportError("No entity of type " + type + " with id " + entityID + " exists");
		}
	}

	protected String getErrorContext() {
		if (currTemplate != null) {
			StringBuffer errorBuff = new StringBuffer("Parameter:");
			errorBuff.append(currTemplate.getID());
			errorBuff.append(": ");
			errorBuff.append(currTemplate.getName());
			if (currGrid != null) {
				errorBuff.append(",act=");
				errorBuff.append(RuleGeneratorHelper.toRuleDateTimeString(currGrid.getSunrise()));
				errorBuff.append("-");
				if (currGrid.getSunset() != null) {
					errorBuff.append(RuleGeneratorHelper.toRuleDateTimeString(currGrid.getSunset()));
				}
				errorBuff.append(",row=");
				errorBuff.append(currRow);
			}
			else {
				errorBuff.append("UNKNOWN");
			}
			return errorBuff.toString();
		}
		else {
			return null;
		}
	}

	protected PrintWriter getPrintWriter(String status, OutputController outputController) throws RuleGenerationException {
		return outputController.getParameterWriter(status);
	}

	public synchronized void writeAll() throws RuleGenerationException {
		super.writeAll();
		getOutputController().closeParameterWriters();
	}

}