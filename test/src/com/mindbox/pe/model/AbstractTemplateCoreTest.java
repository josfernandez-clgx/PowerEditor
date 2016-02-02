package com.mindbox.pe.model;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;

public class AbstractTemplateCoreTest extends AbstractTestWithTestConfig {

	private static class TemplateImpl extends AbstractTemplateCore<GridTemplateColumn> {

		public TemplateImpl(int templateID, String name, TemplateUsageType usageType) {
			super(templateID, name, usageType);
			// TODO Auto-generated constructor stub
		}

		protected GridTemplateColumn createTemplateColumn(GridTemplateColumn source) {
			return null;
		}

		protected void adjustDeletedColumnReferences(int columnNo) {
		}

		protected void adjustChangedColumnReferences(int columnNo, int newColumnNo) {
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractTemplateCoreTest Tests");
		suite.addTestSuite(AbstractTemplateCoreTest.class);
		return suite;
	}

	private AbstractTemplateCore<GridTemplateColumn> templateCore;

	public AbstractTemplateCoreTest(String name) {
		super(name);
	}

	public void testHasRuleIDColumnPositiveCase() throws Exception {
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(2, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		assertTrue(templateCore.hasRuleIDColumn());
	}
	
	public void testHasRuleIDColumnNegativeCase() throws Exception {
		assertFalse(templateCore.hasRuleIDColumn());
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, templateCore.getUsageType())));
		assertFalse(templateCore.hasRuleIDColumn());
	}
	
	public void testGetRuleIDColumnNamesWithNoColumnsReturnsEmptyList() throws Exception {
		assertTrue(templateCore.getRuleIDColumnNames().isEmpty());
	}
	
	public void testGetRuleIDColumnNamesWithNoRuleIDColumnsReturnsEmptyList() throws Exception {
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, templateCore.getUsageType())));
		assertTrue(templateCore.getRuleIDColumnNames().isEmpty());
	}
	
	public void testGetRuleIDColumnNamesHappyCase() throws Exception {
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(2, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		List<String> columnNames = templateCore.getRuleIDColumnNames();
		assertEquals(1, columnNames.size());
		assertTrue(columnNames.contains(templateCore.getColumn(1).getName()));
	}
		
	public void testGetEntityTypeColumnsPositiveCase() throws Exception {
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(2, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		List<GridTemplateColumn> list = templateCore.getEntityTypeColumns();
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getColumnNumber());
	}

	public void testGetEntityTypeColumnsWithNoEntityColumnReturnsEmptyList() throws Exception {
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		List<GridTemplateColumn> list = templateCore.getEntityTypeColumns();
		assertEquals(0, list.size());
	}

	public void testHasEntityTypeColumnForWithNullEntityTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(templateCore,"hasEntityTypeColumnFor", new Class[]{GenericEntityType.class,boolean.class}, new Object[]{null,Boolean.TRUE});
	}
	
	public void testHasEntityTypeColumnForWithNoColumnsReturnsFalse() throws Exception {
		assertFalse(templateCore.hasEntityTypeColumnFor(GenericEntityType.forName("product"), true));
		assertFalse(templateCore.hasEntityTypeColumnFor(GenericEntityType.forName("product"), false));
	}
	
	public void testHasEntityTypeColumnForHappyCase() throws Exception {
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(2, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		templateCore.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(false);
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);
		templateCore.getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		templateCore.getColumn(2).getColumnDataSpecDigest().setEntityType("product");
		templateCore.getColumn(2).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		templateCore.getColumn(2).getColumnDataSpecDigest().setIsEntityAllowed(false);
		assertTrue(templateCore.hasEntityTypeColumnFor(GenericEntityType.forName("product"), true));
		assertTrue(templateCore.hasEntityTypeColumnFor(GenericEntityType.forName("product"), false));
	}
	
	public void testHasEntityTypeColumnForNegativeCase() throws Exception {
		templateCore.addColumn(ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(1, templateCore.getUsageType())));
		templateCore.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		templateCore.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(false);
		assertFalse(templateCore.hasEntityTypeColumnFor(GenericEntityType.forName("product"), true));
		
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(false);
		templateCore.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);
		assertFalse(templateCore.hasEntityTypeColumnFor(GenericEntityType.forName("product"), false));
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		int templateID = ObjectMother.createInteger().intValue();
		templateCore = new TemplateImpl(templateID, "TemplateCor-"+templateID, TemplateUsageType.getAllInstances()[0]);
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
}
