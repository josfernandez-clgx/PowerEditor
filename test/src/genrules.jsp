<%@ page import="com.mindbox.pe.model.deploy.*" %>

<HTML>
<HEAD>
<TITLE>PowerEditor DEPLOYMENT TESTER</TITLE>
<meta http-equiv="Pragma" content="no-cache">
<LINK rel="stylesheet" href="../mb_style.css" type="text/css">
</HEAD>

<BODY class="mb">
<br/>

<%
String status = request.getParameter("_status_");
if (status == null || status.length() == 0) {
	status = "Draft";
}
%>

<hr noshade>

<b>Generating rules...</b><br/>

<%
DeployDataContainer ddc = new DeployDataContainer(status,true,null,null,true,null,true,true,false,0);
com.mindbox.pe.server.generator.RuleGenerator ruleGenerator = new com.mindbox.pe.server.generator.RuleGenerator(status);
ruleGenerator.generate(ddc);
%>

<b>Stats:</b><br/>

<%
com.mindbox.pe.model.deploy.GenerateStats stats = ruleGenerator.getGuidelineStats();
%>
<pre>
Number of rules generated:   <%=stats.getNumRulesGenerated()%>
Number of objects generated: <%=stats.getNumObjectsGenerated()%>
Number of errors generated:  <%=stats.getNumErrorsGenerated()%>
Percentage Complete:         <%=stats.getPercentComplete()%>
</pre>
<br>
<hr noshade>
<font size="1"><%=(new java.util.Date().toString())%></font>
</BODY>
</HTML>


