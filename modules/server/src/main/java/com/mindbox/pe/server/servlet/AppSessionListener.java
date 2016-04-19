package com.mindbox.pe.server.servlet;

import static com.mindbox.pe.common.LogUtil.logDebug;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.config.ConfigurationManager;

public class AppSessionListener implements HttpSessionListener {

	private static final Logger LOG = Logger.getLogger(AppSessionListener.class);

	@Override
	public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
		final Integer timeOutInMinValue = ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getSession().getTimeOutInMin();
		if (timeOutInMinValue != null && timeOutInMinValue.intValue() > 0) {
			httpSessionEvent.getSession().setMaxInactiveInterval(60 * timeOutInMinValue.intValue());
		}
		logDebug(LOG, "session created: %s (timeout=%s min)", httpSessionEvent.getSession(), timeOutInMinValue);
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
		logDebug(LOG, "session destroyed: %s", httpSessionEvent.getSession());
	}
}
