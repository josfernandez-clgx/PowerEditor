/*
 * Created on Jul 10, 2003
 */
package com.mindbox.pe.model;

import java.io.Serializable;

import com.mindbox.pe.common.UtilBase;


/**
 * Guideline context container.
 * This contains an entity type and a set of entity ids.
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 1.10.0
 */
public final class GuidelineContext implements Serializable {

	/**
	 * Tests if the two guideline context arrays represent the same set of guideline context elements.
	 * @param gc1 guideline context array 1
	 * @param gc2 guideline context array 2
	 * @return <code>true</code> if <code>gc1<code> and <code>gc2</code> presetnt the same set of guideline context elements; <code>false</code>, otherwise
	 * @since PowerEditor 4.2.0
	 */
	public static boolean isIdentical(GuidelineContext[] gc1, GuidelineContext[] gc2) {
		if (UtilBase.isSame(gc1, gc2)) return true;
		if (gc1 == null || gc2 == null) return false;
		if (gc1.length != gc2.length) return false;
		for (int i = 0; i < gc1.length; i++) {
			if (!gc1[i].isContainedIn(gc2)) { return false; }
		}
		return true;
	}

	private static final long serialVersionUID = 2003071022559000L;

	/**
	 * Added for generic entity support
	 * @since 3.0.0
	 */
	private final GenericEntityType genericEntityType;
	private final int genericCategoryType;
	private int[] entityIDs = null;

	public GuidelineContext(GenericEntityType genericEntityType) {
		this(genericEntityType, -1);
	}

	public GuidelineContext(int genericCategoryType) {
		this(null, genericCategoryType);
	}

	private GuidelineContext(GenericEntityType genericEntityType, int genericCategoryType) {
		this.genericEntityType = genericEntityType;
		this.genericCategoryType = genericCategoryType;
	}

	public GenericEntityType getGenericEntityTypeForContext() {
		if (hasCategoryContext()) {
			return GenericEntityType.forCategoryType(genericCategoryType);
		}
		else {
			return genericEntityType;
		}
	}

	public boolean hasCategoryContext() {
		return genericCategoryType > -1;
	}
	
	/**
	 * Tests if this is contained in the specified guideline context array.
	 * Note: this is good for equalify test, but not for sub-context test.
	 * @param contexts the guidline context array
	 * @return <code>true</code> if <code>contexts</code> contains the same guideline context that this represents; <code>false</code>, otherwise
	 * @since PowerEditor 4.2.0
	 */
	public boolean isContainedIn(GuidelineContext[] contexts) {
		if (contexts == null) return false;
		for (int i = 0; i < contexts.length; i++) {
			if (contexts[i].genericEntityType == this.genericEntityType
					&& contexts[i].genericCategoryType == this.genericCategoryType && UtilBase.equals(contexts[i].entityIDs, this.entityIDs)) { 
				return true; 
			}
		}
		return false;
	}

	public int getGenericCategoryType() {
		return genericCategoryType;
	}

	public void setIDs(Persistent[] selections) {
		if (selections == null) throw new NullPointerException("selections cannot be null");
		int[] ids = new int[selections.length];
		for (int i = 0; i < selections.length; i++) {
			ids[i] = selections[i].getID();
		}
		entityIDs = ids;
	}

	public void setIDs(int[] ids) {
		if (ids == null) throw new NullPointerException("ids cannot be null");
		entityIDs = ids;
	}

	public int[] getIDs() {
		return entityIDs;
	}

	public String toString() {
		return "GuidelineContext[" + genericEntityType + ":size=" + entityIDs.length + " ]";
	}

	/**
	 * @return Returns the genericEntityType.
	 */
	public GenericEntityType getGenericEntityType() {
		return genericEntityType;
	}
}