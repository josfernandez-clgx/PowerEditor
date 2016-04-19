package com.mindbox.pe.model;

import java.util.Date;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.grid.GridCellCoordinates;
import com.mindbox.pe.model.grid.GridCellSet;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.AbstractTemplateCore;

/**
 * Guideline summary date used for displaying guideline search results.
 * To get the id of the template, use {@link #getID()}.
 * @author Geneho Kim
 * @author MindBox
 */
public class GuidelineReportData extends AbstractTemplateCore<AbstractTemplateColumn> {

	private static final long serialVersionUID = 20031215700000L;

	private final DateSynonym actDate, expDate;
	private final Date creationDate;
	private final GuidelineContext[] context;
	private final boolean isEditable;
	private final String templateVersion;
	private ParameterGrid paramGrid;
	private final String dataType;
	private String matchingRowNumbers;
	private int gridID;
	private boolean isParameter;

	/** A subset of all cells of interest to the specific needs of the report data instance. Empty is interpreted as "all cells". */
	private final GridCellSet cellSubset;

	public GuidelineReportData(int templateID, int gridID, String name, TemplateUsageType usageType, String templateVersion, GuidelineContext[] context, Date creationDate, DateSynonym effDate,
			DateSynonym expDate, boolean isEditable) {
		super(templateID, name, usageType);
		this.isEditable = isEditable;
		this.context = context;
		this.creationDate = creationDate;
		this.templateVersion = templateVersion;
		this.actDate = effDate;
		this.expDate = expDate;
		this.paramGrid = null;
		this.cellSubset = new GridCellSet(new GridCellCoordinates.RowFirstComparator());
		this.dataType = "GuidelineGrid";
		this.gridID = gridID;
		this.isParameter = false;
	}

	public GuidelineReportData(int templateID, String name, ParameterGrid grid, boolean isEditable) {
		super(templateID, name, null);
		this.isEditable = isEditable;
		this.context = grid.extractGuidelineContext();
		this.creationDate = null;
		this.templateVersion = null;
		this.actDate = grid.getEffectiveDate();
		this.expDate = grid.getExpirationDate();
		this.paramGrid = grid;
		this.cellSubset = new GridCellSet(new GridCellCoordinates.RowFirstComparator());
		this.dataType = "ParameterGrid";
		this.isParameter = true;
	}

	protected void adjustChangedColumnReferences(int columnNo, int newColumnNo) {
		//		 noop
	}

	protected void adjustDeletedColumnReferences(int columnNo) {
		// noop
	}

	protected AbstractTemplateColumn createTemplateColumn(AbstractTemplateColumn source) {
		// not used for this implementation
		return null;
	}

	public boolean equals(Object obj) {
		if (obj instanceof GuidelineReportData) {
			GuidelineReportData data = (GuidelineReportData) obj;
			return (data.getID() == this.getID() && UtilBase.isSame(data.getName(), this.getName()) && data.getUsageType() == this.getUsageType() && data.isEditable == this.isEditable
					&& UtilBase.isSame(data.actDate, this.actDate) && UtilBase.isSame(data.expDate, this.expDate) && UtilBase.isSame(data.creationDate, this.creationDate)
					&& UtilBase.isSame(data.templateVersion, this.templateVersion) && UtilBase.isSame(data.dataType, this.dataType) && GuidelineContext.isIdentical(data.context, this.context));
		}
		return false;
	}

	public DateSynonym getActivationDate() {
		return actDate;
	}

	public GridCellSet getCellSubset() {
		return cellSubset;
	}

	public GuidelineContext[] getContext() {
		return context;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getDataType() {
		return dataType;
	}

	public DateSynonym getExpirationDate() {
		return expDate;
	}

	public int getGridID() {
		return gridID;
	}

	public int[] getIDsForCategoryType(int categoryTypeID) {
		for (int i = 0; i < context.length; i++) {
			if (categoryTypeID == context[i].getGenericCategoryType()) {
				return context[i].getIDs();
			}
		}
		return null;
	}

	public int[] getIDsForEntityType(GenericEntityType type) {
		for (int i = 0; i < context.length; i++) {
			if (type == context[i].getGenericEntityType()) {
				return context[i].getIDs();
			}
		}
		return null;
	}

	public String getMatchingRowNumbers() {
		return matchingRowNumbers;
	}

	public ParameterGrid getParameterGrid() {
		return paramGrid;
	}

	public final int getTemplateID() {
		return super.getID();
	}

	public String getTemplateVersion() {
		return templateVersion;
	}

	/**
	 * Tests if this has an expiration date
	 * @return <code>true</code> if this has an expiration date; <code>false</code>, otherwise
	 * @since 5.9.1
	 */
	public boolean hasExpirationDate() {
		return expDate != null;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public boolean isParameter() {
		return isParameter;
	}

	public void setGridID(int gridID) {
		this.gridID = gridID;
	}

	public void setMatchingRowNumbers(String matchingRowNumbers) {
		this.matchingRowNumbers = matchingRowNumbers;
	}

	// This can change if a parameter grid has been updated elsewhere
	// (via bulk update or the Parameters screen).
	public void setParameterGrid(ParameterGrid grid) {
		paramGrid = grid;
	}

	public String toString() {
		return "GuidelineData[" + getID() + ":" + templateVersion + "," + actDate + "," + expDate + "]";
	}
}
