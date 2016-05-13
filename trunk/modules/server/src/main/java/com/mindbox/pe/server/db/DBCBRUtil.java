package com.mindbox.pe.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.server.db.updaters.CBRAttributeUpdater;
import com.mindbox.pe.server.db.updaters.CBRCaseBaseUpdater;
import com.mindbox.pe.server.db.updaters.CBRCaseUpdater;

/**
 * @author Inna Nill
 * @author MindBox
 * @since PowerEditor 4.1.0
 */
public class DBCBRUtil {

    private static final Logger logger = Logger.getLogger("DBCBRUtil");

    private static final String Q_GET_CBR_ATTRIBUTE_FOR_CASE_BASE = 
    	"select attribute_id from MB_CBR_ATTRIBUTE where case_base_id=?";

    private static final String Q_GET_CBR_CASE_FOR_CASE_BASE = 
    	"select case_id from MB_CBR_CASE where case_base_id=?";

    public static List<Integer> getAttributesForCaseBase(int caseBaseID) throws SQLException {
		logger.debug(">>> getAttributesForCaseBase: " + caseBaseID);
		Connection conn = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Integer> list = new ArrayList<Integer>();
		try {
			conn.setAutoCommit(false);
			
			ps = conn.prepareStatement(Q_GET_CBR_ATTRIBUTE_FOR_CASE_BASE);
			ps.setInt(1, caseBaseID);
			rs = ps.executeQuery();

			while (rs.next()) {
				list.add(rs.getInt(1));
			}
			ps.close();
			ps = null;
		}
		catch (Exception ex) {
			// conn.rollback();
			logger.error("Failed to delete items associated with case: " + caseBaseID, ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			DBConnectionManager.getInstance().freeConnection(conn);
		}
		return list;
    }

    public static List<Integer> getCasesForCaseBase(int caseBaseID) throws SQLException {
		logger.debug(">>> getCasesForCaseBase: " + caseBaseID);
		Connection conn = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Integer> list = new ArrayList<Integer>();
		try {
			conn.setAutoCommit(false);
			
			ps = conn.prepareStatement(Q_GET_CBR_CASE_FOR_CASE_BASE);
			ps.setInt(1, caseBaseID);
			rs = ps.executeQuery();

			while (rs.next()) {
				list.add(rs.getInt(1));
			}
			ps.close();
			ps = null;
		}
		catch (Exception ex) {
			logger.error("Failed to delete items associated with case: " + caseBaseID, ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			DBConnectionManager.getInstance().freeConnection(conn);
		}
		return list;
    }

    public static void cloneCBR(List<AbstractIDNameDescriptionObject> allItemsToClone) throws SQLException {
    	logger.info(">>> cloneCBR");
    	CBRCaseBaseUpdater cbUpdater = new CBRCaseBaseUpdater();
    	CBRAttributeUpdater attrUpdater = new CBRAttributeUpdater();
    	CBRCaseUpdater caseUpdater = new CBRCaseUpdater();
    	Connection conn = DBConnectionManager.getInstance().getConnection();
    	Iterator<AbstractIDNameDescriptionObject> it = allItemsToClone.iterator();
    	Object item = null;
    	try {
    		conn.setAutoCommit(false);
    		while( it.hasNext() ) {
    			item = it.next();
    			if (item instanceof CBRCaseBase) {
    				cbUpdater.cloneCBRCaseBase(conn, (CBRCaseBase) item);
    			} else if (item instanceof CBRAttribute) {
    				attrUpdater.cloneCBRAttribute(conn, (CBRAttribute) item);
    			} else if (item instanceof CBRCase) {
    				caseUpdater.cloneCBRCase(conn, (CBRCase) item);
    			}
    		}
    		conn.commit();
    		
    	} catch (Exception ex) {
			logger.error("Failed to clone item", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(conn);
		}
    }

    private DBCBRUtil() {
    }
}
