package com.mindbox.pe.server.db.loaders;


import org.apache.log4j.Logger;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.config.DateFilterConfigHelper;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter;


public abstract class AbstractLoader {

	public static boolean dateSysnonymsPassFilter(int effID, int expID, final DateFilterConfigHelper dateFilterHelper) {
		if (dateFilterHelper == null) return true;

		DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(effID);
		DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(expID);

		return dateFilterHelper.isDateSynonymRangeInRange(effectiveDateSynonym, expirationDateSynonym);
	}

	protected final Logger logger;

	public AbstractLoader() {
		this.logger = Logger.getLogger(getClass());
	}

	public abstract void load(final KnowledgeBaseFilter knowledgeBaseFilterConfig) throws Exception;

	void printInfo(String s) {
		logger.info(s);
	}

}