<%@ include file="/includes/global.jsp" %>

<pe:get-configuration var="configuration" serverLogVar="serverLogConfig" configXmlContentVar="configXmlContentString" />

<!DOCTYPE html PUBLIC "-//W3C//Dtd XHTML 1.0 Transitional//EN" "http://www.w3.org/tr/xhtml1/Dtd/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title><pe:write-application-title /> - Configuration Validation</title>
<meta http-equiv="Pragma" content="no-cache">
<link rel="stylesheet" href="styles/mb_style.css" type="text/css">
</head>
<body class='mb'>
<span class='title'>PowerEditor Configuration Validator</span>
<br/></br>

<c:if test="${not empty configuration.powerEditorConfiguration.knowledgeBaseFilter.dateFilter.endDate}">
	<b>PowerEditor is in READ-ONLY mode because KB Date Filter's end date is set.</b>
</c:if>

<table class="result" width="100%" border="0" cellpadding="3" cellspacing="1">
<tbody>
	<tr class="heading">
		<td colspan="2">Version Information</td>
	</tr>
	<tr class="row2">
		<td>PowerEditor Version</td>
		<td><c:out value="${configuration.appVersion} (${configuration.appBuild})"/></td>
	</tr>
	<tr class="row1">
		<td>Java Version</td>
		<td><%=System.getProperty("java.version")%> by <%=System.getProperty("java.vendor")%> at <%=System.getProperty("java.home")%></td>
	</tr>
	<tr class="row2">
		<td>Operating System Version</td>
		<td><%=System.getProperty("os.name")%> <%=System.getProperty("os.version")%></td>
	</tr>
	<tr class="row1">
		<td>User Name</td>
		<td><%=System.getProperty("user.name")%></td>
	</tr>
	<tr class="row2">
		<td><nobr>Membery Usage (free/total/max)</nobr></td>
		<td><%=(Runtime.getRuntime().freeMemory()/1024)%>/<%=(Runtime.getRuntime().totalMemory()/1024)%>/<%=Runtime.getRuntime().maxMemory()/1024%> (KB)</td>
	</tr>
	<tr class="heading">
		<td colspan="2">KnowledgedBase Filter</td>
	</tr>
	<c:choose>
		<c:when test="${not empty configuration.powerEditorConfiguration.knowledgeBaseFilter.dateFilter}">
			<tr class="row1">
				<td>DateFilter - Begin Date</td>
				<td><fmt:formatDate value="${pe:convertToDate(configuration.powerEditorConfiguration.knowledgeBaseFilter.dateFilter.beginDate)}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
			<tr class="row2">
				<td>DateFilter - End Date</td>
				<td><fmt:formatDate value="${pe:convertToDate(configuration.powerEditorConfiguration.knowledgeBaseFilter.dateFilter.endDate)}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr class="row1"><td>DateFilter</td><td>NOT SPECIFIED</td></tr>
		</c:otherwise>
	</c:choose>
</tbody>
</table>

<h2>Configuration File Content</h2>
<div style="overflow-y: auto; height: 500px; border: 2px solid #444;">
<pre>
<c:out value="${configXmlContentString}" />
</pre>
</div>

<pe:get-system-properties var="systemProperties" />

<h2>System Properties</h2>
<div style="overflow-y: auto; height: 500px; border: 2px solid #444;">
<table class="result" width="100%" border="1" cellpadding="1" cellspacing="0">
<tbody>
	<c:forEach var="mapEntry" items="${systemProperties}">
		<tr>
			<td><c:out value="${mapEntry.key}"/></td>
			<td><c:out value="${mapEntry.value}" default="&nbsp;"/></td>
		</tr>
	</c:forEach>
</tbody>
</table>
</div>

</body>
</html>
