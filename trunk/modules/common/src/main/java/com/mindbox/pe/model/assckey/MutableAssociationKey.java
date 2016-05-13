package com.mindbox.pe.model.assckey;

/**
 * A mutable variation of {@link AssociationKey}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface MutableAssociationKey extends AssociationKey {

	void setAssociableID(int id);
}
