package com.mindbox.pe.server.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter.DateFilter;

public class DateFilterConfigTest extends AbstractTestBase {

	private DateFilter dateFilterConfig;

	private DateFilterConfigHelper dateFilterConfigHelper;

	private Date getBeginDate() {
		return dateFilterConfig.getBeginDate();
	}

	private Date getEndDate() {
		return dateFilterConfig.getEndDate();
	}

	private void setBeginDate(final Date date) {
		dateFilterConfig.setBeginDate(date);
	}

	private void setEndDate(final Date date) {
		dateFilterConfig.setEndDate(date);
	}

	@Before
	public void setUp() throws Exception {
		dateFilterConfig = new DateFilter();
		dateFilterConfigHelper = new DateFilterConfigHelper(dateFilterConfig);
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithBeginAndEnd_FromAndToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		setEndDate(endDate);

		assertFalse(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate is later the end date and toDate is later than the fromDate",
				dateFilterConfigHelper.isDateRangeInRange(new Date(endDate.getTime() + 100), new Date(endDate.getTime() + 1000)));
		assertFalse(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return FALSE when toDate is earlierr the begin date and fromDate is earlier than the toDate",
				dateFilterConfigHelper.isDateRangeInRange(new Date(beginDate.getTime() - 100), new Date(beginDate.getTime() - 10)));
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithBeginAndEnd_NullFromDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		setEndDate(endDate);

		assertFalse(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return FALSE when toDate is earlier than the begin date and fromDate is null",
				dateFilterConfigHelper.isDateRangeInRange(null, new Date(beginDate.getTime() - 10)));
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithBeginAndEnd_NullToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		setEndDate(endDate);

		assertFalse(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate is later than the end date and toDate is null",
				dateFilterConfigHelper.isDateRangeInRange(new Date(endDate.getTime() + 10), null));
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithBeginDate_MatchingToDateAndFromDate() throws Exception {
		Date toDate = new Date();
		setBeginDate(toDate);

		assertFalse(
				"With begin date, #isDateRangeInRange(Date,Date) should return FALSE when toDate equals to the begin date (with from date)",
				dateFilterConfigHelper.isDateRangeInRange(new Date(toDate.getTime() - 2000L), toDate));
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithBeginDate_MatchingToDateAndNullFromDate() throws Exception {
		Date toDate = new Date();
		setBeginDate(toDate);

		assertFalse(
				"With begin date, #isDateRangeInRange(Date,Date) should return FALSE when toDate equals to the begin date (null from date)",
				dateFilterConfigHelper.isDateRangeInRange(null, toDate));
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithEndDate_MatchingFromDateAndNullToDate() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);

		assertFalse("With end date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate equals to the end date (null to date)", dateFilterConfigHelper.isDateRangeInRange(endDate, null));
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithEndDate_MatchingFromDateAndToDate() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);

		assertFalse(
				"With end date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate equals to the end date (with to date)",
				dateFilterConfigHelper.isDateRangeInRange(endDate, new Date(endDate.getTime() + 2000L)));
	}

