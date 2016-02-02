package com.mindbox.pe.server.tag;

import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.mindbox.pe.server.cache.SecurityCacheManager;

public class WriteUserDisplayNameTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3738096502841431559L;

	private String userId;

	@Override
	public int doStartTag() throws JspException {
		if (!isEmptyAfterTrim(userId)) {
			String displayName = SecurityCacheManager.getInstance().getDisplayName(userId);
			try {
				pageContext.getOut().print(displayName);
			}
			catch (IOException e) {
				throw new JspException(e);
			}
		}
		return super.doStartTag();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
