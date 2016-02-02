<%@ page errorPage="/error.jsp" %>
<%@ page import="com.mindbox.pe.communication.*" %>
<%@ page import="com.mindbox.pe.server.servlet.handlers.*" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@page import="com.mindbox.pe.server.ServerException"%>

<%!
/**
 * Password Change handler.
 * This returns the Password Change response object which contains authentication result.
 * @param userID user id to validate
 * @param password password to validate
 * @param request the login request
 * @return the password change response
 * @throws NullPointerException if any of <code>request</code>, <code>userID</code>, and
 *                              <code>password</code> is null
 */
private static PasswordChangeResponse performPasswordChange(HttpServletRequest request, String userID
							, String oldPassword, String newPassword, String confirmNewPassword) throws ServerException {
	if (request == null) throw new NullPointerException("request cannot be null");
	if (userID == null) throw new NullPointerException("userID cannot be null");
	if (oldPassword == null) throw new NullPointerException("password cannot be null");
	if (newPassword == null) throw new NullPointerException("new Password cannot be null");
	if (confirmNewPassword == null) throw new NullPointerException("confirm New Password cannot be null");

	PasswordChangeResponse pwdChangeResponse = (PasswordChangeResponse) new PasswordChangeRequestHandler().handleRequest( 
		new PasswordChangeRequest(userID, oldPassword,newPassword,confirmNewPassword), request);
	return pwdChangeResponse;
}
%>

<%
	String userID = request.getParameter("userID");
	String oldPassword = request.getParameter("oldPassword");
	String newPassword = request.getParameter("newPassword");
	String confirmNewPassword = request.getParameter("confirmNewPassword");

if (userID == null || userID.trim().length() == 0 
	|| oldPassword == null || oldPassword.trim().length() == 0
	|| newPassword == null || newPassword.trim().length() == 0 
	|| confirmNewPassword == null || confirmNewPassword.trim().length() == 0) {
%>
<jsp:forward page="change_pwd.jsp">
		<jsp:param name="msg" value="You must provide user ID, old password, new password and confirm new password."/>
	</jsp:forward>
	
	<%
	}
else {
	// perform password change
	try {
		PasswordChangeResponse pwdChangeResponse = performPasswordChange((HttpServletRequest)request, 
													userID, oldPassword,newPassword,confirmNewPassword );
		if (pwdChangeResponse == null || !pwdChangeResponse.succeeded()) {
		// extract failure messgae and send user back to change password page
		%>
		<jsp:forward page="change_pwd.jsp">
			<jsp:param name="msg" value="<%= pwdChangeResponse.getMsg()%>"/>
		</jsp:forward>
		<%
		}
		else {
		 // if successful, then redirect to login page with success message			
		%>
		<jsp:forward page="login.jsp">
		<jsp:param name="msg" value="Your password has changed successfully. Please use new password to login."/>
		<jsp:param name="userid" value="<%=userID%>"/>
		</jsp:forward>	
		<%
		} // if-else
	}
	catch (Exception ex) {
		ex.printStackTrace();
		%>		
		<jsp:forward page="login.jsp">
			<jsp:param name='msg' value='<%="Failed to authenticate: " + ex.getMessage() + ". Please try again."%>'/>
		</jsp:forward>		
		<%
	} // catch 
}
%>
