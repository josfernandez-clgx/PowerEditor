package com.mindbox.pe.client.common.tree;

import static com.mindbox.pe.client.ClientTestObjectMother.createDateSynonym;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static com.mindbox.pe.unittest.UnitTestHelper.assertNotEquals;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

public class DatedCategoryTreeModelTest extends AbstractTestWithGenericEntityType {

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


	@Test
	public void testConstructorWithNullArgumentThrowsNullPointerException() throws Exception {
		try {
			new DatedCategoryTreeModel(null, new Date(), dataProviderImpl, false);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
		try {
			new DatedCategoryTreeModel(new GenericCategoryNode(createGenericCategory(entityType)), null, dataProviderImpl, false);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
		try {
			new DatedCategoryTreeModel(new GenericCategoryNode(createGenericCategory(entityType)), new Date(), null, false);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	@Test
	public void testConstructorWithNoEntityBuildsCategoryModelOnly() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		GenericCategory category3 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		category2.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID()));
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category2.getID()));

		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);
		dataProviderImpl.add(category3);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, false);

		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(category2.getID(), node.getGenericCategoryID());
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(category3.getID(), node.getGenericCategoryID());
	}

	@Test
	public void testConstructorWithEntityBuildsCategoryAndEntityModelWithOutEntitiesLoaded() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);

		GenericEntity entity1 = createGenericEntity(entityType);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity1);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, true);

		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(category2.getID(), node.getGenericCategoryID());
		assertEquals(0, node.getChildCount());
	}

	@Test
	public void testConstructorWithEntityBuildsCategoryAndEntityModelWithEntitiesLoaded() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);

		GenericEntity entity1 = createGenericEntity(entityType);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity1);

		GenericCategoryNode root = new GenericCategoryNode(category1);
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(root, new Date(), dataProviderImpl, true, false);
		model.addAllGenericEntityNodes((GenericCategoryNode) model.getRoot(), GenericEntityType.forCategoryType(category1.getType()));

		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(category2.getID(), node.getGenericCategoryID());
		assertEquals(1, node.getChildCount());
		GenericEntityNode entityNode = (GenericEntityNode) node.getChildAt(0);
		assertEquals(entity1.getID(), entityNode.getGenericEntityID());
	}

	@Test
	public void testAddGenericCategoryWithNullCategoryThrowsNullPointerException() throws Exception {
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(createGenericCategory(entityType)), new Date(), dataProviderImpl, false);
		assertThrowsNullPointerExceptionWithNullArgs(model, "addGenericCategory", new Class[] { GenericCategory.class });
	}

	@Test
	public void testAddGenericCategoryWithEffectiveCategoryAddsNode() throws Exception {
		GenericCategory rootCategory = createGenericCategory(entityType);
		dataProviderImpl.add(rootCategory);
		Date date = new Date();
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(rootCategory), date, dataProviderImpl, false);

		// add a category with no start and end date
		GenericCategory categoryToAdd = createGenericCategory(entityType);
		categoryToAdd.addParentKey(new DefaultMutableTimedAssociationKey(rootCategory.getID()));
		rootCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(categoryToAdd.getID()));
		dataProviderImpl.add(categoryToAdd);

		model.addGenericCategory(categoryToAdd);

		GenericCategoryNode rootNode = (GenericCategoryNode) model.getRoot();
		assertEquals(1, rootNode.getChildCount());
		GenericCategoryNode node = (GenericCategoryNode) rootNode.getChildAt(0);
		assertEquals(categoryToAdd.getID(), node.getGenericCategoryID());

		// add a category with future end date
		DateSynonym dateSynonym = createDateSynonym();
		dateSynonym.setDate(new Date(date.getTime() + 10));
		GenericCategory categoryToAdd2 = createGenericCategory(entityType);
		categoryToAdd2.addParentKey(new DefaultMutableTimedAssociationKey(categoryToAdd.getID(), null, dateSynonym));
		categoryToAdd.addChildAssociation(new DefaultMutableTimedAssociationKey(categoryToAdd2.getID(), null, dateSynonym));
		dataProviderImpl.add(categoryToAdd2);

		model.addGenericCategory(categoryToAdd2);

		assertEquals(1, rootNode.getChildCount());
		node = (GenericCategoryNode) rootNode.getChildAt(0);
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(categoryToAdd2.getID(), node.getGenericCategoryID());
	}

	@Test
	public void testAddGenericCategoryWithCategoryOfFutureEffectiveDateDoesNotAddNode() throws Exception {
		GenericCategory rootCategory = createGenericCategory(entityType);
		dataProviderImpl.add(rootCategory);
		Date date = new Date();
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(rootCategory), date, dataProviderImpl, false);

		DateSynonym dateSynonym = createDateSynonym();
		dateSynonym.setDate(new Date(date.getTime() + 10));

		GenericCategory categoryToAdd = createGenericCategory(entityType);
		categoryToAdd.addParentKey(new DefaultMutableTimedAssociationKey(rootCategory.getID(), dateSynonym, null));
		rootCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(categoryToAdd.getID(), dateSynonym, null));
		dataProviderImpl.add(categoryToAdd);

		model.addGenericCategory(categoryToAdd);

		GenericCategoryNode rootNode = (GenericCategoryNode) model.getRoot();
		assertEquals(0, rootNode.getChildCount());

		model.recalibrate(new Date(dateSynonym.getDate().getTime() + 10));
		rootNode = (GenericCategoryNode) model.getRoot();
		assertEquals(1, rootNode.getChildCount());
	}

	@Test
	public void testAddGenericCategoryWithCategoryOfPastExpirationDateDoesNotAddNode() throws Exception {
		GenericCategory rootCategory = createGenericCategory(entityType);
		dataProviderImpl.add(rootCategory);
		Date date = new Date();
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(rootCategory), date, dataProviderImpl, false);

		DateSynonym dateSynonym = createDateSynonym();
		dateSynonym.setDate(new Date(date.getTime() - 10));

		GenericCategory categoryToAdd = createGenericCategory(entityType);
		categoryToAdd.addParentKey(new DefaultMutableTimedAssociationKey(rootCategory.getID(), null, dateSynonym));
		rootCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(categoryToAdd.getID(), null, dateSynonym));
		dataProviderImpl.add(categoryToAdd);

		model.addGenericCategory(categoryToAdd);

		GenericCategoryNode rootNode = (GenericCategoryNode) model.getRoot();
		assertEquals(0, rootNode.getChildCount());
	}

	@Test
	public void testEditGenericCategoryWithNullCategoryThrowsNullPointerException() throws Exception {
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(createGenericCategory(entityType)), new Date(), dataProviderImpl, false);
		assertThrowsNullPointerExceptionWithNullArgs(model, "editGenericCategory", new Class[] { GenericCategory.class });
	}

	@Test
	public void testEditGenericCategoryWithRootCategoryOnlyChangesName() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, true);

		category1.setName(String.valueOf(System.currentTimeMillis()));
		model.editGenericCategory(category1);
		assertEquals(category1.getName(), ((GenericCategoryNode) model.getRoot()).getGenericCategory().getName());
		assertEquals(1, ((GenericCategoryNode) model.getRoot()).getChildCount());
	}

	@Test
	public void testEditGenericCategoryWithEffectiveNewParentMovesTheNode() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		GenericCategory category3 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		category2.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID()));
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);
		dataProviderImpl.add(category3);
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), dateSynonym.getDate(), dataProviderImpl, false);

		// move category3 from category 2 to category1
		DateSynonym dateSynonym2 = createDateSynonym();
		dateSynonym2.setDate(new Date(dateSynonym.getDate().getTime() + 100));

		((MutableTimedAssociationKey) category3.getParentKeyIterator().next()).setExpirationDate(dateSynonym);
		((MutableTimedAssociationKey) category2.getChildAssociations(category3.getID()).get(0)).setExpirationDate(dateSynonym);
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID(), dateSynonym, dateSynonym2));
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID(), dateSynonym, dateSynonym2));

		model.editGenericCategory(category3);
		GenericCategoryNode rootNode = (GenericCategoryNode) model.getRoot();
		assertEquals(2, rootNode.getChildCount());
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			assertEquals(0, rootNode.getChildAt(i).getChildCount());
		}
	}

	@Test
	public void testEditGenericCategoryWithNotYetEffectiveNewParentOnlyRemovesTheNode() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		GenericCategory category3 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		category2.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID()));
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);
		dataProviderImpl.add(category3);
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), dateSynonym.getDate(), dataProviderImpl, false);

		// move category3 from category 2 to category1 for a future date
		DateSynonym dateSynonym2 = createDateSynonym();
		dateSynonym2.setDate(new Date(dateSynonym.getDate().getTime() + 100));

		((MutableTimedAssociationKey) category3.getParentKeyIterator().next()).setExpirationDate(dateSynonym);
		((MutableTimedAssociationKey) category2.getChildAssociations(category3.getID()).get(0)).setExpirationDate(dateSynonym);
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID(), dateSynonym2, null));
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID(), dateSynonym2, null));

		model.editGenericCategory(category3);
		GenericCategoryNode rootNode = (GenericCategoryNode) model.getRoot();
		assertEquals(1, rootNode.getChildCount());
		assertEquals(0, rootNode.getChildAt(0).getChildCount());
		assertEquals(category2.getID(), ((GenericCategoryNode) rootNode.getChildAt(0)).getGenericCategoryID());
	}

	@Test
	public void testEditGenericCategoryWithExpiredNewParentOnlyRemovesTheNode() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		GenericCategory category3 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		category2.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID()));
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);
		dataProviderImpl.add(category3);
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), dateSynonym.getDate(), dataProviderImpl, false);

		// move category3 from category2 to category1 expired
		DateSynonym dateSynonym2 = createDateSynonym();
		dateSynonym2.setDate(new Date(dateSynonym.getDate().getTime() - 100));

		((MutableTimedAssociationKey) category3.getParentKeyIterator().next()).setExpirationDate(dateSynonym2);
		((MutableTimedAssociationKey) category2.getChildAssociations(category3.getID()).get(0)).setExpirationDate(dateSynonym2);
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID(), dateSynonym2, dateSynonym));
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID(), dateSynonym2, dateSynonym));

		model.editGenericCategory(category3);
		GenericCategoryNode rootNode = (GenericCategoryNode) model.getRoot();
		assertEquals(1, rootNode.getChildCount());
		assertEquals(0, rootNode.getChildAt(0).getChildCount());
		assertEquals(category2.getID(), ((GenericCategoryNode) rootNode.getChildAt(0)).getGenericCategoryID());
	}

	@Test
	public void testEditGenericCategoryWithAddedEntityAssocationsRefreshesEntityNodes() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);

		GenericEntity entity1 = createGenericEntity(entityType);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity1);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, true);

		// add entity relationship
		GenericEntity entity2 = createGenericEntity(entityType);
		entity2.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity2);

		model.editGenericCategory(category2);

		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(category2.getID(), node.getGenericCategoryID());
		assertEquals(2, node.getChildCount());
		GenericEntityNode entityNode = (GenericEntityNode) node.getChildAt(0);
		assertTrue(entityNode.getGenericEntityID() == entity1.getID() || entityNode.getGenericEntityID() == entity2.getID());
		entityNode = (GenericEntityNode) node.getChildAt(1);
		assertTrue(entityNode.getGenericEntityID() == entity1.getID() || entityNode.getGenericEntityID() == entity2.getID());
	}

	@Test
	public void testEditGenericCategoryWithRemovedEntityAssocationsClearsEntityNodes() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);

		GenericEntity entity1 = createGenericEntity(entityType);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity1);
		GenericEntity entity2 = createGenericEntity(entityType);
		entity2.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity2);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, true);

		// remove entity relationship
		dataProviderImpl.remove(entity1);
		dataProviderImpl.remove(entity2);

		model.editGenericCategory(category2);

		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(category2.getID(), node.getGenericCategoryID());
		assertEquals(0, node.getChildCount());
	}

	@Test
	public void testRemoveGenericCategoryWithNullCategoryThrowsNullPointerException() throws Exception {
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(createGenericCategory(entityType)), new Date(), dataProviderImpl, false);
		assertThrowsNullPointerExceptionWithNullArgs(model, "removeGenericCategory", new Class[] { GenericCategory.class });
	}

	@Test
	public void testRemoveGenericCategoryWithRootCategoryThrowsIllegalArgumentException() throws Exception {
		GenericCategory rootCategory = createGenericCategory(entityType);
		dataProviderImpl.add(rootCategory);
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(rootCategory), new Date(), dataProviderImpl, false);
		assertThrowsException(model, "removeGenericCategory", new Class[] { GenericCategory.class }, new Object[] { rootCategory }, IllegalArgumentException.class);
	}

	@Test
	public void testRemoveGenericCategoryHappyCaseForLeafNode() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, true);

		model.removeGenericCategory(category2);
		assertEquals(0, ((GenericCategoryNode) model.getRoot()).getChildCount());
	}

	@Test
	public void testRemoveGenericCategoryHappyCaseForNonLeafNode() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		GenericCategory category3 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		category2.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID()));
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);
		dataProviderImpl.add(category3);
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), dateSynonym.getDate(), dataProviderImpl, false);

		model.removeGenericCategory(category2);
		assertEquals(0, ((GenericCategoryNode) model.getRoot()).getChildCount());
	}

	@Test
	public void testRemoveGenericEntityWithNullEntityThrowsNullPointerException() throws Exception {
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(createGenericCategory(entityType)), new Date(), dataProviderImpl, false);
		assertThrowsNullPointerExceptionWithNullArgs(model, "removeGenericEntity", new Class[] { GenericEntity.class });
	}

	@Test
	public void testRemoveGenericEntityWithShowEntitySetToFalseIsNoOp() throws Exception {
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(createGenericCategory(entityType)), new Date(), dataProviderImpl, false);
		model.removeGenericEntity(createGenericEntity(entityType));
	}

	@Test
	public void testRemoveGenericEntityWithNoEntityNodeIsNoOp() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);

		GenericEntity entity1 = createGenericEntity(entityType);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity1);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, true, false);
		model.addAllGenericEntityNodes((GenericCategoryNode) model.getRoot(), GenericEntityType.forCategoryType(category1.getType()));
		model.removeGenericEntity(createGenericEntity(entityType));

		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(1, node.getChildCount());
	}

	@Test
	public void testRemoveGenericEntityHappyCaseForSingleEntityNode() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);

		GenericEntity entity1 = createGenericEntity(entityType);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity1);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, true);
		model.removeGenericEntity(entity1);

		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(0, node.getChildCount());
	}

	@Test
	public void testRemoveGenericEntityHappyCaseForMultipleEntityNodes() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		GenericCategory category3 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID()));
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);
		dataProviderImpl.add(category3);

		GenericEntity entity1 = createGenericEntity(entityType);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		GenericEntity entity2 = createGenericEntity(entityType);
		entity2.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		entity2.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category3.getID()));
		dataProviderImpl.add(entity1);
		dataProviderImpl.add(entity2);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, true, false);
		model.addAllGenericEntityNodes((GenericCategoryNode) model.getRoot(), GenericEntityType.forCategoryType(category1.getType()));
		GenericCategoryNode rootNode = (GenericCategoryNode) model.getRoot();
		assertEquals(category1.getId(), rootNode.getGenericCategory().getId());
		assertEquals(2, rootNode.getChildCount());
		assertNotEquals(((GenericCategoryNode) rootNode.getChildAt(0)).getGenericCategoryID(), ((GenericCategoryNode) rootNode.getChildAt(1)).getGenericCategoryID());

		GenericCategoryNode node = (GenericCategoryNode) rootNode.getChildAt(0);
		assertTrue(category2.getID() == node.getGenericCategoryID() || category3.getID() == node.getGenericCategoryID());

		if (node.getGenericCategory().equals(category2)) {
			assertEquals(2, node.getChildCount());
			node = (GenericCategoryNode) rootNode.getChildAt(1);
			assertEquals(category3.getId(), node.getGenericCategory().getId());
			assertEquals(1, node.getChildCount());
		}
		else { // category3
			assertEquals(category3.getId(), node.getGenericCategory().getId());
			assertEquals(1, node.getChildCount());
			node = (GenericCategoryNode) rootNode.getChildAt(1);
			assertEquals(category2.getId(), node.getGenericCategory().getId());
			assertEquals(2, node.getChildCount());
		}
		model.removeGenericEntity(entity2);

		rootNode = (GenericCategoryNode) model.getRoot();
		assertEquals(2, rootNode.getChildCount());

		node = (GenericCategoryNode) rootNode.getChildAt(0);
		if (node.getGenericCategory().equals(category2)) { // category2 now has one child
			assertEquals(1, node.getChildCount());
			node = (GenericCategoryNode) rootNode.getChildAt(1);
			assertEquals(category3.getId(), node.getGenericCategory().getId());
			assertEquals(0, node.getChildCount());
		}
		else { // category3 now has no children
			assertEquals(category3.getID(), node.getGenericCategoryID());
			assertEquals(0, node.getChildCount());
			node = (GenericCategoryNode) rootNode.getChildAt(1);
			assertEquals(category2.getId(), node.getGenericCategory().getId());
			assertEquals(1, node.getChildCount());
		}

	}

	@Test
	public void testRecalibrateWithNullDateThrowsNullPointerException() throws Exception {
		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(createGenericCategory(entityType)), new Date(), dataProviderImpl, false);
		assertThrowsNullPointerExceptionWithNullArgs(model, "recalibrate", new Class[] { Date.class });
	}

	@Test
	public void testRecalibrateHappyCaseForDateChanges() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		GenericCategory category3 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID(), null, dateSynonym));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID(), null, dateSynonym));
		category2.addChildAssociation(new DefaultMutableTimedAssociationKey(category3.getID(), dateSynonym, null));
		category3.addParentKey(new DefaultMutableTimedAssociationKey(category2.getID(), dateSynonym, null));

		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);
		dataProviderImpl.add(category3);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, false);

		model.recalibrate(new Date(dateSynonym.getDate().getTime() - 10));
		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(category2.getID(), node.getGenericCategoryID());
		assertEquals(0, node.getChildCount());

		model.recalibrate(new Date(dateSynonym.getDate().getTime() + 10));
		node = (GenericCategoryNode) model.getRoot();
		assertEquals(0, node.getChildCount());
	}

	@Test
	public void testResetShowEntitiesWithAllowEntitiesHappyCase() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);

		GenericEntity entity1 = createGenericEntity(entityType);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity1);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, false);
		List<GenericCategoryNode> categories = new ArrayList<GenericCategoryNode>();
		categories.add((GenericCategoryNode) model.getRoot());
		categories.add((GenericCategoryNode) ((GenericCategoryNode) model.getRoot()).getChildAt(0));
		model.resetShowEntities(true, categories);

		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(category2.getID(), node.getGenericCategoryID());
		assertEquals(1, node.getChildCount());
		GenericEntityNode entityNode = (GenericEntityNode) node.getChildAt(0);
		assertEquals(entity1.getID(), entityNode.getGenericEntityID());
	}

	@Test
	public void testResetShowEntitiesWithNoEntitiesHappyCase() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		dataProviderImpl.add(category1);
		dataProviderImpl.add(category2);

		GenericEntity entity1 = createGenericEntity(entityType);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		dataProviderImpl.add(entity1);

		DatedCategoryTreeModel model = new DatedCategoryTreeModel(new GenericCategoryNode(category1), new Date(), dataProviderImpl, true);
		List<GenericCategoryNode> categories = new ArrayList<GenericCategoryNode>();
		categories.add(new GenericCategoryNode(category1));
		categories.add(new GenericCategoryNode(category2));
		model.resetShowEntities(false, categories);

		GenericCategoryNode node = (GenericCategoryNode) model.getRoot();
		assertEquals(1, node.getChildCount());
		node = (GenericCategoryNode) node.getChildAt(0);
		assertEquals(category2.getID(), node.getGenericCategoryID());
		assertEquals(0, node.getChildCount());
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		dataProviderImpl = new DataProviderImpl();
	}

	@After
	public void tearDown() throws Exception {
	}
}
