/*
 * Created on 2004. 3. 26.
 *
 */
package com.mindbox.pe.server.imexport.digest;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class Filter {

	private int id = -1;
	private String type = null;
	private String name = null;
	private String criteria = null;
	
	public String getCriteria() {
		return criteria;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setCriteria(String string) {
		criteria = string;
	}

	public void setId(int i) {
		id = i;
	}

	public void setName(String string) {
		name = string;
	}

	public void setType(String string) {
		type = string;
	}

	public String toString() {
		return "Filter["+id+",name="+name+",type="+type+",crit="+criteria+"]";
	}
}
