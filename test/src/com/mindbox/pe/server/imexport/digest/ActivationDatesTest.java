package com.mindbox.pe.server.imexport.digest;

import java.util.Calendar;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;

public class ActivationDatesTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("ActivationDatesTest Tests");
		suite.addTestSuite(ActivationDatesTest.class);
		return suite;
	}

	public ActivationDatesTest(String name) {
		super(name);
	}

	public void testSetActivationDateWithValidDateStr() throws Exception {
		ActivationDates dates = new ActivationDates();
		dates.setActivationDate("2006-01-01T00:00:00");

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2006);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		assertEquals(c.getTime(), dates.effectiveDate());
	}

	public void testSetExpirationDateWithValidDateStr() throws Exception {
		ActivationDates dates = new ActivationDates();
		dates.setExpirationDate("2006-01-01T00:00:00");

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2006);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		assertEquals(c.getTime(), dates.expirationDate());
	}

}
