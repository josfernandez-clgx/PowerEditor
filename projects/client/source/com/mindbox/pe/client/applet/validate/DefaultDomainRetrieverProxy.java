package com.mindbox.pe.client.applet.validate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DomainClass;

final class DefaultDomainRetrieverProxy implements DomainRetrieverProxy {

	private static DefaultDomainRetrieverProxy instance = null;

	public static DefaultDomainRetrieverProxy getInstance() {
		if (instance == null) {
			instance = new DefaultDomainRetrieverProxy();
		}
		return instance;
	}

	private DefaultDomainRetrieverProxy() {
	}

	@Override
	public DomainClass[] fetchAllDomainClasses() throws ServerException, IOException {
		byte[] xmlBytes = ClientUtil.getCommunicator().getDomainDefintionXML();

		// deserialize array of domain classes from server
		ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(xmlBytes)));
		Object object;
		try {
			object = ois.readObject();
			return (DomainClass[]) object;
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		finally {
			ois.close();
		}
	}

}
