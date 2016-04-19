package com.mindbox.pe.model.template;

import static com.mindbox.pe.common.CommonTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.TestObjectMother.createInteger;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.template.AbstractTemplateCore;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplateColumn;

public class AbstractTemplateCoreTest extends AbstractTestWithGenericEntityType {

	private static class TemplateImpl extends AbstractTemplateCore<GridTemplateColumn> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5127043999641243866L;

		public TemplateImpl(int templateID, String name, TemplateUsageType usageType) {
			super(templateID, name, usageType);
		}

		protected void adjustChangedColumnReferences(int columnNo, int newColumnNo) {
		}

		protected void adjustDeletedColumnReferences(int columnNo) {
		}

		protected GridTemplateColumn createTemplateColumn(GridTemplateColumn source) {
			return null;
		}
	}

	private AbstractTemplateCore<GridTemplateColumn> templateCore;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		int templateID = createInteger().intValue();
		templateCore = new TemplateImpl(templateID, "TemplateCor-" + templateID, createUsageType());
	}

	@Test
	public void testGetEntityTypeColumnsPositiveCase() throws Exception {
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(2, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		List<GridTemplateColumn> list = templateCore.getEntityTypeColumns();
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getColumnNumber());
	}

	@Test
	public void testGetEntityTypeColumnsWithNoEntityColumnReturnsEmptyList() throws Exception {
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		List<GridTemplateColumn> list = templateCore.getEntityTypeColumns();
		assertEquals(0, list.size());
	}

	@Test
	public void testGetRuleIDColumnNamesHappyCase() throws Exception {
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(2, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		List<String> columnNames = templateCore.getRuleIDColumnNames();
		assertEquals(1, columnNames.size());
		assertTrue(columnNames.contains(templateCore.getColumn(1).getName()));
	}

	@Test
	public void testGetRuleIDColumnNamesWithNoColumnsReturnsEmptyList() throws Exception {
		assertTrue(templateCore.getRuleIDColumnNames().isEmpty());
	}

	@Test
	public void testGetRuleIDColumnNamesWithNoRuleIDColumnsReturnsEmptyList() throws Exception {
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, templateCore.getUsageType())));
		assertTrue(templateCore.getRuleIDColumnNames().isEmpty());
	}

	@Test
	public void testHasEntityTypeColumnForHappyCase() throws Exception {
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(2, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		templateCore.getColumn(1).getColumnDataSpecDigest().setEntityType(entityType.getName());
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(false);
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);
		templateCore.getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		templateCore.getColumn(2).getColumnDataSpecDigest().setEntityType(entityType.getName());
		templateCore.getColumn(2).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		templateCore.getColumn(2).getColumnDataSpecDigest().setIsEntityAllowed(false);
		assertTrue(templateCore.hasEntityTypeColumnFor(entityType, true));
		assertTrue(templateCore.hasEntityTypeColumnFor(entityType, false));
	}

	@Test
	public void testHasEntityTypeColumnForNegativeCase() throws Exception {
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		templateCore.getColumn(1).getColumnDataSpecDigest().setEntityType(entityType.getName());
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(false);
		assertFalse(templateCore.hasEntityTypeColumnFor(entityType, true));

		templateCore.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(false);
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);
		assertFalse(templateCore.hasEntityTypeColumnFor(entityType, false));
	}

	@Test
	public void testHasEntityTypeColumnForWithNoColumnsReturnsFalse() throws Exception {
		assertFalse(templateCore.hasEntityTypeColumnFor(entityType, true));
		assertFalse(templateCore.hasEntityTypeColumnFor(entityType, false));
	}

	@Test
	public void testHasEntityTypeColumnForWithNullEntityTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(templateCore, "hasEntityTypeColumnFor", new Class[] { GenericEntityType.class, boolean.class }, new Object[] { null, Boolean.TRUE });
	}

	@Test
	public void testHasRuleIDColumnNegativeCase() throws Exception {
		assertFalse(templateCore.hasRuleIDColumn());
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, templateCore.getUsageType())));
		assertFalse(templateCore.hasRuleIDColumn());
	}

	@Test
	public void testHasRuleIDColumnPositiveCase() throws Exception {
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(2, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		assertTrue(templateCore.hasRuleIDColumn());
	}
}
