package com.mindbox.pe.communication;

import java.util.List;

import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.report.AbstractReportSpec;


/**
 * Request to generate reports.
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class ReportRequest extends AbstractReportRequest<StringResponse> {

	private static final long serialVersionUID = 20060124000000L;

	/**
	 * Creates a new instance of this.
	 * @param userID
	 * @param sessionID
	 * @param reportSpec
	 * @param guidelinList set to <code>null</code> if reportSpec is of type {@link com.mindbox.pe.model.report.CustomReportSpec}
	 */
	public ReportRequest(String userID, String sessionID, AbstractReportSpec reportSpec, List<GuidelineReportData> guidelineList) {
		super(userID, sessionID,reportSpec, guidelineList);
	}
}
