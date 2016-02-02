package com.mindbox.pe.server.generator.rule;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;

/**
 * Abstract base test for {@link FunctionCallPatternFactory} unit tests.
 * @author kim
 *
 */
public abstract class AbstractFunctionCallPatternFactoryTest extends AbstractTestWithTestConfig {

	protected PatternFactoryHelper helperMock;
	protected MockControl mockControl;
	protected TemplateUsageType usageType;
	protected FunctionCallPatternFactory functionCallPatternFactory;
	protected ActionTypeDefinition actionTypeDefinition;
	protected GridTemplate template;
	protected DomainAttribute domainAttribute;
	protected GenericEntityType entityType;

	protected AbstractFunctionCallPatternFactoryTest(String name) {
		super(name);
	}

	protected abstract boolean isForTestCondition();

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		entityType = GenericEntityType.getAllGenericEntityTypes()[0];
		mockControl = MockControl.createControl(PatternFactoryHelper.class);
		helperMock = (PatternFactoryHelper) mockControl.getMock();
		usageType = TemplateUsageType.getAllInstances()[0];
		functionCallPatternFactory = new FunctionCallPatternFactory(helperMock, isForTestCondition());
		template = ObjectMother.createGridTemplate(usageType);
		actionTypeDefinition = ObjectMother.createActionTypeDefinition();
		domainAttribute = ObjectMother.createDomainAttribute();
	}

	protected void tearDown() throws Exception {
		GuidelineFunctionManager.getInstance().startLoading();
		super.tearDown();
	}
}
