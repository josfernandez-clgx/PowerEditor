package com.mindbox.pe.server.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.server.model.TimeSliceContainer;

public class TimeSliceGeneratorTest extends AbstractTestWithTestConfig {

	private static class TimeSliceGeneratorImpl extends TimeSliceGenerator {
		private final StringWriter stringWriter = new StringWriter();

		protected PrintWriter getPrintWriter(String status, OutputController outputController) throws RuleGenerationException {
			return new PrintWriter(stringWriter);
		}

		public String getOutputString() {
			return stringWriter.toString();
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("TimeSliceGeneratorTest Tests");
		suite.addTestSuite(TimeSliceGeneratorTest.class);
		return suite;
	}

	private TimeSliceGeneratorImpl generatorImpl;;

	public TimeSliceGeneratorTest(String name) {
		super(name);
	}

	public void testGenerateSingleTimeSliceHappyCaseWithNoEndDate() throws Exception {
		logBegin();
		TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
		TimeSlice timeSlice = TimeSlice.createInstance(ObjectMother.createDateSynonym(), null);
		timeSliceContainer.add(timeSlice);
		timeSliceContainer.freeze();

		generatorImpl.generate(timeSliceContainer);
		logger.info("OUTPUT = " + generatorImpl.getOutputString());
		StringBuffer buff = new StringBuffer();
		buff.append("(define-instance ");
		buff.append(timeSlice.getName());
		buff.append(" (PE:time-slice)");
		buff.append(System.getProperty("line.separator"));
		buff.append("   (PE:activation-date ");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timeSlice.getStartDate().getDate());
		buff.append(DateUtil.dateToJulian(calendar));
		buff.append(")");
		buff.append(System.getProperty("line.separator"));
		buff.append("   (PE:expiration-date :UNSPECIFIED)");
		buff.append(System.getProperty("line.separator"));
		buff.append(")");
		buff.append(System.getProperty("line.separator"));
		testOutputMatches(buff.toString());
		logEnd();
	}

	public void testGenerateSingleTimeSliceHappyCaseWithNoStartDate() throws Exception {
		logBegin();
		TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
		TimeSlice timeSlice = TimeSlice.createInstance(null, ObjectMother.createDateSynonym());
		timeSliceContainer.add(timeSlice);
		timeSliceContainer.freeze();

		generatorImpl.generate(timeSliceContainer);
		logger.info("OUTPUT = " + generatorImpl.getOutputString());
		StringBuffer buff = new StringBuffer();
		buff.append("(define-instance ");
		buff.append(timeSlice.getName());
		buff.append(" (PE:time-slice)");
		buff.append(System.getProperty("line.separator"));
		buff.append("   (PE:activation-date :UNSPECIFIED)");
		buff.append(System.getProperty("line.separator"));
		buff.append("   (PE:expiration-date ");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timeSlice.getEndDate().getDate());
		buff.append(DateUtil.dateToJulian(calendar));
		buff.append(")");
		buff.append(System.getProperty("line.separator"));
		buff.append(")");
		buff.append(System.getProperty("line.separator"));
		testOutputMatches(buff.toString());
		logEnd();
	}
	
	public void testGenerateExceptionInTimeSliceWithNoStartDateNoEndDate() throws Exception {
		logBegin();
		try{
		TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
		timeSliceContainer=null;
		generatorImpl.generate(timeSliceContainer);
		}catch(NullPointerException e){
			String str = e.getMessage();
			logger.info(str);
		}
		logEnd();
	}

	public void testGenerateSingleTimeSliceHappyCaseWithStartAndEndDate() throws Exception {
		logBegin();
		TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
		TimeSlice timeSlice = TimeSlice.createInstance(ObjectMother.createDateSynonym(), ObjectMother.createDateSynonym());
		timeSliceContainer.add(timeSlice);
		timeSliceContainer.freeze();

		generatorImpl.generate(timeSliceContainer);
		logger.info("OUTPUT = " + generatorImpl.getOutputString());
		StringBuffer buff = new StringBuffer();
		buff.append("(define-instance ");
		buff.append(timeSlice.getName());
		buff.append(" (PE:time-slice)");
		buff.append(System.getProperty("line.separator"));
		buff.append("   (PE:activation-date ");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timeSlice.getStartDate().getDate());
		buff.append(DateUtil.dateToJulian(calendar));
		buff.append(")");
		buff.append(System.getProperty("line.separator"));
		buff.append("   (PE:expiration-date ");
		calendar = Calendar.getInstance();
		calendar.setTime(timeSlice.getEndDate().getDate());
		buff.append(DateUtil.dateToJulian(calendar));
		buff.append(")");
		buff.append(System.getProperty("line.separator"));
		buff.append(")");
		buff.append(System.getProperty("line.separator"));
		testOutputMatches(buff.toString());
		logEnd();
	}

	private void testOutputMatches(String expected) throws Exception {
		assertEquals(expected, stripOffCommentsFromOutput());
	}

	private String stripOffCommentsFromOutput() throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(generatorImpl.getOutputString()));
		StringBuffer buff = new StringBuffer();
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			if (!UtilBase.isEmpty(line) && !line.trim().startsWith(";")) {
				buff.append(line);
				buff.append(System.getProperty("line.separator"));
			}
		}
		return buff.toString();
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		generatorImpl = new TimeSliceGeneratorImpl();
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(1, "Draft", "Draft"));
		generatorImpl.init(new OutputController("Draft"));
	}

	protected void tearDown() throws Exception {
		TimeSlice.resetNextID();
		TypeEnumValueManager.getInstance().startLoading();
		config.resetConfiguration();
		super.tearDown();
	}
}
