/*
 * Created on 2004. 6. 4.
 *
 */
package com.mindbox.pe.model.domain;

import java.util.Iterator;


/**
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public class DomainTranslation extends AbstractDomainAttribute {

	private static final long serialVersionUID = 2004060470000L;

	private String linkPath;
	private String attributeType;

	/**
	 * 
	 */
	public DomainTranslation() {
		super();
	}

	/**
	 * @return Returns the attributeType.
	 */
	public String getAttributeType() {
		return attributeType;
	}

	/**
	 * @param attributeType The attributeType to set.
	 */
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return Returns the linkPath.
	 */
	public String getLinkPath() {
		return linkPath;
	}

	/**
	 * @param linkPath The linkPath to set.
	 */
	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("DomainTranslation[");
		buff.append(getName());
		buff.append(",");
		buff.append("linkPath='");
		buff.append(linkPath);
		buff.append("',");
		buff.append(getDisplayLabel());
		buff.append(",");
		buff.append(getContextlessLabel());
		buff.append(",view=");
		for (Iterator<DomainView> iter = domainViewList.iterator(); iter.hasNext();) {
			buff.append(iter.next());
			if (iter.hasNext()) {
				buff.append("+");
			}
		}
		buff.append("]");
		return buff.toString();
	}
}