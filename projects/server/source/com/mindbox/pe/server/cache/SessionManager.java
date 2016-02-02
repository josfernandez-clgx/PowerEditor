package com.mindbox.pe.server.cache;

import static com.mindbox.pe.common.LogUtil.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.PowerEditorSession;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Manages user sessions.
 * Basic session management functionality is provided by J2EE container (or Servlet container). 
 * This class enforces concurrent session limits.
 * @author Geneho Kim
 * @since PowerEditor 1.0
 */
public class SessionManager extends AbstractCacheManager {

	private static SessionManager instance = null;

	public static synchronized SessionManager getInstance() {
		if (instance == null) instance = new SessionManager();
		return instance;
	}

	private final Map<String, PowerEditorSession> sessionMap;
	private final List<String> reloginList; // contains session ids
	private int maxUserSessions;

	private SessionManager() {
		sessionMap = Collections.synchronizedMap(new HashMap<String, PowerEditorSession>());
		reloginList = new ArrayList<String>();
		maxUserSessions = 0x7fffffff;
		init();
	}

	public int countSessions() {
		return sessionMap.size();
	}

	public void registerSession(PowerEditorSession session) throws ServletActionException {
		logger.debug(">>> registerSession: " + session);
		logger.debug("    registerSession: maxUserSessions = " + maxUserSessions);
		logger.debug("    registerSession: sessionMap.size = " + sessionMap.size());

		terminateExpiredSessions();
		if (sessionMap.size() >= maxUserSessions) {
			logger.warn("No more user allowed: no. of current sessions = " + sessionMap.size());
			// check if the same user has sessions, if so, terminate them
			throw new ServletActionException("SessionLimitExceededMsg", "Num sessions " + sessionMap.size() + " exceeds limit of " + maxUserSessions);
		}

		sessionMap.put(session.getSessionId(), session);
		reloginList.remove(session.getSessionId());
	}

	public void markAllSessionForRefresh() {
		synchronized (reloginList) {
			for (Iterator<String> iter = sessionMap.keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				if (!reloginList.contains(key)) {
					reloginList.add(key);
				}
			}
		}
	}

	public boolean needsRelogin(String sessionID) {
		if (sessionID == null) throw new NullPointerException("Session id cannot be null");
		synchronized (reloginList) {
			return reloginList.contains(sessionID);
		}
	}

	public boolean needsRelogin(PowerEditorSession session) {
		return needsRelogin((session == null ? null : session.getSessionId()));
	}

	public void terminateSession(String sessionID) {
		logInfo(logger, "Terminiating session %s...", sessionID);
		terminateSession_internal(sessionID);
	}

	private void terminateSession_internal(String sessionID) {
		logger.info("terminating session " + sessionID);
		PowerEditorSession peSession = sessionMap.remove(sessionID);
		if (peSession != null && peSession.getHttpSession() != null) {
			reloginList.remove(sessionID);
			try {
				peSession.getHttpSession().invalidate();
			}
			catch (IllegalStateException ex) {
				// session was already invalidated. do nothing!
			}
		}

		logger.info("terminateSession: size now=" + sessionMap.size());
	}

	public boolean authenticateSession(String userId, String sessionId) {
		logDebug(logger, ">>> authenticateSession: %s, %s", userId, sessionId);
		
		boolean flag = false;
		PowerEditorSession powerEditorSession = getSession(sessionId);
		logDebug(logger, "... authenticateSession: peSession = %s", powerEditorSession);
		if (powerEditorSession == null) {
			flag = false;
		}
		else if (powerEditorSession.getUserID().equals(userId)) {
			flag = true;
		}

		logDebug(logger, "<<< authenticateSession: %b", flag);
		return flag;
	}

	/**
	 * Tests if there already exists a PE session for the specified Http session id.
	 * @param sessionId session id
	 * @return <code>true</code> if there is a PE session for <code>sessionId</code>; <code>false</code>, otherwise 
	 */
	public boolean hasSession(String sessionId) {
		return sessionMap.containsKey(sessionId);
	}

	public PowerEditorSession getSession(String sessionId) {
		PowerEditorSession sapphiresession = null;
		sapphiresession = sessionMap.get(sessionId);
		return sapphiresession;
	}

	private void init() {
		maxUserSessions = ConfigurationManager.getInstance().getSessionConfiguration().getMaxUserSessions();
	}

	public String toString() {
		return "SessionManager[no. of sessions = " + sessionMap.size() + "]";
	}

	private void terminateExpiredSessions() {
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, PowerEditorSession> entry : sessionMap.entrySet()) {
			PowerEditorSession session = entry.getValue();
			if (session != null) {
				try {
					long lastAccessedTime = session.getHttpSession().getLastAccessedTime();
					long currentTime = System.currentTimeMillis();

					logger.debug("terminateExpiredSessions: session = " + session.getSessionId() + " for " + session.getUserID());
					logger.debug("terminateExpiredSessions: is-new = " + session.getHttpSession().isNew());
					logger.debug("terminateExpiredSessions: last-accessed = " + lastAccessedTime);
					logger.debug("terminateExpiredSessions: current-time  = " + currentTime);
					logger.debug("terminateExpiredSessions: max-inactive  = " + session.getHttpSession().getMaxInactiveInterval());

					if (session.getHttpSession().isNew()) {
						list.add((String) entry.getKey());
					}
					else if (session.getHttpSession().getMaxInactiveInterval() > 0
							&& session.getHttpSession().getMaxInactiveInterval() < (int) ((currentTime - lastAccessedTime) / 1000)) {
						list.add((String) entry.getKey());
					}
				}
				// thrown from HttpSession.getLastAccessedTime when session is invalidated
				catch (Exception ex) {
					logger.warn(
							"terminateExpiredSessions: can't determine session stats; it's probably invalidated by system. It'll be removed from cache (user: "
									+ session.getUserID() + ")",
							ex);
					list.add((String) entry.getKey());
				}
			}
		}

		for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
			String sessionID = iter.next();
			terminateSession_internal(sessionID);
		}
		logger.info("terminateExpiredSessions: removed " + list.size() + " session(s)!!!");
	}

}