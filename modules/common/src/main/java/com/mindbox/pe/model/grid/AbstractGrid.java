package com.mindbox.pe.model.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractIDObject;
import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.ContextContainer;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.AbstractTemplateCore;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

/**
 * Abstract grid.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 1.0
 */
public abstract class AbstractGrid<C extends AbstractTemplateColumn> extends AbstractIDObject implements GridValueContainable, ContextContainer, Auditable {

	private static final long serialVersionUID = 2003052400370000L;

	public static final String COLUMN_DELIM = "|";

	public static final String ROW_DELIM = "~";

	public static final String DRAFT_STATUS = Constants.DRAFT_STATUS;

	public static final boolean isSame(Object obj, Object obj1) {
		if (obj != null && obj1 == null) return false;
		if (obj == null && obj1 != null) return false;
		if (obj == null && obj1 == null)
			return true;
		else
			return obj.equals(obj1);
	}

	private int templateID;
	private String status;
	private Date statusChangeDate;
	private int numRows;
	private DateSynonym effDate, expDate;
	private final Map<GenericEntityType, List<Integer>> genericEntityMap;
	private final Map<GenericEntityType, List<Integer>> genericCategoryMap;
	private AbstractTemplateCore<C> template;
	private int cloneOf;
	private String comments;
	private Date creationDate;

	/**
	 * Construts a new guideline grid that is a copy of the specified source grid. This creates an
	 * identical grid except:
	 * <ol>
	 * <li>The id is set to {@link Persistent#UNASSIGNED_ID}, not <code>sourceGrid.getID()</code>.</li>
	 * <li>The cloneOf is set to <code>sourceGrid.getID()</code>, not
	 * <code>sourceGrid.getCloneOf()</code>.</li>
	 * <li>The creation date is set to <code>new Date()</code>, not
	 * <code>sourceGrid.getCreationDate()</code>.</li>
	 * <li>The status is set to "Draft", not <code>sourceGrid.getStatus()</code>.</li>
	 * <li>The last status change date is set to <code>new Date()</code>, not
	 * <code>sourceGrid.getStatusChangeDate()</code>.</li>
	 * </ol>
	 * This replaces old clone() method.
	 * 
	 * @param sourceGrid
	 *            source grid
	 * @since PowerEditor 4.2.0
	 */
	protected AbstractGrid(AbstractGrid<C> sourceGrid) {
		this(sourceGrid, sourceGrid.getEffectiveDate(), sourceGrid.getExpirationDate());
		this.cloneOf = sourceGrid.getID();
		setStatusChangeDate(creationDate);
		setStatus(DRAFT_STATUS);
	}

	/**
	 * Constructs a new instance copying the specified grid, differing in effective dating and the
	 * template.
	 * 
	 * @param grid
	 *            source grid
	 * @param template
	 *            the template
	 * @param effDate
	 *            effective date
	 * @param expDate
	 *            expiration date
	 * @since PowerEditor 4.2.0
	 */
	protected AbstractGrid(AbstractGrid<C> grid, AbstractTemplateCore<C> template, DateSynonym effDate, DateSynonym expDate) {
		this(Persistent.UNASSIGNED_ID, template, effDate, expDate);
		this.status = grid.getStatus();
		this.statusChangeDate = grid.getStatusChangeDate();
		this.numRows = grid.getNumRows();
		this.comments = grid.getComments();
	}

	/**
	 * Constructs a new instance copying the specified grid, differing in effective dating.
	 * 
	 * @param grid
	 *            source grid
	 * @param effDate
	 *            effective date
	 * @param expDate
	 *            expiration date
	 */
	protected AbstractGrid(AbstractGrid<C> grid, DateSynonym effDate, DateSynonym expDate) {
		this(grid, grid.getTemplate(), effDate, expDate);
	}

	protected AbstractGrid(int gridID, AbstractTemplateCore<C> template, DateSynonym effDate, DateSynonym expDate) {
		this(gridID, template == null ? -1 : template.getID(), effDate, expDate);
		this.template = template;
	}

