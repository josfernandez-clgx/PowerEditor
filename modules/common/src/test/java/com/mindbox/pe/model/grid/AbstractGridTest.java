package com.mindbox.pe.model.grid;

import static com.mindbox.pe.common.CommonTestObjectMother.attachGridTemplateColumn;
import static com.mindbox.pe.common.CommonTestObjectMother.createDateSynonym;
import static com.mindbox.pe.common.CommonTestObjectMother.createGenericEntityType;
import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplate;
import static com.mindbox.pe.common.CommonTestObjectMother.createGuidelineGrid;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.UnitTestHelper.assertArrayEqualsIgnoresOrder;
import static com.mindbox.pe.unittest.UnitTestHelper.assertContains;
import static com.mindbox.pe.unittest.UnitTestHelper.assertNotEquals;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.GridValueContainable;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.unittest.AbstractTestBase;


/**
 * Unit tests for {@link com.mindbox.pe.model.grid.AbstractGrid}.
 * 
 * @author Geneho Kim
 * 
 */
public class AbstractGridTest extends AbstractTestBase {

	private static class GridImpl extends AbstractGrid<GridTemplateColumn> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2803757224112668651L;

		private Object[][] cellValues;

		protected GridImpl(int gridID, int templateID) {
			super(gridID, templateID, null, null);
		}

		protected GridImpl(AbstractGrid<GridTemplateColumn> grid, GridTemplate template, DateSynonym effDate, DateSynonym expDate) {
			super(grid, template, effDate, expDate);
		}

		protected GridImpl(AbstractGrid<GridTemplateColumn> grid) {
			super(grid);
		}

		public boolean isParameterGrid() {
			return false;
		}

		protected boolean hasSameCellValues(AbstractGrid<GridTemplateColumn> abstractgrid) {
			if (cellValues == null) return abstractgrid.isEmpty();
			for (int i = 0; i < cellValues.length; i++) {
				for (int j = 0; j < cellValues[i].length; j++) {
					try {
						if (!isSame(cellValues[i][j], abstractgrid.getCellValueObject(i + 1, j + 1, null))) {
							return false;
						}
					}
					catch (InvalidDataException e) {
						throw new RuntimeException(e);
					}
				}
			}
			return true;
		}

		public boolean hasSameCellValues(GridValueContainable valueContainer) {
			return false;
		}

		public boolean isEmpty() {
			return false;
		}

		public String[] getColumnNames() {
			return null;
		}

		public Object getCellValue(int row, String column) {
			return null;
		}

		public Object[][] getDataObjects() {
			return cellValues;
		}

		public void copyCellValue(GridValueContainable source) {
		}

		public void clearValues() {
		}

		public Object getCellValueObject(int i, int j, Object defaultValue) throws InvalidDataException {
			return (cellValues == null ? defaultValue : cellValues[i - 1][j - 1]);
		}

		public void setValue(int rowID, String columnName, Object value) {
		}

		public void setValue(int rowID, int col, Object value) {
		}

		public Auditable deepCopy() {
			return null;
		}

		@Override
		public boolean hasSameRow(int row, String[] columnNames, int targeRow, GridValueContainable valueContainable) {
			return false;
		}

