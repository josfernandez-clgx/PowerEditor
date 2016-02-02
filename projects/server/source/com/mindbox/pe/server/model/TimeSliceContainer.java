package com.mindbox.pe.server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.comparator.DateSynonymComparatorByDate;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.server.generator.GenerationParams;
import com.mindbox.pe.server.spi.db.EntityDataHolder;

/**
 * Contains for instances of {@link com.mindbox.pe.server.model.TimeSlice} class.
 * This is thread-safe.
 * <b>Usage:</b>
 * <ol>
 * <li><code>TimeSliceContainer tsContainer = new TimeSliceContainer()</code></li>
 * <li>Add time slice objects; e.g., <code>tsContainer.add(timeSlice)</code></li>
 * <li>Call {@link #freeze()}. Now you can safely use <code>tsContainer</code>.</li>
 * </ol>
 * Note that once frozen, no changes can be made. 
 * Use {@link #isFrozen()} to check if an instance frozen.
 * @author Geneho Kim
 *
 */
public class TimeSliceContainer {

	private static final boolean checkStartDateForApplicability(DateSynonym ds1, DateSynonym startDate) {
		if (ds1 == startDate) return true;
		if (startDate == null) {
			return true;
		}
		else {
			return startDate.equals(ds1) || startDate.before(ds1);
		}
	}

	private static final boolean checkEndDateForApplicability(DateSynonym ds1, DateSynonym endDate) {
		if (ds1 == endDate) return true;
		if (ds1 == null) {
			return false;
		}
		else if (endDate == null) {
			return true;
		}
		else {
			return ds1.equals(endDate) || ds1.before(endDate);
		}
	}

	private boolean frozen = false;
	private SortedSet<TimeSlice> tsSet = new TreeSet<TimeSlice>();

	/**
	 * 
	 * @param timeSlice
	 * @throws NullPointerException if <code>timeSlice</code> is <code>null</code>
	 * @throws IllegalArgumentException if a time slice with the same name is found
	 * @throws IllegalStateException if {@link #freeze()} has been called
	 */
	public synchronized void add(TimeSlice timeSlice) {
		if (timeSlice == null) throw new NullPointerException();
		if (frozen) throw new IllegalStateException("This is frozen. No changes can be made");
		if (!tsSet.add(timeSlice)) throw new IllegalArgumentException("Time slice " + timeSlice.getName() + " was already added");
	}

	/**
	 * Freezes this and makes get methods avaiable.
	 * Note that a subsequent call after the first call is a noop.
	 *
	 */
	public synchronized void freeze() {
		frozen = true;
	}

	public synchronized boolean isFrozen() {
		return frozen;
	}

	/**
	 * 
	 * @return list of all {@link TimeSlice} instances in this
	 * @throws IllegalStateException if {@link #freeze()} has not been called
	 */
	public synchronized List<TimeSlice> getAll() {
		checkFrozen();
		List<TimeSlice> list = new LinkedList<TimeSlice>();
		list.addAll(tsSet);
		return list;
	}

	/**
	 * 
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return list of {@link TimeSlice} instances in this that are applicable to the specified date range
	 * @throws IllegalStateException if {@link #freeze()} has not been called
	 */
	public synchronized List<TimeSlice> getApplicableTimeSlices(DateSynonym startDate, DateSynonym endDate) {
		checkFrozen();
		return getApplicableTimeSlices_internal(startDate, endDate);
	}

	private List<TimeSlice> getApplicableTimeSlices_internal(DateSynonym startDate, DateSynonym endDate) {
		List<TimeSlice> list = new LinkedList<TimeSlice>();
		for (Iterator<TimeSlice> iter = tsSet.iterator(); iter.hasNext();) {
			TimeSlice element = iter.next();
			if (checkStartDateForApplicability(element.getStartDate(), startDate) && checkEndDateForApplicability(element.getEndDate(), endDate)) {
				list.add(element);
			}
		}
		return list;
	}
	
