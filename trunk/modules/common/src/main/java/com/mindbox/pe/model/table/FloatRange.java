package com.mindbox.pe.model.table;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

public class FloatRange extends AbstractRange implements Comparable<FloatRange> {

	private static final long serialVersionUID = 20070515000016L;

	public static String getDefaultValue(ColumnDataSpecDigest columnDataSpecDigest) {
		FloatRange range = new FloatRange();
		range.setLowerValue(new Double(columnDataSpecDigest.getMinAsDouble()));
		range.setUpperValue(new Double(columnDataSpecDigest.getMaxAsDouble()));
		return range.toString();
	}

	public static FloatRange parseValue(String s) {
		FloatRange floatRange = new FloatRange();
		GenericIRangeCopyCat copyCat = GenericIRangeCopyCat.valueOf(s, floatRange);
		floatRange.lowerVal = (Number) copyCat.lowerVal;
		floatRange.upperVal = (Number) copyCat.upperVal;
		floatRange.setLowerValueInclusive(copyCat.lowerValInclusive);
		floatRange.setUpperValueInclusive(copyCat.upperValInclusive);
		return floatRange;
	}

	private Number lowerVal = null;
	private Number upperVal = null;

	public FloatRange() {
	}

	private FloatRange(FloatRange source) {
		super(source);
		this.lowerVal = source.lowerVal;
		this.upperVal = source.upperVal;
	}

	public GridCellValue copy() {
		return new FloatRange(this);
	}

	public boolean isForDate() {
		return false;
	}

	public boolean isEmpty() {
		return lowerVal == null && upperVal == null;
	}

	public boolean representsSingleValue() {
		return lowerVal != null && upperVal != null
				&& Float.floatToIntBits(lowerVal.floatValue()) == Float.floatToIntBits(upperVal.floatValue());
	}

	public boolean isSubsumedBy(FloatRange floatrange) {
		boolean flag = true;
		if ((lowerVal != null && floatrange.lowerVal != null && lowerVal.intValue() < floatrange.lowerVal.intValue())
				|| (upperVal != null && floatrange.upperVal != null && upperVal.intValue() > floatrange.upperVal.intValue())) {
			flag = false;
		}
		return flag;
	}

	public boolean hasOverlap(FloatRange floatrange) {
		boolean flag = false;
		FloatRange floatrange1;
		FloatRange floatrange2;
		if (upperVal != null && floatrange.upperVal != null && floatrange.getUpperValue().compareTo(getUpperValue()) > 0) {
			floatrange1 = floatrange;
			floatrange2 = this;
		}
		else {
			floatrange1 = this;
			floatrange2 = floatrange;
		}
		float lowerMax = 0.0f;
		if (floatrange2.lowerVal != null && floatrange1.lowerVal != null) {
			lowerMax = Math.max(floatrange2.lowerVal.floatValue(), floatrange1.lowerVal.floatValue());
		}
		else if (floatrange2.lowerVal != null) {
			lowerMax = floatrange2.lowerVal.floatValue();
		}
		else if (floatrange1.lowerVal != null) {
			lowerMax = floatrange1.lowerVal.floatValue();
		}

		float f = 0;
		if (floatrange2.upperVal != null) {
			f = floatrange2.upperVal.floatValue() - lowerMax;
		}
		else {
			f = lowerMax * -1;
		}
		if (f > 0.0 || f == 0.0 && floatrange2.isUpperValueInclusive() && floatrange1.isLowerValueInclusive()) flag = true;
		return flag;
	}

	public Double getLowerValue() {
		if (lowerVal == null) {
			return null;
		}
		if (lowerVal instanceof Double) {
			return (Double) lowerVal;
		}
		else {
			return new Double(lowerVal.floatValue());
		}
	}

	public void setLowerValue(Double f) {
		lowerVal = f;
	}

	public Double getUpperValue() {
		if (upperVal == null) {
			return null;
		}
		if (upperVal instanceof Double) {
			return (Double) upperVal;
		}
		else {
			return new Double(upperVal.floatValue());
		}
	}

	public void setUpperValue(Double f) {
		upperVal = f;
	}

	public Number getCeiling() {
		return upperVal;
	}

	public Number getFloor() {
		return lowerVal;
	}

	public Object valueOf(String str) throws Exception {
		// handle empty string
		if (UtilBase.isEmpty(str)) return null;
		return Double.valueOf(str);
	}

	public String formatValue(Object value) {
		return (value == null ? "" : value.toString());
	}

	// Produces inconsistent results from all other subclasses of Range, perhaps we should pull-up a common toString?
	public String toString() {
		return (isLowerValueInclusive() ? "[" : "(") + formatValue(lowerVal) + '-' + formatValue(upperVal)
				+ (isUpperValueInclusive() ? "]" : ")");
	}

	// Why is FloatRange the only comparable subclass of Range?
	// Also, this doesn't consider inclusive v. exclusive boundaries.  If this and that have the same low vals
	// but this low val is inclusive and that low val is not, then this.compareTo(that) should be < 0
	/**
	 * @since PowerEditor 4.2.0.
	 */
	public int compareTo(FloatRange obj) {
		if (this == obj || this.equals(obj)) return 0;

		FloatRange that = (FloatRange) obj;
		if (that.lowerVal.doubleValue() < this.lowerVal.doubleValue())
			return 1;
		else if (that.lowerVal.doubleValue() > this.lowerVal.doubleValue())
			return -1;
		else {
			if (that.upperVal.doubleValue() < this.upperVal.doubleValue())
				return 1;
			else
				return -1;
		}
	}
}