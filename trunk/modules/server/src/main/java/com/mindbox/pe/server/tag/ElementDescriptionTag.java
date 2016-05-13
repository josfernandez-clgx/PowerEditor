package com.mindbox.pe.server.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.common.AuditConstants;

public class ElementDescriptionTag extends AbstractValueTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7276801629954007273L;

	public int doStartTag() throws JspException {
		Object obj = getValueObject();
		int elementTypeID = 0;
		if (obj instanceof Number) {
			elementTypeID = ((Number) obj).intValue();
		}
		else {
			try {
				elementTypeID = Integer.parseInt(obj.toString());
			}
			catch (Exception ex) {
				//ignore
			}
		}
		try {
			pageContext.getOut().print(AuditConstants.getElementTypeDescription(elementTypeID));
		}
		catch (IOException ex) {
			throw new JspException(ex);
		}
		return SKIP_BODY;
	}
}
