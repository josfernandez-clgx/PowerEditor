package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.TimeSlice;

public class ComparisonOperatorHelperTest extends AbstractOperatorHelperTestBase {

	private DomainClass domainClass;

	@Test
	public void testFormatForPatternWithNonNumericObjectThrowsRuleGeneratorException() throws Exception {
		try {
			operatorHelper.formatForPattern(Boolean.TRUE, Condition.OP_ANY_VALUE, "var", true, createReference(), TimeSlice.createInstance(null, createDateSynonym()), null);
			fail("Expected RuleGenerationException not thrown");
		}
		catch (RuleGenerationException ex) {
			// expected
		}
		try {
			operatorHelper.formatForPattern(new EnumValues<EnumValue>(), Condition.OP_ANY_VALUE, "var", true, createReference(), TimeSlice.createInstance(null, createDateSynonym()), null);
			fail("Expected RuleGenerationException not thrown");
		}
		catch (RuleGenerationException ex) {
			// expected
		}
	}

	@Test
	public void testFormatForPatternWithNullValueHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR,
				null,
				Condition.OP_GREATER,
				false,
				RuleElementFactory.getInstance().createReference(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName()),
				TimeSlice.createInstance(null, createDateSynonym()));
	}

	@Test
	public void testFormatForPatternWithEmptyStringHappyCase() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR,
				"",
				Condition.OP_GREATER,
				false,
				RuleElementFactory.getInstance().createReference(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName()),
				TimeSlice.createInstance(null, createDateSynonym()));
	}

	@Test
	public void testFormatForPatternForGreater() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(> " + DEFAULT_VAR + " 100)",
				new Integer(100),
				Condition.OP_GREATER,
				false,
				RuleElementFactory.getInstance().createReference(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName()),
				TimeSlice.createInstance(null, createDateSynonym()));
	}

	@Test
	public void testFormatForPatternForGreaterEqual() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(>= " + DEFAULT_VAR + " 100)",
				new Integer(100),
				Condition.OP_GREATER_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));
	}

	@Test
	public void testFormatForPatternForLess() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(< " + DEFAULT_VAR + " 100)",
				new Integer(100),
				Condition.OP_LESS,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));
	}

	@Test
	public void testFormatForPatternForLessEqual() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " 100)",
				new Integer(100),
				Condition.OP_LESS_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));
	}

	@Test
	public void testFormatForPatternForReferenceVariable() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_LESS_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.INEQUALIFY_FUNCTION + " " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_NOT_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(> " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_GREATER,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " ?some-variable)",
				"?some-variable",
				Condition.OP_LESS_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));
	}

	@Test
	public void testFormatForPatternForMathExpressionValue() throws Exception {
		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_LESS_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));
		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(" + RuleGeneratorHelper.INEQUALIFY_FUNCTION + " " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_NOT_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(> " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_GREATER,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));

		testFormatForPattern(
				DEFAULT_VAR + " &:(<= " + DEFAULT_VAR + " (* 1 ?some-var))",
				"(* 1 ?some-var)",
				Condition.OP_LESS_EQUAL,
				false,
				createReference(domainClass),
				TimeSlice.createInstance(null, createDateSynonym()));
	}

	public void setUp() throws Exception {
		super.setUp();
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.ComparisonOperatorHelper", TemplateUsageType.getAllInstances()[0]);
		domainClass = attachDomainAttributes(createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	public void tearDown() throws Exception {
		DomainManager.getInstance().removeFromCache(domainClass.getName());
		super.tearDown();
	}
}
