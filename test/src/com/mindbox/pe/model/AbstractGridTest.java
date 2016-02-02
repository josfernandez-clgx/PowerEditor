package com.mindbox.pe.model;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.exceptions.InvalidDataException;

/**
 * Unit tests for {@link com.mindbox.pe.model.AbstractGrid}.
 * 
 * @author Geneho Kim
 * 
 */
public class AbstractGridTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractGridTest Tests");
		suite.addTestSuite(AbstractGridTest.class);
		return suite;
	}

	private static class GridImpl extends AbstractGrid<GridTemplateColumn> {
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
			if (cellValues == null)
				return abstractgrid.isEmpty();
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
			return (cellValues == null ? defaultValue : cellValues[i-1][j-1]);
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

	public AbstractGridTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		grid = new GridImpl(1, 100);
		grid.setTemplate(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid.setStatusChangeDate(getDate(2006, 5, 31)); // set to a date other than now
	}

	protected void tearDown() throws Exception {
		grid = null;
		config.resetConfiguration();
		super.tearDown();
	}

	public void testEqualsForAbstractGridWithNullGridThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(grid, "equals", new Class[] { AbstractGrid.class }, new Object[] { null });
	}

	public void testEqualsForAbstractGridWithDifferentTemplateIDReturnsFalse() throws Exception {
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID() + 1);
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	public void testEqualsForAbstractGridWithDifferentCellValuesReturnsFalse() throws Exception {
		ObjectMother.attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.cellValues = new String[][] { new String[] { "value1" } };
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid.cellValues = new String[][] { new String[] { "value2" } };
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	public void testEqualsForAbstractGridWithDifferentEffectiveDateReturnsFalse() throws Exception {
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid2.setEffectiveDate(ObjectMother.createDateSynonym());
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	public void testEqualsForAbstractGridWithDifferentExpirationDateReturnsFalse() throws Exception {
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid2.setExpirationDate(ObjectMother.createDateSynonym());
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	public void testEqualsForAbstractGridWithDifferentStatusReturnsFalse() throws Exception {
		grid.setStatus("draft");
		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid2.setStatus(grid.getStatus() + "-new");
		assertFalse(grid.equals(grid2));
		assertFalse(grid2.equals(grid));
	}

	public void testEqualsForAbstractGridHappyCase() throws Exception {
		ObjectMother.attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.cellValues = new String[][] { new String[] { "value1" } };
		grid.setStatus("draft");
		grid.setEffectiveDate(ObjectMother.createDateSynonym());
		grid.setExpirationDate(ObjectMother.createDateSynonym());

		GridImpl grid2 = new GridImpl(2, grid.getTemplateID());
		grid2.setStatus(grid.getStatus());
		grid2.cellValues = new String[][] { new String[] { "value1" } };
		grid2.setEffectiveDate(grid.getEffectiveDate());
		grid2.setExpirationDate(grid.getExpirationDate());
		assertTrue(grid.equals(grid2));
		assertTrue(grid2.equals(grid));
	}

	public void testAddGenericCategoryIDsWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(grid, "addGenericCategoryIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] { null,
				new int[] { 1 } });
	}

	public void testAddGenericCategoryIDsWithNullOrEmptyIDsThrowsIllegalArgumentException() throws Exception {
		assertThrowsException(grid, "addGenericCategoryIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] {
				GenericEntityType.forName("product"), new int[0] }, IllegalArgumentException.class);
		assertThrowsException(grid, "addGenericCategoryIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] {
				GenericEntityType.forName("product"), null }, IllegalArgumentException.class);
	}

	public void testAddGenericCategoryIDsAddsToExistingContext() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });

		grid.addGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 300, 400 });
		assertEquals(new int[] { 100, 200, 300, 400 }, grid.getGenericCategoryIDs(GenericEntityType.forName("product")));
	}

	public void testAddGenericCategoryIDsAddsNewContext() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 100, 200 });
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 11, 22 });

		grid.addGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 300, 400 });
		assertEquals(new int[] { 300, 400 }, grid.getGenericCategoryIDs(GenericEntityType.forName("product")));
	}

	public void testAddGenericCategoryIDsWithDuplicateIDsIgnored() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });

		grid.addGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 200, 400, 20 });
		assertEquals(new int[] { 20, 100, 200, 400 }, grid.getGenericCategoryIDs(GenericEntityType.forName("product")));
	}

	public void testAddGenericEntityIDsWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(grid, "addGenericEntityIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] { null,
				new int[] { 1 } });
	}

	public void testAddGenericEntityIDsWithNullOrEmptyIDsThrowsIllegalArgumentException() throws Exception {
		assertThrowsException(grid, "addGenericEntityIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] {
				GenericEntityType.forName("product"), new int[0] }, IllegalArgumentException.class);
		assertThrowsException(grid, "addGenericEntityIDs", new Class[] { GenericEntityType.class, int[].class }, new Object[] {
				GenericEntityType.forName("product"), (int[]) null }, IllegalArgumentException.class);
	}

	public void testAddGenericEntityIDsAddsToExistingContext() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });

		grid.addGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 300, 400 });
		assertEquals(new int[] { 100, 200, 300, 400 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
	}

	public void testAddGenericEntityIDsAddsNewContext() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 100, 200 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 11, 22 });

		grid.addGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 300, 400 });
		assertEquals(new int[] { 300, 400 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
	}

	public void testAddGenericEntityIDsWithDuplicateIDsIgnored() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });

		grid.addGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200, 400 });
		assertEquals(new int[] { 100, 200, 400 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
	}

	public void testInitForGridTemplateDateDateWithNullGridThrowsNullPointerException() throws Exception {
		try {
			new GridImpl(null, new GridTemplate(2, "test2", TemplateUsageType.getAllInstances()[0]), null, null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testInitForGridTemplateDateDateCreatesCopyExceptIDTemplateAndDates() throws Exception {
		DateSynonym dateSynonym1 = new DateSynonym(100, "date1", "", new Date());
		DateSynonym dateSynonym2 = new DateSynonym(100, "date1", "", new Date());
		AbstractGrid<GridTemplateColumn> grid2 = new GridImpl(grid, new GridTemplate(2, "test2", TemplateUsageType.getAllInstances()[0]), dateSynonym1, dateSynonym2);
		assertEquals(Persistent.UNASSIGNED_ID, grid2.getID());
		assertEquals(2, grid2.getTemplateID());
		assertEquals(grid2.getEffectiveDate().getDate(), dateSynonym1.getDate());
		assertEquals(grid2.getEffectiveDate().getDate(), dateSynonym1.getDate());
		assertEquals(grid.getStatus(), grid2.getStatus());
		assertEquals(grid.getStatusChangeDate(), grid2.getStatusChangeDate());
		assertEquals(grid.getComments(), grid2.getComments());
		assertEquals(grid.getNumRows(), grid2.getNumRows());
	}

	public void testInitForGridWithNullGridThrowsNullPointerException() throws Exception {
		try {
			new GridImpl(null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

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

	public void testCopyEntireContextWithEmptyContextClearsContext() throws Exception {
		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 1, 3, 2 });
		grid2.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 20, 10 });

		grid2.copyEntireContext(grid);
		assertTrue(grid2.isContextEmpty());
	}

	public void testCopyEntireContextWithEmptyEntityContextClearsEntityContext() throws Exception {
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 1111, 2222 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 1, 3, 2 });
		grid2.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 20, 10 });

		grid2.copyEntireContext(grid);
		assertTrue(grid2.hasAnyGenericCategoryContext());
		assertFalse(grid2.hasAnyGenericEntityContext());
	}

	public void testCopyEntireContextWithEmptyCategoryContextClearsCategoryContext() throws Exception {
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 1111, 2222 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 1, 3, 2 });
		grid2.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 20, 10 });

		grid2.copyEntireContext(grid);
		assertFalse(grid2.hasAnyGenericCategoryContext());
		assertTrue(grid2.hasAnyGenericEntityContext());
	}

	public void testCopyEntireContextWithValidContextOnEmptyContextSucceeds() throws Exception {
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 1111, 2222 });
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 444, 555 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.copyEntireContext(grid);

		assertTrue(grid2.hasSameContext(grid));
	}

	public void testCopyEntireContextWithValidContextOverwritesExistingContext() throws Exception {
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 1111, 2222 });
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 444, 555 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 1245 });
		grid2.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 444, 555, 888, 999 });

		grid2.copyEntireContext(grid);

		assertTrue(grid2.hasSameContext(grid));
	}

	public void testHasSameContextWithNullThrowsNullPointerException() throws Exception {
		try {
			grid.hasSameContext(null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testExtractGuidelineContextWithEmptyContextReturnsEmptyArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		assertEquals(0, grid.extractGuidelineContext().length);
	}

	public void testExtractGuidelineContextWithValidGridReturnsCorrectArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 11, 22, 33, 44 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("program"), new int[] { 1000 });

		GuidelineContext[] context = grid.extractGuidelineContext();
		assertEquals(3, context.length);
		for (int i = 0; i < context.length; i++) {
			if (context[i].hasCategoryContext() && context[i].getGenericCategoryType() == GenericEntityType.forName("product").getCategoryType()) {
				assertEquals(new int[] { 100, 200 }, context[i].getIDs());
			}
			else if (context[i].hasCategoryContext() && context[i].getGenericCategoryType() == GenericEntityType.forName("program").getCategoryType()) {
				assertEquals(new int[] { 1000 }, context[i].getIDs());
			}
			else {
				assertEquals(GenericEntityType.forName("channel"), context[i].getGenericEntityType());
				assertEquals(new int[] { 33, 22, 11, 44 }, context[i].getIDs());
			}
		}
	}

	public void testHasSameContextWithEmptyContextReturnsTrue() throws Exception {
		GridImpl grid2 = new GridImpl(2, 100);
		assertTrue(grid.hasSameContext(grid2));
	}

	public void testHasSameContextWithCategoryTypeMismatchReturnsFalse() throws Exception {
		grid.addGenericCategoryID(GenericEntityType.forName("product"), 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericCategoryID(GenericEntityType.forName("channel"), 222);

		assertFalse(grid.hasSameContext(grid2));
	}

	public void testHasSameContextWithEntityTypeMismatchReturnsFalse() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("product"), 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericEntityID(GenericEntityType.forName("channel"), 222);

		assertFalse(grid.hasSameContext(grid2));
	}

	public void testHasSameContextWithCategoryIDMismatchReturnsFalse() throws Exception {
		grid.addGenericCategoryID(GenericEntityType.forName("channel"), 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericCategoryID(GenericEntityType.forName("channel"), 122);

		assertFalse(grid.hasSameContext(grid2));
	}

	public void testHasSameContextWithEntityIDMismatchReturnsFalse() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericEntityID(GenericEntityType.forName("channel"), 122);

		assertFalse(grid.hasSameContext(grid2));
	}

	public void testHasSameContextWithCategoryOnEntityReturnsFalse() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericCategoryID(GenericEntityType.forName("channel"), 222);

		assertFalse(grid.hasSameContext(grid2));
	}

	public void testHasSameContextWithEntityOnCategoryReturnsFalse() throws Exception {
		grid.addGenericCategoryID(GenericEntityType.forName("channel"), 222);

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.addGenericEntityID(GenericEntityType.forName("channel"), 222);

		assertFalse(grid.hasSameContext(grid2));
	}

	public void testHasSameContextWithSameContextReturnsTrue() throws Exception {
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 1, 2, 3 });
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 10, 20 });

		GridImpl grid2 = new GridImpl(2, 100);
		grid2.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 1, 3, 2 });
		grid2.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 20, 10 });

		assertTrue(grid.hasSameContext(grid2));
	}

	public void testGetGenericEntityIDsWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.getGenericEntityIDs(null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testGetGenericEntityIDsWithEmptyGridReturnsEmptyIntArray() throws Exception {
		assertEquals(0, grid.getGenericEntityIDs(GenericEntityType.forName("channel")).length);
	}

	public void testGetGenericEntityIDsWithNotFoundEntityTypeReturnsEmptyIntArray() throws Exception {
		grid.addGenericCategoryID(GenericEntityType.forName("channel"), 100);
		grid.addGenericEntityID(GenericEntityType.forName("product"), 200);
		assertEquals(0, grid.getGenericEntityIDs(GenericEntityType.forName("channel")).length);
	}

	public void testAddGenericCategoryIDWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.addGenericCategoryID(null, 1000);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testAddGenericCategoryIDWithNonPositiveIDThrowsIllegalArgumentException() throws Exception {
		try {
			grid.addGenericCategoryID(GenericEntityType.forName("channel"), 0);
			fail("Excepted IllegalArgumentException not thrown with zero");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
		try {
			grid.addGenericCategoryID(GenericEntityType.forName("channel"), -1);
			fail("Excepted IllegalArgumentException not thrown with -1");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}

	public void testAddGenericEntityIDWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.addGenericEntityID(null, 1000);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testAddGenericEntityIDWithNonPositiveIDThrowsIllegalArgumentException() throws Exception {
		try {
			grid.addGenericEntityID(GenericEntityType.forName("channel"), 0);
			fail("Excepted IllegalArgumentException not thrown with zero");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
		try {
			grid.addGenericEntityID(GenericEntityType.forName("channel"), -1);
			fail("Excepted IllegalArgumentException not thrown with -1");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}

	public void testAddGenericEntityIDWithSameTypeAddsId() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1001);
		assertEquals(new int[] { 1000, 1001 }, grid.getGenericEntityIDs(GenericEntityType.forName("channel")));
	}

	public void testAddGenericEntityIDWithSameValuesIsNoOp() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		assertEquals(new int[] { 1000 }, grid.getGenericEntityIDs(GenericEntityType.forName("channel")));
	}

	public void testAddGenericEntityIDSupportMultipleEntityTypes() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 100);
		grid.addGenericEntityID(GenericEntityType.forName("branch"), 400);
		grid.addGenericEntityID(GenericEntityType.forName("branch"), 401);
		grid.addGenericEntityID(GenericEntityType.forName("branch"), 402);
		assertEquals(new int[] { 100 }, grid.getGenericEntityIDs(GenericEntityType.forName("channel")));
		assertEquals(new int[] { 400, 401, 402 }, grid.getGenericEntityIDs(GenericEntityType.forName("branch")));
	}

	public void testRemoveGenericEntityIDWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.removeGenericEntityID(null, 1000);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testRemoveGenericEntityIDWithNonPositiveIDThrowsIllegalArgumentException() throws Exception {
		try {
			grid.removeGenericEntityID(GenericEntityType.forName("channel"), 0);
			fail("Excepted IllegalArgumentException not thrown with zero");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
		try {
			grid.removeGenericEntityID(GenericEntityType.forName("channel"), -1);
			fail("Excepted IllegalArgumentException not thrown with -1");
		}
		catch (IllegalArgumentException ex) {
			// success
		}
	}

	public void testRemoveGenericEntityIDSameIDIsNoOp() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.removeGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.removeGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.removeGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.removeGenericEntityID(GenericEntityType.forName("channel"), 1000);
		assertEquals(0, grid.getGenericEntityIDs(GenericEntityType.forName("channel")).length);
	}

	public void testRemoveGenericEntityIDSupportsMultipleEntityTypes() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.addGenericEntityID(GenericEntityType.forName("branch"), 400);
		grid.removeGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.removeGenericEntityID(GenericEntityType.forName("branch"), 400);
		assertEquals(0, grid.getGenericEntityIDs(GenericEntityType.forName("channel")).length);
		assertEquals(0, grid.getGenericEntityIDs(GenericEntityType.forName("branch")).length);
	}

	public void testRemoveGenericEntityIDWithSameTypeRemovesIDs() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("branch"), 400);
		grid.addGenericEntityID(GenericEntityType.forName("branch"), 401);
		grid.addGenericEntityID(GenericEntityType.forName("branch"), 402);
		grid.removeGenericEntityID(GenericEntityType.forName("branch"), 400);
		grid.removeGenericEntityID(GenericEntityType.forName("branch"), 402);
		assertEquals(new int[] { 401 }, grid.getGenericEntityIDs(GenericEntityType.forName("branch")));
	}

	public void testGetGenericEntityTypesInUseSupportsMultipleEntityTypes() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.addGenericEntityID(GenericEntityType.forName("branch"), 400);
		grid.addGenericEntityID(GenericEntityType.forName("product"), 1000);
		grid.addGenericEntityID(GenericEntityType.forName("investor"), 400);
		grid.addGenericEntityID(GenericEntityType.forName("program"), 1000);
		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		assertEquals(5, types.length);
		assertContains(types, GenericEntityType.forName("channel"));
		assertContains(types, GenericEntityType.forName("product"));
		assertContains(types, GenericEntityType.forName("investor"));
		assertContains(types, GenericEntityType.forName("branch"));
		assertContains(types, GenericEntityType.forName("investor"));
	}

	public void testGetGenericEntityTypesInUseDoesNotReturnsEmptyListType() throws Exception {
		grid.setGenericEntityIDs( GenericEntityType.forName("product"), new int[0]);
		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		assertEquals(0, types.length);
	}

	public void testGetGenericCategoryEntityTypesInUseDoesNotReturnsEmptyListType() throws Exception {
		grid.setGenericCategoryIDs( GenericEntityType.forName("product"), new int[0]);
		GenericEntityType[] types = grid.getGenericCategoryEntityTypesInUse();
		assertEquals(0, types.length);
	}

	public void testGetGenericEntityTypesWithEmptyContextReturnsEmptyArray() throws Exception {
		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		assertEquals(0, types.length);
	}

	public void testGetGenericEntityTypesWithNotMatchingContextReturnsEmptyArray() throws Exception {
		grid.addGenericCategoryID(GenericEntityType.forName("channel"), 100);
		grid.addGenericCategoryID(GenericEntityType.forName("program"), 100);

		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		assertEquals(0, types.length);
	}

	public void testHasGenericEntityContextWithEmptyContextReturnsFalse() throws Exception {
		assertFalse(grid.hasAnyGenericEntityContext());
	}

	public void testHasGenericEntityContextWithNotMatchingContextReturnsFalse() throws Exception {
		grid.addGenericCategoryID(GenericEntityType.forName("channel"), 100);
		grid.addGenericCategoryID(GenericEntityType.forName("program"), 100);
		assertFalse(grid.hasAnyGenericEntityContext());
	}

	public void testHasGenericEntityContextWithMatchingEntityReturnsTrue() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("program"), 100);
		assertTrue(grid.hasAnyGenericEntityContext());
	}

	public void testSetGenericEntityIDsWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.setGenericEntityIDs(null, new int[] { 100 });
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testSetGenericEntityIDsWithNullIDsThrowsNullPointerException() throws Exception {
		try {
			grid.setGenericEntityIDs(GenericEntityType.forName("channel"), null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testSetGenericEntityIDsSetsEntityIDsOnEmtpyContext() throws Exception {
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1234, 6789 });
		assertEquals(new int[] { 6789, 1234 }, grid.getGenericEntityIDs(GenericEntityType.forName("channel")));
	}

	public void testSetGenericEntityIDsOverwritesEntityIDsOnExistingContext() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 4000);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1234, 6789 });
		assertEquals(new int[] { 6789, 1234 }, grid.getGenericEntityIDs(GenericEntityType.forName("channel")));
	}

	public void testMatchesGenericEntityIDsWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.matchesGenericEntityIDs(null, new int[10]);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testMatchesGenericEntityIDsWithNullIDsWithEmptyTypeReturnsTrue() throws Exception {
		assertTrue(grid.matchesGenericEntityIDs(GenericEntityType.forName("channel"), null));
	}

	public void testMatchesGenericEntityIDsWithNullIDsWithNonEmptyTypeReturnsFalse() throws Exception {
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1234 });
		assertFalse(grid.matchesGenericEntityIDs(GenericEntityType.forName("channel"), null));
	}

	public void testMatchesGenericEntityIDsWithMatchingTypeReturnsTrue() throws Exception {
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1234, 33456 });
		assertTrue(grid.matchesGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1234, 33456 }));
	}

	public void testMatchesGenericEntityIDsWithNonMatchingTypeReturnsFalse() throws Exception {
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1234, 33456 });
		assertFalse(grid.matchesGenericEntityIDs(GenericEntityType.forName("branch"), new int[] { 1234, 33456 }));
	}

	public void testClearAllGenericEntitySupportMultipleEntityTypes() throws Exception {
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 1000);
		grid.addGenericEntityID(GenericEntityType.forName("branch"), 400);
		grid.addGenericEntityID(GenericEntityType.forName("product"), 1000);
		grid.clearAllGenericEntity();
		assertFalse(grid.hasAnyGenericEntityContext());
		assertEquals(0, grid.getGenericEntityTypesInUse().length);
	}

	public void testClearAllGenericEntityDoesNotClearCategories() throws Exception {
		grid.addGenericCategoryID(GenericEntityType.forName("channel"), 100);
		grid.addGenericCategoryID(GenericEntityType.forName("program"), 100);
		grid.clearAllGenericEntity();
		assertFalse(grid.hasAnyGenericEntityContext());
	}

	public void testClearGenericEntityWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			grid.clearGenericEntity(null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testClearGenericEntityWithMatchingTypeClearsAllIDs() throws Exception {
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1234, 6789 });
		grid.clearGenericEntity(GenericEntityType.forName("channel"));
		assertFalse(grid.hasAnyGenericEntityContext());
	}

	public void testClearGenericEntityWithDoesNotClearNonMatchingTypes() throws Exception {
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1234, 6789 });
		grid.setGenericEntityIDs(GenericEntityType.forName("investor"), new int[] { 1234, 6789 });
		grid.clearGenericEntity(GenericEntityType.forName("channel"));
		assertTrue(grid.hasAnyGenericEntityContext());
		assertEquals(new int[] { 6789, 1234 }, grid.getGenericEntityIDs(GenericEntityType.forName("investor")));
	}

	public void testClearGenericEntityWithNonMatchingTypeIsNoOp() throws Exception {
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1234, 6789 });
		grid.clearGenericEntity(GenericEntityType.forName("investor"));
		assertTrue(grid.hasAnyGenericEntityContext());
		assertEquals(new int[] { 6789, 1234 }, grid.getGenericEntityIDs(GenericEntityType.forName("channel")));
	}
    
    public void testConstructorWithNullTemplateHappyCase() throws Exception {
        new GridImpl(grid, null, null, null);
    }
    
}
