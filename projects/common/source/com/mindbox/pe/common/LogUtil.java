package com.mindbox.pe.common;

import org.apache.log4j.Logger;

public class LogUtil {

	public static void logDebug(Logger log, String message, Object... args) {
		if (log.isDebugEnabled()) {
			log.debug(String.format(message, args));
		}
	}

	public static void logInfo(Logger log, String message, Object... args) {
		if (log.isInfoEnabled()) {
			log.info(String.format(message, args));
		}
	}

	public static void logWarn(Logger log, String message, Object... args) {
		log.warn(String.format(message, args));
	}

	public static void logWarn(Logger log, Exception e, String message, Object... args) {
		log.warn(String.format(message, args), e);
	}

	public static void logError(Logger log, Exception e, String message, Object... args) {
		log.error(String.format(message, args), e);
	}

	public static void logError(Logger log, Throwable t, String message, Object... args) {
		log.error(String.format(message, args), t);
	}

	public static void logFatal(Logger log, Throwable t, String message, Object... args) {
		log.fatal(String.format(message, args), t);
	}


	private LogUtil() {
	}
}
