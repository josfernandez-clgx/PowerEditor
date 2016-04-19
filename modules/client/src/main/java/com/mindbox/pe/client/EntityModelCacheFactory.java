package com.mindbox.pe.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import com.mindbox.pe.client.applet.entities.generic.category.CategoryToEntityAssociationData;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.event.EntityDeleteEvent;
import com.mindbox.pe.client.common.event.EntityDeleteListener;
import com.mindbox.pe.client.common.table.TemplateIDNameTableModel;
import com.mindbox.pe.client.common.tree.DatedCategoryTreeModel;
import com.mindbox.pe.client.common.tree.DomainTreeFlatNode;
import com.mindbox.pe.client.common.tree.DomainWithAttributeTreeNode;
import com.mindbox.pe.client.common.tree.GenericCategoryNode;
import com.mindbox.pe.client.common.tree.RootTreeNode;
import com.mindbox.pe.client.common.tree.TemplateTreeNode;
import com.mindbox.pe.client.common.tree.UsageGroupTreeNode;
import com.mindbox.pe.client.common.tree.UsageTypeTreeNode;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.TimedAssociationKey;
import com.mindbox.pe.model.comparator.DateSynonymComparator;
import com.mindbox.pe.model.comparator.GenericCategoryComparator;
import com.mindbox.pe.model.comparator.IDNameObjectComparator;
import com.mindbox.pe.model.comparator.TimedAssociationKeyComparator;
import com.mindbox.pe.model.comparator.TypeEnumValueComparator;
import com.mindbox.pe.model.comparator.UsageTypeComparator;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.DomainView;
import com.mindbox.pe.model.filter.AllSearchFilter;
import com.mindbox.pe.model.filter.GenericEntityByCategoryFilter;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.GuidelineTab;

