package com.mindbox.pe.communication;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.report.AbstractReportSpec;


/**
 * Request to generate reports.
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public abstract class AbstractReportRequest<T extends ResponseComm> extends SessionRequest<T> {

	private final List<GuidelineReportData> guidelineList;
	private final AbstractReportSpec reportSpec;

	/**
	 * Creates a new instance of this.
	 * @param userID
	 * @param sessionID
	 * @param reportSpec
	 * @param guidelinList set to <code>null</code> if reportSpec is of type {@link com.mindbox.pe.model.report.CustomReportSpec}
	 */
	protected AbstractReportRequest(String userID, String sessionID, AbstractReportSpec reportSpec, List<GuidelineReportData> guidelineList) {
		super(userID, sessionID);
		this.reportSpec = reportSpec;
		this.guidelineList = new LinkedList<GuidelineReportData>();
		if (guidelineList != null) this.guidelineList.addAll(guidelineList);
	}

	public List<GuidelineReportData> getGuidelineList() {
		return Collections.unmodifiableList(guidelineList);
	}

	public AbstractReportSpec getReportSpec() {
		return reportSpec;
	}
}
