package com.mindbox.pe.server.model;

import static com.mindbox.pe.server.ServerTestObjectMother.attachGridTemplateColumns;
import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplate;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGrid;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.AbstractTestWithGenericEntityType;
import com.mindbox.pe.server.generator.GenerationParams;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.spi.db.EntityDataHolder;

public class TimeSliceContainerTest extends AbstractTestWithGenericEntityType {

	private static class EntityDataHolderImpl implements EntityDataHolder {

		private Map<Integer, Set<DateSynonym>> dateSnyonymForCategoryChangesMap = new HashMap<Integer, Set<DateSynonym>>();

		@Override
		public void addEntityCompatibility(int entityType1, int entityID1, int entityType2, int entityID2, DateSynonym effectiveDate, DateSynonym expirationDate) {
		}

		@Override
		public void addGenericEntity(int entityID, int entityType, String name, int parentID, Map<String, Object> propertyMap) {
		}

		@Override
		public void addGenericEntityCategory(int categoryType, int categoryID, String name) {
		}

		@Override
		public void addGenericEntityToCategories(int[] categoryIDs, int categoryType, int entityID, int entityType, int effectiveDateID, int expirationDateID) {
		}

		@Override
		public void addGenericEntityToCategory(int categoryID, int categoryType, int entityID, int entityType, int effectiveDateID, int expirationDateID) {
		}

		@Override
		public void addParentAssociation(int categoryType, int categoryID, int parentID, int effectiveDateID, int expirationDateID) {
		}

		@Override
		public Set<DateSynonym> getDateSynonymsForChangesInCategoryRelationships(int categoryType) {
			return dateSnyonymForCategoryChangesMap.get(new Integer(categoryType));
		}

		void setDateSnyonymForCategoryChanges(int categoryType, Set<DateSynonym> dataSynonymSet) {
			dateSnyonymForCategoryChangesMap.put(new Integer(categoryType), dataSynonymSet);
		}

	}

	private TimeSliceContainer timeSliceContainer;

	public void setUp() throws Exception {
		super.setUp();
		timeSliceContainer = new TimeSliceContainer();
	}

	public void tearDown() throws Exception {
		TimeSlice.resetNextID();
		super.tearDown();
	}

	@Test
	public void testAddAfterFreezeThrowsIllegalStateException() throws Exception {
		timeSliceContainer.freeze();
		assertThrowsException(timeSliceContainer, "add", new Class[] { TimeSlice.class }, new Object[] { TimeSlice.createInstance(null, createDateSynonym()) }, IllegalStateException.class);
	}

	@Test
	public void testAddPreservesNaturalOrder() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DateSynonym ds3 = createDateSynonym();
		ds3.setDate(getDate(2006, 10, 10, 10, 10, 59));

