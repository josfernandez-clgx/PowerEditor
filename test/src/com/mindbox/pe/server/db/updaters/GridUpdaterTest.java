package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBTestBase;
import com.mindbox.pe.server.model.GenericCategoryIdentity;
import com.mindbox.pe.server.model.GenericEntityIdentity;

public class GridUpdaterTest extends DBTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GridUpdaterTest Tests");
		suite.addTestSuite(GridUpdaterTest.class);
		return suite;
	}

	public GridUpdaterTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.clearGrids();
		super.tearDown();
	}

	public void testInsertProductGridWithValidValues() throws Exception {
		int gridID = (int) System.currentTimeMillis();
		GenericEntityIdentity[] entityIdentities = new GenericEntityIdentity[] { new GenericEntityIdentity(1, 100) };
		GenericCategoryIdentity[] categoryIdentities = new GenericCategoryIdentity[] { new GenericCategoryIdentity(20, 6789),
				new GenericCategoryIdentity(20, 2345) };

		Connection conn = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			GridUpdater gridUpdater = new GridUpdater(conn);

			gridUpdater.insertProductGrid(
					gridID,
					100,
					"comment",
					new ProductGrid(gridID, new GridTemplate(100,"template",TemplateUsageType.getAllInstances()[0]),null, null),
					"Draft",
					new Date(),
					null,
					null,
					0,
					-1,
					new Date(),
					entityIdentities,
					categoryIdentities);
			conn.commit();

			ps = conn.prepareStatement("select entity_id,entity_type,category_type,category_id from MB_ENTITY_GRID_CONTEXT where grid_id=?");
			ps.setInt(1, gridID);
			rs = ps.executeQuery();
			boolean is2345Found = false;
			boolean is6789Found = false;
			while (rs.next()) {
				int entityID = rs.getInt(1);
				int entityType = rs.getInt(2);
				int catType = rs.getInt(3);
				int catID = rs.getInt(4);
				if (entityType == 1) {
					assertEquals(100, entityID);
					assertEquals(0, catType);
					assertEquals(0, catID);
				}
				else if (catType == 20) {
					assertTrue(catID == 2345 || catID == 6789);
					assertEquals(0, entityType);
					assertEquals(0, entityID);
					if (catID == 2345)
						is2345Found = true;
					if (catID == 6789)
						is6789Found = true;
				}
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;
			assertTrue(is2345Found && is6789Found);
		}
		catch (Exception ex) {
			conn.rollback();
			throw ex;
		}
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

}
