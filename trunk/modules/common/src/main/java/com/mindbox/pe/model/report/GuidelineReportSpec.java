/*
 * Created on 2004. 12. 9.
 *
 */
package com.mindbox.pe.model.report;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class GuidelineReportSpec extends AbstractReportSpec {

	private static final long serialVersionUID = 200412091130001L;

	public boolean isGridOn() {
		return super.isAttributeOn("grid");
	}

	public void setGridOn(boolean on) {
		super.setAttributeOn("grid", on);
	}

	public boolean isRowOn() {
		return super.isAttributeOn("row");
	}

	public void setRowOn(boolean on) {
		super.setAttributeOn("row", on);
	}

	public boolean isStatusOn() {
		return super.isAttributeOn("status");
	}

	public void setStatusOn(boolean on) {
		super.setAttributeOn("status", on);
	}

	public boolean isStatusChangeDateOn() {
		return super.isAttributeOn("statusChangeDate");
	}

	public void setStatusChangeDateOn(boolean on) {
		super.setAttributeOn("statusChangeDate", on);
	}

	public boolean isCreatedDateOn() {
		return super.isAttributeOn("createdDate");
	}

	public void setCreatedDateOn(boolean on) {
		super.setAttributeOn("createdDate", on);
	}

	public boolean isCommentsOn() {
		return super.isAttributeOn("comments");
	}

	public void setCommentsOn(boolean on) {
		super.setAttributeOn("comments", on);
	}
}