<%@ include file="/includes/global.jsp" %>

<%@ page import="com.mindbox.pe.server.spi.ServiceProviderFactory" %>
<%@ page import="com.mindbox.pe.server.cache.SessionManager" %>

<pe:get-app-version var="peVersion" />

<html>
<head><title><pe:write-application-title /> - Change Password</title>
<meta http-equiv="Pragma" content="no-cache">
<LINK rel="stylesheet" href="styles/mb_style.css" type="text/css">
<meta http-equiv="Pragma" content="no-cache">
<script type="text/javascript" language="Javascript">
<!-- //
function validateChangePasswordForm(form) {
	if (form.userID.value == null || form.userID.value == "") {
		alert("Please provide user id");
		return false;
	}
	if (form.oldPassword.value == null || form.oldPassword.value == "") {
		alert("Please provide old password.");
		return false;
	}
	if (form.newPassword.value == null || form.newPassword.value == "") {
		alert("Please provide new password.");
		return false;
	}
	if (form.confirmNewPassword.value == null || form.confirmNewPassword.value == "") {
		alert("Please confirm new password.");
		return false;
	}
	if(form.newPassword.value == form.oldPassword.value){
		form.oldPassword.value = "";
		form.newPassword.value = "";
		form.confirmNewPassword.value = "";
		alert("New password cannot be the same as old password. Please retype");
		return false;
	}
	if(form.newPassword.value != form.confirmNewPassword.value){
		form.newPassword.value = "";
		form.confirmNewPassword.value = "";
		alert("New passwords dont match. Please retype");
		return false;
	}
	return true;
}
// -->
</script>
</head>

<%
// If change_pwd request came from notify_pwd_expire, then a session was created with the user's old password.
// First thing we need to do is invalidate that session.
if (request.getSession() != null && request.getSession().getId() != null) {
	SessionManager.getInstance().terminateSession(request.getSession().getId());
}
%>

<%
String msg = request.getParameter("msg");
String userID = request.getParameter("userID");
%>
<BODY class="mb" onLoad='document.changePWD.userID.focus();'>
<center>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr><td width="100%"><img src="images/blank.gif" width="1" height="18" border="0"></td></tr>
	<tr class="header">
		<td align="center">
		<table cellpadding="0" cellspacing="2" border="0" width="820">
		<tr valign="bottom"><td width="100%" align="center"><img src="images/MB.gif" border="0"></td></tr>
        <tr valign="top"><td class="title" align="center">Change Password</td></tr>
		</table>
		</td>
	</tr>
	<tr><td><img src="images/blank.gif" width="1" height="20" border="0"></td></tr>
	<tr>
		<td align="center">
		<table cellpadding="2" cellspacing="1" border="0" width="700">
			<tr><td><img src="images/blank.gif" width="1" height="4" border="0"></td></tr>
	       <% if (msg != null && msg.length() > 0) {%>
			<tr><td align="center" class='warning'><%=msg%><br><br></td></tr>
			<%}%>
			<tr><td><img src="images/blank.gif" width="1" height="4" border="0"></td></tr>
				<%  String desc = ServiceProviderFactory.getPasswordValidatorProvider().getDescription();
           		if (desc != null) { %>
		            <tr style="font-size: 10pt;" ><td align="left"><%=desc%><br></td></tr>
		            <tr><td><img src="images/blank.gif" width="1" height="4" border="0"></td></tr>
            <%  } %>			
            </td></tr>
			<tr valign="center">
				<td align="center">
			     <form method="post" name="changePWD" action="change_pwd_result.jsp" onSubmit="return validateChangePasswordForm(this);">
			     <table style="font-size: 10pt;" cellpadding="2" cellspacing="0" border="0">
					<tr>
						<td>User ID:</td>
						<% 
						  if (userID!=null && userID.length() > 0){
						%>
						<td><input type="text" name="userID" value="<%=userID%>" size="15" maxlength="15"/></td>
						<%	
							}
						 else { 
						%>
						<td><input type="text" name="userID" size="15" maxlength="15"/></td>
						<% 
						   } 
						%>
					</tr>
					<tr>
						<td>Old Password:</td>
						<td><input type="password" name="oldPassword" size="15" maxlength="50"/></td>
					</tr>
					<tr>
						<td>New Password:</td>
						<td><input type="password" name="newPassword" size="15" maxlength="50"/></td>
					</tr>
					<tr>
						<td>Confirm new Password:</td>
						<td><input type="password" name="confirmNewPassword" size="15" maxlength="50"/></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type='submit' value="Submit"/></td>
				    </tr>
				   </table>
			     </form>
			   </td>
			</tr>
			<tr><td><img src="images/blank.gif" width="1" height="24" border="0"></td></tr>
			<tr valign="center"><td style="font-size: 10pt;" align="center">
				<a href="login.jsp">Home</a>
			</td></tr>
		</table>
	  </td>
	</tr>
	<tr class="footer">
		<td align="center">
		<%@ include file="footer.jsp" %>
		</td>
	</tr>
</table>
</center>
</BODY>
</html>
