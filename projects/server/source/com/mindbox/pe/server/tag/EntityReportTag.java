package com.mindbox.pe.server.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.TimedAssociationKey;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.report.ReportEntityHelper;

/**
 * Implementation of &lt;entity-report&gt; PowerEditor custom tag.
 * <p>
 * </ul>
 * @since PowerEditor 6.0
 */
public class EntityReportTag extends AbstractXMLOutputTag {

	private static final long serialVersionUID = 3038861095417400117L;

	private String date;
	private String entityType;
	private ReportEntityHelper reportEntityHelper;


	public int doStartTag() throws JspException {
		reportEntityHelper = new ReportEntityHelper(date, entityType);
				
		try {
			writeOpen("entity-report");						
			writeEntityReport();		
			
			List<String> errorMessageList = reportEntityHelper.getErrorMessages();
			if (!UtilBase.isEmpty(errorMessageList)) {
				writeElement("error-message", UtilBase.toString(errorMessageList.toArray(new String[0])));
			}
			writeClose("entity-report");
		}
		catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_PAGE;
	}

	private void writeEntityReport() throws IOException {
		if (reportEntityHelper.isDateSpecified() && reportEntityHelper.isEntityTypeSpecified()) {
			writeGenericCategories(reportEntityHelper.getEntityType(), reportEntityHelper.getDate());
			writeGenericEntities(reportEntityHelper.getEntityType(), reportEntityHelper.getDate());
		} 
		else if (reportEntityHelper.isDateSpecified()) {
			writeGenericCategories(reportEntityHelper.getDate());
			writeGenericEntities(reportEntityHelper.getDate());
		}
		else if (reportEntityHelper.isEntityTypeSpecified()) {
			writeGenericCategories(reportEntityHelper.getEntityType());
			writeGenericEntities(reportEntityHelper.getEntityType(), null);
		}
		else {
			// write all
			writeGenericCategories();
			writeGenericEntities(null);
		}
	}
	   

	private void writeGenericCategories(GenericEntityType genericEntityType, Date date) throws IOException {

		int childCatID;
		GenericCategory childCat, rootCat;
		List<Integer> catIDList = new ArrayList<Integer>();
		int categoryType = genericEntityType.getCategoryType();

		// for each root category of a entity type
		List<GenericCategory> rootCatList  = EntityManager.getInstance().getAllRootCategories(categoryType);	
		for (Iterator<GenericCategory> iterator = rootCatList.iterator(); iterator.hasNext();) {

			// write the root category
			rootCat = iterator.next();
			//writeGenericCategoryTag(rootCat, genericEntityType.toString());	               

			// write root and all children category
			catIDList = EntityManager.getInstance().getAllDescendentCategoryIDsAsOf(categoryType, rootCat.getID(), date);
			for (Iterator<Integer> catIter = catIDList.iterator(); catIter.hasNext();) {
				childCatID = catIter.next().intValue();
				childCat = EntityManager.getInstance().getGenericCategory(categoryType, childCatID);
				//writeGenericCategoryTag(childCat, genericEntityType.toString());
		    	writeOpen("category", getAttriValueMapForCategoryTag(childCat, genericEntityType.toString()));	 

		    	// write all associated entities for a category
				List<GenericEntity> entitiesList = EntityManager.getInstance().getAllGenericEntitiesInCategoryAsOf(childCatID, categoryType, date);
				for (GenericEntity genericEntity : entitiesList) {
					writeAssociatedEntityLinkTag(genericEntity);
				}
				writeClose("category");
			}
		}      
	}

