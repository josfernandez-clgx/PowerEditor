package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createFunctionParameterDefinition;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.unittest.AbstractTestBase;

public class FunctionArgumentFactoryTest extends AbstractTestBase {

	private PatternFactoryHelper mockPatternFactoryHelper;
	private FunctionParameterContainer mockFunctionParameterContainer;
	private FunctionArgumentFactory functionArgumentFactory;

	protected void replayAll() {
		replay(mockPatternFactoryHelper);
		replay(mockFunctionParameterContainer);
	}

	@Before
	public void setUp() throws Exception {
		mockPatternFactoryHelper = createMock(PatternFactoryHelper.class);
		mockFunctionParameterContainer = createMock(FunctionParameterContainer.class);
		functionArgumentFactory = new FunctionArgumentFactory(mockPatternFactoryHelper);
	}

	@Test
	public void testCreateFunctionArgumentWithAttrReferenceHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = createFunctionParameterDefinition();

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		Reference reference = createReference();
		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameter(1, reference);

		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();

		expect(mockFunctionParameterContainer.getParameterAt(0)).andReturn(functionParameter);
		expect(mockFunctionParameterContainer.getFunctionTypeDefinition()).andReturn(functionTypeDefinition);

		String variableName = "?var-" + createString();
		expect(mockPatternFactoryHelper.asVariableName(reference.getAttributeName())).andReturn(variableName).times(2);
		expect(mockPatternFactoryHelper.asVariableName(reference.getClassName())).andReturn("?" + reference.getClassName());
		expect(mockPatternFactoryHelper.getDeployLabelForAttribute(reference)).andReturn("pe:" + reference.getAttributeName());
		expect(mockPatternFactoryHelper.getDeployLabelForClass(reference.getClassName())).andReturn("pe:" + reference.getClassName());
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

	@Test
	public void testCreateFunctionArgumentWithClassReferenceHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = createFunctionParameterDefinition();

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		String className = "class-" + createString();
		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameterForClassReference(1, className);

		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();

		expect(mockFunctionParameterContainer.getParameterAt(0)).andReturn(functionParameter);
		expect(mockFunctionParameterContainer.getFunctionTypeDefinition()).andReturn(functionTypeDefinition);

		String variableName = "?" + className;
		expect(mockPatternFactoryHelper.asVariableName(className)).andReturn(variableName).times(2);
		expect(mockPatternFactoryHelper.getDeployLabelForClass(className)).andReturn("pe:" + className);
		replayAll();

		FunctionArgument functionArgument = functionArgumentFactory.createFunctionArgument(1, mockFunctionParameterContainer, patternList);
		assertTrue(functionArgument instanceof StaticFunctionArgument);
		assertEquals(variableName, ((StaticFunctionArgument) functionArgument).getValue());
		assertEquals(1, patternList.size());
		assertEquals(variableName, ((ObjectPattern) patternList.get(0)).getVariableName());
		assertEquals(0, ((ObjectPattern) patternList.get(0)).size());
		verifyAll();
	}

	@Test
	public void testCreateFunctionArgumentWithColumnReferenceHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = createFunctionParameterDefinition();
		functionParameterDefinition.setDeployType(DeployType.SYMBOL);

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		int columnNo = createInt();
		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameter(1, columnNo);

		expect(mockFunctionParameterContainer.getParameterAt(0)).andReturn(functionParameter);
		expect(mockFunctionParameterContainer.getFunctionTypeDefinition()).andReturn(functionTypeDefinition);
		replayAll();

		FunctionArgument functionArgument = functionArgumentFactory.createFunctionArgument(1, mockFunctionParameterContainer, null);
		assertTrue(functionArgument instanceof ColumnReferencePatternValueSlot);
		assertEquals(DeployType.SYMBOL, ((ColumnReferencePatternValueSlot) functionArgument).getParameterDeployType());
		assertEquals(columnNo, ((ColumnReferencePatternValueSlot) functionArgument).getColumnNo());
		assertNull(((ColumnReferencePatternValueSlot) functionArgument).getReference());
		verifyAll();
	}

	@Test
	public void testCreateFunctionArgumentWithNullFunctionParameterContainerThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(functionArgumentFactory, "createFunctionArgument", new Class[] { int.class,
				FunctionParameterContainer.class, LHSPatternList.class }, new Object[] { new Integer(1), null, null });
	}

	@Test
	public void testCreateFunctionArgumentWithStringValueForNonStringDeployTypeHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = createFunctionParameterDefinition();
		functionParameterDefinition.setDeployType(DeployType.SYMBOL);

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameterForStringValue(1);

		expect(mockFunctionParameterContainer.getParameterAt(0)).andReturn(functionParameter);
		expect(mockFunctionParameterContainer.getFunctionTypeDefinition()).andReturn(functionTypeDefinition);
		replayAll();

		FunctionArgument functionArgument = functionArgumentFactory.createFunctionArgument(1, mockFunctionParameterContainer, null);
		assertTrue(functionArgument instanceof StaticFunctionArgument);
		assertEquals(functionParameter.valueString(), ((StaticFunctionArgument) functionArgument).getValue());
		verifyAll();
	}

	@Test
	public void testCreateFunctionArgumentWithStringValueForStringDeployTypeHappyCase() throws Exception {
		FunctionParameterDefinition functionParameterDefinition = createFunctionParameterDefinition();
		functionParameterDefinition.setDeployType(DeployType.STRING);

		FunctionTypeDefinition functionTypeDefinition = RuleObjectMother.createFunctionTypeDefinition();
		functionTypeDefinition.addParameterDefinition(functionParameterDefinition);

		FunctionParameter functionParameter = RuleObjectMother.createFunctionParameterForStringValue(1);

		expect(mockFunctionParameterContainer.getParameterAt(0)).andReturn(functionParameter);
		expect(mockFunctionParameterContainer.getFunctionTypeDefinition()).andReturn(functionTypeDefinition);
		replayAll();

		FunctionArgument functionArgument = functionArgumentFactory.createFunctionArgument(1, mockFunctionParameterContainer, null);
		assertTrue(functionArgument instanceof StaticFunctionArgument);
		assertEquals(
				RuleGeneratorHelper.QUOTE + functionParameter.valueString() + RuleGeneratorHelper.QUOTE,
				((StaticFunctionArgument) functionArgument).getValue());
		verifyAll();
	}

	protected void verifyAll() {
		verify(mockPatternFactoryHelper);
		verify(mockFunctionParameterContainer);
	}
}
