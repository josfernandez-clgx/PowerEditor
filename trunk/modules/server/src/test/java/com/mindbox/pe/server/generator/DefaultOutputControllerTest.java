package com.mindbox.pe.server.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import com.mindbox.pe.common.IOUtil;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.TypeEnumValueManager;

public class DefaultOutputControllerTest extends AbstractTestWithTestConfig {

	private DefaultOutputController outputController;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(1, "Draft", "Draft"));
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(2, "Production", "Production"));
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		TypeEnumValueManager.getInstance().startLoading();
		super.tearDown();
	}

	/**
	 * This test makes sure that if a pre-existing error log file exists when the error writer is created, the old error
	 * file is deleted. This fixes TT item 1766.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOldErrorMessageFileIsDeletedWhenNewErrorWriterCreated() throws Exception {
		FileWriter writer = null;
		try {
			outputController = new DefaultOutputController("Draft");
			File deployDir = (File) ReflectionUtil.getPrivate(outputController, "deployDir");
			deployDir.mkdirs();
			File file = new File(deployDir, DefaultOutputController.ERROR_FILE);
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new FileWriter(file);
			writer.write("this is a test");
			writer.flush();
			writer.close();
			writer = null;

			// sanity check
			assertTrue(file.length() > 0);
			file = null;

			// this should delete the file
			file = outputController.getErrorFile();

			assertEquals(new Long(0), new Long(file.length()));
		}
		catch (RuntimeException ex) {
			logger.error("testOldErrorMessageFileIsDeletedWhenNewErrorWriterCreated failed", ex);
			fail("Exception thrown: " + ex);
		}
		finally {
			IOUtil.close(writer);
		}
	}

	@Test
	public void testWriteErrorMessageBeforeOtherMethodsThrowsNoException() throws Exception {
		// create new outputcontroller
		outputController = new DefaultOutputController("Draft");

		// this will throw a runtime exception if test fails
		try {
			outputController.writeErrorMessage("testWriteErrorMessageBeforeOtherMethodsThrowsNoException", "test error message -- ignore");
		}
		catch (RuntimeException ex) {
			logger.error("testWriteErrorMessageBeforeOtherMethodsThrowsNoException failed", ex);
			fail("Exception thrown: " + ex);
		}
	}

}
