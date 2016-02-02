package com.mindbox.pe.server.generator.processor;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.common.format.CurrencyFormatter;
import com.mindbox.pe.common.format.FloatFormatter;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.FloatDomainAttribute;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.table.BooleanDataHelper;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.AbstractGenerateParms;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.aemodel.AeInstanceAttrValue;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.CellValueLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnMessagesLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnNumberList;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.FreeText;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeSequence;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeToken;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Reference;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.RuleNameLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.SingleFreeChar;
import com.mindbox.pe.server.parser.jtb.message.visitor.ObjectDepthFirst;


/**
 * Message Processor.
 * @author Geneho
 * @since PowerEditor 3.3.0
 */
public class MessageProcessor extends ObjectDepthFirst {


	/**
	 * Writes a value for message.
	 * @param gridColumn
	 * @param obj the value to write
	 * @param buffer the target buffer
	 * @param msgConfig the message configuration
	 * @param defaultWriteValue default value to write
	 */
	public static void writeVal(AbstractTemplateColumn gridColumn, Object obj, StringBuffer buffer, MessageConfiguration msgConfig,
			String defaultWriteValue) {
		if (obj == null) {
			// handle boolean
			if (gridColumn.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
				buffer.append(BooleanDataHelper.toStringValue((Boolean) null, gridColumn.getColumnDataSpecDigest().isBlankAllowed()));
			}
			else {
				buffer.append(defaultWriteValue);
			}
			return;
		}

		Logger.getLogger(MessageProcessor.class).debug(
				"... writeVal: " + obj + " (" + obj.getClass().getName() + ')' + ", col=" + gridColumn);

		if (obj instanceof IRange) {
			writeRange((IRange) obj, buffer, gridColumn.getColumnDataSpecDigest(), msgConfig);
		}
		else if (obj instanceof CategoryOrEntityValue) {
			writeCategoryOrEntityValue(buffer, (CategoryOrEntityValue) obj);
		}
		else if (obj instanceof CategoryOrEntityValues) {
			writeCategoryOrEntityValues(msgConfig, buffer, (CategoryOrEntityValues) obj);
		}
		else if (EnumValues.class.isInstance(obj)) {
			writeEnum(gridColumn, (EnumValues<?>) obj, buffer, msgConfig);
		}
		else if (obj instanceof Integer) {
			long longVal = ((Integer) obj).longValue();
			buffer.append(NumberFormat.getNumberInstance().format(longVal));
		}
		else if (obj instanceof Float || obj instanceof Double) {
			writeDouble(buffer, (Number) obj, gridColumn.getColumnDataSpecDigest());
		}
		else if (obj instanceof DynamicStringValue) {
			buffer.append(obj.toString());
		}
		else if (obj instanceof Date) {
			writeDate(buffer, (Date) obj);
		}
		else {
			if (gridColumn.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)
					&& gridColumn.getColumnDataSpecDigest().getMappedAttribute() != null) {

				// TODO Kim: E.S. -- handle enums from column & data source

				DomainAttribute attribute = DomainManager.getInstance().getDomainAttribute(
						gridColumn.getMAClassName(),
						gridColumn.getMAAttributeName());
				if (attribute != null) {
					String value = DeploymentManager.getInstance().getEnumDisplayValue(
							gridColumn.getMAClassName(),
							gridColumn.getMAAttributeName(),
							obj.toString(),
							false);
					buffer.append((value == null ? "ERROR-DisplayValue-Not-Found-For-" + obj.toString() : value));
					return;
				}
			}
			buffer.append((obj == null ? defaultWriteValue : obj.toString()));
		}
	}

	private static void writeDate(StringBuffer buffer, Date date) {
		buffer.append(ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateFormat().format(date));
	}

	private static void writeDouble(StringBuffer buffer, Number n, ColumnDataSpecDigest dataSpec) {
		FloatFormatter formatter = ColumnDataSpecDigest.TYPE_CURRENCY.equals(dataSpec.getType()) ? new CurrencyFormatter(
				dataSpec.getPrecision()) : new FloatFormatter(dataSpec.getPrecision());
		buffer.append(formatter.format(n));
	}

	public static void writeRange(IRange range, StringBuffer buffer, ColumnDataSpecDigest colDataSpec, MessageConfiguration msgConfig) {
		String[] valueStrs = getRangeValueStrings(range, colDataSpec);
		String floor = valueStrs[0];
		String ceiling = valueStrs[1];

		// cannot use Number.equals as the value class may not match (results in ClassCastException)
		boolean isEqual = range.getFloor() != null && range.getCeiling() != null
				&& Double.doubleToLongBits(range.getFloor().doubleValue()) == Double.doubleToLongBits(range.getCeiling().doubleValue());

		// bracketed range messages, e.g. "(0-100]"
		if (msgConfig.isRangeStyleBracketed()) {
			if (isEqual)
				buffer.append(floor);
			else if (range.getFloor() != null && range.getCeiling() != null
					&& range.getFloor().doubleValue() == range.getCeiling().doubleValue()) {
				buffer.append("= ");
				buffer.append(ceiling);
			}
			else {
				if (range.getFloor() != null) {
					if (range.isLowerValueInclusive())
						buffer.append("[");
					else
						buffer.append("(");
					buffer.append(floor);
				}

				buffer.append("-");

				if (range.getCeiling() != null) {
					buffer.append(ceiling);
					if (range.isUpperValueInclusive())
						buffer.append("]");
					else
						buffer.append(")");

				}
			}
		}
		// symbolic range messages (e.g. >=500, <600)
		else if (msgConfig.isRangeStyleSymbolic()) {
			if (isEqual) {
				buffer.append("=");
				buffer.append(floor);
			}
			else if (range.getFloor() != null && range.getCeiling() != null
					&& range.getFloor().doubleValue() == range.getCeiling().doubleValue()) {
				buffer.append("=");
				buffer.append(ceiling);

			}
			else {
				if (range.getFloor() != null) {
					buffer.append(">");
					if (range.isLowerValueInclusive()) {
						buffer.append("=");
					}
					buffer.append(floor);

					if (range.getCeiling() != null) {
						buffer.append(",");
					}
				}

				if (range.getCeiling() != null) {
					buffer.append("<");
					if (range.isUpperValueInclusive()) {
						buffer.append("=");
					}
					buffer.append(ceiling);
				}
			}
		}
		// verbose range messages (e.g. greater than or equal to zero)
		else {
			if (isEqual) {
				buffer.append("equal to ");
				buffer.append(ceiling);
			}
			else {
				if (range.getFloor() != null) {
					buffer.append("greater than ");
					if (range.isLowerValueInclusive()) {
						buffer.append("or equal to ");
					}
					buffer.append(floor);

					if (range.getCeiling() != null) {
						buffer.append(" and ");
					}
				}

				if (range.getCeiling() != null) {
					buffer.append("less than ");
					if (range.isUpperValueInclusive()) {
						buffer.append("or equal to ");
					}
					buffer.append(ceiling);
				}
			}
		}
	}

	private static String[] getRangeValueStrings(IRange range, ColumnDataSpecDigest colDataSpec) {
		String[] valStrs = new String[2];
		if (range instanceof DateRange) {
			DateRange dateRange = (DateRange) range;
			DateFormat formatter = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat();

			if (dateRange.getMinDate() != null) {
				valStrs[0] = formatter.format(dateRange.getMinDate());
			}
			if (dateRange.getMaxDate() != null) {
				valStrs[1] = formatter.format(dateRange.getMaxDate());
			}
		}
		else if (range instanceof FloatRange) {
			FloatFormatter formatter = ColumnDataSpecDigest.TYPE_CURRENCY_RANGE.equals(colDataSpec.getType()) ? new CurrencyFormatter(
					colDataSpec.getPrecision()) : new FloatFormatter(colDataSpec.getPrecision());

			if (range.getFloor() != null) {
				valStrs[0] = formatter.format(range.getFloor());
			}
			if (range.getCeiling() != null) {
				valStrs[1] = formatter.format(range.getCeiling());
			}
		}
		else {
			if (range.getFloor() != null) {
				valStrs[0] = range.getFloor().toString();
			}
			if (range.getCeiling() != null) {
				valStrs[1] = range.getCeiling().toString();
			}
		}
		return valStrs;
	}

	public static void writeEnum(AbstractTemplateColumn column, EnumValues<?> enumvalues, StringBuffer buffer,
			MessageConfiguration msgConfig) {
		boolean isExclusion = enumvalues.isSelectionExclusion();

		int numEnums = enumvalues.size();
		boolean isMultiSelect = numEnums > 1;

		// write cell prefix (e.g. not, or not any of, etc)
		String enumPrefix = msgConfig.getEnumPrefix(isExclusion, isMultiSelect);
		if (UtilBase.isEmpty(enumPrefix) && isExclusion) {
			enumPrefix = "not ";
		}
		buffer.append(enumPrefix); // was "not "

		for (int i = 0; i < numEnums; i++) {
			// write the enumerated value
			Object ev = enumvalues.get(i);
			String enumValue;
			if (ev instanceof EnumValue) {
				enumValue = ((EnumValue) ev).getDisplayLabel();
			}
			else {
				String dispValue = enumvalues.getEnumValueAsString(i);
				enumValue = DeploymentManager.getInstance().getEnumDisplayValue(
						column.getMAClassName(),
						column.getMAAttributeName(),
						dispValue,
						false);
			}
			buffer.append(enumValue);
			if (i == numEnums - 2) {
				// write the text prior to the last item
				buffer.append(msgConfig.getEnumFinalDelimiter(isExclusion, isMultiSelect));
			}
			else if (i != numEnums - 1) {
				// write text, if its not AFTER the last item
				buffer.append(msgConfig.getEnumDelimiter(isExclusion, isMultiSelect));
			}
		}
	}

	public static void writeCategoryOrEntityValue(StringBuffer buffer, CategoryOrEntityValue value) {
		if (value != null) {
			if (value.isForEntity()) {
				GenericEntity entity = EntityManager.getInstance().getEntity(value.getEntityType(), value.getId());
				if (entity == null) {
					buffer.append("***ERROR: Entity of type " + value.getEntityType() + " with id " + value.getId() + " not found***");
				}
				else {
					buffer.append(entity.getName());
				}
			}
			else {
				GenericCategory category = EntityManager.getInstance().getGenericCategory(
						value.getEntityType().getCategoryType(),
						value.getId());
				if (category == null) {
					buffer.append("***ERROR: Category of type " + value.getEntityType() + " with id " + value.getId() + " not found***");
				}
				else {
					buffer.append(category.getName());
				}
			}
		}
	}

	public static void writeCategoryOrEntityValues(MessageConfiguration msgConfig, StringBuffer buffer, CategoryOrEntityValues value) {
		if (value != null) {
			boolean isExclusion = value.isSelectionExclusion();
			boolean isMultiSelect = value.size() > 1;
			int numEnums = value.size();

			// write cell prefix (e.g. not, or not any of, etc)
			String enumPrefix = msgConfig.getEnumPrefix(isExclusion, isMultiSelect);
			if (UtilBase.isEmpty(enumPrefix) && isExclusion) {
				enumPrefix = "not ";
			}
			buffer.append(enumPrefix); // was "not "
			for (int i = 0; i < numEnums; i++) {
				Object obj = value.get(i);
				if (obj instanceof CategoryOrEntityValue) {
					writeCategoryOrEntityValue(buffer, (CategoryOrEntityValue) obj);
				}
				else {
					buffer.append(obj);
				}
				if (i == numEnums - 2) {
					// write the text prior to the last item
					buffer.append(msgConfig.getEnumFinalDelimiter(isExclusion, isMultiSelect));
				}
				else if (i != numEnums - 1) {
					// write text, if its not AFTER the last item
					buffer.append(msgConfig.getEnumDelimiter(isExclusion, isMultiSelect));
				}
			}
		}
	}

	private final List<String> argList = new LinkedList<String>();
	private final List<AeInstanceAttrValue> emptyPatternList = new ArrayList<AeInstanceAttrValue>();
	private final Logger logger = Logger.getLogger(getClass());
	private final boolean replaceColumnReference;
	private StringBuffer buff = null;
	private int cellValueOverride = -1;
	private MessageConfiguration messageConfiguration;
	private TemplateMessageDigest messageDigest;

	/**
	 * Equivalent to <code>new MessageProcessor(true)</code>.
	 *
	 */
	public MessageProcessor() {
		this(true);
	}

	/**
	 *
	 * @param replaceColumnReference
	 * @since PowerEditor 4.1.1
	 */
	public MessageProcessor(boolean replaceColumnReference) {
		this.replaceColumnReference = replaceColumnReference;
		reset_internal();
	}

	/**
	 * Resets the internal state for processing a new message.
	 */
	synchronized void reset() {
		reset_internal();
	}

	private void reset_internal() {
		buff = new StringBuffer();
		argList.clear();
		emptyPatternList.clear();
		cellValueOverride = -1;
		messageConfiguration = null;
		messageDigest = null;
	}

	/**
	 * Builds the message output for the specified message object and the generateParams.
	 * @param messageObj the message object
	 * @param generateParams generate params
	 * @return the string message output
	 */
	public synchronized String process(Message messageObj, AbstractGenerateParms generateParams, TemplateMessageDigest messageDigest) {
		if (messageObj == null) return "\"\"";
		this.messageDigest = messageDigest;
		String messageStr = process_internal(messageObj, generateParams/*, ruleWriter*/);

		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append("(");
		resultBuffer.append(ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageFormatConversionFunction());
		resultBuffer.append(" \"");
		resultBuffer.append(messageStr);
		resultBuffer.append("\"");

		for (Iterator<String> iter = argList.iterator(); iter.hasNext();) {
			String element = iter.next();
			resultBuffer.append(" ");
			resultBuffer.append(element);
		}

		resultBuffer.append(")");
		return resultBuffer.toString();
	}

	/**
	 * This does not generate the finalized form (that is, without the sprint function call).
	 */
	private synchronized String process_internal(Message messageObj, AbstractGenerateParms generateParams) {
		messageObj.accept(this, generateParams);

		return RuleGeneratorHelper.formatForSprintf(buff.toString());
	}

	public synchronized AeInstanceAttrValue[] getEmptyPatternAttributes() {
		return (AeInstanceAttrValue[]) emptyPatternList.toArray(new AeInstanceAttrValue[0]);
	}

	public Object visit(CellValueLiteral n, Object argu) {
		if (!(argu instanceof AbstractGenerateParms)) return null;

		int colToUse = (cellValueOverride == -1 ? ((AbstractGenerateParms) argu).getColumnNum() : cellValueOverride);
		handleColumnReference(colToUse, (AbstractGenerateParms) argu);

		return null;
	}

	public Object visit(ColumnLiteral n, Object argu) {
		if (!(argu instanceof AbstractGenerateParms)) return null;

		int column = Integer.parseInt(n.f1.tokenImage);

		handleColumnReference(column, (AbstractGenerateParms) argu);
		return null;
	}

	private void handleColumnReference(int column, AbstractGenerateParms params) {
		Object value = params.getColumnValue(column);
		if (value instanceof DynamicStringValue) {
			handleDynamicStringValue((DynamicStringValue) value);
		}
		else {
			writeVal(params.getTemplate().getColumn(column), value, buff, getMessageConfigurationToUse(params), RuleGeneratorHelper.AE_NIL);
		}
	}

	private void handleDynamicStringValue(DynamicStringValue dsValue) {
		if (dsValue == null) return;
		String[] values = dsValue.getDeployValues();
		if (values == null || values.length == 0) {
			buff.append((dsValue == null ? RuleGeneratorHelper.AE_NIL : "\"" + dsValue.toString() + "\""));
		}
		else {
			String messageStr = RuleGeneratorHelper.formatForSprintf(values.length > 0 ? values[0] : dsValue.toString());
			buff.append(messageStr);
			for (int i = 1; i < values.length; i++) {
				argList.add(values[i]);
			}
		}

	}

	public Object visit(NodeToken n, Object argu) {
		// this print <INTEGER_LITERAL> Word
		buff.append(n.tokenImage);
		return null;
	}

	private MessageConfiguration getMessageConfigurationToUse(AbstractGenerateParms parms) {
		return (this.messageConfiguration == null ? parms.getMessageConfiguration() : messageConfiguration);
	}

	public Object visit(RuleNameLiteral n, Object argu) {
		if (!(argu instanceof AbstractGenerateParms)) return null;

		buff.append(((AbstractGenerateParms) argu).getName());
		return null;
	}

	private String getConditionalDelimiterToUse(AbstractGenerateParms generateParams) {
		String currentValue = null;
		if (messageDigest != null) {
			currentValue = messageDigest.getConditionalDelimiter();
		}
		if (UtilBase.isEmpty(currentValue)) {
			currentValue = generateParams.getMessageConfiguration().getConditionalDelimiter();
		}
		return currentValue;
	}

	private String getConditionalFinalDelimiterTouse(AbstractGenerateParms generateParams) {
		String currentValue = null;
		if (messageDigest != null) {
			currentValue = messageDigest.getConditionalFinalDelimiter();
		}
		if (UtilBase.isEmpty(currentValue)) {
			currentValue = generateParams.getMessageConfiguration().getConditionalFinalDelimiter();
		}
		return currentValue;
	}

	@SuppressWarnings("unchecked")
	public Object visit(ColumnMessagesLiteral n, Object argu) {
		if (!(argu instanceof AbstractGenerateParms)) return null;

		AbstractGenerateParms generateParams = (AbstractGenerateParms) argu;
		logger.debug(">>> visit(ColumnMessageLiteral) for " + argu);

		List<Integer> intList = (List<Integer>) n.f2.accept(this, generateParams);
		int intArray[] = Util.toIntArray(intList);
		logger.debug("    visit(ColumnMessageLiteral): intArray = " + UtilBase.toString(intArray));

		if (intArray.length == 0) return null;

		MessageProcessor columnMessageProcessor = new MessageProcessor(this.replaceColumnReference);

		// for each column, put in its message and the delimeter
		GridTemplate template = generateParams.getTemplate();
		String conditionalDelimiter = getConditionalDelimiterToUse(generateParams);
		if (conditionalDelimiter == null) conditionalDelimiter = MessageConfiguration.DEFAULT_CONDITIONAL_DELIMITER;
		String conditionalFinalDelimiter = getConditionalFinalDelimiterTouse(generateParams);
		if (conditionalFinalDelimiter == null) conditionalFinalDelimiter = MessageConfiguration.DEFAULT_CONDITIONAL_FINAL_DELIMITER;
		for (int i = 0; i < intArray.length; i++) {
			if (i > 0 && i < intArray.length - 1) {
				buff.append(conditionalDelimiter);
			}
			else if (i == intArray.length - 1 && i != 0) {
				buff.append(conditionalFinalDelimiter);
			}

			int colNum = intArray[i];

			logger.debug("    visit(ColumnMessageLiteral): column no=" + colNum);

			GridTemplateColumn gridColumn = (GridTemplateColumn) template.getColumn(colNum);
			Object cellValue = generateParams.getColumnValue(colNum);
			logger.debug("    visit(ColumnMessageLiteral): cell value=" + cellValue);

			ColumnMessageFragmentDigest cmfd = gridColumn.getMessageFragmentDigest(cellValue);
			Message parsedMessage = gridColumn.getParsedMessage(cellValue);
			if (parsedMessage == null || cmfd == null) {
				logger.debug("    visit(ColumnMessageLiteral): no column message found - adding column value: "
						+ generateParams.getColumnValue(colNum));
				writeVal(gridColumn, cellValue, buff, generateParams.getMessageConfiguration(), RuleGeneratorHelper.AE_NIL);
			}
			else {
				columnMessageProcessor.reset();
				columnMessageProcessor.cellValueOverride = colNum;
				columnMessageProcessor.messageConfiguration = gridColumn.getMessageConfiguration();
				String columnMessageStr = columnMessageProcessor.process_internal(parsedMessage, generateParams);

				logger.debug("    visit(ColumnMessageLiteral): column message processed = " + columnMessageStr);
				buff.append(columnMessageStr);
				this.argList.addAll(columnMessageProcessor.argList);
			}
		}
		logger.debug("<<< visit(ColumnMessageLiteral)");
		return null;
	}

	public Object visit(ColumnNumberList n, Object argu) {
		if (!(argu instanceof AbstractGenerateParms)) return null;
		AbstractGenerateParms generateParams = (AbstractGenerateParms) argu;

		List<Integer> list = new LinkedList<Integer>();
		Integer columnNo = Integer.valueOf(n.f0.tokenImage);
		if (RuleGeneratorHelper.hasValue(generateParams.getColumnValue(columnNo.intValue()), generateParams)) {
			list.add(columnNo);
		}
		if (n.f1.present()) {
			for (int i = 0; i < n.f1.size(); i++) {
				NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
				if (seq.elementAt(1) != null) {
					columnNo = Integer.valueOf(seq.elementAt(1).toString());
					if (RuleGeneratorHelper.hasValue(generateParams.getColumnValue(columnNo.intValue()), generateParams)) {
						list.add(columnNo);
					}
				}
			}
		}
		return list;
	}

	public Object visit(Reference domainAttributeReference, Object argu) {
		if (argu instanceof AbstractGenerateParms && !UtilBase.isEmpty(domainAttributeReference.f1.tokenImage)) {
			String[] nameParts = domainAttributeReference.f1.tokenImage.split("\\."); // n.f1 = domainClass.attributeName
			String domainClass = nameParts[0];
			String attributeName = nameParts[1];
			DomainAttribute attr = DomainManager.getInstance().getDomainAttribute(domainClass, attributeName);

			if (attr instanceof FloatDomainAttribute) {
				buff.append("%." + ((FloatDomainAttribute) attr).getPrecision() + "f");
				argList.add(AeMapper.getGuidelineInstance().generateAEVariable(attributeName, true));
			}
			else {
				if (attr.getDeployType() == DeployType.DATE) {
					buff.append("%s");
					argList.add("(format-julian-date " + AeMapper.getGuidelineInstance().generateAEVariable(attributeName, true) + " \""
							+ ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateFormatAe() + "\")");
				}
				else {
					buff.append("%a");
					argList.add(AeMapper.getGuidelineInstance().generateAEVariable(attributeName, true));
				}
			}
		}
		return null;
	}

	public Object visit(FreeText n, Object argu) {
		buff.append(n.f0.tokenImage);
		return null;
	}

	public Object visit(SingleFreeChar n, Object argu) {
		buff.append(((NodeToken) n.f0.choice).tokenImage);
		return null;
	}
}