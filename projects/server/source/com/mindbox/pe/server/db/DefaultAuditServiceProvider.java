package com.mindbox.pe.server.db;

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
import com.mindbox.pe.server.spi.AuditServiceProvider;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.server.spi.audit.AuditKBDetail;
import com.mindbox.pe.server.spi.audit.AuditKBDetailData;
import com.mindbox.pe.server.spi.audit.AuditKBMaster;
import com.mindbox.pe.server.spi.audit.AuditSearchCriteria;
import com.mindbox.pe.server.spi.audit.DefaultAuditKBDetailData;
import com.mindbox.pe.server.spi.audit.MutableAuditEvent;
import com.mindbox.pe.server.spi.audit.MutableAuditKBDetail;
import com.mindbox.pe.server.spi.audit.MutableAuditKBMaster;


public final class DefaultAuditServiceProvider implements AuditServiceProvider {

	private static final String Q_SELECT_AUDIT_EVENTS_B4WHERE = "select audit_id,audit_type_id,event_date,user_name from MB_AUDIT ";
	private static final String Q_SELECT_AUDIT_EVENTS_ORDER_BY = " order by audit_id";

	private static final String Q_LOAD_AUDIT_KB_MASTERS_B4ORDER = "select audit_id,kb_audit_id,kb_changed_type_id,element_id from MB_KB_AUDIT_MASTER where audit_id=? ";
	private static final String Q_LOAD_AUDIT_KB_MASTERS_ORDER_BY = " order by kb_audit_id";
	private static final String Q_LOAD_AUDIT_KB_MASTERS = Q_LOAD_AUDIT_KB_MASTERS_B4ORDER + Q_LOAD_AUDIT_KB_MASTERS_ORDER_BY;

	private static final String Q_LOAD_AUDIT_KB_DETAILS = "select kb_audit_id,kb_audit_detail_id,kb_mod_type_id,description from MB_KB_AUDIT_DETAIL"
			+ " where kb_audit_id=? order by kb_audit_detail_id";

	private static final String Q_LOAD_AUDIT_KB_DETAIL_DATA = "select kb_element_type_id,element_value from MB_KB_AUDIT_DETAIL_DATA where kb_audit_detail_id=?";

	private static final String Q_INSERT_AUDIT_EVENT = "insert into MB_AUDIT "
			+ "(audit_id, audit_type_id, user_name, event_date) values (?,?,?,?)";
	private static final String Q_INSERT_KB_AUDIT_MASTER = "insert into MB_KB_AUDIT_MASTER "
			+ "(audit_id, kb_audit_id, kb_changed_type_id, element_id) values (?,?,?,?)";
	private static final String Q_INSERT_KB_AUDIT_DETAIL = "insert into MB_KB_AUDIT_DETAIL "
			+ "(kb_audit_detail_id, kb_audit_id, kb_mod_type_id, description) values (?,?,?,?)";
	private static final String Q_INSERT_KB_AUDIT_DETAIL_DATA = "insert into MB_KB_AUDIT_DETAIL_DATA "
			+ "(kb_audit_detail_id, kb_element_type_id, element_value) values (?,?,?)";

	private static String getAuditMasterSelectQuery(int[] kbChangedElementTypes) {
		if (UtilBase.isEmpty(kbChangedElementTypes)) {
			return Q_LOAD_AUDIT_KB_MASTERS;
		}
		else {
			return Q_LOAD_AUDIT_KB_MASTERS_B4ORDER + " and kb_changed_type_id in (" + UtilBase.toString(kbChangedElementTypes) + ")"
					+ Q_LOAD_AUDIT_KB_MASTERS_ORDER_BY;
		}
	}


	private final Logger logger = Logger.getLogger(getClass());

