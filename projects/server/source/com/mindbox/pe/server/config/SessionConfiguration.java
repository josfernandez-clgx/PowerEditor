package com.mindbox.pe.server.config;

import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;

import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.db.PeDbUserAuthenticationProvider;
import com.mindbox.pe.server.ldap.DefaultUserAuthenticationProvider;
import com.mindbox.pe.server.spi.UserAuthenticationProvider;

public class SessionConfiguration {

	private static final Class<PeDbUserAuthenticationProvider> DEFAULT_USER_AUTHENTICATION_PROVIDER_CLASS = PeDbUserAuthenticationProvider.class;
	private static final Class<DefaultUserAuthenticationProvider> DEFAULT_LDAP_USER_AUTHENTICATION_PROVIDER_CLASS = DefaultUserAuthenticationProvider.class;
	private static final int DEFAULT_MAX_AUTH_ATTEMPTS = 5;
	private static final int DEFAULT_MAX_USER_SESSIONS = 10;
	private static final String DEFAULT_LOGOUT_URL = "/logout.jsp";

	private int maxAuthAttempts = DEFAULT_MAX_AUTH_ATTEMPTS;
	private int maxUserSessions = DEFAULT_MAX_USER_SESSIONS;
	private Class<?> userAuthenticationProviderClass = DEFAULT_USER_AUTHENTICATION_PROVIDER_CLASS;
	private String userIDCookie;
	private String loginUrl;
	private String logoutHttpHeader;
	private String logoutUrlFromConfig;
	private String userAuthenticationProviderClassName;
	private String logoutUrlToUse;

	public SessionConfiguration(Reader reader) {
		try {
			ConfigXMLDigester.getInstance().digestServerSessionConfig(reader, this);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to digest enumeration source confiuration.", e);
		}
	}

	public int getMaxAuthenticationAttempts() {
		return maxAuthAttempts;
	}

	public void setMaxAuthenticationAttempts(String maxAuthAttemptsStr) {
		if (!UtilBase.isEmpty(maxAuthAttemptsStr)) {
			try {
				maxAuthAttempts = Integer.parseInt(maxAuthAttemptsStr);
			}
			catch (NumberFormatException e) {
				throw new RuntimeException("Could not parse MaxAuthAttempts as an integer: " + maxAuthAttemptsStr, e);
			}
		}
	}

	public int getMaxUserSessions() {
		return maxUserSessions;
	}

	public void setMaxUserSessionsStr(String maxUserSessionsStr) {
		if (!UtilBase.isEmpty(maxUserSessionsStr)) {
			try {
				maxUserSessions = Integer.parseInt(maxUserSessionsStr);
			}
			catch (NumberFormatException e) {
				throw new RuntimeException("Could not parse MaxUserSessions as an integer: " + maxUserSessionsStr, e);
			}
		}
	}

	public Class<?> getUserAuthenticationProviderClass() {
		return userAuthenticationProviderClass;
	}

	public String getUserIDCookie() {
		return userIDCookie;
	}

	public void setUserIDCookie(String userIDCookie) {
		this.userIDCookie = userIDCookie;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutHttpHeader() {
		return logoutHttpHeader;
	}

	public void setLogoutHttpHeader(String logoutHttpHeader) {
		this.logoutHttpHeader = logoutHttpHeader;
	}

	public String getLogoutUrlFromConfig() {
		return logoutUrlFromConfig;
	}

	public void setLogoutUrlFromConfig(String logoutUrl) {
		this.logoutUrlFromConfig = logoutUrl;
	}

	public String getUserAuthenticationProviderClassName() {
		return userAuthenticationProviderClassName;
	}

	public void setUserAuthenticationProviderClassName(String userAuthenticationProviderClassName) {
		this.userAuthenticationProviderClassName = userAuthenticationProviderClassName;
	}

	/**
	 * Must be called after parsing XML.
	 * @param userManagementProviderClassname; can be <code>null</code>
	 */
	public void postParseProcess(String userManagementProviderClassname) {
		if (!UtilBase.isEmpty(userAuthenticationProviderClassName)) {
			try {
				this.userAuthenticationProviderClass = Class.forName(userAuthenticationProviderClassName);

				if (!UserAuthenticationProvider.class.isAssignableFrom(this.userAuthenticationProviderClass)) {
					throw new ClassCastException(userAuthenticationProviderClassName + " does not implement "
							+ UserAuthenticationProvider.class);
				}
			}
			catch (Exception e) {
				throw new RuntimeException("Could not find UserAuthenticationProvider plugin in class path: "
						+ userAuthenticationProviderClassName, e);
			}
		}
		else if (userManagementProviderClassname != null && userManagementProviderClassname.endsWith("LDAPUserManagementProvider")) {
			this.userAuthenticationProviderClass = DEFAULT_LDAP_USER_AUTHENTICATION_PROVIDER_CLASS;
		}
	}

	public void resetLogoutUrlToUse(HttpServletRequest request) {
		if (logoutUrlToUse == null) {
			String urlToUse = logoutUrlFromConfig;
			if (isEmptyAfterTrim(urlToUse)) {
				if (!isEmptyAfterTrim(logoutHttpHeader)) {
					String headerStr = request.getHeader(logoutHttpHeader);
					if (headerStr != null) {
						String[] headerStrs = headerStr.split(";");
						if (headerStrs.length > 0) {
							urlToUse = headerStrs[0];
						}
					}
				}
			}

			this.logoutUrlToUse = (isEmptyAfterTrim(urlToUse) ? request.getContextPath() + DEFAULT_LOGOUT_URL : urlToUse);
		}
	}

	public String getLogoutUrlToUse() {
		return logoutUrlToUse;
	}

	@Override
	public String toString() {
		return String.format(
				"SessionConfig[maxSesions=%d,userIDCookie=%s,loginUrl=%s,logoutUrl=%s,logoutHttpHeader=%s,userAuthProvider=%s",
				maxUserSessions,
				userIDCookie,
				loginUrl,
				logoutUrlFromConfig,
				logoutHttpHeader,
				userAuthenticationProviderClass.getName());
	}
}
