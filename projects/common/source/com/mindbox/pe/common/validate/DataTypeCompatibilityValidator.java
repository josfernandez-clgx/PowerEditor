package com.mindbox.pe.common.validate;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.CompoundRuleElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.FunctionCall;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.rule.TestCondition;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.table.FloatRange;

/**
 * Template rule data type compatibility validator.
 * @author deklerk
 *
 */
public class DataTypeCompatibilityValidator {

	static public final int UNKNOWN_DATA_TYPE = -1;
	static public final int NUMERIC_VALUE_DATA_TYPE = 0;
	static public final int NUMERIC_RANGE_DATA_TYPE = 1;
	static public final int DATE_VALUE_DATA_TYPE = 2;
	static public final int DATE_RANGE_DATA_TYPE = 3;
	static public final int BOOLEAN_VALUE_DATA_TYPE = 4;
	static public final int STRING_VALUE_DATA_TYPE = 5;
	static public final int ENUMERATED_VALUE_LIST_DATA_TYPE = 6;
	static public final int SYMBOL_VALUE_DATA_TYPE = 7;
	static public final int ENTITY_VALUE_DATA_TYPE = 8;

	/**
	 * Defines which data types can be used in calculation conditions.
	 * @since PowerEditor 4.3.5
	 */
	public static final int[] DATA_TYPES_FOR_CALC = { NUMERIC_VALUE_DATA_TYPE, DATE_VALUE_DATA_TYPE };

	static public final String UNKNOWN_DATA_TYPE_NAME = "Unknown";
	static public final String NUMERIC_VALUE_DATA_TYPE_NAME = "Numeric";
	static public final String NUMERIC_RANGE_DATA_TYPE_NAME = "Numeric Range";
	static public final String DATE_VALUE_DATA_TYPE_NAME = "Date";
	static public final String DATE_RANGE_DATA_TYPE_NAME = "DateRange";
	static public final String BOOLEAN_VALUE_DATA_TYPE_NAME = "Boolean";
	static public final String STRING_VALUE_DATA_TYPE_NAME = "String";
	static public final String ENUMERATED_VALUE_LIST_DATA_TYPE_NAME = "Enumerated values";
	static public final String SYMBOL_VALUE_DATA_TYPE_NAME = "Symbol";

	static public final int UNKNOWN_OPERATOR_TYPE = -1;
	static public final int EQUALITY_OPERATOR_TYPE = 0;
	static public final int NUMERIC_COMPARISON_OPERATOR_TYPE = 1;
	static public final int MEMBERSHIP_OPERATOR_TYPE = 2;
	static public final int RANGE_OPERATOR_TYPE = 3;
	static public final int EXISTENCE_OPERATOR_TYPE = 4;
    static public final int OP_ENTITY_MATCH_FUNCTION_TYPE = 5;

	/**
	 * 
	 */
	private DataTypeCompatibilityValidator() {

	}

	static public int getGenericDataType(DeployType deployType) {
		if (deployType == DeployType.CURRENCY || deployType == DeployType.INTEGER || deployType == DeployType.FLOAT
				|| deployType == DeployType.PERCENT)
			return NUMERIC_VALUE_DATA_TYPE;
		else if (deployType == DeployType.DATE)
			return DATE_VALUE_DATA_TYPE;
		else if (deployType == DeployType.BOOLEAN)
			return BOOLEAN_VALUE_DATA_TYPE;
		else if (deployType == DeployType.STRING)
			return STRING_VALUE_DATA_TYPE;
		else if (deployType == DeployType.CODE || deployType == DeployType.SYMBOL)
			return SYMBOL_VALUE_DATA_TYPE;
        else if (deployType == DeployType.ENTITY_LIST)
            return ENTITY_VALUE_DATA_TYPE;
		else
			return UNKNOWN_DATA_TYPE;
	}

