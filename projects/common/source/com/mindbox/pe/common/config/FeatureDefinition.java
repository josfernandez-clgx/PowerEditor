/*
 * Created on 2004. 10. 8.
 *
 */
package com.mindbox.pe.common.config;

import java.io.Serializable;


/**
 * FeatureConfiguration. 
 * Note className property is not being used. It's a placeholder for future use.
 * @author Geneho Kim
 * @since PowerEditor 4.0.1
 */
public class FeatureDefinition implements Serializable {

	private static final long serialVersionUID = 200410081400000L;
	
	private String className;
	private String name;
	private boolean enable;
	
	
	public String getClassName() {
		return className;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public String getName() {
		return name;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "Feature["+name+","+enable+",cn="+className+"]";
	}
}
