package com.mindbox.pe.client.common.table;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

public class AbstractCategoryEntityCacheTableModelTest extends AbstractClientTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractCategoryEntityCacheTableModelTest Tests");
		suite.addTestSuite(AbstractCategoryEntityCacheTableModelTest.class);
		return suite;
	}

	private class ModelImpl extends AbstractCategoryEntityCacheTableModel<GuidelineReportData> {
		public ModelImpl() {
			super("Column 1");
		}
		public Object getValueAt(int arg0, int arg1) {
			if (arg0 == 0 && arg1 == 0) {
				return getGenericEntityContext(
						AbstractCategoryEntityCacheTableModelTest.this.entityType,
						super.dataList.get(0).getContext());
			}
			else {
				return null;
			}
		}

	}

	private ModelImpl modelImpl;
	private GenericEntityType entityType;

	public AbstractCategoryEntityCacheTableModelTest(String name) {
		super(name);
	}

	public void testGetGenericEntityContextWithNullTypeReturnsNull() throws Exception {
		assertNull(modelImpl.getGenericEntityContext(null, new GuidelineContext[0]));
	}

	public void testGetGenericEntityContextWithNullContextReturnsNull() throws Exception {
		assertNull(modelImpl.getGenericEntityContext(entityType, null));
	}

	public void testGetGenericEntityContextHappyCaseForCategories() throws Exception {
		GuidelineContext context = new GuidelineContext(entityType.getCategoryType());
		context.setIDs(new int[] { 10, 20 });
		CategoryOrEntityValues values = modelImpl.getGenericEntityContext(entityType, new GuidelineContext[] { context });
		assertEquals(2, values.size());
		for (Iterator<CategoryOrEntityValue> iter = values.iterator(); iter.hasNext();) {
			CategoryOrEntityValue element = iter.next();
			assertFalse(element.isForEntity());
		}
	}

	public void testGetGenericEntityContextHappyCaseForEntities() throws Exception {
		GuidelineContext context = new GuidelineContext(entityType);
		context.setIDs(new int[] { 10, 20 });
		CategoryOrEntityValues values = modelImpl.getGenericEntityContext(entityType, new GuidelineContext[] { context });
		assertEquals(2, values.size());
		for (Iterator<CategoryOrEntityValue> iter = values.iterator(); iter.hasNext();) {
			CategoryOrEntityValue element = iter.next();
			assertTrue(element.isForEntity());
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		entityType = GenericEntityType.getAllGenericEntityTypes()[0];
		modelImpl = new ModelImpl();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
