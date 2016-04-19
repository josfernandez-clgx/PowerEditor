package com.mindbox.pe.server.config;

import static com.mindbox.pe.common.XmlUtil.parseAsDomDocument;
import static com.mindbox.pe.common.XmlUtil.writeDomDocument;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.AbstractTestWithTestConfig;

/**
 * For all add, remove and replace methods, <code>tagNamePath</code> is the '/' seperated path <em>below</em> the root
 * &lt;PowerEditorConfiguration&lt; element to elements to be operated on.
 * 
 * Configuration defaults to "test/config/PowerEditorConfiguration.xml" unless {@link #setPeConfigXmlFile(String)} has
 * been called before any add, remove, or replace methods.
 * 
 */
public class AbstractConfigXmlTest extends AbstractTestWithTestConfig {

	private static class DomUtil { // DomUtil class could be made public in its own file someday if needed.
		public static List<Node> getAllChildElements(List<? extends Node> parents, String tagNamePath) {
			if (parents.isEmpty()) {
				return new ArrayList<Node>();
			}

			int nextSeperator = tagNamePath.indexOf('/');
			if (nextSeperator == -1) {
				List<Node> allChildrenFromAllParents = new ArrayList<Node>();
				for (Iterator<? extends Node> parentIter = parents.iterator(); parentIter.hasNext();) {
					Element parent = (Element) parentIter.next();
					allChildrenFromAllParents.addAll(nodeListToList(parent.getElementsByTagName(tagNamePath)));
				}
				return allChildrenFromAllParents;
			}

			// we have parents and at least one more level deep to go.  Recursive call...
			String currTagName = tagNamePath.substring(0, nextSeperator);
			String remainingTagNamePath = tagNamePath.substring(nextSeperator + 1);
			return getAllChildElements(getAllChildElements(parents, currTagName), remainingTagNamePath);
		}

		public static List<Node> nodeListToList(NodeList nl) {
			List<Node> l = new ArrayList<Node>(nl.getLength());
			for (int i = 0; i < nl.getLength(); i++) {
				l.add(nl.item(i));
			}
			return l;
		}

		public static void remove(Element e) {
			((Element) e.getParentNode()).removeChild(e);
		}

		public static void removeAll(Element root, String tagNamePath) {
			List<? extends Node> toBeRemoved = getAllChildElements(Collections.singletonList(root), tagNamePath);
			for (Iterator<? extends Node> iter = toBeRemoved.iterator(); iter.hasNext();) {
				remove((Element) iter.next());
			}
		}

		public static void removeAll(Element root, String tagNamePath, String attributeName, String attributeValue) {
			if (UtilBase.isEmpty(attributeName) || UtilBase.isEmpty(attributeValue)) {
				return;
			}
			List<Node> candidates = getAllChildElements(Collections.singletonList(root), tagNamePath);
			for (Iterator<Node> iter = candidates.iterator(); iter.hasNext();) {
				Element candidate = (Element) iter.next();
				if (attributeValue.equals(candidate.getAttribute(attributeName))) {
					remove(candidate);
				}
			}
		}

		public static void replaceAttributeValue(Element root, String tagNamePath, String attributeName, String newAttributeValue) {
			if (UtilBase.isEmpty(attributeName)) {
				return;
			}
			List<Node> candidates = getAllChildElements(Collections.singletonList(root), tagNamePath);
			for (Iterator<Node> iter = candidates.iterator(); iter.hasNext();) {
				Element candidate = (Element) iter.next();
				candidate.getAttributeNode(attributeName).setNodeValue(newAttributeValue);
			}
		}

