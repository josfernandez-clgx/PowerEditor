package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.attachReference;
import static com.mindbox.pe.server.ServerTestObjectMother.createCondition;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainAttribute;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClassLinkPattern;
import static com.mindbox.pe.server.ServerTestObjectMother.createExistExpression;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ControlPatternConfigHelper;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.DomainClassLinkPattern;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Pattern;

public class ObjectPatternFactoryTest extends AbstractTestWithTestConfig {

	private PatternFactoryHelper helperMock;
	private TemplateUsageType usageType;
	private ObjectPatternFactory objectPatternFactory;

	@Test
	public void testConstructorWithNullHelperThrowsNullPointerException() throws Exception {
		try {
			new ObjectPatternFactory(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testCreateControlPatternWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createControlPattern", new Class[] { TemplateUsageType.class });
	}

	@Test
	public void testCreateControlPatternWithControlPatternTurnedOffReturnsNull() throws Exception {
		resetControlPatternConfigInvariants(false, "className", null);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		replay(helperMock);

		assertNull(objectPatternFactory.createControlPattern(usageType));
	}

	@Test
	public void testCreateControlPatternHappyCase() throws Exception {
		resetControlPatternConfigInvariants(true, createString(), null);
		ControlPatternConfigHelper controlPatternConfig = ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getControlPatternConfig();

		// set up mockups
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(2);
		expect(helperMock.getDeployLabelForClass(controlPatternConfig.getPattern().getClazz())).andReturn("pe:" + controlPatternConfig.getPattern().getClazz());
		expect(helperMock.asVariableName(controlPatternConfig.getPattern().getClazz())).andReturn("?" + controlPatternConfig.getPattern().getClazz());
		GenericEntityType[] entityTypes = GenericEntityType.getAllGenericEntityTypes();
		List<DomainAttribute> domainAttributeList = new LinkedList<DomainAttribute>();
		for (int i = 0; i < entityTypes.length; i++) {
			if (entityTypes[i].isUsedInContext() && !controlPatternConfig.isDisallowed(entityTypes[i])) {
				DomainAttribute domainAttribute = createDomainAttribute();
				if (i % 2 == 0) {
					domainAttribute.setDeployType(DeployType.STRING);
				}
				expect(helperMock.findDomainAttributeForContextElement(controlPatternConfig, entityTypes[i].getName())).andReturn(domainAttribute);
				expect(helperMock.asVariableName(domainAttribute.getName())).andReturn("?" + domainAttribute.getName());
				domainAttributeList.add(domainAttribute);
			}
		}
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createControlPattern(usageType);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + controlPatternConfig.getPattern().getClazz(), objectPattern.getClassName());
		assertEquals("?" + controlPatternConfig.getPattern().getClazz(), objectPattern.getVariableName());
		assertEquals(domainAttributeList.size(), objectPattern.size());
		for (int i = 0; i < domainAttributeList.size(); i++) {
			AttributePattern attributePattern = objectPattern.get(i);
			DomainAttribute domainAttribute = domainAttributeList.get(i);
			assertEquals(domainAttribute.getDeployLabel(), attributePattern.getAttributeName());
			assertEquals("?" + domainAttribute.getName(), attributePattern.getVariableName());
			assertTrue(attributePattern.getValueSlot() instanceof ContextElementPatternValueSlot);
			assertEquals(domainAttribute.getDeployType() == DeployType.STRING, ((ContextElementPatternValueSlot) attributePattern.getValueSlot()).asString());
		}
	}

	@Test
	public void testCreateRequestPatternWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createRequestPattern", new Class[] { TemplateUsageType.class });
	}

	@Test
	public void testCreateRequestPatternWithControlPatternTurnedOffReturnsNull() throws Exception {
		resetRequestPatternConfigInvariants(false, "className", "prefix", false);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		replay(helperMock);

		assertNull(objectPatternFactory.createRequestPattern(usageType));
	}

