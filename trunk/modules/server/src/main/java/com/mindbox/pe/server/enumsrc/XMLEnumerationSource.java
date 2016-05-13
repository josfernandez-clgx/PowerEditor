package com.mindbox.pe.server.enumsrc;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.mindbox.pe.common.IOUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.server.model.ModelUtil;
import com.mindbox.pe.xsd.extenm.EnumValueType;
import com.mindbox.pe.xsd.extenm.PowerEditorEnumeration;

class XMLEnumerationSource extends AbstractEnumerationSource {

	static final String PARAM_XML_FILE = "xml-file";

	@Override
	protected void initParams(Map<String, String> paramMap) throws EnumSourceConfigException {
		String xmlFileName = paramMap.get(PARAM_XML_FILE);
		if (UtilBase.isEmpty(xmlFileName)) {
			throw new EnumSourceConfigException(PARAM_XML_FILE + " parameter is required");
		}

		logDebug(logger, "initiazing XML enumeration source at %s...", xmlFileName);

		File xmlFile = new File(xmlFileName);

		// read enum values from xml
		FileReader reader = null;
		try {
			reader = new FileReader(xmlFile);

			final PowerEditorEnumeration powerEditorEnumeration = XmlUtil.unmarshal(reader, PowerEditorEnumeration.class);

			for (final EnumValueType enumValueType : powerEditorEnumeration.getEnumValue()) {
				final String selectorKey = !UtilBase.isEmptyAfterTrim(enumValueType.getSelectorValue()) ? enumValueType.getSelectorValue() : DEFAULT_KEY;
				getEnumValueList(selectorKey).add(ModelUtil.asEnumValue(enumValueType));
			}

			logInfo(logger, "XML Enumeration Source %s initialized with file %s", getName(), xmlFile.getAbsolutePath());
		}
		catch (FileNotFoundException e) {
			throw new EnumSourceConfigException(xmlFileName + " not found", e);
		}
		catch (JAXBException e) {
			throw new EnumSourceConfigException(xmlFileName + " is not valid XML", e);
		}
		catch (Exception e) {
			throw new EnumSourceConfigException("Error in initParams", e);
		}
		finally {
			IOUtil.close(reader);
		}
	}
}
