package com.mindbox.pe.communication;

import java.util.List;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GuidelineReportData;

/**
 * To update bulk data in the guideline grid (or policy) search.
 * 
 * @author Inna Nill
 * @author MindBox, LLC
 * @since PowerEditor 4.2.0
 */
public class BulkUpdateGridDataRequest extends SessionRequest<AbstractSimpleResponse> {
	private static final long serialVersionUID = 2004122916423000L;

	private final List<GuidelineReportData> guidelineReportData;
	private final String status;
	private final DateSynonym eff;
	private final DateSynonym exp;

	public BulkUpdateGridDataRequest(String userID, String sessionID,
			List<GuidelineReportData> reportdata, String newStatus, DateSynonym eff, DateSynonym exp) {
		super(userID, sessionID);
		this.guidelineReportData = reportdata;
		this.status = newStatus;
		this.eff = eff;
		this.exp = exp;
	}

	public String getStatus() {
		return status;
	}

	public List<GuidelineReportData> getGuidelineReportData() {
		return guidelineReportData;
	}

	public DateSynonym getEffDate() {
		return eff;
	}

	public DateSynonym getExpDate() {
		return exp;
	}

	public String toString() {
		return "BulkUpdateGridDataRequest[" + getGuidelineReportData().toString() + "," + getStatus()+"]";
	}
}