		@Override
		public boolean isEmptyRow(int row) {
			return false;
		}
	}

	private GridImpl grid;
	private GenericEntityType genericEntityType1;
	private GenericEntityType genericEntityType2;
	private GenericEntityType genericEntityType3;
	private GenericEntityType genericEntityType4;
	private GenericEntityType genericEntityType5;
	private TemplateUsageType templateUsageType;

	@Before
	public void setUp() throws Exception {
		genericEntityType1 = createGenericEntityType(createInt(), createInt());
		genericEntityType2 = createGenericEntityType(createInt(), createInt());
		genericEntityType3 = createGenericEntityType(createInt(), createInt());
		genericEntityType4 = createGenericEntityType(createInt(), createInt());
		genericEntityType5 = createGenericEntityType(createInt(), createInt());
		templateUsageType = createUsageType();
		grid = new GridImpl(1, 100);
		grid.setTemplate(createGridTemplate(templateUsageType));
		grid.setStatusChangeDate(getDate(2006, 5, 31)); // set to a date other than now
	}

	@After
	public void tearDown() throws Exception {
		grid = null;
	}

	@Test
	public void testEqualsForAbstractGridWithNullGridThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(grid, "equals", new Class[] { AbstractGrid.class }, new Object[] { null });
	}

	@Test
	public void testEqualsForAbstractGridWithDifferentTemplateIDReturnsFalse() throws Exception {
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID() + 1);
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	@Test
	public void testEqualsForAbstractGridWithDifferentCellValuesReturnsFalse() throws Exception {
		attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.cellValues = new String[][] { new String[] { "value1" } };
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid.cellValues = new String[][] { new String[] { "value2" } };
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	@Test
	public void testEqualsForAbstractGridWithDifferentEffectiveDateReturnsFalse() throws Exception {
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid2.setEffectiveDate(createDateSynonym());
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	@Test
	public void testEqualsForAbstractGridWithDifferentExpirationDateReturnsFalse() throws Exception {
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid2.setExpirationDate(createDateSynonym());
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	@Test
	public void testEqualsForAbstractGridWithDifferentStatusReturnsFalse() throws Exception {
		grid.setStatus("draft");
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid2.setStatus(grid.getStatus() + "-new");
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	@Test
	public void testEqualsForAbstractGridHappyCase() throws Exception {
		attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.cellValues = new String[][] { new String[] { "value1" } };
		grid.setStatus("draft");
		grid.setEffectiveDate(createDateSynonym());
		grid.setExpirationDate(createDateSynonym());

		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid2.setStatus(grid.getStatus());
		grid2.cellValues = new String[][] { new String[] { "value1" } };
		grid2.setEffectiveDate(grid.getEffectiveDate());
		grid2.setExpirationDate(grid.getExpirationDate());
		assertTrue(grid.equals(grid2));
		assertTrue(grid2.equals(grid));
	}

	@Test
	public void testAddGenericCategoryIDsWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(grid, "addGenericCategoryIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] { null, new int[] { 1 } });
	}

	@Test
	public void testAddGenericCategoryIDsWithNullOrEmptyIDsThrowsIllegalArgumentException() throws Exception {
		assertThrowsException(grid, "addGenericCategoryIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] { genericEntityType2, new int[0] }, IllegalArgumentException.class);
		assertThrowsException(grid, "addGenericCategoryIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] { genericEntityType2, null }, IllegalArgumentException.class);
	}

	@Test
	public void testAddGenericCategoryIDsAddsToExistingContext() throws Exception {
		ProductGrid grid = createGuidelineGrid(templateUsageType);
		grid.setGenericCategoryIDs(genericEntityType2, new int[] { 100, 200 });

		grid.addGenericCategoryIDs(genericEntityType2, new int[] { 300, 400 });
		assertArrayEqualsIgnoresOrder(new int[] { 100, 200, 300, 400 }, grid.getGenericCategoryIDs(genericEntityType2));
	}

	@Test
	public void testAddGenericCategoryIDsAddsNewContext() throws Exception {
		ProductGrid grid = createGuidelineGrid(templateUsageType);
		grid.setGenericCategoryIDs(genericEntityType1, new int[] { 100, 200 });
		grid.setGenericEntityIDs(genericEntityType2, new int[] { 11, 22 });

		grid.addGenericCategoryIDs(genericEntityType2, new int[] { 300, 400 });
		assertArrayEqualsIgnoresOrder(new int[] { 300, 400 }, grid.getGenericCategoryIDs(genericEntityType2));
	}

	@Test
	public void testAddGenericCategoryIDsWithDuplicateIDsIgnored() throws Exception {
		ProductGrid grid = createGuidelineGrid(templateUsageType);
		grid.setGenericCategoryIDs(genericEntityType2, new int[] { 100, 200 });

		grid.addGenericCategoryIDs(genericEntityType2, new int[] { 200, 400, 20 });
		assertArrayEqualsIgnoresOrder(new int[] { 20, 100, 200, 400 }, grid.getGenericCategoryIDs(genericEntityType2));
	}

	@Test
	public void testAddGenericEntityIDsWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(grid, "addGenericEntityIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] { null, new int[] { 1 } });
	}

	@Test
	public void testAddGenericEntityIDsWithNullOrEmptyIDsThrowsIllegalArgumentException() throws Exception {
		assertThrowsException(grid, "addGenericEntityIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] { genericEntityType2, new int[0] }, IllegalArgumentException.class);
		assertThrowsException(grid, "addGenericEntityIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] { genericEntityType2, (int[]) null }, IllegalArgumentException.class);
	}

	@Test
	public void testAddGenericEntityIDsAddsToExistingContext() throws Exception {
		ProductGrid grid = createGuidelineGrid(templateUsageType);
		grid.setGenericEntityIDs(genericEntityType2, new int[] { 100, 200 });

		grid.addGenericEntityIDs(genericEntityType2, new int[] { 300, 400 });
		assertArrayEqualsIgnoresOrder(new int[] { 100, 200, 300, 400 }, grid.getGenericEntityIDs(genericEntityType2));
	}

	@Test
	public void testAddGenericEntityIDsAddsNewContext() throws Exception {
		ProductGrid grid = createGuidelineGrid(templateUsageType);
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 100, 200 });
		grid.setGenericCategoryIDs(genericEntityType2, new int[] { 11, 22 });

		grid.addGenericEntityIDs(genericEntityType2, new int[] { 300, 400 });
		assertArrayEqualsIgnoresOrder(new int[] { 300, 400 }, grid.getGenericEntityIDs(genericEntityType2));
	}

	@Test
	public void testAddGenericEntityIDsWithDuplicateIDsIgnored() throws Exception {
		ProductGrid grid = createGuidelineGrid(templateUsageType);
		grid.setGenericEntityIDs(genericEntityType2, new int[] { 100, 200 });

		grid.addGenericEntityIDs(genericEntityType2, new int[] { 100, 200, 400 });
		assertArrayEqualsIgnoresOrder(new int[] { 100, 200, 400 }, grid.getGenericEntityIDs(genericEntityType2));
	}

	@Test
	public void testInitForGridTemplateDateDateWithNullGridThrowsNullPointerException() throws Exception {
		try {
			new GridImpl(null, new GridTemplate(2, "test2", templateUsageType), null, null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testInitForGridTemplateDateDateCreatesCopyExceptIDTemplateAndDates() throws Exception {
		DateSynonym dateSynonym1 = new DateSynonym(100, "date1", "", new Date());
		DateSynonym dateSynonym2 = new DateSynonym(100, "date1", "", new Date());
		AbstractGrid<GridTemplateColumn> grid2 = new GridImpl(grid, new GridTemplate(2, "test2", templateUsageType), dateSynonym1, dateSynonym2);
		assertEquals(Persistent.UNASSIGNED_ID, grid2.getID());
		assertEquals(2, grid2.getTemplateID());
		assertEquals(grid2.getEffectiveDate().getDate(), dateSynonym1.getDate());
		assertEquals(grid2.getEffectiveDate().getDate(), dateSynonym1.getDate());
		assertEquals(grid.getStatus(), grid2.getStatus());
		assertEquals(grid.getStatusChangeDate(), grid2.getStatusChangeDate());
		assertEquals(grid.getComments(), grid2.getComments());
		assertEquals(grid.getNumRows(), grid2.getNumRows());
	}

	@Test
	public void testInitForGridWithNullGridThrowsNullPointerException() throws Exception {
		try {
			new GridImpl(null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testInitForGridCreatesClonedCopy() throws Exception {
		AbstractGrid<GridTemplateColumn> grid2 = new GridImpl(grid);
		assertEquals(Persistent.UNASSIGNED_ID, grid2.getID());
		assertEquals(grid.getID(), grid2.getCloneOf());
		assertEquals(grid.getTemplateID(), grid2.getTemplateID());
		assertEquals(grid.getEffectiveDate(), grid2.getEffectiveDate());
		assertEquals(grid.getExpirationDate(), grid2.getExpirationDate());
		assertEquals(grid.getStatus(), grid2.getStatus());
		assertEquals(grid2.getCreationDate(), grid2.getStatusChangeDate());
		assertNotEquals(grid.getStatusChangeDate(), grid2.getStatusChangeDate());
		assertEquals(grid.getComments(), grid2.getComments());
		assertEquals(grid.getNumRows(), grid2.getNumRows());
	}

	@Test
	public void testCopyEntireContextWithEmptyContextClearsContext() throws Exception {
		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(genericEntityType1, new int[] { 1, 3, 2 });
		grid2.setGenericEntityIDs(genericEntityType2, new int[] { 20, 10 });

		grid2.copyEntireContext(grid);
		assertTrue(grid2.isContextEmpty());
	}

	@Test
	public void testCopyEntireContextWithEmptyEntityContextClearsEntityContext() throws Exception {
		grid.setGenericCategoryIDs(genericEntityType2, new int[] { 1111, 2222 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(genericEntityType1, new int[] { 1, 3, 2 });
		grid2.setGenericEntityIDs(genericEntityType2, new int[] { 20, 10 });

		grid2.copyEntireContext(grid);
		assertTrue(grid2.hasAnyGenericCategoryContext());
		assertFalse(grid2.hasAnyGenericEntityContext());
	}

	@Test
	public void testCopyEntireContextWithEmptyCategoryContextClearsCategoryContext() throws Exception {
		grid.setGenericEntityIDs(genericEntityType2, new int[] { 1111, 2222 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(genericEntityType1, new int[] { 1, 3, 2 });
		grid2.setGenericEntityIDs(genericEntityType2, new int[] { 20, 10 });

		grid2.copyEntireContext(grid);
		assertFalse(grid2.hasAnyGenericCategoryContext());
		assertTrue(grid2.hasAnyGenericEntityContext());
	}

	@Test
	public void testCopyEntireContextWithValidContextOnEmptyContextSucceeds() throws Exception {
		grid.setGenericCategoryIDs(genericEntityType1, new int[] { 1111, 2222 });
		grid.setGenericEntityIDs(genericEntityType2, new int[] { 444, 555 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.copyEntireContext(grid);

		assertTrue(grid2.hasSameContext(grid));
	}

	@Test
	public void testCopyEntireContextWithValidContextOverwritesExistingContext() throws Exception {
		grid.setGenericCategoryIDs(genericEntityType1, new int[] { 1111, 2222 });
		grid.setGenericEntityIDs(genericEntityType2, new int[] { 444, 555 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(genericEntityType1, new int[] { 1245 });
		grid2.setGenericEntityIDs(genericEntityType2, new int[] { 444, 555, 888, 999 });

		grid2.copyEntireContext(grid);

		assertTrue(grid2.hasSameContext(grid));
	}

	@Test
	public void testHasSameContextWithNullThrowsNullPointerException() throws Exception {
		try {
			grid.hasSameContext(null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testExtractGuidelineContextWithEmptyContextReturnsEmptyArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", templateUsageType), null, null);
		assertEquals(0, grid.extractGuidelineContext().length);
	}

	@Test
	public void testExtractGuidelineContextWithValidGridReturnsCorrectArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", templateUsageType), null, null);
		grid.setGenericCategoryIDs(genericEntityType2, new int[] { 100, 200 });
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 11, 22, 33, 44 });
		grid.setGenericCategoryIDs(genericEntityType4, new int[] { 1000 });

		GuidelineContext[] context = grid.extractGuidelineContext();
		assertEquals(3, context.length);
		for (int i = 0; i < context.length; i++) {
			if (context[i].hasCategoryContext() && context[i].getGenericCategoryType() == genericEntityType2.getCategoryType()) {
				assertArrayEqualsIgnoresOrder(new int[] { 100, 200 }, context[i].getIDs());
			}
			else if (context[i].hasCategoryContext() && context[i].getGenericCategoryType() == genericEntityType4.getCategoryType()) {
				assertArrayEqualsIgnoresOrder(new int[] { 1000 }, context[i].getIDs());
			}
			else {
				assertEquals(genericEntityType1, context[i].getGenericEntityType());
				assertArrayEqualsIgnoresOrder(new int[] { 33, 22, 11, 44 }, context[i].getIDs());
			}
		}
	}

	@Test
	public void testHasSameContextWithEmptyContextReturnsTrue() throws Exception {
		GridImpl grid2 = new GridImpl(2, 100);
		assertTrue(grid.hasSameContext(grid2));
	}

	@Test
	public void testHasSameContextWithCategoryTypeMismatchReturnsFalse() throws Exception {
		grid.addGenericCategoryID(genericEntityType2, 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericCategoryID(genericEntityType1, 222);

		assertFalse(grid.hasSameContext(grid2));
	}

	@Test
	public void testHasSameContextWithEntityTypeMismatchReturnsFalse() throws Exception {
		grid.addGenericEntityID(genericEntityType2, 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericEntityID(genericEntityType1, 222);

		assertFalse(grid.hasSameContext(grid2));
	}

	@Test
	public void testHasSameContextWithCategoryIDMismatchReturnsFalse() throws Exception {
		grid.addGenericCategoryID(genericEntityType1, 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericCategoryID(genericEntityType1, 122);

		assertFalse(grid.hasSameContext(grid2));
	}

	@Test
	public void testHasSameContextWithEntityIDMismatchReturnsFalse() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericEntityID(genericEntityType1, 122);

		assertFalse(grid.hasSameContext(grid2));
	}

	@Test
	public void testHasSameContextWithCategoryOnEntityReturnsFalse() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericCategoryID(genericEntityType1, 222);

		assertFalse(grid.hasSameContext(grid2));
	}

	@Test
	public void testHasSameContextWithEntityOnCategoryReturnsFalse() throws Exception {
		grid.addGenericCategoryID(genericEntityType1, 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericEntityID(genericEntityType1, 222);

		assertFalse(grid.hasSameContext(grid2));
	}

	@Test
	public void testHasSameContextWithSameContextReturnsTrue() throws Exception {
		grid.setGenericCategoryIDs(genericEntityType1, new int[] { 1, 2, 3 });
		grid.setGenericEntityIDs(genericEntityType2, new int[] { 10, 20 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(genericEntityType1, new int[] { 1, 3, 2 });
		grid2.setGenericEntityIDs(genericEntityType2, new int[] { 20, 10 });

		assertTrue(grid.hasSameContext(grid2));
	}

	@Test
	public void testGetGenericEntityIDsWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.getGenericEntityIDs(null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testGetGenericEntityIDsWithEmptyGridReturnsEmptyIntArray() throws Exception {
		assertEquals(0, grid.getGenericEntityIDs(genericEntityType1).length);
	}

	@Test
	public void testGetGenericEntityIDsWithNotFoundEntityTypeReturnsEmptyIntArray() throws Exception {
		grid.addGenericCategoryID(genericEntityType1, 100);
		grid.addGenericEntityID(genericEntityType2, 200);
		assertEquals(0, grid.getGenericEntityIDs(genericEntityType1).length);
	}

	@Test
	public void testAddGenericCategoryIDWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.addGenericCategoryID(null, 1000);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testAddGenericCategoryIDWithNonPositiveIDThrowsIllegalArgumentException() throws Exception {
		try {
			grid.addGenericCategoryID(genericEntityType1, 0);
			fail("Excepted IllegalArgumentException not thrown with zero");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
		try {
			grid.addGenericCategoryID(genericEntityType1, -1);
			fail("Excepted IllegalArgumentException not thrown with -1");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}

	@Test
	public void testAddGenericEntityIDWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.addGenericEntityID(null, 1000);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testAddGenericEntityIDWithNonPositiveIDThrowsIllegalArgumentException() throws Exception {
		try {
			grid.addGenericEntityID(genericEntityType1, 0);
			fail("Excepted IllegalArgumentException not thrown with zero");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
		try {
			grid.addGenericEntityID(genericEntityType1, -1);
			fail("Excepted IllegalArgumentException not thrown with -1");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}

	@Test
	public void testAddGenericEntityIDWithSameTypeAddsId() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 1000);
		grid.addGenericEntityID(genericEntityType1, 1001);
		assertArrayEqualsIgnoresOrder(new int[] { 1000, 1001 }, grid.getGenericEntityIDs(genericEntityType1));
	}

	@Test
	public void testAddGenericEntityIDWithSameValuesIsNoOp() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 1000);
		grid.addGenericEntityID(genericEntityType1, 1000);
		grid.addGenericEntityID(genericEntityType1, 1000);
		grid.addGenericEntityID(genericEntityType1, 1000);
		grid.addGenericEntityID(genericEntityType1, 1000);
		assertArrayEqualsIgnoresOrder(new int[] { 1000 }, grid.getGenericEntityIDs(genericEntityType1));
	}

	@Test
	public void testAddGenericEntityIDSupportMultipleEntityTypes() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 100);
		grid.addGenericEntityID(genericEntityType3, 400);
		grid.addGenericEntityID(genericEntityType3, 401);
		grid.addGenericEntityID(genericEntityType3, 402);
		assertArrayEqualsIgnoresOrder(new int[] { 100 }, grid.getGenericEntityIDs(genericEntityType1));
		assertArrayEqualsIgnoresOrder(new int[] { 400, 401, 402 }, grid.getGenericEntityIDs(genericEntityType3));
	}

	@Test
	public void testRemoveGenericEntityIDWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.removeGenericEntityID(null, 1000);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testRemoveGenericEntityIDWithNonPositiveIDThrowsIllegalArgumentException() throws Exception {
		try {
			grid.removeGenericEntityID(genericEntityType1, 0);
			fail("Excepted IllegalArgumentException not thrown with zero");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
		try {
			grid.removeGenericEntityID(genericEntityType1, -1);
			fail("Excepted IllegalArgumentException not thrown with -1");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}

	@Test
	public void testRemoveGenericEntityIDSameIDIsNoOp() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 1000);
		grid.removeGenericEntityID(genericEntityType1, 1000);
		grid.removeGenericEntityID(genericEntityType1, 1000);
		grid.removeGenericEntityID(genericEntityType1, 1000);
		grid.removeGenericEntityID(genericEntityType1, 1000);
		assertEquals(0, grid.getGenericEntityIDs(genericEntityType1).length);
	}

	@Test
	public void testRemoveGenericEntityIDSupportsMultipleEntityTypes() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 1000);
		grid.addGenericEntityID(genericEntityType3, 400);
		grid.removeGenericEntityID(genericEntityType1, 1000);
		grid.removeGenericEntityID(genericEntityType3, 400);
		assertEquals(0, grid.getGenericEntityIDs(genericEntityType1).length);
		assertEquals(0, grid.getGenericEntityIDs(genericEntityType3).length);
	}

	@Test
	public void testRemoveGenericEntityIDWithSameTypeRemovesIDs() throws Exception {
		grid.addGenericEntityID(genericEntityType3, 400);
		grid.addGenericEntityID(genericEntityType3, 401);
		grid.addGenericEntityID(genericEntityType3, 402);
		grid.removeGenericEntityID(genericEntityType3, 400);
		grid.removeGenericEntityID(genericEntityType3, 402);
		assertArrayEqualsIgnoresOrder(new int[] { 401 }, grid.getGenericEntityIDs(genericEntityType3));
	}

	@Test
	public void testGetGenericEntityTypesInUseSupportsMultipleEntityTypes() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 1000);
		grid.addGenericEntityID(genericEntityType3, 400);
		grid.addGenericEntityID(genericEntityType2, 1000);
		grid.addGenericEntityID(genericEntityType5, 400);
		grid.addGenericEntityID(genericEntityType4, 1000);
		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		assertEquals(5, types.length);
		assertContains(types, genericEntityType1);
		assertContains(types, genericEntityType2);
		assertContains(types, genericEntityType5);
		assertContains(types, genericEntityType3);
		assertContains(types, genericEntityType5);
	}

	@Test
	public void testGetGenericEntityTypesInUseDoesNotReturnsEmptyListType() throws Exception {
		grid.setGenericEntityIDs(genericEntityType2, new int[0]);
		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		assertEquals(0, types.length);
	}

	@Test
	public void testGetGenericCategoryEntityTypesInUseDoesNotReturnsEmptyListType() throws Exception {
		grid.setGenericCategoryIDs(genericEntityType2, new int[0]);
		GenericEntityType[] types = grid.getGenericCategoryEntityTypesInUse();
		assertEquals(0, types.length);
	}

	@Test
	public void testGetGenericEntityTypesWithEmptyContextReturnsEmptyArray() throws Exception {
		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		assertEquals(0, types.length);
	}

	@Test
	public void testGetGenericEntityTypesWithNotMatchingContextReturnsEmptyArray() throws Exception {
		grid.addGenericCategoryID(genericEntityType1, 100);
		grid.addGenericCategoryID(genericEntityType4, 100);

		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		assertEquals(0, types.length);
	}

	@Test
	public void testHasGenericEntityContextWithEmptyContextReturnsFalse() throws Exception {
		assertFalse(grid.hasAnyGenericEntityContext());
	}

	@Test
	public void testHasGenericEntityContextWithNotMatchingContextReturnsFalse() throws Exception {
		grid.addGenericCategoryID(genericEntityType1, 100);
		grid.addGenericCategoryID(genericEntityType4, 100);
		assertFalse(grid.hasAnyGenericEntityContext());
	}

	@Test
	public void testHasGenericEntityContextWithMatchingEntityReturnsTrue() throws Exception {
		grid.addGenericEntityID(genericEntityType4, 100);
		assertTrue(grid.hasAnyGenericEntityContext());
	}

	@Test
	public void testSetGenericEntityIDsWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.setGenericEntityIDs(null, new int[] { 100 });
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testSetGenericEntityIDsWithNullIDsThrowsNullPointerException() throws Exception {
		try {
			grid.setGenericEntityIDs(genericEntityType1, null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testSetGenericEntityIDsSetsEntityIDsOnEmtpyContext() throws Exception {
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 1234, 6789 });
		assertArrayEqualsIgnoresOrder(new int[] { 6789, 1234 }, grid.getGenericEntityIDs(genericEntityType1));
	}

	@Test
	public void testSetGenericEntityIDsOverwritesEntityIDsOnExistingContext() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 4000);
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 1234, 6789 });
		assertArrayEqualsIgnoresOrder(new int[] { 6789, 1234 }, grid.getGenericEntityIDs(genericEntityType1));
	}

	@Test
	public void testMatchesGenericEntityIDsWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.matchesGenericEntityIDs(null, new int[10]);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testMatchesGenericEntityIDsWithNullIDsWithEmptyTypeReturnsTrue() throws Exception {
		assertTrue(grid.matchesGenericEntityIDs(genericEntityType1, null));
	}

	@Test
	public void testMatchesGenericEntityIDsWithNullIDsWithNonEmptyTypeReturnsFalse() throws Exception {
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 1234 });
		assertFalse(grid.matchesGenericEntityIDs(genericEntityType1, null));
	}

	@Test
	public void testMatchesGenericEntityIDsWithMatchingTypeReturnsTrue() throws Exception {
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 1234, 33456 });
		assertTrue(grid.matchesGenericEntityIDs(genericEntityType1, new int[] { 1234, 33456 }));
	}

	@Test
	public void testMatchesGenericEntityIDsWithNonMatchingTypeReturnsFalse() throws Exception {
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 1234, 33456 });
		assertFalse(grid.matchesGenericEntityIDs(genericEntityType3, new int[] { 1234, 33456 }));
	}

	@Test
	public void testClearAllGenericEntitySupportMultipleEntityTypes() throws Exception {
		grid.addGenericEntityID(genericEntityType1, 1000);
		grid.addGenericEntityID(genericEntityType3, 400);
		grid.addGenericEntityID(genericEntityType2, 1000);
		grid.clearAllGenericEntity();
		assertFalse(grid.hasAnyGenericEntityContext());
		assertEquals(0, grid.getGenericEntityTypesInUse().length);
	}

	@Test
	public void testClearAllGenericEntityDoesNotClearCategories() throws Exception {
		grid.addGenericCategoryID(genericEntityType1, 100);
		grid.addGenericCategoryID(genericEntityType4, 100);
		grid.clearAllGenericEntity();
		assertFalse(grid.hasAnyGenericEntityContext());
	}

	@Test
	public void testClearGenericEntityWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.clearGenericEntity(null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testClearGenericEntityWithMatchingTypeClearsAllIDs() throws Exception {
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 1234, 6789 });
		grid.clearGenericEntity(genericEntityType1);
		assertFalse(grid.hasAnyGenericEntityContext());
	}

	@Test
	public void testClearGenericEntityWithDoesNotClearNonMatchingTypes() throws Exception {
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 1234, 6789 });
		grid.setGenericEntityIDs(genericEntityType5, new int[] { 1234, 6789 });
		grid.clearGenericEntity(genericEntityType1);
		assertTrue(grid.hasAnyGenericEntityContext());
		assertArrayEqualsIgnoresOrder(new int[] { 6789, 1234 }, grid.getGenericEntityIDs(genericEntityType5));
	}

	@Test
	public void testClearGenericEntityWithNonMatchingTypeIsNoOp() throws Exception {
		grid.setGenericEntityIDs(genericEntityType1, new int[] { 1234, 6789 });
		grid.clearGenericEntity(genericEntityType5);
		assertTrue(grid.hasAnyGenericEntityContext());
		assertArrayEqualsIgnoresOrder(new int[] { 6789, 1234 }, grid.getGenericEntityIDs(genericEntityType1));
	}

	@Test
	public void testConstructorWithNullTemplateHappyCase() throws Exception {
		new GridImpl(grid, null, null, null);
	}

}
