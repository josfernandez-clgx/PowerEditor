package com.mindbox.pe.common;

import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class DateUtilTest extends AbstractTestBase {

	@Test
	public void testDaysSinceWithNulLThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(DateUtil.class, "daysSince", new Class[] { Date.class }, new Object[] { null });
	}

	@Test
	public void testDaysSince() throws Exception {
		long paddingForTestRunTime = 1000;
		long now = new Date().getTime();

		long justMoreThanADayInFuture = now + DateUtil.MILLIS_PER_DAY + paddingForTestRunTime;
		long justLessThanADayInFuture = now + DateUtil.MILLIS_PER_DAY - 1;
		long justMoreThan2DaysInFuture = now + (DateUtil.MILLIS_PER_DAY * 2) + paddingForTestRunTime;
		long justMoreThanADayInPast = now - DateUtil.MILLIS_PER_DAY - 1;
		long justLessThanADayInPast = now - DateUtil.MILLIS_PER_DAY + paddingForTestRunTime;
		long justMoreThan2DaysInPast = now - (DateUtil.MILLIS_PER_DAY * 2) - 1;

		assertEquals(0, DateUtil.daysSince(new Date(justLessThanADayInFuture)));
		assertEquals(0, DateUtil.daysSince(new Date(justLessThanADayInPast)));
		assertEquals(-1, DateUtil.daysSince(new Date(justMoreThanADayInFuture)));
		assertEquals(1, DateUtil.daysSince(new Date(justMoreThanADayInPast)));
		assertEquals(2, DateUtil.daysSince(new Date(justMoreThan2DaysInPast)));
		assertEquals(-2, DateUtil.daysSince(new Date(justMoreThan2DaysInFuture)));
	}

	@Test
	public void testDaysSinceOver9() throws Exception {
		long now = new Date().getTime();
		long tendaysPast = now - (DateUtil.MILLIS_PER_DAY * 10);
		assertEquals(10, DateUtil.daysSince(new Date(tendaysPast)));
	}
}
