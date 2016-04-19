package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createIntegerRange;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createInteger;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;

import org.junit.Test;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.TimeSlice;

public class RangeOperatorHelperTest extends AbstractOperatorHelperTestBase {

	private DomainClass domainClass;

	@Test
	public void testFormatForPatternWithInvalidObjectThrowsRuleGenerationException() throws Exception {
		assertThrowsException(operatorHelper, "formatForPattern", new Class[] { Object.class, int.class, String.class, boolean.class, Reference.class, TimeSlice.class, Integer.class }, new Object[] {
				Boolean.TRUE,
				new Integer(Condition.OP_BETWEEN),
				"var",
				Boolean.TRUE,
				null,
				null,
				null }, RuleGenerationException.class);
	}

	@Test
	public void testFormatForPatternWithEmptyNumericRangeHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, new IntegerRange(), Condition.OP_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithNullHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, null, Condition.OP_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithNullHappyCaseNotBetween() throws Exception {
		testFormatForPattern(DEFAULT_VAR, null, Condition.OP_NOT_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyStringHappyCase() throws Exception {
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithEmptyStringHappyCaseNotBetween() throws Exception {
		testFormatForPattern(DEFAULT_VAR, "", Condition.OP_NOT_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithSingleValueNumericRangePositiveCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setLowerValue(createInteger());
		range.setUpperValue(range.getLowerValue());
		testFormatForPattern(DEFAULT_VAR + " & " + range.getLowerValue().toString(), range, Condition.OP_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithSingleValueNumericRangeNegativeCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setLowerValue(createInteger());
		range.setUpperValue(range.getLowerValue());
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.INEQUALIFY_FUNCTION + " " + DEFAULT_VAR + " " + range.getLowerValue().toString() + ")",
				range,
				Condition.OP_NOT_BETWEEN,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorOnlyNumericRangePositiveCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setLowerValue(createInteger());
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_GREATER_EQUAL + " " + DEFAULT_VAR + " " + range.getLowerValue().toString() + ")",
				range,
				Condition.OP_BETWEEN,
				false,
				createReference(domainClass));

		range.setLowerValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_GREATER + " " + DEFAULT_VAR + " " + range.getLowerValue().toString() + ")",
				range,
				Condition.OP_BETWEEN,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorOnlyNumericRangeNegativeCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setLowerValue(createInteger());
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS + " " + DEFAULT_VAR + " " + range.getLowerValue().toString() + ")",
				range,
				Condition.OP_NOT_BETWEEN,
				false,
				createReference(domainClass));

		range.setLowerValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS_EQUAL + " " + DEFAULT_VAR + " " + range.getLowerValue().toString() + ")",
				range,
				Condition.OP_NOT_BETWEEN,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithCeilingOnlyNumericRangePositiveCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setUpperValue(createInteger());
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS_EQUAL + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + ")",
				range,
				Condition.OP_BETWEEN,
				false,
				createReference(domainClass));

		range.setUpperValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + ")",
				range,
				Condition.OP_BETWEEN,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithCeilingOnlyNumericRangeNegativeCase() throws Exception {
		IntegerRange range = new IntegerRange();
		range.setUpperValue(createInteger());
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_GREATER + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + ")",
				range,
				Condition.OP_NOT_BETWEEN,
				false,
				createReference(domainClass));

		range.setUpperValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_GREATER_EQUAL + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + ")",
				range,
				Condition.OP_NOT_BETWEEN,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorAndCeilingNumericRangeBothInclusivePositiveCase() throws Exception {
		IntegerRange range = createIntegerRange(5, 100);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS_EQUAL + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + ")",
				range,
				Condition.OP_BETWEEN,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorAndCeilingNumericRangeBothExclusivePositiveCase() throws Exception {
		IntegerRange range = createIntegerRange(5, 100);
		range.setLowerValueInclusive(false);
		range.setUpperValueInclusive(false);
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + Condition.OPSTR_LESS + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR + " " + range.getUpperValue().toString() + ")",
				range,
				Condition.OP_BETWEEN,
				false,
				createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorAndCeilingNumericRangeLowerExclusivePositiveCase() throws Exception {
		IntegerRange range = createIntegerRange(5, 100);
		range.setLowerValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(AND (" + Condition.OPSTR_LESS + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR + ")(" + Condition.OPSTR_LESS_EQUAL + " " + DEFAULT_VAR
				+ " " + range.getUpperValue().toString() + "))", range, Condition.OP_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorAndCeilingNumericRangeUpperExclusivePositiveCase() throws Exception {
		IntegerRange range = createIntegerRange(5, 100);
		range.setUpperValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(AND (" + Condition.OPSTR_LESS_EQUAL + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR + ")(" + Condition.OPSTR_LESS + " " + DEFAULT_VAR
				+ " " + range.getUpperValue().toString() + "))", range, Condition.OP_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorAndCeilingNumericRangeBothInclusiveNegativeCase() throws Exception {
		IntegerRange range = createIntegerRange(5, 100);
		testFormatForPattern(DEFAULT_VAR + " &:(OR (" + Condition.OPSTR_GREATER + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR + ")(" + Condition.OPSTR_GREATER + " " + DEFAULT_VAR + " "
				+ range.getUpperValue().toString() + "))", range, Condition.OP_NOT_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorAndCeilingNumericRangeBothExclusiveNegativeCase() throws Exception {
		IntegerRange range = createIntegerRange(5, 100);
		range.setLowerValueInclusive(false);
		range.setUpperValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(OR (" + Condition.OPSTR_GREATER_EQUAL + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR + ")(" + Condition.OPSTR_GREATER_EQUAL + " "
				+ DEFAULT_VAR + " " + range.getUpperValue().toString() + "))", range, Condition.OP_NOT_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorAndCeilingNumericRangeLowerExclusiveNegativeCase() throws Exception {
		IntegerRange range = createIntegerRange(5, 100);
		range.setLowerValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(OR (" + Condition.OPSTR_GREATER_EQUAL + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR + ")(" + Condition.OPSTR_GREATER + " " + DEFAULT_VAR
				+ " " + range.getUpperValue().toString() + "))", range, Condition.OP_NOT_BETWEEN, false, createReference(domainClass));
	}

	@Test
	public void testFormatForPatternWithFloorAndCeilingNumericRangeUpperExclusiveNegativeCase() throws Exception {
		IntegerRange range = createIntegerRange(5, 100);
		range.setUpperValueInclusive(false);
		testFormatForPattern(DEFAULT_VAR + " &:(OR (" + Condition.OPSTR_GREATER + " " + range.getLowerValue().toString() + " " + DEFAULT_VAR + ")(" + Condition.OPSTR_GREATER_EQUAL + " " + DEFAULT_VAR
				+ " " + range.getUpperValue().toString() + "))", range, Condition.OP_NOT_BETWEEN, false, createReference(domainClass));
	}

	public void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.RangeOperatorHelper", TemplateUsageType.getAllInstances()[0]);
		domainClass = attachDomainAttributes(createDomainClass(), 1);
		((DomainAttribute) domainClass.getDomainAttributes().get(0)).setDeployType(DeployType.INTEGER);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	public void tearDown() throws Exception {
		// Tear downs for RangeOperatorHelperTest
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		super.tearDown();
	}
}