		timeSliceContainer.add(TimeSlice.createInstance(ds3, null));
		timeSliceContainer.add(TimeSlice.createInstance(ds1, ds2));
		timeSliceContainer.add(TimeSlice.createInstance(null, ds1));
		timeSliceContainer.add(TimeSlice.createInstance(ds2, ds3));
		timeSliceContainer.freeze();
		assertEquals(4, timeSliceContainer.getAll().size());
		TimeSlice previousTimeSlice = null;
		for (Iterator<TimeSlice> iter = timeSliceContainer.getAll().iterator(); iter.hasNext();) {
			TimeSlice element = iter.next();
			if (previousTimeSlice != null) {
				assertTrue("failed with previous=" + previousTimeSlice + ",current=" + element, previousTimeSlice.compareTo(element) < 0);
			}
			previousTimeSlice = element;
		}
	}

	@Test
	public void testAddWithExistingNameThrowsIllegalArgumentException() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(null, createDateSynonym());
		timeSliceContainer.add(timeSlice);
		TimeSlice.resetNextID();
		TimeSlice timeSlice2 = TimeSlice.createInstance(createDateSynonym(), createDateSynonym());
		assertThrowsException(timeSliceContainer, "add", new Class[] { TimeSlice.class }, new Object[] { timeSlice2 }, IllegalArgumentException.class);
	}

	@Test
	public void testAddWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(timeSliceContainer, "add", new Class[] { TimeSlice.class });
	}

	@Test
	public void testFreezeHappyCase() throws Exception {
		assertFalse(timeSliceContainer.isFrozen());
		timeSliceContainer.freeze();
		assertTrue(timeSliceContainer.isFrozen());
	}

	@Test
	public void testGenerateTimeSliceGroupsBeforeFreezeThrowsIllegalStateException() throws Exception {
		assertThrowsException(
				timeSliceContainer,
				"generateTimeSliceGroups",
				new Class[] { GenerationParams.class, EntityDataHolder.class },
				new Object[] { null, new EntityDataHolderImpl() },
				IllegalStateException.class);
	}

	@Test
	public void testGenerateTimeSliceGroupsDoesNotUseDSOutsideRange() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice ts1 = TimeSlice.createInstance(null, ds1);
		TimeSlice ts2 = TimeSlice.createInstance(ds1, ds2);
		TimeSlice ts3 = TimeSlice.createInstance(ds2, null);
		timeSliceContainer.add(ts1);
		timeSliceContainer.add(ts2);
		timeSliceContainer.add(ts3);
		timeSliceContainer.freeze();

		ProductGrid grid = createGuidelineGrid(createGridTemplate(createUsageType()));
		attachGridTemplateColumns((GridTemplate) grid.getTemplate(), 2);
		grid.setNumRows(1);
		grid.setNumRows(1);
		grid.setValue(1, grid.getTemplate().getColumn(1).getName(), new CategoryOrEntityValue(entityType, false, 1));
		grid.setValue(1, grid.getTemplate().getColumn(2).getName(), new CategoryOrEntityValue(entityType, true, 2));
		GuidelineGenerateParams generateParams = new GuidelineGenerateParams(ds1, null, grid, -1, 1);

		EntityDataHolderImpl entityDataHolder = new EntityDataHolderImpl();
		entityDataHolder.setDateSnyonymForCategoryChanges(entityType.getCategoryType(), Collections.unmodifiableSet(Collections.singleton(ds1)));
		List<TimeSlice[]> list = timeSliceContainer.generateTimeSliceGroups(generateParams, entityDataHolder);
		assertEquals(1, list.size());
		assertEquals(2, list.get(0).length);
	}

	@Test
	public void testGenerateTimeSliceGroupsHappyCase() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice ts1 = TimeSlice.createInstance(null, ds1);
		TimeSlice ts2 = TimeSlice.createInstance(ds1, ds2);
		TimeSlice ts3 = TimeSlice.createInstance(ds2, null);
		timeSliceContainer.add(ts1);
		timeSliceContainer.add(ts2);
		timeSliceContainer.add(ts3);
		timeSliceContainer.freeze();

		ProductGrid grid = createGuidelineGrid(createGridTemplate(createUsageType()));
		attachGridTemplateColumns((GridTemplate) grid.getTemplate(), 2);
		grid.setNumRows(1);
		grid.setNumRows(1);
		grid.setValue(1, grid.getTemplate().getColumn(1).getName(), new CategoryOrEntityValue(entityType, false, 1));
		grid.setValue(1, grid.getTemplate().getColumn(2).getName(), new CategoryOrEntityValue(entityType, true, 2));
		GuidelineGenerateParams generateParams = new GuidelineGenerateParams(ds1, null, grid, -1, 1);

		EntityDataHolderImpl entityDataHolder = new EntityDataHolderImpl();
		entityDataHolder.setDateSnyonymForCategoryChanges(entityType.getCategoryType(), Collections.unmodifiableSet(Collections.singleton(ds2)));
		List<TimeSlice[]> list = timeSliceContainer.generateTimeSliceGroups(generateParams, entityDataHolder);
		assertEquals(2, list.size());
		assertEquals(1, list.get(0).length);
		assertEquals(1, list.get(1).length);
		assertEquals(ts2, list.get(0)[0]);
		assertEquals(ts3, list.get(1)[0]);
	}

	@Test
	public void testGenerateTimeSliceGroupsWithNoCategoryReturnsSingleSet() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice ts1 = TimeSlice.createInstance(null, ds1);
		TimeSlice ts2 = TimeSlice.createInstance(ds1, ds2);
		TimeSlice ts3 = TimeSlice.createInstance(ds2, null);
		timeSliceContainer.add(ts1);
		timeSliceContainer.add(ts2);
		timeSliceContainer.add(ts3);
		timeSliceContainer.freeze();

		ProductGrid grid = createGuidelineGrid(createGridTemplate(createUsageType()));
		attachGridTemplateColumns((GridTemplate) grid.getTemplate(), 2);
		grid.setNumRows(1);
		GuidelineGenerateParams generateParams = new GuidelineGenerateParams(ds1, null, grid, -1, 1);
		List<TimeSlice[]> list = timeSliceContainer.generateTimeSliceGroups(generateParams, new EntityDataHolderImpl());
		assertEquals(1, list.size());
		assertEquals(2, list.get(0).length);
	}

	@Test
	public void testGenerateTimeSliceGroupsWithNullParamsThrowsNullPointerException() throws Exception {
		timeSliceContainer.freeze();
		assertThrowsNullPointerExceptionWithNullArgs(timeSliceContainer, "generateTimeSliceGroups", new Class[] { GenerationParams.class, EntityDataHolder.class });
	}

	@Test
	public void testGetAllBeforeFreezeThrowsIllegalStateException() throws Exception {
		assertThrowsException(timeSliceContainer, "getAll", new Class[0], new Object[0], IllegalStateException.class);
	}

	@Test
	public void testGetApplicableTimeSliceNamesBeforeFreezeThrowsIllegalStateException() throws Exception {
		assertThrowsException(
				timeSliceContainer,
				"getApplicableTimeSlices",
				new Class[] { DateSynonym.class, DateSynonym.class },
				new Object[] { null, createDateSynonym() },
				IllegalStateException.class);
	}

	@Test
	public void testGetApplicableTimeSlicesHappyCaseWithNoNulls() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DateSynonym ds3 = createDateSynonym();
		ds3.setDate(getDate(2006, 10, 10, 10, 10, 59));

		TimeSlice ts1 = TimeSlice.createInstance(null, ds1);
		TimeSlice ts2 = TimeSlice.createInstance(ds1, ds2);
		TimeSlice ts3 = TimeSlice.createInstance(ds2, ds3);
		TimeSlice ts4 = TimeSlice.createInstance(ds3, null);
		timeSliceContainer.add(ts1);
		timeSliceContainer.add(ts2);
		timeSliceContainer.add(ts3);
		timeSliceContainer.add(ts4);
		timeSliceContainer.freeze();

		List<TimeSlice> list = timeSliceContainer.getApplicableTimeSlices(ds1, ds3);
		assertEquals(2, list.size());
		assertTrue(list.contains(ts2));
		assertTrue(list.contains(ts3));

		list = timeSliceContainer.getApplicableTimeSlices(ds2, ds3);
		assertEquals(1, list.size());
		assertTrue(list.contains(ts3));

		list = timeSliceContainer.getApplicableTimeSlices(ds1, ds2);
		assertEquals(1, list.size());
		assertTrue(list.contains(ts2));
	}

	@Test
	public void testGetApplicableTimeSlicesHappyCaseWithNulls() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DateSynonym ds3 = createDateSynonym();
		ds3.setDate(getDate(2006, 10, 10, 10, 10, 59));

		TimeSlice ts1 = TimeSlice.createInstance(null, ds1);
		TimeSlice ts2 = TimeSlice.createInstance(ds1, ds2);
		TimeSlice ts3 = TimeSlice.createInstance(ds2, ds3);
		TimeSlice ts4 = TimeSlice.createInstance(ds3, null);
		timeSliceContainer.add(ts1);
		timeSliceContainer.add(ts2);
		timeSliceContainer.add(ts3);
		timeSliceContainer.add(ts4);
		timeSliceContainer.freeze();

		List<TimeSlice> list = timeSliceContainer.getApplicableTimeSlices(null, ds3);
		assertEquals(3, list.size());
		assertFalse(list.contains(ts4));

		list = timeSliceContainer.getApplicableTimeSlices(ds1, null);
		assertEquals(3, list.size());
		assertFalse(list.contains(ts1));

		list = timeSliceContainer.getApplicableTimeSlices(ds3, null);
		assertEquals(1, list.size());
		assertTrue(list.contains(ts4));

		list = timeSliceContainer.getApplicableTimeSlices(null, null);
		assertEquals(4, list.size());
	}
}
