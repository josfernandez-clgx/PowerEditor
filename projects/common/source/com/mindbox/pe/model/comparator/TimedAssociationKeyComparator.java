package com.mindbox.pe.model.comparator;

import java.util.Comparator;
import java.util.Date;

import com.mindbox.pe.model.assckey.TimedAssociationKey;

/**
 * Orders TimedAssociationKey instances from the most recent to the oldest. 
 * @author Geneho Kim
 *
 */
public class TimedAssociationKeyComparator implements Comparator<TimedAssociationKey> {

	private static TimedAssociationKeyComparator instance = null;

	public static TimedAssociationKeyComparator getInstance() {
		if (instance == null) {
			instance = new TimedAssociationKeyComparator();
		}
		return instance;
	}

	private TimedAssociationKeyComparator() {
	}

	public int compare(TimedAssociationKey key1, TimedAssociationKey key2) {
		// check sunrise/effective date first order by most recent.
		// null is negative infinity thus older/less than non-null
		if (key1 == key2) return 0;
		int result = compare(key1.getEffectiveDate() == null ? null : key1.getEffectiveDate().getDate(), key2.getEffectiveDate() == null
				? null
				: key2.getEffectiveDate().getDate(), true, true);
		if (result != 0) {
			return result;
		}
		else { // if tie check sunset/expiration. most recent should be more and null is bigger
			result = compare(key1.getExpirationDate() == null ? null : key1.getExpirationDate().getDate(), key2.getExpirationDate() == null
					? null
					: key2.getExpirationDate().getDate(), true, true);
			return result;
		}
	}

	private int compare(Date date1, Date date2, boolean reverseNaturalOrder, boolean isNullBigger) {
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
