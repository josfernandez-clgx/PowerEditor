package com.mindbox.pe.server.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mindbox.pe.common.digest.DomainXMLDigester;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ParameterTemplateXMLDigester;
import com.mindbox.pe.server.db.loaders.CBRLoader;
import com.mindbox.pe.server.db.loaders.DateSynonymLoader;
import com.mindbox.pe.server.db.loaders.GridLoader;
import com.mindbox.pe.server.db.loaders.GuidelineActionLoader;
import com.mindbox.pe.server.db.loaders.GuidelineTemplateLoader;
import com.mindbox.pe.server.db.loaders.ParameterLoader;
import com.mindbox.pe.server.db.loaders.ProcessLoader;
import com.mindbox.pe.server.db.loaders.TypeEnumValueLoader;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.db.GenericEntityDataProvider;
import com.mindbox.pe.server.spi.db.UserDataProvider;
import com.mindbox.pe.xsd.config.FeatureNameType;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter;
import com.mindbox.server.parser.jtb.rule.ParseException;

public final class Loader {

	/**
	 * Clears all cache. Must be called only from the Servlet's destroy method.
	 *
	 */
	static void clearCache() {
		// noop
	}

	private static void loadDomain(final List<String> domainFiles) throws InvalidDataException, IOException, SAXException {
		Logger logger = Logger.getLogger(Loader.class);
		logger.info("Loading domain files...");

		DomainManager domainManager = DomainManager.getInstance();
		domainManager.startLoading();

		DomainXMLDigester.getInstance().reset();
		if (domainFiles != null) {
			for (final String filename : domainFiles) {
				logger.info("Reading " + filename);

				BufferedReader reader = new BufferedReader(new FileReader(filename));
				DomainXMLDigester.getInstance().digestDomainXML(reader);
				reader.close();

				logger.info(filename + " loaded");
			}

			// add digested objects
			for (Object element : DomainXMLDigester.getInstance().getAllObjects()) {
				if (element instanceof DomainClass) {
					domainManager.addDomainClass((DomainClass) element);
				}
			}
		}

		// post load processing
		domainManager.finishLoading();
	}

	private static void loadParameterTemplate(final List<String> templateFiles)
			throws ParseException, SapphireException, SQLException, SAXException, IOException, ParserConfigurationException {
		Logger logger = Logger.getLogger(Loader.class);
		logger.info("Loading templates files...");

		ParameterTemplateManager.getInstance().startLoading();

		ParameterTemplateXMLDigester digester = ParameterTemplateXMLDigester.getInstance();
		digester.reset();
		if (templateFiles != null) {
			for (final String filename : templateFiles) {
				logger.info("Reading " + filename);

				BufferedReader reader = new BufferedReader(new FileReader(filename));
				digester.digestTemplateXML(reader);
				reader.close();

				logger.info(filename + " loaded");
			}

			// add digested objects
			logger.debug("size of all objects = " + digester.getAllObjects().size());
			for (Iterator<Object> iter = digester.getAllObjects().iterator(); iter.hasNext();) {
				Object element = iter.next();
				if (element instanceof ParameterTemplate) {
					ParameterTemplate parameterTemplate = (ParameterTemplate) element;
					overrideEnumColumnMultiselect(parameterTemplate); // temporary for 4.5. Future release will support multiSelect ParamTempl enum cols
					ParameterTemplateManager.getInstance().addParameterTemplate(parameterTemplate);
					logger.info("Added parameter template " + parameterTemplate);
				}
			}
		}

		ParameterTemplateManager.getInstance().finishLoading();
	}

	private static void loadTemplate(KnowledgeBaseFilter knowledgeBaseFilterConfig)
			throws ParseException, SapphireException, SQLException, SAXException, IOException, ParserConfigurationException {
		Logger logger = Logger.getLogger(Loader.class);
		logger.info("Loading templates files...");

		GuidelineTemplateManager.getInstance().startLoading();
		GuidelineFunctionManager.getInstance().startLoading();


		// Load guideline actions first
		GuidelineActionLoader.getInstance().load(knowledgeBaseFilterConfig);

		// load templates from db
		GuidelineTemplateLoader.getInstance().load(knowledgeBaseFilterConfig);

		GuidelineFunctionManager.getInstance().finishLoading();
		GuidelineTemplateManager.getInstance().finishLoading();
	}

