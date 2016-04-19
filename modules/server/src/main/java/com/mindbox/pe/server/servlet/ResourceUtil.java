package com.mindbox.pe.server.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class ResourceUtil {

	private static final class ResourceClassLoader extends ClassLoader {

		private final File baseDir;

		ResourceClassLoader(String baseDir) {
			this.baseDir = new File(baseDir);
			if (!this.baseDir.exists()) {
				throw new IllegalArgumentException(baseDir + " does not exist");
			}
		}

		protected URL findResource(String arg0) {
			String[] strs = arg0.split("\\/");
			if (strs.length == 0) return null;
			File dir = baseDir;
			for (int i = 0; i < strs.length; i++) {
				dir = new File(dir, strs[i]);

				if (!dir.exists()) {
					return null;
				}
			}
			try {
				// dir now points to the file that exists
				URL url = new URL("file:///" + dir.getAbsolutePath());
				return url;
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}

		public URL getResource(String arg0) {
			return super.getResource(arg0);
		}

		public InputStream getResourceAsStream(String arg0) {
			return super.getResourceAsStream(arg0);
		}

	}


	private static ResourceUtil instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static ResourceUtil getInstance() {
		return instance;
	}

	/**
	 * 
	 * @param serverBaseDir
	 * @param propFiles must not be null
	 * @throws IOException
	 */
	static void initialize(String serverBaseDir, String[] propFiles) throws IOException {
		if (instance == null) {
			instance = new ResourceUtil();
		}
		else {
			instance.clearProperties();
		}
		instance.loadPropertyFiles(serverBaseDir, propFiles);
	}

	private final Properties props;
	private final Logger logger;

	private ResourceUtil() {
		logger = Logger.getLogger(getClass());
		props = new Properties();
	}

	private synchronized void clearProperties() {
		props.clear();
	}

	public String getResource(String key, Object... args) {
		if (args == null || args.length == 0) {
			return (props.containsKey(key) ? props.getProperty(key) : key);
		}
		else {
			return (props.containsKey(key) ? MessageFormat.format(props.getProperty(key), args) : String.format("%s with %s", key, args));
		}
	}

	private void loadLocale(ClassLoader classLoader, String configStr) {
		logger.debug(">>> loadLocale: " + configStr);
		ResourceBundle bundle = ResourceBundle.getBundle(configStr, Locale.getDefault(), classLoader);
		for (Enumeration<String> enumeration = bundle.getKeys(); enumeration.hasMoreElements();) {
			String key = enumeration.nextElement();
			props.put(key, bundle.getObject(key));
		}
		logger.debug("<<< loadLocale: " + props.size());
	}

	private synchronized void loadPropertyFiles(String serverBaseDir, String[] propFiles) throws IOException {
		assert (propFiles != null);
		logger.info(">>> loadPropertyFiles: " + serverBaseDir + ", " + propFiles.length + " files");
		ResourceClassLoader classLoader = new ResourceClassLoader(serverBaseDir);
		for (int i = 0; i < propFiles.length; i++) {
			loadLocale(classLoader, propFiles[i]);
		}
		logger.info("<<< loadPropertyFiles");
	}
}