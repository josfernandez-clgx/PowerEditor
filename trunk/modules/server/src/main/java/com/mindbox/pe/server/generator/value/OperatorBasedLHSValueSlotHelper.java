package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.common.LogUtil.logDebug;

import java.text.MessageFormat;
import java.util.Date;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.BooleanDataHelper;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.TimeRange;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ColumnReferencePatternValueSlot;
import com.mindbox.pe.server.generator.rule.PatternValueSlot;
import com.mindbox.pe.server.generator.rule.StringValuePatternValueSlot;
import com.mindbox.pe.server.model.TimeSlice;

/**
 * This supports both {@link ColumnReferencePatternValueSlot} and {@link StringValuePatternValueSlot}.
 * @author Geneho Kim
 *
 */
class OperatorBasedLHSValueSlotHelper extends AbstractLHSValueSlotHelper {

	private final EmptyOperatorHelper emptyOperatorHelper;
	private final EntityMatchFunctionOperatorHelper entityTestFunctionOperatorHelper;
	private final EqualityOperatorHelper equalityOperatorHelp;
	private final ComparisonOperatorHelper comparisonOperatorHelper;
	private final MembershipOperatorHelper membershipOperatorHelper;
	private final RangeOperatorHelper rangeOperatorHelper;

	OperatorBasedLHSValueSlotHelper(RuleGenerationConfigHelper ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
		emptyOperatorHelper = new EmptyOperatorHelper(ruleGenerationConfiguration);
		entityTestFunctionOperatorHelper = new EntityMatchFunctionOperatorHelper(ruleGenerationConfiguration);
		equalityOperatorHelp = new EqualityOperatorHelper(ruleGenerationConfiguration);
		membershipOperatorHelper = new MembershipOperatorHelper(ruleGenerationConfiguration);
		comparisonOperatorHelper = new ComparisonOperatorHelper(ruleGenerationConfiguration);
		rangeOperatorHelper = new RangeOperatorHelper(ruleGenerationConfiguration);

	}

	@Override
	public ValueAndComment generateValue(PatternValueSlot patternValueSlot, GuidelineGenerateParams generateParams, String attribVarName, TimeSlice[] timeSlices, GenericEntityType messageContextType,
			int messageContextEntityID) throws RuleGenerationException {
		Reference reference = patternValueSlot.getReference();
		int op = patternValueSlot.getOperator();

		// Determine precision for the column from which the value is from; if none found, use null.
		final Integer precision;
		if (ColumnReferencePatternValueSlot.class.isInstance(patternValueSlot)) {
			precision = generateParams.getPrecisionAsInteger(ColumnReferencePatternValueSlot.class.cast(patternValueSlot).getColumnNo());
		}
		else {
			precision = null;
		}

		Object valueObject = getValueObject(patternValueSlot, generateParams, precision);
		boolean asString = DomainManager.getInstance().isStringDeployType(reference);

		if (timeSlices.length > 0) {
			return getOperatorHelper(op).formatForPattern(valueObject, op, attribVarName, asString, reference, timeSlices[0], precision);
		}
		else {
			return new ValueAndComment("ERROR_NO_TIME_SLICE_AVAILABLE");
		}
	}

	private OperatorHelper getOperatorHelper(int op) throws RuleGenerationException {
		switch (op) {
		case Condition.OP_ENTITY_MATCH_FUNC:
		case Condition.OP_NOT_ENTITY_MATCH_FUNC:
			return entityTestFunctionOperatorHelper;
		case Condition.OP_EQUAL:
		case Condition.OP_NOT_EQUAL:
			return equalityOperatorHelp;
		case Condition.OP_IN:
		case Condition.OP_NOT_IN:
			return membershipOperatorHelper;
		case Condition.OP_BETWEEN:
		case Condition.OP_NOT_BETWEEN:
			return rangeOperatorHelper;
		case Condition.OP_GREATER:
		case Condition.OP_GREATER_EQUAL:
		case Condition.OP_LESS:
		case Condition.OP_LESS_EQUAL:
			return comparisonOperatorHelper;
		case Condition.OP_IS_EMPTY:
		case Condition.OP_IS_NOT_EMPTY:
			return emptyOperatorHelper;
		default:
			throw new RuleGenerationException("Invalid operator " + op);
		}
	}

