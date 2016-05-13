package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.TimeSlice;

class EntityMatchFunctionOperatorHelper extends AbstractOperatorHelper {

	static final String LINE_SEPARATOR = System.getProperty("line.separator");

	EntityMatchFunctionOperatorHelper(RuleGenerationConfigHelper ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	public ValueAndComment formatForPattern(Object valueObj, int op, String attribVarName, boolean asString, Reference reference, TimeSlice timeSlice, final Integer precision)
			throws RuleGenerationException {
		logger.debug(">>> formatForPattern: value=" + valueObj + ",attrVar=" + attribVarName + ",ref=" + reference);
		StringBuilder valueBuff = new StringBuilder();
		StringBuilder commentBuff = new StringBuilder();
		valueBuff.append(attribVarName);

		if (valueObj == null || UtilBase.isEmpty(valueObj.toString())) {
			return new ValueAndComment(valueBuff.toString());
		}

		int[] categoryIDs = null;
		int[] entityIDs = null;
		GenericEntityType entityType = null;
		boolean exclude = false;

		if (valueObj instanceof CategoryOrEntityValue) {
			CategoryOrEntityValue v = (CategoryOrEntityValue) valueObj;
			entityType = v.getEntityType();
			if (v.isForEntity()) {
				entityIDs = new int[] { v.getId() };
			}
			else {
				categoryIDs = new int[] { v.getId() };
			}
		}
		else if (valueObj instanceof CategoryOrEntityValues) {
			CategoryOrEntityValues v = (CategoryOrEntityValues) valueObj;
			exclude = v.isSelectionExclusion();
			if (v.size() > 0) {
				entityType = ((CategoryOrEntityValue) v.get(0)).getEntityType();
			}
			categoryIDs = v.getCategoryIDs();
			entityIDs = v.getEntityIDs();
		}
		else {
			throw new RuleGenerationException("Invalid object type received: " + valueObj + ". Expecting category or entity value");
		}

		if ((categoryIDs != null && categoryIDs.length > 0) || (entityIDs != null && entityIDs.length > 0)) {
			valueBuff.append(" &:(");
			valueBuff.append(RuleGeneratorHelper.ENTITY_MATCH_FUNCTION);
			valueBuff.append(" ");
			valueBuff.append(":type ");
			valueBuff.append(entityType);
			valueBuff.append(" ");
			valueBuff.append(":id ");
			valueBuff.append(attribVarName);
			valueBuff.append(" ");
			valueBuff.append(":time-slice ");
			valueBuff.append(RuleGeneratorHelper.TIME_SLICE_VARIABLE);
			if (categoryIDs != null && categoryIDs.length > 0) {
				valueBuff.append(" :category ");
				RuleGeneratorHelper.appendIntArrayAsEntityMatchFunctionArg(valueBuff, categoryIDs);
				RuleGeneratorHelper.appendCategoryIntArrayForCommentString(commentBuff, entityType, categoryIDs);
			}
			if (entityIDs != null && entityIDs.length > 0) {
				valueBuff.append(" :entity ");
				RuleGeneratorHelper.appendIntArrayAsEntityMatchFunctionArg(valueBuff, entityIDs);
				RuleGeneratorHelper.appendEntityIntArrayForCommentString(commentBuff, entityType, entityIDs);
			}
			if (!exclude && op == Condition.OP_NOT_ENTITY_MATCH_FUNC || exclude && op == Condition.OP_ENTITY_MATCH_FUNC) {
				valueBuff.append(" :exclude-flag t");
			}
			valueBuff.append(")");
		}

		return new ValueAndComment(valueBuff.toString(), commentBuff.toString());
	}
}