		public static void replaceText(Element e, String newText) {
			NodeList children = e.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					child.setNodeValue(newText);
				}
			}
		}

		public static void replaceText(Element root, String tagNamePath, String newText) {
			List<Node> toBeReplaced = getAllChildElements(Collections.singletonList(root), tagNamePath);
			for (Iterator<Node> iter = toBeReplaced.iterator(); iter.hasNext();) {
				replaceText((Element) iter.next(), newText);
			}
		}
	} // end DomUtil

	private Document domDocument;
	private Element rootElement;

	protected final void assertElementDefaultValue(String tagNamePath, Class<?> configClass, String beanAttributeName, boolean defaultValue) throws Exception {
		removeAll(tagNamePath);
		assertValidElementValue(defaultValue, configClass, beanAttributeName);
	}

	protected final void assertElementDefaultValue(String tagNamePath, Class<?> configClass, String beanAttributeName, String defaultValue) throws Exception {
		removeAll(tagNamePath);
		assertValidElementValue(defaultValue, configClass, beanAttributeName);
	}

	protected final void assertElementNotRequired(String tagNamePath, Class<?> configClass, String beanAttributeName) throws Exception {
		removeAll(tagNamePath);
		assertNull(BeanUtils.getSimpleProperty(instantiateConfig(configClass), beanAttributeName));
	}

	protected final void assertElementRequired(String tagNamePath, Class<?> configClass) throws Exception {
		removeAll(tagNamePath);
		try {
			instantiateConfig(configClass);
			fail("Expected " + IllegalArgumentException.class.getName());
		}
		catch (IllegalArgumentException e) { // PE config convention to throw IAE when missing required element
			// pass
		}
	}

	protected final void assertInvalidElementValue(String tagNamePath, Class<?> configClass, String invalidValue) throws Exception {
		replaceText(tagNamePath, invalidValue);
		try {
			instantiateConfig(configClass);
			fail("Expected " + IllegalArgumentException.class.getName());
		}
		catch (IllegalArgumentException e) {
			// pass
		}
	}

	protected final void assertValidElementValue(boolean expectedVal, Class<?> configClass, String beanAttributeName) throws Exception {
		assertEquals(expectedVal, Boolean.valueOf(BeanUtils.getSimpleProperty(instantiateConfig(configClass), beanAttributeName)).booleanValue());
	}

	protected final void assertValidElementValue(Object expectedVal, Class<?> configClass, String beanAttributeName) throws Exception {
		assertEquals(expectedVal.toString(), BeanUtils.getSimpleProperty(instantiateConfig(configClass), beanAttributeName));
	}

	/**
	 * Call this method <em>after</em> all calls to add, remove, replace to get an InputSteamReader representing the
	 * configuration xml.
	 */
	protected InputStreamReader getPeConfigXml() throws Exception {
		initDomIfNecessary();

		StringWriter stringWriter = new StringWriter();
		writeDomDocument(domDocument, stringWriter, true);

		return new InputStreamReader(new ByteArrayInputStream(stringWriter.toString().getBytes()));
	}

	private void initDomIfNecessary() throws Exception {
		if (!isDomInitialized()) {
			setDefaultConfigFile();
		}
	}

	// assumes a constructor with a single parameter of type InputStreamReader
	private Object instantiateConfig(Class<?> configClass) throws Exception {
		Constructor<?> cstr = configClass.getConstructor(new Class[] { Reader.class });
		Reader reader = getPeConfigXml();
		try {
			return cstr.newInstance(new Object[] { reader });
		}
		catch (InvocationTargetException e) {
			throw (Exception) e.getCause();
		}
	}

	private boolean isDomInitialized() {
		return domDocument != null;
	}

	protected final void removeAll(String tagNamePath) throws Exception {
		initDomIfNecessary();
		DomUtil.removeAll(rootElement, tagNamePath);
	}

	protected final void removeAll(String tagNamePath, String attributeName, String attributeValue) throws Exception {
		initDomIfNecessary();
		DomUtil.removeAll(rootElement, tagNamePath, attributeName, attributeValue);
	}

	protected final void replaceAttributeValue(String tagNamePath, String attributeName, String newValue) throws Exception {
		initDomIfNecessary();
		DomUtil.replaceAttributeValue(rootElement, tagNamePath, attributeName, newValue);
	}

	protected final void replaceText(String tagNamePath, String newText) throws Exception {
		initDomIfNecessary();
		DomUtil.replaceText(rootElement, tagNamePath, newText);
	}

	private void setDefaultConfigFile() throws Exception {
		setPeConfigXmlFile("../config/PowerEditorConfiguration.xml");
		rootElement = domDocument.getDocumentElement();
	}

	/**
	 * Call this method <em>before</em> any others if you need to use config file other than
	 * "test/config/PowerEditorConfiguration.xml".
	 */
	protected final void setPeConfigXmlFile(String path) throws Exception {
		if (isDomInitialized()) {
			throw new IllegalStateException("Config file already set.");
		}
		domDocument = parseAsDomDocument(config.getDataFile(path));
	}
}