	public void insert(AuditEvent auditEvent) throws ServiceException {
		if (auditEvent == null) throw new NullPointerException();
		Connection connection = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(false);
			insertEvent(connection, auditEvent);
			if (auditEvent.kbMasterCount() > 0) {
				for (int i = 0; i < auditEvent.kbMasterCount(); i++) {
					insertMaster(connection, auditEvent.getAuditID(), auditEvent.getKBMaster(i));
				}
			}
			connection.commit();
		}
		catch (Exception ex) {
			DBUtil.rollBackLocallyManagedConnection(connection);
			logger.error("Failed to insert audit event " + auditEvent, ex);
			throw new ServiceException("Failed to insert audit event", ex);
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void insertEvent(Connection connection, AuditEvent auditEvent) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_INSERT_AUDIT_EVENT);
			ps.setInt(1, auditEvent.getAuditID());
			ps.setInt(2, auditEvent.getAuditType().getId());
			ps.setString(3, auditEvent.getUserName());
			DBUtil.setDateValue(ps, 4, auditEvent.getDate());
			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("No row inserted for audit event");
			}
		}
		finally {
			DBUtil.closeLocallyManagedStatement(ps);
		}
	}

	private void insertMaster(Connection connection, int auditID, AuditKBMaster auditMaster) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_INSERT_KB_AUDIT_MASTER);
			ps.setInt(1, auditID);
			ps.setInt(2, auditMaster.getKbAuditID());
			ps.setInt(3, auditMaster.getKbChangedTypeID());
			ps.setInt(4, auditMaster.getElementID());
			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("No row inserted for " + auditMaster);
			}
			insertDetail(connection, auditMaster);
		}
		finally {
			DBUtil.closeLocallyManagedStatement(ps);
		}
	}

	private void insertDetail(Connection connection, AuditKBMaster auditMaster) throws SQLException {
		if (auditMaster == null) {
			throw new NullPointerException();
		}
		if (auditMaster.detailCount() > 0) {
			PreparedStatement ps = null;
			try {
				ps = connection.prepareStatement(Q_INSERT_KB_AUDIT_DETAIL);
				for (int i = 0; i < auditMaster.detailCount(); i++) {
					AuditKBDetail auditDetail = auditMaster.getDetail(i);
					ps.setInt(1, auditDetail.getKbAuditDetailID());
					ps.setInt(2, auditDetail.getKbAuditID());
					ps.setInt(3, auditDetail.getKbModTypeID());
					ps.setString(4, auditDetail.getDescription());
					int count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("No row inserted for " + auditDetail);
					}
					insertDetailData(connection, auditDetail);
				}
			}
			finally {
				DBUtil.closeLocallyManagedStatement(ps);
			}
		}
	}

	private void insertDetailData(Connection connection, AuditKBDetail auditDetail) throws SQLException {
		if (auditDetail == null) {
			throw new NullPointerException();
		}
		if (auditDetail.detailDataCount() > 0) {
			PreparedStatement ps = null;
			try {
				ps = connection.prepareStatement(Q_INSERT_KB_AUDIT_DETAIL_DATA);
				for (int i = 0; i < auditDetail.detailDataCount(); i++) {
					AuditKBDetailData auditDetailData = auditDetail.getDetailData(i);
					ps.setInt(1, auditDetailData.getKbAuditDetailID());
					ps.setInt(2, auditDetailData.getElementTypeID());
					ps.setString(3, auditDetailData.getElementValue());
					int count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("No row inserted for " + auditDetail);
					}
				}
			}
			finally {
				DBUtil.closeLocallyManagedStatement(ps);
			}
		}
	}

	private static MutableAuditEvent asMutableAuditEvent(ResultSet rs) throws SQLException, ParseException {
		MutableAuditEvent auditEvent = new MutableAuditEvent();
		auditEvent.setAuditID(rs.getInt(1));
		auditEvent.setAuditType(AuditEventType.forID(rs.getInt(2)));
		auditEvent.setDate(DBUtil.getDateValue(rs, 3));
		auditEvent.setUserName(UtilBase.trim(rs.getString(4)));
		return auditEvent;
	}

	private static MutableAuditKBMaster asMutableAuditKBMaster(ResultSet rs) throws SQLException {
		MutableAuditKBMaster master = new MutableAuditKBMaster();
		master.setKbAuditID(rs.getInt(2));
		master.setKbChangedTypeID(rs.getInt(3));
		master.setElementID(rs.getInt(4));
		return master;
	}

	private static MutableAuditKBDetail asMutableAuditKBDetail(ResultSet rs) throws SQLException {
		MutableAuditKBDetail detail = new MutableAuditKBDetail();
		detail.setKbAuditID(rs.getInt(1));
		detail.setKbAuditDetailID(rs.getInt(2));
		detail.setKbModTypeID(rs.getInt(3));
		detail.setDescription(UtilBase.trim(rs.getString(4)));
		return detail;
	}

	private PreparedStatement getAuditEventRetrievePS(Connection connection, int[] types, Date beginDate, Date endDate) throws SQLException {
		StringBuffer buff = new StringBuffer(Q_SELECT_AUDIT_EVENTS_B4WHERE);
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

	public List<AuditEvent> retrieveAuditEvents(AuditSearchCriteria auditSearchCriteria) throws ServiceException {
		if (auditSearchCriteria == null) throw new NullPointerException("auditSearchCriteria cannot be null");
		logger.debug("--> retrieveAuditEvents: " + auditSearchCriteria);
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();

			// load all events first
			ps = getAuditEventRetrievePS(
					connection,
					auditSearchCriteria.getAuditTypes(),
					auditSearchCriteria.getBeginDate(),
					auditSearchCriteria.getEndDate());
			rs = ps.executeQuery();

			List<MutableAuditEvent> mutableAuditEventList = new LinkedList<MutableAuditEvent>();
			while (rs.next()) {
				mutableAuditEventList.add(asMutableAuditEvent(rs));
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;

			// load all kb masters
			String masterSelectQuery = getAuditMasterSelectQuery(auditSearchCriteria.getKbModifiedElementTypes());
			logger.debug("    retrieveAuditEvents: masterSelectQuery = " + masterSelectQuery);
			ps = connection.prepareStatement(masterSelectQuery);
			for (Iterator<MutableAuditEvent> iter = mutableAuditEventList.iterator(); iter.hasNext();) {
				MutableAuditEvent auditEvent = iter.next();
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
			for (Iterator<MutableAuditEvent> iter = mutableAuditEventList.iterator(); iter.hasNext();) {
				MutableAuditEvent auditEvent = iter.next();
				for (int i = 0; i < auditEvent.kbMasterCount(); i++) {
					MutableAuditKBMaster auditMaster = (MutableAuditKBMaster) auditEvent.getKBMaster(i);
					ps.setInt(1, auditMaster.getKbAuditID());
					rs = ps.executeQuery();
					while (rs.next()) {
						MutableAuditKBDetail detail = asMutableAuditKBDetail(rs);
						auditMaster.add(detail);
						setDetailData(connection, detail);
					}
					rs.close();
					rs = null;
				}
			}
			ps.close();
			ps = null;
			List<AuditEvent> auditEvents = new LinkedList<AuditEvent>();
			auditEvents.addAll(mutableAuditEventList);
			return Collections.unmodifiableList(auditEvents);
		}
		catch (Exception ex) {
			logger.error("Failed to load audit details", ex);
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
				logger.warn("Failed to release DB resources", ex);
			}
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void setDetailData(Connection conn, MutableAuditKBDetail detail) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_AUDIT_KB_DETAIL_DATA);
			ps.setInt(1, detail.getKbAuditDetailID());
			rs = ps.executeQuery();
			while (rs.next()) {
				int elementTypeID = rs.getInt(1);
				String elementValue = UtilBase.trim(rs.getString(2));
				detail.add(new DefaultAuditKBDetailData(detail.getKbAuditDetailID(), elementTypeID, elementValue));
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
				Logger.getLogger(DefaultAuditServiceProvider.class).warn("Failed to release DB resources", ex);
			}
		}
	}

}
