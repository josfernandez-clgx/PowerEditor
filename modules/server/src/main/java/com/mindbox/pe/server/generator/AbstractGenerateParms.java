package com.mindbox.pe.server.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.table.BooleanDataHelper;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.MessageContainer;
import com.mindbox.pe.model.template.RuleContainer;
import com.mindbox.pe.model.template.TemplateMessageDigest;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.rule.ColumnReferencePatternValueSlot;
import com.mindbox.pe.server.generator.rule.ValueSlot;


/**
 * Abstract Rule Generation parameter container.
 * @author Geneho Kim
 * @since PowerEditor 1.0
 */
public abstract class AbstractGenerateParms implements GenerationParams {

	private String name;
	private String description;
	private int columnNo = -1;
	private final GridTemplate gridTemplate;
	private final String status;
	private final int rowNumber;
	private final List<Object> rowData;
	private FunctionParameter[] parameters;
	private int actionTypeID;
	private final int id;
	private DateSynonym sunrise, sunset;

	public AbstractGenerateParms(int id, DateSynonym sunrise, DateSynonym sunset, GridTemplate gridtemplate, int columnNo, int rowNum, List<Object> rowDataList, String status) {
		if (gridtemplate == null) {
			throw new IllegalArgumentException("template cannot be null");
		}
		if (status == null) {
			throw new IllegalArgumentException("status cannot be null");
		}
		this.id = id;
		this.status = status;
		this.gridTemplate = gridtemplate;
		setColumnNo_internal(columnNo);
		this.rowNumber = rowNum;
		this.rowData = new ArrayList<Object>();
		if (rowDataList != null) {
			this.rowData.addAll(rowDataList);
		}
		this.sunrise = sunrise;
		this.sunset = sunset;

		resetColumnSpecificInvariants();
	}

	/**
	 * Gets the template message digest for the specified message context entity id.
	 * @param entityID message context entity id
	 * @return an instance of {@link TemplateMessageDigest}, if found; <code>null</code>, otherwise
	 */
	public TemplateMessageDigest findTemplateMessageDigest(int entityID) {
		MessageContainer messageContainer = getMessageContainerInScope();
		if (messageContainer != null) {
			TemplateMessageDigest digest = messageContainer.findMessageForEntity(entityID);
			if (digest != null) {
				return digest;
			}
		}
		return null;
	}

	public final int getActionTypeID() {
		return actionTypeID;
	}

	public final Object getCellValue() {
		if (getColumnNum() > 0)
			return getColumnValue(getColumnNum());
		else
			return null;
	}

	@Override
	public final int getColumnNum() {
		return columnNo;
	}

	public final Object getColumnValue(int i) {
		if (i > getRowData().size()) {
			return null;
		}
		return getRowData().get(i - 1);
	}

	public String getDescription() {
		return description;
	}

	@Override
	public final int getID() {
		return id;
	}

	public final MessageConfiguration getMessageConfiguration() {
		MessageConfiguration config = null;
		if (getColumnNum() > 0) {
			if (ConfigurationManager.getInstance().getRuleGenerationConfigHelper(this.getTemplate().getColumn(getColumnNum()).getUsageType()) != null) {
				config = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(this.getTemplate().getColumn(getColumnNum()).getUsageType()).getMessageConfig();
			}
		}
		return (config == null ? ConfigurationManager.getInstance().getRuleGenerationConfigHelper(this.getTemplate().getUsageType()).getMessageConfig() : config);
	}

	private final MessageContainer getMessageContainerInScope() {
		MessageContainer messageContainer = null;
		if (columnNo < 0) {
			messageContainer = gridTemplate;
		}
		else {
			messageContainer = (MessageContainer) gridTemplate.getColumn(columnNo);
		}
		return messageContainer;
	}

	/**
	 * @return Returns the name.
	 */
	public final String getName() {
		return name;
	}

	public final int getNumberOfFunctionParameters() {
		return (parameters == null ? 0 : parameters.length);
	}

	public final FunctionParameter getParameterAt(int paramNo) {
		if (paramNo >= 1 && paramNo <= parameters.length) {
			return parameters[paramNo - 1];
		}
		else {
			return null;
		}
	}

	@Override
	public Integer getPrecisionAsInteger(int columnNo) {
		return (gridTemplate == null ? null : gridTemplate.getColumn(columnNo).getColumnDataSpecDigest().getPercisionAsInteger());
	}

	@Override
	public final List<Object> getRowData() {
		return Collections.unmodifiableList(rowData);
	}

	@Override
	public final int getRowNum() {
		return rowNumber;
	}

