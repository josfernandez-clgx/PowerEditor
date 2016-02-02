/*
 * Created on 2004. 2. 9.
 *
 */
package com.mindbox.pe.server.repository;

import com.mindbox.pe.server.repository.adhoc.AdHocRuleRepositoryManagerImpl;

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

	private final AdHocRuleRepositoryManager adhrManager;

	private RepositoryManageFactory() {
		adhrManager = new AdHocRuleRepositoryManagerImpl();
	}

	public AdHocRuleRepositoryManager getAdHocRuleRepositoryManager() {
		return adhrManager;
	}

}