	/**
	 * Assumes we need to load everything including config files 
	 * @param doNotLoadTypeEnumValues doNotLoadTypeEnumValues
	 * @throws ServerException on error
	 */
	public static void loadToCache(boolean doNotLoadTypeEnumValues) throws ServerException {
		Loader.loadToCache(false, doNotLoadTypeEnumValues);
	}

	/**
	 * 
	 * @param reloadEvent <code>true</code> if this is a "reload". <code>false</code> if we are
	 * loading for the very first time. <code>false</code> will cause the loader to not reload domain and
	 * config data. 
	 * @param doNotLoadTypeEnumValues doNotLoadTypeEnumValues
	 * @throws ServerException on error
	 */
	public static void loadToCache(boolean reloadEvent, boolean doNotLoadTypeEnumValues) throws ServerException {
		long startTime = System.currentTimeMillis();

		Logger logger = Logger.getLogger(Loader.class);
		try {
			final KnowledgeBaseFilter knowledgeBaseFilterConfig = ConfigurationManager.getInstance().getPowerEditorConfiguration().getKnowledgeBaseFilter();
			if (knowledgeBaseFilterConfig != null && knowledgeBaseFilterConfig.getDateFilter() != null) {
				logger.info("Using dateFilter " + knowledgeBaseFilterConfig.getDateFilter());
			}

			logger.info("Loading date synonyms...");
			DateSynonymLoader.getInstance().load(knowledgeBaseFilterConfig);

			logger.info("Loader: date synonym loading time = " + (System.currentTimeMillis() - startTime));
			startTime = System.currentTimeMillis();
			if (!reloadEvent) {
				loadDomain(ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getKnowledgeBase().getDomainFile());

				// Validate configuration that requires that domain files are loaded
				ConfigurationManager.getInstance().validateWithDomainLoaded(DomainManager.getInstance());
			}

			logger.info("Loader: domain XML loading time = " + (System.currentTimeMillis() - startTime));
			startTime = System.currentTimeMillis();

			// added in 4.3.1
			if (!doNotLoadTypeEnumValues) {
				logger.info("Loading type enun values...");
				TypeEnumValueLoader.getInstance().load(knowledgeBaseFilterConfig);
			}

			logger.info("Loading entities...");
			EntityManager entityManager = EntityManager.getInstance();
			entityManager.startLoading();
			try {
				GenericEntityDataProvider genericEntityProvider = ServiceProviderFactory.getPEDataProvider().getGenericEntityDataProvider();
				logger.info("Loading generic categories...");
				genericEntityProvider.loadCategories(entityManager);
				logger.info("Loading generic category parent relationships...");
				genericEntityProvider.loadCategoryParents(entityManager);

				// as of 4.5.0 this loads products, in addition to 4th entity
				logger.info("Loading generic entities...");
				genericEntityProvider.loadGenericEntities(entityManager);

				logger.info("loading entity compatibility data...");
				genericEntityProvider.loadEntityCompaitilityMatrix(entityManager);

				logger.info("Loading generic category to entity links..");
				genericEntityProvider.loadEntityToCategories(entityManager);

				logger.info("Generic Entites loaded");

			}
			catch (Exception ex) {
				logger.error("Failed to load entity data", ex);
				throw new ServletException("Failed to load entity data: " + ex.getMessage());
			}
			finally {
				entityManager.finishLoading();
			}

			logger.info("Loader: entity loading time = " + (System.currentTimeMillis() - startTime));
			startTime = System.currentTimeMillis();

			logger.info("Loader: product loading time = " + (System.currentTimeMillis() - startTime));
			startTime = System.currentTimeMillis();

			// If we are not reloading (ie, we are loading for the first time), then load user info.
			// GKIM: to support LDAP, we need to reload user data on refresh!!!
			//       This conditionaly reloading was implemented to fix TT 710;
			//       We are asking users to re-login after this, so we can reload user info
			loadUserData();

			logger.info("Loader: security data loading time = " + (System.currentTimeMillis() - startTime));
			startTime = System.currentTimeMillis();

			logger.info("Loading templates");
			loadTemplate(knowledgeBaseFilterConfig);

			if (ConfigurationManager.getInstance().isFeatureEnabled(FeatureNameType.PARAMETER)) {
				loadParameterTemplate(ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getKnowledgeBase().getTemplateFile());
			}

			logger.info("Loader: template loading time = " + (System.currentTimeMillis() - startTime));
			startTime = System.currentTimeMillis();

			logger.info("Loading grids...");
			new GridLoader().load(knowledgeBaseFilterConfig);
			logger.info("All grid data loaded");

			logger.info("Loader: grid loading time = " + (System.currentTimeMillis() - startTime));
			startTime = System.currentTimeMillis();

			// load parameters iff parameter feature is enabled
			if (ConfigurationManager.getInstance().isFeatureEnabled(FeatureNameType.PARAMETER)) {
				logger.info("Loading parameters...");
				new ParameterLoader().load(knowledgeBaseFilterConfig);
				logger.info("All parameters loaded");

				logger.info("Loader: parameter loading time = " + (System.currentTimeMillis() - startTime));
				startTime = System.currentTimeMillis();
			}

			// load phase iff phase feature is enabled
			if (ConfigurationManager.getInstance().isFeatureEnabled(FeatureNameType.PHASE)) {
				logger.info("Loading phase data");
				new ProcessLoader().load(knowledgeBaseFilterConfig);

				logger.info("Loader: phase/process data loading time = " + (System.currentTimeMillis() - startTime));
				startTime = System.currentTimeMillis();
			}

			// load CBR data iff cbr feature is enabled
			if (ConfigurationManager.getInstance().isFeatureEnabled(FeatureNameType.CBR)) {
				logger.info("Loading CBR data");
				new CBRLoader().load(knowledgeBaseFilterConfig);
				logger.info("All CBR data loaded");

				logger.info("Loader: CBR loading time = " + (System.currentTimeMillis() - startTime));
				startTime = System.currentTimeMillis();
			}
		}
		catch (Exception exception) {
			logger.error("Exception while loading entities from DB", exception);
			throw new ServerException(exception.getMessage());
		}
	}

