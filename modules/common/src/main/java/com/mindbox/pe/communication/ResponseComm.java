package com.mindbox.pe.communication;


public abstract class ResponseComm extends SapphireComm<ResponseComm> {

	private static final long serialVersionUID = 2003051917439000L;

	public ResponseComm() {
		mWarningFlag = false;
		mWarning = null;
	}

	public void setWarning(String s) {
		mWarning = s;
	}

	public String getWarning() {
		return mWarning;
	}

	public void setWarningFlag(boolean flag) {
		mWarningFlag = flag;
	}

	public boolean getWarningFlag() {
		return mWarningFlag;
	}

	private boolean mWarningFlag;
	private String mWarning;
}
