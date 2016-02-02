package com.mindbox.pe.server.imexport.digest;

import java.io.IOException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.xml.sax.SAXException;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRAttributeType;
import com.mindbox.pe.model.CBRCaseAction;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.CBRCaseClass;
import com.mindbox.pe.model.CBRScoringFunction;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;

/**
 * @author Vineet Khosla
 * @since 5.0.0
 */
public class ImportXMLDigesterTest extends AbstractTestWithTestConfig {

	protected final String filename;

	public ImportXMLDigesterTest(String name) {
		super(name);
		this.filename = "ImportEntityTest-5-0.xml";
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Import XMLDigester Tests");
		suite.addTestSuite(ImportXMLDigesterTest.class);
		return suite;
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	/*
	 * Test method for 'com.mindbox.pe.server.imexport.digest.ImportXMLDigester.digestImportXML(String, Level)'.
	 * Passes the test if there are no exceptions thrown.
	 */
	public void testDigestImportXMLStringLevel() throws IOException, SAXException {

		ImportXMLDigester digester = ImportXMLDigester.getInstance();
		digester.digestImportXML(config.getDataFileContent(filename));
		// the fact that no exception was thrown means pass
	}

	public void testDigestCBRCaseBaseHappyCase() throws Exception {
		String xml = "<powereditor-data>" + "<cbr-data><cbr-case-base id=\"2384\" name=\"Case Base 2\">"
				+ "<activation-dates effectiveDateID=\"1521\"/>" + " <description>some case</description>" + "<case-class id=\"1\"/>"
				+ " <description>some description</description>" + " <index-file>indexFile01</index-file>"
				+ " <match-threshold>10</match-threshold>" + "  <maximum-matches>20</maximum-matches>"
				+ " <naming-attribute>attributeforname</naming-attribute>" + " <scoring-function id=\"3\"/>" + "</cbr-case-base>"
				+ "</cbr-data></powereditor-data>";

		CBRCaseClass caseClass = new CBRCaseClass(1, "class1", "Class1");
		CBRManager.getInstance().addCBRCaseClass(caseClass);
		CBRScoringFunction scoringFunction = new CBRScoringFunction(3, "sf3", "sf3");
		CBRManager.getInstance().addCBRScoringFunction(scoringFunction);

		ImportXMLDigester digester = ImportXMLDigester.getInstance();
		DigestedObjectHolder holder = digester.digestImportXML(xml, Level.DEBUG);

		assertNotNull(holder);
		List<CBRCaseBaseDigest> list = holder.getObjects(CBRCaseBaseDigest.class);
		assertEquals(1, list.size());
		CBRCaseBaseDigest caseBase = list.get(0);
		assertEquals(2384, caseBase.getId());
		assertEquals("Case Base 2", caseBase.getName());
		assertEquals("some description", caseBase.getDescription());
		assertEquals("indexFile01", caseBase.getIndexFile());
		assertEquals(10, caseBase.getMatchThreshold());
		assertEquals(20, caseBase.getMaximumMatches());
		assertEquals("attributeforname", caseBase.getNamingAttribute());
		assertEquals(2384, caseBase.getId());
		assertEquals(caseClass, caseBase.getCaseClass());
		assertEquals(scoringFunction, caseBase.getScoringFunction());
		assertNotNull(caseBase.getActivationDates());
		assertEquals(1521, caseBase.getActivationDates().getEffectiveDateID());
	}

	public void testDigestCBRCaseHappyCase() throws Exception {
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBase();
		CBRCaseAction caseAction = ObjectMother.createCBRCaseAction();

		String xml = "<powereditor-data>" + "<cbr-data><cbr-case id=\"2385\" name=\"Case 3\">"
				+ "<activation-dates effectiveDateID=\"1521\"/>" + " <description>some case</description>"
				+ "<attribute-values><attribute-value id=\"123\" value=\"AV1\">"
				+ "<attribute id=\"2315\" /><description>sgsgsdgsdgs</description>"
				+ "<match-contribution>-2</match-contribution><mismatch-penalty>-99</mismatch-penalty>"
				+ "</attribute-value></attribute-values>" + "<case-actions><case-action id=\"" + caseAction.getId() + "\"/></case-actions>"
				+ "<case-base id=\"" + caseBase.getId() + "\"/>" + "</cbr-case>" + "</cbr-data></powereditor-data>";

		CBRManager.getInstance().addCBRCaseBase(caseBase);
		CBRManager.getInstance().addCBRCaseAction(caseAction);

		ImportXMLDigester digester = ImportXMLDigester.getInstance();
		DigestedObjectHolder holder = digester.digestImportXML(xml, Level.DEBUG);

		assertNotNull(holder);
		List<CBRCaseDigest> list = holder.getObjects(CBRCaseDigest.class);
		assertEquals(1, list.size());
		CBRCaseDigest cbrCase = list.get(0);
		assertEquals(2385, cbrCase.getId());
		assertEquals("Case 3", cbrCase.getName());
		assertEquals("some case", cbrCase.getDescription());
		assertEquals(caseBase, cbrCase.getCaseBase());
		assertEquals(1, cbrCase.getAttributeValues().size());
		assertEquals(123, cbrCase.getAttributeValues().get(0).getId());
		assertEquals("AV1", cbrCase.getAttributeValues().get(0).getName());
		assertEquals("2315", cbrCase.getAttributeValues().get(0).getAttribute().getName());
		assertEquals("sgsgsdgsdgs", cbrCase.getAttributeValues().get(0).getDescription());
		assertEquals(-2, cbrCase.getAttributeValues().get(0).getMatchContribution());
		assertEquals(-99, cbrCase.getAttributeValues().get(0).getMismatchPenalty());
		assertEquals(1, cbrCase.getCaseActions().size());
		assertEquals(caseAction, cbrCase.getCaseActions().get(0));
		assertNotNull(cbrCase.getActivationDates());
		assertEquals(1521, cbrCase.getActivationDates().getEffectiveDateID());
	}

	public void testDigestCBRAttributeHappyCase() throws Exception {
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBase();
		CBRAttributeType attributeType = ObjectMother.createCBRAttributeType();
		String xml = "<powereditor-data>" + "<cbr-data><cbr-attribute id=\"1384\" name=\"TestAttribute\">"
				+ "<description>some TestAttribute</description>" + "<absence-penalty>-8</absence-penalty>" + "<attribute-type id=\""
				+ attributeType.getId() + "\"/><case-base id=\"" + caseBase.getId() + "\"/>"
				+ "<enum-values><enum-value id=\"5\" name=\"EV\"/></enum-values>"
				+ "<highest-value>99.0</highest-value><lowest-value>-50.0</lowest-value>"
				+ "<match-contribution>-5</match-contribution><match-interval>0.25</match-interval><mismatch-penalty>50</mismatch-penalty>"
				+ "<value-range id=\"2\"/>" + "</cbr-attribute>" + "</cbr-data></powereditor-data>";

		CBRManager.getInstance().addCBRCaseBase(caseBase);
		CBRManager.getInstance().addCBRAttributeType(attributeType);

		ImportXMLDigester digester = ImportXMLDigester.getInstance();
		DigestedObjectHolder holder = digester.digestImportXML(xml, Level.DEBUG);

		assertNotNull(holder);
		List<CBRAttribute> list = holder.getObjects(CBRAttribute.class);
		assertEquals(1, list.size());
		CBRAttribute attribute = list.get(0);
		assertEquals(1384, attribute.getId());
		assertEquals("TestAttribute", attribute.getName());
		assertEquals("some TestAttribute", attribute.getDescription());
		assertEquals(-8, attribute.getAbsencePenalty());
		assertEquals(99.0, attribute.getHighestValue());
		assertEquals(-50.0, attribute.getLowestValue());
		assertEquals(-5, attribute.getMatchContribution());
		assertEquals(0.25, attribute.getMatchInterval());
		assertEquals(50, attribute.getMismatchPenalty());
		assertEquals(caseBase, attribute.getCaseBase());
		assertEquals(attributeType, attribute.getAttributeType());
		assertEquals(-1, attribute.getValueRange().getId());
		assertEquals(1, attribute.getEnumeratedValues().size());
		assertEquals("EV", attribute.getEnumeratedValues().get(0).getName());
	}

	public void testDigestEntityWithInvalidParentIDThrowsException() throws Exception {
		String xml = "<?xml version=\"1.0\"?>"
				+ "<powereditor-data>"
				+ "  <entity-data>"
				+ "     <entity id=\"1242\" type=\"product\" parentID=\"zzz\"><property name=\"status\" value=\"Draft\"/></entity></entity-data></powereditor-data>";
		try {
			ImportXMLDigester digester = ImportXMLDigester.getInstance();
			digester.digestImportXML(xml);
			fail("No NumberFormatException thrown");
		}
		catch (SAXException ex) {
			// expected
			assertTrue(ex.getException() instanceof NumberFormatException);
		}
	}

	public void testDigestDateSynonyms() throws Exception {
		String xml = "<?xml version=\"1.0\"?>" + "<powereditor-data>" + "  <date-synonyms>"
				+ "    <date-synonym id=\"112\" name=\"Date Synonym A\">" + "      <description></description>"
				+ "      <date>2006-08-16T14:37:57</date>" + "    </date-synonym>"
				+ "    <date-synonym id=\"113\" name=\"Date Synonym B\">" + "      <description></description>"
				+ "      <date>2006-08-16T14:38:03</date>" + "    </date-synonym>" + "  </date-synonyms>" + "</powereditor-data>";

		ImportXMLDigester digester = ImportXMLDigester.getInstance();
		DigestedObjectHolder holder = digester.digestImportXML(xml);

		assertNotNull(holder);

		List<DateSynonym> list = holder.getObjects(DateSynonym.class);
		assertNotNull("Expecting list of date synonyms", list);
		assertEquals("Date Synonym size mismatch", 2, list.size());
		DateSynonym dsa = list.get(0);
		assertNotNull("Expecting date synonyms in list", list);
		assertEquals("Expecting date synonym named \"Date Synonym A\"", dsa.getName(), "Date Synonym A");

		DateSynonym dsb = list.get(1);
		assertNotNull("Expecting date synonyms in list", list);
		assertEquals("Expecting date synonym named \"Date Synonym B\"", dsb.getName(), "Date Synonym B");
	}

	public void testDigestActivationDates() throws Exception {
		String xml = "<?xml version=\"1.0\"?>" + "<powereditor-data>" + "  <entity-data>" + "<category id=\"1003\" type=\"product\">"
				+ "<property name=\"name\" value=\"Root Category Created by PowerEditor\"/>" + "<parent>" + "<parentID>-1</parentID>"
				+ "<activation-dates effectiveDateID=\"1521\"/>" + "</parent>" + "</category>" + "  </entity-data>" + "</powereditor-data>";

		ImportXMLDigester digester = ImportXMLDigester.getInstance();
		DigestedObjectHolder holder = digester.digestImportXML(xml);

		assertNotNull(holder);

		List<CategoryDigest> list = holder.getObjects(CategoryDigest.class);
		assertEquals(1, list.size());
		CategoryDigest categoryDigest = list.get(0);
		assertEquals(1, categoryDigest.getParents().size());
		Parent parent = (Parent) categoryDigest.getParents().get(0);
		assertNotNull(parent.getActivationDates());
		assertEquals(1521, parent.getActivationDates().getEffectiveDateID());
		assertEquals(ActivationDates.UNSPECIFIED_ID, parent.getActivationDates().getExpirationDateID());
	}

	public void testDigestUsersHandlesStausAndStatus() throws Exception {
		String xml = "<?xml version=\"1.0\"?>" + "<powereditor-data>" + "  <security-data>" + "    <users>"
				+ "      <user id=\"admin\" name=\"Administrator\" password=\"admin\" staus=\"Active\">" + "      </user>"
				+ "      <user id=\"user2\" name=\"User 2\" password=\"user2\" status=\"Inactive\">" + "      </user>" + "    </users>"
				+ "  </security-data>" + "</powereditor-data>";

		ImportXMLDigester digester = ImportXMLDigester.getInstance();
		DigestedObjectHolder holder = digester.digestImportXML(xml);

		assertNotNull(holder);
		List<User> list = holder.getObjects(User.class);
		assertEquals(2, list.size());
		User user = list.get(0);
		assertEquals("admin", user.getId());
		assertEquals("Active", user.getStatus());
		user = list.get(1);
		assertEquals("user2", user.getId());
		assertEquals("Inactive", user.getStatus());
	}

	protected void tearDown() throws Exception {
		DateSynonymManager.getInstance().startLoading();
		CBRManager.getInstance().startDbLoading();
		config.resetConfiguration();
		super.tearDown();
	}
}
