package com.mindbox.pe.client.applet.entities.generic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.common.table.IDNameObjectSelectionTableModel;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Model for IDName Object selection table.
 * 
 * @since PowerEditor 1.0
 */
public class GenericEntitySelectionTableModel extends IDNameObjectSelectionTableModel<GenericEntity> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static GenericEntitySelectionTableModel createInstance(GenericEntityType type) {
		EntityType typeDef = ClientUtil.getEntityConfigHelper().findEntityTypeDefinition(type);
		List<EntityProperty> propDefs = typeDef.getEntityProperty();

		List<String> nameList = new ArrayList<String>();
		nameList.add(ClientUtil.getInstance().getLabel("label.id"));
		nameList.add(ClientUtil.getInstance().getLabel("label.name"));
		List<EntityProperty> propDefList = new LinkedList<EntityProperty>();

		for (final EntityProperty entityProperty : propDefs) {
			if (UtilBase.asBoolean(entityProperty.isShowInSelectionTable(), false)) {
				nameList.add(entityProperty.getDisplayName());
				propDefList.add(entityProperty);
			}
		}

		CategoryType catTypeDef = ClientUtil.getEntityConfigHelper().getCategoryDefinition(type);
		boolean showCategory = (catTypeDef == null ? false : UtilBase.asBoolean(catTypeDef.isShowInSelectionTable(), false));
		if (showCategory) {
			nameList.add(catTypeDef.getName());
		}

		return new GenericEntitySelectionTableModel((String[]) nameList.toArray(new String[0]), propDefList.toArray(new EntityProperty[0]), showCategory);
	}

	private final EntityProperty[] propDefs;
	private final boolean showCategory;
	private Date categoryOnDate;

	/**
	 * Create new IDName Object selection table model with the specified column names.
	 * 
	 * @param columnNames
	 */
	private GenericEntitySelectionTableModel(String[] columnNames, EntityProperty[] propDefs, boolean showCategory) {
		super(columnNames);
		this.propDefs = propDefs;
		this.showCategory = showCategory;
	}

	public Date getCategoryOnDate() {
		return categoryOnDate;
	}

	private String getCategoryString(GenericEntity entity) {
		StringBuilder buff = new StringBuilder();
		if (categoryOnDate != null) {
			for (Iterator<Integer> iter = entity.getCategoryIDList(categoryOnDate).iterator(); iter.hasNext();) {
				int catID = iter.next();
				buff.append(EntityModelCacheFactory.getInstance().getGenericCategoryName(entity.getType(), catID, String.valueOf(catID)));
				if (iter.hasNext()) {
					buff.append(',');
				}
			}
		}
		return buff.toString();
	}

	@Override
	public Class<?> getColumnClass(int i) {
		switch (i) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		default:
			if ((i > 1) && (i - 2 < propDefs.length)) {
				return GenericEntityUtil.getEditValueClass(propDefs[i - 2]);
			}
			else {
				return Object.class;
			}
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		GenericEntity value = (GenericEntity) dataList.get(row);
		switch (col) {
		case 0:
			return String.valueOf((value.getID()));
		case 1:
			return value.getName();
		default:
			if (col > 1 && col - 2 < propDefs.length) {
				return GenericEntityUtil.getPropertyValueForTable(value, propDefs[col - 2]);
			}
			else if (showCategory && col == propDefs.length + 2) {
				return getCategoryString(value);
			}
			else {
				return value;
			}
		}
	}

	public void setCategoryOnDate(Date categoryOnDate) {
		this.categoryOnDate = categoryOnDate;
	}


}