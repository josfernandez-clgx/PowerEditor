package com.mindbox.pe.server.config;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;

public abstract class AbstractDigestedObjectCreationFactory implements ObjectCreationFactory {

	private Digester digester;

	public final Digester getDigester() {
		return digester;
	}

	public final void setDigester(Digester digester) {
		this.digester = digester;
	}

}
