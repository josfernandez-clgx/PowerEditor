package com.mindbox.pe.server.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.comparator.AbstractDateRangeComparator;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;

final class GuidelineParamsProducer {

	private static class ReverseGuidelineGridComparator extends AbstractDateRangeComparator implements Comparator<ProductGrid> {

		@Override
		public int compare(ProductGrid arg0, ProductGrid arg1) {
			if (arg0 == arg1) return 0;
			return -1 * compare(arg0.getSunrise(), arg0.getSunset(), arg1.getSunrise(), arg1.getSunset());
		}
	}

	private static class GridRowsToSkipLookup {
		private final Map<Integer, List<Integer>> rowsToSkipMap = new HashMap<Integer, List<Integer>>();

		private final List<Integer> getSkipIDList(int gridId) {
			if (!rowsToSkipMap.containsKey(gridId)) {
				rowsToSkipMap.put(gridId, new ArrayList<Integer>());
			}
			return rowsToSkipMap.get(gridId);
		}

		public final void addToSkipRows(ProductGrid grid, int rowId) {
			addToSkipRows(grid.getId(), rowId);
		}

		public void addToSkipRows(int gridId, int rowId) {
			getSkipIDList(gridId).add(rowId);
		}

		public final boolean isToSkip(ProductGrid grid, int rowId) {
			return isToSkip(grid.getId(), rowId);
		}

		public boolean isToSkip(int gridId, int rowId) {
			return getSkipIDList(gridId).contains(rowId);
		}
	}

	private static int getRowWithSameData(int row, ProductGrid source, String[] columnNames, ProductGrid target,
			GridRowsToSkipLookup skipLookup) {
		if (row > source.getNumRows()) return -1;
		// check the same row first for performance
		if (!skipLookup.isToSkip(target, row) && source.hasSameRow(row, columnNames, row, target)) {
			return row;
		}
		for (int rowi = 1; rowi <= target.getNumRows(); rowi++) {
			if (rowi != row && !skipLookup.isToSkip(target, rowi) && source.hasSameRow(row, columnNames, rowi, target)) {
				return rowi;
			}
		}
		return -1;
	}

	private static List<List<ProductGrid>> groupByContext(List<ProductGrid> grids) {
		List<List<ProductGrid>> gridGroups = new ArrayList<List<ProductGrid>>();
		if (grids.size() == 1) {
			gridGroups.add(grids);
		}
		else {
			List<Integer> skipList = new ArrayList<Integer>();
			int gridsSize = grids.size();
			// group them by context
			for (int i = 0; i < gridsSize; i++) {
				ProductGrid grid = grids.get(i);
				if (!skipList.contains(grid.getId())) {
					if (i == gridsSize - 1) {
						List<ProductGrid> list = new ArrayList<ProductGrid>();
						list.add(grid);
						gridGroups.add(list);
					}
					else {
						List<ProductGrid> list = new ArrayList<ProductGrid>();
						skipList.add(grid.getId());
						list.add(grid);
						gridGroups.add(list);
						for (int j = i + 1; j < gridsSize; j++) {
							if (grid.hasSameContext(grids.get(j))) {
								skipList.add(grids.get(j).getId());
								list.add(grids.get(j));
							}
						}
					}
				}
			}
			// separate if activations in a group have a gap
			List<List<ProductGrid>> moreGroups = new ArrayList<List<ProductGrid>>();
			for (List<ProductGrid> oneGridGroup : gridGroups) {
				if (oneGridGroup.size() > 1) {
					boolean isGapFound = false;
					List<ProductGrid> newGroup = null;
					Collections.sort(oneGridGroup, new ReverseGuidelineGridComparator());
					Iterator<ProductGrid> iter = oneGridGroup.iterator();
					DateSynonym currExpDate = iter.next().getExpirationDate();
					while (iter.hasNext()) {
						ProductGrid productGrid = iter.next();
						if (currExpDate == null || currExpDate.isSameDate(productGrid.getEffectiveDate())) {
							if (isGapFound) {
								newGroup.add(productGrid);
								iter.remove();
							}
						}
						else {
							isGapFound = true;
							newGroup = new ArrayList<ProductGrid>();
							newGroup.add(productGrid);
							moreGroups.add(newGroup);
							iter.remove();
						}
						currExpDate = productGrid.getExpirationDate();
					}
				}
			}
			if (!moreGroups.isEmpty()) {
				gridGroups.addAll(moreGroups);
			}
		}
		return gridGroups;
	}

	private final Logger logger;
	private final GeneratorErrorContainer generatorErrorContainer;

	public GuidelineParamsProducer(GeneratorErrorContainer generatorErrorContainer) {
		this.generatorErrorContainer = generatorErrorContainer;
		this.logger = Logger.getLogger(getClass());
	}

