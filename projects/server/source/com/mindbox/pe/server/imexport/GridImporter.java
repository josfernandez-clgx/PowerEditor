package com.mindbox.pe.server.imexport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.imexport.digest.Grid;

final class GridImporter extends AbstractImporter<TemplateImportOptionalData> {

	private static int findMappedID(Map<Integer, Integer> idMap, int key) {
		if (idMap != null && !idMap.isEmpty()) {
			Integer intObj = idMap.get(new Integer(key));
			if (intObj != null) return intObj.intValue();
		}
		return key;
	}

	protected GridImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, TemplateImportOptionalData optionalData)
			throws ImportException {
		int[] counts = processGrids(objectHolder.getObjects(Grid.class), optionalData);
		if (counts[0] > 0) {
			importResult.addMessage("  Imported " + counts[0] + " guideline grids", "File: " + filename);
		}
		if (counts[1] > 0) {
			importResult.addMessage("  Imported " + counts[1] + " parameter grids", "File: " + filename);
		}
	}

	/**
	 * 
	 * @param list
	 * @param user
	 * @return int[]{guideline-grid-count, paramter-grid-count}<
	 */
	private int[] processGrids(List<Grid> list, TemplateImportOptionalData optionalData) {
		logger.debug(">>> processGrids: " + list.size() + ",merge=" + merge);
		int gcount = 0;
		int pcount = 0;
		boolean hasError = false;
		for (int i = 0; i < list.size(); i++) {
			Grid grid = list.get(i);
			// skip grids for unimported templates
			if (!optionalData.getUnimportedTemplateIDs().contains(grid.getTemplateID())) {
				if (grid.getType().equalsIgnoreCase(Grid.GRID_TYPE_GUIDELINE)) {
					if (merge) {
						grid.setTemplateID(findMappedID(optionalData.getTemplateIDMap(), grid.getTemplateID()));
					}
					// build grid lists for merge first
					try {
						List<ProductGrid> gridList = ObjectConverter.asGuidelineGridList(
								grid,
								ObjectConverter.fetchContext(grid, merge, optionalData.getEntityIDMap()),
								merge,
								user,
								optionalData.getDateSynonymIDMap(),
								optionalData.getEntityIDMap(),
								optionalData.getReplacementDateSynonymProvider());
						if (gridList != null && !gridList.isEmpty()) {
							try {
								importBusinessLogic.importGridData(gridList.get(0).getTemplateID(), gridList, user);
								gcount += gridList.size();
							}
							catch (ImportException ex) {
								addError(gridList.get(0), ex);
							}
						}
					}
					catch (ImportException ex) {
						addError(grid.toString(), ex);
						if (!hasError) hasError = true;
					}
					catch (DataValidationFailedException ex) {
						addErrors(grid.toString(), ex);
						if (!hasError) hasError = true;
					}
				}
			}
		}
		logger.debug("... processGrids: imported guidelines = " + gcount + ",hasError=" + hasError);
		logger.debug("... processGrids: processing parameter grids...");
		List<List<ParameterGrid>> paramGridListList = new ArrayList<List<ParameterGrid>>();
		hasError = false;
		for (int i = 0; i < list.size(); i++) {
			Grid grid = list.get(i);
			if (grid.getType().equalsIgnoreCase(Grid.GRID_TYPE_PARAMETER)) {
				// build grid lists for merge first
				try {
					List<ParameterGrid> gridList = ObjectConverter.asParameterGridList(
							grid,
							ObjectConverter.fetchContext(grid, merge, optionalData.getEntityIDMap()),
							user,
							optionalData.getDateSynonymIDMap(),
							optionalData.getReplacementDateSynonymProvider());
					ObjectConverter.addAndMergeGridContextWithSameDataIfFound(paramGridListList, gridList);
				}
				catch (ImportException ex) {
					addError(grid.toString(), ex);
					if (!hasError) hasError = true;
				}
				catch (DataValidationFailedException ex) {
					addErrors(grid.toString(), ex);
					if (!hasError) hasError = true;
				}
			}
		}
		logger.debug("... processGrids: parameter listList.size=" + paramGridListList.size() + ",hasError=" + hasError);
		if (!hasError) {
			for (List<ParameterGrid> gridList : paramGridListList) {
				if (gridList != null && !gridList.isEmpty()) {
					ParameterGrid firstGrid = gridList.get(0);
					try {
						for (Iterator<ParameterGrid> iter2 = gridList.iterator(); iter2.hasNext();) {
							ParameterGrid element = (ParameterGrid) iter2.next();
							importBusinessLogic.importParameterGrid(element, user);
						}
						pcount += gridList.size();
					}
					catch (ImportException ex) {
						addError(firstGrid, ex);
					}
				}
			}
		}
		logger.debug("<<< processGrids: gc=" + gcount + ",pc=" + pcount);
		return new int[] { gcount, pcount };
	}

}
