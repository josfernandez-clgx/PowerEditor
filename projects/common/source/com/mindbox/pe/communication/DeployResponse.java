package com.mindbox.pe.communication;

import java.io.Serializable;

public class DeployResponse extends ResponseComm implements Serializable {
	
	private static final long serialVersionUID = 2003052312002008L;

	private int mGenerateRunId;
	private String deployDirectory;

	public DeployResponse(int i, String deployDirectory) {
		setGenerateRunId(i);
		setDeployDirectory(deployDirectory);
	}


	public String getDeployDirectory() {
		return deployDirectory;
	}

	public void setDeployDirectory(String deployDirectory) {
		this.deployDirectory = deployDirectory;
	}

	private void setGenerateRunId(int i) {
		mGenerateRunId = i;
	}

	public int getGenerateRunId() {
		return mGenerateRunId;
	}
}
