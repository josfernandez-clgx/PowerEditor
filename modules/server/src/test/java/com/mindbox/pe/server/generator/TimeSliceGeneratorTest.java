package com.mindbox.pe.server.generator;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.easymock.Capture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.common.IOUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.server.model.TimeSliceContainer;

public class TimeSliceGeneratorTest extends AbstractTestWithTestConfig {

	private TimeSliceGenerator timeSliceGenerator;
	private File timeSliceFile;
	private OutputController mockOutputController;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(1, "Draft", "Draft"));
		mockOutputController = createMock(OutputController.class);
		new File("target/test").mkdirs();
		timeSliceFile = new File("target/test/timeslices.test.art");
	}

	private String stripOffCommentsFromOutput() throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(timeSliceFile));
		try {
			final StringBuilder buff = new StringBuilder();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (!UtilBase.isEmpty(line) && !line.trim().startsWith(";")) {
					buff.append(line);
					buff.append(System.getProperty("line.separator"));
				}
			}
			return buff.toString();
		}
		finally {
			IOUtil.close(reader);
		}
	}

	@After
	@Override
	public void tearDown() throws Exception {
		TimeSlice.resetNextID();
		TypeEnumValueManager.getInstance().startLoading();
		config.resetConfiguration();
		IOUtil.delete(timeSliceFile);
		super.tearDown();
	}

	@Test
	public void testGenerateExceptionInTimeSliceWithNoStartDateNoEndDate() throws Exception {
		logBegin("testGenerateExceptionInTimeSliceWithNoStartDateNoEndDate");
		try {
			TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
			timeSliceContainer = null;

			Capture<String> messageCapture = new Capture<String>();

			expect(mockOutputController.getTimeSliceFile()).andReturn(timeSliceFile);
			mockOutputController.writeErrorMessage(anyObject(String.class), capture(messageCapture));

			replay(mockOutputController);

			timeSliceGenerator = new TimeSliceGenerator(new GenerateStats("target"), mockOutputController);
			timeSliceGenerator.generate(100, timeSliceContainer);

			assertTrue(messageCapture.getValue().indexOf("No date synonym") >= 0);

			verify(mockOutputController);
		}
		catch (NullPointerException e) {
			String str = e.getMessage();
			logger.info(str);
		}
		logEnd("testGenerateExceptionInTimeSliceWithNoStartDateNoEndDate");
	}

	@Test
	public void testGenerateSingleTimeSliceHappyCaseWithNoEndDate() throws Exception {
		logBegin("testGenerateSingleTimeSliceHappyCaseWithNoEndDate");
		TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
		TimeSlice timeSlice = TimeSlice.createInstance(createDateSynonym(), null);
		timeSliceContainer.add(timeSlice);
		timeSliceContainer.freeze();

		expect(mockOutputController.getTimeSliceFile()).andReturn(timeSliceFile);

		replay(mockOutputController);

		timeSliceGenerator = new TimeSliceGenerator(new GenerateStats("target"), mockOutputController);
		timeSliceGenerator.generate(100, timeSliceContainer);

		StringBuilder buff = new StringBuilder();
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

		verify(mockOutputController);
		logEnd("testGenerateSingleTimeSliceHappyCaseWithNoEndDate");
	}

	@Test
	public void testGenerateSingleTimeSliceHappyCaseWithNoStartDate() throws Exception {
		logBegin("testGenerateSingleTimeSliceHappyCaseWithNoStartDate");
		TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
		TimeSlice timeSlice = TimeSlice.createInstance(null, createDateSynonym());
		timeSliceContainer.add(timeSlice);
		timeSliceContainer.freeze();

		expect(mockOutputController.getTimeSliceFile()).andReturn(timeSliceFile);

		replay(mockOutputController);

		timeSliceGenerator = new TimeSliceGenerator(new GenerateStats("target"), mockOutputController);
		timeSliceGenerator.generate(100, timeSliceContainer);

		StringBuilder buff = new StringBuilder();
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

		verify(mockOutputController);
		logEnd("testGenerateSingleTimeSliceHappyCaseWithNoStartDate");
	}

	@Test
	public void testGenerateSingleTimeSliceHappyCaseWithStartAndEndDate() throws Exception {
		logBegin("testGenerateSingleTimeSliceHappyCaseWithStartAndEndDate");
		TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
		TimeSlice timeSlice = TimeSlice.createInstance(createDateSynonym(), createDateSynonym());
		timeSliceContainer.add(timeSlice);
		timeSliceContainer.freeze();

		expect(mockOutputController.getTimeSliceFile()).andReturn(timeSliceFile);

		replay(mockOutputController);

		timeSliceGenerator = new TimeSliceGenerator(new GenerateStats("target"), mockOutputController);
		timeSliceGenerator.generate(100, timeSliceContainer);

		StringBuilder buff = new StringBuilder();
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

		verify(mockOutputController);
		logEnd("testGenerateSingleTimeSliceHappyCaseWithStartAndEndDate");
	}

	private void testOutputMatches(String expected) throws Exception {
		assertEquals(expected, stripOffCommentsFromOutput());
	}
}
