package com.mindbox.pe.server.db;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.UtilBase.isEmpty;
import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;
import static com.mindbox.pe.common.UtilBase.trim;
import static com.mindbox.pe.common.XmlUtil.marshal;
import static com.mindbox.pe.common.XmlUtil.unmarshal;

import java.io.StringReader;
import java.io.StringWriter;
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

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.spi.AuditServiceProvider;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.server.spi.audit.AuditKBMaster;
import com.mindbox.pe.server.spi.audit.AuditSearchCriteria;
import com.mindbox.pe.server.spi.audit.DefaultAuditKBMaster;
import com.mindbox.pe.server.spi.audit.MutableAuditEvent;
import com.mindbox.pe.xsd.audit.ChangeDetail;

public final class DefaultAuditServiceProvider implements AuditServiceProvider {

	private static final String Q_SELECT_AUDIT_EVENTS_B4WHERE = "select audit_id,audit_type_id,event_date,user_name,audit_desc from MB_AUDIT ";
	private static final String Q_SELECT_AUDIT_EVENTS_ORDER_BY = " order by audit_id";

	private static final String Q_LOAD_AUDIT_KB_MASTERS_B4ORDER = "select audit_id,kb_audit_id,kb_changed_type_id,element_id from MB_KB_AUDIT_MASTER where audit_id=? ";
	private static final String Q_LOAD_AUDIT_KB_MASTERS_ORDER_BY = " order by kb_audit_id";
	private static final String Q_LOAD_AUDIT_KB_MASTERS = Q_LOAD_AUDIT_KB_MASTERS_B4ORDER + Q_LOAD_AUDIT_KB_MASTERS_ORDER_BY;
	private static final String Q_LOAD_AUDIT_KB_CHANGE_DETAILS = "select kb_audit_change_detail,kb_audit_id from MB_KB_AUDIT_CHANGES where kb_audit_id=?";

	private static final String Q_INSERT_AUDIT_EVENT = "insert into MB_AUDIT " + "(audit_id, audit_type_id, user_name, event_date, audit_desc) values (?,?,?,?,?)";
	private static final String Q_INSERT_KB_AUDIT_MASTER = "insert into MB_KB_AUDIT_MASTER " + "(audit_id, kb_audit_id, kb_changed_type_id, element_id) values (?,?,?,?)";
	private static final String Q_INSERT_KB_AUDIT_CHANGE_DETAIL = "insert into MB_KB_AUDIT_CHANGES (kb_audit_id,kb_audit_change_detail) values (?,?)";

	private static final Logger LOG = Logger.getLogger(DefaultAuditServiceProvider.class);

	private static MutableAuditEvent asMutableAuditEvent(ResultSet rs) throws SQLException, ParseException {
		MutableAuditEvent auditEvent = new MutableAuditEvent();
		auditEvent.setAuditID(rs.getInt(1));
		auditEvent.setAuditType(AuditEventType.forID(rs.getInt(2)));
		auditEvent.setDate(DBUtil.getDateValue(rs, 3));
		auditEvent.setUserName(trim(rs.getString(4)));
		auditEvent.setDescription(trim(rs.getString(5)));
		return auditEvent;
	}

	private static DefaultAuditKBMaster asMutableAuditKBMaster(ResultSet rs) throws SQLException {
		final int kbAuditId = rs.getInt(2);
		final int kbChangedTypeId = rs.getInt(3);
		final int elementId = rs.getInt(4);
		return new DefaultAuditKBMaster(kbAuditId, kbChangedTypeId, elementId);
	}

	private static String getAuditMasterSelectQuery(int[] kbChangedElementTypes) {
		if (isEmpty(kbChangedElementTypes)) {
			return Q_LOAD_AUDIT_KB_MASTERS;
		}
		else {
			return Q_LOAD_AUDIT_KB_MASTERS_B4ORDER + " and kb_changed_type_id in (" + UtilBase.toString(kbChangedElementTypes) + ")" + Q_LOAD_AUDIT_KB_MASTERS_ORDER_BY;
		}
	}

