/*
 * Created on Jan 17, 2006
 *
 */
package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.report.GuidelineReportSpec;


/**
 * Report Spec cache manager. This is thread-safe.
 * Cached elements are cleared if it's not used for a while.
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class ReportSpecManager {

	private final static long TIMEOUT = 1000 * 60 * 60; // one hour

	private static ReportSpecManager instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static ReportSpecManager getInstance() {
		if (instance == null) {
			instance = new ReportSpecManager();
		}
		return instance;
	}

	private static final class CacheDetail {

		private GuidelineReportSpec reportSpec;
		private List<GuidelineReportData> guidelineList;
		private long lastAccessed;

		public CacheDetail(GuidelineReportSpec reportSpec, List<GuidelineReportData> guidelineList) {
			this.reportSpec = reportSpec;
			this.guidelineList = guidelineList;
			this.lastAccessed = System.currentTimeMillis();
		}
	}

	private long nextID = 1000L;
	private final Map<Long, CacheDetail> cacheMap = new HashMap<Long, CacheDetail>();
	private final Logger logger = Logger.getLogger(getClass());

	private ReportSpecManager() {
		new ClearCacheOnTimeOut(TIMEOUT).start();
	}

	/**
	 * Caches the specified report spec and guideline list and returns the report id for the cache.
	 * @param reportSpec
	 * @param guidelineList
	 * @return new report id; this is only unique per server instance
	 */
	public long cacheNextReportSpec(GuidelineReportSpec reportSpec, List<GuidelineReportData> guidelineList) {
		synchronized (cacheMap) {
			long next = ++nextID;
			cacheMap.put(new Long(next), new CacheDetail(reportSpec, guidelineList));
			logger.info("Cached " + reportSpec + " for " + next);
			return next;
		}
	}

	public GuidelineReportSpec getReportSpec(long id) {
		CacheDetail detail = getCacheDetail(id);
		return (detail == null ? null : detail.reportSpec);
	}

	public GuidelineReportSpec getReportSpec(String idStr) {
		try {
			long id = Long.valueOf(idStr).longValue();
			return getReportSpec(id);
		}
		catch (Exception ex) {
			return null;
		}
	}

	private CacheDetail getCacheDetail(long id) {
		synchronized (cacheMap) {
			CacheDetail detail = cacheMap.get(new Long(id));
			detail.lastAccessed = System.currentTimeMillis();
			return detail;
		}
	}

	public List<GuidelineReportData> getGuidelineList(long id) {
		CacheDetail detail = getCacheDetail(id);
		return (detail == null ? null : detail.guidelineList);
	}

	public List<GuidelineReportData> getGuidelineList(String idStr) {
		try {
			long id = Long.valueOf(idStr).longValue();
			return getGuidelineList(id);
		}
		catch (Exception ex) {
			return null;
		}
	}

	private class ClearCacheOnTimeOut extends Thread {

		private final long interval = 1000 * 60 * 5; // 5 minutes
		private final long timeout;

		public ClearCacheOnTimeOut(long timeout) {
			super(ReportSpecManager.this.getClass() + ":ClearCacheOnTimeOut");
			setDaemon(true);
			this.timeout = timeout;
			
		}

		public void run() {
			logger.info(getName() +  " thread started!!!");
			// run until server shuts down
			while (true) {
				try {
					Thread.sleep(interval);
				}
				catch (InterruptedException ex) {
					// ignore
				}
				synchronized (ReportSpecManager.this.cacheMap) {
					long currTime = System.currentTimeMillis();
					List<Long> keysToRemove = new ArrayList<Long>();
					for (Map.Entry<Long,CacheDetail> entry : ReportSpecManager.this.cacheMap.entrySet()) {
						if ((currTime - entry.getValue().lastAccessed) > this.timeout) {
							keysToRemove.add(entry.getKey());
						}
					}
					for (Iterator<Long> iter = keysToRemove.iterator(); iter.hasNext();) {
						Long element = iter.next();
						cacheMap.remove(element);
						logger.info("Removed cache " + element);
					}
				}
			}
		}
	}
}
