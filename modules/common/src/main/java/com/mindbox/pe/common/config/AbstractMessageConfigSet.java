package com.mindbox.pe.common.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfigType;

public abstract class AbstractMessageConfigSet<V> implements Serializable {

	private static final long serialVersionUID = 2004071124000000L;

	private V anyConfig;
	private V rangeConfig;
	private Map<CellSelectionType, V> cellSelectionMessageMap = new HashMap<CellSelectionType, V>();

	protected AbstractMessageConfigSet() {
	}

	protected AbstractMessageConfigSet(final AbstractMessageConfigSet<V> source) {
		this.anyConfig = source.anyConfig;
		this.rangeConfig = source.rangeConfig;
		this.cellSelectionMessageMap.putAll(source.cellSelectionMessageMap);
	}

	protected abstract V createCopy(V source);

	public final V getAnyConfig() {
		return anyConfig;
	}

	public final V getDefaultConfig() {
		return cellSelectionMessageMap.get(CellSelectionType.DEFAULT);
	}

	/**
	 * Returns the enum config object appropriate to the given parameters
	 * @param isExclusion Did the enum cell value have 'exclude selections' checked
	 * @param isMultiSelect Were there more than one cell-values selected.	 
	 * @return The appropriate enum config object
	 */
	public final V getEnumConfig(boolean isExclusion, boolean isMultiSelect) {
		final V result = cellSelectionMessageMap.get(ConfigUtil.getCellSelectionType(isExclusion, isMultiSelect));
		return result == null ? cellSelectionMessageMap.get(CellSelectionType.DEFAULT) : result;
	}

	public final V getEnumConfig(final CellSelectionType cellSelectionType) {
		return cellSelectionMessageMap.get(cellSelectionType);
	}

	/**
	 * A private auxilary method
	 * @return The singleton range configuration object
	 */
	public final V getRangeConfig() {
		return rangeConfig;
	}

	protected final V getRangeConfigOrDefaultIfNotFound() {
		V rangeConfig = getRangeConfig();
		if (rangeConfig == null) {
			rangeConfig = getEnumConfig(CellSelectionType.DEFAULT);
		}
		return rangeConfig;
	}

	protected final void removeConfig(final MessageConfigType key, final V config, final CellSelectionType cellSelectionType) {
		if (key == null) {
			throw new IllegalArgumentException("Type attribute needed for <MessageFragment>: " + config);
		}
		switch (key) {
		case ANY:
			anyConfig = null;
			break;
		case ENUM:
			if (cellSelectionType == null) {
				throw new IllegalArgumentException("cellSelectionType cannot be null for enum");
			}
			if (cellSelectionType != CellSelectionType.DEFAULT) {
				cellSelectionMessageMap.remove(cellSelectionType);
			}
			break;
		case RANGE:
			rangeConfig = null;
			break;
		case CONDITIONAL:
			// TODO determine the correct course of action
		}
	}

	protected void setDefaultConfig(final V config) {
		if (config == null) {
			throw new IllegalArgumentException("config cannot be null");
		}
		cellSelectionMessageMap.put(CellSelectionType.DEFAULT, config);
	}

	protected abstract void setDefaults(V source);

	/**
	 * Given a msgDigest object, udpates the list.
	 * If the msgDigest is a default, just override defaults.
	 * @param key key
	 * @param config config
	 * @param cellSelectionType cell selection type
	 */
	protected final void updateConfig(final MessageConfigType key, V config, final CellSelectionType cellSelectionType) {
		if (key == null) {
			throw new IllegalArgumentException("Type attribute needed for <MessageFragment>: " + config);
		}
		switch (key) {
		case ANY:
			if (anyConfig == null) {
				anyConfig = createCopy(config);
			}
			else {
				updateInvariants(anyConfig, config);
			}
			break;
		case CONDITIONAL:
			// TODO determine the correct course of action
			break;
		case ENUM:
			if (cellSelectionType == null) {
				throw new IllegalArgumentException("cellSelectionType cannot be null for enum");
			}
			if (cellSelectionType == CellSelectionType.DEFAULT) {
				setDefaults(config);
			}
			else {
				cellSelectionMessageMap.put(cellSelectionType, config);
			}
			break;
		case RANGE:
			if (rangeConfig == null) {
				rangeConfig = createCopy(config);
			}
			else {
				updateInvariants(rangeConfig, config);
			}
		}
	}

	protected abstract void updateInvariants(V target, V source);
}
