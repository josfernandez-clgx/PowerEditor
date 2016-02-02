package com.mindbox.pe.client.common.grid;

import java.util.Date;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IntegerRange;

public class AbstractGridTableModelTest extends AbstractTestWithTestConfig {

	private TestGridTableModel tableModel;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(AbstractGridTableModelTest.class.getName());
		suite.addTestSuite(AbstractGridTableModelTest.class);
		return suite;
	}

	public AbstractGridTableModelTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		GridTemplate template = ObjectMother.attachGridTemplateColumn(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				1);
		tableModel = new TestGridTableModel();
		tableModel.gridTemplate = template;
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		tableModel = null;
		super.tearDown();
	}

	public void testHasNonEmptyDepedentCellPositiveCase() throws Exception {
		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(ObjectMother.createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		tableModel.setRowCount(1);
		tableModel.setValueAt(ObjectMother.createString(), 0, 0);
		tableModel.setValueAt(ObjectMother.createString(), 0, 1);

		assertTrue(tableModel.hasNonEmptyDepedentCell(0, tableModel.gridTemplate.getColumn(1).getName()));
	}

	public void testHasNonEmptyDepedentCellNegativeCase() throws Exception {
		tableModel.setColumnCount(1);
		assertFalse(tableModel.hasNonEmptyDepedentCell(0, tableModel.gridTemplate.getColumn(1).getName()));

		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(ObjectMother.createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		tableModel.setRowCount(1);
		tableModel.setValueAt(ObjectMother.createString(), 0, 0);
		tableModel.setValueAt(null, 0, 1);
		assertFalse(tableModel.hasNonEmptyDepedentCell(0, tableModel.gridTemplate.getColumn(1).getName()));

		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		tableModel.setValueAt(ObjectMother.createString(), 0, 1);
		assertFalse(tableModel.hasNonEmptyDepedentCell(0, tableModel.gridTemplate.getColumn(1).getName()));
	}

	public void testGetNonEmptyDependentColumnIDsWithNoApplicableCols() throws Exception {
		assertTrue(tableModel.getNonEmptyDependentColumnIDs(0, tableModel.gridTemplate.getColumn(1).getName()).isEmpty());

		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(ObjectMother.createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		tableModel.setRowCount(1);
		tableModel.setValueAt(ObjectMother.createString(), 0, 0);
		tableModel.setValueAt(null, 0, 1);

		assertTrue(tableModel.getNonEmptyDependentColumnIDs(0, tableModel.gridTemplate.getColumn(1).getName()).isEmpty());
	}

	public void testGetNonEmptyDependentColumnIDsWithApplicableCols() throws Exception {
		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(ObjectMother.createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		tableModel.setRowCount(1);
		tableModel.setValueAt(ObjectMother.createString(), 0, 0);
		tableModel.setValueAt(ObjectMother.createString(), 0, 1);

		assertEquals(1, tableModel.getNonEmptyDependentColumnIDs(0, tableModel.gridTemplate.getColumn(1).getName()).size());
		assertEquals(2, tableModel.getNonEmptyDependentColumnIDs(0, tableModel.gridTemplate.getColumn(1).getName()).get(0).intValue());
	}

	public void testClearNonEmptyDependentColumnsWithNoApplicableCols() throws Exception {
		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(ObjectMother.createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		String value11 = ObjectMother.createString();
		tableModel.setRowCount(1);
		tableModel.setValueAt(value11, 0, 0);
		tableModel.setValueAt(null, 0, 1);

		tableModel.clearNonEmptyDependentColumns(0, tableModel.gridTemplate.getColumn(1).getName());
		assertEquals(value11, tableModel.getValueAt(0, 0));
		assertNull(tableModel.getValueAt(0,1));
	}

	public void testClearNonEmptyDependentColumnsWithApplicableCols() throws Exception {
		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(ObjectMother.createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		String value11 = ObjectMother.createString();
		String value12 = ObjectMother.createString();
		tableModel.setRowCount(1);
		tableModel.setValueAt(value11, 0, 0);
		tableModel.setValueAt(value12, 0, 1);

		tableModel.clearNonEmptyDependentColumns(0, tableModel.gridTemplate.getColumn(1).getName());
		assertEquals(value11, tableModel.getValueAt(0, 0));
		assertNull(tableModel.getValueAt(0,1));
	}

	public void testSetAndValidateValueAtTakesNull() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("String");
		columnDataSpecDigest.setIsBlankAllowed(true);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("str", 0, 0);
		tableModel.setAndValidateValueAt(null, 0, 0);
		assertNull(tableModel.getValueAt(0, 0));
	}

	public void testSetValueAtHappyCaseForSingleSelectEntityColumn() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createEntityColumnDataSpecDigest("product", true, false, false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		GenericEntity entity = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));

		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(entity, 0, 0);
		assertEquals(entity, tableModel.getValueAt(0, 0));
	}

	public void testSetValueAtHappyCaseForSingleSelectCategoryColumn() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createEntityColumnDataSpecDigest("product", false, true, false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		GenericCategory category = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));

		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(category, 0, 0);
		assertEquals(category, tableModel.getValueAt(0, 0));
	}

	public void testSetValueAtForEnumListSingleSelectAcceptsEnumValue() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("EnumList");
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		EnumValue enumValue = ObjectMother.createEnumValue();
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(enumValue, 0, 0);
		assertEquals(enumValue, tableModel.getValueAt(0, 0));
	}

	public void testSetValueAtForEnumListSingleSelectAcceptsString() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("EnumList");
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		String str = "some value";
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(str, 0, 0);
		assertEquals(str, tableModel.getValueAt(0, 0));
	}

	public void testSetValueAtForEnumListMultiSelectAcceptsEnumValuesOfEnumValue() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("EnumList");
		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		EnumValues<EnumValue> enumValues = new EnumValues<EnumValue>();
		enumValues.add(ObjectMother.createEnumValue());
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(enumValues, 0, 0);
		assertEquals(enumValues, tableModel.getValueAt(0, 0));
	}

	public void testSetValueAtForEnumListMultiSelectAcceptsEnumValuesOfString() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("EnumList");
		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		EnumValues<String> enumValues = new EnumValues<String>();
		String str = "some value";
		enumValues.add(str);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(enumValues, 0, 0);
		assertEquals(enumValues, tableModel.getValueAt(0, 0));
	}

	public void testSetValueAtForFloatColumnSetsDoubleObject() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("Currency");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("123.45", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof Double);
		assertEquals(new Double(123.45), tableModel.getValueAt(0, 0));
	}

	public void testSetValueAtForFloatColumnLargeValue() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("Float");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("15000000.0", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof Double);
		assertEquals(new Double(15000000.0), tableModel.getValueAt(0, 0));
	}

	public void testSetValueAtForCurrencyRangeColumnSetsIRangeObject() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("CurrencyRange");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("[1234.51-56789.01]", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof FloatRange);
		assertEquals(new Double(1234.51), ((FloatRange) tableModel.getValueAt(0, 0)).getFloor());
		assertEquals(new Double(56789.01), ((FloatRange) tableModel.getValueAt(0, 0)).getCeiling());
	}

	public void testSetValueAtForFloatRangeColumnSetsIRangeObject() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("FloatRange");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("[123.45-567.89]", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof FloatRange);
		assertEquals(new Double(123.45), ((FloatRange) tableModel.getValueAt(0, 0)).getFloor());
		assertEquals(new Double(567.89), ((FloatRange) tableModel.getValueAt(0, 0)).getCeiling());
	}

	public void testSetValueAtForIntegerRangeColumnSetsIRangeObject() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType("IntegerRange");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("[12345-56789]", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof IntegerRange);
		assertEquals(12345, ((IntegerRange) tableModel.getValueAt(0, 0)).getFloor().intValue());
		assertEquals(56789, ((IntegerRange) tableModel.getValueAt(0, 0)).getCeiling().intValue());
	}

	public void testGetBooleanColumnClassBlankAllowed() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		testGetColumnClass(String.class, columnDataSpecDigest);
	}

	public void testGetBooleanColumnClassBlankNotAllowed() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		columnDataSpecDigest.setIsBlankAllowed(false);
		testGetColumnClass(Boolean.class, columnDataSpecDigest);
	}

	public void testGetIntegerRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);
		testGetColumnClass(IntegerRange.class, columnDataSpecDigest);
	}

	public void testGetIntegerColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_INTEGER);
		testGetColumnClass(Integer.class, columnDataSpecDigest);
	}

	public void testGetFloatRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE);
		testGetColumnClass(FloatRange.class, columnDataSpecDigest);
	}

	public void testGetFloatColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_FLOAT);
		testGetColumnClass(Double.class, columnDataSpecDigest);
	}

	public void testGetCurrencyRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE);
		testGetColumnClass(FloatRange.class, columnDataSpecDigest);
	}

	public void testGetCurrencyColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_CURRENCY);
		testGetColumnClass(Double.class, columnDataSpecDigest);
	}

	public void testGetEnumColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		testGetColumnClass(EnumValues.class, columnDataSpecDigest);
	}

	public void testGetDateRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_DATE_RANGE);
		testGetColumnClass(String.class, columnDataSpecDigest);
	}

	public void testGetDateTimeRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE);
		testGetColumnClass(String.class, columnDataSpecDigest);
	}

	public void testGetDateColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_DATE);
		testGetColumnClass(Date.class, columnDataSpecDigest);
	}

	public void testGetRuleIDClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		testGetColumnClass(Long.class, columnDataSpecDigest);
	}

	public void testDefaultColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_STRING);
		testGetColumnClass(String.class, columnDataSpecDigest);
	}

	public void testAddRow() throws Exception {
		assertEquals(0, tableModel.getRowCount());
		assertFalse(tableModel.isDirty());

		tableModel.addRow(-1);

		assertEquals(1, tableModel.getRowCount());
		assertTrue(tableModel.isDirty());
	}

	private void testGetColumnClass(Class<?> expectedReturnType, ColumnDataSpecDigest columnDataSpecDigest) {
		if (columnDataSpecDigest == null) throw new NullPointerException("columnDataSpecDigest cannot be null");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		if (expectedReturnType == null) {
			assertNull(tableModel.getColumnClass(0).getName());
		}
		else {
			assertEquals(expectedReturnType.getName(), tableModel.getColumnClass(0).getName());
		}
	}

	private static class TestGridTableModel extends AbstractGridTableModel<GridTemplate> {
		int templateMaxRow = 1;
		GridTemplate gridTemplate;

		TestGridTableModel() {
		}

		protected ColumnDataSpecDigest getColumnDataSpecDigest(int columnNo) {
			return gridTemplate.getColumn(columnNo).getColumnDataSpecDigest();
		}

		protected void setColumnDataSpecDigest(int columnNo, ColumnDataSpecDigest digest) {
			gridTemplate.getColumn(columnNo).setDataSpecDigest(digest);
		}

		protected int getTemplateMaxRow() {
			return templateMaxRow;
		}

		protected int getTemplateColumnCount() {
			return gridTemplate.getColumnCount();
		}

		@Override
		protected GridTemplate getTemplate() {
			return gridTemplate;
		}
	}

}
