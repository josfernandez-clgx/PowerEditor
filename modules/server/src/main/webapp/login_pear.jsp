<%@ page errorPage="/error.jsp" %>
<%@ page import="com.mindbox.pe.communication.*" %>
<%@ page import="com.mindbox.pe.model.UserProfile" %>
<%@ page import="com.mindbox.pe.server.cache.SessionManager" %>
<%@ page import="com.mindbox.pe.server.servlet.handlers.*" %>
<%@ page import="java.lang.Integer" %>
<%@ page import="java.lang.StringBuilder" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="javax.servlet.ServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>

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
 */
private static LoginResponse performLogin(HttpServletRequest request, String userID, String password) {
	if (request == null) throw new NullPointerException("request cannot be null");
	if (userID == null) throw new NullPointerException("userID cannot be null");
	if (password == null) throw new NullPointerException("password cannot be null");

	LoginResponse loginResponse = (LoginResponse) new LoginRequestHandler().handleRequest(new LoginRequest(userID, password), request);
	return loginResponse;
}
%>

<%
boolean login_status = false;
String userID = request.getParameter("userID");
String pwd = request.getParameter("password");

if (userID == null || userID.trim().length() == 0 || pwd == null || pwd.trim().length() == 0) {
} else {
	// authenticate
	LoginResponse loginResponse = null;
	try {
		loginResponse = performLogin((HttpServletRequest)request, userID, pwd);
	}
	catch (Exception ex) {
		ex.printStackTrace();
	} // catch
	if (loginResponse == null || !loginResponse.isAuthenticated()) {
	}
	else if (loginResponse.isAuthenticated() && loginResponse.isPasswordNeedsReset()) {
	}
	else if (loginResponse.getNotifyPasswordExpiration()) {
	}
	else {
		login_status = true;
	}
	pageContext.setAttribute("login_status", login_status);
	if (login_status) {
		response.addCookie(new Cookie("login_status", "true"));
		response.addCookie(new Cookie("ssid", (String) pageContext.getSession().getId()));
		{
			StringBuilder server = new StringBuilder();
			server.append(request.getScheme().toString());
			server.append("://");
			server.append(request.getServerName().toString());
			server.append(":");
			Integer server_port = new Integer(request.getServerPort());
			server.append(server_port.toString());
			server.append(request.getContextPath());
			server.append("/PowerEditorServlet");
			response.addCookie(new Cookie("server", server.toString()));
		}
	} else {
		response.addCookie(new Cookie("login_status", "false"));
	}
}
%>

<html>
<body>
Session id: ${pageContext.session.id}
login-status: ${login_status}
</body>
</html>
