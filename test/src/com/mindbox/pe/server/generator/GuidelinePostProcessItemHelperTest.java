package com.mindbox.pe.server.generator;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.server.cache.DomainManager;

public class GuidelinePostProcessItemHelperTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("GuidelinePostProcessItemHelperTest Tests");
		suite.addTestSuite(GuidelinePostProcessItemHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public GuidelinePostProcessItemHelperTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	public void testProcessForDynamicStringInMessageWithNullRuleParamsThrowsNullPointerException() throws Exception {
		GuidelinePostProcessItemHelper helper = new GuidelinePostProcessItemHelper(GuidelineRuleGenerator.getInstance());
		assertThrowsNullPointerException(
				helper,
				"processForDynamicStringInMessage",
				new Class[] { GuidelineGenerateParams.class, int.class },
				new Object[] { null, new Integer(-1) });
	}

	@SuppressWarnings("unchecked")
	public void testProcessForDynamicStringInMessageIsNoOpWithNoMessageTextFound() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams();
		GuidelinePostProcessItemHelper helper = new GuidelinePostProcessItemHelper(GuidelineRuleGenerator.getInstance());
		helper.processForDynamicStringInMessage(ruleParams, -1);
		assertTrue(((List<Reference>) ReflectionUtil.getPrivate(helper, "postProcessingItemList")).isEmpty());
	}

	@SuppressWarnings("unchecked")
	public void testProcessForDynamicStringInMessageIgnoresInvalidColumnNo() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams();
		TemplateMessageDigest messageDigest = new TemplateMessageDigest();
		messageDigest.setEntityID(-1);
		messageDigest.setText("some %column X%: " + ObjectMother.createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest);

		GuidelinePostProcessItemHelper helper = new GuidelinePostProcessItemHelper(GuidelineRuleGenerator.getInstance());
		helper.processForDynamicStringInMessage(ruleParams, -1);
		assertTrue(((List<Reference>) ReflectionUtil.getPrivate(helper, "postProcessingItemList")).isEmpty());
	}

	@SuppressWarnings("unchecked")
	public void testProcessForDynamicStringInMessageHappyCase() throws Exception {
		DomainClass dc = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		DomainAttribute da = (DomainAttribute) dc.getDomainAttributes().get(0);
		da.setContextlessLabel(da.getName());
		DomainManager.getInstance().addDomainClass(dc);
		
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams();
		DynamicStringValue dsValue = DynamicStringValue.parseValue("Attribute = $" + dc.getName() + "." + da.getName() + "$");
		((List<DynamicStringValue>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(dsValue);
		String col2Value = ObjectMother.createString();
		((List<String>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(col2Value);
		
		TemplateMessageDigest messageDigest = new TemplateMessageDigest();
		messageDigest.setEntityID(-1);
		messageDigest.setText("some %column 2%; and %column 1%");
		ruleParams.getTemplate().addMessageDigest(messageDigest);

		GuidelinePostProcessItemHelper helper = new GuidelinePostProcessItemHelper(GuidelineRuleGenerator.getInstance());
		helper.processForDynamicStringInMessage(ruleParams, -1);
		assertEquals(1, ((List<Reference>) ReflectionUtil.getPrivate(helper, "postProcessingItemList")).size());
		assertEquals(2, dsValue.getDeployValues().length);
		assertEquals("Attribute = %a", dsValue.getDeployValues()[0]);
		assertEquals("?" + da.getName().toLowerCase(), dsValue.getDeployValues()[1]);
	}
}
