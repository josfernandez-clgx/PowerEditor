package com.mindbox.pe.server.generator;

import static com.mindbox.pe.server.ServerTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.template.TemplateMessageDigest;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;

public class GuidelinePostProcessItemHelperTest extends AbstractTestWithTestConfig implements ErrorContextProvider {

	private BufferedGenerator bufferedGenerator;

	@Override
	public String getErrorContext() {
		return getClass().getName();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(1, "Draft", "Draft"));
		bufferedGenerator = new DefaultBufferedGenerator(new GenerateStats("target"), new DefaultOutputController("Draft"), new File("target/dummy"), this);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessForDynamicStringInMessageHappyCase() throws Exception {
		DomainClass dc = attachDomainAttributes(createDomainClass(), 1);
		DomainAttribute da = (DomainAttribute) dc.getDomainAttributes().get(0);
		da.setContextlessLabel(da.getName());
		DomainManager.getInstance().addDomainClass(dc);

		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams();
		DynamicStringValue dsValue = DynamicStringValue.parseValue("Attribute = $" + dc.getName() + "." + da.getName() + "$");
		((List<DynamicStringValue>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(dsValue);
		String col2Value = createString();
		((List<String>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(col2Value);

		TemplateMessageDigest messageDigest = new TemplateMessageDigest();
		messageDigest.setEntityID(-1);
		messageDigest.setText("some %column 2%; and %column 1%");
		ruleParams.getTemplate().addMessageDigest(messageDigest);

		GuidelinePostProcessItemHelper helper = new GuidelinePostProcessItemHelper(bufferedGenerator);
		helper.processForDynamicStringInMessage(ruleParams, -1);
		assertEquals(1, ((List<Reference>) ReflectionUtil.getPrivate(helper, "postProcessingItemList")).size());
		assertEquals(2, dsValue.getDeployValues().length);
		assertEquals("Attribute = %a", dsValue.getDeployValues()[0]);
		assertEquals("?" + da.getName().toLowerCase(), dsValue.getDeployValues()[1]);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessForDynamicStringInMessageIgnoresInvalidColumnNo() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams();
		TemplateMessageDigest messageDigest = new TemplateMessageDigest();
		messageDigest.setEntityID(-1);
		messageDigest.setText("some %column X%: " + createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest);

		GuidelinePostProcessItemHelper helper = new GuidelinePostProcessItemHelper(bufferedGenerator);
		helper.processForDynamicStringInMessage(ruleParams, -1);
		assertTrue(((List<Reference>) ReflectionUtil.getPrivate(helper, "postProcessingItemList")).isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessForDynamicStringInMessageIsNoOpWithNoMessageTextFound() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams();
		GuidelinePostProcessItemHelper helper = new GuidelinePostProcessItemHelper(bufferedGenerator);
		helper.processForDynamicStringInMessage(ruleParams, -1);
		assertTrue(((List<Reference>) ReflectionUtil.getPrivate(helper, "postProcessingItemList")).isEmpty());
	}

	@Test
	public void testProcessForDynamicStringInMessageWithNullRuleParamsThrowsNullPointerException() throws Exception {
		GuidelinePostProcessItemHelper helper = new GuidelinePostProcessItemHelper(bufferedGenerator);
		assertThrowsNullPointerException(helper, "processForDynamicStringInMessage", new Class[] { GuidelineGenerateParams.class, int.class }, new Object[] { null, new Integer(-1) });
	}
}
