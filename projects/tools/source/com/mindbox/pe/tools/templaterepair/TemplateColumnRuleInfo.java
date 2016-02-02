/*
 * Created on 2004. 12. 17.
 *
 */
package com.mindbox.pe.tools.templaterepair;

import com.mindbox.pe.tools.util.TemplateInfo;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class TemplateColumnRuleInfo extends TemplateInfo {

	private int columnID = -1;
	private String ruleDefinition = null;

	public TemplateColumnRuleInfo(int id, String name, String version, String usage, int columnID, String ruleDefinition) {
		super(id,name,usage,version);
		this.columnID = columnID;
		this.ruleDefinition = ruleDefinition;
	}

	public int getColumnID() {
		return columnID;
	}

	public String getRuleDefinition() {
		return ruleDefinition;
	}
	
	public String toString() {
		return "TemplateColumnID[" + super.getID() + ',' + super.getVersion() + ",columnID=" + columnID + ']';
	}
}