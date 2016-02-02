/*
 * Created on 2005. 2. 22.
 *
 */
package com.mindbox.pe.server.config;


/**
 * RuleLHS value configuration container for digest.
 * Encapsulates &lt;Value&gt.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class RuleLHSValueConfig {

	private String type;
	private String deployValue;
	private boolean valueAsString;


	public String getDeployValue() {
		return deployValue;
	}

	public String getType() {
		return type;
	}

	public boolean isValueAsString() {
		return valueAsString;
	}

	public void setDeployValue(String deployValue) {
		this.deployValue = deployValue;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValueAsString(boolean valueAsString) {
		this.valueAsString = valueAsString;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof RuleLHSValueConfig) {
			return type.equals(((RuleLHSValueConfig)obj).type);
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		return "LHSValueConfig["+type+",dv="+deployValue+",asStr?="+valueAsString+"]";
	}
}