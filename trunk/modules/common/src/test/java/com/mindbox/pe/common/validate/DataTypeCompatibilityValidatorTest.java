package com.mindbox.pe.common.validate;

import static com.mindbox.pe.unittest.UnitTestHelper.assertArrayEqualsIgnoresOrder;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.unittest.AbstractTestBase;


public class DataTypeCompatibilityValidatorTest extends AbstractTestBase {

	private static class DomainProviderImpl implements DomainClassProvider {
		private final Map<String, DomainClass> dcMap = new HashMap<String, DomainClass>();

		void clear() {
			dcMap.clear();
		}

		@SuppressWarnings("unused")
		void add(DomainClass dc) {
			dcMap.put(dc.getName(), dc);
		}

		public DomainClass getDomainClass(String className) {
			return dcMap.get(className);
		}
	}

	private DomainProviderImpl domainProviderImpl;

	@Test
	public void testGetLegalGenericDataTypesForParameterBooleanTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] { DataTypeCompatibilityValidator.BOOLEAN_VALUE_DATA_TYPE, DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE }, DeployType.BOOLEAN);
	}

	@Test
	public void testGetLegalGenericDataTypesForParameterDateTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] { DataTypeCompatibilityValidator.DATE_VALUE_DATA_TYPE }, DeployType.DATE);
	}

	@Test
	public void testGetLegalGenericDataTypesForParameterNumericTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] {
				DataTypeCompatibilityValidator.DATE_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.ENUMERATED_VALUE_LIST_DATA_TYPE,
				DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE,
				DataTypeCompatibilityValidator.NUMERIC_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE }, DeployType.INTEGER);
	}

	@Test
	public void testGetLegalGenericDataTypesForParameterStringTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] {
				DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.ENUMERATED_VALUE_LIST_DATA_TYPE,
				DataTypeCompatibilityValidator.STRING_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE }, DeployType.STRING);
	}

	@Test
	public void testGetLegalGenericDataTypesForParameterSymbolTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] {
				DataTypeCompatibilityValidator.BOOLEAN_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.ENUMERATED_VALUE_LIST_DATA_TYPE,
				DataTypeCompatibilityValidator.NUMERIC_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.STRING_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE }, DeployType.SYMBOL);
	}

	private void testGetLegalGenericDataTypesForParameter(int[] expectedTypes, DeployType dt) throws Exception {
		int[] legalTypes = DataTypeCompatibilityValidator.getLegalGenericDataTypesForParameter(dt);
		if (expectedTypes == null) {
			assertNull(legalTypes);
		}
		else {
			assertNotNull(legalTypes);
			assertArrayEqualsIgnoresOrder(expectedTypes, legalTypes);
		}
	}

	@Test
	public void testIsValidWithNullReferenceAndTrueIncompleteOKReturnsNull() throws Exception {
		assertNull(DataTypeCompatibilityValidator.isValid((Reference) null, 0, null, null, null, true));
	}

	@Test
	public void testIsValidWithNullReferenceAndFalseIncompleteOKReturnsNonEmptyString() throws Exception {
		assertTrue(DataTypeCompatibilityValidator.isValid((Reference) null, 0, null, null, null, false).length() > 0);
	}

	@Test
	public void testGetGenericDataTypeWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(DataTypeCompatibilityValidator.class, "getGenericDataType", new Class[] { String.class }, new Object[] { null });
	}

	@Test
	public void testGetGenericDataTypeWithInvalidDataTypeReturnsUnknown() throws Exception {
		assertEquals(DataTypeCompatibilityValidator.UNKNOWN_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType("bogusType"));
	}

	@Test
	public void testGetGenericDataTypeWithNumericRangeTypeReturnsNumericRange() throws Exception {
		assertEquals(DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE));
		assertEquals(DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE));
		assertEquals(DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE));
		assertEquals(DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_TIME_RANGE));
	}

	@Test
	public void testGetGenericDataTypeWithDateRangeTypeReturnsDateRange() throws Exception {
		assertEquals(DataTypeCompatibilityValidator.DATE_RANGE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DATE_RANGE));
		assertEquals(DataTypeCompatibilityValidator.DATE_RANGE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE));
	}

	@Test
	public void testGetGenericDataTypeWithDateTypeReturnsDate() throws Exception {
		assertEquals(DataTypeCompatibilityValidator.DATE_VALUE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DATE));
		assertEquals(DataTypeCompatibilityValidator.DATE_VALUE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DATE_TIME));
	}

	@Test
	public void testGetGenericDataTypeWithStringTypeReturnsString() throws Exception {
		assertEquals(DataTypeCompatibilityValidator.STRING_VALUE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING));
		assertEquals(DataTypeCompatibilityValidator.STRING_VALUE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_STRING));
	}


	@Test
	public void testGetGenericDataTypeOneToOneMappings() throws Exception {
		assertEquals(DataTypeCompatibilityValidator.BOOLEAN_VALUE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_BOOLEAN));
		assertEquals(DataTypeCompatibilityValidator.ENUMERATED_VALUE_LIST_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_ENUM_LIST));
		assertEquals(DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_SYMBOL));
		assertEquals(DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_ENTITY));
	}

	@Test
	public void testGetLegalGenericDataTypesForParameterEntityTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] { DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE }, DeployType.ENTITY_LIST);
	}

	@Test
	public void testGetGenericOperatorTypeWithEntityTypeReturnsEntity() throws Exception {
		assertEquals(DataTypeCompatibilityValidator.OP_ENTITY_MATCH_FUNCTION_TYPE, DataTypeCompatibilityValidator.getGenericOperatorType(Condition.OP_ENTITY_MATCH_FUNC));
		assertEquals(DataTypeCompatibilityValidator.OP_ENTITY_MATCH_FUNCTION_TYPE, DataTypeCompatibilityValidator.getGenericOperatorType(Condition.OP_NOT_ENTITY_MATCH_FUNC));
	}

	@Before
	public void setUp() throws Exception {
		domainProviderImpl = new DomainProviderImpl();
	}

	@After
	public void tearDown() throws Exception {
		if (domainProviderImpl != null) domainProviderImpl.clear();
	}
}
