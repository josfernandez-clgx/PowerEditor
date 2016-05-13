/*
 * Created on 2004. 6. 24.
 *
 */
package com.mindbox.pe.model.process;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.Auditable;


/**
 * Request definition.
 * @author kim
 * @since PowerEditor 3.3.0
 */
public class ProcessRequest extends AbstractIDNameDescriptionObject implements Auditable {

	private static final long serialVersionUID = 2004060240001L;

	private Phase phase;
	private String displayName;
	private String purpose;
	private String requestType;
	private String initFunction;


	/**
	 * @param id
	 * @param name
	 */
	public ProcessRequest(int id, String name, String desc) {
		super(id, name, desc);
	}

	private ProcessRequest(ProcessRequest source) {
		super(source);
		this.displayName = source.displayName;
		this.purpose = source.purpose;
		this.requestType = source.requestType;
		this.initFunction = source.initFunction;
		this.phase = source.phase;
	}

	public Auditable deepCopy() {
		return new ProcessRequest(this);
	}

	public String getAuditDescription() {
		return toString();
	}
	
	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void copyFrom(ProcessRequest request) {
		setName(request.getName());
		this.setDescription(request.getDescription());
		this.phase = request.phase;
		this.initFunction = request.initFunction;
		this.purpose = request.purpose;
		this.requestType = request.requestType;
		this.displayName = request.displayName;
	}

	/**
	 * @return Returns the initFunction.
	 */
	public String getInitFunction() {
		return initFunction;
	}

	/**
	 * @param initFunction The initFunction to set.
	 */
	public void setInitFunction(String initFunction) {
		this.initFunction = initFunction;
	}

	/**
	 * @return Returns the purpose.
	 */
	public String getPurpose() {
		return purpose;
	}

	/**
	 * @param purpose The purpose to set.
	 */
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	/**
	 * @return Returns the requestType.
	 */
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType The requestType to set.
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	/**
	 * @return Returns the phase.
	 */
	public Phase getPhase() {
		return phase;
	}

	/**
	 * @param phase The phase to set.
	 */
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
}