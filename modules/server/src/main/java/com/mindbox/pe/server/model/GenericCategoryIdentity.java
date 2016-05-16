package com.mindbox.pe.server.model;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.5.0
 */
public class GenericCategoryIdentity extends AbstractTypeIDIdentity {

	public GenericCategoryIdentity(int type, int id) {
		super(type, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof GenericCategoryIdentity) {
			return super.equals((GenericCategoryIdentity) obj);
		}
		else {
			return false;
		}
	}

	/**
	 * @return Returns the entityID.
	 */
	public int getCategoryID() {
		return super.getId();
	}

	/**
	 * @return Returns the entityType.
	 */
	public int getCategoryType() {
		return super.getType();
	}
}