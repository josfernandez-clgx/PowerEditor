package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.TimeSlice;

public class ComparisonOperatorHelperTest extends OperatorHelperTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ComparisonOperatorHelperTest Tests");
		suite.addTestSuite(ComparisonOperatorHelperTest.class);
		return suite;
	}

	private DomainClass domainClass;

	public ComparisonOperatorHelperTest(String name) {
		super(name);
	}

	public void testFormatForPatternWithNonNumericObjectThrowsRuleGeneratorException() throws Exception {
		try {
			operatorHelper.formatForPattern(Boolean.TRUE, Condition.OP_ANY_VALUE, "var", true, ObjectMother.createReference(), TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
			fail("Expected RuleGenerationException not thrown");
		}
		catch (RuleGenerationException ex) {
			// expected
		}
		try {
			operatorHelper.formatForPattern(new EnumValues<EnumValue>(), Condition.OP_ANY_VALUE, "var", true, ObjectMother.createReference(), TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
			fail("Expected RuleGenerationException not thrown");
		}
		catch (RuleGenerationException ex) {
			// expected
		}
	}
	
	public void testFormatForPatternWithNullValueHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR,
				null,
				Condition.OP_GREATER,
				false,
				RuleElementFactory.getInstance().createReference(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName()),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
	}

	public void testFormatForPatternWithEmptyStringHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR,
				"",
				Condition.OP_GREATER,
				false,
				RuleElementFactory.getInstance().createReference(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName()),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
	}

	public void testFormatForPatternForGreater() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(> " + DEFAULT_VAR + " 100)",
				new Integer(100),
				Condition.OP_GREATER,
				false,
				RuleElementFactory.getInstance().createReference(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName()),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
	}

	public void testFormatForPatternForGreaterEqual() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(>= " + DEFAULT_VAR + " 100)",
				new Integer(100),
				Condition.OP_GREATER_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
	}

	public void testFormatForPatternForLess() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(< " + DEFAULT_VAR + " 100)",
				new Integer(100),
				Condition.OP_LESS,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
	}

	public void testFormatForPatternForLessEqual() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " 100)",
				new Integer(100),
				Condition.OP_LESS_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
	}

	public void testFormatForPatternForReferenceVariable() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_LESS_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
		
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.INEQUALIFY_FUNCTION + " " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_NOT_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(> " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_GREATER,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_LESS_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
	}

	public void testFormatForPatternForMathExpressionValue() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_LESS_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.INEQUALIFY_FUNCTION + " " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_NOT_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(> " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_GREATER,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_LESS_EQUAL,
				false,
				ObjectMother.createReference(domainClass),
				TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
	}

	protected void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.ComparisonOperatorHelper", TemplateUsageType.getAllInstances()[0]);
		domainClass = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	protected void tearDown() throws Exception {
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		super.tearDown();
	}
}
