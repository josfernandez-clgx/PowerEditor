<%@ include file="/includes/global.jsp"  %>

<p>
<span class='error'>
<logic:messagesPresent>
	<html:messages id="error">
	<bean:write name="error"/><br/>
	</html:messages>
</logic:messagesPresent>
</span>
<hr noshade width="40%">
<html:link forward='login'><bean:message key="link.login"/></html:link>
<p>