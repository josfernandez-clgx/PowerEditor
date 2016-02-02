package com.mindbox.pe.server.validate.oval;

import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.DefaultTimedAssociationKey;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.imexport.digest.NextIDSeed;
import com.mindbox.pe.server.servlet.ResourceUtil;

public class OValDataValidatorTest extends AbstractTestWithGenericEntityType {

	public static Test suite() {
		TestSuite suite = new TestSuite("OValDataValidatorTest Tests");
		suite.addTestSuite(OValDataValidatorTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private OValDataValidator validator;

	public OValDataValidatorTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.validator = OValDataValidator.getInstance();
		ReflectionUtil.executeStaticPrivate(ResourceUtil.class, "initialize", new Class<?>[] {
				String.class,
				String[].class }, new Object[] { "src/resource", new String[] { "ValidationMessages" } });
	}

	public void testValidateForUnMarkedDataHappyCase() throws Exception {
		UserData userData = new UserData("", "", null, null, false, 0, null);
		List<ValidationViolation> violations = validator.validate(userData);
		assertTrue(violations.isEmpty());
	}

	public void testValidateForGenericCategoryPositiveCase() throws Exception {
		GenericCategory genericCategory = ObjectMother.createGenericCategory(entityType);
		genericCategory.setRootIndicator(true);
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertTrue(violations.isEmpty());
	}

	public void testValidateForGenericCategoryNegativeCase_ID() throws Exception {
		GenericCategory genericCategory = ObjectMother.createGenericCategory(entityType);
		genericCategory.setID(-2);
		genericCategory.setRootIndicator(true);
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());

		genericCategory.setID(0);
		violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericCategoryNegativeCase_Name() throws Exception {
		GenericCategory genericCategory = ObjectMother.createGenericCategory(entityType);
		genericCategory.setRootIndicator(true);
		genericCategory.setName(" ");
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericCategoryNegativeCase_Type() throws Exception {
		GenericCategory genericCategory = new GenericCategory(
				Persistent.UNASSIGNED_ID,
				ObjectMother.createString(),
				entityType.getCategoryType() + 1);
		genericCategory.setRootIndicator(true);
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericCategoryNegativeCase_ChildAssociation() throws Exception {
		GenericCategory genericCategory = ObjectMother.createGenericCategory(entityType);
		genericCategory.setRootIndicator(true);
		genericCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(
				-2,
				ObjectMother.createDateSynonym(),
				null));
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());

		genericCategory = ObjectMother.createGenericCategory(entityType);
		genericCategory.setRootIndicator(true);
		genericCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(
				genericCategory.getID(),
				ObjectMother.createDateSynonym(),
				null));
		violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericCategoryNegativeCase_ParentAssocation() throws Exception {
		GenericCategory genericCategory = ObjectMother.createGenericCategory(entityType);
		genericCategory.addParentKey(new DefaultMutableTimedAssociationKey(-2, ObjectMother.createDateSynonym(), null));
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());

		genericCategory = ObjectMother.createGenericCategory(entityType);
		genericCategory.addParentKey(new DefaultMutableTimedAssociationKey(
				genericCategory.getID(),
				ObjectMother.createDateSynonym(),
				null));
		violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityPositiveCase() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(entityType);
		entity.setProperty("string-property", ObjectMother.createString());
		List<ValidationViolation> violations = validator.validate(entity);
		assertTrue(violations.isEmpty());
	}

