/*
 * Created on 2004. 2. 9.
 *
 */
package com.mindbox.pe.server.repository;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class RepositoryManageFactory {

	private static RepositoryManageFactory instance = null;

	public static synchronized RepositoryManageFactory getInstance() {
		if (instance == null) {
			instance = new RepositoryManageFactory();
		}
		return instance;
	}

	private RepositoryManageFactory() {
	}

}
