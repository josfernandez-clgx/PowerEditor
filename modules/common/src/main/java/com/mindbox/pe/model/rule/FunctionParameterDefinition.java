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
	 * Added for digest support.
	 * @since PowerEditor 3.2.0
	 */
	public FunctionParameterDefinition() {
		super(-1, "");
	}

	/**
	 * @param id id
	 * @param name name
	 */
	public FunctionParameterDefinition(int id, String name) {
		super(id, name);
	}

	public void copyFrom(FunctionParameterDefinition paramDef) {
		setName(paramDef.getName());
		setID(paramDef.getID());
		this.deployType = paramDef.deployType;
		this.paramDataString = paramDef.paramDataString;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionParameterDefinition) {
			return this.getID() == ((FunctionParameterDefinition) obj).getID();
		}
		else {
			return false;
		}
	}

	public DeployType getDeployType() {
		return deployType;
	}

	/**
	 * @return the paramDataString
	 * @since PowerEditor 4.0.0
	 */
	public String getParamDataString() {
		return paramDataString;
	}

	public void setDeployType(DeployType type) {
		deployType = type;
	}

	/**
	 * Added for digest support.
	 * @param type type
	 * @since PowerEditor 3.2.0
	 */
	public void setDeployTypeString(String type) {
		setDeployType(DeployType.valueOf(type));
	}

	@Override
	public void setName(String name) {
		super.setName(name);
	}

	/**
	 * @param paramDataString the paramDataString to set.
	 * @since PowerEditor 4.0.0
	 */
	public void setParamDataString(String paramDataString) {
		this.paramDataString = paramDataString;
	}

	@Override
	public String toString() {
		return "Param[" + getID() + "," + getName() + "," + deployType + "]";
	}
}