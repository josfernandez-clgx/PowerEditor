package com.mindbox.pe.model.template;

import java.io.Serializable;

import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfigType;
import com.mindbox.pe.xsd.config.RangeStyleType;

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

	private MessageConfigType type;
	private RangeStyleType rangeStyle;
	private CellSelectionType cellSelection = CellSelectionType.DEFAULT;
	private String enumDelimiter;
	private String enumFinalDelimiter;
	private String enumPrefix;
	private String text;
	private boolean hasConfigurationAttribute = false;

	/** default constructor */
	public ColumnMessageFragmentDigest() {
	}

	/**
	 * 
	 * @param cellSelection cellSelection
	 * @param enumDelimiter enumDelimiter
	 * @param enumFinalDelimiter enumFinalDelimiter
	 * @param enumPrefix enumPrefix
	 */
	public ColumnMessageFragmentDigest(CellSelectionType cellSelection, String enumDelimiter, String enumFinalDelimiter, String enumPrefix) {
		this.type = MessageConfigType.ENUM;
		this.setCellSelection(cellSelection);
		this.setEnumDelimiter(enumDelimiter);
		this.setEnumFinalDelimiter(enumFinalDelimiter);
		this.setEnumPrefix(enumPrefix);
	}

	/**
	 * Creates a new instance of this that is an exact copy of the source.
	 * @param source source
	 * @since PowerEditor 4.3.2
	 */
	public ColumnMessageFragmentDigest(ColumnMessageFragmentDigest source) {
		this();
		copyFrom(source);
	}

	@Override
	public void adjustChangedColumnReferences(int originalColNo, int newColNo) {
		setText(TemplateMessageDigest.adjustColumnReferences(this.getText(), originalColNo, newColNo, false, false));
	}

	@Override
	public void adjustDeletedColumnReferences(int colNo) {
		setText(TemplateMessageDigest.adjustColumnReferences(this.getText(), colNo, -1, true, false));
	}

	@Override
	public boolean containsColumnReference(int colNo) {
		return TemplateMessageDigest.adjustColumnReferences(this.getText(), colNo, -1, false, true) != null;
	}

	public void copyFrom(ColumnMessageFragmentDigest source) {
		this.type = source.type;
		this.rangeStyle = source.rangeStyle;
		this.cellSelection = source.cellSelection;
		this.enumDelimiter = source.enumDelimiter;
		this.enumFinalDelimiter = source.enumFinalDelimiter;
		this.enumPrefix = source.enumPrefix;
		this.text = source.text;
		this.hasConfigurationAttribute = source.hasConfigurationAttribute;
	}

	/* GETTERS */
	public CellSelectionType getCellSelection() {
		return cellSelection;
	}

	public String getEnumDelimiter() {
		return enumDelimiter;
	}

	public String getEnumFinalDelimiter() {
		return enumFinalDelimiter;
	}

	public String getEnumPrefix() {
		return enumPrefix;
	}

	public RangeStyleType getRangeStyle() {
		return rangeStyle;
	}

	public String getText() {
		return text;
	}

	/* SETTERS */

	public MessageConfigType getType() {
		return type;
	}

	/**
	 * If this fragment has no configuration attribute, no config object
	 * needs to be created for it.  hasConfigurationAttribute tracks this.
	 * @return true if a configuration attribute has been set
	 */
	public boolean hasConfigurationAttribute() {
		return hasConfigurationAttribute;
	}

	public void setCellSelection(CellSelectionType cellSelection) {
		this.cellSelection = cellSelection;
		if (cellSelection != null) hasConfigurationAttribute = true;
	}

	public void setEnumDelimiter(String string) {
		enumDelimiter = string;
		if (string != null) hasConfigurationAttribute = true;
	}

	public void setEnumFinalDelimiter(String string) {
		enumFinalDelimiter = string;
		if (string != null) hasConfigurationAttribute = true;
	}

	public void setEnumPrefix(String string) {
		this.enumPrefix = string;
		if (string != null) hasConfigurationAttribute = true;
	}

	public void setRangeStyle(RangeStyleType rangeStyle) {
		this.rangeStyle = rangeStyle;
		if (rangeStyle != null) hasConfigurationAttribute = true;
	}

	public void setText(String text) {
		this.text = text;
		// note, this is not a configuration attribute
	}

	public void setType(MessageConfigType type) {
		this.type = type;
		if (type != null) hasConfigurationAttribute = true;
	}

	@Override
	public String toString() {
		return "ColumnMessageFragmentDigest[type=" + this.getType() + "; cellSelection=" + this.getCellSelection() + "; EnumDelimiter=" + this.getEnumDelimiter()
				+ "; EnumFinalDelimiter=" + this.getEnumFinalDelimiter() + "; EnumPrefix=" + this.getEnumPrefix() + "; text=" + this.getText() + "; rangeStyle="
				+ this.getRangeStyle() + "]";
	}
}