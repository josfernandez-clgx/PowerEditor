package com.mindbox.pe.server.cache;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.DomainClassLink;
import com.mindbox.pe.model.DomainTranslation;
import com.mindbox.pe.model.DomainView;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;

/**
 * Domain model cache manager.
 * @since PowerEditor 1.0
 */
public class DomainManager extends AbstractCacheManager implements DomainClassProvider {

	private static DomainManager mSingleton = null;

	/**
	 * Finds the specified domain translation of among domain classes.
	 * @param contextlessLabel
	 * @return the specified domain translation, if found; <code>null</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	public DomainTranslation findDomainTranslationByContextlessLabel(String contextlessLabel) {
		List<DomainClass> dcList = getAllDomainClasses();
		for (DomainClass dc : dcList) {
			if (dc != null) {
				for (DomainTranslation dt : dc.getDomainTranslations()) {
					if (dt.getContextlessLabel().equals(contextlessLabel)) { return dt; }
				}
			}
		}
		return null;
	}

	/**
	 * Finds the specified domain attribute of among domain classes.
	 * @param contextlessLabel
	 * @return the specified domain attribute, if found; <code>null</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	public DomainAttribute findDomainAttributeByContextlessLabel(String contextlessLabel) {
		List<DomainClass> dcList = getAllDomainClasses();
		for (DomainClass dc : dcList) {
			if (dc != null) {
				for (DomainAttribute da : dc.getDomainAttributes()) {
					if (da.getContextlessLabel().equals(contextlessLabel)) { return da; }
				}
			}
		}
		return null;
	}

	public DomainClass findDomainClassForAttribute(DomainAttribute attribute) {
		List<DomainClass> dcList = getAllDomainClasses();
		for (DomainClass dc : dcList) {
			if (dc != null) {
				for (DomainAttribute da : dc.getDomainAttributes()) {
					if (da == attribute) { return dc; }
				}
			}
		}
		return null;
	}

	/**
	 * Finds the specified domain translation of the speified domain class.
	 * @param className
	 * @param contextlessLabel
	 * @return the specified domain translation, if found; <code>null</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	public DomainTranslation findDomainTranslationByContextlessLabel(String className, String contextlessLabel) {
		DomainClass dc = getDomainClass(className);
		if (dc != null) {
			for (DomainTranslation dt : dc.getDomainTranslations()) {
				if (dt.getContextlessLabel().equals(contextlessLabel)) { return dt; }
			}
		}
		return null;
	}

	/**
	 * Finds the specified domain attribute of the speified domain class.
	 * This first checks contextless label of each attribute that matches <code>contextlessLabel</code>.
	 * If none found, this then checks against name of each attribute of Class <code>className</code>.
	 * @param className
	 * @param contextlessLabel
	 * @return the specified domain attribute, if found; <code>null</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	public DomainAttribute findDomainAttributeByContextlessLabel(String className, String contextlessLabel) {
		DomainClass dc = getDomainClass(className);
		if (dc != null) {
			for (Iterator<DomainAttribute> iter = dc.getDomainAttributes().iterator(); iter.hasNext();) {
				DomainAttribute da = iter.next();
				if (da.getContextlessLabel().equals(contextlessLabel)) { return da; }
			}
			for (Iterator<DomainAttribute> iter = dc.getDomainAttributes().iterator(); iter.hasNext();) {
				DomainAttribute da = iter.next();
				if (da.getName().equals(contextlessLabel)) { return da; }
			}
		}
		return null;
	}

	public DomainClass getDomainClass(String s) {
		if (s == null) {
			logger.debug("<<< getDomainClass: null for arg1 is null");
			return null;
		}
		DomainClass domainclass = domainClassMap.get(s.toUpperCase());
		if (domainclass == null) {
			String msg = "Failed to find a domain class: " + s;
			logger.warn(msg);
		}
		return domainclass;
	}

	public List<DomainClass> getAllDomainClasses() {
		List<DomainClass> list = new LinkedList<DomainClass>();
		list.addAll(domainClassMap.values());
		return Collections.unmodifiableList(list);
	}

	public boolean removeFromCache(String className) {
		return domainClassMap.remove(className) != null;
	}

	private void processClassLinkage(DomainClassLink link) {
		String childName = link.getChildName();
		List<DomainClassLink> obj = domainLinkMap.get(childName);
		if (obj == null) {
			obj = new LinkedList<DomainClassLink>();
			domainLinkMap.put(childName, obj);
		}
		obj.add(link);
	}

	public DomainAttribute getDomainAttributeForAttributeMap(String attributeMap) {
		String[] strs = attributeMap.split("\\.");
		if (strs.length == 2) {
			DomainClass dc = getDomainClass(strs[0]);
			if (dc != null) {
				return dc.getDomainAttribute(strs[1]);
			}
		}
		return null;
	}

	public DomainAttribute getDomainAttribute(String className, String attrname) {
		DomainAttribute domainattribute = null;
		DomainClass domainclass = getDomainClass(className);
		if (domainclass != null) domainattribute = domainclass.getDomainAttribute(attrname);
		return domainattribute;
	}
	
	/**
	 * Tests if the specified domain attribute has enumeration values.
	 * @param className
	 * @param attrname
	 */
	public boolean hasEnumerationValues(String className, String attrname) {
		DomainAttribute da = getDomainAttribute(className, attrname);
		return (da == null ? false : da.hasEnumValue());
	}
	
