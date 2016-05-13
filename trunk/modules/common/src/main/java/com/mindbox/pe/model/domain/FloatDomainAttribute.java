package com.mindbox.pe.model.domain;

import com.mindbox.pe.common.format.FloatFormatter;

/**
 * Domain attribute of a domain class which contains floating point data.
 * This is used by the domain digester.
 * @author davies
 * @since PowerEditor 4.5
 */
public class FloatDomainAttribute extends DomainAttribute {
	
	private static final long serialVersionUID = 522976654252813801L;

	public static final int DEFAULT_PRECISION = FloatFormatter.DEFAULT_PRECISION;
	public static final int MIN_PRECISION = FloatFormatter.MIN_PRECISION;
	
	private int precision = DEFAULT_PRECISION;

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		if (precision >= MIN_PRECISION) {
			this.precision = precision;
		}
	}
	
	public String toString() {
		String s = super.toString();
		int insertionPoint = s.indexOf(',', s.indexOf(',')+1);  // index of second ','
		
		return s.substring(0, insertionPoint) + ',' + precision + s.substring(insertionPoint);
	}
}