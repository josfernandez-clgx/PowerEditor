package com.mindbox.pe.model.table;

import java.util.Date;

import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.ColumnDataSpecDigest;

public class DateRange extends AbstractRange {

	private static final long serialVersionUID = 20070515000012L;

	public static String getDefaultValue(ColumnDataSpecDigest columnDataSpecDigest) {
		DateRange range = new DateRange();
		range.lowerVal = columnDataSpecDigest.getMinAsDate();
		range.upperVal = columnDataSpecDigest.getMaxAsDate();
		return range.toString();
	}

	public static DateRange parseValue(String s) {
		DateRange dateRange = new DateRange();
		GenericIRangeCopyCat copyCat = GenericIRangeCopyCat.valueOf(s, dateRange);
		dateRange.lowerVal = (Date) copyCat.lowerVal;
		dateRange.upperVal = (Date) copyCat.upperVal;
		dateRange.setLowerValueInclusive(copyCat.lowerValInclusive);
		dateRange.setUpperValueInclusive(copyCat.upperValInclusive);
		return dateRange;
	}

	private Date lowerVal = null;
	private Date upperVal = null;

	public DateRange() {
	}

	private DateRange(DateRange source) {
		super(source);
		this.lowerVal = source.lowerVal;
		this.upperVal = source.upperVal;
	}

	public GridCellValue copy() {
		return new DateRange(this);
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

	public boolean isSubsumedBy(DateRange dateRange) {
		boolean flag = true;
		if ((lowerVal != null && dateRange.lowerVal != null && lowerVal.before(dateRange.lowerVal))
				|| (upperVal != null && dateRange.upperVal != null && upperVal.after(dateRange.upperVal))) {
			flag = false;
		}
		return flag;
	}

	public boolean hasOverlap(DateRange dateRange) {
		boolean flag = false;
		DateRange dateRange1;
		DateRange dateRange2;
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
			buff.append(UIConfiguration.FORMAT_DATE.format(lowerVal));
		}
		else {
			buff.append(" ");
		}
		buff.append("-");
		if (upperVal != null) {
			buff.append(UIConfiguration.FORMAT_DATE.format(upperVal));
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
		return UIConfiguration.FORMAT_DATE.parse(str);
	}

	public String formatValue(Object value) {
		if (value instanceof Date) {
			return UIConfiguration.FORMAT_DATE.format((Date) value);
		}
		else {
			return (value == null ? "" : value.toString());
		}
	}

}
