package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;

public class MembershipOperatorHelperTest extends OperatorHelperTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("MembershipOperatorHelperTest Tests");
		suite.addTestSuite(MembershipOperatorHelperTest.class);
		return suite;
	}

	private DomainClass domainClass;

	public MembershipOperatorHelperTest(String name) {
		super(name);
	}

	public void testFormatForPatternWithSingleEnumValueAndEqualOpHappyCase() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		testFormatForPattern(
				DEFAULT_VAR + " & " + enumValue.getDeployValue(),
				enumValue,
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithSingleEnumValueAndNotEqualOpHappyCase() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		testFormatForPattern(
				DEFAULT_VAR + " &  ~ " + enumValue.getDeployValue(),
				enumValue,
				Condition.OP_NOT_EQUAL,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithSingleEnumValueAndInOpHappyCase() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		testFormatForPattern(
				DEFAULT_VAR + " & " + enumValue.getDeployValue(),
				enumValue,
				Condition.OP_IN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithSingleEnumValueAndNotInOpHappyCase() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		testFormatForPattern(
				DEFAULT_VAR + " &  ~ " + enumValue.getDeployValue(),
				enumValue,
				Condition.OP_NOT_IN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithSingleStringAndEqualOpHappyCase() throws Exception {
		String str = ObjectMother.createString();
		testFormatForPattern(DEFAULT_VAR + " & " + str, str, Condition.OP_EQUAL, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithSingleStringAndNotEqualOpHappyCase() throws Exception {
		String str = ObjectMother.createString();
		testFormatForPattern(DEFAULT_VAR + " &  ~ " + str, str, Condition.OP_NOT_EQUAL, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyExcludedEnumValuesHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.createEnumValues();
		enumValues.setSelectionExclusion(true);
		testFormatForPattern(DEFAULT_VAR, enumValues, Condition.OP_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyExcludedEnumValuesHappyCaseNotIN() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.createEnumValues();
		enumValues.setSelectionExclusion(true);
		testFormatForPattern(DEFAULT_VAR, enumValues, Condition.OP_NOT_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyEnumValuesHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.createEnumValues();
		testFormatForPattern(DEFAULT_VAR, enumValues, Condition.OP_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithNullEnumValuesHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, null, Condition.OP_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyStringHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyStringHappyCaseNotIn() throws Exception {
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_NOT_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithEmptyEnumValuesHappyCaseNotIN() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.createEnumValues();
		testFormatForPattern(DEFAULT_VAR, enumValues, Condition.OP_NOT_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithNullEnumValuesHappyCaseNotIN() throws Exception {
		testFormatForPattern(DEFAULT_VAR, null, Condition.OP_NOT_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternForEnumValuesOfEnumValuesHappyCaseMultiple() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 2);
		DeploymentManager.getInstance().addEnumValueMap(
				domainClass.getName(),
				((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(),
				enumValues);
		testFormatForPattern(
				DEFAULT_VAR + " & " + enumValues.get(0).getDeployValue() + " | " + enumValues.get(1).getDeployValue(),
				enumValues,
				Condition.OP_IN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternForEnumValuesOfEnumValuesHappyCaseSingle() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 1);
		DeploymentManager.getInstance().addEnumValueMap(
				domainClass.getName(),
				((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(),
				enumValues);
		testFormatForPattern(
				DEFAULT_VAR + " & " + enumValues.get(0).getDeployValue(),
				enumValues,
				Condition.OP_IN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternForEnumValuesOfEnumValuesHappyCaseMultipleNotIN() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 2);
		DeploymentManager.getInstance().addEnumValueMap(
				domainClass.getName(),
				((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(),
				enumValues);
		testFormatForPattern(
				DEFAULT_VAR + " &  ~ " + enumValues.get(0).getDeployValue() + " &  ~ " + enumValues.get(1).getDeployValue(),
				enumValues,
				Condition.OP_NOT_IN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternForEnumValuesOfEnumValuesHappyCaseSingleNotIN() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 1);
		DeploymentManager.getInstance().addEnumValueMap(
				domainClass.getName(),
				((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName(),
				enumValues);
		testFormatForPattern(
				DEFAULT_VAR + " &  ~ " + enumValues.get(0).getDeployValue(),
				enumValues,
				Condition.OP_NOT_IN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternForEnumValuesOfStringsHappyCaseMultiple() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("one");
		enumValues.add("two");
		testFormatForPattern(DEFAULT_VAR + " & one | two", enumValues, Condition.OP_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternForEnumValuesOfStringsHappyCaseSingle() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("value");
		testFormatForPattern(DEFAULT_VAR + " & value", enumValues, Condition.OP_IN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternForEnumValuesOfStringsHappyCaseMultipleNotIN() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("one");
		enumValues.add("two");
		testFormatForPattern(
				DEFAULT_VAR + " &  ~ one &  ~ two",
				enumValues,
				Condition.OP_NOT_IN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternForEnumValuesOfStringsHappyCaseSingleNotIN() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("value");
		testFormatForPattern(DEFAULT_VAR + " &  ~ value", enumValues, Condition.OP_NOT_IN, false, ObjectMother.createReference(domainClass));
	}

	protected void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper(
				"com.mindbox.pe.server.generator.value.MembershipOperatorHelper",
				TemplateUsageType.getAllInstances()[0]);
		domainClass = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	protected void tearDown() throws Exception {
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		DeploymentManager.getInstance().startLoading();
		// Tear downs for MembershipOperatorHelperTest
		super.tearDown();
	}
}
