package com.mindbox.pe.communication;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.mindbox.pe.model.deploy.GenerateStats;

public class MonitorDeployResponse extends ResponseComm implements Serializable {
	
	private static final long serialVersionUID = 2003052312006001L;


	private int mGenerateRunId;
	private final List<GenerateStats> statsList;

	public MonitorDeployResponse(int i, List<GenerateStats> stats) {
		setGenerateRunId(i);
		this.statsList = Collections.unmodifiableList(stats);
	}

	public void setGenerateRunId(int i) {
		mGenerateRunId = i;
	}

	public int getGenerateRunId() {
		return mGenerateRunId;
	}

	public List<GenerateStats> getStats() {
		return statsList;
	}

}
