package com.mindbox.pe.common;

import static com.mindbox.pe.unittest.UnitTestHelper.equalsNullOrEmpty;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;

public class CommonTestAssert {

	public static void assertCommentEquals(RuleElement e1, RuleElement e2) {
		assertCommentEquals("", e1, e2);
	}

	public static void assertCommentEquals(String message, RuleElement e1, RuleElement e2) {
		assertTrue(message + "; comments do not match for " + e1 + "," + e2, equalsNullOrEmpty(e1.getComment(), e2.getComment()));
	}

	public static void assertContains(GenericCategory category, CategoryOrEntityValues values) {
		assertContains("", category, values);
	}

	public static void assertContains(GenericEntity entity, CategoryOrEntityValues values) {
		assertContains("", entity, values);
	}

	public static void assertContains(String message, GenericCategory category, CategoryOrEntityValues values) {
		if (values == null) fail(message + ": CategoryOrEntityValues is null");
		for (int i = 0; i < values.size(); i++) {
			CategoryOrEntityValue value = (CategoryOrEntityValue) values.get(i);
			if (!value.isForEntity() && value.getId() == category.getId()) return;
		}
		fail(message + ": " + category + " not found in " + values);
	}

	public static void assertContains(String message, GenericEntity entity, CategoryOrEntityValues values) {
		if (values == null) fail(message + ": CategoryOrEntityValues is null");
		for (int i = 0; i < values.size(); i++) {
			CategoryOrEntityValue value = (CategoryOrEntityValue) values.get(i);
			if (value.isForEntity() && value.getId() == entity.getId()) return;
		}
		fail(message + ": " + entity + " not found in " + values);
	}

	public static void assertEquals(ColumnMessageFragmentDigest digest1, ColumnMessageFragmentDigest digest2) {
		assertEquals("", digest1, digest2);
	}

	public static void assertEquals(Condition condition1, Condition condition2) {
		assertEquals("", condition1, condition2);
	}

	public static void assertEquals(GenericEntity entity1, GenericEntity entity2) throws Exception {
		Assert.assertEquals("id mismatch", entity1.getID(), entity2.getID());
		Assert.assertEquals("type mismatch", entity1.getType(), entity2.getType());
		Assert.assertEquals("parent id mismatch", entity1.getParentID(), entity2.getParentID());
		String[] propNames = entity1.getProperties();
		for (int i = 0; i < propNames.length; i++) {
			Assert.assertEquals(propNames[i] + " property mismatch", entity1.getProperty(propNames[i]), entity2.getProperty(propNames[i]));
		}
		Assert.assertEquals("property length mismatch", propNames.length, entity2.getProperties().length);
	}

	public static void assertEquals(GenericEntityCompatibilityData cd1, GenericEntityCompatibilityData cd2) {
		assertEquals("", cd1, cd2);
	}

	public static void assertEquals(String message, ColumnMessageFragmentDigest digest1, ColumnMessageFragmentDigest digest2) {
		Assert.assertEquals(message + "; type mismatch", digest1.getType(), digest2.getType());
		Assert.assertEquals(message + "; text mismatch", digest1.getText(), digest2.getText());
		Assert.assertEquals(message + "; cell selection mismatch", digest1.getCellSelection(), digest2.getCellSelection());
		Assert.assertEquals(message + "; enum delimiter mismatch", digest1.getEnumDelimiter(), digest2.getEnumDelimiter());
		Assert.assertEquals(message + "; enum final delimiter mismatch", digest1.getEnumFinalDelimiter(), digest2.getEnumFinalDelimiter());
		Assert.assertEquals(message + "; enum prefix mismatch", digest1.getEnumPrefix(), digest2.getEnumPrefix());
		Assert.assertEquals(message + "; range style mismatch", digest1.getRangeStyle(), digest2.getRangeStyle());
	}

	public static void assertEquals(String message, Condition condition1, Condition condition2) {
		Assert.assertEquals(message + "; object name mismatch", condition1.getObjectName(), condition2.getObjectName());
		Assert.assertEquals(message + "; operator mismatch", condition1.getOp(), condition2.getOp());
		Assert.assertEquals(message + "; reference mismatch", condition1.getReference(), condition2.getReference());
		Assert.assertEquals(message + "; value mismatch", condition1.getValue().toString(), condition2.getValue().toString());
		assertCommentEquals(message, condition1, condition2);
	}

	public static void assertEquals(String message, GenericEntityCompatibilityData cd1, GenericEntityCompatibilityData cd2) {
		Assert.assertEquals(message + "; id mismatch", cd1.getID(), cd2.getID());
		Assert.assertEquals(message + "; source id mismatch", cd1.getSourceID(), cd2.getSourceID());
		Assert.assertEquals(message + "; source type mismatch", cd1.getSourceType(), cd2.getSourceType());
		Assert.assertEquals(message + "; associable id mismatch", cd1.getAssociableID(), cd2.getAssociableID());
		Assert.assertEquals(message + "; effective date mismatch", cd1.getEffectiveDate(), cd2.getEffectiveDate());
		Assert.assertEquals(message + "; expiration date mismatch", cd1.getExpirationDate(), cd2.getExpirationDate());
		Assert.assertEquals(message + "; generic entity type mismatch", cd1.getGenericEntityType(), cd2.getGenericEntityType());
	}

	public static void assertPropertyEquals(Object bean, String property, Object expectedValue) throws Exception {
		Assert.assertEquals(expectedValue, PropertyUtils.getProperty(bean, property));
	}

	public static void asssertPropertyEquals(Object bean1, Object bean2, String property) throws Exception {
		Assert.assertEquals(property + " mismatch", PropertyUtils.getProperty(bean1, property), PropertyUtils.getProperty(bean2, property));
	}

}
