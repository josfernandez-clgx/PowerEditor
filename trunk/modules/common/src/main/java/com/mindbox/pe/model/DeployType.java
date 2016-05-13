/*
 * Created on Jun 3, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.model;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * 
 * @since PowerEditor 1.0
 */
public final class DeployType implements Serializable {

	private static final long serialVersionUID = 2003060308544000L;

	private static final String _symbol = "SYMBOL";
	private static final String _string = "STRING";
	private static final String _percent = "PERCENT";
	private static final String _date = "DATE";
	private static final String _currency = "CURRENCY";
	private static final String _boolean = "BOOLEAN";
	private static final String _integer = "INTEGER";
	private static final String _code = "CODE";
	private static final String _float = "FLOAT";
	private static final String _relationship = "Relationship";
	private static final String _dynamicString = "DYNAMICSTRING";
    private static final String _entityList= "Entity List";    

	public static final DeployType SYMBOL = new DeployType(_symbol);
	public static final DeployType STRING = new DeployType(_string);
	public static final DeployType PERCENT = new DeployType(_percent);
	public static final DeployType DATE = new DeployType(_date);
	public static final DeployType CURRENCY = new DeployType(_currency);
	public static final DeployType BOOLEAN = new DeployType(_boolean);
	public static final DeployType INTEGER = new DeployType(_integer);
	public static final DeployType CODE = new DeployType(_code);
	public static final DeployType FLOAT = new DeployType(_float);
	// TODO Kim, 2006-12-18: remove this constant
	public static final DeployType RELATIONSHIP = new DeployType(_relationship);
    public static final DeployType ENTITY_LIST = new DeployType(_entityList);    

	/**
	 * Valid deploy types.
	 * @since PowerEditor 4.0.0
	 */
	public static final DeployType[] VALID_VALUES = new DeployType[] {BOOLEAN, CURRENCY, DATE, FLOAT, INTEGER, PERCENT, STRING, SYMBOL, ENTITY_LIST};

	public static DeployType valueOf(String name) {
		if (name == null) throw new NullPointerException("name cannot be null");

		if (name.equalsIgnoreCase(_symbol)) {
			return SYMBOL;
		}
		else if (name.equalsIgnoreCase(_string)) {
			return STRING;
		}
		else if (name.equalsIgnoreCase(_percent)) {
			return PERCENT;
		}
		else if (name.equalsIgnoreCase(_date)) {
			return DATE;
		}
		else if (name.equalsIgnoreCase(_currency)) {
			return CURRENCY;
		}
		else if (name.equalsIgnoreCase(_boolean)) {
			return BOOLEAN;
		}
		else if (name.equalsIgnoreCase(_integer)) {
			return INTEGER;
		}
		else if (name.equalsIgnoreCase(_code)) {
			return CODE;
		}
		else if (name.equalsIgnoreCase(_float)) {
			return FLOAT;
		}
		else if (name.equalsIgnoreCase(_relationship)) {
			return RELATIONSHIP;
		}
        else if (name.equalsIgnoreCase(_entityList)) {
            return ENTITY_LIST;
        }
		else {
			throw new IllegalArgumentException("Invalid DeployType: " + name);
		}
	}

	/**
	 * This returns <code>true</code>, if <code>columnDataType</code> is <code>null</code>.
	 * @param deployType
	 * @param columnDataType
	 * @return <code>true</code>, if <code>columnDataType</code> is <code>null</code>; <code>false</code>, otherwise
	 */
	private static boolean isMatchingColumnType(DeployType deployType, String columnDataType) {
		if (columnDataType == null) return true;
		return (_symbol.equalsIgnoreCase(columnDataType) || _code.equalsIgnoreCase(columnDataType) && deployType != STRING)
				|| ((deployType == SYMBOL || deployType == CODE) && !(_string.equalsIgnoreCase(columnDataType)))
				|| (deployType == STRING && (_string.equalsIgnoreCase(columnDataType) || _dynamicString.equalsIgnoreCase(columnDataType)))
				|| ((deployType == PERCENT || deployType == CURRENCY || deployType == FLOAT) && (_float.equalsIgnoreCase(columnDataType)
						|| _currency.equalsIgnoreCase(columnDataType) || _percent.equalsIgnoreCase(columnDataType)))
				|| (deployType == DATE && _date.equalsIgnoreCase(columnDataType))
				|| (deployType == BOOLEAN && _boolean.equalsIgnoreCase(columnDataType))
				|| (deployType == INTEGER && _integer.equalsIgnoreCase(columnDataType)
				|| (deployType == ENTITY_LIST && _entityList.equalsIgnoreCase(columnDataType)));
	}


	private final String name;

	private DeployType(String name) {
		this.name = name;
	}

	/**
	 * Returns <code>true</code> if <code>deployType</code> is <code>null</code>.
	 * @param deployType
	 * @return
	 */
	public boolean isMatchingColumnDeployType(String deployType) {
		return isMatchingColumnType(this, deployType);
	}
	
	/**
	 * Added so that this can be used as a bean.
	 * @return the name property
	 */
	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

	private Object readResolve() throws ObjectStreamException {
		try {
			return valueOf(this.name);
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidObjectException(ex.getMessage());
		}
	}
}