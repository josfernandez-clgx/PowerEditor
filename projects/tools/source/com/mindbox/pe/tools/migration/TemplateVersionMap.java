/*
 * Created on 2004. 12. 17.
 *
 */
package com.mindbox.pe.tools.migration;

import com.mindbox.pe.tools.util.TemplateInfo;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class TemplateVersionMap extends TemplateInfo {

	private final String label;
	private int parentID = -1;
	
	public TemplateVersionMap(int id, String name, String label, String usage) {
		super(id,name,usage);
		this.label= label;
	}
	
	public int getParentID() {
		return parentID;
	}
	
	public void setParentID(int parentID) {
		this.parentID = parentID;
	}
	
	public String getLabel() {
		return label;
	}
		
	public void setVersion(String version) {
		super.version = version;
	}
	
	public String toString() {
		return "TemplateVersionMap["+super.getID()+"="+version+",parent="+parentID+"]";
	}
}
