package com.mindbox.pe.model.deploy;

import java.io.Serializable;
import java.util.List;

/**
 * Deployment statistics.
 * @author Geneho Kim
 * @author MindBox
 */
public class GenerateStats implements Serializable {

	private static final long serialVersionUID = 2043266566380932174L;

	public static boolean isRunning(List<GenerateStats> statsList) {
		for (GenerateStats stats : statsList) {
			if (stats.isRunning) {
				return true;
			}
		}
		return false;
	}

	public static int computeErrorCount(List<GenerateStats> statsList) {
		int sum = 0;
		for (GenerateStats stats : statsList) {
			sum += stats.getNumErrorsGenerated();
		}
		return sum;
	}

	public static int computePercentage(List<GenerateStats> statsList) {
		int sum = 0;
		for (GenerateStats stats : statsList) {
			sum += stats.getPercentComplete();
		}
		return (int) Math.ceil(sum / statsList.size());
	}

	public static int computeObjectCount(List<GenerateStats> statsList) {
		int sum = 0;
		for (GenerateStats stats : statsList) {
			sum += stats.getNumObjectsGenerated();
		}
		return sum;
	}

	public static int computeRuleCount(List<GenerateStats> statsList) {
		int sum = 0;
		for (GenerateStats stats : statsList) {
			sum += stats.getNumRulesGenerated();
		}
		return sum;
	}

	private int percentageComplete;
	private int ruleCount;
	private int objectCount;
	private int errorCount;
	private String deployDir;
	private boolean isRunning;

	public GenerateStats() {
		isRunning = true;
	}

	public void clear() {
		percentageComplete = 0;
		ruleCount = 0;
		objectCount = 0;
		errorCount = 0;
		deployDir = null;
		isRunning = true;
	}

	public void setPercentComplete(int i) {
		percentageComplete = i;
	}

	public int getPercentComplete() {
		return percentageComplete;
	}

	public String toString() {
		return "stats[running=" + isRunning() + ",percent=" + percentageComplete + ",rules=" + getNumRulesGenerated() + ",objs="
				+ getNumObjectsGenerated() + ",errors=" + getNumErrorsGenerated() + "]";
	}

	public void setRunning(boolean flag) {
		isRunning = flag;
	}

	public void setDeployDir(String s) {
		deployDir = s;
	}

	public String getDeployDir() {
		return deployDir;
	}

	public void setNumRulesGenerated(int i) {
		ruleCount = i;
	}

	public int getNumRulesGenerated() {
		return ruleCount;
	}

	public void setNumObjectsGenerated(int i) {
		objectCount = i;
	}

	public int getNumObjectsGenerated() {
		return objectCount;
	}

	public void setNumErrorsGenerated(int i) {
		errorCount = i;
	}

	public int getNumErrorsGenerated() {
		return errorCount;
	}

	public boolean isRunning() {
		return isRunning;
	}

}
