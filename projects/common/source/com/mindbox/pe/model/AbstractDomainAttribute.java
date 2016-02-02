package com.mindbox.pe.model;


/**
 * Abstract domain attribute class.
 * Encapsulates common behavior of domain attribute and domain translation tags.
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public abstract class AbstractDomainAttribute extends AbstractDomainElement {

	private static final long serialVersionUID = 2003060508453001L;


	/** @since PowerEditor 3.2.0 */
	private String contextlessLabel;

	/**
	 * @return Returns the contextlessLabel.
	 */
	public final String getContextlessLabel() {
		return contextlessLabel;
	}

	/**
	 * @param contextlessLabel The contextlessLabel to set.
	 */
	public final void setContextlessLabel(String contextlessLabel) {
		this.contextlessLabel = contextlessLabel;
	}

}
