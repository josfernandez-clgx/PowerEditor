package com.mindbox.pe.communication;

import java.util.List;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GuidelineReportData;


/**
 * Request for new template version actions (commit and scan).
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class NewTemplateVersionRequest<T extends ResponseComm> extends SessionRequest<T> {

	public static NewTemplateVersionRequest<SaveResponse> createCommitInstance(String userID, String sessionID, int templateID,
			GridTemplate template, DateSynonym ds) {
		return new NewTemplateVersionRequest<SaveResponse>(userID, sessionID, templateID, template, ds);
	}

	public static NewTemplateVersionRequest<ListResponse<List<GuidelineReportData>>> createScanInstance(String userID, String sessionID,
			int templateID, DateSynonym ds) {
		return new NewTemplateVersionRequest<ListResponse<List<GuidelineReportData>>>(userID, sessionID, templateID, ds);
	}

	private static final long serialVersionUID = 200412291733000L;

	private static final int COMMIT = 0;
	private static final int SCAN = 1;

	private final DateSynonym dateSynonym;
	private final int templateID;
	private final GridTemplate template;
	private final int type;

	/**
	 * Creates a new scan new template version request.
	 * @param userID
	 * @param sessionID
	 * @param templateID the original template ID
	 * @param ds cutover date
	 */
	private NewTemplateVersionRequest(String userID, String sessionID, int templateID, DateSynonym ds) {
		super(userID, sessionID);
		this.dateSynonym = ds;
		this.templateID = templateID;
		this.template = null;
		this.type = SCAN;
	}

	/**
	 * Creates a new commit new template version request.
	 * @param userID
	 * @param sessionID
	 * @param templateID the original template ID
	 * @param template new version template
	 * @param ds cutover date
	 */
	private NewTemplateVersionRequest(String userID, String sessionID, int templateID, GridTemplate template, DateSynonym ds) {
		super(userID, sessionID);
		this.dateSynonym = ds;
		this.templateID = templateID;
		this.template = template;
		this.type = COMMIT;
	}

	public boolean isForCommit() {
		return type == COMMIT;
	}

	public int getTemplateID() {
		return templateID;
	}

	public GridTemplate getTemplate() {
		return template;
	}

	public DateSynonym getDateSynonym() {
		return dateSynonym;
	}
}
