package com.mindbox.pe.server.imexport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.config.AbstractDigestedObjectHolder;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;
import com.mindbox.pe.model.filter.NameFilterSpec;
import com.mindbox.pe.model.filter.PersistentFilterSpec;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.imexport.digest.ActivationDates;
import com.mindbox.pe.server.imexport.digest.Association;
import com.mindbox.pe.server.imexport.digest.CategoryDigest;
import com.mindbox.pe.server.imexport.digest.Entity;
import com.mindbox.pe.server.imexport.digest.EntityIdentity;
import com.mindbox.pe.server.imexport.digest.Grid;
import com.mindbox.pe.server.imexport.digest.GridActivation;
import com.mindbox.pe.server.imexport.digest.GridRow;
import com.mindbox.pe.server.imexport.digest.ImportXMLDigester;
import com.mindbox.pe.server.imexport.digest.Parent;
import com.mindbox.pe.server.imexport.digest.Property;
import com.mindbox.pe.server.model.User;

/**
 * Tests for {@link com.mindbox.pe.server.imexport.ObjectConverter}.
 * 
 * @author Geneho Kim
 * 
 */
public class ObjectConverterTest extends AbstractTestWithTestConfig {

	private static void addEntityIdentity(AbstractDigestedObjectHolder objHolder, String type, int id) {
		EntityIdentity entityIdentity = new EntityIdentity();
		entityIdentity.setType(type);
		entityIdentity.setId(id);
		objHolder.addObject(entityIdentity);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(ObjectConverterTest.class);
		suite.setName("ObjectConverterTest Tests");
		return suite;
	}

	private static class TestReplacementImpl implements ReplacementDateSynonymProvider {

		private DateSynonym replacement = ObjectMother.createDateSynonym();

		public DateSynonym getReplacementDateSynonymForImport() throws ImportException {
			return replacement;
		}

		DateSynonym getReplacement() {
			return replacement;
		}
	}

	private TestReplacementImpl testReplacementImpl;

