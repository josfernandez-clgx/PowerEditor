<%@ include file="/includes/global.jsp" %>

<pe:get-configuration var="configuration" serverLogVar="serverLogConfig" />

<HTML>
<HEAD>
<TITLE><pe:write-application-title /> - Configuration Validation</TITLE>
<meta http-equiv="Pragma" content="no-cache">
<LINK rel="stylesheet" href="styles/mb_style.css" type="text/css">
</HEAD>
<%--
<frameset border="0" rows="0,*">
	<frame frameborder="0" noresize name="DummyFrame" src="dummy">
	<frame frameborder="0" noresize scrolling="auto" src="PowerEditorServlet?requestType=validateConfig">
</frameset>
<noframes></noframes>
--%>
<body class='mb'>
<span class='title'>PowerEditor Configuration Validator</span>
<br/></br>

<c:if test="${not empty configuration.knowledgeBaseFilterConfig.dateFilterConfig.endDate}">
	<b>PowerEditor is in READ-ONLY mode because KB Date Filter's end date is set.</b>
</c:if>

<TABLE class="result" width="100%" border="0" cellpadding="3" cellspacing="1">
<TBODY>
	<TR class="heading">
		<TD colspan="2">KnowledgedBase Filter</TD>
	</TR>
	<c:choose>
		<c:when test="${not empty configuration.knowledgeBaseFilterConfig.dateFilterConfig}">
			<TR class="row1">
				<TD>DateFilter - Begin Date</TD>
				<TD><fmt:formatDate value="${configuration.knowledgeBaseFilterConfig.dateFilterConfig.beginDate}" pattern="yyyy-MM-dd HH:mm:ss"/></TD>
			</TR>
			<TR class="row2">
				<TD>DateFilter - End Date</TD>
				<TD><fmt:formatDate value="${configuration.knowledgeBaseFilterConfig.dateFilterConfig.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/></TD>
			</TR>
		</c:when>
		<c:otherwise>
			<TR class="row1"><TD>DateFilter</TD><TD>NOT SPECIFIED</TD></TR>
		</c:otherwise>
	</c:choose>

	<TR class="heading"><TD colspan="2">Configuration Settings</TD></TR>
	<tr class="row1">
		<td><nobr>PowerEditor Configuration File</nobr></td>
		<td><c:out value="${configuration.filename}"/></td>
	</tr>
	<c:forEach var="domainFile" items="${configuration.domainDefinitionFiles}">
		<tr class="row2">
			<td>Domain Definition File</td>
			<td width="100%"><c:out value="${domainFile}"/></td>
		</tr>
	</c:forEach>	

	<tr class="row1">
		<td>Database</td>
		<td><c:out value="${configuration.serverConfiguration.databaseConfig.user}@${configuration.serverConfiguration.databaseConfig.connectionStr}"/>
		<c:if test="${not empty configuration.serverConfiguration.databaseConfig.validationQuery}">
			<c:out value="ValidationQuery: ${configuration.serverConfiguration.databaseConfig.validationQuery}"/>
		</c:if>
		</td>
	</tr>
	<tr class="row2">
		<td>LDAP Configuration</td>
		<td><c:out value="${configuration.ldapConfig}"/></td>
	</tr>
	<tr class="row1">
		<td>Deploy Directory</td>
		<td><c:out value="${configuration.serverConfiguration.deploymentConfig.baseDir}"/></td></tr>
	<tr class="row2">
		<td>Server Log</td>
		<td><c:out value="${serverLogConfig.filename}"/></td>
	</tr>
	<c:set var="featureMap" value="${configuration.featureConfiguration.featureMap}" scope="page"/>
	<tr class="row1">
		<td>CBR Feature</td>
		<td><c:choose><c:when test="${featureMap['cbr'].enable}">ON</c:when><c:otherwise>OFF</c:otherwise></c:choose></td>
	</tr>
	<tr class="row2">
		<td>Parameter Feature</td>
		<td><c:choose><c:when test="${featureMap['parameter'].enable}">ON</c:when><c:otherwise>OFF</c:otherwise></c:choose></td>
	</tr>
	<tr class="row1">
		<td>Phase Feature</td>
		<td><c:choose><c:when test="${featureMap['phase'].enable}">ON</c:when><c:otherwise>OFF</c:otherwise></c:choose></td>
	</tr>
	<tr class="row2">
		<td>Session Config</td>
		<td><c:out value="${configuration.sessionConfiguration}"/></td>
	</tr>
	<tr class="row1">
		<td>Client Window Title</td>
		<td><c:out value="${configuration.uiConfig.clientWindowTitle}"/></td>
	</tr>
	
	<TR class="heading">
		<TD colspan="2">Version Information</TD>
	</TR>
	<tr class="row2">
		<td>PowerEditor Version</td>
		<td><c:out value="${serverPackage.specificationVersion} (${serverPackage.implementationVersion})"/></td>
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
</TBODY>
</TABLE>
<br/>