	public boolean isStringDeployType(Reference reference) {
		return isStringDeployType(reference.getClassName(), reference.getAttributeName());
	}
	
	public boolean isStringDeployType(String className, String attrName) {
		DomainAttribute attribute = getDomainAttribute(className,attrName);
		return (attribute == null ? false : attribute.getDeployType() == DeployType.STRING);
	}

	public synchronized void startLoading() {
		domainClassMap.clear();
	}

	private DomainManager() {
		domainClassMap = new Hashtable<String,DomainClass>();
		domainLinkMap = new Hashtable<String, List<DomainClassLink>>();
	}

	public String toString() {
		String s = "";
		s += "DomainManager with " + domainClassMap.size() + " DomainClasss!";
		s += domainClassMap.toString();
		return s;
	}

	private DomainClassLink findLinkToParent(List<DomainClassLink> parentLinkList, String parent) {
		for (DomainClassLink domainclasslink : parentLinkList) {
			if (domainclasslink.getParentName().equalsIgnoreCase(parent)) { return domainclasslink; }
		}
		return null;
	}

	private List<DomainClassLink> getLinkageList(String child, String ancestor) {
		logger.debug(">>> getLinkageList: child=" + child + ",ancestor=" + ancestor);
		List<DomainClassLink> list = getParentLinks(child);
		if (list == null) return null;
		logger.debug("    getLinkageList: list.size = " + list.size());

		DomainClassLink link = findLinkToParent(list, ancestor);
		if (link != null) {
			List<DomainClassLink> linkageList = new LinkedList<DomainClassLink>();
			linkageList.add(link);
			return linkageList;
		}
		// no direct parent exists, find ancestor path
		else {
			for (DomainClassLink domainclasslink : list) {
				logger.debug("    getLinkageList: processing " + domainclasslink);
				List<DomainClassLink> list1 = getLinkageList(domainclasslink.getParentName(), ancestor);
				if (list1 != null) {
					List<DomainClassLink> linkageList = new LinkedList<DomainClassLink>();
					linkageList.add(domainclasslink);
					linkageList.addAll(list1);
					return linkageList;
				}
			}
		}
		return null;
	}

	public DomainClassLink[] getLinkage(String childName, String ancestorName) {
		logger.info(">>> getLinkage: child=" + childName + ",ancestor=" + ancestorName);
		List<DomainClassLink> list = getLinkageList(childName, ancestorName);
		if (list == null) return null;
		// the list is from child to parent, reverse it before returning it
		Collections.reverse(list);
		return list.toArray(new DomainClassLink[0]);
	}

