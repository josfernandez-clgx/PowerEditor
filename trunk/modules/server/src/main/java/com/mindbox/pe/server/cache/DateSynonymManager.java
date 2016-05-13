/*
 * Created on 2004. 12. 08.
 *
 */
package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.comparator.DateSynonymComparatorByDate;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.server.model.TimeSliceContainer;

/**
 * Date synonym cache manager.
 * @author Geneho
 * @since PowerEditor 4.2.0
 */
public class DateSynonymManager extends AbstractCacheManager {

	private static DateSynonymManager instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static DateSynonymManager getInstance() {
		if (instance == null) {
			instance = new DateSynonymManager();
		}
		return instance;
	}

	private final Map<Integer, DateSynonym> synonymMap = new HashMap<Integer, DateSynonym>();

	private DateSynonymManager() {
	}

	public void finishLoading() {

	}

	public Collection<DateSynonym> getAllDateSynonyms() {
		return synonymMap.values();
	}

	public DateSynonym getDateSynonym(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("date cannot be null");
		}
		for (Iterator<DateSynonym> iter = synonymMap.values().iterator(); iter.hasNext();) {
			DateSynonym element = iter.next();
			if (element.getDate().equals(date)) {
				return element;
			}
		}
		return null;
	}

	public DateSynonym getDateSynonym(int dateSynonymID) {
		if (dateSynonymID < 1) return null;
		return synonymMap.get(new Integer(dateSynonymID));
	}

	public DateSynonym getDateSynonym(String name, Date date) {
		if (date == null) throw new IllegalArgumentException("date cannot be null");
		if (name == null) throw new IllegalArgumentException("name cannot be null");

		for (Iterator<DateSynonym> iter = synonymMap.values().iterator(); iter.hasNext();) {
			DateSynonym element = iter.next();
			if (element.getDate().equals(date) && element.getName().equals(name)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Returns the earliest DateSynonym based on date. Returns null if no DateSynonyms exist.
	 * @return DateSynonym
	 * @since 5.0.0
	 */
	public DateSynonym getEarliestDateSynonym() {
		Collection<DateSynonym> dateSynonyms = DateSynonymManager.getInstance().getAllDateSynonyms();

		if (dateSynonyms == null || dateSynonyms.isEmpty()) {
			return null;
		}

		List<DateSynonym> list = new ArrayList<DateSynonym>(dateSynonyms);
		Collections.sort(list, DateSynonymComparatorByDate.getInstance());
		return list.get(0);
	}


	public boolean hasDateSynonymWithName(String name) {
		for (DateSynonym dateSynonym : synonymMap.values()) {
			if (dateSynonym.getName().equals(name)) return true;
		}
		return false;
	}

	public void insert(DateSynonym dateSynonym) {
		insert_internal(dateSynonym);
	}

	public void insert(int id, String name, String desc, Date date, boolean isNamed) {
		DateSynonym dateSynonym = (isNamed ? new DateSynonym(id, name, desc, date) : DateSynonym.createUnnamedInstance(id, name, desc, date));
		insert_internal(dateSynonym);
	}

	private void insert_internal(DateSynonym dateSynonym) {
		assert (dateSynonym.getID() != -1);
		synchronized (synonymMap) {
			synonymMap.put(new Integer(dateSynonym.getID()), dateSynonym);
		}
	}

	/**
	 * Generates a new set of time slices.
	 * @return null if there is no date synonyms defined
	 */
	public synchronized TimeSliceContainer produceTimeSlices() {
		synchronized (synonymMap) {
			if (synonymMap.isEmpty()) {
				return null;
			}
			final TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
			final List<DateSynonym> list = new ArrayList<DateSynonym>();
			list.addAll(synonymMap.values());
			Collections.sort(list, DateSynonymComparatorByDate.getInstance());
			TimeSlice.resetNextID();
			DateSynonym previous = null;
			for (final DateSynonym dateSynonym : list) {
				// skip duplicates (same date)
				if (previous == null || !previous.getDate().equals(dateSynonym.getDate())) {
					timeSliceContainer.add(TimeSlice.createInstance(previous, dateSynonym));
					previous = dateSynonym;
				}
			}
			timeSliceContainer.add(TimeSlice.createInstance(previous, null));
			timeSliceContainer.freeze();
			return timeSliceContainer;
		}
	}

	public void remove(int dateSynonymID) {
		Integer key = new Integer(dateSynonymID);
		synchronized (synonymMap) {
			if (synonymMap.containsKey(key)) {
				synonymMap.remove(key);
			}
		}
	}

	public void startLoading() {
		synchronized (synonymMap) {
			synonymMap.clear();
		}
	}

	public void update(DateSynonym dateSynonym) {
		Integer key = new Integer(dateSynonym.getID());
		synchronized (synonymMap) {
			if (synonymMap.containsKey(key)) {
				synonymMap.get(key).copyFrom(dateSynonym);
			}
		}
	}

}