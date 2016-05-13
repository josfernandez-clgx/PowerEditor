package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.server.ServerTestObjectMother.attachEnumValue;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValue;
import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValues;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createString;

import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;

public class MembershipOperatorHelperTest extends AbstractOperatorHelperTestBase {

	private DomainClass domainClass;

	@Test
	public void testFormatForPatternWithSingleEnumValueAndEqualOpHappyCase() throws Exception {
		EnumValue enumValue = createEnumValue();
		testFormatForPattern(DEFAULT_VAR + " & " + enumValue.getDeployValue(), enumValue, Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithSingleEnumValueAndNotEqualOpHappyCase() throws Exception {
		EnumValue enumValue = createEnumValue();
		testFormatForPattern(DEFAULT_VAR + " &  ~ " + enumValue.getDeployValue(), enumValue, Condition.OP_NOT_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithSingleEnumValueAndInOpHappyCase() throws Exception {
		EnumValue enumValue = createEnumValue();
		testFormatForPattern(DEFAULT_VAR + " & " + enumValue.getDeployValue(), enumValue, Condition.OP_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithSingleEnumValueAndNotInOpHappyCase() throws Exception {
		EnumValue enumValue = createEnumValue();
		testFormatForPattern(DEFAULT_VAR + " &  ~ " + enumValue.getDeployValue(), enumValue, Condition.OP_NOT_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithSingleStringAndEqualOpHappyCase() throws Exception {
		String str = createString();
		testFormatForPattern(DEFAULT_VAR + " & " + str, str, Condition.OP_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithSingleStringAndNotEqualOpHappyCase() throws Exception {
		String str = createString();
		testFormatForPattern(DEFAULT_VAR + " &  ~ " + str, str, Condition.OP_NOT_EQUAL, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyExcludedEnumValuesHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = createEnumValues();
		enumValues.setSelectionExclusion(true);
		testFormatForPattern(DEFAULT_VAR, enumValues, Condition.OP_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyExcludedEnumValuesHappyCaseNotIN() throws Exception {
		EnumValues<EnumValue> enumValues = createEnumValues();
		enumValues.setSelectionExclusion(true);
		testFormatForPattern(DEFAULT_VAR, enumValues, Condition.OP_NOT_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyEnumValuesHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = createEnumValues();
		testFormatForPattern(DEFAULT_VAR, enumValues, Condition.OP_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithNullEnumValuesHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, null, Condition.OP_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyStringHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyStringHappyCaseNotIn() throws Exception {
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_NOT_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyEnumValuesHappyCaseNotIN() throws Exception {
		EnumValues<EnumValue> enumValues = createEnumValues();
		testFormatForPattern(DEFAULT_VAR, enumValues, Condition.OP_NOT_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithNullEnumValuesHappyCaseNotIN() throws Exception {
		testFormatForPattern(DEFAULT_VAR, null, Condition.OP_NOT_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternForEnumValuesOfEnumValuesHappyCaseMultiple() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 2);
		DeploymentManager.getInstance().addEnumValueMap(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(), enumValues);
		testFormatForPattern(DEFAULT_VAR + " & " + enumValues.get(0).getDeployValue() + " | " + enumValues.get(1).getDeployValue(), enumValues, Condition.OP_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternForEnumValuesOfEnumValuesHappyCaseSingle() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 1);
		DeploymentManager.getInstance().addEnumValueMap(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(), enumValues);
		testFormatForPattern(DEFAULT_VAR + " & " + enumValues.get(0).getDeployValue(), enumValues, Condition.OP_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternForEnumValuesOfEnumValuesHappyCaseMultipleNotIN() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 2);
		DeploymentManager.getInstance().addEnumValueMap(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(), enumValues);
		testFormatForPattern(
				DEFAULT_VAR + " &  ~ " + enumValues.get(0).getDeployValue() + " &  ~ " + enumValues.get(1).getDeployValue(),
				enumValues,
				Condition.OP_NOT_IN,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternForEnumValuesOfEnumValuesHappyCaseSingleNotIN() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 1);
		DeploymentManager.getInstance().addEnumValueMap(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(), enumValues);
		testFormatForPattern(DEFAULT_VAR + " &  ~ " + enumValues.get(0).getDeployValue(), enumValues, Condition.OP_NOT_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternForEnumValuesOfStringsHappyCaseMultiple() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("one");
		enumValues.add("two");
		testFormatForPattern(DEFAULT_VAR + " & one | two", enumValues, Condition.OP_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternForEnumValuesOfStringsHappyCaseSingle() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("value");
		testFormatForPattern(DEFAULT_VAR + " & value", enumValues, Condition.OP_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternForEnumValuesOfStringsHappyCaseMultipleNotIN() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("one");
		enumValues.add("two");
		testFormatForPattern(DEFAULT_VAR + " &  ~ one &  ~ two", enumValues, Condition.OP_NOT_IN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternForEnumValuesOfStringsHappyCaseSingleNotIN() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("value");
		testFormatForPattern(DEFAULT_VAR + " &  ~ value", enumValues, Condition.OP_NOT_IN, false, createReference(domainClass));
	}

	public void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.MembershipOperatorHelper", TemplateUsageType.getAllInstances()[0]);
		domainClass = attachDomainAttributes(createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	public void tearDown() throws Exception {
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		// Tear downs for MembershipOperatorHelperTest
		super.tearDown();
	}
}
