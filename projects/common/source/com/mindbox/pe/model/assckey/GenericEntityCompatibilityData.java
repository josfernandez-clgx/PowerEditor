package com.mindbox.pe.model.assckey;

import net.sf.oval.constraint.CheckWith;
import net.sf.oval.constraint.Min;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.CheckWithCheck.SimpleCheck;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityCompatibilityData extends GenericEntityAssociationKey implements Auditable {

	private static final long serialVersionUID = 2004042980000L;

	@SuppressWarnings("serial")
	private static class NotEqualToSourceIDCheck implements SimpleCheck {
		public boolean isSatisfied(Object validatedObject, Object value) {
			if (validatedObject instanceof GenericEntityCompatibilityData && value instanceof Integer) {
				return ((Integer) value).intValue() != ((GenericEntityCompatibilityData)validatedObject).getAssociableID();
			}
			return false;
		}
	}

	@NotNull
	private final GenericEntityType sourceType;

	@Min(value = 1)
	@CheckWith(value=NotEqualToSourceIDCheck.class, message="violated.compatibility.TargetNotEqualToSource")
	private final int sourceID;

	/**
	 * 
	 * @param sourceType
	 * @param sourceID
	 * @param targetType
	 * @param associableID
	 * @param effDate
	 * @param expDate
	 */
	public GenericEntityCompatibilityData(GenericEntityType sourceType, int sourceID, GenericEntityType targetType,
			int associableID, DateSynonym effDate, DateSynonym expDate) {
		super(targetType, associableID, effDate, expDate);
		this.sourceType = sourceType;
		this.sourceID = sourceID;
	}

	private GenericEntityCompatibilityData(GenericEntityCompatibilityData source) {
		super(source);
		this.sourceID = source.sourceID;
		this.sourceType = source.sourceType;
	}

	public Auditable deepCopy() {
		return new GenericEntityCompatibilityData(this);
	}

	public String getAuditName() {
		return toString();
	}

	public String getAuditDescription() {
		return toString();
	}

	/**
	 * @return the sourceID
	 */
	public int getSourceID() {
		return sourceID;
	}

	/**
	 * @return the sourceType
	 */
	public GenericEntityType getSourceType() {
		return sourceType;
	}
	
	public String toString() {
		return "Compatibility[source=" + sourceType + ":" + sourceID + ",target=" + getGenericEntityType() + ":" + getAssociableID() + ']';
	}

}