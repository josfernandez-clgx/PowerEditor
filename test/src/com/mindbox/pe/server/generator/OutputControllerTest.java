package com.mindbox.pe.server.generator;

import java.io.File;
import java.io.FileWriter;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ServerConfiguration.DeploymentConfig;
import com.mindbox.pe.server.generator.OutputController;

public class OutputControllerTest extends AbstractTestWithTestConfig {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("OutputControllerTest Tests");
		suite.addTestSuite(OutputControllerTest.class);
		return suite;
	}

	private OutputController outputController;
	
	public OutputControllerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(1, "Draft", "Draft"));
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(2, "Production", "Production"));
	}

	protected void tearDown() throws Exception {
		if (outputController != null) {
			outputController.closeErrorWriters();
			outputController.closeRuleWriters();
			outputController.closeParameterWriters();
		}
		config.resetConfiguration();
		TypeEnumValueManager.getInstance().startLoading();
		super.tearDown();
	}

	public void testConstructorDoNotCreateDeployDir() throws Exception {
		// clear deploy dir
		DeploymentConfig deployConfig = ConfigurationManager.getInstance().getServerConfiguration().getDeploymentConfig();
		File deployBaseDir = new File(deployConfig.getBaseDir());

		deleteDir(deployBaseDir, "Failed to clear deploy base dir");
		deployBaseDir.mkdirs();

		// create new outputcontroller
		outputController = new OutputController("Draft");

		// check deploy dir is not created
		File[] files = deployBaseDir.listFiles();
		assertTrue("Deploy base directory is not empty", files == null || files.length == 0);
	}

	public void testWriteErrorMessageBeforeOtherMethodsThrowsNoException() throws Exception {
		// create new outputcontroller
		outputController = new OutputController("Draft");

		// this will throw a runtime exception if test fails
		try {
			outputController.writeErrorMessage(getName(), "test error message -- ignore");
		}
		catch (RuntimeException ex) {
			logger.error(getName() + " failed", ex);
			fail("Exception thrown: " + ex);
		}
	}
    
    /**
     * This test makes sure that if a pre-existing error log file exists when the error writer
     * is created, the old error file is deleted. This fixes TT item 1766.
     * @throws Exception
     */
    public void testOldErrorMessageFileIsDeletedWhenNewErrorWriterCreated() throws Exception {
        try {
        	outputController = new OutputController("Draft");
            File deployDir = (File)ReflectionUtil.getPrivate(outputController, "deployDir");
            deployDir.mkdir();
            File file = new File(deployDir, OutputController.ERROR_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write("this is a test");
            writer.flush();
            writer.close();
            assertTrue(file.length() > 0);
            // this should delete the file
            outputController.getErrorWriter();
            file = new File(deployDir, OutputController.ERROR_FILE);
            long l = file.length();
            assertTrue(l == 0);
        }
        catch (RuntimeException ex) {
            logger.error(getName() + " failed", ex);
            fail("Exception thrown: " + ex);
        }
    }    

}
