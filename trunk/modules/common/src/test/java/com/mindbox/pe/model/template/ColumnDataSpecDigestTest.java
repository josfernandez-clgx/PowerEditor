package com.mindbox.pe.model.template;

import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertNotEquals;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.format.FloatFormatter;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.unittest.AbstractTestBase;

public class ColumnDataSpecDigestTest extends AbstractTestBase {

	private ColumnDataSpecDigest colDataSpec;
	private int newPrecision;
	private int origPrecision;

	@Before
	public void setUp() throws Exception {
		colDataSpec = new ColumnDataSpecDigest();

		newPrecision = 5;
		origPrecision = colDataSpec.getPrecision();
		assertNotEquals(newPrecision, origPrecision); // sanity check
	}

	@Test
	public void testIsEnumListAndSelectorSetForPositiveCase() throws Exception {
		String columnName = "COL-" + createString();
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(EnumSourceType.EXTERNAL);
		colDataSpec.setEnumSelectorColumnName(columnName);
		colDataSpec.setIsMultiSelectAllowed(false);
		assertTrue(colDataSpec.isEnumListAndSelectorSetFor(columnName));

		colDataSpec.setIsMultiSelectAllowed(true);
		assertTrue(colDataSpec.isEnumListAndSelectorSetFor(columnName));
	}

	@Test
	public void testIsEnumListAndSelectorSetForNegativeCase() throws Exception {
		String columnName = "COL-" + createString();
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

	@Test
	public void testResetColumnEnumSourceTypeIfNecessaryWithNoAttrMapNoColEnums() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(null);
		colDataSpec.setAttributeMap(null);
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.COLUMN, colDataSpec.getEnumSourceType());
	}

	@Test
	public void testResetColumnEnumSourceTypeIfNecessaryWithAttrMapNoColEnums() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(null);
		colDataSpec.setAttributeMap("some.class");
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.DOMAIN_ATTRIBUTE, colDataSpec.getEnumSourceType());
	}

	@Test
	public void testResetColumnEnumSourceTypeIfNecessaryWithNoAttrMapColEnums() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(null);
		colDataSpec.setAttributeMap(null);
		colDataSpec.addColumnEnumValue(createString());
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.COLUMN, colDataSpec.getEnumSourceType());
	}

	@Test
	public void testResetColumnEnumSourceTypeIfNecessaryWithAttrMapColEnums() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(null);
		colDataSpec.setAttributeMap("some.class");
		colDataSpec.addColumnEnumValue(createString());
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.COLUMN, colDataSpec.getEnumSourceType());
	}

	@Test
	public void testResetColumnEnumSourceTypeNoOp() throws Exception {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		colDataSpec.setEnumSourceType(EnumSourceType.EXTERNAL);
		colDataSpec.resetColumnEnumSourceTypeIfNecessary();
		assertEquals(EnumSourceType.EXTERNAL, colDataSpec.getEnumSourceType());
	}

	@Test
	public void testConstructorSetsInvariantsCorrectly() throws Exception {
		assertFalse(colDataSpec.isPrecisionSet());
	}

	@Test
	public void testCanBeSelectorPositiveCase() throws Exception {
		colDataSpec.setIsMultiSelectAllowed(true);
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		assertTrue(colDataSpec.canBeSelector());

		colDataSpec.setIsMultiSelectAllowed(false);
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		assertTrue(colDataSpec.canBeSelector());
	}

	@Test
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

	@Test
	public void testGetColumnEnumValuesAsEnumValueListNoEnumReturnsEmptyList() throws Exception {
		assertTrue(colDataSpec.getColumnEnumValuesAsEnumValueList().isEmpty());
	}

	@Test
	public void testGetColumnEnumValuesAsEnumValueListHappyCase() throws Exception {
		String str1 = createString();
		String str2 = createString();
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

	@Test
	public void testIsPrecisionSetPrecisionPositiveCase() throws Exception {
		colDataSpec.setPrecision(newPrecision);
		assertTrue(colDataSpec.isPrecisionSet());
	}

	@Test
	public void testSetPrecisionHappyPath() throws Exception {
		colDataSpec.setPrecision(newPrecision);
		assertEquals(newPrecision, colDataSpec.getPrecision());
	}

	@Test
	public void testSetPrecisionNoOpBelowMin() throws Exception {
		int belowMin = FloatFormatter.MIN_PRECISION - 5;
		assertNotEquals(origPrecision, belowMin); // sanity check

		colDataSpec.setPrecision(belowMin);
		assertEquals(origPrecision, colDataSpec.getPrecision());
	}

	@Test
	public void testSetPrecisionImportHappyPath() throws Exception {
		colDataSpec.setPrecisionImport(String.valueOf(newPrecision) + ".10");
		assertEquals(newPrecision, colDataSpec.getPrecision());
	}

	@Test
	public void testSetPrecisionImportInt() throws Exception {
		colDataSpec.setPrecisionImport(String.valueOf(newPrecision));
		assertEquals(newPrecision, colDataSpec.getPrecision());
	}

	@Test
	public void testSetPrecisionImportNonNumeric() throws Exception {
		assertThrowsException(colDataSpec, "setPrecisionImport", new Class[] { String.class }, new Object[] { "not a number" }, NumberFormatException.class);
	}

	@Test
	public void testSetPrecisionImportWithNullOrEmptyStringIsNoOp() throws Exception {
		colDataSpec.setPrecisionImport(null);
		assertFalse(colDataSpec.isPrecisionSet());
		colDataSpec.setPrecisionImport("\t ");
		assertFalse(colDataSpec.isPrecisionSet());
	}
}
