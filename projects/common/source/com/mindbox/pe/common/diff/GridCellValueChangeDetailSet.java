package com.mindbox.pe.common.diff;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This is thread safe.
 * @author kim
 *
 */
public class GridCellValueChangeDetailSet {

	private final List<GridCellValueChangeDetail> detailList = new LinkedList<GridCellValueChangeDetail>();

	public void add(GridCellValueChangeDetail detail) {
		synchronized (detailList) {
			if (detailList.contains(detail)) throw new IllegalArgumentException("The specified detail already exists");
			detailList.add(detail);
		}
	}

	public boolean isEmpty() {
		synchronized (detailList) {
			return detailList.isEmpty();
		}
	}

	public Iterator<GridCellValueChangeDetail> getOrderedIterator() {
		synchronized (detailList) {
			orderDetails();
			return Collections.unmodifiableList(detailList).iterator();
		}
	}

	private void orderDetails() {
		Collections.sort(detailList, new Comparator<GridCellValueChangeDetail>() {
			public int compare(GridCellValueChangeDetail arg0, GridCellValueChangeDetail arg1) {
				return arg0.compareTo(arg1);
			}
		});
	}
}
