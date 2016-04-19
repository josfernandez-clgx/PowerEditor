/*
 * Created on Jun 11, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.server.cache;

import org.apache.log4j.Logger;

/**
 * Cache manager base class.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public abstract class AbstractCacheManager {

	protected final Logger logger;

	/**
	 * 
	 */
	protected AbstractCacheManager() {
		this.logger = Logger.getLogger(getClass());
	}

}
