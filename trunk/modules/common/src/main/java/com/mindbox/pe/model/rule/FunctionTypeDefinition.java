package com.mindbox.pe.model.rule;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class FunctionTypeDefinition extends AbstractIDNameDescriptionObject {

	private static final long serialVersionUID = -2127830274911958935L;

	private final List<FunctionParameterDefinition> paramDefList = new LinkedList<FunctionParameterDefinition>();
	private String deploymentRule = null;

	/**
	 * Added for digest support.
	 * @since PowerEditor 3.2.0
	 */
	public FunctionTypeDefinition() {
		super(-1, "", null);
	}

	/**
	 * 
	 */
	public FunctionTypeDefinition(int id, String name, String desc) {
		super(id, name, desc);
	}

	/**
	 * Makes sure fields of this are identical to those of the source, except the parameters. 
	 * @param source
	 */
	public synchronized void copyFrom(FunctionTypeDefinition source) {
		this.setID(source.getID());
		this.setName(source.getName());
		this.setDescription(source.getDescription());
		this.deploymentRule = source.deploymentRule;
		this.paramDefList.clear();
		this.paramDefList.addAll(source.paramDefList);
	}

	public void clearParameterDefinitions() {
		this.paramDefList.clear();
	}

	public void addParameterDefinition(FunctionParameterDefinition paramDef) {
		if (!paramDefList.contains(paramDef)) {
			paramDefList.add(paramDef);
		}
	}

	public FunctionParameterDefinition[] getParameterDefinitions() {
		return paramDefList.toArray(new FunctionParameterDefinition[0]);
	}
	public List<FunctionParameterDefinition> getParameterDefinitionList() {
		return Collections.unmodifiableList(paramDefList);
	}
	
	public boolean hasParameter() {
		return !paramDefList.isEmpty();
	}
	
	public int parameterSize() {
		return paramDefList.size();
	}

	/**
	 * 
	 * @param paramNum this is NOT zero-based. use 1 for the first parameter
	 * @return action param definition for <code>paramNum</code>
	 */
	public FunctionParameterDefinition getParameterDefinitionAt(int paramNum) {
		return paramDefList.get(paramNum - 1);
	}
	
	public void removeParameterDefinitionAt(int paramNum) {
		if (paramDefList.size() >= paramNum) {
			paramDefList.remove(paramNum - 1);
			for (FunctionParameterDefinition element : paramDefList) {
				if (element.getID() > paramNum) {
					element.setID(element.getID() - 1);
				}
			}
		}
	}

	public String getDeploymentRule() {
		return deploymentRule;
	}

	public void setDeploymentRule(String string) {
		deploymentRule = string;
	}

	public String toString() {
		return "FunctionType[" + getID() + "," + getName() + ",noParams=" + paramDefList.size() + "]";
	}
}