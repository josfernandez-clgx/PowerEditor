package com.mindbox.pe.communication;

import java.util.List;

import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.report.AbstractReportSpec;


/**
 * Request to generate print reports.
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class PrintRequest extends AbstractReportRequest<ByteArrayResponse> {

	private static final long serialVersionUID = 2004120812000000L;

	/**
	 * 
	 * @param userID userID
	 * @param sessionID sessionID
	 * @param reportSpec reportSpec
	 * @param guidelineList guidelineList
	 */
	public PrintRequest(String userID, String sessionID, AbstractReportSpec reportSpec, List<GuidelineReportData> guidelineList) {
		super(userID, sessionID, reportSpec, guidelineList);
	}
}
