package com.mindbox.pe.server.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ServerConfiguration;

/**
 * Manages writes for various deploy generated files for a particular status.
 * Responsible for determining the deploy directory and the names of the generated files.
 * <p>
 * Create one instance for each unique deployment.
 * @author Geneho
 * @since PowerEditor 1.0
 */
public class OutputController {

	public static final String ERROR_FILE = "deploy-errors.txt";
	private static final boolean AUTO_FLUSH = true;

	private static final MessageFormat errorFormat = new MessageFormat("{0,date,yyyy-MM-dd HH:mm:sss z} [{1}] {2}");

	private class WriterSet {

		final Map<String, PrintWriter> ruleWriters = Collections.synchronizedMap(new HashMap<String, PrintWriter>());
		final Map<String, PrintWriter> paramWriters = Collections.synchronizedMap(new HashMap<String, PrintWriter>());
		PrintWriter errorWriter;

		WriterSet() {
			initWriters();
		}

		private void createDeployDirsIfNecessary() throws RuleGenerationException {
			if (!deployDir.exists()) {
				boolean flag = deployDir.mkdirs();
				if (!flag) {
					logger.warn("Could not create necessary directories: " + deployDir);
					throw new RuleGenerationException("Failed to create deploy directory structure: " + deployDir);
				}
				logger.debug("Succeeded in creating dirs for: " + deployDir);
			}	
		}
			
		private void initWriters() {
			ruleWriters.clear();
			paramWriters.clear();
		}

		PrintWriter getErrorWriter() {
			if (errorWriter != null) return errorWriter;
			try {
				createDeployDirsIfNecessary();
				File file = new File(deployDir, ERROR_FILE);
                if (file.exists()) {
                    if (!file.delete()) {
                        logger.warn("* unable to delete pre-existing error log at " + file.getAbsolutePath());                        
                    } else {
                        logger.info("* Deleted old and re-creating new error log at " + file.getAbsolutePath());                        
                    }
                } else {
                    logger.info("* Creating error log at " + file.getAbsolutePath());                    
                }
				errorWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true);
			}
			catch (Exception ex) {
				logger.error("Failed to get error writer", ex);
				ex.printStackTrace();
			}
			return errorWriter;
		}

		void closeRuleWriters() {
			for (Iterator<PrintWriter> iter = ruleWriters.values().iterator(); iter.hasNext();) {
				PrintWriter element = iter.next();
				element.close();
			}
		}

		void closeErrorWriter() {
			if (errorWriter != null) {
				errorWriter.close();
				errorWriter = null;
			}
		}

		void closeParameterWriters() {
			for (Iterator<PrintWriter> iter = paramWriters.values().iterator(); iter.hasNext();) {
				PrintWriter element = iter.next();
				element.flush();
				element.close();
			}
		}

		/**
		 * Gets a PrintWriter that writes to a file whose name is &quot;<code>name</code>.art&quot; in the deploy directory.
		 * @param name the name of the file (without extension)
		 * @return the PrintWriter
		 * @since PowerEditor 3.3.0
		 */
		PrintWriter getAEFileWriter(String name) throws RuleGenerationException {
			createDeployDirsIfNecessary();
			logger.debug(">>> getAEFileWriter: " + name);
			PrintWriter printwriter = ruleWriters.get(name);
			if (printwriter != null) return printwriter;
			String filename = name + ".art";
			try {
				File file = new File(deployDir, filename);
				printwriter = new PrintWriter(new BufferedWriter(new FileWriter(file)), AUTO_FLUSH);
				ruleWriters.put(name, printwriter);
				logger.debug("<<< getAEFileWriter: " + filename);
				return printwriter;
			}
			catch (Exception exception) {
				logger.error("Failed to get AE file writer for " + name, exception);
				throw new RuleGenerationException("Failed to get AE rule writer for " + name + ": " + exception.getMessage());
			}
		}