	@Test
	public void testCreateRequestPatternHappyCaseWithUsageAsFocusTrue() throws Exception {
		resetRequestPatternConfigInvariants(true, createString(), createString(), true);
		Pattern requestPatternConfig = ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getRequestPatternConfig();

		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createRequestPattern(usageType);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals(requestPatternConfig.getPrefix() + requestPatternConfig.getClazz(), objectPattern.getClassName());
		assertEquals("?" + requestPatternConfig.getClazz(), objectPattern.getVariableName());
		assertEquals(2, objectPattern.size());
		AttributePattern attributePattern = objectPattern.get(0);
		assertEquals(requestPatternConfig.getPrefix() + "focus-of-attention", attributePattern.getAttributeName());
		assertTrue(attributePattern.getValueSlot() instanceof FocusOfAttentionPatternValueSlot);
		attributePattern = objectPattern.get(1);
		assertEquals(requestPatternConfig.getPrefix() + "current-time-slice", attributePattern.getAttributeName());
		assertEquals(RuleGeneratorHelper.TIME_SLICE_VARIABLE, attributePattern.getVariableName());
		assertTrue(attributePattern.getValueSlot() instanceof TimeSlicePatternValueSlot);
	}

	@Test
	public void testCreateRequestPatternHappyCaseWithUsageAsFocusFalse() throws Exception {
		resetRequestPatternConfigInvariants(true, createString(), createString(), false);
		Pattern requestPatternConfig = ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getRequestPatternConfig();

		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createRequestPattern(usageType);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals(requestPatternConfig.getPrefix() + requestPatternConfig.getClazz(), objectPattern.getClassName());
		assertEquals("?" + requestPatternConfig.getClazz(), objectPattern.getVariableName());
		assertEquals(2, objectPattern.size());
		AttributePattern attributePattern = objectPattern.get(0);
		assertEquals(requestPatternConfig.getPrefix() + "focus-of-attention", attributePattern.getAttributeName());
		assertTrue(attributePattern.getValueSlot() instanceof FocusOfAttentionPatternValueSlot);
		attributePattern = objectPattern.get(1);
		assertEquals(requestPatternConfig.getPrefix() + "current-time-slice", attributePattern.getAttributeName());
		assertEquals(RuleGeneratorHelper.TIME_SLICE_VARIABLE, attributePattern.getVariableName());
		assertTrue(attributePattern.getValueSlot() instanceof TimeSlicePatternValueSlot);
	}

