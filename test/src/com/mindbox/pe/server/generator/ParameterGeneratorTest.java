package com.mindbox.pe.server.generator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplateColumn;
import com.mindbox.pe.server.cache.DomainManager;

public class ParameterGeneratorTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("ParameterGeneratorTest Tests");
		suite.addTestSuite(ParameterGeneratorTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private MockControl outputControllerMockControl;
	private OutputController mockOutputController;
	private DomainClass domainClass;
	private DomainAttribute domainAttribute;

	public ParameterGeneratorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		outputControllerMockControl = MockClassControl.createControl(OutputController.class);
		mockOutputController = (OutputController) outputControllerMockControl.getMock();
		domainClass = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		domainAttribute = (DomainAttribute) domainClass.getDomainAttributes().get(0);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	protected void tearDown() throws Exception {
		DomainManager.getInstance().startLoading();
	}

	public void testWriteSingleCellWithInvalidBooleanValueThrowsRuleGenerationException() throws Exception {
		ParameterTemplateColumn column = new ParameterTemplateColumn(1, "boolean", "", 100, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setAttributeMap(domainClass.getName() + "." + domainAttribute.getName());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);
		
		ParameterGenerator.getInstance().init(mockOutputController);

		mockOutputController.getStatus();
		outputControllerMockControl.replay();

		assertThrowsException(ParameterGenerator.getInstance(), "writeSingleCell", new Class[] {
				int.class,
				int.class,
				ParameterTemplateColumn.class,
				String.class,
				ParameterGrid.class,
				boolean.class,
				boolean.class,
				String.class,
				String.class }, 
				new Object[] {
				new Integer(0),
				new Integer(0),
				column,
				"xyz",
				null,
				new Boolean(true),
				new Boolean(true),
				domainClass.getName(),
				domainAttribute.getDeployLabel() }, RuleGenerationException.class);
	}
}
