package com.mindbox.pe.server.config;

import java.util.Date;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.mindbox.pe.model.DateSynonym;

public class DateFilterConfig {

	private Date beginDate;
	private Date endDate;

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public void setBeginDateXmlString(String dateTimeString) {
		try {
			XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);
			setBeginDate(calendar.toGregorianCalendar().getTime());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEndDateXmlString(String dateTimeString) {
		try {
			XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);
			setEndDate(calendar.toGregorianCalendar().getTime());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return String.format("DateFilter[begin=%s,end=%s]", (beginDate == null ? "" : beginDate.toString()), (endDate == null
				? ""
				: endDate.toString()));
	}

	public boolean isInRange(Date date) {
		if (date == null) throw new IllegalArgumentException("date cannot be null");
		if (beginDate == null && endDate == null) {
			return true;
		}
		else {
			return (beginDate == null || !beginDate.after(date)) && (endDate == null || !endDate.before(date));
		}
	}

	public boolean isDateRangeInRange(Date fromDate, Date toDate) {
		boolean result = false;
		if (fromDate == null && toDate == null) {
			result = true;
		}
		else if (fromDate == null) {
			result = this.beginDate == null || this.beginDate.before(toDate); //!this.beginDate.after(toDate);
		}
		else if (toDate == null) {
			result = this.endDate == null || this.endDate.after(fromDate); // !this.endDate.before(fromDate);
		}
		else if (!fromDate.before(toDate)) {
			throw new IllegalArgumentException("toDate must be later than the formDate");
		}
		else {
			//result = (this.beginDate == null || !this.beginDate.after(toDate)) && (this.endDate == null || !this.endDate.before(fromDate));
			result = (this.beginDate == null || this.beginDate.before(toDate)) && (this.endDate == null || this.endDate.after(fromDate));
		}
		return result;
	}

	public boolean isDateSynonymRangeInRange(DateSynonym fromDateSynonym, DateSynonym toDateSynonym) {
		return isDateRangeInRange((fromDateSynonym == null ? null : fromDateSynonym.getDate()), (toDateSynonym == null
				? null
				: toDateSynonym.getDate()));
	}

	public boolean hasBeginOrEndDate() {
		return beginDate != null || endDate != null;
	}

	public boolean hasEndDate() {
		return endDate != null;
	}
}
