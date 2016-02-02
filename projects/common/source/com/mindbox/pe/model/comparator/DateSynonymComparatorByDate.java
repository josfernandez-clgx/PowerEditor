package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.DateSynonym;

public class DateSynonymComparatorByDate implements Comparator<DateSynonym> {

	private static DateSynonymComparatorByDate instance = null;

	public static DateSynonymComparatorByDate getInstance() {
		if (instance == null) {
			instance = new DateSynonymComparatorByDate();
		}
		return instance;
	}

	public int compare(DateSynonym obj1, DateSynonym obj2) {
		if (obj1 == obj2) {
			return 0;
		}
		if (obj1 == null && obj2 != null) {
			return 1;
		}
		else if (obj1 != null && obj2 == null) {
			return -1;
		}
		else {
			return obj1.getDate().compareTo(obj2.getDate());
		}
	}

}
