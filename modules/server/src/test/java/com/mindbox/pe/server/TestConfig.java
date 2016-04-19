/*
 * Created on Jul 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.server;

import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.servlet.Loader;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;

/**
 * TestConfig loads, and exposes the property values in, petest.properties. It finds petest.properties in the classpath
 * in the same package as this class.
 * <p>
 * It also loads the "version" property from the ${project.root}/build.properties file.
 * <p>
 * When running JUnit from the command line, if the current directory is NOT the ${project.root}, then manually set the
 * Java System property "user.dir" to the value ${project.root}. (hint: use either with a "-D" command line switch, or
 * with a "sysproperty" child tag under the "junit" tag in the Ant build file).
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since
 */
public final class TestConfig {

	public static final String PROPERTY_VERSION = "version";

	private static TestConfig INSTANCE = null;

	public static TestConfig getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TestConfig();
		}
		return INSTANCE;
	}

	private final Properties props = new Properties();

	private boolean cacheLoadAttempted = false;
	private boolean configInitialized = false;

	private TestConfig() {
		try {
			loadTestProperties();
			props.setProperty(PROPERTY_VERSION, "Version" + createString());
			Logger.getLogger(getClass()).info("*** CONFIGURATION COMPLETE " + props);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Failed to intialize test config: " + ex.getMessage());
		}
	}

	private void loadTestProperties() throws IOException {
		loadPropertiesFromStream(new FileInputStream("src/test/config/petest.properties"), props);
	}

	private void loadPropertiesFromStream(InputStream is, Properties p) throws IOException {
		try {
			p.load(is);
		}
		finally {
			is.close();
		}
	}

	/**
	 * Initializes server with the default config, specified in the test properties file. This is equivalent to
	 * <code>initServer(getConfigFilename())</code>.
	 * <p>
	 * If you use this in setUp(), you must call {@link #resetConfiguration()} in tearDown().
	 * 
	 * @throws Exception on error
	 * @see #resetConfiguration()
	 */
	public void initServer() throws Exception {
		initServer(getConfigFilename());
		configInitialized = true;
	}

	/**
	 * Initializes server with the specified config file.
	 * 
	 * <p>
	 * If you use this in setUp(), you must call {@link #resetConfiguration()} in tearDown().
	 * 
	 * @throws Exception on error
	 * @see #resetConfiguration()
	 */
	public void initServer(String configFilename) throws Exception {
		final PowerEditorConfiguration powerEditorConfiguration = XmlUtil.unmarshal(new FileReader(configFilename), PowerEditorConfiguration.class);
		ConfigurationManager.initialize("1.0", "b1", powerEditorConfiguration, configFilename);
		configInitialized = true;
	}

	/**
	 * Resets configuration to uninitialized state. This must be called in tearDown(), if setUp() uses
	 * {@link #initServer()} or {@link #initServer(String)}.
	 * 
	 * @see #initServer()
	 * @see #initServer(String)
	 */
	public void resetConfiguration() {
		if (configInitialized) {
			ReflectionUtil.setPrivate(ConfigurationManager.class, "instance", null);
			configInitialized = false;
		}
	}

	public void populateServerCache() throws Exception {
		if (!cacheLoadAttempted) {
			cacheLoadAttempted = true;
			Loader.loadToCache(false);
		}
	}

	public void refreshServerCache() throws Exception {
		// The cache MUST be populated before repopulating it, in order for user loading to
		// work correctly.
		if (!cacheLoadAttempted) {
			populateServerCache();
		}
		cacheLoadAttempted = true;
		Loader.loadToCache(false);
	}

	public String getRequiredStringProperty(String key) {
		String value = props.getProperty(key);
		assertNotNull("Could not find the required propery " + key, value);
		return value;
	}

	public String getConfigFilename() {
		return props.getProperty("mindbox.test.pe.config.file");
	}

	public String getUserID() {
		return props.getProperty("mindbox.test.pe.login.user", "demo");
	}

	public String getPassword() {
		return props.getProperty("mindbox.test.pe.login.pwd", "demo");
	}

	public String getServerURL() {
		return props.getProperty("mindbox.test.pe.server", "http://localhost:8080/powereditor/PowerEditorServlet");
	}

	public String getServerPrefix() {
		return props.getProperty("mindbox.test.pe.server.prefix", "powereditor");
	}

	public String getServerResourceParentPath() {
		return props.getProperty("mindbox.test.pe.server.resource.parent.path");
	}

	public File getDataFile(String filename) {
		return new File(props.getProperty("mindbox.test.pe.dir.data"), filename).getAbsoluteFile();
	}

	public InputStream getDataFileAsStream(String filename) throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream(getDataFile(filename)));
	}

	public String getDataFileContent(String filename) throws IOException {
		return getTextFileContext(getDataFile(filename));
	}

	public String getTextFileContext(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringWriter out = new StringWriter();
		for (String ln = reader.readLine(); ln != null;) {
			out.write(ln);
			ln = reader.readLine();
			if (ln != null) out.write(System.getProperty("line.separator"));
		}
		reader.close();
		out.flush();
		out.close();
		return out.toString();
	}
}
