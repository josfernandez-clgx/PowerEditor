package com.mindbox.pe.model.table;

import java.text.NumberFormat;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.template.ColumnDataSpecDigest;


public class IntegerRange extends AbstractRange {

	private static final Logger LOG = Logger.getLogger(IntegerRange.class);

	private static final long serialVersionUID = 20070515000017L;
	private static final NumberFormat INTEGER_NUMBER_FORMAT = NumberFormat.getIntegerInstance();

	private static class StringToValueMapperImpl implements StringToValueMapper {
		@Override
		public Object valueOf(String str) throws Exception {
			return Integer.valueOf(str);
		}
	}

	private static final StringToValueMapperImpl MAPPER_INSTANCE = new StringToValueMapperImpl();

	public static String getDefaultValue(ColumnDataSpecDigest columnDataSpecDigest) {
		IntegerRange range = new IntegerRange();
		range.lowerVal = new Integer((int) columnDataSpecDigest.getMinAsLong());
		range.upperVal = new Integer((int) columnDataSpecDigest.getMaxAsLong());
		return range.toString();
	}

	public static IntegerRange parseValue(String s) {
		IntegerRange integerRange = parseValueInternal(s);
		// if the first attempt fails, try again
		if (!s.trim().equals(integerRange.toString())) {
			if (LOG.isInfoEnabled()) {
				LOG.info(String.format("parseValue failed: expected %s but was %s; trying again", s, integerRange.toString()));
			}
			integerRange = parseValueInternal(s);
		}
		return integerRange;
	}

	private static IntegerRange parseValueInternal(String s) {
		IntegerRange integerrange = new IntegerRange();
		GenericIRangeCopyCat copyCat = GenericIRangeCopyCat.valueOf(s, MAPPER_INSTANCE);
		integerrange.lowerVal = (Number) copyCat.lowerVal;
		integerrange.upperVal = (Number) copyCat.upperVal;
		integerrange.setLowerValueInclusive(copyCat.lowerValInclusive);
		integerrange.setUpperValueInclusive(copyCat.upperValInclusive);
		return integerrange;
	}

	private Number lowerVal = null;
	private Number upperVal = null;

	public IntegerRange() {
	}

	IntegerRange(IntegerRange source) {
		super(source);
		this.lowerVal = source.lowerVal;
		this.upperVal = source.upperVal;
	}

	public GridCellValue copy() {
		return new IntegerRange(this);
	}

	public boolean isForDate() {
		return false;
	}

	public boolean isEmpty() {
		return lowerVal == null && upperVal == null;
	}

	public boolean representsSingleValue() {
		return lowerVal != null && upperVal != null && lowerVal.equals(upperVal);
	}

	public boolean isSubsumedBy(IntegerRange integerrange) {
		boolean flag = true;
		if ((lowerVal != null && integerrange.lowerVal != null && lowerVal.intValue() < integerrange.lowerVal.intValue())
				|| (upperVal != null && integerrange.upperVal != null && upperVal.intValue() > integerrange.upperVal.intValue())) {
			flag = false;
		}
		return flag;
	}

	public boolean hasOverlap(IntegerRange integerrange) {
		boolean flag = false;
		IntegerRange integerrange1;
		IntegerRange integerrange2;
		if (upperVal != null && integerrange.upperVal != null && integerrange.upperVal.intValue() > upperVal.intValue()) {
			integerrange1 = integerrange;
			integerrange2 = this;
		}
		else {
			integerrange1 = this;
			integerrange2 = integerrange;
		}
		int lowerMax = 0;
		if (integerrange2.lowerVal != null && integerrange1.lowerVal != null) {
			lowerMax = Math.max(integerrange2.lowerVal.intValue(), integerrange1.lowerVal.intValue());
		}
		else if (integerrange2.lowerVal != null) {
			lowerMax = integerrange2.lowerVal.intValue();
		}
		else if (integerrange1.lowerVal != null) {
			lowerMax = integerrange1.lowerVal.intValue();
		}

		int i = 0;
		if (integerrange2.upperVal != null) {
			i = integerrange2.upperVal.intValue() - lowerMax;
		}
		else {
			i = lowerMax * -1;
		}
		if (i > 0 || i == 0 && integerrange2.isUpperValueInclusive() && integerrange1.isLowerValueInclusive()) flag = true;
		return flag;
	}

	/**
	 * Make sure val is not null
	 * @param val must be not null
	 * @return string representatino of val
	 */
	private final String formatInteger(Number val) {
		return INTEGER_NUMBER_FORMAT.format(new Long(val.intValue()));
	}

	@Override
	public String toString() {
		if (lowerVal == null && upperVal == null) {
			return "";
		}

		StringBuilder buff = new StringBuilder();
		if (lowerVal != null) {
			buff.append((isLowerValueInclusive() ? "[" : "("));
			buff.append(formatInteger(lowerVal));
		}
		else {
			buff.append(" ");
		}
		buff.append("-");
		if (upperVal != null) {
			buff.append(formatInteger(upperVal));
			buff.append((isUpperValueInclusive() ? "]" : ")"));
		}
		else {
			buff.append(" ");
		}
		return buff.toString();
	}

	public Integer getLowerValue() {
		if (lowerVal == null) {
			return null;
		}
		if (lowerVal instanceof Integer) {
			return (Integer) lowerVal;
		}
		else {
			return new Integer(lowerVal.intValue());
		}
	}

	public void setLowerValue(Integer i) {
		lowerVal = i;
	}

	public Integer getUpperValue() {
		if (upperVal == null) {
			return null;
		}
		if (upperVal instanceof Integer) {
			return (Integer) upperVal;
		}
		else {
			return new Integer(upperVal.intValue());
		}
	}

	public void setUpperValue(Integer i) {
		upperVal = i;
	}

	public Number getCeiling() {
		return upperVal;
	}

	public Number getFloor() {
		return lowerVal;
	}

	public Object valueOf(String str) throws Exception {
		return Integer.valueOf(str);
	}

	public String formatValue(Object value) {
		if (value instanceof Number) {
			return formatInteger((Number) value);
		}
		else {
			return (value == null ? "" : value.toString());
		}
	}
}
