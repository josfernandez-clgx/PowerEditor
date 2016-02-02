package com.mindbox.pe.server.imexport.digest;

import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

public class ListCreationFactory<T> extends AbstractObjectCreationFactory {

	@Override
	public Object createObject(Attributes arg0) throws Exception {
		return Collections.synchronizedList(new LinkedList<T>());
	}

}
