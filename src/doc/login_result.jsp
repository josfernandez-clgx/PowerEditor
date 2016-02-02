<%@ page errorPage="/error.jsp" %>
<%@ page import="com.mindbox.pe.communication.*" %>
<%@ page import="com.mindbox.pe.server.servlet.handlers.*" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="com.mindbox.pe.model.UserProfile" %>
<%@ page import="com.mindbox.pe.server.cache.SessionManager" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>

<%!
private static String cleanXSS(String value) {
	value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	value = value.replaceAll("'", "&#039;").replaceAll("\"", "&quot;");
	value = value.replaceAll("\\(", "&#040;").replaceAll("\\)", "&#041;").replaceAll("\\[", "&#091;").replaceAll("\\]", "&#093;");
	value = value.replaceAll("=", "&#061;").replaceAll("%", "&#037;");
	value = value.replaceAll("\\|", "&#124;").replaceAll("&", "&amp;").replaceAll(";", "&#059;").replaceAll("\\*", "&#042;").replaceAll(
			"\\$",
			"&#036;").replaceAll("©", "&copy;").replaceAll("\\^", "&#094;").replaceAll("\\+", "&#043;").replaceAll("\\,", "&#130;").replaceAll(
			"\\\\",
			"&#092;").replaceAll("/", "&#047;");
	value = value.replaceAll("eval\\((.*)\\)", "");
	value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
	value = value.replaceAll("script", "");
	return value;
}

/**
 * Log in handler.
 * This returns the login response object which contains authentication result.
 * @param userID user id to validate
 * @param password password to validate
 * @param request the login request
 * @return the login response
 * @throws NullPointerException if any of <code>request</code>, <code>userID</code>, and
 *                              <code>password</code> is null
 */
private static LoginResponse performLogin(HttpServletRequest request, String userID, String password) {
	if (request == null) throw new NullPointerException("request cannot be null");
	if (userID == null) throw new NullPointerException("userID cannot be null");
	if (password == null) throw new NullPointerException("password cannot be null");

	LoginResponse loginResponse = (LoginResponse) new LoginRequestHandler().handleRequest(new LoginRequest(cleanXSS(userID), cleanXSS(password)), request);
	return loginResponse;
}
%>

<%
String userID = request.getParameter("userID");
String pwd = request.getParameter("password");

if (userID == null || userID.trim().length() == 0 || pwd == null || pwd.trim().length() == 0) {
%>

<jsp:forward page="login.jsp">
	<jsp:param name="msg" value="You must provide user ID and password."/>
</jsp:forward>

<%
}
else {
	// authenticate
	LoginResponse loginResponse = null;
	try {
		loginResponse = performLogin((HttpServletRequest)request, userID, pwd);
	}
	catch (Exception ex) {
		ex.printStackTrace();
%>

<jsp:forward page="login.jsp">
	<jsp:param name='msg' value='<%="Failed to authenticate: " + ex.getMessage() + ". Please try again."%>'/>
</jsp:forward>

<%
	} // catch 
	  	if (loginResponse == null || !loginResponse.isAuthenticated()) {
			%>
			
			<jsp:forward page="login.jsp">
				<jsp:param name="msg" value="<%=loginResponse.getLoginFailureMsg()%>"/>
			</jsp:forward>
			
			<%
		}
		else if (loginResponse.isAuthenticated() && loginResponse.isPasswordNeedsReset()) {
			%>
			
			<jsp:forward page="change_pwd.jsp">
				<jsp:param name="msg" value="<%=loginResponse.getLoginFailureMsg()%>"/>
				<jsp:param name="userid" value="<%=userID%>"/>
			</jsp:forward>
			
			<%
	  	}
		else if (loginResponse.getNotifyPasswordExpiration()) {
			%>
			
			<jsp:forward page="notify_pwd_expire.jsp">
				<jsp:param name="days" value="<%=loginResponse.getDaysUntilPasswordExpiration()%>"/>
			</jsp:forward>
			
			<%
		}
		else {
			%>

			<jsp:forward page="auth/launch.jsp"/>

			<%
		}
}
%>