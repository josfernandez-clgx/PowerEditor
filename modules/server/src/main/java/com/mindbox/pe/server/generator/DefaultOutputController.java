package com.mindbox.pe.server.generator;

import static com.mindbox.pe.common.IOUtil.close;
import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;
import static com.mindbox.pe.server.generator.RuleGeneratorHelper.ART_FILE_EXTENSION;
import static com.mindbox.pe.server.generator.RuleGeneratorHelper.getRuleFilename;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.ServerConfig.Deployment;

/**
 * Manages various deploy generated files for a particular status.
 * <p>
 * Responsible for determining the deploy directory and the names of the generated files.
 * Create one instance for each unique deployment.
 * </p>
 * <p>This is thread-safe.</p>.
 * @author Geneho
 * @since PowerEditor 1.0
 */
public class DefaultOutputController implements OutputController {


	public static final String ERROR_FILE = "deploy-errors.txt";

	private static final MessageFormat ERROR_MESSAGE_FORMAT = new MessageFormat("{0,date,yyyy-MM-dd HH:mm:sss z} [{1}] {2}");
	private static final DateFormat TIMESTAMP_DIR_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
	private static final Logger LOG = Logger.getLogger(DefaultOutputController.class);


	private final String status;
	private final File deployDir;
	private File errorFile = null;
	private final Map<String, File> ruleFileMap = new HashMap<String, File>();
	private final Map<String, File> objectFileMap = new HashMap<String, File>();

	/**
	 * Constructs a new Output controller with the specified status.
	 * @param status status
	 * @throws RuleGenerationException on error
	 */
	public DefaultOutputController(final String status) throws RuleGenerationException {
		TypeEnumValue deployTargetStatus = TypeEnumValueManager.getInstance().getEnumValueForValue(TypeEnumValue.TYPE_STATUS, status);
		if (deployTargetStatus == null) {
			throw new RuleGenerationException("The specified status is not found: " + status);
		}

		logDebug(LOG, "creating OutputController: %s", status);
		this.status = status;
		deployDir = initDeployDir(new Date());

		logDebug(LOG, "Created OutputController for %s", status);
	}

