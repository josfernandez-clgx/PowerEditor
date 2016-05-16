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
	 * @param id id 
	 * @param name name
	 * @param desc desc
	 * @param width width
	 * @param usageType usage type
	 */
	public ParameterTemplateColumn(int id, String name, String desc, int width, TemplateUsageType usageType) {
		super(id, name, desc, width, usageType);
	}

	public ParameterTemplateColumn(ParameterTemplateColumn source) {
		super(source);
	}

	/**
	 * Added for digest support.
	 * @param str string
	 * @since PowerEditor 3.2.0
	 */
	public void setColNum(String str) {
		try {
			setColumnNumber(Integer.parseInt(str));
		}
		catch (Exception ex) {
		}
	}

	private void setColumnNumber(int colNo) {
		super.setID(colNo);
	}

	@Override
	public String toString() {
		return "ParameterTempColumn[" + getID() + "," + getName() + ",attr=" + super.getMappedAttribute() + "]";
	}
}
