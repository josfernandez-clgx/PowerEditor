<%@ page errorPage="/error.jsp" %>
<%@ page import="com.mindbox.pe.communication.*" %>
<%@ page import="com.mindbox.pe.model.UserProfile" %>
<%@ page import="com.mindbox.pe.server.cache.SessionManager" %>
<%@ page import="com.mindbox.pe.server.config.ConfigurationManager" %>
<%@ page import="com.mindbox.pe.server.servlet.handlers.*" %>
<%@ page import="java.lang.Integer" %>
<%@ page import="java.lang.StringBuilder" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="javax.servlet.ServletException" %>
<%@ page import="javax.servlet.ServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>

<%!
/**
 * This returns a string containing the logoff URL.
 */
private static String logoffURLString(HttpServletRequest request) throws ServletException {
	ConfigurationManager manager = ConfigurationManager.getInstance();
	if (null == manager) {
		throw new ServletException("Configuration.getInstance() returned null");
	}

	String logoff_url = manager.getLogoutUrlToUse();
	if (null == logoff_url) {
		manager.resetLogoutUrlToUse(request);
		logoff_url = manager.getLogoutUrlToUse();
		if (null == logoff_url) {
			throw new ServletException("manager.getLogoutUrlToUse() returned null");
		}
	} else if (!logoff_url.getClass().equals(String.class)) {
		throw new ServletException("manager.getLogoutUrlToUse() returned non-string");
	}
	return logoff_url;
}
%>

<%!
/**
 * Log in handler.
 * This returns the login response object which contains authentication result.
 * @param userID user id to validate
 * @param password password to validate
 * @param request the login request
 * @return the login response
 * @throws NullPointerException if any of <code>request</code>, <code>userID</code>, and
 *				<code>password</code> is null
 * @throws ServletException if the login fails
 */
private static void performLogin(HttpServletRequest request, String userID, String password) throws ServletException {
	if (request == null) throw new NullPointerException("request cannot be null");
	if (userID == null) throw new NullPointerException("userID cannot be null");
	if (password == null) throw new NullPointerException("password cannot be null");

	LoginResponse loginResponse = (LoginResponse) new LoginRequestHandler().handleRequest(new LoginRequest(userID, password), request);
	if (null == loginResponse) {
		throw new ServletException("performLogin() returned null");
	} else if (!loginResponse.isAuthenticated ()) {
		throw new ServletException("Authentication failure");
	} else if (loginResponse.isPasswordNeedsReset()) {
		throw new ServletException("Password needs reset");
	} else if (loginResponse.getNotifyPasswordExpiration()) {
		throw new ServletException("Password expired");
	}
}
%>

<%!
/**
 * This returns a string containing the server URL.
 */
private static String serverString(HttpServletRequest request) {
	StringBuilder server = new StringBuilder();
	server.append(request.getScheme().toString());
	server.append("://");
	server.append(request.getServerName().toString());
	server.append(":");
	Integer server_port = new Integer(request.getServerPort());
	server.append(server_port.toString());
	server.append(request.getContextPath());
	server.append("/PowerEditorServlet");
	return server.toString();
}
%>

<%
boolean login_status = false;
String userID = request.getParameter("userID");
String pwd = request.getParameter("password");

if (userID == null || userID.trim().length() == 0 || pwd == null || pwd.trim().length() == 0) {
	throw new ServletException("Bad user id");
} else {
	// authenticate
	LoginResponse loginResponse = null;
	try {
		performLogin((HttpServletRequest)request, userID, pwd);
		response.addCookie(new Cookie("login_status", "true"));
		response.addCookie(new Cookie("logoffURL", logoffURLString(request)));
		response.addCookie(new Cookie("server", serverString(request)));
		response.addCookie(new Cookie("ssid", (String) pageContext.getSession().getId()));
	}
	catch (Exception ex) {
		ex.printStackTrace();
		throw ex;
	} // catch
}
%>

<html>
<body>
Session id: ${pageContext.session.id}
login-status: ${login_status}
</body>
</html>
