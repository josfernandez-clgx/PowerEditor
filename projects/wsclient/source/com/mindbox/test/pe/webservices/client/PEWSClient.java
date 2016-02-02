package com.mindbox.test.pe.webservices.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import com.mindbox.test.pe.webservices.wsimportgen.PowerEditorAPIInterface;
import com.mindbox.test.pe.webservices.wsimportgen.PowerEditorAPIInterfaceService;
import com.mindbox.test.pe.webservices.wsimportgen.PowerEditorInterfaceReturnStructure;

/**
 * 
 * @author schneider (from original version by nill)
 * Implements a test client that can call import, export, deploy, and ping
 * functions using PowerEditor's web services interface.
 *
 */
public class PEWSClient {
	
	/**
	 * Allows all kinds of web services calls.  See usage messages for details.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		boolean printUsage = (args.length < 1);
		
		if (!printUsage) {
			PEWSClient client = new PEWSClient();
			String result = null;
			if ("ping".equalsIgnoreCase(args[0])) {
				result = client.ping();
				System.out.println("[RESULT] " + result);
			} else {
				if ("deploy".equalsIgnoreCase(args[0])) {
					String username = (args.length > 2 ? args[1] : "demo");
					String password = (args.length > 2 ? args[2] : "demo");
					result = client.invokeWSDeploy(username, password);
					System.out.println("[RESULT] " + result);
				} else {
					printUsage = (args.length < 2); // filename needed for the rest
					if (!printUsage) {
						if ("export".equalsIgnoreCase(args[0])) {
							String username = (args.length > 3 ? args[2] : "demo");
							String password = (args.length > 3 ? args[3] : "demo");
							result = client.invokeWSExport(args[1], username, password);
							System.out.println("[RESULT] " + result);
						} else {
							if ("import".equalsIgnoreCase(args[0])) {
								boolean merge = (args.length > 1 ? Boolean.valueOf(args[2]) : false);
								String username = (args.length > 4 ? args[3] : "demo");
								String password = (args.length > 4 ? args[4] : "demo");
								result = client.invokeWSImport(new File(args[1]), merge, username, password);
								System.out.println("[RESULT] " + result);
							}
							else {
								printUsage = true;
							}
						}
					}
				}
			}
		}
		if (printUsage) {
			System.out.println("Usage: ");
			System.out.println("java " + PEWSClient.class.getName() + " <command> + arguments");
			System.out.println("");
			System.out.println("Arguments vary by command:");
			System.out.println("--------------------------");
			System.out.println("java " + PEWSClient.class.getName() + " import <filename> [merge] [username] [password]");
			System.out.println("   filename: the name of the file to import");
			System.out.println("   merge:    must be true or false; defaults to false, if missing");
			System.out.println("   username:    PowerEditor user name (uses 'demo' if missing or if password is missing)");
			System.out.println("   password:    PowerEditor password (uses 'demo' if missing)");
			System.out.println("");
			System.out.println("java " + PEWSClient.class.getName() + " export <filename> [username] [password]");
			System.out.println("   filename: the name of the file to fill with export results");
			System.out.println("   username:    PowerEditor user name (uses 'demo' if missing or if password is missing)");
			System.out.println("   password:    PowerEditor password (uses 'demo' if missing)");
			System.out.println("");
			System.out.println("java " + PEWSClient.class.getName() + " deploy [username] [password]");
			System.out.println("   username:    PowerEditor user name (uses 'demo' if missing or if password is missing)");
			System.out.println("   password:    PowerEditor password (uses 'demo' if missing)");
			System.out.println("");
			System.out.println("java " + PEWSClient.class.getName() + " ping");
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
	@SuppressWarnings("unchecked")
	public String invokeWSImport(File file, boolean merge, String username, String password) throws Exception {
		PowerEditorAPIInterface peAPIInterface = new PowerEditorAPIInterfaceService().getPowerEditorAPIInterfacePort();
		System.out.println("Interface obtained: " + peAPIInterface);
		BindingProvider bp = (BindingProvider)peAPIInterface;

		boolean handlerSetup = true;
		try { 
	        List<Handler> chain = new ArrayList<Handler>();
	        chain.add(new PEWSClientSecurityHandler());
			bp.getBinding().setHandlerChain(chain);
		} catch (Exception ex) {
			handlerSetup = false;
			System.out.println("Could not setup client handler chain.  Trying credentials in the call");
		}
		PowerEditorInterfaceReturnStructure peirs = null;
		if (handlerSetup) {
			// Added user/pw setting
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
	
			peirs = peAPIInterface.importEntities(readFile(file), merge);
		} else {
			peirs = peAPIInterface.importEntitiesWithCredentials(readFile(file),
					merge, username, password);
		}
		if (peirs == null) {return "No return values from call"; }

		displayMessages(peirs.getGeneralMessages(), "Messages");

		if (peirs.isErrorFlag()) {
			displayMessages(peirs.getErrorMessages(), "Error Messages");
			
			return "Errors";
		} else {
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
	public String invokeWSExport(String filename, String username, String password) throws Exception {
		PowerEditorAPIInterface peAPIInterface = new PowerEditorAPIInterfaceService().getPowerEditorAPIInterfacePort();
		System.out.println("Interface obtained: " + peAPIInterface);
		BindingProvider bp = (BindingProvider)peAPIInterface;

		boolean handlerSetup = setupHandlers(bp);

		PowerEditorInterfaceReturnStructure peirs = null;

		FilterProperty filterProperty = FilterProperty.loadAsFilterProperty(new File("pewsclient.properties"));
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
		
		if (handlerSetup) {
			// Added user/pw setting
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
	
			peirs = peAPIInterface.exportData(
					exportEntities,
					exportSecurity,
					exportGuidelines,
					exportParameters,
					exportTemplates,
					exportGuidelineActions,
					exportTestConditions,
					exportDateSynonyms,
					includeEmptyContexts,
					includeParentCategories,
					includeChildrenCategories,
					status,
					usageTypes,
					guidelineTemplateIDs,
					paramTemplateIDs,
					useDaysAgo,
					daysAgo,
					activeOnDate,
					contextElements);
		} else {
			peirs = peAPIInterface.exportDataWithCredentials(
					exportEntities,
					exportSecurity,
					exportGuidelines,
					exportParameters,
					exportTemplates,
					exportGuidelineActions,
					exportTestConditions,
					exportDateSynonyms,
					includeEmptyContexts,
					includeParentCategories,
					includeChildrenCategories,
					status,
					usageTypes,
					guidelineTemplateIDs,
					paramTemplateIDs,
					useDaysAgo,
					daysAgo,
					activeOnDate,
					contextElements,
					username,
					password);
		}
		if (peirs == null) {return "No return values from call"; }

		displayMessages(peirs.getGeneralMessages(), "Messages");

		String content = peirs.getContent();
		if (content != null) {
			try {
				writeFile(filename, content);
			} catch (Exception ex) {
				System.out.println("Error writing content to file: " + filename);
			}
		}
		if (peirs.isErrorFlag()) {
			displayMessages(peirs.getErrorMessages(), "Error Messages");
			
			return "Errors";
		} else {
			return "Success: file saved as " + filename;
		}
	}

	/**
	 * Call PowerEditor's Deploy facility.  Files will be on the server. 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String invokeWSDeploy(String username, String password) throws Exception {
		PowerEditorAPIInterface peAPIInterface = new PowerEditorAPIInterfaceService().getPowerEditorAPIInterfacePort();
		System.out.println("Interface obtained: " + peAPIInterface);
		BindingProvider bp = (BindingProvider)peAPIInterface;

		boolean handlerSetup = setupHandlers(bp);

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
		String contextElements = filterProperty.getContextElements();

		if (handlerSetup) {
			// Added user/pw setting
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
	
			peirs = peAPIInterface.deploy(
					status,
					usageTypes,
					deployGuidelines,
					guidelineTemplateIDs,
					deployParameters,
					paramTemplateIDs,
					useDaysAgo,
					daysAgo,
					activeOnDate,
					includeEmptyContexts,
					includeParentCategories,
					includeChildrenCategories,
					includeProcessData,
					includeCBR,
					includeEntities,
					contextElements);
		} else {
			peirs = peAPIInterface.deployWithCredentials(
					status,
					usageTypes,
					deployGuidelines,
					guidelineTemplateIDs,
					deployParameters,
					paramTemplateIDs,
					useDaysAgo,
					daysAgo,
					activeOnDate,
					includeEmptyContexts,
					includeParentCategories,
					includeChildrenCategories,
					includeProcessData,
					includeCBR,
					includeEntities,
					contextElements,
					username,
					password);
		}
		if (peirs == null) {return "No return values from call"; }

		displayMessages(peirs.getGeneralMessages(), "Messages");

		if (peirs.isErrorFlag()) {
			displayMessages(peirs.getErrorMessages(), "Error Messages");
			
			return "Errors";
		} else {
			return "Success: " + peirs.getContent();
		}
	}
	
	/**
	 * Read the contents of a file and return them as a String.
	 * @param file
	 * @return String with file contents
	 * @throws IOException
	 */
	private String readFile(File file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			out.println(line);
		}
		out.flush();
		out.close();
		in.close();
		return stringWriter.getBuffer().toString();
	}
	
	/**
	 * Save the content to a file with the path specified
	 * @param filename  -  full pathname to a file
	 * @param content  - a String of text
	 * @throws IOException
	 */
	private void writeFile(String filename, String content) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		PrintWriter pout = new PrintWriter(bw);
		pout.print(content);
		pout.flush();
		pout.close();
		bw.close();
	}

	public String ping() throws Exception {
		PowerEditorAPIInterface peAPIInterface = new PowerEditorAPIInterfaceService().getPowerEditorAPIInterfacePort();
		System.out.println("Interface obtained: " + peAPIInterface);

		String pingResponse = null;
		PowerEditorInterfaceReturnStructure peirs = peAPIInterface.ping();

		displayMessages(peirs.getGeneralMessages(), "Messages");
		if (peirs.isErrorFlag()) {
			displayMessages(peirs.getErrorMessages(), "Error Messages");
			pingResponse = "Ping Error";
		} else {
			pingResponse = peirs.getContent();
		}
		return pingResponse;
	}
	
	/**
	 * Display a list of messages with a given heading.
	 * @param messageList
	 * @param heading
	 */
	public void displayMessages(List<String> messageList, String heading) {
		System.out.println();
		System.out.println("--------------");
		System.out.println(heading);
		System.out.println("--------------");
		if (messageList == null || messageList.isEmpty()) { return; }
		Iterator<String> it = messageList.iterator();
		int loop = 1;
		while (it.hasNext()) {
			String msg = it.next();
			System.out.println(loop + ": " + msg);
			System.out.println();
			loop++;
		}
	}
	
	/**
	 * Attempt to setup security handlers (an optional facility)
	 * @param bp
	 * @return true/false - whether setup was successful
	 */
	@SuppressWarnings("unchecked")
	private boolean setupHandlers(BindingProvider bp) {
		boolean handlerSetup = true;
		try { 
	        List<Handler> chain = new ArrayList<Handler>();
	        chain.add(new PEWSClientSecurityHandler());
			bp.getBinding().setHandlerChain(chain);
		} catch (Exception ex) {
			handlerSetup = false;
			System.out.println("Could not setup client handler chain.  Trying credentials in the call");
		}
		return handlerSetup;
	}

}