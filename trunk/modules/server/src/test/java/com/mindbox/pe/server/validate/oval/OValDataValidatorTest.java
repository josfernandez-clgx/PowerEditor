package com.mindbox.pe.server.validate.oval;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.server.ServerTestObjectMother.createGenericCategory;
import static com.mindbox.pe.server.ServerTestObjectMother.createGenericEntity;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.DefaultTimedAssociationKey;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.server.AbstractTestWithGenericEntityType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.servlet.ResourceUtil;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityConfig;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;

public class OValDataValidatorTest extends AbstractTestWithGenericEntityType {

	private OValDataValidator validator;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		final CategoryType categoryType = new CategoryType();
		categoryType.setName("category-" + entityType.getName());
		categoryType.setTypeID(entityTypeDefinition.getCategoryType());
		categoryType.setShowInSelectionTable(Boolean.FALSE);

		final PowerEditorConfiguration powerEditorConfiguration = XmlUtil.unmarshal(new FileReader("src/test/config/PowerEditorConfiguration-NoProgram.xml"), PowerEditorConfiguration.class);
		powerEditorConfiguration.setEntityConfig(new EntityConfig());
		powerEditorConfiguration.getEntityConfig().getEntityType().add(entityTypeDefinition);
		powerEditorConfiguration.getEntityConfig().getCategoryType().add(categoryType);

		ConfigurationManager.initialize("1.0", "b1", powerEditorConfiguration, "src/test/config/PowerEditorConfiguration-NoProgram.xml");

