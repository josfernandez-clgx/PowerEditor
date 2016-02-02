package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.AbstractGrid;

/**
 * Orders activations from the most recent to the oldest. 
 * @author Geneho Kim
 *
 */
public class ActivationsComparator extends AbstractDateRangeComparator implements Comparator<AbstractGrid<?>> {

	private static ActivationsComparator instance;

	public static ActivationsComparator getInstance() {
		if (instance == null) {
			instance = new ActivationsComparator();
		}
		return instance;
	}

	private ActivationsComparator() {
	}

	public int compare(AbstractGrid<?> grid1, AbstractGrid<?> grid2) {
		if (grid1 == grid2) return 0;
		return compare(grid1.getSunrise(), grid1.getSunset(), grid2.getSunrise(),grid2.getSunset());
	}
}
