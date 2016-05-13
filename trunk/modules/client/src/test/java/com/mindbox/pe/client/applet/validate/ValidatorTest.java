package com.mindbox.pe.client.applet.validate;

import static com.mindbox.pe.client.ClientTestObjectMother.createDateSynonym;
import static com.mindbox.pe.client.ClientTestObjectMother.createGridTemplate;
import static com.mindbox.pe.client.ClientTestObjectMother.createGuidelineGrid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;

public class ValidatorTest extends AbstractClientTestBase {

	@SuppressWarnings("unchecked")
	private Map<String, List<TypeEnumValue>> getCachedTypeEnumValueMap() throws Exception {
		return (Map<String, List<TypeEnumValue>>) ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedTypeEnumValueMap");
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		ReflectionUtil.setPrivate(EntityModelCacheFactory.class, "instance", null);
	}

	/**
	 * Test validateActivationDateRange with grid not in production
	 * @throws Exception
	 */
	@Test
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
	 * Test validateActivationDateRange with grid in production with edit
	 * production privilege 
	 * @throws Exception
	 */
	@Test
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

	@Test
	public void testValidateActivationDateRangeRejectsNullEffectiveDate() throws Exception {
		String errorKey = Validator.validateActivationDateRange(null, null, "");
		assertEquals("msg.warning.no.activation.date", errorKey);
	}

	private void testValidateDuplicateDates(String expected, ProductGrid grid, List<ProductGrid> gridList, DateSynonym dsEff, DateSynonym dsExp, boolean cloneFlag) throws Exception {
		String result = Validator.validateDuplicateDates(grid, gridList, dsEff, dsExp, cloneFlag);
		assertEquals(expected, result);
	}

	@Test
	public void testValidateDuplicateDatesHappyCaseWithFalseCloneFlag() throws Exception {
		DateSynonym ds1 = createDateSynonym();

		ProductGrid grid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid.setEffectiveDate(ds1);
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(grid);

		grid = createGuidelineGrid((GridTemplate) grid.getTemplate());
		testValidateDuplicateDates("InvalidActivationDateDuplicate", grid, gridList, ds1, null, false);
	}

	@Test
	public void testValidateDuplicateDatesHappyCaseWithTrueCloneFlag() throws Exception {
		ProductGrid grid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(grid);
		testValidateDuplicateDates("InvalidActivationDateDuplicate", grid, gridList, null, null, true);

		DateSynonym ds1 = createDateSynonym();
		grid.setEffectiveDate(ds1);
		testValidateDuplicateDates("InvalidActivationDateDuplicate", grid, gridList, ds1, null, true);

		DateSynonym ds2 = createDateSynonym();
		grid.setExpirationDate(ds2);
		testValidateDuplicateDates("InvalidActivationDateDuplicate", grid, gridList, ds1, ds2, true);
	}

	@Test
	public void testValidateDuplicateDatesWithNoDupsReturnsNull() throws Exception {

		DateSynonym ds1 = createDateSynonym();
		ProductGrid grid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid.setEffectiveDate(ds1);
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(grid);

		grid = createGuidelineGrid((GridTemplate) grid.getTemplate());
		testValidateDuplicateDates(null, grid, gridList, null, null, true);
		testValidateDuplicateDates(null, grid, gridList, null, ds1, false);
	}

	@Test
	public void testValidateDuplicateDatesWithNullOrEmptyListReturnsNull() throws Exception {
		ProductGrid grid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		testValidateDuplicateDates(null, grid, null, null, null, true);
		testValidateDuplicateDates(null, grid, new ArrayList<ProductGrid>(), null, null, false);
	}

	private void testValidateGapsAndOverlapsInDates(String expected, boolean allowGaps, ProductGrid grid, List<ProductGrid> gridList, DateSynonym dsEff, DateSynonym dsExp, boolean editFlag)
			throws Exception {
		String result = Validator.validateGapsAndOverlapsInDates(allowGaps, grid, gridList, dsEff, dsExp, editFlag, false);
		assertEquals(expected, result);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesHappyCaseForEditWithGaps() throws Exception {
		DateSynonym ds0 = createDateSynonym();
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setEffectiveDate(ds1);
		gridList.add(grid2);

		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, ds2, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid1, gridList, null, ds0, true);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesHappyCaseForEditWithOverlaps() throws Exception {
		DateSynonym ds0 = createDateSynonym();
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setEffectiveDate(ds1);
		gridList.add(grid2);


		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, null, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, ds0, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, null, ds2, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid1, gridList, null, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid1, gridList, ds0, null, true);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid1, gridList, null, ds2, true);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesHappyCaseForNonEditWithGaps() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = createGuidelineGrid((GridTemplate) grid1.getTemplate());

		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, ds2, null, false);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesHappyCaseForNonEditWithOverlaps() throws Exception {
		DateSynonym ds0 = createDateSynonym();
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = createGuidelineGrid((GridTemplate) grid1.getTemplate());

		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, null, null, false);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, ds0, null, false);
		testValidateGapsAndOverlapsInDates("msg.warning.invalid.act.date.not.sequential", false, grid2, gridList, null, ds2, false);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesNegativeCaseForEdit() throws Exception {
		DateSynonym ds0 = createDateSynonym();
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setEffectiveDate(ds1);
		gridList.add(grid2);

		testValidateGapsAndOverlapsInDates(null, false, grid2, gridList, ds1, ds2, true);
		testValidateGapsAndOverlapsInDates(null, false, grid2, gridList, ds1, null, true);
		testValidateGapsAndOverlapsInDates(null, false, grid1, gridList, ds0, ds1, true);
		testValidateGapsAndOverlapsInDates(null, false, grid1, gridList, null, ds1, true);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesNegativeCaseForNonEdit() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = createGuidelineGrid((GridTemplate) grid1.getTemplate());

		testValidateGapsAndOverlapsInDates(null, false, grid2, gridList, ds1, ds2, false);
		testValidateGapsAndOverlapsInDates(null, false, grid2, gridList, ds1, null, false);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesWithAllowGapsNegativeCaseForEditWithGaps() throws Exception {
		DateSynonym ds0 = createDateSynonym();
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setEffectiveDate(ds1);
		gridList.add(grid2);

		testValidateGapsAndOverlapsInDates(null, true, grid2, gridList, ds2, null, true);
		testValidateGapsAndOverlapsInDates(null, true, grid1, gridList, null, ds0, true);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesWithAllowGapsNegativeCaseForNonEditWithGaps() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		DateSynonym ds2 = createDateSynonym();
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		ProductGrid grid1 = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setExpirationDate(ds1);
		gridList.add(grid1);
		ProductGrid grid2 = createGuidelineGrid((GridTemplate) grid1.getTemplate());

		testValidateGapsAndOverlapsInDates(null, true, grid2, gridList, ds2, null, false);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesWithListOfSizeOneAndEditTypeReturnsNull() throws Exception {
		ProductGrid grid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(grid);

		DateSynonym ds1 = createDateSynonym();
		testValidateGapsAndOverlapsInDates(null, false, grid, gridList, ds1, null, true);
	}

	@Test
	public void testValidateGapsAndOverlapsInDatesWithNullOrEmptyListReturnsNull() throws Exception {
		ProductGrid grid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		testValidateGapsAndOverlapsInDates(null, false, grid, null, null, null, true);
		testValidateGapsAndOverlapsInDates(null, false, grid, new ArrayList<ProductGrid>(), null, null, false);
	}
}