	/**
	 * Generates a list of time slice groups for which a single AE rule should be written.
	 * <p>
	 * We need to consider changes to category-category relationship over the time
	 * period that the specified ruleParams is active. We need to split time slices into
	 * groups if all of the following is true:<ol>
	 * <li>one of row data (or cell value) contains a reference to a category</li>
	 * <li>the referenced category's relationship to one of its parents or children category changes
	 *     while <code>ruleParams</code> is active</li>
	 * </ol> 
	 * In other words, the returned time slice groups represent a time period where no changes to
	 * category-to-category relationshiop occur for the referenced categories in <code>ruleParams</code>.
	 * So, if <code>ruleParams</code> does not contain any reference to a category, this returns
	 * whatever {@link TimeSliceContainer#getApplicableTimeSlices(com.mindbox.pe.model.DateSynonym, com.mindbox.pe.model.DateSynonym)}
	 * returns with sunrise and sunset date of <code>ruleParams</code>.
	 * 
	 * @param timeSliceContainer
	 * @param generationParams rule params
	 * @return a list of {@link TimeSlice} arrays
	 * @throws IllegalStateException if {@link #freeze()} has not been called
	 */
	public List<TimeSlice[]> generateTimeSliceGroups(GenerationParams generationParams, EntityDataHolder entityDataHolder) {
		checkFrozen();
		List<TimeSlice[]> resultList = new ArrayList<TimeSlice[]>();
		// check if ruleParams has at least one reference to generic category
		if (generationParams.hasGenericCategoryAsCellValue()) {
			// use a sorted set that sorts date synonyms by date
			SortedSet<DateSynonym> dateSynonymSet = new TreeSet<DateSynonym>(DateSynonymComparatorByDate.getInstance());
			// collect all date synonym used for changes in any generic category of the type of one referenced in a cell
			for (Iterator<Object> iter = generationParams.getRowData().iterator(); iter.hasNext();) {
				Object cellValue = iter.next();
				if (cellValue != null) {
					if (cellValue instanceof CategoryOrEntityValue && !((CategoryOrEntityValue) cellValue).isForEntity()) {
						dateSynonymSet.addAll(entityDataHolder.getDateSynonymsForChangesInCategoryRelationships(((CategoryOrEntityValue) cellValue).getEntityType().getCategoryType()));
					}
					else if (cellValue instanceof CategoryOrEntityValues && ((CategoryOrEntityValues) cellValue).hasGenericCategoryReference()) {
						dateSynonymSet.addAll(entityDataHolder.getDateSynonymsForChangesInCategoryRelationships(((CategoryOrEntityValue) ((CategoryOrEntityValues) cellValue).get(0)).getEntityType().getCategoryType()));
					}
				}
			}
			// Remove those that are outside of effective dates of ruleParams
			for (Iterator<DateSynonym> iter = dateSynonymSet.iterator(); iter.hasNext();) {
				DateSynonym element = iter.next();
				if (generationParams.getSunrise() != null && element.notAfter(generationParams.getSunrise())) {
					iter.remove();
				}
				else if (generationParams.getSunset() != null && element.notBefore(generationParams.getSunset())) {
					iter.remove();
				}
			}
			// Generate time slice groups
			DateSynonym startDS = generationParams.getSunrise();
			List<TimeSlice> timeSliceList;
			for (Iterator<DateSynonym> iter = dateSynonymSet.iterator(); iter.hasNext();) {
				DateSynonym element = iter.next();
				timeSliceList = getApplicableTimeSlices_internal(startDS, element);
				resultList.add(timeSliceList.toArray(new TimeSlice[0]));
				startDS = element;
			}
			timeSliceList = getApplicableTimeSlices_internal(startDS, generationParams.getSunset());
			resultList.add(timeSliceList.toArray(new TimeSlice[0]));
		}
		else {
			// if no generic category is found in the row (ruleParams), just return all time slices as a single group
			List<TimeSlice> timeSliceList = getApplicableTimeSlices_internal(generationParams.getSunrise(), generationParams.getSunset());
			resultList.add(timeSliceList.toArray(new TimeSlice[0]));
		}
		return resultList;
	}

	private final void checkFrozen() {
		if (!frozen) throw new IllegalStateException("No ready to be used; call freeze() first!");
	}
	
	public int size() {
		return tsSet.size();
	}
}
