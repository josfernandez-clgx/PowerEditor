package com.mindbox.pe.common.digest;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.xml.sax.Attributes;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.FloatDomainAttribute;

public class DomainAttributeFactory implements ObjectCreationFactory {
	private Digester digester;

	public Object createObject(Attributes attrs) throws Exception {
		DeployType deployType = DeployType.valueOf(attrs.getValue("", "DeployType"));
		return deployType == DeployType.FLOAT || deployType == DeployType.CURRENCY || deployType == DeployType.PERCENT ? new FloatDomainAttribute() : new DomainAttribute();
	}

	public Digester getDigester() {
		return digester;
	}

	public void setDigester(Digester digester) {
		this.digester = digester;
	}
}
