package com.mindbox.ftest.pe.rulegen;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.ftest.pe.util.AbstractRemotePEServerTest;
import com.mindbox.ftest.pe.util.ESPTestUtil;
import com.mindbox.ftest.pe.util.TimeOutCommandExecutor;
import com.mindbox.pe.model.Constants;

/**
 * Contains tests to rebuild a clean PE DB and runs full rule regression tests.
 * 
 * @author kim
 *
 */
public class FullRegressionWithRebuildTests extends AbstractRemotePEServerTest {

	private static final String WAR_FILENAME = "powereditor-pete.war";
	private static final long RULE_TEST_TIMEOUT = 15 * 60 * 1000; // 15 minutes

	public static Test suite() {
		TestSuite suite = new TestSuite("FullRegressionWithRebuildTests Tests");
		suite.addTest(new FullRegressionWithRebuildTests("Full Rule Generation Regression Test"));
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private ESPTestUtil espTestUtil;
	private File webAppsDir;
	private File ruleTestScriptFile;

	public FullRegressionWithRebuildTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.espTestUtil = new ESPTestUtil(config);
		this.ruleTestScriptFile = new File(config.getRequiredStringProperty("mindbox.test.pete.rule.test.script"));
		assertTrue("Rule regression test script does not exist at " + ruleTestScriptFile.getAbsolutePath(), ruleTestScriptFile.exists());
		this.webAppsDir = new File(config.getRequiredStringProperty("mindbox.test.app.server.webapps.dir"));
		assertTrue("PETE webapps directory does not exist at " + webAppsDir.getAbsolutePath(), webAppsDir.exists());
	}

	protected void runTest() throws Throwable {
		logBegin();

		// VERIFY that powereditor.war is built
		File peWarFile = new File("build", WAR_FILENAME);
		assertTrue("PETE War file does not exist; expected at " + peWarFile.getAbsolutePath(), peWarFile.exists());

		// 1. Stop ESP
		if (espTestUtil.isESPRunning()) {
			logger.info("ESP is running. Stopping it first...");
			// GKIM: Because of the way ESP Windows Service works (or configured)
			//       we need to (1) send shutdown request AND (2) then, stop the service.
			espTestUtil.shutDownESP();
			// wait 1 second
			Thread.sleep(1000L);
			stopESPService();			
		}

		// 2. Stop app server
		if (isPowerEditorRunning()) {
			logger.info("PETE PE is running. Stopping PETE app server now...");
			stopAppServerService();
		}

		// 3. Deploy WAR. Assume the latest war is already built by Ant
		logger.info("Deploying PETE war...");
		deployWar(peWarFile);

		// 4. Copy clean DB
		logger.info("Preparing PETE empty DB...");
		prepareCleanPETEDB();

		// 5. Start app server
		logger.info("Starting PETE app server...");
		startAppServerService();

		// 7. Verify app server is running
		assertPowerEditorIsRunning(5 * 60 * 1000L); // allow 5 minutes for PE to come up

		// 8. Import PETE-REF KB
		File fileToImport = new File(config.getRequiredStringProperty("mindbox.test.pete.pe.db.dir"), "PETE-REF-PEDB.xml");
		logger.info("Remotely importing " + fileToImport.getAbsolutePath());
		remoteImport(fileToImport);

		// 9. Generate all rules
		logger.info("Remotely deploying...");
		remoteDeploy(Constants.DRAFT_STATUS);

		// 10. Start ESP
		logger.info("Starting ESP...");
		startESPService();

		// 11. Verify ESP is running
		assertTrue("ESP is not running", espTestUtil.verifyESPIsRunning(5 * 60 * 1000L)); // allow 5 minutes for ESP to come up

		// 12. Run regression tests
		logger.info("Running regression test script " + ruleTestScriptFile.getAbsolutePath());
		runRuleRegressionTests();

		logEnd();
	}

	private void runRuleRegressionTests() throws Exception {
		// TODO Kim: pass additional arguments if required
		String[] cmdArray = new String[] { ruleTestScriptFile.getAbsolutePath() };
		TimeOutCommandExecutor commandExecutor = new TimeOutCommandExecutor(RULE_TEST_TIMEOUT);
		assertEquals("Rule test failed; check output files for details", 0, commandExecutor.execute(cmdArray));
	}

	protected void prepareCleanPETEDB() throws Exception {
		File targetFile = new File(config.getRequiredStringProperty("mindbox.test.pete.pe.db.dir"), "PETE-REF-PEDB.mdb");
		File sourceFile = new File("src/database/MB-PEDB-EMPTY.mdb");
		copy(sourceFile, targetFile);
	}

	protected void deployWar(File peWarFile) throws Exception {
		File peDir = new File(webAppsDir, "powereditor");
		deleteDir(peDir);
		copy(peWarFile, new File(webAppsDir, "powereditor.war"));
	}
}
