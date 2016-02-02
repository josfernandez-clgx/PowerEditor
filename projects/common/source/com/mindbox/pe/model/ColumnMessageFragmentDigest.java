package com.mindbox.pe.model;

import java.io.Serializable;

/**
 * Column Message Fragment: message fragment to include in the rule
 * message if the column is not empty, and if the column is included
 * in the message's %columnMessages% construct.
 * @author Beth Marvel
 * @author MindBox
 */
public class ColumnMessageFragmentDigest implements Serializable, ColumnReferenceContainer {

	private static final long serialVersionUID = 2004071124050000L;
	
	public static final String SPACE_PLACE_HOLDER = "<SP>";  
	
	private String type;
	private String rangeStyle;
	private String cellSelection = AbstractMessageKeyList.TYPE_DEFAULT_KEY;
	private String enumDelimiter;
	private String enumFinalDelimiter;
	private String enumPrefix;
	private String text;
	private boolean hasConfigurationAttribute = false;

	/** default constructor */
	public ColumnMessageFragmentDigest() {
	}

	/** Constructor for Enum types */
	public ColumnMessageFragmentDigest(String cellSelection, String enumDelimiter, String enumFinalDelimiter, String enumPrefix) {
		this.type = AbstractMessageKeyList.ENUM_KEY;
		this.setCellSelection(cellSelection);
		this.setEnumDelimiter(enumDelimiter);
		this.setEnumFinalDelimiter(enumFinalDelimiter);
		this.setEnumPrefix(enumPrefix);
	}

	/**
	 * Creates a new instance of this that is an exact copy of the source.
	 * @param source
	 * @since PowerEditor 4.3.2
	 */
	public ColumnMessageFragmentDigest(ColumnMessageFragmentDigest source) {
		this();
		copyFrom(source);
	}

	public void copyFrom(ColumnMessageFragmentDigest source) {
		this.type = source.type;
		this.rangeStyle = source.rangeStyle;
		this.cellSelection = source.cellSelection;
		this.enumDelimiter = source.enumDelimiter;
		this.enumFinalDelimiter = source.enumFinalDelimiter;
		this.enumPrefix = source.enumPrefix;
		this.text = source.text;
		this.hasConfigurationAttribute=source.hasConfigurationAttribute;
	}
	
	/**
	 * If this fragment has no configuration attribute, no config object
	 * needs to be created for it.  hasConfigurationAttribute tracks this.
	 * @return true if a configuration attribute has been set
	 */
	public boolean hasConfigurationAttribute() {
		return hasConfigurationAttribute;
	}

	/** For debugging */
	public String toString() {
		return "ColumnMessageFragmentDigest[type=" + this.getType() + "; cellSelection=" + this.getCellSelection() + "; EnumDelimiter="
				+ this.getEnumDelimiter() + "; EnumFinalDelimiter=" + this.getEnumFinalDelimiter() + "; EnumPrefix=" + this.getEnumPrefix()
				+ "; text=" + this.getText() + "; rangeStyle=" + this.getRangeStyle() + "]";
	}

	/* THE REST ARE MUNDATE SETTTERS AND GETTERS AS NEEDED BY XMLDIGESTER 
	 * Note, that the setters also set hasConfigurationAttribute to true
	 * if the argument to the setter is not null.
	 */

	/* GETTERS */
	public String getCellSelection() {
		return cellSelection;
	}

	public String getEnumDelimiter() {
		return enumDelimiter;
	}

	public String getEnumFinalDelimiter() {
		return enumFinalDelimiter;
	}

	public String getRangeStyle() {
		return rangeStyle;
	}

	public String getText() {
		return text;
	}

	public String getType() {
		return type;
	}

	public String getEnumPrefix() {
		return enumPrefix;
	}

	/* SETTERS */

	public void setText(String text) {
		this.text = text;
		// note, this is not a configuration attribute
	}

	public void setCellSelection(String string) {
		cellSelection = string;
		if (string != null) hasConfigurationAttribute = true;
	}

	public void setEnumDelimiter(String string) {
		enumDelimiter = string;
		if (string != null) hasConfigurationAttribute = true;
	}

	public void setEnumFinalDelimiter(String string) {
		enumFinalDelimiter = string;
		if (string != null) hasConfigurationAttribute = true;
	}

	public void setRangeStyle(String string) {
		rangeStyle = string;
		if (string != null) hasConfigurationAttribute = true;
	}

	public void setType(String string) {
		type = string;
		if (string != null) hasConfigurationAttribute = true;
	}

	public void setEnumPrefix(String string) {
		this.enumPrefix = string;
		if (string != null) hasConfigurationAttribute = true;
	}

	public void adjustChangedColumnReferences(int originalColNo, int newColNo) {
		setText(TemplateMessageDigest.adjustColumnReferences(this.getText(), originalColNo, newColNo, false, false));
	}

	public void adjustDeletedColumnReferences(int colNo) {
		setText(TemplateMessageDigest.adjustColumnReferences(this.getText(), colNo, -1, true, false));
	}

	public boolean containsColumnReference(int colNo) {
		return TemplateMessageDigest.adjustColumnReferences(this.getText(), colNo, -1, false, true) != null;
	}
}