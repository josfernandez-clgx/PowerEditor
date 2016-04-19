package com.mindbox.pe.server.config;

import java.util.Date;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter.DateFilter;

public class DateFilterConfigHelper {

	private KnowledgeBaseFilter.DateFilter dateFilter;

	public DateFilterConfigHelper(DateFilter dateFilter) {
		super();
		this.dateFilter = dateFilter;
	}

	public Date getBeginDate() {
		return dateFilter.getBeginDate();
	}

	public Date getEndDate() {
		return dateFilter.getEndDate();
	}

	public boolean hasBeginOrEndDate() {
		return dateFilter.getBeginDate() != null || dateFilter.getEndDate() != null;
	}

	public boolean hasEndDate() {
		return dateFilter.getEndDate() != null;
	}

	public boolean isDateRangeInRange(Date fromDate, Date toDate) {
		boolean result = false;
		if (fromDate == null && toDate == null) {
			result = true;
		}
		else if (fromDate == null) {
			result = this.dateFilter.getBeginDate() == null || this.dateFilter.getBeginDate().before(toDate);
		}
		else if (toDate == null) {
			result = this.dateFilter.getEndDate() == null || this.dateFilter.getEndDate().after(fromDate);
		}
		else if (!fromDate.before(toDate)) {
			throw new IllegalArgumentException("toDate must be later than the formDate");
		}
		else {
			result = (this.dateFilter.getBeginDate() == null || this.dateFilter.getBeginDate().before(toDate))
					&& (this.dateFilter.getEndDate() == null || this.dateFilter.getEndDate().after(fromDate));
		}
		return result;
	}

	public boolean isDateSynonymRangeInRange(DateSynonym fromDateSynonym, DateSynonym toDateSynonym) {
		return isDateRangeInRange((fromDateSynonym == null ? null : fromDateSynonym.getDate()), (toDateSynonym == null ? null : toDateSynonym.getDate()));
	}

	public boolean isInRange(Date date) {
		if (date == null) throw new IllegalArgumentException("date cannot be null");
		if (dateFilter.getBeginDate() == null && dateFilter.getEndDate() == null) {
			return true;
		}
		else {
			return (dateFilter.getBeginDate() == null || !dateFilter.getBeginDate().after(date)) && (dateFilter.getEndDate() == null || !dateFilter.getEndDate().before(date));
		}
	}

	@Override
	public String toString() {
		return String.format("DateFilter[begin=%s,end=%s]", (dateFilter.getBeginDate() == null ? "" : dateFilter.getBeginDate().toString()), (dateFilter.getEndDate() == null
				? ""
				: dateFilter.getEndDate().toString()));
	}
}
