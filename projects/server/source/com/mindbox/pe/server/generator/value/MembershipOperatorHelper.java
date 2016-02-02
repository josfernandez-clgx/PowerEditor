package com.mindbox.pe.server.generator.value;

import java.util.List;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.model.TimeSlice;

class MembershipOperatorHelper extends AbstractOperatorHelper {

	MembershipOperatorHelper(RuleGenerationConfiguration ruleGenerationConfiguration) {
		super(ruleGenerationConfiguration);
	}

	@SuppressWarnings("unchecked")
	public ValueAndComment formatForPattern(Object valueObj, int op, String attribVarName, boolean asString, Reference reference, TimeSlice timeSlice)
			throws RuleGenerationException {
		StringBuffer buff = new StringBuffer();
		buff.append(attribVarName);

		if (valueObj instanceof CategoryOrEntityValue || valueObj instanceof CategoryOrEntityValues) {
			throw new RuleGenerationException("Validation error: EntityList column values cannot be used with " + Condition.Aux.toOpString(op));
		}

		EnumValues enumValues = null;
		if (valueObj instanceof EnumValues) {
			enumValues = (EnumValues) valueObj;
		}
		else if (valueObj instanceof List) {
			enumValues = new EnumValues((List) valueObj);
		}
		else {
			enumValues = (valueObj == null ? null : EnumValues.parseValue(valueObj.toString(), true, null));
			// required to handle single enum combo value properly
			if (enumValues != null && !enumValues.isSelectionExclusion() && enumValues.size() == 1) {
				enumValues = null;
			}
		}

		if (enumValues != null) {
			// Note: appendEnumValues method handles both EnumList and EntityList values
			if (!enumValues.isEmpty()) buff.append(" & ");
			appendEnumValues(buff, enumValues, op, reference);
		}
		else if (valueObj != null) {
			// NOTE: Getting here means the value is a single combo item.
			//       Assume the value is deploy value; don't try to get mapped enum value
			buff.append(" & ");
			if (op == Condition.OP_NOT_IN || op == Condition.OP_NOT_EQUAL) {
				buff.append(" ~ ");
			}
			if (valueObj instanceof EnumValue) {
				appendFormatted(buff, ((EnumValue)valueObj).getDeployValue(), asString);
			}
			else {
				appendFormatted(buff, valueObj.toString(), asString);
			}
		}
		return new ValueAndComment(buff.toString());
	}

}