<TABLE class="result" width="100%" border="0" cellpadding="3" cellspacing="0">
<tbody>
	<TR class="heading">
		<TD>Entity Configuration</TD>
	</TR>
	<tr class="colheading">
		<td>Category Type Definitions</td>
	</tr>
	<c:forEach var="catType" items="${configuration.entityConfiguration.categoryTypeDefinitions}">
		<tr class="row1">
			<td><c:out value="${catType}"/></td>
		</tr>
	</c:forEach>
	
	<tr class="colheading">
		<td>Entity Type Definitions</td>
	</tr>
	<c:forEach var="entityType" items="${configuration.entityConfiguration.entityTypeDefinitions}">
		<tr class='row1'>
			<td><c:out value="${entityType}"/></td>
		</tr>
		<c:forEach var="propDef" items="${entityType.entityPropertyDefinitions}">
			<tr class='row2'>
				<td>&nbsp;&nbsp;<c:out value="${propDef}"/></td>
			</tr>
		</c:forEach>
	</c:forEach>
</tbody>
</table>
<br/>

<c:set var="rowID" value="2"/>
<pe:get-usage-types var="allUsageTypes"/>

<TABLE class='result' width='100%' border='0' cellpadding='3' cellspacing='0'>
<thead>
	<TR class='heading'>
		<TD colspan='3'>Usage Type Configuration</TD>
	</TR>
	<TR class='colheading'>
		<td>Identifying Name</td><td>Display Name</td><td>Privilege</td>
	</tr>
</thead>
<tbody>
	<c:forEach var="usageType" items="${allUsageTypes}">
		<tr class='<c:out value="row${rowID}"/>'>
			<td><c:out value="${usageType}"/></td>
			<td><c:out value="${usageType.displayName}"/></td>
			<td><c:out value="${usageType.privilege}"/></td>
		</tr>
		<c:choose><c:when test="${rowID == 2}"><c:set var="rowID" value="1"/></c:when><c:otherwise><c:set var="rowID" value="2"/></c:otherwise></c:choose>
	</c:forEach>
</table><br/>

<pe:get-parameter-templates var="allParameterTemplates"/>

<TABLE class="result" width="100%" border="0" cellpadding="3" cellspacing="0">
<thead>
	<TR class="heading">
		<TD colspan="4">Loaded Parameter Templates</TD>
	</TR>
	<TR class="colheading">
		<td>ID</td><td>Name</td><td>No.Columns</td><td>Max Rows</td>
	</TR>
</thead>
<tbody>
	<c:forEach var="template" items="${allParameterTemplates}">
		<tr class='<c:out value="row${rowID}"/>'>
			<td><c:out value="${template.id}"/></td>
			<td><c:out value="${template.name}"/></td>
			<td><c:out value="${template.columnCount}"/></td>
			<td><c:out value="${template.maxNumOfRows}"/></td>
			<c:choose><c:when test="${rowID == 2}"><c:set var="rowID" value="1"/></c:when><c:otherwise><c:set var="rowID" value="2"/></c:otherwise></c:choose>
		</tr>
	</c:forEach>
</TABLE><br/>

<pe:get-system-properties var="systemProperties" />

<TABLE class="result" width="100%" border="1" cellpadding="1" cellspacing="0">
<thead>
	<TR class="heading">
		<TD colspan='2'>System Properties</TD>
	</TR>
</thead>
<tbody>
	<c:forEach var="mapEntry" items="${systemProperties}">
		<tr>
			<td><c:out value="${mapEntry.key}"/></td>
			<td><c:out value="${mapEntry.value}" default="&nbsp;"/></td>
		</tr>
	</c:forEach>
</tbody>
</TABLE>
</body>
</HTML>
