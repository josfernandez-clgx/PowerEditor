package com.mindbox.pe.server.validate;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.server.ServerTestObjectMother.createGenericCategory;
import static com.mindbox.pe.server.ServerTestObjectMother.createGenericEntity;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.servlet.ResourceUtil;

public class DefaultDataValidatorTest extends AbstractTestWithTestConfig {

	private DefaultDataValidator validator;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		this.validator = new DefaultDataValidator();
		ReflectionUtil.executeStaticPrivate(ResourceUtil.class, "initialize", new Class<?>[] { String.class, String[].class }, new Object[] {
				"src/main/resources",
				new String[] { "ValidationMessages" } });
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		EntityManager.getInstance().startLoading();
	}

	@Test
	public void testValidateForGenericCategoryChecksIDsForExistenceNegativeCase() throws Exception {
		GenericCategory genericCategory = createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(genericCategory.getType(), genericCategory.getID(), genericCategory.getName());
		int nextCategoryID = createInt();
		genericCategory.addParentKey(new DefaultMutableTimedAssociationKey(nextCategoryID, createDateSynonym(), null));

		int childID = createInt();
		genericCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(childID, createDateSynonym(), null));

		List<ValidationViolation> violiations = validator.validate(genericCategory);
		assertEquals(2, violiations.size());
	}

	@Test
	public void testValidateForGenericCategoryChecksIDsForExistencePositiveCase() throws Exception {
		GenericCategory genericCategory = createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(genericCategory.getType(), genericCategory.getID(), genericCategory.getName());
		int nextCategoryID = createInt();
		EntityManager.getInstance().addGenericEntityCategory(genericCategory.getType(), nextCategoryID, "Category-" + nextCategoryID);
		genericCategory.addParentKey(new DefaultMutableTimedAssociationKey(nextCategoryID, createDateSynonym(), null));

		int childID = createInt();
		EntityManager.getInstance().addGenericEntityCategory(genericCategory.getType(), childID, "Category-" + childID);
		genericCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(childID, createDateSynonym(), null));

		List<ValidationViolation> violiations = validator.validate(genericCategory);
		assertTrue(violiations.isEmpty());
	}

	@Test
	public void testValidateForGenericEntityChecksIDsForExistenceNegativeCase() throws Exception {
		int parentID = createInt();
		GenericEntity genericEntity = createGenericEntity(GenericEntityType.forName("channel"));
		genericEntity.setParentID(parentID);
		EntityManager.getInstance().addGenericEntity(genericEntity.getID(), genericEntity.getType().getID(), genericEntity.getName(), parentID, null);

		int categoryID = createInt();
		genericEntity.addCategoryAssociation(new DefaultMutableTimedAssociationKey(categoryID, createDateSynonym(), null));

		List<ValidationViolation> violiations = validator.validate(genericEntity);
		assertEquals(2, violiations.size());
	}

	@Test
	public void testValidateForGenericEntityChecksIDsForExistencePositiveCase() throws Exception {
		int parentID = createInt();
		GenericEntity genericEntity = createGenericEntity(GenericEntityType.forName("channel"));
		genericEntity.setParentID(parentID);
		EntityManager.getInstance().addGenericEntity(genericEntity.getID(), genericEntity.getType().getID(), genericEntity.getName(), parentID, null);
		EntityManager.getInstance().addGenericEntity(parentID, genericEntity.getType().getID(), genericEntity.getName() + parentID, -1, null);

		int categoryID = createInt();
		EntityManager.getInstance().addGenericEntityCategory(genericEntity.getType().getCategoryType(), categoryID, "Category-" + categoryID);
		genericEntity.addCategoryAssociation(new DefaultMutableTimedAssociationKey(categoryID, createDateSynonym(), null));

		List<ValidationViolation> violiations = validator.validate(genericEntity);
		assertTrue(violiations.isEmpty());

		genericEntity.setParentID(-1);
		violiations = validator.validate(genericEntity);
		assertTrue(violiations.isEmpty());
	}

	@Test
	public void testValidateForGenericEntityChecksUniqueNameNegativeCase() throws Exception {
		GenericEntity genericEntity = createGenericEntity(GenericEntityType.forName("channel"));
		EntityManager.getInstance().addGenericEntity(genericEntity.getID(), genericEntity.getType().getID(), genericEntity.getName(), -1, null);

		GenericEntity genericEntity2 = createGenericEntity(GenericEntityType.forName("channel"));
		genericEntity2.setName(genericEntity.getName());

		List<ValidationViolation> violiations = validator.validate(genericEntity2);
		assertEquals(1, violiations.size());
	}

	@Test
	public void testValidateForGenericEntityChecksUniqueNamePositiveCase() throws Exception {
		GenericEntity genericEntity = createGenericEntity(GenericEntityType.forName("channel"));
		List<ValidationViolation> violiations = validator.validate(genericEntity);
		assertTrue(violiations.isEmpty());

		EntityManager.getInstance().addGenericEntity(genericEntity.getID(), genericEntity.getType().getID(), genericEntity.getName(), -1, null);
		violiations = validator.validate(genericEntity);
		assertTrue(violiations.isEmpty());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataChecksIDsForExistenceNegativeCase() throws Exception {
		GenericEntityType entityType = GenericEntityType.forName("channel");
		int sourceID = createInt();
		int targetID = createInt();

		GenericEntityCompatibilityData compatibilityData = new GenericEntityCompatibilityData(entityType, sourceID, entityType, targetID, createDateSynonym(), null);

		List<ValidationViolation> violiations = validator.validate(compatibilityData);
		assertEquals(2, violiations.size());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataChecksIDsForExistencePositiveCase() throws Exception {
		GenericEntityType entityType = GenericEntityType.forName("channel");
		int sourceID = createInt();
		int targetID = createInt();
		EntityManager.getInstance().addGenericEntity(sourceID, entityType.getID(), "entity" + sourceID, -1, null);
		EntityManager.getInstance().addGenericEntity(targetID, entityType.getID(), "entity" + targetID, -1, null);

		GenericEntityCompatibilityData compatibilityData = new GenericEntityCompatibilityData(entityType, sourceID, entityType, targetID, createDateSynonym(), null);

		List<ValidationViolation> violiations = validator.validate(compatibilityData);
		assertTrue(violiations.isEmpty());
	}
}
