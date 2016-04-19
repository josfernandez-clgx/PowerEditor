package com.mindbox.pe.model.rule;

import com.mindbox.pe.model.AbstractIDNameObject;
import com.mindbox.pe.model.DeployType;

/**
 * Function parameter definition.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class FunctionParameterDefinition extends AbstractIDNameObject {

	private static final long serialVersionUID = -2127830274911958936L;

	private DeployType deployType = null;
	private String paramDataString = null;

	/**
	 * @param id
	 * @param name
	 */
	public FunctionParameterDefinition(int id, String name) {
		super(id, name);
	}

	/**
	 * Added for digest support.
	 * @since PowerEditor 3.2.0
	 */
	public FunctionParameterDefinition() {
		super(-1, "");
	}

	public void copyFrom(FunctionParameterDefinition paramDef) {
		setName(paramDef.getName());
		setID(paramDef.getID());
		this.deployType = paramDef.deployType;
		this.paramDataString = paramDef.paramDataString;
	}
	
	/**
	 * @return the paramDataString
	 * @since PowerEditor 4.0.0
	 */
	public String getParamDataString() {
		return paramDataString;
	}

	/**
	 * @param paramDataString the paramDataString to set.
	 * @since PowerEditor 4.0.0
	 */
	public void setParamDataString(String paramDataString) {
		this.paramDataString = paramDataString;
	}

	public DeployType getDeployType() {
		return deployType;
	}

	public void setDeployType(DeployType type) {
		deployType = type;
	}

	/**
	 * Added for digest support.
	 * @since PowerEditor 3.2.0
	 */
	public void setDeployTypeString(String type) {
		setDeployType(DeployType.valueOf(type));
	}

	public boolean equals(Object obj) {
		if (obj instanceof FunctionParameterDefinition) {
			return this.getID() == ((FunctionParameterDefinition) obj).getID();
		}
		else {
			return false;
		}
	}

	public String toString() {
		return "Param[" + getID() + "," + getName() + "," + deployType + "]";
	}

	public void setName(String name) {
		super.setName(name);
	}
}