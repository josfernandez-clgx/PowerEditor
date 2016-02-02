package com.mindbox.pe.common.validate;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;

public class DataTypeCompatibilityValidatorTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DataTypeCompatibilityValidatorTest Tests");
		suite.addTestSuite(DataTypeCompatibilityValidatorTest.class);
		return suite;
	}

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

	public DataTypeCompatibilityValidatorTest(String name) {
		super(name);
	}

	public void testGetLegalGenericDataTypesForParameterBooleanTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] { 
				DataTypeCompatibilityValidator.BOOLEAN_VALUE_DATA_TYPE, 
				DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE },
			DeployType.BOOLEAN);
	}

	public void testGetLegalGenericDataTypesForParameterDateTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] { 
				DataTypeCompatibilityValidator.DATE_VALUE_DATA_TYPE }, 
			DeployType.DATE);
	}
    
	public void testGetLegalGenericDataTypesForParameterNumericTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] { 
				DataTypeCompatibilityValidator.DATE_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE, 
				DataTypeCompatibilityValidator.ENUMERATED_VALUE_LIST_DATA_TYPE,
				DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE,
				DataTypeCompatibilityValidator.NUMERIC_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE }, 
			DeployType.INTEGER);
	}

	public void testGetLegalGenericDataTypesForParameterStringTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] { 
				DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE, 
				DataTypeCompatibilityValidator.ENUMERATED_VALUE_LIST_DATA_TYPE,
				DataTypeCompatibilityValidator.STRING_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE }, 
			DeployType.STRING);
	}

	public void testGetLegalGenericDataTypesForParameterSymbolTypeHappyCase() throws Exception {
		testGetLegalGenericDataTypesForParameter(new int[] { 
				DataTypeCompatibilityValidator.BOOLEAN_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE , 
				DataTypeCompatibilityValidator.ENUMERATED_VALUE_LIST_DATA_TYPE,
				DataTypeCompatibilityValidator.NUMERIC_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.STRING_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE }, 
			DeployType.SYMBOL);
	}

	private void testGetLegalGenericDataTypesForParameter(int[] expectedTypes, DeployType dt) throws Exception {
		int[] legalTypes = DataTypeCompatibilityValidator.getLegalGenericDataTypesForParameter(dt);
		if (expectedTypes == null) {
			assertNull(legalTypes);
		}
		else {
			assertNotNull(legalTypes);
			assertEquals(expectedTypes, legalTypes);
		}
	}

	public void testIsValidWithNullReferenceAndTrueIncompleteOKReturnsNull() throws Exception {
		assertNull(DataTypeCompatibilityValidator.isValid((Reference) null, 0, null, null, null, true));
	}

	public void testIsValidWithNullReferenceAndFalseIncompleteOKReturnsNonEmptyString() throws Exception {
		assertTrue(DataTypeCompatibilityValidator.isValid((Reference) null, 0, null, null, null, false).length() > 0);
	}

	public void testGetGenericDataTypeWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				DataTypeCompatibilityValidator.class,
				"getGenericDataType",
				new Class[] { String.class },
				new Object[] { null });
	}

	public void testGetGenericDataTypeWithInvalidDataTypeReturnsUnknown() throws Exception {
		assertEquals(DataTypeCompatibilityValidator.UNKNOWN_DATA_TYPE, DataTypeCompatibilityValidator.getGenericDataType("bogusType"));
	}

	public void testGetGenericDataTypeWithNumericRangeTypeReturnsNumericRange() throws Exception {
		assertEquals(
				DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE));
		assertEquals(
				DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE));
		assertEquals(
				DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE));
		assertEquals(
				DataTypeCompatibilityValidator.NUMERIC_RANGE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_TIME_RANGE));
	}

	public void testGetGenericDataTypeWithDateRangeTypeReturnsDateRange() throws Exception {
		assertEquals(
				DataTypeCompatibilityValidator.DATE_RANGE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DATE_RANGE));
		assertEquals(
				DataTypeCompatibilityValidator.DATE_RANGE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE));
	}

	public void testGetGenericDataTypeWithDateTypeReturnsDate() throws Exception {
		assertEquals(
				DataTypeCompatibilityValidator.DATE_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DATE));
		assertEquals(
				DataTypeCompatibilityValidator.DATE_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DATE_TIME));
	}

	public void testGetGenericDataTypeWithStringTypeReturnsString() throws Exception {
		assertEquals(
				DataTypeCompatibilityValidator.STRING_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING));
		assertEquals(
				DataTypeCompatibilityValidator.STRING_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_STRING));
	}
        

	public void testGetGenericDataTypeOneToOneMappings() throws Exception {
		assertEquals(
				DataTypeCompatibilityValidator.BOOLEAN_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_BOOLEAN));
		assertEquals(
				DataTypeCompatibilityValidator.ENUMERATED_VALUE_LIST_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_ENUM_LIST));
		assertEquals(
				DataTypeCompatibilityValidator.SYMBOL_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_SYMBOL));
		assertEquals(
				DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE,
				DataTypeCompatibilityValidator.getGenericDataType(ColumnDataSpecDigest.TYPE_ENTITY));
	}
    
    public void testGetLegalGenericDataTypesForParameterEntityTypeHappyCase() throws Exception {
        testGetLegalGenericDataTypesForParameter(new int[] { 
                DataTypeCompatibilityValidator.ENTITY_VALUE_DATA_TYPE },
            DeployType.ENTITY_LIST);
    }

    public void testGetGenericOperatorTypeWithEntityTypeReturnsEntity() throws Exception {
        assertEquals(
                DataTypeCompatibilityValidator.OP_ENTITY_MATCH_FUNCTION_TYPE,
                DataTypeCompatibilityValidator.getGenericOperatorType(Condition.OP_ENTITY_MATCH_FUNC));
        assertEquals(
                DataTypeCompatibilityValidator.OP_ENTITY_MATCH_FUNCTION_TYPE,
                DataTypeCompatibilityValidator.getGenericOperatorType(Condition.OP_NOT_ENTITY_MATCH_FUNC));
    }

	// TODO Kim 11/6/2006: add tests for isValid for FunctionParameter
	//    These tests need to test correctness of DataTypeCompatibilityValidator#LEGAL_CONDITION_COMBINATIONS.

	// TODO Kim 8/12/2006: add tests for isValid for Reference
	//    These tests need to test correctness of DataTypeCompatibilityValidator#LEGAL_CONDITION_COMBINATIONS.

	protected void setUp() throws Exception {
		super.setUp();
		domainProviderImpl = new DomainProviderImpl();
	}

	protected void tearDown() throws Exception {
		if (domainProviderImpl != null) domainProviderImpl.clear();
		super.tearDown();
	}
}
