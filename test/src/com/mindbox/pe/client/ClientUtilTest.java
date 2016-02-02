package com.mindbox.pe.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;

public class ClientUtilTest extends AbstractClientTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ClientUtilTest Tests");
		suite.addTestSuite(ClientUtilTest.class);
		return suite;
	}

	public ClientUtilTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		ReflectionUtil.setPrivate(EntityModelCacheFactory.class, "instance", null);
	}

	public void testSetContextWithCategoryContextArrayClearsEntityInGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 11 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c1.setIDs(new int[] { 200, 300 });
		ClientUtil.setContext(grid, new GuidelineContext[] { c1 });
		assertFalse(grid.hasAnyGenericEntityContext());
		assertTrue(grid.hasGenericCategoryContext(GenericEntityType.forName("product")));
	}

	public void testSetContextWithEntityContextArrayClearsCategoryInGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 11 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		ClientUtil.setContext(grid, new GuidelineContext[] { c1 });
		assertFalse(grid.hasAnyGenericCategoryContext());
		assertTrue(grid.hasGenericEntityContext(GenericEntityType.forName("product")));
	}

	public void testSetContextWithValidContextSetsContextPropertyOnEmptyGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 1, 2, 3, 4, 5 });

		ClientUtil.setContext(grid, new GuidelineContext[] { c1, c2 });
		assertEquals(new int[] { 200, 300 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
		assertEquals(new int[] { 1, 2, 3, 4, 5 }, grid.getGenericCategoryIDs(GenericEntityType.forName("investor")));
	}

	public void testSetContextWithValidContextReplacesExistingContextInGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("investor"), new int[] { 11 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 1, 2, 3, 4, 5 });

		ClientUtil.setContext(grid, new GuidelineContext[] { c1, c2 });
		assertEquals(new int[] { 200, 300 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
		assertEquals(new int[] { 1, 2, 3, 4, 5 }, grid.getGenericCategoryIDs(GenericEntityType.forName("investor")));
	}

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
