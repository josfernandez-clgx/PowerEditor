package com.mindbox.pe.common.timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Timeout Controller.
 * This is thread-safe.
 *
 */
public class TimeOutController {

	private class AboutToTimeOutWork implements Runnable {
		private final long remainingTime;

		private AboutToTimeOutWork(long remainingTime) {
			super();
			this.remainingTime = remainingTime;
		}

		@Override
		public void run() {
			notifyAboutToTimedOut(remainingTime, noticiationTimeUnit);
		}
	}

	private class TimedOutWork implements Runnable {
		@Override
		public void run() {
			notifyTimedOut();
		}
	}

	private final ScheduledExecutorService scheduledExecutorService;
	private final long timeOutInSeconds;
	private final List<Future<?>> scheduledFutures = new ArrayList<Future<?>>();
	private final List<TimeOutListener> timeOutListeners = new ArrayList<TimeOutListener>();
	private final List<Long> notificationIntervals;
	private final TimeUnit noticiationTimeUnit;

	public TimeOutController(final long timeOutInSeconds, final List<Long> notificationIntervals, final TimeUnit notificationTimeUnit) {
		this.timeOutInSeconds = timeOutInSeconds;
		this.scheduledExecutorService = Executors.newScheduledThreadPool(notificationIntervals.size() + 1);
		this.notificationIntervals = new ArrayList<Long>(notificationIntervals);
		this.noticiationTimeUnit = notificationTimeUnit;
		// validate notitication intervals
		for (final long notificationInterval : notificationIntervals) {
			if ((timeOutInSeconds - TimeUnit.SECONDS.convert(notificationInterval, noticiationTimeUnit)) <= 0) {
				throw new IllegalArgumentException(String.format("The notification interval %d in %s is more than the timeout (%d sec.)", notificationInterval, notificationTimeUnit, timeOutInSeconds));
			}
		}
	}

	public void addTimeOutListener(final TimeOutListener timeOutListener) {
		synchronized (timeOutListeners) {
			timeOutListeners.add(timeOutListener);
		}
	}

	private void cancelAllTimers() {
		for (final Future<?> future : scheduledFutures) {
			if (!future.isDone()) {
				future.cancel(true);
			}
		}
	}

	private void notifyAboutToTimedOut(final long remainingTime, final TimeUnit timeUnit) {
		synchronized (timeOutListeners) {
			for (final TimeOutListener timeOutListener : timeOutListeners) {
				timeOutListener.aboutToTimeOut(remainingTime, timeUnit);
			}
		}
	}

	private void notifyTimedOut() {
		synchronized (timeOutListeners) {
			for (final TimeOutListener timeOutListener : timeOutListeners) {
				timeOutListener.timedOut();
			}
		}
	}

	public void removeTimeOutListener(final TimeOutListener timeOutListener) {
		synchronized (timeOutListeners) {
			timeOutListeners.remove(timeOutListener);
		}
	}

	public void restartTimer() {
		synchronized (scheduledFutures) {
			cancelAllTimers();
			scheduledFutures.clear();
			startAllTimers();
		}
	}

	private void startAllTimers() {
		scheduledFutures.add(scheduledExecutorService.schedule(new TimedOutWork(), timeOutInSeconds, TimeUnit.SECONDS));
		for (final long notificationInterval : notificationIntervals) {
			final long runTime = (timeOutInSeconds - TimeUnit.SECONDS.convert(notificationInterval, noticiationTimeUnit));
			scheduledFutures.add(scheduledExecutorService.schedule(new AboutToTimeOutWork(notificationInterval), runTime, TimeUnit.SECONDS));
		}
	}
}
