package com.mindbox.pe.client.common.grid;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.IClientConstants;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.EnumValuesDataHelper;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.table.TimeRange;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

/**
 * Grid cell value validator.
 * @author Geneho Kim
 * @since PowerEditor
 */
public final class CellValidator implements IClientConstants {

	// TODO Kim, 11/06/07: Refactor to return detail error message list, not boolean

	/**
	 * @return <code>true</code> if the dataspec allows null or the value is not null.
	 */
	private static boolean checkForNull(Object value, ColumnDataSpecDigest columnDataSpecDigest) {
		boolean flag = true;
		if (columnDataSpecDigest.isBlankAllowed()) {
			flag = !ExcelAdapter.isNullEmptyString(value);
		}
		if (!flag) {
			ClientUtil.printWarning("Blank not allowed for " + columnDataSpecDigest);
		}
		return flag;
	}

	public static boolean validateEnumValue(String s, ColumnDataSpecDigest columnDataSpecDigest) {
		boolean flag = checkForNull(s, columnDataSpecDigest);
		if (!flag) {
			return false;
		}
		String strToValidate = (s.startsWith(EnumValues.EXCLUSION_PREFIX) ? s.substring(4) : s);

		List<EnumValue> list = EnumValuesDataHelper.getAllEnumValues(
				columnDataSpecDigest,
				DomainModel.getInstance(),
				ClientUtil.getEnumerationSourceProxy());
		if (columnDataSpecDigest.isMultiSelectAllowed()) {
			try {
				for (StringTokenizer stringtokenizer = new StringTokenizer(strToValidate, ",", false); stringtokenizer.hasMoreTokens();) {
					String token = stringtokenizer.nextToken();
					boolean found = false;
					for (EnumValue enumValue : list) {
						EnumValue enumValueObj = (EnumValue) enumValue;
						// check deploy id first, and then the display label
						Integer integerToken = null;
						try {
							integerToken = Integer.valueOf(token);
						}
						catch (NumberFormatException e) {

						}
						if (integerToken != null && integerToken.equals(enumValueObj.getDeployID())) {
							found = true;
							break;
						}
						else if (enumValueObj.getDisplayLabel().equals(token)) {
							found = true;
							break;
						}
					}
					if (!found) {
						flag = false;
						ClientUtil.printInfo("Invalid enum val: " + token);
						break;
					}
				}
			}
			catch (Exception exception) {
				exception.printStackTrace();
				flag = false;
			}
		}
		else {
			try {
				boolean found = false;
				for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
					Object enumValueObj = iter.next();
					if (enumValueObj instanceof EnumValue) {
						EnumValue enumValue = (EnumValue) enumValueObj;
						// check deploy id first, and then the display label
						Integer integerToken = null;
						try {
							integerToken = Integer.valueOf(strToValidate);
						}
						catch (NumberFormatException e) {

						}
						if (integerToken != null && integerToken.equals(enumValue.getDeployID())) {
							found = true;
							break;
						}
						else if (enumValue.getDisplayLabel().equals(strToValidate)) {
							found = true;
							break;
						}
					}
					else if (enumValueObj instanceof String) {
						String enumValue = (String) enumValueObj;
						if (enumValue.equals(strToValidate)) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					ClientUtil.printInfo("Invalid enum val: " + strToValidate);
					flag = false;
				}
			}
			catch (Exception exception1) {
				exception1.printStackTrace();
				flag = false;
			}
		}
		if (!flag) ClientUtil.printWarning("Invalid enum data: " + s);
		return flag;
	}

