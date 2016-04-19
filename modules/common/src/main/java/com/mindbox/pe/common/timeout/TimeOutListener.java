package com.mindbox.pe.common.timeout;

import java.util.concurrent.TimeUnit;

public interface TimeOutListener {

	void aboutToTimeOut(long remainingTime, TimeUnit timeUnit);

	void timedOut();
}
