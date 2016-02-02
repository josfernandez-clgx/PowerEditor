package com.mindbox.pe.server.imexport.digest;

import com.mindbox.pe.model.CBRCase;

public class CBRCaseDigest extends CBRCase {

	private static final long serialVersionUID = -3701000671279790269L;

	private ActivationDates activationDates;

	public ActivationDates getActivationDates() {
		return activationDates;
	}

	public void setActivationDates(ActivationDates activationDates) {
		this.activationDates = activationDates;
	}
	
	public CBRCase asCBRCase() {
		return new CBRCase(this);
	}
}
