package com.mindbox.pe.server.tag;

import java.text.SimpleDateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.DateFilterConfigHelper;

public class WriteApplicationTitleTag extends TagSupport {

	private static final long serialVersionUID = 2010083110440000L;

	private static final SimpleDateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");


	private boolean full = false; // default to short format

	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuilder buff = new StringBuilder();
			buff.append(ConfigurationManager.getInstance().getPowerEditorConfiguration().getUserInterface().getClientWindowTitle());
			if (full) {
				// Write date filter info, if provided
				final DateFilterConfigHelper dateFilterConfigHelper = ConfigurationManager.getInstance().getDateFilterConfigHelper();
				if (dateFilterConfigHelper != null && dateFilterConfigHelper.hasBeginOrEndDate()) {
					synchronized (TITLE_DATE_FORMAT) {
						buff.append(String.format(
								" [%s - %s] ",
								(dateFilterConfigHelper.getBeginDate() == null ? "&nbsp;" : TITLE_DATE_FORMAT.format(dateFilterConfigHelper.getBeginDate())),
								(dateFilterConfigHelper.getEndDate() == null ? "&nbsp;" : TITLE_DATE_FORMAT.format(ConfigurationManager.getInstance().getDateFilterConfigHelper().getEndDate()))));
					}
					if (dateFilterConfigHelper.hasEndDate()) {
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
