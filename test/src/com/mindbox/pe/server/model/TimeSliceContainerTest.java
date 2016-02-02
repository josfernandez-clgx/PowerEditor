package com.mindbox.pe.server.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.generator.GenerationParams;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.spi.db.EntityDataHolder;

public class TimeSliceContainerTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("TimeSliceContainerTest Tests");
		suite.addTestSuite(TimeSliceContainerTest.class);
		return suite;
	}

	private static class EntityDataHolderImpl implements EntityDataHolder {

		private Map<Integer, Set<DateSynonym>> dateSnyonymForCategoryChangesMap = new HashMap<Integer, Set<DateSynonym>>();
		
		void setDateSnyonymForCategoryChanges(int categoryType, Set<DateSynonym> dataSynonymSet) {
			dateSnyonymForCategoryChangesMap.put(new Integer(categoryType), dataSynonymSet);
		}
		public void addEntityCompatibility(int entityType1, int entityID1, int entityType2, int entityID2, DateSynonym effectiveDate, DateSynonym expirationDate) {
		}

		public void addGenericEntity(int entityID, int entityType, String name, int parentID, Map<String,Object> propertyMap) {
		}

		public void addGenericEntityCategory(int categoryType, int categoryID, String categoryName) {
		}

		public void addGenericEntityToCategories(int[] categoryIDs, int categoryType, int entityID, int entityType, int effectiveDateID, int expirationDateID) {
		}

		public void addGenericEntityToCategory(int categoryID, int categoryType, int entityID, int entityType, int effectiveDateID, int expirationDateID) {
		}

		public void addParentAssociation(int categoryType, int categoryID, int parentID, int effectiveDateID, int expirationDateID) {
		}

		public Set<DateSynonym> getDateSynonymsForChangesInCategoryRelationships(int categoryType) {
			return dateSnyonymForCategoryChangesMap.get(new Integer(categoryType));
		}
		
	}
	
	private TimeSliceContainer timeSliceContainer;

	public TimeSliceContainerTest(String name) {
		super(name);
	}

	public void testAddWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(timeSliceContainer, "add", new Class[]
			{ TimeSlice.class});
	}

	public void testAddAfterFreezeThrowsIllegalStateException() throws Exception {
		timeSliceContainer.freeze();
		assertThrowsException(timeSliceContainer, "add", new Class[]
			{ TimeSlice.class}, new Object[]
			{ TimeSlice.createInstance(null, ObjectMother.createDateSynonym())}, IllegalStateException.class);
	}

	public void testAddWithExistingNameThrowsIllegalArgumentException() throws Exception {
		TimeSlice timeSlice = TimeSlice.createInstance(null, ObjectMother.createDateSynonym());
		timeSliceContainer.add(timeSlice);
		TimeSlice.resetNextID();
		TimeSlice timeSlice2 = TimeSlice.createInstance(ObjectMother.createDateSynonym(), ObjectMother.createDateSynonym());
		assertThrowsException(timeSliceContainer, "add", new Class[]
			{ TimeSlice.class}, new Object[]
			{ timeSlice2}, IllegalArgumentException.class);
	}

	public void testAddPreservesNaturalOrder() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DateSynonym ds3 = ObjectMother.createDateSynonym();
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

	public void testFreezeHappyCase() throws Exception {
		assertFalse(timeSliceContainer.isFrozen());
		timeSliceContainer.freeze();
		assertTrue(timeSliceContainer.isFrozen());
	}

	public void testGetAllBeforeFreezeThrowsIllegalStateException() throws Exception {
		assertThrowsException(timeSliceContainer, "getAll", new Class[0], new Object[0], IllegalStateException.class);
	}

	public void testGetApplicableTimeSliceNamesBeforeFreezeThrowsIllegalStateException() throws Exception {
		assertThrowsException(timeSliceContainer, "getApplicableTimeSlices", new Class[]
			{ DateSynonym.class, DateSynonym.class}, new Object[]
			{ null, ObjectMother.createDateSynonym()}, IllegalStateException.class);
	}

	public void testGetApplicableTimeSlicesHappyCaseWithNulls() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DateSynonym ds3 = ObjectMother.createDateSynonym();
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

	public void testGetApplicableTimeSlicesHappyCaseWithNoNulls() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DateSynonym ds3 = ObjectMother.createDateSynonym();
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

	public void testGenerateTimeSliceGroupsWithNullParamsThrowsNullPointerException() throws Exception {
		timeSliceContainer.freeze();
		assertThrowsNullPointerExceptionWithNullArgs(timeSliceContainer, "generateTimeSliceGroups", new Class[]
			{ GenerationParams.class, EntityDataHolder.class});
	}

	public void testGenerateTimeSliceGroupsBeforeFreezeThrowsIllegalStateException() throws Exception {
		assertThrowsException(timeSliceContainer, "generateTimeSliceGroups", new Class[]
			{ GenerationParams.class, EntityDataHolder.class}, new Object[]
			{ null, new EntityDataHolderImpl()}, IllegalStateException.class);
	}

	public void testGenerateTimeSliceGroupsWithNoCategoryReturnsSingleSet() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice ts1 = TimeSlice.createInstance(null, ds1);
		TimeSlice ts2 = TimeSlice.createInstance(ds1, ds2);
		TimeSlice ts3 = TimeSlice.createInstance(ds2, null);
		timeSliceContainer.add(ts1);
		timeSliceContainer.add(ts2);
		timeSliceContainer.add(ts3);
		timeSliceContainer.freeze();

		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType()));
		ObjectMother.attachGridTemplateColumns((GridTemplate) grid.getTemplate(), 2);
		grid.setNumRows(1);
		GuidelineGenerateParams generateParams = new GuidelineGenerateParams(ds1, null, grid, -1, 1);
		List<TimeSlice[]> list = timeSliceContainer.generateTimeSliceGroups(generateParams, new EntityDataHolderImpl());
		assertEquals(1, list.size());
		assertEquals(2, list.get(0).length);
	}

	public void testGenerateTimeSliceGroupsHappyCase() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice ts1 = TimeSlice.createInstance(null, ds1);
		TimeSlice ts2 = TimeSlice.createInstance(ds1, ds2);
		TimeSlice ts3 = TimeSlice.createInstance(ds2, null);
		timeSliceContainer.add(ts1);
		timeSliceContainer.add(ts2);
		timeSliceContainer.add(ts3);
		timeSliceContainer.freeze();

		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType()));
		ObjectMother.attachGridTemplateColumns((GridTemplate) grid.getTemplate(), 2);
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

	public void testGenerateTimeSliceGroupsDoesNotUseDSOutsideRange() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));

		TimeSlice ts1 = TimeSlice.createInstance(null, ds1);
		TimeSlice ts2 = TimeSlice.createInstance(ds1, ds2);
		TimeSlice ts3 = TimeSlice.createInstance(ds2, null);
		timeSliceContainer.add(ts1);
		timeSliceContainer.add(ts2);
		timeSliceContainer.add(ts3);
		timeSliceContainer.freeze();

		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType()));
		ObjectMother.attachGridTemplateColumns((GridTemplate) grid.getTemplate(), 2);
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
	protected void setUp() throws Exception {
		super.setUp();
		timeSliceContainer = new TimeSliceContainer();
	}

	protected void tearDown() throws Exception {
		TimeSlice.resetNextID();
		super.tearDown();
	}
}
