package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DomainClassLink;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.config.RuleLHSValueConfig;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.DomainClassLinkPattern;

public class AttributePatternFactoryTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AttributePatternFactoryTest Tests");
		suite.addTestSuite(AttributePatternFactoryTest.class);
		return suite;
	}

	private AttributePatternFactory attributePatternFactory;
	private PatternFactoryHelper helperMock;
	private MockControl mockControl;
	private TemplateUsageType usageType;

	public AttributePatternFactoryTest(String name) {
		super(name);
	}

	public void testCreateAttributePatternWithNullConditionThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				attributePatternFactory,
				"createAttributePattern",
				new Class[] { Condition.class, String.class, TemplateUsageType.class },
				new Object[] { null, "", usageType });
	}

	public void testCreateAttributePatternWithNullReferenceThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				attributePatternFactory,
				"createAttributePattern",
				new Class[] { Condition.class, String.class, TemplateUsageType.class },
				new Object[] { RuleElementFactory.getInstance().createCondition(), "", usageType });
	}

	public void testCreateAttributePatternWithAnyValueOperatorHappyCase() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_ANY_VALUE);

		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertTrue(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertTrue(UtilBase.isEmpty(attributePattern.getValueText()));
	}

	public void testCreateAttributePatternWithIsEmptyOperatorHappyCase() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_IS_EMPTY);

		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfiguration(usageType));
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.replay();

		RuleLHSValueConfig valueConfig = ConfigurationManager.getInstance().getRuleGenerationConfiguration(usageType).getLHSValueConfig(
				RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED);
		String valueStr = RuleGeneratorHelper.formatForStringType(valueConfig.getDeployValue(), valueConfig.isValueAsString());
		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertEquals(" & " + valueStr, attributePattern.getValueText());
	}

	public void testCreateAttributePatternWithIsNotEmptyOperatorHappyCase() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_IS_NOT_EMPTY);

		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfiguration(usageType));
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.replay();

		RuleLHSValueConfig valueConfig = ConfigurationManager.getInstance().getRuleGenerationConfiguration(usageType).getLHSValueConfig(
				RuleGenerationConfiguration.VAUE_TYPE_UNSPECIFIED);
		String valueStr = RuleGeneratorHelper.formatForStringType(valueConfig.getDeployValue(), valueConfig.isValueAsString());
		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertEquals(" &:(/= " + "?" + condition.getReference().getAttributeName() + ' ' + valueStr + ")", attributePattern.getValueText());
	}

	public void testCreateAttributePatternWithColumnReferenceValueHappyCase() throws Exception {
		Condition condition = ObjectMother.attachColumnReference(ObjectMother.attachReference(ObjectMother.createCondition()));
		condition.setOp(Condition.OP_EQUAL);

		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertFalse(attributePattern.isEmpty());
		assertTrue(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());

		PatternValueSlot valueSlot = (PatternValueSlot) attributePattern.getValueSlot();
		assertEquals(ValueSlot.Type.COLUMN_REFERENCE, valueSlot.getType());
		assertEquals(condition.getOp(), valueSlot.getOperator());
		assertEquals(condition.getReference(), valueSlot.getReference());
		assertEquals(((ColumnReference) condition.getValue()).getColumnNo(), ((Integer) valueSlot.getSlotValue()).intValue());
		assertTrue(UtilBase.isEmpty(valueSlot.getSlotText()));
	}

	public void testCreateAttributePatternWithMathExpValueWithColRefHappyCase() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_GREATER);
		MathExpressionValue meValue = (MathExpressionValue) RuleElementFactory.getInstance().createValue(
				ObjectMother.createColumnReference(),
				"+",
				ObjectMother.createReference());
		condition.setValue(meValue);

		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(meValue.getAttributeReference().getAttributeName()), "?"
				+ meValue.getAttributeReference().getAttributeName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertFalse(attributePattern.isEmpty());
		assertTrue(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());

		PatternValueSlot valueSlot = (PatternValueSlot) attributePattern.getValueSlot();
		assertEquals(ValueSlot.Type.COLUMN_REFERENCE, valueSlot.getType());
		assertEquals(condition.getOp(), valueSlot.getOperator());
		assertEquals(condition.getReference(), valueSlot.getReference());
		assertEquals(meValue.getColumnReference().getColumnNo(), ((Integer) valueSlot.getSlotValue()).intValue());
		assertEquals(
				"(" + meValue.getOperator() + " {0} " + "?" + meValue.getAttributeReference().getAttributeName() + ")",
				valueSlot.getSlotText());
	}

	public void testCreateAttributePatternWithMathExpValueWithStrValueHappyCase() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_GREATER);
		String valueStr = "value-" + ObjectMother.createString();
		MathExpressionValue meValue = (MathExpressionValue) RuleElementFactory.getInstance().createValue(
				valueStr,
				"*",
				ObjectMother.createReference());
		condition.setValue(meValue);

		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(meValue.getAttributeReference().getAttributeName()), "?"
				+ meValue.getAttributeReference().getAttributeName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertFalse(attributePattern.isEmpty());
		assertTrue(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertEquals(
				"(" + meValue.getOperator() + " " + valueStr + " ?" + meValue.getAttributeReference().getAttributeName() + ")",
				attributePattern.getValueSlot().getSlotValue());
	}

	public void testCreateAttributePatternWithReferenceValueHappyCase() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_EQUAL);
		Value referenceValue = RuleElementFactory.getInstance().createValue(ObjectMother.createReference());
		condition.setValue(referenceValue);

		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(((Reference) referenceValue).getAttributeName()), "?"
				+ ((Reference) referenceValue).getAttributeName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertEquals("&:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " ?" + condition.getReference().getAttributeName() + " ?"
				+ ((Reference) referenceValue).getAttributeName() + ')', attributePattern.getValueText());
	}

	public void testCreateAttributePatternWithNonNullStringValueHappyCaseForStringDeploy() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_BETWEEN);
		Value value = RuleElementFactory.getInstance().createValue("value" + ObjectMother.createInt());
		condition.setValue(value);

		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.isStringDeployTypeForAttribute(condition.getReference()), true);
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertFalse(attributePattern.isEmpty());
		assertTrue(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());

		StringValuePatternValueSlot valueSlot = (StringValuePatternValueSlot) attributePattern.getValueSlot();
		assertEquals(ValueSlot.Type.STRING, valueSlot.getType());
		assertEquals(condition.getOp(), valueSlot.getOperator());
		assertEquals(condition.getReference(), valueSlot.getReference());
		assertEquals("\"" + value.toString() + "\"", valueSlot.getSlotValue());
	}

	public void testCreateAttributePatternWithNonNullStringValueHappyCaseForNonStringDeploy() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_BETWEEN);
		String valueStr = "value" + ObjectMother.createInt();
		Value value = RuleElementFactory.getInstance().createValue("\"" + valueStr + "\"");
		condition.setValue(value);

		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.isStringDeployTypeForAttribute(condition.getReference()), false);
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertFalse(attributePattern.isEmpty());
		assertTrue(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());

		StringValuePatternValueSlot valueSlot = (StringValuePatternValueSlot) attributePattern.getValueSlot();
		assertEquals(ValueSlot.Type.STRING, valueSlot.getType());
		assertEquals(condition.getOp(), valueSlot.getOperator());
		assertEquals(condition.getReference(), valueSlot.getReference());
		assertEquals(valueStr, valueSlot.getSlotValue());
	}

	public void testCreateAttributePatternWithNullStringValueHappyCase() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_BETWEEN);

		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:"
				+ condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?"
				+ condition.getReference().getAttributeName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		mockControl.verify();
		assertFalse(attributePattern.isEmpty());
		assertTrue(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());

		StringValuePatternValueSlot valueSlot = (StringValuePatternValueSlot) attributePattern.getValueSlot();
		assertEquals(ValueSlot.Type.STRING, valueSlot.getType());
		assertEquals(condition.getOp(), valueSlot.getOperator());
		assertEquals(condition.getReference(), valueSlot.getReference());
		assertEquals(RuleGeneratorHelper.AE_NIL, valueSlot.getSlotValue());
	}

	public void testCreateLinkAttributePatternWithNullDomainClassLinkPatternThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				attributePatternFactory,
				"createLinkAttributePattern",
				new Class[] { DomainClassLinkPattern.class, TemplateUsageType.class },
				new Object[] { null, usageType });
	}

	public void testCreateLinkAttributePatternWithNullDomainClassLinkThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				attributePatternFactory,
				"createLinkAttributePattern",
				new Class[] { DomainClassLink.class, String.class, TemplateUsageType.class },
				new Object[] { null, "", usageType });
	}

	public void testCreateLinkAttributePatternGeneratesAttributeNameIfDeployNameValueIsNull() throws Exception {
		DomainClassLink domainClassLink = ObjectMother.createDomainClassLink();
		domainClassLink.setDeployValueName(null);

		resetLinkPatternConfigInvariants("", null, false);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.expectAndReturn(helperMock.asVariableName(domainClassLink.getChildName()), "?" + domainClassLink.getChildName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, "object_name", usageType);
		assertTrue(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getChildName() + "-link", attributePattern.getAttributeName());
		assertEquals("?object_name", attributePattern.getVariableName());
	}

	public void testCreateLinkAttributePatternForTestFunctionWithObjectNameHappyCase() throws Exception {
		DomainClassLink domainClassLink = ObjectMother.createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		resetLinkPatternConfigInvariants("$function", null, true);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.expectAndReturn(helperMock.asVariableName(domainClassLink.getChildName()), "?" + domainClassLink.getChildName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, "object_name", usageType);
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals("?object_name", attributePattern.getVariableName());
		assertEquals("&:($function ?" + domainClassLink.getChildName() + " ?object_name)", attributePattern.getValueText());
	}

	public void testCreateLinkAttributePatternForTestFunctionWithNullObjectNameNoSuffixHappyCase() throws Exception {
		DomainClassLink domainClassLink = ObjectMother.createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		// check with null suffix (for default suffix)
		resetLinkPatternConfigInvariants("$function", null, true);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.expectAndReturn(helperMock.asVariableName(domainClassLink.getChildName()), "?" + domainClassLink.getChildName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, null, usageType);
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals("?" + domainClassLink.getChildName() + "-suffix", attributePattern.getVariableName());
		assertEquals(
				"&:($function ?" + domainClassLink.getChildName() + " ?" + domainClassLink.getChildName() + "-suffix" + ")",
				attributePattern.getValueText());
	}

	public void testCreateLinkAttributePatternForTestFunctionWithNullObjectNameWithSuffixHappyCase() throws Exception {
		DomainClassLink domainClassLink = ObjectMother.createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		// check with suffix
		resetLinkPatternConfigInvariants("$function", "-SFX" + ObjectMother.createInt(), true);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.expectAndReturn(helperMock.asVariableName(domainClassLink.getChildName()), "?" + domainClassLink.getChildName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, null, usageType);
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals(
				"?"
						+ domainClassLink.getChildName()
						+ ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getLinkPatternConfig().getVariableSuffix(),
				attributePattern.getVariableName());
		assertEquals("&:($function ?" + domainClassLink.getChildName() + " ?" + domainClassLink.getChildName()
				+ ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getLinkPatternConfig().getVariableSuffix()
				+ ")", attributePattern.getValueText());
	}

	public void testCreateLinkAttributePatternForNonTestFunctionWithObjectNameHappyCase() throws Exception {
		DomainClassLink domainClassLink = ObjectMother.createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		resetLinkPatternConfigInvariants("", null, false);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, "object_name", usageType);
		assertTrue(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals("?object_name", attributePattern.getVariableName());
		assertNull(attributePattern.getValueText());
	}

	public void testCreateLinkAttributePatternForNonTestFunctionWithNullObjectNameHappyCase() throws Exception {
		DomainClassLink domainClassLink = ObjectMother.createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		resetLinkPatternConfigInvariants("", null, false);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.expectAndReturn(helperMock.asVariableName(domainClassLink.getChildName()), "?" + domainClassLink.getChildName());
		mockControl.replay();

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, null, usageType);
		assertTrue(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals("?" + domainClassLink.getChildName(), attributePattern.getVariableName());
		assertNull(attributePattern.getValueText());
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		mockControl = MockControl.createControl(PatternFactoryHelper.class);
		helperMock = (PatternFactoryHelper) mockControl.getMock();
		attributePatternFactory = new AttributePatternFactory(helperMock);
		usageType = TemplateUsageType.getAllInstances()[0];
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
