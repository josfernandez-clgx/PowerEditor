package com.mindbox.pe.client.common.tree;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mindbox.pe.client.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;

/**
 * This should <b>not</b> be part of automated unit test suite. 
 * This provide tests that measures performance of {@link DatedCategoryTreeModel} class.
 * @author Geneho Kim
 * @since 5.1.0
 */
@RunWith(Parameterized.class)
public class DatedCategoryTreeModelPerformanceTest extends AbstractTestWithGenericEntityType {

	@Parameters(name = "{index}: {0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] { { "testResetShowEntitiesPerformance", 10, 3, 5, 2 }, { "testResetShowEntitiesPerformanceOld", 10, 3, 5, 2 } });
	}

	private static class DataProviderImpl implements DatedCategoryTreeModel.DataProvider {

		private final List<GenericCategory> catList = new ArrayList<GenericCategory>();
		private final List<GenericEntity> entList = new ArrayList<GenericEntity>();

		public GenericCategory getGenericCategory(GenericEntityType type, int categoryID) {
			return getGenericCategory(type.getCategoryType(), categoryID);
		}

		public GenericCategory getGenericCategory(int typeID, int categoryID) {
			for (Iterator<GenericCategory> iter = catList.iterator(); iter.hasNext();) {
				GenericCategory element = iter.next();
				if (element.getID() == categoryID && element.getType() == typeID) {
					return element;
				}
			}
			return null;
		}

		void add(GenericCategory genericCategory) {
			catList.add(genericCategory);
		}

		@SuppressWarnings("unused")
		void remove(GenericCategory genericCategory) {
			catList.remove(genericCategory);
		}

		void add(GenericEntity genericEntity) {
			entList.add(genericEntity);
		}

		@SuppressWarnings("unused")
		void remove(GenericEntity genericEntity) {
			entList.remove(genericEntity);
		}

		public List<GenericEntity> getGenericEntitiesInCategory(GenericEntityType type, int categoryID, Date date, boolean includeDescendents) {
			List<GenericEntity> list = new ArrayList<GenericEntity>();
			for (Iterator<GenericEntity> iter = entList.iterator(); iter.hasNext();) {
				GenericEntity element = iter.next();
				if (element.getCategoryIDList(date).contains(new Integer(categoryID))) {
					list.add(element);
				}
			}
			return list;
		}
	}

	private DataProviderImpl dataProviderImpl;
	private final int childLimit;
	private final int tierLimit;
	private final int entityLimit;
	private final int run;
	private GenericCategoryNode rootCategoryNode;

	public DatedCategoryTreeModelPerformanceTest(String name, int childLimit, int tierLimit, int entityLimit, int run) {
		this.childLimit = childLimit;
		this.tierLimit = tierLimit;
		this.entityLimit = entityLimit;
		this.run = run;
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		dataProviderImpl = new DataProviderImpl();
		GenericCategory rootCategory = createGenericCategory(entityType);
		dataProviderImpl.add(rootCategory);

		// build first tier
		addChildCategories(childLimit, rootCategory, tierLimit);

		rootCategoryNode = new GenericCategoryNode(rootCategory);
	}

	@Test
	public void testConstructorPerformanceWithNoEntities() throws Exception {
		long total = 0L;
		for (int i = 0; i < run; i++) {
			long start = System.currentTimeMillis();
			new DatedCategoryTreeModel(rootCategoryNode, new Date(), dataProviderImpl, false);
			long elapsed = System.currentTimeMillis() - start;
			System.out.println("Constructor(no entities) of " + this + " = " + elapsed + " (ms)");
			total += elapsed;
		}
		System.out.println("Average Constructor(no entities) = " + (long) total / run + " (ms)");
	}

	@Test
	public void testConstructorPerformanceWithEntities() throws Exception {
		long total = 0L;
		for (int i = 0; i < run; i++) {
			long start = System.currentTimeMillis();
			new DatedCategoryTreeModel(rootCategoryNode, new Date(), dataProviderImpl, true);
			long elapsed = System.currentTimeMillis() - start;
			System.out.println("Constructor(w/ entities) of " + this + " = " + elapsed + " (ms)");
			total += elapsed;
		}
		System.out.println("Average Constructor(w/ entities) = " + (long) total / run + " (ms)");
	}

	@Test
	public void testResetShowEntitiesPerformanceOld() throws Exception {
		long total = 0L;
		for (int i = 0; i < run; i++) {
			DatedCategoryTreeModel model = new DatedCategoryTreeModel(rootCategoryNode, new Date(), dataProviderImpl, false);
			long start = System.currentTimeMillis();
			model.resetShowEntitiesOld(true);

			long elapsed = System.currentTimeMillis() - start;
			System.out.println("resetShowEntitiesOld of " + this + " = " + elapsed + " (ms)");
			total += elapsed;
		}
		System.out.println("resetShowEntitiesOld = " + (long) total / run + " (ms)");
	}

	private void addChildCategories(int count, GenericCategory parentCategory, int tierLimit) throws Exception {
		for (int i = 0; i < count; i++) {
			GenericCategory category = createGenericCategory(entityType);
			parentCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(category.getID()));
			category.addParentKey(new DefaultMutableTimedAssociationKey(parentCategory.getID()));
			dataProviderImpl.add(category);
			if (tierLimit > 0) {
				addChildCategories(count, category, (tierLimit - 1));
			}
			else {
				addEntities(entityLimit, category);
			}
		}
	}

	private void addEntities(int count, GenericCategory parentCategory) {
		for (int i = 0; i < count; i++) {
			GenericEntity entity = createGenericEntity(entityType);
			entity.addCategoryAssociation(new DefaultMutableTimedAssociationKey(parentCategory.getID()));
			dataProviderImpl.add(entity);
		}
	}

	public String toString() {
		return "DatedCategoryTreeModelPerformanceTest[tier=" + tierLimit + ",children=" + childLimit + ",entity=" + entityLimit + "]";
	}
}
