package com.mindbox.pe.client;


import static com.mindbox.pe.unittest.UnitTestHelper.assertArrayEqualsIgnoresOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;

public class ClientUtilTest extends AbstractClientTestBase {

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		ReflectionUtil.setPrivate(EntityModelCacheFactory.class, "instance", null);
	}

	@Test
	public void testSetContextWithCategoryContextArrayClearsEntityInGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", templateUsageType), null, null);
		grid.setGenericEntityIDs(entityType1, new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(entityType2, new int[] { 11 });

		GuidelineContext c1 = new GuidelineContext(entityType2.getCategoryType());
		c1.setIDs(new int[] { 200, 300 });
		ClientUtil.setContext(grid, new GuidelineContext[] { c1 });
		assertFalse(grid.hasAnyGenericEntityContext());
		assertTrue(grid.hasGenericCategoryContext(entityType2));
	}

	@Test
	public void testSetContextWithEntityContextArrayClearsCategoryInGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", templateUsageType), null, null);
		grid.setGenericEntityIDs(entityType1, new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(entityType2, new int[] { 11 });

		GuidelineContext c1 = new GuidelineContext(entityType2);
		c1.setIDs(new int[] { 200, 300 });
		ClientUtil.setContext(grid, new GuidelineContext[] { c1 });
		assertFalse(grid.hasAnyGenericCategoryContext());
		assertTrue(grid.hasGenericEntityContext(entityType2));
	}

	@Test
	public void testSetContextWithValidContextSetsContextPropertyOnEmptyGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", templateUsageType), null, null);

		GuidelineContext c1 = new GuidelineContext(entityType2);
		c1.setIDs(new int[] { 200, 300 });
		GuidelineContext c2 = new GuidelineContext(entityType3.getCategoryType());
		c2.setIDs(new int[] { 1, 2, 3, 4, 5 });

		ClientUtil.setContext(grid, new GuidelineContext[] { c1, c2 });
		assertArrayEqualsIgnoresOrder(new int[] { 200, 300 }, grid.getGenericEntityIDs(entityType2));
		assertArrayEqualsIgnoresOrder(new int[] { 1, 2, 3, 4, 5 }, grid.getGenericCategoryIDs(entityType3));
	}

	@Test
	public void testSetContextWithValidContextReplacesExistingContextInGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", templateUsageType), null, null);
		grid.setGenericEntityIDs(entityType2, new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(entityType3, new int[] { 11 });

		GuidelineContext c1 = new GuidelineContext(entityType2);
		c1.setIDs(new int[] { 200, 300 });
		GuidelineContext c2 = new GuidelineContext(entityType3.getCategoryType());
		c2.setIDs(new int[] { 1, 2, 3, 4, 5 });

		ClientUtil.setContext(grid, new GuidelineContext[] { c1, c2 });
		assertArrayEqualsIgnoresOrder(new int[] { 200, 300 }, grid.getGenericEntityIDs(entityType2));
		assertArrayEqualsIgnoresOrder(new int[] { 1, 2, 3, 4, 5 }, grid.getGenericCategoryIDs(entityType3));
	}

	@Test
	public void testisHighestStatusHasHighestEnumID() throws Exception {
		Map<String, List<TypeEnumValue>> map = new HashMap<String, List<TypeEnumValue>>();
		List<TypeEnumValue> enums = new ArrayList<TypeEnumValue>();
		TypeEnumValue qa = new TypeEnumValue(1, "qa", "qa");
		TypeEnumValue prod = new TypeEnumValue(2, "prod", "prod");
		enums.add(qa);
		enums.add(prod);
		map.put(TypeEnumValue.TYPE_STATUS, enums);

		ClientUtil.getInstance().resetCachedTypeEnumValueMap(map);

		String highestStatus = ClientUtil.getHighestStatus();
		assertEquals(highestStatus, prod.getValue());
	}
}
