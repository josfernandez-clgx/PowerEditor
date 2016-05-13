package com.mindbox.pe.server.migration.retired;

import static com.mindbox.pe.common.LogUtil.logDebug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.server.spi.audit.AuditSearchCriteria;

@Deprecated
public final class AuditServiceProviderRetired {

	private static final String Q_SELECT_AUDIT_EVENTS_B4WHERE = "select audit_id,audit_type_id,event_date,user_name,audit_desc from MB_AUDIT ";
	private static final String Q_SELECT_AUDIT_EVENTS_ORDER_BY = " order by audit_id";

	private static final String Q_LOAD_AUDIT_KB_MASTERS_B4ORDER = "select audit_id,kb_audit_id,kb_changed_type_id,element_id from MB_KB_AUDIT_MASTER where audit_id=? ";
	private static final String Q_LOAD_AUDIT_KB_MASTERS_ORDER_BY = " order by kb_audit_id";
	private static final String Q_LOAD_AUDIT_KB_MASTERS = Q_LOAD_AUDIT_KB_MASTERS_B4ORDER + Q_LOAD_AUDIT_KB_MASTERS_ORDER_BY;
	private static final String Q_LOAD_AUDIT_KB_DETAILS = "select kb_audit_id,kb_audit_detail_id,kb_mod_type_id,description from MB_KB_AUDIT_DETAIL"
			+ " where kb_audit_id=? order by kb_audit_detail_id";
	private static final String Q_LOAD_AUDIT_KB_DETAIL_DATA = "select kb_element_type_id,element_value from MB_KB_AUDIT_DETAIL_DATA where kb_audit_detail_id=?";

	private static final Logger LOG = Logger.getLogger(AuditServiceProviderRetired.class);

	private static MutableAuditEventRetired asMutableAuditEvent(ResultSet rs) throws SQLException, ParseException {
		MutableAuditEventRetired auditEvent = new MutableAuditEventRetired();
		auditEvent.setAuditID(rs.getInt(1));
		auditEvent.setAuditType(AuditEventType.forID(rs.getInt(2)));
		auditEvent.setDate(DBUtil.getDateValue(rs, 3));
		auditEvent.setUserName(UtilBase.trim(rs.getString(4)));
		auditEvent.setDescription(UtilBase.trim(rs.getString(5)));
		return auditEvent;
	}

	private static MutableAuditKBDetailRetired asMutableAuditKBDetail(ResultSet rs) throws SQLException {
		MutableAuditKBDetailRetired detail = new MutableAuditKBDetailRetired();
		detail.setKbAuditID(rs.getInt(1));
		detail.setKbAuditDetailID(rs.getInt(2));
		detail.setKbModTypeID(rs.getInt(3));
		detail.setDescription(UtilBase.trim(rs.getString(4)));
		return detail;
	}

	private static MutableAuditKBMasterRetired asMutableAuditKBMaster(ResultSet rs) throws SQLException {
		MutableAuditKBMasterRetired master = new MutableAuditKBMasterRetired();
		master.setKbAuditID(rs.getInt(2));
		master.setKbChangedTypeID(rs.getInt(3));
		master.setElementID(rs.getInt(4));
		return master;
	}

	private static String getAuditMasterSelectQuery(int[] kbChangedElementTypes) {
		if (UtilBase.isEmpty(kbChangedElementTypes)) {
			return Q_LOAD_AUDIT_KB_MASTERS;
		}
		else {
			return Q_LOAD_AUDIT_KB_MASTERS_B4ORDER + " and kb_changed_type_id in (" + UtilBase.toString(kbChangedElementTypes) + ")" + Q_LOAD_AUDIT_KB_MASTERS_ORDER_BY;
		}
	}

	private PreparedStatement getAuditEventRetrievePS(Connection connection, int[] types, Date beginDate, Date endDate) throws SQLException {
		StringBuilder buff = new StringBuilder(Q_SELECT_AUDIT_EVENTS_B4WHERE);
		if (!UtilBase.isEmpty(types) || beginDate != null || endDate != null) {
			buff.append("WHERE ");
			if (!UtilBase.isEmpty(types)) {
				buff.append("audit_type_id in (");
				buff.append(UtilBase.toString(types));
				buff.append(')');
			}
			if (beginDate != null) {
				if (!UtilBase.isEmpty(types)) buff.append(" AND ");
				buff.append("event_date >= ?");
			}
			if (endDate != null) {
				if (!UtilBase.isEmpty(types) || beginDate != null) buff.append(" AND ");
				buff.append("event_date <= ?");
			}
		}
		buff.append(Q_SELECT_AUDIT_EVENTS_ORDER_BY);
		PreparedStatement ps = connection.prepareStatement(buff.toString());
		try {
			if (beginDate != null) {
				DBUtil.setDateValue(ps, 1, beginDate);
			}
			if (endDate != null) {
				DBUtil.setDateValue(ps, (beginDate == null ? 1 : 2), endDate);
			}
			return ps;
		}
		catch (SQLException ex) {
			if (ps != null) {
				ps.close();
				ps = null;
			}
			throw ex;
		}
	}