		PrintWriter getObjectWriter(String prefix) throws RuleGenerationException {
			createDeployDirsIfNecessary();
			logger.debug(">>> getParameterWriter: " + prefix);
			PrintWriter printwriter = paramWriters.get(prefix);
			if (printwriter != null) { return printwriter; }

			String filename = prefix + "-instances.art";
			logger.debug("    getParamterWriter: deployDir = " + deployDir + " ,filename = " + filename);
			try {
				File file = new File(deployDir, filename);
				printwriter = new PrintWriter(new BufferedWriter(new FileWriter(file)), AUTO_FLUSH);
				paramWriters.put(prefix, printwriter);

				return printwriter;
			}
			catch (Exception exception) {
				logger.error("Failed to get parameter writer for " + prefix, exception);
				throw new RuleGenerationException("Failed to get parameter writer for " + prefix + ": " + exception.getMessage());
			}
		}

		void writeErrorMessage(String context, String message) {
			logger.warn("*** ErrorMessage: " + message + " at " + context);
			getErrorWriter().println(errorFormat.format(new Object[] { new Date(), context, message}));
		}
	}

	private static final DateFormat TIMESTAMP_DIR_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");

	private final Logger logger;
	private final Map<String, WriterSet> writerSetMap;
	private final String status;
	private final File deployDir;

	/**
	 * Constructs a new Output controller with the specified status.
	 * @param status
	 * @throws RuleGenerationException
	 */
	public OutputController(String status) throws RuleGenerationException {
		this.logger = Logger.getLogger(OutputController.class);
		logger.debug("creating OutputController: " + status);
		this.status = status;
		writerSetMap = new HashMap<String, WriterSet>();
		deployDir = initDeployDir(new Date());

		TypeEnumValue deployTargetStatus = TypeEnumValueManager.getInstance().getEnumValueForValue(TypeEnumValue.TYPE_STATUS, status);
		if (deployTargetStatus == null) throw new RuleGenerationException("The specified status is not found: " + status);

		List<TypeEnumValue> allStatuses = TypeEnumValueManager.getInstance().getAllEnumValues(TypeEnumValue.TYPE_STATUS);
		for (Iterator<TypeEnumValue> iter = allStatuses.iterator(); iter.hasNext();) {
			TypeEnumValue writerStatus = iter.next();
			if (writerStatus.getID() >= deployTargetStatus.getID()) {
				writerSetMap.put(writerStatus.getValue(), new WriterSet());
			}
		}
	}

	private File initDeployDir(Date deployTime) throws RuleGenerationException {
		ServerConfiguration.DeploymentConfig deployConfig = ConfigurationManager.getInstance().getServerConfiguration().getDeploymentConfig();
		
		String baseDirName = deployConfig.getBaseDir() + (deployConfig.getBaseDir().endsWith(File.separator) ? "" : File.separator);
		String deployDirName = baseDirName + status + (deployConfig.useTimestampFolder() ? File.separator + getTimestampDirName(deployTime) : "");
		
		if (deployConfig.saveOldFiles() && !deployConfig.useTimestampFolder()) { // no need to save old when using timestamp folders
			backupDeployDir(baseDirName, deployDirName, deployTime);
		}
		
		return new File(deployDirName);
	}

	private void backupDeployDir(String baseDir, String deployDirName, Date deployTime) throws RuleGenerationException {
		try {
			File[] filesToBeBackedUp = new File(deployDirName).listFiles();
			if (filesToBeBackedUp != null && filesToBeBackedUp.length > 0) {

				String backupDirName = createBackupDir(baseDir, deployTime);
				
				for (int i = 0; i < filesToBeBackedUp.length; i++) {
					if (filesToBeBackedUp[i].isFile()) {
						String newFileName = backupDirName + File.separator + filesToBeBackedUp[i].getName();
						boolean fileMovedSuccessfully = filesToBeBackedUp[i].renameTo(new File(newFileName));
						
						if (!fileMovedSuccessfully) {
							logger.warn("Could not backup " + filesToBeBackedUp[i].getName() + " to " + newFileName);
							throw new RuleGenerationException("Failed to backup " + filesToBeBackedUp[i].getName() + " to " + backupDirName + ".");
						}
					}
				}
			}
		}
		catch (RuleGenerationException rge) {
			throw rge;
		}
		catch (Exception e) {
			logger.error("Failed to initialize with " + status, e);
			throw new RuleGenerationException("Error initializing output-controller: " + e.getMessage());
		}
	}

	private String createBackupDir(String baseDir, Date deployTime) throws RuleGenerationException {
		String backupDirName = baseDir + "backup" + File.separator + status + File.separator + getTimestampDirName(deployTime);
		logger.info("Backup Dir - " + backupDirName);
		
		File backupDir = new File(backupDirName);
		if (!backupDir.exists()) {
			if (!backupDir.mkdirs()) {
				logger.warn("Could not create necessary backup directories: " + backupDirName);
				throw new RuleGenerationException("Failed to create backup directories: " + backupDirName);
			}
		}
		return backupDirName;
	}

	private String getTimestampDirName(Date d) {
		return TIMESTAMP_DIR_FORMAT.format(d);
	}

	public String getDeployDir() {
		return deployDir.getAbsolutePath();
	}

	public final String getStatus() {
		return status;
	}

	// TODO Kim, 2007-01-11: modify to throw a runtime exception, not checked exception
	//           An exception from is should be caught and dealt with. Because we are
	//           using a checked exception, sometimes we need to catch it, and
	//           other times, we just pass it on. We need to remove this confusion!!!
	private WriterSet getWriterSetForStatus(String status) throws RuleGenerationException {
		if (writerSetMap.containsKey(status)) {
			return writerSetMap.get(status);
		}
		else {
			throw new RuleGenerationException("The specified status is not found: " + status);
		}
	}

	private PrintWriter getRuleWriter(String status, String prefix) throws RuleGenerationException {
		return getAEFileWriter(status, prefix + "-rules");
	}

	/**
	 * Gets a PrintWriter that writes to a file whose name is &quot;<code>name</code>.art&quot; in the deploy directory.
	 * @param name the name of the file (without extension)
	 * @return the PrintWriter
	 * @since PowerEditor 3.3.0
	 */
	public PrintWriter getAEFileWriter(String status, String name) throws RuleGenerationException {
		logger.debug(">>> getAEFileWriter: " + status + "," + name);
		return getWriterSetForStatus(status).getAEFileWriter(name);
	}

	public PrintWriter getRuleWriter(String status, AbstractGenerateParms abstractgenerateparms) throws RuleGenerationException {
		return getRuleWriter(status, AeMapper.getRuleset(abstractgenerateparms));
	}

	public PrintWriter getParameterWriter(String status) throws RuleGenerationException {
		logger.debug(">>> getParameterWriter: " + status);
		return getWriterSetForStatus(status).getObjectWriter("parameter");
	}

	public PrintWriter getTimeSliceWriter(String status) throws RuleGenerationException {
		logger.debug(">>> getTimeSliceWriter: " + status);
		return getWriterSetForStatus(status).getObjectWriter("time-slice");
	}

	public void writeErrorMessage(String context, String message) throws RuleGenerationException {
		logger.warn("*** ErrorMessage: " + status + "," + message + " at " + context);
		getWriterSetForStatus(this.status).writeErrorMessage(context, message);
	}

	public PrintWriter getErrorWriter() throws RuleGenerationException {
		return getWriterSetForStatus(this.status).getErrorWriter();
	}

	public void closeRuleWriters() {
		for (Iterator<WriterSet> iter = writerSetMap.values().iterator(); iter.hasNext();) {
			WriterSet element = iter.next();
			try {
				element.closeRuleWriters();
			}
			catch (Exception ex) {
				logger.warn("Failed to close rule writers for  " + element, ex);
			}
		}
	}

	public void closeErrorWriters() {
		for (Iterator<WriterSet> iter = writerSetMap.values().iterator(); iter.hasNext();) {
			WriterSet element = iter.next();
			try {
				element.closeErrorWriter();
			}
			catch (Exception ex) {
				logger.warn("Failed to close error writer for  " + element, ex);
			}
		}
	}

	public void closeParameterWriters() {
		for (Iterator<WriterSet> iter = writerSetMap.values().iterator(); iter.hasNext();) {
			WriterSet element = iter.next();
			try {
				element.closeParameterWriters();
			}
			catch (Exception ex) {
				logger.warn("Failed to close parameter writers for  " + element, ex);
			}
		}
	}

}