	// #isDateRangeInRange(Date,Date) /////////////////////////////////////////////////////////////

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithNoBegin_FromAndToDate() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);

		assertFalse(
				"With no begin date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate is later than the end date and toDate is later than the fromDate",
				dateFilterConfigHelper.isDateRangeInRange(new Date(endDate.getTime() + 100), new Date(endDate.getTime() + 1000)));
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithNoBegin_NullToDate() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);
		assertFalse(
				"With no begin date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate is later than the end date and toDate is null",
				dateFilterConfigHelper.isDateRangeInRange(new Date(endDate.getTime() + 10), null));
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithNoEnd_FromAndToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);

		assertFalse(
				"With no end date, #isDateRangeInRange(Date,Date) should return FALSE when toDate is earlier than the begin date and fromDate is earlier than the toDate",
				dateFilterConfigHelper.isDateRangeInRange(new Date(beginDate.getTime() - 1000), new Date(beginDate.getTime() - 100)));
	}

	@Test
	public void testIsDateRangeInRangeNegativeCaseWithNoEnd_NullFromDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		assertFalse(
				"With no end date, #isDateRangeInRange(Date,Date) should return FALSE when toDate is earler than the begin date",
				dateFilterConfigHelper.isDateRangeInRange(null, new Date(beginDate.getTime() - 10)));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithBeginAndEnd_FromAndToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		setEndDate(endDate);

		Date fromDate = new Date(endDate.getTime() - 1000);
		Date toDate = new Date(endDate.getTime() - 100);
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is equal to the end date and fromDate is earlier than the toDate",
				dateFilterConfigHelper.isDateRangeInRange(fromDate, endDate));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is earlier than the end date and fromDate is earlier than the toDate",
				dateFilterConfigHelper.isDateRangeInRange(fromDate, toDate));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is earlier than end date",
				dateFilterConfigHelper.isDateRangeInRange(fromDate, new Date(endDate.getTime() + 100)));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is earlier than begin date",
				dateFilterConfigHelper.isDateRangeInRange(new Date(beginDate.getTime() - 100), new Date(endDate.getTime() + 100)));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithBeginAndEnd_NullArgs() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		setEndDate(endDate);

		assertTrue("With begin and end date, #isDateRangeInRange(Date,Date) should return true when fromDate and toDate are null", dateFilterConfigHelper.isDateRangeInRange(null, null));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithBeginAndEnd_NullFromDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		setEndDate(endDate);

		assertTrue(dateFilterConfigHelper.isDateRangeInRange(null, new Date(beginDate.getTime() + 1)));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate equals the end date and fromDate is null",
				dateFilterConfigHelper.isDateRangeInRange(null, endDate));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is earlier than the end date and fromDate is null",
				dateFilterConfigHelper.isDateRangeInRange(null, new Date(endDate.getTime() - 10)));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is null",
				dateFilterConfigHelper.isDateRangeInRange(null, new Date(endDate.getTime() + 10)));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithBeginAndEnd_NullToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		setEndDate(endDate);

		//		assertTrue(
		//				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when fromDate equals the end date and toDate is null",
		//				dateFilterConfigHelper.isDateRangeInRange(endDate, null));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when fromDate is earlier than the end date and toDate is null",
				dateFilterConfigHelper.isDateRangeInRange(new Date(endDate.getTime() - 10), null));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoBegin_FromAndToDate() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);

		Date fromDate = new Date(endDate.getTime() - 1000);
		Date toDate = new Date(endDate.getTime() - 100);
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is equal to the end date and fromDate is earlier than the toDate",
				dateFilterConfigHelper.isDateRangeInRange(fromDate, endDate));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is earlier than the end date and fromDate is earlier than the toDate",
				dateFilterConfigHelper.isDateRangeInRange(fromDate, toDate));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is earlier than end date",
				dateFilterConfigHelper.isDateRangeInRange(fromDate, new Date(endDate.getTime() + 100)));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoBegin_NullArgs() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);
		assertTrue("With no begin date, #isDateRangeInRange(Date,Date) should return true when fromDate and toDate are null", dateFilterConfigHelper.isDateRangeInRange(null, null));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoBegin_NullFromDate() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);
		assertTrue(dateFilterConfigHelper.isDateRangeInRange(null, new Date()));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate equals the end date and fromDate is null",
				dateFilterConfigHelper.isDateRangeInRange(null, endDate));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is earlier than the end date and fromDate is null",
				dateFilterConfigHelper.isDateRangeInRange(null, new Date(endDate.getTime() - 10)));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is null",
				dateFilterConfigHelper.isDateRangeInRange(null, new Date(endDate.getTime() + 10)));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoBegin_NullToDate() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);
		//		assertTrue(
		//				"With no begin date, #isDateRangeInRange(Date,Date) should return true when fromDate equals the end date and toDate is null",
		//				dateFilterConfigHelper.isDateRangeInRange(endDate, null));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when fromDate is earlier than the end date and toDate is null",
				dateFilterConfigHelper.isDateRangeInRange(new Date(endDate.getTime() - 10), null));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoBeginAndEnd_FromAndToDate() throws Exception {
		Date fromDate = new Date();
		Date toDate = new Date(fromDate.getTime() + 1000);
		assertTrue(dateFilterConfigHelper.isDateRangeInRange(fromDate, toDate));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoBeginAndEnd_NullArgs() throws Exception {
		assertTrue(dateFilterConfigHelper.isDateRangeInRange(null, null));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoBeginAndEnd_NullFromDate() throws Exception {
		assertTrue(dateFilterConfigHelper.isDateRangeInRange(null, new Date()));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoBeginAndEnd_NullToDate() throws Exception {
		assertTrue(dateFilterConfigHelper.isDateRangeInRange(new Date(), null));
	}

	// isDateRangeInRange(Date,Date) with Begin & End dates ///////////////////////////////////////////////

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoEnd_FromAndToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);

		Date fromDate = new Date(beginDate.getTime() + 1000);
		Date toDate = new Date(fromDate.getTime() + 1000);
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate equals the begin date and toDate is later than the fromDate",
				dateFilterConfigHelper.isDateRangeInRange(beginDate, fromDate));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate is later the begin date and toDate is later than the fromDate",
				dateFilterConfigHelper.isDateRangeInRange(fromDate, toDate));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate is before the begin date and toDate is later than the begin date",
				dateFilterConfigHelper.isDateRangeInRange(new Date(beginDate.getTime() - 100), fromDate));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoEnd_NullArgs() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		assertTrue("With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate and toDate are null", dateFilterConfigHelper.isDateRangeInRange(null, null));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoEnd_NullFromDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		//		assertTrue(
		//				"With no end date, #isDateRangeInRange(Date,Date) should return true when toDate equals the begin date and fromDate is null",
		//				dateFilterConfigHelper.isDateRangeInRange(null, beginDate));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the begin date and fromDate is null",
				dateFilterConfigHelper.isDateRangeInRange(null, new Date(beginDate.getTime() + 10)));
	}

	@Test
	public void testIsDateRangeInRangePositiveCaseWithNoEnd_NullToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate equals the begin date and toDate is null",
				dateFilterConfigHelper.isDateRangeInRange(beginDate, null));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate is earler than the begin date and toDate is null",
				dateFilterConfigHelper.isDateRangeInRange(new Date(beginDate.getTime() - 10), null));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate is later than the begin date and toDate is null",
				dateFilterConfigHelper.isDateRangeInRange(new Date(beginDate.getTime() + 10), null));
	}

	@Test
	public void testIsDateRangeInRangePositveCaseWithBeginDate_MatchingFromDateAndNullToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);

		assertTrue(
				"With begin date, #isDateRangeInRange(Date,Date) should return TRUE when fromDate equals to the begin date (null to date)",
				dateFilterConfigHelper.isDateRangeInRange(beginDate, null));
	}

	@Test
	public void testIsDateRangeInRangePositveCaseWithBeginDate_MatchingFromDateAndToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);

		assertTrue(
				"With begin date, #isDateRangeInRange(Date,Date) should return TRUE when fromDate equals to the begin date (with to date)",
				dateFilterConfigHelper.isDateRangeInRange(beginDate, new Date(beginDate.getTime() + 10)));
	}

	@Test
	public void testIsDateRangeInRangePositveCaseWithEndDate_MatchingToDateAndFromDate() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);

		assertTrue(
				"With begin date, #isDateRangeInRange(Date,Date) should return TRUE when fromDate equals to the begin date (null to date)",
				dateFilterConfigHelper.isDateRangeInRange(new Date(endDate.getTime() - 10), endDate));
	}

	@Test
	public void testIsDateRangeInRangePositveCaseWithEndDate_MatchingToDateAndNullFromDate() throws Exception {
		Date endDate = new Date();
		setEndDate(endDate);

		assertTrue("With begin date, #isDateRangeInRange(Date,Date) should return TRUE when fromDate equals to the begin date (null to date)", dateFilterConfigHelper.isDateRangeInRange(null, endDate));
	}

	@Test
	public void testIsDateRangeInRangeWithInvalidDatesIllegalArgumentException() throws Exception {
		Date fromDate = new Date();
		Date toDate = new Date(fromDate.getTime() - 100);
		try {
			dateFilterConfigHelper.isDateRangeInRange(fromDate, toDate);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testIsDateRangeInRangeWithTheSameFromAndToDate() throws Exception {
		Date beginDate = new Date();
		setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		setEndDate(endDate);

		try {
			dateFilterConfigHelper.isDateRangeInRange(beginDate, beginDate);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testIsDateSynonymRangeInRangeWithNullArgs() throws Exception {
		assertTrue(dateFilterConfigHelper.isDateRangeInRange(null, null));
	}

	@Test
	public void testIsInRangeNegativeCaseWithBeginAndEnd() throws Exception {
		setBeginDate(new Date());
		setEndDate(new Date(getBeginDate().getTime() + 10000));

		assertFalse("dateFilterConfig#isInRange(Date) should return false if passed in a date earlier than the begin date", dateFilterConfigHelper.isInRange(new Date(getBeginDate().getTime() - 10)));
		assertFalse("dateFilterConfig#isInRange(Date) should return false if passed in a date later than the end date", dateFilterConfigHelper.isInRange(new Date(getEndDate().getTime() + 10)));
	}

	@Test
	public void testIsInRangeNegativeCaseWithNoBegin() throws Exception {
		setBeginDate(new Date());
		assertFalse("dateFilterConfig#isInRange(Date) should return false if passed in a date earlier than the begin date", dateFilterConfigHelper.isInRange(new Date(getBeginDate().getTime() - 10)));
	}

	@Test
	public void testIsInRangeNegativeCaseWithNoEnd() throws Exception {
		setEndDate(new Date());
		assertFalse("dateFilterConfig#isInRange(Date) should return false if passed in a date later than the end date", dateFilterConfigHelper.isInRange(new Date(getEndDate().getTime() + 10)));
	}

	@Test
	public void testIsInRangePositiveCaseWithBeginAndEnd() throws Exception {
		setBeginDate(new Date());
		setEndDate(new Date(getBeginDate().getTime() + 10000));

		assertTrue("dateFilterConfig#isInRange(Date) should return true if passed in a date equal to the begin date", dateFilterConfigHelper.isInRange(getBeginDate()));
		assertTrue("dateFilterConfig#isInRange(Date) should return true if passed in a date equal to the end date", dateFilterConfigHelper.isInRange(getEndDate()));
		assertTrue(
				"dateFilterConfig#isInRange(Date) should return false if passed in a date between the being and the end date",
				dateFilterConfigHelper.isInRange(new Date(getBeginDate().getTime() + 1000)));
	}

	// #isDateSynonymRangeInRange(DateSynonym,DateSynonym) //////////////////////////////////////////////////////////////

	@Test
	public void testIsInRangePositiveCaseWithNoBegin() throws Exception {
		setBeginDate(new Date());
		assertTrue("dateFilterConfig#isInRange(Date) should return true if passed in a date equal to the begin date", dateFilterConfigHelper.isInRange(getBeginDate()));
		assertTrue("dateFilterConfig#isInRange should return true if passed in a date later than the begin date", dateFilterConfigHelper.isInRange(new Date(getBeginDate().getTime() + 10000)));
	}

	@Test
	public void testIsInRangePositiveCaseWithNoBeginAndEnd() throws Exception {
		assertTrue(dateFilterConfigHelper.isInRange(new Date()));
	}

	@Test
	public void testIsInRangePositiveCaseWithNoEnd() throws Exception {
		setEndDate(new Date());
		assertTrue("dateFilterConfig#isInRange(Date) should return true if passed in a date equal to the end date", dateFilterConfigHelper.isInRange(getEndDate()));
		assertTrue("dateFilterConfig#isInRange(Date) should return true if passed in a date earlier than the end date", dateFilterConfigHelper.isInRange(new Date(getEndDate().getTime() - 10)));
	}

	@Test
	public void testIsInRangeWithNullThrowsIllegalArgumentException() throws Exception {
		try {
			dateFilterConfigHelper.isInRange(null);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}
