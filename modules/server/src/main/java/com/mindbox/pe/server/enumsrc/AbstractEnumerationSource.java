package com.mindbox.pe.server.enumsrc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.EnumValue;

abstract class AbstractEnumerationSource implements EnumerationSource {

	protected static final String DEFAULT_KEY = "*";
	
	protected String name;
	protected boolean selectorSupported = false;
	protected boolean initialized = false;
	protected final Logger logger = Logger.getLogger(getClass());
	protected final Map<String, List<EnumValue>> enumValueSelectorMap = new HashMap<String, List<EnumValue>>();

	@Override
	public synchronized List<EnumValue> getAllEnumValues() {
		if (!initialized) throw new IllegalStateException("Call initialize first!");
		List<EnumValue> enumValues = new LinkedList<EnumValue>();
		for (List<EnumValue> enumValueList : enumValueSelectorMap.values()) {
			enumValues.addAll(enumValueList);
		}
		return enumValues;
	}

	@Override
	public synchronized List<EnumValue> getApplicable(String selectorValue) {
		if (!initialized) throw new IllegalStateException("Call initialize first!");
		if (!selectorSupported) throw new UnsupportedOperationException("selector is not supported");
		return Collections.unmodifiableList(getEnumValueList(selectorValue));
	}

	protected final List<EnumValue> getEnumValueList(String selectorValue) {
		if (!enumValueSelectorMap.containsKey(selectorValue)) {
			enumValueSelectorMap.put(selectorValue, new LinkedList<EnumValue>());
		}
		return enumValueSelectorMap.get(selectorValue);
	}

	@Override
	public synchronized void initialize(String name, boolean selectorSupported, Map<String, String> paramMap) throws EnumSourceConfigException {
		if (UtilBase.isEmpty(name)) throw new EnumSourceConfigException("name is required");
		this.name = name;
		this.selectorSupported = selectorSupported;
		initParams(paramMap);
		initialized = true;
	}

	@Override
	public synchronized final String getName() {
		return name;
	}

	@Override
	public synchronized final boolean isSelectorSupported() {
		return selectorSupported;
	}

	/**
	 * Initialize the specified param map.
	 * @param paramMap
	 * @throws EnumSourceConfigException
	 */
	protected abstract void initParams(Map<String, String> paramMap) throws EnumSourceConfigException;
}
