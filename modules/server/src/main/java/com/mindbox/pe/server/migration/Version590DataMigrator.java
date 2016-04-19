package com.mindbox.pe.server.migration;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.VersionUtil.isNewer;
import static com.mindbox.pe.server.migration.retired.ModelUtilRetired.asChangeDetails;
import static com.mindbox.pe.server.spi.audit.AuditEventType.KB_MOD;

import java.sql.Connection;
import java.sql.SQLException;

import com.mindbox.pe.server.db.DefaultAuditServiceProvider;
import com.mindbox.pe.server.migration.retired.AuditEventRetired;
import com.mindbox.pe.server.migration.retired.AuditKBMasterRetired;
import com.mindbox.pe.server.migration.retired.AuditServiceProviderRetired;
import com.mindbox.pe.server.spi.AuditServiceProvider;
import com.mindbox.pe.server.spi.audit.DefaultAuditSearchCriteria;
import com.mindbox.pe.xsd.audit.ChangeDetails;

@SuppressWarnings("deprecation")
class Version590DataMigrator extends AbstractDataMigrator implements DataMigrator {

	private static final String MAX_VERSION = "5.9.0";

	@Override
	protected void migrateData(final String currentVersion, final String targetVersion, final Connection connection) throws SQLException {
		if (isNewer(MAX_VERSION, currentVersion)) {
			logInfo(log, "the current version (%s) is older than %s; migrating...", currentVersion, MAX_VERSION);

			// generate audit changes record and convert it to XML and store it
			final DefaultAuditSearchCriteria auditSearchCriteria = new DefaultAuditSearchCriteria();
			auditSearchCriteria.setAuditTypes(new int[] { KB_MOD.getId() });
			final AuditServiceProviderRetired auditServiceProviderRetired = new AuditServiceProviderRetired();

			final AuditServiceProvider auditServiceProvider = new DefaultAuditServiceProvider();
			try {
				for (final AuditEventRetired auditEventRetired : auditServiceProviderRetired.retrieveAuditEvents(auditSearchCriteria)) {
					logDebug(log, "processing %s...", auditEventRetired);
					for (int i = 0; i < auditEventRetired.kbMasterCount(); i++) {
						final AuditKBMasterRetired auditKBMasterRetired = auditEventRetired.getKBMaster(i);

						logDebug(log, "converting %s (%s:%s)...", auditKBMasterRetired.getKbAuditID(), auditKBMasterRetired.getKbChangedTypeID(), auditKBMasterRetired.getElementID());

						final ChangeDetails changeDetails = asChangeDetails(auditKBMasterRetired);
						auditServiceProvider.insertChangeDetails(connection, auditKBMasterRetired.getKbAuditID(), changeDetails.getChangeDetail());

						logDebug(log, "inserted change detail for %s", auditKBMasterRetired.getKbAuditID());
					}
				}
				logDebug(log, "processing completed");
			}
			catch (SQLException e) {
				throw e;
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to migrated previous kb-mod audit entires", e);
			}
		}
		else {
			logInfo(log, "the current version (%s) is not older than %s. Done.", currentVersion, MAX_VERSION);
		}
	}
}