	static public int getGenericDataType(String type) {
		if (type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_DATE) || type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_DATE_TIME))
			return DATE_VALUE_DATA_TYPE;
		else if (type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_DATE_RANGE) || type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE))
			return DATE_RANGE_DATA_TYPE;
		else if (type.equals(ColumnDataSpecDigest.TYPE_TIME_RANGE) || type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_INTEGER_RANGE)
				|| type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_FLOAT_RANGE) || type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE))
			return NUMERIC_RANGE_DATA_TYPE;
		else if (type.equals(ColumnDataSpecDigest.TYPE_INTEGER) || type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_FLOAT)
				|| type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_CURRENCY) || type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_PERCENT))
			return NUMERIC_VALUE_DATA_TYPE;
		else if (type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_BOOLEAN))
			return BOOLEAN_VALUE_DATA_TYPE;
		else if (type.equalsIgnoreCase(ColumnDataSpecDigest.TYPE_ENUM_LIST))
			return ENUMERATED_VALUE_LIST_DATA_TYPE;
		else if (type.equals(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING) || type.equals(ColumnDataSpecDigest.TYPE_STRING))
			return STRING_VALUE_DATA_TYPE;
		else if (type.equals(ColumnDataSpecDigest.TYPE_SYMBOL))
			return SYMBOL_VALUE_DATA_TYPE;
		else if (type.equals(ColumnDataSpecDigest.TYPE_ENTITY))
			return ENTITY_VALUE_DATA_TYPE;
		else if (type.equals(ColumnDataSpecDigest.TYPE_RULE_ID))
			return NUMERIC_VALUE_DATA_TYPE;
		else
			return UNKNOWN_DATA_TYPE;
	}

	static int getPreferredValueDataType(int dataType, int opType) {
		if (opType == RANGE_OPERATOR_TYPE) {
			if (dataType == NUMERIC_VALUE_DATA_TYPE)
				return NUMERIC_RANGE_DATA_TYPE;
			else if (dataType == DATE_VALUE_DATA_TYPE)
				return DATE_RANGE_DATA_TYPE;
		}
		else if (opType == MEMBERSHIP_OPERATOR_TYPE)
			return ENUMERATED_VALUE_LIST_DATA_TYPE;
		return dataType;
	}

	static int getGenericDataType(String value, int preference) {
		if (preference == NUMERIC_VALUE_DATA_TYPE) {
			try {
				Double.parseDouble(value);
				return preference;
			}
			catch (Exception ex) {
			}
		}
		else if (preference == DATE_VALUE_DATA_TYPE) {
			try {
				if (UIConfiguration.FORMAT_DATE.parse(value) != null)
					return preference;
			}
			catch (Exception ex) {
			}
		}
		else if (preference == BOOLEAN_VALUE_DATA_TYPE) {
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
				return preference;
		}
		else if (preference == NUMERIC_RANGE_DATA_TYPE) {
			FloatRange fr = FloatRange.parseValue(value);
			if (fr.getUpperValue() != null || fr.getLowerValue() != null)
				return preference;
		}
		else if (preference == DATE_RANGE_DATA_TYPE) {
			DateRange dr = DateRange.parseValue(value);
			if (dr.getUpperValue() != null || dr.getLowerValue() != null)
				return preference;
		}
		else if (preference == ENUMERATED_VALUE_LIST_DATA_TYPE) {
			// probably need a real check here;
			return preference;
		}
		else if (preference == STRING_VALUE_DATA_TYPE) {
			// probably need a real check here;
			return preference;
		}
		else if (preference == SYMBOL_VALUE_DATA_TYPE) {
			// probably need a real check here;
			return preference;
		}
		return UNKNOWN_DATA_TYPE;
	}

	static String getGenericDataTypeName(String value, int preference) {
		if (preference == DATE_VALUE_DATA_TYPE || preference == UNKNOWN_DATA_TYPE) {
			try {
				if (UIConfiguration.FORMAT_DATE.parse(value) != null)
					return DATE_VALUE_DATA_TYPE_NAME;
			}
			catch (Exception ex) {
			}
		}
		if (preference == DATE_RANGE_DATA_TYPE || preference == UNKNOWN_DATA_TYPE) {
			DateRange dr = DateRange.parseValue(value);
			if (dr.getUpperValue() != null || dr.getLowerValue() != null)
				return DATE_RANGE_DATA_TYPE_NAME;
		}
		if (preference == NUMERIC_RANGE_DATA_TYPE || preference == UNKNOWN_DATA_TYPE) {
			FloatRange fr = FloatRange.parseValue(value);
			if (fr.getUpperValue() != null || fr.getLowerValue() != null)
				return NUMERIC_RANGE_DATA_TYPE_NAME;
		}
		if (preference == NUMERIC_VALUE_DATA_TYPE || preference == UNKNOWN_DATA_TYPE) {
			try {
				Double.parseDouble(value);
				return NUMERIC_VALUE_DATA_TYPE_NAME;
			}
			catch (Exception ex) {
			}
		}
		if (preference == BOOLEAN_VALUE_DATA_TYPE || preference == UNKNOWN_DATA_TYPE) {
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
				return BOOLEAN_VALUE_DATA_TYPE_NAME;
		}
		if (preference == ENUMERATED_VALUE_LIST_DATA_TYPE || preference == UNKNOWN_DATA_TYPE) {
			// probably need a real check here;
			return ENUMERATED_VALUE_LIST_DATA_TYPE_NAME;
		}
		if (preference == STRING_VALUE_DATA_TYPE || preference == UNKNOWN_DATA_TYPE) {
			// probably need a real check here;
			return STRING_VALUE_DATA_TYPE_NAME;
		}
		if (preference == SYMBOL_VALUE_DATA_TYPE) {
			// probably need a real check here;
			return SYMBOL_VALUE_DATA_TYPE_NAME;
		}

		if (preference == UNKNOWN_DATA_TYPE)
			return UNKNOWN_DATA_TYPE_NAME;
		else
			return getGenericDataTypeName(value, UNKNOWN_DATA_TYPE);
	}

	static String getGenericDataTypeName(int dataType) {
		switch (dataType) {
		case UNKNOWN_DATA_TYPE:
			return UNKNOWN_DATA_TYPE_NAME;
		case NUMERIC_VALUE_DATA_TYPE:
			return NUMERIC_VALUE_DATA_TYPE_NAME;
		case NUMERIC_RANGE_DATA_TYPE:
			return NUMERIC_RANGE_DATA_TYPE_NAME;
		case DATE_VALUE_DATA_TYPE:
			return DATE_VALUE_DATA_TYPE_NAME;
		case DATE_RANGE_DATA_TYPE:
			return DATE_RANGE_DATA_TYPE_NAME;
		case BOOLEAN_VALUE_DATA_TYPE:
			return BOOLEAN_VALUE_DATA_TYPE_NAME;
		case STRING_VALUE_DATA_TYPE:
			return STRING_VALUE_DATA_TYPE_NAME;
		case ENUMERATED_VALUE_LIST_DATA_TYPE:
			return ENUMERATED_VALUE_LIST_DATA_TYPE_NAME;
		case SYMBOL_VALUE_DATA_TYPE:
			return SYMBOL_VALUE_DATA_TYPE_NAME;
		}
		return UNKNOWN_DATA_TYPE_NAME;
	}

	static int getGenericOperatorType(int operatorType) {
		switch (operatorType) {
		case Condition.OP_EQUAL:
		case Condition.OP_NOT_EQUAL:
			return EQUALITY_OPERATOR_TYPE;
		case Condition.OP_GREATER:
		case Condition.OP_GREATER_EQUAL:
		case Condition.OP_LESS:
		case Condition.OP_LESS_EQUAL:
			return NUMERIC_COMPARISON_OPERATOR_TYPE;
		case Condition.OP_BETWEEN:
		case Condition.OP_NOT_BETWEEN:
			return RANGE_OPERATOR_TYPE;
		case Condition.OP_IN:
		case Condition.OP_NOT_IN:
			return MEMBERSHIP_OPERATOR_TYPE;
		case Condition.OP_IS_EMPTY:
		case Condition.OP_IS_NOT_EMPTY:
		case Condition.OP_ANY_VALUE:
			return EXISTENCE_OPERATOR_TYPE;
        case Condition.OP_ENTITY_MATCH_FUNC:
        case Condition.OP_NOT_ENTITY_MATCH_FUNC:            
            return OP_ENTITY_MATCH_FUNCTION_TYPE;
		default:
			return UNKNOWN_OPERATOR_TYPE;
		}
	}

	static int[][] LEGAL_PARAMETER_COMBINATIONS = { 
            { NUMERIC_VALUE_DATA_TYPE, NUMERIC_VALUE_DATA_TYPE },
			{ NUMERIC_RANGE_DATA_TYPE, NUMERIC_VALUE_DATA_TYPE }, 
            { NUMERIC_VALUE_DATA_TYPE, NUMERIC_RANGE_DATA_TYPE },
			{ NUMERIC_RANGE_DATA_TYPE, NUMERIC_RANGE_DATA_TYPE }, 
            { NUMERIC_RANGE_DATA_TYPE, DATE_RANGE_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, DATE_VALUE_DATA_TYPE }, 
            { NUMERIC_VALUE_DATA_TYPE, SYMBOL_VALUE_DATA_TYPE },
			{ DATE_VALUE_DATA_TYPE, DATE_VALUE_DATA_TYPE }, 
			{ STRING_VALUE_DATA_TYPE, STRING_VALUE_DATA_TYPE },
			{ BOOLEAN_VALUE_DATA_TYPE, BOOLEAN_VALUE_DATA_TYPE },
            // entity list is compatible with numbers, strings and symbols
            { ENTITY_VALUE_DATA_TYPE, ENTITY_VALUE_DATA_TYPE },
            { NUMERIC_VALUE_DATA_TYPE, ENTITY_VALUE_DATA_TYPE },
            { STRING_VALUE_DATA_TYPE, ENTITY_VALUE_DATA_TYPE },
            { SYMBOL_VALUE_DATA_TYPE, ENTITY_VALUE_DATA_TYPE },
			// allow symbol to string and string to symbol mappings
			{ SYMBOL_VALUE_DATA_TYPE, STRING_VALUE_DATA_TYPE }, 
            { STRING_VALUE_DATA_TYPE, SYMBOL_VALUE_DATA_TYPE },
			// symbol is compatible with boolean and enumeration
			{ SYMBOL_VALUE_DATA_TYPE, BOOLEAN_VALUE_DATA_TYPE }, 
            { BOOLEAN_VALUE_DATA_TYPE, SYMBOL_VALUE_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE }, 
            { SYMBOL_VALUE_DATA_TYPE, SYMBOL_VALUE_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, NUMERIC_VALUE_DATA_TYPE }, 
            { STRING_VALUE_DATA_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE } };

	static int[][] LEGAL_CONDITION_COMBINATIONS = { 
			{ NUMERIC_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, NUMERIC_VALUE_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, NUMERIC_COMPARISON_OPERATOR_TYPE, NUMERIC_VALUE_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, RANGE_OPERATOR_TYPE, NUMERIC_RANGE_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, NUMERIC_COMPARISON_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, MEMBERSHIP_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, MEMBERSHIP_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ STRING_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, STRING_VALUE_DATA_TYPE },
			{ STRING_VALUE_DATA_TYPE, MEMBERSHIP_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ STRING_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ BOOLEAN_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, BOOLEAN_VALUE_DATA_TYPE },
			{ BOOLEAN_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			// symbol is compatible with boolean and enumeration
			//    AND numebers
			{ BOOLEAN_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, SYMBOL_VALUE_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, SYMBOL_VALUE_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, BOOLEAN_VALUE_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, MEMBERSHIP_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, NUMERIC_VALUE_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, NUMERIC_COMPARISON_OPERATOR_TYPE, SYMBOL_VALUE_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, NUMERIC_COMPARISON_OPERATOR_TYPE, NUMERIC_VALUE_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, RANGE_OPERATOR_TYPE, NUMERIC_VALUE_DATA_TYPE },
			{ SYMBOL_VALUE_DATA_TYPE, RANGE_OPERATOR_TYPE, SYMBOL_VALUE_DATA_TYPE },

			// support numeric to date conditions
			{ NUMERIC_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, DATE_VALUE_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, NUMERIC_COMPARISON_OPERATOR_TYPE, DATE_VALUE_DATA_TYPE },
			{ NUMERIC_VALUE_DATA_TYPE, RANGE_OPERATOR_TYPE, DATE_RANGE_DATA_TYPE },
			{ NUMERIC_RANGE_DATA_TYPE, RANGE_OPERATOR_TYPE, DATE_RANGE_DATA_TYPE },
			{ DATE_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, DATE_VALUE_DATA_TYPE },
			{ DATE_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, NUMERIC_VALUE_DATA_TYPE },
			{ DATE_VALUE_DATA_TYPE, NUMERIC_COMPARISON_OPERATOR_TYPE, DATE_VALUE_DATA_TYPE },
			{ DATE_VALUE_DATA_TYPE, NUMERIC_COMPARISON_OPERATOR_TYPE, NUMERIC_VALUE_DATA_TYPE },
			{ DATE_VALUE_DATA_TYPE, RANGE_OPERATOR_TYPE, DATE_RANGE_DATA_TYPE },
			{ DATE_VALUE_DATA_TYPE, EQUALITY_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },
			{ DATE_VALUE_DATA_TYPE, MEMBERSHIP_OPERATOR_TYPE, ENUMERATED_VALUE_LIST_DATA_TYPE },

            // support entity columns for numeric and code only 
            { SYMBOL_VALUE_DATA_TYPE, OP_ENTITY_MATCH_FUNCTION_TYPE, ENTITY_VALUE_DATA_TYPE },
            { NUMERIC_VALUE_DATA_TYPE, OP_ENTITY_MATCH_FUNCTION_TYPE, ENTITY_VALUE_DATA_TYPE }
    };

	public static int[] getLegalGenericDataTypes(DeployType dt, int opType) {
		int lhsGenericDataType = getGenericDataType(dt);
		int genericOpType = getGenericOperatorType(opType);
		List<Integer> legalTypes = new ArrayList<Integer>();
		for (int i = 0; i < LEGAL_CONDITION_COMBINATIONS.length; i++) {
			if (LEGAL_CONDITION_COMBINATIONS[i][0] == lhsGenericDataType && LEGAL_CONDITION_COMBINATIONS[i][1] == genericOpType)
				legalTypes.add(new Integer(LEGAL_CONDITION_COMBINATIONS[i][2]));
		}
		if (legalTypes.size() == 0)
			return null;
		else {
			int[] retVals = new int[legalTypes.size()];
			for (int j = 0; j < legalTypes.size(); j++)
				retVals[j] = legalTypes.get(j).intValue();
			return retVals;
		}
	}

	public static int[] getLegalGenericDataTypesForParameter(DeployType dt) {
		int lhsGenericDataType = getGenericDataType(dt);
		List<Integer> legalTypes = new ArrayList<Integer>();
		for (int i = 0; i < LEGAL_PARAMETER_COMBINATIONS.length; i++) {
			if (LEGAL_PARAMETER_COMBINATIONS[i][0] == lhsGenericDataType)
				legalTypes.add(new Integer(LEGAL_PARAMETER_COMBINATIONS[i][1]));
		}
		if (legalTypes.size() == 0)
			return null;
		else {
			int[] retVals = new int[legalTypes.size()];
			for (int j = 0; j < legalTypes.size(); j++)
				retVals[j] = legalTypes.get(j).intValue();
			return retVals;
		}
	}

	public static String isValid(Condition condition, GridTemplate template, DomainClassProvider domain, boolean incompleteOK) {
		// Huh? The incompleteOK contract seems inconsistent.  If (incompleteOK == false and condition == null) this method returns null, 
		// but isValid(Reference,...) returns "No ref specified..." when incompleteOK == false and condition parts are null (i.e. lhs, op, rhs)
		if (condition != null) {
			return isValid(condition.getReference(), condition.getOp(), condition.getValue(), template, domain, incompleteOK);
		}
		else
			return null;
	}

	public static String isValid(Reference lhs, int op, Value rhs, GridTemplate template, DomainClassProvider domain,
			boolean incompleteOK) {
		if (lhs == null) {
			if (incompleteOK)
				return null;
			else
				return "No reference specified for condition.";
		}
		String conditionString = lhs.toString() + " " + Condition.Aux.toOpString(op);
		DomainClass dc = domain.getDomainClass(lhs.getClassName());
		if (dc == null)
			return "In condition \"" + conditionString + "\": Domain Class " + "\"" + lhs.getClassName() + "\" not found.";
		DomainAttribute da = dc.getDomainAttribute(lhs.getAttributeName());
		if (da == null)
			return "In condition \"" + conditionString + "\": Domain Attribute " + "\"" + lhs.getAttributeName() + "\" not found in class \""
					+ lhs.getClassName() + "\".";
		if (op == Condition.OP_ANY_VALUE || op == Condition.OP_IS_EMPTY || op == Condition.OP_IS_NOT_EMPTY)
			return null;
		if (rhs == null || UtilBase.trim(rhs.toString()).length() == 0) {
			if (incompleteOK)
				return null;
			else
				return "In condition \"" + conditionString + "\": No value specified.";
		}
		int opType = getGenericOperatorType(op);
		if (opType == UNKNOWN_OPERATOR_TYPE) {
			if (incompleteOK)
				return null;
			else
				return "In condition \"" + conditionString + "\": No operator specified.";
		}
		conditionString = conditionString + " " + rhs.toString();

		DeployType lhsDeployType = da.getDeployType();
		int lhsGenericDataType = getGenericDataType(lhsDeployType);

		int rhsGenericDataType = UNKNOWN_DATA_TYPE;
		String rhsDescription = "";
		if (rhs instanceof ColumnReference) {
			int colNo = ((ColumnReference) rhs).getColumnNo();
			AbstractTemplateColumn column = template.getColumn(colNo);
			if (column == null)
				return "In condition \"" + conditionString + "\": Column " + colNo + " does not exist in template.";
			rhsDescription = column.getColumnDataSpecDigest().getType();
			rhsGenericDataType = getGenericDataType(rhsDescription);
		}
		else if (rhs instanceof Reference) {
			Reference rhsRef = (Reference) rhs;
			DomainClass dc2 = domain.getDomainClass(rhsRef.getClassName());
			if (dc2 == null)
				return "In condition \"" + conditionString + "\": Domain Class " + "\"" + rhsRef.getClassName() + "\" not found.";
			DomainAttribute da2 = dc2.getDomainAttribute(rhsRef.getAttributeName());
			if (da2 == null)
				return "In condition \"" + conditionString + "\": Domain Attribute " + "\"" + rhsRef.getAttributeName() + "\" not found in class \""
						+ rhsRef.getClassName() + "\".";
			rhsDescription = da2.getDeployType().getName();
			rhsGenericDataType = getGenericDataType(da2.getDeployType());
		}
		else if (rhs instanceof MathExpressionValue) {
			Reference mathRef = ((MathExpressionValue) rhs).getAttributeReference();
			ColumnReference mathColRef = ((MathExpressionValue) rhs).getColumnReference();
			DomainClass dc2 = domain.getDomainClass(mathRef.getClassName());
			if (dc2 == null)
				return "In condition \"" + conditionString + "\": Domain Class " + "\"" + mathRef.getClassName() + "\" not found.";
			DomainAttribute da2 = dc2.getDomainAttribute(mathRef.getAttributeName());
			if (da2 == null)
				return "In condition \"" + conditionString + "\": Domain Attribute " + "\"" + mathRef.getAttributeName() + "\" not found in class \""
						+ mathRef.getClassName() + "\".";
			if (!UtilBase.isMember(getGenericDataType(da2.getDeployType()), DATA_TYPES_FOR_CALC)) { // != NUMERIC_VALUE_DATA_TYPE)
				return "In condition \"" + conditionString + "\": Domain Attribute " + "\"" + mathRef.getClassName() + "."
						+ mathRef.getAttributeName() + "\" must be numeric.";
			}

			int colNo = mathColRef.getColumnNo();
			AbstractTemplateColumn column = template.getColumn(colNo);
			if (column == null)
				return "In condition \"" + conditionString + "\": Column " + colNo + " does not exist in template.";
			if (!UtilBase.isMember(getGenericDataType(column.getColumnDataSpecDigest().getType()), DATA_TYPES_FOR_CALC)) { // != NUMERIC_VALUE_DATA_TYPE)
				return "In condition \"" + conditionString + "\": Column " + colNo + " must be numeric.";
			}
			rhsGenericDataType = NUMERIC_VALUE_DATA_TYPE;
			rhsDescription = "Calculation";
		}
		else {
			// handle special case where user selected one of the enumerated values of the attribute
			if ((opType == EQUALITY_OPERATOR_TYPE || opType == NUMERIC_COMPARISON_OPERATOR_TYPE) && da.hasEnumValue()) {
				EnumValue[] enumValues = da.getEnumValues();
				if (enumValues != null) {
					for (EnumValue enumVal : enumValues) {
						String displayVal = enumVal.getDisplayLabel();
						if (rhs.toString().equalsIgnoreCase(displayVal))
							return null;
					}
				}
			}
			rhsGenericDataType = getGenericDataType(rhs.toString(), getPreferredValueDataType(lhsGenericDataType, opType));
			rhsDescription = getGenericDataTypeName(rhs.toString(), getPreferredValueDataType(lhsGenericDataType, opType));
		}
		for (int i = 0; i < LEGAL_CONDITION_COMBINATIONS.length; i++) {
			if (LEGAL_CONDITION_COMBINATIONS[i][0] == lhsGenericDataType && LEGAL_CONDITION_COMBINATIONS[i][1] == opType
					&& LEGAL_CONDITION_COMBINATIONS[i][2] == rhsGenericDataType)
				return null;
		}
		String opString = Condition.Aux.toOpString(op);
		return "In condition \"" + conditionString + "\": illegal combination of reference, operation and value: " + lhsDeployType.getName() + " "
				+ opString + " " + rhsDescription;

	}

	public static String isValid(FunctionParameter param, FunctionParameterDefinition paramDef, GridTemplate template,
			DomainClassProvider domain, boolean incompleteOK) {
		if (param == null || param.valueString() == null || UtilBase.trim(param.valueString()).length() == 0) {
			if (incompleteOK)
				return null;
			else
				return "No value specified for parameter.";
		}
		int expectedGenericDataType = getGenericDataType(paramDef.getDeployType());
		int paramGenericDataType = UNKNOWN_DATA_TYPE;
		if (param instanceof ColumnReference) {
			int colNo = ((ColumnReference) param).getColumnNo();
			AbstractTemplateColumn column = template.getColumn(colNo);
			if (column == null)
				return "Column " + colNo + " does not exist in template.";
			String colSpec = column.getColumnDataSpecDigest().getType();
			paramGenericDataType = getGenericDataType(colSpec);
		}
		else if (param instanceof Reference) {
			Reference ref = (Reference) param;
			DomainClass dc2 = domain.getDomainClass(ref.getClassName());
			if (dc2 == null)
				return "Domain Class " + "\"" + ref.getClassName() + "\" not found.";
			DomainAttribute da2 = dc2.getDomainAttribute(ref.getAttributeName());
			if (da2 == null)
				return "Domain Attribute " + "\"" + ref.getAttributeName() + "\" not found in class \"" + ref.getClassName() + "\".";
			paramGenericDataType = getGenericDataType(da2.getDeployType());
		}
		else {
			paramGenericDataType = getGenericDataType(param.valueString(), expectedGenericDataType);
		}
		for (int i = 0; i < LEGAL_PARAMETER_COMBINATIONS.length; i++) {
			if (LEGAL_PARAMETER_COMBINATIONS[i][0] == expectedGenericDataType && LEGAL_PARAMETER_COMBINATIONS[i][1] == paramGenericDataType)
				return null;
		}
		return "Parameter value (\"" + param.toDisplayName() + "\", type = " + getGenericDataTypeName(paramGenericDataType)
				+ ") is incompatible with the defined data type (" + getGenericDataTypeName(expectedGenericDataType) + ").";
	}

	public static String isValid(CompoundRuleElement<?> compound, GridTemplate template, DomainClassProvider domain, boolean incompleteOK) {
		if (compound != null) {
			StringBuffer errors = new StringBuffer();
			for (int i = 0; i < compound.size(); i++) {
				RuleElement element = compound.get(i);
				String error = isValid(element, compound, template, domain, incompleteOK);
				if (error != null)
					errors.append((errors.length() == 0 ? "" : "\n") + error);
			}
			return errors.length() == 0 ? null : errors.toString();
		}
		else
			return null;
	}

	public static String isValid(RuleElement element, CompoundRuleElement<?> parent, GridTemplate template, DomainClassProvider domain,
			boolean incompleteOK) {
		if (element != null) {
			if (element instanceof CompoundRuleElement)
				return isValid((CompoundRuleElement<?>) element, template, domain, incompleteOK);
			else if (element instanceof Condition)
				return isValid((Condition) element, template, domain, incompleteOK);
			else if (element instanceof FunctionParameter && parent instanceof FunctionCall) {
				FunctionParameter param = (FunctionParameter) element;
				int paramNum = param.index();
				FunctionCall funCall = (FunctionCall) parent;
				FunctionTypeDefinition funDef = funCall.getFunctionType();
				FunctionParameterDefinition paramDef = null;
				if (paramNum <= funDef.getParameterDefinitions().length)
					paramDef = funDef.getParameterDefinitionAt(paramNum);
				String typeString = (parent instanceof TestCondition) ? "Test" : "Action";
				if (paramDef == null) {
					return "In " + typeString + " " + funDef.getName() + ": No definition found for function parameter " + paramNum;
				}
				else {
					String error = isValid((FunctionParameter) element, paramDef, template, domain, incompleteOK);
					if (error != null)
						error = "In " + typeString + " " + funDef.getName() + " parameter " + paramNum + "(" + paramDef.getName() + "): " + error;
					return error;
				}
			}
		}
		return null;
	}

	public static String isValid(RuleDefinition ruleDef, GridTemplate template, DomainClassProvider domain, boolean incompleteOK) {
		if (ruleDef != null && template != null) {
			String lhsError = isValid(ruleDef.getRootElement(), template, domain, incompleteOK);
			String rhsError = isValid(ruleDef.getRuleAction(), template, domain, incompleteOK);
			String result = (lhsError == null ? "" : lhsError) + (rhsError == null ? "" : ((lhsError == null ? "" : "\n") + rhsError));
			if (result.length() > 0)
				return result;
		}
		return null;
	}

}