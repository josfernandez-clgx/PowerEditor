package com.mindbox.pe.communication;


public class ReloadConfigurationResponse extends ResponseComm {

	private static final long serialVersionUID = 2003052312001001L;


	private final boolean success;
	private final String reloadFailureMsg;

	public ReloadConfigurationResponse() {
		this.success = true;
		this.reloadFailureMsg = null;
	}

	public ReloadConfigurationResponse(String failureMsg) {
		this.success = false;
		this.reloadFailureMsg = failureMsg;
	}

	public boolean succeeded() {
		return success;
	}

	public String toString() {
		return "ReloadConfigurationResponse[success?="+success+"]";
	}

	public String getReloadFailureMsg() {
		return reloadFailureMsg;
	}

}