	public void testValidateForGenericEntityNegativeCase_ID() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(entityType);
		entity.setProperty("string-property", ObjectMother.createString());
		entity.setID(0);
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());

		entity.setID(-2);
		violations = validator.validate(entity);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityNegativeCase_Type() throws Exception {
		GenericEntity entity = new GenericEntity(1, null, "name");
		entity.setProperty("string-property", ObjectMother.createString());
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(2, violations.size()); // one for null type and one for property check (if type == null, it fails)
	}

	public void testValidateForGenericEntityNegativeCase_Name() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType, "  ");
		entity.setProperty("string-property", ObjectMother.createString());
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityNegativeCase_ParentID() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(entityType);
		entity.setProperty("string-property", ObjectMother.createString());
		entity.setParentID(-2);
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());

		entity.setParentID(0);
		violations = validator.validate(entity);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityNegativeCase_Properties() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(entityType);
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());
		String msg = violations.get(0).getMessage();
		assertEquals("Must provide values for the following properties: "
				+ GenericEntityType.getEntityTypeDefinition(entityType).findPropertyDisplayName("string-property"), msg);
	}

	public void testValidateForGenericEntityNegativeCase_CategoryAssociations() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(entityType);
		entity.setProperty("string-property", ObjectMother.createString());
		entity.addCategoryAssociation(new DefaultMutableTimedAssociationKey(-2));
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityCompatibilityDataPositiveCase() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(
				entityType,
				1,
				entityType,
				2,
				ObjectMother.createDateSynonym(),
				null);
		List<ValidationViolation> violations = validator.validate(data);
		assertTrue(violations.isEmpty());
	}

	public void testValidateForGenericEntityCompatibilityDataNegativeCase_SourceType() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(null, 1, entityType, 2, ObjectMother.createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityCompatibilityDataNegativeCase_SourceID() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(
				entityType,
				0,
				entityType,
				2,
				ObjectMother.createDateSynonym(),
				null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());

		data = new GenericEntityCompatibilityData(entityType, -2, entityType, 2, ObjectMother.createDateSynonym(), null);
		violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityCompatibilityDataNegativeCase_TargetType() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 1, null, 2, ObjectMother.createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityCompatibilityDataNegativeCase_TargetID() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(
				entityType,
				1,
				entityType,
				0,
				ObjectMother.createDateSynonym(),
				null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());

		data = new GenericEntityCompatibilityData(entityType, 2, entityType, -2, ObjectMother.createDateSynonym(), null);
		violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityCompatibilityDataNegativeCase_TargetIDSameAsSourceID() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(
				entityType,
				5,
				entityType,
				5,
				ObjectMother.createDateSynonym(),
				null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityCompatibilityDataNegativeCase_EffDate() throws Exception {
		DateSynonym dateSynonym = ObjectMother.createDateSynonym();
		dateSynonym.setName(" ");
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(
				entityType,
				1,
				entityType,
				2,
				dateSynonym,
				null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityCompatibilityDataNegativeCase_ExpDate() throws Exception {
		DateSynonym dateSynonym1 = ObjectMother.createDateSynonym();
		DateSynonym dateSynonym2 = ObjectMother.createDateSynonym();
		dateSynonym2.setName(" ");
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(
				entityType,
				1,
				entityType,
				2,
				dateSynonym1,
				dateSynonym2);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	public void testValidateForGenericEntityCompatibilityDataNegativeCase_ExpDateBeforeEffDate() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 1, entityType, 2, ds2, ds1);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	public void testValidateForAssocaitionKeyPositiveCase() throws Exception {
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(1, ObjectMother.createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertTrue(violations.isEmpty());
	}

	public void testValidateForAssocaitionKeyNegativeCase_ID() throws Exception {
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(0, ObjectMother.createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
		timedAssocaitionKey = new DefaultTimedAssociationKey(0, ObjectMother.createDateSynonym(), null);
		violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
	}

	public void testValidateForTimedAssocaitionKeyPositiveCase() throws Exception {
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(
				1,
				ObjectMother.createDateSynonym(),
				null);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertTrue(violations.isEmpty());
	}

	public void testValidateForTimedAssocaitionKeyNegativeCase_ActDate() throws Exception {
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(
				1,
				ObjectMother.createDateSynonym(),
				null);
		timedAssocaitionKey.getEffectiveDate().setID(-2);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
	}

	public void testValidateForTimedAssocaitionKeyNegativeCase_ExpDate() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(
				1,
				ds1,
				ds2);
		timedAssocaitionKey.getExpirationDate().setID(-2);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
	}

	public void testValidateForTimedAssocaitionKeyNegativeCase_ExpDateBeforeEffDate() throws Exception {
		DateSynonym ds1 = ObjectMother.createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(1, ds2, ds1);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
	}

	public void testValidateForDateSynonymPositiveCase() throws Exception {
		DateSynonym dateSynonym = ObjectMother.createDateSynonym();
		List<ValidationViolation> violations = validator.validate(dateSynonym);
		assertTrue(violations.isEmpty());
	}

	public void testValidateForDateSynonymNegativeCase_Name() throws Exception {
		DateSynonym dateSynonym = ObjectMother.createDateSynonym();
		dateSynonym.setName(" ");
		dateSynonym.setDate(new Date());
		List<ValidationViolation> violations = validator.validate(dateSynonym);
		assertEquals(1, violations.size());
	}

	public void testValidateForDateSynonymNegativeCase_ID() throws Exception {
		DateSynonym dateSynonym = ObjectMother.createDateSynonym();
		dateSynonym.setID(-2);
		List<ValidationViolation> violations = validator.validate(dateSynonym);
		assertEquals(1, violations.size());

		dateSynonym.setID(0);
		violations = validator.validate(dateSynonym);
		assertEquals(1, violations.size());
	}

	public void testValidateForNextIDSeedPositiveCase() throws Exception {
		NextIDSeed nextIDSeed = new NextIDSeed(DBIdGenerator.AUDIT_ID, 1, 1);
		List<ValidationViolation> violations = validator.validate(nextIDSeed);
		assertTrue(violations.isEmpty());

		nextIDSeed = new NextIDSeed(DBIdGenerator.FILTER_ID, Integer.MAX_VALUE, 1);
		violations = validator.validate(nextIDSeed);
		assertTrue(violations.isEmpty());

		nextIDSeed = new NextIDSeed(DBIdGenerator.GRID_ID, 2, 1);
		violations = validator.validate(nextIDSeed);
		assertTrue(violations.isEmpty());

		nextIDSeed = new NextIDSeed(DBIdGenerator.RULE_ID, 100000, 1);
		violations = validator.validate(nextIDSeed);
		assertTrue(violations.isEmpty());

		nextIDSeed = new NextIDSeed(DBIdGenerator.SEQUENTIAL_ID, 1, Integer.MAX_VALUE);
		violations = validator.validate(nextIDSeed);
		assertTrue(violations.isEmpty());
	}

	public void testValidateForNextIDSeedNegativeCase_Type() throws Exception {
		NextIDSeed nextIDSeed = new NextIDSeed(DBIdGenerator.AUDIT_ID + "X", 1, 1);
		List<ValidationViolation> violations = validator.validate(nextIDSeed);
		assertEquals(1, violations.size());
		assertEquals(DBIdGenerator.AUDIT_ID + "X", violations.get(0).getInvalidValue());

		nextIDSeed = new NextIDSeed(null, 1, 1);
		violations = validator.validate(nextIDSeed);
		assertEquals(1, violations.size());
		assertNull(violations.get(0).getInvalidValue());
	}

	public void testValidateForNextIDSeedNegativeCase_Cache() throws Exception {
		NextIDSeed nextIDSeed = new NextIDSeed(DBIdGenerator.AUDIT_ID, 1, 0);
		List<ValidationViolation> violations = validator.validate(nextIDSeed);
		assertEquals(1, violations.size());
		assertEquals(0, violations.get(0).getInvalidValue());

		nextIDSeed = new NextIDSeed(DBIdGenerator.AUDIT_ID, 1, -2);
		violations = validator.validate(nextIDSeed);
		assertEquals(1, violations.size());
		assertEquals(-2, violations.get(0).getInvalidValue());
	}

	public void testValidateForNextIDSeedNegativeCase_Seed() throws Exception {
		NextIDSeed nextIDSeed = new NextIDSeed(DBIdGenerator.AUDIT_ID, 0, 1);
		List<ValidationViolation> violations = validator.validate(nextIDSeed);
		assertEquals(1, violations.size());
		assertEquals(0, violations.get(0).getInvalidValue());

		nextIDSeed = new NextIDSeed(DBIdGenerator.AUDIT_ID, -1, 1);
		violations = validator.validate(nextIDSeed);
		assertEquals(1, violations.size());
		assertEquals(-1, violations.get(0).getInvalidValue());
	}
}
