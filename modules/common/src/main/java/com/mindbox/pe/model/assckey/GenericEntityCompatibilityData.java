package com.mindbox.pe.model.assckey;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;

import net.sf.oval.constraint.CheckWith;
import net.sf.oval.constraint.CheckWithCheck.SimpleCheck;
import net.sf.oval.constraint.Min;
import net.sf.oval.constraint.NotNull;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityCompatibilityData extends GenericEntityAssociationKey implements Auditable {

	@SuppressWarnings("serial")
	private static class NotEqualToSourceIDCheck implements SimpleCheck {
		@Override
		public boolean isSatisfied(Object validatedObject, Object value) {
			if (validatedObject instanceof GenericEntityCompatibilityData && value instanceof Integer) {
				return ((Integer) value).intValue() != ((GenericEntityCompatibilityData) validatedObject).getAssociableID();
			}
			return false;
		}
	}

	private static final long serialVersionUID = 2004042980000L;

	@NotNull
	private final GenericEntityType sourceType;

	@Min(value = 1)
	@CheckWith(value = NotEqualToSourceIDCheck.class, message = "violated.compatibility.TargetNotEqualToSource")
	private final int sourceID;

	private GenericEntityCompatibilityData(GenericEntityCompatibilityData source) {
		super(source);
		this.sourceID = source.sourceID;
		this.sourceType = source.sourceType;
	}

	/**
	 * 
	 * @param sourceType sourceType
	 * @param sourceID sourceID
	 * @param targetType targetType
	 * @param associableID associableID
	 * @param effDate effDate
	 * @param expDate expDate
	 */
	public GenericEntityCompatibilityData(GenericEntityType sourceType, int sourceID, GenericEntityType targetType, int associableID, DateSynonym effDate, DateSynonym expDate) {
		super(targetType, associableID, effDate, expDate);
		this.sourceType = sourceType;
		this.sourceID = sourceID;
	}

	@Override
	public Auditable deepCopy() {
		return new GenericEntityCompatibilityData(this);
	}

	@Override
	public String getAuditDescription() {
		return toString();
	}

	@Override
	public String getAuditName() {
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

	@Override
	public String toString() {
		return "Compatibility[source=" + sourceType + ":" + sourceID + ",target=" + getGenericEntityType() + ":" + getAssociableID() + ']';
	}

}