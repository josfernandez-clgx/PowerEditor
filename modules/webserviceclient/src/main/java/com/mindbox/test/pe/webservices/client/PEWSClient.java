package com.mindbox.test.pe.webservices.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import com.mindbox.pe.test.wsdl.api.DeployRequest;
import com.mindbox.pe.test.wsdl.api.DeployResponse;
import com.mindbox.pe.test.wsdl.api.DeployWithCredentialsRequest;
import com.mindbox.pe.test.wsdl.api.DeployWithCredentialsResponse;
import com.mindbox.pe.test.wsdl.api.ExportDataRequest;
import com.mindbox.pe.test.wsdl.api.ExportDataWithCredentialsRequest;
import com.mindbox.pe.test.wsdl.api.ImportEntitiesRequest;
import com.mindbox.pe.test.wsdl.api.ImportEntitiesWithCredentialsRequest;
import com.mindbox.pe.test.wsdl.api.PingRequest;
import com.mindbox.pe.test.wsdl.api.PowerEditorAPIInterface;
import com.mindbox.pe.test.wsdl.api.PowerEditorAPIInterfaceService;
import com.mindbox.pe.test.wsdl.api.PowerEditorInterfaceReturnStructure;

/**
 * Implements a test client that can call import, export, deploy, and ping
 * functions using PowerEditor's web services interface.
 *
 */
public class PEWSClient implements Callable<String> {

	enum Command {
		DEPLOY, EXPORT, IMPORT, PING;
	}

	private static final String DEFAULT_USER = "demo";
	private static final String DEFAULT_PASSWORD = "demo";

	/**
	 * Display a list of messages with a given heading.
	 * @param messageList
	 * @param heading
	 */
	private static void displayMessages(List<String> messageList, String heading) {
		System.out.format("%n--------------%n%s%n--------------%n", heading);
		if (messageList != null && !messageList.isEmpty()) {
			int loop = 1;
			for (final String message : messageList) {
				System.out.format("%d: %s%n", loop++, message);
			}
		}
	}

	private static void displayResults(final PowerEditorInterfaceReturnStructure peirs) {
		displayMessages(peirs.getErrorMessages(), "Errors");
		displayMessages(peirs.getWarningMessages(), "Warnins");
		displayMessages(peirs.getGeneralMessages(), "Messages");

	}

	private static String getVersion() throws Exception {
		final Properties properties = new Properties();
		properties.load(PEWSClient.class.getResourceAsStream("app.properties"));
		return properties.getProperty("app.version");
	}

	/**
	 * Allows all kinds of web services calls.  See usage messages for details.
	 * @param args arguments
	 * @throws Exception on error
	 */
	public static void main(String... args) throws Exception {
		try {
			final PEWSClient client = new PEWSClient(args);
			System.out.format("%n[RESULT]%n%s%n%n", client.call());
		}
		catch (IllegalArgumentException e) {
			if (e.getMessage() != null) {
				System.err.format("*ERROR* %s%n%n", e.getMessage());
			}
			printUsage();
		}
		catch (ArrayIndexOutOfBoundsException e) {
			printUsage();
		}
	}

	private static void printUsage() {
		System.out.format(
				"Usage: %n  java -jar PEWSClient.jar <PE-URL> <command> + arguments%n%nArguments vary by command:"
						+ "%n---------------------------------%ncommand arguments%n---------------------------------%n");
		System.out.println("import  <filename> [merge] [username] [password]");
		System.out.println("   filename: the name of the file to import");
		System.out.println("   merge:    must be true or false; defaults to false, if missing");
		System.out.println("   username: PowerEditor user name (uses 'demo' if missing or if password is missing)");
		System.out.println("   password: PowerEditor password (uses 'demo' if missing)");
		System.out.println("");
		System.out.println("export  <filename> [username] [password]");
		System.out.println("   filename: the name of the file to fill with export results");
		System.out.println("   username: PowerEditor user name (uses 'demo' if missing or if password is missing)");
		System.out.println("   password: PowerEditor password (uses 'demo' if missing)");
		System.out.println("");
		System.out.println("deploy  [username] [password]");
		System.out.println("   username: PowerEditor user name (uses 'demo' if missing or if password is missing)");
		System.out.println("   password: PowerEditor password (uses 'demo' if missing)");
		System.out.println("");
		System.out.println("ping    [username] [password]");
		System.out.println("   username: PowerEditor user name (uses 'demo' if missing or if password is missing)");
		System.out.println("   password: PowerEditor password (uses 'demo' if missing)");
		System.out.format("---------------------------------%nSample: java -jar PEWSClient.jar http://localhost:8080/powereditor ping demo demo%n%n");
		System.exit(1);
	}

