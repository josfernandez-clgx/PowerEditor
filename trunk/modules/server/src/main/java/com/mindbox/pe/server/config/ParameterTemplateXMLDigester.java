package com.mindbox.pe.server.config;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnPresentationDigest;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.model.template.ParameterTemplateColumn;

/**
 * Template XML Digester.
 * Usage:<ol>
 * <li>Get an instance of this</li>
 * <li>Call {@link #reset} method to initialize internal state</li>
 * <li>Call {@link #digestTemplateXML(Reader)} once per template definition XML file</li>
 * <li>Call {@link #getAllObjects()} to retrieve parsed (digested) objects</li>
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.2.0
 */
public class ParameterTemplateXMLDigester {

	private static ParameterTemplateXMLDigester instance = null;

	public static ParameterTemplateXMLDigester getInstance() {
		if (instance == null) {
			instance = new ParameterTemplateXMLDigester();
		}
		return instance;
	}

	private final List<Object> objectList;

	private ParameterTemplateXMLDigester() {
		// required to write Log4J logging of Digester
		Logger.getLogger("org.apache.commons.digester.Digester");
		this.objectList = new ArrayList<Object>();
	}

	public synchronized void reset() {
		objectList.clear();
	}

	public void addObject(Object obj) {
		objectList.add(obj);
	}

	public synchronized List<Object> getAllObjects() {
		return Collections.unmodifiableList(objectList);
	}

	private Digester getDigester() {
		Digester digester = new Digester();
		digester.setValidating(false);

	
		// rules for parameter templates --------------------------------------
		
		digester.addObjectCreate("EditorDefinitions/ParameterTemplateDefinition", ParameterTemplate.class);
		digester.addSetProperties("EditorDefinitions/ParameterTemplateDefinition",
				new String[]{"id","usage"},
				new String[]{"idString","usageTypeString"});
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/Description", "description");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/DeployMethod/Script", "deployScriptDetails");		
		digester.addSetNext("EditorDefinitions/ParameterTemplateDefinition", "addObject");
		
		// rules for parameter template columns
		
		digester.addObjectCreate("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition", ParameterTemplateColumn.class);
		digester.addSetProperties("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition",
				new String[]{"id", "attributeMap"},
				new String[]{"idString", "attributeMapOldStr"});
		digester.addSetNext("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition", "addColumn");
		
		digester.addObjectCreate("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/Presentation", ColumnPresentationDigest.class);
		digester.addSetProperties("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/Presentation");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/Presentation/Font", "font");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/Presentation/Color", "color");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/Presentation/ColWidth", "colWidth");
		digester.addSetNext("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/Presentation", "setPresentation");
		
		digester.addObjectCreate("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec", ColumnDataSpecDigest.class);
		digester.addSetProperties("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec/MinValue", "minValue");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec/MaxValue", "maxValue");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec/Precision", "precision");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec/EntityType", "entityType");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec/AllowCategory", "allowCategory");
		digester.addBeanPropertySetter("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec/AllowEntity", "allowEntity");
		digester.addCallMethod("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec/EnumValue", "addEnumValue", 0);
		digester.addSetNext("EditorDefinitions/ParameterTemplateDefinition/ColumnDefinition/DataSpec", "setDataSpecDigest");

		return digester;
	}

	public synchronized void digestTemplateXML(Reader reader) throws IOException, SAXException {
		Digester digester = getDigester();
		digester.push(this);
		digester.parse(reader);
	}

}