<%-- Defines utility methods used for template report and report schema generation --%>
<%@ page import="com.mindbox.pe.model.*,
                 com.mindbox.pe.server.Util,
                 com.mindbox.pe.server.cache.EntityManager,
                 com.mindbox.pe.server.cache.GridManager,
                 com.mindbox.pe.server.report.*,
                 java.util.*" %>

<%-- Method Declarations --%>
<%!
private static String generateElementType(GridTemplateColumn columnObj) {
	ColumnDataSpecDigest digest = columnObj.getColumnDataSpecDigest();
	if (digest.isRangeType()) {
		return "xsd:string";
	}
	else if (digest.getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
		return "xsd:boolean";
	}
	else if (digest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY)) {
		return "xsd:double";
	}
	else if (digest.getType().equals(ColumnDataSpecDigest.TYPE_DATE)) {
		return "xsd:date";
	}
	else if (digest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_TIME)) {
		return "xsd:dateTime";
	}
	else if (digest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT)) {
		return "xsd:float";
	}
	else if (digest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER)) {
		return "xsd:integer";
	}
	else if (digest.getType().equals(ColumnDataSpecDigest.TYPE_PERCENT)) {
		return "xsd:float";
	}
	else {
		return "xsd:string";
	}
}

private static String toXMLString(java.util.Date date) {
	return com.mindbox.pe.common.config.ConfigUtil.toDateXMLString(date);
}

private static String getEntityName(GenericEntityType type, int entityID) {
	GenericEntity entity = EntityManager.getInstance().getEntity(type, entityID);
	return (entity == null ?  String.valueOf(entityID) : entity.getName());
}

private static void addErrorMessage(StringBuffer buff, String message) {
		if (buff.length() > 0) {
			buff.append("; " );
		}
		buff.append(message);
}

private static String[] asValidColumnNames(String colNameStr, List templateList, StringBuffer buff) {
	if (colNameStr == null || colNameStr.trim().length() == 0) return null;
	String[] sa = colNameStr.trim().split(",");
	List list = new ArrayList();
	for (int i = 0; i < sa.length; i++) {
		if (hasColumnWithTitle(templateList, sa[i])) {
			list.add(sa[i]);
		}
		else {
			addErrorMessage(buff, sa[i] + " is not a valid column name");
		}
	}
	return (String[]) list.toArray(new String[0]);
}

private static void removeTemplateWithoutGrid(List templateList) {
	for (Iterator iter = templateList.iterator(); iter.hasNext(); ) {
		GridTemplate gridtemplate = (GridTemplate) iter.next();
		if (!GridManager.getInstance().hasGrids(gridtemplate.getID())) {
			iter.remove();
		}
	}
}

private static boolean hasColumnWithTitle(List templateList, String title) {
	for (Iterator iter = templateList.iterator(); iter.hasNext(); ) {
		GridTemplate gridtemplate = (GridTemplate) iter.next();
		if (gridtemplate.findColumnWithTitle(title, true) != null) {
			return true;
		}
	}
	return false;
}

private static int getMaxColumnCount(List templateList) {
	int max = 0;
	for (Iterator iter = templateList.iterator(); iter.hasNext();) {
		GridTemplate gridtemplate = (GridTemplate) iter.next();
		max = Math.max(max, gridtemplate.getNumColumns());
	}
	return max;
}

private static int getMaxColumnCount(List templateList, String[] colNames) {
	if (colNames == null || colNames.length == 0) return getMaxColumnCount(templateList);
	int max = 0;
	for (Iterator iter = templateList.iterator(); iter.hasNext();) {
		GridTemplate gridtemplate = (GridTemplate) iter.next();
		max = Math.max(max, countNonMatchingColumns(gridtemplate, colNames));
	}
	return max;
}

private static int countNonMatchingColumns(GridTemplate template, String[] colNames) {
	int count = 0;
	for (int col = 1; col <= template.getNumColumns(); col++) {
		if (!Util.isMember(template.getColumn(col).getTitle(), colNames, true)) {
			++count;
		}
	}
	return count;
}

private static Map buildColumnNumberElementMap(String[] names, GridTemplate template) {
	Map map = new TreeMap();
	if (names != null && names.length > 0) {
		for (int i = 0; i < names.length; i++) {
			AbstractTemplateColumn column = template.findColumnWithTitle(names[i], true);
			if (column != null) {
				map.put(new Integer(column.getID()), ReportGenerator.toElementName(names[i])+"_column");
			}
		}
	}
	
	for (int col = 1; col <= template.getNumColumns(); col++) {
		Integer colObj = new Integer(col);
		if (!map.containsKey(colObj)) {
			map.put(colObj, "column" + ReportGenerator.getColumnNamePostfix(col));
		}
	}
	return map;
}

%>


