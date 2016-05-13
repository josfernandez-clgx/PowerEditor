/*
 * Created on 2004. 6. 21.
 *
 */
package com.mindbox.pe.server.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.DomainTranslation;
import com.mindbox.pe.model.domain.DomainView;
import com.mindbox.pe.server.AbstractServerTestBase;

/**
 * Tests multiple domain file support.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.2.0
 * @deprecated disabled until fixed
 */
public class MultipleDomainFileTest extends AbstractServerTestBase {

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		config.populateServerCache();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	//@Test
	public void testDomainClassAttrbitues() throws Exception {
		DomainClass dc = DomainManager.getInstance().getDomainClass("Group");
		assertNotNull("Domain class named 'group' does not exist", dc);

		DomainAttribute attribute = dc.getDomainAttribute("RatioEnd");
		assertNotNull("group domain class does not have 'RatioEnd' attribute", attribute);
		assertTrue("Cache inconsistency: attribute object mismatch", (attribute == DomainManager.getInstance()
				.getDomainAttribute("Group", "RatioEnd")));
		assertEquals("DeployType mismatch", DeployType.SYMBOL, attribute.getDeployType());
		assertEquals("DeployLabel mismatch", "gk01:ratio-end", attribute.getDeployLabel());
		assertEquals("DisplayLabel mismatch", "Ratio Threshold", attribute.getDisplayLabel());
		assertEquals("AllowRuleUsage mismatch", true, attribute.allowRuleUsage());

		attribute = dc.getDomainAttribute("NewAttribute");
		assertNotNull("group domain class does not have 'NewAttribute' attribute", attribute);
		assertTrue(
				"Cache inconsistency: attribute object mismatch",
				(attribute == DomainManager.getInstance().getDomainAttribute("Group", "NewAttribute")));
	}

	//@Test
	public void testDomainClassPropertyOverride() throws Exception {
		DomainClass dc = DomainManager.getInstance().getDomainClass("Group");
		assertNotNull("Domain class named 'group' does not exist", dc);
		assertEquals("DeployLabel mismatch", "gk01:group", dc.getDeployLabel());
		assertEquals("DisplayLabel mismatch", "group01", dc.getDisplayLabel());
		assertEquals("AllowRuleUsage mismatch", false, dc.allowRuleUsage());
		assertEquals("HasMultiplicity mismatch", false, dc.isSingleton());
		assertEquals("superClass mismatch", null, dc.getSuperClass());
	}

	//@Test
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
