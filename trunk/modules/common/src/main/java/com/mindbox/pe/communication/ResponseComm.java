package com.mindbox.pe.communication;

public abstract class ResponseComm extends SapphireComm<ResponseComm> {

	private static final long serialVersionUID = 2003051917439000L;

	private boolean warningFlag;
	private String warning;

	public ResponseComm() {
		warningFlag = false;
		warning = null;
	}

	public String getWarning() {
		return warning;
	}

	public boolean getWarningFlag() {
		return warningFlag;
	}

	public void setWarning(String s) {
		warning = s;
	}

	public void setWarningFlag(boolean flag) {
		warningFlag = flag;
	}
}
