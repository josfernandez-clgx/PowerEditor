package com.mindbox.pe.common.config;

import java.io.Serializable;

/**
 * Represents <UIPolicies> element in PowerEditorConfiguration.xml.
 * @author kim
 *
 */
public class UIPolicies implements Serializable {

	private static final long serialVersionUID = 5688433660362378791L;

	private boolean sequentialActivationDatesEnfored = false;
	private boolean allowGapsInActivationDates = false;

	public boolean isSequentialActivationDatesEnfored() {
		return sequentialActivationDatesEnfored;
	}

	public void setEnforceSequentialActivationDates(String value) {
		this.sequentialActivationDatesEnfored = ConfigUtil.asBoolean(value);
	}

	public boolean isGapsInActivationDatesAllowed() {
		return allowGapsInActivationDates;
	}

	public void setAllowGapsInActivationDates(String value) {
		this.allowGapsInActivationDates = ConfigUtil.asBoolean(value);
	}

	public String toString() {
		return "UIPolicies[sequentialActivationDatesEnfored=" + sequentialActivationDatesEnfored + ",allowGapsInActivationDates="
				+ allowGapsInActivationDates + ']';
	}

}
