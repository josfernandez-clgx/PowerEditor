package com.mindbox.pe.client;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DateSynonym;

/**
 * Provides utility methods useful for unit testing client code, including those that manipulate client cache.
 *
 */
public final class ClientTestUtil {

	public static void prepEntityModelCacheFactoryDateSynonymCache() throws Exception {
		ReflectionUtil.executePrivate(EntityModelCacheFactory.getInstance(), "resortNamedDateSynonyms", new Class[]
			{ DateSynonym[].class}, new Object[]
			{ new DateSynonym[0]});
	}

	@SuppressWarnings("rawtypes")
	public static void clearEntityModelCacheFactoryDateSynonymCache() throws Exception {
		((List) ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "allDateSynonyms")).clear();
		((List) ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "dateSynonymModelList")).clear();
		((List) ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "dateSynonymWithEmptyModelList")).clear();
		((DefaultComboBoxModel) ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "dateSynonymModel")).removeAllElements();
		((DefaultComboBoxModel) ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "dateSynonymWithEmptyModel")).removeAllElements();
	}

	private ClientTestUtil() {}
}
