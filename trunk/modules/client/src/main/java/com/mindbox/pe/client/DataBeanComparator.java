package com.mindbox.pe.client;

import java.util.Comparator;

import com.mindbox.pe.model.IDNameObject;

public class DataBeanComparator implements Comparator<IDNameObject> {

	public DataBeanComparator() {
	}

	public int compare(IDNameObject simpledatabean, IDNameObject simpledatabean1) throws ClassCastException {
		if (simpledatabean == simpledatabean1) return 0;
		return simpledatabean.getName().compareTo(simpledatabean1.getName());
	}
}