	protected AbstractGrid(int gridID, int templateID, DateSynonym effDate, DateSynonym expDate) {
		super(gridID);
		this.genericCategoryMap = new HashMap<GenericEntityType, List<Integer>>();
		this.genericEntityMap = new HashMap<GenericEntityType, List<Integer>>();
		this.effDate = effDate;
		this.expDate = expDate;
		this.templateID = templateID;
		status = DRAFT_STATUS;
		statusChangeDate = new Date();
		numRows = 0;
		comments = "";
		creationDate = new Date();
		cloneOf = -1;
	}

	public final void addGenericCategoryID(GenericEntityType type, int id) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (id <= 0) throw new IllegalArgumentException("id must be greater than zero");
		synchronized (genericCategoryMap) {
			List<Integer> list = getGenericCategoryIDList(type);
			if (!list.contains(id)) {
				list.add(id);
			}
		}
	}

	public final void addGenericCategoryIDs(GenericEntityType type, int[] ids) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (ids == null || ids.length == 0) throw new IllegalArgumentException("id cannot be null or empty");
		synchronized (genericCategoryMap) {
			addGenericCategoryIDs_internal(type, ids);
		}
	}

	/**
	 * Assumes <code>type</code> and <code>ids</code> are not <code>null</code>.
	 * @param type
	 * @param ids
	 */
	private final void addGenericCategoryIDs_internal(GenericEntityType type, int[] ids) {
		synchronized (genericCategoryMap) {
			List<Integer> list = getGenericCategoryIDList(type);
			for (int i = 0; i < ids.length; i++) {
				if (!list.contains(ids[i])) {
					list.add(ids[i]);
				}
			}
		}
	}

	public final void addGenericEntityID(GenericEntityType type, int id) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (id <= 0) throw new IllegalArgumentException("id must be greater than zero");
		synchronized (genericEntityMap) {
			List<Integer> list = getGenericEntityIDList(type);
			if (!list.contains(id)) {
				list.add(id);
			}
		}
	}

	public final void addGenericEntityIDs(GenericEntityType type, int[] ids) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (ids == null || ids.length == 0) throw new IllegalArgumentException("id cannot be null or empty");
		synchronized (genericCategoryMap) {
			addGenericEntityIDs_internal(type, ids);
		}
	}

	/**
	 * Assumes <code>type</code> and <code>ids</code> are not <code>null</code>.
	 * @param type
	 * @param ids
	 */
	private final void addGenericEntityIDs_internal(GenericEntityType type, int[] ids) {
		synchronized (genericEntityMap) {
			List<Integer> list = getGenericEntityIDList(type);
			for (int i = 0; i < ids.length; i++) {
				if (!list.contains(ids[i])) {
					list.add(ids[i]);
				}
			}
		}
	}

	public final void clearAllContext() {
		synchronized (genericCategoryMap) {
			synchronized (genericEntityMap) {
				genericEntityMap.clear();
				genericCategoryMap.clear();
			}
		}
	}

	public final void clearAllGenericCategory() {
		synchronized (genericCategoryMap) {
			genericCategoryMap.clear();
		}
	}

	public final void clearAllGenericEntity() {
		synchronized (genericEntityMap) {
			genericEntityMap.clear();
		}
	}

	public final void clearGenericCategory(GenericEntityType type) {
		if (type == null) throw new NullPointerException("type cannot be null");
		synchronized (genericCategoryMap) {
			if (genericCategoryMap.containsKey(type)) {
				genericCategoryMap.get(type).clear();
				genericCategoryMap.remove(type);
			}
		}
	}

	public final void clearGenericEntity(GenericEntityType type) {
		if (type == null) throw new NullPointerException("type cannot be null");
		synchronized (genericEntityMap) {
			if (genericEntityMap.containsKey(type)) {
				genericEntityMap.remove(type);
			}
		}
	}

	public final int compareSunrise(AbstractGrid<C> abstractgrid) {
		if (getSunrise() == null) return abstractgrid.getSunrise() != null ? -1 : 0;
		if (abstractgrid.getSunrise() == null) return 1;
		if (abstractgrid.getSunrise().after(getSunrise())) return -1;
		return !abstractgrid.getSunrise().before(getSunrise()) ? 0 : 1;
	}

	public final int compareSunset(AbstractGrid<C> abstractgrid) {
		if (getSunset() == null) return abstractgrid.getSunset() != null ? 1 : 0;
		if (abstractgrid.getSunset() == null) return -1;
		if (abstractgrid.getSunset().after(getSunset())) return -1;
		return !abstractgrid.getSunset().before(getSunset()) ? 0 : 1;
	}

	public final void copyEntireContext(AbstractGrid<C> source) {
		synchronized (genericCategoryMap) {
			synchronized (genericEntityMap) {
				this.genericCategoryMap.clear();
				this.genericEntityMap.clear();
				for (Map.Entry<GenericEntityType, List<Integer>> entry : source.genericCategoryMap.entrySet()) {
					List<Integer> newList = new ArrayList<Integer>();
					newList.addAll(entry.getValue());
					this.genericCategoryMap.put(entry.getKey(), newList);
				}
				for (Map.Entry<GenericEntityType, List<Integer>> entry : source.genericEntityMap.entrySet()) {
					List<Integer> newList = new ArrayList<Integer>();
					newList.addAll(entry.getValue());
					this.genericEntityMap.put(entry.getKey(), newList);
				}
			}
		}
	}

	protected boolean equals(AbstractGrid<C> abstractgrid) {
		return abstractgrid.getTemplateID() == getTemplateID() && hasSameCellValues(abstractgrid) && isSame(abstractgrid.getEffectiveDate(), getEffectiveDate())
				&& isSame(abstractgrid.getExpirationDate(), getExpirationDate()) && isSame(abstractgrid.getStatus(), getStatus());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) {
			return false;
		}
		else if (getClass().isInstance(obj)) {
			return equals((AbstractGrid<C>) obj);
		}
		else {
			return false;
		}
	}

	@Override
	public GuidelineContext[] extractGuidelineContext() {
		GuidelineContext context = null;
		List<GuidelineContext> contextList = new ArrayList<GuidelineContext>();
		if (hasAnyGenericEntityContext()) {
			GenericEntityType[] types = getGenericEntityTypesInUse();
			for (int i = 0; i < types.length; i++) {
				context = new GuidelineContext(types[i]);
				context.setIDs(getGenericEntityIDs(types[i]));
				contextList.add(context);
			}
		}
		if (hasAnyGenericCategoryContext()) {
			GenericEntityType[] types = getGenericCategoryEntityTypesInUse();
			for (int i = 0; i < types.length; i++) {
				context = new GuidelineContext(types[i].getCategoryType());
				context.setIDs(getGenericCategoryIDs(types[i]));
				contextList.add(context);
			}
		}
		return (GuidelineContext[]) contextList.toArray(new GuidelineContext[0]);
	}

	@Override
	public String getAuditDescription() {
		StringBuilder buff = new StringBuilder();
		buff.append((isParameterGrid() ? "parameter" : "guideline"));
		buff.append(" activation '");
		buff.append(getAuditName());
		buff.append("'");
		return buff.toString();
	}

	public String getAuditName() {
		StringBuilder buff = new StringBuilder();
		buff.append(getTemplate().getName());
		if (!isParameterGrid()) {
			buff.append(" v. ");
			buff.append(getTemplate().getVersion());
			buff.append(" (Type: ");
			buff.append(getTemplate().getUsageType().getDisplayName());
			buff.append(")");
		}
		buff.append(" ");
		buff.append((getEffectiveDate() == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : getEffectiveDate().getName()));
		buff.append(" - ");
		buff.append((getExpirationDate() == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : getExpirationDate().getName()));
		return buff.toString();
	}

	public final int getCloneOf() {
		return cloneOf;
	}

	protected final int getColumnCount() {
		return (getTemplate() == null ? 0 : getTemplate().getNumColumns());
	}

	public final ColumnDataSpecDigest getColumnDataSpecDigest(int columnNo) {
		return (getTemplate() == null ? null : (getTemplate().getColumn(columnNo) == null ? null : getTemplate().getColumn(columnNo).getColumnDataSpecDigest()));
	}

	@Override
	public String getColumnTitle(final String columnName) {
		for (int i = 1; i <= template.getNumColumns(); i++) {
			AbstractTemplateColumn element = template.getColumn(i);
			if (element.getName().equals(columnName)) {
				return element.getTitle();
			}
		}
		return null;
	}

	public final String getComments() {
		return comments;
	}

	public final Date getCreationDate() {
		return creationDate;
	}

	public final DateSynonym getEffectiveDate() {
		return effDate;
	}

	public final DateSynonym getExpirationDate() {
		return expDate;
	}

	@Override
	public final GenericEntityType[] getGenericCategoryEntityTypesInUse() {
		List<GenericEntityType> typeList = new ArrayList<GenericEntityType>();
		synchronized (genericCategoryMap) {
			for (GenericEntityType type : genericCategoryMap.keySet()) {
				List<Integer> list = genericCategoryMap.get(type);
				if (list != null && !list.isEmpty()) {
					typeList.add(type);
				}
			}
		}
		return typeList.toArray(new GenericEntityType[0]);
	}

	private final List<Integer> getGenericCategoryIDList(GenericEntityType type) {
		if (genericCategoryMap.containsKey(type)) {
			return genericCategoryMap.get(type);
		}
		else {
			List<Integer> list = new ArrayList<Integer>();
			genericCategoryMap.put(type, list);
			return list;
		}
	}

	@Override
	public final int[] getGenericCategoryIDs(GenericEntityType type) {
		if (type == null) throw new NullPointerException("type cannot be null");
		synchronized (genericCategoryMap) {
			if (genericCategoryMap.containsKey(type)) {
				return UtilBase.toIntArray(genericCategoryMap.get(type));
			}
			else {
				return new int[0];
			}
		}
	}

	private final List<Integer> getGenericEntityIDList(GenericEntityType type) {
		if (type == null) throw new NullPointerException("type cannot be null");
		synchronized (genericEntityMap) {
			if (genericEntityMap.containsKey(type)) {
				return genericEntityMap.get(type);
			}
			else {
				List<Integer> list = new ArrayList<Integer>();
				genericEntityMap.put(type, list);
				return list;
			}
		}
	}

	@Override
	public final int[] getGenericEntityIDs(GenericEntityType type) {
		if (type == null) throw new NullPointerException("type cannot be null");
		synchronized (genericEntityMap) {
			if (genericEntityMap.containsKey(type)) {
				return UtilBase.toIntArray(genericEntityMap.get(type));
			}
			else {
				return new int[0];
			}
		}
	}

	@Override
	public final GenericEntityType[] getGenericEntityTypesInUse() {
		List<GenericEntityType> typeList = new ArrayList<GenericEntityType>();
		synchronized (genericEntityMap) {
			for (GenericEntityType type : genericEntityMap.keySet()) {
				List<Integer> list = genericEntityMap.get(type);
				if (list != null && !list.isEmpty()) {
					typeList.add(type);
				}
			}
		}
		return typeList.toArray(new GenericEntityType[0]);
	}

	@Override
	public int getNumRows() {
		return numRows;
	}

	@Override
	public final List<String> getRuleIDColumnNames() {
		return template == null ? null : template.getRuleIDColumnNames();
	}

	public final String getStatus() {
		return status;
	}

	public final Date getStatusChangeDate() {
		return statusChangeDate;
	}

	/**
	 * Gets the sunrise date (date of effective date synonym).
	 * 
	 * @return sunrise date; <code>null</code> if effective date synonym is not set
	 */
	public final Date getSunrise() {
		return (effDate == null ? null : effDate.getDate());
	}

	/**
	 * Gets the sunset date (date of expiration date synonym)
	 * 
	 * @return sunset date; <code>null</code> if expiration date synonym is not set
	 */
	public final Date getSunset() {
		return (expDate == null ? null : expDate.getDate());
	}

	public final AbstractTemplateCore<C> getTemplate() {
		return template;
	}

	public final int getTemplateID() {
		return templateID;
	}

	@Override
	public final boolean hasAnyGenericCategoryContext() {
		synchronized (genericCategoryMap) {
			return !genericCategoryMap.isEmpty();
		}
	}

	@Override
	public final boolean hasAnyGenericEntityContext() {
		synchronized (genericEntityMap) {
			return !genericEntityMap.isEmpty();
		}
	}

	@Override
	public final boolean hasGenericCategoryContext(GenericEntityType type) {
		synchronized (genericCategoryMap) {
			if (genericCategoryMap.containsKey(type)) {
				return !genericCategoryMap.get(type).isEmpty();
			}
			else {
				return false;
			}
		}
	}

	@Override
	public final boolean hasGenericEntityContext(GenericEntityType type) {
		synchronized (genericEntityMap) {
			if (genericEntityMap.containsKey(type)) {
				return !genericEntityMap.get(type).isEmpty();
			}
			else {
				return false;
			}
		}
	}

	public final boolean hasIdenticalEffExtDates(AbstractGrid<C> grid) {
		return hasIdenticalEffExtDates(grid.effDate, grid.expDate);
	}

	public final boolean hasIdenticalEffExtDates(DateSynonym effDate, DateSynonym expDate) {
		return (UtilBase.isSame(this.effDate, effDate) && UtilBase.isSame(this.expDate, expDate));
	}

	/**
	 * 
	 * @param abstractgrid
	 * @return <code>true</code> if this has the same cell values as <code>abstractgrid</code>; <code>false</code>, otherwise;
	 *         <code>false</code> if <code>abstractgrid</code> is <code>null</code>
	 */
	protected abstract boolean hasSameCellValues(AbstractGrid<C> abstractgrid);

	public final boolean hasSameContext(AbstractGrid<C> grid) {
		if (grid == null) throw new NullPointerException("grid cannot be null");
		synchronized (genericCategoryMap) {
			synchronized (genericEntityMap) {
				if (genericCategoryMap.size() != grid.genericCategoryMap.size()) return false;
				if (genericEntityMap.size() != grid.genericEntityMap.size()) return false;
				GenericEntityType[] types = getGenericCategoryEntityTypesInUse();
				for (int i = 0; i < types.length; i++) {
					if (!grid.matchesGenericCategoryIDs(types[i], getGenericCategoryIDs(types[i]))) {
						return false;
					}
				}
				types = getGenericEntityTypesInUse();
				for (int i = 0; i < types.length; i++) {
					if (!grid.matchesGenericEntityIDs(types[i], getGenericEntityIDs(types[i]))) {
						return false;
					}
				}
				return true;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public boolean identical(Object obj) {
		if (obj instanceof AbstractGrid) {
			AbstractGrid<C> abstractgrid = (AbstractGrid<C>) obj;
			return equals(abstractgrid);
		}
		else {
			return false;
		}
	}

	/**
	 * 
	 * @param abstractgrid
	 * @param daysAgo
	 * @return Whether the time range is appropriate.
	 * @since PowerEditor 4.2.0
	 */
	public final boolean inTime(boolean useDaysAgo, int daysAgo) {
		return DateUtil.inTime(useDaysAgo, daysAgo, expDate);
	}

	public final boolean isContextEmpty() {
		synchronized (genericCategoryMap) {
			synchronized (genericEntityMap) {
				return genericCategoryMap.isEmpty() && genericEntityMap.isEmpty();
			}
		}
	}

	public final boolean isNew() {
		return getID() <= 0;
	}


	public abstract boolean isParameterGrid();

	public final boolean matchesGenericCategoryIDs(GenericEntityType type, int[] ids) {
		if (type == null) throw new NullPointerException("type cannot be null");
		synchronized (genericCategoryMap) {
			return UtilBase.equals(getGenericCategoryIDs(type), ids);
		}
	}

	public final boolean matchesGenericEntityIDs(GenericEntityType type, int[] ids) {
		if (type == null) throw new NullPointerException("type cannot be null");
		synchronized (genericEntityMap) {
			return UtilBase.equals(getGenericEntityIDs(type), ids);
		}
	}

	public final void removeGenericCategoryID(GenericEntityType type, int id) {
		if (type == null) throw new NullPointerException("type cannot be null");
		synchronized (genericCategoryMap) {
			List<Integer> list = getGenericCategoryIDList(type);
			Integer value = new Integer(id);
			if (list.contains(value)) {
				list.remove(new Integer(value));
			}
		}
	}

	public final void removeGenericEntityID(GenericEntityType type, int id) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (id <= 0) throw new IllegalArgumentException("id must be greater than zero");
		synchronized (genericEntityMap) {
			List<Integer> list = getGenericEntityIDList(type);
			if (list.contains(id)) {
				list.remove(new Integer(id));
			}
		}
	}

	public final void replaceGenericEntityID(GenericEntityType type, int oldId, int newId) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (newId <= 0) throw new IllegalArgumentException("newId must be greater than zero");
		synchronized (genericEntityMap) {
			List<Integer> list = getGenericEntityIDList(type);
			list.remove(new Integer(oldId));
			if (!list.contains(newId)) {
				list.add(newId);
			}
		}
	}

	public final void setCloneOf(int i) {
		cloneOf = i;
	}

	public final void setComments(String s) {
		comments = s;
	}

	public final void setCreationDate(Date date) {
		creationDate = date;
	}

	public final void setEffectiveDate(DateSynonym effDate) {
		this.effDate = effDate;
	}

	public final void setExpirationDate(DateSynonym expDate) {
		this.expDate = expDate;
	}

	public final void setGenericCategoryIDs(GenericEntityType type, int[] ids) {
		if (type == null) throw new NullPointerException("type cannot be null");
		synchronized (genericCategoryMap) {
			List<Integer> list = getGenericCategoryIDList(type);
			list.clear();
			for (int i = 0; i < ids.length; i++) {
				list.add(ids[i]);
			}
		}
	}

	public final void setGenericEntityIDs(GenericEntityType type, int[] ids) {
		if (type == null) throw new NullPointerException("type cannot be null");
		if (ids == null) throw new NullPointerException("ids cannot be null");
		synchronized (genericEntityMap) {
			List<Integer> list = getGenericEntityIDList(type);
			list.clear();
			for (int i = 0; i < ids.length; i++) {
				list.add(ids[i]);
			}
		}
	}

	@Override
	public void setNumRows(int i) {
		numRows = i;
	}

	public final void setStatus(String s) {
		if (status != null && !status.equals(s)) {
			status = s;
			setStatusChangeDate(new Date());
		}
		if (status == null) status = s;
	}

	public final void setStatusChangeDate(Date date) {
		statusChangeDate = date;
	}

	public void setTemplate(AbstractTemplateCore<C> template) {
		this.template = template;
		setTemplateID(template.getID());
	}

	public final void setTemplateID(int templateID) {
		this.templateID = templateID;
	}

	@Override
	public String toString() {
		return "[" + getID() + ",template=" + getTemplateID() + ",numRows=" + getNumRows() + ",status=" + getStatus() + ",a=" + effDate + "-" + expDate + "]";
	}
}