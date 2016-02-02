package com.mindbox.ftest.pe.rulegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mindbox.ftest.pe.util.ESPTestUtil;
import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.TestConfig;
import com.mindbox.pe.common.UtilBase;

/** 
 * This test exercises rules deployed by PE to a running AE Server.  The input to the test, and its
 * assertions are externalized in AE request xml files.  When run, it recursively finds all
 * xml files in a directory specified in petest.properties, and processes them each in turn.
 * 
 * The design follows that of another MindBox testing tool, Mate.
 * 
 * Each file is processed as a separate subtest, which consists of:
 * 1. extracting the embedded assertions
 * 2. submitting the request to the AE server configured in petest.properties
 * 3. reading the server's response
 * 4. executing the assertions against the response
 *  
 *(note, as an optimization, steps 2-3 are skipped when no assertions are found in step 1.)
 * 
 * This test fails if just one assertions fails.  But for completeness processing continues after failed
 * assertions so that all xml files are processed and all failures are reported for each file.
 * 
 * Per Mate specs, assertions are either of type "include" or "exclude" and appear within 3 line xml comments:
 * 
 * &lt;!--include
 * XPath Expression expected to be found
 * --&gt;
 * 
 * or:
 * 
 * &lt;!--exclude
 * XPath Expressionexpected NOT to be found
 * --&gt;
 * 
 * Assertions like these may occur directly in a request xml file, or in separate "Assertion Patterns File".
 * To reference a Pattern file, an xml file will contain a line such as:
 * 
 * &lt;!--mate relative/path/to/AssertionsPatternFile.txt--&gt;
 */
public class DeployedRulesBatchTest extends AbstractTestWithTestConfig {

	private static final Pattern EXTERNAL_PATTERN_FILENAME_PATTERN = Pattern.compile("<!--\\s*[mM][aA][tT][eE](.*?)-->");

	public static Test suite() {
		TestConfig config = TestConfig.getInstance();
		try {
			TestSuite suite = new TestSuite("DeployedRule Functional Tests");
			File requestFileDir = config.getDataFile("rulegen");
			List<File> requestFiles = getXmlFiles(requestFileDir);
			for (Iterator<File> iter = requestFiles.iterator(); iter.hasNext();) {
				File requestFile = iter.next();
				suite.addTest(new DeployedRulesBatchTest(requestFile));
			}
			return suite;
		}
		catch (IOException e) {
			Logger.getLogger(DeployedRulesBatchTest.class).error(e);
			throw new IllegalStateException("Failed to build test suite for " + DeployedRulesBatchTest.class.getName() + ": "
					+ e.getMessage());
		}
	}

	private static List<File> getXmlFiles(File dir) throws IOException {
		List<File> xmlFiles = new LinkedList<File>();

		File[] dirContents = dir.listFiles();
		for (int i = 0; i < dirContents.length; i++) {
			if (dirContents[i].isDirectory()) {
				xmlFiles.addAll(getXmlFiles(dirContents[i]));
			}
			else if (dirContents[i].getName().endsWith(".xml")) {
				xmlFiles.add(dirContents[i].getCanonicalFile());
			}
		}
		return xmlFiles;
	}

	private static Set<AeResponseAssertion> extractAssertions(File requestXmlFile) throws IOException {
		File parentDir = null;

		Set<AeResponseAssertion> result = new HashSet<AeResponseAssertion>();
		BufferedReader fileReader = new BufferedReader(new FileReader(requestXmlFile));

		for (String line = fileReader.readLine(); line != null; line = fileReader.readLine()) {
			if (AeResponseAssertionType.INCLUDE.assertionStartIndicator.matcher(line).find()) {
				line = fileReader.readLine();
				if (line != null) {
					result.add(new AeResponseAssertion(AeResponseAssertionType.INCLUDE, line.trim()));
				}
			}
			else if (AeResponseAssertionType.EXCLUDE.assertionStartIndicator.matcher(line).find()) {
				line = fileReader.readLine();
				if (line != null) {
					result.add(new AeResponseAssertion(AeResponseAssertionType.EXCLUDE, line.trim()));
				}
			}
			else {
				Matcher matcher = EXTERNAL_PATTERN_FILENAME_PATTERN.matcher(line);
				if (matcher.matches()) {
					if (parentDir == null) {
						parentDir = requestXmlFile.getParentFile();
					}
					result.addAll(extractAssertions(new File(parentDir, matcher.group(1).trim())));
				}
			}
		}
		return result;
	}

	private static class AeResponseAssertion {
		private final AeResponseAssertionType type;
		private final String pattern; // the pattern to be either included or excluded from the file
		private int cachedHash = 0;
		Boolean satisfied = null;

		AeResponseAssertion(AeResponseAssertionType type, String assertion) {
			this.type = type;
			this.pattern = assertion;
		}

