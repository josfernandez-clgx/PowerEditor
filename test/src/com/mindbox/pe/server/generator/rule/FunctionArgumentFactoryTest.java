package com.mindbox.pe.server.generator.rule;

import org.easymock.MockControl;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public class FunctionArgumentFactoryTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("FunctionArgumentFactoryTest Tests");
		suite.addTestSuite(FunctionArgumentFactoryTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private MockControl patternFactoryHelperMockControl;
	private MockControl functionParameterContainerMockControl;
	private PatternFactoryHelper mockPatternFactoryHelper;
	private FunctionParameterContainer mockFunctionParameterContainer;
	private FunctionArgumentFactory functionArgumentFactory;

	public FunctionArgumentFactoryTest(String name) {
		super(name);
	}

	public void testCreateFunctionArgumentWithNullFunctionParameterContainerThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				functionArgumentFactory,
				"createFunctionArgument",
				new Class[] { int.class, FunctionParameterContainer.class, LHSPatternList.class },
				new Object[] { new Integer(1), null, null });
	}

	public void testCreateFunctionArgumentWithColumnReferenceHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = ObjectMother.createFunctionParameterDefinition();
		functionParameterDefinition.setDeployType(DeployType.SYMBOL);

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		int columnNo = ObjectMother.createInt();
		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameter(1, columnNo);

		functionParameterContainerMockControl.expectAndReturn(mockFunctionParameterContainer.getParameterAt(0), functionParameter);
		functionParameterContainerMockControl.expectAndReturn(
				mockFunctionParameterContainer.getFunctionTypeDefinition(),
				functionTypeDefinition);
		replayAll();

		FunctionArgument functionArgument = functionArgumentFactory.createFunctionArgument(1, mockFunctionParameterContainer, null);
		assertTrue(functionArgument instanceof ColumnReferencePatternValueSlot);
		assertEquals(DeployType.SYMBOL, ((ColumnReferencePatternValueSlot) functionArgument).getParameterDeployType());
		assertEquals(columnNo, ((ColumnReferencePatternValueSlot) functionArgument).getColumnNo());
		assertNull(((ColumnReferencePatternValueSlot) functionArgument).getReference());
		verifyAll();
	}

	public void testCreateFunctionArgumentWithAttrReferenceHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = ObjectMother.createFunctionParameterDefinition();

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		Reference reference = ObjectMother.createReference();
		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameter(1, reference);

		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();

		functionParameterContainerMockControl.expectAndReturn(mockFunctionParameterContainer.getParameterAt(0), functionParameter);
		functionParameterContainerMockControl.expectAndReturn(
				mockFunctionParameterContainer.getFunctionTypeDefinition(),
				functionTypeDefinition);

		String variableName = "?var-" + ObjectMother.createString();
		patternFactoryHelperMockControl.expectAndReturn(
				mockPatternFactoryHelper.asVariableName(reference.getAttributeName()),
				variableName,
				2);
		patternFactoryHelperMockControl.expectAndReturn(mockPatternFactoryHelper.asVariableName(reference.getClassName()), "?"
				+ reference.getClassName());
		patternFactoryHelperMockControl.expectAndReturn(mockPatternFactoryHelper.getDeployLabelForAttribute(reference), "pe:"
				+ reference.getAttributeName());
		patternFactoryHelperMockControl.expectAndReturn(mockPatternFactoryHelper.getDeployLabelForClass(reference.getClassName()), "pe:"
				+ reference.getClassName());
		replayAll();

		FunctionArgument functionArgument = functionArgumentFactory.createFunctionArgument(1, mockFunctionParameterContainer, patternList);
		assertTrue(functionArgument instanceof StaticFunctionArgument);
		assertEquals(variableName, ((StaticFunctionArgument) functionArgument).getValue());
		assertEquals(1, patternList.size());
		assertEquals("?" + reference.getClassName(), ((ObjectPattern) patternList.get(0)).getVariableName());
		assertEquals(1, ((ObjectPattern) patternList.get(0)).size());
		assertEquals(variableName, ((ObjectPattern) patternList.get(0)).get(0).getVariableName());
		verifyAll();
	}

	public void testCreateFunctionArgumentWithClassReferenceHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = ObjectMother.createFunctionParameterDefinition();

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		String className = "class-" + ObjectMother.createString();
		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameterForClassReference(1, className);

		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();

		functionParameterContainerMockControl.expectAndReturn(mockFunctionParameterContainer.getParameterAt(0), functionParameter);
		functionParameterContainerMockControl.expectAndReturn(
				mockFunctionParameterContainer.getFunctionTypeDefinition(),
				functionTypeDefinition);

		String variableName = "?" + className;
		patternFactoryHelperMockControl.expectAndReturn(mockPatternFactoryHelper.asVariableName(className), variableName, 2);
		patternFactoryHelperMockControl.expectAndReturn(mockPatternFactoryHelper.getDeployLabelForClass(className), "pe:" + className);
		replayAll();

		FunctionArgument functionArgument = functionArgumentFactory.createFunctionArgument(1, mockFunctionParameterContainer, patternList);
		assertTrue(functionArgument instanceof StaticFunctionArgument);
		assertEquals(variableName, ((StaticFunctionArgument) functionArgument).getValue());
		assertEquals(1, patternList.size());
		assertEquals(variableName, ((ObjectPattern) patternList.get(0)).getVariableName());
		assertEquals(0, ((ObjectPattern) patternList.get(0)).size());
		verifyAll();
	}

	public void testCreateFunctionArgumentWithStringValueForStringDeployTypeHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = ObjectMother.createFunctionParameterDefinition();
		functionParameterDefinition.setDeployType(DeployType.STRING);

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameterForStringValue(1);

		functionParameterContainerMockControl.expectAndReturn(mockFunctionParameterContainer.getParameterAt(0), functionParameter);
		functionParameterContainerMockControl.expectAndReturn(
				mockFunctionParameterContainer.getFunctionTypeDefinition(),
				functionTypeDefinition);
		replayAll();

		FunctionArgument functionArgument = functionArgumentFactory.createFunctionArgument(1, mockFunctionParameterContainer, null);
		assertTrue(functionArgument instanceof StaticFunctionArgument);
		assertEquals(
				RuleGeneratorHelper.QUOTE + functionParameter.valueString() + RuleGeneratorHelper.QUOTE,
				((StaticFunctionArgument) functionArgument).getValue());
		verifyAll();
	}

	public void testCreateFunctionArgumentWithStringValueForNonStringDeployTypeHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = ObjectMother.createFunctionParameterDefinition();
		functionParameterDefinition.setDeployType(DeployType.SYMBOL);

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameterForStringValue(1);

		functionParameterContainerMockControl.expectAndReturn(mockFunctionParameterContainer.getParameterAt(0), functionParameter);
		functionParameterContainerMockControl.expectAndReturn(
				mockFunctionParameterContainer.getFunctionTypeDefinition(),
				functionTypeDefinition);
		replayAll();

		FunctionArgument functionArgument = functionArgumentFactory.createFunctionArgument(1, mockFunctionParameterContainer, null);
		assertTrue(functionArgument instanceof StaticFunctionArgument);
		assertEquals(functionParameter.valueString(), ((StaticFunctionArgument) functionArgument).getValue());
		verifyAll();
	}

	protected void setUp() throws Exception {
		super.setUp();
		patternFactoryHelperMockControl = MockControl.createControl(PatternFactoryHelper.class);
		mockPatternFactoryHelper = (PatternFactoryHelper) patternFactoryHelperMockControl.getMock();
		functionParameterContainerMockControl = MockControl.createControl(FunctionParameterContainer.class);
		mockFunctionParameterContainer = (FunctionParameterContainer) functionParameterContainerMockControl.getMock();
		functionArgumentFactory = new FunctionArgumentFactory(mockPatternFactoryHelper);
	}

	protected void replayAll() {
		patternFactoryHelperMockControl.replay();
		functionParameterContainerMockControl.replay();
	}

	protected void verifyAll() {
		patternFactoryHelperMockControl.verify();
		functionParameterContainerMockControl.verify();
	}
}
