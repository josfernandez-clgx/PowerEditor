package com.mindbox.pe.common;

import static com.mindbox.pe.common.LogUtil.logInfo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

public final class FutureTaskRunnerHelper {

	private static Exception asException(Throwable t) {
		if (Exception.class.isInstance(t)) {
			return Exception.class.cast(t);
		}
		else {
			throw Error.class.cast(t);
		}
	}

	public static void runAndWait(Runnable runnable, long timeOutInSec, Logger log) throws TimeoutException, Exception {
		SimpleUncaughtExceptionHandler simpleUncaughtExceptionHandler = new SimpleUncaughtExceptionHandler();
		FutureTask<Boolean> futureTask = new FutureTask<Boolean>(runnable, true);

		Thread thread = new Thread(futureTask);
		thread.setUncaughtExceptionHandler(simpleUncaughtExceptionHandler);
		thread.setDaemon(true);
		thread.start();

		Exception exceptionToThrow = null;
		// Wait for the task to complete
		try {
			futureTask.get(timeOutInSec, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			// ignore
		}
		catch (ExecutionException e) {
			exceptionToThrow = (e.getCause() == null ? e : asException(e.getCause()));
		}
		finally {
			if (!futureTask.isDone()) {
				logInfo(log, "Task was unsuccessful; canceling");
				futureTask.cancel(true);
			}
		}

		if (simpleUncaughtExceptionHandler.hasUncaughtException()) {
			Throwable uncaughtException = simpleUncaughtExceptionHandler.getUncaughtException();
			exceptionToThrow = asException(uncaughtException.getCause() == null ? uncaughtException : uncaughtException.getCause());
		}

		if (exceptionToThrow != null) {
			throw exceptionToThrow;
		}
	}

	public static <V> V runAndWait(Callable<V> callable, long timeOutInSec, Logger log) throws TimeoutException, Exception {
		SimpleUncaughtExceptionHandler simpleUncaughtExceptionHandler = new SimpleUncaughtExceptionHandler();
		FutureTask<V> futureTask = new FutureTask<V>(callable);

		Thread thread = new Thread(futureTask);
		thread.setUncaughtExceptionHandler(simpleUncaughtExceptionHandler);
		thread.setDaemon(true);
		thread.start();

		Exception exceptionToThrow = null;
		// Wait for the task to complete
		V value = null;
		try {
			value = futureTask.get(timeOutInSec, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			// ignore
		}
		catch (ExecutionException e) {
			exceptionToThrow = (e.getCause() == null ? e : asException(e.getCause()));
		}
		finally {
			if (!futureTask.isDone()) {
				logInfo(log, "Task was unsuccessful; canceling");
				futureTask.cancel(true);
			}
		}

		if (simpleUncaughtExceptionHandler.hasUncaughtException()) {
			Throwable uncaughtException = simpleUncaughtExceptionHandler.getUncaughtException();
			exceptionToThrow = asException(uncaughtException.getCause() == null ? uncaughtException : uncaughtException.getCause());
		}

		if (exceptionToThrow != null) {
			throw exceptionToThrow;
		}
		else {
			return value;
		}
	}

	private FutureTaskRunnerHelper() {
	}
}
