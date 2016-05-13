package com.mindbox.pe.common.diff;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.xsd.audit.ChangeDetail;

/**
 * This is thread safe.
 * @author kim
 *
 */
public class GridCellValueChangeDetailSet {

	private static class ChangeDetailComparatorImpl implements Comparator<ChangeDetail> {
		public int compare(ChangeDetail changeDetail1, ChangeDetail changeDetail2) {
			if (changeDetail1.getRowNumber() == changeDetail2.getRowNumber()) {
				return 0;
			}
			if (changeDetail1.getRowNumber() == changeDetail2.getRowNumber()) {
				return changeDetail1.getColumnId().compareTo(changeDetail2.getColumnId());
			}
			else {
				return changeDetail1.getRowNumber() < changeDetail2.getRowNumber() ? -1 : 1;
			}

		}
	}

	private static final ChangeDetailComparatorImpl COMPARATOR_INSTANCE = new ChangeDetailComparatorImpl();

	private final List<ChangeDetail> detailList = new LinkedList<ChangeDetail>();

	public void add(ChangeDetail detail) {
		synchronized (detailList) {
			if (detailList.contains(detail)) throw new IllegalArgumentException("The specified detail already exists");
			detailList.add(detail);
		}
	}

	public List<ChangeDetail> getDetailList() {
		synchronized (detailList) {
			orderDetails();
			return Collections.unmodifiableList(detailList);
		}
	}

	public Iterator<ChangeDetail> getOrderedIterator() {
		return getDetailList().iterator();
	}

	public boolean isEmpty() {
		synchronized (detailList) {
			return detailList.isEmpty();
		}
	}

	private void orderDetails() {
		Collections.sort(detailList, COMPARATOR_INSTANCE);
	}
}
