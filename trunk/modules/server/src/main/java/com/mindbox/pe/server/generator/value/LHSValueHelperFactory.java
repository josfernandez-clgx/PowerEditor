package com.mindbox.pe.server.generator.value;

import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.model.TemplateUsageType;

/**
 * Factory for {@link com.mindbox.pe.server.generator.value.LHSValueHelper} that reuses instances for the same usage type.
 * @author Geneho Kim
 * @see com.mindbox.pe.server.generator.value.LHSValueHelper
 * @see com.mindbox.pe.model.TemplateUsageType
 */
public class LHSValueHelperFactory {

	public static LHSValueHelper getLHSValueHelper(TemplateUsageType usageType) {
		synchronized (instanceMap) {
			if (instanceMap.containsKey(usageType)) {
				return instanceMap.get(usageType);
			}
			else {
				LHSValueHelper helper = new LHSValueHelper(usageType);
				instanceMap.put(usageType, helper);
				return helper;
			}
		}
	}

	private static Map<TemplateUsageType, LHSValueHelper> instanceMap = new HashMap<TemplateUsageType, LHSValueHelper> ();

	private LHSValueHelperFactory() {

	}
}