	public List<AuditEventRetired> retrieveAuditEvents(AuditSearchCriteria auditSearchCriteria) throws ServiceException {
		if (auditSearchCriteria == null) throw new NullPointerException("auditSearchCriteria cannot be null");
		logDebug(LOG, "--> retrieveAuditEvents: %s", auditSearchCriteria);
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();

			// load all events first
			ps = getAuditEventRetrievePS(connection, auditSearchCriteria.getAuditTypes(), auditSearchCriteria.getBeginDate(), auditSearchCriteria.getEndDate());
			rs = ps.executeQuery();

			List<MutableAuditEventRetired> mutableAuditEventList = new LinkedList<MutableAuditEventRetired>();
			while (rs.next()) {
				mutableAuditEventList.add(asMutableAuditEvent(rs));
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;

			// load all kb masters
			final String masterSelectQuery = getAuditMasterSelectQuery(auditSearchCriteria.getKbModifiedElementTypes());
			ps = connection.prepareStatement(masterSelectQuery);
			for (Iterator<MutableAuditEventRetired> iter = mutableAuditEventList.iterator(); iter.hasNext();) {
				MutableAuditEventRetired auditEvent = iter.next();
				ps.setInt(1, auditEvent.getAuditID());
				rs = ps.executeQuery();
				while (rs.next()) {
					auditEvent.add(asMutableAuditKBMaster(rs));
				}
				rs.close();
				rs = null;

				// if mod element types are specified and no KB master if found, remove the audit event
				if (!UtilBase.isEmpty(auditSearchCriteria.getKbModifiedElementTypes())) {
					if (auditEvent.kbMasterCount() == 0) {
						iter.remove();
					}
				}
			}
			ps.close();
			ps = null;

			// load all kb details
			ps = connection.prepareStatement(Q_LOAD_AUDIT_KB_DETAILS);
			for (Iterator<MutableAuditEventRetired> iter = mutableAuditEventList.iterator(); iter.hasNext();) {
				MutableAuditEventRetired auditEvent = iter.next();
				for (int i = 0; i < auditEvent.kbMasterCount(); i++) {
					AuditKBMasterRetired auditMaster = auditEvent.getKBMaster(i);
					ps.setInt(1, auditMaster.getKbAuditID());
					rs = ps.executeQuery();
					while (rs.next()) {
						MutableAuditKBDetailRetired detail = asMutableAuditKBDetail(rs);
						auditMaster.add(detail);
						setDetailData(connection, detail);
					}
					rs.close();
					rs = null;
				}
			}
			ps.close();
			ps = null;
			List<AuditEventRetired> auditEvents = new LinkedList<AuditEventRetired>();
			auditEvents.addAll(mutableAuditEventList);

			logDebug(LOG, "Found total of %d audit events", auditEvents.size());

			return Collections.unmodifiableList(auditEvents);
		}
		catch (Exception ex) {
			LOG.error("Failed to load audit details", ex);
			throw new ServiceException("Failed to load audit details", ex);
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			}
			catch (SQLException ex) {
				LOG.warn("Failed to release DB resources", ex);
			}
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void setDetailData(Connection conn, MutableAuditKBDetailRetired detail) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_AUDIT_KB_DETAIL_DATA);
			ps.setInt(1, detail.getKbAuditDetailID());
			rs = ps.executeQuery();
			while (rs.next()) {
				int elementTypeID = rs.getInt(1);
				String elementValue = UtilBase.trim(rs.getString(2));
				detail.add(new DefaultAuditKBDetailDataRetired(detail.getKbAuditDetailID(), elementTypeID, elementValue));
			}
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			}
			catch (SQLException ex) {
				Logger.getLogger(AuditServiceProviderRetired.class).warn("Failed to release DB resources", ex);
			}
		}
	}

}
