package com.mindbox.pe.server.generator;

import static com.mindbox.pe.server.ServerTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.server.ServerTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ParameterTemplateColumn;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.unittest.AbstractTestBase;

public class ParameterGeneratorTest extends AbstractTestBase {

	private OutputController mockOutputController;
	private DomainClass domainClass;
	private DomainAttribute domainAttribute;

	@Before
	public void setUp() throws Exception {
		mockOutputController = createMock(OutputController.class);
		domainClass = attachDomainAttributes(createDomainClass(), 1);
		domainAttribute = (DomainAttribute) domainClass.getDomainAttributes().get(0);
		DomainManager.getInstance().addDomainClass(domainClass);
	}

	@After
	public void tearDown() throws Exception {
		DomainManager.getInstance().startLoading();
	}

	@Test
	public void testWriteSingleCellWithInvalidBooleanValueThrowsRuleGenerationException() throws Exception {
		ParameterTemplateColumn column = new ParameterTemplateColumn(1, "boolean", "", 100, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setAttributeMap(domainClass.getName() + "." + domainAttribute.getName());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);

		String status = createString();

		expect(mockOutputController.getStatus()).andReturn(status);
		expect(mockOutputController.getParameterFile()).andReturn(new File("target/dummyfile"));

		replay(mockOutputController);

		final ParameterGenerator parameterGenerator = new ParameterGenerator(new GenerateStats("target"), mockOutputController);

		assertThrowsException(
				parameterGenerator,
				"writeSingleCell",
				new Class[] { int.class, int.class, ParameterTemplateColumn.class, String.class, ParameterGrid.class, boolean.class, boolean.class, String.class, String.class },
				new Object[] { new Integer(0), new Integer(0), column, "xyz", null, new Boolean(true), new Boolean(true), domainClass.getName(), domainAttribute.getDeployLabel() },
				RuleGenerationException.class);
	}
}
