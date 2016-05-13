package com.mindbox.pe.model.deploy;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Deployment statistics.
 * @author Geneho Kim
 * @author MindBox
 */
public class GenerateStats implements Serializable {

	private static final long serialVersionUID = 2043266566380932174L;

	private final String deployDir;
	private AtomicInteger percentageComplete = new AtomicInteger();
	private AtomicInteger ruleCount = new AtomicInteger();
	private AtomicInteger objectCount = new AtomicInteger();
	private AtomicInteger errorCount = new AtomicInteger();
	private AtomicBoolean exportFileWritten = new AtomicBoolean(false);
	private AtomicBoolean isRunning = new AtomicBoolean(true);
	private long completedDateMillis = 0;

	public GenerateStats(String deployDir) {
		super();
		this.deployDir = deployDir;
	}

	public void addNumObjectsGenerated(int count) {
		objectCount.addAndGet(count);
	}

	public void addNumRulesGenerated(int count) {
		ruleCount.addAndGet(count);
	}

	public void addPercentComplete(int amount) {
		percentageComplete.addAndGet(amount);
	}

	public long getCompletedDateMillis() {
		return completedDateMillis;
	}

	public String getDeployDir() {
		return deployDir;
	}

	public int getNumErrorsGenerated() {
		return errorCount.get();
	}

	public int getNumObjectsGenerated() {
		return objectCount.get();
	}

	public int getNumRulesGenerated() {
		return ruleCount.get();
	}

	public int getPercentComplete() {
		return percentageComplete.get();
	}

	public void incrementNumErrors() {
		errorCount.incrementAndGet();
	}

	public void incrementObjectCount() {
		addNumObjectsGenerated(1);
	}

	public void incrementRuleCount() {
		addNumRulesGenerated(1);
	}

	public boolean isExportFileWritten() {
		return exportFileWritten.get();
	}

	public synchronized boolean isRunning() {
		return isRunning.get();
	}

	public synchronized void markAsNotRunning() {
		isRunning.set(false);
		completedDateMillis = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return String.format(
				"GenerateStat[running=%b,percent=%d,rules=%d,objs=%d,errors=%d]",
				isRunning(),
				getPercentComplete(),
				getNumRulesGenerated(),
				getNumObjectsGenerated(),
				getNumErrorsGenerated());
	}

	public synchronized boolean wasDoneForAtLeast(final long threshold) {
		return completedDateMillis > 0 && (System.currentTimeMillis() - completedDateMillis) >= threshold;
	}

}
