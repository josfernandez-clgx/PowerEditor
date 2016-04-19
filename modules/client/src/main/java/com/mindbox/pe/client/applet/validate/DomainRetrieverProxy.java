package com.mindbox.pe.client.applet.validate;

import java.io.IOException;

import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.domain.DomainClass;

public interface DomainRetrieverProxy {

	DomainClass[] fetchAllDomainClasses() throws ServerException, IOException;
}
