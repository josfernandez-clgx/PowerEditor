package com.mindbox.pe.server.imexport.digest;

import com.mindbox.pe.common.config.AbstractDigestedObjectHolder;


/**
 * Contains template rule digest objects.
 * @author Geneho Kim
 * @since PowerEditor 4.2
 */
public class TemplateRuleContainer extends AbstractDigestedObjectHolder {

	private static final long serialVersionUID = 3592550883774961664L;

	private int id = -1;
	private String type;

	public int getId() {
		return id;
	}

	public void setId(int i) {
		id = i;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return "RuleContainer["+id+",type="+type+"]";
	}
}