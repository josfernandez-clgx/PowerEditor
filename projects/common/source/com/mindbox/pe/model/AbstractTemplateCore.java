package com.mindbox.pe.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Encapsulates templates.
 * 
 * @author Geneho Kim
 * 
 */
public abstract class AbstractTemplateCore<C extends AbstractTemplateColumn> extends AbstractIDNameDescriptionObject {

	private static final long serialVersionUID = 2003062012258000L;

	public static final String DEFAULT_STATUS_DRAFT = Constants.DRAFT_STATUS;

	public static final String DEFAULT_VERSION = "1.0";

	private TemplateUsageType usageType;

	private final List<C> columnList;

	private String status;

	/** @since PowerEditor 4.2.0 */
	private String version;

	private int maxNumOfRows;

	private boolean fitGridToScreen;

	private String comment;

	private int parentTemplateID = -1;

	public AbstractTemplateCore(int templateID, String name, TemplateUsageType usageType) {
		this(templateID, name, usageType, Integer.MAX_VALUE, "");
	}

	public AbstractTemplateCore(int templateID, String name, TemplateUsageType usageType, int maxRows, String description) {
		super(templateID, name, description);
		this.usageType = usageType;
		this.columnList = new LinkedList<C>();
		this.maxNumOfRows = maxRows;
	}

	protected AbstractTemplateCore(AbstractTemplateCore<C> source) {
		super(source);
		this.columnList = new LinkedList<C>();
		copyThisInvariants(source);
	}

	public final boolean hasRuleIDColumn() {
		for (C column : columnList) {
			if (column.getColumnDataSpecDigest().isRuleIDType()) {
				return true;
			}
		}
		return false;
	}

	public List<String> getRuleIDColumnNames() {
		List<String> ruleIDColList = new ArrayList<String>();
		for (C column : getColumns()) {
			if (column.getColumnDataSpecDigest().isRuleIDType()) {
				ruleIDColList.add(column.getName());
			}
		}
		return ruleIDColList;
	}
	
	private void copyThisInvariants(AbstractTemplateCore<C> template) {
		this.version = template.version;
		this.comment = template.comment;
		this.fitGridToScreen = template.fitGridToScreen;
		this.maxNumOfRows = template.maxNumOfRows;
		this.status = template.status;
		this.usageType = template.usageType;
		this.parentTemplateID = template.parentTemplateID;
		// copy columns
		this.columnList.clear();
		for (int i = 0; i < template.getNumColumns(); i++) {
			addColumn(createTemplateColumn(template.getColumn(i + 1)));
		}
	}

	public void copyFrom(AbstractTemplateCore<C> template) {
		setName(template.getName());
		setDescription(template.getDescription());
		copyThisInvariants(template);
	}

	public void swapTemplateColumns(int colNum1, int colNum2) {
		int minColNum = Math.min(colNum1, colNum2);
		C minCol = getColumn(minColNum);
		int minIndex = columnList.indexOf(minCol);
		int maxColNum = Math.max(colNum1, colNum2);
		C maxCol = getColumn(maxColNum);
		int maxIndex = columnList.indexOf(maxCol);
		columnList.remove(minCol);
		columnList.remove(maxCol);
		columnList.add(minIndex, maxCol);
		columnList.add(maxIndex, minCol);
		minCol.setID(maxColNum);
		maxCol.setID(minColNum);
		adjustChangedColumnReferences(minColNum, 1000);
		adjustChangedColumnReferences(maxColNum, minColNum);
		adjustChangedColumnReferences(1000, maxColNum);
	}

	protected abstract C createTemplateColumn(C source);

	/**
	 * Gets the id of the parent template.
	 * 
	 * @return the parent template id
	 * @since PowerEditor 4.0.0
	 */
	public final int getParentTemplateID() {
		return parentTemplateID;
	}

	public final void setParentTemplateID(int parentTemplateID) {
		this.parentTemplateID = parentTemplateID;
	}

	public boolean fitToScreen() {
		return fitGridToScreen;
	}

	public final void setFitToScreen(boolean fitToScreen) {
		this.fitGridToScreen = fitToScreen;
	}

