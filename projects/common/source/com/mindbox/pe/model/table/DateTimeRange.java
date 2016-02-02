package com.mindbox.pe.model.table;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.ColumnDataSpecDigest;

public class DateTimeRange extends AbstractRange {

	private static final long serialVersionUID = 20070515000013L;

	public static String getDefaultValue(ColumnDataSpecDigest columnDataSpecDigest) {
		DateTimeRange range = new DateTimeRange();
		range.lowerVal = columnDataSpecDigest.getMinAsDate();
		range.upperVal = columnDataSpecDigest.getMaxAsDate();
		return range.toString();
	}

	private static Pattern pattern = Pattern.compile("^([\\(\\[]\\-?[0-9/: ]*)?\\-(\\-?[0-9/: ]*[\\)\\]])?$");

	public static DateTimeRange parseValue(String s) {
		DateTimeRange dateRange = new DateTimeRange();

		if (!UtilBase.isEmpty(s)) {
			try {
				Matcher matcher = pattern.matcher(s.trim());
				if (matcher.matches()) {
					if (matcher.group(1) != null) {
						dateRange.setLowerValueInclusive(matcher.group(1).charAt(0) == '[');
						dateRange.lowerVal = (Date) dateRange.valueOf(matcher.group(1).substring(1));
					}
					else {
						dateRange.lowerVal = null;
					}
					if (matcher.group(2) != null) {
						int lastIndex = matcher.group(2).length() - 1;
						dateRange.setLowerValueInclusive(matcher.group(2).charAt(lastIndex) == ']');
						dateRange.upperVal = (Date) dateRange.valueOf(matcher.group(2).substring(0, lastIndex));
					}
					else {
						dateRange.upperVal = null;
					}
				}
			}
			catch (Exception exception) {
			}
		}
		return dateRange;
	}

	private Date lowerVal = null;
	private Date upperVal = null;

	public DateTimeRange() {
		super();
	}

	private DateTimeRange(DateTimeRange source) {
		super(source);
		this.lowerVal = source.lowerVal;
		this.upperVal = source.upperVal;
	}

	public GridCellValue copy() {
		return new DateTimeRange(this);
	}

	public boolean isForDate() {
		return true;
	}

	public boolean isEmpty() {
		return lowerVal == null && upperVal == null;
	}

	public boolean representsSingleValue() {
		return lowerVal != null && upperVal != null && lowerVal.equals(upperVal);
	}

	public boolean isSubsumedBy(DateTimeRange dateRange) {
		boolean flag = true;
		if ((lowerVal != null && dateRange.lowerVal != null && lowerVal.before(dateRange.lowerVal))
				|| (upperVal != null && dateRange.upperVal != null && upperVal.after(dateRange.upperVal))) {
			flag = false;
		}
		return flag;
	}

	public boolean hasOverlap(DateTimeRange dateRange) {
		boolean flag = false;
		DateTimeRange dateRange1;
		DateTimeRange dateRange2;
		if (upperVal != null && dateRange.upperVal != null && dateRange.upperVal.after(upperVal)) {
			dateRange1 = dateRange;
			dateRange2 = this;
		}
		else {
			dateRange1 = this;
			dateRange2 = dateRange;
		}
		Date lowerMax = null;
		if (dateRange2.lowerVal != null && dateRange1.lowerVal != null) {
			lowerMax = (dateRange2.lowerVal.after(dateRange1.lowerVal) ? dateRange2.lowerVal : dateRange1.lowerVal);
		}
		else if (dateRange2.lowerVal != null) {
			lowerMax = dateRange2.lowerVal;
		}
		else if (dateRange1.lowerVal != null) {
			lowerMax = dateRange1.lowerVal;
		}

		int i = 0;
		if (dateRange2.upperVal != null) {
			i = dateRange2.upperVal.compareTo(lowerMax);
		}
		else {
			i = (lowerMax == null ? 0 : -1);
		}
		if (i > 0 || i == 0 && dateRange2.isUpperValueInclusive() && dateRange1.isLowerValueInclusive()) flag = true;
		return flag;
	}

	public String toString() {
		if (lowerVal == null && upperVal == null) {
			return "";
		}

		StringBuffer buff = new StringBuffer();
		if (lowerVal != null) {
			buff.append((isLowerValueInclusive() ? "[" : "("));
			buff.append(UIConfiguration.FORMAT_DATE_TIME_SEC.format(lowerVal));
		}
		else {
			buff.append(" ");
		}
		buff.append("-");
		if (upperVal != null) {
			buff.append(UIConfiguration.FORMAT_DATE_TIME_SEC.format(upperVal));
			buff.append((isUpperValueInclusive() ? "]" : ")"));
		}
		else {
			buff.append(" ");
		}
		return buff.toString();
	}

	public Date getLowerValue() {
		return lowerVal;
	}

	public void setLowerValue(Date i) {
		lowerVal = i;
	}

	public Date getUpperValue() {
		return upperVal;
	}

	public void setUpperValue(Date i) {
		upperVal = i;
	}

	public Number getCeiling() {
		return getMaxDate() == null ? null : new Long(getMaxDate().getTime());
	}

	public Number getFloor() {
		return getMinDate() == null ? null : new Long(getMinDate().getTime());
	}

	public Date getMinDate() {
		return lowerVal;
	}

	public Date getMaxDate() {
		return upperVal;
	}

	public Object valueOf(String str) throws Exception {
		return UIConfiguration.FORMAT_DATE_TIME_SEC.parse(str);
	}

	public String formatValue(Object value) {
		if (value instanceof Date) {
			return UIConfiguration.FORMAT_DATE_TIME_SEC.format((Date) value);
		}
		else {
			return (value == null ? "" : value.toString());
		}
	}
}
