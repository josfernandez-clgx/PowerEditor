package com.mindbox.pe.server.db.loaders;


import org.apache.log4j.Logger;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.config.DateFilterConfig;
import com.mindbox.pe.server.config.KnowledgeBaseFilterConfig;


public abstract class AbstractLoader {

	public static boolean dateSysnonymsPassFilter(int effID, int expID, DateFilterConfig dateFilterConfig) {
		if (dateFilterConfig == null) return true;
		
		DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(effID);
		DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(expID);

		return dateFilterConfig.isDateSynonymRangeInRange(effectiveDateSynonym, expirationDateSynonym);
	}

	protected final Logger logger;

	public AbstractLoader() {
		this.logger = Logger.getLogger(getClass());
	}

	public abstract void load(KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws Exception;

	void printInfo(String s) {
		logger.info(s);
	}

}