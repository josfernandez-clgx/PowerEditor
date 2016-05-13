package com.mindbox.pe.server.enumsrc;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.EnumValue;

/**
 * Enumeration Source.
 *
 */
public interface EnumerationSource {

	/**
	 * Gets all enumeration values in this.
	 * @return all enumeration values in this
	 * @throws IllegalStateException if called before {@link #initialize(String, boolean, Map)} is called
	 */
	List<EnumValue> getAllEnumValues();

	/**
	 * Gets all enumeration values applicable for the specified selector value.
	 * @param selectorValue selector value
	 * @return all enumeration applicable for <code>selectorValue</code>
	 * @throws IllegalStateException if called before {@link #initialize(String, boolean, Map)} is called
	 * @throws UnsupportedOperationException if selector is not supported
	 */
	List<EnumValue> getApplicable(String selectorValue);

	/**
	 * Gets the name of this
	 * @return the name
	 */
	String getName();
	
	/**
	 * Tests if this supports selector.
	 * @return <code>true</code> if selector is supported; <code>false</code>, otherwise
	 */
	boolean isSelectorSupported();
	
	/**
	 * Initialize this enum source with the specified arguments.
	 * @param name name of this enumeration source
	 * @param selectorSupported indicates if selector is supported
	 * @param paramMap parameters
	 * @throws EnumSourceConfigException on configuration error
	 */
	void initialize(String name, boolean selectorSupported, Map<String, String> paramMap) throws EnumSourceConfigException;
}
