package com.mindbox.pe.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.grid.ProductGrid;

/**
 * To update context of grids.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 4.20
 */
public class UpdateGridContextRequest extends AbstractGridActionWithContextRequest<AbstractSimpleResponse> {

	private static final long serialVersionUID = 20050209350000L;

	private final List<ProductGrid> grids;

	public UpdateGridContextRequest(String userID, String sessionID, int templateID, List<ProductGrid> grids,
			GuidelineContext[] newContexts) {
		super(userID, sessionID, templateID, newContexts);
		this.grids = new ArrayList<ProductGrid>();
		this.grids.addAll(grids);
	}

	public String toString() {
		return "UpdateGridDataRequest[" + getTemplateID() + ",#ofGrids=" + grids.size() + "]";
	}

	public GuidelineContext[] getNewContexts() {
		return super.getContexts();
	}

	public List<ProductGrid> getGridList() {
		return Collections.unmodifiableList(grids);
	}
}