/**
 * Caches various entities on the client for performance enhancement.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class EntityModelCacheFactory implements DatedCategoryTreeModel.DataProvider {

	private static EntityModelCacheFactory instance = null;

	private static void addAndNotifyTemplateNode(DefaultTreeModel defaultTreeModel, UsageTypeTreeNode usageNode, GridTemplate template) {
		TemplateTreeNode childNode = new TemplateTreeNode(usageNode, template);
		usageNode.addChild(childNode, true);
		defaultTreeModel.nodesWereInserted(usageNode, new int[] { usageNode.indexOfChild(childNode) });
	}

	/**
	 * Copies combo box model. 
	 * If <code>model</code> is <code>null</code>, this returns an empty combo model.
	 * @param model
	 * @return copy of <code>model</code>, if it's not <code>null</code>; empty combo model, otherwise
	 */
	private static DefaultComboBoxModel copyComboBoxModel(ComboBoxModel model) {
		if (model == null) {
			return new DefaultComboBoxModel();
		}

		Object[] items = new Object[model.getSize()];
		for (int i = 0; i < items.length; i++) {
			items[i] = model.getElementAt(i);
		}
		return new DefaultComboBoxModel(items);
	}

	private static TemplateTreeNode findTemplateTreeNode(UsageTypeTreeNode usageNode, int templateID) {
		for (int i = 0; i < usageNode.getChildCount(); i++) {
			TreeNode child = usageNode.getChildAt(i);
			if (child instanceof TemplateTreeNode && ((TemplateTreeNode) child).getTemplate().getID() == templateID) {
				return (TemplateTreeNode) child;
			}
		}
		return null;
	}

	private static UsageTypeTreeNode findUsageTypeTreeNodeFromModel(DefaultTreeModel defaultTreeModel, TemplateUsageType usageType) {
		for (int i = 0; i < ((RootTreeNode) defaultTreeModel.getRoot()).getChildCount(); i++) {
			UsageGroupTreeNode usageGroupTreeNode = (UsageGroupTreeNode) ((RootTreeNode) defaultTreeModel.getRoot()).getChildAt(i);
			for (int j = 0; j < usageGroupTreeNode.getChildCount(); j++) {
				UsageTypeTreeNode usageTypeTreeNode = (UsageTypeTreeNode) usageGroupTreeNode.getChildAt(j);
				if (usageTypeTreeNode.getUsageType() == usageType) {
					return usageTypeTreeNode;
				}
			}
		}
		return null;
	}

	public static EntityModelCacheFactory getInstance() {
		if (instance == null) {
			instance = new EntityModelCacheFactory();
		}
		return instance;
	}

	/**
	 * Tests if the specified domain class has at least one attribute with policy editor domain view.
	 * @param domainClass
	 * @return <code>true</code> if <code>domainClass</code> has at least one attribute with policy editor domain view;
	 *         <code>false</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	static boolean isEnabledForUISelection(DomainClass domainClass) {
		return (domainClass.hasDomainView(DomainView.POLICY_EDITOR) || domainClass.hasDomainView(DomainView.TEMPLATE_EDITOR))
				&& (domainClass.hasDomainViewAttribute(DomainView.POLICY_EDITOR) || domainClass.hasDomainViewAttribute(DomainView.TEMPLATE_EDITOR));
	}

	private static void removeAndNotifyTemplateNodeIfFound(DefaultTreeModel defaultTreeModel, UsageTypeTreeNode usageNode, GridTemplate template) {
		TemplateTreeNode childNode = findTemplateTreeNode(usageNode, template.getID());
		if (childNode != null) {
			usageNode.removeChild(childNode, true);
			defaultTreeModel.nodeStructureChanged(usageNode);
			childNode.clear();
			childNode = null;
		}
	}

	private boolean relodRolesInRoleTabpanel = false;

	private final List<EntityDeleteListener> entityDeleteListenerList;

	private final List<DefaultComboBoxModel> dateSynonymModelList, dateSynonymWithEmptyModelList;

	private final DefaultComboBoxModel dateSynonymModel, dateSynonymWithEmptyModel;
	private final DefaultComboBoxModel privilegeModel, roleModel;
	private final DefaultComboBoxModel paramTemplateModel, paramTemplateWithEmptyModel;
	private final ClientCache cacheInstance;
	private final List<GridTemplate> cachedTemplateList;
	private final List<GridTemplate> cachedSearchTemplateList;
	private final List<DateSynonym> allDateSynonyms = new ArrayList<DateSynonym>();
	private final TemplateIDNameTableModel templateTableModel;
	private final List<DefaultTreeModel> guidelineTemplateTreeModelList = new ArrayList<DefaultTreeModel>();

	private DefaultTreeModel guidelineTemplateTreeModel = null;
	private DefaultTreeModel guidelineTemplateSearchTreeModel = null;
	private DefaultTreeModel attributeTreeModel = null;
	private DefaultTreeModel domainClassTreeModel = null;
	private DefaultTreeModel flatDomainClassTreeModel = null;
	private final Map<TemplateUsageType, UsageTypeTreeNode> usageTreeNodeMap = Collections.synchronizedMap(new HashMap<TemplateUsageType, UsageTypeTreeNode>());
	private final Map<TemplateUsageType, UsageTypeTreeNode> usageSearchTreeNodeMap = Collections.synchronizedMap(new HashMap<TemplateUsageType, UsageTypeTreeNode>());

	private final Map<GenericEntityType, ComboBoxModel> genericEntityComboModelMap = Collections.synchronizedMap(new HashMap<GenericEntityType, ComboBoxModel>());
	private final Map<GenericEntityType, ComboBoxModel> genericEntityWithEmptyComboModelMap = Collections.synchronizedMap(new HashMap<GenericEntityType, ComboBoxModel>());
	private final Map<GenericEntityType, List<DefaultComboBoxModel>> genericEntityComboModelCopyMap = Collections.synchronizedMap(new HashMap<GenericEntityType, List<DefaultComboBoxModel>>());
	private final Map<GenericEntityType, List<DefaultComboBoxModel>> genericEntityWithEmptyComboModelCopyMap = Collections.synchronizedMap(new HashMap<GenericEntityType, List<DefaultComboBoxModel>>());
	private final Map<Integer, List<GenericCategory>> cachedGenericCategoryMap;
	private final Map<Integer, List<DatedCategoryTreeModel>> cachedGenericCategoryTreeModelMap;
	private final Map<String, List<TypeEnumValue>> cachedTypeEnumValueMap = new HashMap<String, List<TypeEnumValue>>();
	private ComboBoxModel usageTypeModel = null;

	private EntityModelCacheFactory() {
		cacheInstance = new ClientCache();
		entityDeleteListenerList = new ArrayList<EntityDeleteListener>();
		dateSynonymModelList = new ArrayList<DefaultComboBoxModel>();
		dateSynonymWithEmptyModelList = new ArrayList<DefaultComboBoxModel>();

		dateSynonymModel = new DefaultComboBoxModel();
		dateSynonymWithEmptyModel = new DefaultComboBoxModel();
		paramTemplateModel = new DefaultComboBoxModel();
		paramTemplateWithEmptyModel = new DefaultComboBoxModel();
		privilegeModel = new DefaultComboBoxModel();
		roleModel = new DefaultComboBoxModel();
		templateTableModel = new TemplateIDNameTableModel();
		cachedTemplateList = new LinkedList<GridTemplate>();
		cachedSearchTemplateList = new LinkedList<GridTemplate>();

		cachedGenericCategoryMap = new HashMap<Integer, List<GenericCategory>>();
		cachedGenericCategoryTreeModelMap = new HashMap<Integer, List<DatedCategoryTreeModel>>();
	}

	public void add(GenericEntity entity) {
		DefaultComboBoxModel model = (DefaultComboBoxModel) getGenericEntityComboModel(entity.getType(), false);
		GenericEntity[] entities = new GenericEntity[model.getSize() + 1];
		for (int i = 0; i < entities.length; i++) {
			entities[i] = (GenericEntity) model.getElementAt(i);
		}
		entities[entities.length - 1] = entity;
		Arrays.sort(entities, new IDNameObjectComparator<GenericEntity>());

		add_helper(entities, false, model);

		add_helper(entities, true, (DefaultComboBoxModel) getGenericEntityComboModel(entity.getType(), true));

		add_helper(entities, false, genericEntityComboModelCopyMap.get(entity.getType()));
		add_helper(entities, true, genericEntityWithEmptyComboModelCopyMap.get(entity.getType()));
	}

	public void add(GridTemplate template) {
		if (ClientUtil.checkViewOrEditGuidelinePermissionOnUsageType(template.getUsageType())) {
			templateTableModel.addData(template);
			cachedSearchTemplateList.add(template);
		}
		cachedTemplateList.add(template);

		UsageTypeTreeNode usageNode = findUsageTypeTreeNode(template.getUsageType());
		if (usageNode != null) {
			addAndNotifyTemplateNode(guidelineTemplateTreeModel, usageNode, template);
			synchronized (guidelineTemplateTreeModelList) {
				for (DefaultTreeModel defaultTreeModel : guidelineTemplateTreeModelList) {
					usageNode = findUsageTypeTreeNodeFromModel(defaultTreeModel, template.getUsageType());
					if (usageNode != null) {
						addAndNotifyTemplateNode(defaultTreeModel, usageNode, template);
					}
				}
			}
		}
		else {
			ClientUtil.getLogger().warn("NO usage type node found for " + template.getUsageType());
		}
		usageNode = findUsageTypeSearchTreeNode(template.getUsageType());
		if (usageNode != null) {
			addAndNotifyTemplateNode(guidelineTemplateSearchTreeModel, usageNode, template);
		}
		else {
			ClientUtil.getLogger().warn("NO usage type node found for " + template.getUsageType());
		}
	}

	private void add_helper(GenericEntity[] entities, boolean hasEmpty, DefaultComboBoxModel model) {
		model.removeAllElements();
		if (hasEmpty) model.addElement(" ");
		for (int i = 0; i < entities.length; i++) {
			model.addElement(entities[i]);
		}
	}

	private void add_helper(GenericEntity[] entities, boolean hasEmpty, List<DefaultComboBoxModel> comboModelList) {
		if (comboModelList != null) {
			for (int i = 0; i < comboModelList.size(); i++) {
				add_helper(entities, hasEmpty, comboModelList.get(i));
			}
		}
	}

	public void addDateSynonym(DateSynonym ds) {
		if (ds.isNamed()) insertNamedDateSynonym(ds);
		if (!allDateSynonyms.contains(ds)) allDateSynonyms.add(ds);
	}

	public void addEntityDeleteListener(EntityDeleteListener listener) {
		synchronized (entityDeleteListenerList) {
			if (!entityDeleteListenerList.contains(listener)) entityDeleteListenerList.add(listener);
		}
	}

	/**
	 * Adds the specified category in all generic category tree models.
	 * 
	 * @param category
	 */
	public void addGenericCategory(GenericCategory category) {
		Integer key = new Integer(category.getType());
		List<GenericCategory> categoryList = getCachedGenericCategoryList(key);
		synchronized (categoryList) {
			for (Iterator<DatedCategoryTreeModel> iter = getCachedGenericCategoryTreeModelList(key).iterator(); iter.hasNext();) {
				DatedCategoryTreeModel element = iter.next();
				element.addGenericCategory(category);
			}
			categoryList.add(category);
		}
	}

	/**
	 * Adds a role to client cache and also to role list that is used to display roles
	 * on Manage users screen
	 * @author vineet khosla
	 * @since PowerEditor 5.0
	 * @param role to add
	 */
	public void addRole(Role role) {
		cacheInstance.addToRoleList(role);
		roleModel.addElement(role);
	}

	/**
	 * This tree includes those UsageTypes on whom the user has View or Edit
	 * Guideline permission. This tree is dispalyed in 'Template Selection' in
	 * 'Search Policies' -> 'Policy Search Criteria' tab 
	 */
	private void buildGuidelineTemplateSearchTreeModel() {
		usageSearchTreeNodeMap.clear();

		final List<GuidelineTab> tabConfigs = ClientUtil.getGuidelineTabs();

		RootTreeNode rootNode = new RootTreeNode(null);
		for (final GuidelineTab guidelineTab : tabConfigs) {
			if (ClientUtil.checkViewOrEditGuidelinePermission(guidelineTab)) {
				UsageGroupTreeNode groupNode = new UsageGroupTreeNode(rootNode, guidelineTab);

				// add usage children
				for (GuidelineTab.UsageType guidelineUsageType : guidelineTab.getUsageType()) {
					UsageTypeTreeNode usageNode = getUsageTypeSearchTreeNode(groupNode, TemplateUsageType.valueOf(guidelineUsageType.getName()));
					if (ClientUtil.checkViewOrEditGuidelinePermissionOnUsageType(usageNode.getUsageType())) {
						groupNode.addChild(usageNode, false);
					}
				}
				rootNode.addChild(groupNode, false);
			}
		}

		// add templates to their respective usage type node parent
		for (Iterator<GridTemplate> iter = cachedTemplateList.iterator(); iter.hasNext();) {
			GridTemplate element = iter.next();
			UsageTypeTreeNode usageNode = findUsageTypeSearchTreeNode(element.getUsageType());
			if (usageNode != null) {
				usageNode.addChild(new TemplateTreeNode(usageNode, element), true);
			}
		}

		guidelineTemplateSearchTreeModel = null;
		guidelineTemplateSearchTreeModel = new DefaultTreeModel(rootNode);
	}

	/**
	 * This tree includes those UsageTypes on whom the user has View or Edit
	 * Template permission. This tree is dispalyed in 'Guideline Template Selection' in
	 * 'Manage Templates' tab 
	 */
	private void buildGuidelineTemplateTreeModel() {
		usageTreeNodeMap.clear();

		final List<GuidelineTab> tabConfigs = ClientUtil.getGuidelineTabs();

		RootTreeNode rootNode = new RootTreeNode(null);
		for (final GuidelineTab guidelineTab : tabConfigs) {
			if (ClientUtil.checkViewOrEditTemplatePermission(guidelineTab)) {
				UsageGroupTreeNode groupNode = new UsageGroupTreeNode(rootNode, guidelineTab);

				// add usage children
				for (GuidelineTab.UsageType guidelineUsageType : guidelineTab.getUsageType()) {
					UsageTypeTreeNode usageNode = getUsageTypeTreeNode(groupNode, TemplateUsageType.valueOf(guidelineUsageType.getName()));
					if (ClientUtil.checkViewOrEditTemplatePermissionOnUsageType(usageNode.getUsageType())) {
						groupNode.addChild(usageNode, false);
					}
				}
				rootNode.addChild(groupNode, false);
			}
		}

		// add templates to their respective usage type node parent
		for (Iterator<GridTemplate> iter = cachedTemplateList.iterator(); iter.hasNext();) {
			GridTemplate element = iter.next();
			UsageTypeTreeNode usageNode = findUsageTypeTreeNode(element.getUsageType());
			if (usageNode != null) {
				usageNode.addChild(new TemplateTreeNode(usageNode, element), true);
			}
		}

		guidelineTemplateTreeModel = null;
		guidelineTemplateTreeModel = new DefaultTreeModel(rootNode);
	}

	public void cacheTemplates() {
		cachedTemplateList.clear();
		try {
			List<GridTemplate> list = ClientUtil.getCommunicator().search(new AllSearchFilter<GridTemplate>(PeDataType.TEMPLATE));
			if (list != null) {
				for (GridTemplate template : list) {
					if (ClientUtil.checkViewOrEditTemplatePermissionOnUsageType(template.getUsageType())) {
						cachedTemplateList.add(template);
					}
				}
			}
		}
		catch (Exception ex) {
			ClientUtil.getInstance().showWarning("msg.error.failure.get.templates", new Object[] { ex.getMessage() });
		}

		// This list is used in search templates table version
		cachedSearchTemplateList.clear();
		try {
			List<GridTemplate> list = ClientUtil.getCommunicator().search(new AllSearchFilter<GridTemplate>(PeDataType.TEMPLATE));
			if (list != null) {
				for (GridTemplate template : list) {
					if (ClientUtil.checkViewOrEditGuidelinePermissionOnUsageType(template.getUsageType())) {
						cachedSearchTemplateList.add(template);
					}
				}
			}
		}
		catch (Exception ex) {
			ClientUtil.getInstance().showWarning("msg.error.failure.get.templates", new Object[] { ex.getMessage() });
		}

	}

	public ComboBoxModel copyGenericEntityComboModel(GenericEntityType type, boolean hasEmpty) {
		return copyGenericEntityComboModel_aux(type, hasEmpty);
	}

	private ComboBoxModel copyGenericEntityComboModel_aux(GenericEntityType type, boolean hasEmpty) {
		DefaultComboBoxModel model = copyComboBoxModel(getGenericEntityModel_aux(type, hasEmpty));
		Map<GenericEntityType, List<DefaultComboBoxModel>> copyMap = (hasEmpty ? genericEntityWithEmptyComboModelCopyMap : genericEntityComboModelCopyMap);
		List<DefaultComboBoxModel> list = null;
		if (copyMap.containsKey(type)) {
			list = copyMap.get(type);
		}
		else {
			list = new ArrayList<DefaultComboBoxModel>();
			copyMap.put(type, list);
		}
		list.add(model);
		return model;
	}

	public ListModel copyGenericEntityListModel(GenericEntityType type, boolean hasEmpty) {
		return copyGenericEntityComboModel_aux(type, hasEmpty);
	}

	public ComboBoxModel createDateSynonymComboModel(boolean hasEmpty) {
		if (hasEmpty) {
			synchronized (dateSynonymWithEmptyModelList) {
				DefaultComboBoxModel model = new DefaultComboBoxModel();
				for (int i = 0; i < dateSynonymWithEmptyModel.getSize(); i++) {
					model.addElement(dateSynonymWithEmptyModel.getElementAt(i));
				}
				dateSynonymWithEmptyModelList.add(model);
				return model;
			}
		}
		else {
			synchronized (dateSynonymModelList) {
				DefaultComboBoxModel model = new DefaultComboBoxModel();
				for (int i = 0; i < dateSynonymModel.getSize(); i++) {
					model.addElement(dateSynonymModel.getElementAt(i));
				}
				dateSynonymModelList.add(model);
				return model;
			}
		}
	}

	private GenericCategoryNode createGenericCategoryNode(GenericCategory category, boolean sort, boolean needsEntitiesLoaded) {
		return (sort ? new GenericCategoryNode(category, GenericCategoryComparator.getSortByNameInstance(), needsEntitiesLoaded) : new GenericCategoryNode(category, needsEntitiesLoaded));
	}

	public DatedCategoryTreeModel createGenericCategoryTreeModel(int categoryType, Date date, boolean showEntities, boolean sort) {
		Integer key = new Integer(categoryType);
		List<GenericCategory> categoryList = getCachedGenericCategoryList(key);
		GenericCategoryNode rootNode = null;
		if (!categoryList.isEmpty()) {
			GenericCategory category = findRootCategory(categoryList); //(GenericCategory) categoryList.get(0);
			rootNode = createGenericCategoryNode(category, sort, showEntities);
		}
		else {
			throw new NullPointerException("Category cannot be null. " + "At least one category must exist int order to create a category tree model.");
		}
		DatedCategoryTreeModel treeModel = new DatedCategoryTreeModel(rootNode, date, this, showEntities, sort);
		getCachedGenericCategoryTreeModelList(key).add(treeModel);
		return treeModel;
	}

	public DatedCategoryTreeModel createGenericCategoryWithEntitiesTreeModel(GenericEntityType entityType, Date date, boolean sort) {
		DatedCategoryTreeModel treeModel = createGenericCategoryTreeModel(entityType.getCategoryType(), date, true, sort);
		return treeModel;
	}

	public DefaultTreeModel createGuidelineTemplateTreeModel() {
		RootTreeNode rootNode = (RootTreeNode) guidelineTemplateTreeModel.getRoot();
		RootTreeNode newRootNode = new RootTreeNode(null);
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			newRootNode.addChild(new UsageGroupTreeNode(newRootNode, (UsageGroupTreeNode) rootNode.getChildAt(i)), false);
		}
		synchronized (guidelineTemplateTreeModelList) {
			DefaultTreeModel defaultTreeModel = new DefaultTreeModel(newRootNode);
			guidelineTemplateTreeModelList.add(defaultTreeModel);
			return defaultTreeModel;
		}
	}

	/**
	 * Call this only when the parent of the specified category has changed.
	 * 
	 * @param category
	 *            category whose parent has changed
	 */
	public void editGenericCategory(GenericCategory category) {
		Integer key = new Integer(category.getType());
		List<GenericCategory> categoryList = getCachedGenericCategoryList(key);
		synchronized (categoryList) {
			for (Iterator<DatedCategoryTreeModel> iter = getCachedGenericCategoryTreeModelList(key).iterator(); iter.hasNext();) {
				DatedCategoryTreeModel element = iter.next();
				element.editGenericCategory(category);
			}
		}
	}

	public List<GenericEntity> findAllGenericEntities(GenericEntityType type, String name) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (UtilBase.isEmpty(name)) return null;
		List<GenericEntity> list = new ArrayList<GenericEntity>();
		ListModel model = getGenericEntityListModel(type, false);
		if (model != null) {
			for (int i = 0; i < model.getSize(); i++) {
				GenericEntity entity = (GenericEntity) model.getElementAt(i);
				if (entity != null && entity.getName().equals(name)) {
					list.add(entity);
				}
			}
		}
		return list;
	}


	/**
	 * Note this considers all dates.
	 * @param parentCategory
	 * @param name
	 * @param date date
	 * @return
	 */
	private GenericCategory findChildCategoryByName(GenericCategory parentCategory, String name) {
		for (Iterator<MutableTimedAssociationKey> iter = parentCategory.getChildrenKeyIterator(); iter.hasNext();) {
			TimedAssociationKey element = iter.next();
			GenericCategory child = getGenericCategory(parentCategory.getType(), element.getAssociableID());
			if (child.getName().equals(name)) {
				return child;
			}
		}
		return null;
	}

	public DateSynonym findDateSynonym(Date date) {
		if (date == null) return null;
		for (int i = 0; i < allDateSynonyms.size(); i++) {
			DateSynonym ds = allDateSynonyms.get(i);
			if (ds.getDate().equals(date)) {
				return ds;
			}
		}
		return null;
	}

	/**
	 * This considers all dates. 
	 * @param categoryType
	 * @param path
	 * @param date
	 * @return
	 */
	private GenericCategory findFullyQualifiedGenericCategoryByPath(int categoryType, String path) {
		GenericCategory category = null;
		final String[] categoryNodes = path.split(Constants.CATEGORY_PATH_DELIMITER);
		if (categoryNodes != null && categoryNodes.length > 0) {
			category = getGenericCategoryRoot(categoryType);
			if (category != null && category.getName().equals(categoryNodes[0])) {
				for (int i = 1; i < categoryNodes.length; i++) {
					category = findChildCategoryByName(category, categoryNodes[i]);
					if (category == null) {
						break;
					}
				}
			}
			else {
				category = null;
			}
		}
		return category;
	}

	public GenericCategory[] findGenericCategoryByName(GenericEntityType type, String name) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (UtilBase.isEmpty(name)) return null;
		return findGenericCategoryByName(type.getCategoryType(), name);
	}

	private GenericCategory[] findGenericCategoryByName(int categoryType, String name) {
		if (UtilBase.isEmpty(name)) return null;
		if (name.indexOf(Constants.CATEGORY_PATH_DELIMITER) > 0) {
			return new GenericCategory[] { findFullyQualifiedGenericCategoryByPath(categoryType, name) };
		}
		else {
			List<GenericCategory> results = new ArrayList<GenericCategory>();
			List<GenericCategory> list = getCachedGenericCategoryList(new Integer(categoryType));
			for (int i = 0; i < list.size(); i++) {
				GenericCategory element = list.get(i);
				if (element.getName().equals(name)) {
					results.add(element);
				}
			}
			return results.size() == 0 ? null : results.toArray(new GenericCategory[0]);
		}
	}

	public GenericEntity findGenericEntity(GenericEntityType type, String name) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (UtilBase.isEmpty(name)) return null;
		ListModel model = getGenericEntityListModel(type, false);
		if (model != null) {
			for (int i = 0; i < model.getSize(); i++) {
				GenericEntity entity = (GenericEntity) model.getElementAt(i);
				if (entity != null && entity.getName().equals(name)) {
					return entity;
				}
			}
		}
		return null;
	}

	public DateSynonym findNamedDateSynonym(Date date) {
		if (date == null) return null;
		for (int i = 0; i < dateSynonymModel.getSize(); i++) {
			DateSynonym ds = (DateSynonym) dateSynonymModel.getElementAt(i);
			if (ds.getDate().equals(date)) {
				return ds;
			}
		}
		return null;
	}

	private GenericCategory findRootCategory(List<GenericCategory> catList) {
		for (Iterator<GenericCategory> iter = catList.iterator(); iter.hasNext();) {
			GenericCategory element = iter.next();
			if (element.isRoot()) return element;
		}
		return catList.get(0);
	}

	public TemplateTreeNode findTemplateTreeNode(TemplateUsageType usageType, int templateID) {
		UsageTypeTreeNode usageNode = findUsageTypeTreeNode(usageType);
		if (usageNode != null) {
			return findTemplateTreeNode(usageNode, templateID);
		}
		return null;
	}

	public UsageTypeTreeNode findUsageTypeSearchTreeNode(TemplateUsageType usageType) {
		return usageSearchTreeNodeMap.get(usageType);
	}

	public UsageTypeTreeNode findUsageTypeTreeNode(TemplateUsageType usageType) {
		return usageTreeNodeMap.get(usageType);
	}

	private void fireEntityDeleted(EntityDeleteEvent e) {
		synchronized (entityDeleteListenerList) {
			for (int i = 0; i < entityDeleteListenerList.size(); i++) {
				entityDeleteListenerList.get(i).entityDeleted(e);
			}
		}
	}

	private void fireEntityDeleted(GenericEntity entity) {
		fireEntityDeleted(new EntityDeleteEvent(entity));
	}

	public List<DateSynonym> getAllDateSynonyms() {
		return Collections.unmodifiableList(allDateSynonyms);
	}

	public List<TypeEnumValue> getAllEnumValues(String typeKey) {
		return Collections.unmodifiableList(getEnumValueList(typeKey));
	}

	public List<GridTemplate> getAllGuidelineTemplates() {
		return Collections.unmodifiableList(cachedTemplateList);
	}

	public List<ParameterTemplate> getAllParameterTemplates() {
		List<ParameterTemplate> templates = new ArrayList<ParameterTemplate>();
		for (int i = 0; i < paramTemplateModel.getSize(); i++) {
			templates.add((ParameterTemplate) paramTemplateModel.getElementAt(i));
		}
		return Collections.unmodifiableList(templates);
	}

	public List<Role> getAllRoles() {
		return cacheInstance.getAllRoles();
	}

	public List<UserData> getAllUsers() {
		return cacheInstance.lookupUserData();
	}

	public TreeModel getAttributeTreeModel() {
		if (attributeTreeModel == null) {
			try {
				DomainModel.initInstance();
				RootTreeNode rootNode = new RootTreeNode(null);
				for (Iterator<DomainClass> iter = DomainModel.getInstance().getDomainClasses().iterator(); iter.hasNext();) {
					DomainClass dc = iter.next();
					if (isEnabledForUISelection(dc)) {
						rootNode.addChild(new DomainWithAttributeTreeNode(dc, rootNode), true);
					}
				}
				attributeTreeModel = new DefaultTreeModel(rootNode);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
		return attributeTreeModel;
	}

	public TreeModel getAttributeTreeModel(int[] genericDataTypes) {
		if (genericDataTypes == null || genericDataTypes.length == 0) return getAttributeTreeModel();
		try {
			DomainModel.initInstance();
			RootTreeNode rootNode = new RootTreeNode(null);
			for (Iterator<DomainClass> iter = DomainModel.getInstance().getDomainClasses().iterator(); iter.hasNext();) {
				DomainClass dc = iter.next();
				if (isEnabledForUISelection(dc)) {
					rootNode.addChild(new DomainWithAttributeTreeNode(dc, rootNode, genericDataTypes), true);
				}
			}
			return new DefaultTreeModel(rootNode);
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
		return null;
	}

	private List<GenericCategory> getCachedGenericCategoryList(Integer categoryTypeKey) {
		if (cachedGenericCategoryMap.containsKey(categoryTypeKey)) {
			return cachedGenericCategoryMap.get(categoryTypeKey);
		}
		else {
			List<GenericCategory> list = new ArrayList<GenericCategory>();
			cachedGenericCategoryMap.put(categoryTypeKey, list);
			return list;
		}
	}

	private List<DatedCategoryTreeModel> getCachedGenericCategoryTreeModelList(Integer categoryTypeKey) {
		if (cachedGenericCategoryTreeModelMap.containsKey(categoryTypeKey)) {
			return cachedGenericCategoryTreeModelMap.get(categoryTypeKey);
		}
		else {
			List<DatedCategoryTreeModel> list = new ArrayList<DatedCategoryTreeModel>();
			cachedGenericCategoryTreeModelMap.put(categoryTypeKey, list);
			return list;
		}
	}

	private GridTemplate getCachedTemplate(int id) {
		for (GridTemplate template : cachedTemplateList) {
			if (template.getID() == id) return template;
		}
		return null;
	}


	/**
	 * @param category
	 * @return A list of entity to category associations for the given category.
	 */
	public List<CategoryToEntityAssociationData> getCategoryToEntityAssociationsByCategory(GenericCategory category) {
		List<CategoryToEntityAssociationData> result = new ArrayList<CategoryToEntityAssociationData>();
		try {
			GenericEntityByCategoryFilter filter = new GenericEntityByCategoryFilter(GenericEntityType.forCategoryType(category.getType()), new int[] { category.getId() }, null, false);
			List<GenericEntity> entities = ClientUtil.getCommunicator().search(filter);
			if (entities != null && !entities.isEmpty()) {
				Collections.sort(entities, new IDNameObjectComparator<GenericEntity>());
				for (Iterator<GenericEntity> iter = entities.iterator(); iter.hasNext();) {
					GenericEntity entity = iter.next();
					List<MutableTimedAssociationKey> keys = entity.getCategoryAssociations(category.getId());
					if (keys != null) {
						for (Iterator<MutableTimedAssociationKey> i = keys.iterator(); i.hasNext();) {
							MutableTimedAssociationKey key = i.next();
							CategoryToEntityAssociationData data = new CategoryToEntityAssociationData(entity, key);
							result.add(data);
						}
					}
				}
			}
		}
		catch (Exception e) {
			ClientUtil.handleRuntimeException(e);
		}
		return result;
	}

	public TreeModel getClassTreeModel() {
		if (domainClassTreeModel == null) {
			try {
				DomainModel.initInstance();
				RootTreeNode rootNode = new RootTreeNode(null);
				for (Iterator<DomainClass> iter = DomainModel.getInstance().getDomainClasses().iterator(); iter.hasNext();) {
					DomainClass dc = iter.next();
					rootNode.addChild(new DomainTreeFlatNode(dc, rootNode), true);
				}

				domainClassTreeModel = new DefaultTreeModel(rootNode);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
		return domainClassTreeModel;
	}

	public ComboBoxModel getDateSynonymComboModel(boolean hasEmpty) {
		return (hasEmpty ? dateSynonymWithEmptyModel : dateSynonymModel);
	}

	private List<TypeEnumValue> getEnumValueList(String typeKey) {
		synchronized (cachedTypeEnumValueMap) {
			if (!cachedTypeEnumValueMap.containsKey(typeKey)) {
				cachedTypeEnumValueMap.put(typeKey, new ArrayList<TypeEnumValue>());
			}
			return cachedTypeEnumValueMap.get(typeKey);
		}
	}

	public TreeModel getFlatClassTreeModel() {
		if (flatDomainClassTreeModel == null) {
			try {
				DomainModel.initInstance();
				RootTreeNode rootNode = new RootTreeNode(null);
				for (Iterator<DomainClass> iter = DomainModel.getInstance().getDomainClasses().iterator(); iter.hasNext();) {
					DomainClass dc = iter.next();
					rootNode.addChild(new DomainTreeFlatNode(dc, rootNode), true);
				}

				flatDomainClassTreeModel = new DefaultTreeModel(rootNode);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
		return flatDomainClassTreeModel;

	}

	public GenericCategory getGenericCategory(GenericEntityType type, int categoryID) {
		return getGenericCategory(type.getCategoryType(), categoryID);
	}

	public GenericCategory getGenericCategory(int categoryType, int categoryID) {
		List<GenericCategory> list = getCachedGenericCategoryList(new Integer(categoryType));
		for (int i = 0; i < list.size(); i++) {
			GenericCategory element = list.get(i);
			if (element.getID() == categoryID) {
				return element;
			}
		}
		return null;
	}

	public int getGenericCategoryCount(int categoryType) {
		return getCachedGenericCategoryList(new Integer(categoryType)).size();
	}

	public String getGenericCategoryName(GenericEntityType type, int id, String defaultValue) {
		GenericCategory category = getGenericCategory(type.getCategoryType(), id);
		return (category == null ? defaultValue : category.getName());
	}

	private GenericCategory getGenericCategoryRoot(int categoryType) {
		List<GenericCategory> list = getCachedGenericCategoryList(new Integer(categoryType));
		for (int i = 0; i < list.size(); i++) {
			GenericCategory category = list.get(i);
			if (category.isRoot()) {
				return category;
			}
		}
		return null;
	}

	public List<GenericEntity> getGenericEntitiesInCategory(GenericEntityType entityType, int categoryID, Date date, boolean includeDescendents) throws ServerException {
		GenericEntityByCategoryFilter filter = new GenericEntityByCategoryFilter(entityType, new int[] { categoryID }, date, includeDescendents);
		List<GenericEntity> result = ClientUtil.getCommunicator().search(filter);
		return result;
	}

	/**
	 * @param type
	 * @param id
	 * @return GenericEntity with <code>id</code>, if found;
	 *         <code>null</code>, otherwise
	 * @since 3.0.0
	 */
	public GenericEntity getGenericEntity(GenericEntityType type, int id) {
		ListModel model = getGenericEntityListModel(type, false);
		if (model != null) {
			for (int i = 0; i < model.getSize(); i++) {
				GenericEntity entity = (GenericEntity) model.getElementAt(i);
				if (entity != null && entity.getID() == id) {
					return entity;
				}
			}
		}
		return null;
	}

	public ComboBoxModel getGenericEntityComboModel(GenericEntityType type, boolean hasEmpty) {
		return getGenericEntityModel_aux(type, hasEmpty);
	}

	public ListModel getGenericEntityListModel(GenericEntityType type, boolean hasEmpty) {
		return getGenericEntityModel_aux(type, hasEmpty);
	}

	/**
	 * @param type
	 * @return A map containing the entity ID as the key and the list
	 *         position as the value
	 *         
	 * @since 5.0.0
	 */
	public Map<Integer, Integer> getGenericEntityListModelMap(GenericEntityType type) {
		ListModel model = getGenericEntityListModel(type, false);
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		if (model != null) {
			for (int i = 0; i < model.getSize(); i++) {
				GenericEntity entity = (GenericEntity) model.getElementAt(i);
				map.put(new Integer(entity.getID()), new Integer(i));
			}
		}
		return map;
	}

	/**
	 * Returns ComboBox model for the specified generic entity type. This
	 * returns chanel, investor, and product model instances for genric entity
	 * types for them -- this logic should be eliminated when channel, investor,
	 * and product are converted to generic entities.
	 * 
	 * @param type
	 * @param hasEmpty
	 * @return the combobox model
	 */
	private ComboBoxModel getGenericEntityModel_aux(GenericEntityType type, boolean hasEmpty) {
		ComboBoxModel model = (ComboBoxModel) (hasEmpty ? genericEntityWithEmptyComboModelMap.get(type) : genericEntityComboModelMap.get(type));
		if (model == null) {
			model = new DefaultComboBoxModel();
			if (hasEmpty) {
				genericEntityWithEmptyComboModelMap.put(type, model);
			}
			else {
				genericEntityComboModelMap.put(type, model);
			}
		}
		return model;
	}

	/**
	 * @param type
	 * @param id
	 * @param defaultValue
	 * @return String
	 * @since 3.0.0
	 */
	public String getGenericEntityName(GenericEntityType type, int id, String defaultValue) {
		if (type == null) throw new NullPointerException("type cannot be null");
		return getGenericEntityName_internal(type, id, defaultValue);
	}

	private String getGenericEntityName_internal(GenericEntityType type, int id, String defaultValue) {
		ListModel model = getGenericEntityListModel(type, false);
		if (model != null) {
			for (int i = 0; i < model.getSize(); i++) {
				GenericEntity entity = (GenericEntity) model.getElementAt(i);
				if (entity != null && entity.getID() == id) {
					return entity.getName();
				}
			}
		}
		return defaultValue;
	}

	public DefaultTreeModel getGuidelineTemplateSearchTreeModel() {
		return guidelineTemplateSearchTreeModel;
	}

	public DefaultTreeModel getGuidelineTemplateTreeModel() {
		return guidelineTemplateTreeModel;
	}

	/**
	 * Return the most recent fully qualified category name. 
	 * @param categoryType
	 * @return fully qualified name.
	 */
	public String getMostRecentFullyQualifiedCategoryName(GenericCategory category) {
		String fullyQualifiedName = null;
		if (category.isRoot()) {
			fullyQualifiedName = category.getName();
		}
		else {
			List<GenericCategory> path = getMostRecentPathToRoot(category);
			for (Iterator<GenericCategory> i = path.iterator(); i.hasNext();) {
				GenericCategory pathCat = i.next();
				fullyQualifiedName = fullyQualifiedName == null ? pathCat.getName() : fullyQualifiedName + Constants.CATEGORY_PATH_DELIMITER + pathCat.getName();
			}
		}
		return fullyQualifiedName;
	}

	private List<GenericCategory> getMostRecentPathToRoot(GenericCategory category) {
		List<GenericCategory> path = new ArrayList<GenericCategory>();
		if (category.isRoot()) {
			path.add(0, category);
		}
		else {
			path = getMostRecentPathToRoot(category, path);
		}
		return path;
	}

	private List<GenericCategory> getMostRecentPathToRoot(GenericCategory child, List<GenericCategory> path) {
		path.add(0, child);
		if (!child.isRoot()) {
			List<MutableTimedAssociationKey> parentKeys = child.getAllParentAssociations();
			Collections.sort(parentKeys, TimedAssociationKeyComparator.getInstance());
			TimedAssociationKey parentKey = parentKeys.get(0);
			GenericCategory parent = getGenericCategory(child.getType(), parentKey.getAssociableID());
			path = getMostRecentPathToRoot(parent, path);
		}
		return path;
	}

	public ComboBoxModel getParameterTemplateComboModel(boolean hasEmpty) {
		return (hasEmpty ? paramTemplateWithEmptyModel : paramTemplateModel);
	}

	public ListModel getPrivilegeListModel() {
		return privilegeModel;
	}

	public ListModel getRoleListModel() {
		return roleModel;
	}

	public TemplateIDNameTableModel getTemplateIDNameTableModel() {
		return templateTableModel;
	}

	/**
	 * Creates a new type enum combo model for the specified type key.
	 * The model returned by this is not cached. 
	 * A new one is created on each call.
	 * @param typeKey the type key
	 * @param hasEmpty if <code>true</code>, the returned combo has an empty string as the first item
	 * @param sortValues if <code>true</code>, the items in the combo are sorted in alphabetical order
	 * @return the type enum combo model for <code>typeKey</code>
	 */
	public ComboBoxModel getTypeEnumComboModel(String typeKey, boolean hasEmpty, boolean sortValues) {
		List<TypeEnumValue> enumValueList = getEnumValueList(typeKey);
		if (sortValues) Collections.sort(enumValueList, TypeEnumValueComparator.getInstance());
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		if (hasEmpty) model.addElement(" ");
		for (Iterator<TypeEnumValue> iter = enumValueList.iterator(); iter.hasNext();) {
			TypeEnumValue element = iter.next();
			model.addElement(element);
		}
		return model;
	}

	public ComboBoxModel getTypeEnumComboModelForDomainAttribute(String attributeMap, boolean hasEmpty, boolean sortValues) {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		DomainAttribute da = DomainModel.getInstance().getDomainAttribute(attributeMap);
		if (da != null) {
			EnumValue[] enumValues = da.getEnumValues();
			List<TypeEnumValue> enumValueList = new ArrayList<TypeEnumValue>();
			for (int i = 0; i < enumValues.length; i++) {
				if (enumValues[i].isActive()) {
					TypeEnumValue typeEnumValue = new TypeEnumValue(enumValues[i].getDeployID().intValue(), enumValues[i].getDeployValue(), enumValues[i].getDisplayLabel());
					enumValueList.add(typeEnumValue);
				}
			}

			if (sortValues) Collections.sort(enumValueList, TypeEnumValueComparator.getInstance());
			if (hasEmpty) model.addElement(" ");
			for (Iterator<TypeEnumValue> iter = enumValueList.iterator(); iter.hasNext();) {
				TypeEnumValue element = iter.next();
				model.addElement(element);
			}
		}
		return model;
	}

	public ComboBoxModel getUsageTypeModel() {
		if (usageTypeModel == null) {
			TemplateUsageType[] usageTypes = TemplateUsageType.getAllInstances();
			Arrays.sort(usageTypes, UsageTypeComparator.getInstance());
			usageTypeModel = new DefaultComboBoxModel(usageTypes);
		}
		return usageTypeModel;
	}

	private UsageTypeTreeNode getUsageTypeSearchTreeNode(TreeNode parent, TemplateUsageType usageType) {
		if (usageSearchTreeNodeMap.containsKey(usageType)) {
			return usageSearchTreeNodeMap.get(usageType);
		}
		else {
			UsageTypeTreeNode node = new UsageTypeTreeNode(parent, usageType);
			usageSearchTreeNodeMap.put(usageType, node);
			return node;
		}
	}

	private UsageTypeTreeNode getUsageTypeTreeNode(TreeNode parent, TemplateUsageType usageType) {
		if (usageTreeNodeMap.containsKey(usageType)) {
			return usageTreeNodeMap.get(usageType);
		}
		else {
			UsageTypeTreeNode node = new UsageTypeTreeNode(parent, usageType);
			usageTreeNodeMap.put(usageType, node);
			return node;
		}
	}

	/**
	 * @param checkForCategory check using either the category name or the entity name.
	 * @param entityType entity type
	 * @param name category or entity name
	 * @return <code>true</code> if there is a duplicated name for category or entity based on the checkForCategory
	 */
	// TT 2100
	public boolean hasDuplicatedCategoryOrEntityName(boolean checkForCategory, GenericEntityType entityType, String name) {
		GenericCategory[] categories = findGenericCategoryByName(entityType, name);
		List<GenericEntity> entities = findAllGenericEntities(entityType, name);

		// check if the inputed category name has duplicated name with another category or an entity
		if (checkForCategory) {
			if ((categories != null && categories.length > 1) || (entities != null && entities.size() > 0)) {
				return true;
			}
		}
		// check if the inputed entity name has duplicated name with another entity or an category
		else {
			if ((categories != null && categories.length > 0) || (entities != null && entities.size() > 1)) {
				return true;
			}
		}

		return false;
	}

	public void insertNamedDateSynonym(DateSynonym ds) {
		insertNamedDateSynonym(ds, dateSynonymModel);
		insertNamedDateSynonym(ds, dateSynonymWithEmptyModel);
		synchronized (dateSynonymModelList) {
			for (Iterator<DefaultComboBoxModel> iter = dateSynonymModelList.iterator(); iter.hasNext();) {
				DefaultComboBoxModel element = iter.next();
				insertNamedDateSynonym(ds, element);
			}
		}
		synchronized (dateSynonymWithEmptyModelList) {
			for (Iterator<DefaultComboBoxModel> iter = dateSynonymWithEmptyModelList.iterator(); iter.hasNext();) {
				DefaultComboBoxModel element = iter.next();
				insertNamedDateSynonym(ds, element);
			}
		}
	}

	public void insertNamedDateSynonym(DateSynonym ds, DefaultComboBoxModel model) {
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof DateSynonym) {
				DateSynonym ds2 = (DateSynonym) model.getElementAt(i);
				if (ds.getName().compareTo(ds2.getName()) <= 0) {
					model.insertElementAt(ds, i);
					return;
				}
			}
		}
		model.addElement(ds);
	}

	private boolean isDescendentAtAnyTime(int categoryID, GenericCategory category) {
		for (Iterator<MutableTimedAssociationKey> iter = category.getChildrenKeyIterator(); iter.hasNext();) {
			MutableTimedAssociationKey key = iter.next();
			GenericCategory child = getGenericCategory(category.getType(), key.getAssociableID());
			if (child.getID() == categoryID) {
				return true;
			}
			else if (isDescendentAtAnyTime(categoryID, child)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if the specified childID is a chid of all the specified parent ids at any point in time
	 * @param childID the child id
	 * @param parentIDs parent ids
	 * @param categoryType category type
	 * @param date
	 * @return <code>true</code> if childID is a child of all parentIDs
	 */
	public boolean isDescendentAtAnyTime(int childID, int parentIDs[], int categoryType) {
		for (int i = 0; i < parentIDs.length; i++) {
			int parentID = parentIDs[i];
			GenericCategory category = getGenericCategory(categoryType, parentID);
			if (!isDescendentAtAnyTime(childID, category)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Keeping track of whether roles list in RoleTabpanel needs to be reloaded because
	 * user hit the reload button on manage users screen and reloaded users and roles
	 * from server
	 * @author vineet khosla
	 * @since PowerEditor 5.0
	 * @return relodRolesInRoleTabpanel
	 */
	public boolean isRelodRolesInRoleTabpanel() {
		return relodRolesInRoleTabpanel;
	}

	public void move(GridTemplate template, TemplateUsageType oldUsage) {
		remove_internal(oldUsage, template);
		add(template);
	}

	public void reloadAllDateSynonyms() throws ServerException {
		allDateSynonyms.clear();
		DateSynonym[] dateSynonyms = ClientUtil.fetchAllDateSynonyms();
		allDateSynonyms.addAll(Arrays.asList(dateSynonyms));
		resortNamedDateSynonyms(dateSynonyms);
	}

	/**
	 * Refresh all cached data.
	 * @throws ServerException on error while communicating with the server
	 */
	public void reloadCache() throws ServerException {
		ClientUtil.getLogger().info(">>> EntityModelCaceh.reloadEntities");
		cacheInstance.clearCache();
		genericEntityComboModelCopyMap.clear();

		attributeTreeModel = null;

		reloadNamedDateSynonyms();
		reloadAllDateSynonyms();

		cacheTemplates();

		templateTableModel.setDataList(cachedSearchTemplateList);

		buildGuidelineTemplateTreeModel();// template tree used in Manage templates tab
		buildGuidelineTemplateSearchTreeModel(); // template tree used in search policies tab

		// cache all generic entities
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		ClientUtil.getLogger().info("caching " + types.length + " generic entity types");

		for (int i = 0; i < types.length; i++) {
			ClientUtil.getLogger().info("caching generic entities for " + types[i]);
			List<GenericEntity> genericEntityList = cacheInstance.lookup(types[i]);
			GenericEntity[] entities = genericEntityList.toArray(new GenericEntity[0]);
			Arrays.sort(entities, new IDNameObjectComparator<GenericEntity>());

			ClientUtil.getLogger().info("generic entities loaded: " + entities.length);

			DefaultComboBoxModel comboModel = null;
			if (genericEntityComboModelMap.containsKey(types[i])) {
				comboModel = (DefaultComboBoxModel) genericEntityComboModelMap.get(types[i]);
				comboModel.removeAllElements();
			}
			else {
				comboModel = new DefaultComboBoxModel();
				genericEntityComboModelMap.put(types[i], comboModel);
			}
			for (int j = 0; j < entities.length; j++) {
				comboModel.addElement(entities[j]);
			}

			DefaultComboBoxModel comboWithEmptyModel = null;
			if (genericEntityWithEmptyComboModelMap.containsKey(types[i])) {
				comboWithEmptyModel = (DefaultComboBoxModel) genericEntityWithEmptyComboModelMap.get(types[i]);
				comboWithEmptyModel.removeAllElements();
			}
			else {
				comboWithEmptyModel = new DefaultComboBoxModel();
				genericEntityWithEmptyComboModelMap.put(types[i], comboWithEmptyModel);
			}
			comboWithEmptyModel.addElement("  ");
			for (int j = 0; j < entities.length; j++) {
				comboWithEmptyModel.addElement(entities[j]);
			}
		}

		// cache generic categories
		cachedGenericCategoryMap.clear();
		for (int i = 0; i < types.length; i++) {
			CategoryType categoryDef = ClientUtil.getEntityConfigHelper().getCategoryDefinition(types[i]);
			if (categoryDef != null) {
				List<GenericCategory> categoryList = cacheInstance.lookupCategory(categoryDef.getTypeID().intValue());
				reloadGenericCategories(new Integer(categoryDef.getTypeID().intValue()), categoryList);
			}
		}

		// parameter templates
		try {
			List<ParameterTemplate> list = ClientUtil.getCommunicator().search(new AllSearchFilter<ParameterTemplate>(PeDataType.PARAMETER_TEMPLATE));
			paramTemplateModel.removeAllElements();
			paramTemplateWithEmptyModel.removeAllElements();
			paramTemplateWithEmptyModel.addElement("Any");
			if (list != null) {
				ParameterTemplate[] templates = list.toArray(new ParameterTemplate[0]);
				Arrays.sort(templates, new IDNameObjectComparator<ParameterTemplate>());
				for (int i = 0; i < templates.length; i++) {
					paramTemplateModel.addElement(templates[i]);
					paramTemplateWithEmptyModel.addElement(templates[i]);
				}
			}
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}

		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_USERS) || ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_ROLES)) {
			privilegeModel.removeAllElements();
			List<Privilege> privList = cacheInstance.lookupPrivileges(true);
			Privilege[] privileges = privList.toArray(new Privilege[0]);
			Arrays.sort(privileges);
			for (int i = 0; i < privileges.length; i++) {
				privilegeModel.addElement(privileges[i]);
			}
		}
		reloadRolesFromServer();
	}

	private void reloadGenericCategories(Integer categoryTypeKey, List<GenericCategory> catList) {
		List<GenericCategory> cachedCategoryList = getCachedGenericCategoryList(categoryTypeKey);
		cachedCategoryList.clear();
		cachedCategoryList.addAll(catList);

		GenericCategory rootCategory = findRootCategory(catList);
		List<DatedCategoryTreeModel> modelsForCategoryType = getCachedGenericCategoryTreeModelList(categoryTypeKey);
		for (Iterator<DatedCategoryTreeModel> categoryTypeModelsIter = modelsForCategoryType.iterator(); categoryTypeModelsIter.hasNext();) {
			DatedCategoryTreeModel model = categoryTypeModelsIter.next();
			model.rebuild(rootCategory);
		}
	}

	public void reloadGuidelineTemplateTreeModel() {
		buildGuidelineTemplateTreeModel();
	}

	private void reloadNamedDateSynonyms() throws ServerException {
		resortNamedDateSynonyms(ClientUtil.fetchAllNamedDateSynonyms());
	}

	/**
	 * Reloads all roles from DB.
	 * @author vineet khosla
	 * @since PowerEditor 5.0
	 */
	public void reloadRolesFromServer() {
		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_USERS) || ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_ROLES)) {
			roleModel.removeAllElements();
			List<Role> roleList = cacheInstance.lookupRoles(true);
			Role[] roles = roleList.toArray(new Role[0]);
			Arrays.sort(roles);
			for (int i = 0; i < roles.length; i++) {
				roleModel.addElement(roles[i]);
			}
			setRelodRolesInRoleTabpanel(true); //letting RoleTabPanel know that it needs to reload roles list
		}
	}

	public void remove(GenericEntity entity) {
		remove_helper(entity, (DefaultComboBoxModel) getGenericEntityComboModel(entity.getType(), false));
		remove_helper(entity, (DefaultComboBoxModel) getGenericEntityComboModel(entity.getType(), true));

		remove_helper(entity, genericEntityComboModelCopyMap.get(entity.getType()));
		remove_helper(entity, genericEntityWithEmptyComboModelCopyMap.get(entity.getType()));

		// TT 1760: remove from all category tree models
		if (entity.getType().hasCategory()) {
			Integer key = new Integer(entity.getType().getCategoryType());
			List<GenericCategory> categoryList = getCachedGenericCategoryList(key);
			synchronized (categoryList) {
				for (Iterator<DatedCategoryTreeModel> iter = getCachedGenericCategoryTreeModelList(key).iterator(); iter.hasNext();) {
					DatedCategoryTreeModel element = iter.next();
					element.removeGenericEntity(entity);
				}
			}
		}
		fireEntityDeleted(entity);
	}

	public void remove(GridTemplate template) {
		if (template != null) {
			remove_internal(template.getUsageType(), template);
			cachedTemplateList.remove(template);
			cachedSearchTemplateList.remove(template);
		}
	}

	private void remove_helper(GenericEntity entity, DefaultComboBoxModel model) {
		model.removeElement(entity);
	}

	private void remove_helper(GenericEntity entity, List<DefaultComboBoxModel> comboModelList) {
		if (comboModelList != null) {
			for (int i = 0; i < comboModelList.size(); i++) {
				remove_helper(entity, comboModelList.get(i));
			}
		}
	}

	private void remove_internal(TemplateUsageType usageType, GridTemplate template) {
		UsageTypeTreeNode usageNode = findUsageTypeTreeNode(usageType);
		if (usageNode != null) {
			removeAndNotifyTemplateNodeIfFound(guidelineTemplateTreeModel, usageNode, template);
			synchronized (guidelineTemplateTreeModelList) {
				for (DefaultTreeModel defaultTreeModel : guidelineTemplateTreeModelList) {
					usageNode = findUsageTypeTreeNodeFromModel(defaultTreeModel, template.getUsageType());
					if (usageNode != null) {
						removeAndNotifyTemplateNodeIfFound(defaultTreeModel, usageNode, template);
					}
				}
			}
		}
		usageNode = findUsageTypeSearchTreeNode(template.getUsageType());
		if (usageNode != null) {
			removeAndNotifyTemplateNodeIfFound(guidelineTemplateSearchTreeModel, usageNode, template);
		}
		templateTableModel.removeData(template);
	}

	public void removeDateSynonym(DateSynonym ds) {
		if (ds.isNamed()) removeNamedDateSynonym(ds);
		allDateSynonyms.remove(ds);
	}

	// TT 2021
	public void removeEntityCategoryAssociation(GenericCategory category) {
		GenericEntityType type = GenericEntityType.forCategoryType(category.getType());
		DefaultComboBoxModel model = (DefaultComboBoxModel) genericEntityComboModelMap.get(type);

		for (int i = 0; i < model.getSize(); i++) {
			GenericEntity entity = (GenericEntity) model.getElementAt(i);
			entity.removeAllCategoryAssociations(category.getId());
		}
	}

	public void removeEntityDeleteListener(EntityDeleteListener listener) {
		synchronized (entityDeleteListenerList) {
			if (entityDeleteListenerList.contains(listener)) entityDeleteListenerList.remove(listener);
		}
	}

	public void removeGenericCategory(GenericCategory category) {
		Integer key = new Integer(category.getType());
		List<GenericCategory> categoryList = getCachedGenericCategoryList(key);
		synchronized (categoryList) {
			if (!category.isRoot()) {
				for (Iterator<DatedCategoryTreeModel> iter = getCachedGenericCategoryTreeModelList(key).iterator(); iter.hasNext();) {
					DatedCategoryTreeModel element = iter.next();
					element.removeGenericCategory(category);
				}
			}
			categoryList.remove(category);
		}
	}

	public void removeNamedDateSynonym(DateSynonym ds) {
		dateSynonymModel.removeElement(ds);
		dateSynonymWithEmptyModel.removeElement(ds);
		synchronized (dateSynonymModelList) {
			for (Iterator<DefaultComboBoxModel> iter = dateSynonymModelList.iterator(); iter.hasNext();) {
				DefaultComboBoxModel element = iter.next();
				element.removeElement(ds);
			}
		}
		synchronized (dateSynonymWithEmptyModelList) {
			for (Iterator<DefaultComboBoxModel> iter = dateSynonymWithEmptyModelList.iterator(); iter.hasNext();) {
				DefaultComboBoxModel element = iter.next();
				element.removeElement(ds);
			}
		}
	}

	/**
	 * Removes a role from client cache and also from role list that is used to display roles
	 * on Manage users screen
	 * @author vineet khosla
	 * @since PowerEditor 5.0
	 * @param role to delete
	 */
	public void removeRole(Role role) {
		cacheInstance.removeFromRoleList(role);
		roleModel.removeElement(role);
	}

	private void resortNamedDateSynonyms(DateSynonym[] dateSynonyms) {
		// sort by name by default
		Arrays.sort(dateSynonyms, DateSynonymComparator.getInstance());
		dateSynonymModel.removeAllElements();
		dateSynonymWithEmptyModel.removeAllElements();
		dateSynonymWithEmptyModel.addElement("");
		for (int i = 0; i < dateSynonyms.length; i++) {
			dateSynonymModel.addElement(dateSynonyms[i]);
			dateSynonymWithEmptyModel.addElement(dateSynonyms[i]);
		}

		synchronized (dateSynonymModelList) {
			for (Iterator<DefaultComboBoxModel> iter = dateSynonymModelList.iterator(); iter.hasNext();) {
				DefaultComboBoxModel element = iter.next();
				element.removeAllElements();
				for (int i = 0; i < dateSynonyms.length; i++) {
					element.addElement(dateSynonyms[i]);
				}
			}
		}
		synchronized (dateSynonymWithEmptyModelList) {
			for (Iterator<DefaultComboBoxModel> iter = dateSynonymWithEmptyModelList.iterator(); iter.hasNext();) {
				DefaultComboBoxModel element = iter.next();
				element.removeAllElements();
				element.addElement("");
				for (int i = 0; i < dateSynonyms.length; i++) {
					element.addElement(dateSynonyms[i]);
				}
			}
		}
	}

	void setCacheTypeEnumValueMap(Map<String, List<TypeEnumValue>> map) {
		this.cachedTypeEnumValueMap.clear();
		if (map != null) {
			this.cachedTypeEnumValueMap.putAll(map);
		}
	}

	/**
	 * Keeping track of whether roles list in RoleTabpanel needs to be reloaded because
	 * user hit the reload button on manage users screen and reloaded users and roles
	 * from server
	 * @author vineet khosla
	 * @since PowerEditor 5.0
	 * @param relodRolesInRoleTabpanel
	 */
	public void setRelodRolesInRoleTabpanel(boolean relodRolesInRoleTabpanel) {
		this.relodRolesInRoleTabpanel = relodRolesInRoleTabpanel;
	}


	public String toGenericCategoryIDStr(int categoryType, List<Integer> categoryIDList) {
		StringBuilder buff = new StringBuilder();
		if (categoryIDList != null) {
			for (Iterator<Integer> iter = categoryIDList.iterator(); iter.hasNext();) {
				Integer element = iter.next();
				GenericCategory cat = getGenericCategory(categoryType, element.intValue());
				buff.append((cat == null ? element.toString() : cat.getName()));
				if (iter.hasNext()) {
					buff.append(",");
				}
			}
		}
		return buff.toString();
	}

	// TT 2072
	public void update(GenericEntity entity) {
		if (entity != null) {
			DefaultComboBoxModel model = (DefaultComboBoxModel) getGenericEntityComboModel(entity.getType(), false);
			GenericEntity[] entities = new GenericEntity[model.getSize()];
			for (int i = 0; i < entities.length; i++) {
				GenericEntity currEntity = (GenericEntity) model.getElementAt(i);
				// replace existing one
				if (currEntity.getId() == entity.getId())
					entities[i] = entity;
				else
					entities[i] = currEntity;
			}
			Arrays.sort(entities, new IDNameObjectComparator<GenericEntity>());
			add_helper(entities, false, model);
			add_helper(entities, true, (DefaultComboBoxModel) getGenericEntityComboModel(entity.getType(), true));

			add_helper(entities, false, genericEntityComboModelCopyMap.get(entity.getType()));
			add_helper(entities, true, genericEntityWithEmptyComboModelCopyMap.get(entity.getType()));

		}
	}

	public void updateCategoryAssociationDateSynonyms(DateSynonym ds) {
		for (List<GenericCategory> GenericCategoryList : cachedGenericCategoryMap.values()) {
			for (int i = 0; i < GenericCategoryList.size(); i++) {
				GenericCategory category = GenericCategoryList.get(i);
				for (Iterator<MutableTimedAssociationKey> j = category.getParentKeyIterator(); j.hasNext();) {
					MutableTimedAssociationKey key = j.next();
					key.updateEffExpDates(ds);
				}
			}
		}
	}

	public void updateEntityAssociationDateSynonyms(DateSynonym ds) {
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < types.length; i++) {
			DefaultComboBoxModel model = (DefaultComboBoxModel) genericEntityComboModelMap.get(types[i]);
			for (int j = 0; j < model.getSize(); j++) {
				GenericEntity entity = (GenericEntity) model.getElementAt(j);
				for (Iterator<MutableTimedAssociationKey> k = entity.getCategoryIterator(); k.hasNext();) {
					MutableTimedAssociationKey key = k.next();
					key.updateEffExpDates(ds);
				}
			}
		}
	}

	public void updateName(GridTemplate template) {
		if (template != null) {
			TemplateTreeNode templateTreeNode = findTemplateTreeNode(template.getUsageType(), template.getID());
			if (templateTreeNode != null) {
				guidelineTemplateTreeModel.nodeChanged(templateTreeNode);
			}
			synchronized (guidelineTemplateTreeModelList) {
				for (DefaultTreeModel defaultTreeModel : guidelineTemplateTreeModelList) {
					UsageTypeTreeNode usageNode = findUsageTypeTreeNodeFromModel(defaultTreeModel, template.getUsageType());
					templateTreeNode = findTemplateTreeNode(usageNode, template.getID());
					if (templateTreeNode != null) {
						guidelineTemplateTreeModel.nodeChanged(templateTreeNode);
					}
				}
			}
			GridTemplate cachedTemplate = getCachedTemplate(template.getID());
			if (cachedTemplate != null) {
				cachedTemplate.setName(template.getName());
			}
			cachedTemplate = templateTableModel.getDataWithID(template.getID());
			if (cachedTemplate != null) {
				cachedTemplate.setName(template.getName());
			}
			templateTableModel.fireTableDataChanged();
		}
	}

}