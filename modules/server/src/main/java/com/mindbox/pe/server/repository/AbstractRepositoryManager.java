/*
 * Created on 2004. 2. 9.
 *
 */
package com.mindbox.pe.server.repository;

import org.apache.log4j.Logger;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public abstract class AbstractRepositoryManager implements RepositoryManager {

	protected final Logger logger;
	
	/**
	 * 
	 */
	protected AbstractRepositoryManager() {
		super();
		this.logger = Logger.getLogger(getClass());
	}

}
