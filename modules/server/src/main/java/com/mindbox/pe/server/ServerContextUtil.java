package com.mindbox.pe.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.common.ContextUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.model.GenericCategoryIdentity;
import com.mindbox.pe.server.model.GenericEntityIdentity;
import com.mindbox.pe.server.model.GridCellDetail;

public class ServerContextUtil {

	public static void addContext(AbstractGrid<?> grid, GuidelineContext[] context) {
		if (grid == null) throw new NullPointerException("grid cannot be null");
		if (context == null || context.length == 0) return;
		for (int i = 0; i < context.length; i++) {
			// handle category context
			if (context[i].getGenericEntityType() == null) {
				grid.addGenericCategoryIDs(GenericEntityType.forCategoryType(context[i].getGenericCategoryType()), context[i].getIDs());
			}
			else {
				grid.addGenericEntityIDs(context[i].getGenericEntityType(), context[i].getIDs());
			}
		}
	}

	/**
	 * @param context A context containing categories that must be a subset of the list of categories
	 * @param categoryIDs  The categories that must be a superset of the context elements
	 * @param includeParents The superset can contains parents 
	 * @param includeChildren The superset can contains children
	 * @return true in the context elements are all contained in the categoryIDs array 
	 */
	public static boolean categoriesContained(GuidelineContext context, int[] categoryIDs, boolean includeParents, boolean includeChildren) {
		if (categoryIDs.length < 1) {
			return false;
		}
		else if (UtilBase.contains(context.getIDs(), categoryIDs)) {
			return true;
		}
		else {
			if (context.getGenericEntityType() != null) {
				// we have entities
				if (includeParents) {
					//all of the entities in the context must have at least one parent in the category set
					for (int i = 0; i < context.getIDs().length; i++) {
						int entityID = context.getIDs()[i];
						if (!EntityManager.getInstance().isEntityDescendentOfCategorySetAtAnyTime(entityID, categoryIDs, context.getGenericEntityType().getCategoryType())) {
							return false;
						}
					}
					return true;
				} // dont care if includeChildren is selected
			}
			else if (context.getGenericCategoryType() < 1) {
				return false;
			}
			else {
				// we have categories.
				if (includeParents) {
					// if any of the categories is a parent to ALL of the categories in the context                    
					for (int i = 0; i < categoryIDs.length; i++) {
						int categoryID = categoryIDs[i];
						if (EntityManager.getInstance().isParentCategoryAtAnyTime(categoryID, context.getIDs(), context.getGenericCategoryType())) {
							return true;
						}
					}
				}
				if (includeChildren) {
					// if any of the categories is a child of ALL the categories in the context
					for (int i = 0; i < categoryIDs.length; i++) {
						int categoryID = categoryIDs[i];
						if (EntityManager.getInstance().isDescendentAtAnyTime(categoryID, context.getIDs(), context.getGenericCategoryType())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param context A context containing entities that must be a subset of the list of entities
	 * @param entityIDs  The entities that must be a superset of the context elements
	 * @param includeParents The superset can contains parents 
	 * @param includeChildren The superset can contains children
	 * @return true in the context elements are all contained in the entityIDs array 
	 */
	public static boolean entitiesContained(GuidelineContext context, int[] entityIDs, boolean includeParents, boolean includeChildren) {
		if (entityIDs.length < 1) {
			return false;
		}
		else if (UtilBase.contains(context.getIDs(), entityIDs)) {
			return true;
		}
		else {
			if ((!includeChildren && !includeParents) || context.getGenericEntityType() != null) {
				// if context.getGenericEntityType() != null they are all entities selected 
				return false; // we already know that there are ids in the context not in entityIDs
			}
			else if (context.getGenericCategoryType() < 1) {
				return false;
			}
			else if (includeChildren) {
				// one of the entity ids has to be a child of all the categories
				for (int i = 0; i < entityIDs.length; i++) {
					int entityID = entityIDs[i];
					if (EntityManager.getInstance().isEntityDescendentOfCategorySetAtAnyTime(entityID, context.getIDs(), context.getGenericCategoryType())) {
						return true;
					}
				}
			}
		}
		return false;
	}


	public static GenericCategoryIdentity[] extractGenericCategoryIdentities(AbstractGrid<?> grid) {
		List<GenericCategoryIdentity> list = new ArrayList<GenericCategoryIdentity>();
		GenericEntityType[] types = grid.getGenericCategoryEntityTypesInUse();
		for (int i = 0; i < types.length; i++) {
			int[] ids = grid.getGenericCategoryIDs(types[i]);
			for (int j = 0; j < ids.length; j++) {
				list.add(new GenericCategoryIdentity(types[i].getCategoryType(), ids[j]));
			}
		}
		return list.toArray(new GenericCategoryIdentity[0]);
	}

	public static GenericCategoryIdentity[] extractGenericCategoryIdentities(AbstractGrid<?> productGrid, int typeID) {
		List<GenericCategoryIdentity> list = new ArrayList<GenericCategoryIdentity>();
		// check generic entities
		GenericEntityType[] types = productGrid.getGenericCategoryEntityTypesInUse();
		for (int i = 0; i < types.length; i++) {
			if (types[i].getCategoryType() == typeID) {
				int[] ids = productGrid.getGenericCategoryIDs(types[i]);
				for (int j = 0; j < ids.length; j++) {
					list.add(new GenericCategoryIdentity(typeID, ids[j]));
				}
			}
		}
		return list.toArray(new GenericCategoryIdentity[0]);
	}

	public static GenericCategoryIdentity[] extractGenericCategoryIdentities(GuidelineContext[] context) {
		List<GenericCategoryIdentity> list = new ArrayList<GenericCategoryIdentity>();
		for (int i = 0; i < context.length; i++) {
			if (context[i].hasCategoryContext()) {
				int[] ids = context[i].getIDs();
				for (int j = 0; j < ids.length; j++) {
					list.add(new GenericCategoryIdentity(context[i].getGenericCategoryType(), ids[j]));
				}
			}
		}
		return list.toArray(new GenericCategoryIdentity[0]);
	}

	public static GenericEntityIdentity[] extractGenericEntityIdentities(AbstractGrid<?> grid) {
		List<GenericEntityIdentity> list = new ArrayList<GenericEntityIdentity>();
		GenericEntityType[] types = grid.getGenericEntityTypesInUse();
		for (int i = 0; i < types.length; i++) {
			int[] ids = grid.getGenericEntityIDs(types[i]);
			for (int j = 0; j < ids.length; j++) {
				list.add(new GenericEntityIdentity(types[i].getID(), ids[j]));
			}
		}
		return list.toArray(new GenericEntityIdentity[0]);
	}

	/**
	 * @param productGrid productGrid
	 * @param typeID type id
	 * @return identity array
	 * @since 3.0.0
	 */
	public static GenericEntityIdentity[] extractGenericEntityIdentities(AbstractGrid<?> productGrid, int typeID) {
		List<GenericEntityIdentity> list = new ArrayList<GenericEntityIdentity>();
		// check generic entities
		GenericEntityType[] types = productGrid.getGenericEntityTypesInUse();
		for (int i = 0; i < types.length; i++) {
			if (types[i].getID() == typeID) {
				int[] ids = productGrid.getGenericEntityIDs(types[i]);
				for (int j = 0; j < ids.length; j++) {
					list.add(new GenericEntityIdentity(typeID, ids[j]));
				}
			}
		}
		return list.toArray(new GenericEntityIdentity[0]);
	}

	public static GenericEntityIdentity[] extractGenericEntityIdentities(GuidelineContext[] context) {
		List<GenericEntityIdentity> list = new ArrayList<GenericEntityIdentity>();
		for (int i = 0; i < context.length; i++) {
			if (context[i].getGenericEntityType() != null) {
				int[] ids = context[i].getIDs();
				for (int j = 0; j < ids.length; j++) {
					list.add(new GenericEntityIdentity(context[i].getGenericEntityType().getID(), ids[j]));
				}
			}
		}
		return list.toArray(new GenericEntityIdentity[0]);
	}

	/**
	 * @param <C> column type
	 * @param contexts Array of contexts that must be part of a row
	 * @param grid Grid to check rows
	 * @param includeParents True if parents of the context elements should be included
	 * @param includeChildren True if children of the context elements should be included
	 * @param includeEmptyContexts True if empty grid cells should be included
	 * @return List containing the row numbers that match the contexts
	 */
	public static <C extends AbstractTemplateColumn> List<Integer> findMatchingGridRows(GuidelineContext[] contexts, AbstractGrid<C> grid, boolean includeParents,
			boolean includeChildren, boolean includeEmptyContexts) {
		List<Integer> rows = new ArrayList<Integer>();
		List<C> entityCols = grid.getTemplate().getEntityTypeColumns();
		if (entityCols != null && entityCols.size() > 0) {
			// iterate over each grid row
			for (int rowNumber = 1; rowNumber <= grid.getNumRows(); rowNumber++) {
				boolean foundRowMatch = false;
				for (Iterator<C> i = entityCols.iterator(); i.hasNext();) {
					if (foundRowMatch) break;
					C c = i.next();
					// iterate each context and match it with the entity type 
					for (int contextIndex = 0; contextIndex < contexts.length; contextIndex++) {
						GuidelineContext context = (GuidelineContext) contexts[contextIndex];
						if (ServerContextUtil.isSameEntityType(context, c.getColumnDataSpecDigest().getEntityType())) {
							Object cellValue = grid.getCellValue(rowNumber, c.getName());
							boolean isEmptyCellValue = UtilBase.isEmptyCellValue(cellValue);
							if (isEmptyCellValue && includeEmptyContexts) {
								rows.add(new Integer(rowNumber));
								foundRowMatch = true;
							}
							if (!isEmptyCellValue) {
								boolean isNegated = false;
								int[] entityIDs = new int[0];
								int[] categoryIDs = new int[0];
								if (cellValue instanceof CategoryOrEntityValues) {
									CategoryOrEntityValues v = (CategoryOrEntityValues) cellValue;
									entityIDs = v.getEntityIDs();
									categoryIDs = v.getCategoryIDs();
									isNegated = v.isSelectionExclusion();
								}
								else if (cellValue instanceof CategoryOrEntityValue) {
									CategoryOrEntityValue v = (CategoryOrEntityValue) cellValue;
									if (v.isForEntity()) {
										entityIDs = new int[] { v.getId() };
									}
									else {
										categoryIDs = new int[] { v.getId() };
									}
								}
								if ((!isNegated && ServerContextUtil.entitiesContained(context, entityIDs, includeParents, includeChildren))
										|| ServerContextUtil.categoriesContained(context, categoryIDs, includeParents, includeChildren)) {
									rows.add(new Integer(rowNumber));
									foundRowMatch = true;
									break;
								}

							}
						}
					}
				}
			}
		}
		return rows;
	}

	public static <C extends AbstractTemplateColumn> List<GridCellDetail> findReferencedGridCellsForEntities(AbstractGrid<C> grid, GenericEntityType type, int entityID,
			boolean forEntity) {
		List<GridCellDetail> detailList = new ArrayList<GridCellDetail>();
		List<C> entityCols = grid.getTemplate().getEntityTypeColumns();
		if (entityCols != null && entityCols.size() > 0) {
			// iterate over each grid row
			for (int rowNumber = 1; rowNumber <= grid.getNumRows(); rowNumber++) {
				// iterate over EntityList columns
				for (Iterator<C> iter = entityCols.iterator(); iter.hasNext();) {
					C column = iter.next();
					// if entity type matches
					if (column.getColumnDataSpecDigest().getEntityType().equals(type.getName())
							&& ((forEntity && column.getColumnDataSpecDigest().isEntityAllowed()) || (!forEntity && column.getColumnDataSpecDigest().isCategoryAllowed()))) {
						Object cellValue = grid.getCellValue(rowNumber, column.getName());
						boolean add = false;
						if (cellValue instanceof CategoryOrEntityValue) {
							if (((CategoryOrEntityValue) cellValue).isForEntity() == forEntity && ((CategoryOrEntityValue) cellValue).getId() == entityID) {
								add = true;
							}
						}
						else if (cellValue instanceof CategoryOrEntityValues) {
							if (((CategoryOrEntityValues) cellValue).hasID(forEntity, entityID)) {
								add = true;
							}
						}
						// add a grid cell detail
						if (add) {
							GridCellDetail detail = new GridCellDetail();
							detail.setGridID(grid.getID());
							detail.setRowID(rowNumber);
							detail.setColumnName(column.getName());
							detail.setCellValue(cellValue);
							detailList.add(detail);
						}
					}
				}
			}
		}
		return detailList;
	}

	public static boolean hasApplicableContext(AbstractGrid<?> productGrid, GuidelineContext[] context) {
		if (context == null || context.length == 0) {
			return productGrid.isContextEmpty();
		}
		for (int i = 0; i < context.length; i++) {
			if (context[i].getGenericEntityType() != null) {
				int[] idsFromGrid = productGrid.getGenericEntityIDs(context[i].getGenericEntityType());
				if (!UtilBase.isContainedIn(context[i].getIDs(), idsFromGrid)) {
					return false;
				}
			}
			else if (context[i].hasCategoryContext()) {
				if (!productGrid.matchesGenericCategoryIDs(GenericEntityType.forCategoryType(context[i].getGenericCategoryType()), context[i].getIDs())) {
					return false;
				}
			}
		}
		return context.length == productGrid.getGenericCategoryEntityTypesInUse().length + productGrid.getGenericEntityTypesInUse().length;
	}

	public static boolean hasSameContext(AbstractGrid<?> productGrid, GuidelineContext[] context) {
		if (context == null || context.length == 0) {
			return productGrid.isContextEmpty();
		}
		for (int i = 0; i < context.length; i++) {
			if (context[i].getGenericEntityType() != null) {
				if (!productGrid.matchesGenericEntityIDs(context[i].getGenericEntityType(), context[i].getIDs())) {
					return false;
				}
			}
			else if (context[i].hasCategoryContext()) {
				if (!productGrid.matchesGenericCategoryIDs(GenericEntityType.forCategoryType(context[i].getGenericCategoryType()), context[i].getIDs())) {
					return false;
				}
			}
		}
		return context.length == productGrid.getGenericCategoryEntityTypesInUse().length + productGrid.getGenericEntityTypesInUse().length;
	}

	public static boolean isParentCategoryContext(GuidelineContext parentContext, GuidelineContext childContext) {
		return EntityManager.getInstance().isParentCategoriesAtAnyTime(parentContext.getIDs(), childContext.getIDs(), parentContext.getGenericCategoryType());
	}

	/**
	 * 
	 * @param parentContexts The contexts that all must be parents of the filter context.
	 * @param childContexts child contexts
	 * @param includeEmptyContexts true if an empty context should be included 
	 * @return <code>true</code> if context of this filter is contained in <code>contexts</code>; <code>false</code>, otherwise
	 */
	public static boolean isParentContext(GuidelineContext[] parentContexts, GuidelineContext[] childContexts, boolean includeEmptyContexts) {
		boolean contextOfSameTypeFound = false;
		for (int i = 0; i < childContexts.length; i++) {
			GuidelineContext childContext = (GuidelineContext) childContexts[i];
			for (int j = 0; j < parentContexts.length; j++) {
				GuidelineContext parentContext = (GuidelineContext) parentContexts[j];
				if (ContextUtil.contextOfSameType(childContext, parentContext)) {
					contextOfSameTypeFound = true;
					if (includeEmptyContexts && UtilBase.isEmpty(parentContext.getIDs()) || UtilBase.contains(childContext.getIDs(), parentContext.getIDs())) {
						break;
					}
					if (childContext.hasCategoryContext() && parentContext.hasCategoryContext()) {
						// both categories
						if (!ServerContextUtil.isParentCategoryContext(parentContext, childContext)) {
							return false;
						}
					}
					else if (!childContext.hasCategoryContext() && !parentContext.hasCategoryContext()) {
						// both entities and we already know they are not the same
						return false;
					}
					else if (!childContext.hasCategoryContext() && parentContext.hasCategoryContext()) {
						// entities selected in filter, categories part of guideline
						// every category of the grid context must be a parent of the entity
						for (int k = 0; k < childContext.getIDs().length; k++) {
							int entityID = childContext.getIDs()[k];
							if (!EntityManager.getInstance().isEntityDescendentOfCategorySetAtAnyTime(entityID, parentContext.getIDs(), parentContext.getGenericCategoryType())) {
								return false;
							}
						}
					}
					else {
						// categories selected in filter, entities part of guideline
						// in this case since the guideline context are entites they cannot be parents
						return false;
					}
				}
			}
		}
		return contextOfSameTypeFound || includeEmptyContexts;
	}

	/**
	 * @param context context
	 * @param entityTypeName Name of the entity type
	 * @return true if the context is of the same entity type as the entityTypeName
	 */
	public static boolean isSameEntityType(GuidelineContext context, String entityTypeName) {
		GenericEntityType genericType = GenericEntityType.forName(entityTypeName);
		if (genericType != null) {
			if (context.getGenericEntityType() != null) {
				return context.getGenericEntityType().equals(genericType);
			}
			else if (context.getGenericCategoryType() > 0) {
				GenericEntityType type = GenericEntityType.forCategoryType(context.getGenericCategoryType());
				return type != null && type.equals(genericType);
			}
		}
		return false;
	}

	public static void setContext(AbstractGrid<?> grid, GuidelineContext[] context) {
		grid.clearAllContext();
		if (context != null) {
			for (int i = 0; i < context.length; i++) {
				if (context[i].getGenericEntityType() != null) {
					grid.setGenericEntityIDs(context[i].getGenericEntityType(), context[i].getIDs());
				}
				else if (context[i].hasCategoryContext()) {
					grid.setGenericCategoryIDs(GenericEntityType.forCategoryType(context[i].getGenericCategoryType()), context[i].getIDs());
				}
			}
		}
	}

	private ServerContextUtil() {
	}
}
