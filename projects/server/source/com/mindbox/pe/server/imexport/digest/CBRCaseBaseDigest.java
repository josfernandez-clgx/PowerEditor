package com.mindbox.pe.server.imexport.digest;

import com.mindbox.pe.model.CBRCaseBase;

public class CBRCaseBaseDigest extends CBRCaseBase {

	private static final long serialVersionUID = 1484873524998648113L;

	private ActivationDates activationDates;

	public ActivationDates getActivationDates() {
		return activationDates;
	}

	public void setActivationDates(ActivationDates activationDates) {
		this.activationDates = activationDates;
	}
	
	public CBRCaseBase asCaseBase() {
		return new CBRCaseBase(this);
	}
}