	public ObjectConverterTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		GridTemplate template = new GridTemplate(1000, "template", TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template);
		ParameterTemplate paramTemplate = new ParameterTemplate(2000, "paramt", 9999, "desc");
		ParameterTemplateManager.getInstance().addParameterTemplate(paramTemplate);
		// This is to eliminate DB calls from ObjectConverter#getEffectiveDateSynonym and ObjectConverter#getExpirationDateSynonym
		DateSynonym dateSynonym = ObjectMother.createDateSynonym();
		dateSynonym.setDate(getDate(2006, 1, 1, 0, 0, 0));
		DateSynonymManager.getInstance().insert(dateSynonym);
		dateSynonym = ObjectMother.createDateSynonym();
		dateSynonym.setDate(getDate(2005, 10, 1, 0, 0, 0));
		DateSynonymManager.getInstance().insert(dateSynonym);
		this.testReplacementImpl = new TestReplacementImpl();
	}

	protected void tearDown() throws Exception {
		GuidelineTemplateManager.getInstance().removeFromCache(1000);
		ParameterTemplateManager.getInstance().removeFromCache(2000);
		GridManager.getInstance().startLoading();
		DateSynonymManager.getInstance().startLoading();
		config.resetConfiguration();
		super.tearDown();
	}

	public void testToFilterTypeStringWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(
				ObjectConverter.class,
				"toFilterTypeString",
				new Class[] { PersistentFilterSpec.class });
	}

	public void testToFilterTypeStringWithGenericEntityFilterReturnsValidValue() throws Exception {
		GenericEntityFilterSpec filter = new GenericEntityFilterSpec(GenericEntityType.forName("product"), "name");
		assertEquals(GenericEntityType.forName("product").toString(), ObjectConverter.toFilterTypeString(filter));
	}

	public void testToFilterTypeStringWithEntityTypeFilterReturnsValidValue() throws Exception {
		NameFilterSpec<CBRCase> filter = new NameFilterSpec<CBRCase>(EntityType.CBR_CASE, null, "name");
		assertEquals(EntityType.CBR_CASE.toString(), ObjectConverter.toFilterTypeString(filter));
	}

	public void testAsGenericCategoryForEntityWithNullGenericEntityTypeThrowsNullPointerException() throws Exception {
		try {
			ObjectConverter.asGenericCategory(null, new Entity(), false, new HashMap<String, Integer>(), testReplacementImpl);
			fail("Expected ImportException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testAsGenericCategoryForEntityWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			ObjectConverter.asGenericCategory(GenericEntityType.forName("product"), null, false, new HashMap<String, Integer>(), testReplacementImpl);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testAsGenericCategoryForEntityWithNullIDMapThrowsNullPointerException() throws Exception {
		try {
			ObjectConverter.asGenericCategory(GenericEntityType.forName("product"), new Entity(), false, null, testReplacementImpl);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testAsGenericCategoryForEntityWithInvalidEntityTypeThrowsIllegalArgumentException() throws Exception {
		Entity entity = new Entity();
		entity.setId(100);
		entity.setType("product");
		try {
			ObjectConverter.asGenericCategory(GenericEntityType.forName("product"), entity, false, new HashMap<String, Integer>(), testReplacementImpl);
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

	public void testAsGenericCategoryForEntityWithFalseMergeAndMatchingParentIDUsesIDMap() throws Exception {
		GenericEntityType type = GenericEntityType.forName("channel");
		Property property = new Property();
		property.setName("name");
		property.setValue("category name");
		Entity entity = new Entity();
		entity.addObject(property);
		entity.setId(100);
		entity.setParentID(20);
		entity.setType("category");
		Map<String, Integer> idMap = new HashMap<String, Integer>();
		idMap.put(ObjectConverter.asCategoryIDMapKey(type.toString(), 20), new Integer(4444));
		GenericCategory category = ObjectConverter.asGenericCategory(type, entity, false, idMap, testReplacementImpl);

		assertEquals(100, category.getID());

		Iterator<MutableTimedAssociationKey> parentKeyIter = category.getParentKeyIterator();
		MutableTimedAssociationKey parentKey = parentKeyIter.next();
		assertEquals(4444, parentKey.getAssociableID());
		assertEquals(testReplacementImpl.getReplacement(), parentKey.getEffectiveDate());
		assertFalse(parentKey.hasExpirationDate());

		assertFalse(parentKeyIter.hasNext()); // only one parent
	}

	public void testAsGenericCategoryForEntityWithFalseMergeAndNonMatchingParentIDUsesOriginal() throws Exception {
		GenericEntityType type = GenericEntityType.forName("channel");
		Property property = new Property();
		property.setName("name");
		property.setValue("category name");
		Entity entity = new Entity();
		entity.addObject(property);
		entity.setId(100);
		entity.setParentID(22);
		entity.setType("category");
		Map<String, Integer> idMap = new HashMap<String, Integer>();
		idMap.put(ObjectConverter.asCategoryIDMapKey(type.toString(), entity.getId()), new Integer(4444));
		GenericCategory category = ObjectConverter.asGenericCategory(type, entity, false, idMap, testReplacementImpl);

		assertEquals(100, category.getID());

		Iterator<MutableTimedAssociationKey> parentKeyIter = category.getParentKeyIterator();
		MutableTimedAssociationKey parentKey = parentKeyIter.next();
		assertEquals(22, parentKey.getAssociableID());
		assertEquals(testReplacementImpl.getReplacement(), parentKey.getEffectiveDate());
		assertFalse(parentKey.hasExpirationDate());

		assertFalse(parentKeyIter.hasNext()); // only one parent
	}

	public void testAsGenericCategoryForCategoryDigestWithNullCategoryDigestThrowsNullPointerException() throws Exception {
		try {
			ObjectConverter.asGenericCategory(null, false, new HashMap<String, Integer>(), null, testReplacementImpl);
			fail("Expected ImportException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testAsGenericCategoryForCategoryDigestWithNullIDMapThrowsNullPointerException() throws Exception {
		try {
			ObjectConverter.asGenericCategory(new CategoryDigest(), false, null, null, testReplacementImpl);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testAsGenericCategoryForCategoryDigestWithFalseMergeAndMatchingParentIDUsesIDMap() throws Exception {
		GenericEntityType type = GenericEntityType.forName("channel");
		Property property = new Property();
		property.setName("name");
		property.setValue("category name");
		CategoryDigest categoryDigest = new CategoryDigest();
		categoryDigest.addObject(property);
		categoryDigest.setId(100);
		categoryDigest.setParentID(20);
		categoryDigest.setType("channel");
		Map<String, Integer> idMap = new HashMap<String, Integer>();
		idMap.put(ObjectConverter.asCategoryIDMapKey(type.toString(), 20), new Integer(4444));
		GenericCategory category = ObjectConverter.asGenericCategory(categoryDigest, false, idMap, null, testReplacementImpl);

		assertEquals(100, category.getID());

		Iterator<MutableTimedAssociationKey> parentKeyIter = category.getParentKeyIterator();
		MutableTimedAssociationKey parentKey = parentKeyIter.next();
		assertEquals(4444, parentKey.getAssociableID());
		assertEquals(testReplacementImpl.getReplacement(), parentKey.getEffectiveDate());
		assertFalse(parentKey.hasExpirationDate());

		assertFalse(parentKeyIter.hasNext()); // only one parent
	}

	public void testAsGenericCategoryForCategoryDigestWithFalseMergeAndNonMatchingParentIDUsesOriginal() throws Exception {
		GenericEntityType type = GenericEntityType.forName("channel");
		Property property = new Property();
		property.setName("name");
		property.setValue("category name");
		CategoryDigest categoryDigest = new CategoryDigest();
		categoryDigest.addObject(property);
		categoryDigest.setId(100);
		categoryDigest.setParentID(20);
		categoryDigest.setType("channel");
		Map<String, Integer> idMap = new HashMap<String, Integer>();
		idMap.put(ObjectConverter.asCategoryIDMapKey(type.toString(), categoryDigest.getId()), new Integer(4444));
		GenericCategory category = ObjectConverter.asGenericCategory(categoryDigest, false, idMap, null, testReplacementImpl);

		assertEquals(100, category.getID());

		Iterator<MutableTimedAssociationKey> parentKeyIter = category.getParentKeyIterator();
		MutableTimedAssociationKey parentKey = parentKeyIter.next();
		assertEquals(20, parentKey.getAssociableID());
		assertEquals(testReplacementImpl.getReplacement(), parentKey.getEffectiveDate());
		assertFalse(parentKey.hasExpirationDate());

		assertFalse(parentKeyIter.hasNext()); // only one parent
	}

	public void testAsGenericCategoryForCategoryDigestWithMultipleParents() throws Exception {
		GenericEntityType type = GenericEntityType.forName("channel");
		Property property = new Property();
		property.setName("name");
		property.setValue("category name");
		CategoryDigest categoryDigest = new CategoryDigest();
		categoryDigest.addObject(property);
		categoryDigest.setId(100);
		categoryDigest.setType("channel");

		Parent parent1 = new Parent();
		parent1.setId(20);
		DateSynonym parent1Activation = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(parent1Activation);
		DateSynonym parent1Expiration = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(parent1Expiration);
		parent1.setActivationDates(ObjectMother.createActivationDates(parent1Activation, parent1Expiration));
		categoryDigest.addObject(parent1);

		Parent parent2 = new Parent();
		parent2.setId(21);
		DateSynonym parent2Activation = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(parent2Activation);
		DateSynonym parent2Expiration = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(parent2Expiration);
		parent2.setActivationDates(ObjectMother.createActivationDates(parent2Activation, parent2Expiration));
		categoryDigest.addObject(parent2);

		Map<String, Integer> idMap = new HashMap<String, Integer>();
		idMap.put(ObjectConverter.asCategoryIDMapKey(type.toString(), categoryDigest.getId()), new Integer(4444));
		GenericCategory category = ObjectConverter.asGenericCategory(categoryDigest, false, idMap, null, testReplacementImpl);

		assertEquals(100, category.getID());

		Iterator<MutableTimedAssociationKey> parentKeyIter = category.getParentKeyIterator();
		assertParentKey(parent1, 20, parent2, 21, parentKeyIter.next());
		assertParentKey(parent1, 20, parent2, 21, parentKeyIter.next());

		assertFalse(parentKeyIter.hasNext()); // exactly 2 parents
	}

	private void assertParentKey(Parent parent1, int parent1Id, Parent parent2, int parent2Id, MutableTimedAssociationKey parentKey) {
		if (parent1Id == parentKey.getAssociableID()) {
			assertEquals(parentKey.getEffectiveDate().getDate(), parent1.getActivationDates().effectiveDate());
			assertEquals(parentKey.getExpirationDate().getDate(), parent1.getActivationDates().expirationDate());
		}
		else if (parent2Id == parentKey.getAssociableID()) {
			assertEquals(parentKey.getEffectiveDate().getDate(), parent2.getActivationDates().effectiveDate());
			assertEquals(parentKey.getExpirationDate().getDate(), parent2.getActivationDates().expirationDate());
		}
		else {
			throw new IllegalStateException("Unexpected parent id: " + parentKey.getAssociableID());
		}
	}

	public void testAsGenericCategoryForCategoryDigestWithNullActivation() throws Exception {
		GenericEntityType type = GenericEntityType.forName("channel");
		Property property = new Property();
		property.setName("name");
		property.setValue("category name");
		CategoryDigest categoryDigest = new CategoryDigest();
		categoryDigest.addObject(property);
		categoryDigest.setId(100);
		categoryDigest.setType("channel");

		Parent parent = new Parent();
		parent.setId(20);
		parent.setActivationDates(null);
		categoryDigest.addObject(parent);

		Map<String, Integer> idMap = new HashMap<String, Integer>();
		idMap.put(ObjectConverter.asCategoryIDMapKey(type.toString(), categoryDigest.getId()), new Integer(4444));
		GenericCategory category = ObjectConverter.asGenericCategory(categoryDigest, false, idMap, null, testReplacementImpl);

		assertEquals(100, category.getID());

		Iterator<MutableTimedAssociationKey> parentKeyIter = category.getParentKeyIterator();
		MutableTimedAssociationKey parentKey = parentKeyIter.next();
		assertEquals(20, parentKey.getAssociableID());
		assertEquals(testReplacementImpl.getReplacement(), parentKey.getEffectiveDate());
		assertNull(parentKey.getExpirationDate());

		assertFalse(parentKeyIter.hasNext()); // exactly 1 parent
	}

	public void testAsGenericEntityWithMultipleCategoryAssociations() throws Exception {
		GenericEntityType type = GenericEntityType.forName("channel");
		Entity entityDigest = new Entity();
		entityDigest.setId(123);
		entityDigest.setType("channel");
		Property nameProperty = new Property();
		nameProperty.setName("name");
		nameProperty.setValue("entity name");
		entityDigest.addObject(nameProperty);

		Association assc1 = new Association();
		EntityIdentity catLink1 = new EntityIdentity();

		GenericCategory category1 = ObjectMother.createGenericCategory(type);
		EntityManager.getInstance().addGenericEntityCategory(type.getCategoryType(), category1.getId(), category1.getName());
		catLink1.setId(category1.getId());
		catLink1.setType("category");
		assc1.setEntityLink(catLink1);
		DateSynonym link1Activation = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(link1Activation);
		DateSynonym link1Expiration = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(link1Expiration);
		assc1.setActivationDates(ObjectMother.createActivationDates(link1Activation, link1Expiration));
		entityDigest.addObject(assc1);

		Association assc2 = new Association();
		EntityIdentity catLink2 = new EntityIdentity();

		GenericCategory category2 = ObjectMother.createGenericCategory(type);
		EntityManager.getInstance().addGenericEntityCategory(type.getCategoryType(), category2.getId(), category2.getName());
		catLink2.setId(category2.getId());
		catLink2.setType("category");
		assc2.setEntityLink(catLink2);
		DateSynonym link2Expiration = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(link2Expiration);
		assc2.setActivationDates(ObjectMother.createActivationDates(link1Expiration, link2Expiration));
		entityDigest.addObject(assc2);

		GenericEntity entity = ObjectConverter.asGenericEntity(type, entityDigest, false, new HashMap<String, Integer>(), null, testReplacementImpl);

		assertEquals(123, entity.getId());
		assertEquals("entity name", entity.getName());

		Iterator<MutableTimedAssociationKey> categoryKeyIter = entity.getCategoryIterator();
		assertCategoryKey(assc1, assc2, categoryKeyIter.next());
		assertCategoryKey(assc1, assc2, categoryKeyIter.next());

		assertFalse(categoryKeyIter.hasNext()); // exactly 2 categories
	}

	public void testAsGenericEntityCategoryAssociationsNullDates_Pre5_0_BackwardCompatibility() throws Exception {
		GenericEntityType type = GenericEntityType.forName("channel");
		Entity entityDigest = new Entity();
		entityDigest.setId(123);
		entityDigest.setType("channel");
		Property nameProperty = new Property();
		nameProperty.setName("name");
		nameProperty.setValue("entity name");
		entityDigest.addObject(nameProperty);

		Association assc = new Association();
		EntityIdentity catLink = new EntityIdentity();

		GenericCategory category = ObjectMother.createGenericCategory(type);
		EntityManager.getInstance().addGenericEntityCategory(type.getCategoryType(), category.getId(), category.getName());
		catLink.setId(category.getId());
		catLink.setType("category");
		assc.setEntityLink(catLink);
		entityDigest.addObject(assc);

		assertNull(assc.getActivationDates()); // sanity check

		GenericEntity entity = ObjectConverter.asGenericEntity(type, entityDigest, false, new HashMap<String, Integer>(), null, testReplacementImpl);

		assertEquals(123, entity.getId());
		assertEquals("entity name", entity.getName());

		Iterator<MutableTimedAssociationKey> categoryKeyIter = entity.getCategoryIterator();
		MutableTimedAssociationKey categoryKey = categoryKeyIter.next();
		assertEquals(testReplacementImpl.getReplacement(), categoryKey.getEffectiveDate());
		assertFalse(categoryKey.hasExpirationDate());

		assertFalse(categoryKeyIter.hasNext()); // exactly 1 category
	}

	private void assertCategoryKey(Association assoc1, Association assoc2, MutableTimedAssociationKey categoryKey) {
		if (assoc1.getEntityLink().getId() == categoryKey.getAssociableID()) {
			assertEquals(categoryKey.getEffectiveDate().getDate(), assoc1.getActivationDates().effectiveDate());
			assertEquals(categoryKey.getExpirationDate().getDate(), assoc1.getActivationDates().expirationDate());
		}
		else if (assoc2.getEntityLink().getId() == categoryKey.getAssociableID()) {
			assertEquals(categoryKey.getEffectiveDate().getDate(), assoc2.getActivationDates().effectiveDate());
			assertEquals(categoryKey.getExpirationDate().getDate(), assoc2.getActivationDates().expirationDate());
		}
		else {
			throw new IllegalStateException("Unexpected category id: " + categoryKey.getAssociableID());
		}
	}

	public void testFetchContextWithNullGridDigestThrowsNullPointerException() throws Exception {
		try {
			ObjectConverter.fetchContext(null, false, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}

	}

	public void testFetchContextWithMultipleGenericEntititesReturnsCorrectResult() throws Exception {
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		addEntityIdentity(gridDigest, "channel", 2);
		addEntityIdentity(gridDigest, "channel", 3);
		addEntityIdentity(gridDigest, "investor", 4);
		addEntityIdentity(gridDigest, "product", 5);

		GuidelineContext[] context = ObjectConverter.fetchContext(gridDigest, false, null);
		assertEquals(3, context.length);

		GuidelineContext c = new GuidelineContext(GenericEntityType.forName("channel"));
		c.setIDs(new int[] { 2, 3 });
		assertTrue(c.isContainedIn(context));
		c = new GuidelineContext(GenericEntityType.forName("investor"));
		c.setIDs(new int[] { 4 });
		assertTrue(c.isContainedIn(context));
		c = new GuidelineContext(GenericEntityType.forName("product"));
		c.setIDs(new int[] { 5 });
		assertTrue(c.isContainedIn(context));
	}

	/**
	 * This test is for backward compatibility to version 4.4.1 or older.
	 * This can be removed if "category" type is no longer supported.
	 * <p>
	 * See Test Tracker item #1497.
	 * @since 4.5.0
	 */
	public void testFetchContextWithCategoryTypesReturnsCorrectResult() throws Exception {
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		addEntityIdentity(gridDigest, "category", 30);
		addEntityIdentity(gridDigest, "category", 40);

		GuidelineContext[] context = ObjectConverter.fetchContext(gridDigest, false, null);
		assertEquals(1, context.length);

		GuidelineContext c = new GuidelineContext(GenericEntityType.forName("product"));
		assertTrue(!c.isContainedIn(context));
		c = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c.setIDs(new int[] { 30, 40 });
		assertTrue(c.isContainedIn(context));
	}

	public void testFetchContextWithMultipleGenericCategoriesReturnsCorrectResult() throws Exception {
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		addEntityIdentity(gridDigest, "generic-category:channel", 30);
		addEntityIdentity(gridDigest, "generic-category:investor", 40);
		addEntityIdentity(gridDigest, "generic-category:investor", 44);
		addEntityIdentity(gridDigest, "generic-category:product", 55);

		GuidelineContext[] context = ObjectConverter.fetchContext(gridDigest, false, null);
		assertEquals(3, context.length);

		GuidelineContext c = new GuidelineContext(GenericEntityType.forName("channel").getCategoryType());
		c.setIDs(new int[] { 30 });
		assertTrue(c.isContainedIn(context));
		c = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c.setIDs(new int[] { 44, 40 });
		assertTrue(c.isContainedIn(context));
		c = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c.setIDs(new int[] { 55 });
		assertTrue(c.isContainedIn(context));
	}

	public void testFetchContextWithGenericEntititesAndCategoriesReturnsCorrectResult() throws Exception {
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		addEntityIdentity(gridDigest, "channel", 3);
		addEntityIdentity(gridDigest, "generic-category:investor", 44);
		addEntityIdentity(gridDigest, "generic-category:product", 55);
		addEntityIdentity(gridDigest, "program", 900);
		addEntityIdentity(gridDigest, "program", 901);

		GuidelineContext[] context = ObjectConverter.fetchContext(gridDigest, false, null);
		assertEquals(4, context.length);

		GuidelineContext c = new GuidelineContext(GenericEntityType.forName("channel"));
		c.setIDs(new int[] { 3 });
		assertTrue(c.isContainedIn(context));
		c = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c.setIDs(new int[] { 44 });
		assertTrue(c.isContainedIn(context));
		c = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c.setIDs(new int[] { 55 });
		assertTrue(c.isContainedIn(context));
		c = new GuidelineContext(GenericEntityType.forName("program"));
		c.setIDs(new int[] { 900, 901 });
		assertTrue(c.isContainedIn(context));
	}

	public void testAsParameterGridListWithNullGridDigestThrowsNullPointerException() throws Exception {
		User user = ObjectMother.createUser();
		assertThrowsNullPointerException(
				ObjectConverter.class,
				"asParameterGridList",
				new Class[] { Grid.class, GuidelineContext[].class, User.class, Map.class, ReplacementDateSynonymProvider.class },
				new Object[] { null, null, user, null, testReplacementImpl });
	}

	public void testAsParameterGridListWithNoActivationDatesHappyCaseNoDateSynIdMap() throws Exception {
		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("parameter");
		gridDigest.setTemplateID(2000);

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setComment("comments " + ObjectMother.createString());
		gridActivation.setId(ObjectMother.createInt());
		gridActivation.setParentID(ObjectMother.createInt());
		gridActivation.setStatus(ObjectMother.createString());

		List<ParameterGrid> list = ObjectConverter.asParameterGridList(gridDigest, null, user, null, testReplacementImpl);
		assertEquals(1, list.size());
		ParameterGrid grid = (ParameterGrid) list.get(0);
		assertTrue(grid.isContextEmpty());
		assertEquals(gridActivation.getId(), grid.getID());
		assertEquals(gridActivation.getParentID(), grid.getCloneOf());
		assertEquals(testReplacementImpl.getReplacement(), grid.getEffectiveDate());
		assertNull(grid.getExpirationDate());
		assertEquals(gridActivation.getComment(), grid.getComments());
		assertEquals(gridActivation.getStatus(), grid.getStatus());
	}

	public void testAsParameterGridListWithNoActivationDatesHappyCaseWithDateSynIdMap() throws Exception {
		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("parameter");
		gridDigest.setTemplateID(2000);

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setComment("comments " + ObjectMother.createString());
		gridActivation.setId(ObjectMother.createInt());
		gridActivation.setParentID(ObjectMother.createInt());
		gridActivation.setStatus(ObjectMother.createString());

		Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();
		idMap.put(new Integer(1), new Integer(2));
		List<ParameterGrid> list = ObjectConverter.asParameterGridList(gridDigest, null, user, idMap, testReplacementImpl);
		assertEquals(1, list.size());
		ParameterGrid grid = (ParameterGrid) list.get(0);
		assertTrue(grid.isContextEmpty());
		assertEquals(gridActivation.getId(), grid.getID());
		assertEquals(gridActivation.getParentID(), grid.getCloneOf());
		assertEquals(testReplacementImpl.getReplacement(), grid.getEffectiveDate());
		assertNull(grid.getExpirationDate());
		assertEquals(gridActivation.getComment(), grid.getComments());
		assertEquals(gridActivation.getStatus(), grid.getStatus());
	}

	public void testAsParameterGridListWithValidGridDigestReturnsCorrectResult() throws Exception {
		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("parameter");
		gridDigest.setTemplateID(2000);

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setActivationDates(new ActivationDates());
		gridActivation.setComment("comments here");
		gridActivation.setCreatedOn("2001-07-01T00:00:00");
		gridActivation.setId(2000);
		gridActivation.setParentID(99);
		gridActivation.setStatus("someStatus");
		gridActivation.setStatusChangedOn("2002-02-28T20:30:00");

		List<ParameterGrid> list = ObjectConverter.asParameterGridList(gridDigest, null, user, null, testReplacementImpl);
		assertEquals(1, list.size());
		ParameterGrid grid = (ParameterGrid) list.get(0);
		assertTrue(grid.isContextEmpty());
		assertEquals(2000, grid.getID());
		assertEquals(99, grid.getCloneOf());
		assertEquals(testReplacementImpl.getReplacement(), grid.getEffectiveDate());
		assertNull(grid.getExpirationDate());
		assertEquals("comments here", grid.getComments());
		assertEquals("someStatus", grid.getStatus());
		assertEquals(getDate(2001, 7, 1), grid.getCreationDate());
		assertEquals(getDate(2002, 2, 28, 20, 30, 0), grid.getStatusChangeDate());
	}

	public void testAsGuidelineGridListWithNoMergeFixesRootCategoryIDs() throws Exception {
		GenericEntityType type = GenericEntityType.forName("product");
		Integer oldRootCatID = ObjectMother.createInteger();
		Integer newRootCatID = ObjectMother.createInteger();
		Map<String, Integer> entityIDMap = new HashMap<String, Integer>();
		entityIDMap.put(ObjectConverter.asCategoryIDMapKey(type.getName(), oldRootCatID.intValue()), newRootCatID);

		User user = ObjectMother.createUser();

		ObjectMother.attachGridTemplateColumns(GuidelineTemplateManager.getInstance().getTemplate(1000), 1);
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(1000);
		GuidelineTemplateManager.getInstance().getTemplate(1000).getColumn(1).setName("col1");
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		template.getColumn(1).getColumnDataSpecDigest().setType("EntityList");
		template.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);
		template.getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);

		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);
		gridDigest.addColumnName(template.getColumn(1).getTitle());

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setActivationDates(new ActivationDates());
		gridActivation.setComment("");
		gridActivation.setCreatedOn("2003-06-01T00:00:00");
		gridActivation.setId(2255);
		gridActivation.setParentID(-1);
		gridActivation.setStatus("Draft");
		gridActivation.setStatusChangedOn("2006-11-28T20:30:00");

		GridRow gridRow = new GridRow();
		gridRow.addCellValue("product:true:" + oldRootCatID.intValue());
		gridActivation.addRow(gridRow);
		gridRow = new GridRow();
		gridRow.addCellValue("product:false:" + oldRootCatID.intValue() + ",product:false:" + (oldRootCatID.intValue() * 2));
		gridActivation.addRow(gridRow);

		List<ProductGrid> list = ObjectConverter.asGuidelineGridList(gridDigest, null, false, user, null, entityIDMap, testReplacementImpl);
		assertEquals(1, list.size());
		ProductGrid grid = (ProductGrid) list.get(0);
		assertEquals(2, grid.getNumRows());

		CategoryOrEntityValues value = (CategoryOrEntityValues) grid.getCellValue(1, "col1");
		assertEquals(new int[] { oldRootCatID.intValue() }, value.getEntityIDs());
		value = (CategoryOrEntityValues) grid.getCellValue(2, "col1");
		assertEquals(new int[] { newRootCatID.intValue(), (oldRootCatID.intValue() * 2) }, value.getCategoryIDs());
	}

	public void testAsGuidelineGridListFixesWithMergeNotFoundInMapThrowsImportException() throws Exception {
		User user = ObjectMother.createUser();

		ObjectMother.attachGridTemplateColumns(GuidelineTemplateManager.getInstance().getTemplate(1000), 1);
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(1000);
		GuidelineTemplateManager.getInstance().getTemplate(1000).getColumn(1).setName("col1");
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		template.getColumn(1).getColumnDataSpecDigest().setType("EntityList");
		template.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);
		template.getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);

		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);
		gridDigest.addColumnName(template.getColumn(1).getTitle());

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setActivationDates(new ActivationDates());
		gridActivation.setComment("");
		gridActivation.setCreatedOn("2003-06-01T00:00:00");
		gridActivation.setId(2255);
		gridActivation.setParentID(-1);
		gridActivation.setStatus("Draft");

		GridRow gridRow = new GridRow();
		gridRow.addCellValue("product:true:" + ObjectMother.createInt());
		gridActivation.addRow(gridRow);

		try {
			ObjectConverter.asGuidelineGridList(gridDigest, null, true, user, null, new HashMap<String,Integer>(), testReplacementImpl);
			fail("Expected ImportException");
		}
		catch (ImportException ex) {
			// expected
		}
	}

	public void testAsGuidelineGridListWithNullGridDigestThrowsNullPointerException() throws Exception {
		User user = ObjectMother.createUser();
		try {
			ObjectConverter.asGuidelineGridList(null, null, false, user, null, null, testReplacementImpl);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	public void testAsGuidelineGridListWithNoActivationDatesHappyCaseNoDateSynIdMap() throws Exception {
		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setComment("comments " + ObjectMother.createString());
		gridActivation.setId(ObjectMother.createInt());
		gridActivation.setParentID(ObjectMother.createInt());
		gridActivation.setStatus(ObjectMother.createString());

		List<ProductGrid> list = ObjectConverter.asGuidelineGridList(gridDigest, null, false, user, null, null, testReplacementImpl);
		assertEquals(1, list.size());
		ProductGrid grid = (ProductGrid) list.get(0);
		assertTrue(grid.isContextEmpty());
		assertEquals(testReplacementImpl.getReplacement(), grid.getEffectiveDate());
		assertNull(grid.getExpirationDate());
		assertEquals(gridActivation.getComment(), grid.getComments());
		assertEquals(gridActivation.getStatus(), grid.getStatus());
	}

	public void testAsGuidelineGridListWithNoActivationDatesHappyCaseWithDateSynIdMap() throws Exception {
		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setComment("comments " + ObjectMother.createString());
		gridActivation.setId(ObjectMother.createInt());
		gridActivation.setParentID(ObjectMother.createInt());
		gridActivation.setStatus(ObjectMother.createString());

		Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();
		idMap.put(new Integer(1), new Integer(2));
		List<ProductGrid> list = ObjectConverter.asGuidelineGridList(gridDigest, null, false, user, idMap, null, testReplacementImpl);
		assertEquals(1, list.size());
		ProductGrid grid = (ProductGrid) list.get(0);
		assertTrue(grid.isContextEmpty());
		assertEquals(testReplacementImpl.getReplacement(), grid.getEffectiveDate());
		assertNull(grid.getExpirationDate());
		assertEquals(gridActivation.getComment(), grid.getComments());
		assertEquals(gridActivation.getStatus(), grid.getStatus());
	}

	public void testAsGuidelineGridListWithGridOfNoCellValuesAddsARow() throws Exception {
		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setActivationDates(new ActivationDates());
		gridActivation.setComment("comments here2");
		gridActivation.setCreatedOn("2003-06-01T00:00:00");
		gridActivation.setId(2255);
		gridActivation.setParentID(-1);
		gridActivation.setStatus("someStatus2");
		gridActivation.setStatusChangedOn("2006-11-28T20:30:00");

		List<ProductGrid> list = ObjectConverter.asGuidelineGridList(gridDigest, null, false, user, null, null, testReplacementImpl);
		assertEquals(1, list.size());
		ProductGrid grid = (ProductGrid) list.get(0);
		assertEquals(1, grid.getNumRows());
	}

	public void testAsGuidelineGridListWithValidGridDigestReturnsCorrectResult() throws Exception {
		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setActivationDates(new ActivationDates());
		gridActivation.setComment("comments here2");
		gridActivation.setCreatedOn("2003-06-01T00:00:00");
		gridActivation.setId(2255);
		gridActivation.setParentID(-1);
		gridActivation.setStatus("someStatus2");
		gridActivation.setStatusChangedOn("2006-11-28T20:30:00");

		GridManager.getInstance().addProductGrid(2255, 1000, "comments", null, "status", null, null, null, 0, -1, new Date());

		List<ProductGrid> list = ObjectConverter.asGuidelineGridList(gridDigest, null, false, user, null, null, testReplacementImpl);
		assertEquals(1, list.size());
		ProductGrid grid = (ProductGrid) list.get(0);
		assertTrue(grid.isContextEmpty());
		assertEquals(2255, grid.getID());
		assertEquals(-1, grid.getCloneOf());
		assertEquals(testReplacementImpl.getReplacement(), grid.getEffectiveDate());
		assertNull(grid.getExpirationDate());
		assertEquals("comments here2", grid.getComments());
		assertEquals("someStatus2", grid.getStatus());
		assertEquals(getDate(2003, 6, 1), grid.getCreationDate());
		assertEquals(getDate(2006, 11, 28, 20, 30, 0), grid.getStatusChangeDate());
	}

	public void testAsGuidelineGridListWithMergeFixesEntityListColumnValues() throws Exception {
		ObjectMother.attachGridTemplateColumns(GuidelineTemplateManager.getInstance().getTemplate(1000), 2);
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(1000);
		GuidelineTemplateManager.getInstance().getTemplate(1000).getColumn(1).setName("col1");
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		template.getColumn(1).getColumnDataSpecDigest().setType("EntityList");
		template.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);
		template.getColumn(2).setName("col2");
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(2));
		template.getColumn(2).getColumnDataSpecDigest().setType("EntityList");
		template.getColumn(2).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(2).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template.getColumn(2).getColumnDataSpecDigest().setIsEntityAllowed(true);
		template.getColumn(2).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);

		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);
		gridDigest.addColumnName(template.getColumn(1).getTitle());
		gridDigest.addColumnName(template.getColumn(2).getTitle());

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setActivationDates(new ActivationDates());
		gridActivation.setComment("comments here2");
		gridActivation.setCreatedOn("2003-06-01T00:00:00");
		gridActivation.setId(2255);
		gridActivation.setParentID(-1);
		gridActivation.setStatus("someStatus2");
		gridActivation.setStatusChangedOn("2006-11-28T20:30:00");

		GridRow gridRow = new GridRow();
		gridRow.addCellValue("product:true:1");
		gridRow.addCellValue("product:true:1,product:true:2");
		gridActivation.addRow(gridRow);
		gridRow = new GridRow();
		gridRow.addCellValue("product:false:1");
		gridRow.addCellValue("product:false:1,product:false:2");
		gridActivation.addRow(gridRow);


		Map<String, Integer> entityIDMap = new HashMap<String, Integer>();
		entityIDMap.put(ObjectConverter.asEntityIDMapKey("product", 1), new Integer(111));
		entityIDMap.put(ObjectConverter.asEntityIDMapKey("product", 2), new Integer(222));
		entityIDMap.put(ObjectConverter.asCategoryIDMapKey("product", 1), new Integer(333));
		entityIDMap.put(ObjectConverter.asCategoryIDMapKey("product", 2), new Integer(444));

		List<ProductGrid> list = ObjectConverter.asGuidelineGridList(gridDigest, null, true, user, null, entityIDMap, testReplacementImpl);
		assertEquals(1, list.size());
		ProductGrid grid = (ProductGrid) list.get(0);
		assertTrue(grid.isContextEmpty());
		assertEquals(getDate(2003, 6, 1), grid.getCreationDate());
		assertEquals(getDate(2006, 11, 28, 20, 30, 0), grid.getStatusChangeDate());
		assertEquals(111, ((CategoryOrEntityValue) grid.getCellValue(1, "col1")).getId());
		assertEquals(333, ((CategoryOrEntityValue) grid.getCellValue(2, "col1")).getId());
		CategoryOrEntityValues value = (CategoryOrEntityValues) grid.getCellValue(1, "col2");
		assertEquals(new int[] { 111, 222 }, value.getEntityIDs());
		value = (CategoryOrEntityValues) grid.getCellValue(2, "col2");
		assertEquals(new int[] { 333, 444 }, value.getCategoryIDs());
	}

	public void testAsGuidelineGridListWithMergeTrueSetsGridIDToNegativeOne() throws Exception {
		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setActivationDates(new ActivationDates());
		gridActivation.setComment("comments");
		gridActivation.setCreatedOn("2003-06-01T00:00:00");
		gridActivation.setId(11111);
		gridActivation.setParentID(-1);
		gridActivation.setStatus("status");
		gridActivation.setStatusChangedOn("2006-11-28T20:30:00");

		GridManager.getInstance().addProductGrid(11111, 1000, "comments", null, "status", null, null, null, 0, -1, new Date());

		List<ProductGrid> list = ObjectConverter.asGuidelineGridList(gridDigest, null, true, user, null, null, testReplacementImpl);
		assertEquals(1, list.size());
		ProductGrid grid = (ProductGrid) list.get(0);
		assertEquals(-1, grid.getID());
	}

	public void testAsGuidelineGridListIfGuidelineIDNotFoundSetsGridIDToNegativeOne() throws Exception {
		User user = ObjectMother.createUser();
		Grid gridDigest = new Grid();
		gridDigest.setType("guideline");
		gridDigest.setTemplateID(1000);

		GridActivation gridActivation = new GridActivation();
		gridDigest.addObject(gridActivation);
		gridActivation.setActivationDates(new ActivationDates());
		gridActivation.setComment("comments here2");
		gridActivation.setCreatedOn("2003-06-01T00:00:00");
		gridActivation.setId(2255);
		gridActivation.setParentID(-1);
		gridActivation.setStatus("someStatus2");
		gridActivation.setStatusChangedOn("2006-11-28T20:30:00");

		List<ProductGrid> list = ObjectConverter.asGuidelineGridList(gridDigest, null, false, user, null, null, testReplacementImpl);
		assertEquals(1, list.size());
		ProductGrid grid = (ProductGrid) list.get(0);
		assertEquals(-1, grid.getID());
	}

	public void testExtractCompabilityLinksWithNoActivationDatesHappyCaseNoDateSynIdMap() throws Exception {
		User user = ObjectMother.createUser();
		GenericEntityType entityType = GenericEntityType.getAllGenericEntityTypes()[0];
		Entity entity = ObjectMother.createEntity(entityType.getName());
		Association association = new Association();
		EntityIdentity identity = new EntityIdentity();
		identity.setId(ObjectMother.createInt());
		identity.setType(entityType.getName());
		association.setEntityLink(identity);
		entity.addObject(association);

		List<GenericEntityCompatibilityData> list = ObjectConverter.extractCompabilityLinks(entityType, entity, false, new HashMap<String, Integer>(), null, testReplacementImpl, user);
		assertEquals(1, list.size());
		GenericEntityCompatibilityData compatibilityData = list.get(0);
		assertEquals(testReplacementImpl.getReplacement(), compatibilityData.getEffectiveDate());
		assertNull(compatibilityData.getExpirationDate());
		assertEquals(identity.getId(), compatibilityData.getAssociableID());
	}

	public void testHasSameDataWithNullArgumentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				ObjectConverter.class,
				"hasSameData",
				new Class[] { List.class, List.class },
				new Object[] { null, new ArrayList<ProductGrid>() });
		assertThrowsNullPointerException(
				ObjectConverter.class,
				"hasSameData",
				new Class[] { List.class, List.class },
				new Object[] { new ArrayList<ProductGrid>(), null });
	}

	public void testHasSameDataWithEmptyListsReturnsTrue() throws Exception {
		assertTrue(invokeHasSameData(new ArrayList<Serializable>(), new ArrayList<Serializable>()));
	}

	public void testHasSameDataWithDifferentSizeReturnsFalse() throws Exception {
		List<Serializable> list = new ArrayList<Serializable>();
		list.add(new Grid());
		assertFalse(invokeHasSameData(new ArrayList<Serializable>(), list));
		assertFalse(invokeHasSameData(list, new ArrayList<Serializable>()));
	}

	public void testHasSameDataWithDifferentTypeGridsReturnsFalse() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		ObjectMother.attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setValue(1, 1, "value");
		List<Serializable> guidelineList = new ArrayList<Serializable>();
		guidelineList.add(grid);

		ParameterGrid paramGrid = ObjectMother.createParameterGrid();
		paramGrid.setCellValues("value");
		List<Serializable> paramList = new ArrayList<Serializable>();
		paramList.add(paramGrid);
		assertFalse(invokeHasSameData(guidelineList, paramList));
		assertFalse(invokeHasSameData(paramList, guidelineList));
	}

	public void testHasSameDataWithDifferentGuidelineGridsReturnsFalse() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		ObjectMother.attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setComments("comments");
		grid.setValue(1, 1, "value1");
		List<Serializable> list1 = new ArrayList<Serializable>();
		list1.add(grid);
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setComments("comments");
		grid.setValue(1, 1, "value2");
		List<Serializable> list2 = new ArrayList<Serializable>();
		list2.add(grid);
		assertFalse(invokeHasSameData(list1, list2));
		assertFalse(invokeHasSameData(list2, list1));
	}

	public void testHasSameDataWithDifferentParameterGridsReturnsFalse() throws Exception {
		ParameterGrid paramGrid = ObjectMother.createParameterGrid();
		paramGrid.setCellValues("value1");
		List<Serializable> list1 = new ArrayList<Serializable>();
		list1.add(paramGrid);

		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setCellValues("value2");
		List<Serializable> list2 = new ArrayList<Serializable>();
		list2.add(paramGrid);
		assertFalse(invokeHasSameData(list1, list2));
		assertFalse(invokeHasSameData(list2, list1));
	}

	public void testHasSameDataHappyCaseForGuidelineGrids() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		ObjectMother.attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setComments("comments1");
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100 });
		grid.setValue(1, 1, "value1");
		List<Serializable> list1 = new ArrayList<Serializable>();
		list1.add(grid);
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setComments("comments2");
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 200 });
		grid.setValue(1, 1, "value2");
		list1.add(grid);

		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setComments("comments2");
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		grid.setValue(1, 1, "value2");
		List<Serializable> list2 = new ArrayList<Serializable>();
		list2.add(grid);
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setComments("comments1");
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 22 });
		grid.setValue(1, 1, "value1");
		list2.add(grid);
		assertTrue(invokeHasSameData(list1, list2));
		assertTrue(invokeHasSameData(list2, list1));
	}

	public void testHasSameDataHappyCaseForParameterGrids() throws Exception {
		ParameterGrid paramGrid = ObjectMother.createParameterGrid();
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		List<Serializable> list1 = new ArrayList<Serializable>();
		list1.add(paramGrid);

		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 20 });
		paramGrid.setCellValues("value2");
		list1.add(paramGrid);

		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("investor"), new int[] { 11 });
		paramGrid.setCellValues("value2");
		List<Serializable> list2 = new ArrayList<Serializable>();
		list2.add(paramGrid);
		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("investor"), new int[] { 22 });
		paramGrid.setCellValues("value1");
		list2.add(paramGrid);
		assertTrue(invokeHasSameData(list1, list2));
		assertTrue(invokeHasSameData(list2, list1));
	}

	private boolean invokeHasSameData(List<Serializable> gridList1, List<Serializable> gridList2) throws Exception {
		Boolean result = (Boolean) ReflectionUtil.executeStaticPrivate(
				ObjectConverter.class,
				"hasSameData",
				new Class[] { List.class, List.class },
				new Object[] { gridList1, gridList2 });
		return (result == null ? false : result.booleanValue());
	}

	public void testAddAndMergeGridContextWithSameDataIfFoundWithNullArgumentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				ObjectConverter.class,
				"addAndMergeGridContextWithSameDataIfFound",
				new Class[] { List.class, List.class },
				new Object[] { null, new ArrayList<ParameterGrid>() });
		assertThrowsNullPointerException(
				ObjectConverter.class,
				"addAndMergeGridContextWithSameDataIfFound",
				new Class[] { List.class, List.class },
				new Object[] { new ArrayList<ParameterGrid>(), null });
	}

	public void testAddAndMergeGridContextWithEmptyGridListListAddToListForParameterGrid() throws Exception {
		List<List<ParameterGrid>> gridListList = new ArrayList<List<ParameterGrid>>();

		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		ParameterGrid paramGrid = ObjectMother.createParameterGrid();
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(1, gridListList.size());
		assertTrue(gridListList.get(0) == gridList);
	}

	public void testAddAndMergeGridContextWithNoSameDataAddToListForParameterGrid() throws Exception {
		List<List<ParameterGrid>> gridListList = new ArrayList<List<ParameterGrid>>();

		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		ParameterGrid paramGrid = ObjectMother.createParameterGrid();
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		gridListList.add(gridList);

		gridList = new ArrayList<ParameterGrid>();
		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value2");
		gridList.add(paramGrid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(2, gridListList.size());
	}

	public void testAddAndMergeGridContextWithSameDataButNotMergeableContextsAddToListForParameterGrid() throws Exception {
		List<List<ParameterGrid>> gridListList = new ArrayList<List<ParameterGrid>>();

		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		ParameterGrid paramGrid = ObjectMother.createParameterGrid();
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		gridList = new ArrayList<ParameterGrid>();
		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 11 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		assertEquals(2, gridListList.size());
	}

	public void testAddAndMergeGridContextWithSameDataUpdatesContextForParameterGrid() throws Exception {
		List<List<ParameterGrid>> gridListList = new ArrayList<List<ParameterGrid>>();

		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		ParameterGrid paramGrid = ObjectMother.createParameterGrid();
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		gridList = new ArrayList<ParameterGrid>();
		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 20 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		assertEquals(1, gridListList.size());
		assertEquals(1, gridListList.get(0).size());
		paramGrid = (ParameterGrid) gridListList.get(0).get(0);
		assertEquals(new int[] { 10, 20 }, paramGrid.getGenericEntityIDs(GenericEntityType.forName("channel")));
	}

	public void testAddAndMergeGridContextWithEmptyGridListListAddToListForGuidelineGrid() throws Exception {
		List<List<ProductGrid>> gridListList = new ArrayList<List<ProductGrid>>();

		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		ObjectMother.attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(1, gridListList.size());
		assertTrue(gridListList.get(0) == gridList);
	}

	public void testAddAndMergeGridContextWithNoSameDataAddToListForGuidelineGrid() throws Exception {
		List<List<ProductGrid>> gridListList = new ArrayList<List<ProductGrid>>();

		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		ObjectMother.attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);
		gridListList.add(gridList);

		gridList = new ArrayList<ProductGrid>();
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 200 });
		grid.setValue(1, 1, "value2");
		gridList.add(grid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(2, gridListList.size());
	}

	public void testAddAndMergeGridContextWithSameDataButNotMergeableContextsAddToListForGuidelineGrid() throws Exception {
		List<List<ProductGrid>> gridListList = new ArrayList<List<ProductGrid>>();

		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		ObjectMother.attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 20 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		gridList = new ArrayList<ProductGrid>();
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 200 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(2, gridListList.size());
	}

	public void testAddAndMergeGridContextWithSameDataUpdatesContextForGuidelineGrid() throws Exception {
		List<List<ProductGrid>> gridListList = new ArrayList<List<ProductGrid>>();

		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		ObjectMother.attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		gridList = new ArrayList<ProductGrid>();
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 200 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(1, gridListList.size());
		assertEquals(1, gridListList.get(0).size());
		grid = (ProductGrid) gridListList.get(0).get(0);
		assertEquals(new int[] { 100, 200 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
	}

	public void testExtractCompabilityLinksSupportsChannelInvestorAsGenericEntities() throws Exception {
		DigestedObjectHolder objectHolder = ImportXMLDigester.getInstance().digestImportXML(
				config.getDataFileContent("450-import-test-channel-investor.xml"));
		List<Entity> entities = objectHolder.getObjects(Entity.class);
		User user = ObjectMother.createUser();
		boolean testPerformed = false;
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();) {
			Entity entity = iter.next();
			if (entity.getType().equals("channel") && entity.getId() == 2) {
				List<GenericEntityCompatibilityData> compList = ObjectConverter.extractCompabilityLinks(
						GenericEntityType.forName(entity.getType()),
						entity,
						false,
						null,
						null,
						testReplacementImpl,
						user);
				assertEquals(1, compList.size());
				GenericEntityCompatibilityData compData = compList.get(0);
				DateSynonym effDate = new DateSynonym(
						compData.getEffectiveDate().getId(),
						compData.getEffectiveDate().getName(),
						compData.getEffectiveDate().getDescription(),
						getDate(2006, 1, 1));
				GenericEntityCompatibilityData expected = new GenericEntityCompatibilityData(
						GenericEntityType.forName("channel"),
						2,
						GenericEntityType.forName("product"),
						399,
						effDate,
						null);
				assertEquals(expected, compData);
				testPerformed = true;
			}
			else if (entity.getType().equals("investor") && entity.getId() == 20225) {
				List<GenericEntityCompatibilityData> compList = ObjectConverter.extractCompabilityLinks(
						GenericEntityType.forName(entity.getType()),
						entity,
						false,
						null,
						null,
						testReplacementImpl,
						user);
				assertEquals(1, compList.size());
				GenericEntityCompatibilityData compData = compList.get(0);
				DateSynonym effDate = new DateSynonym(
						compData.getEffectiveDate().getId(),
						compData.getEffectiveDate().getName(),
						compData.getEffectiveDate().getDescription(),
						getDate(2006, 1, 1));
				GenericEntityCompatibilityData expected = new GenericEntityCompatibilityData(
						GenericEntityType.forName("investor"),
						20225,
						GenericEntityType.forName("channel"),
						20035,
						effDate,
						null);
				assertEquals(expected, compData);
				testPerformed = true;
			}
			else if (entity.getType().equals("investor") && entity.getId() == 1) {
				assertEquals(0, ObjectConverter.extractCompabilityLinks(
						GenericEntityType.forName(entity.getType()),
						entity,
						false,
						null,
						null,
						testReplacementImpl,
						user).size());
				testPerformed = true;
			}
		}
		assertTrue("Object required to test " + getName() + " not found", testPerformed);
	}

	public void testAsGenericEntityWithOverlappingCategoryAssociations() throws Exception {
		GenericEntityType type = GenericEntityType.forName("channel");
		Entity entityDigest = new Entity();
		entityDigest.setId(123);
		entityDigest.setType("channel");
		Property nameProperty = new Property();
		nameProperty.setName("name");
		nameProperty.setValue("entity name");
		entityDigest.addObject(nameProperty);

		Association assc1 = new Association();
		EntityIdentity catLink1 = new EntityIdentity();

		GenericCategory category1 = ObjectMother.createGenericCategory(type);
		EntityManager.getInstance().addGenericEntityCategory(type.getCategoryType(), category1.getId(), category1.getName());
		catLink1.setId(category1.getId());
		catLink1.setType("category");
		assc1.setEntityLink(catLink1);
		DateSynonym link1Activation = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(link1Activation);
		DateSynonym link1Expiration = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(link1Expiration);
		assc1.setActivationDates(ObjectMother.createActivationDates(link1Activation, link1Expiration));
		entityDigest.addObject(assc1);

		Association assc2 = new Association();
		EntityIdentity catLink2 = new EntityIdentity();

		GenericCategory category2 = ObjectMother.createGenericCategory(type);
		EntityManager.getInstance().addGenericEntityCategory(type.getCategoryType(), category2.getId(), category2.getName());
		catLink2.setId(category2.getId());
		catLink2.setType("category");
		assc2.setEntityLink(catLink2);
		assc2.setActivationDates(ObjectMother.createActivationDates(link1Activation, link1Expiration));
		entityDigest.addObject(assc2);

		EntityTypeDefinition entityTypeDef = ConfigurationManager.getInstance().getEntityConfiguration().findEntityTypeDefinition(type);
		boolean originalValue = entityTypeDef.canBelongToMultipleCategories();
		entityTypeDef.setCanBelongToMultipleCategories(true);
		assertNotNull(ObjectConverter.asGenericEntity(type, entityDigest, false, new HashMap<String, Integer>(), null, testReplacementImpl));
		entityTypeDef.setCanBelongToMultipleCategories(false);
		try {
			ObjectConverter.asGenericEntity(type, entityDigest, false, new HashMap<String, Integer>(), null, testReplacementImpl);
			fail("Expected ImportException not thrown");
		}
		catch (ImportException e) {
			// expected
		}
		entityTypeDef.setCanBelongToMultipleCategories(originalValue);
	}

	public void testAsRoleHappyPath() throws Exception {
		Privilege priv = ObjectMother.createPrivilege();
		com.mindbox.pe.server.imexport.digest.Role digestRole = ObjectMother.createDigestRole();
		com.mindbox.pe.server.imexport.digest.Privilege digestPriv = ObjectMother.createDigestPrivilege();

		digestPriv.setName(priv.getName());
		digestRole.addPrivilegeID(String.valueOf(digestPriv.getId()));

		getMockSecurityCacheManager().findPrivilegeByName(digestPriv.getName());
		getMockSecurityCacheManagerControl().setReturnValue(priv);

		replay();

		Role role = ObjectConverter.asRole(digestRole, Arrays.asList(new com.mindbox.pe.server.imexport.digest.Privilege[] { digestPriv }));

		verify();

		assertEquals(digestRole.getId(), role.getId());
		assertEquals(digestRole.getName(), role.getName());
		assertEquals(1, role.getPrivileges().size());
		assertTrue(role.getPrivileges().contains(priv));
	}

	public void testAsRolePrivNotDigested() throws Exception {
		Privilege priv = ObjectMother.createPrivilege();
		com.mindbox.pe.server.imexport.digest.Role digestRole = ObjectMother.createDigestRole();
		com.mindbox.pe.server.imexport.digest.Privilege digestPriv = ObjectMother.createDigestPrivilege();

		digestPriv.setName(priv.getName());
		digestRole.addPrivilegeID(String.valueOf(digestPriv.getId()));

		replay();

		Role role = ObjectConverter.asRole(digestRole, new ArrayList<com.mindbox.pe.server.imexport.digest.Privilege>());

		verify();

		assertEquals(digestRole.getId(), role.getId());
		assertEquals(digestRole.getName(), role.getName());
		assertTrue(role.getPrivileges().isEmpty());
	}

	public void testAsRolePrivNotCached() throws Exception {
		com.mindbox.pe.server.imexport.digest.Role digestRole = ObjectMother.createDigestRole();
		com.mindbox.pe.server.imexport.digest.Privilege digestPriv = ObjectMother.createDigestPrivilege();

		digestRole.addPrivilegeID(String.valueOf(digestPriv.getId()));

		getMockSecurityCacheManager().findPrivilegeByName(digestPriv.getName());
		getMockSecurityCacheManagerControl().setReturnValue(null);

		replay();

		Role role = ObjectConverter.asRole(digestRole, Arrays.asList(new com.mindbox.pe.server.imexport.digest.Privilege[] { digestPriv }));

		verify();

		assertEquals(digestRole.getId(), role.getId());
		assertEquals(digestRole.getName(), role.getName());
		assertTrue(role.getPrivileges().isEmpty());
	}

	public void testValidateUserObjectThrowsImportExceptionIfUserIDNotProvided() throws Exception {
		User user = ObjectMother.createUser();
		user.setUserID("");
		assertThrowsException(
				ObjectConverter.class,
				"validateUserObject",
				new Class[] { User.class },
				new Object[] { user },
				ImportException.class);
	}

	public void testValidateUserObjectThrowsImportExceptionIfUserPasswordNotProvider() throws Exception {
		User user = ObjectMother.createUser();
		user.setPasswordHistory(new ArrayList<UserPassword>());
		assertThrowsException(
				ObjectConverter.class,
				"validateUserObject",
				new Class[] { User.class },
				new Object[] { user },
				ImportException.class);
	}

	public void testValidateUserObjectThrowsImportExceptionIfUserStatusNotProvider() throws Exception {
		User user = ObjectMother.createUser();
		user.setStatus("");
		assertThrowsException(
				ObjectConverter.class,
				"validateUserObject",
				new Class[] { User.class },
				new Object[] { user },
				ImportException.class);
	}

	public void testUnknownPrivsForRole_UnknownPriv() throws Exception {
		com.mindbox.pe.server.imexport.digest.Role digestRole = ObjectMother.createDigestRole();
		com.mindbox.pe.server.imexport.digest.Privilege digestPriv = ObjectMother.createDigestPrivilege();

		digestRole.addPrivilegeID(String.valueOf(digestPriv.getId()));

		getMockSecurityCacheManager().findPrivilegeByName(digestPriv.getName());
		getMockSecurityCacheManagerControl().setReturnValue(null); // unknown priv

		replay();

		List<String> result = ObjectConverter.unknownPrivsForRole(digestRole, Arrays.asList(new com.mindbox.pe.server.imexport.digest.Privilege[] { digestPriv }));

		verify();

		assertEquals(1, result.size());
		assertTrue(result.contains(digestPriv.getName()));
	}

	public void testUnknownPrivsForRole_KnownPriv() throws Exception {
		Privilege priv = ObjectMother.createPrivilege();
		com.mindbox.pe.server.imexport.digest.Role digestRole = ObjectMother.createDigestRole();
		com.mindbox.pe.server.imexport.digest.Privilege digestPriv = ObjectMother.createDigestPrivilege();

		digestRole.addPrivilegeID(String.valueOf(digestPriv.getId()));

		getMockSecurityCacheManager().findPrivilegeByName(digestPriv.getName());
		getMockSecurityCacheManagerControl().setReturnValue(priv);

		replay();

		List<String> result = ObjectConverter.unknownPrivsForRole(digestRole, Arrays.asList(new com.mindbox.pe.server.imexport.digest.Privilege[] { digestPriv }));

		verify();

		assertTrue(result.isEmpty());
	}

	public void testContextsAreMergeableWithCategoryAndEntityOfSameTypeReturnsFalse() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0].getCategoryType());
		c1.setIDs(new int[] { 1 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0]);
		c2.setIDs(new int[] { 2 });
		testContextsAreMergeable(new GuidelineContext[] { c1 }, new GuidelineContext[] { c2 }, false);
	}

	public void testContextsAreMergeableWithDifferentCategoriesOfSameTypeReturnsTrue() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0].getCategoryType());
		c1.setIDs(new int[] { 1 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0].getCategoryType());
		c2.setIDs(new int[] { 2 });
		testContextsAreMergeable(new GuidelineContext[] { c1 }, new GuidelineContext[] { c2 }, true);
	}

	public void testContextsAreMergeableWithCategoryAndEntityOfDifferentTypeReturnsTrue() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0].getCategoryType());
		c1.setIDs(new int[] { 1 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[1]);
		c2.setIDs(new int[] { 2 });
		testContextsAreMergeable(new GuidelineContext[] { c1 }, new GuidelineContext[] { c2 }, true);
	}

	private void testContextsAreMergeable(GuidelineContext[] mergeToContext, GuidelineContext[] mergeFromContext, boolean expectedResult)
			throws Exception {
		Boolean result = (Boolean) ReflectionUtil.executeStaticPrivate(
				ObjectConverter.class,
				"contextsAreMergeable",
				new Class[] { GuidelineContext[].class, GuidelineContext[].class },
				new Object[] { mergeToContext, mergeFromContext });
		assertNotNull(result);
		assertTrue(expectedResult == result.booleanValue());
	}
}
