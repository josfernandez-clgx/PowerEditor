package com.mindbox.pe.server.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntityType;

public class ReportEntityHelper {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private final List<String> errorMessageList;
	public final Logger logger = Logger.getLogger(ReportEntityHelper.class);
	private final Date date;
	private final GenericEntityType entityType;

	public ReportEntityHelper (String dateStr, String entityTypeStr) {
		logger.debug("<init>: Filter Input - " + "date=" + dateStr + ",entityType=" + entityTypeStr);

		this.errorMessageList = new ArrayList<String>();
		this.date = (UtilBase.isEmptyAfterTrim(dateStr) ? null : parseDate(dateStr));

		if(!UtilBase.isEmptyAfterTrim(entityTypeStr)) {
			entityType = GenericEntityType.forName(entityTypeStr);
			if (entityType == null) {
				addErrorMessage(" Invalid entity type entered: " + entityTypeStr + ". Default to return all entity types.");				
			}
		} else {
			entityType = null;			
		}

		logger.debug("date=" + date + ",generic entityType=" + entityType);
	}
	
	public void addErrorMessage(String message) {
		errorMessageList.add(message);
	}
	
	public Date parseDate(String dateStr) {
		Date date = null;
		try {
			date = dateFormat.parse(dateStr);
		}
		catch (ParseException e) {
			addErrorMessage(" Invalid date format entered: " + dateStr + ". Date format pattern must be " + dateFormat.toPattern() + ". Default to return all date.");
		}
		return date;
	}
	
	public boolean isEntityTypeSpecified() {
		return entityType != null;
	}

	public boolean isDateSpecified() {
		return date != null;
	}

	public Date getDate() {
		return date;
	}

	public GenericEntityType getEntityType() {
		return entityType;
	}

	public List<String> getErrorMessages() {
		return Collections.unmodifiableList(errorMessageList);
	}


 
  	
	
     	
}

