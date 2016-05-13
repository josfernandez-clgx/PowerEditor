package com.mindbox.pe.communication;

import java.io.Serializable;

import com.mindbox.pe.model.deploy.GenerateStats;

public class MonitorDeployResponse extends ResponseComm implements Serializable {

	private static final long serialVersionUID = 2003052312006001L;

	private final int gnerateRunId;
	private final GenerateStats generateStats;

	public MonitorDeployResponse(int gnerateRunId, GenerateStats generateStats) {
		super();
		this.gnerateRunId = gnerateRunId;
		this.generateStats = generateStats;
	}

	public final GenerateStats getGenerateStats() {
		return generateStats;
	}

	public final int getGnerateRunId() {
		return gnerateRunId;
	}
}
