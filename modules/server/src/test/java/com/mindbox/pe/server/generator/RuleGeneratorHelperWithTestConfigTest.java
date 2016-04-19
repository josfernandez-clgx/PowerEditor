package com.mindbox.pe.server.generator;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class RuleGeneratorHelperWithTestConfigTest extends AbstractTestWithTestConfig {

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testToRuleDateTimeStringForDateSynonymWithDatesSynonymHappyCase() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		assertEquals(RuleGeneratorHelper.toRuleDateTimeString(dateSynonym.getDate()), RuleGeneratorHelper.toRuleDateTimeString(dateSynonym));
	}

	@Test
	public void testToRuleDateTimeStringForDateSynonymWithNullHappyCase() throws Exception {
		assertEquals(RuleGeneratorHelper.AE_NIL, RuleGeneratorHelper.toRuleDateTimeString((DateSynonym) null));
	}

	@Test
	public void testToRuleDateTimeStringForDateWithDateHappyCase() throws Exception {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		assertEquals(String.valueOf(DateUtil.dateToJulian(calendar)), RuleGeneratorHelper.toRuleDateTimeString(date));
	}

	@Test
	public void testToRuleDateTimeStringForDateWithNullHappyCase() throws Exception {
		assertEquals(RuleGeneratorHelper.AE_NIL, RuleGeneratorHelper.toRuleDateTimeString((Date) null));
	}

}
