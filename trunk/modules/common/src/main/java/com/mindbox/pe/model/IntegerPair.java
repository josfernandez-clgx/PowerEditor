package com.mindbox.pe.model;

import java.io.Serializable;

public class IntegerPair implements Serializable {

	private static final long serialVersionUID = -3908854042829387880L;

	private Integer value1;
	private Integer value2;
	private transient Integer hashCodeCache = null;

	public IntegerPair(Integer value1, Integer value2) {
		super();
		this.value1 = value1;
		this.value2 = value2;
	}

	@Override
	public boolean equals(Object arg0) {
		if (IntegerPair.class.isInstance(arg0)) {
			return this.hashCode() == arg0.hashCode();
		}
		else {
			return false;
		}
	}

	public Integer getValue1() {
		return value1;
	}

	public Integer getValue2() {
		return value2;
	}

	@Override
	public int hashCode() {
		if (hashCodeCache == null) {
			hashCodeCache = String.format("%s:%s", value1, value2).hashCode();
		}
		return hashCodeCache;
	}

	@Override
	public String toString() {
		return String.format("(%s,%s)", value1, value2);
	}
}
