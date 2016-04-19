package com.mindbox.pe.server.model;

import java.util.Date;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DateSynonym;

public class TimeSlice implements Comparable<TimeSlice> {

	private static final String NAME_PREFIX = "TS";

	private static long nextID = 1000L;

	private static TimeSlice createInstance() {
		synchronized (NAME_PREFIX) {
			return new TimeSlice(NAME_PREFIX + nextID++);
		}
	}

	/**
	 * Factory method for creating new instances.
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws IllegalArgumentException if both <code>startDate</code> and <code>endDate</code> is <code>null</code>
	 */
	public static TimeSlice createInstance(DateSynonym startDate, DateSynonym endDate) {
		if (startDate == null && endDate == null) throw new IllegalArgumentException("One of startDate and endDate must be not null");
		TimeSlice timeSlice = createInstance();
		timeSlice.startDate = startDate;
		timeSlice.endDate = endDate;
		return timeSlice;
	}

	public static void resetNextID() {
		synchronized (NAME_PREFIX) {
			nextID = 1000L;
		}
	}

	private final String name;
	private DateSynonym startDate, endDate;

	private TimeSlice(String name) {
		this.name = name;
	}

	/**
	 * Compares the dates of the specified time slice object.
	 * <p>
	 * Note: this class has a natural ordering that is inconsistent with equals.
	 * @param obj the time slice object; must be an instanceof {@link TimeSlice}
	 */
	public int compareTo(TimeSlice ts) {
		if (this.equals(ts)) return 0;
		if (startDate == null) {
			return (ts.startDate == null ? 0 : -1);
		}
		else {
			return this.startDate.before(ts.startDate) ? -1 : (UtilBase.isSame(endDate, ts.endDate) ? 0 : 1);
		}
	}

	/**
	 * Tests if the specified time slice object has the same name as this.
	 * @param obj the time slice object; must be an instanceof {@link TimeSlice}
	 */
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof TimeSlice) {
			return this.name.equals(((TimeSlice) obj).name);
		}
		else {
			return false;
		}
	}

	public int hashCode() {
		return name.hashCode();
	}

	public DateSynonym getEndDate() {
		return endDate;
	}

	public DateSynonym getStartDate() {
		return startDate;
	}

	public String getName() {
		return name;
	}

	public Date getAsOfDate() {
		// If timeSlice has no start date, it must have an end date
		return (startDate == null ? new Date(endDate.getDate().getTime() - 1) : startDate.getDate());
	}
	
	public String toString() {
		return "TimeSlice[name=" + name + ']';
	}
}