	/**
	 * Read the contents of a file and return them as a String.
	 * @param file
	 * @return String with file contents
	 * @throws IOException
	 */
	private static String readFile(final File file) throws IOException {
		final BufferedReader in = new BufferedReader(new FileReader(file));
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter out = new PrintWriter(stringWriter);
		try {
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				out.println(line);
			}
			out.flush();
			return stringWriter.toString();
		}
		finally {
			out.close();
			in.close();
		}
	}

	/**
	 * Save the content to a file with the path specified
	 * @param filename  -  full pathname to a file
	 * @param content  - a String of text
	 * @throws IOException
	 */
	private static void writeFile(final File file, final String content) throws IOException {
		final PrintWriter pout = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		try {
			pout.print(content);
			pout.flush();
		}
		finally {
			pout.close();
		}
	}

	private final boolean merge;
	private final Command command;
	private final File file;
	private final String username;
	private final String password;
	private final URL serverUrl;

	/**
	 * Constructor.
	 * @param command
	 * @throws IllegalArgumentException if arguments provided do not meet expectations
	 */
	private PEWSClient(final String... args) {
		if (args.length > 1) {
			try {
				this.serverUrl = new URL(args[0].trim());
				this.command = Command.valueOf(args[1].toUpperCase());

				int credentailsIndex = 2;
				switch (command) {
				case EXPORT:
				case IMPORT:
					if (args.length < 3) {
						throw new IllegalArgumentException("filename must be provided.");
					}
					file = new File(args[2].trim());
					if (command == Command.IMPORT && !file.exists()) {
						throw new IllegalArgumentException(String.format("The provided file doesn't exist: %s%n", args[0]));
					}
					++credentailsIndex;

					if (command == Command.IMPORT) {
						merge = Boolean.valueOf(args[3].trim().toLowerCase());
						++credentailsIndex;
					}
					else {
						merge = false;
					}
					break;
				default: // PING
					file = null;
					merge = false;
				}
				if (args.length > credentailsIndex) {
					username = args[credentailsIndex].trim();
					if (args.length > credentailsIndex + 1) {
						password = args[credentailsIndex + 1].trim();
					}
					else {
						password = DEFAULT_PASSWORD;
					}
				}
				else {
					username = DEFAULT_USER;
					password = DEFAULT_PASSWORD;
				}

				if (username != null && password == null) {
					throw new IllegalArgumentException("Password is required when username is provided");
				}
			}
			catch (MalformedURLException e) {
				throw new IllegalArgumentException(String.format("The provided URL is invalid: %s%n", args[0]));
			}
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String call() throws Exception {
		System.out.format(
				"%n-----------------------------------------------%nMindBox PowerEditor WebService Client v.%s%n-----------------------------------------------%nExecuting %s as %s...%n",
				getVersion(),
				command,
				username);
		final String result;
		switch (command) {
		case DEPLOY:
			result = invokeWSDeploy();
			break;
		case EXPORT:
			result = invokeWSExport();
			break;
		case IMPORT:
			result = invokeWSImport();
			break;
		default:
			result = ping();
		}
		return result;
	}

	private PowerEditorAPIInterface getPowerEditorAPIInterface() {
		final PowerEditorAPIInterface powerEditorAPIInterface = new PowerEditorAPIInterfaceService(
				getClass().getResource("/PowerEditorAPIInterfaceService.wsdl"),
				new QName("http://webservices.server.pe.mindbox.com/", "PowerEditorAPIInterfaceService")).getPowerEditorAPIInterfacePort();
		final BindingProvider bp = BindingProvider.class.cast(powerEditorAPIInterface);
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, String.format("%s/PowerEditorWebService", serverUrl.toExternalForm()));
		return powerEditorAPIInterface;
	}

	/**
	 * Call PowerEditor's Deploy facility.  Files will be on the server. 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private String invokeWSDeploy() throws Exception {
		PowerEditorAPIInterface peAPIInterface = getPowerEditorAPIInterface();
		System.out.println("Interface obtained: " + peAPIInterface);
		BindingProvider bp = (BindingProvider) peAPIInterface;

		PowerEditorInterfaceReturnStructure peirs = null;

		FilterProperty filterProperty = FilterProperty.loadAsFilterProperty(new File("pewsclient.properties"));
		String status = (filterProperty.getStatus() == null ? "Draft" : filterProperty.getStatus());
		List<String> usageTypes = filterProperty.getUsageTypes();
		boolean deployGuidelines = filterProperty.isIncludeGuidelines();
		List<Integer> guidelineTemplateIDs = filterProperty.getGuidelineTemplateIDs();
		boolean deployParameters = filterProperty.isIncludeParameters();
		List<Integer> paramTemplateIDs = filterProperty.getParamTemplateIDs();
		boolean useDaysAgo = filterProperty.isUseDaysAgo();
		int daysAgo = filterProperty.getDaysAgo();
		String activeOnDate = filterProperty.getActiveOnDate();
		boolean includeEmptyContexts = filterProperty.isIncludeEmptyContexts();
		boolean includeParentCategories = filterProperty.isIncludeParentCategories();
		boolean includeChildrenCategories = filterProperty.isIncludeChildrenCategories();
		boolean includeProcessData = filterProperty.isIncludeProcessData();
		boolean includeCBR = filterProperty.isIncludeCBR();
		boolean includeEntities = filterProperty.isIncludeEntities();
		boolean includePolicies = filterProperty.isIncludePolicies();
		String contextElements = filterProperty.getContextElements();

		if (!filterProperty.isPassCredentialsAsArguments()) {
			// Added user/pw setting
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

			final DeployRequest request = new DeployRequest();
			request.getGuidelineTemplateIDs().addAll(guidelineTemplateIDs);
			request.getParamTemplateIDs().addAll(paramTemplateIDs);
			request.getUsageTypes().addAll(usageTypes);
			request.setActiveOnDate(activeOnDate);
			request.setContextElements(contextElements);
			request.setDaysAgo(daysAgo);
			request.setDeployGuidelines(deployGuidelines);
			request.setDeployParameters(deployParameters);
			request.setExportPolicies(includePolicies);
			request.setIncludeCBR(includeCBR);
			request.setIncludeChildrenCategories(includeChildrenCategories);
			request.setIncludeEmptyContexts(includeEmptyContexts);
			request.setIncludeEntities(includeEntities);
			request.setIncludeParentCategories(includeParentCategories);
			request.setIncludeProcessData(includeProcessData);
			request.setStatus(status);
			request.setUseDaysAgo(useDaysAgo);
			final DeployResponse deployResponse = peAPIInterface.deploy(request);
			peirs = deployResponse.getReturn();
		}
		else {
			final DeployWithCredentialsRequest request = new DeployWithCredentialsRequest();
			request.getGuidelineTemplateIDs().addAll(guidelineTemplateIDs);
			request.getParamTemplateIDs().addAll(paramTemplateIDs);
			request.getUsageTypes().addAll(usageTypes);
			request.setActiveOnDate(activeOnDate);
			request.setContextElements(contextElements);
			request.setDaysAgo(daysAgo);
			request.setDeployGuidelines(deployGuidelines);
			request.setDeployParameters(deployParameters);
			request.setExportPolicies(includePolicies);
			request.setIncludeCBR(includeCBR);
			request.setIncludeChildrenCategories(includeChildrenCategories);
			request.setIncludeEmptyContexts(includeEmptyContexts);
			request.setIncludeEntities(includeEntities);
			request.setIncludeParentCategories(includeParentCategories);
			request.setIncludeProcessData(includeProcessData);
			request.setStatus(status);
			request.setUseDaysAgo(useDaysAgo);
			request.setUserID(username);
			request.setPassword(password);
			final DeployWithCredentialsResponse deployResponse = peAPIInterface.deployWithCredentials(request);
			peirs = deployResponse.getReturn();
		}

		if (peirs == null) {
			return "No return values from call";
		}

		displayResults(peirs);

		if (!peirs.getErrorMessages().isEmpty()) {
			return "Errors";
		}
		else {
			return "Success: " + peirs.getContent();
		}
	}

	/**
	 * Call the Power Editor's Export method.  Save the contents to the file specified.
	 * @param filename
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private String invokeWSExport() throws Exception {
		final PowerEditorAPIInterface peAPIInterface = getPowerEditorAPIInterface();
		System.out.println("Interface obtained: " + peAPIInterface);
		BindingProvider bp = (BindingProvider) peAPIInterface;

		PowerEditorInterfaceReturnStructure peirs = null;

		final FilterProperty filterProperty = FilterProperty.loadAsFilterProperty(new File("pewsclient.properties"));
		String status = (filterProperty.getStatus() == null ? "Draft" : filterProperty.getStatus());
		List<String> usageTypes = filterProperty.getUsageTypes();
		List<Integer> guidelineTemplateIDs = filterProperty.getGuidelineTemplateIDs();
		List<Integer> paramTemplateIDs = filterProperty.getParamTemplateIDs();
		boolean useDaysAgo = filterProperty.isUseDaysAgo();
		int daysAgo = filterProperty.getDaysAgo();
		String activeOnDate = filterProperty.getActiveOnDate();
		String contextElements = filterProperty.getContextElements();

		boolean exportEntities = filterProperty.isIncludeEntities();
		boolean exportSecurity = filterProperty.isIncludeSecurity();
		boolean exportGuidelines = filterProperty.isIncludeGuidelines();
		boolean exportParameters = filterProperty.isIncludeParameters();
		boolean exportTemplates = filterProperty.isIncludeTemplates();
		boolean exportGuidelineActions = filterProperty.isIncludeGuidelineActions();
		boolean exportTestConditions = filterProperty.isIncludeTestConditions();
		boolean exportDateSynonyms = filterProperty.isIncludeDateSynonyms();
		boolean includeEmptyContexts = filterProperty.isIncludeEmptyContexts();
		boolean includeParentCategories = filterProperty.isIncludeParentCategories();
		boolean includeChildrenCategories = filterProperty.isIncludeChildrenCategories();

		if (!filterProperty.isPassCredentialsAsArguments()) {
			// Added user/pw setting
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

			final ExportDataRequest request = new ExportDataRequest();
			request.getGuidelineTemplateIDs().addAll(guidelineTemplateIDs);
			request.getParamTemplateIDs().addAll(paramTemplateIDs);
			request.getUsageTypes().addAll(usageTypes);
			request.setActiveOnDate(activeOnDate);
			request.setContextElements(contextElements);
			request.setDaysAgo(daysAgo);
			request.setExportDateSynonyms(exportDateSynonyms);
			request.setExportEntities(exportEntities);
			request.setExportGuidelineActions(exportGuidelineActions);
			request.setExportGuidelines(exportGuidelines);
			request.setExportParameters(exportParameters);
			request.setExportSecurity(exportSecurity);
			request.setExportTemplates(exportTemplates);
			request.setExportTestConditions(exportTestConditions);
			request.setIncludeChildrenCategories(includeChildrenCategories);
			request.setIncludeEmptyContexts(includeEmptyContexts);
			request.setIncludeParentCategories(includeParentCategories);
			request.setStatus(status);
			request.setUseDaysAgo(useDaysAgo);
			peirs = peAPIInterface.exportData(request).getReturn();
		}
		else {
			final ExportDataWithCredentialsRequest request = new ExportDataWithCredentialsRequest();
			request.getGuidelineTemplateIDs().addAll(guidelineTemplateIDs);
			request.getParamTemplateIDs().addAll(paramTemplateIDs);
			request.getUsageTypes().addAll(usageTypes);
			request.setActiveOnDate(activeOnDate);
			request.setContextElements(contextElements);
			request.setDaysAgo(daysAgo);
			request.setExportDateSynonyms(exportDateSynonyms);
			request.setExportEntities(exportEntities);
			request.setExportGuidelineActions(exportGuidelineActions);
			request.setExportGuidelines(exportGuidelines);
			request.setExportParameters(exportParameters);
			request.setExportSecurity(exportSecurity);
			request.setExportTemplates(exportTemplates);
			request.setExportTestConditions(exportTestConditions);
			request.setIncludeChildrenCategories(includeChildrenCategories);
			request.setIncludeEmptyContexts(includeEmptyContexts);
			request.setIncludeParentCategories(includeParentCategories);
			request.setStatus(status);
			request.setUseDaysAgo(useDaysAgo);
			request.setUserID(username);
			request.setPassword(password);
			peirs = peAPIInterface.exportDataWithCredentials(request).getReturn();
		}
		if (peirs == null) {
			return "No return values from call";
		}

		displayResults(peirs);

		String content = peirs.getContent();
		if (content != null) {
			try {
				writeFile(file, content);
			}
			catch (Exception ex) {
				System.out.println("Error writing content to file: " + file.getAbsolutePath());
				throw ex;
			}
		}
		if (!peirs.getErrorMessages().isEmpty()) {
			return "Errors";
		}
		else {
			return "Success: file saved as " + file.getAbsolutePath();
		}
	}

	/**
	 * Import specified XML file to Power Editor.
	 * @param file
	 * @param merge
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private String invokeWSImport() throws Exception {
		PowerEditorAPIInterface peAPIInterface = getPowerEditorAPIInterface();
		System.out.println("Interface obtained: " + peAPIInterface);
		BindingProvider bp = (BindingProvider) peAPIInterface;

		FilterProperty filterProperty = FilterProperty.loadAsFilterProperty(new File("pewsclient.properties"));
		PowerEditorInterfaceReturnStructure peirs = null;
		if (!filterProperty.isPassCredentialsAsArguments()) {
			// Added user/pw setting
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

			final ImportEntitiesRequest request = new ImportEntitiesRequest();
			request.setContent(readFile(file));
			request.setMerge(merge);
			peirs = peAPIInterface.importEntities(request).getReturn();
		}
		else {
			final ImportEntitiesWithCredentialsRequest request = new ImportEntitiesWithCredentialsRequest();
			request.setContent(readFile(file));
			request.setMerge(merge);
			request.setPassword(password);
			request.setUserID(username);
			peirs = peAPIInterface.importEntitiesWithCredentials(request).getReturn();
		}
		if (peirs == null) {
			return "No return values from call";
		}

		displayResults(peirs);

		if (!peirs.getErrorMessages().isEmpty()) {
			return "Errors";
		}
		else {
			return "Success: " + peirs.getContent();
		}
	}

	private String ping() throws Exception {
		final PowerEditorAPIInterface peAPIInterface = getPowerEditorAPIInterface();
		System.out.println("Interface obtained: " + peAPIInterface);
		if (username != null) {
			final BindingProvider bp = (BindingProvider) peAPIInterface;
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}
		return peAPIInterface.ping(new PingRequest()).getStatus();
	}
}