	private void backupDeployDir(final String baseDir, final String deployDirName, final Date deployTime) throws RuleGenerationException {
		try {
			File[] filesToBeBackedUp = new File(deployDirName).listFiles();
			if (filesToBeBackedUp != null && filesToBeBackedUp.length > 0) {

				String backupDirName = createBackupDir(baseDir, deployTime);

				for (int i = 0; i < filesToBeBackedUp.length; i++) {
					if (filesToBeBackedUp[i].isFile()) {
						String newFileName = backupDirName + File.separator + filesToBeBackedUp[i].getName();
						boolean fileMovedSuccessfully = filesToBeBackedUp[i].renameTo(new File(newFileName));

						if (!fileMovedSuccessfully) {
							LOG.warn("Could not backup " + filesToBeBackedUp[i].getName() + " to " + newFileName);
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
			LOG.error("Failed to initialize with " + status, e);
			throw new RuleGenerationException("Error initializing output-controller: " + e.getMessage());
		}
	}

	private String createBackupDir(String baseDir, Date deployTime) throws RuleGenerationException {
		String backupDirName = baseDir + "backup" + File.separator + status + File.separator + getTimestampDirName(deployTime);
		LOG.info("Backup Dir - " + backupDirName);

		File backupDir = new File(backupDirName);
		if (!backupDir.exists()) {
			if (!backupDir.mkdirs()) {
				LOG.warn("Could not create necessary backup directories: " + backupDirName);
				throw new RuleGenerationException("Failed to create backup directories: " + backupDirName);
			}
		}
		return backupDirName;
	}

	private synchronized void createDeployDirsIfNecessary() throws RuleGenerationException {
		if (!deployDir.exists()) {
			boolean flag = deployDir.mkdirs();
			if (!flag) {
				LOG.warn("Could not create necessary directories: " + deployDir);
				throw new RuleGenerationException("Failed to create deploy directory structure: " + deployDir);
			}
			logDebug(LOG, "Succeeded in creating dirs for [%s]", deployDir);
		}
	}

	@Override
	public File getCbrFile() throws RuleGenerationException {
		return getObjectFile("cbr-data");
	}

	@Override
	public String getDeployDir() {
		return deployDir.getAbsolutePath();
	}

	@Override
	public synchronized File getErrorFile() throws RuleGenerationException {
		if (errorFile == null) {
			errorFile = getErrorFileInternal();
		}
		return errorFile;
	}

	private File getErrorFileInternal() throws RuleGenerationException {
		createDeployDirsIfNecessary();
		final File file = new File(deployDir, ERROR_FILE);
		if (file.exists()) {
			logDebug(LOG, "Deleting old error file at %s...", file.getAbsolutePath());
			if (!file.delete()) {
				logWarn(LOG, "* unable to delete pre-existing error log at %s", file.getAbsolutePath());
			}
			else {
				logInfo(LOG, "* Deleted old and re-creating new error log at %s", file.getAbsolutePath());
			}
		}
		else {
			logInfo(LOG, "* Creating error log at %s", file.getAbsolutePath());
		}
		return file;
	}

	private File getObjectFile(String prefix) throws RuleGenerationException {
		createDeployDirsIfNecessary();
		synchronized (objectFileMap) {
			logDebug(LOG, ">>> getObjectFile: %s", prefix);
			File file;
			if (objectFileMap.containsKey(prefix)) {
				file = objectFileMap.get(prefix);
			}
			else {
				createDeployDirsIfNecessary();

				final String filename = String.format("%s-instances%s", prefix, ART_FILE_EXTENSION);
				logDebug(LOG, "    getObjectFile: deployDir = %s, filename=%s", deployDir, filename);
				file = new File(deployDir, filename);
			}
			return file;
		}
	}

	@Override
	public File getParameterFile() throws RuleGenerationException {
		return getObjectFile("parameter");
	}

	@Override
	public File getProcessFile() throws RuleGenerationException {
		return getObjectFile("app-phase-data");
	}

	@Override
	public File getRuleFile(GenerationParams generationParams) throws RuleGenerationException {
		return getRuleFile(AeMapper.getRuleset(generationParams), (generationParams.getTemplate() == null ? "" : generationParams.getTemplate().getName()));
	}

	private File getRuleFile(String ruleSetName, String templateName) throws RuleGenerationException {
		return getRuleFileInternal(getRuleFilename(ruleSetName, templateName));
	}

	@Override
	public File getRuleFile(TemplateUsageType templateUsageType) throws RuleGenerationException {
		return getRuleFile(templateUsageType, null);
	}

	@Override
	public File getRuleFile(TemplateUsageType templateUsageType, String templateName) throws RuleGenerationException {
		return getRuleFile(AeMapper.getRuleset(templateUsageType), templateName);
	}

	private File getRuleFileInternal(final String baseName) throws RuleGenerationException {
		createDeployDirsIfNecessary();
		logDebug(LOG, "getRuleFileInternal: %s", baseName);
		synchronized (ruleFileMap) {
			File file;
			if (ruleFileMap.containsKey(baseName)) {
				file = ruleFileMap.get(baseName);
			}
			else {
				file = new File(deployDir, baseName + ART_FILE_EXTENSION);
				logDebug(LOG, "getRuleFileInternal: deployDir = %s, filename=%s", deployDir, file.getName());
			}
			return file;
		}
	}

	@Override
	public final String getStatus() {
		return status;
	}

	@Override
	public File getTimeSliceFile() throws RuleGenerationException {
		return getObjectFile("time-slice");
	}

	private String getTimestampDirName(Date d) {
		synchronized (TIMESTAMP_DIR_FORMAT) {
			return TIMESTAMP_DIR_FORMAT.format(d);
		}
	}

	private File initDeployDir(Date deployTime) throws RuleGenerationException {
		Deployment deployConfig = ConfigurationManager.getInstance().getServerConfigHelper().getDeploymentConfig();

		final boolean useTimestampFolder = UtilBase.asBoolean(deployConfig.isUseTimeStampFolder(), true);
		String baseDirName = deployConfig.getBaseDir() + (deployConfig.getBaseDir().endsWith(File.separator) ? "" : File.separator);
		String deployDirName = baseDirName + status + (useTimestampFolder ? File.separator + getTimestampDirName(deployTime) : "");

		if (UtilBase.asBoolean(deployConfig.isSaveOldFiles(), true) && !useTimestampFolder) { // no need to save old when using timestamp folders
			backupDeployDir(baseDirName, deployDirName, deployTime);
		}

		return new File(deployDirName);
	}

	@Override
	public void writeErrorMessage(String context, String message) throws RuleGenerationException {
		logWarn(LOG, "*** ErrorMessage: %s, %s at %s", status, message, context);
		getErrorFile();
		synchronized (errorFile) {
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(new FileWriter(errorFile, true));
				writer.println(ERROR_MESSAGE_FORMAT.format(new Object[] { new Date(), context, message }));
				writer.flush();
			}
			catch (Exception e) {
				logWarn(LOG, e, "Failed to write error message: %s (context:%s)", message, context);
			}
			finally {
				close(writer);
			}
		}
	}
}