package com.mindbox.pe.server.generator.value;

import java.util.Date;

import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ContextElementPatternValueSlot;
import com.mindbox.pe.server.generator.rule.PatternValueSlot;
import com.mindbox.pe.server.model.TimeSlice;

class ContextElementValueSlotHelper extends AbstractLHSValueSlotHelper {

	ContextElementValueSlotHelper(RuleGenerationConfigHelper ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	void appendContextMatchFunctionPatternAsOf(StringBuilder valueBuff, StringBuilder commentBuff, String varName, GenericEntityType entityType, int[] categoryIDs, int[] entityIDs, boolean exclude,
			Date date) {
		if (categoryIDs != null && categoryIDs.length > 0) {
			valueBuff.append(" &:(");
			valueBuff.append(RuleGeneratorHelper.ENTITY_MATCH_FUNCTION);
			valueBuff.append(' ');
			valueBuff.append(":type ");
			valueBuff.append(entityType.toString());
			valueBuff.append(' ');
			valueBuff.append(":id ");
			valueBuff.append(varName);
			valueBuff.append(' ');
			valueBuff.append(":time-slice ");
			valueBuff.append(RuleGeneratorHelper.TIME_SLICE_VARIABLE);
			valueBuff.append(' ');
			valueBuff.append(":category ");
			RuleGeneratorHelper.appendIntArrayAsEntityMatchFunctionArg(valueBuff, categoryIDs);
			RuleGeneratorHelper.appendCategoryIntArrayForCommentString(commentBuff, entityType, categoryIDs);
			boolean hasEntityIDs = entityIDs != null && entityIDs.length > 0;
			if (hasEntityIDs || exclude) {
				valueBuff.append(' ');
			}
			if (hasEntityIDs) {
				valueBuff.append(":entity ");
				RuleGeneratorHelper.appendIntArrayAsEntityMatchFunctionArg(valueBuff, entityIDs);
				RuleGeneratorHelper.appendEntityIntArrayForCommentString(commentBuff, entityType, entityIDs);
				if (exclude) {
					valueBuff.append(' ');
				}
			}
			if (exclude) {
				valueBuff.append(":exclude-flag t");
			}
			valueBuff.append(')');
		}
	}

	private void appendControlPatternLineForEntityType(StringBuilder valueBuff, StringBuilder commentBuff, String varName, TimeSlice timeSlice, GuidelineGenerateParams ruleParams,
			GenericEntityType type, boolean forceQuote) throws RuleGenerationException {
		// if the context contains categories only
		if (ruleParams.hasGenericCategoryContext(type)) {
			appendContextMatchFunctionPatternAsOf(valueBuff, commentBuff, varName, type, ruleParams.getGenericCategoryIDs(type), null, false, timeSlice.getAsOfDate());
		}
		// if the context contains entities only
		else if (ruleParams.hasGenericEntityContext(type)) {
			int[] entityIDs = ruleParams.extractEntityIDsForControlPattern(timeSlice, type);
			if (entityIDs != null && entityIDs.length > 0) {
				valueBuff.append(" & ");
				for (int j = 0; j < entityIDs.length; j++) {
					GenericEntity entity = EntityManager.getInstance().getEntity(type, entityIDs[j]);
					if (j > 0) {
						valueBuff.append(" | ");
						commentBuff.append(", ");
					}
					if (entity != null) {
						valueBuff.append(forceQuote(RuleGeneratorHelper.getGenericEntityIDValue(entity), forceQuote));
						commentBuff.append(entity.getName());
					}
					else {
						valueBuff.append("ERROR-" + type + "-" + entityIDs[j] + "-not-found");
						throw new RuleGenerationException("No entity of type " + type + " with id " + entityIDs[j] + " exists");
					}
				}
			}
			else if (entityIDs == null) {
				valueBuff.append(" nil");
				commentBuff.append("[nil]");
			}
		}
	}

	private void appendControlPatternValueForSingleEntity(StringBuilder valueBuff, StringBuilder commentBuff, GenericEntityType type, int entityID, boolean forceQuote) throws RuleGenerationException {
		valueBuff.append(" & ");
		GenericEntity entity = EntityManager.getInstance().getEntity(type, entityID);
		if (entity != null) {
			valueBuff.append(forceQuote(RuleGeneratorHelper.getGenericEntityIDValue(entity), forceQuote));
			commentBuff.append(entity.getName());
		}
		else {
			valueBuff.append("ERROR-" + type + "-" + entityID + "-not-found");
			commentBuff.append("[" + entityID + "]");
			throw new RuleGenerationException("No entity of type " + type + " with id " + entityID + " exists");
		}
	}

	/**
	 * If s is null, return null.
	 * If (quote), then ensure that s is quoted (if it already is, then return unchanged, otherwise add quotes),
	 * else, ensure that s is NOT quoted (if s is quoted, remove the quotes, otherwise return unchanged)
	 */
	private String forceQuote(String s, boolean quote) {
		if (s == null) {
			return null;
		}
		if (quote) {
			return s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"' ? s : "\"" + s + '"';
		}
		else {
			return s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"' ? s.substring(1, s.length() - 1) : s;
		}
	}

	public ValueAndComment generateValue(PatternValueSlot patternValueSlot, GuidelineGenerateParams generateParams, String attribVarName, TimeSlice[] timeSlices, GenericEntityType messageContextType,
			int messageContextEntityID) throws RuleGenerationException {
		final ContextElementPatternValueSlot cepValueSlot = (ContextElementPatternValueSlot) patternValueSlot;
		StringBuilder valueBuff = new StringBuilder();
		valueBuff.append(attribVarName);
		StringBuilder commentBuff = new StringBuilder();

		if (timeSlices.length == 0) {
			valueBuff.append(" & ERROR_NO_TIME_SLICE_AVAILABLE");
		}
		else {
			if (messageContextType == null || cepValueSlot.getGenericEntityType() != messageContextType) {
				if (generateParams.hasGenericCategoryContext(cepValueSlot.getGenericEntityType()) || generateParams.hasGenericEntityContext(cepValueSlot.getGenericEntityType())) {
					// QN 2007-0817 always format as integer
					appendControlPatternLineForEntityType(valueBuff, commentBuff, attribVarName, timeSlices[0], generateParams, cepValueSlot.getGenericEntityType(), false);
				}
			}
			else if (messageContextType != null && cepValueSlot.getGenericEntityType() == messageContextType) {
				// QN 2007-0817 always format as integer
				appendControlPatternValueForSingleEntity(valueBuff, commentBuff, cepValueSlot.getGenericEntityType(), messageContextEntityID, false);
			}
		}

		return new ValueAndComment(valueBuff.toString(), commentBuff.toString());
	}
}
