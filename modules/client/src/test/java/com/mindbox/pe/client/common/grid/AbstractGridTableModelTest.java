package com.mindbox.pe.client.common.grid;

import static com.mindbox.pe.client.ClientTestObjectMother.attachGridTemplateColumn;
import static com.mindbox.pe.client.ClientTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.client.ClientTestObjectMother.createEntityColumnDataSpecDigest;
import static com.mindbox.pe.client.ClientTestObjectMother.createEnumValue;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static com.mindbox.pe.client.ClientTestObjectMother.createGridTemplate;
import static com.mindbox.pe.client.ClientTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;

public class AbstractGridTableModelTest extends AbstractClientTestBase {

	private static class TestGridTableModel extends AbstractGridTableModel<GridTemplate> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8271839912966917302L;

		private int templateMaxRow = 1;
		private GridTemplate gridTemplate;

		TestGridTableModel() {
		}

		protected ColumnDataSpecDigest getColumnDataSpecDigest(int columnNo) {
			return gridTemplate.getColumn(columnNo).getColumnDataSpecDigest();
		}

		@Override
		protected GridTemplate getTemplate() {
			return gridTemplate;
		}

		protected int getTemplateColumnCount() {
			return gridTemplate.getColumnCount();
		}

		protected int getTemplateMaxRow() {
			return templateMaxRow;
		}

