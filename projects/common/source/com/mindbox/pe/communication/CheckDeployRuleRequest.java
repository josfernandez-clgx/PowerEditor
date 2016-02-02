package com.mindbox.pe.communication;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class CheckDeployRuleRequest extends AbstractTemplateColumnRequest<BooleanResponse> {

	private static final long serialVersionUID = 2003121516304000L;

	
	/**
	 * @param userID
	 * @param sessionID
	 */
	public CheckDeployRuleRequest(String userID, String sessionID, int templateID, int columnID) {
		super(userID, sessionID, templateID, columnID);
	}
}