		this.validator = OValDataValidator.getInstance();
		ReflectionUtil.executeStaticPrivate(ResourceUtil.class, "initialize", new Class<?>[] { String.class, String[].class }, new Object[] {
				"src/main/resources",
				new String[] { "ValidationMessages" } });
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		ReflectionUtil.setPrivate(ConfigurationManager.class, "instance", null);
	}

	@Test
	public void testValidateForAssocaitionKeyNegativeCase_ID() throws Exception {
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(0, createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
		timedAssocaitionKey = new DefaultTimedAssociationKey(0, createDateSynonym(), null);
		violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForAssocaitionKeyPositiveCase() throws Exception {
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(1, createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidateForDateSynonymNegativeCase_ID() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		dateSynonym.setID(-2);
		List<ValidationViolation> violations = validator.validate(dateSynonym);
		assertEquals(1, violations.size());

		dateSynonym.setID(0);
		violations = validator.validate(dateSynonym);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForDateSynonymNegativeCase_Name() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		dateSynonym.setName(" ");
		dateSynonym.setDate(new Date());
		List<ValidationViolation> violations = validator.validate(dateSynonym);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForDateSynonymPositiveCase() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		List<ValidationViolation> violations = validator.validate(dateSynonym);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidateForGenericCategoryNegativeCase_ChildAssociation() throws Exception {
		GenericCategory genericCategory = createGenericCategory(entityType);
		genericCategory.setRootIndicator(true);
		genericCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(-2, createDateSynonym(), null));
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());

		genericCategory = createGenericCategory(entityType);
		genericCategory.setRootIndicator(true);
		genericCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(genericCategory.getID(), createDateSynonym(), null));
		violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericCategoryNegativeCase_ID() throws Exception {
		GenericCategory genericCategory = createGenericCategory(entityType);
		genericCategory.setID(-2);
		genericCategory.setRootIndicator(true);
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());

		genericCategory.setID(0);
		violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericCategoryNegativeCase_Name() throws Exception {
		GenericCategory genericCategory = createGenericCategory(entityType);
		genericCategory.setRootIndicator(true);
		genericCategory.setName(" ");
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
		logger.info(violations.get(0));
	}

	@Test
	public void testValidateForGenericCategoryNegativeCase_ParentAssocation() throws Exception {
		GenericCategory genericCategory = createGenericCategory(entityType);
		genericCategory.addParentKey(new DefaultMutableTimedAssociationKey(-2, createDateSynonym(), null));
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());

		genericCategory = createGenericCategory(entityType);
		genericCategory.addParentKey(new DefaultMutableTimedAssociationKey(genericCategory.getID(), createDateSynonym(), null));
		violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericCategoryNegativeCase_Type() throws Exception {
		GenericCategory genericCategory = new GenericCategory(Persistent.UNASSIGNED_ID, "name", entityType.getCategoryType() + 1);
		genericCategory.setRootIndicator(true);
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericCategoryPositiveCase() throws Exception {
		GenericCategory genericCategory = createGenericCategory(entityType);
		genericCategory.setRootIndicator(true);
		List<ValidationViolation> violations = validator.validate(genericCategory);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataNegativeCase_EffDate() throws Exception {
		DateSynonym dateSynonym = createDateSynonym();
		dateSynonym.setName(" ");
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 1, entityType, 2, dateSynonym, null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataNegativeCase_ExpDate() throws Exception {
		DateSynonym dateSynonym1 = createDateSynonym();
		DateSynonym dateSynonym2 = createDateSynonym();
		dateSynonym2.setName(" ");
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 1, entityType, 2, dateSynonym1, dateSynonym2);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataNegativeCase_ExpDateBeforeEffDate() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 1, entityType, 2, ds2, ds1);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataNegativeCase_SourceID() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 0, entityType, 2, createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());

		data = new GenericEntityCompatibilityData(entityType, -2, entityType, 2, createDateSynonym(), null);
		violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataNegativeCase_SourceType() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(null, 1, entityType, 2, createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataNegativeCase_TargetID() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 1, entityType, 0, createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());

		data = new GenericEntityCompatibilityData(entityType, 2, entityType, -2, createDateSynonym(), null);
		violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataNegativeCase_TargetIDSameAsSourceID() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 5, entityType, 5, createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataNegativeCase_TargetType() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 1, null, 2, createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(data);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityCompatibilityDataPositiveCase() throws Exception {
		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 1, entityType, 2, createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(data);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidateForGenericEntityNegativeCase_CategoryAssociations() throws Exception {
		GenericEntity entity = createGenericEntity(entityType);
		entity.setProperty("string-property", createString());
		entity.addCategoryAssociation(new DefaultMutableTimedAssociationKey(-2));
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityNegativeCase_ID() throws Exception {
		GenericEntity entity = createGenericEntity(entityType);
		entity.setProperty("string-property", createString());
		entity.setID(0);
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());

		entity.setID(-2);
		violations = validator.validate(entity);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityNegativeCase_Name() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType, " ");
		entity.setProperty("string-property", createString());
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityNegativeCase_ParentID() throws Exception {
		GenericEntity entity = createGenericEntity(entityType);
		entity.setProperty("string-property", createString());
		entity.setParentID(-2);
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());

		entity.setParentID(0);
		violations = validator.validate(entity);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForGenericEntityNegativeCase_Properties() throws Exception {
		GenericEntity entity = createGenericEntity(entityType);
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size());
		String msg = violations.get(0).getMessage();
		assertEquals(
				String.format(
						"Must provide values for the following properties: %s",
						ConfigurationManager.getInstance().getEntityConfigHelper().findPropertyDisplayName(entity.getType(), "string-property")),
				msg);
	}

	@Test
	public void testValidateForGenericEntityNegativeCase_Type() throws Exception {
		GenericEntity entity = new GenericEntity(1, null, "name");
		entity.setProperty("string-property", createString());
		List<ValidationViolation> violations = validator.validate(entity);
		assertEquals(1, violations.size()); // one for null type and one for property check (if type == null, it fails)
	}

	@Test
	public void testValidateForGenericEntityPositiveCase() throws Exception {
		GenericEntity entity = createGenericEntity(entityType);
		entity.setProperty("string-property", createString());
		List<ValidationViolation> violations = validator.validate(entity);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidateForTimedAssocaitionKeyNegativeCase_ActDate() throws Exception {
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(1, createDateSynonym(), null);
		timedAssocaitionKey.getEffectiveDate().setID(-2);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForTimedAssocaitionKeyNegativeCase_ExpDate() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(1, ds1, ds2);
		timedAssocaitionKey.getExpirationDate().setID(-2);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForTimedAssocaitionKeyNegativeCase_ExpDateBeforeEffDate() throws Exception {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2006, 10, 10, 10, 10, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2006, 10, 10, 10, 10, 30));
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(1, ds2, ds1);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertEquals(1, violations.size());
	}

	@Test
	public void testValidateForTimedAssocaitionKeyPositiveCase() throws Exception {
		DefaultTimedAssociationKey timedAssocaitionKey = new DefaultTimedAssociationKey(1, createDateSynonym(), null);
		List<ValidationViolation> violations = validator.validate(timedAssocaitionKey);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidateForUnMarkedDataHappyCase() throws Exception {
		UserData userData = new UserData("", "", null, null, false, 0, null, null);
		List<ValidationViolation> violations = validator.validate(userData);
		assertTrue(violations.isEmpty());
	}
}
