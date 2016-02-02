package com.mindbox.pe.model.comparator;

import java.util.Date;

/**
 * Orders activations from the most recent to the oldest. 
 * @author Geneho Kim
 *
 */
public abstract class AbstractDateRangeComparator {

	protected AbstractDateRangeComparator() {
	}

	protected final int compare(Date effDate1, Date expDate1, Date effDate2, Date expDate2) {
		// check sunrise/effective date first order by most recent.
		// null is negative infinity thus older/less than non-null
		int result = compare(effDate1, effDate2, true, true);
		if (result != 0) {
			return result;
		}
		else { // if tie check sunset/expiration. most recent should be more and null is bigger
			result = compare(expDate1, expDate2, true, false);
			return result;
		}
	}
	
	protected final int compare(Date date1, Date date2, boolean reverseNaturalOrder, boolean isNullBigger) {
		if (date1 != null && date2 != null) {
			int result = date1.compareTo(date2);
			if (reverseNaturalOrder) result *= -1;
			return result;
		}
		else if (date1 == null && date2 == null) {
			return 0;
		}
		else if (date1 == null) {
			return (isNullBigger ? 1 : -1);
		}
		else {
			return (isNullBigger ? -1 : 1);
		}
	}

}
