package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.TimeSlice;

public class RangeOperatorHelperTest extends OperatorHelperTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("RangeOperatorHelperTest Tests");
		suite.addTestSuite(RangeOperatorHelperTest.class);
		return suite;
	}

	private DomainClass domainClass;

	public RangeOperatorHelperTest(String name) {
		super(name);
	}

	public void testFormatForPatternWithInvalidObjectThrowsRuleGenerationException() throws Exception {
		assertThrowsException(
				operatorHelper,
				"formatForPattern",
				new Class[] { Object.class, int.class, String.class, boolean.class, Reference.class, TimeSlice.class },
				new Object[] { Boolean.TRUE, new Integer(Condition.OP_BETWEEN), "var", Boolean.TRUE, null, null },
				RuleGenerationException.class);
	}

	public void testFormatForPatternWithEmptyNumericRangeHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, new IntegerRange(), Condition.OP_BETWEEN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithNullHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR,
				null,
				Condition.OP_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}
	
	public void testFormatForPatternWithNullHappyCaseNotBetween() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR,
				null,
				Condition.OP_NOT_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}
	
	public void testFormatForPatternWithEmptyStringHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR,
				"",
				Condition.OP_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}
	
	public void testFormatForPatternWithEmptyStringHappyCaseNotBetween() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR,
				"",
				Condition.OP_NOT_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}
	
	public void testFormatForPatternWithSingleValueNumericRangePositiveCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setLowerValue(ObjectMother.createInteger());
		range.setUpperValue(range.getLowerValue());
		testFormatForPattern(
				DEFAULT_VAR + " & " + range.getLowerValue().toString(),
				range,
				Condition.OP_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithSingleValueNumericRangeNegativeCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setLowerValue(ObjectMother.createInteger());
		range.setUpperValue(range.getLowerValue());
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.INEQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + range.getLowerValue().toString() + ")",
				range,
				Condition.OP_NOT_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorOnlyNumericRangePositiveCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setLowerValue(ObjectMother.createInteger());
		testFormatForPattern(DEFAULT_VAR + " &:(" + Condition.OPSTR_GREATER_EQUAL + " " + DEFAULT_VAR + " "
				+ range.getLowerValue().toString() + ")", range, Condition.OP_BETWEEN, false, ObjectMother.createReference(domainClass));

		range.setLowerValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(" + Condition.OPSTR_GREATER + " " + DEFAULT_VAR + " "
				+ range.getLowerValue().toString() + ")", range, Condition.OP_BETWEEN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorOnlyNumericRangeNegativeCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setLowerValue(ObjectMother.createInteger());
		testFormatForPattern(DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS + " " + DEFAULT_VAR + " "
				+ range.getLowerValue().toString() + ")", range, Condition.OP_NOT_BETWEEN, false, ObjectMother.createReference(domainClass));

		range.setLowerValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS_EQUAL + " " + DEFAULT_VAR + " "
				+ range.getLowerValue().toString() + ")", range, Condition.OP_NOT_BETWEEN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithCeilingOnlyNumericRangePositiveCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setUpperValue(ObjectMother.createInteger());
		testFormatForPattern(DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS_EQUAL + " " + DEFAULT_VAR + " "
				+ range.getUpperValue().toString() + ")", range, Condition.OP_BETWEEN, false, ObjectMother.createReference(domainClass));

		range.setUpperValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS + " " + DEFAULT_VAR + " "
				+ range.getUpperValue().toString() + ")", range, Condition.OP_BETWEEN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithCeilingOnlyNumericRangeNegativeCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setUpperValue(ObjectMother.createInteger());
		testFormatForPattern(DEFAULT_VAR + " &:(" + Condition.OPSTR_GREATER + " " + DEFAULT_VAR + " "
				+ range.getUpperValue().toString() + ")", range, Condition.OP_NOT_BETWEEN, false, ObjectMother.createReference(domainClass));

		range.setUpperValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(" + Condition.OPSTR_GREATER_EQUAL + " " + DEFAULT_VAR + " "
				+ range.getUpperValue().toString() + ")", range, Condition.OP_NOT_BETWEEN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorAndCeilingNumericRangeBothInclusivePositiveCase() throws Exception {
		IntegerRange range = ObjectMother.createIntegerRange(5, 100);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS_EQUAL + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR
						+ " " + range.getUpperValue().toString() + ")",
				range,
				Condition.OP_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorAndCeilingNumericRangeBothExclusivePositiveCase() throws Exception {
		IntegerRange range = ObjectMother.createIntegerRange(5, 100);
		range.setLowerValueInclusive(false);
		range.setUpperValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR + " "
						+ range.getUpperValue().toString() + ")",
				range,
				Condition.OP_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorAndCeilingNumericRangeLowerExclusivePositiveCase() throws Exception {
		IntegerRange range = ObjectMother.createIntegerRange(5, 100);
		range.setLowerValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(AND (" + Condition.OPSTR_LESS + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR
						+ ")(" + Condition.OPSTR_LESS_EQUAL + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + "))",
				range,
				Condition.OP_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorAndCeilingNumericRangeUpperExclusivePositiveCase() throws Exception {
		IntegerRange range = ObjectMother.createIntegerRange(5, 100);
		range.setUpperValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(AND (" + Condition.OPSTR_LESS_EQUAL + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR
						+ ")(" + Condition.OPSTR_LESS + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + "))",
				range,
				Condition.OP_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorAndCeilingNumericRangeBothInclusiveNegativeCase() throws Exception {
		IntegerRange range = ObjectMother.createIntegerRange(5, 100);
		testFormatForPattern(DEFAULT_VAR + " &:(OR (" + Condition.OPSTR_GREATER + " " + range.getLowerValue().toString() + " "
				+ DEFAULT_VAR + ")(" + Condition.OPSTR_GREATER + " " + DEFAULT_VAR + " " + range.getUpperValue().toString()
				+ "))", range, Condition.OP_NOT_BETWEEN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorAndCeilingNumericRangeBothExclusiveNegativeCase() throws Exception {
		IntegerRange range = ObjectMother.createIntegerRange(5, 100);
		range.setLowerValueInclusive(false);
		range.setUpperValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(OR (" + Condition.OPSTR_GREATER_EQUAL + " " + range.getLowerValue().toString()
				+ " " + DEFAULT_VAR + ")(" + Condition.OPSTR_GREATER_EQUAL + " " + DEFAULT_VAR + " "
				+ range.getUpperValue().toString() + "))", range, Condition.OP_NOT_BETWEEN, false, ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorAndCeilingNumericRangeLowerExclusiveNegativeCase() throws Exception {
		IntegerRange range = ObjectMother.createIntegerRange(5, 100);
		range.setLowerValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(OR (" + Condition.OPSTR_GREATER_EQUAL + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR
						+ ")(" + Condition.OPSTR_GREATER + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + "))",
				range,
				Condition.OP_NOT_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}

	public void testFormatForPatternWithFloorAndCeilingNumericRangeUpperExclusiveNegativeCase() throws Exception {
		IntegerRange range = ObjectMother.createIntegerRange(5, 100);
		range.setUpperValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(OR (" + Condition.OPSTR_GREATER + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR
						+ ")(" + Condition.OPSTR_GREATER_EQUAL + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + "))",
				range,
				Condition.OP_NOT_BETWEEN,
				false,
				ObjectMother.createReference(domainClass));
	}

	protected void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.RangeOperatorHelper", TemplateUsageType.getAllInstances()[0]);
		domainClass = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		((DomainAttribute) domainClass.getDomainAttributes().get(0)).setDeployType(DeployType.INTEGER);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	protected void tearDown() throws Exception {
		// Tear downs for RangeOperatorHelperTest
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		super.tearDown();
	}
}
