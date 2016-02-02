/*
 * Created on 2004. 6. 25.
 */
package com.mindbox.pe.server.db.loaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseFactory;
import com.mindbox.pe.model.process.PhaseReference;
import com.mindbox.pe.model.process.ProcessRequest;
import com.mindbox.pe.model.process.UsagePhaseTask;
import com.mindbox.pe.server.cache.ProcessManager;
import com.mindbox.pe.server.config.KnowledgeBaseFilterConfig;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;

/**
 * Process Loader.
 * Responsible for loading requests and phases.
 * @author kim
 * @since PowerEditor 3.3.0
 */
public class ProcessLoader extends AbstractLoader {

	private static final String Q_LOAD_PHASE = "select phase_id,phase_type,phase_name,display_name,task_name,prereq_type from MB_PHASE order by phase_id";

	private static final String Q_LOAD_PHASE_LINK = "select parent_phase_id,child_phase_id from MB_PHASE_LINK";

	private static final String Q_LOAD_PHASE_PREREQ = "select phases_id,prereq_phase_id from MB_PHASE_PREREQ";

	private static final String Q_LOAD_REQUEST = "select request_id,request_name,request_type,display_name,description,init_function,purpose,phase_id from MB_REQUEST";

	public void load(KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws Exception {
		// NOTE: filter not used as date filter is not applicable to guideline actions
		
		ProcessManager.getInstance().startLoading();
		try {
			loadPhase();
			loadPhaseLink();
			loadPhasePrerequisite();
			loadRequest();
		}
		finally {
			ProcessManager.getInstance().finishLoading();
		}
	}

	private void loadPhase() throws SQLException {
		logger.info("=== PROCESS: PHASE ===");
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_PHASE);
			rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				int type = rs.getInt(2);
				String name = UtilBase.trim(rs.getString(3));
				String dispName = UtilBase.trim(rs.getString(4));
				String taskName = UtilBase.trim(rs.getString(5));
				int prereqType = rs.getInt(6);
				Phase phase = PhaseFactory.createPhase(type, id, name, dispName);
				if (taskName != null && taskName.length() > 0) {
					phase.setPhaseTask(new UsagePhaseTask(TemplateUsageType.valueOf(taskName)));
				}
				if (phase instanceof PhaseReference) {
					((PhaseReference) phase).setReferecePhase(PhaseFactory.createPhase(PhaseFactory.TYPE_SEQUENCE, prereqType, "", ""));
				}
				else {
					phase.setDisjunctivePrereqs(prereqType == 1);
				}
				ProcessManager.getInstance().addPhase(phase);
				logger.info("Proces-Phase: " + id + " of " + type + ",name=" + name + ",disp=" + dispName + ",task=" + taskName);
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			dbconnectionmanager.freeConnection(conn);
		}
	}

	private void loadPhaseLink() throws SQLException {
		logger.info("=== PROCESS: PHASE LINK ===");
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_PHASE_LINK);
			rs = ps.executeQuery();

			while (rs.next()) {
				int parentID = rs.getInt(1);
				int childID = rs.getInt(2);

				if (parentID == childID) {
					logger.warn("Ignored: parentID == childID: parent=" + parentID + ",child=" + childID);
				}
				else {
					Phase parent = ProcessManager.getInstance().getPhase(parentID);
					Phase child = ProcessManager.getInstance().getPhase(childID);
					if (parent != null && child != null) {
						parent.addSubPhase(child);
						child.setParent(parent);
						logger.info("Process-Phase-Link: parent=" + parentID + ",child=" + childID);
					}
					else if (parent == null) {
						logger.warn("Ignored: parent not found: parent=" + parentID + ",child=" + childID);
					}
					else {
						logger.warn("Ignored: child not found: parent=" + parentID + ",child=" + childID);
					}
				}
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			dbconnectionmanager.freeConnection(conn);
		}
	}

	private void loadPhasePrerequisite() throws SQLException {
		logger.info("=== PROCESS: PHASE PREREQUISITE ===");
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_PHASE_PREREQ);
			rs = ps.executeQuery();

			while (rs.next()) {
				int phaseID = rs.getInt(1);
				int prereqID = rs.getInt(2);

				if (phaseID == prereqID) {
					logger.warn("Ignored: parentID == childID: phase=" + phaseID + ",prereq=" + prereqID);
				}
				else {
					Phase phase = ProcessManager.getInstance().getPhase(phaseID);
					Phase prereq = ProcessManager.getInstance().getPhase(prereqID);
					if (phase != null && prereq != null) {
						phase.addPrerequisite(prereq);
						logger.info("Process-Phase-Prereq: phase=" + phaseID + ",prereq=" + prereqID);
					}
					else if (phase == null) {
						logger.warn("Ignored: phase not found: phase=" + phaseID + ",prereq=" + prereqID);
					}
					else {
						logger.warn("Ignored: prereq not found: phase=" + phaseID + ",prereq=" + prereqID);
					}
				}
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			dbconnectionmanager.freeConnection(conn);
		}
	}

	private void loadRequest() throws SQLException {
		logger.info("=== PROCESS: REQUEST ===");
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_REQUEST);
			rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String name = UtilBase.trim(rs.getString(2));
				String type = UtilBase.trim(rs.getString(3));
				String dispName = UtilBase.trim(rs.getString(4));
				String description = UtilBase.trim(rs.getString(5));
				String initFunction = UtilBase.trim(rs.getString(6));
				String purpose = UtilBase.trim(rs.getString(7));
				int phaseID = rs.getInt(8);

				Phase phase = null;
				if (phaseID > 0) {
					phase = ProcessManager.getInstance().getPhase(phaseID);
					if (phase == null) {
						logger.warn("Process-Request: phase not set - invalid phaseID: id=" + id + ",name=" + name + ",type=" + type
								+ ",func=" + initFunction + ",purpose=" + purpose + ",phaseID=" + phaseID);
					}
				}
				ProcessRequest request = new ProcessRequest(id, name, description);
				request.setRequestType(type);
				request.setInitFunction(initFunction);
				request.setPurpose(purpose);
				request.setDisplayName(dispName);
				if (phase != null) {
					request.setPhase(phase);
				}
				ProcessManager.getInstance().addRequest(request);
				logger.warn("Process-Request: id=" + id + ",name=" + name + ",type=" + type + ",func=" + initFunction + ",purpose="
						+ purpose + ",phaseID=" + phaseID);
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			dbconnectionmanager.freeConnection(conn);
		}
	}

}