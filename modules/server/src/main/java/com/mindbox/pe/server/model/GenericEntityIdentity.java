package com.mindbox.pe.server.model;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityIdentity extends AbstractTypeIDIdentity {

	public GenericEntityIdentity(int type, int id) {
		super(type, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof GenericEntityIdentity) {
			return super.equals((GenericEntityIdentity) obj);
		}
		else {
			return false;
		}
	}

	/**
	 * @return the entityID.
	 */
	public int getEntityID() {
		return super.getId();
	}

	/**
	 * @return the entityType.
	 */
	public int getEntityType() {
		return super.getType();
	}
}