	private void writeGenericCategories(Date date) throws IOException {		
		// for each category type
		GenericEntityType[] genericEntityTypeList = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < genericEntityTypeList.length; i++) {
			writeGenericCategories(genericEntityTypeList[i], date);			       	
		}
	}

	private void writeGenericCategories(GenericEntityType genericEntityType) throws IOException {
		// write all categories for a entity type  
		for (Iterator<GenericCategory> iter = EntityManager.getInstance().getAllCategories(genericEntityType.getCategoryType()).iterator(); iter.hasNext();) {
			GenericCategory category = iter.next();
			//writeGenericCategoryTag(category, genericEntityType.toString());	               
	    	writeOpen("category", getAttriValueMapForCategoryTag(category, genericEntityType.toString()));	 

	    	// write all associated entities for a category
			List<GenericEntity> entitiesList = EntityManager.getInstance().getAllGenericEntitiesInCategoryAtAnyTime(category.getID(), genericEntityType.getCategoryType());
			for (GenericEntity genericEntity : entitiesList) {
				writeAssociatedEntityLinkTag(genericEntity);
			}
			writeClose("category");
		}
	}

	private void writeGenericCategories() throws IOException {
		GenericEntityType[] genericEntityTypeList = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < genericEntityTypeList.length; i++) {
			writeGenericCategories(genericEntityTypeList[i]);
		}	
	}
	
	private String getGenericCategoryName(int categoryTypeID, int categoryID) {
		GenericCategory category = EntityManager.getInstance().getGenericCategory(categoryTypeID, categoryID);
		if (category != null ){ return category.getName();}
		return "";    
	}
	private String getGenericEntityName(GenericEntityType type, int entityID) {
		GenericEntity entity = EntityManager.getInstance().getEntity(type, entityID);
		if (entity != null ){ return entity.getName();}
		return "";    
	}

    private String getTier1CategoryName(String fullyQualifiedName) {
    	String Tier1Cat ="";  	
    	if (UtilBase.isEmptyAfterTrim(fullyQualifiedName)) { 
    		return Tier1Cat; 
    	}
    	
    	StringTokenizer st = new StringTokenizer(fullyQualifiedName, Constants.CATEGORY_PATH_DELIMITER_REPORT); 
    	try {
	        st.nextToken(); // skip root
	    	Tier1Cat = st.nextToken(); 	    	
		} 
    	catch (NoSuchElementException e) {
			// do nothing;
		}
		
		return Tier1Cat;
     }

	private Map<String,String> getAttriValueMapForCategoryTag(GenericCategory category, String entityTypeName) throws IOException {
		Map<String,String> attriValueMap = new LinkedHashMap<String, String>();
		
	   	attriValueMap.put("id", String.valueOf(category.getID()));
	   	attriValueMap.put("type", entityTypeName);
	   	attriValueMap.put("name", category.getName());
	   	attriValueMap.put("fullyQualifiedName", EntityManager.getInstance().getMostRecentFullyQualifiedCategoryName(category));
	
	   	// write parent associate 
	   	if (category.isRoot()) {
	      	attriValueMap.put("parentID", "-1");       	
	    } else {      	 	
	        for (Iterator<MutableTimedAssociationKey> parentIter = category.getParentKeyIterator(); parentIter.hasNext();) {
				TimedAssociationKey asscKey = parentIter.next();
		      	attriValueMap.put("parentID", String.valueOf(asscKey.getAssociableID()));
		      	attriValueMap.put("parentName", getGenericCategoryName(category.getType(), asscKey.getAssociableID()));
			}
	    }    
		return attriValueMap;
	}
            
    private void writeAssociatedEntityLinkTag(GenericEntity entity) throws IOException{
      	int entityID =  entity.getID();
    	writeOpen("entity-link", 
    				"id", String.valueOf(entityID), 
    				"name", getGenericEntityName(entity.getType(), entityID));
		writeClose("entity-link");
   }

    
    private void writePropertyTag(String name, String displayName, String value) throws IOException {
    	writeOpen("property",  "name", name, "displayName", displayName, "value", value);
       	writeClose("property");
    }

    private void writeAssociatedCategoryLinkTag(GenericCategory category) throws IOException{
 		Map<String,String> attriValueMap = new LinkedHashMap<String, String>();
      	String fullyQualifiedName = EntityManager.getInstance().getMostRecentFullyQualifiedCategoryName(category);		
      	int categoryID = category.getID();
      	attriValueMap.put("id", String.valueOf(categoryID));
      	attriValueMap.put("name", getGenericCategoryName(category.getType(), categoryID));
      	attriValueMap.put("fullyQualifiedName", fullyQualifiedName);
      	attriValueMap.put("tier1Category", getTier1CategoryName(fullyQualifiedName));
      	
    	writeOpen("category-link", attriValueMap);
		writeClose("category-link");		
    }
    
    private void writeAssociatedCategoryLink(GenericEntity entity, Date date) throws IOException{
    	List<GenericCategory> list;
    	if (date == null) {
  		 	list = EntityManager.getInstance().getAllAssociatedCategoriesForEntityAtAnyTime(entity.getID(),entity.getType());   		
    	} else {
	      	list = EntityManager.getInstance().getAllAssociatedCategoriesForEntityAsOf(entity.getID(), entity.getType(), date);   		
    	}
    	
		for (GenericCategory category: list) { 
			writeAssociatedCategoryLinkTag(category);			
		}
    }

    private void writeAssociatiedCompatibilityLink(GenericEntity entity, Date date) throws IOException{

    	List<GenericEntityAssociationKey> list;       
    	if (date == null) {
    		list = EntityManager.getInstance().getCompatibilities(entity.getID(), entity.getType());  		
    	} else {
    		list = EntityManager.getInstance().getCompatibilitiesAsOf(entity.getID(), entity.getType(), date);   		
    	}
    	
		for (GenericEntityAssociationKey asscKey: list) { 
        	GenericEntityType asscType = asscKey.getGenericEntityType();
        	int assctID = asscKey.getAssociableID();
        	GenericEntity asscEntity = EntityManager.getInstance().getEntity(asscType, assctID);
        	writeAssociatedEntityLinkTag(asscEntity);			
		}
    }
    
    private void writeEntityTag(GenericEntity entity, Date date) throws IOException{	
 		Map<String,String> attriValueMap = new LinkedHashMap<String, String>();
      	attriValueMap.put("id", String.valueOf(entity.getID()));
       	attriValueMap.put("type", entity.getType().toString());
       	attriValueMap.put("name", entity.getName());
      	attriValueMap.put("parentID", String.valueOf(entity.getParentID()));
      	attriValueMap.put("parentName", getGenericEntityName(entity.getType(), entity.getParentID()));
    	writeOpen("entity", attriValueMap);
      	
        // write properties
        String[] props = entity.getProperties();
		EntityTypeDefinition entityTypeDef = ConfigurationManager.getInstance().getEntityConfiguration().findEntityTypeDefinition(entity.getType());
        for (int j = 0; j < props.length; j++) {
            Object value = entity.getProperty(props[j]);
            String valueStr = null;
            if (value == null) {
                valueStr = "";
            }
            else if (value instanceof Date) {
                valueStr = ConfigUtil.toDateXMLReportString((Date) value);                
            }
            else {
                valueStr = value.toString();
            }
    		String displayName = "";
    		if (entityTypeDef != null) displayName = entityTypeDef.findPropertyDisplayName(props[j]);
            if (displayName == null) displayName = "";
    		writePropertyTag(props[j], displayName, valueStr);
        }

        // write linked categories
        writeAssociatedCategoryLink(entity, date);

        // write compatibility data
        writeAssociatiedCompatibilityLink(entity, date);
		writeClose("entity");   
    }

	private void writeGenericEntities(GenericEntityType genericEntityType, Date date) throws IOException {
		// write all entities for an entity type
        for (Iterator<GenericEntity> iter = EntityManager.getInstance().getAllEntities(genericEntityType).iterator(); iter.hasNext();) {
             writeEntityTag(iter.next(), date);
		}
	}
    
	private void writeGenericEntities(Date date) throws IOException {
		GenericEntityType[] entityTypeList = GenericEntityType.getAllGenericEntityTypes();
		for (int i = 0; i < entityTypeList.length; i++) {
			writeGenericEntities(entityTypeList[i], date);
		}	
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
}
