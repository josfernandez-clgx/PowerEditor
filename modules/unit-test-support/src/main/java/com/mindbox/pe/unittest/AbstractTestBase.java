package com.mindbox.pe.unittest;

import org.apache.log4j.Logger;

/**
 * Base test case for all PowerEdtior tests. Check the available protected fields and methods.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 */
public abstract class AbstractTestBase {

	protected final Logger logger = Logger.getLogger(getClass());

	protected final void log(Object msg) {
		logger.info(msg);
	}

	protected final void log(Object msg, Throwable t) {
		logger.error(msg, t);
	}

	protected final void logBegin(String test) {
		logger.info("=== BEGINING OF " + test + " ===");
	}

	protected final void logEnd(String test) {
		logger.info("=== END OF " + test + " ===");
	}

}
