package com.mindbox.pe.server.generator;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.TemplateMessageDigest;

public class AbstractGenerateParmsTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractGenerateParmsTest Tests");
		suite.addTestSuite(AbstractGenerateParmsTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public AbstractGenerateParmsTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
	
	public void testFindMessageTextReturnsNullIfNotFound() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams();
		// when there are no messages
		assertNull(ruleParams.findTemplateMessageDigest(-1));
		
		// there there is a message for a different entity id
		TemplateMessageDigest messageDigest = new TemplateMessageDigest();
		messageDigest.setEntityID(1);
		messageDigest.setText("message " + ObjectMother.createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest);
		assertNull(ruleParams.findTemplateMessageDigest(2));
	}
	
	public void testFindMessageTextHappyCaseForDefaultEntityID() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams();
		TemplateMessageDigest messageDigest = new TemplateMessageDigest();
		messageDigest.setEntityID(-1);
		messageDigest.setText("message " + ObjectMother.createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest);
		assertEquals(messageDigest.getText(), ruleParams.findTemplateMessageDigest(ObjectMother.createInt()).getText());
	}
	
	public void testFindMessageTextHappyCaseForMatchingEntityID() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams();
		TemplateMessageDigest messageDigest1 = new TemplateMessageDigest();
		messageDigest1.setEntityID(-1);
		messageDigest1.setText("message " + ObjectMother.createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest1);
		TemplateMessageDigest messageDigest2 = new TemplateMessageDigest();
		messageDigest2.setEntityID(ObjectMother.createInt());
		messageDigest2.setText("message " + ObjectMother.createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest2);

		assertEquals(messageDigest2.getText(), ruleParams.findTemplateMessageDigest(messageDigest2.getEntityID()).getText());
	}
	
}
