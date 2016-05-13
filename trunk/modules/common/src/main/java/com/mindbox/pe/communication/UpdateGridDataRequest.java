package com.mindbox.pe.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mindbox.pe.model.grid.ProductGrid;

/**
 * To retrieve grid summary (template lists) for a given template usage type.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class UpdateGridDataRequest extends AbstractGridActionRequest<UpdateGridDataResponse> {

	private static final long serialVersionUID = 2003071510003000L;

	private final List<ProductGrid> grids;
	private final List<ProductGrid> removedGrids;

	public UpdateGridDataRequest(String userID, String sessionID, int templateID, List<ProductGrid> grids, List<ProductGrid> removedGrids) {
		super(userID, sessionID, templateID);
		this.grids = new ArrayList<ProductGrid>();
		this.grids.addAll(grids);
		this.removedGrids = new ArrayList<ProductGrid>();
		this.removedGrids.addAll(removedGrids);
	}

	public List<ProductGrid> getGridList() {
		return Collections.unmodifiableList(grids);
	}

	public List<ProductGrid> getRemovedGrids() {
		return Collections.unmodifiableList(removedGrids);
	}

	@Override
	public String toString() {
		return "UpdateGridDataRequest[" + getTemplateID() + "]";
	}

}
