package com.mindbox.pe.server.generator;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.model.DateSynonym;

public class RuleGeneratorHelperWithTestConfigTest extends AbstractTestWithTestConfig {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("RuleGeneratorHelperTestWithTestConfig Tests");
		suite.addTestSuite(RuleGeneratorHelperWithTestConfigTest.class);
		return suite;
	}

	public RuleGeneratorHelperWithTestConfigTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	public void testToRuleDateTimeStringForDateSynonymWithNullHappyCase() throws Exception {
		assertEquals(RuleGeneratorHelper.AE_NIL, RuleGeneratorHelper.toRuleDateTimeString((DateSynonym) null));
	}

	public void testToRuleDateTimeStringForDateSynonymWithDatesSynonymHappyCase() throws Exception {
		DateSynonym dateSynonym = ObjectMother.createDateSynonym();
		assertEquals(RuleGeneratorHelper.toRuleDateTimeString(dateSynonym.getDate()), RuleGeneratorHelper.toRuleDateTimeString(dateSynonym));
	}

	public void testToRuleDateTimeStringForDateWithNullHappyCase() throws Exception {
		assertEquals(RuleGeneratorHelper.AE_NIL, RuleGeneratorHelper.toRuleDateTimeString((Date) null));
	}

	public void testToRuleDateTimeStringForDateWithDateHappyCase() throws Exception {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		assertEquals(String.valueOf(DateUtil.dateToJulian(calendar)), RuleGeneratorHelper.toRuleDateTimeString(date));
	}

}