	private Object getValueObject(ColumnReferencePatternValueSlot valueSlot, GuidelineGenerateParams generateParams) throws RuleGenerationException {
		Object obj = generateParams.getColumnValue(valueSlot.getColumnNo());
		logDebug(logger, "getValueObject(ColumnReferencePatternValueSlot): obj = %s (%s)", obj, (obj == null ? "" : "(" + obj.getClass().getName() + ")"));
		// check column data spec
		AbstractTemplateColumn templateColumn = generateParams.getTemplate().getColumn(valueSlot.getColumnNo());
		if (templateColumn.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_DATE_RANGE)
				|| templateColumn.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE)) {
			if (!(obj instanceof DateRange)) {
				obj = (obj == null ? null : DateRange.parseValue(obj.toString()));
			}
			return obj;
		}
		else if (templateColumn.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_TIME_RANGE)) {
			if (obj instanceof TimeRange) {
				return obj;
			}
			else {
				return (obj == null ? null : TimeRange.parseTimeRangeValue(obj.toString()));
			}
		}

		if (!(templateColumn.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN))) {
			if (obj == null) return null;

			if (obj instanceof Boolean) {
				return (((Boolean) obj).booleanValue() ? RuleGeneratorHelper.AE_TRUE : RuleGeneratorHelper.AE_NIL);
			}
			else if (EnumValues.class.isInstance(obj) || obj instanceof Date || obj instanceof EnumValue || obj instanceof CategoryOrEntityValue || obj instanceof CategoryOrEntityValues) {
				return obj;
			}
			else if (templateColumn.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST) && templateColumn.getColumnDataSpecDigest().isMultiSelectAllowed()) {
				// obj is NOT an instance of EnumValues
				EnumValues<?> enumValues = EnumValues.parseValue(obj.toString());
				return enumValues;
			}
			else {
				if (templateColumn.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.DOMAIN_ATTRIBUTE && templateColumn.getMappedAttribute() != null) {
					obj = AeMapper.getEnumAttributeIfApplicable(templateColumn.getMAClassName(), templateColumn.getMAAttributeName(), obj.toString());
				}
				return obj;
			}
		}
		// for boolean column
		else {
			if (obj == null) {
				return templateColumn.getColumnDataSpecDigest().isBlankAllowed() ? null : RuleGeneratorHelper.AE_NIL;
			}
			else {
				Boolean booleanValue;
				if (obj instanceof Boolean) {
					booleanValue = (Boolean) obj;
				}
				else {
					booleanValue = BooleanDataHelper.mapToBooleanValue(obj.toString(), templateColumn.getColumnDataSpecDigest().isBlankAllowed());
				}
				return booleanValue == null ? "" : (booleanValue.booleanValue() ? RuleGeneratorHelper.AE_TRUE : RuleGeneratorHelper.AE_NIL);
			}
		}
	}

	private Object getValueObject(PatternValueSlot patternValueSlot, GuidelineGenerateParams generateParams, final Integer precision) throws RuleGenerationException {
		if (patternValueSlot instanceof ColumnReferencePatternValueSlot) {
			Object valueObject = getValueObject((ColumnReferencePatternValueSlot) patternValueSlot, generateParams);
			logDebug(logger, "getValueObject(PatternValueSlot): valueObject = %s", valueObject);
			if (UtilBase.isEmpty(patternValueSlot.getSlotText())) {
				return valueObject;
			}
			else {
				String strValue = null;
				if (valueObject instanceof Number) {
					strValue = RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getFloatFormatter(precision).format((Number) valueObject);
				}
				else if (valueObject instanceof Date) {
					strValue = RuleGeneratorHelper.formatDateValueForLHS((Date) valueObject);
				}
				else {
					strValue = valueObject.toString();
				}
				return MessageFormat.format(patternValueSlot.getSlotText(), new Object[] { strValue });
			}
		}
		else if (patternValueSlot instanceof StringValuePatternValueSlot) {
			return ((StringValuePatternValueSlot) patternValueSlot).getSlotValue();
		}
		else {
			throw new RuleGenerationException(patternValueSlot + " is not supported for operator based LHS pattern generation");
		}
	}
}
