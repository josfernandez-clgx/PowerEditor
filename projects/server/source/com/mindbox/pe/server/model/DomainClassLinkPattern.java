/*
 * Created on Dec 17, 2005
 *
 */
package com.mindbox.pe.server.model;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DomainClassLink;


/**
 * Contains details of a link pattern.
 * @author Geneho Kim
 * @since PowerEditor 4.3.11
 */
public class DomainClassLinkPattern {

	private final DomainClassLink domainClassLink;
	private String objectName;

	public DomainClassLinkPattern(DomainClassLink classLink) {
		this.domainClassLink = classLink;
	}
	
	public DomainClassLink getDomainClassLink() {
		return domainClassLink;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof DomainClassLinkPattern) {
			return ((DomainClassLinkPattern) obj).domainClassLink.equals(this.domainClassLink)
					&& UtilBase.isSame(this.objectName, ((DomainClassLinkPattern) obj).objectName);
		}
		else {
			return false;
		}
	}
}
