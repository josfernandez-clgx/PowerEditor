/*
 * Created on 2004. 6. 25.
 */
package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseReference;
import com.mindbox.pe.model.process.ProcessRequest;

/**
 * Process manager.
 * Maintains requests and phases.
 * @author kim
 * @since PowerEditor  3.3.0
 */
public class ProcessManager extends AbstractCacheManager {

	private static ProcessManager instance = null;

	public static ProcessManager getInstance() {
		if (instance == null) {
			instance = new ProcessManager();
		}
		return instance;
	}

	private final Map<Integer, ProcessRequest> requestMap;
	private final Map<Integer, Phase> phaseMap;

	private ProcessManager() {
		requestMap = new HashMap<Integer, ProcessRequest>();
		phaseMap = new HashMap<Integer, Phase>();
	}

	public void addRequest(ProcessRequest request) {
		synchronized (requestMap) {
			requestMap.put(new Integer(request.getID()), request);
		}
	}

	public void addPhase(Phase phase) {
		synchronized (phaseMap) {
			phaseMap.put(new Integer(phase.getID()), phase);
		}
	}

	public void updatePhase(Phase phase) {
		Phase cachedPhase = getPhase_internal(phase.getID());
		cachedPhase.copyFrom(phase);
	}

	private Phase getPhase_internal(int id) {
		Integer key = new Integer(id);
		if (phaseMap.containsKey(key)) {
			return phaseMap.get(key);
		}
		else {
			return null;
		}
	}

	public Phase getPhase(int id) {
		synchronized (phaseMap) {
			return getPhase_internal(id);
		}
	}

	public void removePhase(int phaseID) {
		synchronized (phaseMap) {
			Integer key = new Integer(phaseID);
			if (phaseMap.containsKey(key)) {
				phaseMap.remove(key);
			}
		}
	}

	private ProcessRequest getRequest_internal(int id) {
		Integer key = new Integer(id);
		if (requestMap.containsKey(key)) {
			return requestMap.get(key);
		}
		else {
			return null;
		}
	}

	public ProcessRequest getRequest(int id) {
		synchronized (requestMap) {
			return getRequest_internal(id);
		}
	}

	public void removeRequest(int requestID) {
		synchronized (requestMap) {
			Integer key = new Integer(requestID);
			if (requestMap.containsKey(key)) {
				requestMap.remove(key);
			}
		}
	}

	public void updateRequest(ProcessRequest request) {
		ProcessRequest cachedRequest = getRequest_internal(request.getID());
		cachedRequest.copyFrom(request);
	}

	public List<ProcessRequest> getAllRequests() {
		List<ProcessRequest> list = new ArrayList<ProcessRequest>();
		list.addAll(requestMap.values());
		return list;
	}

	public List<Phase> getAllPhases() {
		List<Phase> list = new ArrayList<Phase>();
		list.addAll(phaseMap.values());
		return list;
	}

	public synchronized void startLoading() {
		requestMap.clear();
		phaseMap.clear();
	}

	public synchronized void finishLoading() {
		for (Iterator<Phase> iter = phaseMap.values().iterator(); iter.hasNext();) {
			Phase element = iter.next();
			if (element instanceof PhaseReference) {
				((PhaseReference) element).setReferecePhase(getPhase(((PhaseReference) element).getReferecePhase().getID()));
			}
		}
	}
}