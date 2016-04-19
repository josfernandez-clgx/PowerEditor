<%@ include file="/includes/global.jsp" %>
<%@ page import="com.mindbox.pe.server.spi.*" %>

<pe:get-login-url var="loginUrl" />
<c:choose>
	<%-- Redirect to external Login URL if PE is configured to use one. --%>
	<c:when test="${loginUrl ne '/login.jsp'}">
		<c:redirect url="${loginUrl}" />
	</c:when>
	<c:otherwise>

<html:html locale="true">
<head>
<title><pe:write-application-title /> - Login</title>
<link rel="stylesheet" href='<c:url value="/styles/mb_style.css"/>' type="text/css">
<meta http-equiv="Pragma" content="no-cache">
<script type="text/javascript" language="Javascript">
<!-- //
function validateLoginForm(form) {
	if (form.userID.value == null || form.userID.value == "") {
		alert("Please provide user id");
		form.userID.focus();
		return false;
	}
	
	var userIdStr = form.userID.value;
	if (!userIdStr.match("^[A-Za-z0-9_]+$")) {
		alert("Invalid user id. Only alphas and numbers are allowed.");
		return false;
	}
	
	if (form.password.value == null || form.password.value == "") {
		alert("Please provide password.");
		form.password.focus();
		return false;
	}
	
	return true;
}
// -->
</script>
</head>

<%
String msg = request.getParameter("msg");
String userID = request.getParameter("userid");
String pwd = request.getParameter("credential");
%>

<pe:get-configuration var="peConfig" />

<body class="mb" onLoad='document.login.userID.focus();'>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr><td width="100%"><img src="images/blank.gif" width="1" height="18" border="0"></td></tr>
<tr class="header">
	<td align="center">
	<table cellpadding="0" cellspacing="2" border="0" width="620">
	<tr valign="bottom"><td width="100%" align="center"><img src="images/MB.gif" border="0"></td></tr>
	<!--<tr valign="top"><td class="title" align="center">PowerEditor Sign In</td></tr>-->
	<tr valign="top"><td align="center"><img src="images/pe_signin.gif" border="0"></td></tr>
	</table>
	</td>
</tr>
<tr><td><img src="images/blank.gif" width="1" height="12" border="0"></td></tr>
<tr>
	<td align="center">
	<table cellpadding="2" cellspacing="1" border="0">
	<tr><td><img src="images/blank.gif" width="1" height="4" border="0"></td></tr>
<%
	if (msg != null && msg.length() > 0) { 
%>
	<tr><td class='warning'><%=msg%></td></tr>
<%	
	} 
%>
	<tr><td><img src="images/blank.gif" width="1" height="4" border="0"></td></tr>
	<tr valign="center">
		<td align="center">
		<form method="post" name="login" action="login_result.jsp" onSubmit="return validateLoginForm(this);">
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
			<td>Password:</td>
			<td><input type="password" name="password" size="15" maxlength="15"/></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type='submit' value="Sign In"/></td>
		</tr>
		</table>
		</form>
		</td>
	</tr>
	<%
	/* Checking if UserManagementProvider is LDAP and if it is, then checking if the user
	 * is allowed to change their password or not.
	 ServiceProviderFactory.getUserManagementProvider().arePasswordsPersistable()
	 */
	 /*
	if (LDAPUserManagementProvider.class.getName().equals(
		ConfigurationManager.getInstance().getServerConfiguration().getDatabaseConfig().getUserManagementProviderClassName()))
		{ // UserManagementProvider is LDAP and not PEDB
 		LDAPConnectionConfig ldapConnectionConfig = ConfigurationManager.getInstance().getLDAPConnectionConfig(); 
 			if(ldapConnectionConfig.isAllowUpdate() 
 			     && ldapConnectionConfig.isAllowChangePassword()){ // user is allowed to change their password
			 	%>
			 	<tr><td><img src="images/blank.gif" width="1" height="24" border="0"></td></tr>
				<tr valign="center"><td style="font-size: 10pt;" align="center">
					<a href="change_pwd.jsp">Change Password</a>
				</td></tr>
			 	<%	
 			}
		}
	else { // UserManagementProvider is PEDB and not LDAP
				%>
				<tr><td><img src="images/blank.gif" width="1" height="24" border="0"></td></tr>
				<tr valign="center"><td style="font-size: 10pt;" align="center">
					<a href="change_pwd.jsp">Change Password</a>
				</td></tr>
				<%	
	  }
	*/%>
	<%
	if (ServiceProviderFactory.getUserManagementProvider().arePasswordsPersistable())
		{	%>
			 	<tr><td><img src="images/blank.gif" width="1" height="24" border="0"></td></tr>
				<tr valign="center"><td style="font-size: 10pt;" align="center">
					<a href="change_pwd.jsp">Change Password</a>
				</td></tr>
			 	<%			
		}
	%>
	
	</table>
	</td>
</tr>
<tr>
	<td align='center'>
		<p style="font-size: 10pt; color: 6a2c2c; text-align: center; padding-top: 20px;">
		<c:choose>
			<c:when test="${not empty peConfig.powerEditorConfiguration.userInterface.unauthorizedAccessWarningText}">
				<c:out value="${peConfig.powerEditorConfiguration.userInterface.unauthorizedAccessWarningText}" escapeXml="false" />
			</c:when>
			<c:otherwise>
The access to and use of the application is restricted to authorized users only. Unauthorized access to the application is prohibited.
			</c:otherwise>
		</c:choose>
		</p>
	</td>
</tr>

<tr class="footer">
	<td align="center">
	<%@ include file="footer.jsp" %>
	</td>
</tr>
</table>

</body>
</html:html>

	</c:otherwise>
</c:choose>
