package com.mindbox.pe.server.db;

import com.mindbox.pe.server.ServerTestBase;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class DBTestBase extends ServerTestBase {

	/**
	 * @param name
	 */
	public DBTestBase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		super.config.initServer();
	}
	
	
}
