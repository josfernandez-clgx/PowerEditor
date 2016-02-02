package com.mindbox.pe.server.generator.rule;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.DomainClassLinkPattern;

public class ObjectPatternFactoryTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ObjectPatternFactoryTest Tests");
		suite.addTestSuite(ObjectPatternFactoryTest.class);
		return suite;
	}

	private PatternFactoryHelper helperMock;
	private MockControl mockControl;
	private TemplateUsageType usageType;
	private ObjectPatternFactory objectPatternFactory;

	public ObjectPatternFactoryTest(String name) {
		super(name);
	}

	public void testConstructorWithNullHelperThrowsNullPointerException() throws Exception {
		try {
			new ObjectPatternFactory(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}
	
	public void testCreateControlPatternWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createControlPattern", new Class[]
			{ TemplateUsageType.class});
	}

	public void testCreateControlPatternWithControlPatternTurnedOffReturnsNull() throws Exception {
		resetControlPatternConfigInvariants(false, "className", null);
		mockControl.expectAndReturn(helperMock.getRuleGenerationConfiguration(usageType), ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.replay();

		assertNull(objectPatternFactory.createControlPattern(usageType));
	}

	public void testCreateControlPatternHappyCase() throws Exception {
		resetControlPatternConfigInvariants(true, ObjectMother.createString(), null);
		RuleGenerationConfiguration.ControlPatternConfig controlPatternConfig = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getControlPatternConfig();

		// set up mockups
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(controlPatternConfig.getPatternClassName()), "pe:"
				+ controlPatternConfig.getPatternClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(controlPatternConfig.getPatternClassName()), "?"
				+ controlPatternConfig.getPatternClassName());
		GenericEntityType[] entityTypes = GenericEntityType.getAllGenericEntityTypes();
		List<DomainAttribute> domainAttributeList = new LinkedList<DomainAttribute>();
		for (int i = 0; i < entityTypes.length; i++) {
			if (entityTypes[i].isUsedInContext() && !controlPatternConfig.isDisallowed(entityTypes[i])) {
				DomainAttribute domainAttribute = ObjectMother.createDomainAttribute();
				if (i % 2 == 0) {
					domainAttribute.setDeployType(DeployType.STRING);
				}
				mockControl.expectAndReturn(helperMock.findDomainAttributeForContextElement(
						controlPatternConfig,
						entityTypes[i].getName()), domainAttribute);
				mockControl.expectAndReturn(helperMock.asVariableName(domainAttribute.getName()), "?" + domainAttribute.getName());
				domainAttributeList.add(domainAttribute);
			}
		}
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createControlPattern(usageType);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + controlPatternConfig.getPatternClassName(), objectPattern.getClassName());
		assertEquals("?" + controlPatternConfig.getPatternClassName(), objectPattern.getVariableName());
		assertEquals(domainAttributeList.size(), objectPattern.size());
		for (int i = 0; i < domainAttributeList.size(); i++) {
			AttributePattern attributePattern = objectPattern.get(i);
			DomainAttribute domainAttribute = domainAttributeList.get(i);
			assertEquals(domainAttribute.getDeployLabel(), attributePattern.getAttributeName());
			assertEquals("?" + domainAttribute.getName(), attributePattern.getVariableName());
			assertTrue(attributePattern.getValueSlot() instanceof ContextElementPatternValueSlot);
			assertEquals(
					domainAttribute.getDeployType() == DeployType.STRING,
					((ContextElementPatternValueSlot) attributePattern.getValueSlot()).asString());
		}
	}

	public void testCreateRequestPatternWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createRequestPattern", new Class[]
			{ TemplateUsageType.class});
	}

	public void testCreateRequestPatternWithControlPatternTurnedOffReturnsNull() throws Exception {
		resetRequestPatternConfigInvariants(false, "className", "prefix", false);
		mockControl.expectAndReturn(helperMock.getRuleGenerationConfiguration(usageType), ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.replay();

		assertNull(objectPatternFactory.createRequestPattern(usageType));
	}

	public void testCreateRequestPatternHappyCaseWithUsageAsFocusTrue() throws Exception {
		resetRequestPatternConfigInvariants(true, ObjectMother.createString(), ObjectMother.createString(), true);
		RuleGenerationConfiguration.RequestPatternConfig requestPatternConfig = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getRequestPatternConfig();

		mockControl.expectAndReturn(helperMock.getRuleGenerationConfiguration(usageType), ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createRequestPattern(usageType);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals(requestPatternConfig.getPrefix() + requestPatternConfig.getPatternClassName(), objectPattern.getClassName());
		assertEquals("?" + requestPatternConfig.getPatternClassName(), objectPattern.getVariableName());
		assertEquals(2, objectPattern.size());
		AttributePattern attributePattern = objectPattern.get(0);
		assertEquals(requestPatternConfig.getPrefix() + "focus-of-attention", attributePattern.getAttributeName());
		assertTrue(attributePattern.getValueSlot() instanceof FocusOfAttentionPatternValueSlot);
		attributePattern = objectPattern.get(1);
		assertEquals(requestPatternConfig.getPrefix() + "current-time-slice", attributePattern.getAttributeName());
		assertEquals(RuleGeneratorHelper.TIME_SLICE_VARIABLE, attributePattern.getVariableName());
		assertTrue(attributePattern.getValueSlot() instanceof TimeSlicePatternValueSlot);
	}

	public void testCreateRequestPatternHappyCaseWithUsageAsFocusFalse() throws Exception {
		resetRequestPatternConfigInvariants(true, ObjectMother.createString(), ObjectMother.createString(), false);
		RuleGenerationConfiguration.RequestPatternConfig requestPatternConfig = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getRequestPatternConfig();

		mockControl.expectAndReturn(helperMock.getRuleGenerationConfiguration(usageType), ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createRequestPattern(usageType);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals(requestPatternConfig.getPrefix() + requestPatternConfig.getPatternClassName(), objectPattern.getClassName());
		assertEquals("?" + requestPatternConfig.getPatternClassName(), objectPattern.getVariableName());
		assertEquals(2, objectPattern.size());
		AttributePattern attributePattern = objectPattern.get(0);
		assertEquals(requestPatternConfig.getPrefix() + "focus-of-attention", attributePattern.getAttributeName());
		assertTrue(attributePattern.getValueSlot() instanceof FocusOfAttentionPatternValueSlot);
		attributePattern = objectPattern.get(1);
		assertEquals(requestPatternConfig.getPrefix() + "current-time-slice", attributePattern.getAttributeName());
		assertEquals(RuleGeneratorHelper.TIME_SLICE_VARIABLE, attributePattern.getVariableName());
		assertTrue(attributePattern.getValueSlot() instanceof TimeSlicePatternValueSlot);
	}

	public void testCreateEmptyObjectPatternForReferenceWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createEmptyObjectPattern", new Class[]
			{ Reference.class});
	}

	public void testCreateEmptyObjectPatternForReferenceHappyCase() throws Exception {
		Reference reference = ObjectMother.createReference();
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(reference.getClassName()), "pe:" + reference.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(reference.getClassName()), "?" + reference.getClassName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(reference.getClassName());
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + reference.getClassName(), objectPattern.getClassName());
		assertEquals("?" + reference.getClassName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	public void testCreateEmptyObjectPatternForStringWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createEmptyObjectPattern", new Class[]
			{ String.class});
	}

	public void testCreateEmptyObjectPatternForStringHappyCase() throws Exception {
		String className = ObjectMother.createString();
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(className), "pe:" + className);
		mockControl.expectAndReturn(helperMock.asVariableName(className), "?" + className);
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(className);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + className, objectPattern.getClassName());
		assertEquals("?" + className, objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	public void testCreateEmptyObjectPatternForExistWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createEmptyObjectPattern", new Class[]
			{ ExistExpression.class});
	}

	public void testCreateEmptyObjectPatternForExistHappyCaseWithNoObjectNameAndNoExcludedName() throws Exception {
		ExistExpression existExpression = ObjectMother.createExistExpression();

		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression.getClassName()), "pe:" + existExpression.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression.getClassName()), "?" + existExpression.getClassName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(existExpression);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + existExpression.getClassName(), objectPattern.getClassName());
		assertEquals("?" + existExpression.getClassName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	public void testCreateEmptyObjectPatternForExistHappyCaseWithObjectNameAndNoExcludedName() throws Exception {
		ExistExpression existExpression = ObjectMother.createExistExpression();
		existExpression.setObjectName(ObjectMother.createString());

		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression.getClassName()), "pe:" + existExpression.getClassName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(existExpression);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + existExpression.getClassName(), objectPattern.getClassName());
		assertEquals("?" + existExpression.getObjectName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	public void testCreateEmptyObjectPatternForExistHappyCaseWithNoObjectNameAndExcludedName() throws Exception {
		ExistExpression existExpression = ObjectMother.createExistExpression();
		existExpression.setExcludedObjectName(ObjectMother.createString());

		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression.getClassName()), "pe:" + existExpression.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression.getClassName()), "?" + existExpression.getClassName());
		mockControl.expectAndReturn(helperMock.formatForExcludedObject(existExpression.getExcludedObjectName()), "?" + existExpression.getExcludedObjectName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(existExpression);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + existExpression.getClassName(), objectPattern.getClassName());
		assertEquals("?" + existExpression.getClassName() + " ?" + existExpression.getExcludedObjectName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	public void testCreateEmptyObjectPatternForExistHappyCaseWithObjectNameAndExcludedName() throws Exception {
		ExistExpression existExpression = ObjectMother.createExistExpression();
		existExpression.setObjectName(ObjectMother.createString());
		existExpression.setExcludedObjectName(ObjectMother.createString());

		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression.getClassName()), "pe:" + existExpression.getClassName());
		mockControl.expectAndReturn(helperMock.formatForExcludedObject(existExpression.getExcludedObjectName()), "?" + existExpression.getExcludedObjectName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createEmptyObjectPattern(existExpression);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + existExpression.getClassName(), objectPattern.getClassName());
		assertEquals("?" + existExpression.getObjectName() + " ?" + existExpression.getExcludedObjectName(), objectPattern.getVariableName());
		assertTrue(objectPattern.isEmpty());
	}

	public void testCreateObjectPatternWithNullArgThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(objectPatternFactory, "createObjectPattern", new Class[]
			{ Condition.class, TemplateUsageType.class}, new Object[]
			{ null, usageType});
		assertThrowsNullPointerException(objectPatternFactory, "createObjectPattern", new Class[]
			{ Condition.class, TemplateUsageType.class}, new Object[]
			{ ObjectMother.createCondition(), null});
	}

	public void testCreateObjectPatternHappyCaseWithObjectName() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_ANY_VALUE);
		condition.setObjectName(ObjectMother.createString());

		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition.getReference()), "pe:" + condition.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getObjectName()), "?" + condition.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getClassName(), "?" + condition.getObjectName()), "?" + condition.getObjectName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:" + condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?" + condition.getReference().getAttributeName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createObjectPattern(condition, usageType);
		mockControl.verify();

		assertTrue(objectPattern.canBeSkipped());
		assertEquals("pe:" + condition.getReference().getClassName(), objectPattern.getClassName());
		assertEquals("?" + condition.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// Kim: don't need to test details of the attribute pattern here, as it's tested in {@link AttributePatternFactoryTest}
	}

	public void testCreateObjectPatternHappyCaseWithNoObjectName() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_ANY_VALUE);

		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition.getReference()), "pe:" + condition.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getClassName(), null), "?" + condition.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:" + condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?" + condition.getReference().getAttributeName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createObjectPattern(condition, usageType);
		mockControl.verify();

		assertTrue(objectPattern.canBeSkipped());
		assertEquals("pe:" + condition.getReference().getClassName(), objectPattern.getClassName());
		assertEquals("?" + condition.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// Kim: don't need to test details of the attribute pattern here, as it's tested in {@link AttributePatternFactoryTest}
	}

	public void testCreateObjectPatternHappyCaseWithObjectVarOverride() throws Exception {
		Condition condition = ObjectMother.attachReference(ObjectMother.createCondition());
		condition.setOp(Condition.OP_ANY_VALUE);

		String varOverride = ObjectMother.createString();
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition.getReference()), "pe:" + condition.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getClassName(), varOverride), varOverride);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition.getReference()), "pe:" + condition.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition.getReference().getAttributeName(), null), "?" + condition.getReference().getAttributeName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createObjectPattern(condition, usageType, varOverride);
		mockControl.verify();

		assertTrue(objectPattern.canBeSkipped());
		assertEquals("pe:" + condition.getReference().getClassName(), objectPattern.getClassName());
		assertEquals(varOverride, objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
	}
	
	public void testCreateLinkObjectPatternNullArgThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(objectPatternFactory, "createLinkObjectPattern", new Class[]
			{ DomainClassLinkPattern.class, TemplateUsageType.class}, new Object[]
			{ null, usageType});
		assertThrowsNullPointerException(objectPatternFactory, "createLinkObjectPattern", new Class[]
			{ DomainClassLinkPattern.class, TemplateUsageType.class}, new Object[]
			{ ObjectMother.createDomainClassLinkPattern(), null});
	}

	public void testCreateLinkObjectPatternHappyCase() throws Exception {
		DomainClassLinkPattern domainClassLinkPattern = ObjectMother.createDomainClassLinkPattern();

		resetLinkPatternConfigInvariants("", null, false);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(domainClassLinkPattern.getDomainClassLink().getParentName()), "pe:"
				+ domainClassLinkPattern.getDomainClassLink().getParentName());
		mockControl.expectAndReturn(helperMock.asVariableName(domainClassLinkPattern.getDomainClassLink().getParentName()), "?"
				+ domainClassLinkPattern.getDomainClassLink().getParentName());
		mockControl.expectAndReturn(helperMock.getRuleGenerationConfiguration(usageType), ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
		mockControl.expectAndReturn(helperMock.asVariableName(domainClassLinkPattern.getDomainClassLink().getChildName()), "?"
				+ domainClassLinkPattern.getDomainClassLink().getChildName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createLinkObjectPattern(domainClassLinkPattern, usageType);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + domainClassLinkPattern.getDomainClassLink().getParentName(), objectPattern.getClassName());
		assertEquals("?" + domainClassLinkPattern.getDomainClassLink().getParentName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// Kim: don't need to test details of the attribute pattern here, as it's tested in {@link AttributePatternFactoryTest}
	}

	public void testCreateSingleAttrbiuteObjectPatternWithNullReferenceThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPatternFactory, "createSingleAttrbiuteObjectPattern", new Class[]
			{ Reference.class});
	}

	public void testCreateSingleAttrbiuteObjectPatternHappyCaseWithAttribute() throws Exception {
		Reference reference = ObjectMother.createReference();
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(reference.getClassName()), "pe:" + reference.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(reference.getClassName()), "?" + reference.getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(reference), "pe:" + reference.getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(reference.getAttributeName()), "?" + reference.getAttributeName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createSingleAttrbiuteObjectPattern(reference);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + reference.getClassName(), objectPattern.getClassName());
		assertEquals("?" + reference.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		AttributePattern attributePattern = objectPattern.get(0);
		assertEquals("pe:" + reference.getAttributeName(), attributePattern.getAttributeName());
		assertEquals("?" + reference.getAttributeName(), attributePattern.getVariableName());
		assertTrue(attributePattern.isEmpty());
	}

	public void testCreateSingleAttrbiuteObjectPatternHappyCaseWithoutAttribute() throws Exception {
		Reference reference = ObjectMother.createReference();
		reference.setAttributeName(null);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(reference.getClassName()), "pe:" + reference.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(reference.getClassName()), "?" + reference.getClassName());
		mockControl.replay();

		ObjectPattern objectPattern = objectPatternFactory.createSingleAttrbiuteObjectPattern(reference);
		mockControl.verify();

		assertFalse(objectPattern.canBeSkipped());
		assertEquals("pe:" + reference.getClassName(), objectPattern.getClassName());
		assertEquals("?" + reference.getClassName(), objectPattern.getVariableName());
		assertEquals(0, objectPattern.size());
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		mockControl = MockControl.createControl(PatternFactoryHelper.class);
		helperMock = (PatternFactoryHelper) mockControl.getMock();
		objectPatternFactory = new ObjectPatternFactory(helperMock);
		usageType = TemplateUsageType.getAllInstances()[0];
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
