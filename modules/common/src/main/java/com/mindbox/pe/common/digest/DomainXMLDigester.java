package com.mindbox.pe.common.digest;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.DomainClassLink;
import com.mindbox.pe.model.domain.DomainTranslation;
import com.mindbox.pe.model.domain.DomainViewDigest;

/**
 * Domain XML Digester.
 * Usage:<ol>
 * <li>Get an instance of this</li>
 * <li>Call {@link #reset} method to initialize internal state</li>
 * <li>Call {@link #digestDomainXML(Reader)} once per domain definition XML file</li>
 * <li>Call {@link #getAllObjects()} to retrieve parsed (digested) objects</li>
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.2.0
 */
public class DomainXMLDigester {

	private static DomainXMLDigester instance = null;

	public static DomainXMLDigester getInstance() {
		if (instance == null) {
			instance = new DomainXMLDigester();
		}
		return instance;
	}

	private final List<Object> objectList;

	private DomainXMLDigester() {
		// required to write Log4J logging of Digester
		Logger.getLogger("org.apache.commons.digester.Digester");
		this.objectList = new LinkedList<Object>();
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

		digester.addObjectCreate("DomainModel/DomainClass", DomainClass.class);
		digester.addSetProperties("DomainModel/DomainClass", 
				new String[]{"Name","DeployLabel","DisplayLabel","AllowRuleUsage","HasMultiplicity"},
				new String[]{"name","deployLabel","displayLabel","allowRuleUsage","hasMultiplicity"});
		digester.addSetNext("DomainModel/DomainClass", "addObject");

		digester.addObjectCreate("DomainModel/DomainClass/DomainClassLink", DomainClassLink.class);
		digester.addSetProperties("DomainModel/DomainClass/DomainClassLink",
				new String[]{"ParentClassName","ChildClassName","DeployValue","HasMultiplicity"},
				new String[]{"parentName","childName","deployValueName","hasMultiplicity"});
		digester.addSetNext("DomainModel/DomainClass/DomainClassLink", "addDomainClassLink");
		
		digester.addFactoryCreate("DomainModel/DomainClass/DomainAttribute", DomainAttributeFactory.class);
		digester.addSetProperties("DomainModel/DomainClass/DomainAttribute",
				new String[]{"Name","DeployType","DeployLabel","DisplayLabel","AllowRuleUsage","ContextlessLabel","Precision"},
				new String[]{"name","deployTypeString","deployLabel","displayLabel","allowRuleUsage","contextlessLabel","precision"});
		digester.addSetNext("DomainModel/DomainClass/DomainAttribute", "addDomainAttribute");
		
		digester.addObjectCreate("DomainModel/DomainClass/DomainAttribute/DomainView", DomainViewDigest.class);
		digester.addSetProperties("DomainModel/DomainClass/DomainAttribute/DomainView",
				new String[]{"ViewType"},
				new String[]{"viewType"});
		digester.addSetNext("DomainModel/DomainClass/DomainAttribute/DomainView", "addDomainViewDigest");
		
		digester.addObjectCreate("DomainModel/DomainClass/DomainAttribute/EnumValue", EnumValue.class);
		digester.addSetProperties("DomainModel/DomainClass/DomainAttribute/EnumValue",
				new String[]{"DeployID","DeployValue","DisplayLabel","Inactive"},
				new String[]{"deployID","deployValue","displayLabel","inactive"});
		digester.addSetNext("DomainModel/DomainClass/DomainAttribute/EnumValue", "addEnumValue");

		digester.addObjectCreate("DomainModel/DomainClass/DomainTranslation", DomainTranslation.class);
		digester.addSetProperties("DomainModel/DomainClass/DomainTranslation",
				new String[]{"Name","AttributeType","LinkPath","DisplayLabel","ContextlessLabel"},
				new String[]{"name","attributeType","linkPath","displayLabel","contextlessLabel"});
		digester.addSetNext("DomainModel/DomainClass/DomainTranslation", "addDomainTranslation");
		
		digester.addObjectCreate("DomainModel/DomainClass/DomainTranslation/DomainView", DomainViewDigest.class);
		digester.addSetProperties("DomainModel/DomainClass/DomainTranslation/DomainView",
				new String[]{"ViewType"},
				new String[]{"viewType"});
		digester.addSetNext("DomainModel/DomainClass/DomainTranslation/DomainView", "addDomainViewDigest");
		
		return digester;
	}
	
	public void digestDomainXML(Reader reader) throws IOException, SAXException {
		Digester digester = getDigester();
		digester.push(this);
		digester.parse(reader);
	}

}
