package com.mindbox.pe.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML parser wrapper for Java1.4 XML Parser implementation.
 * Client needs an XML Parser for the override message feature.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public final class PowerEditorXMLParser {

	private static PowerEditorXMLParser instance = null;

	/**
	 * Gest the one and only instance of this.
	 * @return the one and only instance of this
	 * @throws ParserConfigurationException if XML parser has not been configured properly
	 */
	public static PowerEditorXMLParser getInstance() throws ParserConfigurationException {
		if (instance == null) {
			instance = new PowerEditorXMLParser();
		}
		return instance;
	}

	/**
	 * Gets the first one with the specified tag name of the specified element's children.
	 * @param element the element the first child with <code>tagName</code> of which to return
	 * @param tagName the tag name of the child to return
	 * @return the first child with <code>tagName</code>, if found; null, otherwise
	 */
	public static Element getFirstChild(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		if (list.getLength() > 0) {
			return (Element) list.item(0);
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the string value of the first child of the specified element with the specified tag name.
	 * Note that this returning <code>null</code> of an empty string
	 * does not necessarily mean the parent element has no such child.
	 * <p>
	 * Equivalent to <code>getValueOfFirstChild(element, tagName, "")</code>
	 * @param element the parent element
	 * @param tagName the tag name
	 * @return the string value of the first child element with <code>tagName</code>, if found; <code>""</code>, otherwise
	 */
	public static String getValueOfFirstChild(Element element, String tagName) {
		return getValueOfFirstChild(element, tagName, "");
	}

	/**
	 * Gets the string value of the first child of the specified element with the specified tag name.
	 * @param element the parent element
	 * @param tagName the tag name
	 * @param default value to return if there is no such element is found
	 * @return the string value of the first child element with <code>tagName</code>, if found; <code>defaultValue</code>, otherwise
	 */
	public static String getValueOfFirstChild(Element element, String tagName, String defaultValue) {
		Element child = getFirstChild(element, tagName);
		return (child == null ? defaultValue : getValue(child));
	}

	/**
	 * Gets the value of the specified element.
	 * @param element the element
	 * @return the value (trimmed if it's not null)
	 */
	public static String getValue(Element element) {
		String value = element.getNodeValue();
		if (value == null && element.hasChildNodes()) {
			if (element.getFirstChild() instanceof CharacterData) {
				value = ((CharacterData) element.getFirstChild()).getData();
			}
		}
		return (value == null ? null : value.trim());
	}

	private final Logger logger = Logger.getLogger(PowerEditorXMLParser.class);
	private final DocumentBuilder docBuilder;

	private PowerEditorXMLParser() throws ParserConfigurationException {
		docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		logger.debug("<init> docBuilder = " + docBuilder);
	}

	/**
	 * Parses the specified XML file and retrusn DOM document object for it.
	 * @param filename the XML file to parse
	 * @return the DOM document object representing <code>filename</code>
	 * @throws SAXException on an SAX Parser related error
	 * @throws IOException on File IO error
	 */
	public Document loadXML(String filename) throws SAXException, IOException {
		Document document = docBuilder.parse(new File(filename));
		return document;
	}

	/**
	 * Parses the XML content from the specified reader and retrusn DOM document object for it.
	 * @param in the reader from which to read XML content
	 * @return the DOM document object representing <code>filename</code>
	 * @throws SAXException on an SAX Parser related error
	 * @throws IOException on File IO error
	 */
	public Document loadXML(Reader in) throws SAXException, IOException {
		Document document = docBuilder.parse(new InputSource(in));
		return document;
	}

	/**
	 * Parses the XML content from the specified reader and retrusn DOM document object for it.
	 * @param in the input stream from which to read XML content
	 * @return the DOM document object representing <code>filename</code>
	 * @throws SAXException on an SAX Parser related error
	 * @throws IOException on File IO error
	 */
	public Document loadXML(InputStream in) throws SAXException, IOException {
		Document document = docBuilder.parse(new InputSource(in));
		return document;
	}
}
