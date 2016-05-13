package com.mindbox.pe.model.grid;

import java.util.Date;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * Concrete representation of a grid.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class ProductGrid extends AbstractGuidelineGrid {

	private static final long serialVersionUID = 2003052400378000L;

	public static ProductGrid copyOf(ProductGrid source, GridTemplate newTemplate, DateSynonym effDate, DateSynonym expDate) {
		return new ProductGrid(source, newTemplate, effDate, expDate);
	}

	private ProductGrid(ProductGrid source, GridTemplate newTemplate, DateSynonym effDate, DateSynonym expDate) {
		super(source, newTemplate, effDate, expDate);
		this.copyEntireContext(source);
		this.copyCellValue(source);
		this.setCloneOf(source.getID());
		this.setNumRows(source.getNumRows());
		this.setStatus(DRAFT_STATUS);
		this.setStatusChangeDate(new Date());
	}

	public ProductGrid(int gridID, GridTemplate template, DateSynonym effDate, DateSynonym expDate) {
		super(gridID, template, effDate, expDate);
	}

	private ProductGrid(ProductGrid source) {
		super(source, (GridTemplate) source.getTemplate(), source.getEffectiveDate(), source.getExpirationDate());
		setCreationDate(source.getCreationDate());
		setCloneOf(source.getCloneOf());
		setID(source.getID());
		this.copyCellValue(source);
	}

	public Auditable deepCopy() {
		return new ProductGrid(this);
	}

	public boolean isParameterGrid() {
		return false;
	}
}