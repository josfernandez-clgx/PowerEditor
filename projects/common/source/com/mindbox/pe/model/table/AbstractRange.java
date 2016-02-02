package com.mindbox.pe.model.table;

import java.io.Serializable;

import com.mindbox.pe.common.UtilBase;

public abstract class AbstractRange implements IRange, StringToValueMapper, Serializable {
	
	private boolean lowerValInclusive = true;
	private boolean upperValInclusive = true;

	protected AbstractRange() {
	}
	
	protected AbstractRange(AbstractRange source) {
		this.lowerValInclusive=source.lowerValInclusive;
		this.upperValInclusive = source.upperValInclusive;
	}
	
	public boolean isLowerValueInclusive() {
		return lowerValInclusive;
	}

	public void setLowerValueInclusive(boolean lowerValInclusive) {
		this.lowerValInclusive = lowerValInclusive;
	}

	public boolean isUpperValueInclusive() {
		return upperValInclusive;
	}

	public void setUpperValueInclusive(boolean upperValInclusive) {
		this.upperValInclusive = upperValInclusive;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!obj.getClass().getName().equals(this.getClass().getName())) { // subclass-safe, multiple-classloader-safe
			return false;
		}
		AbstractRange that = (AbstractRange) obj;
		return this.isLowerValueInclusive() == that.isLowerValueInclusive() && this.isUpperValueInclusive() == that.isUpperValueInclusive()
				&& UtilBase.nullSafeEquals(this.getCeiling(), that.getCeiling())
				&& UtilBase.nullSafeEquals(this.getFloor(), that.getFloor());
	}

	public int hashCode() {
		int result = 13;
		result = result * 17 + (isLowerValueInclusive() ? 1 : 0);
		result = result * 17 + (isUpperValueInclusive() ? 1 : 0);
		result = result * 17 + (getCeiling() == null ? 0 : getCeiling().hashCode());
		result = result * 17 + (getFloor() == null ? 0 : getFloor().hashCode());
		return result;
	}
}
