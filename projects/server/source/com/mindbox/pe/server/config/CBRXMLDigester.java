/*
 * Created on 2004. 10. 04.
 *
 */
package com.mindbox.pe.server.config;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mindbox.pe.model.CBRAttributeType;
import com.mindbox.pe.model.CBRCaseAction;
import com.mindbox.pe.model.CBRCaseClass;
import com.mindbox.pe.model.CBRScoringFunction;
import com.mindbox.pe.server.cache.CBRManager;

/**
 * Case-Base XML Digester.
 * Usage:<ol>
 * <li>Get an instance of this</li>
 * <li>Call {@link #digestCBRXML(Reader, CBRManager)} once per template definition XML file</li>
 * </ol>
 * @author Inna Nill
 * @author MindBox
 * @since PowerEditor 4.0.1
 */
public class CBRXMLDigester {

	private static CBRXMLDigester instance = null;

	public static CBRXMLDigester getInstance() {
		if (instance == null) {
			instance = new CBRXMLDigester();
		}
		return instance;
	}

	//private final List objectList;

	private CBRXMLDigester() {
		// required to write Log4J logging of Digester
		Logger.getLogger("org.apache.commons.digester.Digester");
		//this.objectList = new ArrayList();
	}

	public synchronized void digestCBRXML(Reader reader, CBRManager cbrManager) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.push(cbrManager);

		// rules for each static (XML) case class
		digester.addObjectCreate("CBRConfiguration/CaseClasses/CBRCaseClass",CBRCaseClass.class);
		digester.addSetProperties("CBRConfiguration/CaseClasses/CBRCaseClass",
				new String[]{"id","symbol","displayName"},
				new String[]{"ID","symbol","name"});
		digester.addSetNext("CBRConfiguration/CaseClasses/CBRCaseClass","addObject");
		
		// rules for scoring functions
		digester.addObjectCreate("CBRConfiguration/ScoringFunctions/CBRScoringFunction",CBRScoringFunction.class);
		digester.addSetProperties("CBRConfiguration/ScoringFunctions/CBRScoringFunction",
				new String[]{"id","symbol","displayName"},
				new String[]{"ID","symbol","name"});
		digester.addSetNext("CBRConfiguration/ScoringFunctions/CBRScoringFunction","addObject");

		// rules for attribute types
		digester.addObjectCreate("CBRConfiguration/AttributeTypes/CBRAttributeType",CBRAttributeType.class);
		digester.addSetProperties("CBRConfiguration/AttributeTypes/CBRAttributeType",
				new String[]{"id","symbol","displayName","defaultMatchContribution","defaultMismatchPenalty",
				             "defaultAbsensePenalty","defaultValueRange","askForMatchInterval",
							 "askForNumericRange"},
				new String[]{"ID","symbol","name","defaultMatchContribution","defaultMismatchPenalty",
	             			 "defaultAbsensePenalty","defaultValueRange","askForMatchInterval",
				 			 "askForNumericRange"});
		digester.addSetNext("CBRConfiguration/AttributeTypes/CBRAttributeType","addObject");

		// rules for case actions
		digester.addObjectCreate("CBRConfiguration/CaseActions/CBRCaseAction",CBRCaseAction.class);
		digester.addSetProperties("CBRConfiguration/CaseActions/CBRCaseAction",
				new String[]{"id","symbol","displayName"},
				new String[]{"ID","symbol","name"});
		digester.addSetNext("CBRConfiguration/CaseActions/CBRCaseAction","addObject");

		digester.parse(reader);
	}

}