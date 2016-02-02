package com.mindbox.pe.model;

import java.util.List;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.format.FloatFormatter;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;

public class ColumnDataSpecDigestTest extends AbstractTestBase {
	private ColumnDataSpecDigest colDataSpec;
	private int newPrecision;
	private int origPrecision;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(ColumnDataSpecDigestTest.class.getName());
		suite.addTestSuite(ColumnDataSpecDigestTest.class);
		return suite;
	}

	public ColumnDataSpecDigestTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		colDataSpec = new ColumnDataSpecDigest();

		newPrecision = 5;
		origPrecision = colDataSpec.getPrecision();
		assertNotEquals(newPrecision, origPrecision); // sanity check
	}

	public void testIsEnumListAndSelectorSetForPositiveCase() throws Exception {
		String columnName = "COL-" + ObjectMother.createString();
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(EnumSourceType.EXTERNAL);
		colDataSpec.setEnumSelectorColumnName(columnName);
		colDataSpec.setIsMultiSelectAllowed(false);
		assertTrue(colDataSpec.isEnumListAndSelectorSetFor(columnName));

		colDataSpec.setIsMultiSelectAllowed(true);
		assertTrue(colDataSpec.isEnumListAndSelectorSetFor(columnName));
	}

	public void testIsEnumListAndSelectorSetForNegativeCase() throws Exception {
		String columnName = "COL-" + ObjectMother.createString();
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENTITY);
		assertFalse(colDataSpec.isEnumListAndSelectorSetFor(columnName));

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		assertFalse(colDataSpec.isEnumListAndSelectorSetFor(columnName));

		colDataSpec.setEnumSourceType(EnumSourceType.EXTERNAL);
		colDataSpec.setEnumSelectorColumnName(null);
		assertFalse(colDataSpec.isEnumListAndSelectorSetFor(columnName));

		colDataSpec.setEnumSourceType(EnumSourceType.EXTERNAL);
		colDataSpec.setEnumSelectorColumnName(columnName + "X");
		assertFalse(colDataSpec.isEnumListAndSelectorSetFor(columnName));
	}

	public void testResetColumnEnumSourceTypeIfNecessaryWithNoAttrMapNoColEnums() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(null);
		colDataSpec.setAttributeMap(null);
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.COLUMN, colDataSpec.getEnumSourceType());
	}

	public void testResetColumnEnumSourceTypeIfNecessaryWithAttrMapNoColEnums() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(null);
		colDataSpec.setAttributeMap("some.class");
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.DOMAIN_ATTRIBUTE, colDataSpec.getEnumSourceType());
	}

	public void testResetColumnEnumSourceTypeIfNecessaryWithNoAttrMapColEnums() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(null);
		colDataSpec.setAttributeMap(null);
		colDataSpec.addColumnEnumValue(ObjectMother.createString());
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.COLUMN, colDataSpec.getEnumSourceType());
	}

	public void testResetColumnEnumSourceTypeIfNecessaryWithAttrMapColEnums() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(null);
		colDataSpec.setAttributeMap("some.class");
		colDataSpec.addColumnEnumValue(ObjectMother.createString());
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.COLUMN, colDataSpec.getEnumSourceType());
	}

	public void testResetColumnEnumSourceTypeNoOp() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(EnumSourceType.EXTERNAL);
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.EXTERNAL, colDataSpec.getEnumSourceType());
	}

	public void testConstructorSetsInvariantsCorrectly() throws Exception {
		assertFalse(colDataSpec.isPrecisionSet());
	}

	public void testCanBeSelectorPositiveCase() throws Exception {
		colDataSpec.setIsMultiSelectAllowed(true);
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		assertTrue(colDataSpec.canBeSelector());

		colDataSpec.setIsMultiSelectAllowed(false);
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		assertTrue(colDataSpec.canBeSelector());
	}

	public void testCanBeSelectorNegativeCase() throws Exception {
		colDataSpec.setIsMultiSelectAllowed(true);
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setIsMultiSelectAllowed(false);
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_CURRENCY);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_DATE);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_DATE_RANGE);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_DATE_TIME);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENTITY);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_FLOAT);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_INTEGER);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_PERCENT);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_STRING);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_SYMBOL);
		assertFalse(colDataSpec.canBeSelector());

		colDataSpec.setType(ColumnDataSpecDigest.TYPE_TIME_RANGE);
		assertFalse(colDataSpec.canBeSelector());
	}

	public void testGetColumnEnumValuesAsEnumValueListNoEnumReturnsEmptyList() throws Exception {
		assertTrue(colDataSpec.getColumnEnumValuesAsEnumValueList().isEmpty());
	}

	public void testGetColumnEnumValuesAsEnumValueListHappyCase() throws Exception {
		String str1 = ObjectMother.createString();
		String str2 = ObjectMother.createString();
		colDataSpec.addColumnEnumValue(str1);
		colDataSpec.addColumnEnumValue(str2);

		List<EnumValue> enumValues = colDataSpec.getColumnEnumValuesAsEnumValueList();
		assertEquals(2, enumValues.size());
		assertEquals(str1, enumValues.get(0).getDisplayLabel());
		assertEquals(str1, enumValues.get(0).getDeployValue());
		assertFalse(enumValues.get(0).hasDeployID());
		assertEquals(str2, enumValues.get(1).getDisplayLabel());
		assertEquals(str2, enumValues.get(1).getDeployValue());
		assertFalse(enumValues.get(1).hasDeployID());
	}

	public void testIsPrecisionSetPrecisionPositiveCase() throws Exception {
		colDataSpec.setPrecision(newPrecision);
		assertTrue(colDataSpec.isPrecisionSet());
	}

	public void testSetPrecisionHappyPath() throws Exception {
		colDataSpec.setPrecision(newPrecision);
		assertEquals(newPrecision, colDataSpec.getPrecision());
	}

	public void testSetPrecisionNoOpBelowMin() throws Exception {
		int belowMin = FloatFormatter.MIN_PRECISION - 5;
		assertNotEquals(origPrecision, belowMin); // sanity check

		colDataSpec.setPrecision(belowMin);
		assertEquals(origPrecision, colDataSpec.getPrecision());
	}

	public void testSetPrecisionImportHappyPath() throws Exception {
		colDataSpec.setPrecisionImport(String.valueOf(newPrecision) + ".10");
		assertEquals(newPrecision, colDataSpec.getPrecision());
	}

	public void testSetPrecisionImportInt() throws Exception {
		colDataSpec.setPrecisionImport(String.valueOf(newPrecision));
		assertEquals(newPrecision, colDataSpec.getPrecision());
	}

	public void testSetPrecisionImportNonNumeric() throws Exception {
		assertThrowsException(
				colDataSpec,
				"setPrecisionImport",
				new Class[] { String.class },
				new Object[] { "not a number" },
				NumberFormatException.class);
	}

	public void testSetPrecisionImportWithNullOrEmptyStringIsNoOp() throws Exception {
		colDataSpec.setPrecisionImport(null);
		assertFalse(colDataSpec.isPrecisionSet());
		colDataSpec.setPrecisionImport("\t ");
		assertFalse(colDataSpec.isPrecisionSet());
	}
}
