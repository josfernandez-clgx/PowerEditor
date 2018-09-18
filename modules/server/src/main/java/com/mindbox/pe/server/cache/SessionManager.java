package com.mindbox.pe.server.cache;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;

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
	private long sessionTimeoutMillis;

	private SessionManager() {
		sessionMap = Collections.synchronizedMap(new HashMap<String, PowerEditorSession>());
		reloginList = new ArrayList<String>();
		maxUserSessions = 0x7fffffff;
		init();
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

	public int countSessions() {
		return sessionMap.size();
	}

	public PowerEditorSession getSession(final String sessionId) {
		PowerEditorSession sapphiresession = null;
		sapphiresession = sessionMap.get(sessionId);
		return sapphiresession;
	}

	/**
	 * Tests if there already exists a PE session for the specified Http session id.
	 * @param sessionId session id
	 * @return <code>true</code> if there is a PE session for <code>sessionId</code>; <code>false</code>, otherwise
	 */
	public boolean hasSession(final String sessionId) {
		return sessionMap.containsKey(sessionId);
	}

	private void init() {
		maxUserSessions = ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getSession().getMaxUserSessions().intValue();
		sessionTimeoutMillis = ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getSession().getTimeOutInMin().longValue() * 60 * 1000L;
	}

	public boolean isSessionExpired(final String sessionId) {
		if (hasSession(sessionId)) {
			return (System.currentTimeMillis() - getSession(sessionId).getLastAccessedTime()) > sessionTimeoutMillis;
		}
		return false;
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

	public boolean needsRelogin(final PowerEditorSession session) {
		return needsRelogin((session == null ? null : session.getSessionId()));
	}

	public boolean needsRelogin(final String sessionID) {
		if (sessionID == null) throw new NullPointerException("Session id cannot be null");
		synchronized (reloginList) {
			return reloginList.contains(sessionID);
		}
	}

	public void registerSession(PowerEditorSession session) throws ServletActionException {
		logger.debug(">>> registerSession: " + session);
		logger.debug("	  registerSession: maxUserSessions = " + maxUserSessions);
		logger.debug("	  registerSession: sessionMap.size = " + sessionMap.size());

		terminateExpiredSessions();
		if (sessionMap.size() >= maxUserSessions) {
			logger.warn("No more user allowed: no. of current sessions = " + sessionMap.size());
			// check if the same user has sessions, if so, terminate them
			throw new ServletActionException("SessionLimitExceededMsg", "Num sessions " + sessionMap.size() + " exceeds limit of " + maxUserSessions);
		}

		String ID = session.getSessionId();
		sessionMap.put(ID, session);
		reloginList.remove(session.getSessionId());
	}

	public void resetLastAccessedTime(final String sessionId) {
		if (hasSession(sessionId)) {
			getSession(sessionId).resetLastAccessedTime();
			logDebug(logger, "reset last access time for %s", sessionId);
		}
	}

	private void terminateExpiredSessions() {
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, PowerEditorSession> entry : sessionMap.entrySet()) {
			PowerEditorSession session = entry.getValue();
			if (session != null) {
				try {
					long lastAccessedTime = session.getLastAccessedTime();
					long currentTime = System.currentTimeMillis();

					logger.debug("terminateExpiredSessions: session = " + session.getSessionId() + " for " + session.getUserID());
					logger.debug("terminateExpiredSessions: is-new = " + session.getHttpSession().isNew());
					logger.debug("terminateExpiredSessions: last-accessed = " + lastAccessedTime);
					logger.debug("terminateExpiredSessions: current-time  = " + currentTime);
					logger.debug("terminateExpiredSessions: max-inactive  = " + session.getHttpSession().getMaxInactiveInterval());

					if (session.getHttpSession().isNew()) {
						list.add((String) entry.getKey());
					}
					else if (session.getHttpSession().getMaxInactiveInterval() > 0 && session.getHttpSession().getMaxInactiveInterval() < (int) ((currentTime - lastAccessedTime) / 1000)) {
						list.add((String) entry.getKey());
					}
				}
				// thrown from HttpSession.getLastAccessedTime when session is invalidated
				catch (Exception ex) {
					logger.warn("terminateExpiredSessions: can't determine session stats; it's probably invalidated by system. It'll be removed from cache (user: " + session.getUserID() + ")", ex);
					list.add(entry.getKey());
				}
			}
		}

		for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
			String sessionID = iter.next();
			terminateSession_internal(sessionID);
		}
		logger.info("terminateExpiredSessions: removed " + list.size() + " session(s)!!!");
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

	@Override
	public String toString() {
		return "SessionManager[no. of sessions = " + sessionMap.size() + "]";
	}

	public String mapToString() {
		StringBuffer buffer = new StringBuffer("sessionMap{");
		for (Map.Entry<String, PowerEditorSession> entry : sessionMap.entrySet()) {
			buffer.append("[key=\"");
			buffer.append(entry.getKey());
			buffer.append("\",value=\"");
			buffer.append(entry.getValue().toString());
			buffer.append("\"]");
		}
		buffer.append('}');
		return buffer.toString();
	}
}
