package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;

public class AbstractGuidelineGridTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractGuidelineGridTest Tests");
		suite.addTestSuite(AbstractGuidelineGridTest.class);
		return suite;
	}

	private static class GridImpl extends AbstractGuidelineGrid {

		protected GridImpl(AbstractGuidelineGrid sourceGrid) {
			super(sourceGrid);
		}

		protected GridImpl(int gridID, GridTemplate template, DateSynonym effDate, DateSynonym expDate) {
			super(gridID, template, effDate, expDate);
		}

		public boolean isParameterGrid() {
			return true;
		}

		public Auditable deepCopy() {
			return null;
		}
	}

	private AbstractGuidelineGrid grid;

	public void testHasRuleIDPositiveCaseWithLongValue() throws Exception {
		grid.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		grid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long longValue = new Long(ObjectMother.createInt());
		grid.setValue(1, 1, longValue);
		assertTrue(grid.hasRuleID(longValue));
	}

	public void testHasRuleIDPositiveCaseWithStringValue() throws Exception {
		grid.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		grid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		String strValue = String.valueOf(ObjectMother.createInt());
		grid.setValue(1, 1, strValue);
		assertTrue(grid.hasRuleID(Long.valueOf(strValue)));
	}

	public void testHasRuleIDNegativeCaseWithNoRuleIDColumn() throws Exception {
		grid.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		String strValue = String.valueOf(ObjectMother.createInt());
		grid.setValue(1, 1, strValue);
		assertFalse(grid.hasRuleID(Long.valueOf(strValue)));
	}

	public void testHasRuleIDNegativeCaseWithRuleIDColumn() throws Exception {
		grid.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		grid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long longValue = new Long(ObjectMother.createInt());
		grid.setValue(1, 1, longValue);
		assertFalse(grid.hasRuleID(longValue + 1));
	}

	public void testIdenticalWithNullReturnsFalse() throws Exception {
		assertFalse(grid.identical(null));
	}

	public void testIdenticalWithDifferentCommentsReturnsFalse() throws Exception {
		grid.setComments("comments");
		GridImpl grid2 = new GridImpl(1, ObjectMother.attachGridTemplateColumn(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				1), null, null);
		grid2.setComments("some");
		assertFalse(grid.identical(grid2));
		assertFalse(grid2.identical(grid));
	}

	public void testIdenticalHappyCase() throws Exception {
		grid.setComments("comments");
		GridImpl grid2 = new GridImpl(1, (GridTemplate) grid.getTemplate(), null, null);
		grid2.setComments("comments");
		assertTrue(grid.identical(grid2));
		assertTrue(grid2.identical(grid));
	}

	public void testHasSameCellValuesWithNullReturnsFalse() throws Exception {
		assertFalse(grid.hasSameCellValues(null));
	}

	public void testHasSameCellValuesWithDifferentCellValuesReturnsFalse() throws Exception {
		grid.setValue(1, 1, "value1");

		GridImpl grid2 = new GridImpl(1, ObjectMother.attachGridTemplateColumn(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				1), null, null);
		grid2.setValue(1, 1, "value2");

		assertFalse(grid.hasSameCellValues(grid2));
		assertFalse(grid2.hasSameCellValues(grid));
	}

	public void testHasSameCellValuesHappyCase() throws Exception {
		grid.setValue(1, 1, "value1");
		grid.setValue(2, 1, "value2");
		GridImpl grid2 = new GridImpl(1, ObjectMother.attachGridTemplateColumn(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				1), null, null);
		grid2.setValue(1, 1, "value1");
		grid2.setValue(2, 1, "value2");

		assertTrue(grid.hasSameCellValues(grid2));
		assertTrue(grid2.hasSameCellValues(grid));
	}

	public AbstractGuidelineGridTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		grid = new GridImpl(1, ObjectMother.attachGridTemplateColumn(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				1), null, null);
	}

	protected void tearDown() throws Exception {
		grid = null;
		config.resetConfiguration();
		super.tearDown();
	}
}
