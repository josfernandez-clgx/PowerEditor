package com.mindbox.pe.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;

//////////////////////////////////////////////////////////////////////////
// Acknowledgements:
//
// The two methods "dateToJulian" and "julianToDate" are copied from
// com.zlst.sgo.SGOComponent.java by Zelestra (zelestra.com).
//
//////////////////////////////////////////////////////////////////////////

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class DateUtil {

	public static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
	private static final SimpleDateFormat XML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	/**
	 * Converts calendar date to julian date.
	 * Many thanks to the US Naval Observatory.
	 * @param cal the calendar date
	 * @return the julian date
	 */
	public static double dateToJulian(final Calendar cal) {
		double jy, ja, jm;

		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DATE);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int mn = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);

		if (cal.get(Calendar.ERA) == GregorianCalendar.BC) y = -y + 1;
		if (m > 2) {
			jy = y;
			jm = m + 1;
		}
		else {
			jy = y - 1;
			jm = m + 13;
		}

		double intgr = Math.floor(Math.floor(365.25 * jy) + Math.floor(30.6001 * jm) + d + 1720995);

		double gregcal = 15 + (31 * (10 + (12 * 1582)));
		if (d + (31 * (m + (12 * y))) >= gregcal) {
			ja = Math.floor(0.01 * jy);
			intgr += 2 - ja + Math.floor(0.25 * ja);
		}

		double dayfrac = (h / 24.0) - 0.5;
		if (dayfrac < 0.0) {
			dayfrac += 1.0;
			--intgr;
		}

		double frac = dayfrac + (((mn + (s / 60.0)) / 60.0) / 24.0);

		double jd0 = (intgr + frac) * 100000;
		double jd = Math.floor(jd0);
		if (jd0 - jd > 0.5) ++jd;

		return (jd / 100000);
	}

	/**
	 * @return Number of complete 24 hour timespans since d.  If d is in the future, the result will be negative (or zero if d is less than one day in the future).
	 * @throws NullPointerException if passwordChangeDate is null.
	 */
	public static int daysSince(Date d) {
		return daysSince(new Date(), d);
	}

	public static int daysSince(Date source, Date target) {
		return (int) ((source.getTime() - target.getTime()) / MILLIS_PER_DAY);
	}

	public static final boolean inTime(final boolean useDaysAgo, final int daysAgo, final Date asOfDate, final DateSynonym expDate) {
		if (!useDaysAgo) { // if the days ago is not to be used, generate for all items.
			return true;
		}
		if (expDate == null) { // if there is no expiration date, generate.
			return true;
		}
		final Date expDateDate = expDate.getDate();
		final Date dateToUse = (asOfDate == null ? new Date() : asOfDate);
		final long delta = dateToUse.getTime() - expDateDate.getTime();
		if (delta >= ((long) daysAgo * Constants.DAY_ADJUSTMENT)) {
			return false;
		}
		return true;
	}

	public static final boolean inTime(final boolean useDaysAgo, final int daysAgo, final DateSynonym expDate) {
		return inTime(useDaysAgo, daysAgo, new Date(), expDate);
	}

	/**
	 * Converts Julian date to calendar date.
	 * Many thanks to the US Naval Observatory.
	 * @param jd julian date
	 * @return the calendar date
	 */
	public static Calendar julianToDate(double jd) {

		double j1, j2, j3, j4, j5;
		double intgr = Math.floor(jd);
		double frac = jd - intgr;
		double gregjd = 2299161;

		if (intgr >= gregjd) {
			double tmp = Math.floor(((intgr - 1867216) - 0.25) / 36524.25);
			j1 = intgr + 1 + tmp - Math.floor(0.25 * tmp);
		}
		else
			j1 = intgr;

		double dayfrac = frac + 0.5;
		if (dayfrac >= 1.0) {
			dayfrac -= 1.0;
			++j1;
		}

		j2 = j1 + 1524;
		j3 = Math.floor(6680.0 + ((j2 - 2439870) - 122.1) / 365.25);
		j4 = Math.floor(j3 * 365.25);
		j5 = Math.floor((j2 - j4) / 30.6001);

		int d = (int) Math.floor(j2 - j4 - Math.floor(j5 * 30.6001));
		int m = (int) Math.floor(j5 - 1);
		if (m > 12) m -= 12;
		int y = (int) Math.floor(j3 - 4715);
		if (m > 2) --y;
		if (y <= 0) --y;

		int hr = (int) Math.floor(dayfrac * 24.0);
		int mn = (int) Math.floor((dayfrac * 24.0 - hr) * 60.0);
		double f = ((dayfrac * 24.0 - hr) * 60.0 - mn) * 60.0;
		int sc = (int) Math.floor(f);
		f -= sc;
		if (f > 0.5) ++sc;

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.set(Calendar.ERA, (y < 0) ? GregorianCalendar.BC : GregorianCalendar.AD);
		c.set(Calendar.YEAR, ((y < 0) ? -y : y));
		c.set(Calendar.MONTH, m - 1);
		c.set(Calendar.DATE, d);
		c.set(Calendar.HOUR_OF_DAY, hr);
		c.set(Calendar.MINUTE, mn);
		c.set(Calendar.SECOND, sc);

		return c;
	}

	public static Calendar stripTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}


	public static Date toDate(String dateXMLStr) {
		if (dateXMLStr == null || dateXMLStr.trim().length() < 1) {
			return null;
		}
		try {
			return XML_DATE_FORMAT.parse(dateXMLStr);
		}
		catch (Exception ex) {
			return null;
		}
	}

	public static Date toDate(XMLGregorianCalendar xmlGregorianCalendar) {
		return xmlGregorianCalendar.toGregorianCalendar().getTime();
	}

	public static String toDateXMLString(Date date) {
		if (date == null) {
			return "";
		}
		else {
			synchronized (XML_DATE_FORMAT) {
				return XML_DATE_FORMAT.format(date);
			}
		}
	}

	public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
		if (date == null) return null;

		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date);

		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		}
		catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private DateUtil() {
	}
}
