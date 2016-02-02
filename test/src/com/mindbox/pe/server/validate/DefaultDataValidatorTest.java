package com.mindbox.pe.server.validate;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.servlet.ResourceUtil;

public class DefaultDataValidatorTest extends AbstractTestWithTestConfig {

	public static Test suite() {

		TestSuite suite = new TestSuite("DefaultDataValidatorTest Tests");
		suite.addTestSuite(DefaultDataValidatorTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private DefaultDataValidator validator;

	public DefaultDataValidatorTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		this.validator = new DefaultDataValidator();
		ReflectionUtil.executeStaticPrivate(
				ResourceUtil.class,
				"initialize",
				new Class<?>[] { String.class, String[].class },
				new Object[] { "src/resource", new String[] { "ValidationMessages" } });
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EntityManager.getInstance().startLoading();
	}

	public void testValidateForGenericCategoryChecksIDsForExistencePositiveCase() throws Exception {
		GenericCategory genericCategory = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(genericCategory.getType(), genericCategory.getID(), genericCategory.getName());
		int nextCategoryID = ObjectMother.createInt();
		EntityManager.getInstance().addGenericEntityCategory(genericCategory.getType(), nextCategoryID, "Category-" + nextCategoryID);
		genericCategory.addParentKey(new DefaultMutableTimedAssociationKey(nextCategoryID, ObjectMother.createDateSynonym(), null));

		int childID = ObjectMother.createInt();
		EntityManager.getInstance().addGenericEntityCategory(genericCategory.getType(), childID, "Category-" + childID);
		genericCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(childID, ObjectMother.createDateSynonym(), null));

		List<ValidationViolation> violiations = validator.validate(genericCategory);
		assertTrue(violiations.isEmpty());
	}

	public void testValidateForGenericCategoryChecksIDsForExistenceNegativeCase() throws Exception {
		GenericCategory genericCategory = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(genericCategory.getType(), genericCategory.getID(), genericCategory.getName());
		int nextCategoryID = ObjectMother.createInt();
		genericCategory.addParentKey(new DefaultMutableTimedAssociationKey(nextCategoryID, ObjectMother.createDateSynonym(), null));

		int childID = ObjectMother.createInt();
		genericCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(childID, ObjectMother.createDateSynonym(), null));

		List<ValidationViolation> violiations = validator.validate(genericCategory);
		assertEquals(2, violiations.size());
	}

	public void testValidateForGenericEntityChecksUniqueNamePositiveCase() throws Exception {
		GenericEntity genericEntity = ObjectMother.createGenericEntity(GenericEntityType.forName("channel"));
		List<ValidationViolation> violiations = validator.validate(genericEntity);
		assertTrue(violiations.isEmpty());

		EntityManager.getInstance().addGenericEntity(
				genericEntity.getID(),
				genericEntity.getType().getID(),
				genericEntity.getName(),
				-1,
				null);
		violiations = validator.validate(genericEntity);
		assertTrue(violiations.isEmpty());
	}

	public void testValidateForGenericEntityChecksUniqueNameNegativeCase() throws Exception {
		GenericEntity genericEntity = ObjectMother.createGenericEntity(GenericEntityType.forName("channel"));
		EntityManager.getInstance().addGenericEntity(
				genericEntity.getID(),
				genericEntity.getType().getID(),
				genericEntity.getName(),
				-1,
				null);

		GenericEntity genericEntity2 = ObjectMother.createGenericEntity(GenericEntityType.forName("channel"));
		genericEntity2.setName(genericEntity.getName());

		List<ValidationViolation> violiations = validator.validate(genericEntity2);
		assertEquals(1, violiations.size());
	}

	public void testValidateForGenericEntityChecksIDsForExistencePositiveCase() throws Exception {
		int parentID = ObjectMother.createInt();
		GenericEntity genericEntity = ObjectMother.createGenericEntity(GenericEntityType.forName("channel"));
		genericEntity.setParentID(parentID);
		EntityManager.getInstance().addGenericEntity(
				genericEntity.getID(),
				genericEntity.getType().getID(),
				genericEntity.getName(),
				parentID,
				null);
		EntityManager.getInstance().addGenericEntity(
				parentID,
				genericEntity.getType().getID(),
				genericEntity.getName() + parentID,
				-1,
				null);

		int categoryID = ObjectMother.createInt();
		EntityManager.getInstance().addGenericEntityCategory(
				genericEntity.getType().getCategoryType(),
				categoryID,
				"Category-" + categoryID);
		genericEntity.addCategoryAssociation(new DefaultMutableTimedAssociationKey(categoryID, ObjectMother.createDateSynonym(), null));

		List<ValidationViolation> violiations = validator.validate(genericEntity);
		assertTrue(violiations.isEmpty());

		genericEntity.setParentID(-1);
		violiations = validator.validate(genericEntity);
		assertTrue(violiations.isEmpty());
	}

	public void testValidateForGenericEntityChecksIDsForExistenceNegativeCase() throws Exception {
		int parentID = ObjectMother.createInt();
		GenericEntity genericEntity = ObjectMother.createGenericEntity(GenericEntityType.forName("channel"));
		genericEntity.setParentID(parentID);
		EntityManager.getInstance().addGenericEntity(
				genericEntity.getID(),
				genericEntity.getType().getID(),
				genericEntity.getName(),
				parentID,
				null);

		int categoryID = ObjectMother.createInt();
		genericEntity.addCategoryAssociation(new DefaultMutableTimedAssociationKey(categoryID, ObjectMother.createDateSynonym(), null));

		List<ValidationViolation> violiations = validator.validate(genericEntity);
		assertEquals(2, violiations.size());
	}

	public void testValidateForGenericEntityCompatibilityDataChecksIDsForExistencePositiveCase() throws Exception {
		GenericEntityType entityType = GenericEntityType.forName("channel");
		int sourceID = ObjectMother.createInt();
		int targetID = ObjectMother.createInt();
		EntityManager.getInstance().addGenericEntity(sourceID, entityType.getID(), "entity" + sourceID, -1, null);
		EntityManager.getInstance().addGenericEntity(targetID, entityType.getID(), "entity" + targetID, -1, null);

		GenericEntityCompatibilityData compatibilityData = new GenericEntityCompatibilityData(
				entityType,
				sourceID,
				entityType,
				targetID,
				ObjectMother.createDateSynonym(),
				null);

		List<ValidationViolation> violiations = validator.validate(compatibilityData);
		assertTrue(violiations.isEmpty());
	}

	public void testValidateForGenericEntityCompatibilityDataChecksIDsForExistenceNegativeCase() throws Exception {
		GenericEntityType entityType = GenericEntityType.forName("channel");
		int sourceID = ObjectMother.createInt();
		int targetID = ObjectMother.createInt();

		GenericEntityCompatibilityData compatibilityData = new GenericEntityCompatibilityData(
				entityType,
				sourceID,
				entityType,
				targetID,
				ObjectMother.createDateSynonym(),
				null);

		List<ValidationViolation> violiations = validator.validate(compatibilityData);
		assertEquals(2, violiations.size());
	}
}
