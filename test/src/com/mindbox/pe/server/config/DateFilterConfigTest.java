package com.mindbox.pe.server.config;

import java.util.Date;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class DateFilterConfigTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DateFilterConfigTest.class);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("DateFilterConfigTest Tests");
		suite.addTestSuite(DateFilterConfigTest.class);
		return suite;
	}

	public DateFilterConfigTest(String name) {
		super(name);
	}

	public void testIsInRangePositiveCaseWithNoBeginAndEnd() throws Exception {
		assertTrue(dateFilterConfig.isInRange(new Date()));
	}

	public void testIsInRangePositiveCaseWithNoBegin() throws Exception {
		dateFilterConfig.setBeginDate(new Date());
		assertTrue(
				"dateFilterConfig#isInRange(Date) should return true if passed in a date equal to the begin date",
				dateFilterConfig.isInRange(dateFilterConfig.getBeginDate()));
		assertTrue(
				"dateFilterConfig#isInRange should return true if passed in a date later than the begin date",
				dateFilterConfig.isInRange(new Date(dateFilterConfig.getBeginDate().getTime() + 10000)));
	}

	public void testIsInRangeNegativeCaseWithNoBegin() throws Exception {
		dateFilterConfig.setBeginDate(new Date());
		assertFalse(
				"dateFilterConfig#isInRange(Date) should return false if passed in a date earlier than the begin date",
				dateFilterConfig.isInRange(new Date(dateFilterConfig.getBeginDate().getTime() - 10)));
	}

	public void testIsInRangePositiveCaseWithNoEnd() throws Exception {
		dateFilterConfig.setEndDate(new Date());
		assertTrue(
				"dateFilterConfig#isInRange(Date) should return true if passed in a date equal to the end date",
				dateFilterConfig.isInRange(dateFilterConfig.getEndDate()));
		assertTrue(
				"dateFilterConfig#isInRange(Date) should return true if passed in a date earlier than the end date",
				dateFilterConfig.isInRange(new Date(dateFilterConfig.getEndDate().getTime() - 10)));
	}

	public void testIsInRangeNegativeCaseWithNoEnd() throws Exception {
		dateFilterConfig.setEndDate(new Date());
		assertFalse(
				"dateFilterConfig#isInRange(Date) should return false if passed in a date later than the end date",
				dateFilterConfig.isInRange(new Date(dateFilterConfig.getEndDate().getTime() + 10)));
	}

	public void testIsInRangePositiveCaseWithBeginAndEnd() throws Exception {
		dateFilterConfig.setBeginDate(new Date());
		dateFilterConfig.setEndDate(new Date(dateFilterConfig.getBeginDate().getTime() + 10000));

		assertTrue(
				"dateFilterConfig#isInRange(Date) should return true if passed in a date equal to the begin date",
				dateFilterConfig.isInRange(dateFilterConfig.getBeginDate()));
		assertTrue(
				"dateFilterConfig#isInRange(Date) should return true if passed in a date equal to the end date",
				dateFilterConfig.isInRange(dateFilterConfig.getEndDate()));
		assertTrue(
				"dateFilterConfig#isInRange(Date) should return false if passed in a date between the being and the end date",
				dateFilterConfig.isInRange(new Date(dateFilterConfig.getBeginDate().getTime() + 1000)));
	}

	public void testIsInRangeNegativeCaseWithBeginAndEnd() throws Exception {
		dateFilterConfig.setBeginDate(new Date());
		dateFilterConfig.setEndDate(new Date(dateFilterConfig.getBeginDate().getTime() + 10000));

		assertFalse(
				"dateFilterConfig#isInRange(Date) should return false if passed in a date earlier than the begin date",
				dateFilterConfig.isInRange(new Date(dateFilterConfig.getBeginDate().getTime() - 10)));
		assertFalse(
				"dateFilterConfig#isInRange(Date) should return false if passed in a date later than the end date",
				dateFilterConfig.isInRange(new Date(dateFilterConfig.getEndDate().getTime() + 10)));
	}

	public void testIsInRangeWithNullThrowsIllegalArgumentException() throws Exception {
		try {
			dateFilterConfig.isInRange(null);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testIsDateRangeInRangeWithInvalidDatesIllegalArgumentException() throws Exception {
		Date fromDate = new Date();
		Date toDate = new Date(fromDate.getTime() - 100);
		try {
			dateFilterConfig.isDateRangeInRange(fromDate, toDate);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testIsDateRangeInRangeWithTheSameFromAndToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		dateFilterConfig.setEndDate(endDate);

		try {
			dateFilterConfig.isDateRangeInRange(beginDate, beginDate);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	// #isDateRangeInRange(Date,Date) /////////////////////////////////////////////////////////////

	public void testIsDateRangeInRangePositiveCaseWithNoBeginAndEnd_NullArgs() throws Exception {
		assertTrue(dateFilterConfig.isDateRangeInRange(null, null));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoBeginAndEnd_NullToDate() throws Exception {
		assertTrue(dateFilterConfig.isDateRangeInRange(new Date(), null));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoBeginAndEnd_NullFromDate() throws Exception {
		assertTrue(dateFilterConfig.isDateRangeInRange(null, new Date()));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoBeginAndEnd_FromAndToDate() throws Exception {
		Date fromDate = new Date();
		Date toDate = new Date(fromDate.getTime() + 1000);
		assertTrue(dateFilterConfig.isDateRangeInRange(fromDate, toDate));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoEnd_NullArgs() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate and toDate are null",
				dateFilterConfig.isDateRangeInRange(null, null));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoEnd_NullToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate equals the begin date and toDate is null",
				dateFilterConfig.isDateRangeInRange(beginDate, null));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate is earler than the begin date and toDate is null",
				dateFilterConfig.isDateRangeInRange(new Date(beginDate.getTime() - 10), null));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate is later than the begin date and toDate is null",
				dateFilterConfig.isDateRangeInRange(new Date(beginDate.getTime() + 10), null));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoEnd_NullFromDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		//		assertTrue(
		//				"With no end date, #isDateRangeInRange(Date,Date) should return true when toDate equals the begin date and fromDate is null",
		//				dateFilterConfig.isDateRangeInRange(null, beginDate));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the begin date and fromDate is null",
				dateFilterConfig.isDateRangeInRange(null, new Date(beginDate.getTime() + 10)));
	}

	public void testIsDateRangeInRangeNegativeCaseWithNoEnd_NullFromDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		assertFalse(
				"With no end date, #isDateRangeInRange(Date,Date) should return FALSE when toDate is earler than the begin date",
				dateFilterConfig.isDateRangeInRange(null, new Date(beginDate.getTime() - 10)));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoEnd_FromAndToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);

		Date fromDate = new Date(beginDate.getTime() + 1000);
		Date toDate = new Date(fromDate.getTime() + 1000);
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate equals the begin date and toDate is later than the fromDate",
				dateFilterConfig.isDateRangeInRange(beginDate, fromDate));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate is later the begin date and toDate is later than the fromDate",
				dateFilterConfig.isDateRangeInRange(fromDate, toDate));
		assertTrue(
				"With no end date, #isDateRangeInRange(Date,Date) should return true when fromDate is before the begin date and toDate is later than the begin date",
				dateFilterConfig.isDateRangeInRange(new Date(beginDate.getTime() - 100), fromDate));
	}

	public void testIsDateRangeInRangeNegativeCaseWithNoEnd_FromAndToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);

		assertFalse(
				"With no end date, #isDateRangeInRange(Date,Date) should return FALSE when toDate is earlier than the begin date and fromDate is earlier than the toDate",
				dateFilterConfig.isDateRangeInRange(new Date(beginDate.getTime() - 1000), new Date(beginDate.getTime() - 100)));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoBegin_NullArgs() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when fromDate and toDate are null",
				dateFilterConfig.isDateRangeInRange(null, null));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoBegin_NullToDate() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);
		//		assertTrue(
		//				"With no begin date, #isDateRangeInRange(Date,Date) should return true when fromDate equals the end date and toDate is null",
		//				dateFilterConfig.isDateRangeInRange(endDate, null));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when fromDate is earlier than the end date and toDate is null",
				dateFilterConfig.isDateRangeInRange(new Date(endDate.getTime() - 10), null));
	}

	public void testIsDateRangeInRangeNegativeCaseWithNoBegin_NullToDate() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);
		assertFalse(
				"With no begin date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate is later than the end date and toDate is null",
				dateFilterConfig.isDateRangeInRange(new Date(endDate.getTime() + 10), null));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoBegin_NullFromDate() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);
		assertTrue(dateFilterConfig.isDateRangeInRange(null, new Date()));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate equals the end date and fromDate is null",
				dateFilterConfig.isDateRangeInRange(null, endDate));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is earlier than the end date and fromDate is null",
				dateFilterConfig.isDateRangeInRange(null, new Date(endDate.getTime() - 10)));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is null",
				dateFilterConfig.isDateRangeInRange(null, new Date(endDate.getTime() + 10)));
	}

	public void testIsDateRangeInRangePositiveCaseWithNoBegin_FromAndToDate() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);

		Date fromDate = new Date(endDate.getTime() - 1000);
		Date toDate = new Date(endDate.getTime() - 100);
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is equal to the end date and fromDate is earlier than the toDate",
				dateFilterConfig.isDateRangeInRange(fromDate, endDate));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is earlier than the end date and fromDate is earlier than the toDate",
				dateFilterConfig.isDateRangeInRange(fromDate, toDate));
		assertTrue(
				"With no begin date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is earlier than end date",
				dateFilterConfig.isDateRangeInRange(fromDate, new Date(endDate.getTime() + 100)));
	}

	public void testIsDateRangeInRangeNegativeCaseWithNoBegin_FromAndToDate() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);

		assertFalse(
				"With no begin date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate is later than the end date and toDate is later than the fromDate",
				dateFilterConfig.isDateRangeInRange(new Date(endDate.getTime() + 100), new Date(endDate.getTime() + 1000)));
	}

	// isDateRangeInRange(Date,Date) with Begin & End dates ///////////////////////////////////////////////

	public void testIsDateRangeInRangePositiveCaseWithBeginAndEnd_NullArgs() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		dateFilterConfig.setEndDate(endDate);

		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when fromDate and toDate are null",
				dateFilterConfig.isDateRangeInRange(null, null));
	}

	public void testIsDateRangeInRangePositiveCaseWithBeginAndEnd_NullToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		dateFilterConfig.setEndDate(endDate);

		//		assertTrue(
		//				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when fromDate equals the end date and toDate is null",
		//				dateFilterConfig.isDateRangeInRange(endDate, null));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when fromDate is earlier than the end date and toDate is null",
				dateFilterConfig.isDateRangeInRange(new Date(endDate.getTime() - 10), null));
	}

	public void testIsDateRangeInRangeNegativeCaseWithBeginAndEnd_NullToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		dateFilterConfig.setEndDate(endDate);

		assertFalse(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate is later than the end date and toDate is null",
				dateFilterConfig.isDateRangeInRange(new Date(endDate.getTime() + 10), null));
	}

	public void testIsDateRangeInRangePositiveCaseWithBeginAndEnd_NullFromDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		dateFilterConfig.setEndDate(endDate);

		assertTrue(dateFilterConfig.isDateRangeInRange(null, new Date(beginDate.getTime() + 1)));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate equals the end date and fromDate is null",
				dateFilterConfig.isDateRangeInRange(null, endDate));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is earlier than the end date and fromDate is null",
				dateFilterConfig.isDateRangeInRange(null, new Date(endDate.getTime() - 10)));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is null",
				dateFilterConfig.isDateRangeInRange(null, new Date(endDate.getTime() + 10)));
	}

	public void testIsDateRangeInRangeNegativeCaseWithBeginAndEnd_NullFromDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		dateFilterConfig.setEndDate(endDate);

		assertFalse(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return FALSE when toDate is earlier than the begin date and fromDate is null",
				dateFilterConfig.isDateRangeInRange(null, new Date(beginDate.getTime() - 10)));
	}

	public void testIsDateRangeInRangePositiveCaseWithBeginAndEnd_FromAndToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		dateFilterConfig.setEndDate(endDate);

		Date fromDate = new Date(endDate.getTime() - 1000);
		Date toDate = new Date(endDate.getTime() - 100);
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is equal to the end date and fromDate is earlier than the toDate",
				dateFilterConfig.isDateRangeInRange(fromDate, endDate));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is earlier than the end date and fromDate is earlier than the toDate",
				dateFilterConfig.isDateRangeInRange(fromDate, toDate));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is earlier than end date",
				dateFilterConfig.isDateRangeInRange(fromDate, new Date(endDate.getTime() + 100)));
		assertTrue(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return true when toDate is later than the end date and fromDate is earlier than begin date",
				dateFilterConfig.isDateRangeInRange(new Date(beginDate.getTime() - 100), new Date(endDate.getTime() + 100)));
	}

	public void testIsDateRangeInRangeNegativeCaseWithBeginAndEnd_FromAndToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);
		Date endDate = new Date(beginDate.getTime() + 10000);
		dateFilterConfig.setEndDate(endDate);

		assertFalse(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate is later the end date and toDate is later than the fromDate",
				dateFilterConfig.isDateRangeInRange(new Date(endDate.getTime() + 100), new Date(endDate.getTime() + 1000)));
		assertFalse(
				"With begin and end date, #isDateRangeInRange(Date,Date) should return FALSE when toDate is earlierr the begin date and fromDate is earlier than the toDate",
				dateFilterConfig.isDateRangeInRange(new Date(beginDate.getTime() - 100), new Date(beginDate.getTime() - 10)));
	}

	public void testIsDateRangeInRangeNegativeCaseWithEndDate_MatchingFromDateAndToDate() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);

		assertFalse(
				"With end date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate equals to the end date (with to date)",
				dateFilterConfig.isDateRangeInRange(endDate, new Date(endDate.getTime() + 2000L)));
	}

	public void testIsDateRangeInRangeNegativeCaseWithEndDate_MatchingFromDateAndNullToDate() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);

		assertFalse(
				"With end date, #isDateRangeInRange(Date,Date) should return FALSE when fromDate equals to the end date (null to date)",
				dateFilterConfig.isDateRangeInRange(endDate, null));
	}

	public void testIsDateRangeInRangeNegativeCaseWithBeginDate_MatchingToDateAndFromDate() throws Exception {
		Date toDate = new Date();
		dateFilterConfig.setBeginDate(toDate);

		assertFalse(
				"With begin date, #isDateRangeInRange(Date,Date) should return FALSE when toDate equals to the begin date (with from date)",
				dateFilterConfig.isDateRangeInRange(new Date(toDate.getTime() - 2000L), toDate));
	}

	public void testIsDateRangeInRangeNegativeCaseWithBeginDate_MatchingToDateAndNullFromDate() throws Exception {
		Date toDate = new Date();
		dateFilterConfig.setBeginDate(toDate);

		assertFalse(
				"With begin date, #isDateRangeInRange(Date,Date) should return FALSE when toDate equals to the begin date (null from date)",
				dateFilterConfig.isDateRangeInRange(null, toDate));
	}

	public void testIsDateRangeInRangePositveCaseWithBeginDate_MatchingFromDateAndNullToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);

		assertTrue(
				"With begin date, #isDateRangeInRange(Date,Date) should return TRUE when fromDate equals to the begin date (null to date)",
				dateFilterConfig.isDateRangeInRange(beginDate, null));
	}

	public void testIsDateRangeInRangePositveCaseWithBeginDate_MatchingFromDateAndToDate() throws Exception {
		Date beginDate = new Date();
		dateFilterConfig.setBeginDate(beginDate);

		assertTrue(
				"With begin date, #isDateRangeInRange(Date,Date) should return TRUE when fromDate equals to the begin date (with to date)",
				dateFilterConfig.isDateRangeInRange(beginDate, new Date(beginDate.getTime() + 10)));
	}

	public void testIsDateRangeInRangePositveCaseWithEndDate_MatchingToDateAndNullFromDate() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);

		assertTrue(
				"With begin date, #isDateRangeInRange(Date,Date) should return TRUE when fromDate equals to the begin date (null to date)",
				dateFilterConfig.isDateRangeInRange(null, endDate));
	}

	public void testIsDateRangeInRangePositveCaseWithEndDate_MatchingToDateAndFromDate() throws Exception {
		Date endDate = new Date();
		dateFilterConfig.setEndDate(endDate);

		assertTrue(
				"With begin date, #isDateRangeInRange(Date,Date) should return TRUE when fromDate equals to the begin date (null to date)",
				dateFilterConfig.isDateRangeInRange(new Date(endDate.getTime() - 10), endDate));
	}

	// #isDateSynonymRangeInRange(DateSynonym,DateSynonym) //////////////////////////////////////////////////////////////

	public void testIsDateSynonymRangeInRangeWithNullArgs() throws Exception {
		assertTrue(dateFilterConfig.isDateRangeInRange(null, null));
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dateFilterConfig = new DateFilterConfig();
	}

	private DateFilterConfig dateFilterConfig;
}
