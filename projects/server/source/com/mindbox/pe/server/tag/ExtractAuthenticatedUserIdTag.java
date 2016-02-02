package com.mindbox.pe.server.tag;

import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.PowerEditorSession;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.spi.ServiceProviderFactory;

public class ExtractAuthenticatedUserIdTag extends AbstractVarTag {

	private static final long serialVersionUID = 2010083110440000L;

	private static final String USER_ID_REQUEST_PARAM = "userID";

	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public int doStartTag() throws JspException {
		String userName = null;
		if (!isEmptyAfterTrim(ConfigurationManager.getInstance().getSessionConfiguration().getUserIDCookie())) {
			HttpServletRequest request = HttpServletRequest.class.cast(pageContext.getRequest());
			// Get the user id from the header and then cookie
			userName = extractUserNameFromHeader(request, ConfigurationManager.getInstance().getSessionConfiguration().getUserIDCookie());
			if (isEmptyAfterTrim(userName)) {
				userName = extractUserNameFromCookie(
						request,
						ConfigurationManager.getInstance().getSessionConfiguration().getUserIDCookie());
			}
		}

		if (isEmptyAfterTrim(userName)) {
			userName = pageContext.getRequest().getParameter(USER_ID_REQUEST_PARAM);
		}
		else {
			// Create a new session using the cookie user id if new
			String userNameToUse = SecurityCacheManager.getInstance().getMatchingUserName(userName);
			logger.info("User found from Cookie/Header = " + userNameToUse);
			
			try {
				ServiceProviderFactory.getUserAuthenticationProvider().notifySsoAuthentication(userNameToUse);
			}
			catch (Exception e) {
				throw new JspException("Failed to motify SSO authentication for " + userNameToUse, e);
			}

			if (!SessionManager.getInstance().hasSession(pageContext.getSession().getId())) {
				try {
					SessionManager.getInstance().registerSession(new PowerEditorSession(pageContext.getSession(), userNameToUse));
				}
				catch (ServletActionException e) {
					throw new JspException(e);
				}
			}
		}

		if (userName != null) {
			userName = userName.trim();
		}

		logger.debug("username = " + userName);

		setVarObject(userName);
		return SKIP_BODY;
	}

	private String extractUserNameFromCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		logger.debug(String.format("extractUserNameFromCookie: name=%s, value=%s", cookieName, cookies));
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				logger.debug(String.format(
						"Checking cookie: name=%s,value=%s,path=%s,domain=%s",
						cookie.getName(),
						cookie.getValue(),
						cookie.getPath(),
						cookie.getDomain()));

				if (cookie.getName().equalsIgnoreCase(cookieName)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	private String extractUserNameFromHeader(HttpServletRequest request, String headerName) {
		for (Enumeration<?> enumeration = request.getHeaderNames(); enumeration.hasMoreElements();) {
			String header = String.class.cast(enumeration.nextElement());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Checking HTTP header: name=[%s], value=[%s]", header, request.getHeader(headerName)));
			}
			if (header.equalsIgnoreCase(headerName)) {
				return request.getHeader(headerName);
			}
		}
		return null;
	}
}
