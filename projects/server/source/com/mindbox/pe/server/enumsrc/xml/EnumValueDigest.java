package com.mindbox.pe.server.enumsrc.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.EnumValue;

public class EnumValueDigest {

	public static List<EnumValueDigest> parseEnumValues(Reader reader) throws IOException, SAXException {
		List<EnumValueDigest> list = new LinkedList<EnumValueDigest>();

		Digester digester = new Digester();
		digester.setValidating(false);
		digester.push(list);

		digester.addObjectCreate("PowerEditorEnumeration/EnumValue", EnumValueDigest.class);
		digester.addBeanPropertySetter("PowerEditorEnumeration/EnumValue/Value", "value");
		digester.addBeanPropertySetter("PowerEditorEnumeration/EnumValue/DisplayLabel", "displayLabel");
		digester.addBeanPropertySetter("PowerEditorEnumeration/EnumValue/SelectorValue", "selectorValue");
		digester.addBeanPropertySetter("PowerEditorEnumeration/EnumValue/Inactive", "inactive");
		digester.addSetNext("PowerEditorEnumeration/EnumValue", "add");

		digester.parse(reader);

		return list;
	}

	private String value;
	private String displayLabel;
	private String selectorValue;
	private boolean inactive = false;

	public EnumValue asEnumValue() {
		EnumValue enumValue = new EnumValue();
		enumValue.setDeployValue(getValue());
		enumValue.setDisplayLabel(getDisplayLabel());
		enumValue.setInactive(String.valueOf(inactive));
		return enumValue;
	}

	public boolean hasSelectorValue() {
		return !UtilBase.isEmptyAfterTrim(selectorValue);
	}
	
	public boolean isInactive() {
		return inactive;
	}

	public void setInactive(boolean inactive) {
		this.inactive = inactive;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayLabel() {
		return (UtilBase.isEmptyAfterTrim(displayLabel) ? value : displayLabel);
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public String getSelectorValue() {
		return selectorValue;
	}

	public void setSelectorValue(String selectorValue) {
		this.selectorValue = selectorValue;
	}
}
