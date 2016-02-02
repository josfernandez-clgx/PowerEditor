/*
 * Created on Jul 14, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.communication;

import java.util.List;

import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ProductGrid;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class GridDataResponse extends ListResponse<ProductGrid> {

	private static final long serialVersionUID = 20030712340000L;


	private final GridTemplate template;
	private final String displayString;
	
	/**
	 * @param resultList
	 */
	public GridDataResponse(GridTemplate template, List<ProductGrid> resultList, String contextDisplayString) {
		super(resultList);
		this.template = template;
		this.displayString= contextDisplayString;
	}

	public GridTemplate getTemplate() {
		return template;
	}
	
	public String getDisplayString() {
		return displayString;
	}
}