	public synchronized void finishLoading() throws InvalidDataException {
		logger.info(">>> finishLoading");

		// Need to make sure that number and type of attributes match
		// up in config xml file and domain xml file........
		TemplateUsageType[] types = TemplateUsageType.getAllInstances();
		for (int idx = 0; idx < types.length; idx++) {
			logger.debug("... finishLoading: checking " + types[idx]);
			RuleGenerationConfiguration ruleConfig = ConfigurationManager.getInstance().getRuleGenerationConfiguration(types[idx]);
			RuleGenerationConfiguration.PatternConfig controlPatternConfig = ruleConfig.getControlPatternConfig();
			logger.debug("... finishLoading: ruleConfig="+ruleConfig+" , patternConfig="+controlPatternConfig);
			DomainClass domainClass = getDomainClass(controlPatternConfig.getPatternClassName());
			// Check if a domain class for the "control" Pattern in PowerEditorConfiguration.xml exists.
			// If it does not, throw an exception.
			if (domainClass == null) { throw new IllegalStateException("No " + controlPatternConfig.getPatternClassName()
					+ " domain class present in domain xml file."); }
			Map<String,String> configAttributes = ((RuleGenerationConfiguration.ControlPatternConfig) controlPatternConfig).getAttributes();
			for (Iterator<String> iter = configAttributes.keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				DomainAttribute domainAttr = domainClass.getDomainAttribute(configAttributes.get(key));
				if (domainAttr == null) { throw new IllegalStateException("No matching domain attribute found for " + key); }
			}
		}

		for (DomainClass domainClass : domainClassMap.values()) {
			if (domainClass.allowRuleUsage()) {
				if (domainClass.allowRuleUsage()) {
					domainClass.addDomainView(DomainView.TEMPLATE_EDITOR);
				}
				domainClass.addDomainView(DomainView.ENGINE_OBJECTS);
			}
			
			// process class links
			for (DomainClassLink link : domainClass.getDomainClassLinks()) {
				logger.info("finishLoading: processing " + link);
				// put the link in the cache
				processClassLinkage(link);

				// put the link attribute to the parent class
				String parentName = link.getParentName();
				String childName = link.getChildName();
				DomainAttribute attribute = new DomainAttribute();
				attribute.setName(childName);
				attribute.setDeployType(DeployType.RELATIONSHIP);
				attribute.setDeployLabel(link.getDeployValueName()); // Changed @since PowerEditor 4.2.0.
				attribute.setDisplayLabel("");
				attribute.setAllowRuleUsage("0");

				DomainClass parentClass = getDomainClass(parentName);
				if (parentClass == null) { throw new IllegalStateException(link + " contains invalid parent class name: class " + parentName
						+ " not found"); }
				parentClass.addDomainAttribute(attribute);
			}

			// attribute processing
			for (DomainAttribute attribute : domainClass.getDomainAttributes()) {
				logger.info("finishLoading: processing " + attribute + " with " + domainClass.getName()+"."+attribute.getName());
				
				// populate view and contextless label, if necessary
				if (attribute.getContextlessLabel() == null || attribute.getContextlessLabel().length() == 0) {
					attribute.setContextlessLabel(attribute.getDisplayLabel());
				}
				if (attribute.isDomainViewEmpty()) {
					if (attribute.allowRuleUsage()) {
						attribute.addDomainView(DomainView.TEMPLATE_EDITOR);
					}
					attribute.addDomainView(DomainView.ENGINE_OBJECTS);
				}

				// process enum list for attributes
				if (attribute.hasEnumValue()) {
					logger.debug("finishLoading: processing enum values...");

					List<String> displayLabelList = new LinkedList<String>();
					List<String> deployValueList = new LinkedList<String>();
					EnumValue[] values = attribute.getEnumValues();
					List<EnumValue> enumValueList = new LinkedList<EnumValue>();
					String key = domainClass.getName()+"."+attribute.getName();
					for (int i = 0; i < values.length; i++) {
						logger.debug("finishLoading: --> " + values[i]);
						if (!values[i].isValidForDomainEnumValue()) { throw new IllegalStateException("Illegal EnumValue configuration for " + values[i]); }

						displayLabelList.add(values[i].getDisplayLabel());
						deployValueList.add(values[i].getDeployValue());
						enumValueList.add(values[i]);
						UIConfiguration.addEnumValue(key,values[i]);
					}

					// add to deployment manager
					DeploymentManager.getInstance().addEnumValueMap(
							domainClass.getName(),
							attribute.getName(),
							enumValueList);
				}
			} // for

			// translation processing
			for (DomainTranslation translation : domainClass.getDomainTranslations()) {

				// populate view and contextless label, if necessary
				if (translation.getContextlessLabel() == null || translation.getContextlessLabel().length() == 0) {
					translation.setContextlessLabel(translation.getDisplayLabel());
				}
				if (translation.isDomainViewEmpty()) {
					translation.addDomainView(DomainView.POLICY_EDITOR);
				}
			}
		}

		//		 iterate over templates
		logger.debug("finishLoading: About to iterate over templates. There are " + GuidelineTemplateManager.getInstance().getAllTemplates().size());
		for (GridTemplate element : GuidelineTemplateManager.getInstance().getAllTemplates()) {

			logger.debug("Grid-Cell: getting grids for template " + element.getID());

			// iterate over loaded grids
			for (Iterator<ProductGrid> iterator = GridManager.getInstance().getAllGridsForTemplate(element.getID()).iterator(); iterator.hasNext();) {
				ProductGrid grid = iterator.next();

				logger.debug("Grid-Cell: getting values for grid " + grid.getID() + ", " + grid.toString());
			}
		}
		logger.info("<<< finishLoading");
	}

	/**
	 * Adds the specified domain class in the cache.
	 * @param domainClass the domain class to add
	 */
	public synchronized void addDomainClass(DomainClass domainClass) {
		if (domainClass == null) throw new NullPointerException("cannot add null domain class");
		if (domainClass.getName() == null || domainClass.getName().length() == 0)
				throw new IllegalArgumentException("Cannot add a domain class with no name");
		if (domainClassMap.containsKey(domainClass.getName())) {
			// update existing domain class
			updateDomainClass(domainClass);
		}
		else {
			domainClassMap.put(domainClass.getName().toUpperCase(), domainClass);
		}
	}

	private void updateDomainClass(DomainClass domainClass) {
		domainClassMap.remove(domainClass.getName());
		domainClassMap.put(domainClass.getName(), domainClass);
	}


	public List<DomainClassLink> getParentLinks(String s) {
		return domainLinkMap.get(s);
	}

	public static synchronized DomainManager getInstance() {
		if (mSingleton == null) mSingleton = new DomainManager();
		return mSingleton;
	}

	private Map<String,DomainClass> domainClassMap;
	private Map<String, List<DomainClassLink>> domainLinkMap;
}