	private PreparedStatement getAuditEventRetrievePS(Connection connection, int[] types, Date beginDate, Date endDate) throws SQLException {
		StringBuilder buff = new StringBuilder(Q_SELECT_AUDIT_EVENTS_B4WHERE);
		if (!isEmpty(types) || beginDate != null || endDate != null) {
			buff.append("WHERE ");
			if (!isEmpty(types)) {
				buff.append("audit_type_id in (");
				buff.append(UtilBase.toString(types));
				buff.append(')');
			}
			if (beginDate != null) {
				if (!isEmpty(types)) buff.append(" AND ");
				buff.append("event_date >= ?");
			}
			if (endDate != null) {
				if (!isEmpty(types) || beginDate != null) buff.append(" AND ");
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

	@Override
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
			LOG.error("Failed to insert audit event " + auditEvent, ex);
			throw new ServiceException("Failed to insert audit event", ex);
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	@Override
	public void insertChangeDetails(Connection connection, final int kbAuditId, final List<ChangeDetail> changeDetails) throws SQLException, JAXBException {
		if (kbAuditId < 1) {
			throw new IllegalArgumentException("kbAuditId must be positive");
		}
		if (changeDetails == null) {
			throw new IllegalArgumentException("changeDetails cannot be null");
		}

		if (!changeDetails.isEmpty()) {
			PreparedStatement ps = null;
			try {
				ps = connection.prepareStatement(Q_INSERT_KB_AUDIT_CHANGE_DETAIL);
				ps.setInt(1, kbAuditId);
				for (final ChangeDetail changeDetail : changeDetails) {
					final StringWriter stringWriter = new StringWriter();
					marshal(changeDetail, stringWriter, true, false, ChangeDetail.class);
					ps.setString(2, stringWriter.toString());
					int count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("No row inserted for " + changeDetail);
					}
				}
			}
			finally {
				DBUtil.closeLocallyManagedStatement(ps);
			}
		}
	}

	private void insertDetails(Connection connection, AuditKBMaster auditMaster) throws SQLException, JAXBException {
		assert (auditMaster != null);
		insertChangeDetails(connection, auditMaster.getKbAuditID(), auditMaster.getChangeDetails());
	}

	private void insertEvent(Connection connection, AuditEvent auditEvent) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(Q_INSERT_AUDIT_EVENT);
			ps.setInt(1, auditEvent.getAuditID());
			ps.setInt(2, auditEvent.getAuditType().getId());
			ps.setString(3, auditEvent.getUserName());
			DBUtil.setDateValue(ps, 4, auditEvent.getDate());
			ps.setString(5, auditEvent.getDescription());
			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("No row inserted for audit event");
			}
		}
		finally {
			DBUtil.closeLocallyManagedStatement(ps);
		}
	}

	private void insertMaster(Connection connection, int auditID, AuditKBMaster auditMaster) throws SQLException, JAXBException {
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
			insertDetails(connection, auditMaster);
		}
		finally {
			DBUtil.closeLocallyManagedStatement(ps);
		}
	}

	@Override
	public List<AuditEvent> retrieveAuditEvents(AuditSearchCriteria auditSearchCriteria) throws ServiceException {
		if (auditSearchCriteria == null) {
			throw new NullPointerException("auditSearchCriteria cannot be null");
		}

		logDebug(LOG, "--> retrieveAuditEvents: %s", auditSearchCriteria);

		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();

			// load all events first
			ps = getAuditEventRetrievePS(connection, auditSearchCriteria.getAuditTypes(), auditSearchCriteria.getBeginDate(), auditSearchCriteria.getEndDate());
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
			final String masterSelectQuery = getAuditMasterSelectQuery(auditSearchCriteria.getKbModifiedElementTypes());
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
				if (!isEmpty(auditSearchCriteria.getKbModifiedElementTypes())) {
					if (auditEvent.kbMasterCount() == 0) {
						iter.remove();
					}
				}
			}
			ps.close();
			ps = null;

			// load all kb details
			ps = connection.prepareStatement(Q_LOAD_AUDIT_KB_CHANGE_DETAILS);
			for (Iterator<MutableAuditEvent> iter = mutableAuditEventList.iterator(); iter.hasNext();) {
				final MutableAuditEvent auditEvent = iter.next();
				for (int i = 0; i < auditEvent.kbMasterCount(); i++) {
					final DefaultAuditKBMaster auditKBMaster = (DefaultAuditKBMaster) auditEvent.getKBMaster(i);
					ps.setInt(1, auditKBMaster.getKbAuditID());
					rs = ps.executeQuery();
					while (rs.next()) {
						String changeDetailXmlValue = rs.getString(1);
						if (changeDetailXmlValue == null || changeDetailXmlValue.isEmpty()) {
							changeDetailXmlValue = DBUtil.extractBlobValue(rs, 1);
						}

						logDebug(LOG, "Change Detail XML: %n%s%n", changeDetailXmlValue);

						if (!isEmptyAfterTrim(changeDetailXmlValue)) {
							try {
								final ChangeDetail changeDetail = unmarshal(new StringReader(changeDetailXmlValue), ChangeDetail.class);
								auditKBMaster.add(changeDetail);
							}
							catch (Exception e) {
								logError(LOG, e, "Change detail not added. Failed to parse change detail from [%s]", changeDetailXmlValue);
							}
						}
					}
					rs.close();
					rs = null;
				}
			}
			ps.close();
			ps = null;

			List<AuditEvent> auditEvents = new LinkedList<AuditEvent>();
			auditEvents.addAll(mutableAuditEventList);

			logDebug(LOG, "Found total of %d audit events", auditEvents.size());

			return Collections.unmodifiableList(auditEvents);
		}
		catch (Exception ex) {
			logError(LOG, ex, "Failed to load audit details for %s", auditSearchCriteria);
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

}
