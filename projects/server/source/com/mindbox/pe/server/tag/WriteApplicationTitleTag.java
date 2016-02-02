package com.mindbox.pe.server.tag;

import java.text.SimpleDateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.mindbox.pe.server.config.ConfigurationManager;

public class WriteApplicationTitleTag extends TagSupport {

	private static final long serialVersionUID = 2010083110440000L;

	private static final SimpleDateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");


	private boolean full = false; // default to short format

	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuilder buff = new StringBuilder();
			buff.append(ConfigurationManager.getInstance().getUIConfiguration().getClientWindowTitle());
			if (full) {
				// Write date filter info, if provided
				if (ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig().getDateFilterConfig() != null
						&& ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig().getDateFilterConfig().hasBeginOrEndDate()) {
					buff.append(String.format(
							" [%s - %s] ",
							(ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig().getDateFilterConfig().getBeginDate() == null
									? "&nbsp;"
									: TITLE_DATE_FORMAT.format(ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig().getDateFilterConfig().getBeginDate())),
							(ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig().getDateFilterConfig().getEndDate() == null
									? "&nbsp;"
									: TITLE_DATE_FORMAT.format(ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig().getDateFilterConfig().getEndDate()))));
					if (ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig().getDateFilterConfig().hasEndDate()) {
						buff.append("(READ-ONLY)");
					}
				}
				else {
					buff.append(" [All Dates]");
				}
			}

			pageContext.getOut().println(buff.toString());
			buff = null;

			return super.doStartTag();
		}
		catch (Exception e) {
			throw new JspException(e);
		}
	}

	public boolean isFull() {
		return full;
	}

	public void setFull(boolean full) {
		this.full = full;
	}


}
