package com.mindbox.pe.client.common.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

public class AbstractCategoryEntityCacheTableModelTest extends AbstractClientTestBase {

	private class ModelImpl extends AbstractCategoryEntityCacheTableModel<GuidelineReportData> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -401941969150827465L;

		public ModelImpl() {
			super("Column 1");
		}

		public Object getValueAt(int arg0, int arg1) {
			if (arg0 == 0 && arg1 == 0) {
				return getGenericEntityContext(AbstractCategoryEntityCacheTableModelTest.this.entityType, super.dataList.get(0).getContext());
			}
			else {
				return null;
			}
		}

	}

	private ModelImpl modelImpl;
	private GenericEntityType entityType;

	@Test
	public void testGetGenericEntityContextWithNullTypeReturnsNull() throws Exception {
		assertNull(modelImpl.getGenericEntityContext(null, new GuidelineContext[0]));
	}

	@Test
	public void testGetGenericEntityContextWithNullContextReturnsNull() throws Exception {
		assertNull(modelImpl.getGenericEntityContext(entityType, null));
	}

	@Test
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

	@Test
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

	@Before
	public void setUp() throws Exception {
		super.setUp();
		entityType = GenericEntityType.getAllGenericEntityTypes()[0];
		modelImpl = new ModelImpl();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
