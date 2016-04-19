package com.mindbox.pe.communication;


public class MonitorDeployRequest extends SessionRequest<MonitorDeployResponse> {
	
	private static final long serialVersionUID = 2003052312006000L;

	public String toString() {
		return "MonitorDeployRequest=" + getGenerateRunId();
	}

	public MonitorDeployRequest(String s, String s1, int i) {
		super(s, s1);
		setGenerateRunId(i);
	}

	private void setGenerateRunId(int i) {
		mGenerateRunId = i;
	}

	public int getGenerateRunId() {
		return mGenerateRunId;
	}

	private int mGenerateRunId;
}