	public static void loadUserData() throws ServerException {
		Logger logger = Logger.getLogger(Loader.class);
		logger.info("Loading users & authorization data...");
		SecurityCacheManager securityCache = SecurityCacheManager.getInstance();
		securityCache.startLoading();
		UserDataProvider userDataProvider = ServiceProviderFactory.getUserManagementProvider();
		try {
			userDataProvider.loadAllPrivileges(securityCache);
			userDataProvider.loadAllRoles(securityCache);
			userDataProvider.loadAllPrivilegesToRoles(securityCache);
			if (userDataProvider.cacheUserObjects()) {
				userDataProvider.loadAllUsers(securityCache);
				userDataProvider.loadAllUsersToRoles(securityCache);
			}
		}
		catch (Exception ex) {
			logger.error("Failed to load user  data", ex);
			throw new ServerException("Failed to load user-authorization data: " + ex.getMessage());
		}
		finally {
			SecurityCacheManager.getInstance().finishLoading();
		}
	}

	// NOT UNIT TESTED!  Too much going on in this class.
	// temporary for 4.5. Future release will support multiSelect ParamTempl enum cols.
	private static void overrideEnumColumnMultiselect(ParameterTemplate parameterTemplate) {
		for (AbstractTemplateColumn col : parameterTemplate.getColumns()) {
			ColumnDataSpecDigest colSpec = col.getColumnDataSpecDigest();
			colSpec.setIsMultiSelectAllowed(false);
		}
	}

	private Loader() {
	}
}