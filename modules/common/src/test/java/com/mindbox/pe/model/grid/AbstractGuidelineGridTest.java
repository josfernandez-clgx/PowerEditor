package com.mindbox.pe.model.grid;

import static com.mindbox.pe.common.CommonTestObjectMother.attachGridTemplateColumn;
import static com.mindbox.pe.common.CommonTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplate;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractGuidelineGridTest extends AbstractTestBase {

	private static class GridImpl extends AbstractGuidelineGrid {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5770866060107041779L;

		protected GridImpl(AbstractGuidelineGrid sourceGrid) {
			super(sourceGrid);
		}

		protected GridImpl(int gridID, GridTemplate template, DateSynonym effDate, DateSynonym expDate) {
			super(gridID, template, effDate, expDate);
		}

		public Auditable deepCopy() {
			return null;
		}

		public boolean isParameterGrid() {
			return true;
		}
	}

	private AbstractGuidelineGrid grid;
	private TemplateUsageType usageType;

	@Before
	public void setUp() throws Exception {
		usageType = createUsageType();
		grid = new GridImpl(1, attachGridTemplateColumn(createGridTemplate(usageType), 1), null, null);
	}

	@Test
	public void testHasRuleIDNegativeCaseWithNoRuleIDColumn() throws Exception {
		grid.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		String strValue = String.valueOf(createInt());
		grid.setValue(1, 1, strValue);
		assertFalse(grid.hasRuleID(Long.valueOf(strValue)));
	}

	@Test
	public void testHasRuleIDNegativeCaseWithRuleIDColumn() throws Exception {
		grid.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		grid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long longValue = new Long(createInt());
		grid.setValue(1, 1, longValue);
		assertFalse(grid.hasRuleID(longValue + 1));
	}

	@Test
	public void testHasRuleIDPositiveCaseWithLongValue() throws Exception {
		grid.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		grid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long longValue = new Long(createInt());
		grid.setValue(1, 1, longValue);
		assertTrue(grid.hasRuleID(longValue));
	}

	@Test
	public void testHasRuleIDPositiveCaseWithStringValue() throws Exception {
		grid.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		grid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		String strValue = String.valueOf(createInt());
		grid.setValue(1, 1, strValue);
		assertTrue(grid.hasRuleID(Long.valueOf(strValue)));
	}

	@Test
	public void testHasSameCellValuesHappyCase() throws Exception {
		grid.setValue(1, 1, "value1");
		grid.setValue(2, 1, "value2");
		GridImpl grid2 = new GridImpl(1, attachGridTemplateColumn(createGridTemplate(usageType), 1), null, null);
		grid2.setValue(1, 1, "value1");
		grid2.setValue(2, 1, "value2");

		assertTrue(grid.hasSameCellValues(grid2));
		assertTrue(grid2.hasSameCellValues(grid));
	}

	@Test
	public void testHasSameCellValuesWithDifferentCellValuesReturnsFalse() throws Exception {
		grid.setValue(1, 1, "value1");

		GridImpl grid2 = new GridImpl(1, attachGridTemplateColumn(createGridTemplate(usageType), 1), null, null);
		grid2.setValue(1, 1, "value2");

		assertFalse(grid.hasSameCellValues(grid2));
		assertFalse(grid2.hasSameCellValues(grid));
	}

	@Test
	public void testHasSameCellValuesWithNullReturnsFalse() throws Exception {
		assertFalse(grid.hasSameCellValues(null));
	}

	@Test
	public void testIdenticalHappyCase() throws Exception {
		grid.setComments("comments");
		GridImpl grid2 = new GridImpl(1, (GridTemplate) grid.getTemplate(), null, null);
		grid2.setComments("comments");
		assertTrue(grid.identical(grid2));
		assertTrue(grid2.identical(grid));
	}

	@Test
	public void testIdenticalWithDifferentCommentsReturnsFalse() throws Exception {
		grid.setComments("comments");
		GridImpl grid2 = new GridImpl(1, attachGridTemplateColumn(createGridTemplate(usageType), 1), null, null);
		grid2.setComments("some");
		assertFalse(grid.identical(grid2));
		assertFalse(grid2.identical(grid));
	}

	@Test
	public void testIdenticalWithNullReturnsFalse() throws Exception {
		assertFalse(grid.identical(null));
	}
}