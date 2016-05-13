package com.mindbox.pe.server.tag;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.model.ServerStats;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class ServerStatsTag extends AbstractVarTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2458754051626237631L;

	public int doStartTag() throws JspException {
		ServerStats statsObj = BizActionCoordinator.getInstance().gatherServerStats();
		setVarObject(statsObj);
		return SKIP_BODY;
	}
}