		protected void setColumnDataSpecDigest(int columnNo, ColumnDataSpecDigest digest) {
			gridTemplate.getColumn(columnNo).setDataSpecDigest(digest);
		}
	}

	private TestGridTableModel tableModel;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		GridTemplate template = attachGridTemplateColumn(createGridTemplate(TemplateUsageType.getAllInstances()[0]), 1);
		tableModel = new TestGridTableModel();
		tableModel.gridTemplate = template;
	}

	@After
	public void tearDown() throws Exception {
		tableModel = null;
		super.tearDown();
	}

	@Test
	public void testAddRow() throws Exception {
		assertEquals(0, tableModel.getRowCount());
		assertFalse(tableModel.isDirty());

		tableModel.addRow(-1);

		assertEquals(1, tableModel.getRowCount());
		assertTrue(tableModel.isDirty());
	}

	@Test
	public void testClearNonEmptyDependentColumnsWithApplicableCols() throws Exception {
		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		String value11 = createString();
		String value12 = createString();
		tableModel.setRowCount(1);
		tableModel.setValueAt(value11, 0, 0);
		tableModel.setValueAt(value12, 0, 1);

		tableModel.clearNonEmptyDependentColumns(0, tableModel.gridTemplate.getColumn(1).getName());
		assertEquals(value11, tableModel.getValueAt(0, 0));
		assertNull(tableModel.getValueAt(0, 1));
	}

	@Test
	public void testClearNonEmptyDependentColumnsWithNoApplicableCols() throws Exception {
		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		String value11 = createString();
		tableModel.setRowCount(1);
		tableModel.setValueAt(value11, 0, 0);
		tableModel.setValueAt(null, 0, 1);

		tableModel.clearNonEmptyDependentColumns(0, tableModel.gridTemplate.getColumn(1).getName());
		assertEquals(value11, tableModel.getValueAt(0, 0));
		assertNull(tableModel.getValueAt(0, 1));
	}

	@Test
	public void testDefaultColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_STRING);
		testGetColumnClass(String.class, columnDataSpecDigest);
	}

	@Test
	public void testGetBooleanColumnClassBlankAllowed() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		testGetColumnClass(String.class, columnDataSpecDigest);
	}

	@Test
	public void testGetBooleanColumnClassBlankNotAllowed() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		columnDataSpecDigest.setIsBlankAllowed(false);
		testGetColumnClass(Boolean.class, columnDataSpecDigest);
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

	@Test
	public void testGetCurrencyColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_CURRENCY);
		testGetColumnClass(Double.class, columnDataSpecDigest);
	}

	@Test
	public void testGetCurrencyRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE);
		testGetColumnClass(FloatRange.class, columnDataSpecDigest);
	}

	@Test
	public void testGetDateColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_DATE);
		testGetColumnClass(Date.class, columnDataSpecDigest);
	}

	@Test
	public void testGetDateRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_DATE_RANGE);
		testGetColumnClass(String.class, columnDataSpecDigest);
	}

	@Test
	public void testGetDateTimeRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE);
		testGetColumnClass(String.class, columnDataSpecDigest);
	}

	@Test
	public void testGetEnumColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		testGetColumnClass(EnumValues.class, columnDataSpecDigest);
	}

	@Test
	public void testGetFloatColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_FLOAT);
		testGetColumnClass(Double.class, columnDataSpecDigest);
	}

	@Test
	public void testGetFloatRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE);
		testGetColumnClass(FloatRange.class, columnDataSpecDigest);
	}

	@Test
	public void testGetIntegerColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_INTEGER);
		testGetColumnClass(Integer.class, columnDataSpecDigest);
	}

	@Test
	public void testGetIntegerRangeColumnClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);
		testGetColumnClass(IntegerRange.class, columnDataSpecDigest);
	}

	@Test
	public void testGetNonEmptyDependentColumnIDsWithApplicableCols() throws Exception {
		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		tableModel.setRowCount(1);
		tableModel.setValueAt(createString(), 0, 0);
		tableModel.setValueAt(createString(), 0, 1);

		assertEquals(1, tableModel.getNonEmptyDependentColumnIDs(0, tableModel.gridTemplate.getColumn(1).getName()).size());
		assertEquals(2, tableModel.getNonEmptyDependentColumnIDs(0, tableModel.gridTemplate.getColumn(1).getName()).get(0).intValue());
	}

	@Test
	public void testGetNonEmptyDependentColumnIDsWithNoApplicableCols() throws Exception {
		assertTrue(tableModel.getNonEmptyDependentColumnIDs(0, tableModel.gridTemplate.getColumn(1).getName()).isEmpty());

		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		tableModel.setRowCount(1);
		tableModel.setValueAt(createString(), 0, 0);
		tableModel.setValueAt(null, 0, 1);

		assertTrue(tableModel.getNonEmptyDependentColumnIDs(0, tableModel.gridTemplate.getColumn(1).getName()).isEmpty());
	}

	@Test
	public void testGetRuleIDClass() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		testGetColumnClass(Long.class, columnDataSpecDigest);
	}

	@Test
	public void testHasNonEmptyDepedentCellNegativeCase() throws Exception {
		tableModel.setColumnCount(1);
		assertFalse(tableModel.hasNonEmptyDepedentCell(0, tableModel.gridTemplate.getColumn(1).getName()));

		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		tableModel.setRowCount(1);
		tableModel.setValueAt(createString(), 0, 0);
		tableModel.setValueAt(null, 0, 1);
		assertFalse(tableModel.hasNonEmptyDepedentCell(0, tableModel.gridTemplate.getColumn(1).getName()));

		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		tableModel.setValueAt(createString(), 0, 1);
		assertFalse(tableModel.hasNonEmptyDepedentCell(0, tableModel.gridTemplate.getColumn(1).getName()));
	}

	@Test
	public void testHasNonEmptyDepedentCellPositiveCase() throws Exception {
		tableModel.setColumnCount(2);
		tableModel.gridTemplate.addColumn(createGridTemplateColumn(2, tableModel.gridTemplate.getUsageType()));

		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);

		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSelectorColumnName(tableModel.gridTemplate.getColumn(1).getName());
		tableModel.setColumnDataSpecDigest(2, columnDataSpecDigest);

		tableModel.setRowCount(1);
		tableModel.setValueAt(createString(), 0, 0);
		tableModel.setValueAt(createString(), 0, 1);

		assertTrue(tableModel.hasNonEmptyDepedentCell(0, tableModel.gridTemplate.getColumn(1).getName()));
	}

	@Test
	public void testSetAndValidateValueAtTakesNull() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType("String");
		columnDataSpecDigest.setIsBlankAllowed(true);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("str", 0, 0);
		tableModel.setAndValidateValueAt(null, 0, 0);
		assertNull(tableModel.getValueAt(0, 0));
	}

	@Test
	public void testSetValueAtForCurrencyRangeColumnSetsIRangeObject() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType("CurrencyRange");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("[1234.51-56789.01]", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof FloatRange);
		assertEquals(new Double(1234.51), ((FloatRange) tableModel.getValueAt(0, 0)).getFloor());
		assertEquals(new Double(56789.01), ((FloatRange) tableModel.getValueAt(0, 0)).getCeiling());
	}

	@Test
	public void testSetValueAtForEnumListMultiSelectAcceptsEnumValuesOfEnumValue() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType("EnumList");
		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		EnumValues<EnumValue> enumValues = new EnumValues<EnumValue>();
		enumValues.add(createEnumValue());
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(enumValues, 0, 0);
		assertEquals(enumValues, tableModel.getValueAt(0, 0));
	}

	@Test
	public void testSetValueAtForEnumListMultiSelectAcceptsEnumValuesOfString() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
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

	@Test
	public void testSetValueAtForEnumListSingleSelectAcceptsEnumValue() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType("EnumList");
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		EnumValue enumValue = createEnumValue();
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(enumValue, 0, 0);
		assertEquals(enumValue, tableModel.getValueAt(0, 0));
	}

	@Test
	public void testSetValueAtForEnumListSingleSelectAcceptsString() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType("EnumList");
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		String str = "some value";
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(str, 0, 0);
		assertEquals(str, tableModel.getValueAt(0, 0));
	}

	@Test
	public void testSetValueAtForFloatColumnLargeValue() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType("Float");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("15000000.0", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof Double);
		assertEquals(new Double(15000000.0), tableModel.getValueAt(0, 0));
	}

	@Test
	public void testSetValueAtForFloatColumnSetsDoubleObject() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType("Currency");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("123.45", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof Double);
		assertEquals(new Double(123.45), tableModel.getValueAt(0, 0));
	}

	@Test
	public void testSetValueAtForFloatRangeColumnSetsIRangeObject() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType("FloatRange");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("[123.45-567.89]", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof FloatRange);
		assertEquals(new Double(123.45), ((FloatRange) tableModel.getValueAt(0, 0)).getFloor());
		assertEquals(new Double(567.89), ((FloatRange) tableModel.getValueAt(0, 0)).getCeiling());
	}

	@Test
	public void testSetValueAtForIntegerRangeColumnSetsIRangeObject() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType("IntegerRange");
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt("[12345-56789]", 0, 0);
		assertTrue(tableModel.getValueAt(0, 0) instanceof IntegerRange);
		assertEquals(12345, ((IntegerRange) tableModel.getValueAt(0, 0)).getFloor().intValue());
		assertEquals(56789, ((IntegerRange) tableModel.getValueAt(0, 0)).getCeiling().intValue());
	}

	@Test
	public void testSetValueAtHappyCaseForSingleSelectCategoryColumn() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createEntityColumnDataSpecDigest(entityType1.getName(), false, true, false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		GenericCategory category = createGenericCategory(entityType1);

		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(category, 0, 0);
		assertEquals(category, tableModel.getValueAt(0, 0));
	}

	@Test
	public void testSetValueAtHappyCaseForSingleSelectEntityColumn() throws Exception {
		ColumnDataSpecDigest columnDataSpecDigest = createEntityColumnDataSpecDigest(entityType1.getName(), true, false, false);
		tableModel.setColumnDataSpecDigest(1, columnDataSpecDigest);
		GenericEntity entity = createGenericEntity(entityType1);

		tableModel.setColumnCount(1);
		tableModel.addRow(-1);
		tableModel.setValueAt(entity, 0, 0);
		assertEquals(entity, tableModel.getValueAt(0, 0));
	}

}
