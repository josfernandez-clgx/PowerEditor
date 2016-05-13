/*
 * Created on 2005. 6. 28.
 *
 */
package com.mindbox.pe.common.validate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Warning info warning consumer. 
 * This is a thread-safe implementation of {@link com.mindbox.pe.common.validate.WarningConsumer}.
 * @author Geneho Kim
 * @since PowerEditor 4.3.2
 */
public class WarningInfoWarningConsumer implements WarningConsumer {

	private List<WarningInfo> warningList = new LinkedList<WarningInfo>();


	public void addWarning(int level, String message) {
		addWarning(level, message, null);
	}

	public void addWarning(int level, String message, String resource) {
		synchronized (warningList) {
			warningList.add(new WarningInfo(level, message, resource));
		}
	}

	public void addWarning(WarningInfo warningInfo) {
		synchronized (warningList) {
			warningList.add(warningInfo);
		}
	}

	public boolean hasWarnings() {
		synchronized (warningList) {
			return !warningList.isEmpty();
		}
	}

	/**
	 * Gets list of {@link WarningInfo} objects.
	 */
	public List<WarningInfo> getAllWarnings() {
		synchronized (warningList) {
			return Collections.unmodifiableList(warningList);
		}
	}

	public void clearWarnings() {
		synchronized (warningList) {
			warningList.clear();
		}
	}
}