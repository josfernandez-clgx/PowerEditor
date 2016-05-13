package com.mindbox.pe.common;

import static com.mindbox.pe.common.IOUtil.close;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtil {

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

	public static Element getFirstChildWithName(Node node, String elementName) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeName().equals(elementName)) {
				return Element.class.cast(childNode);
			}
		}
		return null;
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

	public static boolean hasAttributeValue(Node node, String attributeName, String expectedValue) {
		if (node == null) {
			throw new IllegalArgumentException("node cannot be null");
		}

		NamedNodeMap namedNodeMap = node.getAttributes();
		Node attributeNode = namedNodeMap.getNamedItem(attributeName);
		if (attributeNode != null) {
			return expectedValue.equals(node.getTextContent());
		}
		else {
			return false;
		}
	}

	/**
	 * Generates XML document from the specified JAXB element object as string. This internally calls
	 * {@link #marshall(Object, Writer, boolean)}.
	 * 
	 * @param rootElement
	 * @param writeXmlDeclaration
	 * @param classesToRecognize classes for the marshaller to work with (optional)
	 * @return XML string
	 * @throws JAXBException
	 */
	public static String marshal(Object rootElement, boolean writeXmlDeclaration, Class<?>... classesToRecognize) throws JAXBException {
		StringWriter stringWriter = new StringWriter();
		marshal(rootElement, stringWriter, false, writeXmlDeclaration, classesToRecognize);
		return stringWriter.toString();
	}

	/**
	 * Writes the specified root element object as XML document to the specified writer.
	 * 
	 * @param rootElement JAXB element object to write
	 * @param writer writer
	 * @param writeXmlDeclaration
	 * @param classesToRecognize classes for the marshaller to work with (optional)
	 * @throws JAXBException on error
	 */
	public static void marshal(Object rootElement, Writer writer, boolean closeWriter, boolean writeXmlDeclaration, Class<?>... classesToRecognize) throws JAXBException {
		try {
			Class<?>[] classesToPassOn = Arrays.copyOf(classesToRecognize, classesToRecognize.length + 1);
			classesToPassOn[classesToPassOn.length - 1] = rootElement.getClass();

			JAXBContext jaxbContext = JAXBContext.newInstance(classesToPassOn);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.valueOf(!writeXmlDeclaration));
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(rootElement, writer);
		}
		finally {
			if (closeWriter) {
				close(writer);
			}
		}
	}

	public static void marshalNonRoot(JAXBElement<?> nonRootElement, Writer writer, boolean closeWriter, boolean writeXmlDeclaration, Class<?>... classesToRecognize) throws JAXBException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(classesToRecognize);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.valueOf(!writeXmlDeclaration));
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(nonRootElement, writer);
		}
		finally {
			if (closeWriter) {
				close(writer);
			}
		}
	}

	public static Document parseAsDomDocument(File file) throws ParserConfigurationException, SAXException, IOException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null");
		}

		return parseAsDomDocument(new FileInputStream(file));
	}

	private static Document parseAsDomDocument(final InputSource inputSource) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document domDocument = documentBuilder.parse(inputSource);
		return domDocument;
	}

	public static Document parseAsDomDocument(InputStream in) throws ParserConfigurationException, SAXException, IOException {
		return parseAsDomDocument(in, true);
	}

	public static Document parseAsDomDocument(InputStream in, boolean closeIn) throws ParserConfigurationException, SAXException, IOException {
		if (in == null) {
			throw new IllegalArgumentException("in cannot be null");
		}

		try {
			return parseAsDomDocument(new InputSource(in));
		}
		finally {
			if (closeIn) {
				close(in);
			}
		}
	}

	public static Document parseAsDomDocument(final Reader reader, boolean closeReader) throws ParserConfigurationException, SAXException, IOException {
		if (reader == null) {
			throw new IllegalArgumentException("reader cannot be null");
		}

		try {
			return parseAsDomDocument(new InputSource(reader));
		}
		finally {
			if (closeReader) {
				close(reader);
			}
		}
	}

	public static <T> T unmarshal(Reader reader, Class<T> expectedClass) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(expectedClass);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		Object obj = unmarshaller.unmarshal(reader);
		return expectedClass.cast(obj);
	}

	public static <T> T unmarshal(String xmlContent, Class<T> expectedClass) throws JAXBException {
		StringReader stringReader = new StringReader(xmlContent);
		return unmarshal(stringReader, expectedClass);
	}

	@SuppressWarnings("unchecked")
	public static <T> JAXBElement<T> unmarshalNonRoot(Reader reader, Class<T> expectedClass, Class<?>... classesToRecognize) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(classesToRecognize);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		Object obj = unmarshaller.unmarshal(reader);
		JAXBElement<T> jaxbElement = (JAXBElement<T>) obj;

		return jaxbElement;
	}

	public static void writeDomDocument(Document document, File targetFile) throws TransformerFactoryConfigurationError, TransformerException, IOException {
		writeDomDocument(document, new FileWriter(targetFile));
	}

	public static void writeDomDocument(Document document, Writer writer) throws TransformerFactoryConfigurationError, TransformerException, IOException {
		writeDomDocument(document, writer, true);
	}

	public static void writeDomDocument(Document document, Writer writer, boolean closeWriter) throws TransformerFactoryConfigurationError, TransformerException, IOException {
		DOMSource domSource = new DOMSource(document);
		Result result = new StreamResult(writer);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(domSource, result);

		writer.flush();

		if (closeWriter) {
			close(writer);
		}
	}

	private XmlUtil() {
	}
}