package com.mindbox.pe.server.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.imexport.ImportException;
import com.mindbox.pe.server.imexport.ImportService;
import com.mindbox.pe.server.model.User;

/**
 * A simple servlet to load the PE DB from an exported xml file 
 * intended for use by PETE (the PowerEditor Test Environment).
 * 
 * <dl>3 optional parameters
 * 		<dt>user</dt>
 * 		<dd>The authenticated user authorized to do the DB import</dd>
 * 
 * 		<dt>file</dt>
 * 		<dd>The XML file containing the DB contents to import</dd>
 * 
 * 		<dt>size</dt>
 * 		<dd>The number of characters in the XML file (an optimization to initialize the buffer, precise value is not necessary)</dd>
 * </dl>
 */
public class ImportDb extends HttpServlet {
	public static final String DEFAULT_USER = "demo";
	public static final String DEFAULT_FILE = "c:/MindBox/MBXProjects/PETE/REF/PowerEditor/database/PETE-REF-PEDB.xml";
	public static final int DEFAULT_SIZE = 850000; // roughly the size, in number of chars, of the AOB 2.3 DB exported to xml
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		File xmlFile = getFile(req);
		String xml = textFile2String(xmlFile, getSize(req));

		ImportSpec params = new ImportSpec(xmlFile.getName(), xml, ImportSpec.IMPORT_DATA_REQUEST, false);
		ImportService importService = new ImportService();
		try {
			importService.importDataXML(params, getUser(req));
		}
		catch (ImportException e) {
			throw new ServletException(e);
		}
		ImportResult result = importService.getImportResult();

		res.getWriter().println(result);
		printConditionally(res.getWriter(), "Messages", result.getMessages());
		printConditionally(res.getWriter(), "Errors", result.getErrorMessages());
	}

	private User getUser(HttpServletRequest req) {
        return SecurityCacheManager.getInstance().getUser(getParameter(req, "user", DEFAULT_USER));
	}

	private File getFile(HttpServletRequest req) throws IOException {
		String xmlFileName = getParameter(req, "file", DEFAULT_FILE);
		File xmlFile = new File(xmlFileName);
		return xmlFile.getCanonicalFile();
	}
	
	private int getSize(HttpServletRequest req) {
		return getParameter(req, "size", null) == null ? DEFAULT_SIZE : Integer.parseInt(getParameter(req, "size", null));
	}

	private String getParameter(HttpServletRequest req, String key, String def) {
		return req.getParameter(key) == null ? def : req.getParameter(key);
	}
	
	private String textFile2String(File textFile, int initBufferSize) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(textFile));
		StringBuffer sb = new StringBuffer(initBufferSize);
		for (String line = fileReader.readLine(); line != null; line = fileReader.readLine()) {
			sb.append(line);
		}
		fileReader.close();
		return sb.toString();
	}

	private void printConditionally(PrintWriter writer, String description, List<?> list) {
		if (list != null && list.size() > 0) {
			writer.println(description + ' ' + list);
		}
	}
}
