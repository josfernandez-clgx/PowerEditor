package com.mindbox.pe.server.generator;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.table.BooleanDataHelper;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.model.template.ParameterTemplateColumn;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ParameterContextConfigHelper;
import com.mindbox.pe.server.model.GenericEntityIdentity;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterGenerator implements ErrorContextProvider {

	private static final String DEFAULT_INSTANCE_FUNCTION = "make-instance";

	private static final Logger LOG = Logger.getLogger(ParameterGenerator.class);

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
	static final String getIDValueForParameter(ParameterContextConfigHelper paramContextConfig, String type, GenericEntity genericEntity, boolean writeAsString) {
		if (genericEntity == null) return "Nil";
		StringBuilder buff = new StringBuilder();
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
			Object value = genericEntity.getProperty(keyType);
			buff.append((value == null ? RuleGeneratorHelper.AE_NIL : value));
		}
		if (paramContextConfig.findAttributeIsValueAsString(type)) {
			buff.append('"');
		}
		return buff.toString();
	}

	private ParameterTemplate currTemplate = null;
	private ParameterGrid currGrid = null;
	private int currRow = 0;
	private final BufferedGenerator bufferedGenerator;

	public ParameterGenerator(GenerateStats generateStats, OutputController outputController) throws RuleGenerationException {
		this.bufferedGenerator = new DefaultBufferedGenerator(generateStats, outputController, outputController.getParameterFile(), this);
		currTemplate = null;
		currRow = 0;
	}

	public synchronized void generate(final int percentageAllocation, final GuidelineReportFilter filter) throws RuleGenerationException {
		logDebug(LOG, "--> generate: %d, %s", percentageAllocation, filter);

		final ParameterTemplateManager paramTemplateManager = ParameterTemplateManager.getInstance();
		List<ParameterTemplate> paramTemplates = null;
		if (filter.getParameterTemplateIDs().isEmpty()) {
			paramTemplates = paramTemplateManager.getTemplates();
		}
		else {
			paramTemplates = new ArrayList<ParameterTemplate>();
			for (int templateID : filter.getParameterTemplateIDs()) {
				ParameterTemplate parameterTemplate = paramTemplateManager.getTemplate(templateID);
				if (parameterTemplate == null) {
					throw new RuleGenerationException("No parameter template of id " + templateID + " found!");
				}
				paramTemplates.add(parameterTemplate);
			}
		}

		if (paramTemplates == null || paramTemplates.isEmpty()) {
			logInfo(LOG, "No parameter templates specified!");
			bufferedGenerator.getGenerateStats().addPercentComplete(percentageAllocation);
		}
		else {
			final int totalMetaCount = paramTemplates.size();
			final int basePercentageAllocation = percentageAllocation / totalMetaCount;
			try {
				bufferedGenerator.startGeneration();

				for (ParameterTemplate element : paramTemplates) {
					logInfo(LOG, "generate: processing parameter template: %s", element);
					generate(element, filter);

					bufferedGenerator.writeOut();
					bufferedGenerator.getGenerateStats().addPercentComplete(basePercentageAllocation);
				}
				logInfo(LOG, "generate: all parameter templates are processed");
			}
			catch (RuleGenerationException e) {
				throw e;
			}
			catch (Exception e) {
				LOG.error("Failed to generate parameters from " + getErrorContext(), e);
				throw new RuleGenerationException("Failed to generate parameters:" + e.getMessage());
			}
			finally {
				bufferedGenerator.endGeneration();
				bufferedGenerator.getGenerateStats().addPercentComplete(percentageAllocation - (basePercentageAllocation * totalMetaCount));
			}
		}
	}

	private void generate(ParameterTemplate template, GuidelineReportFilter filter) throws RuleGenerationException, IOException {
		logDebug(LOG, ">>> generate(template): %s", template);
		if (template.getDeployMethod() == ParameterTemplate.DEPLOY_AS_OBJECTS) {
			for (Iterator<ParameterGrid> iter = ParameterManager.getInstance().getGrids(template.getID()).iterator(); iter.hasNext();) {
				ParameterGrid paramGrid = iter.next();
				if (filter.isAcceptable(paramGrid)) {
					generate(template, paramGrid);
				}
				else {
					LOG.info("generate: grid skipped because the filter rejected it: " + paramGrid);
				}
			}
		}
		else {
			String scriptDetails = template.getDeployScriptDetails();
			if (scriptDetails == null || scriptDetails.length() == 0) {
				LOG.warn("generate: parameter template skipped as its script details are not found: " + template.getID());
				bufferedGenerator.reportError("Parameter template " + template.getID() + " not deployed as its script details are not found");
			}
			else {
				try {
					String[] strs = scriptDetails.split(" ");
					for (int i = 0; i < strs.length; i++) {
						if (strs[i].equals("%templateID%")) {
							strs[i] = String.valueOf(template.getID());
						}
					}
					LOG.info("Executing '" + UtilBase.toString(strs) + "' for Parameter Template " + template.getID());
					Runtime.getRuntime().exec(strs);
				}
				catch (Exception ex) {
					LOG.error("Failed to execute " + scriptDetails, ex);
					bufferedGenerator.reportError("Failed to execute " + scriptDetails + " for Parameter Template " + template.getID() + ": " + ex.getMessage());
				}
			}
		}
		LOG.info("<<< generate(template): ");
	}

	private void generate(ParameterTemplate template, ParameterGrid grid) throws RuleGenerationException, IOException {
		LOG.debug(">>> generate(template,grid): " + template + "," + grid);

		this.currTemplate = template;
		this.currGrid = grid;

		final ParameterContextConfigHelper paramContextConfig = ConfigurationManager.getInstance().getParameterContextConfigHelper();

		DateSynonym[] dateSynonymRanges = EntityManager.getInstance().getDateSynonymsForChangesInCategoryToEntityRelationships(grid, grid.getEffectiveDate(), grid.getExpirationDate());
		// Note: dateSynonymRanges may contain null
		for (int i = 0; i < dateSynonymRanges.length - 1; i++) {
			writeInstance(paramContextConfig, template, grid, dateSynonymRanges[i], dateSynonymRanges[i + 1]);
		}
		bufferedGenerator.writeOut();
	}

	@Override
	public String getErrorContext() {
		if (currTemplate != null) {
			StringBuilder errorBuff = new StringBuilder("Parameter:");
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

	private final String getMappedAttributeDeployLabel(String className, String attrName) throws RuleGenerationException {
		DomainClass domainclass = DomainManager.getInstance().getDomainClass(className);
		if (domainclass != null) {
			DomainAttribute domainattribute = domainclass.getDomainAttribute(attrName);
			if (domainattribute != null) {
				return domainattribute.getDeployLabel();
			}
			else {
				bufferedGenerator.reportError("Class " + className + " does not have an attribute named " + attrName);
				throw new RuleGenerationException("Could not locate attrib " + attrName + " for class " + className);
			}
		}
		else {
			bufferedGenerator.reportError("No class with name " + className + " found");
			throw new RuleGenerationException("Could not locate class " + className);
		}
	}

	private final String getMappedClassDeployLabel(String className) throws RuleGenerationException {
		DomainClass domainclass = DomainManager.getInstance().getDomainClass(className);
		if (domainclass != null) {
			return domainclass.getDeployLabel();
		}
		else {
			bufferedGenerator.reportError("No class with name " + className + " found");
			throw new RuleGenerationException("Could not locate class " + className);
		}
	}

	private String getMappedClassName(ParameterTemplate template) throws RuleGenerationException {
		return getMappedClassDeployLabel(template.getColumn(1).getMAClassName());
	}

	private synchronized String getUniqueNum() {
		if (counter > 9999999) counter = 0; // Protect against overflow.
		return "" + (new java.util.Date().getTime()) + counter++;
	}

	private void writeCategoryOrEntityValue(CategoryOrEntityValue value, boolean asString) throws RuleGenerationException {
		LOG.debug("writeValue(): writing CategoryOrEntityValue: " + value);
		if (value == null) {
			bufferedGenerator.print("");
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
			bufferedGenerator.print(valueToPrint);
		}
	}

	private void writeGenericCategoryAttributes(ParameterContextConfigHelper paramContextConfig, ParameterGrid grid, String className, boolean useParen, DateSynonym effDate)
			throws RuleGenerationException {
		GenericEntityType[] types = grid.getGenericCategoryEntityTypesInUse();
		for (int typei = 0; typei < types.length; typei++) {
			int[] catIDs = grid.getGenericCategoryIDs(types[typei]);
			GenericEntityIdentity[] entities = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(
					types[typei].getCategoryType(),
					catIDs,
					(effDate == null ? new Date() : effDate.getDate()),
					false); // false indicates we care about dates in entity-category associations
			if (entities != null && entities.length > 0) {
				if (useParen) {
					bufferedGenerator.openParan();
				}
				String attrName = paramContextConfig.findAttributeName(types[typei].toString(), types[typei].toString() + "Context");
				bufferedGenerator.print(getMappedAttributeDeployLabel(className, attrName));
				bufferedGenerator.print(" ");
				bufferedGenerator.print("(create$ ");
				// QN 2007-08-15 always print entity ID to be in synch with the LHS context.
				for (int k = 0; k < entities.length; k++) {
					writeGenericEntityPattern(types[typei], entities[k].getEntityID());
					bufferedGenerator.print(" ");
				}
				bufferedGenerator.print(")");
				if (useParen) {
					bufferedGenerator.closeParan();
				}
				bufferedGenerator.nextLine();
			}
		}
	}

	private void writeGenericEntityAttributes(ParameterContextConfigHelper paramContextConfig, ParameterGrid grid, String className, boolean useParen) throws RuleGenerationException {
		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		for (int typei = 0; typei < types.length; typei++) {
			int[] entityIDs = grid.getGenericEntityIDs(types[typei]);
			if (entityIDs != null && entityIDs.length > 0) {
				if (useParen) {
					bufferedGenerator.openParan();
				}
				final String attrName = paramContextConfig.findAttributeName(types[typei].toString(), types[typei].toString() + "Context");
				bufferedGenerator.print(getMappedAttributeDeployLabel(className, attrName));
				bufferedGenerator.print(" ");
				bufferedGenerator.print("(create$ ");
				// QN 2007-08-15 always print entity ID to be in synch with the LHS context.
				//boolean writeAsString = DomainManager.getInstance().isStringDeployType(className, attrName);						
				for (int k = 0; k < entityIDs.length; k++) {
					//writeGenericEntityID(paramContextConfig, types[typei], entityIDs[k], writeAsString);
					writeGenericEntityPattern(types[typei], entityIDs[k]);
					bufferedGenerator.print(" ");
				}
				bufferedGenerator.print(")");
				if (useParen) {
					bufferedGenerator.closeParan();
				}
				bufferedGenerator.nextLine();
			}
		}
	}

	final void writeGenericEntityID(ParameterContextConfigHelper paramConfig, GenericEntityType type, int entityID, boolean writeAsString) throws RuleGenerationException {
		LOG.debug("writeGenericEntityID(" + type + "," + entityID + ")");
		GenericEntity entity = EntityManager.getInstance().getEntity(type, entityID);
		if (entity != null) {
			bufferedGenerator.print(getIDValueForParameter(paramConfig, type.toString(), EntityManager.getInstance().getEntity(type, entityID), writeAsString));
		}
		else {
			bufferedGenerator.print("ERROR-" + type + "-" + entityID + "-not-found");
			bufferedGenerator.reportError("No entity of type " + type + " with id " + entityID + " exists");
		}
	}

	/**
	 * @param type
	 * @param entityID
	 * @param usageType the usage type
	 * @since 3.0.0
	 */
	private void writeGenericEntityPattern(GenericEntityType type, int entityID) throws RuleGenerationException {
		LOG.debug("writeGenericEntityPattern(" + type + "," + entityID + ")");
		GenericEntity entity = EntityManager.getInstance().getEntity(type, entityID);
		if (entity != null) {
			bufferedGenerator.print(RuleGeneratorHelper.getGenericEntityIDValue(entity));
		}
		else {
			bufferedGenerator.print("ERROR-" + type + "-" + entityID + "-not-found");
			bufferedGenerator.reportError("No entity of type " + type + " with id " + entityID + " exists");
		}
	}

	private void writeInstance(ParameterContextConfigHelper paramContextConfig, ParameterTemplate template, ParameterGrid grid, DateSynonym startDate, DateSynonym endDate)
			throws RuleGenerationException {
		LOG.debug(">>> writeInstance: " + template + "," + grid);

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
		final String instanceCreateText = ConfigurationManager.getInstance().getObjectGenerationDefault() == null
				? DEFAULT_INSTANCE_FUNCTION
				: UtilBase.isEmpty(ConfigurationManager.getInstance().getObjectGenerationDefault().getInstanceCreateText())
						? DEFAULT_INSTANCE_FUNCTION
						: ConfigurationManager.getInstance().getObjectGenerationDefault().getInstanceCreateText();
		boolean useParen;
		String instanceName;
		if (instanceCreateText.equals(DEFAULT_INSTANCE_FUNCTION)) {
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
			bufferedGenerator.openParan();
			bufferedGenerator.print(instanceCreateText); // either make-instance or define-instance
			if (instanceName.length() > 0) {
				instanceName = "gen-" + getUniqueNum();
			}
			bufferedGenerator.print(" " + instanceName); // if instance-name is "", then nothing will be printed
			// here.
			bufferedGenerator.print(" " + mappedClassName);
			bufferedGenerator.nextLineIndent();

			// write dates
			if (startDate != null) {
				if (useParen) {
					bufferedGenerator.openParan();
				}
				bufferedGenerator.print(actDateAttrName);
				bufferedGenerator.print(" ");
				bufferedGenerator.print(RuleGeneratorHelper.toRuleDateTimeString(startDate));
				if (useParen) {
					bufferedGenerator.closeParan();
				}
				bufferedGenerator.nextLine();
			}
			if (endDate != null) {
				if (useParen) {
					bufferedGenerator.openParan();
				}
				bufferedGenerator.print(expDateAttrName);
				bufferedGenerator.print(" ");
				bufferedGenerator.print(RuleGeneratorHelper.toRuleDateTimeString(endDate));
				if (useParen) {
					bufferedGenerator.closeParan();
				}
				bufferedGenerator.nextLine();
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
					bufferedGenerator.reportError("Failed to get cell value (i,j)=(" + (row + 1) + "," + (col + 1) + "): " + ex.getMessage());
					throw new RuleGenerationException("Failed to get cell value (i,j)=(" + (row + 1) + "," + (col + 1) + "): " + ex.getMessage());
				}
			} // for each column

			bufferedGenerator.nextLineOutdent();
			bufferedGenerator.closeParan();
			bufferedGenerator.nextLine();
			bufferedGenerator.nextLine();

			bufferedGenerator.getGenerateStats().incrementObjectCount();
		} // for each row in the grid
	}

	// TODO Kim, 2007-04-23: refactor to use value classes for each column data type, similar to rule generation
	private boolean writeSingleCell(int row, int col, ParameterTemplateColumn column, String valueStr, ParameterGrid grid, boolean doNextLine, boolean useParen, String className, String colAttrName)
			throws RuleGenerationException {
		try {
			boolean doWriteAttr = !UtilBase.isEmpty(valueStr);
			if (doWriteAttr && column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
				doWriteAttr = BooleanDataHelper.mapToBooleanValue(valueStr, column.getColumnDataSpecDigest().isBlankAllowed()) != null;
			}
			if (doWriteAttr) {
				valueStr = valueStr.trim();
				if (doNextLine) {
					bufferedGenerator.nextLine();
				}

				if (useParen) {
					bufferedGenerator.openParan();
				}
				bufferedGenerator.print(colAttrName);
				bufferedGenerator.print(" ");

				DeployType deployType = DomainManager.getInstance().getDomainAttribute(className, column.getMAAttributeName()).getDeployType();
				boolean writeAsString = (deployType == DeployType.STRING);
				// write value
				if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
					Object obj = grid.getCellValueObject(row + 1, col + 1, null);
					CategoryOrEntityValue categoryOrEntityValue = (obj instanceof CategoryOrEntityValue ? (CategoryOrEntityValue) obj : CategoryOrEntityValue.valueOf(
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
						bufferedGenerator.reportError("DeployValue of " + valueStr + " not found for Class.Attribute " + column.getMappedAttribute());
						mappedValue = "ERROR-DeployValue-Not-Found-" + valueStr;
					}

					bufferedGenerator.print(mappedValue);
				}
				else if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
					String strToUse = (valueStr == null ? null : (valueStr.equalsIgnoreCase("Y") ? BooleanDataHelper.TRUE_VALUE : valueStr));
					if (!BooleanDataHelper.isValidString(strToUse)) {
						throw new RuleGenerationException("Invalid boolean value at row " + (row + 1) + ", column " + (col + 1) + ": " + valueStr);
					}
					Boolean mappedValue = BooleanDataHelper.mapToBooleanValue(strToUse, column.getColumnDataSpecDigest().isBlankAllowed());
					if (mappedValue != null) {
						String valueToPrint = (deployType == DeployType.BOOLEAN
								? (mappedValue.booleanValue() ? RuleGeneratorHelper.AE_TRUE : RuleGeneratorHelper.AE_NIL)
								: ((mappedValue.booleanValue() ? "Y" : "N")));
						if (writeAsString) {
							bufferedGenerator.print("\"" + valueToPrint + "\"");
						}
						else {
							bufferedGenerator.print(valueToPrint);
						}
					}
				}
				else {
					if (writeAsString) {
						bufferedGenerator.print("\"" + valueStr + "\"");
					}
					else {
						bufferedGenerator.print(valueStr);
					}
				}
				if (useParen) {
					bufferedGenerator.closeParan();
				}
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
			bufferedGenerator.reportError("Failed to write cell value (i,j)=(" + (row + 1) + "," + (col + 1) + "): " + ex.getMessage());
			throw new RuleGenerationException("Failed to get cell value (i,j)=(" + (row + 1) + "," + (col + 1) + "): " + ex.getMessage());
		}
	}


}