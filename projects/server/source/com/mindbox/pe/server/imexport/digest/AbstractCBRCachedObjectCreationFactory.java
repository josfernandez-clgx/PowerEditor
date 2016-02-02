package com.mindbox.pe.server.imexport.digest;

import org.apache.commons.digester.AbstractObjectCreationFactory;

import com.mindbox.pe.server.cache.CBRManager;

public abstract class AbstractCBRCachedObjectCreationFactory extends AbstractObjectCreationFactory {

	protected final CBRManager cbrManager = CBRManager.getInstance();
}
