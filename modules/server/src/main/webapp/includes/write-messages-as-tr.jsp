<logic:messagesPresent>
	<html:messages id="error">
	<tr class="error">
		<td colspan="2"><bean:write name="error"/></td>
	</tr>
	</html:messages>
</logic:messagesPresent>
<logic:messagesPresent message="true">
	<html:messages message="true" id="messageStr">
	<tr class="warning">
		<td colspan="2"><bean:write name="messageStr"/></td>
	</tr>
	</html:messages>
</logic:messagesPresent>
