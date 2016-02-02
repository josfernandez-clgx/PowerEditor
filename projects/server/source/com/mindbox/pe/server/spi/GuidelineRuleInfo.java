/*
 * Created on 2005. 7. 1.
 *
 */
package com.mindbox.pe.server.spi;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.3.3
 */
public class GuidelineRuleInfo {

	private int templateID;
	private int columnNo;
	private String ruleString;

	public GuidelineRuleInfo(int templateID, int columnNo, String ruleString) {
		this.templateID = templateID;
		this.columnNo = columnNo;
		this.ruleString = ruleString;
	}

	public int getColumnNo() {
		return columnNo;
	}

	public String getRuleString() {
		return ruleString;
	}

	public int getTemplateID() {
		return templateID;
	}

	public void setColumnNo(int columnNo) {
		this.columnNo = columnNo;
	}

	public void setRuleString(String ruleString) {
		this.ruleString = ruleString;
	}

	public void setTemplateID(int templateID) {
		this.templateID = templateID;
	}
}
