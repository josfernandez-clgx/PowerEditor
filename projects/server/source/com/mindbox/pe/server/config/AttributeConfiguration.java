/*
 * Created on 2005. 5. 5.
 *
 */
package com.mindbox.pe.server.config;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class AttributeConfiguration {

	private String type;
	private String name;
	private String value;
	private boolean valueAsString;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public boolean isValueAsString() {
		return valueAsString;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValueAsString(boolean valueAsString) {
		this.valueAsString = valueAsString;
	}
	
	public String toString() {
		return "Attribute[type="+type+",name="+name+",value="+value+",valueAsStr="+valueAsString+']';
	}
}