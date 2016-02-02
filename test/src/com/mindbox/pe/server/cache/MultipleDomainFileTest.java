/*
 * Created on 2004. 6. 21.

 *
 */
package com.mindbox.pe.server.cache;

import junit.framework.TestSuite;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.DomainTranslation;
import com.mindbox.pe.model.DomainView;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.ServerTestBase;


/**
 * Tests multiple domain file support.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.2.0
 * @deprecated disabled until fixed
 */
public class MultipleDomainFileTest extends ServerTestBase {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(MultipleDomainFileTest.class);
		suite.setName("Multiple Domain File Tests");
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	/**
	 * @param name
	 */
	public MultipleDomainFileTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		config.populateServerCache();
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDomainClassPropertyOverride() throws Exception {
		DomainClass dc = DomainManager.getInstance().getDomainClass("Group");
		assertNotNull("Domain class named 'group' does not exist", dc);
		assertEquals("DeployLabel mismatch", "gk01:group", dc.getDeployLabel());
		assertEquals("DisplayLabel mismatch", "group01", dc.getDisplayLabel());
		assertEquals("AllowRuleUsage mismatch", false, dc.allowRuleUsage());
		assertEquals("HasMultiplicity mismatch", false, dc.isSingleton());
		//assertEquals("persistence mismatch", true, dc.isPersistent());
		assertEquals("superClass mismatch", null, dc.getSuperClass());
	}
	
	public void testDomainClassAttrbitues() throws Exception {
		DomainClass dc = DomainManager.getInstance().getDomainClass("Group");
		assertNotNull("Domain class named 'group' does not exist", dc);
		
		DomainAttribute attribute = dc.getDomainAttribute("RatioEnd");
		assertNotNull("group domain class does not have 'RatioEnd' attribute", attribute);
		assertTrue("Cache inconsistency: attribute object mismatch", 
				(attribute == DomainManager.getInstance().getDomainAttribute("Group","RatioEnd")));
		assertEquals("DeployType mismatch", DeployType.SYMBOL, attribute.getDeployType());
		assertEquals("DeployLabel mismatch", "gk01:ratio-end", attribute.getDeployLabel());
		assertEquals("DisplayLabel mismatch", "Ratio Threshold", attribute.getDisplayLabel());
		assertEquals("AllowRuleUsage mismatch", true, attribute.allowRuleUsage());
		
		attribute = dc.getDomainAttribute("NewAttribute");
		assertNotNull("group domain class does not have 'NewAttribute' attribute", attribute);
		assertTrue("Cache inconsistency: attribute object mismatch", 
				(attribute == DomainManager.getInstance().getDomainAttribute("Group","NewAttribute")));
	}
	
	public void testDomainClassTranslations() throws Exception {
		DomainClass dc = DomainManager.getInstance().getDomainClass("Group");
		assertNotNull("Domain class named 'group' does not exist", dc);
		
		DomainTranslation translation = dc.getDomainTranslation("BorrowerName");
		assertNotNull("group class does not have 'BorrowerName' translation", translation);
		assertEquals("LinkPath mismatch", "InputContainer.Borrower.Name", translation.getLinkPath());
		assertEquals("DisplayLabel mismatch", "Borrower FullName", translation.getDisplayLabel());
		assertEquals("ContextlessLabel mismatch", "Borrower's Full Name", translation.getContextlessLabel());
		assertTrue("DomainView mismatch", translation.hasDomainView(DomainView.TEMPLATE_EDITOR));

		translation = dc.getDomainTranslation("NewTranslation");
		assertNotNull("group class does not have 'NewTranslation' translation", translation);
	}
}