	/**
	 * @return Returns the comment.
	 */
	public final String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            The comment to set.
	 */
	public final void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gest the version of this.
	 * 
	 * @return the version
	 * @since PowerEditor 4.2.0
	 */
	public final String getVersion() {
		return version;
	}

	/**
	 * Sets the version to the specified value.
	 * 
	 * @param version
	 *            new version
	 * @since PowerEditor 4.2.0
	 */
	public final void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return Returns the status.
	 */
	public final String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public final void setStatus(String status) {
		this.status = status;
	}

	public final void addColumn(C column) {
		columnList.add(column);
	}

	public final List<C> getColumns() {
		return Collections.unmodifiableList(columnList);
	}

	public final C getColumn(int colNo) {
		for (C element : columnList) {
			if (element.getColumnNumber() == colNo) {
				return element;
			}
		}
		return null;
	}

	public final C getColumn(String columnName) {
		for (int j = 0; j < columnList.size(); j++) {
			C c = columnList.get(j);
			if (c.getName().equals(columnName)) return c;
		}
		return null;
	}

	public final C findColumnWithTitle(String title, boolean ignoreCase) {
		for (int j = 0; j < columnList.size(); j++) {
			C column = columnList.get(j);
			if ((ignoreCase && column.getTitle().equalsIgnoreCase(title)) || column.getTitle().equals(title)) {
				return column;
			}
		}
		return null;
	}

	/**
	 * Removes all columns. This performs no column reference checks.
	 */
	public final void removeAllTemplateColumns() {
		columnList.clear();
	}

	public final void removeTemplateColumn(int columnNo) {
		columnList.remove(getColumn(columnNo));
		// remove all references within the rule and action
		adjustDeletedColumnReferences(columnNo);

		C col = getColumn(++columnNo);
		while (col != null) {
			col.setID(columnNo - 1);
			adjustChangedColumnReferences(columnNo, columnNo - 1);
			col = getColumn(++columnNo);
		}
	}

	protected abstract void adjustDeletedColumnReferences(int columnNo);

	protected abstract void adjustChangedColumnReferences(int columnNo, int newColumnNo);

	/**
	 * Added for digest support.
	 * 
	 * @param str
	 * @since PowerEditor 3.2.0
	 */
	public final void setMaxRows(String str) {
		try {
			setMaxNumOfRows(Integer.parseInt(str));
		}
		catch (Exception ex) {
		}
	}

	public final int getMaxNumOfRows() {
		return maxNumOfRows;
	}

	public final int getNumColumns() {
		return getColumnCount();
	}

	public final void setMaxNumOfRows(int i) {
		maxNumOfRows = i;
	}

	/**
	 * Gets the number of columns in this template.
	 * 
	 * @return the column count
	 */
	public final int getColumnCount() {
		return columnList.size();
	}

	public final void setUsageType(TemplateUsageType s) {
		usageType = s;
	}

	/**
	 * Added for digest support.
	 * 
	 * @since PowerEditor 3.2.0
	 */
	public final void setUsageTypeString(String typeStr) {
		setUsageType(TemplateUsageType.valueOf(typeStr));
	}

	public final TemplateUsageType getUsageType() {
		return usageType;
	}

	public boolean equals(Object obj) {
		return (obj instanceof AbstractTemplateCore && super.equals(obj));
	}

	public List<C> getEntityTypeColumns() {
		List<C> list = new ArrayList<C>();
		for (C column : columnList) {
			if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
				list.add(column);
			}
		}
		return list;
	}

	public boolean hasEntityTypeColumns() {
		for (C column : columnList) {
			if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasEntityTypeColumnFor(GenericEntityType type, boolean forEntity) {
		if (type == null) throw new NullPointerException("type cannot be null");
		for (C column : columnList) {
			if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)
					&& column.getColumnDataSpecDigest().getEntityType().equals(type.getName())
					&& ((column.getColumnDataSpecDigest().isEntityAllowed() && forEntity) || column.getColumnDataSpecDigest().isCategoryAllowed()
							&& !forEntity)) {
				return true;
			}
		}
		return false;
	}
}
