/* Feb 18, 2005. */

package com.mindbox.pe.tools.migration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This utility adds fields to attributes in the passed-in domain xml file.
 * If the deployIDsChosen is true, "DeployID" is added to enums.
 * If the linkChosen is true, "DeployValue" is added to the "DomainClassLink" attribute.
 * It is to be used for migration from previous versions of PE to PE 4.2.0 only.
 * @author Inna Nill
 * @since PowerEditor 4.2.0
 *
 */
class DomainFileRepairFor4_2 {

	private static DomainFileRepairFor4_2 instance = null;

	private final DocumentBuilder docBuilder;

	public static void main(String[] args) {
		if ( args.length < 2 ) {
			System.out.println("Usage: DomainFileRepairFor4_2 <in_domainfilename> <out_domainfilename>[true/false]");
			System.exit(-1);
		}
		String input = args[0];
		String output = args[1];
		System.out.println(">>> Repairing domain xml file: " + input);

		boolean unique = false;
		if ( args.length > 2 ) {
			if (args[1].equalsIgnoreCase("true"))
				unique = true;
		}

		try {
		    DomainFileRepairFor4_2.getInstance().process(true, true, input, output, unique);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("<<< Finished repairing domain xml file.");
		}

	}

	public void process(boolean deployIDsChosen, boolean linkChosen, String input, String output, boolean unique) throws SAXException, IOException {
		Document doc = loadXML(input);
		Element element = doc.getDocumentElement();
		if (deployIDsChosen)
		    element = addEnumIDs(element, unique);
		if (linkChosen)
		    element = addLinkDeployValues(element);
		writeNewFile(element, output);
	}

	public Document loadXML(String filename) throws SAXException, IOException {
		Document document = docBuilder.parse(new File(filename));
		return document;
	}

	public Element addEnumIDs(Element element, boolean unique) {
		int counter = 1;
		NodeList list = element.getElementsByTagName("DomainAttribute");
		for ( int idx = 0; idx < list.getLength(); idx++ ) {
			// Reset if values don't have to be completely unique.
			if ( unique == false ) {
				counter = 1;
			}
			Element attr = (Element) list.item(idx);
			NodeList enums = attr.getElementsByTagName("EnumValue");
			for ( int jdx = 0; jdx < enums.getLength(); jdx++ ) {
				Element enumeration = (Element) enums.item(jdx);
				if ( !enumeration.hasAttribute("DeployID")) {
					enumeration.setAttribute("DeployID", new Integer(counter++).toString());
				}
			}
		}
		return element;
	}

	public Element addLinkDeployValues(Element element) {
		NodeList list = element.getElementsByTagName("DomainClassLink");
		for ( int idx = 0; idx < list.getLength(); idx++ ) {
			Element link = (Element)list.item(idx);
			if ( !link.hasAttribute("DeployValue")) {
				String parentName = link.getAttribute("ParentClassName");
				String childName = link.getAttribute("ChildClassName");
				link.setAttribute("DeployValue", parentName+"-link-"+childName);
			}
		}
		return element;
	}

	public void writeNewFile(Element newDocElement, String outputFile) throws IOException {
		File newFile = new File(outputFile);
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
		writer.println(newDocElement);
		writer.flush();
		writer.close();
	}

	public static DomainFileRepairFor4_2 getInstance() throws ParserConfigurationException {
		if (instance == null) {
			instance = new DomainFileRepairFor4_2();
		}
		return instance;
	}

	private DomainFileRepairFor4_2() throws ParserConfigurationException {
		docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}
}