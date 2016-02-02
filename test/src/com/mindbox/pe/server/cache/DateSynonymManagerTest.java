package com.mindbox.pe.server.cache;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.server.model.TimeSliceContainer;

public class DateSynonymManagerTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DateSynonymManagerTest Tests");
		suite.addTestSuite(DateSynonymManagerTest.class);
		return suite;
	}

	private DateSynonymManager dateSynonymManager;

	public DateSynonymManagerTest(String name) {
		super(name);
	}

	public void testProduceTimeSlicesWithEmptyDateSynonymReturnsNull() {
		TimeSliceContainer container = dateSynonymManager.produceTimeSlices();
		assertEquals(container,null);
	}

	public void testProduceTimeSlicesHappyCaseWithOneDateSynonym() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2005, 10, 10, 00, 00, 00));
		dateSynonymManager.insert(ds1);

		TimeSliceContainer container = dateSynonymManager.produceTimeSlices();
		assertTrue(container.isFrozen());
		List<TimeSlice> list = container.getAll();
		assertEquals(2, list.size());
		assertEquals("TS1000", list.get(0).getName());
		assertEquals(null, list.get(0).getStartDate());
		assertEquals(ds1, list.get(0).getEndDate());
		assertEquals(ds1, list.get(1).getStartDate());
		assertEquals(null, list.get(1).getEndDate());
	}

	public void testProduceTimeSlicesHappyCaseWithTwoDateSynonym() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2005, 10, 10, 00, 00, 00));
		dateSynonymManager.insert(ds1);
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2005, 10, 10, 01, 00, 00));
		dateSynonymManager.insert(ds2);

		TimeSliceContainer container = dateSynonymManager.produceTimeSlices();
		assertTrue(container.isFrozen());
		List<TimeSlice> list = container.getAll();
		assertEquals(3, list.size());
		assertEquals("TS1000", list.get(0).getName());
		assertEquals(null, list.get(0).getStartDate());
		assertEquals(ds1, list.get(0).getEndDate());
		assertEquals(ds1, list.get(1).getStartDate());
		assertEquals(ds2, list.get(1).getEndDate());
		assertEquals(ds2, list.get(2).getStartDate());
		assertEquals(null, list.get(2).getEndDate());
	}

	protected void setUp() throws Exception {
		super.setUp();
		dateSynonymManager = DateSynonymManager.getInstance();
	}

	protected void tearDown() throws Exception {
		// Tear downs for DateSynonymManagerTest
		DateSynonymManager.getInstance().startLoading();
		TimeSlice.resetNextID();
		super.tearDown();
	}
}