	public static boolean validateValue(String s, ColumnDataSpecDigest columnDataSpecDigest) {
		boolean flag = checkForNull(s, columnDataSpecDigest);
		if (!flag) {
			return false;
		}
		if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_SYMBOL)) {
			return UtilBase.isValidSymbol(s);
		}
		else if (columnDataSpecDigest.isDoubleType()) {
			try {
				double d = Double.valueOf(s).doubleValue();
				flag = validateValue(d, columnDataSpecDigest);
			}
			catch (Exception _ex) {
				flag = false;
			}
			if (!flag) ClientUtil.printWarning("Invalid float data: " + s);
			return flag;
		}
		else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER)) {
			try {
				int i = Integer.parseInt(s);
				flag = validateValue(i, columnDataSpecDigest);
			}
			catch (Exception e) {
				e.printStackTrace();
				flag = false;
			}
			if (!flag) ClientUtil.printWarning("Invalid int data: " + s);
			return flag;
		}
		else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER_RANGE)) {
			IntegerRange integerrange = IntegerRange.parseValue(s);
			if (integerrange == null) {
				flag = false;
			}
			else {
				if (integerrange.getLowerValue() != null && columnDataSpecDigest.getMinAsLong() > integerrange.getLowerValue().intValue())
					flag = false;
				if (integerrange.getUpperValue() != null && columnDataSpecDigest.getMaxAsLong() < integerrange.getUpperValue().intValue())
					flag = false;
				if (integerrange.getLowerValue() == null && integerrange.getUpperValue() == null && !columnDataSpecDigest.isBlankAllowed()) {
					flag = false;
				}
			}
			if (!flag) ClientUtil.printWarning("Invalid intRange data: " + s);
			return flag;
		}
		else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT_RANGE)
				|| columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE)) {
			FloatRange floatrange = FloatRange.parseValue(s);
			if (floatrange == null) {
				flag = false;
			}
			else {
				if (floatrange.getLowerValue() != null && columnDataSpecDigest.getMinAsDouble() > floatrange.getLowerValue().doubleValue())
					flag = false;
				if (floatrange.getUpperValue() != null && columnDataSpecDigest.getMaxAsDouble() < floatrange.getUpperValue().doubleValue())
					flag = false;
				if (floatrange.getLowerValue() == null && floatrange.getUpperValue() == null && !columnDataSpecDigest.isBlankAllowed()) {
					flag = false;
				}
			}
			if (!flag) ClientUtil.printWarning("Invalid floatRange data: " + s);
			return flag;
		}
		else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_TIME_RANGE)) {
			TimeRange timeRange = TimeRange.parseTimeRangeValue(s);
			if (timeRange == null) {
				flag = false;
			}
			else {
				if (timeRange.getLowerValue() != null && 0 > timeRange.getLowerValue().intValue()) flag = false;
				if (timeRange.getUpperValue() != null && 24 * 60 * 60 < timeRange.getUpperValue().intValue()) flag = false;
				if (timeRange.getLowerValue() == null && timeRange.getUpperValue() == null && !columnDataSpecDigest.isBlankAllowed()) {
					flag = false;
				}
			}
			if (!flag) ClientUtil.printWarning("Invalid time range data: " + s);
			return flag;
		}
		return flag;
	}

	public static boolean validateValue(Integer value, ColumnDataSpecDigest columnDataSpecDigest) {
		if (value == null) {
			return true;
		}
		else {
			return validateValue(value.intValue(), columnDataSpecDigest);
		}
	}

	public static boolean validateValue(Long value, ColumnDataSpecDigest columnDataSpecDigest) {
		if (value == null) {
			return true;
		}
		else {
			return validateValue(value.longValue(), columnDataSpecDigest);
		}
	}

	public static boolean validateValue(Float value, ColumnDataSpecDigest columnDataSpecDigest) {
		if (value == null) {
			return true;
		}
		else {
			return validateValue(value.doubleValue(), columnDataSpecDigest);
		}
	}

	public static boolean validateValue(Double value, ColumnDataSpecDigest columnDataSpecDigest) {
		if (value == null) {
			return true;
		}
		else {
			return validateValue(value.doubleValue(), columnDataSpecDigest);
		}
	}

	public static boolean validateValue(int i, ColumnDataSpecDigest columnDataSpecDigest) {
		boolean flag = true;
		try {
			if (columnDataSpecDigest.getMinAsLong() > i || columnDataSpecDigest.getMaxAsLong() < i) flag = false;
		}
		catch (Exception e) {
			flag = false;
		}
		if (!flag) ClientUtil.printWarning("Invalid int data: " + i);
		return flag;
	}

	public static boolean validateValue(long i, ColumnDataSpecDigest columnDataSpecDigest) {
		boolean flag = true;
		try {
			if (columnDataSpecDigest.getMinAsLong() > i || columnDataSpecDigest.getMaxAsLong() < i) flag = false;
		}
		catch (Exception e) {
			flag = false;
		}
		if (!flag) ClientUtil.printWarning("Invalid int data: " + i);
		return flag;
	}

	public static boolean validateValue(double f, ColumnDataSpecDigest columnDataSpecDigest) {
		boolean flag = true;
		try {
			if (Double.compare(columnDataSpecDigest.getMinAsDouble(), f) > 0
					|| Double.compare(f, columnDataSpecDigest.getMaxAsDouble()) > 0) {
				flag = false;
			}
		}
		catch (Exception e) {
			flag = false;
		}
		if (!flag) ClientUtil.printWarning("Invalid float data: " + f);
		return flag;
	}

	public static boolean validateValue(Date min, Date max, ColumnDataSpecDigest columnDataSpecDigest) {
		boolean flag = true;
		if (min != null && columnDataSpecDigest.getMinAsDate() != null && columnDataSpecDigest.getMinAsDate().after(min)) {
			flag = false;
		}
		if (max != null && columnDataSpecDigest.getMaxAsDate() != null && columnDataSpecDigest.getMaxAsDate().before(max)) {
			flag = false;
		}
		if (min == null && max == null && !columnDataSpecDigest.isBlankAllowed()) {
			flag = false;
		}
		if (!flag) ClientUtil.printWarning("Invalid intRange data: " + min + " - " + max);
		return flag;
	}

}
