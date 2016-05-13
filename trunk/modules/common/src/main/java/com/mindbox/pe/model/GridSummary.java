package com.mindbox.pe.model;

import java.io.Serializable;

import com.mindbox.pe.model.template.GridTemplate;

/**
 * Grid summary, encapsulating template selection table row in Manage Guideline screen.
 * @author Geneho Kim
 * @since PowerEditor 1.0
 */
public class GridSummary implements Serializable {

	private static final long serialVersionUID = 3788074788302412765L;

	private final int templateId;
	private final String name;
	private boolean locked;
	private boolean common;
	private boolean editAllowed;
	private boolean gridInstantiations;
	private boolean subContext;
	private final String version;

	public GridSummary(GridTemplate gridtemplate) {
		locked = false;
		common = false;
		editAllowed = false;
		gridInstantiations = false;
		subContext = false;
		this.templateId = gridtemplate.getID();
		this.name = gridtemplate.getName();
		this.version = gridtemplate.getVersion();
	}


	public boolean isSubContext() {
		return subContext;
	}

	public void setSubContext(boolean subContext) {
		this.subContext = subContext;
	}

	public boolean hasGridInstantiations() {
		return gridInstantiations;
	}

	public boolean isEditAllowed() {
		return editAllowed;
	}

	public String toString() {
		return "GridTemplateSummary[" + getName() + ",editAllowed=" + editAllowed + ",hasGrids=" + gridInstantiations + ",locked=" + locked
				+ ",common=" + common + ",subc=" + subContext + "]";
	}

	public String getVerion() {
		return version;
	}

	public boolean isCommon() {
		return common;
	}

	public boolean isLocked() {
		return locked;
	}

	public int getTemplateID() {
		return templateId;
	}

	public String getName() {
		return name;
	}

	public void setCommon(boolean flag) {
		this.common = flag;
	}

	public void setGridInstantiations(boolean flag) {
		this.gridInstantiations = flag;
	}

	public void setEditAllowed(boolean flag) {
		this.editAllowed = flag;
	}

	public void setLocked(boolean flag) {
		this.locked = flag;
	}

}