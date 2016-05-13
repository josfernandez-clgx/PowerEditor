package com.mindbox.pe.server.cache;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.server.model.TimeSliceContainer;
import com.mindbox.pe.unittest.AbstractTestBase;

public class DateSynonymManagerTest extends AbstractTestBase {

	private DateSynonymManager dateSynonymManager;

	@Before
	public void setUp() throws Exception {
		dateSynonymManager = DateSynonymManager.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		// Tear downs for DateSynonymManagerTest
		DateSynonymManager.getInstance().startLoading();
		TimeSlice.resetNextID();
	}

	@Test
	public void testProduceTimeSlicesHappyCaseWithOneDateSynonym() throws Exception {
		DateSynonym ds1 = createDateSynonym();
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

	@Test
	public void testProduceTimeSlicesHappyCaseWithTwoDateSynonym() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2005, 10, 10, 00, 00, 00));
		dateSynonymManager.insert(ds1);
		DateSynonym ds2 = createDateSynonym();
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

	@Test
	public void testProduceTimeSlicesWithEmptyDateSynonymReturnsNull() {
		TimeSliceContainer container = dateSynonymManager.produceTimeSlices();
		assertEquals(container, null);
	}
}
