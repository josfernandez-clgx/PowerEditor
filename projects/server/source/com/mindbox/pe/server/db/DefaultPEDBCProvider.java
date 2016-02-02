package com.mindbox.pe.server.db;

import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.loaders.GenericEntityLoader;
import com.mindbox.pe.server.db.updaters.GenericEntityUpdater;
import com.mindbox.pe.server.spi.GuidelineRuleProvider;
import com.mindbox.pe.server.spi.PEDBCProvider;
import com.mindbox.pe.server.spi.UserManagementProvider;
import com.mindbox.pe.server.spi.db.GenericEntityDataProvider;
import com.mindbox.pe.server.spi.db.GenericEntityDataUpdater;
import com.mindbox.pe.server.spi.db.PEDataProvider;
import com.mindbox.pe.server.spi.db.PEDataUpdater;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PE 2.0.0
 */
public class DefaultPEDBCProvider implements PEDBCProvider {

	/**
	 * 
	 * @author Gene Kim
	 * @author MindBox, Inc
	 * @since 
	 */
	private static class DefaultPEDataProvider implements PEDataProvider {

		private final GenericEntityDataProvider entityProvider;

		public DefaultPEDataProvider() {
			GenericEntityLoader el = new GenericEntityLoader();
			this.entityProvider = el;
		}

		public GenericEntityDataProvider getGenericEntityDataProvider() {
			return entityProvider;
		}

	}

	/**
	 * 
	 * @author Gene Kim
	 * @author MindBox, Inc
	 * @since 
	 */
	private static class DefaultPEDataUpdater implements PEDataUpdater {

		private final GenericEntityDataUpdater genericEntityUpdater;

		public DefaultPEDataUpdater() {
			this.genericEntityUpdater = new GenericEntityUpdater();
		}

		public GenericEntityDataUpdater getGenericEntityDataUpdater() {
			return genericEntityUpdater;
		}
	}

	private final PEDataProvider provider;
	private final PEDataUpdater updater;
	private GuidelineRuleProvider ruleProvider;
	private UserManagementProvider userManagementProvider;

	public DefaultPEDBCProvider() {
		provider = new DefaultPEDataProvider();
		updater = new DefaultPEDataUpdater();
	}

	public UserManagementProvider getUserManagementProvider() {
		if (userManagementProvider == null) {
			String classname = ConfigurationManager.getInstance().getServerConfiguration().getDatabaseConfig().getUserManagementProviderClassName();
			if (classname == null || classname.length() == 0) {
				userManagementProvider = new DefaultUserManagementProvider();
			}
			else {
				Class<?> providerClass = null;
				try {
					providerClass = Class.forName(classname);

					if (!(UserManagementProvider.class.isAssignableFrom(providerClass))) { throw new IllegalArgumentException(
							"The <Database><UserManagementProviderClass> class " + providerClass.getName()
									+ " is invalid. It must be implement UserManagementProvider interface."); }

					userManagementProvider = (UserManagementProvider) providerClass.newInstance();
				}
				catch (Exception ex) {
					throw new IllegalArgumentException("Failed to initialize <Server><Database><UserManagementProviderClass> " + classname + ": "
							+ ex.toString());
				}
			}
		}
		return userManagementProvider;
	}

	public GuidelineRuleProvider getGuidelineRuleProvider() {
		if (ruleProvider == null) {
			String classname = ConfigurationManager.getInstance().getServerConfiguration().getDatabaseConfig().getGuidelineRuleProviderClassName();
			if (classname == null || classname.length() == 0) {
				ruleProvider = new DefaultGuidelineRuleProvider();
			}
			else {
				Class<?> providerClass = null;
				try {
					providerClass = Class.forName(classname);

					if (!(GuidelineRuleProvider.class.isAssignableFrom(providerClass))) { throw new IllegalArgumentException(
							"The <Database><GuidelineRuleProviderClass> class " + providerClass.getName()
									+ " is invalid. It must be implement GuidelineRuleProvider interface."); }

					ruleProvider = (GuidelineRuleProvider) providerClass.newInstance();
				}
				catch (Exception ex) {
					throw new IllegalArgumentException("Failed to initialize <Server><Database><GuidelineRuleProviderClass> " + classname + ": "
							+ ex.toString());
				}
			}
		}
		return ruleProvider;
	}

	public PEDataProvider getDataProvider() {
		return provider;
	}

	public PEDataUpdater getDataUpdater() {
		return updater;
	}

	public PEDBCProvider createInstance() {
		return this;
	}

}
