/*
 * Created on 2004. 4. 2.
 *
 */
package com.mindbox.pe.communication;

import com.mindbox.pe.model.filter.GuidelineReportFilter;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ExportRequest extends AbstractGuidelineReportFilterRequest<ByteArrayResponse> {

	private static final long serialVersionUID = 242450650030981719L;

	/**
	 * @param exportSpec
	 * @param userID
	 * @param sessionID
	 */
	public ExportRequest(GuidelineReportFilter filter, String userID, String sessionID) {
		super(userID, sessionID, filter);
	}

}