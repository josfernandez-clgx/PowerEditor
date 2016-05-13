package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createActionTypeDefinition;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainAttribute;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplate;
import static org.easymock.EasyMock.createMock;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;

/**
 * Abstract base test for {@link FunctionCallPatternFactory} unit tests.
 * 
 * @author kim
 * 
 */
public abstract class AbstractFunctionCallPatternFactoryTest extends AbstractTestWithTestConfig {

	protected PatternFactoryHelper helperMock;
	protected TemplateUsageType usageType;
	protected FunctionCallPatternFactory functionCallPatternFactory;
	protected ActionTypeDefinition actionTypeDefinition;
	protected GridTemplate template;
	protected DomainAttribute domainAttribute;
	protected GenericEntityType entityType;

	protected abstract boolean isForTestCondition();

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		entityType = GenericEntityType.getAllGenericEntityTypes()[0];
		helperMock = createMock(PatternFactoryHelper.class);
		usageType = TemplateUsageType.getAllInstances()[0];
		functionCallPatternFactory = new FunctionCallPatternFactory(helperMock, isForTestCondition());
		template = createGridTemplate(usageType);
		actionTypeDefinition = createActionTypeDefinition();
		domainAttribute = createDomainAttribute();
	}

	public void tearDown() throws Exception {
		GuidelineFunctionManager.getInstance().startLoading();
		super.tearDown();
	}
}
