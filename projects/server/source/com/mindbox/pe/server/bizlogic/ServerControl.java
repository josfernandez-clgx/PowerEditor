/*
 * Created on 2004. 8. 10.
 */
package com.mindbox.pe.server.bizlogic;

/**
 * Maintains Server state. 
 * This is thread-safe.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
public class ServerControl {

	private static final ServerControl syncInstance = new ServerControl();

	private static final int STATUS_RUNNING = 1;
	private static final int STATUS_RELOADING = 2;
	private static final int STATUS_STOPPED = -1;

	private static int status = STATUS_RUNNING;

	public static boolean isServerStopped() {
		synchronized (syncInstance) {
			return status == STATUS_STOPPED;
		}
	}

	public static boolean isServerRunning() {
		synchronized (syncInstance) {
			return status == STATUS_RUNNING;
		}
	}

	public static boolean isServerReloading() {
		synchronized (syncInstance) {
			return status == STATUS_RELOADING;
		}
	}

	public static void setStatusToStopped() {
		synchronized (syncInstance) {
			status = STATUS_STOPPED;
		}
	}
	
	public static void setStatusToRunning() {
		synchronized (syncInstance) {
			status = STATUS_RUNNING;
		}
	}

	public static void setStatusToReloading() {
		synchronized (syncInstance) {
			status = STATUS_RELOADING;
		}
	}

	private ServerControl() {

	}
}