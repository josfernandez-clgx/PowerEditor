package com.mindbox.pe.model.assckey;

import net.sf.oval.constraint.NotNull;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityAssociationKey extends AbstractTimedAssociationKey implements Persistent {

	private static final long serialVersionUID = 2004042980001L;

	@NotNull
	private final GenericEntityType type;

	/**
	 * @param type the generic entity type
	 * @param associableID
	 * @param effDate
	 * @param expDate
	 */
	public GenericEntityAssociationKey(GenericEntityType type, int associableID, DateSynonym effDate, DateSynonym expDate) {
		super(associableID, effDate, expDate);
		this.type = type;
	}

	public GenericEntityAssociationKey(GenericEntityAssociationKey source) {
		super(source);
		this.type = source.type;
	}

	/**
	 * This always returns zero. <b>Do not use for identification purpose</b>.
	 * This method exists just to make this class implement the {@link Persistent} interface.
	 * @return 0
	 */
	public final int getID() {
		return 0;
	}

	public GenericEntityType getGenericEntityType() {
		return type;
	}

	public void setEffectiveDate(DateSynonym effectiveDate) {
		super.setEffectiveDate(effectiveDate);
	}

	public void setExpirationDate(DateSynonym expirationDate) {
		super.setExpirationDate(expirationDate);
	}

}
