package com.mindbox.ftest.pe.util;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;

/**
 * Executes a command with time out.
 * @author kim
 *
 */
public final class TimeOutCommandExecutor {

	private static class CommandThread extends Thread {

		private final String[] cmdArray;
		private Exception exceptionFromCmdProcess;
		private int exitCode = Integer.MIN_VALUE;
		private boolean running = false;
		private final Logger logger;

		public CommandThread(String[] cmdArray, Logger logger) {
			this.cmdArray = cmdArray;
			this.logger = logger;
		}

		private synchronized boolean isRunning() {
			return running;
		}

		private synchronized void setRunning(boolean running, Exception exception) {
			this.running = running;
			this.exceptionFromCmdProcess = exception;
		}

		private synchronized int getExitCode() {
			return exitCode;
		}

		private synchronized void setExitCode(int exitCode) {
			this.exitCode = exitCode;
			this.running = false;
		}

		private synchronized Exception getExceptionFromCmdProcess() {
			return exceptionFromCmdProcess;
		}

		public void run() {
			logger.info("--> run: " + UtilBase.toString(cmdArray));
			setRunning(true, null);
			Process process = null;
			try {
				process = Runtime.getRuntime().exec(cmdArray);
				logger.info("... run: process started " + process);
				process.waitFor();
				logger.info("... run: setting exit code");
				setExitCode(process.exitValue());
			}
			catch (IOException e) {
				setRunning(false, e);
			}
			catch (InterruptedException e) {
				setRunning(false, e);
				if (process != null) {
					process.destroy();
				}
			}
			catch (Exception e) {
				setRunning(false, e);
			}
		}
	}

	private final long commandTimeOut;
	private final Logger logger;

	public TimeOutCommandExecutor(long commandTimeOut) {
		this.commandTimeOut = commandTimeOut;
		this.logger = Logger.getLogger(getClass());
	}

	/**
	 * 
	 * @return exit code from the command; null, if command was timed out
	 * @throws CommandTimedOutException if command didn't complete withtin the specified timeout
	 */
	public synchronized int execute(String[] cmdArray) throws Exception {
		if (cmdArray == null || cmdArray.length == 0) throw new IllegalArgumentException("cmdArray must not be null or empty");
		
		if (logger.isDebugEnabled()) logger.debug("--> execute " + UtilBase.toString(cmdArray));
		
		// Execute the command in a new thread
		final CommandThread commandThread = new CommandThread(cmdArray, logger);
		commandThread.start();

		long startTime = System.currentTimeMillis();
		do {
			try {
				Thread.sleep(500L);
			}
			catch (InterruptedException e) {
			}
		} while (commandThread.isRunning() && (System.currentTimeMillis() - startTime) < commandTimeOut);
			
		logger.debug("... execute: checking status...");
		if (commandThread.isRunning()) {
			logger.debug("Command timed out; waited " + commandTimeOut / 1000 + " seconds");
			commandThread.interrupt();
			try {
				Thread.sleep(250L);
			}
			catch (InterruptedException e) {
			}
			throw new CommandTimedOutException("Command " + UtilBase.toString(cmdArray) + " timed out; waited " + commandTimeOut / 1000
					+ " seconds");
		}
		else {
			if (commandThread.getExceptionFromCmdProcess() != null) {
				throw commandThread.getExceptionFromCmdProcess();
			}
			logger.debug("<-- execute: " + commandThread.getExitCode());
			return commandThread.getExitCode();
		}
	}
}