		@SuppressWarnings("unused")
		public boolean matches(String line) {
			return !UtilBase.isEmpty(line) && line.matches(pattern);
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof AeResponseAssertion)) {
				return false;
			}
			AeResponseAssertion that = (AeResponseAssertion) obj;
			return this == that || (this.type == that.type && this.pattern.equals(that.pattern));
		}

		public int hashCode() {
			if (cachedHash == 0) {
				synchronized (this) {
					cachedHash = 17;
					cachedHash = cachedHash * 37 + type.hashCode();
					cachedHash = cachedHash * 37 + pattern.hashCode();
				}
			}
			return cachedHash;
		}

		public String toString() {
			return type + " " + pattern;
		}
	}

	private static class AeResponseAssertionType {
		public static final AeResponseAssertionType INCLUDE = new AeResponseAssertionType("Include", Pattern
				.compile("<!--\\s*[iI][nN][cC][lL][uU][dD][eE]"));
		public static final AeResponseAssertionType EXCLUDE = new AeResponseAssertionType("Exclude", Pattern
				.compile("<!--\\s*[eE][xX][cC][lL][uU][dD][eE]"));

		final String name;
		final Pattern assertionStartIndicator; // the xml comment pattern that initiates an instance of this assertion type

		AeResponseAssertionType(String name, Pattern assertionStartIndicator) {
			this.name = name;
			this.assertionStartIndicator = assertionStartIndicator;
		}

		public String toString() {
			return name;
		}
	}

	private File requestXml;
	private boolean saveOutput = true;
	private File outputDir;
	private CachedXPathAPI cachedXPathAPI;
	private DocumentBuilder documentBuilder;

	public DeployedRulesBatchTest(File requestXML) {
		super(DeployedRulesBatchTest.class.getName() + ": " + requestXML.getName());
		this.requestXml = requestXML;
	}

	protected void setUp() throws Exception {
		super.setUp();
		outputDir = new File(config.getRequiredStringProperty("mindbox.test.ruletest.output.dir"));
		if (outputDir.isFile()) {
			throw new IllegalStateException("Output directory must not be a file: " + outputDir);
		}
		else {
			outputDir.mkdirs();
		}
		this.cachedXPathAPI = new CachedXPathAPI();
		this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	protected void tearDown() throws Exception {
		cachedXPathAPI = null;
		super.tearDown();
	}

	protected void runTest() throws Throwable {
		logBegin();
		Set<AeResponseAssertion> failures = null;
		Set<AeResponseAssertion> assertions = extractAssertions(requestXml);
		if (!assertions.isEmpty()) { // avoid the round trip to the server if no assertions to test
			String responseStr = new ESPTestUtil(config).sendRequest(requestXml);
			if (saveOutput) {
				saveOutput(responseStr);
			}
			failures = executeAssertions(assertions, responseStr);
		}
		if (failures != null) {
			assertTrue("# of failures = " + failures.size() + "; Details: " + failures, failures.isEmpty());
		}
		logEnd();
	}

	/** @return Set of assertions that failed for one request/response cycle 
	 * @throws SAXException 
	 * @throws FactoryConfigurationError 
	 * @throws ParserConfigurationException 
	 * @throws SAXException */
	private Set<AeResponseAssertion> executeAssertions(Set<AeResponseAssertion> assertions, String responseStr) throws IOException,
			SAXException {
		Document rootNode = documentBuilder.parse(new InputSource(new StringReader(responseStr)));
		for (Iterator<AeResponseAssertion> assertionsIter = assertions.iterator(); assertionsIter.hasNext();) {
			AeResponseAssertion assertion = assertionsIter.next();

			// short-circuit if the assertion pattern already found eariler in the reponse
			// otherwise, if this line matches the assertion pattern...
			try {
				if (assertion.satisfied == null && matches(rootNode, assertion.pattern)) {
					// ...satisfied = true if the assertion is that the pattern is expected (i.e. INCLUDE), else false (i.e. EXCLUDE)
					assertion.satisfied = Boolean.valueOf(assertion.type == AeResponseAssertionType.INCLUDE);
				}
			}
			catch (TransformerException e) {
				logger.warn("Failed to check pattern " + assertion.pattern, e);
				assertion.satisfied = Boolean.FALSE;
			}
		}

		// gather failures
		Set<AeResponseAssertion> failures = new HashSet<AeResponseAssertion>();
		for (Iterator<AeResponseAssertion> assertionsIter = assertions.iterator(); assertionsIter.hasNext();) {
			AeResponseAssertion assertion = assertionsIter.next();
			// fail if satisfied == FALSE (n.b. this can only happen above to EXCLUDE type assertions)
			//         OR assertion is an INCLUDE (i.e. pattern expected) but not pattern was not found.
			if (assertion.satisfied == Boolean.FALSE || (assertion.satisfied == null && assertion.type == AeResponseAssertionType.INCLUDE)) {
				failures.add(assertion);
			}
		}

		return failures;
	}

	private boolean matches(Node rootNode, String pattern) throws TransformerException {
		return cachedXPathAPI.selectSingleNode(rootNode, pattern) != null;
	}

	private void saveOutput(String outputStr) {
		File outputFile = new File(outputDir, requestXml.getName().substring(0, requestXml.getName().length() - 4) + "-output.xml");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
			out.write(outputStr);
			out.flush();
			out.close();
		}
		catch (Exception ex) {
			logger.warn("Failed to store output into " + outputFile.getAbsolutePath());
		}
	}
}
