package com.mindbox.pe.model.template;

import com.mindbox.pe.model.TemplateUsageType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterTemplateColumn extends AbstractTemplateColumn {

	private static final long serialVersionUID = 2003122613004001L;

	/**
	 * Added for digester support.
	 * @since PowerEditor 3.2.0
	 */
	public ParameterTemplateColumn() {
		super(-1, "", null, 100, null);
	}

	/**
	 * @param id
	 * @param name
	 * @param desc
	 * @param width
	 * @param dataSpec
	 * @param mappedAttr
	 */
	public ParameterTemplateColumn(int id, String name, String desc, int width, TemplateUsageType usageType) {
		super(id, name, desc, width, usageType);
	}

	public ParameterTemplateColumn(ParameterTemplateColumn source) {
		super(source);
	}

	public String toString() {
		return "ParameterTempColumn[" + getID() + "," + getName() + ",attr=" + super.getMappedAttribute() + "]";
	}

	private void setColumnNumber(int colNo) {
		super.setID(colNo);
	}

	/**
	 * Added for digest support.
	 * @param str
	 * @since PowerEditor 3.2.0
	 */
	public void setColNum(String str) {
		try {
			setColumnNumber(Integer.parseInt(str));
		}
		catch (Exception ex) {
		}
	}
}
