package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.attachColumnReference;
import static com.mindbox.pe.server.ServerTestObjectMother.attachReference;
import static com.mindbox.pe.server.ServerTestObjectMother.createColumnReference;
import static com.mindbox.pe.server.ServerTestObjectMother.createCondition;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClassLink;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainClassLink;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.DomainClassLinkPattern;

public class AttributePatternFactoryTest extends AbstractTestWithTestConfig {

	private AttributePatternFactory attributePatternFactory;
	private PatternFactoryHelper helperMock;
	private TemplateUsageType usageType;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		helperMock = createMock(PatternFactoryHelper.class);
		attributePatternFactory = new AttributePatternFactory(helperMock);
		usageType = TemplateUsageType.getAllInstances()[0];
	}

	@Test
	public void testCreateAttributePatternWithAnyValueOperatorHappyCase() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_ANY_VALUE);

		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
		assertTrue(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertTrue(UtilBase.isEmpty(attributePattern.getValueText()));
	}

	@Test
	public void testCreateAttributePatternWithColumnReferenceValueHappyCase() throws Exception {
		Condition condition = attachColumnReference(attachReference(createCondition()));
		condition.setOp(Condition.OP_EQUAL);

		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
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

	@Test
	public void testCreateAttributePatternWithIsEmptyOperatorHappyCase() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_IS_EMPTY);

		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType));
		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		replay(helperMock);

		com.mindbox.pe.xsd.config.RuleGenerationLHS.Value valueConfig = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).getLHSValueConfig(
				RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED);
		String valueStr = RuleGeneratorHelper.formatForStringType(valueConfig.getDeployValue(), UtilBase.asBoolean(valueConfig.isValueAsString(), false));
		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertEquals(" & " + valueStr, attributePattern.getValueText());
	}

	@Test
	public void testCreateAttributePatternWithIsNotEmptyOperatorHappyCase() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_IS_NOT_EMPTY);

		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType));
		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		replay(helperMock);

		com.mindbox.pe.xsd.config.RuleGenerationLHS.Value valueConfig = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).getLHSValueConfig(
				RuleGenerationConfigHelper.VAUE_TYPE_UNSPECIFIED);
		String valueStr = RuleGeneratorHelper.formatForStringType(valueConfig.getDeployValue(), UtilBase.asBoolean(valueConfig.isValueAsString(), false));
		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertEquals(" &:(/= " + "?" + condition.getReference().getAttributeName() + ' ' + valueStr + ")", attributePattern.getValueText());
	}

	@Test
	public void testCreateAttributePatternWithMathExpValueWithColRefHappyCase() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_GREATER);
		MathExpressionValue meValue = (MathExpressionValue) RuleElementFactory.getInstance().createValue(createColumnReference(), "+", createReference());
		condition.setValue(meValue);

		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(meValue.getAttributeReference().getAttributeName())).andReturn("?" + meValue.getAttributeReference().getAttributeName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
		assertFalse(attributePattern.isEmpty());
		assertTrue(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());

		PatternValueSlot valueSlot = (PatternValueSlot) attributePattern.getValueSlot();
		assertEquals(ValueSlot.Type.COLUMN_REFERENCE, valueSlot.getType());
		assertEquals(condition.getOp(), valueSlot.getOperator());
		assertEquals(condition.getReference(), valueSlot.getReference());
		assertEquals(meValue.getColumnReference().getColumnNo(), ((Integer) valueSlot.getSlotValue()).intValue());
		assertEquals("(" + meValue.getOperator() + " {0} " + "?" + meValue.getAttributeReference().getAttributeName() + ")", valueSlot.getSlotText());
	}

	@Test
	public void testCreateAttributePatternWithMathExpValueWithStrValueHappyCase() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_GREATER);
		String valueStr = "value-" + createString();
		MathExpressionValue meValue = (MathExpressionValue) RuleElementFactory.getInstance().createValue(valueStr, "*", createReference());
		condition.setValue(meValue);

		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(meValue.getAttributeReference().getAttributeName())).andReturn("?" + meValue.getAttributeReference().getAttributeName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
		assertFalse(attributePattern.isEmpty());
		assertTrue(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertEquals("(" + meValue.getOperator() + " " + valueStr + " ?" + meValue.getAttributeReference().getAttributeName() + ")", attributePattern.getValueSlot().getSlotValue());
	}

	@Test
	public void testCreateAttributePatternWithNonNullStringValueHappyCaseForNonStringDeploy() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_BETWEEN);
		String valueStr = "value" + createInt();
		Value value = RuleElementFactory.getInstance().createValue("\"" + valueStr + "\"");
		condition.setValue(value);

		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		expect(helperMock.isStringDeployTypeForAttribute(condition.getReference())).andReturn(false);
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
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

	@Test
	public void testCreateAttributePatternWithNonNullStringValueHappyCaseForStringDeploy() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_BETWEEN);
		Value value = RuleElementFactory.getInstance().createValue("value" + createInt());
		condition.setValue(value);

		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		expect(helperMock.isStringDeployTypeForAttribute(condition.getReference())).andReturn(true);
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
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

	@Test
	public void testCreateAttributePatternWithNullConditionThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				attributePatternFactory,
				"createAttributePattern",
				new Class[] { Condition.class, String.class, TemplateUsageType.class },
				new Object[] { null, "", usageType });
	}

	@Test
	public void testCreateAttributePatternWithNullReferenceThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(attributePatternFactory, "createAttributePattern", new Class[] { Condition.class, String.class, TemplateUsageType.class }, new Object[] {
				RuleElementFactory.getInstance().createCondition(),
				"",
				usageType });
	}

	@Test
	public void testCreateAttributePatternWithNullStringValueHappyCase() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_BETWEEN);

		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
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

	@Test
	public void testCreateAttributePatternWithReferenceValueHappyCase() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_EQUAL);
		Value referenceValue = RuleElementFactory.getInstance().createValue(createReference());
		condition.setValue(referenceValue);

		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(((Reference) referenceValue).getAttributeName())).andReturn("?" + ((Reference) referenceValue).getAttributeName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createAttributePattern(condition, null, usageType);
		verify(helperMock);
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals("pe:" + condition.getReference().getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + condition.getReference().getAttributeName(), attributePattern.getVariableName());
		assertEquals(
				"&:(" + RuleGeneratorHelper.EQUALIFY_FUNCTION + " ?" + condition.getReference().getAttributeName() + " ?" + ((Reference) referenceValue).getAttributeName() + ')',
				attributePattern.getValueText());
	}

	@Test
	public void testCreateLinkAttributePatternForNonTestFunctionWithNullObjectNameHappyCase() throws Exception {
		DomainClassLink domainClassLink = createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		resetLinkPatternConfigInvariants("", null, false);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		expect(helperMock.asVariableName(domainClassLink.getChildName())).andReturn("?" + domainClassLink.getChildName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, null, usageType);
		assertTrue(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals("?" + domainClassLink.getChildName(), attributePattern.getVariableName());
		assertNull(attributePattern.getValueText());
	}

	@Test
	public void testCreateLinkAttributePatternForNonTestFunctionWithObjectNameHappyCase() throws Exception {
		DomainClassLink domainClassLink = createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		resetLinkPatternConfigInvariants("", null, false);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, "object_name", usageType);
		assertTrue(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals("?object_name", attributePattern.getVariableName());
		assertNull(attributePattern.getValueText());
	}

	@Test
	public void testCreateLinkAttributePatternForTestFunctionWithNullObjectNameNoSuffixHappyCase() throws Exception {
		DomainClassLink domainClassLink = createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		// check with null suffix (for default suffix)
		resetLinkPatternConfigInvariants("$function", null, true);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		expect(helperMock.asVariableName(domainClassLink.getChildName())).andReturn("?" + domainClassLink.getChildName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, null, usageType);
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals("?" + domainClassLink.getChildName() + "-suffix", attributePattern.getVariableName());
		assertEquals("&:($function ?" + domainClassLink.getChildName() + " ?" + domainClassLink.getChildName() + "-suffix" + ")", attributePattern.getValueText());
	}

	@Test
	public void testCreateLinkAttributePatternForTestFunctionWithNullObjectNameWithSuffixHappyCase() throws Exception {
		DomainClassLink domainClassLink = createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		// check with suffix
		resetLinkPatternConfigInvariants("$function", "-SFX" + createInt(), true);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		expect(helperMock.asVariableName(domainClassLink.getChildName())).andReturn("?" + domainClassLink.getChildName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, null, usageType);
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals(
				"?" + domainClassLink.getChildName() + ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getLinkPatternConfig().getVariableSuffix(),
				attributePattern.getVariableName());
		assertEquals("&:($function ?" + domainClassLink.getChildName() + " ?" + domainClassLink.getChildName()
				+ ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getLinkPatternConfig().getVariableSuffix() + ")", attributePattern.getValueText());
	}

	@Test
	public void testCreateLinkAttributePatternForTestFunctionWithObjectNameHappyCase() throws Exception {
		DomainClassLink domainClassLink = createDomainClassLink();
		domainClassLink.setDeployValueName("pe:" + domainClassLink.getChildName());

		resetLinkPatternConfigInvariants("$function", null, true);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		expect(helperMock.asVariableName(domainClassLink.getChildName())).andReturn("?" + domainClassLink.getChildName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, "object_name", usageType);
		assertFalse(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getDeployValueName(), attributePattern.getAttributeName());
		assertEquals("?object_name", attributePattern.getVariableName());
		assertEquals("&:($function ?" + domainClassLink.getChildName() + " ?object_name)", attributePattern.getValueText());
	}

	@Test
	public void testCreateLinkAttributePatternGeneratesAttributeNameIfDeployNameValueIsNull() throws Exception {
		DomainClassLink domainClassLink = createDomainClassLink();
		domainClassLink.setDeployValueName(null);

		resetLinkPatternConfigInvariants("", null, false);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		expect(helperMock.asVariableName(domainClassLink.getChildName())).andReturn("?" + domainClassLink.getChildName());
		replay(helperMock);

		AttributePattern attributePattern = attributePatternFactory.createLinkAttributePattern(domainClassLink, "object_name", usageType);
		assertTrue(attributePattern.isEmpty());
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(domainClassLink.getChildName() + "-link", attributePattern.getAttributeName());
		assertEquals("?object_name", attributePattern.getVariableName());
	}

	@Test
	public void testCreateLinkAttributePatternWithNullDomainClassLinkPatternThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(attributePatternFactory, "createLinkAttributePattern", new Class[] { DomainClassLinkPattern.class, TemplateUsageType.class }, new Object[] { null, usageType });
	}

	@Test
	public void testCreateLinkAttributePatternWithNullDomainClassLinkThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(attributePatternFactory, "createLinkAttributePattern", new Class[] { DomainClassLink.class, String.class, TemplateUsageType.class }, new Object[] {
				null,
				"",
				usageType });
	}
}
