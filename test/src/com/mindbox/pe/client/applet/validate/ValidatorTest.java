package com.mindbox.pe.client.applet.validate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;

public class ValidatorTest extends AbstractClientTestBase {

	public ValidatorTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ValidatorTest Tests");
		suite.addTestSuite(ValidatorTest.class);

		return suite;
	}

	public void testValidateGapsAndOverlapsInDatesWithNullOrEmptyListReturnsNull() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		testValidateGapsAndOverlapsInDates(null, false, grid, null, null, null, true);
		testValidateGapsAndOverlapsInDates(null, false, grid, new ArrayList<ProductGrid>(), null, null, false);
	}

	public void testValidateGapsAndOverlapsInDatesWithListOfSizeOneAndEditTypeReturnsNull() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(grid);
		
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		testValidateGapsAndOverlapsInDates(null, false, grid, gridList, ds1, null, true);
	}

	public void testValidateGapsAndOverlapsInDatesHappyCaseForEditWithGaps() throws Exception {
		DateSynonym ds0 = ObjectMother.createDateSynonym();
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setEffectiveDate(ds1);
		gridList.add(grid2);
		
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, ds2, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid1, gridList, null, ds0, true);
	}

	public void testValidateGapsAndOverlapsInDatesWithAllowGapsNegativeCaseForEditWithGaps() throws Exception {
		DateSynonym ds0 = ObjectMother.createDateSynonym();
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setEffectiveDate(ds1);
		gridList.add(grid2);
		
		testValidateGapsAndOverlapsInDates(null, true, grid2, gridList, ds2, null, true);
		testValidateGapsAndOverlapsInDates(null, true, grid1, gridList, null, ds0, true);
	}

	public void testValidateGapsAndOverlapsInDatesHappyCaseForNonEditWithGaps() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, ds2, null, false);
	}

	public void testValidateGapsAndOverlapsInDatesWithAllowGapsNegativeCaseForNonEditWithGaps() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		
		testValidateGapsAndOverlapsInDates(null, true, grid2, gridList, ds2, null, false);
	}

	public void testValidateGapsAndOverlapsInDatesHappyCaseForEditWithOverlaps() throws Exception {
		DateSynonym ds0 = ObjectMother.createDateSynonym();
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setEffectiveDate(ds1);
		gridList.add(grid2);
		
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, null, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, ds0, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, null, ds2, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid1, gridList, null, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid1, gridList, ds0, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid1, gridList, null, ds2, true);
	}

	public void testValidateGapsAndOverlapsInDatesHappyCaseForNonEditWithOverlaps() throws Exception {
		DateSynonym ds0 = ObjectMother.createDateSynonym();
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, null, null, false);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, ds0, null, false);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, null, ds2, false);
	}

	public void testValidateGapsAndOverlapsInDatesNegativeCaseForEdit() throws Exception {
		DateSynonym ds0 = ObjectMother.createDateSynonym();
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setEffectiveDate(ds1);
		gridList.add(grid2);
		
		testValidateGapsAndOverlapsInDates(null, false, grid2, gridList, ds1, ds2, true);
		testValidateGapsAndOverlapsInDates(null, false, grid2, gridList, ds1, null, true);
		testValidateGapsAndOverlapsInDates(null, false, grid1, gridList, ds0, ds1, true);
		testValidateGapsAndOverlapsInDates(null, false, grid1, gridList, null, ds1, true);
	}

	public void testValidateGapsAndOverlapsInDatesNegativeCaseForNonEdit() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		
		testValidateGapsAndOverlapsInDates(null, false, grid2, gridList, ds1, ds2, false);
		testValidateGapsAndOverlapsInDates(null, false, grid2, gridList, ds1, null, false);
	}

	private void testValidateGapsAndOverlapsInDates(String expected, boolean allowGaps, ProductGrid grid, List<ProductGrid> gridList, DateSynonym dsEff,
			DateSynonym dsExp, boolean editFlag) throws Exception {
		String result = Validator.validateGapsAndOverlapsInDates(allowGaps, grid, gridList, dsEff, dsExp, editFlag, false);
		assertEquals(expected, result);
	}

	public void testValidateDuplicateDatesWithNullOrEmptyListReturnsNull() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		testValidateDuplicateDates(null, grid, null, null, null, true);
		testValidateDuplicateDates(null, grid, new ArrayList<ProductGrid>(), null, null, false);
	}

	public void testValidateDuplicateDatesHappyCaseWithTrueCloneFlag() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(grid);
		testValidateDuplicateDates("InvalidActivationDateDuplicate", grid, gridList, null, null, true);

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		grid.setEffectiveDate(ds1);
		testValidateDuplicateDates("InvalidActivationDateDuplicate", grid, gridList, ds1, null, true);

		DateSynonym ds2 = ObjectMother.createDateSynonym();
		grid.setExpirationDate(ds2);
		testValidateDuplicateDates("InvalidActivationDateDuplicate", grid, gridList, ds1, ds2, true);
	}

	public void testValidateDuplicateDatesHappyCaseWithFalseCloneFlag() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();

		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid.setEffectiveDate(ds1);
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(grid);

		grid = ObjectMother.createGuidelineGrid((GridTemplate) grid.getTemplate());
		testValidateDuplicateDates("InvalidActivationDateDuplicate", grid, gridList, ds1, null, false);
	}

	public void testValidateDuplicateDatesWithNoDupsReturnsNull() throws Exception {

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid.setEffectiveDate(ds1);
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(grid);

		grid = ObjectMother.createGuidelineGrid((GridTemplate) grid.getTemplate());
		testValidateDuplicateDates(null, grid, gridList, null, null, true);
		testValidateDuplicateDates(null, grid, gridList, null, ds1, false);
	}

	private void testValidateDuplicateDates(String expected, ProductGrid grid, List<ProductGrid> gridList, DateSynonym dsEff,
			DateSynonym dsExp, boolean cloneFlag) throws Exception {
		String result = Validator.validateDuplicateDates(grid, gridList, dsEff, dsExp, cloneFlag);
		assertEquals(expected, result);
	}

	public void testValidateActivationDateRangeRejectsNullEffectiveDate() throws Exception {
		String errorKey = Validator.validateActivationDateRange(null, null, "");
		assertEquals("msg.warning.no.activation.date", errorKey);
	}

	/**
	 * Test validateActivationDateRange with grid not in production
	 * @throws Exception
	 */
	public void testValidateActivationDateRangeNotProduction() throws Exception {
		// set up production status in cache
		Map<String, List<TypeEnumValue>> map = getCachedTypeEnumValueMap();
		List<TypeEnumValue> enums = new ArrayList<TypeEnumValue>();
		TypeEnumValue qa = new TypeEnumValue(1, "qa", "qa");
		TypeEnumValue prod = new TypeEnumValue(2, "prod", "prod");
		enums.add(qa);
		enums.add(prod);
		map.put(TypeEnumValue.TYPE_STATUS, enums);

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2006);
		DateSynonym earlier = DateSynonym.createUnnamedInstance(c.getTime());
		c.set(Calendar.YEAR, 2007);
		DateSynonym later = DateSynonym.createUnnamedInstance(c.getTime());
		String errorKey = Validator.validateActivationDateRange(later, earlier, qa.getValue());
		assertNotNull(errorKey);
		assertEquals(errorKey, "InvalidActivationDateRangeMsg");
		errorKey = Validator.validateActivationDateRange(earlier, later, qa.getValue());
		assertNull(errorKey);
	}

	/**
	 * Test validateActivationDateRange with grid in production without
	 * production privilege
	 * TODO Gaughan: figure out how remove the privilege
	 * @throws Exception
	 */
	public void testValidateActivationDateRangeProductionWithoutPrivilege() throws Exception {
	}

	/**
	 * Test validateActivationDateRange with grid in production with edit
	 * production privilege 
	 * @throws Exception
	 */
	public void testValidateActivationDateRangeProductionWithPrivilege() throws Exception {
		// set up production status in cache
		Map<String, List<TypeEnumValue>> map = getCachedTypeEnumValueMap();
		List<TypeEnumValue> enums = new ArrayList<TypeEnumValue>();
		TypeEnumValue qa = new TypeEnumValue(1, "qa", "qa");
		TypeEnumValue prod = new TypeEnumValue(2, "prod", "prod");
		enums.add(qa);
		enums.add(prod);
		map.put(TypeEnumValue.TYPE_STATUS, enums);

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2005);
		DateSynonym earlier = DateSynonym.createUnnamedInstance(c.getTime());
		c.set(Calendar.YEAR, 2006);
		DateSynonym later = DateSynonym.createUnnamedInstance(c.getTime());

		String errorKey = Validator.validateActivationDateRange(later, earlier, prod.getValue());
		assertEquals("InvalidActivationDateRangeMsg", errorKey);

		errorKey = Validator.validateActivationDateRange(earlier, later, prod.getValue());
		assertNull(errorKey, null);
		c.set(Calendar.YEAR, 2010);
		DateSynonym futureEarlier = DateSynonym.createUnnamedInstance(c.getTime());
		c.set(Calendar.YEAR, 2011);
		DateSynonym futureLater = DateSynonym.createUnnamedInstance(c.getTime());
		errorKey = Validator.validateActivationDateRange(futureEarlier, futureLater, prod.getValue());
		assertNull(errorKey);
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<TypeEnumValue>> getCachedTypeEnumValueMap() throws Exception {
		return (Map<String, List<TypeEnumValue>>) ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedTypeEnumValueMap");
	}

	protected void setUp() throws Exception {
		super.setUp();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		ReflectionUtil.setPrivate(EntityModelCacheFactory.class, "instance", null);
	}
}
