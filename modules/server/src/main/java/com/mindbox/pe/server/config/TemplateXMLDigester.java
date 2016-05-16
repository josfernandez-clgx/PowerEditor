/*
 * Created on 2004. 3. 18.
 *
 */
package com.mindbox.pe.server.config;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.template.ColumnAttributeItemDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.template.ColumnPresentationDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.TemplateMessageDigest;

/**
 * Template XML Digester.
 * As of PowerEditor 4.0, this is no longer used for digesting template XML files.
 * This is used to import pre-4.2 template definition, as a part of migration tools. 
 * This no longer digests parameter templates.
 * <p>
 * Usage:<ol>
 * <li>Get an instance of this</li>
 * <li>Call {@link #reset} method to initialize internal state</li>
 * <li>Call {@link #digestTemplateXML(Reader)} once per template definition XML file</li>
 * <li>Call {@link #getAllObjects()} to retrieve parsed (digested) objects</li>
 * </ol>
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.2.0
 */
public class TemplateXMLDigester {

	private static TemplateXMLDigester instance = null;

	public static TemplateXMLDigester getInstance() {
		if (instance == null) {
			instance = new TemplateXMLDigester();
		}
		return instance;
	}

	private final List<Object> objectList;

	private TemplateXMLDigester() {
		// required to write Log4J logging of Digester
		Logger.getLogger("org.apache.commons.digester.Digester");
		this.objectList = new ArrayList<Object>();
	}

	public void addObject(Object obj) {
		objectList.add(obj);
	}

	public synchronized void digestTemplateXML(Reader reader) throws IOException, SAXException {
		Digester digester = getDigester();
		digester.push(this);
		digester.parse(reader);
	}

	public synchronized void digestTemplateXML(String content/*Reader reader*/) throws IOException, SAXException {
		StringReader reader = new StringReader(content);
		digestTemplateXML(reader);
		reader.close();
	}

	public synchronized List<Object> getAllObjects() {
		return Collections.unmodifiableList(objectList);
	}

	private Digester getDigester() {
		Digester digester = new Digester();
		digester.setValidating(false);

		// [1] rules for adhoc rule actions -------------------------------------------------
		digester.addObjectCreate("EditorDefinitions/AdHocRuleAction", ActionTypeDefinition.class);
		digester.addSetProperties("EditorDefinitions/AdHocRuleAction", new String[] { "id" }, new String[] { "idString" });
		digester.addBeanPropertySetter("EditorDefinitions/AdHocRuleAction/Description", "description");
		digester.addBeanPropertySetter("EditorDefinitions/AdHocRuleAction/DeploymentAction", "deploymentRule");
		digester.addSetNext("EditorDefinitions/AdHocRuleAction", "addObject");

		digester.addCallMethod("EditorDefinitions/AdHocRuleAction/UsageList/Usage", "addUsageTypeString", 0);

		digester.addObjectCreate("EditorDefinitions/AdHocRuleAction/ParameterList/Parameter", FunctionParameterDefinition.class);
		digester.addSetProperties(
				"EditorDefinitions/AdHocRuleAction/ParameterList/Parameter",
				new String[] { "paramNum", "deployType" },
				new String[] { "idString", "deployTypeString" });
		digester.addSetNext("EditorDefinitions/AdHocRuleAction/ParameterList/Parameter", "addParameterDefinition");


		// [2] rules for guideline templates (3.3 format) ------------------------------------

		digester.addObjectCreate("EditorDefinitions/GridTemplateDefinition", GridTemplate.class);
		digester.addSetProperties("EditorDefinitions/GridTemplateDefinition", new String[] { "id", "usage" }, new String[] { "idString", "usageTypeString" });
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/Description", "description");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/QualificationRule/QualificationCondition", "ruleDefinitionString");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/RuleExplanation", "ruleExplanation");
		digester.addSetNext("EditorDefinitions/GridTemplateDefinition", "addObject");

		digester.addObjectCreate("EditorDefinitions/GridTemplateDefinition/QualificationRule/Message", TemplateMessageDigest.class);
		digester.addSetProperties("EditorDefinitions/GridTemplateDefinition/QualificationRule/Message");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/QualificationRule/Message", "text");
		digester.addSetNext("EditorDefinitions/GridTemplateDefinition/QualificationRule/Message", "addMessageDigest");

		// [3] rules for guideline template columns (3.3 format) ----------------------------------

		digester.addObjectCreate("EditorDefinitions/GridTemplateDefinition/ColumnDefinition", GridTemplateColumn.class);
		digester.addSetProperties("EditorDefinitions/GridTemplateDefinition/ColumnDefinition", new String[] { "id" }, new String[] { "idString" });
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DeploymentRule", "ruleDefinitionString");
		digester.addSetNext("EditorDefinitions/GridTemplateDefinition/ColumnDefinition", "addGridTemplateColumn");

		digester.addObjectCreate("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Message", TemplateMessageDigest.class);
		digester.addSetProperties("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Message");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Message", "text");
		digester.addSetNext("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Message", "addMessageDigest");

		digester.addObjectCreate("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Presentation", ColumnPresentationDigest.class);
		digester.addSetProperties("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Presentation");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Presentation/Font", "font");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Presentation/Color", "color");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Presentation/ColWidth", "colWidth");
		digester.addSetNext("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/Presentation", "setPresentation");

		digester.addObjectCreate("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec", ColumnDataSpecDigest.class);
		digester.addSetProperties("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec/MinValue", "minValue");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec/MaxValue", "maxValue");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec/Precision", "precision");
		digester.addCallMethod("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec/EnumValue", "addEnumValue", 0);
		digester.addSetNext("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec", "setDataSpecDigest");

		// process attribute items
		digester.addObjectCreate("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec/AttributeItem", ColumnAttributeItemDigest.class);
		digester.addSetProperties("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec/AttributeItem");
		digester.addSetNext("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/DataSpec/AttributeItem", "addAttributeItem");

		// ColumnMessage fragments
		digester.addObjectCreate("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/MessageFragment", ColumnMessageFragmentDigest.class);
		digester.addSetProperties("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/MessageFragment");
		digester.addBeanPropertySetter("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/MessageFragment/Text", "text");
		digester.addSetNext("EditorDefinitions/GridTemplateDefinition/ColumnDefinition/MessageFragment", "addColumnMessageFragment");

		return digester;
	}

	public synchronized void reset() {
		objectList.clear();
	}
}