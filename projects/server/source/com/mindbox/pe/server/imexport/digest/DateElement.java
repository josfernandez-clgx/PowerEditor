package com.mindbox.pe.server.imexport.digest;

import java.util.Date;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.validate.oval.PositiveOrUnassigned;


/**
 * Date Synonym digest object.
 * @since PowerEditor 4.2
 */
public class DateElement {

	@PositiveOrUnassigned
	private int id = -1;
	
	@NotNull
	@NotEmpty
	private String name = null;
	
	private String description = null;
	
	@NotNull
	private Date date;
	
	
	public String getDescription() {
		return description;
	}
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(String dateStr) {
		this.date = ConfigUtil.toDate(dateStr);
	}

	public String toString() {
		return "DateElement["+id+","+name+","+ConfigUtil.toDateXMLString(date)+",desc="+description+"]";
	}


}