	public List<GuidelineGenerateParams> generateProductGenParms(String status, GridTemplate gridtemplate, List<ProductGrid> gridList,
			GuidelineReportFilter filter) throws RuleGenerationException {
		logger.debug(">>> buildProductGenParms: " + gridtemplate);
		LinkedList<GuidelineGenerateParams> ruleParamList = new LinkedList<GuidelineGenerateParams>();
		try {
			// filter out unapplicable grids
			List<ProductGrid> gridsToUse = new LinkedList<ProductGrid>();
			for (ProductGrid prodGrid : gridList) {
				if (logger.isDebugEnabled()) logger.debug("buildProductGenParms: processing " + prodGrid);

				// If effective date is the same as the expiration date, generate a deploy error message
				// and skip the guideline activation
				if (RuleGeneratorHelper.hasSameEffectiveAndExpirationDates(prodGrid)) {
					generatorErrorContainer.reportError("WARNING: No rules generated for activation " + prodGrid.getAuditDescription()
							+ " because it's effective date is the same as the expiration date");
				}
				// TT 1946, part (3)
				// If prodGrid contains context not allowed in the control pattern, throw RuleGenerationException
				else if (RuleGeneratorHelper.getFirstDisallowedEntityInContext(prodGrid) != null) {
					throw new RuleGenerationException(RuleGeneratorHelper.getFirstDisallowedEntityInContext(prodGrid).getDisplayName()
							+ " is not allowed in the context for " + gridtemplate.getUsageType().getDisplayName() + " guideline type");
				}
				// add only if the status is allowed and it's not too long ago.
				else if (filter == null || filter.isAcceptable(prodGrid)) {
					gridsToUse.add(prodGrid);
				}
				else {
					if (logger.isDebugEnabled()) logger.debug("Ignoring " + prodGrid + "; didn't pass the filter");
				}
			}
			// group grids with same context together
			List<List<ProductGrid>> gridGroups = groupByContext(gridsToUse);

			// process applicable grids
			for (List<ProductGrid> sameContextGrids : gridGroups) {
				produceRuleParams(sameContextGrids, ruleParamList);
			}
			logger.debug("<<< buildProductGenParms");
			return ruleParamList;
		}
		catch (RuleGenerationException ex) {
			logger.error("Failed to genereate rue-generataion-params for " + gridtemplate, ex);
			throw ex;
		}
		catch (Exception exception) {
			logger.error("Failed to genereate rue-generataion-params for " + gridtemplate, exception);
			throw new RuleGenerationException(exception.getMessage());
		}
	}

	private void produceRuleParams(List<ProductGrid> gridsWithSameContext, List<GuidelineGenerateParams> ruleParamList)
			throws RuleGenerationException, InvalidDataException {
		switch (gridsWithSameContext.size()) {
		case 0:
			break;
		case 1: {
			addRuleParamForAllRows(gridsWithSameContext.get(0), ruleParamList);
			break;
		}
		default: {
			Collections.sort(gridsWithSameContext, new ReverseGuidelineGridComparator());
			//			int maxRows = getMaxRows(gridsWithSameContext);
			String[] columnNames = gridsWithSameContext.get(0).getColumnNames();

			GridRowsToSkipLookup skipLookup = new GridRowsToSkipLookup();

			ProductGrid previousGrid;
			ProductGrid currGrid;
			ProductGrid gridToCheck;
			DateSynonym currExpDate;
			for (int gridIndex = 0; gridIndex < gridsWithSameContext.size(); gridIndex++) {
				for (int rowId = 1; rowId <= gridsWithSameContext.get(gridIndex).getNumRows(); rowId++) {
					previousGrid = null;
					currGrid = gridsWithSameContext.get(gridIndex);
					if (!skipLookup.isToSkip(currGrid, rowId)) {
						currExpDate = currGrid.getExpirationDate();
						boolean added = false;
						boolean spansMultiple = false;
						for (int i = gridIndex + 1; i < gridsWithSameContext.size() && !added; i++) {
							gridToCheck = gridsWithSameContext.get(i);
							if (!skipLookup.isToSkip(currGrid, rowId)) {
								int sameRowId = getRowWithSameData(rowId, currGrid, columnNames, gridToCheck, skipLookup);
								if (sameRowId > 0) {
									currExpDate = gridToCheck.getExpirationDate();
									skipLookup.addToSkipRows(gridToCheck, sameRowId);
									spansMultiple = true;
								}
								else if (rowId <= currGrid.getNumRows()) {
									// diff. row found; add rule param
									ruleParamList.add(new GuidelineGenerateParams(
											currGrid.getEffectiveDate(),
											currExpDate,
											currGrid,
											-1,
											rowId,
											spansMultiple));
									skipLookup.addToSkipRows(currGrid, rowId);
									added = true;
								}
								else {
									previousGrid = currGrid;
									currGrid = gridToCheck;
									currExpDate = currGrid.getExpirationDate();
								}
							}
						}
						// handle the last one
						if (!added && rowId <= currGrid.getNumRows()) {
							// check if parent had a duplicate
							if (!skipLookup.isToSkip(currGrid, rowId) && (previousGrid == null || getRowWithSameData(rowId, currGrid, columnNames, previousGrid, skipLookup) < 1)) {
								ruleParamList.add(new GuidelineGenerateParams(currGrid.getEffectiveDate(), currExpDate, currGrid, -1, rowId, spansMultiple));
								skipLookup.addToSkipRows(currGrid, rowId);
							}
						}
					}
				}
			}
		}
		}
	}

	private void addRuleParamForAllRows(ProductGrid prodGrid, List<GuidelineGenerateParams> ruleParamList) throws RuleGenerationException {
		int rowCount = prodGrid.getNumRows();
		// iterate over rows to generate a rule per grid row
		for (int rowi = 0; rowi < rowCount; rowi++) {
			// on InvalidDataException, throw it and stop rule gen.
			try {
				GuidelineGenerateParams ruleParams = new GuidelineGenerateParams(
						prodGrid.getEffectiveDate(),
						prodGrid.getExpirationDate(),
						prodGrid,
						-1,
						rowi + 1,
						false);
				ruleParamList.add(ruleParams);
			}
			catch (InvalidDataException _ex) {
				logger.error("Invalid data for " + prodGrid + " at row " + (rowi + 1) + ": " + _ex.getMessage());
				throw new RuleGenerationException("Invalid data at row " + (rowi + 1) + ": " + _ex.getMessage());
			}
		} // for
	}
}
