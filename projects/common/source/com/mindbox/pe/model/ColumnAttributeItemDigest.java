/*
 * Created on 2004. 6. 4.
 *
 */
package com.mindbox.pe.model;

import java.io.Serializable;

/**
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public class ColumnAttributeItemDigest implements Serializable {

	private static final long serialVersionUID = 2004061070000L;

	private String name;
	private String displayValue;

	/**
	 * 
	 */
	public ColumnAttributeItemDigest() {
		super();
	}

	/**
	 * Constructs a fully initialized instance.
	 * @param name
	 * @param displayValue
	 * @since PowerEditor 4.0
	 */
	public ColumnAttributeItemDigest(String name, String displayValue) {
		this();
		this.name = name;
		this.displayValue = displayValue;
	}

	/**
	 * Create a new instance of this that is an exact copy of the source.
	 * This performs deep-copy.
	 * @param source
	 * @since PowerEditor 4.3.2
	 */
	public ColumnAttributeItemDigest(ColumnAttributeItemDigest source) {
		this(source.name, source.displayValue);
	}

	/**
	 * @return Returns the displayValue.
	 */
	public String getDisplayValue() {
		return displayValue;
	}

	/**
	 * @param displayValue The displayValue to set.
	 */
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "AttributeItem[" + name + ":" + displayValue + "]";
	}
}