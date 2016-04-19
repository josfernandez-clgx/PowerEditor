/*
 * Created on 2004. 6. 4.
 *
 */
package com.mindbox.pe.model.template;


/**
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public final class ColumnPresentationDigest {

	private String title;
	private String font;
	private String color;
	private int columnWidth;

	/**
	 * 
	 */
	public ColumnPresentationDigest() {
		super();
	}

	/**
	 * @return Returns the color.
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color The color to set.
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return Returns the font.
	 */
	public String getFont() {
		return font;
	}

	/**
	 * @param font The font to set.
	 */
	public void setFont(String font) {
		this.font = font;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return Returns the columnWidth.
	 */
	public int getColumnWidth() {
		return columnWidth;
	}

	public void setColWidth(String str) {
		try {
			this.columnWidth = Integer.parseInt(str);
		}
		catch (Exception ex) {}
	}

}