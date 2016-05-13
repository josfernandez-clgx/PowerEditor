package com.mindbox.pe.common;

public class SimpleUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private Throwable exception = null;

	public synchronized void uncaughtException(Thread arg0, Throwable arg1) {
		this.exception = arg1;
	}

	public synchronized Throwable getUncaughtException() {
		return exception;
	}

	public synchronized boolean hasUncaughtException() {
		return exception != null;
	}
}
