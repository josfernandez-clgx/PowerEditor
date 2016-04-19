package com.mindbox.pe.client.applet.validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.DomainClassLink;
import com.mindbox.pe.model.domain.DomainTranslation;
import com.mindbox.pe.model.domain.DomainView;

/**
 * Domain Model class maintains all domain classes. It is singleton.
 * It has list of domain classes.
 */
public final class DomainModel implements DomainClassProvider {

	/**
	 * Format XML string for &,>,<,',"
	 * @param str string to be formatted
	 * @return formatted string
	 */
	public static String formatXMLString(String str) {
		if (str == null) return new String("");
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll("\"", "&quot;");
		str = str.replaceAll("'", "&apos;");
		return str;
	}

	private static DomainModel instance = null;

	/**
	 * Initialize the singleton instance.
	 * @throws ServerException on error while getting domain model from the server
	 * @throws IOException on I/O error
	 * @throws ClassNotFoundException if a required Java class is not found in the classpath
	 */
	public static void initInstance() throws ServerException, IOException {
		initInstance(DefaultDomainRetrieverProxy.getInstance());
	}

	public final static void initInstance(DomainRetrieverProxy domainRetrieverProxy) throws ServerException, IOException {
		if (instance == null) {
			try {
				instance = new DomainModel(domainRetrieverProxy);
			}
			catch (IOException ex) {
				ClientUtil.getLogger().error("Failed to get domain XML", ex);
				throw ex;
			}
		}
	}

	public static DomainModel getInstance() {
		if (instance == null) throw new IllegalStateException("Initialize this first with MainFrame");
		return instance;
	}


	private List<DomainClass> domainClassList;
	private final Map<String, List<DomainClassLink>> domainLinkMap;

	/**
	 * Constructor.
	 * @throws ServerException on error while getting domain model from the server
	 * @throws IOException on I/O error
	 * @throws ClassNotFoundException if a required Java class is not found in the classpath
	 */
	private DomainModel(DomainRetrieverProxy domainRetrieverProxy) throws ServerException, IOException {
		this.domainClassList = Collections.synchronizedList(new ArrayList<DomainClass>());
		this.domainLinkMap = new HashMap<String, List<DomainClassLink>>();


		DomainClass[] domainClasses = domainRetrieverProxy.fetchAllDomainClasses();

		// add digested objects
		for (int i = 0; i < domainClasses.length; i++) {
			DomainClass domainClass = domainClasses[i];
			if (domainClass.allowRuleUsage()) {
				if (domainClass.allowRuleUsage()) {
					domainClass.addDomainView(DomainView.TEMPLATE_EDITOR);
				}
				domainClass.addDomainView(DomainView.ENGINE_OBJECTS);
			}
			addDomainClass(domainClass);

			// process class links
			for (Iterator<DomainClassLink> iterator = domainClass.getDomainClassLinks().iterator(); iterator.hasNext();) {
				DomainClassLink link = iterator.next();
				// put the link in the cache
				processClassLinkage(link);
			}

			// attribute processing
			for (Iterator<DomainAttribute> iterator = domainClass.getDomainAttributes().iterator(); iterator.hasNext();) {
				DomainAttribute attribute = iterator.next();

				// populate view and contextless label, if necessary
				if (attribute.getContextlessLabel() == null || attribute.getContextlessLabel().length() == 0) {
					attribute.setContextlessLabel(attribute.getDisplayLabel());
				}
				if (attribute.isDomainViewEmpty()) {
					if (attribute.allowRuleUsage()) {
						attribute.addDomainView(DomainView.TEMPLATE_EDITOR);
						attribute.addDomainView(DomainView.ENGINE_OBJECTS);
					}
					else {
						attribute.addDomainView(DomainView.ENGINE_OBJECTS);
					}
				}
			} // for

			// translation processing
			for (Iterator<DomainTranslation> iterator = domainClass.getDomainTranslations().iterator(); iterator.hasNext();) {
				DomainTranslation translation = iterator.next();

				// populate view and contextless label, if necessary
				if (translation.getContextlessLabel() == null || translation.getContextlessLabel().length() == 0) {
					translation.setContextlessLabel(translation.getDisplayLabel());
				}
				if (translation.isDomainViewEmpty()) {
					translation.addDomainView(DomainView.POLICY_EDITOR);
				}
			}
		}

	}

	/**
	 * This method adds domain class to list of domain classes.
	 * @param domClass domain class.
	 */
	private void addDomainClass(DomainClass domClass) {
		domainClassList.add(domClass);
	}

	private void processClassLinkage(DomainClassLink link) {
		List<DomainClassLink> list = domainLinkMap.get(link.getParentName());
		if (list == null) {
			list = new LinkedList<DomainClassLink>();
			domainLinkMap.put(link.getParentName(), list);
		}
		list.add(link);
	}

	public List<DomainClass> getChildClasses(String name) {
		List<DomainClass> childList = new ArrayList<DomainClass>();
		List<DomainClassLink> linkList = domainLinkMap.get(name);
		if (linkList != null) {
			DomainClass dc = null;
			for (Iterator<DomainClassLink> iter = linkList.iterator(); iter.hasNext();) {
				DomainClassLink element = iter.next();
				dc = getDomainClass(element.getChildName());
				if (dc != null) {
					childList.add(dc);
				}
			}
		}
		return childList;
	}

	/**
	 * This method returns list of domain classes
	 * @return list of domain classes.
	 */
	public List<DomainClass> getDomainClasses() {
		return Collections.unmodifiableList(domainClassList);
	}

	public void clearAll() {
		domainClassList.clear();
	}

	/**
	 * Return domain class for the given class name
	 * @param className name of the domain class
	 * @return domain class
	 */
	public DomainClass getDomainClass(String className) {
		DomainClass domClass = null;
		for (int i = 0; i < domainClassList.size(); i++) {
			domClass = domainClassList.get(i);
			if (domClass.getName().equalsIgnoreCase(className)) {
				return domClass;
			}
		}
		return null;
	}

	public DomainAttribute getDomainAttribute(String attributeMap) {
		String[] strs = attributeMap.split("\\.");
		if (strs.length == 2) {
			DomainClass dc = getDomainClass(strs[0]);
			if (dc != null) {
				return dc.getDomainAttribute(strs[1]);
			}
		}
		return null;
	}

	public boolean isEmpty() {
		return domainClassList.isEmpty();
	}

	/**
	 * Return domain class index in list of indexes.
	 * @param className name of domain class
	 * @return domain class index
	 */

	public int getClassIndex(String className) {
		String s1 = className.toUpperCase();
		DomainClass domClass = null;
		for (int i = 0; i < domainClassList.size(); i++) {
			domClass = domainClassList.get(i);
			if (domClass.getName().equalsIgnoreCase(s1)) return i;
		}

		return -1;
	}

}