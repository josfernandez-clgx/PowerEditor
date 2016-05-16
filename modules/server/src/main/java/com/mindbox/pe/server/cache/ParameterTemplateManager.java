package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.model.template.ParameterTemplateColumn;
import com.mindbox.pe.server.Util;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterTemplateManager extends AbstractCacheManager {

	private static ParameterTemplateManager instance = null;

	public static synchronized ParameterTemplateManager getInstance() {
		if (instance == null) instance = new ParameterTemplateManager();
		return instance;
	}

	private final Map<Integer, ParameterTemplate> templateMap;

	private ParameterTemplateManager() {
		super();
		templateMap = new HashMap<Integer, ParameterTemplate>();
	}

	public void addParameterTemplate(ParameterTemplate template) {
		templateMap.put(new Integer(template.getID()), template);
	}

	public void finishLoading() {
		for (Iterator<ParameterTemplate> iter = templateMap.values().iterator(); iter.hasNext();) {
			ParameterTemplate template = iter.next();

			for (Iterator<ParameterTemplateColumn> iterator = template.getColumns().iterator(); iterator.hasNext();) {
				ParameterTemplateColumn column = iterator.next();
				// use attribute's title when column is mapped to one
				if (column.getMappedAttribute() != null && column.getTitle() == null) {
					String className = column.getMAClassName();
					String attrName = column.getMAAttributeName();
					DomainAttribute domainattribute = DomainManager.getInstance().getDomainAttribute(className, attrName);
					if (domainattribute != null) {
						column.setTitle(domainattribute.getDisplayLabel());
					}
					else {
						column.setTitle(column.getMappedAttribute());
					}
				}
				else if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
					column.getColumnDataSpecDigest().setEnumSourceType(column.getColumnDataSpecDigest().hasEnumValue() ? EnumSourceType.COLUMN : EnumSourceType.DOMAIN_ATTRIBUTE);
				}
			}
		}
	}

	public ParameterTemplate getTemplate(int id) {
		return templateMap.get(new Integer(id));
	}

	/**
	 * Gets a parameter template with the specified name.
	 * @param name name
	 * @return the parameter template with <code>name</code>, if found; <code>null</code>, otherwise
	 * @since PowerEditor 4.4.0
	 */
	public ParameterTemplate getTemplate(String name) {
		for (Map.Entry<Integer, ParameterTemplate> entry : templateMap.entrySet()) {
			if (entry.getValue().getName().equalsIgnoreCase(name)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public int getTemplateCount() {
		return templateMap.size();
	}

	/**
	 * Gets all parameter templates.
	 * @return all parameter templates
	 */
	public List<ParameterTemplate> getTemplates() {
		LinkedList<ParameterTemplate> linkedlist = new LinkedList<ParameterTemplate>();
		for (Iterator<ParameterTemplate> iter = templateMap.values().iterator(); iter.hasNext();) {
			linkedlist.add(iter.next());
		}
		return linkedlist;
	}

	/**
	 * Gets a list of templates of whic id is in the specified int array.
	 * @param ids array of template ids
	 * @return list of templates of which id is in <code>ids</code>
	 * @since PowerEditor 4.3.6
	 */
	public List<ParameterTemplate> getTemplates(int[] ids) {
		if (Util.isEmpty(ids)) {
			return getTemplates();
		}
		else {
			List<ParameterTemplate> list = new ArrayList<ParameterTemplate>();
			for (int i = 0; i < ids.length; i++) {
				ParameterTemplate template = getTemplate(ids[i]);
				if (template != null) {
					list.add(template);
				}
			}
			return list;
		}
	}

	public void removeFromCache(int templateID) {
		templateMap.remove(new Integer(templateID));
	}

	public void startLoading() {
		templateMap.clear();
	}

}