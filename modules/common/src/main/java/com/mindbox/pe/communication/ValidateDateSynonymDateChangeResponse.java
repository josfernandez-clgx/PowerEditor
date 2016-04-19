package com.mindbox.pe.communication;

import java.util.Collections;
import java.util.List;

import com.mindbox.pe.model.GuidelineReportData;


public class ValidateDateSynonymDateChangeResponse extends AbstractSimpleResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6076293811281754401L;

	private final boolean valid;
	private final List<GuidelineReportData> wouldBeInvalidGuidelines;

	public ValidateDateSynonymDateChangeResponse(boolean valid, List<GuidelineReportData> wouldBeInvalidGuidelines) {
		super();
		this.valid = valid;
		this.wouldBeInvalidGuidelines = Collections.unmodifiableList(wouldBeInvalidGuidelines);
	}

	public boolean isValid() {
		return valid;
	}

	public List<GuidelineReportData> getWouldBeInvalidGuidelines() {
		return wouldBeInvalidGuidelines;
	}
}
