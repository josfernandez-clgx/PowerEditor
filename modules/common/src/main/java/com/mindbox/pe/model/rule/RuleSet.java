package com.mindbox.pe.model.rule;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class RuleSet extends AbstractIDNameDescriptionObject {

	private static final long serialVersionUID = -2127830274911958931L;

	private int channelID = -1;
	private int investorID = -1;
	private int productID = -1;
	private int[] categoryIDs = new int[0];
	private final Map<GenericEntityType,Integer> genericEntityMap;
	private final Map<GenericEntityType,Integer> genericCategoryMap;

	private Date activationDate = null;
	private Date expirationDate = null;
	
	private TemplateUsageType usageType = null;

	private String status = Constants.DRAFT_STATUS;

	public RuleSet(TemplateUsageType usageType, int id, String name, String desc) {
		super(id, name, desc);
		this.usageType = usageType;
		genericEntityMap= new HashMap<GenericEntityType,Integer>();
		genericCategoryMap = new HashMap<GenericEntityType,Integer>();
	}

	public RuleSet(int id, String name, String desc) {
		this(null, id, name, desc);
	}

	public boolean hasGenericCategoryContext() {
		return !genericCategoryMap.isEmpty();
	}
	
	public int getGenericCategoryID(GenericEntityType type) {
		if (genericCategoryMap.containsKey(type)) {
			return genericCategoryMap.get(type);
		}
		else {
			return -1;
		}
	}
	
	/**
	 * Even if all entities are generic we need to keep this one for Ad-hoc rule imports.
	 * @return get the id of the first generic category in this
	 * 
	 */
	public int getFirstGenericCategoryID() {
		if (genericCategoryMap.isEmpty()) return -1;
		return genericCategoryMap.values().iterator().next();
	}
	
	/**
	 * Even if all entities are generic we need to keep this one for Ad-hoc rule imports.
	 * @return get the generic entity type of the first generic category in this
	 */
	public GenericEntityType getFirstEntityTypeForGenericCategory() {
		if (genericCategoryMap.isEmpty()) return null;
		return genericCategoryMap.keySet().iterator().next();
	}
	
	public void clearGenericCategory(GenericEntityType type) {
		if (genericCategoryMap.containsKey(type)) {
			genericCategoryMap.remove(type);
		}
	}
	
	public void setGenericCategoryID(GenericEntityType type, int id) {
		if (genericCategoryMap.containsKey(type)) {
			genericCategoryMap.remove(type);
		}
		genericCategoryMap.put(type, id);
	}
	
	public GenericEntityType[] getGenericEntityTypesInUse() {
		return genericEntityMap.keySet().toArray(new GenericEntityType[0]);
	}
	
	public boolean hasGenericEntityContext() {
		return !genericEntityMap.isEmpty();
	}
	
	public int getGenericEntityID(GenericEntityType type) {
		if (genericEntityMap.containsKey(type)) {
			return genericEntityMap.get(type);
		}
		else {
			return -1;
		}
	}
	
	public void clearGenericEntity(GenericEntityType type) {
		if (genericEntityMap.containsKey(type)) {
			genericEntityMap.remove(type);
		}
	}
	
	public void setGenericEntityID(GenericEntityType type, int id) {
		if (genericEntityMap.containsKey(type)) {
			genericEntityMap.remove(type);
		}
		genericEntityMap.put(type, id);
	}
	
	public String toString() {
		return "RuleSet["+super.getName()+",id="+super.getID()+",act="+activationDate+"-"+expirationDate+"]";
	}

	public boolean hasCategoryID() {
		return categoryIDs != null && categoryIDs.length > 0;
	}
	
	public int[] getCategoryIDs() {
		return categoryIDs;
	}
	
	public int getChannelID() {
		return channelID;
	}

	public int getInvestorID() {
		return investorID;
	}

	public int getProductID() {
		return productID;
	}

	public void setCategoryIDs(int[] ids) {
		categoryIDs = (ids == null ? new int[0] : ids);
	}

	public void setChannelID(int i) {
		channelID = i;
	}

	public void setInvestorID(int i) {
		investorID = i;
	}

	public void setProductID(int i) {
		productID = i;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setActivationDate(Date date) {
		activationDate = date;
	}

	public void setExpirationDate(Date date) {
		expirationDate = date;
	}

	public boolean equals(Object obj) {
		if (obj instanceof RuleSet) {
			return this.getID() == ((RuleSet) obj).getID() || this.getName().equals(((RuleSet) obj).getName());
		}
		else {
			return false;
		}
	}

	public TemplateUsageType getUsageType() {
		return usageType;
	}

	public void setUsageType(TemplateUsageType type) {
		usageType = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String string) {
		status = string;
	}

}
