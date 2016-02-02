package com.mindbox.pe.server.enumsrc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.enumsrc.xml.EnumValueDigest;

class XMLEnumerationSource extends AbstractEnumerationSource {

	static final String PARAM_XML_FILE = "xml-file";

	@Override
	protected void initParams(Map<String, String> paramMap) throws EnumSourceConfigException {
		String xmlFileName = paramMap.get(PARAM_XML_FILE);
		if (UtilBase.isEmpty(xmlFileName)) throw new EnumSourceConfigException(PARAM_XML_FILE + " parameter is required");
		
		if (logger.isDebugEnabled()) logger.debug("initiazing XML enumeration source at " + xmlFileName);
		
		File xmlFile = new File(xmlFileName);

		// read enum values from xml
		FileReader reader = null;
		try {
			reader = new FileReader(xmlFile);
			List<EnumValueDigest> digestList = EnumValueDigest.parseEnumValues(reader);
			for (EnumValueDigest digest : digestList) {
				getEnumValueList((digest.hasSelectorValue() ? digest.getSelectorValue() : DEFAULT_KEY)).add(digest.asEnumValue());
			}
			
			logger.info(String.format("XML Enumeration Source %s initialized with file %s", getName(), xmlFile.getAbsolutePath()));
		}
		catch (FileNotFoundException e) {
			throw new EnumSourceConfigException(xmlFileName + " not found", e);
		}
		catch (IOException e) {
			throw new EnumSourceConfigException("Error reading file " + xmlFileName, e);
		}
		catch (SAXException e) {
			throw new EnumSourceConfigException(xmlFileName + " is not valid XML", e);
		}
		finally {
			if (reader != null) try {
				reader.close();
			}
			catch (IOException e) {
				logger.warn("Failed to close " + xmlFileName, e);
			}
		}
	}


}
