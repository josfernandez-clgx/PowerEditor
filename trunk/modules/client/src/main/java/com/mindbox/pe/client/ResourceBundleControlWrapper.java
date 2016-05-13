package com.mindbox.pe.client;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class ResourceBundleControlWrapper extends ResourceBundle.Control {

	private final Logger logger = Logger.getLogger(getClass());


	@Override
	public List<Locale> getCandidateLocales(String baseName, Locale locale) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("--> getCandidateLocales: %s,locale=%s", baseName, locale));
		}

		List<Locale> list = super.getCandidateLocales(baseName, locale);

		if (logger.isDebugEnabled()) {
			logger.debug("<-- getCandiateLocales: " + list);
		}
		return list;
	}

	@Override
	public Locale getFallbackLocale(String baseName, Locale locale) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("--> getFallbackLocale: %s,locale=%s", baseName, locale));
		}

		Locale fallBackLocale = super.getFallbackLocale(baseName, locale);

		if (logger.isDebugEnabled()) {
			logger.debug("<-- getFallbackLocale: " + fallBackLocale);
		}
		return fallBackLocale;
	}

	@Override
	public long getTimeToLive(String baseName, Locale locale) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("--> getTimeToLive: %s,locale=%s", baseName, locale));
		}

		long time = super.getTimeToLive(baseName, locale);

		if (logger.isDebugEnabled()) {
			logger.debug("<-- getTimeToLive: " + time);
		}
		return time;
	}

	@Override
	public List<String> getFormats(String baseName) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("--> getFormats: %s", baseName));
		}

		List<String> list = super.getFormats(baseName);

		if (logger.isDebugEnabled()) {
			logger.debug("<-- getFormats: " + list);
		}
		return list;
	}

	@Override
	public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"--> needsReload: %s,locale=%s,format=%s,loader=%s,bundle=%s,loadTime=%d",
					baseName,
					locale,
					format,
					loader,
					bundle,
					loadTime));
		}

		boolean result = super.needsReload(baseName, locale, format, loader, bundle, loadTime);

		if (logger.isDebugEnabled()) {
			logger.debug("<-- needsReload: " + result);
		}

		return result;
	}

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"--> newBundle: %s,locale=%s,format=%s,loader=%s,reload=%b",
					baseName,
					locale,
					format,
					loader,
					reload));
		}

		ResourceBundle resourceBundle = super.newBundle(baseName, locale, format, loader, reload);

		if (logger.isDebugEnabled()) {
			logger.debug("<-- newBundle: " + resourceBundle);
		}
		return resourceBundle;
	}

	@Override
	public String toBundleName(String baseName, Locale locale) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("--> toBundleName: %s,locale=%s", baseName, locale));
		}

		String bundleName = super.toBundleName(baseName, locale);

		if (logger.isDebugEnabled()) {
			logger.debug("<-- toBundleName: " + bundleName);
		}

		return bundleName;
	}

}
