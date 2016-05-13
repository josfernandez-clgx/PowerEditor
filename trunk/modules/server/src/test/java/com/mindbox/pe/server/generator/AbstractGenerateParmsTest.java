package com.mindbox.pe.server.generator;

import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.template.TemplateMessageDigest;
import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class AbstractGenerateParmsTest extends AbstractTestWithTestConfig {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	@After
	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testFindMessageTextHappyCaseForDefaultEntityID() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams();
		TemplateMessageDigest messageDigest = new TemplateMessageDigest();
		messageDigest.setEntityID(-1);
		messageDigest.setText("message " + createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest);
		assertEquals(messageDigest.getText(), ruleParams.findTemplateMessageDigest(createInt()).getText());
	}

	@Test
	public void testFindMessageTextHappyCaseForMatchingEntityID() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams();
		TemplateMessageDigest messageDigest1 = new TemplateMessageDigest();
		messageDigest1.setEntityID(-1);
		messageDigest1.setText("message " + createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest1);
		TemplateMessageDigest messageDigest2 = new TemplateMessageDigest();
		messageDigest2.setEntityID(createInt());
		messageDigest2.setText("message " + createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest2);

		assertEquals(messageDigest2.getText(), ruleParams.findTemplateMessageDigest(messageDigest2.getEntityID()).getText());
	}

	@Test
	public void testFindMessageTextReturnsNullIfNotFound() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams();
		// when there are no messages
		assertNull(ruleParams.findTemplateMessageDigest(-1));

		// there there is a message for a different entity id
		TemplateMessageDigest messageDigest = new TemplateMessageDigest();
		messageDigest.setEntityID(1);
		messageDigest.setText("message " + createString());
		ruleParams.getTemplate().addMessageDigest(messageDigest);
		assertNull(ruleParams.findTemplateMessageDigest(2));
	}

}