	@Test
	public void testCreateEmptyObjectPatternForReferenceWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createEmptyObjectPattern", new Class[] { Reference.class });
	}

	@Test
	public void testCreateEmptyObjectPatternForReferenceHappyCase() throws Exception {
		Reference reference = createReference();
		expect(helperMock.getDeployLabelForClass(reference.getClassName())).andReturn("pe:" + reference.getClassName());
		expect(helperMock.asVariableName(reference.getClassName())).andReturn("?" + reference.getClassName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(reference.getClassName());
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + reference.getClassName(), objectPattern.getClassName());
		assertEquals("?" + reference.getClassName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	@Test
	public void testCreateEmptyObjectPatternForStringWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createEmptyObjectPattern", new Class[] { String.class });
	}

	@Test
	public void testCreateEmptyObjectPatternForStringHappyCase() throws Exception {
		String className = createString();
		expect(helperMock.getDeployLabelForClass(className)).andReturn("pe:" + className);
		expect(helperMock.asVariableName(className)).andReturn("?" + className);
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(className);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + className, objectPattern.getClassName());
		assertEquals("?" + className, objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	@Test
	public void testCreateEmptyObjectPatternForExistWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createEmptyObjectPattern", new Class[] { ExistExpression.class });
	}

	@Test
	public void testCreateEmptyObjectPatternForExistHappyCaseWithNoObjectNameAndNoExcludedName() throws Exception {
		ExistExpression existExpression = createExistExpression();

		expect(helperMock.getDeployLabelForClass(existExpression.getClassName())).andReturn("pe:" + existExpression.getClassName());
		expect(helperMock.asVariableName(existExpression.getClassName())).andReturn("?" + existExpression.getClassName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(existExpression);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + existExpression.getClassName(), objectPattern.getClassName());
		assertEquals("?" + existExpression.getClassName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	@Test
	public void testCreateEmptyObjectPatternForExistHappyCaseWithObjectNameAndNoExcludedName() throws Exception {
		ExistExpression existExpression = createExistExpression();
		existExpression.setObjectName(createString());

		expect(helperMock.getDeployLabelForClass(existExpression.getClassName())).andReturn("pe:" + existExpression.getClassName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(existExpression);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + existExpression.getClassName(), objectPattern.getClassName());
		assertEquals("?" + existExpression.getObjectName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	@Test
	public void testCreateEmptyObjectPatternForExistHappyCaseWithNoObjectNameAndExcludedName() throws Exception {
		ExistExpression existExpression = createExistExpression();
		existExpression.setExcludedObjectName(createString());

		expect(helperMock.getDeployLabelForClass(existExpression.getClassName())).andReturn("pe:" + existExpression.getClassName());
		expect(helperMock.asVariableName(existExpression.getClassName())).andReturn("?" + existExpression.getClassName());
		expect(helperMock.formatForExcludedObject(existExpression.getExcludedObjectName())).andReturn("?" + existExpression.getExcludedObjectName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(existExpression);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + existExpression.getClassName(), objectPattern.getClassName());
		assertEquals("?" + existExpression.getClassName() + " ?" + existExpression.getExcludedObjectName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	@Test
	public void testCreateEmptyObjectPatternForExistHappyCaseWithObjectNameAndExcludedName() throws Exception {
		ExistExpression existExpression = createExistExpression();
		existExpression.setObjectName(createString());
		existExpression.setExcludedObjectName(createString());

		expect(helperMock.getDeployLabelForClass(existExpression.getClassName())).andReturn("pe:" + existExpression.getClassName());
		expect(helperMock.formatForExcludedObject(existExpression.getExcludedObjectName())).andReturn("?" + existExpression.getExcludedObjectName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(existExpression);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + existExpression.getClassName(), objectPattern.getClassName());
		assertEquals("?" + existExpression.getObjectName() + " ?" + existExpression.getExcludedObjectName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	@Test
	public void testCreateObjectPatternWithNullArgThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(objectPatternFactory, "createObjectPattern", new Class[] { Condition.class, TemplateUsageType.class }, new Object[] { null, usageType });
		assertThrowsNullPointerException(objectPatternFactory, "createObjectPattern", new Class[] { Condition.class, TemplateUsageType.class }, new Object[] { createCondition(), null });
	}

	@Test
	public void testCreateObjectPatternHappyCaseWithObjectName() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_ANY_VALUE);
		condition.setObjectName(createString());

		expect(helperMock.getDeployLabelForClass(condition.getReference())).andReturn("pe:" + condition.getReference().getClassName());
		expect(helperMock.asVariableName(condition.getObjectName())).andReturn("?" + condition.getObjectName());
		expect(helperMock.asVariableName(condition.getReference().getClassName(), "?" + condition.getObjectName())).andReturn("?" + condition.getObjectName());
		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createObjectPattern(condition, usageType);
		verify(helperMock);

		assertTrue(objectPattern.canBeSkipped());
		assertEquals("pe:" + condition.getReference().getClassName(), objectPattern.getClassName());
		assertEquals("?" + condition.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// Kim: don't need to test details of the attribute pattern here, as it's tested in {@link AttributePatternFactoryTest}
	}

	@Test
	public void testCreateObjectPatternHappyCaseWithNoObjectName() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_ANY_VALUE);

		expect(helperMock.getDeployLabelForClass(condition.getReference())).andReturn("pe:" + condition.getReference().getClassName());
		expect(helperMock.asVariableName(condition.getReference().getClassName(), null)).andReturn("?" + condition.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createObjectPattern(condition, usageType);
		verify(helperMock);

		assertTrue(objectPattern.canBeSkipped());
		assertEquals("pe:" + condition.getReference().getClassName(), objectPattern.getClassName());
		assertEquals("?" + condition.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// Kim: don't need to test details of the attribute pattern here, as it's tested in {@link AttributePatternFactoryTest}
	}

	@Test
	public void testCreateObjectPatternHappyCaseWithObjectVarOverride() throws Exception {
		Condition condition = attachReference(createCondition());
		condition.setOp(Condition.OP_ANY_VALUE);

		String varOverride = createString();
		expect(helperMock.getDeployLabelForClass(condition.getReference())).andReturn("pe:" + condition.getReference().getClassName());
		expect(helperMock.asVariableName(condition.getReference().getClassName(), varOverride)).andReturn(varOverride);
		expect(helperMock.getDeployLabelForAttribute(condition.getReference())).andReturn("pe:" + condition.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition.getReference().getAttributeName(), null)).andReturn("?" + condition.getReference().getAttributeName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createObjectPattern(condition, usageType, varOverride);
		verify(helperMock);

		assertTrue(objectPattern.canBeSkipped());
		assertEquals("pe:" + condition.getReference().getClassName(), objectPattern.getClassName());
		assertEquals(varOverride, objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
	}

	@Test
	public void testCreateLinkObjectPatternNullArgThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(objectPatternFactory, "createLinkObjectPattern", new Class[] { DomainClassLinkPattern.class, TemplateUsageType.class }, new Object[] { null, usageType });
		assertThrowsNullPointerException(objectPatternFactory, "createLinkObjectPattern", new Class[] { DomainClassLinkPattern.class, TemplateUsageType.class }, new Object[] {
				createDomainClassLinkPattern(),
				null });
	}

	@Test
	public void testCreateLinkObjectPatternHappyCase() throws Exception {
		DomainClassLinkPattern domainClassLinkPattern = createDomainClassLinkPattern();

		resetLinkPatternConfigInvariants("", null, false);
		expect(helperMock.getDeployLabelForClass(domainClassLinkPattern.getDomainClassLink().getParentName())).andReturn("pe:" + domainClassLinkPattern.getDomainClassLink().getParentName());
		expect(helperMock.asVariableName(domainClassLinkPattern.getDomainClassLink().getParentName())).andReturn("?" + domainClassLinkPattern.getDomainClassLink().getParentName());
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
		expect(helperMock.asVariableName(domainClassLinkPattern.getDomainClassLink().getChildName())).andReturn("?" + domainClassLinkPattern.getDomainClassLink().getChildName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createLinkObjectPattern(domainClassLinkPattern, usageType);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + domainClassLinkPattern.getDomainClassLink().getParentName(), objectPattern.getClassName());
		assertEquals("?" + domainClassLinkPattern.getDomainClassLink().getParentName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// Kim: don't need to test details of the attribute pattern here, as it's tested in {@link AttributePatternFactoryTest}
	}

	@Test
	public void testCreateSingleAttrbiuteObjectPatternWithNullReferenceThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createSingleAttrbiuteObjectPattern", new Class[] { Reference.class });
	}

	@Test
	public void testCreateSingleAttrbiuteObjectPatternHappyCaseWithAttribute() throws Exception {
		Reference reference = createReference();
		expect(helperMock.getDeployLabelForClass(reference.getClassName())).andReturn("pe:" + reference.getClassName());
		expect(helperMock.asVariableName(reference.getClassName())).andReturn("?" + reference.getClassName());
		expect(helperMock.getDeployLabelForAttribute(reference)).andReturn("pe:" + reference.getAttributeName());
		expect(helperMock.asVariableName(reference.getAttributeName())).andReturn("?" + reference.getAttributeName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createSingleAttrbiuteObjectPattern(reference);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + reference.getClassName(), objectPattern.getClassName());
		assertEquals("?" + reference.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		AttributePattern attributePattern = objectPattern.get(0);
		assertEquals("pe:" + reference.getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + reference.getAttributeName(), attributePattern.getVariableName());
		assertTrue(attributePattern.isEmpty());
	}

	@Test
	public void testCreateSingleAttrbiuteObjectPatternHappyCaseWithoutAttribute() throws Exception {
		Reference reference = createReference();
		reference.setAttributeName(null);
		expect(helperMock.getDeployLabelForClass(reference.getClassName())).andReturn("pe:" + reference.getClassName());
		expect(helperMock.asVariableName(reference.getClassName())).andReturn("?" + reference.getClassName());
		replay(helperMock);

		ObjectPattern objectPattern = objectPatternFactory.createSingleAttrbiuteObjectPattern(reference);
		verify(helperMock);

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + reference.getClassName(), objectPattern.getClassName());
		assertEquals("?" + reference.getClassName(), objectPattern.getVariableName());
		assertEquals(0, objectPattern.size());
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		helperMock = createMock(PatternFactoryHelper.class);
		objectPatternFactory = new ObjectPatternFactory(helperMock);
		usageType = TemplateUsageType.getAllInstances()[0];
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}
}
