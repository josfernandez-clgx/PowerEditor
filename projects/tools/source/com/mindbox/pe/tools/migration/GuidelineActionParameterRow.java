/*
 * Created on 2005. 3. 10.
 *
 */
package com.mindbox.pe.tools.migration;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class GuidelineActionParameterRow {

	private int actionID;
	private int paramID;
	private String name;
	private String deployType;

	public GuidelineActionParameterRow(int actionID, int paramID, String name, String deployType) {
		this.actionID = actionID;
		this.paramID = paramID;
		this.name = name;
		this.deployType = deployType;
	}

	public int getActionID() {
		return actionID;
	}

	public String getDeployType() {
		return deployType;
	}

	public String getName() {
		return name;
	}

	public int getParamID() {
		return paramID;
	}

	public String toString() {
		return "ActionParam["+actionID+','+paramID+','+name+','+deployType+']';
	}
}