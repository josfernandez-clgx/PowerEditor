package com.mindbox.pe.client.common;

import com.mindbox.pe.client.ClientUtil;

/**
 * Indicates no enumeration source is configured.
 * 
 * Instantiaing an instance of this requires propery initialization of {@link ClientUtil}.
 */
public class EnumerationSourceNotConfiguredException extends AbstractClientGeneratedRuntimeException {

	private static final long serialVersionUID = 200806110000001L;
	
	public EnumerationSourceNotConfiguredException() {
		super("msg.error.no.enum.source");
	}
}