	public final RuleContainer getRuleContainerInScope() {
		return getRuleContainerInScope_internal();
	}

	private RuleContainer getRuleContainerInScope_internal() {
		RuleContainer container = null;
		if (columnNo < 0) {
			container = gridTemplate;
		}
		else {
			container = (RuleContainer) gridTemplate.getColumn(columnNo);
		}
		return container;
	}

	public final List<Long> getRuleIDs() {
		List<Long> ruleIDList = new LinkedList<Long>();
		if (gridTemplate.hasRuleIDColumn()) {
			List<String> columnNames = gridTemplate.getRuleIDColumnNames();
			for (String columnName : columnNames) {
				int columnNo = gridTemplate.getColumn(columnName).getID();
				Object cellValue = getColumnValue(columnNo);
				if (cellValue != null) {
					if (cellValue instanceof Number) {
						ruleIDList.add(new Long(((Number) cellValue).longValue()));
					}
					else if (cellValue instanceof String) {
						try {
							ruleIDList.add(Long.valueOf((String) cellValue));
						}
						catch (NumberFormatException ex) {
							// ignore; don't add to the list
						}
					}
				}
			}
		}
		return ruleIDList;
	}

	@Override
	public final String getStatus() {
		return status;
	}

	@Override
	public final DateSynonym getSunrise() {
		return sunrise;
	}

	@Override
	public final DateSynonym getSunset() {
		return sunset;
	}

	@Override
	public final GridTemplate getTemplate() {
		return gridTemplate;
	}

	@Override
	public abstract TemplateUsageType getUsage();

	public boolean hasEntitySpecificMessage() {
		return getMessageContainerInScope().hasEntitySpecificMessage();
	}

	public final boolean isEmptyRow() {
		if (rowData.isEmpty()) return true;
		for (Object cellValue : rowData) {
			if (!UtilBase.isEmptyCellValue(cellValue)) {
				return false;
			}
		}
		return true;
	}

	public final boolean isEmptyValue(ValueSlot value) {
		if (value == null || value.toString() == null || value.toString().length() == 0) return true;
		if (value instanceof ColumnReferencePatternValueSlot) {
			Object valueObj = getColumnValue(((ColumnReferencePatternValueSlot) value).getColumnNo());
			AbstractTemplateColumn column = gridTemplate.getColumn(((ColumnReferencePatternValueSlot) value).getColumnNo());
			if (column != null && column.getColumnDataSpecDigest().getType().equalsIgnoreCase(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
				if (column.getColumnDataSpecDigest().isBlankAllowed()) {
					return valueObj == null || valueObj.toString().trim().length() == 0 || valueObj.equals(BooleanDataHelper.ANY_VALUE);
				}
				return false;
			}
			else if (valueObj == null || valueObj.toString() == null || valueObj.toString().trim().length() == 0) {
				return true;
			}
			else {
				if (valueObj instanceof IRange) {
					return ((IRange) valueObj).isEmpty();
				}
				else if (valueObj instanceof EnumValues<?>) {
					return ((EnumValues<?>) valueObj).isEmpty();
				}
				else {
					return false;
				}
			}
		}
		else {
			return false;
		}
	}

	private void resetColumnSpecificInvariants() {
		RuleDefinition ruleDef = null;
		RuleContainer container = getRuleContainerInScope_internal();
		if (container != null) {
			ruleDef = container.getRuleDefinition();
		}

		if (ruleDef != null) {
			parameters = new FunctionParameter[ruleDef.sizeOfActionParemeters()];
			for (int i = 0; i < parameters.length; i++) {
				parameters[i] = ruleDef.getFunctionParameterAt(i);
			}
			actionTypeID = ruleDef.getActionTypeID();
		}
		else {
			parameters = null;
			actionTypeID = 0;
		}
	}

	private void setColumnNo_internal(int columnNo) {
		final GridTemplateColumn column = gridTemplate.getColumn(columnNo);
		if (columnNo != -1 && column == null) {
			throw new IllegalArgumentException("Invalid columnNo " + columnNo + " for " + gridTemplate.getID());
		}
		this.columnNo = columnNo;
	}

	public final void setColumNumber(int columnNo) {
		setColumnNo_internal(columnNo);
		resetColumnSpecificInvariants();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format(
				"%s[name=%s,rowNum=%d,col=%d,status=%s;sunrise=%s,sunset=%s]",
				getClass().getSimpleName(),
				name,
				getRowNum(),
				columnNo,
				status,
				getSunrise(),
				getSunrise());
	}

}