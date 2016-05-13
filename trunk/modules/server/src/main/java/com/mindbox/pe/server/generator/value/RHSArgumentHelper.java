package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class RHSArgumentHelper {

	private final CellValueRHSValueSlotHelper cellValueRHSValueSlotHelper = new CellValueRHSValueSlotHelper();
	private final ColumnReferenceRHSValueSlotHelper columnReferenceRHSValueSlotHelper = new ColumnReferenceRHSValueSlotHelper();
	private final DatePropertyValueSlotHelper datePropertyValueSlotHelper = new DatePropertyValueSlotHelper();
	private final RowNumberValueSlotHelper rowNumberValueSlotHelper = new RowNumberValueSlotHelper();
	private final RuleNameValueSlotHelper ruleNameValueSlotHelper = new RuleNameValueSlotHelper();
	private final RuleIDValueSlotHelper ruleIDValueSlotHelper = new RuleIDValueSlotHelper();
	private final ContextValueSlotHelper contextValueSlotHelper = new ContextValueSlotHelper();
	private final CategoryIDValueSlotHelper categoryIDValueSlotHelper = new CategoryIDValueSlotHelper();
	private final CategoryNameValueSlotHelper categoryNameValueSlotHelper = new CategoryNameValueSlotHelper();
	private final EntityIDValueSlotHelper entityIDValueSlotHelper = new EntityIDValueSlotHelper();
	private final ActivationSpanValueSlotHelper activationSpanValueSlotHelper = new ActivationSpanValueSlotHelper();

	public String generateValue(ValueSlot valueSlot, GuidelineGenerateParams ruleParams) throws RuleGenerationException {
		return getValueSlotHelper(valueSlot).generateValue(ruleParams, valueSlot);
	}

	private RHSValueSlotHelper getValueSlotHelper(ValueSlot valueSlot) throws RuleGenerationException {
		ValueSlot.Type type = valueSlot.getType();
		switch (type) {
		case CELL_VALUE:
			return cellValueRHSValueSlotHelper;
		case COLUMN_REFERENCE:
			return columnReferenceRHSValueSlotHelper;
		case DATE_PROPERTY:
			return datePropertyValueSlotHelper;
		case ROW_NUMBER:
			return rowNumberValueSlotHelper;
		case RULE_NAME:
			return ruleNameValueSlotHelper;
		case CONTEXT:
			return contextValueSlotHelper;
		case CATEGORY_ID:
			return categoryIDValueSlotHelper;
		case CATEGORY_NAME:
			return categoryNameValueSlotHelper;
		case ENTITY_ID:
			return entityIDValueSlotHelper;
		case RULE_ID:
			return ruleIDValueSlotHelper;
		case ACTIVATION_SPAN:
			return activationSpanValueSlotHelper;
		default:
			throw new RuleGenerationException("Unsupported value slot type: " + type);
		}

	}
}
